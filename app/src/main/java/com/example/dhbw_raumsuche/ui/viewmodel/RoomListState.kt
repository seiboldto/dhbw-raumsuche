package com.example.dhbw_raumsuche.ui.viewmodel

import com.example.dhbw_raumsuche.data.local.entity.RoomEntity

data class RoomListState(
    val rooms: List<RoomEntity> = emptyList(),
    val isloading: Boolean = true,
    val sortType: RoomSortType = RoomSortType.ROOM_ID
)