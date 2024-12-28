package com.example.dhbw_raumsuche.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey(autoGenerate = false)
    val roomId: String,
    val fullName: String,
    val building: String,
    val floor: String,
    val number: String
)
