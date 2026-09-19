package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyStreakDialog(
    streakDays: Int,
    displayLanguage: DisplayLanguage,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Calculate current day of the week to highlight today and active weekly progress
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val currentDayOfWeek = today.dayOfWeek // DayOfWeek enum (MONDAY..SUNDAY)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color(0xFF1A1A1A),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                    // Celebration Glow
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(glowScale)
                            .alpha(glowAlpha)
                            .background(Gold.copy(alpha = 0.3f), CircleShape)
                    )

                    NitnemLogo(
                        baseSize = 130.dp,
                        showShimmer = true,
                        showGlow = false
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (streakDays >= 7) "Weekly Streak!".translate(displayLanguage) else "Daily Nitnem Streak".translate(displayLanguage),
                    color = Gold,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (streakDays > 0) "You've stayed consistent for".translate(displayLanguage) else "Start your spiritual streak today!".translate(displayLanguage),
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    RollingNumberText(
                        value = streakDays.toString().translateDigits(displayLanguage),
                        color = Color.White,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Days".translate(displayLanguage),
                        color = Gold,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // ---------------------------------------------------------
                // CURRENT WEEK DAYS ROW (Mon - Sun layout with Today indicator)
                // ---------------------------------------------------------
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
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

                    daysOfWeek.forEachIndexed { index, (dayEnum, label) ->
                        val isToday = dayEnum == currentDayOfWeek
                        val isCompleted = dayEnum.ordinal <= currentDayOfWeek.ordinal && 
                                          (currentDayOfWeek.ordinal - dayEnum.ordinal) < streakDays

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
                                            isCompleted -> Gold.copy(alpha = 0.25f)
                                            else -> Color.White.copy(alpha = 0.08f)
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
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ---------------------------------------------------------
                // MONTHLY 30-DAY NITNEM HEATMAP GRID
                // ---------------------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .padding(12.dp)
                ) {
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
                            text = "${(streakDays).coerceAtMost(30)} / 30 " + "Days".translate(displayLanguage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 5 rows x 6 cols = 30-day heatmap grid
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (row in 0 until 5) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (col in 0 until 6) {
                                    val dayIndex = (row * 6) + col
                                    val isHeatmapActive = dayIndex >= (30 - streakDays.coerceIn(0, 30))
                                    val cellAlpha = if (isHeatmapActive) 0.85f else 0.08f
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 2.dp)
                                            .height(14.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isHeatmapActive) Gold.copy(alpha = cellAlpha) else Color.White.copy(alpha = cellAlpha))
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Keep up the daily Nitnem to grow your streak, sync today's count, and earn more XP.".translate(displayLanguage),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text(
                        text = "Continue".translate(displayLanguage),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
        }
    }
}