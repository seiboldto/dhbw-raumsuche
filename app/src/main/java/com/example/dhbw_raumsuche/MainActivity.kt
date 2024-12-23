package com.example.dhbw_raumsuche

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.dhbw_raumsuche.network.ICalDataExtractor.Companion.parseICalData
import com.example.dhbw_raumsuche.ui.theme.Dhbw_raumsucheTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
