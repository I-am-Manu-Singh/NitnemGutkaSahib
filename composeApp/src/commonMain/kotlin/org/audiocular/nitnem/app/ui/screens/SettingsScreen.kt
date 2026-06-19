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
import org.audiocular.nitnem.data.CuratedAudioCatalog
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

    Widget("Widget Settings"),

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

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Widget Content".translate(uiState.displayLanguage),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val contents = listOf("Mascot Assistant", "Meditation & Quote")
                        contents.forEach { contentName ->
                            val selected = uiState.widgetContent == contentName
                            val labelName = if (contentName == "Meditation & Quote") "Reflection & Progress" else contentName
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = if (selected) Accent.copy(alpha = 0.15f) else SurfaceColor,
                                border = BorderStroke(1.dp, if (selected) Accent else Color.Transparent),
                                onClick = { viewModel.setWidgetContent(contentName) }
                            ) {
                                Box(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = labelName.translate(uiState.displayLanguage),
                                        fontSize = 13.sp,
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
}/*
 * HELPERS
 */
fun formatDuration(
    durationMs: Long
): String {

    if (durationMs <= 0L) {
        return ""
    }

    val totalSeconds =
        durationMs / 1000

    val hours =
        totalSeconds / 3600

    val minutes =
        (totalSeconds % 3600) / 60

    val seconds =
        totalSeconds % 60

    return when {

        hours > 0 -> {

            String.format(
                "%d:%02d:%02d",
                hours,
                minutes,
                seconds
            )
        }

        else -> {

            String.format(
                "%02d:%02d",
                minutes,
                seconds
            )
        }
    }
}
@Composable
private fun SyncedBadge() {

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Accent.copy(alpha = 0.12f),
        modifier = Modifier.padding(start = 6.dp)
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 6.dp,
                vertical = 2.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(10.dp)
            )

            Spacer(
                modifier = Modifier.width(4.dp)
            )

            Text(
                text = "Synced",
                color = Accent,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AudioSectionHeader(
    title: String,
    subtitle: String
) {

    Text(
        text = title,
        color = Accent,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )

    Text(
        text = subtitle,
        color = TextSecondary,
        fontSize = 11.sp,
        modifier = Modifier.padding(
            bottom = 8.dp
        )
    )
}
@Composable
private fun AudioTrackRow(

    title: String,

    subtitle: String,

    selected: Boolean,

    synced: Boolean,

    downloading: Boolean = false,

    showDelete: Boolean = false,

    onSelect: () -> Unit,

    onPlay: () -> Unit,

    onEdit: () -> Unit,

    onDownload: (() -> Unit)? = null,

    onDelete: (() -> Unit)? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selected,
            onClick = onSelect
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),

            verticalArrangement = Arrangement.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = title,

                    modifier = Modifier.weight(
                        1f,
                        fill = false
                    ),

                    color = TextPrimary,

                    fontWeight = FontWeight.Bold,

                    fontSize = 14.sp,

                    maxLines = 2,

                    overflow = TextOverflow.Ellipsis
                )

                if (synced) {
                    SyncedBadge()
                }
            }

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = subtitle,

                color = TextSecondary,

                fontSize = 12.sp,

                maxLines = 1,

                overflow = TextOverflow.Ellipsis
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {

            if (downloading) {

                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(2.dp),

                    color = Accent,

                    strokeWidth = 2.dp
                )

            } else {

                onDownload?.let {

                    IconButton(
                        onClick = it,
                        modifier = Modifier.size(36.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onPlay,
                modifier = Modifier.size(36.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (showDelete) {

                IconButton(
                    onClick = {
                        onDelete?.invoke()
                    },
                    modifier = Modifier.size(36.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetMockPreview(
    widgetTheme: String,
    widgetContent: String,
    displayLanguage: DisplayLanguage
) {
    val isDark = AppTheme.themeMode != ThemeMode.Light

    val (bg, border, textPrimary, textSecondary, accentColor) = when (widgetTheme) {
        "Light" -> Quintuple(Color(0xFFFDFBF7), BorderStroke(1.dp, Gold.copy(alpha = 0.5f)), Primary, TextPrimary.copy(alpha = 0.70f), Gold)
        else -> Quintuple(Color(0xFF0F1115), BorderStroke(1.dp, Gold), Color.White, Color.White.copy(alpha = 0.65f), Gold)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().height(148.dp),
        shape = RoundedCornerShape(24.dp),
        color = bg,
        border = border
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            when (widgetContent) {
                "Mascot Assistant" -> {
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Brush.radialGradient(listOf(accentColor.copy(alpha = 0.55f), accentColor.copy(alpha = 0.20f)))), contentAlignment = Alignment.Center) {
                            Text("🦅", fontSize = 28.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Daily Nitnem".translate(displayLanguage), color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(3.dp))
                            Text(text = "Begin your Nitnem today 🌅".translate(displayLanguage), color = textSecondary, fontSize = 11.sp)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Japji".translate(displayLanguage), "Jaap".translate(displayLanguage), "Chaupai".translate(displayLanguage)).forEach { name ->
                                    Box(modifier = Modifier.background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text(name, color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(modifier = Modifier.background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)).padding(horizontal = 8.dp, vertical = 5.dp)) {
                            Text("🔥 0", color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                else -> {
                    // Meditation & Quote
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Text("✨", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "ੴ Satnam Waheguru", color = textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(2.dp))
                            Text(text = "Reflection • Upcoming: Japji Sahib".translate(displayLanguage), color = textSecondary, fontSize = 11.sp)
                        }
                        Box(modifier = Modifier.background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(100.dp)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                            Text("🔥 0/5", color = textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)