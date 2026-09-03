package org.audiocular.nitnem.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

enum class TextLanguage {
    PUNJABI, HINDI, ENGLISH
}

data class SpeechSegment(
    val text: String,
    val language: TextLanguage
)

class AndroidTextToSpeechHelper(
    private val context: Context
) : TextToSpeechHelper, TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null
    
    private var targetLocale: Locale = Locale.US
    private var targetPitch: Float = 1.0f
    private var targetSpeechRate: Float = 1.0f

    private val speechQueue = mutableListOf<SpeechSegment>()
    private var currentQueueIndex = 0

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    playNextSegment()
                }
                override fun onError(utteranceId: String?) {
                    playNextSegment()
                }
            })

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

    private fun splitIntoSegments(text: String): List<SpeechSegment> {
        if (text.isBlank()) return emptyList()
        
        val list = mutableListOf<SpeechSegment>()
        val currentSegment = StringBuilder()
        var currentLang: TextLanguage? = null
        
        fun getLang(ch: Char): TextLanguage {
            return when {
                ch in '\u0A00'..'\u0A7F' -> TextLanguage.PUNJABI
                ch in '\u0900'..'\u097F' -> TextLanguage.HINDI
                else -> TextLanguage.ENGLISH
            }
        }
        
        for (ch in text) {
            val lang = getLang(ch)
            val resolvedLang = if (ch.isWhitespace() || ch.isLetterOrDigit().not()) {
                currentLang ?: lang
            } else {
                lang
            }
            
            if (currentLang == null) {
                currentLang = resolvedLang
                currentSegment.append(ch)
            } else if (resolvedLang == currentLang) {
                currentSegment.append(ch)
            } else {
                val segmentText = currentSegment.toString().trim()
                if (segmentText.isNotEmpty()) {
                    list.add(SpeechSegment(segmentText, currentLang))
                }
                currentSegment.setLength(0)
                currentLang = resolvedLang
                currentSegment.append(ch)
            }
        }
        
        val segmentText = currentSegment.toString().trim()
        if (segmentText.isNotEmpty() && currentLang != null) {
            list.add(SpeechSegment(segmentText, currentLang))
        }
        
        return list
    }

    private fun playNextSegment() {
        val nextSegment: SpeechSegment?
        val index: Int
        
        synchronized(speechQueue) {
            if (currentQueueIndex < speechQueue.size) {
                nextSegment = speechQueue[currentQueueIndex]
                currentQueueIndex++
                index = currentQueueIndex
            } else {
                nextSegment = null
                index = -1
            }
        }

        if (nextSegment != null) {
            val locale = when (nextSegment.language) {
                TextLanguage.PUNJABI -> Locale("pa", "IN")
                TextLanguage.HINDI -> Locale("hi", "IN")
                TextLanguage.ENGLISH -> Locale.US
            }
            
            tts?.setLanguage(locale)
            tts?.setPitch(targetPitch)
            tts?.setSpeechRate(targetSpeechRate)
            tts?.speak(nextSegment.text, TextToSpeech.QUEUE_FLUSH, null, "Segment_$index")
        }
    }

    override fun speak(text: String) {
        if (text.isBlank()) return
        
        if (isInitialized) {
            val segments = splitIntoSegments(text)
            if (segments.isEmpty()) return
            synchronized(speechQueue) {
                speechQueue.clear()
                speechQueue.addAll(segments)
                currentQueueIndex = 0
            }
            playNextSegment()
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
            synchronized(speechQueue) {
                speechQueue.clear()
                currentQueueIndex = 0
            }
            tts?.stop()
        }
    }

    override fun release() {
        try {
            synchronized(speechQueue) {
                speechQueue.clear()
                currentQueueIndex = 0
            }
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