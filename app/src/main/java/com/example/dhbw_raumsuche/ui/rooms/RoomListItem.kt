package com.example.dhbw_raumsuche.ui.rooms

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dhbw_raumsuche.R
import com.example.dhbw_raumsuche.data.local.dataclass.RoomWithEvents
import com.example.dhbw_raumsuche.ui.theme.darkGreen
import com.example.dhbw_raumsuche.ui.viewmodel.RoomViewModel

@Composable
fun RoomListItem(roomViewModel: RoomViewModel, roomWithEvents: RoomWithEvents) {
    // Managing expanded State for Card expansion
    var expandedState by remember { mutableStateOf(false) }

    // Animating the Arrow rotation
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    // Material3 Card for a block item
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxWidth(.80f)) {
                    Text(
                        text = roomWithEvents.room.fullName,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer, // Contrast color
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    RoomStatus(roomWithEvents)
                }
                Row {
                    ShowFavStar(roomViewModel, roomWithEvents)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(rotationState)
                            .clickable { expandedState = !expandedState },
                    )
                }
            }
            if (expandedState) {
                Timeline(roomWithEvents.eventsToday)
            }
        }
    }
}

@Composable
private fun ShowFavStar(roomViewModel: RoomViewModel, roomWithEvents: RoomWithEvents) {
    val favorites = roomViewModel.favorites.collectAsState()

    Icon(
        Icons.Default.Star,
        contentDescription = null,
        modifier = Modifier
            .clickable { roomViewModel.toggleFavorite(roomWithEvents.room.roomId) },
        tint = if (favorites.value.contains(roomWithEvents.room.roomId)) Color.White else Color.Black
    )
}

@Composable
private fun RoomStatus(roomWithEvents: RoomWithEvents) {
    if (roomWithEvents.isFree) {
        Text(
            text = stringResource(R.string.free) + roomWithEvents.getReadableFreeTime(),
            fontSize = 12.sp,
            color = darkGreen
        )
    } else {
        Text(
            text = stringResource(R.string.occupied),
            fontSize = 12.sp,
            color = Color.Red
        )
    }
}