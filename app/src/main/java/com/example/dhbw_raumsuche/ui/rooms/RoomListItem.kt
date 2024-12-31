package com.example.dhbw_raumsuche.ui.rooms

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.dhbw_raumsuche.R
import com.example.dhbw_raumsuche.data.local.dataclass.RoomWithEvents
import com.example.dhbw_raumsuche.ui.theme.darkgreen
import com.example.dhbw_raumsuche.ui.viewmodel.RoomViewModel
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import com.example.dhbw_raumsuche.data.local.entity.EventEntity as EventEntity


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
            color = darkgreen
        )
    } else {
        Text(
            text = stringResource(R.string.occupied),
            fontSize = 12.sp,
            color = Color.Red
        )
    }
}

@Composable
fun Timeline(events: List<EventEntity>, startTime: Int = 0, endTime: Int = 24) {
    val totalMinutes = (endTime - startTime) * 60
    val blockHeight = 40.dp
    val labelHeight = 20.dp
    val labelPadding = 8.dp
    val backgroundColor = MaterialTheme.colorScheme.inverseOnSurface
    val occupiedColor = MaterialTheme.colorScheme.error
    val labelColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(blockHeight + 2 * labelHeight + labelPadding)
            .padding(horizontal = 16.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                color = backgroundColor,
                size = Size(size.width, size.height - 2 * labelHeight.toPx() - labelPadding.toPx())
            )

            val usedLabelHeights = mutableListOf<Float>()

            events.forEach { event ->
                val eventStartFraction =
                    (event.start.toFractionOfDay() * totalMinutes - startTime * 60) / totalMinutes
                val eventEndFraction =
                    (event.end.toFractionOfDay() * totalMinutes - startTime * 60) / totalMinutes

                val startX = size.width * eventStartFraction
                val endX = size.width * eventEndFraction

                drawRect(
                    color = occupiedColor,
                    topLeft = Offset(startX, 0f),
                    size = Size(endX - startX, size.height - 2 * labelHeight.toPx() - labelPadding.toPx())
                )

                val labelYBase = size.height - labelHeight.toPx() / 2
                val adjustedLabelY = usedLabelHeights.find { kotlin.math.abs(it - labelYBase) < labelHeight.toPx() }
                    ?.let { labelYBase - labelHeight.toPx() } ?: labelYBase
                usedLabelHeights.add(adjustedLabelY)

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        event.start.toTimeLabel(),
                        startX,
                        adjustedLabelY,
                        android.graphics.Paint().apply {
                            color = labelColor.toArgb()
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }

                val adjustedEndLabelY = usedLabelHeights.find { kotlin.math.abs(it - labelYBase) < labelHeight.toPx() }
                    ?.let { labelYBase - labelHeight.toPx() } ?: labelYBase
                usedLabelHeights.add(adjustedEndLabelY)

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        event.end.toTimeLabel(),
                        endX,
                        adjustedEndLabelY,
                        android.graphics.Paint().apply {
                            color = labelColor.toArgb()
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}

private fun Date.toFractionOfDay(): Float {
    val localDateTime = this.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    val totalMinutes = localDateTime.hour * 60 + localDateTime.minute
    return totalMinutes / (24 * 60).toFloat()
}

private fun Date.toTimeLabel(): String {
    val localDateTime = this.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    return String.format(Locale.getDefault(), "%02d:%02d", localDateTime.hour, localDateTime.minute)
}