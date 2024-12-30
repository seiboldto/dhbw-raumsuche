package com.example.dhbw_raumsuche.ui.viewmodel

import com.example.dhbw_raumsuche.location.Building

data class RoomFilterSettings(val selectedBuildings: Set<Building> = setOf()) {

    fun updateBuildings(building: Building): Set<Building> {
        val updatedSet = selectedBuildings.toMutableSet()
        if (updatedSet.contains(building)) {
            updatedSet.remove(building)
        } else {
            updatedSet.add(building)
        }
        return updatedSet
    }

}