package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VoidPurple,
    onPrimary = AbyssBlack,
    primaryContainer = VoidPurpleDark,
    onPrimaryContainer = VoidPurpleLight,
    secondary = AstralCyan,
    onSecondary = AbyssBlack,
    secondaryContainer = AstralCyanDark,
    onSecondaryContainer = AstralCyanLight,
    tertiary = BloodCrimson,
    onTertiary = AbyssBlack,
    tertiaryContainer = BloodCrimsonDark,
    onTertiaryContainer = BloodCrimsonLight,
    background = AbyssBlack,
    onBackground = StarlightSilver,
    surface = ObsidianSurface,
    onSurface = StarlightSilver,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = GhostGray,
    outline = SlateBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
