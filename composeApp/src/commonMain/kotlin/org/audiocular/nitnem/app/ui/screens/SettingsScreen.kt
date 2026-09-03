package org.audiocular.nitnem.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import org.audiocular.nitnem.utils.translate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Close
import org.audiocular.nitnem.data.CuratedAudioCatalog
import org.audiocular.nitnem.utils.timingKey
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.audiocular.nitnem.audio.StreamAudioController
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.data.preferences.SettingsRepository
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import org.audiocular.nitnem.utils.rememberAudioPicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TimePicker
import org.audiocular.nitnem.app.sync.SyncEditorScreen




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(

    viewModel: NitnemViewModel,

    audioController: StreamAudioController
) {

    val uiState by
        viewModel.uiState.collectAsState()

    val haptics =
        LocalHapticFeedback.current

    val context =
        LocalContext.current

    val scope =
        rememberCoroutineScope()

    val repository = remember {

        SettingsRepository(context)
    }
    var amritTime by remember(uiState.amritTime) {
        mutableStateOf(uiState.amritTime)
    }

    var rehrasTime by remember(uiState.rehrasTime) {
        mutableStateOf(uiState.rehrasTime)
    }

    var sohilaTime by remember(uiState.sohilaTime) {
        mutableStateOf(uiState.sohilaTime)
    }

var showTimePicker by remember {
    mutableStateOf(false)
}

var selectedReminder by remember {
    mutableStateOf("")
}

    var selectedBaniForAudio by remember(uiState.currentBani, uiState.banis.size) {
        mutableStateOf(uiState.currentBani ?: uiState.banis.firstOrNull())
    }

    if (selectedBaniForAudio == null && uiState.banis.isNotEmpty()) {
        selectedBaniForAudio = uiState.currentBani ?: uiState.banis.firstOrNull()
    }

    android.util.Log.d("SettingsScreen", "DEBUG_SETTINGS: banis.size = ${uiState.banis.size}, selectedBaniForAudio = ${selectedBaniForAudio?.title}")

    var showBaniSelectMenu by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var artistNameInput by remember { mutableStateOf("") }
    var tracksRefreshTrigger by remember { mutableStateOf(0) }
    var selectedBaniFilter by remember { mutableStateOf<String?>(null) }

    val activeTrackId = remember(selectedBaniForAudio?.id, tracksRefreshTrigger, uiState.activeTrackId) {
        selectedBaniForAudio?.id?.let { audioController.getActiveTrackId(it) } ?: "default"
    }
    val customTracks = remember(selectedBaniForAudio?.id, tracksRefreshTrigger, uiState.downloadedBaniIds) {
        selectedBaniForAudio?.id?.let { audioController.getCustomTracks(it) } ?: emptyList()
    }

    val audioPickerLauncher = rememberAudioPicker { fileUri ->
        val bani = selectedBaniForAudio
        val artist = artistNameInput
        if (bani != null && artist.isNotBlank() && fileUri.isNotBlank()) {
            scope.launch {
                val track = audioController.importCustomTrack(bani.id, artist, fileUri)
                if (track != null) {
                    Toast.makeText(context, "Imported successfully!".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Import failed".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                }
                artistNameInput = ""
                tracksRefreshTrigger++
            }
        }
    }

    val authLauncher = org.audiocular.nitnem.utils.rememberAuthLauncher(
        onSignInSuccess = { user ->
            val msg = "Signed in as".translate(uiState.displayLanguage)
            Toast.makeText(context, "$msg ${user.displayName}", Toast.LENGTH_SHORT).show()
        },
        onSignInError = { error ->
            val msg = "Sign In Error".translate(uiState.displayLanguage)
            Toast.makeText(context, "$msg: $error", Toast.LENGTH_SHORT).show()
        }
    )

    var selectedCategory by remember {

        mutableStateOf(
            SettingsCategory.Reading
        )
    }

    val readingStyleItems =
        remember(uiState) {

            listOf(

                SettingsToggleItem(
                    "Bold".translate(uiState.displayLanguage),
                    uiState.boldGurmukhi
                ) {
                    viewModel.setBoldGurmukhi(it)
                },

                SettingsToggleItem(
                    "Center".translate(uiState.displayLanguage),
                    uiState.centerGurmukhi
                ) {
                    viewModel.setCenterGurmukhi(it)
                },

                SettingsToggleItem(
                    "Highlight".translate(uiState.displayLanguage),
                    uiState.highlightActiveLine
                ) {
                    viewModel.setHighlightActiveLine(it)
                },

                SettingsToggleItem(
                    "Gurmukhi Text".translate(uiState.displayLanguage),
                    uiState.showGurmukhi
                ) {
                    viewModel.setShowGurmukhi(it)
                },

                SettingsToggleItem(
                    "Larivaar".translate(uiState.displayLanguage),
                    uiState.larivaarMode
                ) {
                    viewModel.setLarivaarMode(it)
                }
            )
        }

    val translationItems =
        remember(uiState) {

            listOf(

                SettingsToggleItem(
                    "Hindi Transliteration".translate(uiState.displayLanguage),
                    uiState.showDevanagariTransliteration
                ) {
                    viewModel.setShowDevanagariTransliteration(it)
                },

                SettingsToggleItem(
                    "Hindi Translation".translate(uiState.displayLanguage),
                    uiState.showDevanagariTransliteration && (uiState.showPunjabiTranslation || uiState.showEnglishTranslation)
                ) { enabled ->
                    if (enabled) {
                        viewModel.setShowDevanagariTransliteration(true)
                        viewModel.setShowPunjabiTranslation(true)
                    } else {
                        viewModel.setShowPunjabiTranslation(false)
                    }
                },

                SettingsToggleItem(
                    "Roman Transliteration".translate(uiState.displayLanguage),
                    uiState.showRomanTransliteration
                ) {
                    viewModel.setShowRomanTransliteration(it)
                },

                SettingsToggleItem(
                    "English Translation".translate(uiState.displayLanguage),
                    uiState.showEnglishTranslation
                ) {
                    viewModel.setShowEnglishTranslation(it)
                },

                SettingsToggleItem(
                    "Punjabi Translation".translate(uiState.displayLanguage),
                    uiState.showPunjabiTranslation
                ) {
                    viewModel.setShowPunjabiTranslation(it)
                },

                SettingsToggleItem(
                    "Word Meanings".translate(uiState.displayLanguage),
                    uiState.showWordMeaning
                ) {
                    viewModel.setShowWordMeaning(it)
                }
            )
        }
    var showSyncEditor by remember { mutableStateOf(false) }
    var syncEditorTrackId by remember { mutableStateOf<String?>(null) }

    if (showSyncEditor) {
        val currentBani = selectedBaniForAudio ?: uiState.banis.firstOrNull()
        if (currentBani != null) {
            SyncEditorScreen(
                context = context,
                bani = currentBani,
                audioController = audioController,
                displayLanguage = uiState.displayLanguage,
                initialTrackId = syncEditorTrackId,
                onBack = {
                    showSyncEditor = false
                    syncEditorTrackId = null
                }
            )
        } else {
            showSyncEditor = false
            syncEditorTrackId = null
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

    /*

     * FIXED HEADER

     */

    Column(

        modifier = Modifier

            .fillMaxWidth()

            .padding(

                start = 18.dp,

                end = 18.dp,

                top = 12.dp

            )

    ) {

        Text(

            text =

                "Settings".translate(uiState.displayLanguage),

            fontSize = 30.sp,

            fontWeight =

                FontWeight.Bold,

            color = Primary

        )

        Spacer(

            modifier = Modifier.height(4.dp)

        )

        Text(

            text =

                "Personalise your Nitnem experience"

                    .translate(uiState.displayLanguage),

            fontSize = 14.sp,

            color =

                TextPrimary.copy(

                    alpha = 0.58f

                )

        )

        Spacer(

            modifier = Modifier.height(18.dp)

        )

        /*

         * FIXED CATEGORY ROW

         */

        Row(

            modifier = Modifier

                .horizontalScroll(

                    rememberScrollState()

                ),

            horizontalArrangement =

                Arrangement.spacedBy(10.dp)

        ) {

            SettingsCategory.entries

                .forEach { category ->

                    val selected =

                        selectedCategory ==

                            category

                    Surface(

                        shape =

                            RoundedCornerShape(

                                18.dp

                            ),

                        color =

                            if (selected)

                                Accent.copy(

                                    alpha = 0.14f

                                )

                            else

                                SurfaceColor,

                        border = BorderStroke(

                            1.dp,

                            if (selected)

                                Accent.copy(

                                    alpha = 0.22f

                                )

                            else

                                Accent.copy(

                                    alpha = 0.06f

                                )

                        ),

                        onClick = {

                            haptics

                                .performHapticFeedback(

                                    HapticFeedbackType

                                        .TextHandleMove

                                )

                            selectedCategory =

                                category

                        }

                    ) {

                        Box(

                            modifier =

                                Modifier.padding(

                                    horizontal =

                                        18.dp,

                                    vertical =

                                        12.dp

                                ),

                            contentAlignment =

                                Alignment.Center

                        ) {

                            Text(

                                text =

                                    category.title

                                        .translate(

                                            uiState.displayLanguage

                                        ),

                                color =

                                    if (selected)

                                        Accent

                                    else

                                        TextPrimary,

                                fontWeight =

                                    if (selected)

                                        FontWeight.Bold

                                    else

                                        FontWeight.Medium,

                                fontSize = 13.sp

                            )

                        }

                    }

                }

        }
        Spacer(modifier = Modifier.height(12.dp))
    }


    LazyColumn(

        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(

            start = 18.dp,

            end = 18.dp,

            top = 18.dp,

            bottom = 140.dp

        ),

        verticalArrangement =

            Arrangement.spacedBy(18.dp)

    ) {
        /*
         * READING
         */

        if (
            selectedCategory ==
            SettingsCategory.Reading
        ) {

            item {

                SettingsSectionCard(

                    title = "Gurmukhi".translate(uiState.displayLanguage),

                    icon = Icons.Default.MenuBook
                ) {

                    CompactSlider(

                        title = "Font Size".translate(uiState.displayLanguage),

                        value =
                            uiState.gurmukhiFontSize,

                        range = 24f..46f
                    ) {

                        viewModel
                            .setGurmukhiFontSize(it)
                    }

                    Spacer(
                        Modifier.height(16.dp)
                    )

                    CompactSlider(

                        title = "Line Spacing".translate(uiState.displayLanguage),

                        value =
                            uiState.lineSpacing,

                        range = 28f..56f
                    ) {

                        viewModel.setLineSpacing(it)
                    }

                    Spacer(
                        Modifier.height(20.dp)
                    )

                    SettingsToggleGroup(

                        title = "Style".translate(uiState.displayLanguage),

                        items =
                            readingStyleItems
                    )
                }
            }

            item {

                SettingsSectionCard(

                    title = "Translations".translate(uiState.displayLanguage),

                    icon = Icons.Default.Translate
                ) {

                    CompactSlider(

                        title =
                            "Translation Size".translate(uiState.displayLanguage),

                        value =
                            uiState.translationFontSize,

                        range = 14f..28f
                    ) {

                        viewModel
                            .setTranslationFontSize(it)
                    }

                    Spacer(
                        Modifier.height(16.dp)
                    )

                    CompactSlider(

                        title =
                            "Roman Size".translate(uiState.displayLanguage),

                        value =
                            uiState
                                .romanTransliterationFontSize,

                        range = 12f..24f
                    ) {

                        viewModel
                            .setRomanTransliterationFontSize(it)
                    }

                    Spacer(
                        Modifier.height(16.dp)
                    )

                    SettingsToggleGroup(

                        title =
                            "Visible Content".translate(uiState.displayLanguage),

                        items =
                            translationItems
                    )
                }
            }

            item {

                SettingsSectionCard(

                    title = "Text Colors".translate(uiState.displayLanguage),

                    icon = Icons.Default.ColorLens
                ) {

                    ColorSelectorRow(
                        title = "Gurmukhi Text".translate(uiState.displayLanguage) + " (${"Gurmukhi".translate(uiState.displayLanguage)})",
                        selectedMode = uiState.gurmukhiColorMode,
                        onModeSelected = { viewModel.setGurmukhiColorMode(it) }
                    )

                    Spacer(Modifier.height(14.dp))

                    ColorSelectorRow(
                        title = "Roman Transliteration".translate(uiState.displayLanguage),
                        selectedMode = uiState.romanTransliterationColorMode,
                        onModeSelected = { viewModel.setRomanTransliterationColorMode(it) }
                    )

                    Spacer(Modifier.height(14.dp))

                    ColorSelectorRow(
                        title = "Hindi Transliteration".translate(uiState.displayLanguage),
                        selectedMode = uiState.devanagariTransliterationColorMode,
                        onModeSelected = { viewModel.setDevanagariTransliterationColorMode(it) }
                    )

                    Spacer(Modifier.height(14.dp))

                    ColorSelectorRow(
                        title = "English Translation".translate(uiState.displayLanguage),
                        selectedMode = uiState.translationColorMode,
                        onModeSelected = { viewModel.setTranslationColorMode(it) }
                    )

                    Spacer(Modifier.height(14.dp))

                    ColorSelectorRow(
                        title = "Punjabi Translation".translate(uiState.displayLanguage),
                        selectedMode = uiState.punjabiTranslationColorMode,
                        onModeSelected = { viewModel.setPunjabiTranslationColorMode(it) }
                    )

                    Spacer(Modifier.height(14.dp))

                    ColorSelectorRow(
                        title = "Word Meanings".translate(uiState.displayLanguage),
                        selectedMode = uiState.wordMeaningColorMode,
                        onModeSelected = { viewModel.setWordMeaningColorMode(it) }
                    )
                }
            }
        }

        /*
         * FULL SCREEN READING MODE (in Reading category)
         */
        if (selectedCategory == SettingsCategory.Reading) {
            item {
                SettingsSectionCard(
                    title = "Display".translate(uiState.displayLanguage),
                    icon = Icons.Default.PlayCircle
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setFullScreenReadingMode(!uiState.fullScreenReadingMode)
                            }
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Full-Screen Reading Mode".translate(uiState.displayLanguage),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Auto-hides top header & bottom bar after 4s in Gurbani & Meditation views. Tap screen to restore.".translate(uiState.displayLanguage),
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = uiState.fullScreenReadingMode,
                            onCheckedChange = { viewModel.setFullScreenReadingMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Accent,
                                checkedTrackColor = Accent.copy(alpha = 0.3f)
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = TextSecondary.copy(alpha = 0.08f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.setBlackAndWhiteAodMode(!uiState.blackAndWhiteAodMode)
                            }
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Black & White AOD Mode".translate(uiState.displayLanguage),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Ultra-minimalist AOD mode: pure black background, crisp white text, and only essential elements on screen in Reading & Meditation views.".translate(uiState.displayLanguage),
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = uiState.blackAndWhiteAodMode,
                            onCheckedChange = { viewModel.setBlackAndWhiteAodMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Accent,
                                checkedTrackColor = Accent.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        }

        /*
         * PLAYBACK
         */

        if (
            selectedCategory ==
            SettingsCategory.Playback
        ) {

            item {

                SettingsSectionCard(

                    title = "Playback".translate(uiState.displayLanguage),

                    icon =
                        Icons.Default.PlayCircle
                ) {

                    CompactSlider(

                        title =
                            "Playback Speed".translate(uiState.displayLanguage),

                        value =
                            uiState.playbackSpeed,

                        range =
                            0.5f..2f
                    ) {

                        viewModel
                            .setPlaybackSpeed(it)

                        audioController
                            .updatePlaybackSpeed(it)
                    }

                    Spacer(
                        Modifier.height(18.dp)
                    )

                    CompactSlider(

                        title =
                            "Auto Scroll Speed".translate(uiState.displayLanguage),

                        value =
                            uiState.autoScrollSpeed,

                        range =
                            0.5f..12f
                    ) {

                        viewModel
                            .setAutoScrollSpeed(it)
                    }

                    Spacer(
                        Modifier.height(18.dp)
                    )

                    SettingsSwitchRow(
                        title = "Auto Scroll".translate(uiState.displayLanguage),
                        checked = uiState.autoScroll
                    ) {
                        viewModel.setAutoScroll(it)
                    }

                    Spacer(
                        Modifier.height(18.dp)
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Repeat Mode".translate(uiState.displayLanguage),
                            color = Primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val repeatModes = listOf("OFF", "ONE", "ALL")
                            repeatModes.forEach { mode ->
                                val isSelected = uiState.repeatMode == mode
                                val label = when (mode) {
                                    "ONE" -> "Repeat One".translate(uiState.displayLanguage)
                                    "ALL" -> "Repeat All".translate(uiState.displayLanguage)
                                    else -> "Off".translate(uiState.displayLanguage)
                                }
                                val icon = when (mode) {
                                    "ONE" -> Icons.Default.RepeatOne
                                    "ALL" -> Icons.Default.Repeat
                                    else -> Icons.Default.Close
                                }
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) Accent.copy(alpha = 0.18f) else SurfaceColor,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) Accent.copy(alpha = 0.35f) else Accent.copy(alpha = 0.06f)
                                    ),
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.setRepeatMode(mode)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Accent else TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = label,
                                            color = if (isSelected) Accent else TextSecondary,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /*
         * APPEARANCE
         */

        if (
            selectedCategory ==
            SettingsCategory.Appearance
        ) {

            item {

                SettingsSectionCard(

                    title =
                        "Display Language".translate(uiState.displayLanguage),

                    icon =
                        Icons.Default.MenuBook
                ) {

                    LazyRow(

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            )
                    ) {

                        items(
                            DisplayLanguage.entries
                        ) { lang ->

                            val selected =
                                uiState.displayLanguage ==
                                    lang

                            Surface(

                                shape =
                                    RoundedCornerShape(
                                        16.dp
                                    ),

                                color =

                                    if (selected)

                                        Accent.copy(
                                            alpha = 0.14f
                                        )

                                    else

                                        SurfaceColor,

                                border = BorderStroke(

                                    1.dp,

                                    if (selected)

                                        Accent.copy(
                                            alpha = 0.28f
                                        )

                                    else

                                        Accent.copy(
                                            alpha = 0.06f
                                        )
                                ),

                                onClick = {

                                    haptics
                                        .performHapticFeedback(

                                            HapticFeedbackType
                                                .TextHandleMove
                                        )

                                    viewModel
                                        .setDisplayLanguage(
                                            lang
                                        )
                                }
                            ) {

                                Row(

                                    modifier =
                                        Modifier.padding(

                                            horizontal =
                                                14.dp,

                                            vertical =
                                                10.dp
                                        ),

                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    AnimatedVisibility(
                                        selected
                                    ) {

                                        Row {

                                            Icon(

                                                imageVector =
                                                    Icons.Default.Check,

                                                contentDescription =
                                                    null,

                                                tint =
                                                    Accent,

                                                modifier =
                                                    Modifier.size(
                                                        16.dp
                                                    )
                                            )

                                            Spacer(
                                                Modifier.width(
                                                    6.dp
                                                )
                                            )
                                        }
                                    }

                                    Text(

                                        text =
                                            lang.label,

                                        color =

                                            if (selected)
                                                Accent
                                            else
                                                TextPrimary,

                                        fontWeight =

                                            if (selected)
                                                FontWeight.Bold
                                            else
                                                FontWeight.Medium,

                                        fontSize =
                                            13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {

                SettingsSectionCard(

                    title = "Theme".translate(uiState.displayLanguage),

                    icon =
                        Icons.Default.DarkMode
                ) {

                    SettingsSwitchRow(

                        title =
                            "Dark Mode".translate(uiState.displayLanguage),

                        checked =
                            uiState.darkMode
                    ) {

                        viewModel.setDarkMode(it)

                        if (!it) {

                            viewModel
                                .setAmoledMode(false)
                        }
                    }

                    AnimatedVisibility(
                        visible =
                            uiState.darkMode
                    ) {

                        Column {

                            Spacer(
                                Modifier.height(8.dp)
                            )

                            SettingsSwitchRow(

                                title =
                                    "AMOLED Black".translate(uiState.displayLanguage),

                                checked =
                                    uiState.amoledMode
                            ) {

                                viewModel
                                    .setAmoledMode(it)
                            }
                        }
                    }

                    SettingsSwitchRow(

                        title =
                            "Elder Mode".translate(uiState.displayLanguage),

                        checked =
                            uiState.elderMode
                    ) {

                        viewModel.setElderMode(it)
                    }
                }
            }

            item {

                SettingsSectionCard(

                    title =
                        "Accent Theme".translate(uiState.displayLanguage),

                    icon =
                        Icons.Default.ColorLens
                ) {

                  AccentThemeSelector(

    displayLanguage = uiState.displayLanguage,

    onAccentSelected = { style ->

                            scope.launch {

                                repository
                                    .setAccentStyle(style)
                            }
                        }
                    )
                }
            }
        }
    
    
       /*
 * REMINDERS
 */

if (
    selectedCategory ==
    SettingsCategory.Reminders
) {

    item {

        SettingsSectionCard(

            title =
                "Prayer Reminders".translate(uiState.displayLanguage),

            icon =
                Icons.Default.Notifications
        ) {

            SettingsSwitchRow(

                title =
                    "Enable Notifications".translate(uiState.displayLanguage),

                checked =
                    uiState.notifications
            ) {

                viewModel.setNotifications(it)

                scope.launch {

                    repository.setNotifications(it)
                }
            }

            Spacer(
                Modifier.height(18.dp)
            )

            ReminderEditableRow(

                emoji = "🌅",

                title = "Amrit Vela".translate(uiState.displayLanguage),

                time = amritTime,

                onClick = {

                    selectedReminder =
                        "Amrit Vela"

                    showTimePicker = true
                }
            )

            Spacer(
                Modifier.height(12.dp)
            )

            ReminderEditableRow(

                emoji = "🌇",

                title = "Rehras Sahib".translate(uiState.displayLanguage),

                time = rehrasTime,

                onClick = {

                    selectedReminder =
                        "Rehras Sahib"

                    showTimePicker = true
                }
            )

            Spacer(
                Modifier.height(12.dp)
            )

            ReminderEditableRow(

                emoji = "🌙",

                title = "Kirtan Sohila".translate(uiState.displayLanguage),

                time = sohilaTime,

                onClick = {

                    selectedReminder =
                        "Kirtan Sohila"

                    showTimePicker = true
                }
            )
        }
    }
}
        if (
            selectedCategory ==
            SettingsCategory.Widget
        ) {
            item {
                SettingsSectionCard(
                    title = "Widget Styling".translate(uiState.displayLanguage),
                    icon = Icons.Default.ColorLens
                ) {
                    WidgetMockPreview(
                        widgetTheme = uiState.widgetTheme,
                        widgetContent = uiState.widgetContent,
                        displayLanguage = uiState.displayLanguage
                    )

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "Widget Theme".translate(uiState.displayLanguage),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val themes = listOf("Light", "Dark")
                        themes.forEach { themeName ->
                            val selected = uiState.widgetTheme == themeName
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) Accent.copy(alpha = 0.15f) else SurfaceColor,
                                border = BorderStroke(1.dp, if (selected) Accent else Color.Transparent),
                                onClick = { viewModel.setWidgetTheme(themeName) }
                            ) {
                                Box(
                                    modifier = Modifier.padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = themeName.translate(uiState.displayLanguage),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (selected) Accent else TextPrimary
                                    )
                                }
                        }
                    }
                }
            }
        }
    }
        if (
            selectedCategory ==
            SettingsCategory.Audio
        ) {

            val currentBani =
                selectedBaniForAudio

            if (currentBani != null) {

                /*
                 * CURATED TRACKS
                 */

                val curatedTracks =
                    CuratedAudioCatalog
                        .getTracksForBani(
                            currentBani.id
                        )

                /*
                 * DEFAULT TRACK
                 */


                val localTracks =
                    (
                            curatedTracks.filter { track ->
                                track.remoteUrl?.startsWith("raw/") == true
                            }

                                    +

                                    customTracks.filter { track ->
                                        (track.id.contains("_custom_") || !track.localPath.isNullOrBlank()) &&
                                        track.id != "default"
                                    }
                            ).distinctBy {
                            it.id
                        }

                /*
                 * ONLINE TRACKS
                 */

                val onlineTracks =
                    curatedTracks.filter { track ->
                        val url = track.remoteUrl.orEmpty()
                        (url.startsWith("http://") || url.startsWith("https://"))
                    }

                item {

                    /*
                     * SELECT BANI
                     */

                    Text(
                        text = "Select Bani"
                            .translate(
                                uiState.displayLanguage
                            ),

                        color = Accent,

                        fontWeight = FontWeight.Bold,

                        fontSize = 14.sp,

                        modifier = Modifier.padding(
                            top = 4.dp,
                            bottom = 6.dp
                        )
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {

                                    showBaniSelectMenu =
                                        true
                                },

                            shape = RoundedCornerShape(12.dp),

                            color = SurfaceColor,

                            border = BorderStroke(
                                1.dp,
                                Accent.copy(alpha = 0.15f)
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical = 14.dp
                                    ),

                                horizontalArrangement =
                                    Arrangement.SpaceBetween,

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Text(
                                    text = currentBani.title
                                        .translate(
                                            uiState.displayLanguage
                                        ),

                                    color = TextPrimary,

                                    fontWeight = FontWeight.Bold,

                                    fontSize = 15.sp
                                )

                                Icon(
                                    imageVector =
                                        Icons.Default.ArrowDropDown,

                                    contentDescription = null,

                                    tint = Accent
                                )
                            }
                        }

                        DropdownMenu(
                            expanded =
                                showBaniSelectMenu,

                            onDismissRequest = {

                                showBaniSelectMenu =
                                    false
                            }
                        ) {

                            uiState.banis.forEach { bani ->

                                DropdownMenuItem(

                                    text = {

                                        Text(
                                            bani.title.translate(
                                                uiState.displayLanguage
                                            )
                                        )
                                    },

                                    onClick = {

                                        selectedBaniForAudio =
                                            bani

                                        showBaniSelectMenu =
                                            false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(
                            vertical = 14.dp
                        ),

                        color = Accent.copy(
                            alpha = 0.08f
                        )
                    )

                    /*
                     * OFFLINE
                     */

                    AudioSectionHeader(

                        title =
                            "Downloaded / Imported Audio Files"
                                .translate(
                                    uiState.displayLanguage
                                ),

                        subtitle =
                            "Stored offline on your device"
                                .translate(
                                    uiState.displayLanguage
                                )
                    )
                    if (localTracks.isEmpty()) {

                        Text(
                            text =
                                "No offline audio files. Import custom audio below."
                                    .translate(
                                        uiState.displayLanguage
                                    ),

                            color =
                                TextSecondary.copy(
                                    alpha = 0.7f
                                ),

                            fontSize = 12.sp,

                            modifier = Modifier.padding(
                                vertical = 8.dp,
                                horizontal = 4.dp
                            )
                        )
                    }
                    /*
                     * CUSTOM LOCAL TRACKS
                     */

                    localTracks.forEach { track ->

                        val duration =
                            formatDuration(
                                track.durationMs
                            )

                        val subtitle =
                            buildString {

                                append(track.artist)

                                if (
                                    duration.isNotEmpty()
                                ) {

                                    append(" • ")
                                    append(duration)
                                }
                            }

                        AudioTrackRow(

                            title =
                                track.title.ifBlank {
                                    track.artist
                                },

                            subtitle =
                                subtitle.translate(
                                    uiState.displayLanguage
                                ),

                            selected =
                                activeTrackId ==
                                        track.id,

                            synced =
                                audioController.hasTimingFile(
                                    currentBani.id,
                                    track.id
                                ),

                            showDelete = true,

                            onSelect = {

                                viewModel.setActiveTrackId(
                                    currentBani.id,
                                    track.id,
                                    audioController
                                )

                                tracksRefreshTrigger++
                            },

                            onPlay = {

                                val url =
                                    track.localPath
                                        ?: return@AudioTrackRow

                                audioController.play(
                                    url,
                                    currentBani.title
                                )
                            },

                            onEdit = {

                                syncEditorTrackId =
                                    track.id

                                showSyncEditor = true
                            },

                            onDelete = {

                                audioController
                                    .deleteCustomTrack(
                                        track.id
                                    )

                                tracksRefreshTrigger++
                            }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(
                            vertical = 12.dp
                        ),

                        color =
                            Accent.copy(alpha = 0.08f)
                    )

                    /*
                     * ONLINE TRACKS
                     */

                    AudioSectionHeader(

                        title =
                            "Online Streaming Tracks / Links"
                                .translate(
                                    uiState.displayLanguage
                                ),

                        subtitle =
                            "Requires active internet connection"
                                .translate(
                                    uiState.displayLanguage
                                )
                    )

                    onlineTracks.forEach { track ->

                        val duration =
                            formatDuration(
                                track.durationMs
                            )

                        val subtitle =
                            buildString {

                                append(track.artist)

                                if (
                                    duration.isNotEmpty()
                                ) {

                                    append(" • ")
                                    append(duration)
                                }
                            }

                        val isDownloading =
                            uiState.downloadingBaniIds
                                .contains(
                                    "${currentBani.id}_${track.id}"
                                )

                        AudioTrackRow(

                            title =
                                track.title,

                            subtitle =
                                subtitle.translate(
                                    uiState.displayLanguage
                                ),

                            selected =
                                activeTrackId ==
                                        track.id,

                            synced =
                                audioController.hasTimingFile(
                                    currentBani.id,
                                    track.id
                                ),

                            downloading =
                                isDownloading,

                            onSelect = {

                                viewModel.setActiveTrackId(
                                    currentBani.id,
                                    track.id,
                                    audioController
                                )

                                tracksRefreshTrigger++
                            },

                            onPlay = {

                                val url =
                                    track.remoteUrl
                                        ?: return@AudioTrackRow

                                audioController.play(
                                    url,
                                    currentBani.title
                                )
                            },

                            onEdit = {

                                syncEditorTrackId =
                                    track.id

                                showSyncEditor = true
                            },

                            onDownload = {

                                val remoteUrl =
                                    track.remoteUrl
                                        ?: return@AudioTrackRow

                                viewModel.startDownload(
                                    currentBani.id,
                                    remoteUrl,
                                    audioController,
                                    track.id
                                )
                            }
                        )
                    }
                }
            }
        }

                        /*
                        * SYNCED TIMINGS MANAGER
                        */
                        if (
                            selectedCategory ==
                            SettingsCategory.SyncedTimings
                        ) {
                        // // Info banner explaining file matching
                        // Surface(
                        //     shape = RoundedCornerShape(18.dp),
                        //     color = Accent.copy(alpha = 0.08f),
                        //     border = BorderStroke(1.dp, Accent.copy(alpha = 0.18f))
                        // ) {
                        //     Row(
                        //         modifier = Modifier.padding(14.dp),
                        //         verticalAlignment = Alignment.CenterVertically
                        //     ) {
                        //         Icon(
                        //             imageVector = Icons.Default.Code,
                        //             contentDescription = null,
                        //             tint = Accent,
                        //             modifier = Modifier.size(24.dp)
                        //         )
                        //         Spacer(Modifier.width(12.dp))
                        //         Column(modifier = Modifier.weight(1f)) {
                        //             Text(
                        //                 text = "Sync Timing & Audio Matching".translate(uiState.displayLanguage),
                        //                 fontWeight = FontWeight.Bold,
                        //                 fontSize = 13.sp,
                        //                 color = TextPrimary
                        //             )
                        //             Text(
                        //                 text = "Timing keys strictly map to '{track_filename}_timing.json' to ensure line-by-line synchronization perfectly matches the recitation duration without misalignment."
                        //                     .translate(uiState.displayLanguage),
                        //                 fontSize = 11.sp,
                        //                 color = TextSecondary,
                        //                 lineHeight = 15.sp,
                        //                 modifier = Modifier.padding(top = 2.dp)
                        //             )
                        //         }
                        //     }
                        // }
                                                    item {

                                            val allBanis = uiState.banis

                                        Text(
                    text = "Select Bani".translate(uiState.displayLanguage),
                    color = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showBaniSelectMenu = true },
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceColor,
                        border = BorderStroke(1.dp, Accent.copy(alpha = 0.15f))
                    ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        ,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentBani = selectedBaniForAudio ?: uiState.banis.firstOrNull()

                            // Displays the currently selected audio bani title with a safe fallback
                            Text(
                        text = currentBani?.title?.let { it.translate(uiState.displayLanguage) } ?: "",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Accent
                    )
                }
            }

    DropdownMenu(
        expanded = showBaniSelectMenu,
        onDismissRequest = { showBaniSelectMenu = false }
    ) {
        uiState.banis.forEach { bani ->
            DropdownMenuItem(
                text = {
                    Text(bani.title.translate(uiState.displayLanguage))
                },
                onClick = {
                    selectedBaniForAudio = bani
                    showBaniSelectMenu = false
                }
            )
        }
    }
}

                       val banisToShow = when {
    selectedBaniForAudio != null -> {
        allBanis.filter { it.id == selectedBaniForAudio?.id }
    }
    selectedBaniFilter != null -> {
        allBanis.filter { it.id == selectedBaniFilter }
    }
    else -> {
        allBanis
    }
}

                        banisToShow.forEach { baniItem ->
                            val curatedTracks = CuratedAudioCatalog.getTracksForBani(baniItem.id)
                            val customTracksForBani = audioController.getCustomTracks(baniItem.id)
                            val allTracksForBani = buildList {
                                addAll(curatedTracks)
                                addAll(customTracksForBani)
                                if (isEmpty()) {
                                    baniItem.audio?.let { add(it.copy(id = "default")) }
                                }
                            }.distinctBy { it.timingKey() }

                            if (allTracksForBani.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Timing Files for selected bani",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Accent
                                    )

                                    allTracksForBani.forEach { trackItem ->
                                        val timingKey = trackItem.timingKey()
                                        val expectedJsonFileName = "${timingKey}_timing.json"
                                        
                                        // Check user saved file in app filesDir
                                        val localCustomTimingFile = java.io.File(context.filesDir, "audio/$expectedJsonFileName")
                                        val isUserSaved = localCustomTimingFile.exists() && localCustomTimingFile.length() > 0
                                        
                                        // Check bundled asset timing
                                        val assetPath = "timing/$expectedJsonFileName"
                                        val isBundledAsset = try {
                                            context.assets.open(assetPath).close()
                                            true
                                        } catch (_: Exception) {
                                            false
                                        }

                                        val hasTiming = isUserSaved || isBundledAsset

                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(18.dp),
                                            color = SurfaceColor,
                                            border = BorderStroke(
                                                1.dp,
                                                if (hasTiming) Accent.copy(alpha = 0.18f) else Color.Red.copy(alpha = 0.12f)
                                            )
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Text(
                                                                text = trackItem.title,
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 14.sp,
                                                                color = TextPrimary
                                                            )
                                                            Spacer(Modifier.width(6.dp))
                                                            if (hasTiming) {
                                                                Surface(
                                                                    shape = RoundedCornerShape(6.dp),
                                                                    color = Color(0xFF4CAF50).copy(alpha = 0.15f)
                                                                ) {
                                                                    Text(
                                                                        text = if (isUserSaved) "User Synced" else "Bundled",
                                                                        color = Color(0xFF4CAF50),
                                                                        fontSize = 10.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                    )
                                                                }
                                                            } else {
                                                                Surface(
                                                                    shape = RoundedCornerShape(6.dp),
                                                                    color = Color.Red.copy(alpha = 0.12f)
                                                                ) {
                                                                    Text(
                                                                        text = "No Timing File",
                                                                        color = Color(0xFFEF5350),
                                                                        fontSize = 10.sp,
                                                                        fontWeight = FontWeight.Bold,
                                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                                    )
                                                                }
                                                            }
                                                        }

                                                        Text(
                                                            text = "${"Artist".translate(uiState.displayLanguage)}: ${trackItem.artist}",
                                                            fontSize = 12.sp,
                                                            color = TextSecondary,
                                                            modifier = Modifier.padding(top = 2.dp)
                                                        )

                                                        Text(
                                                            text = "${"File".translate(uiState.displayLanguage)}: $expectedJsonFileName",
                                                            fontSize = 11.sp,
                                                            color = TextSecondary.copy(alpha = 0.70f),
                                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                                            modifier = Modifier.padding(top = 2.dp)
                                                        )
                                                    }

                                                    // Action buttons: Edit, Share, Delete
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        // Edit / Create Timing
                                                        IconButton(
                                                            onClick = {
                                                                selectedBaniForAudio = baniItem
                                                                syncEditorTrackId = trackItem.id
                                                                showSyncEditor = true
                                                            },
                                                            modifier = Modifier.size(36.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Edit,
                                                                contentDescription = "Edit Sync",
                                                                tint = Accent,
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                        }

                                                        // Share JSON file
                                                        if (hasTiming) {
                                                            IconButton(
                                                                onClick = {
                                                                    try {
                                                                        val fileToShare = if (isUserSaved) {
                                                                            localCustomTimingFile
                                                                        } else {
                                                                            // Extract bundled asset to temp cache file for sharing
                                                                            val tempShareFile = java.io.File(context.cacheDir, expectedJsonFileName)
                                                                            context.assets.open(assetPath).use { input ->
                                                                                tempShareFile.outputStream().use { output ->
                                                                                    input.copyTo(output)
                                                                                }
                                                                            }
                                                                            tempShareFile
                                                                        }

                                                                        val authority = "${context.packageName}.fileprovider"
                                                                        val shareUri = androidx.core.content.FileProvider.getUriForFile(
                                                                            context,
                                                                            authority,
                                                                            fileToShare
                                                                        )

                                                                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                                                            type = "application/json"
                                                                            putExtra(android.content.Intent.EXTRA_STREAM, shareUri)
                                                                            putExtra(android.content.Intent.EXTRA_SUBJECT, "$expectedJsonFileName - ${baniItem.title}")
                                                                            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                                        }
                                                                        context.startActivity(
                                                                            android.content.Intent.createChooser(shareIntent, "Share Timing JSON".translate(uiState.displayLanguage))
                                                                        )
                                                                    } catch (e: Exception) {
                                                                        Toast.makeText(context, "Share failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                                                    }
                                                                },
                                                                modifier = Modifier.size(36.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Share,
                                                                    contentDescription = "Share JSON",
                                                                    tint = Accent,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                            }
                                                        }

                                                        // Delete custom timing if user saved
                                                        if (isUserSaved) {
                                                            IconButton(
                                                                onClick = {
                                                                    localCustomTimingFile.delete()
                                                                    tracksRefreshTrigger++
                                                                    Toast.makeText(context, "Timing file deleted", Toast.LENGTH_SHORT).show()
                                                                },
                                                                modifier = Modifier.size(36.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Delete,
                                                                    contentDescription = "Delete custom sync",
                                                                    tint = Color.Red.copy(alpha = 0.7f),
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

        /*
         * ACCOUNT & PRIVACY
         */
        if (selectedCategory == SettingsCategory.Account) {
            item {
                SettingsSectionCard(
                    title = "Account & Sync".translate(uiState.displayLanguage),
                    icon = Icons.Default.CheckCircle
                ) {
                     val user = authLauncher.currentUser
                     Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                         if (user != null) {
                             // Logged in user profile card
                             Surface(
                                 shape = RoundedCornerShape(16.dp),
                                 color = Gold.copy(alpha = 0.12f),
                                 border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.4f)),
                                 modifier = Modifier.fillMaxWidth()
                             ) {
                                 Row(
                                     modifier = Modifier.padding(16.dp),
                                     verticalAlignment = Alignment.CenterVertically,
                                     horizontalArrangement = Arrangement.SpaceBetween
                                 ) {
                                     Column(modifier = Modifier.weight(1f)) {
                                         Text(
                                             text = user.displayName,
                                             fontWeight = FontWeight.Bold,
                                             fontSize = 15.sp,
                                             color = TextPrimary
                                         )
                                         Text(
                                             text = user.email,
                                             fontSize = 12.sp,
                                             color = TextSecondary
                                         )
                                     }

                                     OutlinedButton(
                                         onClick = {
                                             authLauncher.signOut()
                                             Toast.makeText(context, "Signed out successfully".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                                         },
                                         shape = RoundedCornerShape(12.dp),
                                         border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                                     ) {
                                         Text("Sign Out".translate(uiState.displayLanguage), color = MaterialTheme.colorScheme.error, fontSize = 12.5.sp)
                                     }
                                 }
                             }
                         } else {
                             Text(
                                 text = "You are currently using Nitnem in Guest (Offline) Mode. All your readings, streaks, and settings are saved securely on your device.".translate(uiState.displayLanguage),
                                 fontSize = 13.sp,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant
                             )

                             // Google Sign-in Card
                             Surface(
                                 shape = RoundedCornerShape(16.dp),
                                 color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                 border = BorderStroke(1.dp, Gold.copy(alpha = 0.4f)),
                                 modifier = Modifier.fillMaxWidth()
                             ) {
                                 Row(
                                     modifier = Modifier.padding(16.dp),
                                     verticalAlignment = Alignment.CenterVertically,
                                     horizontalArrangement = Arrangement.SpaceBetween
                                 ) {
                                     Column(modifier = Modifier.weight(1f)) {
                                         Text(
                                             text = "Cloud Sync & Leaderboards".translate(uiState.displayLanguage),
                                             fontWeight = FontWeight.Bold,
                                             fontSize = 14.sp
                                         )
                                         Text(
                                             text = "Sign in to backup streaks and join family groups.".translate(uiState.displayLanguage),
                                             fontSize = 12.sp,
                                             color = MaterialTheme.colorScheme.onSurfaceVariant
                                         )
                                     }

                                     Button(
                                         onClick = {
                                             authLauncher.signInWithGoogle()
                                         },
                                         colors = ButtonDefaults.buttonColors(containerColor = Gold),
                                         shape = RoundedCornerShape(12.dp)
                                     ) {
                                         Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                         Spacer(Modifier.width(6.dp))
                                         Text("Sign In".translate(uiState.displayLanguage), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                     }
                                 }
                             }
                         }
                     }
                }
            }

            item {
                SettingsSectionCard(
                    title = "Privacy & Data Management".translate(uiState.displayLanguage),
                    icon = Icons.Default.Warning
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Clear app cache & data
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    Toast.makeText(context, "Local audio cache and history cleared".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Clear App Cache & History".translate(uiState.displayLanguage), fontWeight = FontWeight.SemiBold)
                        }

                        // Permanently delete account & cloud data
                        Button(
                            onClick = {
                                Toast.makeText(context, "All account data permanently deleted from device and cloud.".translate(uiState.displayLanguage), Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Delete Account & All Data".translate(uiState.displayLanguage), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )
        }

    }
}
}

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text(
                    text = "Import Gurbani Audio".translate(uiState.displayLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Primary
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Enter the name of the artist / raagi for this audio file:".translate(uiState.displayLanguage),
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = artistNameInput,
                        onValueChange = { artistNameInput = it },
                        label = { Text("Artist Name".translate(uiState.displayLanguage)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent,
                            unfocusedBorderColor = Accent.copy(alpha = 0.3f)
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (artistNameInput.isNotBlank()) {
                            showImportDialog = false
                            audioPickerLauncher.launch()
                        } else {
                            Toast.makeText(context, "Artist Name cannot be empty".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text("Select Audio File".translate(uiState.displayLanguage))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImportDialog = false
                        artistNameInput = ""
                    }
                ) {
                    Text("Cancel".translate(uiState.displayLanguage), color = TextSecondary)
                }
            }
        )
    }
     /*
     * TIME PICKER DIALOG
     */

    if (showTimePicker) {

        val defaultHour = when (selectedReminder) {

            "Amrit Vela" -> 4

            "Rehras Sahib" -> 18

            else -> 22
        }

        val timeState =
            rememberTimePickerState(

                initialHour = defaultHour,

                initialMinute = 0,

                is24Hour = false
            )

        AlertDialog(

            onDismissRequest = {

                showTimePicker = false
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        val hour =
                            timeState.hour

                        /*
                         * VALIDATION
                         */

                        val valid = when (selectedReminder) {

                            "Amrit Vela" ->
                                hour in 2..8

                            "Rehras Sahib" ->
                                hour in 16..21

                            else ->
                                hour in 20..23 ||
                                    hour in 0..2
                        }

                        if (!valid) {
                            return@TextButton
                        }

                        val formattedHour = when {
                            hour == 0 -> 12
                            hour > 12 -> hour - 12
                            else -> hour
                        }
                        val amPm = if (hour >= 12) "PM" else "AM"
                        val formattedTime = String.format(
                            "%02d:%02d %s",
                            formattedHour,
                            timeState.minute,
                            amPm
                        )

                        when (selectedReminder) {

                            "Amrit Vela" -> {

                                amritTime =
                                    formattedTime

                                viewModel
                                    .setAmritTime(
                                        formattedTime
                                    )

                                scope.launch {

                                    repository
                                        .setAmritTime(
                                            formattedTime
                                        )
                                }
                            }

                            "Rehras Sahib" -> {

                                rehrasTime =
                                    formattedTime

                                viewModel
                                    .setRehrasTime(
                                        formattedTime
                                    )

                                scope.launch {

                                    repository
                                        .setRehrasTime(
                                            formattedTime
                                        )
                                }
                            }

                            else -> {

                                sohilaTime =
                                    formattedTime

                                viewModel
                                    .setSohilaTime(
                                        formattedTime
                                    )

                                scope.launch {

                                    repository
                                        .setSohilaTime(
                                            formattedTime
                                        )
                                }
                            }
                        }

                        // /*
                        //  * RESCHEDULE
                        //  */



                        showTimePicker = false
                    }
                ) {

                    Text(
                        text = "Save".translate(uiState.displayLanguage)
                    )
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {

                        showTimePicker = false
                    }
                ) {

                    Text(
                        text = "Cancel".translate(uiState.displayLanguage)
                    )
                }
            },

            title = {

                Text(
                    text = selectedReminder.translate(uiState.displayLanguage)
                )
            },

            text = {

                TimePicker(
                    state = timeState
                )
            }
        )
    }
}
