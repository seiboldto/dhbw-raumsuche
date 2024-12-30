package com.example.dhbw_raumsuche.ui.viewmodel

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class Theme {
    Dark,
    Light,
    System
}

class SettingsModel : ViewModel() {
    private val _theme = MutableStateFlow(Theme.System)
    val theme: StateFlow<Theme> get() = _theme

    fun onThemeChange(theme: Theme) {
        _theme.value = theme
    }
}

val LocalSettingsModel = compositionLocalOf<SettingsModel> {
    error("No SettingsModel provided")
}