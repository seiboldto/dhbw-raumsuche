package com.example.dhbw_raumsuche.ical

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.TimeZoneRegistry
import net.fortuna.ical4j.model.TimeZoneRegistryImpl
import net.fortuna.ical4j.util.MapTimeZoneCache
import java.io.StringReader

class ICalParser {

    companion object {

        fun parseICal(iCals: List<String>): List<Calendar> {
            // Operate ical4j with minify.
            // See https://stackoverflow.com/questions/50733209/ical4j-2-2-0-using-grape-throws-java-lang-noclassdeffounderror-javax-cache-con
            MapTimeZoneCache() // Tell proguard that the class is used
            System.setProperty(
                "net.fortuna.ical4j.timezone.cache.impl",
                "net.fortuna.ical4j.util.MapTimeZoneCache"
            )

            val registry: TimeZoneRegistry = TimeZoneRegistryImpl("zoneinfo-outlook-global/")
            val builder = CalendarBuilder(registry)

            return iCals.map { iCal ->
                val sin = StringReader(cleanMalformedICal(iCal))
                builder.build(sin)
            }
        }

        private fun cleanMalformedICal(calendarString: String): String {
            return calendarString.replace("\\n\\s".toRegex(), "")
        }
    }

}