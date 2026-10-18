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
import com.creativeblisslabs.nitnemguktasteek.utils.AuthLauncher
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ChevronRight
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.LegalDocumentDialog
import nitnemguktasteek.composeapp.generated.resources.Res
import nitnemguktasteek.composeapp.generated.resources.ic_creative_bliss_labs_logo
import org.jetbrains.compose.resources.painterResource
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
import com.creativeblisslabs.nitnemguktasteek.app.sync.SyncEditorScreen
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.creativeblisslabs.nitnemguktasteek.audio.StreamAudioController
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.data.preferences.SettingsRepository
import com.creativeblisslabs.nitnemguktasteek.viewmodel.NitnemViewModel
import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState
import com.creativeblisslabs.nitnemguktasteek.utils.rememberAudioPicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TimePicker
import androidx.compose.ui.graphics.vector.ImageVector
import com.creativeblisslabs.nitnemguktasteek.domain.billing.RevenueCatBillingManager
import com.revenuecat.purchases.kmp.ui.revenuecatui.Paywall
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallOptions
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallListener
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: NitnemViewModel,
    audioController: StreamAudioController,
    onPaywallVisibilityChanged: (Boolean) -> Unit = {},
    initialCategory: SettingsCategory = SettingsCategory.Reading,
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptics = LocalHapticFeedback.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { 
        val prefs = com.creativeblisslabs.nitnemguktasteek.data.preferences.AppPreferences(context)
        SettingsRepository(context, prefs) 
    }
    
    var amritTime by remember(uiState.amritTime) { mutableStateOf(uiState.amritTime) }
    var rehrasTime by remember(uiState.rehrasTime) { mutableStateOf(uiState.rehrasTime) }
    var sohilaTime by remember(uiState.sohilaTime) { mutableStateOf(uiState.sohilaTime) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedReminder by remember { mutableStateOf("") }

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
    var activeLegalDoc by remember { mutableStateOf<String?>(null) }

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

    var selectedCategory by remember(initialCategory) { mutableStateOf(initialCategory) }

    val billingManager = remember { com.creativeblisslabs.nitnemguktasteek.domain.billing.PremiumBillingManager.instance }
    val revenueCatBillingManager = remember { RevenueCatBillingManager.instance }
    val billingState by billingManager.billingState.collectAsState()
    val isPremiumOrTrial = billingState.isPremium || billingState.isLifetimePatron || billingState.isTrialActive
    var showSevaModal by remember { mutableStateOf(false) }

    var readingSubTab by remember { mutableStateOf(0) }

    val readingStyleItems = remember(uiState) {
        listOf(
            SettingsToggleItem("Bold".translate(uiState.displayLanguage), uiState.boldGurmukhi) { viewModel.setBoldGurmukhi(it) },
            SettingsToggleItem("Center".translate(uiState.displayLanguage), uiState.centerGurmukhi) { viewModel.setCenterGurmukhi(it) },
            SettingsToggleItem("Highlight".translate(uiState.displayLanguage), uiState.highlightActiveLine) { viewModel.setHighlightActiveLine(it) },
            SettingsToggleItem("Gurmukhi Text".translate(uiState.displayLanguage), uiState.showGurmukhi) { viewModel.setShowGurmukhi(it) },
            SettingsToggleItem("Larivaar".translate(uiState.displayLanguage), uiState.larivaarMode) { viewModel.setLarivaarMode(it) }
        )
    }

    val translationItems = remember(uiState) {
        listOf(
            SettingsToggleItem("Hindi Transliteration".translate(uiState.displayLanguage), uiState.showDevanagariTransliteration) { viewModel.setShowDevanagariTransliteration(it) },
            SettingsToggleItem("Hindi Translation".translate(uiState.displayLanguage), uiState.showDevanagariTransliteration && (uiState.showPunjabiTranslation || uiState.showEnglishTranslation)) { enabled ->
                if (enabled) {
                    viewModel.setShowDevanagariTransliteration(true)
                    viewModel.setShowPunjabiTranslation(true)
                } else {
                    viewModel.setShowPunjabiTranslation(false)
                }
            },
            SettingsToggleItem("Roman Transliteration".translate(uiState.displayLanguage), uiState.showRomanTransliteration) { viewModel.setShowRomanTransliteration(it) },
            SettingsToggleItem("English Translation".translate(uiState.displayLanguage), uiState.showEnglishTranslation) { viewModel.setShowEnglishTranslation(it) },
            SettingsToggleItem("Punjabi Translation".translate(uiState.displayLanguage), uiState.showPunjabiTranslation) { viewModel.setShowPunjabiTranslation(it) },
            SettingsToggleItem("Word Meanings".translate(uiState.displayLanguage), uiState.showWordMeaning) { viewModel.setShowWordMeaning(it) }
        )
    }

    var showSyncEditor by remember { mutableStateOf(false) }
    var syncEditorTrackId by remember { mutableStateOf<String?>(null) }
    var isPaywallOpenState by remember { mutableStateOf(false) }
    val autoHideTabs = setOf(com.creativeblisslabs.nitnemguktasteek.app.models.AppTab.Experience, com.creativeblisslabs.nitnemguktasteek.app.models.AppTab.Practice)


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
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isLandscape = maxWidth > maxHeight && maxWidth > 600.dp

            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(0.36f).fillMaxHeight().padding(top = 8.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Settings".translate(uiState.displayLanguage),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = "Personalise your Nitnem experience".translate(uiState.displayLanguage),
                            fontSize = 13.sp,
                            color = TextPrimary.copy(alpha = 0.58f)
                        )
                        Spacer(Modifier.height(4.dp))

                      Column(
    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(8.dp)
) {
    SettingsCategory.entries.forEach { category ->
        val selected = selectedCategory == category
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = if (selected) Accent.copy(alpha = 0.15f) else SurfaceColor,
            border = BorderStroke(
                1.dp,
                if (selected) Accent.copy(alpha = 0.35f) else Accent.copy(alpha = 0.06f)
            ),
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                selectedCategory = category
            }
        ) {
            // Changed from Box to Row to align Icon and Text side-by-side
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Gap between icon and text
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null, // Set proper accessibility description if needed
                    modifier = Modifier.size(20.dp),
                    tint = if (selected) Accent else TextPrimary.copy(alpha = 0.7f)
                )
                
                Text(
                    text = category.title.translate(uiState.displayLanguage),
                    color = if (selected) Accent else TextPrimary,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.5.sp
                )
            }
        }
    }
}

                    }

                    Box(
                        modifier = Modifier.weight(0.64f).fillMaxHeight()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 140.dp, end = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                           settingsContent(
    selectedCategory = selectedCategory,
    readingSubTab = readingSubTab,
    onReadingSubTabChange = { readingSubTab = it },
    pendingArtistName = pendingArtistName,
    onPendingArtistNameChange = { pendingArtistName = it },
    artistNameInput = artistNameInput,
    onArtistNameInputChange = { artistNameInput = it },
    showImportDialog = showImportDialog,
    onShowImportDialogChange = { showImportDialog = it },
    showAssetDownloaderDialog = showAssetDownloaderDialog,
    onShowAssetDownloaderDialogChange = { showAssetDownloaderDialog = it },
    showSyncEditor = showSyncEditor,
    onShowSyncEditorChange = { showSyncEditor = it },
    syncEditorTrackId = syncEditorTrackId,
    onSyncEditorTrackIdChange = { syncEditorTrackId = it },
    activeLegalDoc = activeLegalDoc,
    onActiveLegalDocChange = { activeLegalDoc = it },
    revenueCatBillingManager = revenueCatBillingManager,
    showSevaModal = showSevaModal,
    onShowSevaModalChange = { showSevaModal = it },
    showTimePicker = showTimePicker,
    onShowTimePickerChange = { showTimePicker = it },
    selectedReminder = selectedReminder,
    onSelectedReminderChange = { selectedReminder = it },
    amritTime = amritTime,
    rehrasTime = rehrasTime,
    sohilaTime = sohilaTime,
    haptics = haptics,
    scope = scope,
    context = context,
    uiState = uiState,
    viewModel = viewModel,
    repository = repository,
    audioController = audioController,
    audioPickerLauncher = audioPickerLauncher,
    readingStyleItems = readingStyleItems,
    translationItems = translationItems,
    isPremiumOrTrial = isPremiumOrTrial,
    authLauncher = authLauncher,
    selectedBaniForAudio = selectedBaniForAudio,
    onSelectedBaniForAudioChange = { selectedBaniForAudio = it },
    showBaniSelectMenu = showBaniSelectMenu,
    onShowBaniSelectMenuChange = { showBaniSelectMenu = it },
    availableBanis = availableBanis,
    activeTrackId = activeTrackId,
    allTracksForCurrentBani = allTracksForCurrentBani,
    tracksRefreshTrigger = tracksRefreshTrigger,
    onTracksRefreshTriggerInc = { tracksRefreshTrigger++ }
)
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 12.dp)
                    ) {
                        Text(
                            text = "Settings".translate(uiState.displayLanguage),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Personalise your Nitnem experience".translate(uiState.displayLanguage),
                            fontSize = 14.sp,
                            color = TextPrimary.copy(alpha = 0.58f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

Row(
    modifier = Modifier.horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    SettingsCategory.entries.forEach { category ->
        val selected = selectedCategory == category
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (selected) Accent.copy(alpha = 0.14f) else SurfaceColor,
            border = BorderStroke(
                1.dp,
                if (selected) Accent.copy(alpha = 0.22f) else Accent.copy(alpha = 0.06f)
            ),
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                selectedCategory = category
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = if (selected) Accent else TextPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = category.title.translate(uiState.displayLanguage),
                    color = if (selected) Accent else TextPrimary,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        settingsContent(
                            selectedCategory = selectedCategory,
                            readingSubTab = readingSubTab,
                            onReadingSubTabChange = { readingSubTab = it },
                            pendingArtistName = pendingArtistName,
                            onPendingArtistNameChange = { pendingArtistName = it },
                            artistNameInput = artistNameInput,
                            onArtistNameInputChange = { artistNameInput = it },
                            showImportDialog = showImportDialog,
                            onShowImportDialogChange = { showImportDialog = it },
                            showAssetDownloaderDialog = showAssetDownloaderDialog,
                            onShowAssetDownloaderDialogChange = { showAssetDownloaderDialog = it },
                            showSyncEditor = showSyncEditor,
                            onShowSyncEditorChange = { showSyncEditor = it },
                            syncEditorTrackId = syncEditorTrackId,
                            onSyncEditorTrackIdChange = { syncEditorTrackId = it },
                            activeLegalDoc = activeLegalDoc,
                            onActiveLegalDocChange = { activeLegalDoc = it },
                            revenueCatBillingManager = revenueCatBillingManager,
                            showSevaModal = showSevaModal,
                            onShowSevaModalChange = { showSevaModal = it },
                            showTimePicker = showTimePicker,
                            onShowTimePickerChange = { showTimePicker = it },
                            selectedReminder = selectedReminder,
                            onSelectedReminderChange = { selectedReminder = it },
                            amritTime = amritTime,
                            rehrasTime = rehrasTime,
                            sohilaTime = sohilaTime,
                            haptics = haptics,
                            scope = scope,
                            context = context,
                            uiState = uiState,
                            viewModel = viewModel,
                            repository = repository,
                            audioController = audioController,
                            audioPickerLauncher = audioPickerLauncher,
                            readingStyleItems = readingStyleItems,
                            translationItems = translationItems,
                            isPremiumOrTrial = isPremiumOrTrial,
                            authLauncher = authLauncher,
                            selectedBaniForAudio = selectedBaniForAudio,
                            onSelectedBaniForAudioChange = { selectedBaniForAudio = it },
                            showBaniSelectMenu = showBaniSelectMenu,
                            onShowBaniSelectMenuChange = { showBaniSelectMenu = it },
                            availableBanis = availableBanis,
                            activeTrackId = activeTrackId,
                            allTracksForCurrentBani = allTracksForCurrentBani,
                            tracksRefreshTrigger = tracksRefreshTrigger,
                            onTracksRefreshTriggerInc = { tracksRefreshTrigger++ }
                        )
                    }
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

    if (showTimePicker) {
        val defaultHour = when (selectedReminder) {
            "Amrit Vela" -> 4
            "Rehras Sahib" -> 18
            else -> 22
        }

        val timeState = rememberTimePickerState(
            initialHour = defaultHour,
            initialMinute = 0,
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val hour = timeState.hour
                        val valid = when (selectedReminder) {
                            "Amrit Vela" -> hour in 2..8
                            "Rehras Sahib" -> hour in 16..21
                            else -> hour in 20..23 || hour in 0..2
                        }

                        if (!valid) { return@TextButton }

                        val formattedHour = when {
                            hour == 0 -> 12
                            hour > 12 -> hour - 12
                            else -> hour
                        }
                        val amPm = if (hour >= 12) "PM" else "AM"
                        val formattedTime = String.format("%02d:%02d %s", formattedHour, timeState.minute, amPm)

                        when (selectedReminder) {
                            "Amrit Vela" -> {
                                amritTime = formattedTime
                                viewModel.setAmritTime(formattedTime)
                                scope.launch { repository.setAmritTime(formattedTime) }
                            }
                            "Rehras Sahib" -> {
                                rehrasTime = formattedTime
                                viewModel.setRehrasTime(formattedTime)
                                scope.launch { repository.setRehrasTime(formattedTime) }
                            }
                            else -> {
                                sohilaTime = formattedTime
                                viewModel.setSohilaTime(formattedTime)
                                scope.launch { repository.setSohilaTime(formattedTime) }
                            }
                        }
                        showTimePicker = false
                    }
                ) {
                    Text(text = "Save".translate(uiState.displayLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(text = "Cancel".translate(uiState.displayLanguage))
                }
            },
            title = { Text(text = selectedReminder.translate(uiState.displayLanguage)) },
            text = { TimePicker(state = timeState) }
        )
    }

    if (showAssetDownloaderDialog) {
        com.creativeblisslabs.nitnemguktasteek.app.ui.components.AssetDownloaderDialog(
            audioController = audioController,
            displayLanguage = uiState.displayLanguage,
            onDismiss = { showAssetDownloaderDialog = false }
        )
    }

    if (showSevaModal) {
        com.creativeblisslabs.nitnemguktasteek.app.ui.components.SevaSupporterModalSheet(
            displayLanguage = uiState.displayLanguage,
            onDismiss = { showSevaModal = false }
        )
    }

activeLegalDoc?.let { docType ->
    LegalDocumentDialog(
        docType = docType,
        displayLanguage = uiState.displayLanguage,
        onDismiss = { activeLegalDoc = null }
    )
}
}

@OptIn(ExperimentalMaterial3Api::class)
private fun androidx.compose.foundation.lazy.LazyListScope.settingsContent(
    selectedCategory: SettingsCategory,
    readingSubTab: Int,
    onReadingSubTabChange: (Int) -> Unit,
    pendingArtistName: String,
    onPendingArtistNameChange: (String) -> Unit,
    artistNameInput: String,
    onArtistNameInputChange: (String) -> Unit,
    showImportDialog: Boolean,
    onShowImportDialogChange: (Boolean) -> Unit,
    showAssetDownloaderDialog: Boolean,
    onShowAssetDownloaderDialogChange: (Boolean) -> Unit,
    showSyncEditor: Boolean,
    onShowSyncEditorChange: (Boolean) -> Unit,
    syncEditorTrackId: String?,
    onSyncEditorTrackIdChange: (String?) -> Unit,
    activeLegalDoc: String?,
    onActiveLegalDocChange: (String?) -> Unit,
    revenueCatBillingManager: RevenueCatBillingManager,
    showSevaModal: Boolean,
    onShowSevaModalChange: (Boolean) -> Unit,
    showTimePicker: Boolean,
    onShowTimePickerChange: (Boolean) -> Unit,
    selectedReminder: String,
    onSelectedReminderChange: (String) -> Unit,
    amritTime: String,
    rehrasTime: String,
    sohilaTime: String,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    scope: kotlinx.coroutines.CoroutineScope,
    context: android.content.Context,
    uiState: NitnemUiState,
    viewModel: NitnemViewModel,
    repository: SettingsRepository,
    audioController: StreamAudioController,
    audioPickerLauncher: com.creativeblisslabs.nitnemguktasteek.utils.AudioPickerLauncher,
    readingStyleItems: List<SettingsToggleItem>,
    translationItems: List<SettingsToggleItem>,
    isPremiumOrTrial: Boolean,
    authLauncher: com.creativeblisslabs.nitnemguktasteek.utils.AuthLauncher,
    selectedBaniForAudio: com.creativeblisslabs.nitnemguktasteek.models.Bani?,
    onSelectedBaniForAudioChange: (com.creativeblisslabs.nitnemguktasteek.models.Bani?) -> Unit,
    showBaniSelectMenu: Boolean,
    onShowBaniSelectMenuChange: (Boolean) -> Unit,
    availableBanis: List<com.creativeblisslabs.nitnemguktasteek.models.Bani>,
    activeTrackId: String,
    allTracksForCurrentBani: List<com.creativeblisslabs.nitnemguktasteek.models.AudioTrack>,
    tracksRefreshTrigger: Int,
    onTracksRefreshTriggerInc: () -> Unit
) {
    // READING CATEGORY
    if (selectedCategory == SettingsCategory.Reading) {
        val subTabs = listOf(
            "Gurmukhi".translate(uiState.displayLanguage),
            "Translations".translate(uiState.displayLanguage),
            "Text Colors".translate(uiState.displayLanguage)
        )

        item {
            PrimaryTabRow(
                selectedTabIndex = readingSubTab,
                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
                containerColor = Color.Transparent
            ) {
                subTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = readingSubTab == index,
                        onClick = { onReadingSubTabChange(index) },
                        text = { Text(text = title, style = MaterialTheme.typography.titleSmall) }
                    )
                }
            }
        }

        when (readingSubTab) {
            0 -> {
                item {
                    SettingsSectionCard(
                        title = "Gurmukhi".translate(uiState.displayLanguage),
                        icon = Icons.Default.MenuBook
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = if (uiState.centerGurmukhi) Alignment.CenterHorizontally else Alignment.Start
                            ) {
                                Text(
                                    text = "Preview".translate(uiState.displayLanguage),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Gold
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "ੴ ਸਤਿ ਨਾਮੁ ਕਰਤਾ ਪੁਰਖੁ ਨਿਰਭਉ ਨਿਰਵੈਰੁ ॥",
                                    fontSize = uiState.gurmukhiFontSize.sp,
                                    lineHeight = uiState.lineSpacing.sp,
                                    fontWeight = if (uiState.boldGurmukhi) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = com.creativeblisslabs.nitnemguktasteek.app.ui.theme.resolveGurmukhiFontByKey(uiState.gurmukhiFont),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = if (uiState.centerGurmukhi) TextAlign.Center else TextAlign.Start
                                )
                            }
                        }

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

                        TierAwareFontSelector(
                            title = "Gurmukhi Script Calligraphy".translate(uiState.displayLanguage),
                            options = com.creativeblisslabs.nitnemguktasteek.app.ui.theme.GurmukhiFontFamily.values().map {
                                Triple(it.name, it.title, it.requiredTier)
                            },
                            selectedKey = uiState.gurmukhiFont,
                            sampleScriptType = "gurmukhi",
                            resolveFontFamily = { key -> com.creativeblisslabs.nitnemguktasteek.app.ui.theme.resolveGurmukhiFontByKey(key) },
                            onSelect = { viewModel.setGurmukhiFont(it) },
                            onLockedClicked = { revenueCatBillingManager.showPaywall() }
                        )

                        Spacer(Modifier.height(20.dp))

                        SettingsToggleGroup(
                            title = "Style".translate(uiState.displayLanguage),
                            items = readingStyleItems
                        )
                    }
                }
            }
            1 -> {
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

                        TierAwareFontSelector(
                            title = "Roman Transliteration Font".translate(uiState.displayLanguage),
                            options = com.creativeblisslabs.nitnemguktasteek.app.ui.theme.RomanFontFamily.values().map {
                                Triple(it.name, it.title, it.requiredTier)
                            },
                            selectedKey = uiState.romanFont,
                            sampleScriptType = "roman",
                            resolveFontFamily = { key -> com.creativeblisslabs.nitnemguktasteek.app.ui.theme.resolveRomanFontByKey(key) },
                            onSelect = { viewModel.setRomanFont(it) },
                            onLockedClicked = { revenueCatBillingManager.showPaywall() }
                        )

                        Spacer(Modifier.height(16.dp))

                        TierAwareFontSelector(
                            title = "Devanagari (Hindi) Font".translate(uiState.displayLanguage),
                            options = com.creativeblisslabs.nitnemguktasteek.app.ui.theme.DevanagariFontFamily.values().map {
                                Triple(it.name, it.title, it.requiredTier)
                            },
                            selectedKey = uiState.devanagariFont,
                            sampleScriptType = "devanagari",
                            resolveFontFamily = { key -> com.creativeblisslabs.nitnemguktasteek.app.ui.theme.resolveDevanagariFontByKey(key) },
                            onSelect = { viewModel.setDevanagariFont(it) },
                            onLockedClicked = { revenueCatBillingManager.showPaywall() }
                        )

                        Spacer(Modifier.height(16.dp))

                        SettingsToggleGroup(
                            title = "Visible Content".translate(uiState.displayLanguage),
                            items = translationItems
                        )
                    }
                }
            }
            2 -> {
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

    // APPEARANCE CATEGORY
    if (selectedCategory == SettingsCategory.Appearance) {
        item {
            SettingsSectionCard(
                title = "Display Language".translate(uiState.displayLanguage),
                icon = Icons.Default.MenuBook
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(DisplayLanguage.entries) { lang ->
                        val selected = uiState.displayLanguage == lang

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (selected) Accent.copy(alpha = 0.14f) else SurfaceColor,
                            border = BorderStroke(
                                1.dp,
                                if (selected) Accent.copy(alpha = 0.28f) else Accent.copy(alpha = 0.06f)
                            ),
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                viewModel.setDisplayLanguage(lang)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AnimatedVisibility(selected) {
                                    Row {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                    }
                                }

                                Text(
                                    text = lang.label,
                                    color = if (selected) Accent else TextPrimary,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPremiumOrTrial) {
                                viewModel.setFullScreenReadingMode(!uiState.fullScreenReadingMode)
                            } else {
                                revenueCatBillingManager.showPaywall()
                            }
                        }
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Full-Screen Reading Mode".translate(uiState.displayLanguage),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            if (!isPremiumOrTrial) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Gold,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
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
                        checked = uiState.fullScreenReadingMode && isPremiumOrTrial,
                        onCheckedChange = {
                            if (isPremiumOrTrial) {
                                viewModel.setFullScreenReadingMode(it)
                            } else {
                                revenueCatBillingManager.showPaywall()
                            }
                        },
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
                            if (isPremiumOrTrial) {
                                viewModel.setBlackAndWhiteAodMode(!uiState.blackAndWhiteAodMode)
                            } else {
                                revenueCatBillingManager.showPaywall()
                            }
                        }
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Black & White AOD Mode".translate(uiState.displayLanguage),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            if (!isPremiumOrTrial) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Gold,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
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
                        checked = uiState.blackAndWhiteAodMode && isPremiumOrTrial,
                        onCheckedChange = {
                            if (isPremiumOrTrial) {
                                viewModel.setBlackAndWhiteAodMode(it)
                            } else {
                                revenueCatBillingManager.showPaywall()
                            }
                        },
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
        icon = Icons.Default.DarkMode
    ) {
        SettingsSwitchRow(
            title = "Dark Mode".translate(uiState.displayLanguage),
            checked = uiState.darkMode
        ) {
            viewModel.setDarkMode(it)
            if (!it) {
                viewModel.setAmoledMode(false)
            }
        }

        AnimatedVisibility(visible = uiState.darkMode) {
            Column {
                Spacer(Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPremiumOrTrial) {
                                viewModel.setAmoledMode(!uiState.amoledMode)
                            } else {
                                revenueCatBillingManager.showPaywall()
                            }
                        }
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically, 
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "AMOLED Black".translate(uiState.displayLanguage),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            if (!isPremiumOrTrial) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Gold,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Text(
                            text = "Turns off pixels entirely on AMOLED screens for absolute pitch black backgrounds and enhanced battery savings.".translate(uiState.displayLanguage),
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = uiState.amoledMode && isPremiumOrTrial,
                        onCheckedChange = {
                            if (isPremiumOrTrial) {
                                viewModel.setAmoledMode(it)
                            } else {
                                revenueCatBillingManager.showPaywall()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Accent,
                            checkedTrackColor = Accent.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
    }
}


        item {
            SettingsSectionCard(
                title = "Accent Theme".translate(uiState.displayLanguage),
                icon = Icons.Default.ColorLens
            ) {
                AccentThemeSelector(
                    displayLanguage = uiState.displayLanguage,
                    onAccentSelected = { style ->
                        scope.launch {
                            repository.setAccentStyle(style)
                        }
                    },
                    onLockedThemeClicked = {
                        revenueCatBillingManager.showPaywall()
                    }
                )
            }
        }

        item {
            SettingsSectionCard(
                title = "Ambient Spiritual Particles".translate(uiState.displayLanguage),
                icon = Icons.Default.AutoAwesome
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Choose the meditative particle aura displayed gently during Gurbani reading & meditation views.".translate(uiState.displayLanguage),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    val particleEffects = listOf(
                        Triple("GoldenStardust", "Golden Stardust", "✨ Celestial golden sparks drifting upward"),
                        Triple("DivineLotus", "Divine Lotus", "🌸 Gentle petal motes radiating warmth"),
                        Triple("AmritRipples", "Amrit Ripples", "💧 Soothing crystalline ambient waves"),
                        Triple("CosmicSparks", "Cosmic Sparks", "🌌 Multidimensional ethereal glow points"),
                        Triple("FloatingEmbers", "Floating Embers", "🔥 Meditative warm sacred amber aura"),
                        Triple("Disabled", "Disabled (Off)", "🚫 Clean distraction-free solid background")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        particleEffects.forEach { (effectKey, title, desc) ->
                            val isSelected = uiState.ambientParticleEffect == effectKey
                            val isLocked = effectKey != "GoldenStardust" && effectKey != "Disabled" && !isPremiumOrTrial

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isLocked) {
                                            revenueCatBillingManager.showPaywall()
                                        } else {
                                            viewModel.setAmbientParticleEffect(effectKey)
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Accent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.35f),
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 0.8.dp,
                                    if (isSelected) Accent else Color.White.copy(alpha = 0.10f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            if (isLocked) revenueCatBillingManager.showPaywall()
                                            else viewModel.setAmbientParticleEffect(effectKey)
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Accent,
                                            unselectedColor = TextSecondary.copy(alpha = 0.6f)
                                        ),
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = title.translate(uiState.displayLanguage),
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                fontSize = 13.5.sp,
                                                color = if (isSelected) Accent else TextPrimary
                                            )
                                            if (isLocked) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = Gold,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = desc.translate(uiState.displayLanguage),
                                            fontSize = 11.5.sp,
                                            color = TextSecondary,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            SettingsSectionCard(
                title = "Playback".translate(uiState.displayLanguage),
                icon = Icons.Default.PlayCircle
            ) {
                CompactSlider(
                    title = "Playback Speed".translate(uiState.displayLanguage),
                    value = uiState.playbackSpeed,
                    range = 0.5f..2f
                ) {
                    viewModel.setPlaybackSpeed(it)
                    audioController.updatePlaybackSpeed(it)
                }

                Spacer(Modifier.height(18.dp))

                CompactSlider(
                    title = "Auto Scroll Speed".translate(uiState.displayLanguage),
                    value = uiState.autoScrollSpeed,
                    range = 0.5f..12f
                ) {
                    viewModel.setAutoScrollSpeed(it)
                }

                Spacer(Modifier.height(18.dp))

                SettingsSwitchRow(
                    title = "Auto Scroll".translate(uiState.displayLanguage),
                    checked = uiState.autoScroll
                ) {
                    viewModel.setAutoScroll(it)
                }

                Spacer(Modifier.height(18.dp))

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

                Spacer(Modifier.height(18.dp))

                var dailyGoalMinutes by remember { mutableStateOf(15) }
                CompactSlider(
                    title = "Daily Recitation Target (Minutes)".translate(uiState.displayLanguage),
                    value = dailyGoalMinutes.toFloat(),
                    range = 5f..120f
                ) {
                    dailyGoalMinutes = it.toInt()
                }
            }
        }
    }

    // REMINDERS CATEGORY
    if (selectedCategory == SettingsCategory.Reminders) {
        item {
            SettingsSectionCard(
                title = "Privacy & Data Management".translate(uiState.displayLanguage),
                icon = Icons.Default.Warning
            ) {
                val user = authLauncher.currentUser
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    if (user != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Gold.copy(alpha = 0.12f),
                            border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            modifier = Modifier.size(42.dp),
                                            color = Gold.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, Gold.copy(alpha = 0.5f))
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                if (user.photoUrl.isNotBlank()) {
                                                    AsyncImage(
                                                        model = user.photoUrl,
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                } else {
                                                    Text(
                                                        text = user.displayName.firstOrNull()?.toString()?.uppercase() ?: "G",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp,
                                                        color = Gold
                                                    )
                                                }
                                            }
                                        }

                                        Column {
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
                        }
                    } else {
                        Text(
                            text = "You are currently using Nitnem in Guest (Offline) Mode. All your readings, streaks, and settings are saved securely on your device.".translate(uiState.displayLanguage),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedButton(
                                onClick = {
                                    authLauncher.signInWithGoogle()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onBackground
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Sign In".translate(uiState.displayLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                title = "Prayer Reminders".translate(uiState.displayLanguage),
                icon = Icons.Default.Notifications
            ) {
                SettingsSwitchRow(
                    title = "Enable Notifications".translate(uiState.displayLanguage),
                    checked = uiState.notifications
                ) {
                    viewModel.setNotifications(it)
                    scope.launch {
                        repository.setNotifications(it)
                    }
                }

                Spacer(Modifier.height(18.dp))

                ReminderEditableRow(
                    icon = Icons.Default.WbSunny,
                    title = "Amrit Vela".translate(uiState.displayLanguage),
                    time = amritTime,
                    onClick = {
                        onSelectedReminderChange("Amrit Vela")
                        onShowTimePickerChange(true)
                    }
                )

                Spacer(Modifier.height(12.dp))

                ReminderEditableRow(
                    icon = Icons.Default.WbTwilight,
                    title = "Rehras Sahib".translate(uiState.displayLanguage),
                    time = rehrasTime,
                    onClick = {
                        onSelectedReminderChange("Rehras Sahib")
                        onShowTimePickerChange(true)
                    }
                )

                Spacer(Modifier.height(12.dp))

                ReminderEditableRow(
                    icon = Icons.Default.Bedtime,
                    title = "Kirtan Sohila".translate(uiState.displayLanguage),
                    time = sohilaTime,
                    onClick = {
                        onSelectedReminderChange("Kirtan Sohila")
                        onShowTimePickerChange(true)
                    }
                )
            }
        }
    }


    // AUDIO CATEGORY
    if (selectedCategory == SettingsCategory.Audio) {
        val currentBani = selectedBaniForAudio
        if (currentBani != null) {
            item {
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

                val localTracks = allTracksForCurrentBani.filter { track ->
                    audioController.isAudioDownloaded(currentBani.id, track.id) ||
                            (track.remoteUrl?.startsWith("raw/") == true && audioController.getLocalAudioPath(currentBani.id, track.id) != null) ||
                            !track.localPath.isNullOrBlank()
                }

                var isDownloadingAllCatalog by remember { mutableStateOf(false) }
                var downloadAllProgress by remember { mutableStateOf(0f) }
                var downloadAllCurrentTitle by remember { mutableStateOf("") }
                var downloadAllCompletedCount by remember { mutableStateOf(0) }
                var downloadAllTotalCount by remember { mutableStateOf(0) }

                SettingsSectionCard(
                    title = "Select Bani".translate(uiState.displayLanguage),
                    icon = Icons.Default.Warning
                ) {
                   Row(
    modifier = Modifier
        .fillMaxWidth()
        .padding(top = 0.dp, bottom = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    // Left side placeholder or other content if needed (kept empty for layout spacing)
    Spacer(modifier = Modifier.width(1.dp))

    // Show "Download Everything" button only when NOT currently downloading
    if (!isDownloadingAllCatalog) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Accent.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, Accent.copy(alpha = 0.35f)),
            onClick = {
                if (!isDownloadingAllCatalog && !isMultiDownloading) {
                    isDownloadingAllCatalog = true
                    scope.launch {
                        val allAssets = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.ALL_REMOTE_ASSETS
                        val remainingAssets = allAssets.filter { asset ->
                            !audioController.isAudioDownloaded(asset.baniId, asset.id)
                        }
                        downloadAllTotalCount = allAssets.size
                        downloadAllCompletedCount = allAssets.size - remainingAssets.size
                        if (remainingAssets.isEmpty()) {
                            isDownloadingAllCatalog = false
                            Toast.makeText(context, "All Audios & Timings already downloaded!".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        var successfulDownloads = 0
                        var failedDownloads = 0
                        remainingAssets.forEachIndexed { rIndex, asset ->
                            val currentIdx = allAssets.size - remainingAssets.size + rIndex
                            downloadAllCurrentTitle = "${asset.baniTitle} (${asset.artist})"
                            downloadAllProgress = currentIdx.toFloat() / allAssets.size.coerceAtLeast(1)
                            try {
                                val audioUrl = asset.rawAudioUrl()
                                val ok = audioController.downloadAudio(
                                    baniId = asset.baniId,
                                    remoteUrl = audioUrl,
                                    trackId = asset.id
                                )
                                val tKey = asset.toAudioTrack().timingKey()
                                if (tKey.isNotBlank()) {
                                    TimingLoader.downloadTimingFile(
                                        context = context,
                                        timingKey = tKey
                                    )
                                }
                                if (ok) {
                                    successfulDownloads++
                                    viewModel.setDownloadedBanis(uiState.downloadedBaniIds + "${asset.baniId}_${asset.id}")
                                } else {
                                    failedDownloads++
                                }
                            } catch (e: Exception) {
                                failedDownloads++
                                e.printStackTrace()
                            }
                            downloadAllCompletedCount = allAssets.size - remainingAssets.size + rIndex + 1
                            downloadAllProgress = (allAssets.size - remainingAssets.size + rIndex + 1).toFloat() / allAssets.size.coerceAtLeast(1)
                            onTracksRefreshTriggerInc()
                        }
                        isDownloadingAllCatalog = false
                        if (failedDownloads > 0 && successfulDownloads == 0) {
                            Toast.makeText(context, "Network unavailable. Download failed.".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                        } else if (failedDownloads > 0) {
                            Toast.makeText(context, "$successfulDownloads audios downloaded, $failedDownloads failed.".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "All Audios & Timings Downloaded!".translate(uiState.displayLanguage), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Download Everything".translate(uiState.displayLanguage),
                    color = Accent,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Download progress tracking row appears when downloading tracks
if (isDownloadingAllCatalog) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Accent.copy(alpha = 0.12f),
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
                        text = "Downloading ($downloadAllCompletedCount/$downloadAllTotalCount): $downloadAllCurrentTitle",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${(downloadAllProgress * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Accent,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            LinearProgressIndicator(
                progress = { downloadAllProgress },
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

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().clickable { onShowBaniSelectMenuChange(true) },
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceColor,
                            border = BorderStroke(1.dp, Accent.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
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
                            onDismissRequest = { onShowBaniSelectMenuChange(false) }
                        ) {
                            availableBanis.forEach { bani ->
                                DropdownMenuItem(
                                    text = { Text(bani.title.translate(uiState.displayLanguage)) },
                                    onClick = {
                                        onSelectedBaniForAudioChange(bani)
                                        selectedTrackIdsForDownload.clear()
                                        onShowBaniSelectMenuChange(false)
                                    }
                                )
                            }
                        }
                    }

                    if (undownloadedTracks.isNotEmpty()) {
                        Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                                    onTracksRefreshTriggerInc()
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
                                    Text(
                                        text = "${"Download Selected".translate(uiState.displayLanguage)} (${selectedTrackIdsForDownload.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    )
                                }
                            }

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
                                            modifier = Modifier.fillMaxWidth().height(3.5.dp).clip(RoundedCornerShape(2.dp)),
                                            color = Accent,
                                            trackColor = Accent.copy(alpha = 0.2f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { onShowImportDialogChange(true) },
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
                                            onTracksRefreshTriggerInc()
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
                                viewModel.setActiveTrackId(currentBani.id, track.id, audioController)
                                onTracksRefreshTriggerInc()
                            },
                            onPlay = {
                                val url = if (isDownloaded) {
                                    audioController.getLocalAudioPath(currentBani.id, track.id)
                                        ?: track.localPath
                                        ?: track.remoteUrl
                                } else {
                                    track.remoteUrl
                                } ?: return@AudioTrackRow

                                audioController.play(url, currentBani.title)
                            },
                            onEdit = {
                                onSyncEditorTrackIdChange(track.id)
                                onShowSyncEditorChange(true)
                            },
                            onDownload = if (!isDownloaded && !track.remoteUrl.isNullOrBlank()) {
                                {
                                    viewModel.startDownload(currentBani.id, track.remoteUrl!!, audioController, track.id)
                                    scope.launch {
                                        TimingLoader.downloadTimingFile(context, track.timingKey())
                                        onTracksRefreshTriggerInc()
                                    }
                                }
                            } else null,
                            onDelete = if (isDownloaded) {
                                {
                                    audioController.deleteAudio(currentBani.id, track.id)
                                    audioController.deleteCustomTrack(track.id)
                                    selectedTrackIdsForDownload.remove(track.id)
                                    onTracksRefreshTriggerInc()
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }
// ABOUT CATEGORY
if (selectedCategory == SettingsCategory.About) {
    item {
        SettingsSectionCard {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(92.dp)
                            .shadow(elevation = 10.dp, shape = CircleShape, spotColor = Gold.copy(alpha = 0.35f))
                            .background(color = MaterialTheme.colorScheme.surface, shape = CircleShape)
                            .clip(CircleShape)
                            .border(BorderStroke(1.5.dp, Gold.copy(alpha = 0.5f)), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_creative_bliss_labs_logo),
                            contentDescription = "Creative Bliss Labs Logo",
                            modifier = Modifier.size(76.dp).clip(CircleShape)
                        )
                    }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "Bani: Nitnem & Steek Companion",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = TextPrimary
                            )

                            Text(
                                text = " • 100% Free & Ad-Free Forever"
                                    .translate(uiState.displayLanguage),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        
                            Text(
                                text = "Developed with Seva by Creative Bliss Labs",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Accent
                            )
                        }

                     

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Gold.copy(alpha = 0.15f)
                        )

//                        val socialLinks = listOf(
//                            Triple("Website", "https://creativeblisslabs.com", Icons.Default.Public),
//                            Triple("Instagram", "https://instagram.com/creativeblisslabs", Icons.Default.CameraAlt),
//                            Triple("YouTube", "https://youtube.com/@creativeblisslabs", Icons.Default.PlayCircle)
//                        )
                        val socialLinks = listOf<Triple<String, String, ImageVector>>()
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            socialLinks.chunked(5).forEach { rowLinks ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    rowLinks.forEach { (label, url, icon) ->
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    val intent = android.content.Intent(
                                                        android.content.Intent.ACTION_VIEW,
                                                        android.net.Uri.parse(url)
                                                    )
                                                    try {
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, url, Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
                                                border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f)),
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = label,
                                                        tint = Gold,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TextSecondary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
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
        item {
            SettingsSectionCard(
                title = "Support, Feedback & Reporting".translate(uiState.displayLanguage),
                icon = Icons.Default.Favorite
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                data = android.net.Uri.parse("mailto:creativeblisslabs@gmail.com")
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Gurbani Text/Steek Correction Report - Nitnem App")
                                putExtra(android.content.Intent.EXTRA_TEXT, "Bani Name:\nLine / Ang:\nObserved Text:\nSuggested Correction:\nSource Reference:\n")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Email: creativeblisslabs@gmail.com", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF2B1D12) else Color.White
                        ),
                        border = BorderStroke(1.dp, Gold.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Report Mistake in Gurbani / Steek".translate(uiState.displayLanguage),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp,
                            maxLines = 1
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                data = android.net.Uri.parse("mailto:creativeblisslabs@gmail.com")
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Feature Request - Nitnem Gutka Steek")
                                putExtra(android.content.Intent.EXTRA_TEXT, "Hello Creative Bliss Labs team,\n\nI would love to suggest the following feature or idea for Nitnem App:\n\n")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Email: creativeblisslabs@gmail.com", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF2B1D12) else Color.White
                        ),
                        border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Suggest New Feature / Audio Track".translate(uiState.displayLanguage),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp,
                            maxLines = 1
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.creativeblisslabs.nitnemguktasteek")
                            )
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Visit Play Store to rate", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF2B1D12) else Color.White
                        ),
                        border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.StarRate,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "Rate 5 Stars on Google Play Store".translate(uiState.displayLanguage),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.5.sp,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = {
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_SUBJECT, "Bani: Nitnem & Steek Companion App")
                                putExtra(
                                    android.content.Intent.EXTRA_TEXT,
                                    "Waheguru Ji Ka Khalsa, Waheguru Ji Ki Fateh! 🙏\n\nRead and listen to Nitnem Gurbani with translations, audio sync and beautiful themes: https://play.google.com/store/apps/details?id=com.creativeblisslabs.nitnemguktasteek"
                                )
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Nitnem App with Sangat"))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp), spotColor = Gold.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Gold,
                                            Gold.copy(alpha = 0.85f),
                                            Gold.copy(alpha = 0.95f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Share App with Family & Friends".translate(uiState.displayLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SettingsSectionCard(
                title = "Legal, Transparency & Compliance".translate(uiState.displayLanguage),
                icon = Icons.Default.Gavel
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "We hold reverence for Sri Guru Granth Sahib Ji and transparency for the Sikh Sangat as our highest priorities. Review our honest terms, privacy commitments, and open-source attributions below.".translate(uiState.displayLanguage),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    val legalDocs = listOf(
                        Triple("Privacy Policy", Icons.Default.Security, "Privacy Policy"),
                        Triple("Terms of Service & EULA", Icons.Default.Description, "Terms of Service"),
                        Triple("Cancellation & Refund Policy", Icons.Default.Receipt, "Refund Policy"),
                        Triple("Scripture & Open Source Attributions", Icons.Default.MenuBook, "Attributions")
                    )

                    legalDocs.forEach { (title, icon, docType) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onActiveLegalDocChange(docType)
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f),
                            border = BorderStroke(0.8.dp, Gold.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Gold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = title.translate(uiState.displayLanguage),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}