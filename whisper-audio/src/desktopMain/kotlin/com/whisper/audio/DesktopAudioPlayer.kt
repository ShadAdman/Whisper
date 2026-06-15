package com.whisper.audio

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.sound.sampled.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DesktopAudioPlayer : AudioPlayer {
    private var line: SourceDataLine? = null

    override suspend fun play(samples: FloatArray) = withContext(Dispatchers.IO) {
        stop()

        val format = AudioFormat(48000f, 16, 1, true, false)
        val info = DataLine.Info(SourceDataLine::class.java, format)

        if (!AudioSystem.isLineSupported(info)) return@withContext

        line = AudioSystem.getLine(info) as SourceDataLine
        line?.open(format)
        line?.start()

        val buffer = ByteBuffer.allocate(samples.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        for (sample in samples) {
            val s = (sample.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort()
            buffer.putShort(s)
        }

        line?.write(buffer.array(), 0, buffer.position())
        line?.drain()
    }

    override suspend fun stop() {
        line?.stop()
        line?.close()
        line = null
    }
}
