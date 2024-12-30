package com.example.dhbw_raumsuche.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dhbw_raumsuche.data.local.dataclass.RoomWithEvents
import com.example.dhbw_raumsuche.ui.viewmodel.RoomSortType
import com.example.dhbw_raumsuche.ui.viewmodel.RoomViewModel
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import com.example.dhbw_raumsuche.location.Building


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    roomViewModel: RoomViewModel
    ) {
    val isLoading by roomViewModel.isLoading.collectAsState()
    val roomListState by roomViewModel.roomList.collectAsState()
    val filterSettings by roomViewModel.filterSettings.collectAsState()
    val selectedSortType by roomViewModel.sortType.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()}
        } else {

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            "Raumsuche",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Einstellungen"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { innerPadding ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
                content = {
                    Text("Filter:")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Building.entries.forEach {
                            Button(
                                onClick = { roomViewModel.setBuildingFilter(it) }
                            ) { Text(it.name) }
                        }
                    }
                    Text("Sort:")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = CenterVertically
                    ) {
                        RoomSortType.entries.forEach { sortType ->
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        roomViewModel.setSortType(sortType)
                                    },
                                verticalAlignment = CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedSortType == sortType,
                                    onClick = {
                                        roomViewModel.setSortType(sortType)
                                    }
                                )
                                Text(text = sortType.name)
                            }
                        }
                    }
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(roomListState) { roomWithEvents ->
                            RoomListItem(roomWithEvents)
                        }
                    }
                }
            )
        }

    }
}

@Composable
fun RoomListItem(roomWithEvents: RoomWithEvents) {

    //Managing expanded State for Cardexpantion
    var expandedState by remember { mutableStateOf(false) }
    //Animating the Arrow rotation
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )
    //favorite
    var favorite by remember { mutableStateOf(false) }
    val starcolor = if(favorite) Color.Yellow else Color.Black    //starcolor when favored

    // Material3 Card for a block item
    Card(
        modifier = Modifier
            .fillMaxWidth(1f) // Adjust width
            .height(if (expandedState) 200.dp else 80.dp) // Fixed height
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ), //enable smooth content size animations
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer // Use Material3 color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Elevation for depth
        onClick = {
            expandedState = !expandedState      //expand on click
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (!expandedState) {
                Text(
                    text = roomWithEvents.room.fullName,
                    style = MaterialTheme.typography.bodyLarge.copy( // Use Material3 typography
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer, // Contrast color
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            } else {
                Text(
                    text = roomWithEvents.room.fullName,
                    style = MaterialTheme.typography.bodyLarge.copy( // Use Material3 typography
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer, // Contrast color
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Star",
                    modifier = Modifier
                        .clickable { favorite = !favorite },
                    tint = starcolor
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Drop-down Arrow",
                    modifier = Modifier
                        .rotate(rotationState)
                        .clickable { expandedState = !expandedState },
                )
            }
        }
    }

}