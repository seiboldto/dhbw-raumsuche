package com.example.dhbw_raumsuche

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.dhbw_raumsuche.network.ServerConnector.Companion.downloadAndExtractRoomsData
import com.example.dhbw_raumsuche.ui.theme.Dhbw_raumsucheTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dhbw_raumsucheTheme {
                // Call your composable here
                Scaffold(
                    topBar = { AppTopBar() } // Use a stable TopBar
                ) { paddingValues ->
                    // Display the list content with padding from Scaffold
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background // Use theme's background color
                        ) {
                            RoomListScreen() // The composable displaying the list
                        }
                    }
                }

            }
        }
        getRoomsData()
    }

    private fun getRoomsData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val roomsData = downloadAndExtractRoomsData()
                Log.d("MainActivity", "Data received: $roomsData")
            }
        }
    }

}

@Composable
fun RoomListScreen() {
    // Sample data
    val itemsList = List(10) { "Item #${it + 1}" }

    // LazyColumn for vertical scrolling
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp), // Space between items
        horizontalAlignment = Alignment.CenterHorizontally // Center items horizontally
    ) {
        items(itemsList) { item ->
            RoomListItem(text = item)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    // Stable CenterAlignedTopAppBar
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text("Small Top App Bar")
        }
    )

}

@Composable
fun RoomListItem(text: String) {
    // Material3 Card for a block item
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f) // Adjust width
            .height(80.dp), // Fixed height
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer // Use Material3 color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Elevation for depth
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), // Fill the card size
            contentAlignment = Alignment.Center // Center content inside the card
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy( // Use Material3 typography
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer // Contrast color
            )
        }
    }
}
