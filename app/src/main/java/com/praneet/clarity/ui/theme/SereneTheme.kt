package com.praneet.clarity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 🎨 Serene Palette from Image
val SerenePrimary = Color(0xFF5E7153)
val SereneSecondary = Color(0xFFA3B18A)
val SereneTertiary = Color(0xFFF5F5F0)
val SereneNeutral = Color(0xFFFAF9F6)
val SereneInverted = Color(0xFF1C1C1C)

private val SereneLightColorScheme = lightColorScheme(
    primary = SerenePrimary,
    onPrimary = Color.White,
    secondary = SereneSecondary,
    onSecondary = Color.White,
    tertiary = SereneTertiary,
    onTertiary = SerenePrimary,
    background = SereneNeutral,
    surface = SereneNeutral,
    onBackground = SereneInverted,
    onSurface = SereneInverted,
    surfaceVariant = SereneTertiary,
    onSurfaceVariant = SerenePrimary
)

private val SereneDarkColorScheme = darkColorScheme(
    primary = SereneSecondary,
    onPrimary = SereneInverted,
    secondary = SerenePrimary,
    onSecondary = Color.White,
    tertiary = SerenePrimary,
    onTertiary = SereneTertiary,
    background = SereneInverted,
    surface = SereneInverted,
    onBackground = SereneNeutral,
    onSurface = SereneNeutral,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = SereneSecondary
)

@Composable
fun SereneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SereneDarkColorScheme else SereneLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
