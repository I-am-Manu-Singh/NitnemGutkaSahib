package com.creativeblisslabs.nitnemguktasteek.utils

import androidx.compose.runtime.*
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechBoundaryImmediate

class IosTextToSpeechHelper : TextToSpeechHelper {
    private val synthesizer = AVSpeechSynthesizer()
    private var languageCodeStr = "en-US"

    override fun setLanguage(languageCode: String) {
        languageCodeStr = when (languageCode.lowercase()) {
            "hi" -> "hi-IN"
            "pa" -> "pa-IN"
            else -> "en-US"
        }
    }

    override fun speak(text: String) {
        val utterance = AVSpeechUtterance(string = text)
        utterance.voice = AVSpeechSynthesisVoice.voiceWithLanguage(languageCodeStr)
        synthesizer.speakUtterance(utterance)
    }

    override fun stop() {
        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundaryImmediate)
    }

    override fun release() {
        // AVSpeechSynthesizer cleanup is automatic
    }
}

@Composable
actual fun rememberTextToSpeechHelper(): TextToSpeechHelper {
    val helper = remember { IosTextToSpeechHelper() }
    DisposableEffect(helper) {
        onDispose {
            helper.stop()
        }
    }
    return helper
}
