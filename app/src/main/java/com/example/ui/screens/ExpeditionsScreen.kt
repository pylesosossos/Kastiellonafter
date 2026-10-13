package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun ExpeditionsScreen(
    currencies: Currencies,
    biomes: List<Biome>,
    familiars: List<Familiar>,
    activeBiomeId: Int,
    activeBoss: ActiveBossFight?,
    scientific: Boolean,
    onSelectBiome: (Int) -> Unit,
    onStartBossFight: (Biome) -> Unit,
    onUpgradeFamiliar: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
    ) {
        // Active Boss Battle Banner
        if (activeBoss != null) {
            item {
                ActiveBossCard(activeBoss = activeBoss, scientific = scientific)
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Biomes Header
        item {
            Text(
                text = "ИЗМЕРЕНИЯ И БИОМЫ",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = StarlightSilver
            )
            Text(
                text = "Продвигайтесь вглубь Бездны. Побеждайте боссов для открытия новых миров.",
                fontSize = 11.sp,
                color = GhostGray,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        items(biomes, key = { it.id }) { biome ->
            val isActive = biome.id == activeBiomeId
            val canEnter = biome.isUnlocked

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isActive) ObsidianSurfaceVariant else ObsidianSurface)
                    .border(
                        1.dp,
                        if (isActive) VoidPurpleLight else if (canEnter) SlateBorder else DimAsh.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = biome.name,
                                fontFamily = CinzelFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (canEnter) StarlightSilver else DimAsh
                            )
                            if (isActive) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "[ТЕКУЩИЙ]",
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VoidPurpleLight
                                )
                            }
                        }
                        Text(
                            text = biome.subtitle,
                            fontSize = 11.sp,
                            color = GhostGray
                        )
                    }

                    if (canEnter && !isActive) {
                        Button(
                            onClick = { onSelectBiome(biome.id) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VoidPurpleDark),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("enter_biome_${biome.id}")
                        ) {
                            Text("ВОЙТИ", fontFamily = CinzelFontFamily, fontSize = 10.sp)
                        }
                    }
                }

                Text(
                    text = biome.description,
                    fontSize = 11.sp,
                    color = GhostGray,
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
                        text = "Множитель DPS: ×${biome.dpsMultiplier}",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        color = AstralCyanLight
                    )

                    if (canEnter) {
                        Button(
                            onClick = { onStartBossFight(biome) },
                            enabled = activeBoss == null,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (biome.isBossDefeated) AmberRelicDark else BloodCrimsonDark
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                            modifier = Modifier.testTag("challenge_boss_${biome.id}")
                        ) {
                            Text(
                                text = if (biome.isBossDefeated) "ПОВТОРИТЬ БОССА" else "ВЫЗВАТЬ БОССА",
                                fontFamily = CinzelFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = "Требуется победа над прошлым боссом",
                            fontSize = 10.sp,
                            color = DimAsh
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Familiars Section
        item {
            Text(
                text = "СУЩНОСТИ И ФАМИЛЬЯРЫ",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = StarlightSilver,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            Text(
                text = "Призывайте и развивайте обитателей бездны за Эссенцию Хаоса (◈).",
                fontSize = 11.sp,
                color = GhostGray,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        items(familiars, key = { it.id }) { fam ->
            val cost = fam.baseCostEssence * Math.pow(fam.costMultiplier, fam.level.toDouble())
            val canUpgrade = currencies.essence >= cost

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, if (fam.isUnlocked) AstralCyanDark.copy(alpha = 0.6f) else SlateBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = fam.name,
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = StarlightSilver
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ур. ${fam.level}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 11.sp,
                            color = VoidPurpleLight
                        )
                    }
                    Text(
                        text = fam.lore,
                        fontSize = 11.sp,
                        color = GhostGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (fam.autoClicksPerSec > 0.0) {
                            Text(
                                text = "Автоклик: ${(fam.autoClicksPerSec * fam.level).toInt()}/сек",
                                fontSize = 10.sp,
                                color = AstralCyanLight
                            )
                        }
                        if (fam.dpsBonusPercent > 0.0) {
                            Text(
                                text = "Бонус DPS: +${(fam.dpsBonusPercent * fam.level).toInt()}%",
                                fontSize = 10.sp,
                                color = AmberRelicLight
                            )
                        }
                    }
                }

                Button(
                    onClick = { onUpgradeFamiliar(fam.id) },
                    enabled = canUpgrade,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VoidPurpleDark,
                        disabledContainerColor = ObsidianSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("upgrade_familiar_${fam.id}")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (fam.level == 0) "ПРИЗВАТЬ" else "+1 УР",
                            fontFamily = CinzelFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canUpgrade) StarlightSilver else DimAsh
                        )
                        Text(
                            text = "◈ ${BigNum.format(cost, scientific)}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 10.sp,
                            color = if (canUpgrade) AstralCyanLight else DimAsh
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActiveBossCard(
    activeBoss: ActiveBossFight,
    scientific: Boolean
) {
    val hpFraction = (activeBoss.currentHp / activeBoss.maxHp).toFloat().coerceIn(0f, 1f)
    val timeFraction = (activeBoss.remainingSeconds / activeBoss.durationSeconds).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF3B0718), ObsidianSurface, AbyssBlack)
                )
            )
            .border(1.5.dp, BloodCrimson, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚔ ИСПЫТАНИЕ БОССА",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = BloodCrimsonLight
            )
            Text(
                text = "${activeBoss.remainingSeconds.toInt()} сек",
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (activeBoss.remainingSeconds < 10f) BloodCrimsonLight else AmberRelicLight
            )
        }

        Text(
            text = activeBoss.bossName,
            fontFamily = CinzelFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = StarlightSilver,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        // Boss HP Bar
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Здоровье босса:",
                    fontSize = 11.sp,
                    color = GhostGray
                )
                Text(
                    text = "${BigNum.format(activeBoss.currentHp, scientific)} / ${BigNum.format(activeBoss.maxHp, scientific)}",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = BloodCrimsonLight
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { hpFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = BloodCrimson,
                trackColor = ObsidianSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Time bar
        LinearProgressIndicator(
            progress = { timeFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = AmberRelic,
            trackColor = ObsidianSurfaceVariant
        )
    }
}
