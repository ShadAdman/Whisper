package com.whisper.core.packet

interface PacketEncoder {
    fun encode(packet: WhisperPacket): ByteArray
}

class DefaultPacketEncoder(
    private val crcCalculator: CrcCalculator = Crc16Ccitt()
) : PacketEncoder {
    override fun encode(packet: WhisperPacket): ByteArray {
        val payloadSize = packet.payload.size
        if (payloadSize > 255) {
            throw IllegalArgumentException("Payload size too large: $payloadSize")
        }

        val result = ByteArray(PacketConstants.HEADER_SIZE + payloadSize + PacketConstants.CRC_SIZE)
        
        // Preamble
        PacketConstants.PREAMBLE.copyInto(result, 0)
        
        // Metadata (Version, Type, Length)
        result[4] = packet.version
        result[5] = packet.type
        result[6] = payloadSize.toByte()
        
        // Payload
        packet.payload.copyInto(result, 7)
        
        // Calculate CRC on Version, Type, Length, and Payload
        val dataToCrc = result.copyOfRange(4, PacketConstants.HEADER_SIZE + payloadSize)
        val crc = crcCalculator.calculate(dataToCrc)
        
        // Append CRC
        crc.copyInto(result, PacketConstants.HEADER_SIZE + payloadSize)
        
        return result
    }
}
