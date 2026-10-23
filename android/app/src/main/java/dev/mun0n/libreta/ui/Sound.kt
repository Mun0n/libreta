package dev.mun0n.libreta.ui

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.sin

/** A short quiet tick. Off unless Ajustes turns it on. Failures are ignored. */
class FoundSound {
    fun play() {
        thread(name = "libreta-tick", isDaemon = true) {
            var track: AudioTrack? = null
            try {
                val sampleRate = 22050
                val count = sampleRate / 20
                val buffer = ShortArray(count) { i ->
                    val env = 1.0 - i.toDouble() / count
                    (sin(2.0 * Math.PI * 660.0 * i / sampleRate) * env * 0.1 * Short.MAX_VALUE).toInt().toShort()
                }
                track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build(),
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build(),
                    )
                    .setBufferSizeInBytes(count * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()
                track.write(buffer, 0, count)
                track.play()
                Thread.sleep(90)
            } catch (_: Throwable) {
                // A missing audio device should not interrupt the sheet.
            } finally {
                try {
                    track?.release()
                } catch (_: Throwable) {
                }
            }
        }
    }
}
