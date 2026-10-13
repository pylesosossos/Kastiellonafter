package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.model.ParticleQuality
import com.example.ui.theme.*
import kotlin.random.Random

private data class CosmicParticle(
    var x: Float,
    var y: Float,
    val radius: Float,
    val speedY: Float,
    val speedX: Float,
    val color: Color,
    var alpha: Float
)

@Composable
fun CosmicBackgroundCanvas(
    modifier: Modifier = Modifier,
    particleQuality: ParticleQuality = ParticleQuality.HIGH
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cosmic_bg")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_step"
    )

    val particles = remember(particleQuality) {
        val count = particleQuality.maxParticles
        val list = mutableListOf<CosmicParticle>()
        val colors = listOf(VoidPurpleLight, AstralCyan, BloodCrimsonLight, AmberRelicLight)
        for (i in 0 until count) {
            list.add(
                CosmicParticle(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    radius = Random.nextFloat() * 2.5f + 1f,
                    speedY = -(Random.nextFloat() * 0.0015f + 0.0005f),
                    speedX = (Random.nextFloat() - 0.5f) * 0.0006f,
                    color = colors[Random.nextInt(colors.size)],
                    alpha = Random.nextFloat() * 0.6f + 0.2f
                )
            )
        }
        list
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        // Deep abyss radial gradient background
        val width = size.width
        val height = size.height

        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1D0E36),
                    Color(0xFF0F071E),
                    Color(0xFF06030B)
                ),
                center = Offset(width * 0.5f, height * 0.35f),
                radius = width * 1.1f
            )
        )

        // Draw ambient cosmic dust & embers
        for (p in particles) {
            p.y += p.speedY
            p.x += p.speedX
            if (p.y < 0f) {
                p.y = 1f
                p.x = Random.nextFloat()
            }
            if (p.x < 0f) p.x = 1f
            if (p.x > 1f) p.x = 0f

            drawCircle(
                color = p.color.copy(alpha = p.alpha),
                radius = p.radius,
                center = Offset(p.x * width, p.y * height)
            )
        }
    }
}
