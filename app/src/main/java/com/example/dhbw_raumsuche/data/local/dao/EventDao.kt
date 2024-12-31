package com.example.dhbw_raumsuche.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.dhbw_raumsuche.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Upsert
    suspend fun insertEvent(event: EventEntity)

    @Upsert
    suspend fun insertEvents(rooms: List<EventEntity>)

    @Delete
    suspend fun deleteEvent(roomEntity: EventEntity)

    @Query("SELECT * FROM events")
    fun getEvents(): List<EventEntity>
}