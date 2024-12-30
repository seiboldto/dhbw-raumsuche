package com.example.dhbw_raumsuche

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.dhbw_raumsuche.data.RoomDataProvider
import com.example.dhbw_raumsuche.data.local.RoomsDatabase
import com.example.dhbw_raumsuche.ical.ICalParser
import com.example.dhbw_raumsuche.location.GPSToLocationService
import com.example.dhbw_raumsuche.location.GPSToLocationService.Companion.checkLocationPermission
import com.example.dhbw_raumsuche.location.LocationViewModel
import com.example.dhbw_raumsuche.ui.LoadingScreen
import com.example.dhbw_raumsuche.ui.theme.CustomTheme
import com.example.dhbw_raumsuche.ui.viewmodel.LocalSettingsModel
import com.example.dhbw_raumsuche.ui.viewmodel.RoomViewModel
import com.example.dhbw_raumsuche.ui.viewmodel.SettingsViewModel
import com.example.dhbw_raumsuche.ui.viewmodel.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val db by lazy {
        RoomsDatabase.getInstance(this)
    }

    private val roomViewModel: RoomViewModel by viewModels<RoomViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return RoomViewModel(db.roomDao()) { getRoomData() } as T
                }
            }
        }
    )

    private val settingsViewModel: SettingsViewModel by viewModels<SettingsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return SettingsViewModel(applicationContext.dataStore) as T
                }
            }
        }
    )

    private val locationViewModel = LocationViewModel()
    private lateinit var gpsToLocationService: GPSToLocationService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationViewModel.updateLocation(
                gpsToLocationService, this
            )
        } else {
            Toast.makeText(
                this,
                "This app requires permission to be granted in order to show the nearest building to your location!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gpsToLocationService = GPSToLocationService(this)
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(LocalSettingsModel provides settingsViewModel) {
                CustomTheme {
                   LoadingScreen(roomViewModel, settingsViewModel)
                }
            }
        }
    }

    private fun getRoomData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.setIsLoading(true)

                try {
                    val roomData =
                        withContext(Dispatchers.IO) { RoomDataProvider.getRoomData(this@MainActivity) }
                    settingsViewModel.setIsLoading(false)
                    val parser = withContext(Dispatchers.Default) { ICalParser(this@MainActivity) }
                    parser.parseICal(roomData.iCals)
                } catch (err: Throwable) {
                    settingsViewModel.setError(err)
                }
            }
        }
    }

    private fun updateLocation() {
        if (!checkLocationPermission(this)) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationViewModel.updateLocation(
                gpsToLocationService, this
            )
        }
    }

}


