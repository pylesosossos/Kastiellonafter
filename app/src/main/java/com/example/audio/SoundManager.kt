package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isEnabled: Boolean = true

    enum class SoundEffect {
        TAP,
        CRIT,
        UPGRADE,
        BOSS_HIT,
        BOSS_VICTORY,
        PRESTIGE
    }

    private val sampleRate = 22050
    private val audioBuffers = mutableMapOf<SoundEffect, ShortArray>()

    init {
        scope.launch {
            audioBuffers[SoundEffect.TAP] = generateTone(480.0, 0.05, 0.25)
            audioBuffers[SoundEffect.CRIT] = generateTone(220.0, 0.12, 0.45, isCrit = true)
            audioBuffers[SoundEffect.UPGRADE] = generateChime(listOf(523.25, 659.25, 783.99), 0.18, 0.35)
            audioBuffers[SoundEffect.BOSS_HIT] = generateTone(130.0, 0.09, 0.4)
            audioBuffers[SoundEffect.BOSS_VICTORY] = generateChime(listOf(440.0, 554.37, 659.25, 880.0), 0.35, 0.5)
            audioBuffers[SoundEffect.PRESTIGE] = generateChime(listOf(293.66, 369.99, 440.0, 587.33), 0.5, 0.6)
        }
    }

    fun play(effect: SoundEffect) {
        if (!isEnabled) return
        scope.launch {
            val buffer = audioBuffers[effect] ?: return@launch
            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                // Auto-release after playing
                scope.launch {
                    kotlinx.coroutines.delay((buffer.size * 1000L / sampleRate) + 50)
                    try {
                        track.stop()
                        track.release()
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {
                // Audio synthesis gracefully ignores device sound allocation issues
            }
        }
    }

    private fun generateTone(freq: Double, durationSec: Double, volume: Double, isCrit: Boolean = false): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        val angularFreq = 2.0 * PI * freq / sampleRate
        val decayRate = 5.0 / durationSec

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-decayRate * t) * volume
            val wave = if (isCrit) {
                // Distorted cosmic crunch
                (sin(angularFreq * i) * 0.7 + sin(angularFreq * 0.5 * i) * 0.3).coerceIn(-1.0, 1.0)
            } else {
                sin(angularFreq * i)
            }
            buffer[i] = (wave * envelope * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateChime(frequencies: List<Double>, durationSec: Double, volume: Double): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(totalSamples)
        val decayRate = 3.5 / durationSec

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-decayRate * t) * (volume / frequencies.size)
            var sample = 0.0
            for (freq in frequencies) {
                sample += sin(2.0 * PI * freq * i / sampleRate)
            }
            buffer[i] = (sample * envelope * Short.MAX_VALUE).coerceIn(-32768.0, 32767.0).toInt().toShort()
        }
        return buffer
    }
}
