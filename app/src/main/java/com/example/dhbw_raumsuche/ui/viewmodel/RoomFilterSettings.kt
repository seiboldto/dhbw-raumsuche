package com.example.dhbw_raumsuche.ui.viewmodel

import com.example.dhbw_raumsuche.location.Building
import com.example.dhbw_raumsuche.location.Floor

data class RoomFilterSettings(val selectedBuildings: Set<Building> = setOf(), val locationBuilding: Building? = null, val locationFloor: Floor? = null, val favorites: Boolean = false, val free: Boolean = false) {
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