package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.Gold
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.utils.translateDigits
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@Composable
fun WeeklyStreakDialog(
    streakDays: Int,
    displayLanguage: DisplayLanguage,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val currentDayOfWeek = today.dayOfWeek

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Determine if the screen is wide enough for a side-by-side layout (e.g., Landscape or Tablet)
            val isWideScreen = maxWidth > 600.dp

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = if (isWideScreen) 720.dp else 440.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF171513),
                border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.35f)),
                shadowElevation = 16.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Top Right Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(32.dp)
                            .zIndex(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 28.dp)
                    ) {
                        if (isWideScreen) {
                            // ── WIDE / LANDSCAPE LAYOUT (Two Columns) ──
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Column: Emblem, Title, Counter, Action Button
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    CelebrationEmblem(glowScale, glowAlpha)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    StreakTitleSection(streakDays, displayLanguage)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    StreakCounterDisplay(streakDays, displayLanguage)

                                   
                                }

                                // Right Column: Weekly Tracker, Heatmap, Insight
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    WeeklyTrackerSection(currentDayOfWeek, streakDays, displayLanguage)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    HeatmapSection(streakDays, displayLanguage)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    InsightText(displayLanguage)

                                     Spacer(modifier = Modifier.height(16.dp))

                                    PrimaryActionButton(displayLanguage, onDismiss)
                                }
                            }
                        } else {
                            // ── PORTRAIT / COMPACT LAYOUT (Single Column) ──
                            CelebrationEmblem(glowScale, glowAlpha)

                            Spacer(modifier = Modifier.height(14.dp))

                            StreakTitleSection(streakDays, displayLanguage)

                            Spacer(modifier = Modifier.height(4.dp))

                            StreakCounterDisplay(streakDays, displayLanguage)

                            Spacer(modifier = Modifier.height(14.dp))

                            WeeklyTrackerSection(currentDayOfWeek, streakDays, displayLanguage)

                            Spacer(modifier = Modifier.height(14.dp))

                            HeatmapSection(streakDays, displayLanguage)

                            Spacer(modifier = Modifier.height(14.dp))

                            InsightText(displayLanguage)

                            Spacer(modifier = Modifier.height(20.dp))

                            PrimaryActionButton(displayLanguage, onDismiss)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CelebrationEmblem(glowScale: Float, glowAlpha: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(130.dp)
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .scale(glowScale)
                .alpha(glowAlpha)
                .background(
                    Brush.radialGradient(
                        listOf(Gold.copy(alpha = 0.45f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        NitnemLogo(
            baseSize = 105.dp,
            showShimmer = true,
            showGlow = false
        )
    }
}

@Composable
private fun StreakTitleSection(streakDays: Int, displayLanguage: DisplayLanguage) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (streakDays >= 7) "Weekly Milestone!".translate(displayLanguage) else "Daily Nitnem Streak".translate(displayLanguage),
            color = Gold,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (streakDays > 0) "You've stayed consistent for".translate(displayLanguage) else "Start your spiritual streak today!".translate(displayLanguage),
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StreakCounterDisplay(streakDays: Int, displayLanguage: DisplayLanguage) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = Color(0xFFFF7043),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        RollingNumberText(
            value = streakDays.toString().translateDigits(displayLanguage),
            color = Color.White,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Days".translate(displayLanguage),
            color = Gold,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WeeklyTrackerSection(
    currentDayOfWeek: DayOfWeek,
    streakDays: Int,
    displayLanguage: DisplayLanguage
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val daysOfWeek = listOf(
                DayOfWeek.MONDAY to "M",
                DayOfWeek.TUESDAY to "T",
                DayOfWeek.WEDNESDAY to "W",
                DayOfWeek.THURSDAY to "T",
                DayOfWeek.FRIDAY to "F",
                DayOfWeek.SATURDAY to "S",
                DayOfWeek.SUNDAY to "S"
            )

            val currentDayIndex = currentDayOfWeek.ordinal
            daysOfWeek.forEachIndexed { index, (dayEnum, label) ->
                val isToday = dayEnum == currentDayOfWeek
                val daysAgo = currentDayIndex - index
                val isCompleted = if (daysAgo == 0) {
                    streakDays > 0
                } else if (daysAgo > 0) {
                    daysAgo < streakDays
                } else {
                    false
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isToday -> Gold
                                    isCompleted -> Gold.copy(alpha = 0.35f)
                                    else -> Color.White.copy(alpha = 0.06f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isToday) Color.Black else (if (isCompleted) Gold else Color.White.copy(alpha = 0.4f)),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    if (isToday) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(Gold, CircleShape)
                        )
                    } else {
                        Spacer(Modifier.size(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeatmapSection(streakDays: Int, displayLanguage: DisplayLanguage) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "30-Day Activity Heatmap".translate(displayLanguage),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Text(
                    text = "${streakDays.coerceAtMost(30)} / 30 " + "Days".translate(displayLanguage),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until 5) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (col in 0 until 6) {
                            val dayIndex = (row * 6) + col
                            val isHeatmapActive = dayIndex >= (30 - streakDays.coerceIn(0, 30))
                            val cellAlpha = if (isHeatmapActive) 0.85f else 0.08f
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (isHeatmapActive) Gold.copy(alpha = cellAlpha)
                                        else Color.White.copy(alpha = cellAlpha)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightText(displayLanguage: DisplayLanguage) {
    Text(
        text = "Keep up the daily Nitnem to grow your streak, sync today's count, and earn more XP.".translate(displayLanguage),
        color = Color.White.copy(alpha = 0.65f),
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp),
        lineHeight = 16.sp
    )
}

@Composable
private fun PrimaryActionButton(displayLanguage: DisplayLanguage, onDismiss: () -> Unit) {
    Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(
            containerColor = Gold,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(
            text = "Continue".translate(displayLanguage),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}