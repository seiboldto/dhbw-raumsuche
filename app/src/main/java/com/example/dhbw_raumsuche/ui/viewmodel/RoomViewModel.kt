package com.example.dhbw_raumsuche.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.dhbw_raumsuche.data.local.dao.RoomDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RoomViewModel(
    private val roomDao: RoomDao
) : ViewModel() {

    // State flows for rooms and filters
    private val _state = MutableStateFlow(RoomListState())
    private val _sortType = MutableStateFlow(RoomSortType.ROOM_ID)

    // Directly collect the flow of rooms with events from the DAO
    private val _rooms = roomDao.getRoomsWithEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val state = combine(_state, _sortType, _rooms) { state, sortType, rooms ->
            val sortedRooms = when (sortType) {
                RoomSortType.ROOM_ID -> rooms.sortedBy { it.room.roomId }
                RoomSortType.BUILDING -> rooms.sortedBy { it.room.building }
                RoomSortType.FLOOR -> rooms.sortedBy { it.room.floor }}
            state.copy(
                rooms = sortedRooms,
                sortType = sortType
            )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RoomListState())

    fun OnEvent(event: RoomListEvent) {
        when (event) {
            is RoomListEvent.SortRooms -> {
                _sortType.value = event.sortType
            }
        }
    }

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
