package com.example.dhbw_raumsuche.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dhbw_raumsuche.ui.viewmodel.RoomViewModel
import com.example.dhbw_raumsuche.ui.viewmodel.SettingsViewModel


@Composable
fun LoadingScreen(
    roomViewModel: RoomViewModel,
    settingsModel: SettingsViewModel,
) {
    val isLoading by settingsModel.isLoading.collectAsState()
    val error by settingsModel.error.collectAsState()

    if (error != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Warning, contentDescription = null)
            Text("Ein unerwarteter Fehler ist aufgetreten. Bitte überprüfe die Internetverbindung oder starte die App neu.")
        }
    } else if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        MainScreen(roomViewModel)
    }
}