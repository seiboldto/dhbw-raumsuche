package com.example.dhbw_raumsuche.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dhbw_raumsuche.ui.viewmodel.RoomListEvent
import com.example.dhbw_raumsuche.ui.viewmodel.RoomListState
import com.example.dhbw_raumsuche.ui.viewmodel.RoomSortType

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RoomScreen(
    state: RoomListState,
    onEvent: (RoomListEvent) -> Unit,
    ) {
    Scaffold() { _ ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
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
                                    onEvent(RoomListEvent.SortRooms(sortType))
                                },
                            verticalAlignment = CenterVertically
                        ) {
                            RadioButton(
                                selected = state.sortType == sortType,
                                onClick = {
                                    onEvent(RoomListEvent.SortRooms(sortType))
                                }
                            )
                            Text(text = sortType.name)
                        }
                    }
                }
            }
            items(state.rooms) { roomWithEvents ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (roomWithEvents.isFree){
                            Text(
                                text = "Frei " + roomWithEvents.getReadableFreeTime(),
                                fontSize = 12.sp,
                                color = Color.Magenta
                            )
                        }else{
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

                        roomWithEvents.eventsToday.forEach { event -> Text(text = event.start.toString() + " " + event.title, color = Color.Blue)}
                    }
                }
            }
        }
    }
}