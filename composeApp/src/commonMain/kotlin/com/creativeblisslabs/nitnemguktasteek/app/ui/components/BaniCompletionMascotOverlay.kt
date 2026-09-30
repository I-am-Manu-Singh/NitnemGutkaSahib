package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    fun isStepCompleted(stepId: String): Boolean {
        return stepId in completedBaniIds ||
                stepId.replace("-", "_") in completedBaniIds ||
                stepId.replace("_", "-") in completedBaniIds ||
                (stepId == "benti-chaupai-sahib" && ("chaupai-sahib" in completedBaniIds || "chaupai_sahib" in completedBaniIds)) ||
                (stepId == "anand-sahib" && ("anand_sahib" in completedBaniIds || "anand_sahib_short" in completedBaniIds))
    }

    val actualCompletedCount = remember(completedBaniIds, completedTodayCount) {
        val matchingInDaily = DAILY_NITNEM_STEPS.count { isStepCompleted(it.id) }
        if (matchingInDaily > 0) matchingInDaily else completedTodayCount.coerceIn(0, 7)
    }

    val completedStepsCount = actualCompletedCount
    val isAllNitnemDone = completedStepsCount >= 7

    // Animate the step index smoothly when the user finishes a step
    val animatedMascotStep by animateFloatAsState(
        targetValue = completedStepsCount.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "mascot_step_anim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "board_ambient")
    val mascotFloatY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_float"
    )

    val sheetBgColor = if (isLight) Color(0xFFFFFDF9) else Color(0xFF13110F)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBgColor,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        properties = ModalBottomSheetDefaults.properties()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (isLight) listOf(
                            Color(0xFFFFFDF9),
                            Color(0xFFF7F2EC),
                            Color(0xFFEFE8DE)
                        ) else listOf(
                            Color(0xFF13110F),
                            Color(0xFF1A1715),
                            Color(0xFF0E0C0B)
                        )
                    )
                )
        ) {
            val isLandscapeOrWide = maxWidth > 640.dp || (maxWidth > maxHeight && maxWidth > 500.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = if (isLandscapeOrWide) 20.dp else 16.dp,
                        end = if (isLandscapeOrWide) 20.dp else 16.dp,
                        bottom = 12.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Bar
                HeaderBar(
                    isAllNitnemDone = isAllNitnemDone,
                    completedStepsCount = completedStepsCount,
                    displayLanguage = displayLanguage,
                    isLight = isLight,
                    onDismiss = onDismiss
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isLandscapeOrWide) {


                    // Landscape / Tablet Split Layout
                    Box(modifier = Modifier.weight(1f)) {
                        CelebrationHeroBanner(
                            isStreakCelebration = isStreakCelebration,
                            isAllNitnemDone = isAllNitnemDone,
                            streakDayCount = streakDayCount,
                            baniTitle = baniTitle,
                            mascotFloatY = mascotFloatY,
                            isLight = isLight,
                            displayLanguage = displayLanguage
                        )
                    }
                    // Horizontal Rail Board View for Landscape
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        HorizontalSpiritualBoard(
                            isStepCompleted = ::isStepCompleted,
                            animatedMascotStep = animatedMascotStep,
                            isAllNitnemDone = isAllNitnemDone,
                            mascotFloatY = mascotFloatY,
                            isLight = isLight,
                            displayLanguage = displayLanguage
                        )
                    }
                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        // FIX: This forces the spaced items to cluster in the absolute center of the Row
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        ProgressXpCardCompact(
                            completedStepsCount = completedStepsCount,
                            displayLanguage = displayLanguage,
                            isLight = isLight
                        )

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Accent),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = if (isAllNitnemDone) "Waheguru 🙏".translate(displayLanguage) else "Continue Paath".translate(displayLanguage),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                        }
                    }


                } else {
                    // Mobile Portrait Layout
                    CelebrationHeroBanner(
                        isStreakCelebration = isStreakCelebration,
                        isAllNitnemDone = isAllNitnemDone,
                        streakDayCount = streakDayCount,
                        baniTitle = baniTitle,
                        mascotFloatY = mascotFloatY,
                        isLight = isLight,
                        displayLanguage = displayLanguage
                    )

                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        VerticalSpiritualBoard(
                            isStepCompleted = ::isStepCompleted,
                            animatedMascotStep = animatedMascotStep,
                            isAllNitnemDone = isAllNitnemDone,
                            mascotFloatY = mascotFloatY,
                            isLight = isLight,
                            displayLanguage = displayLanguage
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    FooterBar(
                        completedStepsCount = completedStepsCount,
                        isAllNitnemDone = isAllNitnemDone,
                        displayLanguage = displayLanguage,
                        isLight = isLight,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}

/**
 * Top App Header Bar
 */
@Composable
private fun HeaderBar(
    isAllNitnemDone: Boolean,
    completedStepsCount: Int,
    displayLanguage: DisplayLanguage,
    isLight: Boolean,
    onDismiss: () -> Unit
) {
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
                color = Gold.copy(alpha = 0.20f),
                modifier = Modifier.size(38.dp),
                border = BorderStroke(1.dp, Gold.copy(alpha = 0.45f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isAllNitnemDone) Icons.Default.EmojiEvents else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column {
                Text(
                    text = if (isAllNitnemDone) "Nitnem Completed! 🎉".translate(displayLanguage)
                    else "Nitnem Daily Journey".translate(displayLanguage),
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isLight) Color(0xFF2C1E14) else Color.White
                )
                Text(
                    text = "${completedStepsCount}/7 " + "Banis Completed".translate(displayLanguage),
                    fontSize = 11.5.sp,
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
                .background(if (isLight) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.09f))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = if (isLight) Color(0xFF4E342E) else Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Top celebration status banner.
 */
@Composable
private fun CelebrationHeroBanner(
    isStreakCelebration: Boolean,
    isAllNitnemDone: Boolean,
    streakDayCount: Int?,
    baniTitle: String?,
    mascotFloatY: Float,
    isLight: Boolean,
    displayLanguage: DisplayLanguage
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (isAllNitnemDone) Gold.copy(alpha = 0.16f) else Accent.copy(alpha = 0.08f),
        border = BorderStroke(
            1.dp,
            if (isAllNitnemDone) Gold.copy(alpha = 0.45f) else Accent.copy(alpha = 0.22f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .offset(y = mascotFloatY.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Gold.copy(alpha = 0.22f),
                    border = BorderStroke(1.2.dp, Gold),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when {
                                isAllNitnemDone -> Icons.Default.EmojiEvents
                                isStreakCelebration -> Icons.Default.LocalFireDepartment
                                else -> Icons.Default.Stars
                            },
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
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
                    color = if (isLight) Color(0xFF6D4C41) else Color(0xFFB0BEC5),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

/**
 * Landscape / Wide horizontal track:
 * Cards are laid out along a horizontally winding dotted path with the mascot animating along the curves.
 */
@Composable
private fun HorizontalSpiritualBoard(
    isStepCompleted: (String) -> Boolean,
    animatedMascotStep: Float,
    isAllNitnemDone: Boolean,
    mascotFloatY: Float,
    isLight: Boolean,
    displayLanguage: DisplayLanguage
) {
    val density = LocalDensity.current
    val totalSteps = 7
    val cardWidth = 190.dp
    val cardSpacing = 24.dp

    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    if (isLight) listOf(Color(0xFFFAF7F2), Color(0xFFF1ECE4))
                    else listOf(Color(0xFF181513), Color(0xFF221F1C))
                )
            )
            .border(
                1.dp,
                if (isLight) Color(0xFFE2DCD2) else Color(0xFF36322E),
                RoundedCornerShape(20.dp)
            )
    ) {
        val boardHeightPx = constraints.maxHeight.toFloat()
        val cardWidthPx = with(density) { cardWidth.toPx() }
        val cardSpacingPx = with(density) { cardSpacing.toPx() }
        val totalTrackWidthPx = totalSteps * cardWidthPx + (totalSteps - 1) * cardSpacingPx + with(density) { 32.dp.toPx() }

        // Compute coordinate centers for horizontal layout
        val stepAnchors = remember(boardHeightPx, totalTrackWidthPx) {
            val startXPx = with(density) { 16.dp.toPx() } + cardWidthPx / 2f
            List(totalSteps) { idx ->
                val x = startXPx + idx * (cardWidthPx + cardSpacingPx)
                // Alternate height slightly to create a wave path
                val y = if (idx % 2 == 0) boardHeightPx * 0.35f else boardHeightPx * 0.65f
                Offset(x, y)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(with(density) { totalTrackWidthPx.toDp() })
                .horizontalScroll(scrollState)
        ) {
            // Draw continuous dotted / filled curves connecting the cards
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(with(density) { totalTrackWidthPx.toDp() })
            ) {
                for (i in 0 until totalSteps - 1) {
                    val p0 = stepAnchors[i]
                    val p3 = stepAnchors[i + 1]
                    val isStepDone = isStepCompleted(DAILY_NITNEM_STEPS[i].id)

                    val midX = (p0.x + p3.x) / 2f
                    val path = Path().apply {
                        moveTo(p0.x, p0.y)
                        cubicTo(
                            midX, p0.y,
                            midX, p3.y,
                            p3.x, p3.y
                        )
                    }

                    // Background dashed line
                    drawPath(
                        path = path,
                        color = if (isStepDone) Gold.copy(alpha = 0.5f) else (if (isLight) Color(0xFFD3C8BA) else Color(0xFF453F3A)),
                        style = Stroke(
                            width = 4.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = if (!isStepDone) PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f) else null
                        )
                    )

                    // Active completed path
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

            // Cards positioned horizontally along the wave
            DAILY_NITNEM_STEPS.forEachIndexed { idx, step ->
                val isDone = isStepCompleted(step.id)
                val nextTargetStep = DAILY_NITNEM_STEPS.firstOrNull { !isStepCompleted(it.id) }
                val isCurrentTarget = nextTargetStep?.id == step.id
                val anchor = stepAnchors[idx]

                Box(
                    modifier = Modifier
                        .offset(
                            x = with(density) { (anchor.x - cardWidthPx / 2f).toDp() },
                            y = with(density) { (anchor.y - 36.dp.toPx()).toDp() }
                        )
                        .width(cardWidth)
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

            // Animated mascot gliding along the horizontal spline
            if (stepAnchors.isNotEmpty()) {
                val currentProgress = animatedMascotStep.coerceIn(0f, (totalSteps - 1).toFloat() - 0.001f)
                val lowerIndex = currentProgress.toInt()
                val t = currentProgress - lowerIndex

                val p0 = stepAnchors[lowerIndex]
                val p3 = stepAnchors[lowerIndex + 1]
                val midX = (p0.x + p3.x) / 2f
                val p1 = Offset(midX, p0.y)
                val p2 = Offset(midX, p3.y)

                // Cubic Bezier formula: B(t) = (1-t)^3*p0 + 3(1-t)^2*t*p1 + 3(1-t)*t^2*p2 + t^3*p3
                val oneMinusT = 1f - t
                val mascotX = (oneMinusT * oneMinusT * oneMinusT * p0.x) +
                        (3f * oneMinusT * oneMinusT * t * p1.x) +
                        (3f * oneMinusT * t * t * p2.x) +
                        (t * t * t * p3.x)
                val mascotY = (oneMinusT * oneMinusT * oneMinusT * p0.y) +
                        (3f * oneMinusT * oneMinusT * t * p1.y) +
                        (3f * oneMinusT * t * t * p2.y) +
                        (t * t * t * p3.y)

                Box(
                    modifier = Modifier
                        .offset(
                            x = with(density) { (mascotX - 20.dp.toPx()).toDp() },
                            y = with(density) { (mascotY - 20.dp.toPx() + mascotFloatY.dp.toPx()).toDp() }
                        )
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Gold.copy(alpha = 0.25f))
                        .border(1.5.dp, Gold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    MascotBani(
                        state = if (isAllNitnemDone) MascotState.Celebrating else MascotState.Walking,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

/**
 * Mobile Portrait snake-and-ladder layout with the mascot gliding along the vertical curves.
 */
@Composable
private fun VerticalSpiritualBoard(
    isStepCompleted: (String) -> Boolean,
    animatedMascotStep: Float,
    isAllNitnemDone: Boolean,
    mascotFloatY: Float,
    isLight: Boolean,
    displayLanguage: DisplayLanguage
) {
    val density = LocalDensity.current
    val totalSteps = 7

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    if (isLight) listOf(Color(0xFFFAF7F2), Color(0xFFF1ECE4))
                    else listOf(Color(0xFF181513), Color(0xFF221F1C))
                )
            )
            .border(
                1.dp,
                if (isLight) Color(0xFFE2DCD2) else Color(0xFF36322E),
                RoundedCornerShape(20.dp)
            )
            .padding(10.dp)
    ) {
        val boardWidthPx = constraints.maxWidth.toFloat()
        val boardHeightPx = constraints.maxHeight.toFloat()
        val stepHeightPx = boardHeightPx / totalSteps.toFloat()

        // Card center anchors
        val stepAnchors = remember(boardWidthPx, boardHeightPx) {
            List(totalSteps) { idx ->
                val isAlignRight = (idx + 1) % 2 == 0
                val x = if (isAlignRight) boardWidthPx - with(density) { 34.dp.toPx() }
                else with(density) { 34.dp.toPx() }
                val y = boardHeightPx - (idx + 0.5f) * stepHeightPx
                Offset(x, y)
            }
        }

        // Draw curved dotted track connecting card anchors
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0 until totalSteps - 1) {
                val start = stepAnchors[i]
                val end = stepAnchors[i + 1]
                val isStepDone = isStepCompleted(DAILY_NITNEM_STEPS[i].id)

                val controlYOffset = stepHeightPx * 0.45f
                val path = Path().apply {
                    moveTo(start.x, start.y)
                    cubicTo(
                        start.x, start.y - controlYOffset,
                        end.x, end.y + controlYOffset,
                        end.x, end.y
                    )
                }

                drawPath(
                    path = path,
                    color = if (isStepDone) Gold.copy(alpha = 0.55f) else (if (isLight) Color(0xFFD3C8BA) else Color(0xFF453F3A)),
                    style = Stroke(
                        width = 4.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = if (!isStepDone) PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f) else null
                    )
                )

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

        // Card node hierarchy
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            DAILY_NITNEM_STEPS.reversed().forEach { step ->
                val isDone = isStepCompleted(step.id)
                val nextTargetStep = DAILY_NITNEM_STEPS.firstOrNull { !isStepCompleted(it.id) }
                val isCurrentTarget = nextTargetStep?.id == step.id
                val isAlignRight = (step.targetIndex % 2 == 0)

                Row(
                    modifier = Modifier.fillMaxWidth(),
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

        // Animated mascot sliding along the vertical path
        if (boardHeightPx > 0 && boardWidthPx > 0 && stepAnchors.isNotEmpty()) {
            val currentProgress = animatedMascotStep.coerceIn(0f, (totalSteps - 1).toFloat() - 0.001f)
            val lowerIndex = currentProgress.toInt()
            val t = currentProgress - lowerIndex

            val p0 = stepAnchors[lowerIndex]
            val p3 = stepAnchors[lowerIndex + 1]
            val cyOffset = stepHeightPx * 0.45f
            val p1 = Offset(p0.x, p0.y - cyOffset)
            val p2 = Offset(p3.x, p3.y + cyOffset)

            val oneMinusT = 1f - t
            val mascotX = (oneMinusT * oneMinusT * oneMinusT * p0.x) +
                    (3f * oneMinusT * oneMinusT * t * p1.x) +
                    (3f * oneMinusT * t * t * p2.x) +
                    (t * t * t * p3.x)
            val mascotY = (oneMinusT * oneMinusT * oneMinusT * p0.y) +
                    (3f * oneMinusT * oneMinusT * t * p1.y) +
                    (3f * oneMinusT * t * t * p2.y) +
                    (t * t * t * p3.y)

            Box(
                modifier = Modifier
                    .offset(
                        x = with(density) { (mascotX - 20.dp.toPx()).toDp() },
                        y = with(density) { (mascotY - 20.dp.toPx() + mascotFloatY.dp.toPx()).toDp() }
                    )
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Gold.copy(alpha = 0.25f))
                    .border(1.5.dp, Gold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                MascotBani(
                    state = if (isAllNitnemDone) MascotState.Celebrating else MascotState.Walking,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * Spiritual Station Tile Card.
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
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "target_pulse"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = when {
            isDone -> if (isLight) Color(0xFFF9EED9) else Color(0xFF33291F)
            isCurrentTarget -> if (isLight) Color(0xFFE8F3FD) else Color(0xFF1E2833)
            else -> if (isLight) Color.White.copy(alpha = 0.95f) else Color(0xFF262320)
        },
        border = BorderStroke(
            width = if (isCurrentTarget) 1.6.dp else 1.dp,
            color = when {
                isDone -> Gold.copy(alpha = 0.85f)
                isCurrentTarget -> Accent
                else -> if (isLight) Color(0xFFDED8CE) else Color(0xFF3D3833)
            }
        ),
        modifier = Modifier
            .widthIn(min = 175.dp, max = 220.dp)
            .scale(if (isCurrentTarget) pulseScale else 1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isDone -> Gold
                            isCurrentTarget -> Accent.copy(alpha = 0.25f)
                            else -> if (isLight) Color(0xFFECE6DD) else Color(0xFF36322E)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = if (isCurrentTarget) Accent else (if (isLight) Color(0xFF8D6E63) else Color(0xFF9E9E9E)),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "#${step.targetIndex}".translateDigits(displayLanguage),
                        fontSize = 10.5.sp,
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
        }
    }
}

/**
 * Compact XP display card for landscape mode.
 */
@Composable
private fun ProgressXpCardCompact(
    completedStepsCount: Int,
    displayLanguage: DisplayLanguage,
    isLight: Boolean
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Accent.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, Accent.copy(alpha = 0.28f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
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
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLight) Color(0xFF2C1E14) else Color.White
            )
        }
    }
}

/**
 * Portrait bottom sheet footer with quick summary & dismiss button.
 */
@Composable
private fun FooterBar(
    completedStepsCount: Int,
    isAllNitnemDone: Boolean,
    displayLanguage: DisplayLanguage,
    isLight: Boolean,
    onDismiss: () -> Unit
) {
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
                    fontSize = 12.sp,
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
                fontSize = 13.sp
            )
        }
    }
}