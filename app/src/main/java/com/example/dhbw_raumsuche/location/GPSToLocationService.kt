package com.example.dhbw_raumsuche.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.abs

class GPSToLocationService : LocationService {

    private val fusedLocationProviderClient: FusedLocationProviderClient

    constructor(context: Context)
            : this(
        LocationServices.getFusedLocationProviderClient(context),
    )

    constructor(
        fusedLocationProviderClient: FusedLocationProviderClient,
    ) {
        this.fusedLocationProviderClient = fusedLocationProviderClient
    }

    override suspend fun getLocation(
        context: Context
    ): UserLocation? {
        if (!checkLocationPermission(context)
        ) {
            return null
        }
        return suspendCancellableCoroutine { cont ->
            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener) =
                        CancellationTokenSource().token

                    override fun isCancellationRequested() = false
                }).addOnSuccessListener { location ->
                if (location != null) {
                    val building = calculateNearestBuildingForLocation(location)
                    val floor = calculateNearestFloor(location, building)
                    cont.resume(UserLocation(building.building, floor))
                } else {
                    cont.resumeWithException(Exception("Location could not be retrieved"))
                }
            }
                .addOnFailureListener { e ->
                    cont.resumeWithException(e)
                }
        }
    }

    private fun calculateNearestBuildingForLocation(location: Location): DHBWBuilding {
        return DHBWBuilding.buildings.minByOrNull { building ->
            val buildingLocation = Location("custom_provider").apply {
                latitude = building.latitude
                longitude = building.longitude
                building.floors[Floor.FirstFloor]?.let { altitude = it }
            }
            location.distanceTo(buildingLocation)
        } ?: DHBWBuilding.buildings.first()
    }

    private fun calculateNearestFloor(
        location: Location,
        building: DHBWBuilding
    ): Floor {
        return building.floors
            .minByOrNull { floor ->
                abs(location.altitude - floor.value)
            }?.key ?: Floor.FirstFloor

    }

    companion object {
        fun checkLocationPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}