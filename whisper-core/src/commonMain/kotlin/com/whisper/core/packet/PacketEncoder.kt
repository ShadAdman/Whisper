package com.whisper.core.packet

import com.whisper.core.error.FecConfig
import com.whisper.core.error.FecEncoder
import com.whisper.core.error.RepetitionFecEncoder

interface PacketEncoder {
    fun encode(packet: WhisperPacket, fecConfig: FecConfig = FecConfig(enabled = false)): ByteArray
}

class DefaultPacketEncoder(
    private val crcCalculator: CrcCalculator = Crc16Ccitt()
) : PacketEncoder {
    override fun encode(packet: WhisperPacket, fecConfig: FecConfig): ByteArray {
        val payloadSize = packet.payload.size
        if (payloadSize > 255) {
            throw IllegalArgumentException("Payload size too large: $payloadSize")
        }

        // 1. Build raw packet (Metadata + Payload)
        val rawData = ByteArray(PacketConstants.METADATA_SIZE + payloadSize)
        rawData[0] = packet.version
        rawData[1] = packet.type
        rawData[2] = payloadSize.toByte()
        packet.payload.copyInto(rawData, 3)

        // 2. Calculate and append CRC
        val crc = crcCalculator.calculate(rawData)
        val dataWithCrc = ByteArray(rawData.size + PacketConstants.CRC_SIZE)
        rawData.copyInto(dataWithCrc, 0)
        crc.copyInto(dataWithCrc, rawData.size)

        // 3. Apply FEC if enabled
        val finalData = if (fecConfig.enabled) {
            val encoder = RepetitionFecEncoder(fecConfig.redundancy)
            encoder.encode(dataWithCrc)
        } else {
            dataWithCrc
        }

        // 4. Attach Preamble (Preamble is NOT FEC encoded to allow easy sync)
        val result = ByteArray(PacketConstants.PREAMBLE_SIZE + finalData.size)
        PacketConstants.PREAMBLE.copyInto(result, 0)
        finalData.copyInto(result, PacketConstants.PREAMBLE_SIZE)
        
        return result
    }
}
