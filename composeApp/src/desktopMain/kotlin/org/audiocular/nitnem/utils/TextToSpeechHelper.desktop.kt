package org.audiocular.nitnem.utils

import androidx.compose.runtime.*

class DesktopTextToSpeechHelper : TextToSpeechHelper {
    override fun speak(text: String) {
        println("TTS speaking: $text")
    }

    override fun stop() {
        println("TTS stopped")
    }

    override fun release() {
        // No-op
    }
}

@Composable
actual fun rememberTextToSpeechHelper(): TextToSpeechHelper {
    val helper = remember { DesktopTextToSpeechHelper() }
    return helper
}
