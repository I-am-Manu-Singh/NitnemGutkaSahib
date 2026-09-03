package org.audiocular.nitnem.app.ui.components

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
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.app.ui.theme.Accent
import org.audiocular.nitnem.app.ui.theme.AppTheme
import org.audiocular.nitnem.app.ui.theme.Gold
import org.audiocular.nitnem.app.ui.theme.ThemeMode
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateDigits

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
 * Full screen Snake & Ladder style Gamified Spiritual Progression overlay.
 * Animates Mascot progressing along a winding track as banis are completed from 0 to Kirtan Sohila (Won/Completed).
 */
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

    // Animate mascot step position along the track
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
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crown_wiggle"
    )

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
                .background(
                    Brush.verticalGradient(
                        colors = if (isLight) listOf(
                            Color(0xFF1E1610).copy(alpha = 0.92f),
                            Color(0xFF0F0B08).copy(alpha = 0.98f)
                        ) else listOf(
                            Color(0xFF0D0B09).copy(alpha = 0.95f),
                            Color.Black
                        )
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
                .padding(top = 32.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.94f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* absorb inner clicks */ },
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLight) Color(0xFFFFFDF8) else Color(0xFF1A1714)
                ),
                border = BorderStroke(
                    2.dp,
                    Brush.linearGradient(listOf(Gold, Accent, Gold))
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ── TOP BAR: Header + Dismiss ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Gold.copy(alpha = 0.16f),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isAllNitnemDone) Icons.Default.EmojiEvents else Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Gold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = if (isAllNitnemDone) "Nitnem Completed! 🎉".translate(displayLanguage)
                                    else "Nitnem Daily Journey".translate(displayLanguage),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isLight) Color(0xFF2C1E14) else Color.White
                                )
                                Text(
                                    text = "${completedStepsCount}/7 " + "Banis Completed".translate(displayLanguage),
                                    fontSize = 11.sp,
                                    color = Accent,
                                    fontWeight = FontWeight.Bold
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

                    Spacer(Modifier.height(10.dp))

                    // ── CONGRATULATORY HERO BANNER ──
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAllNitnemDone) Gold.copy(alpha = 0.15f) else Accent.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, if (isAllNitnemDone) Gold.copy(alpha = 0.4f) else Accent.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                MascotBani(
                                    state = if (isAllNitnemDone) MascotState.Celebrating
                                    else if (completedStepsCount >= 5) MascotState.Happy
                                    else MascotState.Reading,
                                    modifier = Modifier.size(46.dp)
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
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLight) Color(0xFF3E2723) else Color.White
                                )
                                Text(
                                    text = if (isAllNitnemDone) "All 7 Banis recited today. Blissful peace & blessings!".translate(displayLanguage)
                                    else "Climb the Nitnem path step by step towards peace.".translate(displayLanguage),
                                    fontSize = 11.sp,
                                    color = if (isLight) Color(0xFF5D4037).copy(alpha = 0.8f) else Color(0xFFB0BEC5),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // ── 7-STEP SNAKE & LADDER SPIRITUAL PROGRESS BOARD ──
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.verticalGradient(
                                    if (isLight) listOf(Color(0xFFF7F3EB), Color(0xFFEFE8DA))
                                    else listOf(Color(0xFF141210), Color(0xFF221F1B))
                                )
                            )
                            .border(
                                1.dp,
                                if (isLight) Color(0xFFE0D8C8) else Color(0xFF38332E),
                                RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        // Custom Canvas connecting path curves with ladders & gold particles
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val stepHeight = h / 7f

                            // Draw connecting ladder/rail lines between steps
                            for (i in 0 until 6) {
                                val isStepDone = i < completedStepsCount
                                val y1 = h - (i + 0.5f) * stepHeight
                                val y2 = h - (i + 1.5f) * stepHeight

                                val x1 = if (i % 2 == 0) w * 0.25f else w * 0.75f
                                val x2 = if (i % 2 == 0) w * 0.75f else w * 0.25f

                                // Connecting curve
                                val path = Path().apply {
                                    moveTo(x1, y1)
                                    cubicTo(
                                        x1, y1 - (stepHeight * 0.4f),
                                        x2, y2 + (stepHeight * 0.4f),
                                        x2, y2
                                    )
                                }

                                // Background glowing track
                                drawPath(
                                    path = path,
                                    color = if (isStepDone) Gold.copy(alpha = 0.45f) else Color.Gray.copy(alpha = 0.15f),
                                    style = Stroke(
                                        width = 6.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        pathEffect = if (!isStepDone) PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f) else null
                                    )
                                )

                                // Ladder rungs for visual delight
                                if (isStepDone) {
                                    drawPath(
                                        path = path,
                                        color = Accent,
                                        style = Stroke(
                                            width = 2.5.dp.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                    )
                                }
                            }
                        }

                        // Scrollable/Distributable Column of 7 Station Tiles (from bottom=Start to top=Won)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Render from Step 7 (Top - Won) down to Step 1 (Bottom - Start)
                            DAILY_NITNEM_STEPS.reversed().forEach { step ->
                                val stepIdx = step.targetIndex
                                val isDone = stepIdx <= completedStepsCount
                                val isCurrentTarget = stepIdx == completedStepsCount + 1
                                val isAlignRight = (stepIdx % 2 == 0)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
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

                    // ── FOOTER: Action & Progress Summary ──
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
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Gold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isAllNitnemDone) "+50 XP " + "Bonus!".translate(displayLanguage)
                                    else "+5 XP " + "Per Bani".translate(displayLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Accent
                                )
                            }
                        }

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAllNitnemDone) Gold else Accent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text(
                                text = if (isAllNitnemDone) "Awesome!".translate(displayLanguage) else "Continue".translate(displayLanguage),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual milestone node in the Nitnem Snake & Ladder path.
 */
@Composable
private fun StationTile(
    step: NitnemJourneyStep,
    isDone: Boolean,
    isCurrentTarget: Boolean,
    isLight: Boolean,
    displayLanguage: DisplayLanguage
) {
    val infinite = rememberInfiniteTransition(label = "station_glow")
    val pulseScale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "target_pulse"
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = when {
            isDone -> Gold.copy(alpha = if (isLight) 0.22f else 0.28f)
            isCurrentTarget -> Accent.copy(alpha = if (isLight) 0.16f else 0.22f)
            else -> if (isLight) Color.White.copy(alpha = 0.85f) else Color(0xFF282522)
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
            .widthIn(min = 180.dp, max = 240.dp)
            .scale(if (isCurrentTarget) pulseScale else 1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Icon Badge with Status Indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isDone -> Gold
                            isCurrentTarget -> Accent.copy(alpha = 0.25f)
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
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = if (isCurrentTarget) Accent else (if (isLight) Color(0xFF8D6E63) else Color(0xFF9E9E9E)),
                        modifier = Modifier.size(18.dp)
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
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDone) Gold else Accent
                    )
                    Text(
                        text = "• ${step.period.translate(displayLanguage)}",
                        fontSize = 9.5.sp,
                        color = if (isLight) Color(0xFF795548) else Color(0xFFB0BEC5)
                    )
                }

                Text(
                    text = step.title.translate(displayLanguage),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLight) Color(0xFF3E2723) else Color.White
                )
            }

            // Mascot Avatar on current active step
            if (isCurrentTarget) {
                Box(
                    modifier = Modifier.size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MascotBani(
                        state = MascotState.Walking,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}