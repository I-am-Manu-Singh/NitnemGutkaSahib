package org.audiocular.nitnem.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import org.audiocular.nitnem.audio.rememberStreamAudioController
import org.audiocular.nitnem.app.models.AppTab
import org.audiocular.nitnem.app.models.ExperienceTab
import org.audiocular.nitnem.app.notifications.rememberNotificationBridge
import org.audiocular.nitnem.app.ui.components.BaniCompletionMascotOverlay
import org.audiocular.nitnem.app.ui.components.BottomBar
import org.audiocular.nitnem.app.ui.player.ThinMiniPlayer
import org.audiocular.nitnem.app.ui.screens.GurbaniExperienceScreen
import org.audiocular.nitnem.app.ui.screens.HomeScreen
import org.audiocular.nitnem.app.ui.screens.PracticeScreen
import org.audiocular.nitnem.app.ui.screens.SettingsScreen
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.app.widget.rememberJourneyWidgetBridge
import org.audiocular.nitnem.audiosync.AudioLineSynchronizer
import org.audiocular.nitnem.data.OfflineFirstNitnemRepository
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import androidx.compose.foundation.layout.WindowInsets

@Composable
fun NitnemRoot(
    viewModel: NitnemViewModel
) {

    val repository = remember {
        OfflineFirstNitnemRepository()
    }

    val uiState by
    viewModel.uiState.collectAsState()

    val banis by
    repository.observeBanis()
        .collectAsState(
            initial = emptyList()
        )

    val audioController =
        rememberStreamAudioController()

    val playback by
    audioController.playback.collectAsState()

    val synchronizer = remember {
        AudioLineSynchronizer()
    }

    var preparedKey by remember {
        mutableStateOf<String?>(null)
    }

    val effectiveLines = remember(
        uiState.syncedLines,
        uiState.currentBani?.id
    ) {

        uiState.syncedLines.ifEmpty {

            uiState.currentBani
                ?.lines
                .orEmpty()
        }
    }



    /*
     * CURRENT LINE
     */
    val currentLineIndex =
        effectiveLines.indexOfFirst {
            it.id ==
                    synchronizer.activeLine(
                        effectiveLines,
                        playback.positionMs
                    )?.id
        }

    var showChrome by remember {
        mutableStateOf(true)
    }

    // Tracks user touch events to re-show chrome and restart the auto-hide countdown
    var interactionTick by remember { mutableStateOf(0) }

    // Auto-hide chrome (BottomBar + headers) when fullScreenReadingMode is enabled
    // and the user is on a reading/meditation tab.
    val autoHideTabs = setOf(AppTab.Experience, AppTab.Practice)
    LaunchedEffect(uiState.selectedTab, uiState.fullScreenReadingMode, interactionTick) {
        if (uiState.fullScreenReadingMode && uiState.selectedTab in autoHideTabs) {
            showChrome = true   // always show briefly on interaction / tab switch
            delay(4_000L)
            showChrome = false
        } else {
            showChrome = true
        }
    }

    /*
     * WIDGET
     */

    val widgetBridge =
        rememberJourneyWidgetBridge()

    val nitnemBaniIds = remember {
        setOf(
            "japji-sahib",
            "jaap-sahib",
            "tav-prasad-savaiye",
            "benti-chaupai-sahib",
            "anand-sahib",
            "rehras-sahib",
            "kirtan-sohila"
        )
    }

    LaunchedEffect(
        uiState.completedBanis,
        banis.size,
        uiState.currentBani?.id,
        uiState.streakDays,
        uiState.weeklyCompleted,
        uiState.monthlyConsistencyDays,
        uiState.widgetTheme,
        uiState.widgetContent
    ) {

        if (banis.isNotEmpty()) {
            val completedNitnemCount = uiState.completedBanis.count { it in nitnemBaniIds }

            widgetBridge.update(

                completed =
                    completedNitnemCount,

                total =
                    7,

                currentTitle =
                    uiState.currentBani?.title
                        ?: "Japji Sahib",

                streakDays =
                    uiState.streakDays,

                weeklyCompleted =
                    uiState.weeklyCompleted,

                monthlyConsistencyDays =
                    uiState.monthlyConsistencyDays,

                completedBaniIds =
                    uiState.completedBanis.toList(),

                widgetTheme =
                    uiState.widgetTheme,

                widgetContent =
                    uiState.widgetContent
            )
        }
    }

    // Synchronize user progress directly to Firestore whenever stats change
    val leaderboardRepo = org.audiocular.nitnem.domain.social.rememberFamilyLeaderboardRepository()
    val authLauncher = org.audiocular.nitnem.utils.rememberAuthLauncher()
    val authUser = authLauncher.currentUser

    LaunchedEffect(
        uiState.totalXp,
        uiState.streakDays,
        uiState.quizHighscore,
        uiState.totalListeningMinutes,
        uiState.completedBanis,
        authUser?.uid
    ) {
        if (leaderboardRepo != null && authUser != null) {
            val userDisplayName = authUser.displayName.takeIf { it.isNotBlank() }
                ?: authUser.email.substringBefore('@').replace(".", " ").replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
                .takeIf { it.isNotBlank() }
                ?: "Gurmukh"

            val progress = org.audiocular.nitnem.domain.social.FamilyMemberProgress(
                userId = authUser.uid,
                username = authUser.email.substringBefore('@'),
                displayName = userDisplayName,
                xp = uiState.totalXp,
                level = (uiState.totalXp / 500).coerceAtLeast(1),
                currentStreak = uiState.streakDays,
                bestStreak = uiState.streakDays.coerceAtLeast(1),
                totalRecitationMinutes = uiState.totalListeningMinutes,
                completedBanisCount = uiState.completedBanis.size,
                completedBanisToday = uiState.completedBanis.toList(),
                quizScore = uiState.quizHighscore,
                avatarMascotState = ""
            )
            leaderboardRepo.syncUserProgress("family_group_main", progress)
        }
    }

    /*
     * NOTIFICATIONS
     */

    val notificationBridge =
        rememberNotificationBridge()

    val lastCompletedBaniCount =
        remember {
            mutableStateOf(-1)
        }

    var globalCelebrationBaniTitle by remember { mutableStateOf<String?>(null) }
    var globalStreakCelebrationDays by remember { mutableStateOf<Int?>(null) }
    
    // Remember initial set to identify new completions correctly across initial load and runtime
    var previousCompletedBanis by remember { mutableStateOf<Set<String>?>(null) }

    LaunchedEffect(
        uiState.completedBanis
    ) {
        val prev = previousCompletedBanis
        if (prev == null) {
            // Initial load of completed banis
            previousCompletedBanis = uiState.completedBanis
        } else {
            val newlyAdded = uiState.completedBanis - prev
            if (newlyAdded.isNotEmpty()) {
                val newlyCompletedId = newlyAdded.first()
                val baniObj = banis.firstOrNull { it.id == newlyCompletedId }
                globalCelebrationBaniTitle = baniObj?.title ?: newlyCompletedId
            }
            previousCompletedBanis = uiState.completedBanis
        }

        val currentCount =
            uiState.completedBanis.size

        if (uiState.notifications) {

            if (
                lastCompletedBaniCount.value != -1 &&
                currentCount >
                lastCompletedBaniCount.value
            ) {

                val completed =
                    uiState.completedBanis.lastOrNull()

                if (completed != null) {

                    val baniTitle =

                        banis.firstOrNull {
                            it.id == completed
                        }?.title ?: completed

                    notificationBridge
                        .sendCompletionNotification(
                            baniTitle
                        )
                }
            }
        }

        lastCompletedBaniCount.value =
            currentCount
    }

    /*
     * STREAK CELEBRATION & NOTIFICATION
     */

    val lastStreak = remember { mutableStateOf(-1) }

    LaunchedEffect(
        uiState.streakDays
    ) {
        if (uiState.streakDays > 0) {
            viewModel.checkAndCelebrateDailyStreak(uiState.streakDays) { celebrationDays ->
                globalStreakCelebrationDays = celebrationDays
            }
        }

        if (
            uiState.notifications &&
            uiState.streakDays > 0
        ) {

            if (
                lastStreak.value != -1 &&
                uiState.streakDays >
                lastStreak.value
            ) {

                notificationBridge
                    .sendStreakNotification(
                        uiState.streakDays
                    )
            }
        }

        lastStreak.value =
            uiState.streakDays
    }

    /*
     * SLEEP TIMER
     */

    LaunchedEffect(
        uiState.sleepTimerMinutes
    ) {

        val minutes =
            uiState.sleepTimerMinutes

        if (minutes > 0) {

            delay(
                minutes * 60 * 1000L
            )

            audioController.playPause()

            viewModel.setSleepTimer(0)
        }
    }

    /*
     * LOAD DATA
     */

    LaunchedEffect(
        banis.map { it.id }
    ) {

        viewModel.loadBanis(banis)

        val downloaded =
            banis.filter {

                audioController
                    .isAudioDownloaded(it.id)

            }.map {
                it.id
            }.toSet()

        viewModel.setDownloadedBanis(
            downloaded
        )
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            playback
        }
            .distinctUntilChanged()
            .collect { currentPlayback ->

                viewModel.updatePlayback(
                    currentPlayback
                )
            }
    }


    /*
     * PLAYBACK SPEED
     */

    LaunchedEffect(
        uiState.playbackSpeed
    ) {

        audioController
            .updatePlaybackSpeed(
                uiState.playbackSpeed
            )
    }
    LaunchedEffect(currentLineIndex) {
        println(
            "MINI PLAYER LINE -> $currentLineIndex @ ${playback.positionMs}"
        )
    }
    /*
     * UI
     */

    Scaffold(

        containerColor = Background,

        contentWindowInsets =
            WindowInsets(0),

        bottomBar = {

            AnimatedVisibility(
                visible = showChrome
            ) {

                BottomBar(

                    selectedTab =
                        uiState.selectedTab,

                    playback = playback,

                    currentBani =
                        uiState.currentBani,

                    language =
                        uiState.displayLanguage,

                    onPlayPause = {

                        audioController.playPause()
                    },

                    onExpandPlayer = {

                        viewModel.toggleMiniPlayer(
                            !uiState.miniPlayerVisible
                        )
                    },

                    onToggleMiniPlayer = {

                        viewModel.toggleMiniPlayer(
                            !uiState.miniPlayerVisible
                        )
                    },

                    onTabSelected = {

                        viewModel.selectTab(it)
                    }
                )
            }
        }

    ) { padding ->

        val backgroundModifier = when {

            uiState.amoledMode -> {

                Modifier.background(
                    Color.Black
                )
            }

            else -> {

                Modifier.background(

                    Brush.verticalGradient(

                        listOf(
                            SurfaceColor,
                            Background
                        )
                    )
                )
            }
        }

        Box(

            modifier = Modifier
                .fillMaxSize()
                .then(backgroundModifier)
                .padding(padding)
                // .padding(top = 28.dp)
                .pointerInput(uiState.fullScreenReadingMode, uiState.selectedTab) {
                    // On any touch in fullscreen mode tabs, restore chrome and restart the hide timer
                    if (uiState.fullScreenReadingMode && uiState.selectedTab in autoHideTabs) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent()
                                interactionTick++
                            }
                        }
                    }
                }

        ) {

            when (uiState.selectedTab) {

                AppTab.Home -> {

                    HomeScreen(
                        viewModel = viewModel,
                        audioController = audioController
                    )
                }

                AppTab.Experience -> {

                    GurbaniExperienceScreen(

                        viewModel = viewModel,

                        audioController =
                            audioController,

                        onSeekToLine = {
                                line: BaniLine ->

                            audioController.seekTo(
                                line.startsAtMs
                            )

                            audioController.resume()
                        }
                    )
                }

                AppTab.Practice -> {

                    val currentBani =

                        uiState.currentBani
                            ?: banis.firstOrNull()

                    if (currentBani != null) {

                        PracticeScreen(

                            audioController =
                                audioController,

                            bani = currentBani,

                            displayLanguage =
                                uiState.displayLanguage,
                            uiState = uiState,
                            viewModel = viewModel
                        )
                    }
                }

                AppTab.Settings -> {

                    SettingsScreen(

                        viewModel = viewModel,

                        audioController =
                            audioController
                    )
                }
            }

            /*
             * MINI PLAYER
             */

            Box(

                modifier = Modifier
                    .fillMaxSize(),

                contentAlignment =
                    Alignment.BottomCenter
            ) {

                AnimatedVisibility(

                    visible =
                        uiState.miniPlayerVisible && (!uiState.fullScreenReadingMode || showChrome)
                ) {

                    ThinMiniPlayer(

                        modifier = Modifier
                            .fillMaxWidth(),

                        bani =
                            uiState.currentBani,

                        playback =
                            playback,
                        onSeekTo = { position ->

                            audioController.seekTo(position)

                        },
                        currentLine =
                            currentLineIndex
                                .coerceAtLeast(0),

                        totalLines =
                            effectiveLines.size,

                        displayLanguage =
                            uiState.displayLanguage,
                        repeatMode =
                            playback.repeatMode,
                        isMuted =
                            playback.isMuted,
                        onMuteToggle = {
                            audioController.setVolume(if (playback.isMuted) 1f else 0f)
                        },
                        onOpenPlayer = {
                            viewModel.selectTab(
                                AppTab.Experience
                            )
                            viewModel.selectExperienceTab(
                                ExperienceTab.Listen
                            )
                        },
                        onPlayPause = {
                            audioController.playPause()
                        },
                        onSeekForward = {
                            audioController
                                .seekForward(
                                    10000L
                                )
                        },
                        onSeekBack = {
                            audioController
                                .seekBack(
                                    10000L
                                )
                        },
                        onRepeatToggle = {
                            val nextMode = when (playback.repeatMode) {
                                "OFF" -> "ONE"
                                "ONE" -> "ALL"
                                else -> "OFF"
                            }
                            audioController.setRepeatMode(nextMode)
                        },
                        onSpeedToggle = {
                            val currentSpeed = playback.speed
                            val nextSpeed = when {
                                currentSpeed < 1.0f -> 1.0f
                                currentSpeed < 1.25f -> 1.25f
                                currentSpeed < 1.5f -> 1.5f
                                currentSpeed < 2.0f -> 2.0f
                                else -> 0.75f
                            }
                            viewModel.setPlaybackSpeed(nextSpeed)
                            audioController.updatePlaybackSpeed(nextSpeed)
                        },

                        onBookmark = {

                            val currentLine =

                                synchronizer.activeLine(

                                    effectiveLines,

                                    playback.positionMs
                                )

                            currentLine?.let { line ->

                                viewModel.toggleBookmark(
                                    line.id
                                )
                            }
                        }
                    )
                }
            }
        }

        globalCelebrationBaniTitle?.let { bTitle ->
            val completedNitnemCount = uiState.completedBanis.count { it in nitnemBaniIds }
            val totalNitnemCount = banis.count { it.id in nitnemBaniIds }.takeIf { it > 0 } ?: 7
            BaniCompletionMascotOverlay(
                baniTitle = bTitle,
                completedTodayCount = completedNitnemCount,
                totalNitnemCount = totalNitnemCount,
                completedBaniIds = uiState.completedBanis,
                displayLanguage = uiState.displayLanguage,
                onDismiss = { globalCelebrationBaniTitle = null }
            )
        }

        globalStreakCelebrationDays?.let { sDays ->
            BaniCompletionMascotOverlay(
                streakDayCount = sDays,
                completedBaniIds = uiState.completedBanis,
                displayLanguage = uiState.displayLanguage,
                onDismiss = { globalStreakCelebrationDays = null }
            )
        }
    }
}