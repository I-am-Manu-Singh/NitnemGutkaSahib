package org.audiocular.nitnem.app.ui.screens

import android.widget.Toast
import org.audiocular.nitnem.utils.timingKey
import androidx.compose.animation.core.animateDpAsState
import org.audiocular.nitnem.app.models.ExperienceTab
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.audiocular.nitnem.audio.StreamAudioController
import org.audiocular.nitnem.app.sync.TimingLoader
import org.audiocular.nitnem.app.ui.components.GurbaniLineCard
import org.audiocular.nitnem.app.ui.components.GurbaniLineUiConfig
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.app.notifications.rememberNotificationBridge
import org.audiocular.nitnem.audiosync.AudioLineSynchronizer
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateDigits
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.WindowInsets
import org.audiocular.nitnem.app.ui.components.GurbaniMeaningSheet
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.lazy.LazyListState
import org.audiocular.nitnem.data.CuratedAudioCatalog

    @Composable
    fun GurbaniExperienceScreen(
        viewModel: NitnemViewModel,
        audioController: StreamAudioController,
        onSeekToLine: (BaniLine) -> Unit,
        onChromeVisibilityChanged: (Boolean) -> Unit = {}
    ) {

        var compactHeader by rememberSaveable {
            mutableStateOf(false)
        }

        var selectedMeaningLine by remember {
            mutableStateOf<BaniLine?>(null)
        }

        val uiState by
            viewModel.uiState.collectAsState()

        var activeTab by rememberSaveable {
            mutableStateOf(
                uiState.selectedExperienceTab
            )
        }

        LaunchedEffect(
            uiState.selectedExperienceTab
        ) {

            activeTab =
                uiState.selectedExperienceTab
        }

        val bani =
            uiState.currentBani

        val playback by
            audioController.playback.collectAsState()

        val currentActiveTrackId =
            remember(
                bani?.id,
                uiState.activeTrackId
            ) {
                uiState.activeTrackId.ifEmpty {
                    "default"
                }
            }

        val scope =
            rememberCoroutineScope()

        val context =
            LocalContext.current

        val density = LocalDensity.current
        // Use a small positive offset (20dp) so the active synchronized card lands just below
        // the header, fully visible rather than half-hidden above the viewport top.
        val scrollOffsetPx = with(density) { 20.dp.roundToPx() }

        var pauseReadAutoScroll by remember {
            mutableStateOf(false)
        }

        var wasAutoScrolling by remember {
            mutableStateOf(false)
        }

        val fabBottomPadding by animateDpAsState(

            targetValue =

                if (uiState.miniPlayerVisible)
                    80.dp
                else
                    8.dp,

            label = ""
        )

        var timingLoadState by remember {
            mutableStateOf<Boolean?>(null)
        }

        val syncedLines by produceState(

            initialValue = emptyList<BaniLine>(),

            key1 = bani?.id,

            key2 = currentActiveTrackId

        ) {

            if (bani == null) {

                timingLoadState = null
                value = emptyList()

                return@produceState
            }

            /*
             * GET ACTUAL ACTIVE AUDIO URL
             */

            val activeAudioUrl =
                audioController.getActiveAudioUrl(
                    bani.id
                )

            /*
             * RESOLVE TRACK USING ACTIVE TRACK ID
             */

            val track =
                (
                        audioController
                            .getCustomTracks(bani.id)

                                +

                                CuratedAudioCatalog
                                    .getTracksForBani(bani.id)

                        ).firstOrNull {
                        it.id == currentActiveTrackId
                    }

            /*
             * RESOLVE TIMING KEY
             */

            val timingKey = when {

                track != null -> {

                    track.timingKey()
                }

                activeAudioUrl
                    ?.contains("audio/") == true -> {

                    activeAudioUrl
                        .substringAfterLast("/")
                        .substringBeforeLast(".")
                }

                activeAudioUrl
                    ?.startsWith("raw/") == true -> {

                    activeAudioUrl
                        .removePrefix("raw/")
                        .substringBeforeLast(".")
                }

                else -> {

                    currentActiveTrackId
                        .replace("-", "_")
                }
            }

            println(
                "TIMING DEBUG -> " +
                        "bani=${bani.id}, " +
                        "track=$currentActiveTrackId, " +
                        "timingKey=$timingKey, " +
                        "audioUrl=$activeAudioUrl"
            )
            println(
                "TIMING DEBUG -> " +
                        "bani=${bani.id}, " +
                        "uiTrack=${uiState.activeTrackId}, " +
                        "controllerTrack=${audioController.getActiveTrackId(bani.id)}, " +
                        "resolvedTrack=$currentActiveTrackId, " +
                        "timingKey=$timingKey, " +
                        "audioUrl=$activeAudioUrl"
            )
            val hasTimingFile =
                audioController.hasTimingFile(
                    bani.id,
                    currentActiveTrackId
                )

            println(
                "TIMING CHECK -> " +
                        "bani=${bani.id}, " +
                        "track=$currentActiveTrackId, " +
                        "timingKey=$timingKey, " +
                        "hasTimingFile=$hasTimingFile"
            )
            val timings =
                if (hasTimingFile) {

                    val loaded =
                        TimingLoader.loadTimings(
                            context,
                            timingKey
                        )

                    println(
                        "TIMING LOAD -> " +
                                "timingKey=$timingKey, " +
                                "count=${loaded.size}"
                    )

                    loaded

                } else {

                    println(
                        "TIMING LOAD -> NO TIMING FILE"
                    )

                    emptyList()
                }

            timingLoadState = timings.isNotEmpty()

            if (timings.isEmpty()) {

                value = bani.lines.map { it.copy(startsAtMs = 0L, endsAtMs = 0L) }

                return@produceState
            }


            val timingMap =
                timings.associateBy {
                    it.id
                }
            println(
                "TIMING MAP SIZE -> ${timingMap.size}"
            )

            println(
                "FIRST TIMING IDS -> ${
                    timings.take(5).joinToString {
                        it.id
                    }
                }"
            )

            println(
                "FIRST BANI IDS -> ${
                    bani.lines.take(5).joinToString {
                        it.id
                    }
                }"
            )
            value =
                bani.lines.map { line ->

                    val timing =
                        timingMap[line.id]

                    if (timing != null) {

                        line.copy(

                            startsAtMs =
                                timing.startsAtMs,

                            endsAtMs =
                                timing.endsAtMs
                        )

                    } else {

                        line
                    }
                }
        }

    LaunchedEffect(
        timingLoadState
    ) {

        when (timingLoadState) {

            true -> {

                Toast.makeText(

                    context,

                    "Timings loaded successfully."
                        .translate(uiState.displayLanguage),

                    Toast.LENGTH_SHORT

                ).show()
            }

            false -> Unit

            null -> Unit
        }
    }
        val synchronizer = remember {
            AudioLineSynchronizer()
        }

        val activeLine =
            synchronizer.activeLine(
                syncedLines,
                playback.positionMs
            )
        val currentScrollSpeed by rememberUpdatedState(uiState.autoScrollSpeed)
        var showSpeedSlider by remember { mutableStateOf(false) }


        val segmentListState =
            rememberLazyListState()

        val lazyListState =
            rememberLazyListState()

        val notificationBridge = rememberNotificationBridge()
        /*
           * AUTO-COMPLETION TRIGGERS
           */
        val lastItemVisible by remember(syncedLines) {
            derivedStateOf {
                val layoutInfo = lazyListState.layoutInfo
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                if (syncedLines.isEmpty() || visibleItemsInfo.isEmpty()) {
                    false
                } else {
                    val lastVisibleItem = visibleItemsInfo.last()
                    lastVisibleItem.index >= syncedLines.size - 1
                }
            }
        }
        val hasTimings by remember(syncedLines) {
            derivedStateOf {
                syncedLines.any {
                    it.startsAtMs > 0L || it.endsAtMs > 0L
                }
            }
        }
        val currentLineIndex = remember(
            activeLine,
            syncedLines,
            activeTab,
            lazyListState.firstVisibleItemIndex,
            hasTimings
        ) {
            if (activeTab == ExperienceTab.Listen && hasTimings && activeLine != null) {
                val idx = syncedLines.indexOfFirst {
                    it.id == activeLine?.id
                }
                if (idx != -1) idx else lazyListState.firstVisibleItemIndex
            } else {
                lazyListState.firstVisibleItemIndex
            }
        }

        val readingProgressPercent by remember(
            activeTab,
            syncedLines,
            currentLineIndex,
            lastItemVisible
        ) {
            derivedStateOf {
                val totalLines = syncedLines.size
                if (totalLines == 0) return@derivedStateOf 0

                if (activeTab == ExperienceTab.Read) {
                    if (lastItemVisible) {
                        100
                    } else {
                        val lastVisibleIndex =
                            lazyListState.layoutInfo
                                .visibleItemsInfo
                                .lastOrNull()
                                ?.index ?: 0
                        (((lastVisibleIndex + 1).toFloat() / totalLines) * 100f).toInt().coerceIn(0, 99)
                    }
                } else {
                    // Listen mode progress
                    if (currentLineIndex >= totalLines - 1) {
                        100
                    } else {
                        (((currentLineIndex + 1).toFloat() / totalLines) * 100f).toInt().coerceIn(0, 99)
                    }
                }
            }
        }

        LaunchedEffect(
            bani?.id,
            readingProgressPercent,
            activeTab
        ) {

            if (
                bani != null &&
                readingProgressPercent in 1..100
            ) {

                notificationBridge.showReadingProgressNotification(
                    baniId = bani.id,
                    baniTitle = bani.title,
                    progress = readingProgressPercent
                )

            } else {

                notificationBridge.dismissReadingProgressNotification()
            }
        }

        val segments = remember(
            bani?.id,
            syncedLines
        ) {
            if (bani != null) {
                getBaniSegments(
                    bani.title,
                    syncedLines
                )
            } else {
                emptyList()
            }
        }
        val activeSegmentIndex by remember(
            currentLineIndex,
            segments
        ) {

            derivedStateOf {

                segments.indexOfFirst { segment ->

                    currentLineIndex in
                            segment.startIndex..segment.endIndex

                }.let {

                    if (it == -1) 0 else it
                }
            }
        }

        val lineUiConfig = remember(uiState) {

            GurbaniLineUiConfig(

                gurmukhiFontSize =
                    uiState.gurmukhiFontSize,

                romanTransliterationFontSize =
                    uiState.romanTransliterationFontSize,

                translationFontSize =
                    uiState.translationFontSize,

                wordMeaningFontSize =
                    uiState.wordMeaningFontSize,

                lineSpacing =
                    uiState.lineSpacing,

                showTranslation =
                    uiState.showTranslation,

                showRomanTransliteration =
                    uiState.showRomanTransliteration,

                showEnglishTranslation =
                    uiState.showEnglishTranslation,

                showPunjabiTranslation =
                    uiState.showPunjabiTranslation,

                showDevanagariTransliteration =
                    uiState.showDevanagariTransliteration,

                showWordMeaning =
                    uiState.showWordMeaning,

                gurmukhiColorMode =
                    uiState.gurmukhiColorMode,

                romantransliterationColorMode =
                    uiState.romanTransliterationColorMode,

                translationColorMode =
                    uiState.translationColorMode,

                wordMeaningColorMode =
                    uiState.wordMeaningColorMode,

                boldGurmukhi =
                    uiState.boldGurmukhi,

                italicRomanTransliteration =
                    uiState.italicRomanTransliteration,

                centerGurmukhi =
                    uiState.centerGurmukhi,

                highlightActiveLine =
                    uiState.highlightActiveLine,

                showLineProgress =
                    uiState.showLineProgress,

                compactMode =
                    uiState.compactMode,

                larivaarMode =
                    uiState.larivaarMode
            )
        }

        LaunchedEffect(Unit) {

            onChromeVisibilityChanged(
                true
            )
        }

        /*
     * AUTO SCROLL
     */

        LaunchedEffect(
            activeLine,
            activeTab,
            hasTimings
        ) {
            if (
                hasTimings &&
                activeLine != null &&
                activeTab == ExperienceTab.Listen
            ) {
                val index =
                    syncedLines.indexOfFirst {
                        it.id == activeLine?.id
                    }
                if (index >= 0) {
                    lazyListState.animateScrollToItem(index, scrollOffset = scrollOffsetPx)
                }
            }
        }
        LaunchedEffect(syncedLines) {

        viewModel.setSyncedLines(
            syncedLines
        )
    }
    LaunchedEffect(showSpeedSlider) {

        if (showSpeedSlider) {

            delay(3000)

            showSpeedSlider = false
        }
    }
        /*
         * READ MODE AUTO SCROLL
         */


    LaunchedEffect(
        uiState.autoScroll,
        activeTab,
        pauseReadAutoScroll,
        hasTimings,
        uiState.autoScrollSpeed
    ) {

        val isAutoScrolling =
            uiState.autoScroll &&
            (activeTab == ExperienceTab.Read ||
                    (activeTab == ExperienceTab.Listen && !hasTimings)) &&
            !pauseReadAutoScroll

        if (isAutoScrolling && !wasAutoScrolling) {

            showSpeedSlider = true

            val speedText = String.format("%.1f", currentScrollSpeed)

            val msg =
                "Auto Scroll Started".translate(uiState.displayLanguage) +
                        " (${"Speed".translate(uiState.displayLanguage)}: $speedText)"

            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        wasAutoScrolling = isAutoScrolling

        if (
            !uiState.autoScroll ||
            (activeTab != ExperienceTab.Read &&
                    (activeTab != ExperienceTab.Listen || hasTimings)) ||
            pauseReadAutoScroll
        ) {
            showSpeedSlider = false
            return@LaunchedEffect
        }

        while (isActive) {
            try {
                lazyListState.scroll(scrollPriority = androidx.compose.foundation.MutatePriority.Default) {
                    var lastFrameTimeNanos = 0L
                    while (isActive) {
                        withFrameNanos { frameTimeNanos ->
                            if (lastFrameTimeNanos > 0L) {
                                val elapsedNanos = frameTimeNanos - lastFrameTimeNanos
                                val elapsedSeconds = elapsedNanos / 1_000_000_000f
                                val pixelsPerSecond = currentScrollSpeed.coerceIn(0.5f, 12f) * 25f
                                val scrollAmount = pixelsPerSecond * elapsedSeconds
                                scrollBy(scrollAmount)
                            }
                            lastFrameTimeNanos = frameTimeNanos
                        }
                    }
                }
            } catch (e: Exception) {
                delay(100L)
                while (lazyListState.isScrollInProgress && isActive) {
                    delay(100L)
                }
            }
        }
    }

        LaunchedEffect(
            activeSegmentIndex
        ) {

            if (
                segments.isNotEmpty() &&
                activeSegmentIndex >= 0
            ) {

                segmentListState.animateScrollToItem(
                    activeSegmentIndex
                )
            }
        }

        /*
         * VOLUME
         */

       LaunchedEffect(activeTab) {

        if (activeTab == ExperienceTab.Read) {

            audioController.setVolume(0f)
             Toast.makeText(
                        context,
                        "Audio Muted".translate(uiState.displayLanguage),
                        Toast.LENGTH_SHORT
                    ).show()
        } else {

            audioController.setVolume(1f)
        }
    }
        /*
           * LISTEN MODE
           */
        val playbackCompleted by remember(
            activeLine,
            syncedLines,
            hasTimings
        ) {
            derivedStateOf {

                hasTimings &&
                        activeLine?.id ==
                        syncedLines.lastOrNull()?.id
            }
        }
        LaunchedEffect(
            bani?.id,
            activeTab,
            playbackCompleted,
            lastItemVisible
        ) {
            println(
                "COMPLETION CHECK -> " +
                        "position=${playback.positionMs}, " +
                        "duration=${playback.durationMs}, " +
                        "activeLine=${activeLine?.id}, " +
                        "playbackCompleted=$playbackCompleted"
            )
            val currentBani =
                bani ?: return@LaunchedEffect

            if (
                uiState.completedBanis.contains(
                    currentBani.id
                )
            ) {
                return@LaunchedEffect
            }

            /*
             * LISTEN MODE
             */
            if (
                activeTab == ExperienceTab.Listen &&
                playbackCompleted
            ) {
                viewModel.markComplete(currentBani)
            }

            /*
             * READ MODE
             */
            if (
                activeTab == ExperienceTab.Read &&
                lastItemVisible
            ) {



                viewModel.markComplete(
                    currentBani
                )

                notificationBridge.dismissReadingProgressNotification()
            }
        }

        LaunchedEffect(activeLine?.id) {
            activeLine?.let {
                println("ACTIVE LINE -> ${it.id}")
            }
        }
        LaunchedEffect(playbackCompleted) {
            println(
                "PLAYBACK COMPLETED -> $playbackCompleted"
            )
        }
        LaunchedEffect(
            bani?.id,
            currentActiveTrackId
        ) {

            val currentBani =
                bani ?: return@LaunchedEffect

            println(
                "TRACK EFFECT -> bani=${currentBani.id}, track=$currentActiveTrackId"
            )

            val url =
                audioController.getActiveAudioUrl(
                    currentBani.id
                )

            println(
                "TRACK EFFECT -> url=$url"
            )

            if (url != null) {

                audioController.play(
                    url = url,
                    title = currentBani.title
                )
            }
        }
        LaunchedEffect(uiState.activeTrackId) {

            println("UI TRACK -> ${uiState.activeTrackId}")

        }
        Scaffold(

        containerColor = Color.Transparent,

        contentWindowInsets = WindowInsets(0)

    ) { paddingValues ->

        Box(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

        /*
         * MAIN LAYOUT
         */

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            /*
             * TOP HEADER
             */

            Surface(

                modifier = Modifier
                    .fillMaxWidth(),

                shape = RoundedCornerShape(
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),

                color = SurfaceColor,

                tonalElevation = 8.dp,

                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                    ) {

                        /*
                         * COLLAPSIBLE TITLE
                         */

                        Row(

                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    compactHeader = !compactHeader
                                }
                                .padding(
                                    horizontal = 18.dp,
                                    vertical = 14.dp
                                ),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(

                                text = (bani?.title ?: "Nitnem").translate(uiState.displayLanguage),

                                modifier = Modifier.weight(1f),

                                fontSize =

                                    if (compactHeader)
                                        16.sp
                                    else
                                        22.sp,

                                fontWeight = FontWeight.Bold,

                                color = Primary,

                                maxLines = 1
                            )

                            Surface(

                                shape = CircleShape,

                                color =
                                    Gold.copy(alpha = 0.12f)
                            ) {

                                Box(

                                    modifier = Modifier
                                        .size(34.dp),

                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Icon(

                                        imageVector =

                                            if (compactHeader)
                                                Icons.Default.KeyboardArrowDown
                                            else
                                                Icons.Default.KeyboardArrowUp,

                                        contentDescription = null,

                                        tint = Gold,

                                        modifier =
                                            Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        /*
                         * EXPANDABLE CONTENT
                         */

                        AnimatedVisibility(
                            visible = !compactHeader
                        ) {

                            Column {

                                PremiumTabSwitcher(

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp),

                                    selectedTab = activeTab,

                                    playbackActive =
                                        playback.isPlaying,

                                    displayLanguage = uiState.displayLanguage,

                                    onTabSelected = { tab ->

                                        activeTab = tab

                                        viewModel.selectExperienceTab(tab)
                                    }
                                )

                                Spacer(
                                    Modifier.height(10.dp)
                                )

                                 if (syncedLines.size > 1 && segments.isNotEmpty()) {

                                                SegmentJumpBar(

                                                    listState = segmentListState,

                                                    segments = segments,

                                                    currentSegmentIndex = activeSegmentIndex,

                                                    displayLanguage = uiState.displayLanguage,

                                                    onJumpToIndex = { idx ->

                                                        val targetSegment =
                                                            segments.indexOfFirst {
                                                                idx in it.startIndex..it.endIndex
                                                            }.coerceAtLeast(0)

                                                        pauseReadAutoScroll = true
   //to stop clipping of top card for active synchronized card
                                                        syncedLines
                                                            .getOrNull(idx)
                                                            ?.let { line ->

                                                                if (
                                                                    hasTimings &&
                                                                    line.endsAtMs > line.startsAtMs
                                                                ) {

                                                                    onSeekToLine(line)
                                                                }
                                                            }

                                                        scope.launch {

                                                            lazyListState.animateScrollToItem(idx, scrollOffset = scrollOffsetPx)

                                                            segmentListState.animateScrollToItem(
                                                                targetSegment
                                                            )

                                                            delay(1500)

                                                            pauseReadAutoScroll = false
                                                        }
                                                    }
                                                )
                                            }
                                Spacer(
                                    Modifier.height(12.dp)
                                )
                            }
                        }
                    }
                }
            /*
             * CONTENT AREA
             */
                    Spacer(
                        Modifier.height(6.dp)
                    )
            Box(

                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                when (activeTab) {

                    ExperienceTab.Read,
                    ExperienceTab.Listen -> {

                        LazyColumn(

                            state = lazyListState,

                            modifier =
                                Modifier.fillMaxSize(),

                            contentPadding =
                                PaddingValues(

                                    start = 18.dp,
                                    end = 18.dp,
                                    top = 8.dp,
                                    bottom = 140.dp
                                ),

                            verticalArrangement =
                                Arrangement.spacedBy(

                                    if (uiState.compactMode)
                                        10.dp
                                    else
                                        16.dp
                                )
                        ) {

                            itemsIndexed(

                                items = syncedLines,

                                key = { _, line ->
                                    line.id
                                }

                            ) { _, line ->

                                val isActive =
                                    activeLine?.id == line.id

                                val targetProgress =
                                    if (
                                        isActive &&
                                        line.endsAtMs >
                                        line.startsAtMs
                                    ) {
                                        (
                                            (
                                                playback.positionMs -
                                                    line.startsAtMs
                                            ).toFloat() /
                                                (
                                                    line.endsAtMs -
                                                        line.startsAtMs
                                                    ).toFloat()
                                            )
                                                .coerceIn(0f, 1f)
                                    } else {
                                        0f
                                    }

                                val lineProgress by animateFloatAsState(
                                    targetValue = targetProgress,
                                    animationSpec = androidx.compose.animation.core.tween(
                                        durationMillis = 100,
                                        easing = androidx.compose.animation.core.LinearEasing
                                    ),
                                    label = "lineProgress"
                                )

                                GurbaniLineCard(

                                    line = line,

                                    config = lineUiConfig,

                                    isActive =

                                        activeTab ==
                                            ExperienceTab.Listen &&
                                            isActive,

                                    lineProgress =
                                        lineProgress,

                                    isBookmarked =

                                        uiState
                                            .bookmarkedLineIds
                                            .contains(line.id),

                                    onBookmark = {

                                        viewModel.toggleBookmark(
                                            line.id
                                        )
                                    },

                                    onClick = {

                                       if (
                                            hasTimings &&
                                            line.endsAtMs > line.startsAtMs
                                        ){

                                            onSeekToLine(line)
                                        }
                                    },

                                    onMeaningClick = {

                                        selectedMeaningLine = it
                                    }
                                )
                            }

                            item {

                                Spacer(
                                    Modifier.height(50.dp)
                                )
                            }
                        }
                    }

                    ExperienceTab.Bookmarks -> {

                        val bookmarkedLines =

                            syncedLines.filter {

                                uiState.bookmarkedLineIds
                                    .contains(it.id)
                            }

                        if (bookmarkedLines.isEmpty()) {

                            EmptyBookmarks(uiState.displayLanguage)

                        } else {

                            LazyColumn(

                                modifier =
                                    Modifier.fillMaxSize(),

                                contentPadding =
                                    PaddingValues(

                                        start = 18.dp,
                                        end = 18.dp,
                                        top = 8.dp,
                                        bottom = 140.dp
                                    ),

                                verticalArrangement =
                                    Arrangement.spacedBy(

                                        if (uiState.compactMode)
                                            10.dp
                                        else
                                            16.dp
                                    )
                            ) {

                                items(

                                    items = bookmarkedLines,

                                    key = { line ->
                                        line.id
                                    }

                                ) { line ->

                                    GurbaniLineCard(

                                        line = line,

                                        config =
                                            lineUiConfig,

                                        isActive = false,

                                        lineProgress = 0f,

                                        isBookmarked = true,

                                        onBookmark = {

                                            viewModel.toggleBookmark(
                                                line.id
                                            )
                                        },

                                        onClick = {

                                          if (
                                                hasTimings &&
                                                line.endsAtMs > line.startsAtMs
                                            ){

                                                onSeekToLine(line)
                                            }
                                        },

                                        onMeaningClick = {

                                            selectedMeaningLine = it
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        /*
         * FLOATING BUTTONS
         */

        val showAutoscrollSlider = (activeTab == ExperienceTab.Read) || (activeTab == ExperienceTab.Listen && !hasTimings)

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = fabBottomPadding,
                    end = 8.dp
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (showAutoscrollSlider) {
                FloatingJumpButton(
                    icon = Icons.Default.Speed,
                    onClick = {
                        showSpeedSlider = !showSpeedSlider
                    },
                    onDoubleTap = {}
                )
            }

            FloatingJumpButton(
                icon = Icons.Default.KeyboardDoubleArrowUp,
                onClick = {
                    pauseReadAutoScroll = true
                    scope.launch {
                        val firstVisible = lazyListState.firstVisibleItemIndex
                        val currentSegmentIdx = segments.indexOfFirst { firstVisible in it.startIndex..it.endIndex }
                        val targetSegmentIdx = (if (currentSegmentIdx != -1) currentSegmentIdx - 1 else 0).coerceAtLeast(0)
                        val targetIndex = if (segments.isNotEmpty()) segments[targetSegmentIdx].startIndex else 0

                        syncedLines
                            .getOrNull(targetIndex)
                            ?.let { line ->
                               if (
                                        hasTimings &&
                                        line.endsAtMs > line.startsAtMs
                                    ){
                                    onSeekToLine(line)
                                }
                            }

                        lazyListState
                            .animateScrollToItem(
                                targetIndex,
                                scrollOffset = 20
                            )

                        segmentListState
                            .animateScrollToItem(
                                targetSegmentIdx
                            )
pauseReadAutoScroll = true

scope.launch {

    delay(1500)

    pauseReadAutoScroll = false
}
                    }
                },
                onDoubleTap = {
                    scope.launch {
                        lazyListState
                            .animateScrollToItem(0)
                    }
                }
            )

            FloatingJumpButton(
                icon = Icons.Default.KeyboardDoubleArrowDown,
                onClick = {
                    pauseReadAutoScroll = true
                    scope.launch {
                        val firstVisible = lazyListState.firstVisibleItemIndex
                        val currentSegmentIdx = segments.indexOfFirst { firstVisible in it.startIndex..it.endIndex }
                        val targetSegmentIdx = (if (currentSegmentIdx != -1) currentSegmentIdx + 1 else 0).coerceAtMost(segments.lastIndex)
                        val targetIndex = if (segments.isNotEmpty()) segments[targetSegmentIdx].startIndex else 0

                        syncedLines
                            .getOrNull(targetIndex)
                            ?.let { line ->
                               if (
                                hasTimings &&
                                line.endsAtMs > line.startsAtMs
                            ){
                                    onSeekToLine(line)
                                }
                            }

                        lazyListState
                            .animateScrollToItem(
                                targetIndex,
                                scrollOffset = 20
                            )

                        segmentListState
                            .animateScrollToItem(
                                targetSegmentIdx
                            )
                                    pauseReadAutoScroll = true

                                    scope.launch {

                                        delay(1500)

                                        pauseReadAutoScroll = false
                                    }
                    }
                },
                onDoubleTap = {
                    scope.launch {
                        lazyListState
                            .animateScrollToItem(
                                syncedLines.lastIndex
                            )
                    }
                }
            )
        }

        /*
         * AUTO SCROLL MANUAL SPEED SLIDER
         */
        if (showAutoscrollSlider && showSpeedSlider) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 24.dp, end = 24.dp, bottom = fabBottomPadding + 8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = SurfaceColor.copy(alpha = 0.95f),
                tonalElevation = 6.dp,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, Accent.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.setAutoScroll(!uiState.autoScroll) }
                    ) {
                        Icon(
                            imageVector = if (uiState.autoScroll) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Toggle Autoscroll",
                            tint = Accent
                        )
                    }
                    Text(
                        text = "Auto Scroll".translate(uiState.displayLanguage),
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(75.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                  androidx.compose.material3.Slider(
                            value = uiState.autoScrollSpeed,

                            onValueChange = {
                                viewModel.setAutoScrollSpeed(it)

                                // keep visible while changing
                                showSpeedSlider = true
                            },

                            valueRange = 0.5f..8.0f,

                            modifier = Modifier.weight(1f),

                            colors = androidx.compose.material3.SliderDefaults.colors(
                                activeTrackColor = Accent,
                                thumbColor = Accent
                            )
                        )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", uiState.autoScrollSpeed),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(30.dp)
                    )
                }
            }
        }

        /*
         * MEANING SHEET
         */

        GurbaniMeaningSheet(
            line = selectedMeaningLine,
            onDismiss = {
                selectedMeaningLine = null
            },
            gurmukhiFontSize =
                uiState.gurmukhiFontSize,
            lineSpacing =
                uiState.lineSpacing,
            boldGurmukhi =
                uiState.boldGurmukhi,
            centerGurmukhi =
                uiState.centerGurmukhi,
            showEnglishTranslation =
                uiState.showEnglishTranslation,
            showPunjabiTranslation =
                uiState.showPunjabiTranslation,
            showDevanagariTransliteration =
                uiState.showDevanagariTransliteration,
            showWordMeaning =
                uiState.showWordMeaning,
            showRomanTransliteration =
                uiState.showRomanTransliteration,
            larivaarMode =
                uiState.larivaarMode,
            gurmukhiColorMode =
                uiState.gurmukhiColorMode,
            translationColorMode =
                uiState.translationColorMode,
            wordMeaningColorMode =
                uiState.wordMeaningColorMode,
            displayLanguage =
                uiState.displayLanguage,
            onToggleEnglish = {
                viewModel.setShowEnglishTranslation(it)
            },
            onTogglePunjabi = {
                viewModel.setShowPunjabiTranslation(it)
            },
            onToggleHindi = {
                viewModel.setShowDevanagariTransliteration(it)
            },
            onToggleWordMeaning = {
                viewModel.setShowWordMeaning(it)
            },
            onToggleRoman = {
                viewModel.setShowRomanTransliteration(it)
            },
            onToggleCenter = {
                viewModel.setCenterGurmukhi(it)
            },
            onToggleLarivaar = {
                viewModel.setLarivaarMode(it)
            }
        )
    }
}
}
@Composable
private fun EmptyBookmarks(displayLanguage: DisplayLanguage) {

    Box(

        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),

        contentAlignment =
            Alignment.Center
    ) {

        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(

                imageVector =
                    Icons.Filled.Bookmarks,

                contentDescription = null,

                tint =
                    TextPrimary.copy(alpha = 0.25f),

                modifier =
                    Modifier.size(56.dp)
            )

            Spacer(
                Modifier.height(16.dp)
            )

          Text(

    text =
        "No bookmarks yet".translate(displayLanguage),

    color =
        TextPrimary.copy(alpha = 0.4f),

    fontSize = 17.sp
)
        }
    }
}

@Composable
private fun PremiumTabSwitcher(
    modifier: Modifier = Modifier,
    selectedTab: ExperienceTab,
    playbackActive: Boolean,
    displayLanguage: DisplayLanguage,
    onTabSelected: (ExperienceTab) -> Unit
) {

    val tabs =
        ExperienceTab.entries

    val selectedIndex =
        tabs.indexOf(selectedTab)

    val containerColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Primary.copy(alpha = 0.06f)
        }

        ThemeMode.Dark -> {
            Color.White.copy(alpha = 0.08f)
        }

        ThemeMode.Amoled -> {
            Color.White.copy(alpha = 0.05f)
        }
    }

    Surface(

        modifier = modifier,

        shape = RoundedCornerShape(100.dp),

        color = containerColor
    ) {

        BoxWithConstraints(

            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(4.dp)
        ) {

            val tabWidth =
                maxWidth / tabs.size

            val animatedOffset by animateDpAsState(

                targetValue =
                    tabWidth * selectedIndex,

                label = ""
            )

            /*
             * SLIDING ACTIVE PILL
             */
            Box(

                modifier = Modifier
                    .offset(x = animatedOffset)
                    .width(tabWidth)
                    .fillMaxHeight()
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(

                        Brush.horizontalGradient(

                            listOf(

                                Gold.copy(alpha = 0.22f),

                                Gold.copy(alpha = 0.14f)
                            )
                        )
                    )
            )

            /*
             * TAB ROW
             */
            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                tabs.forEach { tab ->

                    val selected =
                        selectedTab == tab

                    val contentColor by animateColorAsState(

                        targetValue =

                            if (selected)
                                Gold
                            else
                                TextPrimary.copy(alpha = 0.60f),

                        label = ""
                    )

                    val icon = when (tab) {

                        ExperienceTab.Read ->
                            Icons.Default.MenuBook

                        ExperienceTab.Listen ->

                            if (playbackActive)
                                Icons.Default.GraphicEq
                            else
                                Icons.Default.PlayCircleFilled

                        ExperienceTab.Bookmarks ->
                            Icons.Default.Bookmark
                    }

                    Box(

                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(100.dp))
                            .clickable {
                                onTabSelected(tab)
                            },

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Row(

                            horizontalArrangement =
                                Arrangement.Center,

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(

                                imageVector = icon,

                                contentDescription = null,

                                tint = contentColor,

                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(
                                Modifier.width(6.dp)
                            )

                            Text(

                                text = tab.label.translate(displayLanguage),

                                color = contentColor,

                                fontSize = 12.sp,

                                fontWeight =

                                    if (selected)
                                        FontWeight.SemiBold
                                    else
                                        FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SegmentJumpBar(

    segments: List<BaniSegment>,

    currentSegmentIndex: Int,

    listState: LazyListState,

    displayLanguage: DisplayLanguage,

    onJumpToIndex: (Int) -> Unit
) {

    val activeContainer = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Accent.copy(alpha = 0.14f)
        }

        ThemeMode.Dark -> {
            Gold.copy(alpha = 0.14f)
        }

        ThemeMode.Amoled -> {
            Gold.copy(alpha = 0.12f)
        }
    }

    val inactiveContainer = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Color.Black.copy(alpha = 0.04f)
        }

        ThemeMode.Dark -> {
            Color.White.copy(alpha = 0.05f)
        }

        ThemeMode.Amoled -> {
            Color.White.copy(alpha = 0.03f)
        }
    }

    val activeTextColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Accent
        }

        else -> {
            Gold
        }
    }

    val inactiveTextColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Color(0xFF666666)
        }

        ThemeMode.Dark -> {
            Color.White.copy(alpha = 0.52f)
        }

        ThemeMode.Amoled -> {
            Color.White.copy(alpha = 0.44f)
        }
    }

    LazyRow(

        state = listState,

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 2.dp
            ),

        horizontalArrangement =
            Arrangement.spacedBy(10.dp),

        contentPadding =
            PaddingValues(horizontal = 6.dp)
    ) {

        itemsIndexed(segments) { groupIdx, segment ->

            val isActive =
                currentSegmentIndex == groupIdx

            val animatedContainer by animateColorAsState(

                targetValue =

                    if (isActive)
                        activeContainer
                    else
                        inactiveContainer,

                label = ""
            )

            val animatedTextColor by animateColorAsState(

                targetValue =

                    if (isActive)
                        activeTextColor
                    else
                        inactiveTextColor,

                label = ""
            )

            val animatedScale by animateFloatAsState(

                targetValue =

                    if (isActive)
                        1f
                    else
                        0.94f,

                label = ""
            )

            Surface(

                modifier = Modifier
                    .graphicsLayer {

                        scaleX = animatedScale
                        scaleY = animatedScale
                    }
                    .combinedClickable(

                        onClick = {

                            onJumpToIndex(
                                segment.startIndex
                            )
                        },

                        onDoubleClick = {

                            onJumpToIndex(0)
                        }
                    ),

                shape = RoundedCornerShape(18.dp),

                color = animatedContainer,

                border = BorderStroke(

                    1.dp,

                    if (isActive) {

                        activeTextColor.copy(
                            alpha = 0.16f
                        )

                    } else {

                        Color.Transparent
                    }
                ),

                tonalElevation =

                    if (isActive)
                        2.dp
                    else
                        0.dp
            ) {

                Row(

                    modifier = Modifier
                        .padding(
                            horizontal =

                                if (isActive)
                                    16.dp
                                else
                                    13.dp,

                            vertical = 5.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    /*
                     * ACTIVE DOT
                     */
                    AnimatedVisibility(
                        visible = isActive
                    ) {

                        Box(

                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(activeTextColor)
                        )
                    }

                    Text(

                        text = remember(segment.label, displayLanguage) {
                            val parts = segment.label.split(" ")
                            if (parts.size == 2) {
                                val prefix = parts[0].translate(displayLanguage)
                                "$prefix ${parts[1]}".translateDigits(displayLanguage)
                            } else {
                                segment.label.translate(displayLanguage).translateDigits(displayLanguage)
                            }
                        },

                        fontSize = 12.sp,

                        fontWeight =

                            if (isActive)
                                FontWeight.SemiBold
                            else
                                FontWeight.Medium,

                        color = animatedTextColor
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FloatingJumpButton(

    icon: androidx.compose.ui.graphics.vector.ImageVector,

    onClick: () -> Unit,

    onDoubleTap: () -> Unit
) {

    Surface(

        modifier = Modifier
            .size(42.dp)
            .combinedClickable(

                onClick = onClick,

                onDoubleClick = onDoubleTap
            ),

        shape = CircleShape,

        color = SurfaceColor,

        tonalElevation = 8.dp,

        shadowElevation = 8.dp,

        border = BorderStroke(
            1.dp,
            Gold.copy(alpha = 0.14f)
        )
    ) {

        Box(
            contentAlignment =
                Alignment.Center
        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = Gold
            )
        }
    }
}