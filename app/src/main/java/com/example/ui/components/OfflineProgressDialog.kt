package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.BigNum
import com.example.ui.theme.*
import com.example.viewmodel.OfflineRewardInfo

@Composable
fun OfflineProgressDialog(
    rewardInfo: OfflineRewardInfo,
    scientific: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(ObsidianSurfaceVariant, ObsidianSurface, AbyssBlack)
                    )
                )
                .border(1.5.dp, VoidPurpleDark, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "ПРОБУЖДЕНИЕ ОТО СНА",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AmberRelicLight,
                    textAlign = TextAlign.Center
                )

                val hours = rewardInfo.secondsAway / 3600
                val minutes = (rewardInfo.secondsAway % 3600) / 60
                val seconds = rewardInfo.secondsAway % 60
                val timeStr = when {
                    hours > 0 -> "$hours ч $minutes мин"
                    minutes > 0 -> "$minutes мин $seconds сек"
                    else -> "$seconds сек"
                }

                Text(
                    text = "Пока вы отсутствовали в материальном мире ($timeStr), древние машины и сущности продолжали вытягивать эманации Бездны:",
                    fontSize = 13.sp,
                    color = StarlightSilver,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AbyssBlack.copy(alpha = 0.6f))
                        .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (rewardInfo.echoesEarned > 0.0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("✦ Эхо Бездны:", color = GhostGray, fontSize = 13.sp)
                            Text(
                                "+${BigNum.format(rewardInfo.echoesEarned, scientific)}",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = VoidPurpleLight,
                                fontSize = 13.sp
                            )
                        }
                    }
                    if (rewardInfo.essenceEarned > 0.0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("◈ Эссенция Хаоса:", color = GhostGray, fontSize = 13.sp)
                            Text(
                                "+${BigNum.format(rewardInfo.essenceEarned, scientific)}",
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = AstralCyanLight,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("offline_collect_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VoidPurpleDark,
                        contentColor = StarlightSilver
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "ПОГЛОТИТЬ НАГРАДУ",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
