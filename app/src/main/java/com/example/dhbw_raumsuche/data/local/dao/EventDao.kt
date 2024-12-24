package com.example.dhbw_raumsuche.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.dhbw_raumsuche.data.local.entity.EventEntity
import com.example.dhbw_raumsuche.data.local.entity.RoomEntity

@Dao
interface EventDao {
    @Upsert
    suspend fun insertEvent(event: EventEntity)

    @Upsert
    suspend fun insertEvents(rooms: List<RoomEntity>)

    @Delete
    suspend fun deleteEvent(roomEntity: RoomEntity)

    @Query("SELECT * FROM events")
    fun getEvents(): List<EventEntity>
}