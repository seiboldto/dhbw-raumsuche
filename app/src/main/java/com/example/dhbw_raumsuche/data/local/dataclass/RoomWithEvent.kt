package com.example.dhbw_raumsuche.data.local.dataclass

import com.example.dhbw_raumsuche.data.local.entity.EventEntity
import com.example.dhbw_raumsuche.data.local.entity.RoomEntity

import androidx.room.Embedded
import androidx.room.Relation
import java.util.Calendar

data class RoomWithEvents(
    @Embedded val room: RoomEntity, // Embeds RoomEntity data
    @Relation(
        parentColumn = "roomId",
        entityColumn = "roomId"
    )
    val events: List<EventEntity> // List of associated events
) {
    val eventsToday: List<EventEntity>
        get() {
            val todayStart = getTodayStart()
            val tomorrowStart = getTomorrowStart()
            return events.filter { event ->
                event.start.time in todayStart..<tomorrowStart
            }
        }

    val isFree: Boolean
        get() {
            val now = Calendar.getInstance().timeInMillis
            return events.none { event ->
                event.start.time <= now && event.end.time >= now
            }
        }

    val isOccupied: Boolean
        get() {
            return !isFree
        }

    val freeTime: Long
        get() {
            val now = Calendar.getInstance().timeInMillis
            val nextEvent = events
                .filter { event -> event.start.time > now }
                .minByOrNull { it.start.time }

            return nextEvent?.start?.time?.minus(now) ?: Long.MAX_VALUE
        }

    val building: String
        get() {
            return room.building
        }

    fun getReadableFreeTime(): String {
        return when {
            freeTime > 24 * 60 * 60 * 1000 -> "> 24h"
            freeTime >= 60 * 60 * 1000 -> "${freeTime / (60 * 60 * 1000)}h"
            freeTime >= 60 * 1000 -> "${freeTime / (60 * 1000)}min"
            else -> "< 1min"
        }
    }

    private fun getTodayStart(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getTomorrowStart(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
