package com.example.dhbw_raumsuche.ui.rooms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.example.dhbw_raumsuche.data.local.dataclass.RoomWithEvents
import java.time.LocalTime
import java.util.Calendar
import kotlin.math.floor

@Composable
fun RoomDisplay(roomWithEvents: RoomWithEvents) {
    val lineColor = MaterialTheme.colorScheme.primary
    val lineEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    val occupiedColor = Color.Red

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val calculateXFromTime: (Int) -> Float = { elapsedSeconds ->
            // This calculation probably breaks on days with a DST transition ¯\_(ツ)_/¯
            val secondsInDay = 60 * 60 * 24

            val progress = elapsedSeconds.toFloat() / secondsInDay.toFloat()
            floor(progress * canvasWidth)
        }

        roomWithEvents.events.forEach { e ->
            val calendar = Calendar.getInstance()
            val startTime = e.start.time
            val endTime = e.end.time

            calendar.time = e.start
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val startElapsedSeconds = ((startTime - calendar.timeInMillis) / 1000).toInt()
            val endElapsedSeconds = ((endTime - calendar.timeInMillis) / 1000).toInt()
            val x1 = calculateXFromTime(startElapsedSeconds)
            val x2 = calculateXFromTime(endElapsedSeconds)

            drawRect(
                color = occupiedColor,
                topLeft = Offset(x1, 0f),
                size = Size(x2 - x1, canvasHeight),
            )
        }

        val now = LocalTime.now()
        val x = calculateXFromTime(now.toSecondOfDay())

        drawLine(
            color = lineColor,
            start = Offset(x, 0f),
            end = Offset(x, canvasHeight),
            pathEffect = lineEffect,
            strokeWidth = 20f
        )

    }
}