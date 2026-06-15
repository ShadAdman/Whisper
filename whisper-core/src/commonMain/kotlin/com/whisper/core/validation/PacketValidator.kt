package com.whisper.core.validation

import com.whisper.core.packet.WhisperPacket

interface PacketValidator {
    fun isValid(packet: WhisperPacket): Boolean
}
