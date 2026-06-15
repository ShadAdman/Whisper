package com.whisper.audio

import kotlinx.coroutines.*
import javax.sound.sampled.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DesktopAudioRecorder : AudioRecorder {
    private var listener: ((FloatArray) -> Unit)? = null
    private var line: TargetDataLine? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun setListener(listener: (FloatArray) -> Unit) {
        this.listener = listener
    }

    override suspend fun start() {
        if (line != null) return

        val format = AudioFormat(48000f, 16, 1, true, false)
        val info = DataLine.Info(TargetDataLine::class.java, format)
        
        if (!AudioSystem.isLineSupported(info)) {
            return
        }

        line = AudioSystem.getLine(info) as TargetDataLine
        line?.open(format)
        line?.start()

        recordingJob = scope.launch {
            val buffer = ByteArray(1024)
            while (isActive && line?.isOpen == true) {
                val read = line?.read(buffer, 0, buffer.size) ?: -1
                if (read > 0) {
                    val floats = FloatArray(read / 2)
                    val bb = ByteBuffer.wrap(buffer, 0, read).order(ByteOrder.LITTLE_ENDIAN)
                    for (i in floats.indices) {
                        floats[i] = bb.short.toFloat() / Short.MAX_VALUE
                    }
                    listener?.invoke(floats)
                }
            }
        }
    }

    override suspend fun stop() {
        recordingJob?.cancelAndJoin()
        recordingJob = null
        
        line?.stop()
        line?.close()
        line = null
    }
}
