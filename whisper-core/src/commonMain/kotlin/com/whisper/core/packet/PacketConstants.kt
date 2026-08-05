package com.whisper.core.packet

object PacketConstants {
    val PREAMBLE = byteArrayOf(0xAA.toByte(), 0xAA.toByte(), 0xAA.toByte(), 0xAA.toByte())
    const val PREAMBLE_SIZE = 4
    const val METADATA_SIZE = 3 // VERSION(1) + TYPE(1) + LENGTH(1)
    const val HEADER_SIZE = PREAMBLE_SIZE + METADATA_SIZE
    const val CRC_SIZE = 2 // CRC-16
}
