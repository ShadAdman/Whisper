package com.whisper.core.model

sealed interface DetectionEvent

data class FrequencyDetected(
    val detection: FrequencyDetection
) : DetectionEvent

data object SignalLost : DetectionEvent
