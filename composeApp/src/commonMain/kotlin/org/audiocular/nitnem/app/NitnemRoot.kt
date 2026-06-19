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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import org.audiocular.nitnem.audio.rememberStreamAudioController
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
                .padding(top = 28.dp)

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
                        uiState.miniPlayerVisible
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

                                    effectiveLines,

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
    }
}