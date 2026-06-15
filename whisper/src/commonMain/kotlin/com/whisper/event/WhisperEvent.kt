package com.whisper.event

import com.whisper.core.packet.WhisperPacket

sealed interface WhisperEvent

data class DeviceDetected(val deviceId: String) : WhisperEvent
data class PacketReceived(val packet: WhisperPacket) : WhisperEvent
data class ErrorOccurred(val message: String) : WhisperEvent
