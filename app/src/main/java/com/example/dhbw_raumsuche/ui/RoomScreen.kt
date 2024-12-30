package com.example.dhbw_raumsuche.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dhbw_raumsuche.location.Building
import com.example.dhbw_raumsuche.ui.viewmodel.RoomSortType
import com.example.dhbw_raumsuche.ui.viewmodel.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomScreen(
    roomViewModel: RoomViewModel
) {
    val roomListState by roomViewModel.roomList.collectAsState()
    val filterSettings by roomViewModel.filterSettings.collectAsState()
    val selectedSortType by roomViewModel.sortType.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                if (roomWithEvents.isFree) {
                                    Text(
                                        text = "Frei " + roomWithEvents.getReadableFreeTime(),
                                        fontSize = 12.sp,
                                        color = Color.Magenta
                                    )
                                } else {
                                    Text(
                                        text = "Belegt",
                                        fontSize = 12.sp,
                                        color = Color.Red
                                    )
                                }
                                Text(
                                    text = roomWithEvents.room.fullName,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 20.sp
                                )
                                if (roomWithEvents.room.building != "") {
                                    Text(
                                        text = "Gebäude:${roomWithEvents.room.building} Etage:${roomWithEvents.room.floor} Nummer:${roomWithEvents.room.number}",
                                        fontSize = 16.sp
                                    )
                                }

                                roomWithEvents.eventsToday.forEach { event ->
                                    Text(
                                        text = event.start.toString() + " " + event.title,
                                        color = Color.Blue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
