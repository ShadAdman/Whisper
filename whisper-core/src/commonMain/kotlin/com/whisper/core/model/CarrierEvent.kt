package com.whisper.core.model

sealed interface CarrierEvent

data class CarrierDetected(
    val detection: FrequencyDetection
) : CarrierEvent

data object CarrierLost : CarrierEvent
