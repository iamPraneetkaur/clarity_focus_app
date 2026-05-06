package com.praneet.clarity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MinimalLightColors = lightColorScheme(
    primary = Color(0xFF222222),
    onPrimary = Color.White,
    secondary = Color(0xFF888888),
    onSecondary = Color.White,
    background = Color(0xFFF7F7F7),
    surface = Color.White,
    onBackground = Color(0xFF111111),
    onSurface = Color(0xFF111111)
)

private val MinimalDarkColors = darkColorScheme(
    primary = Color(0xFFE0E0E0),
    onPrimary = Color.Black,
    secondary = Color(0xFFAAAAAA),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFEAEAEA),
    onSurface = Color(0xFFEAEAEA)
)

@Composable
fun MinimalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) MinimalDarkColors else MinimalLightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}