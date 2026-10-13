package com.example.data

import android.content.Context
import android.util.Base64
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets

data class SaveData(
    val version: Int = 1,
    val lastSavedTimestamp: Long = System.currentTimeMillis(),
    val currencies: Currencies = Currencies(),
    val coreUpgrades: List<CoreUpgrade> = GameContent.initialCoreUpgrades(),
    val structures: List<Structure> = GameContent.initialStructures(),
    val runes: List<Rune> = GameContent.initialRunes(),
    val artifacts: List<Artifact> = GameContent.initialArtifacts(),
    val familiars: List<Familiar> = GameContent.initialFamiliars(),
    val biomes: List<Biome> = GameContent.initialBiomes(),
    val constellations: List<ConstellationPerk> = GameContent.initialConstellations(),
    val quests: List<Quest> = GameContent.initialQuests(),
    val lore: List<LoreChapter> = GameContent.initialLore(),
    val statistics: GameStatistics = GameStatistics(),
    val settings: GameSettings = GameSettings(),
    val activeBiomeId: Int = 1
)

class SaveManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("void_heart_save_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SAVE_JSON = "key_game_save_v1"
        private const val CURRENT_VERSION = 1
    }

    fun saveGame(saveData: SaveData): Boolean {
        return try {
            val json = serializeToJson(saveData.copy(lastSavedTimestamp = System.currentTimeMillis()))
            prefs.edit().putString(KEY_SAVE_JSON, json.toString()).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadGame(): SaveData {
        val jsonString = prefs.getString(KEY_SAVE_JSON, null) ?: return SaveData()
        return try {
            val json = JSONObject(jsonString)
            deserializeFromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            SaveData()
        }
    }

    fun exportSaveString(saveData: SaveData): String {
        val json = serializeToJson(saveData)
        return Base64.encodeToString(json.toString().toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
    }

    fun importSaveString(encoded: String): SaveData? {
        return try {
            val decodedBytes = Base64.decode(encoded.trim(), Base64.NO_WRAP)
            val jsonString = String(decodedBytes, StandardCharsets.UTF_8)
            val json = JSONObject(jsonString)
            deserializeFromJson(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun resetGame(): SaveData {
        prefs.edit().clear().apply()
        return SaveData()
    }

    private fun serializeToJson(data: SaveData): JSONObject {
        val root = JSONObject()
        root.put("version", CURRENT_VERSION)
        root.put("lastSavedTimestamp", data.lastSavedTimestamp)
        root.put("activeBiomeId", data.activeBiomeId)

        // Currencies
        val currObj = JSONObject()
        currObj.put("echoes", data.currencies.echoes)
        currObj.put("shards", data.currencies.shards)
        currObj.put("essence", data.currencies.essence)
        currObj.put("timeAshes", data.currencies.timeAshes)
        currObj.put("voidCores", data.currencies.voidCores)
        currObj.put("ancientRunes", data.currencies.ancientRunes)
        root.put("currencies", currObj)

        // Core Upgrades
        val upgradesArr = JSONArray()
        data.coreUpgrades.forEach { u ->
            val obj = JSONObject()
            obj.put("id", u.id)
            obj.put("level", u.level)
            upgradesArr.put(obj)
        }
        root.put("coreUpgrades", upgradesArr)

        // Structures
        val structsArr = JSONArray()
        data.structures.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("count", s.count)
            structsArr.put(obj)
        }
        root.put("structures", structsArr)

        // Runes
        val runesArr = JSONArray()
        data.runes.forEach { r ->
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("isEquipped", r.isEquipped)
            runesArr.put(obj)
        }
        root.put("runes", runesArr)

        // Artifacts
        val artsArr = JSONArray()
        data.artifacts.forEach { a ->
            val obj = JSONObject()
            obj.put("id", a.id)
            obj.put("level", a.level)
            obj.put("isUnlocked", a.isUnlocked)
            artsArr.put(obj)
        }
        root.put("artifacts", artsArr)

        // Familiars
        val famsArr = JSONArray()
        data.familiars.forEach { f ->
            val obj = JSONObject()
            obj.put("id", f.id)
            obj.put("level", f.level)
            obj.put("isUnlocked", f.isUnlocked)
            famsArr.put(obj)
        }
        root.put("familiars", famsArr)

        // Biomes
        val biomesArr = JSONArray()
        data.biomes.forEach { b ->
            val obj = JSONObject()
            obj.put("id", b.id)
            obj.put("isUnlocked", b.isUnlocked)
            obj.put("isBossDefeated", b.isBossDefeated)
            biomesArr.put(obj)
        }
        root.put("biomes", biomesArr)

        // Constellations
        val constArr = JSONArray()
        data.constellations.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("rank", c.rank)
            constArr.put(obj)
        }
        root.put("constellations", constArr)

        // Quests
        val questsArr = JSONArray()
        data.quests.forEach { q ->
            val obj = JSONObject()
            obj.put("id", q.id)
            obj.put("isClaimed", q.isClaimed)
            questsArr.put(obj)
        }
        root.put("quests", questsArr)

        // Lore
        val loreArr = JSONArray()
        data.lore.forEach { l ->
            val obj = JSONObject()
            obj.put("id", l.id)
            obj.put("isUnlocked", l.isUnlocked)
            loreArr.put(obj)
        }
        root.put("lore", loreArr)

        // Statistics
        val statsObj = JSONObject()
        statsObj.put("totalClicks", data.statistics.totalClicks)
        statsObj.put("totalEchoesEarned", data.statistics.totalEchoesEarned)
        statsObj.put("totalBossesDefeated", data.statistics.totalBossesDefeated)
        statsObj.put("totalPrestigeCount", data.statistics.totalPrestigeCount)
        statsObj.put("totalTransmutes", data.statistics.totalTransmutes)
        statsObj.put("highestDps", data.statistics.highestDps)
        statsObj.put("playTimeSeconds", data.statistics.playTimeSeconds)
        statsObj.put("firstLaunchTimestamp", data.statistics.firstLaunchTimestamp)
        root.put("statistics", statsObj)

        // Settings
        val setObj = JSONObject()
        setObj.put("soundEnabled", data.settings.soundEnabled)
        setObj.put("hapticsEnabled", data.settings.hapticsEnabled)
        setObj.put("scientificNotation", data.settings.scientificNotation)
        setObj.put("particleQuality", data.settings.particleQuality.name)
        root.put("settings", setObj)

        return root
    }

    private fun deserializeFromJson(json: JSONObject): SaveData {
        val lastSaved = json.optLong("lastSavedTimestamp", System.currentTimeMillis())
        val activeBiomeId = json.optInt("activeBiomeId", 1)

        val currObj = json.optJSONObject("currencies")
        val currencies = if (currObj != null) {
            Currencies(
                echoes = currObj.optDouble("echoes", 0.0),
                shards = currObj.optDouble("shards", 0.0),
                essence = currObj.optDouble("essence", 0.0),
                timeAshes = currObj.optDouble("timeAshes", 0.0),
                voidCores = currObj.optDouble("voidCores", 0.0),
                ancientRunes = currObj.optDouble("ancientRunes", 0.0)
            )
        } else Currencies()

        // Core Upgrades merge with initial
        val upgradeLevels = mutableMapOf<String, Int>()
        val upgradesArr = json.optJSONArray("coreUpgrades")
        if (upgradesArr != null) {
            for (i in 0 until upgradesArr.length()) {
                val o = upgradesArr.getJSONObject(i)
                upgradeLevels[o.getString("id")] = o.optInt("level", 0)
            }
        }
        val coreUpgrades = GameContent.initialCoreUpgrades().map { u ->
            val lvl = upgradeLevels[u.id] ?: 0
            u.copy(level = lvl)
        }

        // Structures merge
        val structCounts = mutableMapOf<String, Int>()
        val structsArr = json.optJSONArray("structures")
        if (structsArr != null) {
            for (i in 0 until structsArr.length()) {
                val o = structsArr.getJSONObject(i)
                structCounts[o.getString("id")] = o.optInt("count", 0)
            }
        }
        val structures = GameContent.initialStructures().map { s ->
            val cnt = structCounts[s.id] ?: 0
            s.copy(count = cnt)
        }

        // Runes
        val runeEquipped = mutableMapOf<String, Boolean>()
        val runesArr = json.optJSONArray("runes")
        if (runesArr != null) {
            for (i in 0 until runesArr.length()) {
                val o = runesArr.getJSONObject(i)
                runeEquipped[o.getString("id")] = o.optBoolean("isEquipped", false)
            }
        }
        val runes = GameContent.initialRunes().map { r ->
            val eq = runeEquipped[r.id] ?: false
            r.copy(isEquipped = eq)
        }

        // Artifacts
        val artMap = mutableMapOf<String, Pair<Int, Boolean>>()
        val artsArr = json.optJSONArray("artifacts")
        if (artsArr != null) {
            for (i in 0 until artsArr.length()) {
                val o = artsArr.getJSONObject(i)
                artMap[o.getString("id")] = Pair(o.optInt("level", 0), o.optBoolean("isUnlocked", false))
            }
        }
        val artifacts = GameContent.initialArtifacts().map { a ->
            val pair = artMap[a.id]
            if (pair != null) a.copy(level = pair.first, isUnlocked = pair.second) else a
        }

        // Familiars
        val famMap = mutableMapOf<String, Pair<Int, Boolean>>()
        val famsArr = json.optJSONArray("familiars")
        if (famsArr != null) {
            for (i in 0 until famsArr.length()) {
                val o = famsArr.getJSONObject(i)
                famMap[o.getString("id")] = Pair(o.optInt("level", 0), o.optBoolean("isUnlocked", false))
            }
        }
        val familiars = GameContent.initialFamiliars().map { f ->
            val pair = famMap[f.id]
            if (pair != null) f.copy(level = pair.first, isUnlocked = pair.second) else f
        }

        // Biomes
        val biomeMap = mutableMapOf<Int, Pair<Boolean, Boolean>>()
        val biomesArr = json.optJSONArray("biomes")
        if (biomesArr != null) {
            for (i in 0 until biomesArr.length()) {
                val o = biomesArr.getJSONObject(i)
                biomeMap[o.getInt("id")] = Pair(o.optBoolean("isUnlocked", false), o.optBoolean("isBossDefeated", false))
            }
        }
        val biomes = GameContent.initialBiomes().map { b ->
            val pair = biomeMap[b.id]
            if (pair != null) b.copy(isUnlocked = pair.first, isBossDefeated = pair.second) else b
        }

        // Constellations
        val constMap = mutableMapOf<String, Int>()
        val constArr = json.optJSONArray("constellations")
        if (constArr != null) {
            for (i in 0 until constArr.length()) {
                val o = constArr.getJSONObject(i)
                constMap[o.getString("id")] = o.optInt("rank", 0)
            }
        }
        val constellations = GameContent.initialConstellations().map { c ->
            val r = constMap[c.id] ?: 0
            c.copy(rank = r)
        }

        // Quests
        val questMap = mutableMapOf<String, Boolean>()
        val questsArr = json.optJSONArray("quests")
        if (questsArr != null) {
            for (i in 0 until questsArr.length()) {
                val o = questsArr.getJSONObject(i)
                questMap[o.getString("id")] = o.optBoolean("isClaimed", false)
            }
        }
        val quests = GameContent.initialQuests().map { q ->
            val cl = questMap[q.id] ?: false
            q.copy(isClaimed = cl)
        }

        // Lore
        val loreMap = mutableMapOf<Int, Boolean>()
        val loreArr = json.optJSONArray("lore")
        if (loreArr != null) {
            for (i in 0 until loreArr.length()) {
                val o = loreArr.getJSONObject(i)
                loreMap[o.getInt("id")] = o.optBoolean("isUnlocked", false)
            }
        }
        val lore = GameContent.initialLore().map { l ->
            val un = loreMap[l.id] ?: l.isUnlocked
            l.copy(isUnlocked = un)
        }

        // Statistics
        val statsObj = json.optJSONObject("statistics")
        val stats = if (statsObj != null) {
            GameStatistics(
                totalClicks = statsObj.optLong("totalClicks", 0L),
                totalEchoesEarned = statsObj.optDouble("totalEchoesEarned", 0.0),
                totalBossesDefeated = statsObj.optInt("totalBossesDefeated", 0),
                totalPrestigeCount = statsObj.optInt("totalPrestigeCount", 0),
                totalTransmutes = statsObj.optInt("totalTransmutes", 0),
                highestDps = statsObj.optDouble("highestDps", 0.0),
                playTimeSeconds = statsObj.optLong("playTimeSeconds", 0L),
                firstLaunchTimestamp = statsObj.optLong("firstLaunchTimestamp", System.currentTimeMillis())
            )
        } else GameStatistics()

        // Settings
        val setObj = json.optJSONObject("settings")
        val settings = if (setObj != null) {
            val pqName = setObj.optString("particleQuality", ParticleQuality.HIGH.name)
            val pq = try { ParticleQuality.valueOf(pqName) } catch (_: Exception) { ParticleQuality.HIGH }
            GameSettings(
                soundEnabled = setObj.optBoolean("soundEnabled", true),
                hapticsEnabled = setObj.optBoolean("hapticsEnabled", true),
                scientificNotation = setObj.optBoolean("scientificNotation", false),
                particleQuality = pq
            )
        } else GameSettings()

        return SaveData(
            version = CURRENT_VERSION,
            lastSavedTimestamp = lastSaved,
            currencies = currencies,
            coreUpgrades = coreUpgrades,
            structures = structures,
            runes = runes,
            artifacts = artifacts,
            familiars = familiars,
            biomes = biomes,
            constellations = constellations,
            quests = quests,
            lore = lore,
            statistics = stats,
            settings = settings,
            activeBiomeId = activeBiomeId
        )
    }
}
