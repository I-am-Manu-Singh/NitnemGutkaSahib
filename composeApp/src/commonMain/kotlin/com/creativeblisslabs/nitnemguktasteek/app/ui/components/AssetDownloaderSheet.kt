package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.Accent
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.AppTheme
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.Gold
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.ThemeMode
import com.creativeblisslabs.nitnemguktasteek.audio.StreamAudioController
import com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog
import com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetTrack
import com.creativeblisslabs.nitnemguktasteek.app.ui.screens.SyncedBadge
import com.creativeblisslabs.nitnemguktasteek.app.ui.screens.formatDuration
import com.creativeblisslabs.nitnemguktasteek.utils.translate

/**
 * Interactive Dialog & Sheet for managing On-Demand Audio & Timing Downloads from GitHub.
 * Allows users to preview online tracks, selectively download individual or all Banis,
 * view local offline storage status, and seamlessly update files.
 */
@Composable
fun AssetDownloaderDialog(
    audioController: StreamAudioController,
    displayLanguage: DisplayLanguage,
    onDismiss: () -> Unit
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) } // 0: Nitnem Banis, 1: All Banis
    val selectedTrackIds = remember { mutableStateListOf<String>() }

    var isDownloadingAll by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var currentDownloadingTitle by remember { mutableStateOf("") }
    var downloadError by remember { mutableStateOf<String?>(null) }

    // Remote asset lists
    val currentCatalog = remember { RemoteAssetCatalog.currentAssets }
    val nitnemAssets = remember(currentCatalog) { currentCatalog.filter { it.isPrimaryNitnem } }
    val otherAssets = remember(currentCatalog) { currentCatalog.filter { !it.isPrimaryNitnem } }
    val displayedAssets = if (selectedTab == 0) nitnemAssets else currentCatalog

    // Total size of selected items
    val totalSelectedBytes = remember(selectedTrackIds.toList(), currentCatalog) {
        currentCatalog
            .filter { it.id in selectedTrackIds }
            .sumOf { it.approxSizeBytes }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isLight) Color(0xFFFFFDF8) else Color(0xFF191614)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = if (isLight) Color(0xFFFFFDF8) else Color(0xFF191614)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 14.dp)
                ) {
                    // ── HEADER ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Accent.copy(alpha = 0.16f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Cloud,
                                        contentDescription = null,
                                        tint = Accent,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = "Audio & Timing Manager".translate(displayLanguage),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLight) Color(0xFF2C1E14) else Color.White
                                )
                                Text(
                                    text = "On-Demand GitHub Downloads & Stream".translate(displayLanguage),
                                    fontSize = 11.5.sp,
                                    color = if (isLight) Color(0xFF795548) else Color(0xFFB0BEC5)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isLight) Color.Black.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = if (isLight) Color(0xFF4E342E) else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ── TABS: Nitnem vs All ──
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isLight) Color(0xFFEFEBE9) else Color(0xFF2A2521))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TabButton(
                            title = "Daily Nitnem (${nitnemAssets.size})".translate(displayLanguage),
                            selected = selectedTab == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 0 }
                        )
                        TabButton(
                            title = "All Banis (${currentCatalog.size})".translate(displayLanguage),
                            selected = selectedTab == 1,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 1 }
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // ── SELECTION TOOLBAR ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = {
                                    displayedAssets.forEach {
                                        if (it.id !in selectedTrackIds) selectedTrackIds.add(it.id)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Select All".translate(displayLanguage), fontSize = 12.sp, color = Accent, fontWeight = FontWeight.Bold)
                            }

                            TextButton(
                                onClick = { selectedTrackIds.clear() },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Clear".translate(displayLanguage), fontSize = 12.sp, color = if (isLight) Color(0xFF8D6E63) else Color(0xFF9E9E9E))
                            }
                        }

                        if (selectedTrackIds.isNotEmpty()) {
                            Text(
                                text = "${selectedTrackIds.size} Selected • ${formatFileSize(totalSelectedBytes)}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Gold
                            )
                        }
                    }

                    // ── DOWNLOAD PROGRESS CARD (Thin, sequential) ──
                    AnimatedVisibility(visible = isDownloadingAll) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Accent.copy(alpha = 0.12f),
                            border = BorderStroke(0.8.dp, Accent.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
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
                                            modifier = Modifier.size(13.dp),
                                            color = Accent,
                                            strokeWidth = 1.5.dp
                                        )
                                        Text(
                                            text = "Downloading: $currentDownloadingTitle",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isLight) Color(0xFF2C1E14) else Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "${(downloadProgress * 100).toInt()}%",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Accent,
                                        modifier = Modifier.padding(start = 6.dp)
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { downloadProgress },
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

                    // ── SCROLLABLE LIST OF ASSETS ──
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        displayedAssets.forEach { asset ->
                            val isDownloaded = audioController.isAudioDownloaded(asset.baniId, asset.id)
                            val isSelected = asset.id in selectedTrackIds

                            RemoteAssetRow(
                                asset = asset,
                                isDownloaded = isDownloaded,
                                isSelected = isSelected,
                                isLight = isLight,
                                displayLanguage = displayLanguage,
                                onToggleSelect = {
                                    if (isSelected) selectedTrackIds.remove(asset.id)
                                    else selectedTrackIds.add(asset.id)
                                },
                                onDownloadSingle = {
                                    scope.launch {
                                        currentDownloadingTitle = "${asset.baniTitle} (${asset.artist})"
                                        isDownloadingAll = true
                                        downloadProgress = 0.2f
                                        val ok = audioController.downloadAudio(asset.baniId, asset.rawAudioUrl(), asset.id)
                                        downloadProgress = 1.0f
                                        isDownloadingAll = false
                                    }
                                },
                                onDeleteSingle = {
                                    audioController.deleteAudio(asset.baniId, asset.id)
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // ── FOOTER ACTIONS ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, if (isLight) Color.Black.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.20f))
                        ) {
                            Text(
                                text = "Close".translate(displayLanguage),
                                color = if (isLight) Color(0xFF3E2723) else Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = {
                                if (selectedTrackIds.isEmpty()) return@Button
                                scope.launch {
                                    isDownloadingAll = true
                                    downloadError = null
                                    val targets = currentCatalog.filter { it.id in selectedTrackIds }
                                    var completed = 0

                                    targets.forEach { target ->
                                        currentDownloadingTitle = "${target.baniTitle} (${target.artist})"
                                        downloadProgress = completed.toFloat() / targets.size
                                        audioController.downloadAudio(target.baniId, target.rawAudioUrl(), target.id)
                                        completed++
                                        downloadProgress = completed.toFloat() / targets.size
                                    }

                                    isDownloadingAll = false
                                    selectedTrackIds.clear()
                                }
                            },
                            enabled = selectedTrackIds.isNotEmpty() && !isDownloadingAll,
                            colors = ButtonDefaults.buttonColors(containerColor = Accent),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .height(46.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (selectedTrackIds.isEmpty()) "Download Selected".translate(displayLanguage)
                                else "${"Download".translate(displayLanguage)} (${selectedTrackIds.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RemoteAssetRow(
    asset: RemoteAssetTrack,
    isDownloaded: Boolean,
    isSelected: Boolean,
    isLight: Boolean,
    displayLanguage: DisplayLanguage,
    onToggleSelect: () -> Unit,
    onDownloadSingle: () -> Unit,
    onDeleteSingle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = when {
            isSelected -> Accent.copy(alpha = if (isLight) 0.12f else 0.18f)
            isDownloaded -> Gold.copy(alpha = if (isLight) 0.08f else 0.12f)
            else -> if (isLight) Color(0xFFFBF8F2) else Color(0xFF231F1C)
        },
        border = BorderStroke(
            1.dp,
            when {
                isSelected -> Accent
                isDownloaded -> Gold.copy(alpha = 0.35f)
                else -> if (isLight) Color(0xFFE8E0D2) else Color(0xFF38332E)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleSelect)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Accent,
                    uncheckedColor = if (isLight) Color(0xFF8D6E63) else Color(0xFF9E9E9E)
                ),
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = asset.baniTitle.translate(displayLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = if (isLight) Color(0xFF2C1E14) else Color.White
                    )

                    if (asset.timingFileName != null) {
                        SyncedBadge()
                    }

                    if (isDownloaded) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF4CAF50).copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "Downloaded".translate(displayLanguage),
                                color = Color(0xFF4CAF50),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Accent.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Online Stream".translate(displayLanguage),
                                color = Accent,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "${asset.artist} • ${formatDuration(asset.durationMs)} • ${formatFileSize(asset.approxSizeBytes)}",
                    fontSize = 11.5.sp,
                    color = if (isLight) Color(0xFF6D4C41) else Color(0xFFB0BEC5),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(top = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Accent.copy(alpha = 0.08f),
                        border = BorderStroke(0.6.dp, Accent.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Accent, modifier = Modifier.size(10.dp))
                            Text(
                                text = asset.audioFileName,
                                fontSize = 10.sp,
                                color = Accent,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    val timingName = asset.timingFileName
                    if (timingName != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Gold.copy(alpha = 0.12f),
                            border = BorderStroke(0.6.dp, Gold.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, tint = Gold, modifier = Modifier.size(10.dp))
                                Text(
                                    text = timingName,
                                    fontSize = 10.sp,
                                    color = Gold,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Quick download or delete action icon
            if (isDownloaded) {
                IconButton(
                    onClick = onDeleteSingle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF5350),
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onDownloadSingle,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = Accent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) Accent else Color.Transparent,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.White else (if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF4E342E) else Color.White.copy(alpha = 0.7f))
            )
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 MB"
    val mb = bytes / (1024.0 * 1024.0)
    return "%.1f MB".format(mb)
}
