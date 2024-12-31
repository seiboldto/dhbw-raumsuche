package com.example.dhbw_raumsuche.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.dhbw_raumsuche.data.local.dao.RoomDao
import com.example.dhbw_raumsuche.data.local.dataclass.RoomWithEvents
import com.example.dhbw_raumsuche.location.Building
import com.example.dhbw_raumsuche.location.Floor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoomViewModel(
    private val roomDao: RoomDao, private val getRoomData: () -> Unit
) : ViewModel() {
    // Directly collect the flow of rooms with events from the DAO
    private val _rooms = MutableStateFlow<List<RoomWithEvents>>(emptyList())

    // State flows for sort types and filters
    private val _filterSettings = MutableStateFlow(RoomFilterSettings())
    val filterSettings: StateFlow<RoomFilterSettings> = _filterSettings

    private val _sortType = MutableStateFlow(RoomSortType.ROOM_ID)
    val sortType: StateFlow<RoomSortType> = _sortType

    val roomList: StateFlow<List<RoomWithEvents>> =
        combine(_rooms, _filterSettings, _sortType) { rooms, filterSettings, sortType ->
            val filteredRooms = filterRooms(rooms, filterSettings)
            when (sortType) {
                RoomSortType.ROOM_ID -> filteredRooms.sortedBy { it.room.roomId }
                RoomSortType.BUILDING -> filteredRooms.sortedBy { it.room.building }
                RoomSortType.FLOOR -> filteredRooms.sortedBy { it.room.floor }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun filterRooms(
        rooms: List<RoomWithEvents>,
        filterSettings: RoomFilterSettings
    ): List<RoomWithEvents> {
        return rooms.filter {
            // Location filter overrides the selected buildings
            if (filterSettings.locationBuilding != null && filterSettings.locationFloor != null) {
                it.building == filterSettings.locationBuilding.toString() && it.room.floor == when (filterSettings.locationFloor) {
                    Floor.FirstFloor -> "1"
                    Floor.SecondFloor -> "2"
                    Floor.ThirdFloor -> "3"
                    Floor.FourthFloor -> "4"
                }
            } else (filterSettings.selectedBuildings.isEmpty() || it.building.isNotEmpty() && filterSettings.selectedBuildings.contains(
                Building.valueOf(it.building)
            ))
        }
    }

    init {
        loadRooms()
    }

    private fun loadRooms() {
        viewModelScope.launch {
            getRoomData()
            CoroutineScope(Dispatchers.IO).launch {
                _rooms.emit(roomDao.getRoomsWithEvents())

            }
        }
    }

    fun setSortType(sortType: RoomSortType) {
        viewModelScope.launch { _sortType.emit(sortType) }
    }

    fun setBuildingFilter(building: Building) {
        viewModelScope.launch {
            _filterSettings.value = _filterSettings.value.copy(
                selectedBuildings = _filterSettings.value.updateBuildings(building)
            )
        }
    }

    fun setLocationFilter(building: Building?, floor: Floor?) {
        viewModelScope.launch {
            _filterSettings.value =
                _filterSettings.value.copy(locationBuilding = building, locationFloor = floor)
        }
    }
}
