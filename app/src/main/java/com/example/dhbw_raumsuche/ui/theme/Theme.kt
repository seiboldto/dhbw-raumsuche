package com.example.dhbw_raumsuche.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import com.example.dhbw_raumsuche.ui.viewmodel.LocalSettingsModel
import com.example.dhbw_raumsuche.ui.viewmodel.Theme

private val DarkColorScheme = darkColorScheme(
    primary = White40,
    primaryContainer = Blue40,
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue80,
    primaryContainer = Color(0xFFFFEFEF),
)

@Composable
fun CustomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val settings = LocalSettingsModel.current
    val theme = settings.theme.collectAsState()

    val colorScheme = when (theme.value) {
        Theme.System -> if (darkTheme) DarkColorScheme else LightColorScheme
        Theme.Dark -> DarkColorScheme
        Theme.Light -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}