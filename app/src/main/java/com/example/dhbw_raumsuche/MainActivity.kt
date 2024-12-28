package com.example.dhbw_raumsuche

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.dhbw_raumsuche.data.RoomDataProvider
import com.example.dhbw_raumsuche.location.GPSToLocationService
import com.example.dhbw_raumsuche.location.GPSToLocationService.Companion.checkLocationPermission
import com.example.dhbw_raumsuche.location.LocationViewModel
import com.example.dhbw_raumsuche.network.ICalDataExtractor.Companion.parseICalData
import com.example.dhbw_raumsuche.ui.theme.Dhbw_raumsucheTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

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
            Dhbw_raumsucheTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        getRoomData()
        updateLocation()
    }

    private fun getRoomData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val roomData =
                    withContext(Dispatchers.IO) { RoomDataProvider.getRoomData(this@MainActivity) }
                val eventsByRoom =
                    withContext(Dispatchers.Default) { parseICalData(roomData.iCals) }
                Log.d("MainActivity", eventsByRoom.keys.toString())
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Dhbw_raumsucheTheme {
        Greeting("Android")
    }
}
