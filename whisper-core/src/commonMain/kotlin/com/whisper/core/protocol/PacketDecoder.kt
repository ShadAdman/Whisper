package com.whisper.core.protocol

import com.whisper.core.packet.WhisperPacket

interface PacketDecoder {
    fun decode(data: ByteArray): WhisperPacket?
}
