package com.example.dhbw_raumsuche.ui.viewmodel

import com.example.dhbw_raumsuche.location.Building

data class RoomFilterSettings(val selectedBuildings: MutableSet<Building> = mutableSetOf()) {

    fun updateBuildings(building: Building): RoomFilterSettings {
        if (selectedBuildings.contains(building)) {
            selectedBuildings.remove(building)
        } else {
            selectedBuildings.add(building)
        }
        return this
    }

}