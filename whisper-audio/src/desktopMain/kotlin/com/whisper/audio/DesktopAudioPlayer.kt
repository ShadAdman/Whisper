package com.whisper.audio

import com.whisper.core.model.AudioFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.sound.sampled.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DesktopAudioPlayer : AudioPlayer {
    private var line: SourceDataLine? = null

    override suspend fun play(frame: AudioFrame) {
        withContext(Dispatchers.IO) {
            stop()

            val format = AudioFormat(frame.sampleRate.toFloat(), 16, frame.channels, true, false)
            val info = DataLine.Info(SourceDataLine::class.java, format)

            if (!AudioSystem.isLineSupported(info)) return@withContext

            line = AudioSystem.getLine(info) as SourceDataLine
            line?.open(format)
            line?.start()

            val buffer = ByteBuffer.allocate(frame.samples.size * 2).order(ByteOrder.LITTLE_ENDIAN)
            for (sample in frame.samples) {
                val s = (sample.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort()
                buffer.putShort(s)
            }

            line?.write(buffer.array(), 0, buffer.position())
            line?.drain()
        }
    }

    override suspend fun stop() {
        line?.stop()
        line?.close()
        line = null
    }
}
