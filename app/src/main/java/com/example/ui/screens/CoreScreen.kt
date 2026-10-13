package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BigNum
import com.example.model.CoreUpgrade
import com.example.model.Currencies
import com.example.ui.components.BuyMultiplierToggle
import com.example.ui.components.VoidHeartCanvas
import com.example.ui.theme.*
import com.example.viewmodel.BuyMultiplier
import com.example.viewmodel.FloatingText

@Composable
fun CoreScreen(
    currencies: Currencies,
    coreUpgrades: List<CoreUpgrade>,
    floatingTexts: List<FloatingText>,
    clickPower: Double,
    critChance: Double,
    critMultiplier: Double,
    buyMultiplier: BuyMultiplier,
    scientific: Boolean,
    isVoidSurge: Boolean,
    isBloodEclipse: Boolean,
    onVoidHeartClicked: () -> Unit,
    onBuyUpgrade: (String) -> Unit,
    onSetBuyMultiplier: (BuyMultiplier) -> Unit,
    onActivateVoidSurge: () -> Unit,
    onActivateBloodEclipse: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Central Void Heart
        item {
            VoidHeartCanvas(
                floatingTexts = floatingTexts,
                isVoidSurge = isVoidSurge,
                isBloodEclipse = isBloodEclipse,
                onClick = onVoidHeartClicked
            )
        }

        // Stats Badges (Click Power, Crit Chance, Crit Mult)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatPill(
                    label = "СИЛА КЛИКА",
                    value = BigNum.format(clickPower, scientific),
                    color = VoidPurpleLight,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "ШАНС КРИТА",
                    value = "${(critChance * 100).toInt()}%",
                    color = BloodCrimsonLight,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "МНОЖИТЕЛЬ",
                    value = "×${String.format(java.util.Locale.US, "%.1f", critMultiplier)}",
                    color = AmberRelicLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Active Skills Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActiveSkillButton(
                    title = "ЯРОСТЬ БЕЗДНЫ",
                    subtitle = if (isVoidSurge) "АКТИВНО (УРОН ×4)" else "ГОТОВО (20 сек)",
                    isActive = isVoidSurge,
                    activeColor = AstralCyan,
                    onClick = onActivateVoidSurge,
                    modifier = Modifier.weight(1f)
                )
                ActiveSkillButton(
                    title = "КРОВАВОЕ ЗАТМЕНИЕ",
                    subtitle = if (isBloodEclipse) "АКТИВНО (100% КРИТ)" else "ГОТОВО (15 сек)",
                    isActive = isBloodEclipse,
                    activeColor = BloodCrimson,
                    onClick = onActivateBloodEclipse,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section Header with Multiplier Toggle
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "УЛУЧШЕНИЯ ЯДРА",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = StarlightSilver
                )
                BuyMultiplierToggle(
                    currentMultiplier = buyMultiplier,
                    onSelect = onSetBuyMultiplier
                )
            }
        }

        // Upgrades list
        items(coreUpgrades, key = { it.id }) { upgrade ->
            CoreUpgradeItem(
                upgrade = upgrade,
                availableEchoes = currencies.echoes,
                buyMultiplier = buyMultiplier,
                scientific = scientific,
                onBuy = { onBuyUpgrade(upgrade.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianSurface)
            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = color
            )
            Text(
                text = label,
                fontFamily = CinzelFontFamily,
                fontSize = 9.sp,
                color = GhostGray
            )
        }
    }
}

@Composable
private fun ActiveSkillButton(
    title: String,
    subtitle: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) activeColor.copy(alpha = 0.25f) else ObsidianSurface)
            .border(1.dp, if (isActive) activeColor else SlateBorder, RoundedCornerShape(10.dp))
            .clickable(enabled = !isActive) { onClick() }
            .padding(vertical = 8.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isActive) activeColor else StarlightSilver
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = if (isActive) activeColor else GhostGray
            )
        }
    }
}

@Composable
private fun CoreUpgradeItem(
    upgrade: CoreUpgrade,
    availableEchoes: Double,
    buyMultiplier: BuyMultiplier,
    scientific: Boolean,
    onBuy: () -> Unit
) {
    val countToBuy = if (buyMultiplier == BuyMultiplier.MAX) {
        BigNum.calculateMaxAffordable(upgrade.baseCost, upgrade.costMultiplier, upgrade.level, availableEchoes)
    } else {
        buyMultiplier.amount
    }

    val displayCount = if (countToBuy <= 0) 1 else countToBuy
    val totalCost = BigNum.calculateTotalCost(upgrade.baseCost, upgrade.costMultiplier, upgrade.level, displayCount)
    val canAfford = availableEchoes >= totalCost && displayCount > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ObsidianSurface)
            .border(1.dp, if (canAfford) VoidPurpleDark.copy(alpha = 0.6f) else SlateBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = upgrade.name,
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = StarlightSilver
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ур. ${upgrade.level}",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = VoidPurpleLight
                )
            }
            Text(
                text = upgrade.description,
                fontSize = 11.sp,
                color = GhostGray,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Bonuses summary
            Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (upgrade.clickPowerAdd > 0.0) {
                    Text("+${BigNum.format(upgrade.clickPowerAdd * displayCount, scientific)} клик", fontSize = 10.sp, color = AstralCyanLight)
                }
                if (upgrade.critChanceAdd > 0.0) {
                    Text("+${(upgrade.critChanceAdd * displayCount * 100).toInt()}% крит", fontSize = 10.sp, color = BloodCrimsonLight)
                }
                if (upgrade.clickMultiplierAdd > 0.0) {
                    Text("+${(upgrade.clickMultiplierAdd * displayCount * 100).toInt()}% урон", fontSize = 10.sp, color = AmberRelicLight)
                }
            }
        }

        Button(
            onClick = onBuy,
            enabled = canAfford,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VoidPurpleDark,
                disabledContainerColor = ObsidianSurfaceVariant
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("buy_upgrade_${upgrade.id}")
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "+$displayCount",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canAfford) StarlightSilver else DimAsh
                )
                Text(
                    text = "✦ ${BigNum.format(totalCost, scientific)}",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.sp,
                    color = if (canAfford) AstralCyanLight else DimAsh
                )
            }
        }
    }
}
