package com.example.dhbw_raumsuche.ical

import android.content.Context
import com.example.dhbw_raumsuche.data.local.RoomsDatabase
import com.example.dhbw_raumsuche.data.local.dao.EventDao
import com.example.dhbw_raumsuche.data.local.entity.EventEntity
import com.example.dhbw_raumsuche.data.local.entity.RoomEntity
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.Component
import net.fortuna.ical4j.model.TimeZoneRegistry
import net.fortuna.ical4j.model.TimeZoneRegistryImpl
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Location
import net.fortuna.ical4j.util.MapTimeZoneCache
import java.io.StringReader
import java.sql.Date

class ICalParser(private val context: Context) {
    private val db: RoomsDatabase by lazy { RoomsDatabase.getInstance(context) }
    private val eventDao: EventDao by lazy { db.eventDao() }

    suspend fun parseICal(iCals: List<String>) {
        // Operate ical4j with minify.
        // See https://stackoverflow.com/questions/50733209/ical4j-2-2-0-using-grape-throws-java-lang-noclassdeffounderror-javax-cache-con
        MapTimeZoneCache() // Tell proguard that the class is used
        System.setProperty(
            "net.fortuna.ical4j.timezone.cache.impl",
            "net.fortuna.ical4j.util.MapTimeZoneCache"
        )

        val registry: TimeZoneRegistry = TimeZoneRegistryImpl("zoneinfo-outlook-global/")
        val builder = CalendarBuilder(registry)

        for (iCal in iCals) {
            val sin = StringReader(cleanMalformedICal(iCal))
            val calendar: Calendar = builder.build(sin)
            updateDbFromICal(calendar)
        }
    }

    private fun cleanMalformedICal(calendarString: String): String {
        return calendarString.replace("\\n\\s".toRegex(), "")
    }

    private suspend fun updateDbFromICal(calendar: Calendar) {
        val events: List<VEvent> = calendar.getComponents(Component.VEVENT)
        val locations: List<Location> =
            events.mapNotNull { event -> event.location }.toSet().toList()

        for (location in locations) {
            val room = iCalLocationToRoom(location) ?: continue
            addRoomToDatabase(room)
        }

        for (event in events) {
            val eventEntity = iCalEventToEventEntity(event)
            if (eventEntity != null) {
                eventDao.insertEvent(eventEntity)
            }
        }
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

    private suspend fun addRoomToDatabase(room: RoomEntity) {
        db.roomDao().insertRoom(room)
    }
}