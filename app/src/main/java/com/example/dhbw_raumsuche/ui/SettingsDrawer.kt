package com.example.dhbw_raumsuche.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.dhbw_raumsuche.ui.viewmodel.LocalSettingsModel
import com.example.dhbw_raumsuche.ui.viewmodel.Theme

@Composable
fun SettingsDrawer() {
    val settings = LocalSettingsModel.current
    val selectedTheme = settings.theme.collectAsState()

    Text("DHBW Raumsuche", modifier = Modifier.padding(16.dp))
    HorizontalDivider()
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Farbschema")
        Theme.entries.forEach { t ->
            Row(
                modifier = Modifier.fillMaxWidth().selectable(
                    selected = t == selectedTheme.value,
                    onClick = {
                        settings.onThemeChange(t)
                    },
                    role = Role.RadioButton
                )) {
                RadioButton(
                    selected = t == selectedTheme.value,
                    onClick = null
                )
                Text(text = t.name, modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}