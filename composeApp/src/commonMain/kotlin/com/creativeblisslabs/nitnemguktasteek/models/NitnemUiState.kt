package com.creativeblisslabs.nitnemguktasteek.models
import com.creativeblisslabs.nitnemguktasteek.models.CompanionState
import com.creativeblisslabs.nitnemguktasteek.models.BaniLine
import com.creativeblisslabs.nitnemguktasteek.app.models.AppTab
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.models.ExperienceTab
import com.creativeblisslabs.nitnemguktasteek.data.Hukamnama
data class NitnemUiState(

    /*
     * NAVIGATION
     */

    val selectedTab: AppTab =
        AppTab.Home,

    val selectedExperienceTab: ExperienceTab =
        ExperienceTab.Listen,
    
    /*
     * CONTENT
     */

    val banis: List<Bani> =
        emptyList(),

    val currentBani: Bani? = null,

    val completedBanis: Set<String> =
        emptySet(),

    /*
     * PLAYBACK
     */

    val playback: PlaybackSnapshot =
        PlaybackSnapshot(),

    val playbackSpeed: Float =
        1f,

    val showMiniPlayer: Boolean =
        false,

    val miniPlayerVisible: Boolean =
        false,

    val repeatMode: String =
        "OFF",

    val ambientMode: Boolean =
        false,

    val sleepTimerMinutes: Int =
        0,

    val persistentPlayback: Boolean =
        true,

    val backgroundAudio: Boolean =
        true,

    /*
     * AUTO SCROLL
     */

    val autoScroll: Boolean =
        true,

    val autoScrollSpeed: Float =
        1f,

    /*
     * READING
     */

    val displayLanguage:
        DisplayLanguage =
        DisplayLanguage.English,

    val lineSpacing: Float =
        36f,

    val gurmukhiFontSize: Float =
        30f,

    val romanTransliterationFontSize:
        Float = 20f,

    val translationFontSize: Float =
        18f,

    val wordMeaningFontSize: Float =
        16f,
val syncedLines: List<BaniLine> = emptyList(),
    /*
     * VISIBILITY
     */
val totalListeningMinutes: Int = 0,
    val showGurmukhi: Boolean =
        true,

    val showTranslation: Boolean =
        true,

    val showRomanTransliteration:
        Boolean = true,

    val showEnglishTranslation:
        Boolean = true,

    val showPunjabiTranslation:
        Boolean = false,

    val showDevanagariTransliteration:
        Boolean = false,

    val showWordMeaning: Boolean =
        false,

    /*
     * STYLING
     */

    val boldGurmukhi: Boolean =
        true,

    val italicRomanTransliteration:
        Boolean = true,

    val centerGurmukhi: Boolean =
        true,

    val highlightActiveLine:
        Boolean = true,

    val showLineProgress:
        Boolean = true,

    val compactMode: Boolean =
        false,

    val focusMode: Boolean =
        false,
    val fullScreenReadingMode: Boolean =
        false,
    val blackAndWhiteAodMode: Boolean =
        false,
    val larivaarMode: Boolean =
        true,
    val amritTime: String = "04:30 AM",
    val rehrasTime: String = "06:30 PM",
    val sohilaTime: String = "10:00 PM",
    /*
     * THEME
     */

    val darkMode: Boolean =
        false,

    val amoledMode: Boolean =
        false,

    val accentStyle: String =
        "Emerald",

    val gurmukhiFont: String =
        "Default",

    val romanFont: String =
        "Default",

    val devanagariFont: String =
        "Default",

    val gurmukhiColorMode: String =
        "default",

    val romanTransliterationColorMode: String =
        "default",

    val devanagariTransliterationColorMode: String =
        "default",

    val punjabiTranslationColorMode: String =
        "default",

    val translationColorMode: String =
        "default",

    val wordMeaningColorMode: String =
        "default",


    /*
     * REMINDERS
     */

    val notifications: Boolean =
        true,

    val smartReminders: Boolean =
        true,

    val reminderVibration:
        Boolean = true,

    val amritVelaEnabled:
        Boolean = true,

    val amritVelaHour: Int =
        4,

    val amritVelaMinute: Int =
        30,

    val rehrasEnabled: Boolean =
        true,

    val rehrasHour: Int =
        18,

    val rehrasMinute: Int =
        30,

    val sohilaEnabled: Boolean =
        true,

    val sohilaHour: Int =
        22,

    val sohilaMinute: Int =
        0,

    /*
     * STREAKS
     */

    val streakDays: Int =
        0,

    val weeklyCompleted: Int =
        0,

    val monthlyConsistencyDays:
        Int = 0,

    /*
     * DOWNLOADS
     */

    val downloadedBaniIds:
        Set<String> = emptySet(),

    val downloadingBaniIds:
        Set<String> = emptySet(),

    /*
     * BOOKMARKS
     */

    val bookmarkedLineIds:
        Set<String> = emptySet(),

    /*
     * SEARCH
     */
    
    val searchQuery: String =
        "",

    /*
     * ACTIVE LINE
     */

    val activeLineId: String? =
        null,

    /*
     * ONBOARDING
     */

    val onboardingCompleted:
        Boolean = false,

    /*
     * WIDGET CONFIGURATION
     */

    val widgetTheme: String = "Classic Gold",

    val widgetContent: String = "Next Bani",
    val isPremium: Boolean = false,
    val shouldShowPaywall: Boolean = false,
    val selectedInfoBani: Bani? = null,
val hukamnama: Hukamnama? = null,
    val activeTrackId: String = "default",
    val totalXp: Int = 0,
    val meditationMinutes: Int = 0,
    val malaMantraCount: Int = 0,
    val quizHighscore: Int = 0,
    val activeQuizSession: QuizSession? = null,
    val meditationTimerRunning: Boolean = false,
    val meditationRemainingSeconds: Int = 0,
    val meditationDurationSeconds: Int = 0,
    val meditationSelectedMode: String = "Mantra",
    val ambientParticleEffect: String = "Disabled",
    val companionState: CompanionState = CompanionState()
)