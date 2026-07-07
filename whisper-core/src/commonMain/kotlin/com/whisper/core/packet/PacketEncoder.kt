package com.whisper.core.packet

interface PacketEncoder {
    fun encode(packet: WhisperPacket): ByteArray
}

class DefaultPacketEncoder : PacketEncoder {
    override fun encode(packet: WhisperPacket): ByteArray {
        val payloadSize = packet.payload.size
        if (payloadSize > 255) {
            throw IllegalArgumentException("Payload size too large: $payloadSize")
        }

        val result = ByteArray(PacketConstants.HEADER_SIZE + payloadSize + 1) // +1 for checksum
        
        // Preamble
        PacketConstants.PREAMBLE.copyInto(result, 0)
        
        // Metadata
        result[4] = packet.version
        result[5] = packet.type
        result[6] = payloadSize.toByte()
        
        // Payload
        packet.payload.copyInto(result, 7)
        
        // Checksum (Simple XOR for now)
        var checksum: Byte = 0
        for (i in 4 until (PacketConstants.HEADER_SIZE + payloadSize)) {
            checksum = (checksum.toInt() xor result[i].toInt()).toByte()
        }
        result[result.size - 1] = checksum
        
        return result
    }
}
