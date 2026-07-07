package com.whisper.core.packet

sealed interface PacketResult

data class ValidPacket(
    val packet: WhisperPacket
) : PacketResult

data object InvalidPacket : PacketResult
