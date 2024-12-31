package com.example.dhbw_raumsuche.ui.rooms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.dhbw_raumsuche.data.local.entity.EventEntity
import java.time.ZoneId
import java.util.Date
import java.util.Locale

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
            .height(blockHeight + 3 * labelHeight + labelPadding)
            .padding(horizontal = 16.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                color = backgroundColor,
                size = Size(size.width, size.height - 2 * labelHeight.toPx() - labelPadding.toPx())
            )

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
                    size = Size(
                        endX - startX,
                        size.height - 2 * labelHeight.toPx() - labelPadding.toPx()
                    )
                )

                val startY = -labelHeight.toPx() / 2
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        event.start.toTimeLabel(),
                        startX,
                        startY,
                        android.graphics.Paint().apply {
                            color = labelColor.toArgb()
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }

                val endY =
                    size.height - 2 * labelHeight.toPx() + labelHeight.toPx() / 2
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        event.end.toTimeLabel(),
                        endX,
                        endY,
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