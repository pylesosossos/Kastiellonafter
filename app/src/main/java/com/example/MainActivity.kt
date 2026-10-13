package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

enum class GameTab(
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
) {
    CORE("Ядро", Icons.Filled.FlashOn, Icons.Outlined.FlashOn),
    STRUCTURES("Строения", Icons.Filled.PrecisionManufacturing, Icons.Outlined.PrecisionManufacturing),
    ALCHEMY("Алхимия", Icons.Filled.AutoFixHigh, Icons.Outlined.AutoFixHigh),
    EXPEDITIONS("Походы", Icons.Filled.Explore, Icons.Outlined.Explore),
    GRIMOIRE("Гримуар", Icons.Filled.AutoStories, Icons.Outlined.AutoStories)
}

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainGameScreen(viewModel = viewModel)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveGameNow()
    }
}

@Composable
fun MainGameScreen(viewModel: GameViewModel) {
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    val buyMultiplier by viewModel.buyMultiplier.collectAsStateWithLifecycle()
    val floatingTexts by viewModel.floatingTexts.collectAsStateWithLifecycle()
    val activeBoss by viewModel.activeBoss.collectAsStateWithLifecycle()
    val offlineReward by viewModel.offlineReward.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()
    val voidSurgeActive by viewModel.voidSurgeActive.collectAsStateWithLifecycle()
    val bloodEclipseActive by viewModel.bloodEclipseActive.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(GameTab.CORE) }

    // Intercept back button on secondary screens
    if (currentTab != GameTab.CORE) {
        BackHandler {
            currentTab = GameTab.CORE
        }
    }

    val dps = remember(gameState, voidSurgeActive) { viewModel.calculateTotalDps() }
    val clickPower = remember(gameState, voidSurgeActive) { viewModel.calculateClickPower() }
    val critChance = remember(gameState) { viewModel.calculateCritChance() }
    val critMultiplier = remember(gameState) { viewModel.calculateCritMultiplier() }
    val prestigeGain = remember(gameState) { viewModel.calculatePrestigeGain() }

    Box(modifier = Modifier.fillMaxSize()) {
        // Multi-layer procedural cosmic canvas background
        CosmicBackgroundCanvas(particleQuality = gameState.settings.particleQuality)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.statusBars,
            topBar = {
                Column {
                    ResourceHeaderBar(
                        currencies = gameState.currencies,
                        dps = dps,
                        scientific = gameState.settings.scientificNotation
                    )
                    BannerNotification(message = bannerMessage)
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = ObsidianSurface,
                    contentColor = StarlightSilver,
                    windowInsets = WindowInsets.navigationBars,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    GameTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontFamily = CinzelFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = StarlightSilver,
                                selectedTextColor = VoidPurpleLight,
                                indicatorColor = VoidPurpleDark,
                                unselectedIconColor = GhostGray,
                                unselectedTextColor = GhostGray
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    GameTab.CORE -> {
                        CoreScreen(
                            currencies = gameState.currencies,
                            coreUpgrades = gameState.coreUpgrades,
                            floatingTexts = floatingTexts,
                            clickPower = clickPower,
                            critChance = critChance,
                            critMultiplier = critMultiplier,
                            buyMultiplier = buyMultiplier,
                            scientific = gameState.settings.scientificNotation,
                            isVoidSurge = voidSurgeActive,
                            isBloodEclipse = bloodEclipseActive,
                            onVoidHeartClicked = { viewModel.onVoidHeartClicked() },
                            onBuyUpgrade = { viewModel.buyCoreUpgrade(it) },
                            onSetBuyMultiplier = { viewModel.setBuyMultiplier(it) },
                            onActivateVoidSurge = { viewModel.activateVoidSurge() },
                            onActivateBloodEclipse = { viewModel.activateBloodEclipse() }
                        )
                    }
                    GameTab.STRUCTURES -> {
                        StructuresScreen(
                            structures = gameState.structures,
                            availableEchoes = gameState.currencies.echoes,
                            totalDps = dps,
                            buyMultiplier = buyMultiplier,
                            scientific = gameState.settings.scientificNotation,
                            onBuyStructure = { viewModel.buyStructure(it) },
                            onSetBuyMultiplier = { viewModel.setBuyMultiplier(it) }
                        )
                    }
                    GameTab.ALCHEMY -> {
                        AlchemyAndRunesScreen(
                            currencies = gameState.currencies,
                            runes = gameState.runes,
                            artifacts = gameState.artifacts,
                            scientific = gameState.settings.scientificNotation,
                            onTransmute = { viewModel.transmute(it) },
                            onToggleRune = { viewModel.toggleRune(it) },
                            onUpgradeArtifact = { viewModel.upgradeArtifact(it) }
                        )
                    }
                    GameTab.EXPEDITIONS -> {
                        ExpeditionsScreen(
                            currencies = gameState.currencies,
                            biomes = gameState.biomes,
                            familiars = gameState.familiars,
                            activeBiomeId = gameState.activeBiomeId,
                            activeBoss = activeBoss,
                            scientific = gameState.settings.scientificNotation,
                            onSelectBiome = { viewModel.setActiveBiome(it) },
                            onStartBossFight = { viewModel.startBossFight(it) },
                            onUpgradeFamiliar = { viewModel.upgradeFamiliar(it) }
                        )
                    }
                    GameTab.GRIMOIRE -> {
                        GrimoireScreen(
                            gameState = gameState,
                            prestigeGain = prestigeGain,
                            onPerformPrestige = { viewModel.performPrestige() },
                            onUpgradeConstellation = { viewModel.upgradeConstellation(it) },
                            onClaimQuest = { viewModel.claimQuest(it) },
                            isQuestCompleted = { viewModel.isQuestCompleted(it, gameState) },
                            onUpdateSettings = { s, h, sc, p -> viewModel.updateSettings(s, h, sc, p) },
                            onSaveGameNow = { viewModel.saveGameNow() },
                            onExportSave = { viewModel.exportSaveString() },
                            onImportSave = { viewModel.importSaveString(it) },
                            onHardReset = { viewModel.hardResetGame() }
                        )
                    }
                }
            }
        }

        // Offline reward welcome modal
        if (offlineReward != null) {
            OfflineProgressDialog(
                rewardInfo = offlineReward!!,
                scientific = gameState.settings.scientificNotation,
                onDismiss = { viewModel.dismissOfflineReward() }
            )
        }
    }
}
