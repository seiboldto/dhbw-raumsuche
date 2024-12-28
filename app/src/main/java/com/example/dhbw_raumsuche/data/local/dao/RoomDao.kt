package com.example.dhbw_raumsuche.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.dhbw_raumsuche.data.local.entity.RoomEntity
import java.sql.Date

@Dao
interface RoomDao {
    @Upsert
    suspend fun insertRoom(room: RoomEntity)

    @Upsert
    suspend fun insertRooms(rooms: List<RoomEntity>)

    @Delete
    suspend fun deleteRoom(roomEntity: RoomEntity)

    @Query("SELECT * FROM rooms")
    fun getRooms(): List<RoomEntity>

    @Query("SELECT * FROM rooms WHERE building = :building")
    fun getRoomsByBuilding(building: String): List<RoomEntity>

    @Query("SELECT * FROM rooms WHERE floor = :floor")
    fun getRoomsByFloor(floor: String): List<RoomEntity>

    @Query("SELECT * FROM rooms WHERE building= :building AND floor = :floor")
    fun getRoomsByBuildingAndFloor(building: String, floor: String): List<RoomEntity>

    @Query(
        """
        SELECT * FROM rooms 
        WHERE roomId NOT IN (
            SELECT DISTINCT roomId FROM events 
            WHERE NOT 'end' <= :start OR 'start' >= :end
        )
        """
    )
    suspend fun getAvailableRooms(start: Date, end: Date): List<RoomEntity>

}