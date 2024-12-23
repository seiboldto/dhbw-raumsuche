package com.example.dhbw_raumsuche.location

import android.content.Context

interface LocationService {

    suspend fun getLocation(context: Context): UserLocation?

}