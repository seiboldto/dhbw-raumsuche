package com.example.dhbw_raumsuche.location

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dhbw_raumsuche.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

class LocationViewModel(public val requestLocation: () -> Unit) : ViewModel() {
    var building by mutableStateOf<Building?>(null)
    var floor by mutableStateOf<Floor?>(null)

    fun updateLocation(
        locationService: LocationService,
        context: Context
    ) {
        viewModelScope.launch {
            locationService.getLocation(context)?.let {
                building = it.building
                floor = it.floor
            }
        }
    }
}

val LocalLocationModel = compositionLocalOf<LocationViewModel> {
    error("No LocationModel provided")
}
