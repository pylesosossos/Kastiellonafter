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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameContent
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun AlchemyAndRunesScreen(
    currencies: Currencies,
    runes: List<Rune>,
    artifacts: List<Artifact>,
    scientific: Boolean,
    onTransmute: (String) -> Unit,
    onToggleRune: (String) -> Unit,
    onUpgradeArtifact: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Alchemy, 1: Runes & Artifacts

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        // Sub-navigation tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ObsidianSurface)
                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedTab == 0) VoidPurpleDark else Color.Transparent)
                    .clickable { selectedTab = 0 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "АЛХИМИЯ ХАОСА",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (selectedTab == 0) StarlightSilver else GhostGray
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedTab == 1) VoidPurpleDark else Color.Transparent)
                    .clickable { selectedTab = 1 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "РУНЫ И АРТЕФАКТЫ",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (selectedTab == 1) StarlightSilver else GhostGray
                )
            }
        }

        if (selectedTab == 0) {
            AlchemyTab(currencies = currencies, scientific = scientific, onTransmute = onTransmute)
        } else {
            RunesAndArtifactsTab(
                currencies = currencies,
                runes = runes,
                artifacts = artifacts,
                scientific = scientific,
                onToggleRune = onToggleRune,
                onUpgradeArtifact = onUpgradeArtifact
            )
        }
    }
}

@Composable
private fun AlchemyTab(
    currencies: Currencies,
    scientific: Boolean,
    onTransmute: (String) -> Unit
) {
    val recipes = GameContent.initialAlchemyRecipes()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Text(
                text = "ТРАНСМУТАЦИЯ МАТЕРИИ",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = StarlightSilver,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Преобразуйте базовые ресурсы в редкие катализаторы. Есть шанс критического удвоения выхода!",
                fontSize = 12.sp,
                color = GhostGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(recipes, key = { it.id }) { recipe ->
            val canAfford = when (recipe.inputCurrency) {
                CurrencyType.ECHOES -> currencies.echoes >= recipe.inputCost
                CurrencyType.SHARDS -> currencies.shards >= recipe.inputCost
                CurrencyType.ESSENCE -> currencies.essence >= recipe.inputCost
                CurrencyType.TIME_ASHES -> currencies.timeAshes >= recipe.inputCost
                CurrencyType.VOID_CORES -> currencies.voidCores >= recipe.inputCost
                CurrencyType.ANCIENT_RUNES -> currencies.ancientRunes >= recipe.inputCost
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, if (canAfford) AstralCyanDark.copy(alpha = 0.5f) else SlateBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = recipe.name,
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = StarlightSilver
                    )
                    Text(
                        text = recipe.description,
                        fontSize = 11.sp,
                        color = GhostGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Расход: ${BigNum.format(recipe.inputCost, scientific)} ${recipe.inputCurrency.symbol}",
                            fontSize = 11.sp,
                            color = BloodCrimsonLight
                        )
                        Text(
                            text = "→ Выход: +${BigNum.format(recipe.outputAmount, scientific)} ${recipe.outputCurrency.symbol}",
                            fontSize = 11.sp,
                            color = AstralCyanLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = { onTransmute(recipe.id) },
                    enabled = canAfford,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VoidPurpleDark,
                        disabledContainerColor = ObsidianSurfaceVariant
                    ),
                    modifier = Modifier.testTag("transmute_${recipe.id}")
                ) {
                    Text(
                        text = "СОЗДАТЬ",
                        fontFamily = CinzelFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canAfford) StarlightSilver else DimAsh
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun RunesAndArtifactsTab(
    currencies: Currencies,
    runes: List<Rune>,
    artifacts: List<Artifact>,
    scientific: Boolean,
    onToggleRune: (String) -> Unit,
    onUpgradeArtifact: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Runes Section
        item {
            val equippedCount = runes.count { it.isEquipped }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ДРЕВНИЕ РУНЫ",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = StarlightSilver
                )
                Text(
                    text = "Установлено: $equippedCount / 3",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 12.sp,
                    color = VoidEmerald
                )
            }
        }

        items(runes, key = { it.id }) { rune ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (rune.isEquipped) ObsidianSurfaceVariant else ObsidianSurface)
                    .border(
                        1.dp,
                        if (rune.isEquipped) VoidEmerald else SlateBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = rune.name,
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = StarlightSilver
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Тир ${rune.tier}",
                            fontSize = 10.sp,
                            color = AmberRelicLight
                        )
                    }
                    Text(
                        text = "Эффект: +${rune.bonusPercent.toInt()}% (${rune.type.title})",
                        fontSize = 11.sp,
                        color = AstralCyanLight,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Button(
                    onClick = { onToggleRune(rune.id) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (rune.isEquipped) VoidEmerald.copy(alpha = 0.8f) else VoidPurpleDark
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("toggle_rune_${rune.id}")
                ) {
                    Text(
                        text = if (rune.isEquipped) "СНЯТЬ" else "ᛟ ${BigNum.format(rune.costRunes, scientific)}",
                        fontFamily = CinzelFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarlightSilver
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Artifacts Section
        item {
            Text(
                text = "АРТЕФАКТЫ ВЕЧНОСТИ",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = StarlightSilver,
                modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
            )
            Text(
                text = "Легендарные реликвии боссов. Улучшаются за Ядра Пустоты (⦿).",
                fontSize = 12.sp,
                color = GhostGray,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        items(artifacts, key = { it.id }) { art ->
            val upgradeCost = art.baseUpgradeCostCores * (art.level + 1)
            val canUpgrade = currencies.voidCores >= upgradeCost && art.level < art.maxLevel

            val rarityColor = when (art.rarity) {
                ArtifactRarity.ELDRITCH -> Color(0xFFE11D48)
                ArtifactRarity.MYTHIC -> Color(0xFFA855F7)
                ArtifactRarity.EPIC -> Color(0xFF06B6D4)
                ArtifactRarity.RARE -> Color(0xFFF59E0B)
                ArtifactRarity.COMMON -> GhostGray
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = art.name,
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = StarlightSilver
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "[${art.rarity.label}]",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = rarityColor
                        )
                    }

                    Text(
                        text = "ур. ${art.level}/${art.maxLevel}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 12.sp,
                        color = VoidPurpleLight
                    )
                }

                Text(
                    text = art.lore,
                    fontSize = 11.sp,
                    color = GhostGray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = art.effectDescription,
                        fontSize = 11.sp,
                        color = AstralCyanLight,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )

                    Button(
                        onClick = { onUpgradeArtifact(art.id) },
                        enabled = canUpgrade,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VoidPurpleDark,
                            disabledContainerColor = ObsidianSurfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                        modifier = Modifier.testTag("upgrade_artifact_${art.id}")
                    ) {
                        Text(
                            text = if (art.level >= art.maxLevel) "MAX" else "⦿ ${BigNum.format(upgradeCost, scientific)}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canUpgrade) AmberRelicLight else DimAsh
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
