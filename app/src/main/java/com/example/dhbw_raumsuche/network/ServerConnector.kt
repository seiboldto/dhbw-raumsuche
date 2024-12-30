package com.example.dhbw_raumsuche.network

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream

class ServerConnector {

    companion object {
        suspend fun downloadRoomData(): String {
            val client = OkHttpClient()
            val request = createRequest()
            return fetchRoomData(client, request)
        }

        private suspend fun fetchRoomData(client: OkHttpClient, request: Request): String {
            val response = withContext(IO) { client.newCall(request).execute() }

            if (!response.isSuccessful) {
                throw Exception("Failed to download file: ${response.code}")
            }

            return withContext(IO) {
                GZIPInputStream(response.body.byteStream()).use { gzipStream ->
                    val reader = InputStreamReader(gzipStream)
                    reader.readText()
                }
            }
        }

        private fun createRequest(): Request {
            return Request.Builder()
                .url("http://192.248.187.245/api/v1/dhbw-rooms")
                .addHeader("Accept", "application/dhbw")
                .build()
        }
    }

}
