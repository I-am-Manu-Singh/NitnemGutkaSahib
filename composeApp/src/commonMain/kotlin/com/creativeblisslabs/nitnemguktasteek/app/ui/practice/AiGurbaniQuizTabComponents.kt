package com.creativeblisslabs.nitnemguktasteek.app.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.utils.translateDigits
import com.creativeblisslabs.nitnemguktasteek.utils.translateHindiTransliteration
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState
import com.creativeblisslabs.nitnemguktasteek.viewmodel.NitnemViewModel
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.MascotBani
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.MascotState
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.RollingNumberText
import com.creativeblisslabs.nitnemguktasteek.models.QuizQuestion
import com.creativeblisslabs.nitnemguktasteek.models.QuizSession
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.AppTheme
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.ThemeMode
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.Gold
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.Accent

/*
 * ─────────────────────────────────────────────────────────────
 * REUSABLE COMPONENTS
 * ─────────────────────────────────────────────────────────────
 */

@Composable
fun QuizSectionLabel(
    title: String,
    color: Color
) {

    Text(

        text = title,

        color = color,

        fontSize = 13.sp,

        fontWeight = FontWeight.Bold
    )
}

@Composable
fun QuizChoiceChip(
    text: String,
    selected: Boolean,
    answered: Boolean,
    correct: Boolean,
    enabled: Boolean,
    colors: PracticeColors,
    onClick: () -> Unit
) {

    val isLight = AppTheme.themeMode == ThemeMode.Light

    val background = when {
        answered && correct -> {
            colors.success.copy(alpha = if (isLight) 0.16f else 0.24f)
        }

        answered && selected && !correct -> {
            colors.error.copy(alpha = if (isLight) 0.16f else 0.24f)
        }

        else -> {
            if (isLight) {
                Color.White.copy(alpha = 0.90f)
            } else {
                colors.cardSecondary.copy(
                    alpha = 0.34f
                )
            }
        }
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = background,
        border = BorderStroke(
            1.dp,
            if (isLight) {
                if (answered && correct) colors.success.copy(alpha = 0.5f)
                else if (answered && selected) colors.error.copy(alpha = 0.5f)
                else colors.accent.copy(alpha = 0.16f)
            } else {
                colors.textSecondary.copy(alpha = 0.08f)
            }
        ),
        modifier = Modifier.clickable(
            enabled = enabled,
            onClick = onClick
        )
    ) {

        Text(

            text = text,

            color =
                colors.textPrimary,

            fontWeight =
                FontWeight.SemiBold,

            modifier =
                Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 14.dp
                )
        )
    }
}

@Composable
fun QuizOptionCard(
    text: String,
    selected: Boolean,
    answered: Boolean,
    correct: Boolean,
    colors: PracticeColors,
    onClick: () -> Unit
) {

    val background = when {

        answered && correct -> {
            colors.success.copy(alpha = 0.24f)
        }

        answered && selected && !correct -> {
            colors.error.copy(alpha = 0.24f)
        }

        else -> {

            colors.cardSecondary.copy(
                alpha = 0.32f
            )
        }
    }

    Surface(

        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = !answered,
                onClick = onClick
            ),

        shape =
            RoundedCornerShape(28.dp),

        color = background,

        border = BorderStroke(
            1.dp,
            colors.textSecondary.copy(
                alpha = 0.08f
            )
        )
    ) {

        Text(

            text = text,

            color =
                colors.textPrimary,

            fontSize = 16.sp,

            lineHeight = 26.sp,

            fontWeight =
                FontWeight.Medium,

            modifier =
                Modifier.padding(20.dp)
        )
    }
}

@Composable
fun QuizBinaryButton(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    answered: Boolean,
    correct: Boolean,
    colors: PracticeColors,
    onClick: () -> Unit
) {

    val background = when {

        answered && correct -> {
            colors.success.copy(alpha = 0.24f)
        }

        answered && selected && !correct -> {
            colors.error.copy(alpha = 0.24f)
        }

        else -> {

            colors.cardSecondary.copy(
                alpha = 0.32f
            )
        }
    }

    Surface(

        modifier = modifier
            .clickable(
                enabled = !answered,
                onClick = onClick
            ),

        shape =
            RoundedCornerShape(26.dp),

        color = background,

        border = BorderStroke(
            1.dp,
            colors.textSecondary.copy(
                alpha = 0.08f
            )
        )
    ) {

        Text(

            text = text,

            color =
                colors.textPrimary,

            textAlign =
                TextAlign.Center,

            fontWeight =
                FontWeight.Bold,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 18.dp
                    )
        )
    }
}

@Composable
private fun PremiumResultStat(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accent: Color,
    colors: PracticeColors
) {

    Surface(

        modifier = modifier,

        shape =
            RoundedCornerShape(24.dp),

        color =
            accent.copy(alpha = 0.12f)
    ) {

        Column(

            modifier =
                Modifier.padding(18.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(

                text = value,

                color = accent,

                fontSize = 28.sp,

                fontWeight =
                    FontWeight.ExtraBold
            )

            Spacer(
                Modifier.height(4.dp)
            )

            Text(

                text = title,

                color =
                    colors.textSecondary,

                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun QuizScoreOverlay(
    score: Int,
    totalQuestions: Int,
    mistakes: Int,
    xpEarned: Int,
    hearts: Int,
    wrongAnswers: List<String>,
    displayLanguage: DisplayLanguage,
    onRestart: () -> Unit,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "quiz_score_mascot_pulse")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_scale"
    )

    val isLight = AppTheme.themeMode == ThemeMode.Light
    val isPerfect = score == totalQuestions && hearts == 5

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
                .background(Color.Black.copy(alpha = 0.78f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.86f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* absorb click */ },
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLight) Color(0xFFFFFBF0) else Color(0xFF1C1A17)
                ),
                border = BorderStroke(2.dp, Brush.linearGradient(listOf(Gold, Accent)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = if (isLight) Color(0xFF5D4037) else Color(0xFFE0E0E0)
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            // Pulsing Mascot
                            Box(
                                modifier = Modifier.size(140.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(125.dp)
                                        .scale(auraScale)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(Gold.copy(alpha = 0.35f), Color.Transparent)
                                            )
                                        )
                                )

                                MascotBani(
                                    state = when {
                                        hearts <= 0 -> MascotState.Oops
                                        isPerfect -> MascotState.Celebrating
                                        score > 0 -> MascotState.Happy
                                        else -> MascotState.Oops
                                    },
                                    modifier = Modifier.size(110.dp)
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = when {
                                    hearts <= 0 -> "Out of Lives!".translate(displayLanguage)
                                    isPerfect -> "Perfect Score!".translate(displayLanguage)
                                    else -> "Quiz Complete!".translate(displayLanguage)
                                },
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Gold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(8.dp))

                            // XP Pill
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = Accent.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, Accent.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    RollingNumberText(
                                        value = xpEarned,
                                        prefix = "+",
                                        suffix = " XP",
                                        color = Accent,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Stats: Correct and Mistakes Cards
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Correct Card
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFF4CAF50).copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.3f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "$score / $totalQuestions".translateDigits(displayLanguage),
                                            color = Color(0xFF4CAF50),
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Correct".translate(displayLanguage),
                                            color = if (isLight) Color(0xFF2E7D32) else Color(0xFF81C784),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // Mistakes Card
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFE53935).copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.3f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "$mistakes".translateDigits(displayLanguage),
                                            color = Color(0xFFE53935),
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = "Mistakes".translate(displayLanguage),
                                            color = if (isLight) Color(0xFFC62828) else Color(0xFFEF9A9A),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Mistakes Review List Section
                        if (wrongAnswers.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Review Mistakes".translate(displayLanguage),
                                    color = Color(0xFFE53935),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            itemsIndexed(wrongAnswers) { _, entry ->
                                val parts = entry.split(":::")
                                val qText = parts.getOrNull(0).orEmpty()
                                val aText = parts.getOrNull(1).orEmpty()

                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isLight) Color(0xFFF5F5F5) else Color(0xFF262421),
                                    border = BorderStroke(1.dp, (if (isLight) Color.Black else Color.White).copy(alpha = 0.08f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text("Q: ", color = Color(0xFFE53935), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(qText, color = if (isLight) Color.Black else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text("A: ", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(aText, color = if (isLight) Color.Black else Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Restart Quiz Button in overlay
                    Button(
                        onClick = onRestart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        Text(
                            text = "Restart Quiz".translate(displayLanguage),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
