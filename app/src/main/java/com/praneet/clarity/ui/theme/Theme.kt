package com.praneet.clarity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

enum class ThemeStyle {
    MINIMAL, SERENE, GAME
}

@Composable
fun ClarityTheme(
    themeStyle: ThemeStyle = ThemeStyle.SERENE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    when (themeStyle) {
        ThemeStyle.MINIMAL -> MinimalTheme(darkTheme, content)
        ThemeStyle.SERENE -> SereneTheme(darkTheme, content)
        ThemeStyle.GAME -> GameTheme(darkTheme, content)
    }
}
