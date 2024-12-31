package com.example.dhbw_raumsuche.data.local

import android.content.Context
import com.example.dhbw_raumsuche.data.local.dao.EventDao
import com.example.dhbw_raumsuche.data.local.entity.EventEntity
import com.example.dhbw_raumsuche.data.local.entity.RoomEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Location
import java.sql.Date

class RoomPersistenceService(private val context: Context) {

    private val db: RoomsDatabase by lazy { RoomsDatabase.getInstance(context) }
    private val eventDao: EventDao by lazy { db.eventDao() }

    suspend fun updateDbFromICal(calendars: List<Calendar>) = withContext(
        Dispatchers.IO
    ) {
        val roomEntities = async {
            calendars
                .flatMap { it.getComponents<VEvent>(Component.VEVENT) }
                .mapNotNull { it.location }
                .flatMap { iCalLocationToRooms(it) }
                .toSet()
        }

        val eventEntities = async {
            calendars
                .flatMap { it.getComponents<VEvent>(Component.VEVENT) }
                .mapNotNull { iCalEventToEventEntity(it) }
                .flatten()
                .toSet()
        }

        val rooms = roomEntities.await()
        val events = eventEntities.await()

        addRoomsToDatabase(rooms.toList())
        eventDao.insertEvents(events.toList())
    }

    private fun iCalEventToEventEntity(event: VEvent): List<EventEntity>? {
        val id = event.uid ?: return null
        val start = event.startDate ?: return null
        val end = event.endDate ?: return null
        val title = event.summary ?: return null
        val location = event.location ?: return null
        val roomIds = getRoomIdsToLocation(location)

        return roomIds.map {
            EventEntity(
                eventId = id.toString(),
                start = Date(start.date.time),
                end = Date(end.date.time),
                title = title.value,
                roomId = it.key
            )
        }.toList()
    }

    private fun getRoomIdsToLocation(location: Location): Map<String, String> {
        val roomIds = mutableMapOf<String, String>()

        val parts = location.value.split(",")

        val numberBuildingRegex = Regex("^(\\d+|\\d+\\.\\d+) ?([A-D]) .*")
        val buildingNumberRegex = Regex("^([A-D]) ?(\\d+|\\d+\\.\\d+) .*")

        parts.forEach {
            val trimmedPart = it.trim()
            val numberBuilding = numberBuildingRegex.find(trimmedPart)
            val buildingNumber = buildingNumberRegex.find(trimmedPart)

            if (numberBuilding != null) {
                val (number, building) = numberBuilding.destructured
                roomIds[number + building] = trimmedPart
            } else if (buildingNumber != null) {
                val (building, number) = buildingNumber.destructured
                roomIds[number + building] = trimmedPart
            }
        }

        return roomIds
    }

    private fun iCalLocationToRooms(location: Location): List<RoomEntity> {
        val idMap = getRoomIdsToLocation(location)

        return idMap.map { (id, location) ->
            val building = id[id.length - 1].toString()
            val number = id.substring(0, id.length - 1)
            val floor = number[0].toString()

            RoomEntity(
                roomId = id,
                fullName = location,
                building = building,
                floor = floor,
                number = number
            )
        }
    }

    private suspend fun addRoomsToDatabase(roomEntities: List<RoomEntity>) {
        db.roomDao().insertRooms(roomEntities)
    }
}