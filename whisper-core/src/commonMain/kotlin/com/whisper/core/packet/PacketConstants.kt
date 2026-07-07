package com.whisper.core.packet

object PacketConstants {
    val PREAMBLE = byteArrayOf(0xAA.toByte(), 0xAA.toByte(), 0xAA.toByte(), 0xAA.toByte())
    const val HEADER_SIZE = 7 // PREAMBLE(4) + VERSION(1) + TYPE(1) + LENGTH(1)
}
