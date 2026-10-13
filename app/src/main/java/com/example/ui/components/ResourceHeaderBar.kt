package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BigNum
import com.example.model.Currencies
import com.example.model.CurrencyType
import com.example.ui.theme.*

@Composable
fun ResourceHeaderBar(
    currencies: Currencies,
    dps: Double,
    scientific: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        AbyssBlack.copy(alpha = 0.95f),
                        ObsidianSurface.copy(alpha = 0.85f),
                        Color.Transparent
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Main currency: Echoes
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "✦",
                        fontSize = 24.sp,
                        color = VoidPurpleLight,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = BigNum.format(currencies.echoes, scientific),
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = StarlightSilver
                    )
                }
                Text(
                    text = "ЭХО БЕЗДНЫ",
                    fontFamily = CinzelFontFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp,
                    color = GhostGray
                )
            }

            // DPS indicator
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianSurfaceVariant)
                    .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "+${BigNum.format(dps, scientific)} /сек",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = AstralCyanLight
                    )
                    Text(
                        text = "ГЕНЕРАЦИЯ",
                        fontFamily = CinzelFontFamily,
                        fontSize = 9.sp,
                        color = GhostGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Secondary resources bar: Shards, Essence, Time Ashes, Void Cores, Ancient Runes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(ObsidianSurface.copy(alpha = 0.6f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResourceBadge(
                symbol = CurrencyType.SHARDS.symbol,
                amount = BigNum.format(currencies.shards, scientific),
                color = AstralCyanLight,
                label = "Осколки"
            )
            ResourceBadge(
                symbol = CurrencyType.ESSENCE.symbol,
                amount = BigNum.format(currencies.essence, scientific),
                color = VoidPurpleLight,
                label = "Эссенция"
            )
            ResourceBadge(
                symbol = CurrencyType.TIME_ASHES.symbol,
                amount = BigNum.format(currencies.timeAshes, scientific),
                color = AmberRelicLight,
                label = "Пепел"
            )
            ResourceBadge(
                symbol = CurrencyType.VOID_CORES.symbol,
                amount = BigNum.format(currencies.voidCores, scientific),
                color = BloodCrimsonLight,
                label = "Ядра"
            )
            ResourceBadge(
                symbol = CurrencyType.ANCIENT_RUNES.symbol,
                amount = BigNum.format(currencies.ancientRunes, scientific),
                color = VoidEmerald,
                label = "Руны"
            )
        }
    }
}

@Composable
private fun ResourceBadge(
    symbol: String,
    amount: String,
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            text = symbol,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 3.dp)
        )
        Text(
            text = amount,
            fontFamily = JetBrainsMonoFontFamily,
            fontSize = 11.sp,
            color = StarlightSilver
        )
    }
}
