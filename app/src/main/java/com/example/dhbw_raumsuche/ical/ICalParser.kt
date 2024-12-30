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

    suspend fun parseICal(icals: List<String>) {
        // operate ical4j with minify. see https://stackoverflow.com/questions/50733209/ical4j-2-2-0-using-grape-throws-java-lang-noclassdeffounderror-javax-cache-con
        MapTimeZoneCache() // tell proguard that the class is used
        System.setProperty(
            "net.fortuna.ical4j.timezone.cache.impl",
            "net.fortuna.ical4j.util.MapTimeZoneCache"
        )

        val registry: TimeZoneRegistry = TimeZoneRegistryImpl("zoneinfo-outlook-global/")
        val builder = CalendarBuilder(registry)

        for (ical in icals) {
            val sin = StringReader(cleanMalformedIcal(ical))
            val calendar: Calendar = builder.build(sin)
            updateDbFromIcal(calendar)
        }
    }

    private fun cleanMalformedIcal(calendarString: String): String {
        return calendarString.replace("\\n\\s".toRegex(), "")
    }

    private suspend fun updateDbFromIcal(calendar: Calendar) {
        val events: List<VEvent> = calendar.getComponents(Component.VEVENT)
        val locations: List<Location> =
            events.mapNotNull { event -> event.location }.toSet().toList()

        for (location in locations) {
            val room = icalLocationToRoom(location) ?: continue
            addRoomToDatabase(room)
        }

        for (event in events) {
            val eventEntity = icalEventToEventEntity(event)
            if (eventEntity != null) {
                eventDao.insertEvent(eventEntity)
            }
        }
    }

    private fun icalEventToEventEntity(event: VEvent): EventEntity? {
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
        val idRegex = "(.+?)\\s(.*)".toRegex()
        val matchIdResult = idRegex.find(location.value)
        return matchIdResult?.groupValues?.get(1)
    }

    private fun icalLocationToRoom(location: Location): RoomEntity? {
        val id = getRoomId(location) ?: return null

        val matchDetailResult: MatchResult? = "([0-9])([0-9][0-9])([A-Z])".toRegex().find(id)

        val building: String = matchDetailResult?.groups?.get(3)?.value ?: ""
        val floor = matchDetailResult?.groups?.get(1)?.value ?: ""
        val number = matchDetailResult?.groups?.get(2)?.value ?: ""

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