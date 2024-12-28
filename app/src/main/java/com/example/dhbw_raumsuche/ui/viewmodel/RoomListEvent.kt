package com.example.dhbw_raumsuche.ui.viewmodel

sealed interface RoomListEvent {
    data class SortRooms(val sortType: RoomSortType) : RoomListEvent
}