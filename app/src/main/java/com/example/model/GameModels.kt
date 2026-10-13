package com.example.model

enum class CurrencyType(val displayName: String, val symbol: String) {
    ECHOES("Эхо Бездны", "✦"),
    SHARDS("Осколки Пустоты", "❖"),
    ESSENCE("Эссенция Хаоса", "◈"),
    TIME_ASHES("Пепел Времени", "✵"),
    VOID_CORES("Ядра Пустоты", "⦿"),
    ANCIENT_RUNES("Древние Руны", "ᛟ")
}

data class Currencies(
    val echoes: Double = 0.0,
    val shards: Double = 0.0,
    val essence: Double = 0.0,
    val timeAshes: Double = 0.0,
    val voidCores: Double = 0.0,
    val ancientRunes: Double = 0.0
)

data class CoreUpgrade(
    val id: String,
    val name: String,
    val description: String,
    val level: Int = 0,
    val baseCost: Double,
    val costMultiplier: Double,
    val costCurrency: CurrencyType = CurrencyType.ECHOES,
    val clickPowerAdd: Double = 0.0,
    val clickMultiplierAdd: Double = 0.0,
    val critChanceAdd: Double = 0.0,
    val critMultiplierAdd: Double = 0.0,
    val requiredEchoesSeen: Double = 0.0
)

data class Structure(
    val id: String,
    val name: String,
    val description: String,
    val count: Int = 0,
    val baseCost: Double,
    val costMultiplier: Double,
    val costCurrency: CurrencyType = CurrencyType.ECHOES,
    val baseDps: Double,
    val essencePerSec: Double = 0.0,
    val requiredEchoesSeen: Double = 0.0
)

data class AlchemyRecipe(
    val id: String,
    val name: String,
    val description: String,
    val inputCurrency: CurrencyType,
    val inputCost: Double,
    val outputCurrency: CurrencyType,
    val outputAmount: Double,
    val bonusOutputChance: Double = 0.15
)

enum class RuneType(val title: String) {
    DEVOUR("Пожирание"),     // Click power
    ETERNITY("Вечность"),     // Passive DPS
    RUIN("Разрушение"),       // Crit chance & dmg
    ALCHEMY("Трансмутация"),  // Alchemy rate
    COSMOS("Космос")          // Global multiplier
}

data class Rune(
    val id: String,
    val name: String,
    val type: RuneType,
    val tier: Int = 1,
    val bonusPercent: Double,
    val costRunes: Double,
    val isEquipped: Boolean = false
)

enum class ArtifactRarity(val label: String) {
    COMMON("Обычный"),
    RARE("Редкий"),
    EPIC("Эпический"),
    MYTHIC("Мифический"),
    ELDRITCH("Древний")
}

data class Artifact(
    val id: String,
    val name: String,
    val lore: String,
    val rarity: ArtifactRarity,
    val level: Int = 0,
    val maxLevel: Int = 10,
    val baseUpgradeCostCores: Double,
    val effectDescription: String,
    val isUnlocked: Boolean = false
)

data class Familiar(
    val id: String,
    val name: String,
    val subtitle: String,
    val lore: String,
    val level: Int = 0,
    val autoClicksPerSec: Double = 0.0,
    val dpsBonusPercent: Double = 0.0,
    val baseCostEssence: Double = 50.0,
    val costMultiplier: Double = 1.6,
    val isUnlocked: Boolean = false
)

data class Biome(
    val id: Int,
    val name: String,
    val subtitle: String,
    val description: String,
    val unlockEchoesRequired: Double,
    val dpsMultiplier: Double,
    val bossName: String,
    val bossTitle: String,
    val bossMaxHp: Double,
    val bossRewardCores: Double,
    val bossRewardAshes: Double,
    val isUnlocked: Boolean = false,
    val isBossDefeated: Boolean = false
)

data class ConstellationPerk(
    val id: String,
    val name: String,
    val description: String,
    val rank: Int = 0,
    val maxRank: Int = 20,
    val costAshes: Double,
    val costMultiplier: Double = 1.5,
    val bonusPerRank: Double
)

enum class QuestType {
    CLICKS,
    TOTAL_ECHOES,
    STRUCTURES,
    BOSS_KILLS,
    ALCHEMY_TRANSMUTES,
    PRESTIGE_COUNT
}

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val target: Double,
    val type: QuestType,
    val rewardCurrency: CurrencyType,
    val rewardAmount: Double,
    val isClaimed: Boolean = false
)

data class LoreChapter(
    val id: Int,
    val title: String,
    val unlockCondition: String,
    val text: String,
    val isUnlocked: Boolean = false
)

data class GameStatistics(
    val totalClicks: Long = 0L,
    val totalEchoesEarned: Double = 0.0,
    val totalBossesDefeated: Int = 0,
    val totalPrestigeCount: Int = 0,
    val totalTransmutes: Int = 0,
    val highestDps: Double = 0.0,
    val playTimeSeconds: Long = 0L,
    val firstLaunchTimestamp: Long = System.currentTimeMillis()
)

data class GameSettings(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val scientificNotation: Boolean = false,
    val particleQuality: ParticleQuality = ParticleQuality.HIGH
)

enum class ParticleQuality(val label: String, val maxParticles: Int) {
    HIGH("Максимум", 60),
    BALANCED("Баланс", 30),
    LOW("Энергосбережение", 10)
}

data class ActiveBossFight(
    val biomeId: Int,
    val bossName: String,
    val maxHp: Double,
    var currentHp: Double,
    val durationSeconds: Float = 30f,
    var remainingSeconds: Float = 30f
)
