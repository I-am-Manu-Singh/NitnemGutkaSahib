package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.utils.translateAng
import com.creativeblisslabs.nitnemguktasteek.utils.translateDigits
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BaniCard(
    bani: Bani,
    selected: Boolean,
    progress: Float,
    onClick: () -> Unit,
    artistName: String? = null,
    hasSyncedTiming: Boolean = false,
    hasAudioFile: Boolean = false,
    displayLanguage: DisplayLanguage = DisplayLanguage.English,
    leadingIcon: ImageVector = resolveBaniTheme(bani.title).icon,
    onInfoClick: (() -> Unit)? = null
) {
    val audio = bani.audio

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700),
        label = ""
    )

    /*
     * THEME
     */
    val baniTheme = remember(bani.title) {
        resolveBaniTheme(bani.title)
    }

    val prayerAccent = baniTheme.accent
    val iconGradient = baniTheme.gradient

    /*
     * RANDOM FLOATING ANIMATION
     */
    val startX = remember { Random.nextInt(-3, 3).toFloat() }
    val endX = remember { Random.nextInt(-7, 7).toFloat() }
    val startY = remember { Random.nextInt(-3, 3).toFloat() }
    val endY = remember { Random.nextInt(-6, 6).toFloat() }
    val startRotation = remember { Random.nextInt(-4, 4).toFloat() }
    val endRotation = remember { Random.nextInt(-12, 12).toFloat() }

    /*
     * ANIMATIONS
     */
    val infinite = if (selected) {
        rememberInfiniteTransition(label = "")
    } else {
        null
    }

    val containerPulse = infinite?.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )?.value ?: 1f

    val iconScale = infinite?.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )?.value ?: 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = prayerAccent.copy(
                alpha = when {
                    selected && AppTheme.themeMode == ThemeMode.Light -> 0.10f
                    selected && AppTheme.themeMode == ThemeMode.Dark -> 0.08f
                    selected && AppTheme.themeMode == ThemeMode.Amoled -> 0.06f
                    AppTheme.themeMode == ThemeMode.Light -> 0.035f
                    AppTheme.themeMode == ThemeMode.Dark -> 0.022f
                    else -> 0.016f
                }
            )
        ),
        border = BorderStroke(
            width = if (selected) 1.dp else 0.6.dp,
            color = if (selected) {
                prayerAccent.copy(
                    alpha = when (AppTheme.themeMode) {
                        ThemeMode.Light -> 0.22f
                        ThemeMode.Dark -> 0.13f
                        ThemeMode.Amoled -> 0.09f
                    }
                )
            } else {
                when (AppTheme.themeMode) {
                    ThemeMode.Light -> Color.Black.copy(alpha = 0.04f)
                    ThemeMode.Dark -> Color.White.copy(alpha = 0.04f)
                    ThemeMode.Amoled -> Color.White.copy(alpha = 0.045f)
                }
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            /*
             * MAIN CONTENT
             */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                /*
                 * ICON AREA
                 */
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .scale(containerPulse)
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    prayerAccent.copy(alpha = 0.14f)
                                } else {
                                    prayerAccent.copy(alpha = 0.08f)
                                }
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .scale(containerPulse)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(iconGradient)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    /*
                     * TITLE ROW WITH ACCENTED INFO BUTTON
                     */
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bani.title.translate(displayLanguage),
                            modifier = Modifier.weight(1f, fill = false),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (onInfoClick != null) {
                            Spacer(Modifier.width(6.dp))

                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(prayerAccent.copy(alpha = 0.15f))
                                    .clickable(onClick = onInfoClick),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Bani Information".translate(displayLanguage),
                                    tint = prayerAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }

                    /*
                     * AUTHOR
                     */
                    Text(
                        text = bani.author.translate(displayLanguage),
                        color = TextPrimary.copy(alpha = 0.60f),
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    /*
                     * PROGRESS / STRUCTURE
                     */
                    val activeProgressString = remember(bani, progress, selected, displayLanguage) {
                        if (selected) {
                            val lineIndex = (progress * (bani.lines.size - 1)).toInt()
                                .coerceIn(0, bani.lines.size - 1)

                            calculateBaniProgress(
                                bani.title,
                                bani.lines,
                                lineIndex
                            )?.getLocalizedLabel(displayLanguage)
                        } else {
                            null
                        }
                    }

                    Text(
                        text = if (selected && activeProgressString != null) {
                            "${bani.ang.translateAng(displayLanguage)} • $activeProgressString"
                        } else {
                            "${bani.ang.translateAng(displayLanguage)} • ${bani.structure.translate(displayLanguage)}"
                        }.translateDigits(displayLanguage),
                        color = TextPrimary.copy(alpha = 0.52f),
                        fontSize = 10.sp,
                        lineHeight = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(2.dp))

                    /*
                     * CHIPS
                     */
                    val hasAudio = remember(bani, artistName) {
                        bani.audio != null || !artistName.isNullOrBlank()
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                        maxItemsInEachRow = 4
                    ) {
                        MiniInfoChip(
                            text = "${bani.estimatedMinutes} ${"mins".translate(displayLanguage)}".translateDigits(displayLanguage)
                        )

                        MiniInfoChip(
                            text = bani.time.name.translate(displayLanguage)
                        )

                        if (hasSyncedTiming) {
                            MiniInfoChip(
                                text = "Synced".translate(displayLanguage),
                                highlight = true
                            )
                        } else if (hasAudioFile || hasAudio) {
                            MiniInfoChip(
                                text = "Audio".translate(displayLanguage)
                            )
                        }

                        if (!artistName.isNullOrBlank()) {
                            MiniInfoChip(
                                text = artistName,
                                maxWidth = 90.dp
                            )
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                /*
                 * RIGHT SIDE
                 */
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val eqAlpha = if (selected) {
                        infinite?.animateFloat(
                            initialValue = 0.55f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(700),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = ""
                        )?.value ?: 1f
                    } else {
                        1f
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    prayerAccent.copy(alpha = 0.07f)
                                } else {
                                    when (AppTheme.themeMode) {
                                        ThemeMode.Light -> Mist
                                        ThemeMode.Dark -> Color.White.copy(alpha = 0.05f)
                                        ThemeMode.Amoled -> Color.White.copy(alpha = 0.04f)
                                    }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selected) {
                                Icons.Default.GraphicEq
                            } else {
                                Icons.Default.KeyboardArrowRight
                            },
                            contentDescription = null,
                            tint = if (selected) {
                                prayerAccent.copy(alpha = eqAlpha)
                            } else {
                                TextPrimary.copy(alpha = 0.6f)
                            }
                        )
                    }
                }
            }
        }

        /*
         * PROGRESS
         */
        if (animatedProgress > 0.001f) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = prayerAccent,
                trackColor = Color.Transparent
            )
        }
    }
}

@Composable
private fun MiniInfoChip(
    text: String,
    highlight: Boolean = false,
    maxWidth: androidx.compose.ui.unit.Dp = 95.dp
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (highlight) {
                    Accent.copy(alpha = 0.12f)
                } else {
                    when (AppTheme.themeMode) {
                        ThemeMode.Light -> Mist
                        ThemeMode.Dark -> SurfaceVariant
                        ThemeMode.Amoled -> Color(0xFF111111)
                    }
                }
            )
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.widthIn(max = maxWidth),
            color = if (highlight) Accent else TextPrimary,
            fontSize = 9.sp,
            fontWeight = if (highlight) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false
        )
    }
}

@Composable
private fun BaniDetailItem(
    label: String,
    value: String,
    displayLanguage: DisplayLanguage = DisplayLanguage.English
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextPrimary.copy(alpha = 0.65f),    
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.width(100.dp))

        Text(
            text = value,
            color = TextPrimary,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaniInfoBottomSheet(
    selectedBani: Bani,
    infoSheetState: SheetState,
    displayLanguage: DisplayLanguage = DisplayLanguage.English,
    onDismiss: () -> Unit
) {
    // Resolve theme once per Bani ID to prevent re-computation during render passes
    val baniTheme = remember(selectedBani.id) {
        resolveBaniTheme(selectedBani.title)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = infoSheetState,
        shape = RoundedCornerShape(
            topStart = 32.dp,
            topEnd = 32.dp
        ),
        containerColor = SurfaceColor,
        tonalElevation = 0.dp
    ) {
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 22.dp,
                    vertical = 12.dp
                )
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(baniTheme.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = baniTheme.icon,
                        contentDescription = null,
                        tint = baniTheme.accent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedBani.title.translate(displayLanguage),
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Bani Information".translate(displayLanguage),
                        color = TextPrimary.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Bani Details
            if (baniTheme.about.isNotBlank()) {
                Text(
                    text = "About".translate(displayLanguage),
                    color = Accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = baniTheme.about.translate(displayLanguage),
                    color = TextPrimary.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }

            if (baniTheme.significance.isNotBlank()) {
                HorizontalDivider(
                    color = TextPrimary.copy(alpha = 0.06f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Text(
                    text = "Significance".translate(displayLanguage),
                    color = Accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = baniTheme.significance.translate(displayLanguage),
                    color = TextPrimary.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(
                color = TextPrimary.copy(alpha = 0.06f),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            BaniDetailItem(
                label = "Author".translate(displayLanguage),
                value = selectedBani.author.translate(displayLanguage),
                displayLanguage = displayLanguage
            )
            HorizontalDivider(
                color = TextPrimary.copy(alpha = 0.06f),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            BaniDetailItem(
                label = "Source Ang".translate(displayLanguage),
                value = selectedBani.ang.translateAng(displayLanguage),
                displayLanguage = displayLanguage
            )
            HorizontalDivider(
                color = TextPrimary.copy(alpha = 0.06f),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            BaniDetailItem(
                label = "Structure".translate(displayLanguage),
                value = selectedBani.structure.translate(displayLanguage),
                displayLanguage = displayLanguage
            )
            HorizontalDivider(
                color = TextPrimary.copy(alpha = 0.06f),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            BaniDetailItem(
                label = "Estimated Time".translate(displayLanguage),
                value = "${selectedBani.estimatedMinutes} ${"minutes".translate(displayLanguage)}".translateDigits(displayLanguage),
                displayLanguage = displayLanguage
            )

            selectedBani.source?.let { source ->
                HorizontalDivider(
                    color = TextPrimary.copy(alpha = 0.06f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                BaniDetailItem(
                    label = "Source".translate(displayLanguage),
                    value = source.text,
                    displayLanguage = displayLanguage
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}