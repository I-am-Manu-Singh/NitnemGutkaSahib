package com.creativeblisslabs.nitnemguktasteek.app.ui.practice

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.border
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.animation.AnimatedVisibility
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.utils.translateDigits
import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState
import com.creativeblisslabs.nitnemguktasteek.viewmodel.NitnemViewModel
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.MascotBani
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.MascotState
import com.creativeblisslabs.nitnemguktasteek.utils.rememberTextToSpeechHelper
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlinx.coroutines.isActive
import androidx.compose.ui.hapticfeedback.HapticFeedback
import kotlinx.coroutines.CoroutineScope
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI

enum class SimranMode(
    val title: String,
    val subtitle: String,
    val guidance: String
) {
    SaasSaas(
        title = "Saas Saas",
        subtitle = "Breath Focus",
        guidance = "Inhale 'Wahe' mentally and exhale 'Guru'. Synchronize awareness with each breath cycle."
    ),
    MoolMantar(
        title = "Mool Mantar",
        subtitle = "Stillness",
        guidance = "Remain internally silent and observe the subtle vibration of Naam within."
    ),
    Mala(
        title = "Mala Counter",
        subtitle = "Naam Jaap",
        guidance = "Tap the orb once for each Waheguru repetition. Complete a full Mala of 108 repetitions while maintaining focus on the Naam rather than the count."
    )
}

@Composable
fun SimranAbhyasTab(
    colors: PracticeColors,
    displayLanguage: DisplayLanguage,
    uiState: NitnemUiState,
    viewModel: NitnemViewModel
) {
    val defaultDuration = 10 * 60
    val initialRunning = uiState.meditationTimerRunning
    val initialSeconds = if (uiState.meditationRemainingSeconds > 0) uiState.meditationRemainingSeconds else defaultDuration
    val initialMode = try {
        SimranMode.valueOf(uiState.meditationSelectedMode)
    } catch (e: Exception) {
        SimranMode.SaasSaas
    }

    var targetCount by rememberSaveable { mutableStateOf(108) }
    var malaCount = if (targetCount > 0) uiState.malaMantraCount % targetCount else uiState.malaMantraCount
    val completedMalas = if (targetCount > 0) uiState.malaMantraCount / targetCount else 0

    var showCompletionDialog by remember { mutableStateOf(false) }
    var completionMessage by remember { mutableStateOf("") }

    val haptic = LocalHapticFeedback.current
    val bounceScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    var mode by rememberSaveable { mutableStateOf(initialMode) }
    var running by rememberSaveable { mutableStateOf(initialRunning) }
    val mascotState = when (mode) {
        SimranMode.SaasSaas -> if (running) MascotState.Simran else MascotState.Meditating
        SimranMode.MoolMantar -> if (running) MascotState.Simran else MascotState.Meditating
        SimranMode.Mala -> if (running) MascotState.Simran else MascotState.Meditating
    }
    var breathingIn by remember { mutableStateOf(true) }
    var ttsEnabled by rememberSaveable { mutableStateOf(true) }
    var ttsSpeed by rememberSaveable { mutableStateOf(0.85f) }
    
    val ttsHelper = rememberTextToSpeechHelper()
    
    LaunchedEffect(Unit) {
        ttsHelper.setLanguage("pa")
        ttsHelper.setPitch(1.0f)
    }

    LaunchedEffect(ttsSpeed) {
        ttsHelper.setSpeechRate(ttsSpeed)
    }

    DisposableEffect(Unit) {
        onDispose { ttsHelper.stop() }
    }

    val inhalePrompts = remember(displayLanguage) {
        listOf(
            when (displayLanguage) {
                DisplayLanguage.English -> "Wahe..."
                DisplayLanguage.Hindi -> "वाहि..."
                DisplayLanguage.Punjabi -> "ਵਾਹਿ..."
            }
        )
    }
    val exhalePrompts = remember(displayLanguage) {
        listOf(
            when (displayLanguage) {
                DisplayLanguage.English -> "Guru..."
                DisplayLanguage.Hindi -> "गुरू..."
                DisplayLanguage.Punjabi -> "ਗੁਰੂ..."
            }
        )
    }
    val moolMantarLines = remember {
        listOf(
            "ੴ ਸਤਿ ਨਾਮੁ ਕਰਤਾ ਪੁਰਖੁ",
            "ਨਿਰਭਉ ਨਿਰਵੈਰੁ",
            "ਅਕਾਲ ਮੂਰਤਿ ਅਜੂਨੀ ਸੈਭੰ ਗੁਰ ਪ੍ਰਸਾਦਿ ॥",
            "॥ ਜਪੁ ॥",
            "ਆਦਿ ਸਚੁ ਜੁਗਾਦਿ ਸਚੁ ॥",
            "ਹੈ ਭੀ ਸਚੁ ਨਾਨਕ ਹੋਸੀ ਭੀ ਸਚੁ ॥"
        )
    }

    var totalSeconds by rememberSaveable { mutableStateOf(initialSeconds) }

    LaunchedEffect(running, totalSeconds, mode) {
        viewModel.updateMeditationState(
            running = running,
            remainingSeconds = totalSeconds,
            durationSeconds = defaultDuration,
            selectedMode = mode.name
        )
    }

    LaunchedEffect(running, mode, ttsEnabled) {
        if (!running || !ttsEnabled) return@LaunchedEffect
        val breathDuration = (5200L / ttsSpeed.coerceIn(0.5f, 2.0f)).toLong()

        if (mode == SimranMode.MoolMantar) {
            while (isActive && running) {
                for (line in moolMantarLines) {
                    if (!isActive || !running) break
                    ttsHelper.speak(line)
                    delay((3800L / ttsSpeed.coerceIn(0.5f, 2.0f)).toLong())
                }
                delay(4000L)
            }
            return@LaunchedEffect
        }

        if (mode == SimranMode.Mala) {
            // Mala manual tap triggers TTS utterance directly on tap
            return@LaunchedEffect
        }

        while (isActive && running) {
            when (mode) {
                SimranMode.SaasSaas -> {
                    breathingIn = true
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    if (ttsEnabled) ttsHelper.speak(inhalePrompts.random())
                    delay(breathDuration)

                    breathingIn = false
                    if (ttsEnabled) ttsHelper.speak(exhalePrompts.random())
                    delay(breathDuration)
                }
                SimranMode.Mala -> {
                    val chantInterval = (2200L / ttsSpeed.coerceIn(0.5f, 2.0f)).toLong()
                    if (ttsEnabled) {
                        ttsHelper.speak("ਵਾਹਿਗੁਰੂ")
                    }
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    malaCount++
                    delay(chantInterval)
                }
                else -> break
            }
        }
    }

    LaunchedEffect(running, totalSeconds) {
        if (running && totalSeconds > 0) {
            delay(1000L)
            totalSeconds--
        } else if (totalSeconds <= 0) {
            running = false
            ttsHelper.stop()
            val meditationMin = defaultDuration / 60
            viewModel.addMeditationMinutes(meditationMin)
            completionMessage = "Completed %d minutes of Simran!\nEarned +%d XP"
                .translate(displayLanguage)
                .replaceFirst("%d", meditationMin.toString())
                .replace("%d", (meditationMin * 10).toString())
                .translateDigits(displayLanguage)
            showCompletionDialog = true

            delay(800)
            ttsHelper.speak("Session complete. Remain still for a moment.".translate(displayLanguage))
        }
    }

    val infinite = rememberInfiniteTransition(label = "SimranBreath")
    val animatedScale by infinite.animateFloat(
        initialValue = 0.94f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathScale"
    )

    val mins = totalSeconds / 60
    val secs = totalSeconds % 60
    val elapsed = defaultDuration - totalSeconds

    val mantra = when {
        !running -> "Practice".translate(displayLanguage)
        mode == SimranMode.SaasSaas -> {
            when (displayLanguage) {
                DisplayLanguage.English -> if (breathingIn) "Wahe" else "Guru"
                DisplayLanguage.Hindi -> if (breathingIn) "वाहि" else "गुरू"
                DisplayLanguage.Punjabi -> if (breathingIn) "ਵਾਹਿ" else "ਗੁਰੂ"
            }
        }
        mode == SimranMode.Mala -> {
            when (displayLanguage) {
                DisplayLanguage.English -> "Waheguru"
                DisplayLanguage.Hindi -> "वाहेगुरू"
                DisplayLanguage.Punjabi -> "ਵਾਹਿਗੁਰੂ"
            }
        }
        else -> {
            when (displayLanguage) {
                DisplayLanguage.English -> "Anhad"
                DisplayLanguage.Hindi -> "अनहद"
                DisplayLanguage.Punjabi -> "ਅਨਹਦੁ"
            }
        }
    }

    val breathingText = when {
        !running -> "Ready to begin".translate(displayLanguage)
        mode == SimranMode.SaasSaas -> if (breathingIn) "Inhale • Wahe".translate(displayLanguage) else "Exhale • Guru".translate(displayLanguage)
        mode == SimranMode.Mala -> "Listen inwardly".translate(displayLanguage)
        else -> "Internal stillness".translate(displayLanguage)
    }

    val progress = (elapsed / defaultDuration.toFloat()).coerceIn(0f, 1f)
  if (uiState.blackAndWhiteAodMode) {
        // ULTRA-MINIMALIST CENTERED AOD MODE
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (mode == SimranMode.Mala) {
                MalaOrbSection(
                    colors = colors, displayLanguage = displayLanguage, uiState = uiState,
                    viewModel = viewModel, targetCount = targetCount, malaCount = malaCount,
                    completedMalas = completedMalas, bounceScale = bounceScale, haptic = haptic,
                    scope = scope, onTargetSelected = { targetCount = it },
                    onMalaClick = { if (ttsEnabled) ttsHelper.speak("ਵਾਹਿਗੁਰੂ") },
                    onCompletion = { message ->
                        completionMessage = message
                        showCompletionDialog = true
                    }
                )
            } else {
                MeditationOrbSection(
                    mode = mode, colors = colors, displayLanguage = displayLanguage, running = running,
                    mins = mins, secs = secs, mantra = mantra, breathingText = breathingText, progress = progress,
                    animatedScale = animatedScale, mascotState = mascotState, totalSeconds = totalSeconds,
                    uiState = uiState,
                    onDurationSelected = { minutes -> totalSeconds = minutes * 60 },
                    onPlayPauseClick = {
                        running = !running
                        if (!running) ttsHelper.stop()
                    },
                    onResetClick = {
                        running = false
                        totalSeconds = defaultDuration
                        breathingIn = true
                        ttsHelper.stop()
                    }
                )
            }
        }
    } else {
        // CHANGED: Replaced LazyColumn with Column for even vertical distribution
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 18.dp, end = 18.dp, top = 0.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly, // Distributes space evenly between elements
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimranModeSwitcher(
                currentMode = mode,
                displayLanguage = displayLanguage,
                onModeSelected = {
                    if (running) ttsHelper.stop()
                    mode = it
                }
            )

            SimranTtsToggle(
                enabled = ttsEnabled,
                speechRate = ttsSpeed,
                colors = colors,
                displayLanguage = displayLanguage,
                onToggle = { ttsEnabled = it },
                onSpeedChange = { ttsSpeed = it }
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = colors.card.copy(alpha = 0.28f),
                border = BorderStroke(1.dp, colors.textSecondary.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (mode == SimranMode.Mala) {
                        MalaOrbSection(
                            colors = colors, displayLanguage = displayLanguage, uiState = uiState,
                            viewModel = viewModel, targetCount = targetCount, malaCount = malaCount,
                            completedMalas = completedMalas, bounceScale = bounceScale, haptic = haptic,
                            scope = scope, onTargetSelected = { targetCount = it },
                            onMalaClick = { if (ttsEnabled) ttsHelper.speak("ਵਾਹਿਗੁਰੂ") },
                            onCompletion = { message ->
                                completionMessage = message
                                showCompletionDialog = true
                            }
                        )
                    } else {
                        MeditationOrbSection(
                            mode = mode, colors = colors, displayLanguage = displayLanguage, running = running,
                            mins = mins, secs = secs, mantra = mantra, breathingText = breathingText, progress = progress,
                            animatedScale = animatedScale, mascotState = mascotState, totalSeconds = totalSeconds,
                            uiState = uiState,
                            onDurationSelected = { minutes -> totalSeconds = minutes * 60 },
                            onPlayPauseClick = {
                                running = !running
                                if (!running) ttsHelper.stop()
                            },
                            onResetClick = {
                                running = false
                                totalSeconds = defaultDuration
                                breathingIn = true
                                ttsHelper.stop()
                            }
                        )
                    }

                    AnimatedVisibility(visible = !running && malaCount == 0) {
                        Column {
                            Spacer(Modifier.height(16.dp))
                            SimranGuidanceCard(mode = mode, colors = colors, displayLanguage = displayLanguage)
                        }
                    }
                }
            }
        }
    }


    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = colors.card,
            title = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    MascotBani(state = MascotState.Celebrating, modifier = Modifier.size(96.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(text = "Well Done!".translate(displayLanguage), color = colors.textPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                }
            },
            text = {
                Text(text = completionMessage, color = colors.textSecondary, fontSize = 14.sp, lineHeight = 22.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            },
            confirmButton = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Surface(
                        onClick = { showCompletionDialog = false },
                        shape = RoundedCornerShape(16.dp),
                        color = colors.accent.copy(alpha = 0.12f)
                    ) {
                        Text(text = "Continue".translate(displayLanguage), color = colors.accent, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp))
                    }
                }
            }
        )
    }
}

@Composable
private fun SimranModeSwitcher(
    currentMode: SimranMode,
    displayLanguage: DisplayLanguage,
    onModeSelected: (SimranMode) -> Unit
) {
    val modes = SimranMode.entries
    val selectedIndex = modes.indexOf(currentMode)
    val containerColor = if (AppTheme.themeMode == ThemeMode.Light) Color.Black.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.05f)

    Surface(shape = RoundedCornerShape(100.dp), color = containerColor) {
        BoxWithConstraints(modifier = Modifier.height(48.dp).padding(4.dp)) {
            val itemWidth = maxWidth / modes.size
            val animatedOffset by animateDpAsState(targetValue = itemWidth * selectedIndex, label = "mode_offset")

            Box(
                modifier = Modifier.offset(x = animatedOffset).width(itemWidth).fillMaxHeight()
                    .clip(RoundedCornerShape(100.dp))
                    .background(Brush.horizontalGradient(listOf(Gold.copy(alpha = 0.24f), Gold.copy(alpha = 0.14f))))
            )

            Row(modifier = Modifier.fillMaxSize()) {
                modes.forEach { mode ->
                    val selected = currentMode == mode
                    val textColor by animateColorAsState(targetValue = if (selected) Gold else TextPrimary.copy(alpha = 0.60f), label = "mode_text")

                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(100.dp))
                            .clickable { onModeSelected(mode) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = mode.title.translate(displayLanguage), color = textColor, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerChip(minute: Int, currentSeconds: Int, colors: PracticeColors, onClick: () -> Unit) {
    val selected = currentSeconds == minute * 60
    Surface(
        modifier = Modifier.width(64.dp).clip(RoundedCornerShape(100.dp)).clickable { onClick() },
        shape = RoundedCornerShape(100.dp),
        color = if (selected) colors.accent.copy(alpha = 0.18f) else Color.Transparent,
        border = BorderStroke(1.dp, if (selected) colors.accent.copy(alpha = 0.20f) else colors.textSecondary.copy(alpha = 0.12f))
    ) {
        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text(text = "${minute}m", color = if (selected) colors.accent else colors.textSecondary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TargetChip(target: Int, currentTarget: Int, colors: PracticeColors, onClick: () -> Unit) {
    val selected = currentTarget == target
    val label = if (target == 0) "∞" else target.toString()
    
    Surface(
        modifier = Modifier.width(64.dp).clip(RoundedCornerShape(100.dp)).clickable { onClick() },
        shape = RoundedCornerShape(100.dp),
        color = if (selected) colors.accent.copy(alpha = 0.18f) else Color.Transparent,
        border = BorderStroke(1.dp, if (selected) colors.accent.copy(alpha = 0.20f) else colors.textSecondary.copy(alpha = 0.12f))
    ) {
        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text(text = label, color = if (selected) colors.accent else colors.textSecondary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SimranTtsToggle(
    enabled: Boolean,
    speechRate: Float,
    colors: PracticeColors,
    displayLanguage: DisplayLanguage,
    onToggle: (Boolean) -> Unit,
    onSpeedChange: (Float) -> Unit
) {
    val containerColor = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFFF5F5F5).copy(alpha = 0.92f) else Color(0xFF1E1E1E).copy(alpha = 0.88f)
    val borderColor = if (AppTheme.themeMode == ThemeMode.Light) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.07f)

    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = containerColor, border = BorderStroke(1.dp, borderColor)) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = if (enabled) Gold else colors.textSecondary.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Audio Guidance".translate(displayLanguage), color = colors.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(text = if (enabled) "Punjabi Vocal Guidance (On)".translate(displayLanguage) else "Guidance muted".translate(displayLanguage), color = colors.textSecondary, fontSize = 11.sp)
                }
                Switch(
                    checked = enabled, onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Gold, uncheckedThumbColor = colors.textSecondary.copy(alpha = 0.5f), uncheckedTrackColor = colors.textSecondary.copy(alpha = 0.12f))
                )
            }

            if (enabled) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Speed",
                        tint = Gold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Speed".translate(displayLanguage),
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.width(8.dp))
                    Slider(
                        value = speechRate,
                        onValueChange = onSpeedChange,
                        valueRange = 0.5f..1.5f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Gold,
                            activeTrackColor = Gold,
                            inactiveTrackColor = colors.textSecondary.copy(alpha = 0.15f)
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${String.format("%.1f", speechRate)}x".translateDigits(displayLanguage),
                        color = colors.textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SimranGuidanceCard(mode: SimranMode, colors: PracticeColors, displayLanguage: DisplayLanguage) {
    val guidanceCardColor = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFFF7F4EE) else Color(0xFF1B1B1B)
    val quoteCardColor = if (AppTheme.themeMode == ThemeMode.Light) colors.accent.copy(alpha = 0.05f) else colors.accent.copy(alpha = 0.08f)
    val borderColor = if (AppTheme.themeMode == ThemeMode.Light) Color.Black.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.06f)

    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = guidanceCardColor, border = BorderStroke(1.dp, borderColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(colors.accent))
                Spacer(Modifier.width(8.dp))
                Text(text = mode.title.translate(displayLanguage), color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(Modifier.width(6.dp))
                Text(text = "• ${mode.subtitle.translate(displayLanguage)}", color = colors.textSecondary, fontSize = 11.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(text = mode.guidance.translate(displayLanguage), color = colors.textSecondary, fontSize = 11.sp, lineHeight = 18.sp)
        }
    }
}
@Composable
private fun MeditationOrbSection(
    mode: SimranMode,
    colors: PracticeColors,
    displayLanguage: DisplayLanguage,
    running: Boolean,
    mins: Int,
    secs: Int,
    mantra: String,
    breathingText: String,
    progress: Float,
    animatedScale: Float,
    mascotState: MascotState,
    totalSeconds: Int,
    uiState: NitnemUiState = NitnemUiState(),
    onDurationSelected: (Int) -> Unit,
    onPlayPauseClick: () -> Unit,
    onResetClick: () -> Unit
) {
    val isAod = uiState.blackAndWhiteAodMode

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        if (!isAod) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                TimerChip(5, totalSeconds, colors) { onDurationSelected(5) }
                TimerChip(10, totalSeconds, colors) { onDurationSelected(10) }
            }

            Spacer(Modifier.width(12.dp))
        }

        Box(modifier = Modifier.size(290.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress }, 
                modifier = Modifier.size(270.dp), 
                color = if (isAod) Color.White else Gold, 
                strokeWidth = 5.dp,
                trackColor = if (isAod) Color.White.copy(alpha = 0.15f) else when (AppTheme.themeMode) {
                    ThemeMode.Light -> Color.Black.copy(alpha = 0.05f)
                    ThemeMode.Dark -> Color.White.copy(alpha = 0.05f)
                    ThemeMode.Amoled -> Color.White.copy(alpha = 0.03f)
                }
            )
            
            // Dynamic scale wrapper for the whole orb
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .graphicsLayer {
                        val scale = if (mode == SimranMode.SaasSaas && running) animatedScale else 1f
                        scaleX = scale
                        scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!isAod) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.radialGradient(
                                    colors = when (AppTheme.themeMode) {
                                        ThemeMode.Light -> listOf(
                                            Color.Black.copy(alpha = 0.16f),
                                            Color.Black.copy(alpha = 0.05f),
                                            Color.Transparent
                                        )
                                        ThemeMode.Dark -> listOf(
                                            colors.accent.copy(alpha = 0.22f),
                                            colors.accent.copy(alpha = 0.04f),
                                            Color.Transparent
                                        )
                                        ThemeMode.Amoled -> listOf(
                                            colors.accent.copy(alpha = 0.18f),
                                            colors.accent.copy(alpha = 0.03f),
                                            Color.Transparent
                                        )
                                    },
                                    radius = 380f
                                ),
                                shape = CircleShape
                            )
                    )
                }

                val meditationOrbColors: List<Color> = if (isAod) {
                    listOf(Color.Black, Color.Black, Color.Black)
                } else when (AppTheme.themeMode) {
                    ThemeMode.Light -> listOf(Color.White, Color(0xFFF8F5EE), colors.accent.copy(alpha = 0.15f))
                    ThemeMode.Dark -> listOf(Color(0xFF262626), Color(0xFF1C1C1C), colors.accent.copy(alpha = 0.10f))
                    ThemeMode.Amoled -> listOf(Color(0xFF101010), Color(0xFF070707), colors.accent.copy(alpha = 0.08f))
                }

                // Foreground layout body - identical footprint to the Mala counter setup
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            // Shadows provide depth fallback metrics on supported canvas pipelines
                            shadowElevation = when (AppTheme.themeMode) {
                                ThemeMode.Light -> 28f
                                ThemeMode.Dark -> 10f
                                ThemeMode.Amoled -> 6f
                            }
                            shape = CircleShape
                            clip = false
                        }
                        .clip(CircleShape)
                        .clickable { onPlayPauseClick() }
                        .background(Brush.radialGradient(colors = meditationOrbColors, radius = 550f))
                        .border(
                            width = 1.dp,
                            color = when (AppTheme.themeMode) {
                                ThemeMode.Light -> Color.White.copy(alpha = 0.9f)
                                ThemeMode.Dark -> Color.White.copy(alpha = 0.08f)
                                ThemeMode.Amoled -> Color.White.copy(alpha = 0.05f)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally, 
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MascotBani(state = mascotState, modifier = Modifier.size(60.dp))
                        
                        Text(
                            text = mantra, 
                            color = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF1B1B1B) else Color.White, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 22.sp
                        )
                        
                        Text(
                            text = "%02d:%02d".format(mins, secs), 
                            color = if (isAod) Color.White else if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF9C6B00) else Gold, 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 26.sp
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp), 
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(onClick = onPlayPauseClick, shape = CircleShape, color = if (isAod) Color.White.copy(alpha = 0.20f) else Gold.copy(alpha = 0.14f), border = BorderStroke(1.dp, if (isAod) Color.White.copy(alpha = 0.35f) else Gold.copy(alpha = 0.22f))) {
                                Box(modifier = Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                                    Icon(imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null, tint = if (isAod) Color.White else Gold, modifier = Modifier.size(20.dp))
                                }
                            }
                            Surface(onClick = onResetClick, shape = CircleShape, color = if (isAod) Color.White.copy(alpha = 0.12f) else if (AppTheme.themeMode == ThemeMode.Light) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.08f), border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))) {
                                Box(modifier = Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White.copy(alpha = 0.90f), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        
                        Text(
                            text = breathingText, 
                            modifier = Modifier.padding(horizontal = 12.dp), 
                            color = if (isAod) Color.White.copy(alpha = 0.80f) else if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF404040) else Color.White.copy(alpha = 0.72f), 
                            fontStyle = FontStyle.Italic, 
                            fontSize = 10.sp, 
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        if (!isAod) {
            Spacer(Modifier.width(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                TimerChip(15, totalSeconds, colors) { onDurationSelected(15) }
                TimerChip(31, totalSeconds, colors) { onDurationSelected(31) }
            }
        }
    }
}
@Composable
private fun MalaOrbSection(
    colors: PracticeColors,
    displayLanguage: DisplayLanguage,
    uiState: NitnemUiState,
    viewModel: NitnemViewModel,
    targetCount: Int,
    malaCount: Int,
    completedMalas: Int,
    bounceScale: Animatable<Float, AnimationVector1D>,
    haptic: HapticFeedback,
    scope: CoroutineScope,
    onTargetSelected: (Int) -> Unit,
    onMalaClick: () -> Unit = {},
    onCompletion: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            TargetChip(108, targetCount, colors) { onTargetSelected(108) }
            TargetChip(250, targetCount, colors) { onTargetSelected(250) }
        }

        Spacer(Modifier.width(12.dp))

        Box(modifier = Modifier.size(290.dp), contentAlignment = Alignment.Center) {
            if (targetCount > 0) {
                Canvas(modifier = Modifier.size(270.dp)) {
                    val radius = size.minDimension / 2f - 12f
                    val center = Offset(size.width / 2f, size.height / 2f)
                    
                    // Draw core string
                    drawCircle(
                        color = Color(0xFF5D4037).copy(alpha = 0.4f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    
                    val totalBeads = 54
                    val progressRatio = (malaCount.toFloat() / targetCount).coerceIn(0f, 1f)
                    val activeBeadCount = (progressRatio * totalBeads).toInt()
                    
                    for (i in 0 until totalBeads) {
                        val angle = (i * 2 * PI / totalBeads) - (PI / 2)
                        val beadX = center.x + kotlin.math.cos(angle).toFloat() * radius
                        val beadY = center.y + kotlin.math.sin(angle).toFloat() * radius
                        val beadPos = Offset(beadX, beadY)
                        
                        val isActive = i < activeBeadCount
                        val beadColor = if (isActive) Gold else Color(0xFFD7CCC8)
                        val borderColor = if (isActive) Color(0xFFE65100) else Color(0xFF8D6E63)
                        
                        // Glowing Aura for active beads
                        if (isActive) {
                            drawCircle(
                                color = Gold.copy(alpha = 0.25f),
                                radius = 8.dp.toPx(),
                                center = beadPos
                            )
                        }
                        
                        // Solid Bead Body
                        drawCircle(
                            color = beadColor,
                            radius = 5.dp.toPx(),
                            center = beadPos
                        )
                        
                        // 3D Spherical Highlight
                        drawCircle(
                            color = Color.White.copy(alpha = 0.55f),
                            radius = 1.8.dp.toPx(),
                            center = Offset(beadX - 1.2.dp.toPx(), beadY - 1.2.dp.toPx())
                        )
                        
                        // Bead Outline Rim
                        drawCircle(
                            color = borderColor,
                            radius = 5.dp.toPx(),
                            center = beadPos,
                            style = Stroke(width = 0.8.dp.toPx())
                        )
                    }
                }
            }

            // FIX: Explicitly typed array declaration to bypass the compiler's lambda scoping bug
            val malaOrbColors: List<Color> = when (AppTheme.themeMode) {
                ThemeMode.Light -> listOf(
                    Color.White,
                    Color(0xFFF8F5EE),
                    colors.accent.copy(alpha = 0.15f)
                )
                ThemeMode.Dark -> listOf(
                    Color(0xFF262626),
                    Color(0xFF1C1C1C),
                    colors.accent.copy(alpha = 0.10f)
                )
                ThemeMode.Amoled -> listOf(
                    Color(0xFF101010),
                    Color(0xFF070707),
                    colors.accent.copy(alpha = 0.08f)
                )
            }

            Box(
                modifier = Modifier.size(240.dp).scale(bounceScale.value)
                    .graphicsLayer {
                        shadowElevation = when (AppTheme.themeMode) {
                            ThemeMode.Light -> 28f
                            ThemeMode.Dark -> 10f
                            ThemeMode.Amoled -> 6f
                        }
                        shape = CircleShape
                        clip = false
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = malaOrbColors, // Bound to the newly isolated, safe reference variable
                            radius = 550f
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = when (AppTheme.themeMode) {
                            ThemeMode.Light -> Color.White.copy(alpha = 0.9f)
                            ThemeMode.Dark -> Color.White.copy(alpha = 0.08f)
                            ThemeMode.Amoled -> Color.White.copy(alpha = 0.05f)
                        },
                        shape = CircleShape
                    )
                    .clickable {
                        // ... rest of your clickable block code logic executes as normal
                        scope.launch {
                            bounceScale.animateTo(0.92f, tween(60))
                            bounceScale.animateTo(1f, tween(100))
                        }
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                        // FIX: Calculate execution index on local path thread snapshot to prevent concurrent lifecycle jumps
                        val snapshotCount = uiState.malaMantraCount + 1
                        viewModel.incrementMalaCount()
                        onMalaClick()

                        if (targetCount > 0 && snapshotCount % targetCount == 0) {
                            onCompletion(
                                "Completed a full Mala of %d repetitions!\nEarned +5 XP"
                                    .translate(displayLanguage)
                                    .replace("%d", targetCount.toString())
                                    .translateDigits(displayLanguage)
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    MascotBani(state = MascotState.Reading, modifier = Modifier.size(60.dp))
                    Text(text = "ਵਾਹਿਗੁਰੂ", color = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF1B1B1B) else Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    Text(text = if (targetCount > 0) "$malaCount / $targetCount" else "$malaCount", color = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF9C6B00) else Gold, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)

                    Surface(
                        onClick = { viewModel.resetMalaCount() }, shape = CircleShape,
                        color = if (AppTheme.themeMode == ThemeMode.Light) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, Gold.copy(alpha = 0.20f))
                    ) {
                        Box(modifier = Modifier.size(34.dp), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF444444) else Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            TargetChip(500, targetCount, colors) { onTargetSelected(500) }
            TargetChip(0, targetCount, colors) { onTargetSelected(0) }
        }
    }

    Spacer(Modifier.height(18.dp))

}