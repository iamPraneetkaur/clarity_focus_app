package com.praneet.clarity.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.praneet.clarity.ui.theme.ThemeStyle

class SettingsViewModel(context: Context) : ViewModel() {
    private val prefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    
    private val _themeStyle = mutableStateOf(
        ThemeStyle.valueOf(prefs.getString("theme_style", ThemeStyle.SERENE.name) ?: ThemeStyle.SERENE.name)
    )
    val themeStyle: State<ThemeStyle> = _themeStyle

    fun setThemeStyle(style: ThemeStyle) {
        _themeStyle.value = style
        prefs.edit().putString("theme_style", style.name).apply()
    }

    companion object {
        fun provideFactory(context: Context): androidx.lifecycle.ViewModelProvider.Factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(context) as T
            }
        }
    }
}
