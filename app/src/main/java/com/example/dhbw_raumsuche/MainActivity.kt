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
import com.example.dhbw_raumsuche.data.local.RoomsDatabase
import com.example.dhbw_raumsuche.ical.ICalParser
import com.example.dhbw_raumsuche.network.ServerConnector.Companion.downloadAndExtractRoomsData
import com.example.dhbw_raumsuche.ui.theme.Dhbw_raumsucheTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val db by lazy { RoomsDatabase.getInstance(applicationContext) }

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
        getRoomsData()
    }

    private fun getRoomsData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val icalParser: ICalParser = ICalParser(applicationContext)
                icalParser.parseICal()
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
