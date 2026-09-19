package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.Accent
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.AppTheme
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.Gold
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.ThemeMode
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.utils.translateDigits

/**
 * Metadata representation of a step in the daily Nitnem spiritual progression board.
 */
data class NitnemJourneyStep(
    val id: String,
    val title: String,
    val period: String, // "Amrit Vela", "Evening", "Night"
    val icon: ImageVector,
    val targetIndex: Int // 1 to 7
)

val DAILY_NITNEM_STEPS = listOf(
    NitnemJourneyStep(
        id = "japji-sahib",
        title = "Japji Sahib",
        period = "Amrit Vela",
        icon = Icons.Default.WbSunny,
        targetIndex = 1
    ),
    NitnemJourneyStep(
        id = "jaap-sahib",
        title = "Jaap Sahib",
        period = "Amrit Vela",
        icon = Icons.Default.Shield,
        targetIndex = 2
    ),
    NitnemJourneyStep(
        id = "tav-prasad-savaiye",
        title = "Tav Prasad Savaiye",
        period = "Amrit Vela",
        icon = Icons.Default.AutoAwesome,
        targetIndex = 3
    ),
    NitnemJourneyStep(
        id = "benti-chaupai-sahib",
        title = "Chaupai Sahib",
        period = "Amrit Vela",
        icon = Icons.Default.Security,
        targetIndex = 4
    ),
    NitnemJourneyStep(
        id = "anand-sahib",
        title = "Anand Sahib & Ardas",
        period = "Amrit Vela",
        icon = Icons.Default.Celebration,
        targetIndex = 5
    ),
    NitnemJourneyStep(
        id = "rehras-sahib",
        title = "Rehras & Ardas",
        period = "Evening",
        icon = Icons.Default.WbTwilight,
        targetIndex = 6
    ),
    NitnemJourneyStep(
        id = "kirtan-sohila",
        title = "Kirtan Sohila",
        period = "Night",
        icon = Icons.Default.NightsStay,
        targetIndex = 7
    )
)

/**
 * Full-screen edge-to-edge Snake & Ladder Spiritual Journey overlay.
 * Animates Mascot smoothly progressing along a winding celestial track
 * from 0 (Amrit Vela) to Kirtan Sohila (Victory/Completed).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaniCompletionMascotOverlay(
    baniTitle: String? = null,
    streakDayCount: Int? = null,
    completedTodayCount: Int = 0,
    totalNitnemCount: Int = 7,
    completedBaniIds: Set<String> = emptySet(),
    displayLanguage: DisplayLanguage,
    onDismiss: () -> Unit
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val isStreakCelebration = streakDayCount != null

    // Determine highest completed step index (0 to 7)
    val completedStepsCount = remember(completedBaniIds, completedTodayCount) {
        val matchingInDaily = DAILY_NITNEM_STEPS.count { it.id in completedBaniIds }
        if (matchingInDaily > 0) matchingInDaily else completedTodayCount.coerceIn(0, 7)
    }

    val isAllNitnemDone = completedStepsCount >= 7

    // Animate mascot step position smoothly along track
    val animatedMascotStep by animateFloatAsState(
        targetValue = completedStepsCount.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mascot_step_anim"
    )

    // Ambient floating / pulsing effects
    val infiniteTransition = rememberInfiniteTransition(label = "board_ambient")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    val winnerCrownRotate by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crown_wiggle"
    )

    val mascotFloatY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_float"
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isLight) Color(0xFFFFFBF5) else Color(0xFF0F0E0D),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        // True edge-to-edge full screen container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isLight) listOf(
                            Color(0xFFFFFBF5),
                            Color(0xFFF5EFEB),
                            Color(0xFFEDE3D8)
                        ) else listOf(
                            Color(0xFF0F0E0D),
                            Color(0xFF171412),
                            Color(0xFF0A0908)
                        )
                    )
                )
        ) {
            // Background celestial constellation particles
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val stars = listOf(
                    Offset(w * 0.12f, h * 0.15f),
                    Offset(w * 0.88f, h * 0.18f),
                    Offset(w * 0.20f, h * 0.40f),
                    Offset(w * 0.82f, h * 0.45f),
                    Offset(w * 0.15f, h * 0.70f),
                    Offset(w * 0.85f, h * 0.75f),
                    Offset(w * 0.50f, h * 0.10f),
                    Offset(w * 0.50f, h * 0.85f)
                )
                stars.forEach { pos ->
                    drawCircle(
                        color = Gold.copy(alpha = 0.25f),
                        radius = 2.5.dp.toPx(),
                        center = pos
                    )
                    drawCircle(
                        color = Gold.copy(alpha = 0.08f),
                        radius = 8.dp.toPx() * auraScale,
                        center = pos
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── 1. TOP APP BAR ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Gold.copy(alpha = 0.18f),
                            modifier = Modifier.size(40.dp),
                            border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isAllNitnemDone) Icons.Default.EmojiEvents else Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Gold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isAllNitnemDone) "Nitnem Completed! 🎉".translate(displayLanguage)
                                else "Nitnem Daily Journey".translate(displayLanguage),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isLight) Color(0xFF2C1E14) else Color.White
                            )
                            Text(
                                text = "${completedStepsCount}/7 " + "Banis Completed".translate(displayLanguage),
                                fontSize = 12.sp,
                                color = Accent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isLight) Color.Black.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isLight) Color(0xFF4E342E) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // ── 2. CELEBRATION HERO BANNER ──
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = if (isAllNitnemDone) Gold.copy(alpha = 0.16f) else Accent.copy(alpha = 0.09f),
                    border = BorderStroke(1.dp, if (isAllNitnemDone) Gold.copy(alpha = 0.45f) else Accent.copy(alpha = 0.22f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .offset(y = mascotFloatY.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            MascotBani(
                                state = if (isAllNitnemDone) MascotState.Celebrating
                                else if (completedStepsCount >= 4) MascotState.Happy
                                else MascotState.Reading,
                                modifier = Modifier.size(52.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    isStreakCelebration -> "🔥 ${streakDayCount} Day Streak Active!".translate(displayLanguage)
                                    isAllNitnemDone -> "Waheguru Ji! You Won Today's Journey!".translate(displayLanguage)
                                    baniTitle != null -> "${baniTitle.translate(displayLanguage)} Completed!"
                                    else -> "Advancing on Spiritual Ladder".translate(displayLanguage)
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLight) Color(0xFF3E2723) else Color.White
                            )
                            Text(
                                text = if (isAllNitnemDone) "All 7 Banis recited today. Blissful peace & blessings!".translate(displayLanguage)
                                else "Climb the Nitnem path step by step towards peace.".translate(displayLanguage),
                                fontSize = 11.5.sp,
                                color = if (isLight) Color(0xFF5D4037).copy(alpha = 0.85f) else Color(0xFFB0BEC5),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ── 3. FULL-SCREEN SNAKE & LADDER SPIRITUAL PROGRESS BOARD ──
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(
                            Brush.verticalGradient(
                                if (isLight) listOf(Color(0xFFFAF6EE), Color(0xFFF1EADB))
                                else listOf(Color(0xFF141210), Color(0xFF221F1B))
                            )
                        )
                        .border(
                            1.dp,
                            if (isLight) Color(0xFFE0D8C8) else Color(0xFF38332E),
                            RoundedCornerShape(26.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    // Custom Canvas connecting path curves with ladders & gold particles
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val stepHeight = h / 7f

                        // Draw connecting curved tracks between stations (Step 1 at bottom to Step 7 at top)
                        for (i in 0 until 6) {
                            val isStepDone = i < completedStepsCount
                            val y1 = h - (i + 0.5f) * stepHeight
                            val y2 = h - (i + 1.5f) * stepHeight

                            val x1 = if (i % 2 == 0) w * 0.22f else w * 0.78f
                            val x2 = if (i % 2 == 0) w * 0.78f else w * 0.22f

                            val path = Path().apply {
                                moveTo(x1, y1)
                                cubicTo(
                                    x1, y1 - (stepHeight * 0.45f),
                                    x2, y2 + (stepHeight * 0.45f),
                                    x2, y2
                                )
                            }

                            // Track stroke
                            drawPath(
                                path = path,
                                color = if (isStepDone) Gold.copy(alpha = 0.55f) else Color.Gray.copy(alpha = 0.18f),
                                style = Stroke(
                                    width = 7.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    pathEffect = if (!isStepDone) PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f) else null
                                )
                            )

                            if (isStepDone) {
                                drawPath(
                                    path = path,
                                    color = Accent,
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                )
                            }
                        }
                    }

                    // Column of 7 Station Tiles (from Step 7 Top to Step 1 Bottom)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        DAILY_NITNEM_STEPS.reversed().forEach { step ->
                            val stepIdx = step.targetIndex
                            val isDone = step.id in completedBaniIds
                            // Current target is the first incomplete step
                            val nextTargetStep = DAILY_NITNEM_STEPS.firstOrNull { it.id !in completedBaniIds }
                            val isCurrentTarget = nextTargetStep?.id == step.id
                            val isAlignRight = (stepIdx % 2 == 0)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                horizontalArrangement = if (isAlignRight) Arrangement.End else Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StationTile(
                                    step = step,
                                    isDone = isDone,
                                    isCurrentTarget = isCurrentTarget,
                                    isLight = isLight,
                                    displayLanguage = displayLanguage
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ── 4. FOOTER: Quick Progress Summary & Close ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Accent.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Accent.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Gold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${completedStepsCount * 100} XP " + "Earned Today".translate(displayLanguage),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLight) Color(0xFF2C1E14) else Color.White
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isAllNitnemDone) "Waheguru 🙏".translate(displayLanguage) else "Continue Paath".translate(displayLanguage),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * High-polish station tile on the spiritual board.
 */
@Composable
private fun StationTile(
    step: NitnemJourneyStep,
    isDone: Boolean,
    isCurrentTarget: Boolean,
    isLight: Boolean,
    displayLanguage: DisplayLanguage
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tile_target_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "target_pulse"
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = when {
            isDone -> Gold.copy(alpha = if (isLight) 0.64f else 0.70f)
            isCurrentTarget -> Accent.copy(alpha = if (isLight) 0.58f else 0.64f)
            else -> if (isLight) Color.White.copy(alpha = 0.90f) else Color(0xFF282522)
        },
        border = BorderStroke(
            width = if (isCurrentTarget) 2.dp else 1.dp,
            color = when {
                isDone -> Gold
                isCurrentTarget -> Accent
                else -> if (isLight) Color(0xFFD7CCC8) else Color(0xFF423D38)
            }
        ),
        modifier = Modifier
            .widthIn(min = 200.dp, max = 260.dp)
            .scale(if (isCurrentTarget) pulseScale else 1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon Badge with Status Indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isDone -> Gold
                            isCurrentTarget -> Accent.copy(alpha = 0.28f)
                            else -> if (isLight) Color(0xFFEFEBE9) else Color(0xFF3A3530)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = if (isCurrentTarget) Accent else (if (isLight) Color(0xFF8D6E63) else Color(0xFF9E9E9E)),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "#${step.targetIndex}".translateDigits(displayLanguage),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDone) Gold else Accent
                    )
                    Text(
                        text = "• ${step.period.translate(displayLanguage)}",
                        fontSize = 10.sp,
                        color = if (isLight) Color(0xFF795548) else Color(0xFFB0BEC5)
                    )
                }

                Text(
                    text = step.title.translate(displayLanguage),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF3E2723) else Color.White
                )
            }

            // Animated Mascot walking/reading next to the active step
            if (isCurrentTarget) {
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MascotBani(
                        state = MascotState.Walking,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
    }
}