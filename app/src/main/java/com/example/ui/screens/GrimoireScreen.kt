package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.SaveData
import com.example.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GrimoireScreen(
    gameState: SaveData,
    prestigeGain: Double,
    onPerformPrestige: () -> Unit,
    onUpgradeConstellation: (String) -> Unit,
    onClaimQuest: (String) -> Unit,
    isQuestCompleted: (Quest) -> Boolean,
    onUpdateSettings: (Boolean?, Boolean?, Boolean?, ParticleQuality?) -> Unit,
    onSaveGameNow: () -> Boolean,
    onExportSave: () -> String,
    onImportSave: (String) -> Boolean,
    onHardReset: () -> Unit
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Prestige & Constellations, 1: Quests, 2: Lore, 3: Settings & Stats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Tab Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianSurface)
                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                .padding(2.dp)
        ) {
            val tabs = listOf("ПЕЧАТЬ", "ЗАДАНИЯ", "ЛОР", "ОПЦИИ")
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedSubTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) VoidPurpleDark else Color.Transparent)
                        .clickable { selectedSubTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isSelected) StarlightSilver else GhostGray
                    )
                }
            }
        }

        when (selectedSubTab) {
            0 -> PrestigeTab(
                gameState = gameState,
                prestigeGain = prestigeGain,
                onPerformPrestige = onPerformPrestige,
                onUpgradeConstellation = onUpgradeConstellation
            )
            1 -> QuestsTab(
                gameState = gameState,
                onClaimQuest = onClaimQuest,
                isQuestCompleted = isQuestCompleted
            )
            2 -> LoreTab(gameState = gameState)
            3 -> SettingsTab(
                gameState = gameState,
                onUpdateSettings = onUpdateSettings,
                onSaveGameNow = onSaveGameNow,
                onExportSave = onExportSave,
                onImportSave = onImportSave,
                onHardReset = onHardReset
            )
        }
    }
}

@Composable
private fun PrestigeTab(
    gameState: SaveData,
    prestigeGain: Double,
    onPerformPrestige: () -> Unit,
    onUpgradeConstellation: (String) -> Unit
) {
    var showPrestigeConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, AmberRelic.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ПРОРЫВ ПЕЧАТИ (ПРЕСТИЖ)",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = AmberRelicLight
                )
                Text(
                    text = "Разорвите текущую ткань реальности, чтобы закалить дух в предвечном пламени.",
                    fontSize = 11.sp,
                    color = GhostGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Награда: +${BigNum.format(prestigeGain, gameState.settings.scientificNotation)}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AmberRelicLight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "✵ Пепла Времени", color = AmberRelicLight, fontSize = 14.sp)
                }

                Text(
                    text = "Будет сброшено: Эхо, Осколки, Сооружения и базовые улучшения.\nСохранится: Созвездия, Артефакты, Ядра Пустоты, Руны, Лор и Статистика.",
                    fontSize = 10.sp,
                    color = GhostGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Button(
                    onClick = { showPrestigeConfirm = true },
                    enabled = prestigeGain > 0.0,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberRelicDark,
                        disabledContainerColor = ObsidianSurfaceVariant
                    ),
                    modifier = Modifier.testTag("break_seal_button")
                ) {
                    Text(
                        text = "СОВЕРШИТЬ ПРОРЫВ",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (prestigeGain > 0.0) StarlightSilver else DimAsh
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Constellations Section
        item {
            Text(
                text = "СОЗВЕЗДИЯ ВЕЧНОСТИ",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = StarlightSilver,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Постоянные мета-улучшения за Пепел Времени (✵). Сохраняются навсегда.",
                fontSize = 11.sp,
                color = GhostGray,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        items(gameState.constellations, key = { it.id }) { perk ->
            val cost = perk.costAshes * Math.pow(perk.costMultiplier, perk.rank.toDouble())
            val canUpgrade = gameState.currencies.timeAshes >= cost && perk.rank < perk.maxRank

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = perk.name,
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = StarlightSilver
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ранг ${perk.rank}/${perk.maxRank}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = AmberRelicLight
                        )
                    }
                    Text(
                        text = perk.description,
                        fontSize = 11.sp,
                        color = GhostGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Button(
                    onClick = { onUpgradeConstellation(perk.id) },
                    enabled = canUpgrade,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberRelicDark,
                        disabledContainerColor = ObsidianSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("upgrade_perk_${perk.id}")
                ) {
                    Text(
                        text = if (perk.rank >= perk.maxRank) "MAX" else "✵ ${BigNum.format(cost, gameState.settings.scientificNotation)}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canUpgrade) StarlightSilver else DimAsh
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showPrestigeConfirm) {
        AlertDialog(
            onDismissRequest = { showPrestigeConfirm = false },
            title = {
                Text(
                    text = "Подтверждение Прорыва",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = AmberRelicLight
                )
            },
            text = {
                Text(
                    text = "Вы уверены? Ваши сооружения и текущее Эхо будут сброшены, взамен вы получите +${BigNum.format(prestigeGain, gameState.settings.scientificNotation)} Пепла Времени для созвездий.",
                    color = StarlightSilver
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPrestigeConfirm = false
                        onPerformPrestige()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberRelicDark)
                ) {
                    Text("ПЕРЕРОДИТЬСЯ", fontFamily = CinzelFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrestigeConfirm = false }) {
                    Text("ОТМЕНА", color = GhostGray)
                }
            },
            containerColor = ObsidianSurface
        )
    }
}

@Composable
private fun QuestsTab(
    gameState: SaveData,
    onClaimQuest: (String) -> Unit,
    isQuestCompleted: (Quest) -> Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        items(gameState.quests, key = { it.id }) { quest ->
            val completed = isQuestCompleted(quest)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianSurface)
                    .border(
                        1.dp,
                        if (quest.isClaimed) DimAsh.copy(alpha = 0.2f) else if (completed) VoidEmerald else SlateBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = quest.title,
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (quest.isClaimed) DimAsh else StarlightSilver
                    )
                    Text(
                        text = quest.description,
                        fontSize = 11.sp,
                        color = GhostGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = "Награда: +${BigNum.format(quest.rewardAmount, gameState.settings.scientificNotation)} ${quest.rewardCurrency.displayName}",
                        fontSize = 10.sp,
                        color = if (quest.isClaimed) DimAsh else AstralCyanLight,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Button(
                    onClick = { onClaimQuest(quest.id) },
                    enabled = completed && !quest.isClaimed,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VoidEmerald,
                        disabledContainerColor = ObsidianSurfaceVariant
                    ),
                    modifier = Modifier.testTag("claim_quest_${quest.id}")
                ) {
                    Text(
                        text = if (quest.isClaimed) "ВЫПОЛНЕНО" else if (completed) "ЗАБРАТЬ" else "В ПРОЦЕССЕ",
                        fontFamily = CinzelFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (completed && !quest.isClaimed) AbyssBlack else DimAsh
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LoreTab(gameState: SaveData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        items(gameState.lore, key = { it.id }) { chapter ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSurface)
                    .border(
                        1.dp,
                        if (chapter.isUnlocked) VoidPurpleDark.copy(alpha = 0.5f) else SlateBorder.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = chapter.title,
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (chapter.isUnlocked) AmberRelicLight else DimAsh
                    )
                    Text(
                        text = if (chapter.isUnlocked) "ОТКРЫТО" else "ЗАПЕЧАТАНО",
                        fontSize = 10.sp,
                        color = if (chapter.isUnlocked) AstralCyanLight else DimAsh
                    )
                }

                if (chapter.isUnlocked) {
                    Text(
                        text = chapter.text,
                        fontSize = 12.sp,
                        color = StarlightSilver,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    Text(
                        text = "Условие снятия печати: ${chapter.unlockCondition}",
                        fontSize = 11.sp,
                        color = GhostGray,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SettingsTab(
    gameState: SaveData,
    onUpdateSettings: (Boolean?, Boolean?, Boolean?, ParticleQuality?) -> Unit,
    onSaveGameNow: () -> Boolean,
    onExportSave: () -> String,
    onImportSave: (String) -> Boolean,
    onHardReset: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var showExportDialog by remember { mutableStateOf(false) }
    var exportString by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }
    var importInputText by remember { mutableStateOf("") }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showResetDoubleConfirm by remember { mutableStateOf(false) }
    var saveStatusMsg by remember { mutableStateOf<String?>(null) }

    val dateFormat = remember { SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault()) }
    val lastSaveStr = dateFormat.format(Date(gameState.lastSavedTimestamp))

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Stats
        item {
            Text(
                text = "СТАТИСТИКА БЕЗДНЫ",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = StarlightSilver,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatRow("Всего кликов по Сердцу:", gameState.statistics.totalClicks.toString())
                StatRow("Всего добыто Эха:", BigNum.format(gameState.statistics.totalEchoesEarned, gameState.settings.scientificNotation))
                StatRow("Повержено боссов:", gameState.statistics.totalBossesDefeated.toString())
                StatRow("Совершено Прорывов (Престижей):", gameState.statistics.totalPrestigeCount.toString())
                StatRow("Трансмутаций проведено:", gameState.statistics.totalTransmutes.toString())
                StatRow("Рекордная генерация:", "${BigNum.format(gameState.statistics.highestDps, gameState.settings.scientificNotation)} /сек")
                StatRow("Последнее сохранение:", lastSaveStr)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Toggles
        item {
            Text(
                text = "НАСТРОЙКИ",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = StarlightSilver,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Звуковые эффекты", color = StarlightSilver, fontSize = 13.sp)
                    Switch(
                        checked = gameState.settings.soundEnabled,
                        onCheckedChange = { onUpdateSettings(it, null, null, null) },
                        modifier = Modifier.testTag("toggle_sound")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Тактильный отклик (вибрация)", color = StarlightSilver, fontSize = 13.sp)
                    Switch(
                        checked = gameState.settings.hapticsEnabled,
                        onCheckedChange = { onUpdateSettings(null, it, null, null) },
                        modifier = Modifier.testTag("toggle_haptics")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Научная нотация чисел (1.23e6)", color = StarlightSilver, fontSize = 13.sp)
                    Switch(
                        checked = gameState.settings.scientificNotation,
                        onCheckedChange = { onUpdateSettings(null, null, it, null) },
                        modifier = Modifier.testTag("toggle_scientific")
                    )
                }

                // Particle density
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Частицы и эффекты", color = StarlightSilver, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ParticleQuality.values().forEach { q ->
                            val isSel = gameState.settings.particleQuality == q
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) VoidPurpleDark else ObsidianSurfaceVariant)
                                    .clickable { onUpdateSettings(null, null, null, q) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(q.label, fontSize = 10.sp, color = if (isSel) StarlightSilver else GhostGray)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Save & Data Management
        item {
            Text(
                text = "УПРАВЛЕНИЕ ДАННЫМИ",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = StarlightSilver,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val ok = onSaveGameNow()
                        saveStatusMsg = if (ok) "Сохранено!" else "Ошибка сохранения"
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VoidPurpleDark),
                    modifier = Modifier.weight(1f).testTag("save_now_button")
                ) {
                    Text("СОХРАНИТЬ", fontFamily = CinzelFontFamily, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        exportString = onExportSave()
                        showExportDialog = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceVariant),
                    modifier = Modifier.weight(1f).testTag("export_save_button")
                ) {
                    Text("ЭКСПОРТ", fontFamily = CinzelFontFamily, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        importInputText = ""
                        showImportDialog = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceVariant),
                    modifier = Modifier.weight(1f).testTag("import_save_button")
                ) {
                    Text("ИМПОРТ", fontFamily = CinzelFontFamily, fontSize = 11.sp)
                }
            }

            if (saveStatusMsg != null) {
                Text(
                    text = saveStatusMsg ?: "",
                    color = AstralCyanLight,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showResetConfirm = true },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BloodCrimsonLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, BloodCrimsonDark),
                modifier = Modifier.fillMaxWidth().testTag("reset_game_button")
            ) {
                Text("ПОЛНЫЙ СБРОС ПРОГРЕССА", fontFamily = CinzelFontFamily, fontSize = 12.sp)
            }
        }
    }

    // Export Dialog
    if (showExportDialog) {
        Dialog(onDismissRequest = { showExportDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ЭКСПОРТ СОХРАНЕНИЯ",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = StarlightSilver
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Скопируйте эту строку в буфер обмена для переноса на другое устройство:",
                        fontSize = 11.sp,
                        color = GhostGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = exportString,
                        onValueChange = {},
                        readOnly = true,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = AbyssBlack,
                            unfocusedContainerColor = AbyssBlack
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(exportString))
                                showExportDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VoidPurpleDark)
                        ) {
                            Text("СКОПИРОВАТЬ", fontFamily = CinzelFontFamily)
                        }
                        TextButton(onClick = { showExportDialog = false }) {
                            Text("ЗАКРЫТЬ", color = GhostGray)
                        }
                    }
                }
            }
        }
    }

    // Import Dialog
    if (showImportDialog) {
        Dialog(onDismissRequest = { showImportDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, SlateBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ИМПОРТ СОХРАНЕНИЯ",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = StarlightSilver
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Вставьте сюда ранее экспортированную строку сохранения:",
                        fontSize = 11.sp,
                        color = GhostGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = importInputText,
                        onValueChange = { importInputText = it },
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = AbyssBlack,
                            unfocusedContainerColor = AbyssBlack
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (onImportSave(importInputText)) {
                                    showImportDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AstralCyanDark)
                        ) {
                            Text("ЗАГРУЗИТЬ", fontFamily = CinzelFontFamily)
                        }
                        TextButton(onClick = { showImportDialog = false }) {
                            Text("ОТМЕНА", color = GhostGray)
                        }
                    }
                }
            }
        }
    }

    // Reset Confirm 1
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Сброс прогресса", color = BloodCrimsonLight, fontFamily = CinzelFontFamily) },
            text = { Text("Вы собираетесь полностью удалить текущее сохранение. Весь прогресс, улучшения и артефакты будут стерты навсегда.", color = StarlightSilver) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        showResetDoubleConfirm = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BloodCrimsonDark)
                ) {
                    Text("ДА, ПРОДОЛЖИТЬ", fontFamily = CinzelFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("ОТМЕНА", color = GhostGray)
                }
            },
            containerColor = ObsidianSurface
        )
    }

    // Reset Double Confirm
    if (showResetDoubleConfirm) {
        AlertDialog(
            onDismissRequest = { showResetDoubleConfirm = false },
            title = { Text("ФИНАЛЬНОЕ ПОДТВЕРЖДЕНИЕ", color = BloodCrimson, fontFamily = CinzelFontFamily, fontWeight = FontWeight.Bold) },
            text = { Text("Вы абсолютно уверены? Это действие невозможно отменить!", color = StarlightSilver) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDoubleConfirm = false
                        onHardReset()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BloodCrimson)
                ) {
                    Text("СТЕРЕТЬ ВСЁ", fontFamily = CinzelFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDoubleConfirm = false }) {
                    Text("ОТМЕНА", color = GhostGray)
                }
            },
            containerColor = ObsidianSurface
        )
    }
}

@Composable
private fun StatRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = GhostGray, fontSize = 11.sp)
        Text(text = value, fontFamily = JetBrainsMonoFontFamily, color = StarlightSilver, fontSize = 11.sp)
    }
}
