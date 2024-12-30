package com.example.dhbw_raumsuche.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class Theme {
    Dark,
    Light,
    System
}

class SettingsViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> = _error

    private val _theme = MutableStateFlow(Theme.System)
    val theme: StateFlow<Theme> get() = _theme

    init {
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                preferences[SettingsKeys.THEME]?.let { Theme.valueOf(it) } ?: Theme.System
            }.collect { _theme.value = it }
        }
    }

    fun onThemeChange(theme: Theme) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[SettingsKeys.THEME] = theme.name
            }
        }
        _theme.value = theme
    }

    fun setIsLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setError(error: Throwable) {
        _error.value = error
    }
}

val LocalSettingsModel = compositionLocalOf<SettingsViewModel> {
    error("No SettingsModel provided")
}

val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys  {
    val THEME = stringPreferencesKey("theme")
}