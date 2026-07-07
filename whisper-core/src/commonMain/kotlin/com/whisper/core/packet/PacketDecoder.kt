package com.whisper.core.packet

interface PacketDecoder {
    fun decode(data: ByteArray): WhisperPacket?
}

class DefaultPacketDecoder : PacketDecoder {
    override fun decode(data: ByteArray): WhisperPacket? {
        if (data.size < PacketConstants.HEADER_SIZE + 1) return null

        // Validate Preamble (assuming it's already handled by synchronizer, but let's be safe)
        for (i in 0 until 4) {
            if (data[i] != PacketConstants.PREAMBLE[i]) return null
        }

        val version = data[4]
        val type = data[5]
        val length = data[6].toInt() and 0xFF

        if (data.size < PacketConstants.HEADER_SIZE + length + 1) return null

        val payload = ByteArray(length)
        data.copyInto(payload, 0, PacketConstants.HEADER_SIZE, PacketConstants.HEADER_SIZE + length)

        // Validate Checksum
        var checksum: Byte = 0
        for (i in 4 until (PacketConstants.HEADER_SIZE + length)) {
            checksum = (checksum.toInt() xor data[i].toInt()).toByte()
        }
        val receivedChecksum = data[PacketConstants.HEADER_SIZE + length]

        if (checksum != receivedChecksum) {
            println("Checksum mismatch: expected $checksum, got $receivedChecksum")
            return null
        }

        return WhisperPacket(version, type, payload, timestamp = 0L)
    }
}
