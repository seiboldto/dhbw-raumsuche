package com.example.dhbw_raumsuche.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dhbw_raumsuche.data.local.dao.EventDao
import com.example.dhbw_raumsuche.data.local.dao.RoomDao
import com.example.dhbw_raumsuche.data.local.entity.EventEntity
import com.example.dhbw_raumsuche.data.local.entity.RoomEntity

@Database(
    entities = [RoomEntity::class, EventEntity::class],
    version = 1
)
@TypeConverters(DateConverter::class)
abstract class RoomsDatabase: RoomDatabase() {
    abstract fun roomDao(): RoomDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: RoomsDatabase? = null

        // Singleton Pattern to only ever have one Instance of the Database
        fun getInstance(context: Context): RoomsDatabase {
            return INSTANCE ?: synchronized(this) {
                createDatabase(context).also { INSTANCE = it }
            }
        }

        private fun createDatabase(context: Context): RoomsDatabase {
            return Room.databaseBuilder(
                context,
                RoomsDatabase::class.java,
                "rooms.db"
            ).build()
        }
    }
}