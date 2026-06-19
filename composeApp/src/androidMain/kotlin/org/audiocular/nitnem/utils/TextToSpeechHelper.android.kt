package org.audiocular.nitnem.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

class AndroidTextToSpeechHelper(
    private val context: Context
) : TextToSpeechHelper, TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null
    
    private var targetLocale: Locale = Locale.US
    private var targetPitch: Float = 1.0f
    private var targetSpeechRate: Float = 1.0f

    init {
        // Initialize the service asynchronously
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            
            // FIX: Safely apply any configurations that were queued up before initialization finished
            setLanguageAndVerify(targetLocale)
            tts?.setPitch(targetPitch)
            tts?.setSpeechRate(targetSpeechRate)

            pendingText?.let {
                speak(it)
                pendingText = null
            }
        } else {
            println("TTS Engine failed to initialize completely.")
        }
    }

    override fun speak(text: String) {
        if (text.isBlank()) return
        
        if (isInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "BaniAssistantUtterance")
        } else {
            pendingText = text
        }
    }

    override fun setPitch(pitch: Float) {
        targetPitch = pitch
        if (isInitialized) {
            tts?.setPitch(pitch)
        }
    }

    override fun setSpeechRate(rate: Float) {
        targetSpeechRate = rate
        if (isInitialized) {
            tts?.setSpeechRate(rate)
        }
    }

    override fun setLanguage(languageCode: String) {
        targetLocale = when (languageCode.lowercase().trim()) {
            "hi" -> Locale("hi", "IN")
            "pa" -> Locale("pa", "IN")
            else -> Locale.US
        }
        
        if (isInitialized) {
            setLanguageAndVerify(targetLocale)
        }
    }

    // FIX: Extracted safety block to handle missing native language data packs gracefully
    private fun setLanguageAndVerify(locale: Locale) {
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            println("Target Locale $locale missing or unsupported. Falling back safely.")
            tts?.setLanguage(Locale.US)
        } else {
            println("TTS Engine language successfully matched to: $locale")
        }
    }

    override fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
    }

    override fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            println("Error releasing TTS engine: ${e.message}")
        } finally {
            tts = null
            isInitialized = false
        }
    }
}

@Composable
actual fun rememberTextToSpeechHelper(): TextToSpeechHelper {
    val context = LocalContext.current
    val helper = remember { AndroidTextToSpeechHelper(context) }
    
    DisposableEffect(helper) {
        onDispose {
            helper.release()
        }
    }
    return helper
}