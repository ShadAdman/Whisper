package com.whisper.audio

import com.whisper.core.model.AudioFrame
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

class DesktopAudioRecorder : AudioRecorder {
    private val _samples = MutableSharedFlow<AudioFrame>(extraBufferCapacity = 64)
    override val samples: SharedFlow<AudioFrame> = _samples.asSharedFlow()
    
    private var job: Job? = null
    private var line: TargetDataLine? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun start() {
        if (job != null) return
        
        val format = AudioFormat(48000f, 16, 1, true, false)
        val info = DataLine.Info(TargetDataLine::class.java, format)
        
        if (!AudioSystem.isLineSupported(info)) {
            throw IllegalStateException("Line not supported")
        }
        
        line = AudioSystem.getLine(info) as TargetDataLine
        line?.open(format)
        line?.start()
        
        job = scope.launch {
            val buffer = ByteArray(2048) // 1024 samples
            val floatBuffer = FloatArray(1024)
            while (isActive) {
                val read = line?.read(buffer, 0, buffer.size) ?: -1
                if (read > 0) {
                    val bb = ByteBuffer.wrap(buffer, 0, read).order(ByteOrder.LITTLE_ENDIAN)
                    for (i in 0 until read / 2) {
                        floatBuffer[i] = bb.short / 32768f
                    }
                    val frame = AudioFrame(
                        samples = floatBuffer.copyOf(read / 2),
                        sampleRate = 48000,
                        channels = 1,
                        timestamp = System.currentTimeMillis()
                    )
                    _samples.emit(frame)
                }
            }
        }
    }

    override suspend fun stop() {
        job?.cancelAndJoin()
        job = null
        line?.stop()
        line?.close()
        line = null
    }
}

actual fun createAudioRecorder(): AudioRecorder = DesktopAudioRecorder()
