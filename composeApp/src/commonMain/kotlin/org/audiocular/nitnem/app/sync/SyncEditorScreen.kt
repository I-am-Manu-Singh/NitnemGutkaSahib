package org.audiocular.nitnem.app.sync

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.audiocular.nitnem.app.audio.StreamAudioController
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.LineTiming
import org.audiocular.nitnem.models.AudioTrack
import org.audiocular.nitnem.data.generated.GeneratedBaniCatalog
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateHindiTransliteration
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import java.io.File
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun SyncEditorScreen(
    context: Context,
    bani: Bani,
    audioController: StreamAudioController,
    displayLanguage: DisplayLanguage = DisplayLanguage.English,
    initialTrackId: String? = null,
    onBack: () -> Unit = {}
) {

    val playback by
        audioController.playback.collectAsState()

    val timings =
        remember {
            mutableStateListOf<LineTiming>()
        }
    val scope =
        rememberCoroutineScope()
    val listState =
        rememberLazyListState()

    var currentIndex by remember {
        mutableStateOf(0)
    }

    var selectedBani by remember {
        mutableStateOf(bani)
    }

    val tracks = remember(selectedBani) {
        val list = mutableListOf<AudioTrack>()
        selectedBani.audio?.let { defaultAudio ->
            list.add(
                AudioTrack(
                    id = "default",
                    title = defaultAudio.title.ifBlank { "Default Track" },
                    artist = defaultAudio.artist.ifBlank { "Default Artist" },
                    durationMs = defaultAudio.durationMs,
                    license = defaultAudio.license,
                    attribution = defaultAudio.attribution,
                    remoteUrl = defaultAudio.remoteUrl,
                    localPath = defaultAudio.localPath
                )
            )
        } ?: run {
            list.add(
                AudioTrack(
                    id = "default",
                    title = "Default Track",
                    artist = "Default Artist",
                    durationMs = 0L,
                    license = "",
                    attribution = ""
                )
            )
        }
        list.addAll(audioController.getCustomTracks(selectedBani.id))
        list
    }

    var selectedTrack by remember(selectedBani) {
        val initialTrack = if (initialTrackId != null) {
            tracks.firstOrNull { it.id == initialTrackId } ?: tracks.first()
        } else {
            tracks.first()
        }
        mutableStateOf(initialTrack)
    }

    LaunchedEffect(selectedBani, selectedTrack) {
        val loaded = TimingLoader.loadTimings(context, selectedBani.id, selectedTrack.id)
        timings.clear()
        timings.addAll(loaded)
        currentIndex = 0

        val url = if (selectedTrack.id != "default") {
            selectedTrack.localPath ?: selectedTrack.remoteUrl
        } else {
            audioController.getLocalAudioPath(selectedBani.id) ?: selectedTrack.remoteUrl
        }
        if (!url.isNullOrBlank()) {
            audioController.prepare(url = url, title = selectedBani.title)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audioController.pause()
        }
    }

    var showBaniDropdown by remember { mutableStateOf(false) }
    var showTrackDropdown by remember { mutableStateOf(false) }
    var showTutorialSheet by remember { mutableStateOf(false) }

    val gradient = when (AppTheme.themeMode) {
        ThemeMode.Light -> {
            listOf(
                Background,
                SurfaceVariant
            )
        }
        ThemeMode.Dark -> {
            listOf(
                Background,
                SurfaceColor
            )
        }
        ThemeMode.Amoled -> {
            listOf(
                Background,
                Color.Black
            )
        }
    }

    val cardColor = SurfaceColor
    val cardSecondary = SurfaceVariant
    val textPrimary = TextPrimary
    val textSecondary = TextSecondary
    val accent = Accent
    val accentSoft = AccentSoft
    val accentBorder = AccentBorder
    val success = Color(0xFF46D39A)
    val error = Color(0xFFFF7070)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    gradient
                )
            )
            .padding(
                horizontal = 14.dp,
                vertical = 10.dp
            )
    ) {

        /*
         * BANI & TRACK SELECTORS
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Bani Dropdown Card
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    onClick = { showBaniDropdown = true },
                    shape = RoundedCornerShape(16.dp),
                    color = cardColor,
                    border = BorderStroke(1.dp, accentBorder),
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Bani".translate(displayLanguage),
                                color = textSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = selectedBani.title.translate(displayLanguage),
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = accent
                        )
                    }
                }

                DropdownMenu(
                    expanded = showBaniDropdown,
                    onDismissRequest = { showBaniDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.45f)
                ) {
                    GeneratedBaniCatalog.banis.forEach { b ->
                        DropdownMenuItem(
                            text = { Text(b.title.translate(displayLanguage)) },
                            onClick = {
                                selectedBani = b
                                showBaniDropdown = false
                            }
                        )
                    }
                }
            }

            // Track Dropdown Card
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    onClick = { showTrackDropdown = true },
                    shape = RoundedCornerShape(16.dp),
                    color = cardColor,
                    border = BorderStroke(1.dp, accentBorder),
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Audio Track".translate(displayLanguage),
                                color = textSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (selectedTrack.id == "default") {
                                    "Default".translate(displayLanguage)
                                } else {
                                    selectedTrack.artist
                                },
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = accent
                        )
                    }
                }

                DropdownMenu(
                    expanded = showTrackDropdown,
                    onDismissRequest = { showTrackDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.45f)
                ) {
                    tracks.forEach { track ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (track.id == "default") {
                                        "Default (${track.artist})"
                                    } else {
                                        track.artist
                                    }
                                )
                            },
                            onClick = {
                                selectedTrack = track
                                showTrackDropdown = false
                            }
                        )
                    }
                }
            }
        }

        /*
         * TOP PLAYER BAR
         */

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            color = cardColor,
            border = BorderStroke(
                1.dp,
                accentBorder
            ),
            tonalElevation = 2.dp,
            shadowElevation = if (AppTheme.themeMode == ThemeMode.Light) {
                4.dp
            } else {
                0.dp
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                /*
                 * HEADER ROW
                 */

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Sync Studio".translate(displayLanguage),
                                color = accent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.width(6.dp))
                            IconButton(
                                onClick = { showTutorialSheet = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Sync Tutorial",
                                    tint = accent.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(2.dp))

                        Text(
                            text = formatTime(playback.positionMs),
                            color = textSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        CompactHeaderButton(
                            icon = if (playback.isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                            onClick = {
                                audioController.playPause()
                            }
                        )

                        CompactHeaderButton(
                            icon = Icons.Default.FastRewind,
                            onClick = {
                                audioController.seekBack(2000)
                            }
                        )

                        CompactHeaderButton(
                            icon = Icons.Default.Undo,
                            onClick = {
                                if (timings.isNotEmpty()) {
                                    timings.removeLast()
                                    currentIndex = (currentIndex - 1).coerceAtLeast(0)
                                }
                            }
                        )

                        CompactHeaderButton(
                            icon = Icons.Default.Save,
                            onClick = {
                                try {
                                    if (timings.isNotEmpty()) {
                                        val last = timings.last()
                                        timings[timings.lastIndex] = last.copy(
                                            endsAtMs = playback.durationMs
                                        )
                                    }

                                    val json = TimingExporter.export(timings)
                                    val audioDir = File(context.filesDir, "audio").apply {
                                        if (!exists()) {
                                            mkdirs()
                                        }
                                    }
                                    val filename = if (selectedTrack.id != "default") {
                                        "${selectedBani.id}_${selectedTrack.id}_timings.json"
                                    } else {
                                        "${selectedBani.id}_timings.json"
                                    }
                                    val file = File(audioDir, filename)
                                    file.writeText(json)

                                    Toast.makeText(
                                        context,
                                        "Saved".translate(displayLanguage),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Save failed".translate(displayLanguage),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )

                        SpeedSelectorButton(
                            currentSpeed = playback.speed,
                            onSpeedSelected = { speed ->
                                audioController.updatePlaybackSpeed(speed)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                /*
                 * PROGRESS BAR
                 */

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            accent.copy(alpha = 0.08f)
                        )
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(
                                if (playback.durationMs <= 0L) {
                                    0f
                                } else {
                                    playback.positionMs.toFloat() / playback.durationMs.toFloat()
                                }
                            )
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Gold,
                                        accent
                                    )
                                )
                            )
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        /*
         * LINE LIST
         */

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(
                bottom = 120.dp
            )
        ) {

            itemsIndexed(
                items = selectedBani.lines,
                key = { _, line ->
                    line.id
                }
            ) { index, line ->

                val timing = timings.firstOrNull {
                    it.id == line.id
                }

                val isActive = index == currentIndex

                val background by animateColorAsState(
                    targetValue = when {
                        isActive -> {
                            accentSoft
                        }
                        else -> {
                            cardColor
                        }
                    },
                    label = ""
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = {
                                val currentMs = playback.positionMs
                                timings.removeAll {
                                    it.id == line.id
                                }

                                if (timings.isNotEmpty()) {
                                    val previous = timings.last()
                                    timings[timings.lastIndex] = previous.copy(
                                        endsAtMs = currentMs
                                    )
                                }

                                timings.add(
                                    LineTiming(
                                        id = line.id,
                                        startsAtMs = currentMs,
                                        endsAtMs = currentMs + 1000
                                    )
                                )

                                val nextIndex = (index + 1).coerceAtMost(
                                    selectedBani.lines.lastIndex
                                )

                                currentIndex = nextIndex

                                scope.launch {
                                    delay(120L)
                                    listState.animateScrollToItem(
                                        index = nextIndex,
                                        scrollOffset = -220
                                    )
                                }
                            },
                            onLongClick = {
                                timings.removeAll {
                                    it.id == line.id
                                }
                                currentIndex = index
                            }
                        ),
                    shape = RoundedCornerShape(24.dp),
                    color = background,
                    border = BorderStroke(
                        width = if (isActive) {
                            1.5.dp
                        } else {
                            1.dp
                        },
                        color = if (isActive) {
                            accent.copy(alpha = 0.22f)
                        } else {
                            accentBorder
                        }
                    ),
                    tonalElevation = if (isActive) {
                        6.dp
                    } else {
                        0.dp
                    }
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {

                        /*
                         * HEADER
                         */

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Surface(
                                shape = CircleShape,
                                color = accentSoft
                            ) {

                                Box(
                                    modifier = Modifier.size(30.dp),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        text = "${index + 1}",
                                        color = accent,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = if (timing != null) {
                                        formatTime(
                                            timing.startsAtMs
                                        )
                                    } else {
                                        "Not Synced".translate(displayLanguage)
                                    },
                                    color = if (timing != null) {
                                        accent
                                    } else {
                                        textSecondary
                                    },
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )

                                AnimatedVisibility(
                                    visible = isActive
                                ) {

                                    Text(
                                        text = "LIVE".translate(displayLanguage),
                                        color = success,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        /*
                         * GURMUKHI
                         */

                        Text(
                            text = line.gurmukhi,
                            color = textPrimary,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 36.sp,
                            fontSize = 25.sp
                        )

                        if (line.hindi.isNotBlank()) {

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = line.hindi.translateHindiTransliteration(),
                                color = accent,
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }
    }

    if (showTutorialSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTutorialSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 4.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "How to Sync Gurbani".translate(displayLanguage),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = "Follow these simple steps to synchronize audio recitation with Gurbani lines:".translate(displayLanguage),
                    color = textSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Step 1
                TutorialStepRow(
                    stepNumber = 1,
                    title = "Select Bani & Track".translate(displayLanguage),
                    description = "Choose the Gurbani and specific artist track from the dropdown selectors at the top of the Sync Studio.".translate(displayLanguage),
                    accent = accent,
                    accentSoft = accentSoft,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                Spacer(Modifier.height(12.dp))

                // Step 2
                TutorialStepRow(
                    stepNumber = 2,
                    title = "Play & Listen".translate(displayLanguage),
                    description = "Tap the Play button to start the audio recitation. Listen along closely.".translate(displayLanguage),
                    accent = accent,
                    accentSoft = accentSoft,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                Spacer(Modifier.height(12.dp))

                // Step 3
                TutorialStepRow(
                    stepNumber = 3,
                    title = "Tap to Highlight".translate(displayLanguage),
                    description = "Tap each Gurbani line card exactly at the moment the reciter starts reading that specific line. This logs the start time.".translate(displayLanguage),
                    accent = accent,
                    accentSoft = accentSoft,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                Spacer(Modifier.height(12.dp))

                // Step 4
                TutorialStepRow(
                    stepNumber = 4,
                    title = "Adjust / Undo".translate(displayLanguage),
                    description = "Missed a beat? Use the Rewind button (-2s) to go back, or tap the Undo button to delete the last timing. Long-press any card to clear its timing and jump back to editing it.".translate(displayLanguage),
                    accent = accent,
                    accentSoft = accentSoft,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                Spacer(Modifier.height(12.dp))

                // Step 5
                TutorialStepRow(
                    stepNumber = 5,
                    title = "Save Your Work".translate(displayLanguage),
                    description = "Tap the Save button (💾) when finished. This saves your timing file, which will now automatically highlight text when playing this track in the Gurbani Reader!".translate(displayLanguage),
                    accent = accent,
                    accentSoft = accentSoft,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }
        }
    }
}

@Composable
private fun TutorialStepRow(
    stepNumber: Int,
    title: String,
    description: String,
    accent: Color,
    accentSoft: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accentSoft.copy(alpha = 0.3f))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = accent,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = description,
                color = textSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun PremiumPlayerBar(
    playbackPosition: Long,
    playbackDuration: Long,
    isPlaying: Boolean,
    displayLanguage: DisplayLanguage,
    onPlayPause: () -> Unit,
    onSeekBack: () -> Unit,
    onUndo: () -> Unit,
    onSave: () -> Unit
) {

    val cardColor = when (
        AppTheme.themeMode
    ) {

        ThemeMode.Light -> {
            Color.White
        }

        ThemeMode.Dark -> {
            Color(0xFF1B1B1B)
        }

        ThemeMode.Amoled -> {
            Color(0xFF101010)
        }
    }

    Surface(

        shape =
            RoundedCornerShape(28.dp),

        color = cardColor,

        tonalElevation = 10.dp
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(

                    text =
                        formatTime(
                            playbackPosition
                        ),

                    color = Gold,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 24.sp
                )

                Spacer(
                    Modifier.weight(1f)
                )

                Text(

                    text =
                        formatTime(
                            playbackDuration
                        ),

                    color =
                        Gold.copy(alpha = 0.72f),

                    fontSize = 14.sp
                )
            }

            Spacer(
                Modifier.height(18.dp)
            )

            Row(

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                SyncActionButton(

                    icon =

                        if (isPlaying)
                            Icons.Default.Pause
                        else
                            Icons.Default.PlayArrow,

                    text =

                        if (isPlaying)
                            "Pause".translate(displayLanguage)
                        else
                            "Play".translate(displayLanguage),

                    onClick =
                        onPlayPause
                )

                SyncActionButton(

                    icon =
                        Icons.Default.FastRewind,

                    text = "-2s",

                    onClick =
                        onSeekBack
                )

                SyncActionButton(

                    icon =
                        Icons.Default.Undo,

                    text = "Undo".translate(displayLanguage),

                    onClick =
                        onUndo
                )

                SyncActionButton(

                    icon =
                        Icons.Default.Save,

                    text = "Save".translate(displayLanguage),

                    onClick =
                        onSave
                )
            }
        }
    }
}

@Composable
private fun SyncActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {

    Surface(

        onClick = onClick,

        shape =
            RoundedCornerShape(18.dp),

        color =
            Gold.copy(alpha = 0.12f)
    ) {

        Row(

            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = Gold,

                modifier =
                    Modifier.size(18.dp)
            )

            Text(

                text = text,

                color = Gold,

                fontWeight =
                    FontWeight.SemiBold,

                fontSize = 13.sp
            )
        }
    }
}

private fun formatTime(
    ms: Long
): String {

    val totalSeconds =
        ms / 1000

    val minutes =
        totalSeconds / 60

    val seconds =
        totalSeconds % 60

    return "%02d:%02d".format(
        minutes,
        seconds
    )
}
@Composable
private fun CompactHeaderButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {

    Surface(

        onClick = onClick,

        shape = CircleShape,

        color =
            Gold.copy(alpha = 0.12f)
    ) {

        Box(

            modifier = Modifier
                .size(42.dp),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = Gold,

                modifier =
                    Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SpeedSelectorButton(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            onClick = { expanded = true },
            shape = CircleShape,
            color = Gold.copy(alpha = 0.12f)
        ) {
            Box(
                modifier = Modifier.size(42.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${currentSpeed}x",
                    color = Gold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                DropdownMenuItem(
                    text = { Text("${speed}x") },
                    onClick = {
                        onSpeedSelected(speed)
                        expanded = false
                    }
                )
            }
        }
    }
}