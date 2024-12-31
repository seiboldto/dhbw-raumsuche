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
        Dispatchers.IO) {
        val roomEntities = async {
            calendars
                .flatMap { it.getComponents<VEvent>(Component.VEVENT) }
                .mapNotNull { it.location }
                .mapNotNull { iCalLocationToRoom(it) }
                .toSet()
        }

        val eventEntities = async {
            calendars
                .flatMap { it.getComponents<VEvent>(Component.VEVENT) }
                .mapNotNull { iCalEventToEventEntity(it) }
                .toSet()
        }

        val rooms = roomEntities.await()
        val events = eventEntities.await()

        addRoomsToDatabase(rooms.toList())
        eventDao.insertEvents(events.toList())
    }

    private fun iCalEventToEventEntity(event: VEvent): EventEntity? {
        val id = event.uid ?: return null
        val start = event.startDate ?: return null
        val end = event.endDate ?: return null
        val title = event.summary ?: return null
        val location = event.location ?: return null
        val roomId = getRoomId(location) ?: return null

        return EventEntity(
            eventId = id.toString(),
            start = Date(start.date.time),
            end = Date(end.date.time),
            title = title.value,
            roomId = roomId
        )
    }

    private fun getRoomId(location: Location): String? {
        // The source room names do not follow a specified pattern.
        // As such, they need to be transformed to [Number][Building].

        // "000A ..." or "000 A ..."
        val numberBuilding = Regex("^(\\d+|\\d+\\.\\d+) ?([A-D]) .*").find(location.value)
        // "A000 ..." or "A 000 "
        val buildingNumber = Regex("^([A-D]) ?(\\d+|\\d+\\.\\d+) .*").find(location.value)

        if (numberBuilding != null) {
            val (number, building) = numberBuilding.destructured
            return number + building
        } else if (buildingNumber != null) {
            val (building, number) = buildingNumber.destructured
            return number + building
        }

        return null
    }

    private fun iCalLocationToRoom(location: Location): RoomEntity? {
        val id = getRoomId(location) ?: return null

        val building = id[id.length - 1].toString()
        val number = id.substring(0, id.length - 1)
        val floor = number[0].toString()

        return RoomEntity(
            roomId = id,
            fullName = location.value,
            building = building,
            floor = floor,
            number = number
        )
    }

    private suspend fun addRoomsToDatabase(roomEntities: List<RoomEntity>) {
        db.roomDao().insertRooms(roomEntities)
    }
}