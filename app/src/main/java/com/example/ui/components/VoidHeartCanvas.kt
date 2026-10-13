package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.FloatingText
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ClickBurstParticle(
    val id: Long,
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    var alpha: Float,
    val radius: Float
)

@Composable
fun VoidHeartCanvas(
    modifier: Modifier = Modifier,
    floatingTexts: List<FloatingText>,
    isVoidSurge: Boolean,
    isBloodEclipse: Boolean,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val glowPulse = remember { Animatable(1f) }

    // Ambient heart pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "heart_pulse")
    val pulseRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_rotate"
    )

    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    val burstParticles = remember { mutableStateListOf<ClickBurstParticle>() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .testTag("void_heart_click_target")
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        coroutineScope.launch {
                            scale.snapTo(0.91f)
                            scale.animateTo(
                                targetValue = 1.0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }

                        // Spawn click particles
                        val colors = if (isBloodEclipse) {
                            listOf(BloodCrimson, BloodCrimsonLight, AmberRelic)
                        } else if (isVoidSurge) {
                            listOf(AstralCyan, AstralCyanLight, VoidPurpleLight)
                        } else {
                            listOf(VoidPurpleLight, AstralCyan, Color.White, AmberRelicLight)
                        }

                        val count = if (isBloodEclipse || isVoidSurge) 16 else 10
                        for (i in 0 until count) {
                            val angle = Random.nextFloat() * 2f * PI.toFloat()
                            val speed = Random.nextFloat() * 12f + 4f
                            burstParticles.add(
                                ClickBurstParticle(
                                    id = System.nanoTime() + i,
                                    x = offset.x,
                                    y = offset.y,
                                    vx = cos(angle) * speed,
                                    vy = sin(angle) * speed,
                                    color = colors[Random.nextInt(colors.size)],
                                    alpha = 1f,
                                    radius = Random.nextFloat() * 3.5f + 1.5f
                                )
                            )
                        }

                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val currentScale = scale.value * breathingScale
            val heartRadius = 68.dp.toPx() * currentScale

            // Draw expanding/dying burst particles
            val iterator = burstParticles.iterator()
            while (iterator.hasNext()) {
                val p = iterator.next()
                p.x += p.vx
                p.y += p.vy
                p.alpha -= 0.05f
                if (p.alpha <= 0f) {
                    iterator.remove()
                } else {
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha),
                        radius = p.radius,
                        center = Offset(p.x, p.y)
                    )
                }
            }

            // Radial halo aura glow
            val glowColor = when {
                isBloodEclipse -> BloodCrimson
                isVoidSurge -> AstralCyan
                else -> VoidPurple
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.45f),
                        glowColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = heartRadius * 2.2f
                ),
                radius = heartRadius * 2.2f,
                center = center
            )

            // Outer Orbit Runic Rings
            rotate(pulseRotation, center) {
                drawCircle(
                    color = SlateBorder.copy(alpha = 0.6f),
                    radius = heartRadius * 1.55f,
                    center = center,
                    style = Stroke(width = 1.5f)
                )

                // Orbit runes
                val runeCount = 6
                for (i in 0 until runeCount) {
                    val angle = (i * 2 * PI / runeCount).toFloat()
                    val rx = center.x + cos(angle) * heartRadius * 1.55f
                    val ry = center.y + sin(angle) * heartRadius * 1.55f
                    drawCircle(
                        color = AstralCyan.copy(alpha = 0.8f),
                        radius = 3.5f,
                        center = Offset(rx, ry)
                    )
                }
            }

            rotate(-pulseRotation * 0.6f, center) {
                drawCircle(
                    color = VoidPurpleDark.copy(alpha = 0.5f),
                    radius = heartRadius * 1.3f,
                    center = center,
                    style = Stroke(width = 2.0f)
                )
            }

            // The Void Heart: Faceted Obsidian & Crystal Geometry
            // Diamond / Octahedral Crystal Path
            val top = Offset(center.x, center.y - heartRadius * 1.15f)
            val bottom = Offset(center.x, center.y + heartRadius * 1.25f)
            val left = Offset(center.x - heartRadius, center.y)
            val right = Offset(center.x + heartRadius, center.y)

            // Base Crystal gradient fill
            val crystalPath = Path().apply {
                moveTo(top.x, top.y)
                lineTo(right.x, right.y)
                lineTo(bottom.x, bottom.y)
                lineTo(left.x, left.y)
                close()
            }

            val crystalBrush = Brush.linearGradient(
                colors = listOf(
                    if (isBloodEclipse) Color(0xFF881337) else Color(0xFF581C87),
                    if (isBloodEclipse) Color(0xFF4C0519) else Color(0xFF2E1065),
                    Color(0xFF090314)
                ),
                start = top,
                end = bottom
            )
            drawPath(crystalPath, crystalBrush)

            // Top-Left facet
            val topLeftFacet = Path().apply {
                moveTo(top.x, top.y)
                lineTo(left.x, left.y)
                lineTo(center.x, center.y)
                close()
            }
            drawPath(topLeftFacet, Brush.linearGradient(listOf(Color(0xFF7E22CE), Color(0xFF3B0764))))

            // Top-Right facet (highlighted)
            val topRightFacet = Path().apply {
                moveTo(top.x, top.y)
                lineTo(right.x, right.y)
                lineTo(center.x, center.y)
                close()
            }
            drawPath(topRightFacet, Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFF581C87))))

            // Bottom-Left facet
            val botLeftFacet = Path().apply {
                moveTo(left.x, left.y)
                lineTo(bottom.x, bottom.y)
                lineTo(center.x, center.y)
                close()
            }
            drawPath(botLeftFacet, Brush.linearGradient(listOf(Color(0xFF3B0764), Color(0xFF1E1B4B))))

            // Bottom-Right facet
            val botRightFacet = Path().apply {
                moveTo(right.x, right.y)
                lineTo(bottom.x, bottom.y)
                lineTo(center.x, center.y)
                close()
            }
            drawPath(botRightFacet, Brush.linearGradient(listOf(Color(0xFF4C1D95), Color(0xFF0F0728))))

            // Fissures / Veins of Reality
            val veinColor = if (isBloodEclipse) BloodCrimsonLight else AstralCyanLight
            drawPath(
                Path().apply {
                    moveTo(center.x, center.y - heartRadius * 0.5f)
                    lineTo(center.x - 12f, center.y - 10f)
                    lineTo(center.x + 8f, center.y + 15f)
                    lineTo(center.x - 4f, center.y + heartRadius * 0.7f)
                },
                color = veinColor,
                style = Stroke(width = 2.5f)
            )

            // Central Glowing Eye / Void Core
            val corePulse = (breathingScale - 0.96f) * 12f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        if (isBloodEclipse) BloodCrimson else AstralCyan,
                        Color.Transparent
                    ),
                    center = center,
                    radius = (22f + corePulse) * currentScale
                ),
                radius = (22f + corePulse) * currentScale,
                center = center
            )
        }

        // Floating Damage / Crit texts
        for (item in floatingTexts) {
            Box(
                modifier = Modifier
                    .offset(x = item.xOffsetRatio.dp, y = (item.yOffsetRatio - 40).dp)
            ) {
                Text(
                    text = item.text,
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (item.isCrit) 20.sp else 16.sp,
                    color = if (item.isCrit) BloodCrimsonLight else AstralCyanLight
                )
            }
        }
    }
}
