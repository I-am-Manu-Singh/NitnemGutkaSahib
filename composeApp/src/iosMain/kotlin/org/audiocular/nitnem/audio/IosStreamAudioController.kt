package org.audiocular.nitnem.app.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.audiocular.nitnem.audio.StreamAudioController
import org.audiocular.nitnem.models.PlaybackSnapshot

private class IosStreamAudioController : StreamAudioController {
    private val _playback = MutableStateFlow(
        PlaybackSnapshot(error = "iOS shared UI compiles; AVPlayer actual playback comes next.")
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
            error = "iOS needs an AVPlayer-backed actual controller."
        )
    }

    override fun seekTo(positionMs: Long) {
        _playback.value = _playback.value.copy(positionMs = positionMs)
    }

    override fun release() = Unit
}

@Composable
actual fun rememberStreamAudioController(): StreamAudioController =
    remember { IosStreamAudioController() }
