package com.creativeblisslabs.nitnemguktasteek.app

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
import com.creativeblisslabs.nitnemguktasteek.audio.rememberStreamAudioController
import com.creativeblisslabs.nitnemguktasteek.app.models.AppTab
import com.creativeblisslabs.nitnemguktasteek.app.models.ExperienceTab
import com.creativeblisslabs.nitnemguktasteek.app.notifications.rememberNotificationBridge
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.BaniCompletionMascotOverlay
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.BottomBar
import com.creativeblisslabs.nitnemguktasteek.app.ui.player.ThinMiniPlayer
import com.creativeblisslabs.nitnemguktasteek.app.ui.screens.GurbaniExperienceScreen
import com.creativeblisslabs.nitnemguktasteek.app.ui.screens.HomeScreen
import com.creativeblisslabs.nitnemguktasteek.app.ui.screens.PracticeScreen
import com.creativeblisslabs.nitnemguktasteek.app.ui.screens.SettingsScreen
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.app.widget.rememberJourneyWidgetBridge
import com.creativeblisslabs.nitnemguktasteek.audiosync.AudioLineSynchronizer
import com.creativeblisslabs.nitnemguktasteek.data.OfflineFirstNitnemRepository
import com.creativeblisslabs.nitnemguktasteek.models.BaniLine
import com.creativeblisslabs.nitnemguktasteek.viewmodel.NitnemViewModel
import androidx.compose.foundation.layout.WindowInsets
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.utils.rememberAuthLauncher
import com.creativeblisslabs.nitnemguktasteek.domain.social.rememberFamilyLeaderboardRepository
import kotlinx.datetime.todayIn

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

    val context = androidx.compose.ui.platform.LocalContext.current
    var pendingPlayBaniId by remember { mutableStateOf<String?>(null) }

    val authLauncher = rememberAuthLauncher()
    val authUser = authLauncher.currentUser
    val leaderboardRepo = rememberFamilyLeaderboardRepository()

    LaunchedEffect(uiState.downloadedBaniIds, uiState.downloadingBaniIds) {
        val targetBaniId = pendingPlayBaniId
        if (targetBaniId != null) {
            val activeTrackId = audioController.getActiveTrackId(targetBaniId)
            val isStillDownloading = uiState.downloadingBaniIds.contains("${targetBaniId}_$activeTrackId") ||
                    (activeTrackId == "default" && uiState.downloadingBaniIds.contains(targetBaniId))
            if (!isStillDownloading) {
                val url = audioController.getActiveAudioUrl(targetBaniId)
                val currentBani = uiState.banis.firstOrNull { it.id == targetBaniId }
                if (url != null && currentBani != null) {
                    android.widget.Toast.makeText(
                        context,
                        "Download complete! Starting playback...".translate(uiState.displayLanguage),
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    audioController.play(url, currentBani.title)
                }
                pendingPlayBaniId = null
            }
        }
    }

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

    var isAssistantOpen by remember {
        mutableStateOf(false)
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

    // 1. Fetch Dynamic Audio Catalog from Firestore
    LaunchedEffect(Unit) {
        val tracks = authLauncher.fetchAudioCatalog()
        if (tracks.isNotEmpty()) {
            com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.updateCatalog(tracks)
        }
    }

    // 2. Restore User Profile & Streak stats from Firestore upon login or app launch
    LaunchedEffect(authUser?.uid) {
        val uid = authUser?.uid
        if (uid != null) {
            val restoredProfile = authLauncher.restoreUserProfile(uid)
            if (restoredProfile != null) {
                viewModel.restoreFromCloudUserData(
                    xp = restoredProfile.totalXp,
                    streak = restoredProfile.streakDays,
                    completedBanisCount = restoredProfile.completedBanisCount,
                    completedToday = restoredProfile.completedBanisToday,
                    totalMinutes = restoredProfile.totalRecitationMinutes,
                    quizScore = restoredProfile.quizScore,
                    malaCount = restoredProfile.malaMantraCount
                )
            }
        }
    }

    // 3. Synchronize user progress directly to Firestore whenever stats change
    LaunchedEffect(
        uiState.totalXp,
        uiState.streakDays,
        uiState.quizHighscore,
        uiState.totalListeningMinutes,
        uiState.completedBanis,
        uiState.malaMantraCount,
        authUser?.uid
    ) {
        if (leaderboardRepo != null && authUser != null) {
            val userDisplayName = authUser.displayName.takeIf { it.isNotBlank() }
                ?: authUser.email.substringBefore('@').replace(".", " ").replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
                .takeIf { it.isNotBlank() }
                ?: "Gurmukh"

            val progress = com.creativeblisslabs.nitnemguktasteek.domain.social.FamilyMemberProgress(
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

            // Also backup to user daily history subcollection
            val todayStr = kotlinx.datetime.Clock.System.todayIn(kotlinx.datetime.TimeZone.currentSystemDefault()).toString()
            authLauncher.syncDailyHistory(
                userId = authUser.uid,
                date = todayStr,
                completedBanis = uiState.completedBanis.toList(),
                readingMinutes = uiState.totalListeningMinutes,
                listeningMinutes = uiState.totalListeningMinutes
            )
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

                    // Also dispatch daily progress milestone notification
                    notificationBridge
                        .sendDailyProgressNotification(
                            completedCount = currentCount,
                            totalCount = 7
                        )
                }
            }
        }

        lastCompletedBaniCount.value =
            currentCount
    }

    /*
     * STREAK & WEEKLY CELEBRATION & NOTIFICATION
     */

    val lastStreak = remember { mutableStateOf(-1) }
    val lastWeeklyCount = remember { mutableStateOf(-1) }

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

    LaunchedEffect(
        uiState.weeklyCompleted
    ) {
        if (uiState.notifications && uiState.weeklyCompleted > 0) {
            if (lastWeeklyCount.value != -1 && uiState.weeklyCompleted > lastWeeklyCount.value && uiState.weeklyCompleted % 7 == 0) {
                notificationBridge.sendWeeklySummaryNotification(
                    completedCount = uiState.weeklyCompleted,
                    streakDays = uiState.streakDays
                )
            }
        }
        lastWeeklyCount.value = uiState.weeklyCompleted
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
                        val currentBani = uiState.currentBani ?: banis.firstOrNull()
                        if (currentBani != null) {
                            val activeTrackId = audioController.getActiveTrackId(currentBani.id)
                            val isDownloading = uiState.downloadingBaniIds.contains("${currentBani.id}_$activeTrackId") ||
                                    (activeTrackId == "default" && uiState.downloadingBaniIds.contains(currentBani.id))
                            if (isDownloading) {
                                pendingPlayBaniId = currentBani.id
                                android.widget.Toast.makeText(
                                    context,
                                    "Audio file is downloading... Playback will start automatically when ready.".translate(uiState.displayLanguage),
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val url = audioController.getActiveAudioUrl(currentBani.id)
                                if (url != null && !playback.isPlaying && !playback.isReady) {
                                    audioController.play(url, currentBani.title)
                                } else {
                                    audioController.playPause()
                                }
                            }
                        } else {
                            audioController.playPause()
                        }
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
                        audioController = audioController,
                        onAssistantVisibilityChanged = { isOpen ->
                            isAssistantOpen = isOpen
                        }
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
                        uiState.miniPlayerVisible && uiState.selectedTab != AppTab.Practice && (!uiState.fullScreenReadingMode || showChrome) && !isAssistantOpen
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
                            val currentBani = uiState.currentBani ?: banis.firstOrNull()
                            if (currentBani != null) {
                                val activeTrackId = audioController.getActiveTrackId(currentBani.id)
                                val isDownloading = uiState.downloadingBaniIds.contains("${currentBani.id}_$activeTrackId") ||
                                        (activeTrackId == "default" && uiState.downloadingBaniIds.contains(currentBani.id))
                                if (isDownloading) {
                                    pendingPlayBaniId = currentBani.id
                                    android.widget.Toast.makeText(
                                        context,
                                        "Audio file is downloading... Playback will start automatically when ready.".translate(uiState.displayLanguage),
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val url = audioController.getActiveAudioUrl(currentBani.id)
                                    if (url != null && !playback.isPlaying && !playback.isReady) {
                                        audioController.play(url, currentBani.title)
                                    } else {
                                        audioController.playPause()
                                    }
                                }
                            } else {
                                audioController.playPause()
                            }
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