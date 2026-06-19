package org.audiocular.nitnem.app.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.audiocular.nitnem.audio.StreamAudioController
import org.audiocular.nitnem.models.PlaybackSnapshot

private class DesktopStreamAudioController : StreamAudioController {
    private val _playback = MutableStateFlow(
        PlaybackSnapshot(error = "Desktop streaming playback is not wired yet. Android uses Media3.")
    )

    override val playback: StateFlow<PlaybackSnapshot> = _playback

    override fun prepare(url: String, title: String, initialPositionMs: Long) {
        _playback.value = _playback.value.copy(
            isReady = url.isNotBlank(),
            positionMs = initialPositionMs
        )
    }

    override fun playPause() {
        _playback.value = _playback.value.copy(
            isPlaying = !_playback.value.isPlaying,
            error = "Desktop compile target is current; native playback comes next."
        )
    }

    override fun seekTo(positionMs: Long) {
        _playback.value = _playback.value.copy(positionMs = positionMs)
    }

    override fun release() = Unit

    override suspend fun downloadAudio(baniId: String, remoteUrl: String, trackId: String?): Boolean = false

    override fun isAudioDownloaded(baniId: String, trackId: String?): Boolean = false

    override fun getLocalAudioPath(baniId: String, trackId: String?): String? = null

    override fun deleteAudio(baniId: String, trackId: String?) = Unit
}

@Composable
actual fun rememberStreamAudioController(): StreamAudioController =
    remember { DesktopStreamAudioController() }
