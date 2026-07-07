package com.whisper.core.packet

class PacketSynchronizer(
    private val decoder: PacketDecoder = DefaultPacketDecoder()
) {
    enum class State {
        SEARCHING,
        READING_METADATA,
        READING_PAYLOAD,
        PACKET_COMPLETE
    }

    private var state = State.SEARCHING
    private val buffer = mutableListOf<Byte>()
    private var expectedLength = 0

    fun processByte(byte: Byte): WhisperPacket? {
        buffer.add(byte)

        return when (state) {
            State.SEARCHING -> {
                if (buffer.size >= 4) {
                    if (isPreambleFound()) {
                        state = State.READING_METADATA
                    } else {
                        buffer.removeAt(0)
                    }
                }
                null
            }
            State.READING_METADATA -> {
                if (buffer.size >= PacketConstants.HEADER_SIZE) {
                    expectedLength = buffer[6].toInt() and 0xFF
                    state = State.READING_PAYLOAD
                }
                null
            }
            State.READING_PAYLOAD -> {
                if (buffer.size >= PacketConstants.HEADER_SIZE + expectedLength + 1) {
                    val packet = decoder.decode(buffer.toByteArray())
                    reset()
                    packet
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
        expectedLength = 0
    }
}
