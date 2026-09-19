package com.creativeblisslabs.nitnemguktasteek.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.AccentStyle
import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState

private val Context.settingsDataStore by
    preferencesDataStore(
        name = "nitnem_settings"
    )

class SettingsRepository(
    private val context: Context
) {

    companion object {

        /*
         * THEME
         */

        private val DARK_MODE =
            booleanPreferencesKey(
                "dark_mode"
            )

        private val AMOLED_MODE =
            booleanPreferencesKey(
                "amoled_mode"
            )

        private val ACCENT_STYLE =
            stringPreferencesKey(
                "accent_style"
            )

        /*
         * READING
         */

        private val GURMUKHI_SIZE =
            floatPreferencesKey(
                "gurmukhi_size"
            )

        private val ROMAN_SIZE =
            floatPreferencesKey(
                "roman_size"
            )

        private val TRANSLATION_SIZE =
            floatPreferencesKey(
                "translation_size"
            )

        private val WORD_MEANING_SIZE =
            floatPreferencesKey(
                "word_meaning_size"
            )

        private val LINE_SPACING =
            floatPreferencesKey(
                "line_spacing"
            )

        /*
         * CONTENT
         */

        private val SHOW_GURMUKHI =
            booleanPreferencesKey(
                "show_gurmukhi"
            )

        private val SHOW_TRANSLATION =
            booleanPreferencesKey(
                "show_translation"
            )

        private val SHOW_ROMAN =
            booleanPreferencesKey(
                "show_roman"
            )

        private val SHOW_WORD_MEANING =
            booleanPreferencesKey(
                "show_word_meaning"
            )

        private val SHOW_ENGLISH =
            booleanPreferencesKey(
                "show_english"
            )

        private val SHOW_PUNJABI =
            booleanPreferencesKey(
                "show_punjabi"
            )

        private val SHOW_DEVANAGARI =
            booleanPreferencesKey(
                "show_devanagari"
            )

        /*
         * STYLE
         */

        private val BOLD_GURMUKHI =
            booleanPreferencesKey(
                "bold_gurmukhi"
            )

        private val CENTER_GURMUKHI =
            booleanPreferencesKey(
                "center_gurmukhi"
            )

        private val COMPACT_MODE =
            booleanPreferencesKey(
                "compact_mode"
            )

        private val LARIVAAR_MODE =
            booleanPreferencesKey(
                "larivaar_mode"
            )

        private val HIGHLIGHT_ACTIVE =
            booleanPreferencesKey(
                "highlight_active"
            )

        private val ITALIC_ROMAN =
            booleanPreferencesKey(
                "italic_roman"
            )

        private val SHOW_LINE_PROGRESS =
            booleanPreferencesKey(
                "show_line_progress"
            )

        private val FOCUS_MODE =
            booleanPreferencesKey(
                "focus_mode"
            )

        private val FULL_SCREEN_READING_MODE =
            booleanPreferencesKey(
                "full_screen_reading_mode"
            )

        private val BLACK_AND_WHITE_AOD_MODE =
            booleanPreferencesKey(
                "black_and_white_aod_mode"
            )

        private val SHOW_MINI_PLAYER =
            booleanPreferencesKey(
                "show_mini_player"
            )

        /*
         * PLAYBACK
         */

        private val PLAYBACK_SPEED =
            floatPreferencesKey(
                "playback_speed"
            )

        private val AUTO_SCROLL =
            booleanPreferencesKey(
                "auto_scroll"
            )

        private val AUTO_SCROLL_SPEED =
            floatPreferencesKey(
                "auto_scroll_speed"
            )

        private val REPEAT_MODE =
            stringPreferencesKey(
                "repeat_mode"
            )

        private val AMBIENT_MODE =
            booleanPreferencesKey(
                "ambient_mode"
            )

        private val PERSISTENT_PLAYBACK =
            booleanPreferencesKey(
                "persistent_playback"
            )

        private val BACKGROUND_AUDIO =
            booleanPreferencesKey(
                "background_audio"
            )

        /*
         * REMINDERS
         */

        private val NOTIFICATIONS =
            booleanPreferencesKey(
                "notifications"
            )

        private val SMART_REMINDERS =
            booleanPreferencesKey(
                "smart_reminders"
            )

        private val AMRIT_TIME =
            stringPreferencesKey(
                "amrit_time"
            )

        private val REHRAS_TIME =
            stringPreferencesKey(
                "rehras_time"
            )

        private val SOHILA_TIME =
            stringPreferencesKey(
                "sohila_time"
            )

        /*
         * LANGUAGE
         */

        private val DISPLAY_LANGUAGE =
            stringPreferencesKey(
                "display_language"
            )

        /*
         * ACCESSIBILITY
         */

        private val ELDER_MODE =
            booleanPreferencesKey(
                "elder_mode"
            )

        /*
         * COLORS
         */

        private val GURMUKHI_COLOR_MODE =
            stringPreferencesKey(
                "gurmukhi_color_mode"
            )

        private val ROMAN_COLOR_MODE =
            stringPreferencesKey(
                "roman_color_mode"
            )

        private val DEVANAGARI_COLOR_MODE =
            stringPreferencesKey(
                "devanagari_color_mode"
            )

        private val PUNJABI_COLOR_MODE =
            stringPreferencesKey(
                "punjabi_color_mode"
            )

        private val TRANSLATION_COLOR_MODE =
            stringPreferencesKey(
                "translation_color_mode"
            )

        private val WORD_MEANING_COLOR_MODE =
            stringPreferencesKey(
                "word_meaning_color_mode"
            )

        /*
         * ONBOARDING
         */

        private val ONBOARDING_DONE =
            booleanPreferencesKey(
                "onboarding_done"
            )

        private val WIDGET_THEME =
            stringPreferencesKey(
                "widget_theme"
            )

        private val WIDGET_CONTENT =
            stringPreferencesKey(
                "widget_content"
            )

        private val LAST_STREAK_CELEBRATED_DATE =
            stringPreferencesKey(
                "last_streak_celebrated_date"
            )
    }

    suspend fun getLastStreakCelebratedDate(): String {
        return context.settingsDataStore.data.map { prefs ->
            prefs[LAST_STREAK_CELEBRATED_DATE] ?: ""
        }.first()
    }

    suspend fun setLastStreakCelebratedDate(date: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[LAST_STREAK_CELEBRATED_DATE] = date
        }
    }

    /*
     * SETTINGS FLOW
     */

    val preferencesFlow:
        Flow<NitnemUiState> =

        context.settingsDataStore.data.map { prefs ->

            NitnemUiState(

                /*
                 * THEME
                 */

                darkMode =
                    prefs[DARK_MODE]
                        ?: false,

                amoledMode =
                    prefs[AMOLED_MODE]
                        ?: false,

                elderMode =
                    prefs[ELDER_MODE]
                        ?: false,

                accentStyle =
                    prefs[ACCENT_STYLE]
                        ?: AccentStyle.Emerald.name,

                /*
                 * READING
                 */

                gurmukhiFontSize =
                    prefs[GURMUKHI_SIZE]
                        ?: 30f,

                romanTransliterationFontSize =
                    prefs[ROMAN_SIZE]
                        ?: 20f,

                translationFontSize =
                    prefs[TRANSLATION_SIZE]
                        ?: 18f,

                wordMeaningFontSize =
                    prefs[WORD_MEANING_SIZE]
                        ?: 16f,

                lineSpacing =
                    prefs[LINE_SPACING]
                        ?: 36f,

                /*
                 * CONTENT
                 */

                showGurmukhi =
                    prefs[SHOW_GURMUKHI]
                        ?: true,

                showTranslation =
                    prefs[SHOW_TRANSLATION]
                        ?: true,

                showRomanTransliteration =
                    prefs[SHOW_ROMAN]
                        ?: true,

                showWordMeaning =
                    prefs[SHOW_WORD_MEANING]
                        ?: false,

                showEnglishTranslation =
                    prefs[SHOW_ENGLISH]
                        ?: true,

                showPunjabiTranslation =
                    prefs[SHOW_PUNJABI]
                        ?: false,

                showDevanagariTransliteration =
                    prefs[SHOW_DEVANAGARI]
                        ?: false,

                /*
                 * STYLE
                 */

                boldGurmukhi =
                    prefs[BOLD_GURMUKHI]
                        ?: true,

                centerGurmukhi =
                    prefs[CENTER_GURMUKHI]
                        ?: true,

                compactMode =
                    prefs[COMPACT_MODE]
                        ?: false,

                larivaarMode =
                    prefs[LARIVAAR_MODE]
                        ?: true,

                highlightActiveLine =
                    prefs[HIGHLIGHT_ACTIVE]
                        ?: true,

                italicRomanTransliteration =
                    prefs[ITALIC_ROMAN]
                        ?: true,

                showLineProgress =
                    prefs[SHOW_LINE_PROGRESS]
                        ?: true,

                focusMode =
                    prefs[FOCUS_MODE]
                        ?: false,

                fullScreenReadingMode =
                    prefs[FULL_SCREEN_READING_MODE]
                        ?: false,

                blackAndWhiteAodMode =
                    prefs[BLACK_AND_WHITE_AOD_MODE]
                        ?: false,

                /*
                 * PLAYBACK
                 */

                playbackSpeed =
                    prefs[PLAYBACK_SPEED]
                        ?: 1f,

                autoScroll =
                    prefs[AUTO_SCROLL]
                        ?: true,

                autoScrollSpeed =
                    prefs[AUTO_SCROLL_SPEED]
                        ?: 1f,

                repeatMode =
                    prefs[REPEAT_MODE]
                        ?: "OFF",

                ambientMode =
                    prefs[AMBIENT_MODE]
                        ?: false,

                persistentPlayback =
                    prefs[PERSISTENT_PLAYBACK]
                        ?: true,

                backgroundAudio =
                    prefs[BACKGROUND_AUDIO]
                        ?: true,

                showMiniPlayer =
                    prefs[SHOW_MINI_PLAYER]
                        ?: false,

                /*
                 * REMINDERS
                 */

                notifications =
                    prefs[NOTIFICATIONS]
                        ?: true,

                smartReminders =
                    prefs[SMART_REMINDERS]
                        ?: true,

                amritTime =
                    prefs[AMRIT_TIME].let { if (it.isNullOrBlank()) "04:30 AM" else it },

                rehrasTime =
                    prefs[REHRAS_TIME].let { if (it.isNullOrBlank()) "06:30 PM" else it },

                sohilaTime =
                    prefs[SOHILA_TIME].let { if (it.isNullOrBlank()) "10:00 PM" else it },

                /*
                 * LANGUAGE
                 */

                displayLanguage =
                    runCatching {

                        DisplayLanguage.valueOf(

                            prefs[DISPLAY_LANGUAGE]
                                ?: DisplayLanguage.English.name
                        )

                    }.getOrDefault(
                        DisplayLanguage.English
                    ),

                /*
                 * COLORS
                 */

                gurmukhiColorMode =
                    prefs[GURMUKHI_COLOR_MODE]
                        ?: "default",

                romanTransliterationColorMode =
                    prefs[ROMAN_COLOR_MODE]
                        ?: "default",

                devanagariTransliterationColorMode =
                    prefs[DEVANAGARI_COLOR_MODE]
                        ?: "default",

                punjabiTranslationColorMode =
                    prefs[PUNJABI_COLOR_MODE]
                        ?: "default",

                translationColorMode =
                    prefs[TRANSLATION_COLOR_MODE]
                        ?: "default",

                wordMeaningColorMode =
                    prefs[WORD_MEANING_COLOR_MODE]
                        ?: "default",

                /*
                 * ONBOARDING
                 */

                onboardingCompleted =
                    prefs[ONBOARDING_DONE]
                        ?: false,

                widgetTheme =
                    prefs[WIDGET_THEME]
                        ?: "Dark",

                widgetContent =
                    prefs[WIDGET_CONTENT]
                        ?: "Mascot Assistant"
            )
        }

    /*
     * THEME
     */

    suspend fun setDarkMode(
        enabled: Boolean
    ) {

        context.settingsDataStore.edit {

            it[DARK_MODE] = enabled
        }
    }

    suspend fun setAmoledMode(
        enabled: Boolean
    ) {

        context.settingsDataStore.edit {

            it[AMOLED_MODE] = enabled
        }
    }

    suspend fun setAccentStyle(
        style: AccentStyle
    ) {

        context.settingsDataStore.edit {

            it[ACCENT_STYLE] =
                style.name
        }
    }

    fun accentStyleFlow():
        Flow<AccentStyle> {

        return context
            .settingsDataStore
            .data
            .map { prefs ->

                runCatching {

                    AccentStyle.valueOf(

                        prefs[ACCENT_STYLE]
                            ?: AccentStyle.Emerald.name
                    )

                }.getOrDefault(
                    AccentStyle.Emerald
                )
            }
    }

    /*
     * READING
     */

    suspend fun setGurmukhiSize(value: Float) {

        context.settingsDataStore.edit {

            it[GURMUKHI_SIZE] = value
        }
    }

    suspend fun setRomanFontSize(value: Float) {

        context.settingsDataStore.edit {

            it[ROMAN_SIZE] = value
        }
    }

    suspend fun setTranslationFontSize(value: Float) {

        context.settingsDataStore.edit {

            it[TRANSLATION_SIZE] = value
        }
    }

    suspend fun setWordMeaningFontSize(value: Float) {

        context.settingsDataStore.edit {

            it[WORD_MEANING_SIZE] = value
        }
    }

    suspend fun setLineSpacing(value: Float) {

        context.settingsDataStore.edit {

            it[LINE_SPACING] = value
        }
    }

    /*
     * CONTENT
     */

    suspend fun setShowGurmukhi(enabled: Boolean) {
        context.settingsDataStore.edit {
            it[SHOW_GURMUKHI] = enabled
        }
    }

    suspend fun setShowTranslation(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[SHOW_TRANSLATION] = enabled
        }
    }

    suspend fun setShowRomanTransliteration(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[SHOW_ROMAN] = enabled
        }
    }

    suspend fun setShowWordMeaning(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[SHOW_WORD_MEANING] = enabled
        }
    }

    suspend fun setShowEnglishTranslation(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[SHOW_ENGLISH] = enabled
        }
    }

    suspend fun setShowPunjabiTranslation(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[SHOW_PUNJABI] = enabled
        }
    }

    suspend fun setShowDevanagariTransliteration(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[SHOW_DEVANAGARI] = enabled
        }
    }

    /*
     * REMINDERS
     */

    suspend fun setNotifications(
        enabled: Boolean
    ) {

        context.settingsDataStore.edit {

            it[NOTIFICATIONS] =
                enabled
        }
    }

    suspend fun setAmritTime(
        time: String
    ) {

        context.settingsDataStore.edit {

            it[AMRIT_TIME] = time
        }
    }

    suspend fun setRehrasTime(
        time: String
    ) {

        context.settingsDataStore.edit {

            it[REHRAS_TIME] = time
        }
    }

    suspend fun setSohilaTime(
        time: String
    ) {

        context.settingsDataStore.edit {

            it[SOHILA_TIME] = time
        }
    }

    suspend fun setSmartReminders(
        enabled: Boolean
    ) {

        context.settingsDataStore.edit {

            it[SMART_REMINDERS] =
                enabled
        }
    }

    /*
     * LANGUAGE
     */

    suspend fun setDisplayLanguage(
        language: DisplayLanguage
    ) {

        context.settingsDataStore.edit {

            it[DISPLAY_LANGUAGE] =
                language.name
        }
    }

    /*
     * STYLE
     */

    suspend fun setBoldGurmukhi(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[BOLD_GURMUKHI] = enabled
        }
    }

    suspend fun setCenterGurmukhi(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[CENTER_GURMUKHI] = enabled
        }
    }

    suspend fun setCompactMode(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[COMPACT_MODE] = enabled
        }
    }

    suspend fun setLarivaarMode(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[LARIVAAR_MODE] = enabled
        }
    }

    suspend fun setHighlightActiveLine(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[HIGHLIGHT_ACTIVE] = enabled
        }
    }

    suspend fun setShowMiniPlayer(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[SHOW_MINI_PLAYER] = enabled
        }
    }

    suspend fun setItalicRoman(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[ITALIC_ROMAN] = enabled
        }
    }

    suspend fun setShowLineProgress(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[SHOW_LINE_PROGRESS] = enabled
        }
    }

    suspend fun setFocusMode(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[FOCUS_MODE] = enabled
        }
    }

    suspend fun setFullScreenReadingMode(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[FULL_SCREEN_READING_MODE] = enabled
        }
    }

    suspend fun setBlackAndWhiteAodMode(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[BLACK_AND_WHITE_AOD_MODE] = enabled
        }
    }

    /*
     * PLAYBACK
     */

    suspend fun setPlaybackSpeed(speed: Float) {

        context.settingsDataStore.edit {

            it[PLAYBACK_SPEED] = speed
        }
    }

    suspend fun setAutoScroll(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[AUTO_SCROLL] = enabled
        }
    }

    suspend fun setAutoScrollSpeed(speed: Float) {

        context.settingsDataStore.edit {

            it[AUTO_SCROLL_SPEED] = speed
        }
    }

    suspend fun setRepeatMode(mode: String) {
        context.settingsDataStore.edit {
            it[REPEAT_MODE] = mode
        }
    }

    suspend fun setAmbientMode(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[AMBIENT_MODE] = enabled
        }
    }

    suspend fun setPersistentPlayback(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[PERSISTENT_PLAYBACK] = enabled
        }
    }

    suspend fun setBackgroundAudio(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[BACKGROUND_AUDIO] = enabled
        }
    }

    /*
     * ACCESSIBILITY
     */

    suspend fun setElderMode(enabled: Boolean) {

        context.settingsDataStore.edit {

            it[ELDER_MODE] = enabled
        }
    }

    /*
     * COLORS
     */

    suspend fun setGurmukhiColorMode(mode: String) {

        context.settingsDataStore.edit {

            it[GURMUKHI_COLOR_MODE] = mode
        }
    }

    suspend fun setRomanColorMode(mode: String) {

        context.settingsDataStore.edit {

            it[ROMAN_COLOR_MODE] = mode
        }
    }

    suspend fun setDevanagariColorMode(mode: String) {

        context.settingsDataStore.edit {

            it[DEVANAGARI_COLOR_MODE] = mode
        }
    }

    suspend fun setPunjabiColorMode(mode: String) {

        context.settingsDataStore.edit {

            it[PUNJABI_COLOR_MODE] = mode
        }
    }

    suspend fun setTranslationColorMode(mode: String) {

        context.settingsDataStore.edit {

            it[TRANSLATION_COLOR_MODE] = mode
        }
    }

    suspend fun setWordMeaningColorMode(mode: String) {

        context.settingsDataStore.edit {

            it[WORD_MEANING_COLOR_MODE] = mode
        }
    }

    /*
     * ONBOARDING
     */

    suspend fun completeOnboarding() {

        context.settingsDataStore.edit {

            it[ONBOARDING_DONE] = true
        }
    }

    suspend fun setWidgetTheme(value: String) {
        context.settingsDataStore.edit {
            it[WIDGET_THEME] = value
        }
    }

    suspend fun setWidgetContent(value: String) {
        context.settingsDataStore.edit {
            it[WIDGET_CONTENT] = value
        }
    }

    suspend fun saveOnboardingSettings(
        language: DisplayLanguage,
        darkMode: Boolean,
        amoledMode: Boolean,
        accent: AccentStyle,
        notifications: Boolean,
        amritTime: String,
        rehrasTime: String,
        sohilaTime: String,
        autoScroll: Boolean,
        repeatMode: String = "OFF",
        ambientMode: Boolean,
        widgetTheme: String,
        widgetContent: String,
        showGurmukhi: Boolean? = null,
        showDevanagari: Boolean? = null,
        showRoman: Boolean? = null,
        showEnglish: Boolean? = null,
        showPunjabi: Boolean? = null,
        showWordMeaning: Boolean? = null,
        larivaarMode: Boolean? = null
    ) {
        context.settingsDataStore.edit { preferences ->
            preferences[DISPLAY_LANGUAGE] = language.name
            preferences[DARK_MODE] = darkMode
            preferences[AMOLED_MODE] = amoledMode
            preferences[ACCENT_STYLE] = accent.name
            preferences[NOTIFICATIONS] = notifications
            preferences[AMRIT_TIME] = amritTime
            preferences[REHRAS_TIME] = rehrasTime
            preferences[SOHILA_TIME] = sohilaTime
            preferences[AUTO_SCROLL] = autoScroll
            preferences[REPEAT_MODE] = repeatMode
            preferences[AMBIENT_MODE] = ambientMode
            preferences[WIDGET_THEME] = widgetTheme
            preferences[WIDGET_CONTENT] = widgetContent
            preferences[ONBOARDING_DONE] = true

            val defaultEn = language == DisplayLanguage.English
            val defaultPa = language == DisplayLanguage.Punjabi
            val defaultHi = language == DisplayLanguage.Hindi

            if (showGurmukhi != null) preferences[SHOW_GURMUKHI] = showGurmukhi
            preferences[SHOW_ENGLISH] = showEnglish ?: defaultEn
            preferences[SHOW_PUNJABI] = showPunjabi ?: defaultPa
            preferences[SHOW_DEVANAGARI] = showDevanagari ?: defaultHi
            preferences[SHOW_ROMAN] = showRoman ?: defaultEn
            if (showWordMeaning != null) preferences[SHOW_WORD_MEANING] = showWordMeaning
            if (larivaarMode != null) preferences[LARIVAAR_MODE] = larivaarMode
        }
    }
}