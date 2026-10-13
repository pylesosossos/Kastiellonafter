package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BigNum
import com.example.model.Structure
import com.example.ui.components.BuyMultiplierToggle
import com.example.ui.theme.*
import com.example.viewmodel.BuyMultiplier

@Composable
fun StructuresScreen(
    structures: List<Structure>,
    availableEchoes: Double,
    totalDps: Double,
    buyMultiplier: BuyMultiplier,
    scientific: Boolean,
    onBuyStructure: (String) -> Unit,
    onSetBuyMultiplier: (BuyMultiplier) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "СООРУЖЕНИЯ ДОБЫЧИ",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = StarlightSilver
                    )
                    Text(
                        text = "Автоматическая генерация энергии Бездны",
                        fontSize = 11.sp,
                        color = GhostGray
                    )
                }

                BuyMultiplierToggle(
                    currentMultiplier = buyMultiplier,
                    onSelect = onSetBuyMultiplier
                )
            }
        }

        items(structures, key = { it.id }) { structure ->
            StructureCard(
                structure = structure,
                availableEchoes = availableEchoes,
                buyMultiplier = buyMultiplier,
                scientific = scientific,
                onBuy = { onBuyStructure(structure.id) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun StructureCard(
    structure: Structure,
    availableEchoes: Double,
    buyMultiplier: BuyMultiplier,
    scientific: Boolean,
    onBuy: () -> Unit
) {
    val countToBuy = if (buyMultiplier == BuyMultiplier.MAX) {
        BigNum.calculateMaxAffordable(structure.baseCost, structure.costMultiplier, structure.count, availableEchoes)
    } else {
        buyMultiplier.amount
    }

    val displayCount = if (countToBuy <= 0) 1 else countToBuy
    val totalCost = BigNum.calculateTotalCost(structure.baseCost, structure.costMultiplier, structure.count, displayCount)
    val canAfford = availableEchoes >= totalCost && displayCount > 0

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = structure.name,
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = StarlightSilver
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "×${structure.count}",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AstralCyanLight
                )
            }

            Text(
                text = structure.description,
                fontSize = 11.sp,
                color = GhostGray,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Row(
                modifier = Modifier.padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "+${BigNum.format(structure.baseDps * structure.count, scientific)} /сек",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = VoidPurpleLight
                )
                if (structure.essencePerSec > 0.0) {
                    Text(
                        text = "+${BigNum.format(structure.essencePerSec * structure.count, scientific)} эссенции/сек",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = AstralCyanLight
                    )
                }
            }
        }

        Button(
            onClick = onBuy,
            enabled = canAfford,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AstralCyanDark,
                disabledContainerColor = ObsidianSurfaceVariant
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("buy_structure_${structure.id}")
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
                    color = if (canAfford) AmberRelicLight else DimAsh
                )
            }
        }
    }
}
