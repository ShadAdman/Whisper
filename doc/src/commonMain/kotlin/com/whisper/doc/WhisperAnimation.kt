package com.whisper.doc

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WhisperAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "WhisperAnimation")
    
    val waveProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveProgress"
    )

    val dataProgress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DataProgress"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val phoneWidth = 80.dp.toPx()
        val phoneHeight = 150.dp.toPx()
        val phonePadding = 60.dp.toPx()

        val phone1X = phonePadding
        val phone2X = width - phonePadding - phoneWidth
        val centerY = height / 2

        // Draw Phone 1
        drawRoundRect(
            color = Color.Gray,
            topLeft = Offset(phone1X, centerY - phoneHeight / 2),
            size = Size(phoneWidth, phoneHeight),
            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Phone 1 screen
        val phone1ScreenColor = if (dataProgress.value < 0.1f) Color.Cyan else Color.Cyan.copy(alpha = 0.3f)
        drawRoundRect(
            color = phone1ScreenColor,
            topLeft = Offset(phone1X + 8.dp.toPx(), centerY - phoneHeight / 2 + 12.dp.toPx()),
            size = Size(phoneWidth - 16.dp.toPx(), phoneHeight - 32.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )

        // Draw Phone 2
        drawRoundRect(
            color = Color.Gray,
            topLeft = Offset(phone2X, centerY - phoneHeight / 2),
            size = Size(phoneWidth, phoneHeight),
            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Phone 2 screen
        val phone2ScreenColor = if (dataProgress.value > 0.9f) Color.Green else Color.Green.copy(alpha = 0.3f)
        drawRoundRect(
            color = phone2ScreenColor,
            topLeft = Offset(phone2X + 8.dp.toPx(), centerY - phoneHeight / 2 + 12.dp.toPx()),
            size = Size(phoneWidth - 16.dp.toPx(), phoneHeight - 32.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
        )

        // Nearby device indicator (small pulsing dot above Phone 2)
        val pulse = (sin(waveProgress.value * PI.toFloat() * 2) + 1f) / 2f
        drawCircle(
            color = Color.Green.copy(alpha = pulse),
            radius = 6.dp.toPx(),
            center = Offset(phone2X + phoneWidth / 2, centerY - phoneHeight / 2 - 20.dp.toPx())
        )

        // Draw Waves
        val waveSourceX = phone1X + phoneWidth
        val waveDestX = phone2X
        val waveDistance = waveDestX - waveSourceX
        
        val numberOfWaves = 5
        for (i in 0 until numberOfWaves) {
            val progress = (waveProgress.value + i.toFloat() / numberOfWaves) % 1f
            val x = waveSourceX + progress * waveDistance
            val alpha = 1f - progress
            
            drawArc(
                color = Color(0xFF64B5F6).copy(alpha = alpha * 0.6f),
                startAngle = -60f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(x - 30.dp.toPx(), centerY - 60.dp.toPx()),
                size = Size(60.dp.toPx(), 120.dp.toPx()),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Draw Data Packets
        val packetX = waveSourceX + dataProgress.value * waveDistance
        val packetY = centerY + sin(dataProgress.value * PI.toFloat() * 2) * 15.dp.toPx()
        
        drawCircle(
            color = Color(0xFFFF4081),
            radius = 6.dp.toPx(),
            center = Offset(packetX, packetY)
        )
    }
}
