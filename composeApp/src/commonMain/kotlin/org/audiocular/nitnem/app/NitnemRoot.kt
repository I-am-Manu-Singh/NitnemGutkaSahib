package org.audiocular.nitnem.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.audiocular.nitnem.app.audio.rememberStreamAudioController
import org.audiocular.nitnem.app.models.AppTab
import org.audiocular.nitnem.app.models.ExperienceTab
import org.audiocular.nitnem.app.notifications.rememberNotificationBridge
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
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

    val currentLineIndex = remember(
        playback.positionMs,
        uiState.currentBani
    ) {

        val activeLine =

            synchronizer.activeLine(

                uiState.currentBani
                    ?.lines
                    .orEmpty(),

                playback.positionMs
            )

        uiState.currentBani
            ?.lines
            ?.indexOfFirst {

                it.id == activeLine?.id
            }
            ?: 0
    }

    var showChrome by remember {
        mutableStateOf(true)
    }

    /*
     * WIDGET
     */

    val widgetBridge =
        rememberJourneyWidgetBridge()

    LaunchedEffect(
        uiState.completedBanis,
        banis,
        uiState.currentBani,
        uiState.streakDays,
        uiState.weeklyCompleted,
        uiState.monthlyConsistencyDays
    ) {

        if (banis.isNotEmpty()) {

            widgetBridge.update(

                completed =
                    uiState.completedBanis.size,

                total =
                    banis.size,

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
                    uiState.completedBanis.toList()
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

    LaunchedEffect(
        uiState.completedBanis
    ) {
        val currentCount = uiState.completedBanis.size
        if (uiState.notifications) {
            if (lastCompletedBaniCount.value != -1 && currentCount > lastCompletedBaniCount.value) {
                val completed = uiState.completedBanis.lastOrNull()
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
        lastCompletedBaniCount.value = currentCount
    }

    /*
     * STREAK NOTIFICATION
     */

    val lastStreak =
        remember {
            mutableStateOf(-1)
        }

    LaunchedEffect(
        uiState.streakDays
    ) {

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
        banis
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

    /*
     * PLAYBACK
     */

    LaunchedEffect(
        playback
    ) {

        viewModel.updatePlayback(
            playback
        )
    }

    /*
     * PREPARE AUDIO
     */

    var preparedUrl by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(
        uiState.currentBani
    ) {
        val bani = uiState.currentBani
        if (bani != null) {
            val trackId = audioController.getActiveTrackId(bani.id)
            viewModel.updateActiveTrackId(trackId)
        }
    }

    LaunchedEffect(
        uiState.currentBani,
        uiState.downloadedBaniIds,
        uiState.activeTrackId
    ) {

        val bani =
            uiState.currentBani
                ?: return@LaunchedEffect

        val activeUrl =
            audioController
                .getActiveAudioUrl(
                    bani.id
                )

        val url = when {

            activeUrl != null -> {
                activeUrl
            }

            else -> {
                val audio = bani.audio
                val durationMs = if (audio != null && audio.durationMs > 0L) {
                    audio.durationMs
                } else {
                    (bani.estimatedMinutes * 60000L).coerceAtLeast(60000L)
                }
                "simulate://${bani.id}?durationMs=$durationMs"
            }
        }

        if (
            !url.isNullOrBlank() &&
            preparedUrl != url
        ) {

            preparedUrl = url

            println(
                "Preparing audio: $url"
            )

            audioController.prepare(url, bani.title)
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

    /*
     * RELEASE
     */

    // Controller release is handled by the application process/service lifecycle since it is a Singleton

    /*
     * UI
     */

    Scaffold(

        containerColor = Background,

        contentWindowInsets = WindowInsets(0),

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
        .padding(top = 28.dp)

) {

            when (uiState.selectedTab) {

                AppTab.Home -> {

                    HomeScreen(
                        viewModel = viewModel
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
                            displayLanguage = uiState.displayLanguage
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
                        (uiState.miniPlayerVisible || uiState.playback.isPlaying) &&
                            uiState.selectedTab != AppTab.Settings
                ) {

                    ThinMiniPlayer(

                        modifier = Modifier
                            .fillMaxWidth(),

                        bani =
                            uiState.currentBani,

                        playback =
                            uiState.playback,

                        currentLine =
                            currentLineIndex
                                .coerceAtLeast(0),

                        totalLines =
                            uiState.currentBani
                                ?.lines
                                ?.size ?: 0,

                        displayLanguage =
                            uiState.displayLanguage,

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

                        onRepeatLine = {

                            val currentLine =

                                synchronizer.activeLine(

                                    uiState.currentBani
                                        ?.lines
                                        .orEmpty(),

                                    playback.positionMs
                                )

                            currentLine?.let { line ->

                                audioController.seekTo(
                                    line.startsAtMs
                                )
                            }
                        },

                        onBookmark = {

                            val currentLine =

                                synchronizer.activeLine(

                                    uiState.currentBani
                                        ?.lines
                                        .orEmpty(),

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
    }
}