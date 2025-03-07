package com.example.dhbw_raumsuche

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.Manifest
import android.widget.Toast
import com.example.dhbw_raumsuche.data.RoomDataProvider.Companion.getRoomData
import com.example.dhbw_raumsuche.data.local.RoomPersistenceService
import com.example.dhbw_raumsuche.data.local.RoomsDatabase
import com.example.dhbw_raumsuche.ical.ICalParser
import com.example.dhbw_raumsuche.location.GPSToLocationService
import com.example.dhbw_raumsuche.location.GPSToLocationService.Companion.checkLocationPermission
import com.example.dhbw_raumsuche.location.LocalLocationModel
import com.example.dhbw_raumsuche.location.LocationViewModel
import com.example.dhbw_raumsuche.ui.LoadingScreen
import com.example.dhbw_raumsuche.ui.theme.CustomTheme
import com.example.dhbw_raumsuche.ui.viewmodel.LocalSettingsModel
import com.example.dhbw_raumsuche.ui.viewmodel.RoomViewModel
import com.example.dhbw_raumsuche.ui.viewmodel.SettingsViewModel
import com.example.dhbw_raumsuche.ui.viewmodel.favoritesStore
import com.example.dhbw_raumsuche.ui.viewmodel.settingsStore

class MainActivity : ComponentActivity() {
    private val db by lazy {
        RoomsDatabase.getInstance(this)
    }

    private val roomViewModel: RoomViewModel by viewModels<RoomViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return RoomViewModel(
                        applicationContext.favoritesStore,
                        db.roomDao()
                    ) { writeLatestRoomDataToDB() } as T
                }
            }
        }
    )

    private val settingsViewModel: SettingsViewModel by viewModels<SettingsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return SettingsViewModel(applicationContext.settingsStore) as T
                }
            }
        }
    )

    private val locationViewModel = LocationViewModel(requestLocation = { requestLocation() })
    private lateinit var gpsToLocationService: GPSToLocationService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationViewModel.updateLocation(
                gpsToLocationService, this
            )
        } else {
            Toast.makeText(this, R.string.location_error, Toast.LENGTH_LONG)
        }
    }

    private fun requestLocation() {
        if (!checkLocationPermission(this)) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationViewModel.updateLocation(
                gpsToLocationService, this
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        gpsToLocationService = GPSToLocationService(this)

        setContent {
            CompositionLocalProvider(LocalSettingsModel provides settingsViewModel) {
                CompositionLocalProvider(LocalLocationModel provides locationViewModel) {
                    CustomTheme {
                        LoadingScreen(roomViewModel, settingsViewModel)
                    }
                }
            }
        }
    }

    private suspend fun writeLatestRoomDataToDB() {
        settingsViewModel.setIsLoading(true)
        try {
            val roomData = getRoomData(this@MainActivity)
            settingsViewModel.setIsLoading(false)
            val persistenceService = RoomPersistenceService(this@MainActivity)
            persistenceService.updateDbFromICal(ICalParser.parseICal(roomData.iCals))
        } catch (err: Throwable) {
            settingsViewModel.setError(err)
        } finally {
            settingsViewModel.setIsLoading(false)
        }
    }
}