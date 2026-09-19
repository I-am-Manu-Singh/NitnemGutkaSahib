package com.creativeblisslabs.nitnemguktasteek.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.creativeblisslabs.nitnemguktasteek.utils.translate
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
import com.creativeblisslabs.nitnemguktasteek.data.CuratedAudioCatalog
import com.creativeblisslabs.nitnemguktasteek.app.sync.TimingLoader
import com.creativeblisslabs.nitnemguktasteek.utils.timingKey
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
import com.creativeblisslabs.nitnemguktasteek.audio.StreamAudioController
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.data.preferences.SettingsRepository
import com.creativeblisslabs.nitnemguktasteek.viewmodel.NitnemViewModel
import com.creativeblisslabs.nitnemguktasteek.utils.rememberAudioPicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TimePicker
import com.creativeblisslabs.nitnemguktasteek.app.sync.SyncEditorScreen




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(

    viewModel: NitnemViewModel,

    audioController: StreamAudioController,
    initialCategory: SettingsCategory = SettingsCategory.Reading
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

    val availableBanis = remember(uiState.banis) {
        if (uiState.banis.isNotEmpty()) uiState.banis else com.creativeblisslabs.nitnemguktasteek.data.generated.GeneratedBaniCatalog.banis
    }

    var selectedBaniForAudio by remember(uiState.currentBani, availableBanis) {
        mutableStateOf(uiState.currentBani ?: availableBanis.firstOrNull())
    }

    if (selectedBaniForAudio == null && availableBanis.isNotEmpty()) {
        selectedBaniForAudio = uiState.currentBani ?: availableBanis.firstOrNull()
    }

    var showBaniSelectMenu by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showAssetDownloaderDialog by remember { mutableStateOf(false) }
    var artistNameInput by remember { mutableStateOf("") }
    var tracksRefreshTrigger by remember { mutableStateOf(0) }
    var selectedBaniFilter by remember { mutableStateOf<String?>(null) }

    val activeTrackId = remember(selectedBaniForAudio?.id, tracksRefreshTrigger, uiState.activeTrackId) {
        selectedBaniForAudio?.id?.let { audioController.getActiveTrackId(it) } ?: "default"
    }
    val customTracks = remember(selectedBaniForAudio?.id, tracksRefreshTrigger, uiState.downloadedBaniIds) {
        selectedBaniForAudio?.id?.let { audioController.getCustomTracks(it) } ?: emptyList()
    }

    val allTracksForCurrentBani = remember(selectedBaniForAudio?.id, customTracks, tracksRefreshTrigger, uiState.downloadedBaniIds) {
        val baniId = selectedBaniForAudio?.id ?: return@remember emptyList()
        val curated = CuratedAudioCatalog.getTracksForBani(baniId)
        val remote = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTracksForBani(baniId).map { it.toAudioTrack() }
        val custom = customTracks.filter { it.id != "default" }
        (curated + remote + custom).distinctBy { it.id }
    }

    val localTracks = remember(selectedBaniForAudio?.id, allTracksForCurrentBani, tracksRefreshTrigger, uiState.downloadedBaniIds) {
        val baniId = selectedBaniForAudio?.id ?: return@remember emptyList()
        allTracksForCurrentBani.filter { track ->
            audioController.isAudioDownloaded(baniId, track.id) ||
                    (track.remoteUrl?.startsWith("raw/") == true && audioController.getLocalAudioPath(baniId, track.id) != null) ||
                    !track.localPath.isNullOrBlank()
        }
    }

    val onlineTracks = remember(allTracksForCurrentBani, localTracks) {
        allTracksForCurrentBani.filter { it !in localTracks }
    }

    var pendingArtistName by remember { mutableStateOf("") }
    val audioPickerLauncher = rememberAudioPicker { fileUri ->
        val bani = selectedBaniForAudio
        val artist = pendingArtistName.ifBlank { artistNameInput }
        if (bani != null && artist.isNotBlank() && fileUri.isNotBlank()) {
            scope.launch {
                val track = audioController.importCustomTrack(bani.id, artist, fileUri)
                if (track != null) {
                    viewModel.setActiveTrackId(bani.id, track.id, audioController)
                    Toast.makeText(context, "Imported successfully!".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Import failed".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                }
                pendingArtistName = ""
                artistNameInput = ""
                tracksRefreshTrigger++
            }
        }
    }

    val authLauncher = com.creativeblisslabs.nitnemguktasteek.utils.rememberAuthLauncher(
        onSignInSuccess = { user ->
            val msg = "Signed in as".translate(uiState.displayLanguage)
            Toast.makeText(context, "$msg ${user.displayName}", Toast.LENGTH_SHORT).show()
        },
        onSignInError = { error ->
            val msg = "Sign In Error".translate(uiState.displayLanguage)
            Toast.makeText(context, "$msg: $error", Toast.LENGTH_SHORT).show()
        }
    )

    var selectedCategory by remember(initialCategory) {

        mutableStateOf(
            initialCategory
        )
    }

    var readingSubTab by remember { mutableStateOf(0) }

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
if (selectedCategory == SettingsCategory.Reading) {
    // 1. Define the internal sub-tabs for the Reading category
    val subTabs = listOf(
        "Gurmukhi".translate(uiState.displayLanguage),
        "Translations".translate(uiState.displayLanguage),
        "Text Colors".translate(uiState.displayLanguage)
    )

    // 2. Render the Tab Switcher
    item {
        PrimaryTabRow(
            selectedTabIndex = readingSubTab,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            containerColor = Color.Transparent
        ) {
            subTabs.forEachIndexed { index, title ->
                Tab(
                    selected = readingSubTab == index,
                    onClick = { readingSubTab = index },
                    text = { Text(text = title, style = MaterialTheme.typography.titleSmall) }
                )
            }
        }
    }

    // 3. Render Content dynamically based on the active tab switch
    when (readingSubTab) {
        0 -> { // Gurmukhi Tab
            item {
                SettingsSectionCard(
                    title = "Gurmukhi".translate(uiState.displayLanguage),
                    icon = Icons.Default.MenuBook
                ) {
                    CompactSlider(
                        title = "Font Size".translate(uiState.displayLanguage),
                        value = uiState.gurmukhiFontSize,
                        range = 24f..46f
                    ) {
                        viewModel.setGurmukhiFontSize(it)
                    }

                    Spacer(Modifier.height(16.dp))

                    CompactSlider(
                        title = "Line Spacing".translate(uiState.displayLanguage),
                        value = uiState.lineSpacing,
                        range = 28f..56f
                    ) {
                        viewModel.setLineSpacing(it)
                    }

                    Spacer(Modifier.height(20.dp))

                    SettingsToggleGroup(
                        title = "Style".translate(uiState.displayLanguage),
                        items = readingStyleItems
                    )
                }
            }
        }

        1 -> { // Translations Tab
            item {
                SettingsSectionCard(
                    title = "Translations".translate(uiState.displayLanguage),
                    icon = Icons.Default.Translate
                ) {
                    CompactSlider(
                        title = "Translation Size".translate(uiState.displayLanguage),
                        value = uiState.translationFontSize,
                        range = 14f..28f
                    ) {
                        viewModel.setTranslationFontSize(it)
                    }

                    Spacer(Modifier.height(16.dp))

                    CompactSlider(
                        title = "Roman Size".translate(uiState.displayLanguage),
                        value = uiState.romanTransliterationFontSize,
                        range = 12f..24f
                    ) {
                        viewModel.setRomanTransliterationFontSize(it)
                    }

                    Spacer(Modifier.height(16.dp))

                    SettingsToggleGroup(
                        title = "Visible Content".translate(uiState.displayLanguage),
                        items = translationItems
                    )
                }
            }
        }

        2 -> { // Text Colors Tab
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
 * REMINDERS
 */

if (
    selectedCategory ==
    SettingsCategory.Reminders
) {
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

            val currentBani =
                selectedBaniForAudio

            if (currentBani != null) {

                item {
                    /*
                     * MULTISELECT DOWNLOAD TOOLBAR & SEQUENTIAL PROGRESS
                     */
                    val selectedTrackIdsForDownload = remember { mutableStateListOf<String>() }
                    val downloadingTimingTrackIds = remember { mutableStateListOf<String>() }
                    var isMultiDownloading by remember { mutableStateOf(false) }
                    var multiDownloadProgress by remember { mutableStateOf(0f) }
                    var multiCurrentTitle by remember { mutableStateOf("") }
                    var multiCompletedCount by remember { mutableStateOf(0) }
                    var multiTotalCount by remember { mutableStateOf(0) }

                    val undownloadedTracks = allTracksForCurrentBani.filter { track ->
                        val isDownloaded = audioController.isAudioDownloaded(currentBani.id, track.id) ||
                                (track.remoteUrl?.startsWith("raw/") == true && audioController.getLocalAudioPath(currentBani.id, track.id) != null) ||
                                !track.localPath.isNullOrBlank()
                        !isDownloaded && !track.remoteUrl.isNullOrBlank()
                    }

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

                            availableBanis.forEach { bani ->

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

                                        selectedTrackIdsForDownload.clear()

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
                     * MULTISELECT DOWNLOAD ACTION CARD
                     */
                    if (undownloadedTracks.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Accent.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, Accent.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val allSelected = undownloadedTracks.all { it.id in selectedTrackIdsForDownload }
                                        Checkbox(
                                            checked = allSelected,
                                            onCheckedChange = { checked ->
                                                if (checked) {
                                                    selectedTrackIdsForDownload.clear()
                                                    selectedTrackIdsForDownload.addAll(undownloadedTracks.map { it.id })
                                                } else {
                                                    selectedTrackIdsForDownload.clear()
                                                }
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Accent,
                                                uncheckedColor = Accent.copy(alpha = 0.5f),
                                                checkmarkColor = Color.White
                                            )
                                        )
                                        Text(
                                            text = if (allSelected) "Deselect All".translate(uiState.displayLanguage) else "Select All Available".translate(uiState.displayLanguage),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.5.sp,
                                            color = TextPrimary
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (!isMultiDownloading && selectedTrackIdsForDownload.isNotEmpty()) {
                                                isMultiDownloading = true
                                                val tracksToDownload = undownloadedTracks.filter { it.id in selectedTrackIdsForDownload }
                                                multiTotalCount = tracksToDownload.size
                                                multiCompletedCount = 0
                                                scope.launch {
                                                    tracksToDownload.forEachIndexed { index, track ->
                                                        multiCurrentTitle = track.title.ifBlank { track.artist }
                                                        multiDownloadProgress = index.toFloat() / tracksToDownload.size.coerceAtLeast(1)
                                                        val rawUrl = track.remoteUrl ?: return@forEachIndexed
                                                        audioController.downloadAudio(
                                                            baniId = currentBani.id,
                                                            remoteUrl = rawUrl,
                                                            trackId = track.id
                                                        )
                                                        TimingLoader.downloadTimingFile(
                                                            context = context,
                                                            timingKey = track.timingKey()
                                                        )
                                                        multiCompletedCount = index + 1
                                                        multiDownloadProgress = (index + 1).toFloat() / tracksToDownload.size.coerceAtLeast(1)
                                                        tracksRefreshTrigger++
                                                    }
                                                    isMultiDownloading = false
                                                    selectedTrackIdsForDownload.clear()
                                                }
                                            }
                                        },
                                        enabled = !isMultiDownloading && selectedTrackIdsForDownload.isNotEmpty(),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Accent,
                                            contentColor = Color.White,
                                            disabledContainerColor = Accent.copy(alpha = 0.3f),
                                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "${"Download".translate(uiState.displayLanguage)} (${selectedTrackIdsForDownload.size})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                /*
                                 * SEQUENTIAL DOWNLOAD PROGRESS CARD
                                 */
                                if (isMultiDownloading) {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        color = Accent.copy(alpha = 0.12f)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    modifier = Modifier.weight(1f, fill = false)
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(12.dp),
                                                        color = Accent,
                                                        strokeWidth = 1.5.dp
                                                    )
                                                    Text(
                                                        text = "Downloading ($multiCompletedCount/$multiTotalCount): $multiCurrentTitle",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = TextPrimary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                Text(
                                                    text = "${(multiDownloadProgress * 100).toInt()}%",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Accent,
                                                    modifier = Modifier.padding(start = 6.dp)
                                                )
                                            }

                                            LinearProgressIndicator(
                                                progress = { multiDownloadProgress },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(3.5.dp)
                                                    .clip(RoundedCornerShape(2.dp)),
                                                color = Accent,
                                                trackColor = Accent.copy(alpha = 0.2f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    /*
                     * IMPORT CUSTOM AUDIO TRACK BUTTON
                     */
                    OutlinedButton(
                        onClick = { showImportDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, Accent.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent)
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${"Import Custom Audio for".translate(uiState.displayLanguage)} ${currentBani.title.translate(uiState.displayLanguage)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    /*
                     * AUDIO TRACKS & TIMINGS
                     */
                    AudioSectionHeader(
                        title = "Audio Tracks & Synced Timings".translate(uiState.displayLanguage),
                        subtitle = "Select, stream, download, or edit synced timings for offline use".translate(uiState.displayLanguage)
                    )

                    if (allTracksForCurrentBani.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceColor,
                            border = BorderStroke(1.dp, Accent.copy(alpha = 0.12f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = Accent.copy(alpha = 0.6f),
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "No default online audio track cataloged yet for ${currentBani.title.translate(uiState.displayLanguage)}.".translate(uiState.displayLanguage),
                                    color = TextSecondary,
                                    fontSize = 12.5.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "You can import your own MP3/M4A audio recording above.".translate(uiState.displayLanguage),
                                    color = TextSecondary.copy(alpha = 0.7f),
                                    fontSize = 11.5.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    allTracksForCurrentBani.forEach { track ->
                        val duration = formatDuration(track.durationMs)
                        val isDownloaded = audioController.isAudioDownloaded(currentBani.id, track.id) ||
                                (track.remoteUrl?.startsWith("raw/") == true && audioController.getLocalAudioPath(currentBani.id, track.id) != null) ||
                                !track.localPath.isNullOrBlank()

                        val isDownloading = uiState.downloadingBaniIds.contains("${currentBani.id}_${track.id}") ||
                                (track.id == "default" && uiState.downloadingBaniIds.contains(currentBani.id)) ||
                                (isMultiDownloading && multiCurrentTitle.contains(track.artist))

                        val hasTiming = audioController.hasTimingFile(currentBani.id, track.id)
                        val isTimingDownloaded = TimingLoader.isTimingDownloadedLocally(context, track.timingKey()) || hasTiming

                        val subtitle = buildString {
                            append(track.artist)
                            if (duration.isNotEmpty()) {
                                append(" • ")
                                append(duration)
                            }
                            if (isDownloaded) {
                                append(" • ")
                                append("Offline".translate(uiState.displayLanguage))
                            } else {
                                append(" • ")
                                append("Online Stream".translate(uiState.displayLanguage))
                            }
                        }

                        val remoteAsset = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById(track.id)
                            ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTracksForBani(currentBani.id).firstOrNull { it.id == track.id }
                        val audioFileName = remoteAsset?.audioFileName ?: track.localPath?.substringAfterLast('/') ?: track.remoteUrl?.substringAfterLast('/')?.takeIf { it.isNotBlank() } ?: "${track.id}.mp3"
                        val timingFileName = remoteAsset?.timingFileName ?: "${track.timingKey()}_timing.json"

                        val isAvailableForDownload = !isDownloaded && !track.remoteUrl.isNullOrBlank()
                        val isTimingDownloading = track.id in downloadingTimingTrackIds

                        AudioTrackRow(
                            title = track.title.ifBlank { track.artist },
                            subtitle = subtitle,
                            selected = activeTrackId == track.id,
                            synced = hasTiming,
                            downloading = isDownloading,
                            showDelete = isDownloaded,
                            audioFileName = audioFileName,
                            timingFileName = timingFileName,
                            isTimingDownloaded = isTimingDownloaded,
                            isTimingDownloading = isTimingDownloading,
                            onDownloadTiming = if (!isTimingDownloaded && (remoteAsset?.timingFileName != null || !track.remoteUrl.isNullOrBlank())) {
                                {
                                    scope.launch {
                                        downloadingTimingTrackIds.add(track.id)
                                        val ok = TimingLoader.downloadTimingFile(context, track.timingKey())
                                        downloadingTimingTrackIds.remove(track.id)
                                        if (ok) {
                                            Toast.makeText(context, "Timing file downloaded and matched!".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                                            tracksRefreshTrigger++
                                        } else {
                                            Toast.makeText(context, "Could not download timing file".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else null,
                            isChecked = if (isAvailableForDownload) (track.id in selectedTrackIdsForDownload) else null,
                            onCheckedChange = if (isAvailableForDownload) {
                                { checked ->
                                    if (checked) {
                                        if (track.id !in selectedTrackIdsForDownload) selectedTrackIdsForDownload.add(track.id)
                                    } else {
                                        selectedTrackIdsForDownload.remove(track.id)
                                    }
                                }
                            } else null,
                            onSelect = {
                                viewModel.setActiveTrackId(
                                    currentBani.id,
                                    track.id,
                                    audioController
                                )
                                tracksRefreshTrigger++
                            },
                            onPlay = {
                                val url = if (isDownloaded) {
                                    audioController.getLocalAudioPath(currentBani.id, track.id)
                                        ?: track.localPath
                                        ?: track.remoteUrl
                                } else {
                                    track.remoteUrl
                                } ?: return@AudioTrackRow

                                audioController.play(
                                    url,
                                    currentBani.title
                                )
                            },
                            onEdit = {
                                syncEditorTrackId = track.id
                                showSyncEditor = true
                            },
                            onDownload = if (!isDownloaded && !track.remoteUrl.isNullOrBlank()) {
                                {
                                    viewModel.startDownload(
                                        currentBani.id,
                                        track.remoteUrl!!,
                                        audioController,
                                        track.id
                                    )
                                    // Auto download matching timing
                                    scope.launch {
                                        TimingLoader.downloadTimingFile(context, track.timingKey())
                                        tracksRefreshTrigger++
                                    }
                                }
                            } else null,
                            onDelete = if (isDownloaded) {
                                {
                                    audioController.deleteAudio(currentBani.id, track.id)
                                    audioController.deleteCustomTrack(track.id)
                                    selectedTrackIdsForDownload.remove(track.id)
                                    tracksRefreshTrigger++
                                }
                            } else null
                        )
                    }
                }
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(30.dp)
            )
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
                            pendingArtistName = artistNameInput
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
                        pendingArtistName = ""
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

    if (showAssetDownloaderDialog) {
        com.creativeblisslabs.nitnemguktasteek.app.ui.components.AssetDownloaderDialog(
            audioController = audioController,
            displayLanguage = uiState.displayLanguage,
            onDismiss = { showAssetDownloaderDialog = false }
        )
    }
    }
}
}



