package com.example.dhbw_raumsuche.location

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {

    var building by mutableStateOf("")
    var floor by mutableStateOf("")

    fun updateLocation(
        locationService: LocationService,
        context: Context
    ) {
        viewModelScope.launch {
            locationService.getLocation(context)?.let {
                building = it.building.name
                floor = it.floor.name
            }
        }
    }

}