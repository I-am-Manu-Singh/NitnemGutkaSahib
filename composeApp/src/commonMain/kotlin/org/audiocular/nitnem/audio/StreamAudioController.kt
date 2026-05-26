package org.audiocular.nitnem.app.audio

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow
import org.audiocular.nitnem.models.AudioTrack
import org.audiocular.nitnem.models.PlaybackSnapshot

interface StreamAudioController {


    val playback:
        StateFlow<PlaybackSnapshot>

    /*
     * PREPARE
     */

    fun prepare(
        url: String,
        title: String = "Nitnem",
        initialPositionMs: Long = 0L
    )

    /*
     * PLAY
     */

    fun play(
        url: String,
        title: String = "Nitnem"
    )

    fun pause()

    fun resume()

    fun playPause()

    /*
     * SEEK
     */
    
    fun seekTo(
        positionMs: Long
    )

    fun seekForward(
        millis: Long = 10_000L
    ) {

        val next =

            playback.value.positionMs + millis

        seekTo(

            next.coerceAtMost(
                playback.value.durationMs
            )
        )
    }

    fun seekBack(
        millis: Long = 10_000L
    ) {

        val next =

            playback.value.positionMs - millis

        seekTo(
            next.coerceAtLeast(0L)
        )
    }

    /*
     * AUDIO
     */

    fun setVolume(
        volume: Float
    ) {}

    fun updatePlaybackSpeed(
        speed: Float
    ) {}

    /*
     * REPEAT
     */

    fun repeatLine(
        startMs: Long
    ) {

        seekTo(startMs)
    }

    /*
     * BACKGROUND
     */

    fun startBackgroundPlayback()

    fun stopBackgroundPlayback()

    /*
     * STATE
     */

    fun savePlaybackState()

    fun restorePlaybackState()

    /*
     * STOP
     */

    fun stop()

    fun release()

    /*
     * DOWNLOADS
     */

    suspend fun downloadAudio(

        baniId: String,

        remoteUrl: String
    ): Boolean

    fun isAudioDownloaded(
        baniId: String
    ): Boolean

    fun getLocalAudioPath(
        baniId: String
    ): String?

    fun deleteAudio(
        baniId: String
    )

    fun getCustomTracks(): List<AudioTrack> = emptyList()
    fun getCustomTracks(baniId: String): List<AudioTrack> = emptyList()
    fun importCustomTrack(baniId: String, artist: String, fileUri: String): AudioTrack? = null
    fun deleteCustomTrack(trackId: String): Boolean = false
    fun getActiveTrackId(baniId: String): String = "default"
    fun setActiveTrackId(baniId: String, trackId: String) {}
    fun getActiveAudioUrl(baniId: String): String? = null
}

@Composable
expect fun rememberStreamAudioController():StreamAudioController