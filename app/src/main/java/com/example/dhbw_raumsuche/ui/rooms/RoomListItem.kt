package com.example.dhbw_raumsuche.ui.rooms

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dhbw_raumsuche.data.local.dataclass.RoomWithEvents
import com.example.dhbw_raumsuche.ui.theme.darkgreen


@Composable
fun RoomListItem(roomWithEvents: RoomWithEvents) {

    //Managing expanded State for Cardexpantion
    var expandedState by remember { mutableStateOf(false) }
    //Animating the Arrow rotation
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f
    )

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
                Row(
                    verticalAlignment = CenterVertically,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(0.90f)
                ) {
                    Text(
                        text = roomWithEvents.room.fullName,
                        style = MaterialTheme.typography.bodyLarge.copy( // Use Material3 typography
                            fontWeight = FontWeight.Bold
                        ),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer, // Contrast color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ShowRoomStatus(roomWithEvents)
                }
                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    ShowFavStar()
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-down Arrow",
                        modifier = Modifier
                            .rotate(rotationState)
                            .clickable { expandedState = !expandedState },
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(0.90f)
                ) {
                    Text(
                        text = roomWithEvents.room.fullName,
                        style = MaterialTheme.typography.bodyLarge.copy( // Use Material3 typography
                            fontWeight = FontWeight.Bold
                        ),
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer, // Contrast color
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ShowRoomStatus(roomWithEvents)
                }
                Row(modifier = Modifier.align(Alignment.TopEnd)) {
                    ShowFavStar()
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
}

@Composable
private fun ShowFavStar() {
    //favorite
    var favorite by remember { mutableStateOf(false) }
    val starcolor = if (favorite) Color.Yellow else Color.Black    //starcolor when favored

    Icon(
        Icons.Default.Star,
        contentDescription = "Star",
        modifier = Modifier
            .clickable { favorite = !favorite },
        tint = starcolor
    )
}

@Composable
private fun ShowRoomStatus(roomWithEvents: RoomWithEvents) {
    if (roomWithEvents.isFree) {
        Text(
            text = "Frei " + roomWithEvents.getReadableFreeTime(),
            fontSize = 12.sp,
            color = darkgreen
        )
    } else {
        Text(
            text = "Belegt",
            fontSize = 12.sp,
            color = Color.Red
        )
    }
}