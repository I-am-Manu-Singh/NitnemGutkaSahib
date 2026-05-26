package org.audiocular.nitnem.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import org.audiocular.nitnem.app.audio.StreamAudioController
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.data.preferences.SettingsRepository
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.rememberAudioPicker
import org.audiocular.nitnem.models.AudioTrack
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TimePicker
import org.audiocular.nitnem.app.sync.SyncEditorScreen

data class SettingsToggleItem(

    val title: String,

    val checked: Boolean,

    val onChecked: (Boolean) -> Unit
)

private enum class SettingsCategory(
    val title: String
) {

    Reading("Reading"),

    Playback("Playback"),

    Appearance("Appearance"),

    Reminders("Reminders"),

    Audio("Manage Audio")
}
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

    val activeTrackId = remember(selectedBaniForAudio?.id, tracksRefreshTrigger, uiState.activeTrackId) {
        selectedBaniForAudio?.id?.let { audioController.getActiveTrackId(it) } ?: "default"
    }
    val customTracks = remember(selectedBaniForAudio?.id, tracksRefreshTrigger) {
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
                    "Compact".translate(uiState.displayLanguage),
                    uiState.compactMode
                ) {
                    viewModel.setCompactMode(it)
                },

                SettingsToggleItem(
                    "Highlight".translate(uiState.displayLanguage),
                    uiState.highlightActiveLine
                ) {
                    viewModel.setHighlightActiveLine(it)
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
                        title = "Translation".translate(uiState.displayLanguage),
                        selectedMode = uiState.translationColorMode,
                        onModeSelected = { viewModel.setTranslationColorMode(it) }
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

                        title =
                            "Auto Scroll".translate(uiState.displayLanguage),

                        checked =
                            uiState.autoScroll
                    ) {

                        viewModel.setAutoScroll(it)
                    }

                    SettingsSwitchRow(

                        title =
                            "Repeat Mode".translate(uiState.displayLanguage),

                        checked =
                            uiState.repeatMode
                    ) {

                        viewModel.setRepeatMode(it)
                    }

                    SettingsSwitchRow(

                        title =
                            "Ambient Mode".translate(uiState.displayLanguage),

                        checked =
                            uiState.ambientMode
                    ) {

                        viewModel.setAmbientMode(it)
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
        SettingsCategory.Audio
    ) {
        val currentBani = selectedBaniForAudio
        if (currentBani != null) {

            item {
                SettingsSectionCard(
                    title = "Manage Audio".translate(uiState.displayLanguage),
                    icon = Icons.Default.PlayCircle
                ) {
                    val defaultTrack = currentBani.audio
                    val defaultLocalPath = audioController.getLocalAudioPath(currentBani.id)
                    val defaultIsLocal = defaultLocalPath != null

                    val localTracks = customTracks.filter { it.localPath != null }
                    val onlineTracks = customTracks.filter { it.localPath == null }

                    // 1. Select Bani dropdown row
                    Text(
                        text = "Select Bani".translate(uiState.displayLanguage),
                        color = Accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
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
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentBani.title.translate(uiState.displayLanguage),
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
                            onDismissRequest = { showBaniSelectMenu = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            uiState.banis.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b.title.translate(uiState.displayLanguage)) },
                                    onClick = {
                                        selectedBaniForAudio = b
                                        showBaniSelectMenu = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Accent.copy(alpha = 0.08f)
                    )

                    // 2. Audio tracks list
                    // --- Downloaded / Imported Audio Files ---
                    Text(
                        text = "Downloaded / Imported Audio Files".translate(uiState.displayLanguage),
                        color = Accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Stored offline on your device".translate(uiState.displayLanguage),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val hasLocal = defaultIsLocal || localTracks.isNotEmpty()
                    if (!hasLocal) {
                        Text(
                            text = "No offline audio files. Download the default track or import custom audio below.".translate(uiState.displayLanguage),
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    } else {
                        // Render default track if it is local
                        if (defaultIsLocal) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = activeTrackId == "default",
                                    onClick = {
                                        viewModel.selectBani(currentBani.id)
                                        viewModel.setActiveTrackId(currentBani.id, "default", audioController)
                                        val url = defaultLocalPath ?: ""
                                        if (url.isNotBlank()) {
                                            if (uiState.playback.isPlaying) {
                                                audioController.play(url, currentBani.title)
                                            } else {
                                                audioController.prepare(url, currentBani.title)
                                            }
                                        }
                                        tracksRefreshTrigger++
                                    }
                                )
                                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                                    Text(
                                        text = "Default Recitation (Offline)".translate(uiState.displayLanguage),
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = (defaultTrack?.artist ?: "Unknown Artist").translate(uiState.displayLanguage),
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        syncEditorTrackId = "default"
                                        showSyncEditor = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Accent
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.selectBani(currentBani.id)
                                        val url = defaultLocalPath ?: ""
                                        if (url.isNotBlank()) {
                                            audioController.play(url, currentBani.title)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Accent
                                    )
                                }
                            }
                        }

                        // Render user imported local tracks
                        localTracks.forEach { track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = activeTrackId == track.id,
                                    onClick = {
                                        viewModel.selectBani(currentBani.id)
                                        viewModel.setActiveTrackId(currentBani.id, track.id, audioController)
                                        val url = track.localPath ?: ""
                                        if (url.isNotBlank()) {
                                            if (uiState.playback.isPlaying) {
                                                audioController.play(url, currentBani.title)
                                            } else {
                                                audioController.prepare(url, currentBani.title)
                                            }
                                        }
                                        tracksRefreshTrigger++
                                    }
                                )
                                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                                    Text(
                                        text = if (track.title.isNotBlank()) track.title else track.artist,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    val durationStr = remember(track.durationMs) {
                                        if (track.durationMs > 0L) {
                                            val min = track.durationMs / 60000L
                                            val sec = (track.durationMs % 60000L) / 1000L
                                            "$min min $sec sec"
                                        } else {
                                            ""
                                        }
                                    }
                                    val subtitle = buildString {
                                        append(track.artist)
                                        if (durationStr.isNotEmpty()) {
                                            append(" • ")
                                            append(durationStr)
                                        }
                                    }
                                    Text(
                                        text = subtitle.translate(uiState.displayLanguage),
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        syncEditorTrackId = track.id
                                        showSyncEditor = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Accent
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.selectBani(currentBani.id)
                                        val url = track.localPath ?: ""
                                        if (url.isNotBlank()) {
                                            audioController.play(url, currentBani.title)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Accent
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        audioController.deleteCustomTrack(track.id)
                                        tracksRefreshTrigger++
                                        Toast.makeText(context, "Deleted track".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color(0xFFFF5252)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Accent.copy(alpha = 0.08f)
                    )

                    // --- Online Streaming Tracks / Links ---
                    Text(
                        text = "Online Streaming Tracks / Links".translate(uiState.displayLanguage),
                        color = Accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Requires active internet connection".translate(uiState.displayLanguage),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    val hasOnline = !defaultIsLocal || onlineTracks.isNotEmpty()
                    if (!hasOnline) {
                        Text(
                            text = "No online streaming tracks available.".translate(uiState.displayLanguage),
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    } else {
                        // Render default track if it is online (not downloaded)
                        if (!defaultIsLocal) {
                            val remoteUrl = defaultTrack?.remoteUrl ?: ""
                            val isDownloading = uiState.downloadingBaniIds.contains(currentBani.id)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = activeTrackId == "default",
                                    onClick = {
                                        viewModel.selectBani(currentBani.id)
                                        viewModel.setActiveTrackId(currentBani.id, "default", audioController)
                                        if (remoteUrl.isNotBlank()) {
                                            if (uiState.playback.isPlaying) {
                                                audioController.play(remoteUrl, currentBani.title)
                                            } else {
                                                audioController.prepare(remoteUrl, currentBani.title)
                                            }
                                        }
                                        tracksRefreshTrigger++
                                    }
                                )
                                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                                    Text(
                                        text = "Default Recitation (Online Stream)".translate(uiState.displayLanguage),
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = (defaultTrack?.artist ?: "Unknown Artist").translate(uiState.displayLanguage),
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }

                                if (isDownloading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp).padding(4.dp),
                                        color = Accent,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    IconButton(
                                        onClick = {
                                            if (remoteUrl.isNotBlank()) {
                                                viewModel.startDownload(currentBani.id, remoteUrl, audioController)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "Download default recitation",
                                            tint = Accent
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        syncEditorTrackId = "default"
                                        showSyncEditor = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Accent
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.selectBani(currentBani.id)
                                        if (remoteUrl.isNotBlank()) {
                                            audioController.play(remoteUrl, currentBani.title)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Accent
                                    )
                                }
                            }
                        }

                        // Render online / curated streaming tracks
                        onlineTracks.forEach { track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = activeTrackId == track.id,
                                    onClick = {
                                        viewModel.selectBani(currentBani.id)
                                        viewModel.setActiveTrackId(currentBani.id, track.id, audioController)
                                        val url = track.remoteUrl ?: ""
                                        if (url.isNotBlank()) {
                                            if (uiState.playback.isPlaying) {
                                                audioController.play(url, currentBani.title)
                                            } else {
                                                audioController.prepare(url, currentBani.title)
                                            }
                                        }
                                        tracksRefreshTrigger++
                                    }
                                )
                                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                                    Text(
                                        text = if (track.title.isNotBlank()) track.title else track.artist,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    val durationStr = remember(track.durationMs) {
                                        if (track.durationMs > 0L) {
                                            val min = track.durationMs / 60000L
                                            val sec = (track.durationMs % 60000L) / 1000L
                                            "$min min $sec sec"
                                        } else {
                                            ""
                                        }
                                    }
                                    val subtitle = buildString {
                                        append(track.artist)
                                        if (durationStr.isNotEmpty()) {
                                            append(" • ")
                                            append(durationStr)
                                        }
                                    }
                                    Text(
                                        text = subtitle.translate(uiState.displayLanguage),
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        syncEditorTrackId = track.id
                                        showSyncEditor = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Accent
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.selectBani(currentBani.id)
                                        val url = track.remoteUrl ?: ""
                                        if (url.isNotBlank()) {
                                            audioController.play(url, currentBani.title)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Accent
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Accent.copy(alpha = 0.08f)
                    )

                    // 3. Import and Sync Studio premium button row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showImportDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            color = Accent.copy(alpha = 0.06f),
                            border = BorderStroke(1.dp, Accent.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.UploadFile,
                                    contentDescription = null,
                                    tint = Accent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Import".translate(uiState.displayLanguage),
                                    color = Accent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showSyncEditor = true },
                            shape = RoundedCornerShape(14.dp),
                            color = Accent.copy(alpha = 0.06f),
                            border = BorderStroke(1.dp, Accent.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = Accent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Sync Studio".translate(uiState.displayLanguage),
                                    color = Accent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

        item {

            Spacer(
                modifier =
                    Modifier.height(60.dp)
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

                        // scope.launch {

                        //     PrayerReminderScheduler
                        //         .schedulePrayerReminders(

                        //             context = context,

                        //             amritTime = amritTime,

                        //             rehrasTime = rehrasTime,

                        //             sohilaTime = sohilaTime
                        //         )
                        // }

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
@Composable
private fun ReminderEditableRow(

    emoji: String,

    title: String,

    time: String,

    onClick: () -> Unit
) {

    Surface(

        modifier = Modifier
            .fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            SurfaceColor,

        border =
            BorderStroke(
                1.dp,
                Accent.copy(alpha = 0.08f)
            ),

        onClick = onClick
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = emoji,
                fontSize = 22.sp
            )

            Spacer(
                Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = title,

                    color = TextPrimary,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize = 15.sp
                )

                Spacer(
                    Modifier.height(2.dp)
                )

                Text(

                    text = time,

                    color =
                        Accent,

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }

            Icon(

                imageVector =
                    Icons.Default.Edit,

                contentDescription = null,

                tint =
                    Accent
            )
        }
    }
}
/*
 * SECTION CARD
 */

@Composable
private fun SettingsSectionCard(

    title: String,

    icon:
        androidx.compose.ui.graphics.vector
            .ImageVector,

    content:
        @Composable ColumnScope.() -> Unit
) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(22.dp),

        color =
            SurfaceColor,

        border = BorderStroke(

            1.dp,

            Accent.copy(alpha = 0.08f)
        )
    ) {

        Column(

            modifier =
                Modifier.padding(22.dp)
        ) {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(

                    modifier =
                        Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Accent.copy(
                                    alpha = 0.12f
                                )
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector = icon,

                        contentDescription =
                            null,

                        tint = Accent,

                        modifier =
                            Modifier.size(20.dp)
                    )
                }

                Spacer(
                    Modifier.width(14.dp)
                )

                Text(

                    text = title,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 20.sp,

                    color = Primary
                )
            }

            Spacer(
                Modifier.height(24.dp)
            )

            content()
        }
    }
}

/*
 * SWITCH ROW
 */

@Composable
private fun SettingsSwitchRow(

    title: String,

    checked: Boolean,

    onChecked:
        (Boolean) -> Unit
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(

            text = title,

            color = Primary,

            fontWeight =
                FontWeight.Medium
        )

        Switch(

            checked = checked,

            onCheckedChange =
                onChecked
        )
    }
}

/*
 * SLIDER
 */

@Composable
private fun CompactSlider(

    title: String,

    value: Float,

    range:
        ClosedFloatingPointRange<Float>,

    onValueChange:
        (Float) -> Unit
) {

    Column {

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(

                text = title,

                color = Primary,

                fontWeight =
                    FontWeight.Medium
            )

            Text(

                text =
                    String.format(
                        "%.1f",
                        value
                    ),

                color = Accent,

                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            Modifier.height(8.dp)
        )

        Slider(

            value = value,

            onValueChange =
                onValueChange,

            valueRange = range
        )
    }
}

/*
 * TOGGLE GROUP
 */

@Composable
private fun SettingsToggleGroup(

    title: String,

    items:
        List<SettingsToggleItem>
) {

    Column {

        Text(

            text = title,

            color = Primary,

            fontWeight =
                FontWeight.SemiBold,

            fontSize = 14.sp
        )

        Spacer(
            Modifier.height(12.dp)
        )

        Column(

            verticalArrangement =
                Arrangement.spacedBy(2.dp)
        ) {

            items.forEachIndexed { index, item ->

                CompactToggleRow(

                    title =
                        item.title,

                    checked =
                        item.checked,

                    onChecked =
                        item.onChecked
                )

                if (
                    index !=
                    items.lastIndex
                ) {

                    HorizontalDivider(

                        color =
                            TextPrimary.copy(
                                alpha = 0.08f
                            )
                    )
                }
            }
        }
    }
}

/*
 * TOGGLE ROW
 */

@Composable
private fun CompactToggleRow(

    title: String,

    checked: Boolean,

    onChecked:
        (Boolean) -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(

            text = title,

            modifier =
                Modifier.weight(1f),

            color = Primary,

            fontSize = 14.sp
        )

        Switch(

            checked = checked,

            onCheckedChange =
                onChecked
        )
    }
}

/*
 * REMINDER ROW
 */

@Composable
private fun ReminderRow(

    emoji: String,

    title: String,

    displayLanguage: DisplayLanguage
){

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(18.dp),

        color =
            MaterialTheme.colorScheme
                .surfaceContainerHighest
                .copy(alpha = 0.35f),

        border = BorderStroke(

            1.dp,

            Accent.copy(alpha = 0.06f)
        )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(

                text = emoji,

                fontSize = 24.sp
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = title,

                    fontWeight =
                        FontWeight.SemiBold,

                    fontSize = 15.sp,

                    color = Primary
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

          Text(

    text =
        "Custom reminder schedule".translate(displayLanguage),

    fontSize = 12.sp,

    color =
        TextPrimary.copy(
            alpha = 0.55f
        )
)
            }

            Surface(

                shape =
                    RoundedCornerShape(50.dp),

                color =
                    Accent.copy(alpha = 0.12f)
            ) {

              Text(

    text = "Manage".translate(displayLanguage),

    modifier =
        Modifier.padding(
            horizontal = 12.dp,
            vertical = 6.dp
        ),

    color = Accent,

    fontSize = 12.sp,

    fontWeight =
        FontWeight.SemiBold
)
            }
        }
    }
}

/*
 * ACCENT SELECTOR
 */

@Composable
private fun AccentThemeSelector(

    displayLanguage: DisplayLanguage,

    onAccentSelected:
        (AccentStyle) -> Unit
){

    val accentOptions = remember {

        listOf(

            AccentStyle.Emerald  to "Emerald",

            AccentStyle.Sapphire to "Sapphire",

            AccentStyle.Royal    to "Royal",

            AccentStyle.Sunset   to "Sunset"
        )
    }

    val previewColors = remember {

        mapOf(

            AccentStyle.Emerald
                    to Color(0xFF44D7B6),

            AccentStyle.Sapphire
                    to Color(0xFF5DAEFF),

            AccentStyle.Royal
                    to Color(0xFF9B7CFF),

            AccentStyle.Sunset
                    to Color(0xFFFF9E57)
        )
    }

    LazyRow(

        horizontalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        items(accentOptions) { (style, label) ->

            val selected =
                AppTheme.accentStyle == style

            val color =
                previewColors[style]
                    ?: Accent

            Column(

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Surface(

                    modifier =
                        Modifier.size(

                            if (selected)
                                56.dp
                            else
                                46.dp
                        ),

                    shape = CircleShape,

                    color = color,

                    border = BorderStroke(

                        if (selected)
                            3.dp
                        else
                            1.dp,

                        if (selected)
                            color.copy(alpha = 0.5f)
                        else
                            Color.Transparent
                    ),

                    onClick = {

                        onAccentSelected(style)
                    }
                ) {

                    AnimatedVisibility(
                        visible = selected
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Check,

                                contentDescription =
                                    null,

                                tint = Color.White,

                                modifier =
                                    Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(

                    text = label.translate(displayLanguage),

                    fontSize = 11.sp,

                    color =

                        if (selected)
                            color
                        else
                            TextPrimary.copy(
                                alpha = 0.5f
                            ),

                    fontWeight =

                        if (selected)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun ColorSelectorRow(
    title: String,
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    val modes = listOf(
        "default", "accent", "muted", "white", "gold", "purple", "blue", "green", "orange"
    )
    val modeColors = mapOf(
        "default" to TextPrimary.copy(alpha = 0.8f),
        "accent" to Accent,
        "muted" to TextSecondary,
        "white" to Color.White,
        "gold" to Color(0xFFD6B25E),
        "purple" to Color(0xFFB39DFF),
        "blue" to Color(0xFF7AB8FF),
        "green" to Color(0xFF63D8B6),
        "orange" to Color(0xFFFFB36A)
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Primary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(modes) { mode ->
                val color = modeColors[mode] ?: Color.Gray
                val selected = selectedMode == mode
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) Accent else Color.LightGray.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { onModeSelected(mode) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (mode == "white") Color.Black else Color.White)
                        )
                    }
                }
            }
        }
    }
}