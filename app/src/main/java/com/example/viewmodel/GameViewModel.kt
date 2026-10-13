package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.GameContent
import com.example.data.SaveData
import com.example.data.SaveManager
import com.example.haptics.HapticManager
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

data class FloatingText(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isCrit: Boolean,
    val xOffsetRatio: Float,
    val yOffsetRatio: Float
)

data class OfflineRewardInfo(
    val secondsAway: Long,
    val echoesEarned: Double,
    val essenceEarned: Double
)

enum class BuyMultiplier(val label: String, val amount: Int) {
    X1("×1", 1),
    X10("×10", 10),
    X25("×25", 25),
    X100("×100", 100),
    MAX("MAX", -1)
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val saveManager = SaveManager(application)
    val soundManager = SoundManager()
    val hapticManager = HapticManager(application)

    private val _gameState = MutableStateFlow(saveManager.loadGame())
    val gameState: StateFlow<SaveData> = _gameState.asStateFlow()

    private val _buyMultiplier = MutableStateFlow(BuyMultiplier.X1)
    val buyMultiplier: StateFlow<BuyMultiplier> = _buyMultiplier.asStateFlow()

    private val _floatingTexts = MutableStateFlow<List<FloatingText>>(emptyList())
    val floatingTexts: StateFlow<List<FloatingText>> = _floatingTexts.asStateFlow()

    private val _activeBoss = MutableStateFlow<ActiveBossFight?>(null)
    val activeBoss: StateFlow<ActiveBossFight?> = _activeBoss.asStateFlow()

    private val _offlineReward = MutableStateFlow<OfflineRewardInfo?>(null)
    val offlineReward: StateFlow<OfflineRewardInfo?> = _offlineReward.asStateFlow()

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    // Temporary active ability buffs
    private val _voidSurgeActive = MutableStateFlow(false)
    val voidSurgeActive: StateFlow<Boolean> = _voidSurgeActive.asStateFlow()

    private val _bloodEclipseActive = MutableStateFlow(false)
    val bloodEclipseActive: StateFlow<Boolean> = _bloodEclipseActive.asStateFlow()

    private var gameLoopJob: Job? = null
    private var autoSaveCounter = 0

    init {
        // Sync sound & haptics with saved settings
        soundManager.isEnabled = _gameState.value.settings.soundEnabled
        hapticManager.isEnabled = _gameState.value.settings.hapticsEnabled

        checkOfflineProgress()
        startGameLoop()
    }

    private fun checkOfflineProgress() {
        val lastSave = _gameState.value.lastSavedTimestamp
        val now = System.currentTimeMillis()
        val deltaSeconds = (now - lastSave) / 1000L

        if (deltaSeconds >= 10L) {
            val dps = calculateTotalDps()
            val essenceRate = calculateTotalEssenceRate()

            // Calculate max offline hours from constellations & artifacts
            val anomalyRank = _gameState.value.constellations.find { it.id == "perk_chrono_anomaly" }?.rank ?: 0
            val hourglassLvl = _gameState.value.artifacts.find { it.id == "oblivion_hourglass" }?.level ?: 0
            val maxHours = 8.0 + (anomalyRank * 2.0) + (hourglassLvl * 1.0)
            val maxSeconds = (maxHours * 3600.0).toLong()

            val effectiveSeconds = min(deltaSeconds, maxSeconds)
            val efficiency = (0.6 + (anomalyRank * 0.1) + (hourglassLvl * 0.05)).coerceAtMost(1.0)

            val earnedEchoes = dps * effectiveSeconds * efficiency
            val earnedEssence = essenceRate * effectiveSeconds * efficiency

            if (earnedEchoes > 0.0 || earnedEssence > 0.0) {
                _gameState.update { state ->
                    val newCurr = state.currencies.copy(
                        echoes = state.currencies.echoes + earnedEchoes,
                        essence = state.currencies.essence + earnedEssence
                    )
                    val newStats = state.statistics.copy(
                        totalEchoesEarned = state.statistics.totalEchoesEarned + earnedEchoes
                    )
                    state.copy(currencies = newCurr, statistics = newStats)
                }
                _offlineReward.value = OfflineRewardInfo(effectiveSeconds, earnedEchoes, earnedEssence)
            }
        }
    }

    fun dismissOfflineReward() {
        _offlineReward.value = null
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            val tickRateMs = 100L // 10 updates per second for smooth passive resource accumulation & boss fights
            while (isActive) {
                delay(tickRateMs)
                val dt = tickRateMs / 1000.0
                onTick(dt)
            }
        }
    }

    private fun onTick(dt: Double) {
        val dps = calculateTotalDps()
        val essenceRate = calculateTotalEssenceRate()
        val familiarClicksPerSec = calculateFamiliarAutoClicks()

        // Familiar auto-clicking
        if (familiarClicksPerSec > 0.0) {
            val clicksThisTick = familiarClicksPerSec * dt
            if (Random.nextDouble() < clicksThisTick) {
                onVoidHeartClicked(isAuto = true)
            }
        }

        val dpsGain = dps * dt
        val essenceGain = essenceRate * dt

        _gameState.update { state ->
            val newEchoes = state.currencies.echoes + dpsGain
            val newEssence = state.currencies.essence + essenceGain
            val totalEarned = state.statistics.totalEchoesEarned + dpsGain
            val playTime = state.statistics.playTimeSeconds + 1L // approximate
            val highestDps = max(state.statistics.highestDps, dps)

            state.copy(
                currencies = state.currencies.copy(
                    echoes = newEchoes,
                    essence = newEssence
                ),
                statistics = state.statistics.copy(
                    totalEchoesEarned = totalEarned,
                    highestDps = highestDps,
                    playTimeSeconds = playTime
                )
            )
        }

        // Boss fight countdown and passive boss damage
        val currentBoss = _activeBoss.value
        if (currentBoss != null) {
            val newRemaining = currentBoss.remainingSeconds - dt.toFloat()
            val artifactBossBonus = 1.0 + ((_gameState.value.artifacts.find { it.id == "astral_blade" }?.level ?: 0) * 0.5)
            val bossDamageThisTick = dpsGain * artifactBossBonus
            val newHp = max(0.0, currentBoss.currentHp - bossDamageThisTick)

            if (newHp <= 0.0) {
                onBossDefeated(currentBoss)
            } else if (newRemaining <= 0f) {
                // Time up! Defeat
                _activeBoss.value = null
                soundManager.play(SoundManager.SoundEffect.BOSS_HIT)
                showBanner("Время истекло! Босс скрылся в Пустоте.")
            } else {
                _activeBoss.value = currentBoss.copy(
                    currentHp = newHp,
                    remainingSeconds = newRemaining
                )
            }
        }

        // Auto-save periodically every 15 seconds
        autoSaveCounter++
        if (autoSaveCounter >= 150) {
            autoSaveCounter = 0
            saveManager.saveGame(_gameState.value)
        }
    }

    fun onVoidHeartClicked(isAuto: Boolean = false) {
        val clickPower = calculateClickPower()
        val critChance = calculateCritChance()
        val critMult = calculateCritMultiplier()

        val isCrit = _bloodEclipseActive.value || (Random.nextDouble() < critChance)
        val finalPower = if (isCrit) clickPower * critMult else clickPower

        // Sound & Haptics
        if (!isAuto) {
            if (isCrit) {
                soundManager.play(SoundManager.SoundEffect.CRIT)
                hapticManager.crit()
            } else {
                soundManager.play(SoundManager.SoundEffect.TAP)
                hapticManager.tap()
            }
        }

        // Floating combat text
        if (!isAuto || Random.nextDouble() < 0.2) {
            val randomX = (Random.nextFloat() - 0.5f) * 120f
            val randomY = (Random.nextFloat() - 0.5f) * 60f
            val text = if (isCrit) "КРИТ! +${BigNum.format(finalPower)}" else "+${BigNum.format(finalPower)}"
            val item = FloatingText(text = text, isCrit = isCrit, xOffsetRatio = randomX, yOffsetRatio = randomY)
            _floatingTexts.update { it.takeLast(12) + item }
            viewModelScope.launch {
                delay(1200)
                _floatingTexts.update { list -> list.filter { it.id != item.id } }
            }
        }

        // Damage to active boss if present
        val currentBoss = _activeBoss.value
        if (currentBoss != null) {
            val artifactBossBonus = 1.0 + ((_gameState.value.artifacts.find { it.id == "astral_blade" }?.level ?: 0) * 0.5)
            val bossHit = finalPower * artifactBossBonus
            val newHp = max(0.0, currentBoss.currentHp - bossHit)
            if (newHp <= 0.0) {
                onBossDefeated(currentBoss)
            } else {
                _activeBoss.value = currentBoss.copy(currentHp = newHp)
            }
        }

        // Update currency & clicks
        _gameState.update { state ->
            val newEchoes = state.currencies.echoes + finalPower
            val newTotalClicks = state.statistics.totalClicks + 1L
            val newTotalEarned = state.statistics.totalEchoesEarned + finalPower
            state.copy(
                currencies = state.currencies.copy(echoes = newEchoes),
                statistics = state.statistics.copy(
                    totalClicks = newTotalClicks,
                    totalEchoesEarned = newTotalEarned
                )
            )
        }

        checkQuests()
        checkLore()
    }

    fun startBossFight(biome: Biome) {
        if (_activeBoss.value != null) return
        _activeBoss.value = ActiveBossFight(
            biomeId = biome.id,
            bossName = "${biome.bossName} — ${biome.bossTitle}",
            maxHp = biome.bossMaxHp,
            currentHp = biome.bossMaxHp
        )
        soundManager.play(SoundManager.SoundEffect.BOSS_HIT)
        hapticManager.pulse()
        showBanner("Брошен вызов: ${biome.bossName}!")
    }

    private fun onBossDefeated(boss: ActiveBossFight) {
        _activeBoss.value = null
        val biome = _gameState.value.biomes.find { it.id == boss.biomeId } ?: return

        // Calculate rewards with supernova perk
        val supernovaRank = _gameState.value.constellations.find { it.id == "perk_supernova" }?.rank ?: 0
        val rewardMult = 1.0 + (supernovaRank * 0.4)
        val coresEarned = biome.bossRewardCores * rewardMult
        val ashesEarned = biome.bossRewardAshes * rewardMult

        _gameState.update { state ->
            val updatedBiomes = state.biomes.map { b ->
                if (b.id == biome.id) b.copy(isBossDefeated = true)
                else if (b.id == biome.id + 1) b.copy(isUnlocked = true)
                else b
            }
            val newCurrencies = state.currencies.copy(
                voidCores = state.currencies.voidCores + coresEarned,
                timeAshes = state.currencies.timeAshes + ashesEarned
            )
            val newStats = state.statistics.copy(
                totalBossesDefeated = state.statistics.totalBossesDefeated + 1
            )
            state.copy(
                biomes = updatedBiomes,
                currencies = newCurrencies,
                statistics = newStats
            )
        }

        soundManager.play(SoundManager.SoundEffect.BOSS_VICTORY)
        hapticManager.pulse()
        showBanner("ПОБЕДА! ${boss.bossName} повержен! +${BigNum.format(coresEarned)} Ядер, +${BigNum.format(ashesEarned)} Пепла!")
        checkQuests()
        checkLore()
        saveGameNow()
    }

    fun setBuyMultiplier(mult: BuyMultiplier) {
        _buyMultiplier.value = mult
    }

    fun buyCoreUpgrade(upgradeId: String) {
        val state = _gameState.value
        val upgrade = state.coreUpgrades.find { it.id == upgradeId } ?: return
        val currentLevel = upgrade.level
        val mult = _buyMultiplier.value

        val countToBuy = if (mult == BuyMultiplier.MAX) {
            BigNum.calculateMaxAffordable(upgrade.baseCost, upgrade.costMultiplier, currentLevel, state.currencies.echoes)
        } else {
            mult.amount
        }

        if (countToBuy <= 0) return
        val totalCost = BigNum.calculateTotalCost(upgrade.baseCost, upgrade.costMultiplier, currentLevel, countToBuy)
        if (state.currencies.echoes < totalCost) return

        _gameState.update { s ->
            val newEchoes = s.currencies.echoes - totalCost
            val updatedList = s.coreUpgrades.map { u ->
                if (u.id == upgradeId) u.copy(level = u.level + countToBuy) else u
            }
            s.copy(
                currencies = s.currencies.copy(echoes = newEchoes),
                coreUpgrades = updatedList
            )
        }
        soundManager.play(SoundManager.SoundEffect.UPGRADE)
        hapticManager.tap()
    }

    fun buyStructure(structureId: String) {
        val state = _gameState.value
        val struct = state.structures.find { it.id == structureId } ?: return
        val currentCount = struct.count
        val mult = _buyMultiplier.value

        // Cost reduction from perk & cloak artifact
        val chaosRank = state.constellations.find { it.id == "perk_chaos_mastery" }?.rank ?: 0
        val cloakLvl = state.artifacts.find { it.id == "cloak_of_the_void" }?.level ?: 0
        val discount = (chaosRank * 0.03 + cloakLvl * 0.05).coerceAtMost(0.5)
        val discountedBaseCost = struct.baseCost * (1.0 - discount)

        val countToBuy = if (mult == BuyMultiplier.MAX) {
            BigNum.calculateMaxAffordable(discountedBaseCost, struct.costMultiplier, currentCount, state.currencies.echoes)
        } else {
            mult.amount
        }

        if (countToBuy <= 0) return
        val totalCost = BigNum.calculateTotalCost(discountedBaseCost, struct.costMultiplier, currentCount, countToBuy)
        if (state.currencies.echoes < totalCost) return

        _gameState.update { s ->
            val newEchoes = s.currencies.echoes - totalCost
            val updatedStructs = s.structures.map { st ->
                if (st.id == structureId) st.copy(count = st.count + countToBuy) else st
            }
            s.copy(
                currencies = s.currencies.copy(echoes = newEchoes),
                structures = updatedStructs
            )
        }
        soundManager.play(SoundManager.SoundEffect.UPGRADE)
        hapticManager.tap()
        checkQuests()
        checkLore()
    }

    fun transmute(recipeId: String) {
        val state = _gameState.value
        val recipe = GameContent.initialAlchemyRecipes().find { it.id == recipeId } ?: return

        val hasInput = when (recipe.inputCurrency) {
            CurrencyType.ECHOES -> state.currencies.echoes >= recipe.inputCost
            CurrencyType.SHARDS -> state.currencies.shards >= recipe.inputCost
            CurrencyType.ESSENCE -> state.currencies.essence >= recipe.inputCost
            CurrencyType.TIME_ASHES -> state.currencies.timeAshes >= recipe.inputCost
            CurrencyType.VOID_CORES -> state.currencies.voidCores >= recipe.inputCost
            CurrencyType.ANCIENT_RUNES -> state.currencies.ancientRunes >= recipe.inputCost
        }
        if (!hasInput) return

        // Check critical double yield
        val genesisRank = state.constellations.find { it.id == "perk_alchemical_genesis" }?.rank ?: 0
        val grimoireLvl = state.artifacts.find { it.id == "grimoire_of_madness" }?.level ?: 0
        val runeBonus = if (state.runes.any { it.type == RuneType.ALCHEMY && it.isEquipped }) 0.25 else 0.0
        val doubleChance = (recipe.bonusOutputChance + (genesisRank * 0.05) + (grimoireLvl * 0.04) + runeBonus).coerceAtMost(0.75)

        val isCriticalYield = Random.nextDouble() < doubleChance
        val outputAmount = if (isCriticalYield) recipe.outputAmount * 2.0 else recipe.outputAmount

        _gameState.update { s ->
            var echoes = s.currencies.echoes
            var shards = s.currencies.shards
            var essence = s.currencies.essence
            var ashes = s.currencies.timeAshes
            var cores = s.currencies.voidCores
            var runes = s.currencies.ancientRunes

            when (recipe.inputCurrency) {
                CurrencyType.ECHOES -> echoes -= recipe.inputCost
                CurrencyType.SHARDS -> shards -= recipe.inputCost
                CurrencyType.ESSENCE -> essence -= recipe.inputCost
                CurrencyType.TIME_ASHES -> ashes -= recipe.inputCost
                CurrencyType.VOID_CORES -> cores -= recipe.inputCost
                CurrencyType.ANCIENT_RUNES -> runes -= recipe.inputCost
            }

            when (recipe.outputCurrency) {
                CurrencyType.ECHOES -> echoes += outputAmount
                CurrencyType.SHARDS -> shards += outputAmount
                CurrencyType.ESSENCE -> essence += outputAmount
                CurrencyType.TIME_ASHES -> ashes += outputAmount
                CurrencyType.VOID_CORES -> cores += outputAmount
                CurrencyType.ANCIENT_RUNES -> runes += outputAmount
            }

            val newStats = s.statistics.copy(totalTransmutes = s.statistics.totalTransmutes + 1)
            s.copy(
                currencies = Currencies(echoes, shards, essence, ashes, cores, runes),
                statistics = newStats
            )
        }

        soundManager.play(SoundManager.SoundEffect.UPGRADE)
        hapticManager.tap()
        if (isCriticalYield) {
            showBanner("Критическая трансмутация! Двойной выход: +${BigNum.format(outputAmount)} ${recipe.outputCurrency.displayName}!")
        }
        checkQuests()
        checkLore()
    }

    fun toggleRune(runeId: String) {
        val state = _gameState.value
        val rune = state.runes.find { it.id == runeId } ?: return

        if (!rune.isEquipped) {
            // Check max equipped runes (3 max)
            val equippedCount = state.runes.count { it.isEquipped }
            if (equippedCount >= 3) {
                showBanner("Достигнут лимит рун (максимум 3). Снимите одну перед установкой.")
                return
            }
            if (state.currencies.ancientRunes < rune.costRunes) {
                showBanner("Недостаточно Древних Рун (требуется ${BigNum.format(rune.costRunes)}).")
                return
            }
            // Equip and deduct
            _gameState.update { s ->
                val newRunesCur = s.currencies.ancientRunes - rune.costRunes
                val updated = s.runes.map { if (it.id == runeId) it.copy(isEquipped = true) else it }
                s.copy(currencies = s.currencies.copy(ancientRunes = newRunesCur), runes = updated)
            }
            soundManager.play(SoundManager.SoundEffect.UPGRADE)
            hapticManager.pulse()
        } else {
            // Unequip
            _gameState.update { s ->
                val updated = s.runes.map { if (it.id == runeId) it.copy(isEquipped = false) else it }
                s.copy(runes = updated)
            }
            soundManager.play(SoundManager.SoundEffect.TAP)
        }
    }

    fun upgradeArtifact(artifactId: String) {
        val state = _gameState.value
        val artifact = state.artifacts.find { it.id == artifactId } ?: return
        if (artifact.level >= artifact.maxLevel) return

        val cost = artifact.baseUpgradeCostCores * (artifact.level + 1)
        if (state.currencies.voidCores < cost) {
            showBanner("Недостаточно Ядер Пустоты для улучшения артефакта.")
            return
        }

        _gameState.update { s ->
            val newCores = s.currencies.voidCores - cost
            val updated = s.artifacts.map { a ->
                if (a.id == artifactId) a.copy(level = a.level + 1, isUnlocked = true) else a
            }
            s.copy(currencies = s.currencies.copy(voidCores = newCores), artifacts = updated)
        }
        soundManager.play(SoundManager.SoundEffect.UPGRADE)
        hapticManager.pulse()
        showBanner("${artifact.name} улучшен до уровня ${artifact.level + 1}!")
    }

    fun upgradeFamiliar(familiarId: String) {
        val state = _gameState.value
        val familiar = state.familiars.find { it.id == familiarId } ?: return
        val cost = familiar.baseCostEssence * Math.pow(familiar.costMultiplier, familiar.level.toDouble())

        if (state.currencies.essence < cost) {
            showBanner("Недостаточно Эссенции Хаоса для призыва/улучшения фамильяра.")
            return
        }

        _gameState.update { s ->
            val newEssence = s.currencies.essence - cost
            val updated = s.familiars.map { f ->
                if (f.id == familiarId) f.copy(level = f.level + 1, isUnlocked = true) else f
            }
            s.copy(currencies = s.currencies.copy(essence = newEssence), familiars = updated)
        }
        soundManager.play(SoundManager.SoundEffect.UPGRADE)
        hapticManager.pulse()
        showBanner("${familiar.name} пробужден (Уровень ${familiar.level + 1})!")
    }

    fun upgradeConstellation(perkId: String) {
        val state = _gameState.value
        val perk = state.constellations.find { it.id == perkId } ?: return
        if (perk.rank >= perk.maxRank) return

        val cost = perk.costAshes * Math.pow(perk.costMultiplier, perk.rank.toDouble())
        if (state.currencies.timeAshes < cost) {
            showBanner("Недостаточно Пепла Времени.")
            return
        }

        _gameState.update { s ->
            val newAshes = s.currencies.timeAshes - cost
            val updated = s.constellations.map { c ->
                if (c.id == perkId) c.copy(rank = c.rank + 1) else c
            }
            s.copy(currencies = s.currencies.copy(timeAshes = newAshes), constellations = updated)
        }
        soundManager.play(SoundManager.SoundEffect.PRESTIGE)
        hapticManager.pulse()
    }

    fun activateVoidSurge() {
        if (_voidSurgeActive.value) return
        _voidSurgeActive.value = true
        soundManager.play(SoundManager.SoundEffect.PRESTIGE)
        hapticManager.pulse()
        showBanner("ЯРОСТЬ БЕЗДНЫ АКТИВИРОВАНА! Урон клика ×4 на 20 секунд!")
        viewModelScope.launch {
            delay(20000)
            _voidSurgeActive.value = false
            showBanner("Действие Ярости Бездны иссякло.")
        }
    }

    fun activateBloodEclipse() {
        if (_bloodEclipseActive.value) return
        _bloodEclipseActive.value = true
        soundManager.play(SoundManager.SoundEffect.CRIT)
        hapticManager.pulse()
        showBanner("КРОВАВОЕ ЗАТМЕНИЕ! 100% шанс критического удара на 15 секунд!")
        viewModelScope.launch {
            delay(15000)
            _bloodEclipseActive.value = false
            showBanner("Кровавое Затмение рассеялось.")
        }
    }

    fun calculatePrestigeGain(): Double {
        val lifetimeEchoes = _gameState.value.statistics.totalEchoesEarned
        if (lifetimeEchoes < 100000.0) return 0.0
        val baseAshes = floor(sqrt(lifetimeEchoes / 100000.0))
        val starArtifactBonus = 1.0 + ((_gameState.value.artifacts.find { it.id == "heart_of_dying_star" }?.level ?: 0) * 0.2)
        return floor(baseAshes * starArtifactBonus)
    }

    fun performPrestige() {
        val ashesGain = calculatePrestigeGain()
        if (ashesGain <= 0.0) {
            showBanner("Недостаточно силы для Прорыва Печати. Соберите больше Эха!")
            return
        }

        _gameState.update { s ->
            val newTotalAshes = s.currencies.timeAshes + ashesGain
            val newPrestigeCount = s.statistics.totalPrestigeCount + 1

            s.copy(
                currencies = Currencies(
                    echoes = 0.0,
                    shards = 0.0,
                    essence = 0.0,
                    timeAshes = newTotalAshes,
                    voidCores = s.currencies.voidCores, // Boss cores persist!
                    ancientRunes = s.currencies.ancientRunes // Runes persist!
                ),
                coreUpgrades = GameContent.initialCoreUpgrades(),
                structures = GameContent.initialStructures(),
                statistics = s.statistics.copy(totalPrestigeCount = newPrestigeCount),
                activeBiomeId = 1
            )
        }

        _activeBoss.value = null
        soundManager.play(SoundManager.SoundEffect.PRESTIGE)
        hapticManager.pulse()
        showBanner("ПРОРЫВ ПЕЧАТИ СВЕРШЕН! Получено +${BigNum.format(ashesGain)} Пепла Времени!")
        checkQuests()
        checkLore()
        saveGameNow()
    }

    fun claimQuest(questId: String) {
        val state = _gameState.value
        val quest = state.quests.find { it.id == questId } ?: return
        if (quest.isClaimed || !isQuestCompleted(quest, state)) return

        _gameState.update { s ->
            var curr = s.currencies
            when (quest.rewardCurrency) {
                CurrencyType.ECHOES -> curr = curr.copy(echoes = curr.echoes + quest.rewardAmount)
                CurrencyType.SHARDS -> curr = curr.copy(shards = curr.shards + quest.rewardAmount)
                CurrencyType.ESSENCE -> curr = curr.copy(essence = curr.essence + quest.rewardAmount)
                CurrencyType.TIME_ASHES -> curr = curr.copy(timeAshes = curr.timeAshes + quest.rewardAmount)
                CurrencyType.VOID_CORES -> curr = curr.copy(voidCores = curr.voidCores + quest.rewardAmount)
                CurrencyType.ANCIENT_RUNES -> curr = curr.copy(ancientRunes = curr.ancientRunes + quest.rewardAmount)
            }
            val updatedQuests = s.quests.map { if (it.id == questId) it.copy(isClaimed = true) else it }
            s.copy(currencies = curr, quests = updatedQuests)
        }
        soundManager.play(SoundManager.SoundEffect.UPGRADE)
        hapticManager.pulse()
        showBanner("Задание выполнено: ${quest.title}! Получено +${BigNum.format(quest.rewardAmount)} ${quest.rewardCurrency.displayName}!")
    }

    fun isQuestCompleted(quest: Quest, state: SaveData): Boolean {
        return when (quest.type) {
            QuestType.CLICKS -> state.statistics.totalClicks >= quest.target
            QuestType.TOTAL_ECHOES -> state.statistics.totalEchoesEarned >= quest.target
            QuestType.STRUCTURES -> state.structures.sumOf { it.count } >= quest.target
            QuestType.BOSS_KILLS -> state.statistics.totalBossesDefeated >= quest.target
            QuestType.ALCHEMY_TRANSMUTES -> state.statistics.totalTransmutes >= quest.target
            QuestType.PRESTIGE_COUNT -> state.statistics.totalPrestigeCount >= quest.target
        }
    }

    private fun checkQuests() {
        // Automatically handled by UI state observations
    }

    private fun checkLore() {
        val state = _gameState.value
        _gameState.update { s ->
            val updatedLore = s.lore.map { l ->
                if (l.isUnlocked) l
                else {
                    val shouldUnlock = when (l.id) {
                        1 -> true
                        2 -> s.structures.find { it.id == "void_dredger" }?.let { it.count >= 5 } ?: false
                        3 -> s.structures.find { it.id == "cultist_coven" }?.let { it.count >= 1 } ?: false
                        4 -> s.statistics.totalTransmutes >= 1
                        5 -> s.biomes.find { it.id == 1 }?.isBossDefeated ?: false
                        6 -> s.biomes.find { it.id == 3 }?.isUnlocked ?: false
                        7 -> s.statistics.totalPrestigeCount >= 1
                        8 -> s.biomes.find { it.id == 5 }?.isBossDefeated ?: false
                        else -> false
                    }
                    if (shouldUnlock) l.copy(isUnlocked = true) else l
                }
            }
            s.copy(lore = updatedLore)
        }
    }

    fun setActiveBiome(biomeId: Int) {
        val biome = _gameState.value.biomes.find { it.id == biomeId } ?: return
        if (!biome.isUnlocked) return
        _gameState.update { it.copy(activeBiomeId = biomeId) }
        showBanner("Переход в биом: ${biome.name}")
    }

    fun updateSettings(sound: Boolean? = null, haptics: Boolean? = null, scientific: Boolean? = null, particleQuality: ParticleQuality? = null) {
        _gameState.update { s ->
            val newSettings = s.settings.copy(
                soundEnabled = sound ?: s.settings.soundEnabled,
                hapticsEnabled = haptics ?: s.settings.hapticsEnabled,
                scientificNotation = scientific ?: s.settings.scientificNotation,
                particleQuality = particleQuality ?: s.settings.particleQuality
            )
            soundManager.isEnabled = newSettings.soundEnabled
            hapticManager.isEnabled = newSettings.hapticsEnabled
            s.copy(settings = newSettings)
        }
        saveGameNow()
    }

    fun saveGameNow(): Boolean {
        return saveManager.saveGame(_gameState.value)
    }

    fun exportSaveString(): String {
        return saveManager.exportSaveString(_gameState.value)
    }

    fun importSaveString(encoded: String): Boolean {
        val loaded = saveManager.importSaveString(encoded) ?: return false
        _gameState.value = loaded
        soundManager.isEnabled = loaded.settings.soundEnabled
        hapticManager.isEnabled = loaded.settings.hapticsEnabled
        showBanner("Сохранение успешно загружено!")
        return true
    }

    fun hardResetGame() {
        _gameState.value = saveManager.resetGame()
        _activeBoss.value = null
        showBanner("Прогресс сброшен. Сердце Бездны спит.")
    }

    fun showBanner(msg: String) {
        _bannerMessage.value = msg
        viewModelScope.launch {
            delay(3500)
            if (_bannerMessage.value == msg) {
                _bannerMessage.value = null
            }
        }
    }

    // Calculations
    fun calculateClickPower(): Double {
        val state = _gameState.value
        var basePower = 1.0

        for (u in state.coreUpgrades) {
            basePower += u.level * u.clickPowerAdd
        }

        var multiplier = 1.0
        for (u in state.coreUpgrades) {
            multiplier += u.level * u.clickMultiplierAdd
        }

        // Constellation "Первородная Сила"
        val primalRank = state.constellations.find { it.id == "perk_primal_strike" }?.rank ?: 0
        multiplier += primalRank * 0.25

        // Artifact "Око Незрячего Бога"
        val eyeLvl = state.artifacts.find { it.id == "azathoth_eye" }?.level ?: 0
        multiplier += eyeLvl * 0.50

        // Rune "Пожирание"
        val devourRunes = state.runes.filter { it.type == RuneType.DEVOUR && it.isEquipped }
        for (r in devourRunes) {
            multiplier += r.bonusPercent / 100.0
        }

        // Constellation "Абсолютный Резонанс"
        val resRank = state.constellations.find { it.id == "perk_abyssal_resonance" }?.rank ?: 0
        val totalStructs = state.structures.sumOf { it.count }
        multiplier += resRank * totalStructs * 0.01

        if (_voidSurgeActive.value) {
            multiplier *= 4.0
        }

        return basePower * multiplier
    }

    fun calculateCritChance(): Double {
        val state = _gameState.value
        var chance = 0.05
        for (u in state.coreUpgrades) {
            chance += u.level * u.critChanceAdd
        }
        val clairvoyanceRank = state.constellations.find { it.id == "perk_clairvoyance" }?.rank ?: 0
        chance += clairvoyanceRank * 0.02
        val eyeLvl = state.artifacts.find { it.id == "azathoth_eye" }?.level ?: 0
        chance += eyeLvl * 0.05

        val ruinRunes = state.runes.filter { it.type == RuneType.RUIN && it.isEquipped }
        for (r in ruinRunes) {
            chance += (r.bonusPercent * 0.5) / 100.0
        }
        return chance.coerceIn(0.05, 0.85)
    }

    fun calculateCritMultiplier(): Double {
        val state = _gameState.value
        var mult = 2.0
        for (u in state.coreUpgrades) {
            mult += u.level * u.critMultiplierAdd
        }
        val clairvoyanceRank = state.constellations.find { it.id == "perk_clairvoyance" }?.rank ?: 0
        mult += clairvoyanceRank * 0.30

        val ruinRunes = state.runes.filter { it.type == RuneType.RUIN && it.isEquipped }
        for (r in ruinRunes) {
            mult += r.bonusPercent / 100.0
        }
        return mult
    }

    fun calculateTotalDps(): Double {
        val state = _gameState.value
        var baseDps = 0.0
        for (s in state.structures) {
            baseDps += s.count * s.baseDps
        }

        if (baseDps == 0.0) return 0.0

        var multiplier = 1.0

        // Constellation "Звезда Вечности"
        val eternityRank = state.constellations.find { it.id == "perk_eternity" }?.rank ?: 0
        multiplier += eternityRank * 0.15

        // Artifact "Венец Пустоты"
        val crownLvl = state.artifacts.find { it.id == "void_crown" }?.level ?: 0
        multiplier += crownLvl * 0.30

        // Biome multiplier
        val activeBiome = state.biomes.find { it.id == state.activeBiomeId }
        if (activeBiome != null) {
            multiplier *= activeBiome.dpsMultiplier
        }

        // Runes Eternity & Cosmos
        val eternityRunes = state.runes.filter { it.type == RuneType.ETERNITY && it.isEquipped }
        for (r in eternityRunes) {
            multiplier += r.bonusPercent / 100.0
        }
        val cosmosRunes = state.runes.filter { it.type == RuneType.COSMOS && it.isEquipped }
        for (r in cosmosRunes) {
            multiplier += r.bonusPercent / 100.0
        }

        // Familiars bonus
        val mirrorLvl = state.artifacts.find { it.id == "mirror_of_ether" }?.level ?: 0
        val familiarMultiplier = 1.0 + (mirrorLvl * 0.30)
        for (f in state.familiars) {
            if (f.isUnlocked && f.level > 0) {
                multiplier += (f.dpsBonusPercent * f.level * familiarMultiplier) / 100.0
            }
        }

        // Passive bonus from Time Ashes (+1% per Ash)
        multiplier += (state.currencies.timeAshes * 0.01).coerceAtMost(10.0)

        if (_voidSurgeActive.value) {
            multiplier *= 2.0
        }

        return baseDps * multiplier
    }

    fun calculateTotalEssenceRate(): Double {
        val state = _gameState.value
        var baseRate = 0.0
        for (s in state.structures) {
            baseRate += s.count * s.essencePerSec
        }
        val grimoireLvl = state.artifacts.find { it.id == "grimoire_of_madness" }?.level ?: 0
        val mult = 1.0 + (grimoireLvl * 0.20)
        return baseRate * mult
    }

    fun calculateFamiliarAutoClicks(): Double {
        val state = _gameState.value
        var rate = 0.0
        val mirrorLvl = state.artifacts.find { it.id == "mirror_of_ether" }?.level ?: 0
        val familiarMultiplier = 1.0 + (mirrorLvl * 0.30)
        for (f in state.familiars) {
            if (f.isUnlocked && f.level > 0) {
                rate += f.autoClicksPerSec * f.level * familiarMultiplier
            }
        }
        return rate
    }
}
