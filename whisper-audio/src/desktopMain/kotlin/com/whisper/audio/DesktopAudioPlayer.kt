package com.whisper.audio

import com.whisper.core.model.AudioFrame
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

class DesktopAudioPlayer : AudioPlayer {
    private var line: SourceDataLine? = null

    override suspend fun play(frame: AudioFrame) {
        if (line == null) {
            val format = AudioFormat(frame.sampleRate.toFloat(), 16, frame.channels, true, false)
            val info = DataLine.Info(SourceDataLine::class.java, format)
            if (!AudioSystem.isLineSupported(info)) {
                return
            }
            line = AudioSystem.getLine(info) as SourceDataLine
            line?.open(format)
            line?.start()
        }

        val buffer = ByteArray(frame.samples.size * 2)
        val bb = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN)
        for (sample in frame.samples) {
            val s = (sample * 32767).toInt().coerceIn(-32768, 32767).toShort()
            bb.putShort(s)
        }
        
        line?.write(buffer, 0, buffer.size)
    }

    override suspend fun stop() {
        line?.stop()
        line?.flush()
        line?.close()
        line = null
    }
}

actual fun createAudioPlayer(): AudioPlayer = DesktopAudioPlayer()
