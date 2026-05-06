package com.praneet.clarity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameLightColors = lightColorScheme(
    primary = Color(0xFF6C63FF),
    onPrimary = Color.White,
    secondary = Color(0xFFFF6584),
    onSecondary = Color.White,
    tertiary = Color(0xFFFFD369),
    background = Color(0xFFF4F4FF),
    surface = Color.White,
    onBackground = Color(0xFF1A1A2E),
    onSurface = Color(0xFF1A1A2E)
)

private val GameDarkColors = darkColorScheme(
    primary = Color(0xFF8B80FF),
    onPrimary = Color.Black,
    secondary = Color(0xFFFF7A95),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFE082),
    background = Color(0xFF0F0F1B),
    surface = Color(0xFF1C1C2B),
    onBackground = Color(0xFFEAEAFF),
    onSurface = Color(0xFFEAEAFF)
)

@Composable
fun GameTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) GameDarkColors else GameLightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}