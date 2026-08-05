package com.whisper.core.packet

import com.whisper.core.error.FecConfig
import com.whisper.core.error.FecDecoder
import com.whisper.core.error.RepetitionFecDecoder

interface PacketDecoder {
    fun decode(data: ByteArray, fecConfig: FecConfig = FecConfig(enabled = false)): PacketResult
}

class DefaultPacketDecoder(
    private val crcCalculator: CrcCalculator = Crc16Ccitt()
) : PacketDecoder {
    override fun decode(data: ByteArray, fecConfig: FecConfig): PacketResult {
        // data contains [PREAMBLE (skipped by synchronizer usually, but here we expect it at start if passed)]
        // or data is just the payload part.
        // Actually, PacketSynchronizer passes the entire buffer starting with Preamble.

        if (data.size < PacketConstants.PREAMBLE_SIZE) return InvalidPacket

        // 1. Skip Preamble
        val protectedData = data.copyOfRange(PacketConstants.PREAMBLE_SIZE, data.size)

        // 2. Decode FEC if enabled
        val decodedData = if (fecConfig.enabled) {
            val decoder = RepetitionFecDecoder(fecConfig.redundancy)
            decoder.decode(protectedData) ?: return InvalidPacket
        } else {
            protectedData
        }

        if (decodedData.size < PacketConstants.METADATA_SIZE + PacketConstants.CRC_SIZE) return InvalidPacket

        // 3. Extract Metadata and Payload
        val version = decodedData[0]
        val type = decodedData[1]
        val length = decodedData[2].toInt() and 0xFF

        if (decodedData.size < PacketConstants.METADATA_SIZE + length + PacketConstants.CRC_SIZE) return InvalidPacket

        val payload = decodedData.copyOfRange(PacketConstants.METADATA_SIZE, PacketConstants.METADATA_SIZE + length)
        val receivedCrc = decodedData.copyOfRange(PacketConstants.METADATA_SIZE + length, PacketConstants.METADATA_SIZE + length + PacketConstants.CRC_SIZE)

        // 4. Validate CRC
        val dataToCrc = decodedData.copyOfRange(0, PacketConstants.METADATA_SIZE + length)
        val calculatedCrc = crcCalculator.calculate(dataToCrc)

        if (!calculatedCrc.contentEquals(receivedCrc)) {
            return InvalidPacket
        }

        return ValidPacket(WhisperPacket(version, type, payload, timestamp = 0L))
    }
}
