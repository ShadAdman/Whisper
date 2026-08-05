package com.whisper.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.whisper.core.model.AudioFrame
import com.whisper.core.util.WLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidAudioPlayer(private val context: Context) : AudioPlayer {
    private var audioTrack: AudioTrack? = null

    override suspend fun play(frame: AudioFrame) {
        withContext(Dispatchers.IO) {
            stop()
            
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)

            WLogger.d("AndroidAudioPlayer", "Starting playback: ${frame.samples.size} samples at ${frame.sampleRate}Hz, volume set to $maxVolume")
            WLogger.d("AndroidAudioPlayer", "Starting playback: ${frame.samples.size} samples at ${frame.sampleRate}Hz")

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
                var maxVal = 0f
                for (i in frame.samples.indices) {
                    val s = frame.samples[i]
                    if (kotlin.math.abs(s) > maxVal) maxVal = kotlin.math.abs(s)
                    shortSamples[i] = (s * 32767).toInt().toShort()
                }
                WLogger.i("AndroidAudioPlayer", "Playing frame with ${frame.samples.size} samples, max amplitude: $maxVal, sampleRate: ${frame.sampleRate}")
                
                track.play()
                val written = track.write(shortSamples, 0, shortSamples.size, AudioTrack.WRITE_BLOCKING)
                WLogger.i("AndroidAudioPlayer", "Written $written samples to AudioTrack")
                
                // Force volume to max for testing
                // Note: This requires MODIFY_AUDIO_SETTINGS which we have
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
