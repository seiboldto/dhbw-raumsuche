package com.example.dhbw_raumsuche.network

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

class ServerConnector {

    companion object {
        suspend fun downloadAndExtractJson(): String? {
            val client = OkHttpClient()

            val url = "http://192.248.187.245:80/api/dhbw-rooms"
            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/dhbw")
                .build()

            try {
                val response = withContext(IO) { client.newCall(request).execute() }

                if (!response.isSuccessful) {
                    throw Exception("Failed to download file: ${response.code}")
                }

                val gzipStream = withContext(IO) {
                    GZIPInputStream(response.body.byteStream())
                }
                val reader = InputStreamReader(gzipStream)
                val json = Json.decodeFromString<ApiResponse>(reader.readText())
                return json.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

}
