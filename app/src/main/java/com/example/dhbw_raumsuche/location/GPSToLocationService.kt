package com.example.dhbw_raumsuche.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.runBlocking
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
                    val floor = calculateNearestFloor(context, location, building)
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
        context: Context,
        location: Location,
        building: DHBWBuilding
    ): Floor {
        return if (isAtTheUniversity(context, location)) {
            building.floors
                .minByOrNull { floor ->
                    abs(location.altitude - floor.value)
                }?.key ?: Floor.FirstFloor
        } else {
            Floor.FirstFloor
        }
    }

    private fun isAtTheUniversity(context: Context, location: Location): Boolean {
        return runBlocking {
            try {
                val address = getAddressForLocation(context, location)
                address?.getAddressLine(0)
                    ?.contains("Seckenheimer Landstraße|Coblitzallee|Coblitzweg|Hans-Thoma") != null
            } catch (e: Exception) {
                Log.e(
                    "GPSToLocationService",
                    "An error occurred while fetching the address: ${e.message}",
                    e
                )
                false
            }
        }
    }

    private suspend fun getAddressForLocation(context: Context, location: Location): Address? {
        return suspendCancellableCoroutine { cont ->
            val geocoder = Geocoder(context)
            geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        cont.resume(addresses.firstOrNull())
                    }

                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                        cont.resumeWithException(Exception(errorMessage))
                    }
                })
        }
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