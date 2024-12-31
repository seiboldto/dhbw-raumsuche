package com.example.dhbw_raumsuche.data

import android.content.Context
import android.util.Log
import com.example.dhbw_raumsuche.network.RoomJson
import com.example.dhbw_raumsuche.network.ServerConnector.Companion.downloadRoomData
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class RoomDataProvider {

    companion object {
        private const val LOGGING_TAG = "RoomDataProvider"
        private const val ROOM_JSON_FILENAME = "room.json"

        suspend fun getRoomData(context: Context): RoomJson {
            val file = File(context.filesDir, ROOM_JSON_FILENAME)

            val localData = readLocalRoomData(file)
            if (localData != null && isLatestJson(localData.updatedAt.toLong() * 1000)) {
                Log.d(LOGGING_TAG, "Local RoomJson is up to date")
                return localData
            }

            return downloadAndSaveRoomData(file)
        }

        private fun readLocalRoomData(file: File): RoomJson? {
            return if (file.exists()) {
                try {
                    val jsonString = file.readText()
                    Json.decodeFromString(jsonString)
                } catch (e: Exception) {
                    Log.e(
                        LOGGING_TAG,
                        "Error while reading or parsing the local room json file: ${e.message}"
                    )
                    null
                }
            } else {
                null
            }
        }

        private suspend fun downloadAndSaveRoomData(file: File): RoomJson {
            Log.d(LOGGING_TAG, "start downloading the latest RoomJson")
            val latestRoomJsonString = downloadRoomData()
            Log.d(LOGGING_TAG, "start writing RoomJson to disk")
            file.writeText(latestRoomJsonString)
            return Json.decodeFromString(latestRoomJsonString)
        }

        private fun isLatestJson(timestamp: Long): Boolean {
            val currentDate = LocalDate.now()
            val dateFromTimestamp =
                Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

            return currentDate == dateFromTimestamp
        }

    }

}