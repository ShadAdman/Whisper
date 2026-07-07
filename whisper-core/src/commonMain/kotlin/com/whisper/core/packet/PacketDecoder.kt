package com.whisper.core.packet

interface PacketDecoder {
    fun decode(data: ByteArray): PacketResult
}

class DefaultPacketDecoder(
    private val crcCalculator: CrcCalculator = Crc16Ccitt()
) : PacketDecoder {
    override fun decode(data: ByteArray): PacketResult {
        if (data.size < PacketConstants.HEADER_SIZE + PacketConstants.CRC_SIZE) return InvalidPacket

        // Validate Preamble
        for (i in 0 until 4) {
            if (data[i] != PacketConstants.PREAMBLE[i]) return InvalidPacket
        }

        val version = data[4]
        val type = data[5]
        val length = data[6].toInt() and 0xFF

        if (data.size < PacketConstants.HEADER_SIZE + length + PacketConstants.CRC_SIZE) return InvalidPacket

        // Extract Payload
        val payload = ByteArray(length)
        data.copyInto(payload, 0, PacketConstants.HEADER_SIZE, PacketConstants.HEADER_SIZE + length)

        // Validate CRC
        val dataToCrc = data.copyOfRange(4, PacketConstants.HEADER_SIZE + length)
        val calculatedCrc = crcCalculator.calculate(dataToCrc)
        
        val receivedCrc = data.copyOfRange(PacketConstants.HEADER_SIZE + length, PacketConstants.HEADER_SIZE + length + PacketConstants.CRC_SIZE)

        if (!calculatedCrc.contentEquals(receivedCrc)) {
            println("CRC mismatch")
            return InvalidPacket
        }

        return ValidPacket(WhisperPacket(version, type, payload, timestamp = 0L))
    }
}
