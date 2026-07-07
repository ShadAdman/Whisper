package com.whisper.core.model

data class FrequencyDetection(
    val frequency: Float,
    val magnitude: Float,
    val timestamp: Long = 0L
)
