package com.example.dhbw_raumsuche.network

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

class ICalDataExtractor {

    data class Event(
        val start: LocalDateTime,
        val end: LocalDateTime,
        val summary: String
    )

    companion object {
        fun parseICalData(iCalData: List<String>): Map<String, List<Event>> {
            val dateTimeFormatter = createDateTimeFormatter()
            val eventsByRoom = mutableMapOf<String, MutableList<Event>>()

            val eventPattern = Regex(
                """BEGIN:VEVENT.*?DTSTART;TZID=Europe/Berlin:(\d{8}T\d{6}).*?DTEND;TZID=Europe/Berlin:(\d{8}T\d{6}).*?SUMMARY:([^\n]+).*?LOCATION:(\d+\s?\w|[^\n]+).*?END:VEVENT""",
                RegexOption.DOT_MATCHES_ALL
            )

            iCalData.forEach { data ->
                for (match in eventPattern.findAll(data)) {
                    val room = match.groupValues[4]
                    if (room.startsWith("E") || room.contains("MA") || room.contains("Online")) {
                        continue
                    }

                    val start = LocalDateTime.parse(match.groupValues[1], dateTimeFormatter)
                    val end = LocalDateTime.parse(match.groupValues[2], dateTimeFormatter)
                    val summary = match.groupValues[3].trim()
                    val event = Event(start, end, summary)
                    eventsByRoom.computeIfAbsent(room) { mutableListOf() }.add(event)
                }
            }
            return eventsByRoom.mapValues { it.value.toList() }
        }

        private fun createDateTimeFormatter(): DateTimeFormatter? {
            return DateTimeFormatterBuilder()
                .appendPattern("yyyyMMdd'T'HHmm")
                .optionalStart()
                .appendPattern("ss")
                .optionalEnd()
                .toFormatter()
        }

    }

}