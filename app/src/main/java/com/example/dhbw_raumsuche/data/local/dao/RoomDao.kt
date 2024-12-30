package com.example.dhbw_raumsuche.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.dhbw_raumsuche.data.local.dataclass.RoomWithEvents
import com.example.dhbw_raumsuche.data.local.entity.RoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Upsert
    suspend fun insertRoom(room: RoomEntity)

    @Upsert
    suspend fun insertRooms(rooms: List<RoomEntity>)

    @Delete
    suspend fun deleteRoom(roomEntity: RoomEntity)

    @Query("SELECT * FROM rooms ORDER BY roomId ASC")
    fun getRooms(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms ORDER BY floor ASC")
    fun getRoomsSortByFloor(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms ORDER BY building ASC")
    fun getRoomsSortByBuilding(): Flow<List<RoomEntity>>

    @Query("SELECT * FROM rooms")
    @Transaction // Ensures that all operations are done in a single transaction
    suspend fun getRoomsWithEvents(): List<RoomWithEvents>

}