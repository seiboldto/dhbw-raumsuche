package com.example.dhbw_raumsuche.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.dhbw_raumsuche.R
import com.example.dhbw_raumsuche.ui.viewmodel.LocalSettingsModel
import com.example.dhbw_raumsuche.ui.viewmodel.Theme

@Composable
fun SettingsDrawer() {
    val settings = LocalSettingsModel.current
    val selectedTheme = settings.theme.collectAsState()

    Text(
        stringResource(R.string.app_name),
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.titleMedium
    )
    HorizontalDivider()
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = stringResource(R.string.color_scheme), style = MaterialTheme.typography.labelMedium)
        Theme.entries.forEach { t ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = t == selectedTheme.value,
                        onClick = {
                            settings.onThemeChange(t)
                        },
                        role = Role.RadioButton
                    )
            ) {
                RadioButton(
                    selected = t == selectedTheme.value,
                    onClick = null
                )
                Text(
                    text = stringResource( when (t) {
                        Theme.Light -> R.string.color_scheme_light
                        Theme.Dark -> R.string.color_scheme_dark
                        Theme.System -> R.string.color_scheme_system
                    }), modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}