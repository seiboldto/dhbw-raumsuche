package com.example.dhbw_raumsuche.ui.viewmodel

import com.example.dhbw_raumsuche.data.local.dataclass.RoomWithEvents

data class RoomListState(
    val rooms: List<RoomWithEvents> = emptyList(),
    val isloading: Boolean = true,
    val sortType: RoomSortType = RoomSortType.ROOM_ID
)