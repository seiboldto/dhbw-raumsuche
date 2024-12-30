package com.example.dhbw_raumsuche.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.dhbw_raumsuche.data.local.dao.RoomDao
import com.example.dhbw_raumsuche.data.local.dataclass.RoomWithEvents
import com.example.dhbw_raumsuche.location.Building
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

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
            (filterSettings.selectedBuildings.isEmpty() || it.building.isNotEmpty() && filterSettings.selectedBuildings.contains(
                Building.valueOf(it.building)
            ))
        }
    }

    init {
        loadRooms()
    }

    private fun loadRooms() {
        viewModelScope.launch {
            _isLoading.emit(true)
            getRoomData()
            CoroutineScope(Dispatchers.IO).launch {
                _rooms.emit(roomDao.getRoomsWithEvents())
                _isLoading.emit(false)
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

//    fun OnEvent(event: RoomListEvent) {
//        when (event) {
//            is RoomListEvent.SortRooms -> {
//                _sortType.value = event.sortType
//            }
//        }
//    }

    /*
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _rooms = _sortType.flatMapLatest { sortType ->
        when (sortType) {
            RoomSortType.ROOM_ID -> roomDao.getRooms()
            RoomSortType.FLOOR -> roomDao.getRoomsSortByFloor()
            RoomSortType.BUILDING -> roomDao.getRoomsSortByBuilding()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    */

}
