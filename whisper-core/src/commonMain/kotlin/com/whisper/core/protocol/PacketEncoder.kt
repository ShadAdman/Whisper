package com.whisper.core.protocol

import com.whisper.core.packet.WhisperPacket

interface PacketEncoder {
    fun encode(packet: WhisperPacket): ByteArray
}
