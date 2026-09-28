package com.creativeblisslabs.nitnemguktasteek.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.creativeblisslabs.nitnemguktasteek.audio.StreamAudioController
import com.creativeblisslabs.nitnemguktasteek.app.models.AppTab
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.models.ExperienceTab
import com.creativeblisslabs.nitnemguktasteek.data.analytics.AnalyticsRepository
import com.creativeblisslabs.nitnemguktasteek.data.bookmarks.BookmarksRepository
import com.creativeblisslabs.nitnemguktasteek.data.playback.PlaybackRepository
import com.creativeblisslabs.nitnemguktasteek.data.preferences.SettingsRepository
import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.BaniLine
import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState
import com.creativeblisslabs.nitnemguktasteek.models.QuizSession
import com.creativeblisslabs.nitnemguktasteek.models.QuizQuestion
import com.creativeblisslabs.nitnemguktasteek.models.PlaybackSnapshot
import com.creativeblisslabs.nitnemguktasteek.data.Hukamnama
import com.creativeblisslabs.nitnemguktasteek.models.CompanionState
import com.creativeblisslabs.nitnemguktasteek.gamification.CompanionProgression
class NitnemViewModel(

    private val settingsRepository:
        SettingsRepository,

    private val analyticsRepository:
        AnalyticsRepository,

    private val playbackRepository:
        PlaybackRepository,

    private val bookmarksRepository:
        BookmarksRepository

) : ViewModel() {
    private var lastSavedPlaybackBucket = -1L
    private var lastTrackedMinute = -1
    private var restoredBaniId: String? = null

    private val _uiState =
        MutableStateFlow(
            NitnemUiState()
        )
    val uiState:
        StateFlow<NitnemUiState> =
        _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            analyticsRepository.recordDailyAppOpen()
        }

        observeSettings()

        observeAnalytics()

        observePlayback()

        observeBookmarks()
    }
   fun setHukamnama(
    data: Hukamnama
) {

    _uiState.update {

        it.copy(
            hukamnama = data
        )
    }
}

    fun hasValidTimings(

        lines: List<BaniLine>

    ): Boolean {

        return lines.any {

            it.startsAtMs > 0L

        }

    }
    fun setSyncedLines(
        lines: List<BaniLine>
    ) {

        val normalized = lines
            .distinctBy { it.id }
            .sortedBy { it.startsAtMs }
            .filter {
                it.startsAtMs >= 0L
            }

        if (
            _uiState.value.syncedLines ==
            normalized
        ) {
            return
        }

        _uiState.update {

            it.copy(
                syncedLines = normalized
            )
        }
    }

    fun awardXp(amount: Int) {
        _uiState.update { it.copy(totalXp = it.totalXp + amount) }
    }

    fun addMeditationMinutes(minutes: Int) {
        _uiState.update { 
            it.copy(
                meditationMinutes = it.meditationMinutes + minutes,
                totalXp = it.totalXp + (minutes * 10)
            )
        }
    }

    fun incrementMalaCount() {
        _uiState.update { 
            val newMalaCount = it.malaMantraCount + 1
            val newXp = if (newMalaCount % 108 == 0) it.totalXp + 5 else it.totalXp
            it.copy(
                malaMantraCount = newMalaCount,
                totalXp = newXp
            )
        }
    }

    fun addMalaCount(count: Int) {
        _uiState.update {
            val newMalaCount = it.malaMantraCount + count
            val newXp = it.totalXp + (count / 108) * 5
            it.copy(
                malaMantraCount = newMalaCount,
                totalXp = newXp
            )
        }
    }

    fun restoreFromCloudUserData(
        xp: Int,
        streak: Int,
        completedBanisCount: Int,
        completedToday: List<String>,
        totalMinutes: Int,
        quizScore: Int,
        malaCount: Int
    ) {
        _uiState.update { state ->
            val updatedCompleted = if (completedToday.isNotEmpty()) completedToday.toSet() else state.completedBanis
            state.copy(
                totalXp = maxOf(state.totalXp, xp),
                streakDays = maxOf(state.streakDays, streak),
                completedBanis = updatedCompleted,
                totalListeningMinutes = maxOf(state.totalListeningMinutes, totalMinutes),
                quizHighscore = maxOf(state.quizHighscore, quizScore),
                malaMantraCount = maxOf(state.malaMantraCount, malaCount)
            )
        }
    }

    fun resetMalaCount() {
        _uiState.update {
            it.copy(malaMantraCount = 0)
        }
    }

    fun setQuizSession(session: QuizSession?) {
        _uiState.update {
            it.copy(activeQuizSession = session)
        }
    }

    fun updateQuizHighscore(score: Int) {
        _uiState.update {
            it.copy(quizHighscore = maxOf(it.quizHighscore, score))
        }
    }

    fun updateMeditationState(
        running: Boolean,
        remainingSeconds: Int,
        durationSeconds: Int,
        selectedMode: String
    ) {
        _uiState.update {
            it.copy(
                meditationTimerRunning = running,
                meditationRemainingSeconds = remainingSeconds,
                meditationDurationSeconds = durationSeconds,
                meditationSelectedMode = selectedMode
            )
        }
    }
    /*
     * LOAD BANIS
     */

    fun loadBanis(
        items: List<Bani>
    ) {

        if (
            _uiState.value.banis ==
            items
        ) {
            return
        }

        _uiState.update { state ->

            val resolvedBani =

                items.firstOrNull {
                    it.id == restoredBaniId
                }
                    ?: state.currentBani
                    ?: items.firstOrNull()

            state.copy(

                banis = items,

                currentBani =
                    resolvedBani
            )
        }
    }

    /*
     * OBSERVERS
     */

    private fun observeSettings() {

        viewModelScope.launch {

            settingsRepository
                .preferencesFlow
                .collectLatest { settings ->

                    _uiState.update {

                        it.copy(

                            /*
                             * LANGUAGE
                             */

                            displayLanguage =
                                settings.displayLanguage,

                            /*
                             * VISIBILITY
                             */

                            showTranslation =
                                settings.showTranslation,

                            showEnglishTranslation =
                                settings.showEnglishTranslation,

                            showPunjabiTranslation =
                                settings.showPunjabiTranslation,

                            showRomanTransliteration =
                                settings.showRomanTransliteration,

                            showDevanagariTransliteration =
                                settings.showDevanagariTransliteration,

                            showWordMeaning =
                                settings.showWordMeaning,
                            amritTime =
    settings.amritTime,

rehrasTime =
    settings.rehrasTime,

sohilaTime =
    settings.sohilaTime,
                            /*
                             * STYLE
                             */

                            boldGurmukhi =
                                settings.boldGurmukhi,

                            italicRomanTransliteration =
                                settings.italicRomanTransliteration,

                            centerGurmukhi =
                                settings.centerGurmukhi,

                             compactMode =
                                 settings.compactMode,

                             larivaarMode =
                                 settings.larivaarMode,

                            highlightActiveLine =
                                settings.highlightActiveLine,

                            /*
                             * TYPOGRAPHY
                             */

                            lineSpacing =
                                settings.lineSpacing,

                            gurmukhiFontSize =
                                settings.gurmukhiFontSize,

                            translationFontSize =
                                settings.translationFontSize,

                            romanTransliterationFontSize =
                                settings.romanTransliterationFontSize,

                            wordMeaningFontSize =
                                settings.wordMeaningFontSize,

                            /*
                             * THEME
                             */

                            darkMode =
                                settings.darkMode,

                            amoledMode =
                                settings.amoledMode,

                            gurmukhiFont =
                                settings.gurmukhiFont,

                            romanFont =
                                settings.romanFont,

                            devanagariFont =
                                settings.devanagariFont,
                         
                            /*
                             * PLAYBACK
                             */

                            autoScroll =
                                settings.autoScroll,

                            playbackSpeed =
                                settings.playbackSpeed,

                            miniPlayerVisible =
                                settings.showMiniPlayer,

                            /*
                             * REMINDERS
                             */

                            notifications =
                                settings.notifications,

                            /*
                             * COLOR CUSTOMIZATIONS
                             */

                            gurmukhiColorMode =
                                settings.gurmukhiColorMode,

                            romanTransliterationColorMode =
                                settings.romanTransliterationColorMode,

                            devanagariTransliterationColorMode =
                                settings.devanagariTransliterationColorMode,

                            punjabiTranslationColorMode =
                                settings.punjabiTranslationColorMode,

                            translationColorMode =
                                settings.translationColorMode,

                            wordMeaningColorMode =
                                settings.wordMeaningColorMode,

                             widgetTheme =
                                 settings.widgetTheme,

                             widgetContent =
                                 settings.widgetContent,

                             showGurmukhi =
                                 settings.showGurmukhi,

                             showLineProgress =
                                 settings.showLineProgress,

                             focusMode =
                                 settings.focusMode,

                             fullScreenReadingMode =
                                 settings.fullScreenReadingMode,

                             blackAndWhiteAodMode =
                                 settings.blackAndWhiteAodMode,

                             autoScrollSpeed =
                                 settings.autoScrollSpeed
                        )
                    }
                }
        }
    }

    private fun observeAnalytics() {

        viewModelScope.launch {

            analyticsRepository
                .analyticsFlow
                .collectLatest { analytics ->

                    val today =
                        LocalDate.now()
                            .toString()

                    val todayCompleted =

                        analytics.history
                            .firstOrNull {
                                it.date == today
                            }

                    _uiState.update { state ->
                        val completedTodayCount = todayCompleted?.completedBanis?.size ?: 0
                        val updatedCompanion = com.creativeblisslabs.nitnemguktasteek.gamification.CompanionProgression()
                            .nurture(state.companionState, completedTodayCount)

                        state.copy(
                            completedBanis =
                                todayCompleted
                                    ?.completedBanis
                                    ?: emptySet(),

                            streakDays =
                                analyticsRepository
                                    .calculateCurrentStreak(
                                        analytics.history
                                    ),

                            weeklyCompleted =
                                analyticsRepository
                                    .weeklyCompletedDays(
                                        analytics.history
                                    ),

                            monthlyConsistencyDays =
                                analyticsRepository
                                    .monthlyConsistency(
                                        analytics.history
                                    ),

                            totalListeningMinutes =
                                analytics.history.sumOf {
                                    it.listeningMinutes
                                },

                            companionState = updatedCompanion
                        )
                    }
                }
        }
    }

    private fun observeBookmarks() {

        viewModelScope.launch {

            bookmarksRepository
                .bookmarksFlow
                .collectLatest { bookmarks ->

                    _uiState.update {

                        it.copy(
                            bookmarkedLineIds =
                                bookmarks
                        )
                    }
                }
        }
    }

    private fun observePlayback() {

        viewModelScope.launch {

            playbackRepository
                .playbackFlow
                .collectLatest { playback ->

                    val current =
                        _uiState.value.playback

                    /*
                     * IGNORE SAME PLAYBACK
                     */

                    if (

                        current.positionMs ==
                        playback.position &&

                        current.speed ==
                        playback.speed

                    ) {
                        return@collectLatest
                    }

                    restoredBaniId =
                        playback.baniId

                    val bani =
                        _uiState.value.banis
                            .firstOrNull {
                                it.id == playback.baniId
                            }

                    _uiState.update {

                        it.copy(

                            currentBani =
                                bani ?: it.currentBani,

                            playbackSpeed =
                                playback.speed,

                            playback =
                                it.playback.copy(

                                    positionMs =
                                        playback.position,

                                    speed =
                                        playback.speed
                                )
                        )
                    }
                }
        }
    }
    /*
     * NAVIGATION
     */

    fun selectTab(
        tab: AppTab
    ) {

        _uiState.update {

            val updatedExperienceTab = if (tab == AppTab.Experience) {
                ExperienceTab.Listen
            } else {
                it.selectedExperienceTab
            }
            it.copy(
                selectedTab = tab,
                selectedExperienceTab = updatedExperienceTab
            )
        }
    }

    fun selectExperienceTab(
        tab: ExperienceTab
    ) {

        _uiState.update {

            it.copy(
                selectedExperienceTab = tab
            )
        }
    }

    /*
     * BANI
     */

    fun selectBani(
        baniId: String
    ) {

        val bani =
            _uiState.value.banis
                .firstOrNull {
                    it.id == baniId
                }

        val hasAudio = bani?.audio != null ||
                com.creativeblisslabs.nitnemguktasteek.data.CuratedAudioCatalog.getTracksForBani(baniId).isNotEmpty() ||
                com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTracksForBani(baniId).isNotEmpty()

        _uiState.update {

            it.copy(
                currentBani = bani,
                selectedExperienceTab = if (hasAudio) ExperienceTab.Listen else ExperienceTab.Read
            )
        }
    }

    fun markComplete(
        bani: Bani
    ) {

        viewModelScope.launch {
            awardXp(50)
            analyticsRepository
                .markCompleted(

                    baniId = bani.id,

                    readingMinutes =
                        bani.estimatedMinutes
                )
        }
    }

    /*
     * PLAYBACK
     */

    fun updatePlayback(
        snapshot: PlaybackSnapshot
    ) {

        val previous =
            _uiState.value.playback

        /*
         * THROTTLE UPDATES
         */

        val previousBucket =
            previous.positionMs / 5000L

        val currentBucket =
            snapshot.positionMs / 5000L

        /*
         * IGNORE IDENTICAL UPDATES
         */

        if (

            previous.isPlaying ==
            snapshot.isPlaying &&

            previous.currentTitle ==
            snapshot.currentTitle &&

            previousBucket ==
            currentBucket &&

            previous.speed ==
            snapshot.speed

        ) {
            return
        }

        val startedPlaying =

            snapshot.isPlaying &&
                    !previous.isPlaying

        /*
         * UI STATE
         */

        _uiState.update {

            it.copy(

                playback = snapshot,

                miniPlayerVisible =

                    if (startedPlaying)
                        true
                    else
                        it.miniPlayerVisible
            )
        }

        /*
         * PERSIST MINI PLAYER
         */

        if (startedPlaying) {

            viewModelScope.launch {

                settingsRepository
                    .setShowMiniPlayer(true)
            }
        }

        val baniId =
            _uiState.value.currentBani?.id
                ?: return

        /*
         * SAVE PLAYBACK
         */

        val saveBucket =
            snapshot.positionMs / 15000L

        if (
            saveBucket !=
            lastSavedPlaybackBucket
        ) {

            lastSavedPlaybackBucket =
                saveBucket

            viewModelScope.launch {

                playbackRepository
                    .savePlayback(

                        baniId = baniId,

                        position =
                            snapshot.positionMs,

                        speed =
                            snapshot.speed
                    )
            }
        }

        /*
         * ANALYTICS
         */
        val currentMinute =

            (
                    snapshot.positionMs /
                            60000L
                    ).toInt()

        if (
            lastTrackedMinute == -1
        ) {

            lastTrackedMinute =
                currentMinute

            return
        }

        if (

            snapshot.isPlaying &&

            currentMinute ==
            lastTrackedMinute + 1

        ) {

            lastTrackedMinute =
                currentMinute

            viewModelScope.launch {

                analyticsRepository
                    .addListeningMinutes(1)
            }
        }
    }

    fun resetPlayback() {

        _uiState.update {

            it.copy(
                playback =
                    PlaybackSnapshot()
            )
        }
    }

    fun toggleMiniPlayer(
        visible: Boolean
    ) {

        _uiState.update {

            it.copy(
                miniPlayerVisible =
                    visible
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setShowMiniPlayer(
                    visible
                )
        }
    }

    fun setRepeatMode(
        mode: String
    ) {
        _uiState.update {
            it.copy(
                repeatMode = mode
            )
        }
    }

    fun toggleRepeatMode() {
        _uiState.update {
            val next = when (it.repeatMode) {
                "OFF" -> "ONE"
                "ONE" -> "ALL"
                else -> "OFF"
            }
            it.copy(repeatMode = next)
        }
    }

    fun setPlaybackSpeed(
        speed: Float
    ) {

        _uiState.update {

            it.copy(
                playbackSpeed = speed
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setPlaybackSpeed(
                    speed
                )
        }
    }

    fun setAutoScroll(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                autoScroll = enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setAutoScroll(
                    enabled
                )
        }
    }

    fun setAutoScrollSpeed(
        speed: Float
    ) {

        _uiState.update {

            it.copy(
                autoScrollSpeed = speed
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setAutoScrollSpeed(
                    speed
                )
        }
    }

    fun setSleepTimer(
        minutes: Int
    ) {

        _uiState.update {

            it.copy(
                sleepTimerMinutes =
                    minutes
            )
        }
    }

    /*
     * LANGUAGE
     */

    fun setDisplayLanguage(
        language: DisplayLanguage
    ) {
        val showEn = language == DisplayLanguage.English
        val showPa = language == DisplayLanguage.Punjabi
        val showHi = language == DisplayLanguage.Hindi

        _uiState.update {
            it.copy(
                displayLanguage = language,
                showEnglishTranslation = showEn,
                showPunjabiTranslation = showPa,
                showDevanagariTransliteration = showHi,
                showRomanTransliteration = showEn
            )
        }

        viewModelScope.launch {
            settingsRepository.setDisplayLanguage(language)
            settingsRepository.setShowEnglishTranslation(showEn)
            settingsRepository.setShowPunjabiTranslation(showPa)
            settingsRepository.setShowDevanagariTransliteration(showHi)
            settingsRepository.setShowRomanTransliteration(showEn)
        }
    }

    /*
     * TYPOGRAPHY
     */

    fun setLineSpacing(
        spacing: Float
    ) {

        _uiState.update {

            it.copy(
                lineSpacing = spacing
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setLineSpacing(
                    spacing
                )
        }
    }

    fun setGurmukhiFontSize(
        size: Float
    ) {

        _uiState.update {

            it.copy(
                gurmukhiFontSize = size
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setGurmukhiSize(
                    size
                )
        }
    }

    fun setGurmukhiColorMode(mode: String) {
        _uiState.update {
            it.copy(gurmukhiColorMode = mode)
        }
        viewModelScope.launch {
            settingsRepository.setGurmukhiColorMode(mode)
        }
    }

    fun setRomanTransliterationColorMode(mode: String) {
        _uiState.update {
            it.copy(romanTransliterationColorMode = mode)
        }
        viewModelScope.launch {
            settingsRepository.setRomanColorMode(mode)
        }
    }

    fun setDevanagariTransliterationColorMode(mode: String) {
        _uiState.update {
            it.copy(devanagariTransliterationColorMode = mode)
        }
        viewModelScope.launch {
            settingsRepository.setDevanagariColorMode(mode)
        }
    }

    fun setPunjabiTranslationColorMode(mode: String) {
        _uiState.update {
            it.copy(punjabiTranslationColorMode = mode)
        }
        viewModelScope.launch {
            settingsRepository.setPunjabiColorMode(mode)
        }
    }

    fun setTranslationColorMode(mode: String) {
        _uiState.update {
            it.copy(translationColorMode = mode)
        }
        viewModelScope.launch {
            settingsRepository.setTranslationColorMode(mode)
        }
    }

    fun setWordMeaningColorMode(mode: String) {
        _uiState.update {
            it.copy(wordMeaningColorMode = mode)
        }
        viewModelScope.launch {
            settingsRepository.setWordMeaningColorMode(mode)
        }
    }

    fun setTranslationFontSize(
        size: Float
    ) {

        _uiState.update {

            it.copy(
                translationFontSize =
                    size
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setTranslationFontSize(
                    size
                )
        }
    }

    fun setRomanTransliterationFontSize(
        size: Float
    ) {

        _uiState.update {

            it.copy(
                romanTransliterationFontSize =
                    size
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setRomanFontSize(
                    size
                )
        }
    }

    fun setWordMeaningFontSize(
        size: Float
    ) {

        _uiState.update {

            it.copy(
                wordMeaningFontSize =
                    size
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setWordMeaningFontSize(
                    size
                )
        }
    }

    /*
     * VISIBILITY
     */

    fun setShowTranslation(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                showTranslation =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setShowTranslation(
                    enabled
                )
        }
    }

    fun setShowGurmukhi(
        enabled: Boolean
    ) {
        _uiState.update {
            it.copy(
                showGurmukhi = enabled
            )
        }
        viewModelScope.launch {
            settingsRepository.setShowGurmukhi(enabled)
        }
    }

    fun setShowRomanTransliteration(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                showRomanTransliteration =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setShowRomanTransliteration(
                    enabled
                )
        }
    }

    fun setShowEnglishTranslation(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                showEnglishTranslation =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setShowEnglishTranslation(
                    enabled
                )
        }
    }

    fun setShowPunjabiTranslation(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                showPunjabiTranslation =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setShowPunjabiTranslation(
                    enabled
                )
        }
    }

    fun setShowDevanagariTransliteration(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                showDevanagariTransliteration =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setShowDevanagariTransliteration(
                    enabled
                )
        }
    }

    fun setShowWordMeaning(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                showWordMeaning =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setShowWordMeaning(
                    enabled
                )
        }
    }
fun setAmritTime(time: String) {

    _uiState.update {

        it.copy(
            amritTime = time
        )
    }

    viewModelScope.launch {

        settingsRepository.setAmritTime(time)
    }
}

fun setRehrasTime(time: String) {

    _uiState.update {

        it.copy(
            rehrasTime = time
        )
    }

    viewModelScope.launch {

        settingsRepository.setRehrasTime(time)
    }
}
fun setSohilaTime(time: String) {

    _uiState.update {

        it.copy(
            sohilaTime = time
        )
    }

    viewModelScope.launch {

        settingsRepository.setSohilaTime(time)
    }
}
    /*
     * STYLE
     */

    fun setBoldGurmukhi(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                boldGurmukhi =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setBoldGurmukhi(
                    enabled
                )
        }
    }

    fun setItalicRomanTransliteration(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                italicRomanTransliteration =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setItalicRoman(
                    enabled
                )
        }
    }

    fun setCenterGurmukhi(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                centerGurmukhi =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setCenterGurmukhi(
                    enabled
                )
        }
    }

    fun setHighlightActiveLine(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                highlightActiveLine =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setHighlightActiveLine(
                    enabled
                )
        }
    }

    fun setShowLineProgress(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                showLineProgress =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setShowLineProgress(
                    enabled
                )
        }
    }

    fun setCompactMode(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                compactMode =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setCompactMode(
                    enabled
                )
        }
    }

    fun setLarivaarMode(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                larivaarMode =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setLarivaarMode(
                    enabled
                )
        }
    }

    fun setFocusMode(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                focusMode =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setFocusMode(
                    enabled
                )
        }
    }

    fun setFullScreenReadingMode(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                fullScreenReadingMode =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setFullScreenReadingMode(
                    enabled
                )
        }
    }

    fun setBlackAndWhiteAodMode(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                blackAndWhiteAodMode =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setBlackAndWhiteAodMode(
                    enabled
                )
        }
    }

    /*
     * THEME
     */

    fun setDarkMode(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                darkMode =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setDarkMode(
                    enabled
                )
        }
    }

    fun setAmoledMode(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                amoledMode =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setAmoledMode(
                    enabled
                )
        }
    }

    fun setGurmukhiFont(font: String) {
        _uiState.update {
            it.copy(gurmukhiFont = font)
        }
        viewModelScope.launch {
            settingsRepository.setGurmukhiFont(font)
        }
    }

    fun setRomanFont(font: String) {
        _uiState.update {
            it.copy(romanFont = font)
        }
        viewModelScope.launch {
            settingsRepository.setRomanFont(font)
        }
    }

    fun setDevanagariFont(font: String) {
        _uiState.update {
            it.copy(devanagariFont = font)
        }
        viewModelScope.launch {
            settingsRepository.setDevanagariFont(font)
        }
    }

    fun setWidgetTheme(
        theme: String
    ) {
        _uiState.update {
            it.copy(
                widgetTheme = theme
            )
        }

        viewModelScope.launch {
            settingsRepository
                .setWidgetTheme(
                    theme
                )
        }
    }

    fun setWidgetContent(
        content: String
    ) {
        _uiState.update {
            it.copy(
                widgetContent = content
            )
        }

        viewModelScope.launch {
            settingsRepository
                .setWidgetContent(
                    content
                )
        }
    }

   

    /*
     * REMINDERS
     */

    fun setNotifications(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                notifications =
                    enabled
            )
        }

        viewModelScope.launch {

            settingsRepository
                .setNotifications(
                    enabled
                )
        }
    }

    fun setSmartReminders(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                smartReminders =
                    enabled
            )
        }
    }

    fun setAmbientMode(
        enabled: Boolean
    ) {

        _uiState.update {

            it.copy(
                ambientMode =
                    enabled
            )
        }
    }

    /*
     * DOWNLOADS
     */

    fun setDownloadedBanis(
        ids: Set<String>
    ) {

        _uiState.update {

            it.copy(
                downloadedBaniIds =
                    ids
            )
        }
    }

    fun startDownload(

        baniId: String,

        remoteUrl: String,

        audioController:
            StreamAudioController,
        trackId: String? = null
    ) {

        val key = if (trackId != null && trackId != "default") "${baniId}_$trackId" else baniId
        if (
            _uiState.value
                .downloadingBaniIds
                .contains(key)
        ) return

        _uiState.update {

            it.copy(

                downloadingBaniIds =

                    it.downloadingBaniIds +
                            key
            )
        }

        viewModelScope.launch {

            val success =

                audioController
                    .downloadAudio(

                        baniId = baniId,
                        remoteUrl = remoteUrl,
                        trackId = trackId
                    )

            _uiState.update {

                it.copy(

                    downloadingBaniIds =

                        it.downloadingBaniIds -
                                key,

                    downloadedBaniIds =

                        if (success)

                            it.downloadedBaniIds +
                                    key

                        else
                            it.downloadedBaniIds
                )
            }
        }
    }

    fun deleteBaniAudio(

        baniId: String,

        audioController:
            StreamAudioController
    ) {

        audioController.deleteAudio(
            baniId
        )

        _uiState.update {

            it.copy(

                downloadedBaniIds =

                    it.downloadedBaniIds -
                            baniId
            )
        }
    }

    /*
     * BOOKMARKS
     */

    fun toggleBookmark(
        lineId: String
    ) {

        viewModelScope.launch {

            bookmarksRepository
                .toggleBookmark(
                    lineId
                )
        }
    }

    fun clearAllBookmarks() {

        viewModelScope.launch {

            bookmarksRepository
                .clearBookmarks()
        }
    }

    /*
     * SEARCH
     */

    fun setSearchQuery(
        query: String
    ) {

        _uiState.update {

            it.copy(
                searchQuery =
                    query
            )
        }
    }

    /*
     * ACTIVE LINE
     */

    fun setActiveLine(
        lineId: String?
    ) {

        _uiState.update {

            it.copy(
                activeLineId =
                    lineId
            )
        }
    }

    /*
     * ONBOARDING
     */

    fun completeOnboarding() {

        _uiState.update {

            it.copy(
                onboardingCompleted =
                    true
            )
        }

        viewModelScope.launch {

            settingsRepository
                .completeOnboarding()
        }
    }

    fun updateActiveTrackId(trackId: String) {
        _uiState.update {
            it.copy(activeTrackId = trackId)
        }
    }

    fun setActiveTrackId(
        baniId: String,
        trackId: String,
        audioController: StreamAudioController
    ) {
        audioController.setActiveTrackId(baniId, trackId)
        _uiState.update {
            it.copy(activeTrackId = trackId)
        }
    }

    fun checkAndCelebrateDailyStreak(streakDays: Int, onCelebrate: (Int) -> Unit) {
        if (streakDays <= 0) return
        val today = LocalDate.now().toString()
        viewModelScope.launch {
            val lastCelebrated = settingsRepository.getLastStreakCelebratedDate()
            if (lastCelebrated != today) {
                settingsRepository.setLastStreakCelebratedDate(today)
                onCelebrate(streakDays)
            }
        }
    }

    fun setAmbientParticleEffect(effect: String) {
        _uiState.update {
            it.copy(ambientParticleEffect = effect)
        }
        viewModelScope.launch {
            settingsRepository.setAmbientParticleEffect(effect)
        }
    }
}