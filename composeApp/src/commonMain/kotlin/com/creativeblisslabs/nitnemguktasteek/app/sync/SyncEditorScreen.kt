package com.creativeblisslabs.nitnemguktasteek.app.sync
import com.creativeblisslabs.nitnemguktasteek.data.CuratedAudioCatalog
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import com.creativeblisslabs.nitnemguktasteek.utils.timingKey
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Share
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.creativeblisslabs.nitnemguktasteek.utils.timingKey
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
import com.creativeblisslabs.nitnemguktasteek.audio.StreamAudioController
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.LineTiming
import com.creativeblisslabs.nitnemguktasteek.data.generated.GeneratedBaniCatalog
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.utils.translateHindiTransliteration
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Sync
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Lightbulb


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
    val tracks = remember(
        selectedBani.id,
        playback.currentTitle
    ) {

        buildList {

            selectedBani.audio?.let {

                add(
                    it.copy(
                        id = "default"
                    )
                )
            }

            addAll(
                CuratedAudioCatalog
                    .getTracksForBani(
                        selectedBani.id
                    )
            )

            addAll(
                com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog
                    .getTracksForBani(
                        selectedBani.id
                    ).map { it.toAudioTrack() }
            )

            addAll(
                audioController
                    .getCustomTracks(
                        selectedBani.id
                    )
            )
        }.distinctBy {
            it.id
        }
    }
    var selectedTrack by remember(
        selectedBani.id,
        tracks
    ) {

        mutableStateOf(

            tracks.firstOrNull {
                it.id == initialTrackId
            } ?: tracks.firstOrNull()
        )
    }

    val activeTrack =
        selectedTrack ?: return
LaunchedEffect(
    selectedBani.id,
    selectedTrack?.id
) {

    val loaded = TimingLoader.loadTimings(
        context,
        selectedTrack?.timingKey()
            ?: selectedBani.id
    )
    timings.clear()
    timings.addAll(loaded)

    currentIndex = 0

   scope.launch {
    listState.scrollToItem(0)
}
    val localDownloaded =

        audioController.getLocalAudioPath(
            selectedBani.id,
            activeTrack.id
        )

    val url = when {

        !localDownloaded.isNullOrBlank() -> {

            localDownloaded
        }

        activeTrack.localPath?.isNotBlank() == true -> {

            activeTrack.localPath
        }

        activeTrack.remoteUrl?.isNotBlank() == true -> {

            activeTrack.remoteUrl
        }

        else -> null
    }

    if (!url.isNullOrBlank()) {

        audioController.prepare(
            url = url,
            title = selectedBani.title
        )
    }
}

    DisposableEffect(Unit) {
        onDispose {
            audioController.stop()
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
val timingMap = remember(timings.toList()) {
    timings.associateBy { it.id }
}
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
 * TOP SECTION
 */
        Column(

            modifier = Modifier.fillMaxWidth(),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            /*
             * HEADER ROW
             */

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                /*
                 * BACK BUTTON
                 */

                Surface(

                    onClick = onBack,

                    shape = CircleShape,

                    color = accent.copy(alpha = 0.10f),

                    border = BorderStroke(
                        1.dp,
                        accent.copy(alpha = 0.14f)
                    )
                ) {

                    Box(

                        modifier = Modifier
                            .size(34.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Back",

                            tint = accent,

                            modifier =
                                Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(
                    Modifier.width(12.dp)
                )

                /*
                 * TITLE + TIMING
                 */

                Column(

                    modifier =
                        Modifier.weight(1f),

                    verticalArrangement =
                        Arrangement.Center
                ) {

                    Text(

                        text =
                            "Sync Studio"
                                .translate(displayLanguage),

                        color =
                            textPrimary,

                        fontSize = 17.sp,

                        fontWeight =
                            FontWeight.Bold,

                        maxLines = 1
                    )

                    Spacer(
                        Modifier.height(1.dp)
                    )

                    Text(

                        text =
                            formatTime(
                                playback.positionMs
                            ),

                        color =
                            textSecondary,

                        fontSize = 11.sp
                    )
                }

                /*
                 * INFO BUTTON
                 */

                Surface(

                    onClick = {
                        showTutorialSheet = true
                    },

                    shape = CircleShape,

                    color = accent.copy(alpha = 0.10f),

                    border = BorderStroke(
                        1.dp,
                        accent.copy(alpha = 0.14f)
                    )
                ) {

                    Box(

                        modifier = Modifier
                            .size(34.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.Info,

                            contentDescription =
                                "Sync Tutorial",

                            tint =
                                accent.copy(alpha = 0.82f),

                            modifier =
                                Modifier.size(18.dp)
                        )
                    }
                }
            }

            /*
             * ACTIONS ROW
             */

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                CompactHeaderButton(

                    icon = if (
                        playback.isPlaying
                    ) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow
                    },

                    onClick = {
                        audioController.playPause()
                    }
                )

                CompactHeaderButton(

                    icon =
                        Icons.Default.FastRewind,

                    onClick = {
                        audioController.seekBack(2000)
                    }
                )

                CompactHeaderButton(

                    icon =
                        Icons.Default.Undo,

                    onClick = {

                        val lastTiming =
                            timings.lastOrNull()
                                ?: return@CompactHeaderButton

                        timings.remove(lastTiming)

                        val targetIndex =

                            selectedBani.lines
                                .indexOfFirst {
                                    it.id == lastTiming.id
                                }
                                .coerceAtLeast(0)

                        currentIndex = targetIndex

                        scope.launch {

                            listState.animateScrollToItem(
                                targetIndex
                            )
                        }
                    }
                )

                CompactHeaderButton(

                    icon =
                        Icons.Default.Save,

                    onClick = {

                        try {

                            if (timings.isNotEmpty()) {

                                val last =
                                    timings.last()

                                val safeEnd =

                                    playback.positionMs
                                        .coerceAtLeast(
                                            last.startsAtMs
                                        )

                                timings[timings.lastIndex] =
                                    last.copy(
                                        endsAtMs = safeEnd
                                    )
                            }

                            val sortedTimings =

                                timings.sortedBy {
                                    it.startsAtMs
                                }

                            val normalizedTimings =

                                sortedTimings.mapIndexed { index, timing ->

                                    val next =
                                        sortedTimings.getOrNull(index + 1)

                                    timing.copy(

                                        endsAtMs =
                                            next?.startsAtMs
                                                ?: timing.endsAtMs
                                    )
                                }

                            val json =
                                TimingExporter.export(
                                    normalizedTimings
                                )

                            val audioDir =
                                File(
                                    context.filesDir,
                                    "audio"
                                ).apply {

                                    if (!exists()) {
                                        mkdirs()
                                    }
                                }

                            val filename =
                                "${selectedTrack?.timingKey()}_timing.json"

                            val file =
                                File(audioDir, filename)

                            file.writeText(json)

                            Toast.makeText(

                                context,

                                "Saved"
                                    .translate(displayLanguage),

                                Toast.LENGTH_SHORT

                            ).show()

                        } catch (e: Exception) {

                            Toast.makeText(

                                context,

                                "Save failed"
                                    .translate(displayLanguage),

                                Toast.LENGTH_SHORT

                            ).show()
                        }
                    }
                )

                CompactHeaderButton(

                    icon =
                        Icons.Default.Share,

                    onClick = {

                        try {

                            val filename =
                                "${selectedTrack?.timingKey()}_timing.json"

                            val audioDir =
                                File(
                                    context.filesDir,
                                    "audio"
                                )

                            val file =
                                File(audioDir, filename)

                            val fileToShare = if (file.exists() && file.length() > 0) {
                                file
                            } else {
                                // Extract bundled asset timing to cacheDir for sharing
                                val assetPath = "timing/$filename"
                                val cacheFile = File(context.cacheDir, filename)
                                try {
                                    context.assets.open(assetPath).use { input ->
                                        cacheFile.outputStream().use { output ->
                                            input.copyTo(output)
                                        }
                                    }
                                    if (cacheFile.exists() && cacheFile.length() > 0) cacheFile else null
                                } catch (_: Exception) {
                                    null
                                }
                            }

                            if (fileToShare != null) {
                                val authority = "${context.packageName}.fileprovider"
                                val uri = androidx.core.content.FileProvider.getUriForFile(
                                    context,
                                    authority,
                                    fileToShare
                                )

                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_SEND
                                ).apply {
                                    type = "application/json"
                                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                    putExtra(android.content.Intent.EXTRA_SUBJECT, "$filename - ${selectedBani.title}")
                                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }

                                context.startActivity(
                                    android.content.Intent.createChooser(
                                        intent,
                                        "Share Gurbani Timings".translate(displayLanguage)
                                    )
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please save the timing file first"
                                        .translate(displayLanguage),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: Exception) {

                            e.printStackTrace()

                            Toast.makeText(

                                context,

                                "Sharing failed"
                                    .translate(displayLanguage),

                                Toast.LENGTH_SHORT

                            ).show()
                        }
                    }
                )

                Spacer(
                    Modifier.weight(1f)
                )

                SpeedSelectorButton(

                    currentSpeed =
                        playback.speed,

                    onSpeedSelected = { speed ->

                        audioController
                            .updatePlaybackSpeed(speed)
                    }
                )
            }

            /*
             * PROGRESS BAR
             */

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(
                        RoundedCornerShape(50)
                    )
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

                                (
                                        playback.positionMs.toFloat() /
                                                playback.durationMs.toFloat()
                                        ).coerceIn(0f, 1f)
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

        Spacer(Modifier.height(12.dp))
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
                            if (activeTrack.id.endsWith("_default")) {
                                "Default".translate(displayLanguage)
                            } else {
                                activeTrack.title.ifBlank {
                                    selectedTrack?.artist
                                }
                            }?.let {
                                Text(
                                    text = it,
                                    color = textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
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
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    tracks.forEach { track ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when {
                                        track.id == "default" -> "Default (${track.artist})"
                                        track.id.contains("_custom_") -> "${track.artist} (Custom Track)"
                                        else -> track.title.ifBlank { track.artist }
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

        // Matched Audio & Timing File Info Banner
        val audioFileNameDisplay = selectedTrack?.localPath?.substringAfterLast('/')
            ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById(selectedTrack?.id ?: "")?.audioFileName
            ?: "${selectedTrack?.id}.mp3"
        val timingFileNameDisplay = "${selectedTrack?.timingKey()}_timing.json"

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = cardSecondary.copy(alpha = 0.5f),
            border = BorderStroke(0.8.dp, accent.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = accent, modifier = Modifier.size(12.dp))
                    Text(
                        text = audioFileNameDisplay,
                        fontSize = 10.5.sp,
                        color = textPrimary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("➔", fontSize = 10.sp, color = accent)
                    Icon(Icons.Default.Sync, contentDescription = null, tint = Gold, modifier = Modifier.size(12.dp))
                    Text(
                        text = timingFileNameDisplay,
                        fontSize = 10.5.sp,
                        color = Gold,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (timings.isNotEmpty()) success.copy(alpha = 0.15f) else Gold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (timings.isNotEmpty()) "${timings.size} synced" else "New Timing",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (timings.isNotEmpty()) success else Gold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))

        /*
         * LINE LIST
         */

        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
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

              val timing = timingMap[line.id]

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
                                val existingIndex = timings.indexOfFirst {
                                    it.id == line.id
                                }

                                if (existingIndex >= 0) {
                                    while (
                                        timings.size > existingIndex + 1
                                    ) {
                                        timings.removeAt(timings.lastIndex) // Safe cross-Android implementation
                                    }

                                    timings.removeAt(existingIndex)
                                }

                                if (timings.isNotEmpty()) {
                                    val previous = timings.last()
                                    timings[timings.lastIndex] = previous.copy(
                                        endsAtMs =
                                        currentMs.coerceAtLeast(
                                            previous.startsAtMs + 50L
                                        )
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
                               if (nextIndex < selectedBani.lines.size) {
    listState.animateScrollToItem(
        index = nextIndex,
        scrollOffset = -220
    )
}
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

            onDismissRequest = {
                showTutorialSheet = false
            },

            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),

            containerColor = SurfaceColor,

            dragHandle = {

                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .width(42.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(
                            Accent.copy(alpha = 0.22f)
                        )
                )
            }
        ) {

            LazyColumn(

                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),

                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 4.dp,
                    bottom = 32.dp
                ),

                verticalArrangement =
                    Arrangement.spacedBy(14.dp)
            ) {

                item {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(

                            shape = CircleShape,

                            color = AccentSoft
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(42.dp),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(

                                    imageVector =
                                        Icons.Default.GraphicEq,

                                    contentDescription = null,

                                    tint = Accent
                                )
                            }
                        }

                        Spacer(
                            Modifier.width(14.dp)
                        )

                        Column {

                            Text(

                                text =
                                    "Sync Studio Guide"
                                        .translate(displayLanguage),

                                color = Accent,

                                fontWeight = FontWeight.Bold,

                                fontSize = 20.sp
                            )

                            Text(

                                text =
                                    "Create perfectly synced Gurbani timings"
                                        .translate(displayLanguage),

                                color = TextSecondary,

                                fontSize = 12.sp
                            )
                        }
                    }
                }

                item {

                    Surface(

                        shape = RoundedCornerShape(22.dp),

                        color =
                            Accent.copy(alpha = 0.06f),

                        border = BorderStroke(
                            1.dp,
                            Accent.copy(alpha = 0.12f)
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),

                            verticalArrangement =
                                Arrangement.spacedBy(16.dp)
                        ) {

                            TutorialStepRow(
                                stepNumber = 1,
                                title = "Select Bani & Audio".translate(displayLanguage),
                                description = "Choose the Gurbani and recitation track you want to synchronize.".translate(displayLanguage),
                                accent = Accent,
                                accentSoft = AccentSoft,
                                textPrimary = TextPrimary,
                                textSecondary = TextSecondary
                            )

                            TutorialStepRow(
                                stepNumber = 2,
                                title = "Play the Recitation".translate(displayLanguage),
                                description = "Start playback and carefully listen to the reciter.".translate(displayLanguage),
                                accent = Accent,
                                accentSoft = AccentSoft,
                                textPrimary = TextPrimary,
                                textSecondary = TextSecondary
                            )

                            TutorialStepRow(
                                stepNumber = 3,
                                title = "Tap Each Gurbani Line".translate(displayLanguage),
                                description = "Tap a line exactly when the reciter begins reading it.".translate(displayLanguage),
                                accent = Accent,
                                accentSoft = AccentSoft,
                                textPrimary = TextPrimary,
                                textSecondary = TextSecondary
                            )

                            TutorialStepRow(
                                stepNumber = 4,
                                title = "Fix Mistakes".translate(displayLanguage),
                                description = "Use Undo or long-press a line to remove incorrect timings.".translate(displayLanguage),
                                accent = Accent,
                                accentSoft = AccentSoft,
                                textPrimary = TextPrimary,
                                textSecondary = TextSecondary
                            )

                            TutorialStepRow(
                                stepNumber = 5,
                                title = "Save Timing File".translate(displayLanguage),
                                description = "Your synced timing file will automatically power live highlighting in Gurbani Experience mode.".translate(displayLanguage),
                                accent = Accent,
                                accentSoft = AccentSoft,
                                textPrimary = TextPrimary,
                                textSecondary = TextSecondary
                            )
                        }
                    }
                }

                item {

                    Surface(

                        shape = RoundedCornerShape(18.dp),

                        color =
                            Gold.copy(alpha = 0.08f),

                        border = BorderStroke(
                            1.dp,
                            Gold.copy(alpha = 0.12f)
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Lightbulb,

                                contentDescription = null,

                                tint = Gold
                            )

                            Spacer(
                                Modifier.width(12.dp)
                            )

                            Text(

                                text =
                                    "Use headphones for precise synchronization."
                                        .translate(displayLanguage),

                                color = TextPrimary,

                                fontSize = 13.sp,

                                lineHeight = 20.sp
                            )
                        }
                    }
                }
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
            .background(Color.Transparent)
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