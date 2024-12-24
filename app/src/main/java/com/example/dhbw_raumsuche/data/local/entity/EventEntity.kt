package com.example.dhbw_raumsuche.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "events",
        indices = [Index("roomId")],
        foreignKeys = [
            ForeignKey(
            entity = RoomEntity::class,
            parentColumns = ["roomId"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE  // Optional: Specify behavior on delete
    ),

])
data class EventEntity(
    @PrimaryKey(autoGenerate = false)
    val eventId: String,
    val start: Date,
    val end: Date,
    val title: String,
    val roomId: String
)