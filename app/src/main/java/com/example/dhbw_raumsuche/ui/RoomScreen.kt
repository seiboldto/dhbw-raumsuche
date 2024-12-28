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
            items(state.rooms) { room ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = room.roomId,
                            fontSize = 20.sp
                        )
                        Text(
                            text = room.fullName,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Gebäude:${room.building} Etage:${room.floor} Nummer:${room.number}",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}