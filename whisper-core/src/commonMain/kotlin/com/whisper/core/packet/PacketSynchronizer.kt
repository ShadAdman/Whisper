package com.whisper.core.packet

import com.whisper.core.error.FecConfig
import com.whisper.core.error.RepetitionFecDecoder

class PacketSynchronizer(
    private val decoder: PacketDecoder = DefaultPacketDecoder(),
    private val fecConfig: FecConfig = FecConfig(enabled = false)
) {
    enum class State {
        SEARCHING,
        READING_HEADER,
        READING_BODY,
        PACKET_COMPLETE
    }

    private var state = State.SEARCHING
    private val buffer = mutableListOf<Byte>()
    private var expectedPayloadLength = 0

    fun processByte(byte: Byte): PacketResult? {
        buffer.add(byte)

        return when (state) {
            State.SEARCHING -> {
                if (buffer.size >= PacketConstants.PREAMBLE_SIZE) {
                    if (isPreambleFound()) {
                        state = State.READING_HEADER
                    } else {
                        buffer.removeAt(0)
                    }
                }
                null
            }
            State.READING_HEADER -> {
                val redundancy = if (fecConfig.enabled) fecConfig.redundancy else 1
                val headerBytesNeeded = PacketConstants.PREAMBLE_SIZE + (PacketConstants.METADATA_SIZE * redundancy)
                
                if (buffer.size >= headerBytesNeeded) {
                    // Decode header to find payload length
                    val encodedHeader = buffer.subList(PacketConstants.PREAMBLE_SIZE, headerBytesNeeded).toByteArray()
                    val decodedHeader = if (fecConfig.enabled) {
                        RepetitionFecDecoder(fecConfig.redundancy).decode(encodedHeader)
                    } else {
                        encodedHeader
                    }

                    if (decodedHeader != null && decodedHeader.size >= 3) {
                        expectedPayloadLength = decodedHeader[2].toInt() and 0xFF
                        state = State.READING_BODY
                    } else {
                        // Header corrupted beyond FEC repair
                        reset()
                    }
                }
                null
            }
            State.READING_BODY -> {
                val redundancy = if (fecConfig.enabled) fecConfig.redundancy else 1
                val totalBytesNeeded = PacketConstants.PREAMBLE_SIZE + 
                        ((PacketConstants.METADATA_SIZE + expectedPayloadLength + PacketConstants.CRC_SIZE) * redundancy)
                
                if (buffer.size >= totalBytesNeeded) {
                    val result = decoder.decode(buffer.toByteArray(), fecConfig)
                    reset()
                    result
                } else {
                    null
                }
            }
            State.PACKET_COMPLETE -> {
                reset()
                null
            }
        }
    }

    private fun isPreambleFound(): Boolean {
        if (buffer.size < 4) return false
        for (i in 0 until 4) {
            if (buffer[i] != PacketConstants.PREAMBLE[i]) return false
        }
        return true
    }

    fun reset() {
        state = State.SEARCHING
        buffer.clear()
        expectedPayloadLength = 0
    }
}
