package com.example.dhbw_raumsuche.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.dhbw_raumsuche.data.local.dao.RoomDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class RoomViewModel(
    private val roomDao: RoomDao
) : ViewModel() {

    // State flows for rooms and filters
    private val _state = MutableStateFlow(RoomListState())
    private val _sortType = MutableStateFlow(RoomSortType.ROOM_ID)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _rooms = _sortType.flatMapLatest { sortType ->
        when (sortType) {
            RoomSortType.ROOM_ID -> roomDao.getRooms()
            RoomSortType.FLOOR -> roomDao.getRoomsSortByFloor()
            RoomSortType.BUILDING -> roomDao.getRoomsSortByBuilding()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val state = combine(_state, _sortType, _rooms) { state, sortType, rooms ->
            state.copy(
                rooms = rooms,
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


}
