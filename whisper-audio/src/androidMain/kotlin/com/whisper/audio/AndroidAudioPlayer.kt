package com.whisper.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.whisper.core.model.AudioFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidAudioPlayer : AudioPlayer {
    private var audioTrack: AudioTrack? = null

    override suspend fun play(frame: AudioFrame) {
        withContext(Dispatchers.IO) {
            stop()

            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufferSize = AudioTrack.getMinBufferSize(
                frame.sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                audioFormat
            )
            
            val actualBufferSize = maxOf(minBufferSize, frame.samples.size * 2)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(audioFormat)
                        .setSampleRate(frame.sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(actualBufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            if (audioTrack?.state != AudioTrack.STATE_INITIALIZED) {
                println("Error: AudioTrack initialization failed")
                audioTrack = null
                return@withContext
            }

            audioTrack?.let { track ->
                val shortSamples = ShortArray(frame.samples.size)
                for (i in frame.samples.indices) {
                    shortSamples[i] = (frame.samples[i] * 32767).toInt().toShort()
                }
                track.play()
                track.write(shortSamples, 0, shortSamples.size, AudioTrack.WRITE_BLOCKING)
            }
        }
    }

    override suspend fun stop() {
        audioTrack?.let { track ->
            if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                track.stop()
            }
            track.release()
        }
        audioTrack = null
    }
}
