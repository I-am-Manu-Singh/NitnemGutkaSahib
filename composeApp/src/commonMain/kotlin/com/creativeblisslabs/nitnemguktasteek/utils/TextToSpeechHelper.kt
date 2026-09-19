package com.creativeblisslabs.nitnemguktasteek.utils

import androidx.compose.runtime.Composable

interface TextToSpeechHelper {
    fun speak(text: String)
    fun stop()
    fun release()
    fun setPitch(pitch: Float) {}
    fun setSpeechRate(rate: Float) {}
    fun setLanguage(languageCode: String) {}
}

@Composable
expect fun rememberTextToSpeechHelper(): TextToSpeechHelper