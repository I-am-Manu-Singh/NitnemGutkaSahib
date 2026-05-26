package org.audiocular.nitnem.app.ui.practice

import org.audiocular.nitnem.app.ui.practice.PracticeColors
import org.audiocular.nitnem.app.ui.practice.practiceColors
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.fadeIn
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedVisibility
import org.audiocular.nitnem.app.audio.StreamAudioController
import org.audiocular.nitnem.app.sync.SyncEditorScreen
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.graphics.vector.ImageVector

enum class MeditationMode(
    val title: String,
    val subtitle: String
) {

    Breath(
        "Breath Focus",
        "Slow mindful breathing"
    ),

    Simran(
        "Waheguru Simran",
        "Repeat and focus"
    ),

    Silent(
        "Silent Stillness",
        "Pure quiet meditation"
    )
}

enum class SimranMode(
    val title: String,
    val subtitle: String,
    val guidance: String
) {

    SaasSaas(
        title = "Saas Saas",
        subtitle = "Breath Focus",
        guidance =
            "Inhale 'Wahe' mentally and exhale 'Guru'. Synchronize awareness with each breath cycle."
    ),

    Dhyaan(
        title = "Dhyaan",
        subtitle = "Naam Listening",
        guidance =
            "Recite Waheguru gently and listen deeply to the internal resonance of the Shabad."
    ),

    Anhad(
        title = "Anhad",
        subtitle = "Stillness",
        guidance =
            "Remain internally silent and observe the subtle vibration of Naam within."
    )
}
@Composable
fun SimranAbhyasTab(
    colors: PracticeColors,
    displayLanguage: DisplayLanguage
) {

    var mode by rememberSaveable {
        mutableStateOf(
            SimranMode.SaasSaas
        )
    }

    var running by rememberSaveable {
        mutableStateOf(false)
    }

    val defaultDuration = 10 * 60

    var totalSeconds by rememberSaveable {
        mutableStateOf(defaultDuration)
    }

    /*
     * TIMER
     */

    LaunchedEffect(
        running,
        totalSeconds
    ) {

        if (running && totalSeconds > 0) {

            delay(1000L)

            totalSeconds--

        } else if (totalSeconds <= 0) {

            running = false
        }
    }

    /*
     * BREATHING
     */

    val infinite =
        rememberInfiniteTransition(
            label = "SimranBreath"
        )

    val animatedScale by infinite.animateFloat(

        initialValue = 0.94f,

        targetValue = 1.05f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                durationMillis = 3200,
                easing = EaseInOutCubic
            ),

            repeatMode = RepeatMode.Reverse
        ),

        label = "BreathScale"
    )

    val mins =
        totalSeconds / 60

    val secs =
        totalSeconds % 60

    val elapsed =
        defaultDuration - totalSeconds

    val breathingIn =
        animatedScale > 0.99f

    val mantra = when {

        !running -> {

            "Practice"
                .translate(displayLanguage)
        }

        mode ==
            SimranMode.SaasSaas -> {

            if (breathingIn)
                "ਵਾਹਿ"
            else
                "ਗੁਰੂ"
        }

        mode ==
            SimranMode.Dhyaan -> {

            "ਵਾਹਿਗੁਰੂ"
        }

        else -> {

            "ਅਨਹਦੁ"
        }
    }

    val breathingText = when {

        !running -> {

            "Ready to begin".translate(displayLanguage)
        }

        mode ==
            SimranMode.SaasSaas -> {

            if (breathingIn)
                "Inhale • Wahe".translate(displayLanguage)
            else
                "Exhale • Guru".translate(displayLanguage)
        }

        mode ==
            SimranMode.Dhyaan -> {

            "Listen inwardly".translate(displayLanguage)
        }

        else -> {

            "Internal stillness".translate(displayLanguage)
        }
    }

    val progress =
        (
            elapsed /
                defaultDuration.toFloat()
            ).coerceIn(0f, 1f)

    val secondaryTextColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Color(0xFF10332E).copy(alpha = 0.72f)
        }

        else -> {
            Color.White.copy(alpha = 0.72f)
        }
    }

    val progressTrackColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Color.Black.copy(alpha = 0.08f)
        }

        ThemeMode.Dark -> {
            Color.White.copy(alpha = 0.08f)
        }

        ThemeMode.Amoled -> {
            Color.White.copy(alpha = 0.05f)
        }
    }

    LazyColumn(

        modifier = Modifier
            .fillMaxSize(),

        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 0.dp,
            bottom = 40.dp
        ),

        verticalArrangement =
            Arrangement.spacedBy(18.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        item {

            Surface(

                modifier = Modifier
                    .fillMaxWidth(),

                shape = RoundedCornerShape(32.dp),

                color =
                    colors.card.copy(
                        alpha = 0.28f
                    ),

                border = BorderStroke(

                    1.dp,

                    colors.textSecondary.copy(
                        alpha = 0.08f
                    )
                )
            ) {

                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 18.dp
                        ),

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                 
                    
                    /*
                     * MODE SWITCHER
                     */

                    /*
                     * MEDITATION ORB
                     */

                    Box(

                        modifier = Modifier
                            .size(280.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Box(

                            modifier = Modifier
                                .size(250.dp)
                                .scale(

                                    if (running)
                                        animatedScale
                                    else
                                        1f
                                )
                                .clip(CircleShape)
                                .background(
                                    Gold.copy(alpha = 0.10f)
                                )
                        )

                        Box(

                            modifier = Modifier
                                .size(210.dp)
                                .scale(

                                    if (running)
                                        animatedScale
                                    else
                                        1f
                                )
                                .clip(CircleShape)
                                .background(

                                    Brush.radialGradient(
                                        listOf(
                                            colors.accent,
                                            Primary
                                        )
                                    )
                                )
                                .clickable {

                                    if (
                                        totalSeconds <= 0
                                    ) {

                                        totalSeconds =
                                            defaultDuration
                                    }

                                    running =
                                        !running
                                },

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Column(

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Icon(

                                    imageVector =
                                        Icons.Default
                                            .SelfImprovement,

                                    contentDescription =
                                        null,

                                    tint = Gold,

                                    modifier =
                                        Modifier.size(50.dp)
                                )

                                Spacer(
                                    Modifier.height(14.dp)
                                )

                                Text(

                                    text = mantra,

                                    color = Color.White,

                                    fontWeight =
                                        FontWeight.Bold,

                                    fontSize = 32.sp
                                )

                                Spacer(
                                    Modifier.height(6.dp)
                                )

                                Text(

                                    text =
                                        "%02d:%02d"
                                            .format(
                                                mins,
                                                secs
                                            ),

                                    color = Gold,

                                    fontWeight =
                                        FontWeight.Bold,

                                    fontSize = 34.sp
                                )

                                Spacer(
                                    Modifier.height(6.dp)
                                )

                                Text(

                                    text =
                                        breathingText,

                                    color =
                                        Color.White.copy(
                                            alpha = 0.78f
                                        ),

                                    fontStyle =
                                        FontStyle.Italic,

                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                        Spacer(
                        Modifier.height(12.dp)
                    )

                    SimranModeSwitcher(

                        currentMode = mode,

                        displayLanguage = displayLanguage,

                        onModeSelected = {
                            mode = it
                        }
                    )

                    Spacer(
                        Modifier.height(12.dp)
                    )

                   
                    /*
                     * GUIDANCE
                     */

                   /*
 * PREMIUM GUIDANCE CARD
 * LIGHT THEME FIXED
 */

val guidanceCardColor = when (AppTheme.themeMode) {

    ThemeMode.Light -> {
        Color(0xFFF7F4EE)
    }

    ThemeMode.Dark -> {
        Color(0xFF1B1B1B)
    }

    ThemeMode.Amoled -> {
        Color(0xFF101010)
    }
}

val quoteCardColor = when (AppTheme.themeMode) {

    ThemeMode.Light -> {
        colors.accent.copy(alpha = 0.05f)
    }

    else -> {
        colors.accent.copy(alpha = 0.08f)
    }
}

val borderColor = when (AppTheme.themeMode) {

    ThemeMode.Light -> {
        Color.Black.copy(alpha = 0.04f)
    }

    else -> {
        Color.White.copy(alpha = 0.06f)
    }
}

Surface(

    modifier = Modifier
        .fillMaxWidth(),

    shape = RoundedCornerShape(22.dp),

    color = guidanceCardColor,

    border = BorderStroke(
        1.dp,
        borderColor
    ),

    tonalElevation = 0.dp,
    shadowElevation =

        if (AppTheme.themeMode ==
            ThemeMode.Light
        ) {
            1.dp
        } else {
            3.dp
        }
) {

    Column(

        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) {

        /*
         * HEADER
         */

        Row(

            modifier = Modifier
                .fillMaxWidth(),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(

                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        colors.accent
                    )
            )

            Spacer(
                Modifier.width(8.dp)
            )

            Text(

                text =
                    mode.title.translate(displayLanguage),

                color =
                    colors.textPrimary,

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                Modifier.width(6.dp)
            )

            Text(

                text =
                    "• ${mode.subtitle.translate(displayLanguage)}",

                color =
                    colors.textSecondary,

                fontSize = 11.sp
            )

            Spacer(
                Modifier.weight(1f)
            )

            Surface(

                shape =
                    RoundedCornerShape(100.dp),

                color =
                    colors.accent.copy(
                        alpha = 0.10f
                    )
            ) {

                Text(

                    text = "ABHYAS",

                    color =
                        colors.accent,

                    fontSize = 9.sp,

                    fontWeight =
                        FontWeight.ExtraBold,

                    letterSpacing = 1.sp,

                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    )
                )
            }
        }

        Spacer(
            Modifier.height(12.dp)
        )

        /*
         * GUIDANCE
         */

        Text(

            text =
                mode.guidance.translate(displayLanguage),

            color =
                colors.textSecondary,

            fontSize = 11.sp,

            lineHeight = 18.sp
        )

        Spacer(
            Modifier.height(14.dp)
        )

        /*
         * QUOTE
         */

        Surface(

            shape =
                RoundedCornerShape(16.dp),

            color =
                quoteCardColor
        ) {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 12.dp
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(

                    text =
                        "ਗੁਰਮੁਖਿ ਰੋਮਿ ਰੋਮਿ ਹਰਿ ਧਿਆਵੈ ॥",

                    color =
                        colors.accent,

                    fontSize = 14.sp,

                    fontWeight =
                        FontWeight.Bold,

                    textAlign =
                        TextAlign.Center
                )

                Spacer(
                    Modifier.height(4.dp)
                )

                Text(

                    text =
                        "Meditate upon the Divine with every fiber of your being.".translate(displayLanguage),

                    color =
                        colors.textSecondary,

                    textAlign =
                        TextAlign.Center,

                    fontSize = 10.sp,

                    lineHeight = 15.sp
                )
            }
        }
    }
}

                    Spacer(
                        Modifier.height(18.dp)
                    )

                    /*
                     * DURATION
                     */

                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            )
                    ) {

                        listOf(5, 10, 15, 31)
                            .forEach { minute ->

                                val selected =
                                    totalSeconds ==
                                        minute * 60

                                Surface(

                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(
                                            RoundedCornerShape(
                                                100.dp
                                            )
                                        )
                                        .clickable {

                                            running = false

                                            totalSeconds =
                                                minute * 60
                                        },

                                    shape =
                                        RoundedCornerShape(
                                            100.dp
                                        ),

                                    color =

                                        if (selected) {

                                            colors.accent.copy(
                                                alpha = 0.18f
                                            )

                                        } else {

                                            Color.Transparent
                                        },

                                    border = BorderStroke(

                                        1.dp,

                                        if (selected) {

                                            colors.accent
                                                .copy(alpha = 0.20f)

                                        } else {

                                            colors.textSecondary
                                                .copy(alpha = 0.12f)
                                        }
                                    )
                                ) {

                                    Box(

                                        modifier = Modifier
                                            .padding(
                                                vertical = 10.dp
                                            ),

                                        contentAlignment =
                                            Alignment.Center
                                    ) {

                                        Text(

                                            text =
                                                "${minute}m",

                                            color =

                                                if (selected)
                                                    colors.accent
                                                else
                                                    colors.textSecondary,

                                            fontWeight =
                                                FontWeight.SemiBold,

                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                    }

                    Spacer(
                        Modifier.height(16.dp)
                    )

                    /*
                     * PROGRESS
                     */

                    LinearProgressIndicator(

                        progress = {
                            progress
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),

                        color = Gold,

                        trackColor =
                            progressTrackColor
                    )

                    Spacer(
                        Modifier.height(12.dp)
                    )

                    Text(

                        text = when {

                            totalSeconds <= 0 ->
                                "Session completed peacefully".translate(displayLanguage)

                            running ->
                                "Stay present with the breath".translate(displayLanguage)

                            else ->
                                "Tap the circle to begin".translate(displayLanguage)
                        },

                        color =
                            secondaryTextColor,

                        fontSize = 12.sp
                    )

                    Spacer(
                        Modifier.height(12.dp)
                    )

                    /*
                     * CONTROLS
                     */

                    Row(

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                14.dp
                            )
                    ) {

                        MeditationActionButton(

                            icon = if (
                                running
                            )
                                Icons.Default.Pause
                            else
                                Icons.Default.PlayArrow,

                            text = if (
                                running
                            )
                                "Pause".translate(displayLanguage)
                            else
                                "Start".translate(displayLanguage),

                            onClick = {

                                if (
                                    totalSeconds <= 0
                                ) {

                                    totalSeconds =
                                        defaultDuration
                                }

                                running =
                                    !running
                            }
                        )

                        MeditationActionButton(

                            icon =
                                Icons.Default.Refresh,

                            text = "Reset".translate(displayLanguage),

                            onClick = {

                                running = false

                                totalSeconds =
                                    defaultDuration
                            }
                        )
                    }
                }
            }
        }
    }
}   
/*
 * ─────────────────────────────────────────────────────────────
 * MODE SWITCHER
 * ─────────────────────────────────────────────────────────────
 */

@Composable
private fun SimranModeSwitcher(
    currentMode: SimranMode,
    displayLanguage: DisplayLanguage,
    onModeSelected: (SimranMode) -> Unit
) {

    val modes =
        SimranMode.entries

    val selectedIndex =
        modes.indexOf(currentMode)

    val containerColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Color.Black.copy(alpha = 0.04f)
        }

        ThemeMode.Dark -> {
            Color.White.copy(alpha = 0.05f)
        }

        ThemeMode.Amoled -> {
            Color.White.copy(alpha = 0.04f)
        }
    }

    Surface(

        shape = RoundedCornerShape(100.dp),

        color = containerColor
    ) {

        BoxWithConstraints(

            modifier = Modifier
                .height(48.dp)
                .padding(4.dp)
        ) {

            val itemWidth =
                maxWidth / modes.size

            val animatedOffset by animateDpAsState(

                targetValue =
                    itemWidth * selectedIndex,

                label = ""
            )

            Box(

                modifier = Modifier
                    .offset(x = animatedOffset)
                    .width(itemWidth)
                    .fillMaxHeight()
                    .clip(
                        RoundedCornerShape(100.dp)
                    )
                    .background(

                        Brush.horizontalGradient(

                            listOf(

                                Gold.copy(alpha = 0.24f),
                                Gold.copy(alpha = 0.14f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                modes.forEach { mode ->

                    val selected =
                        currentMode == mode

                    val textColor by animateColorAsState(

                        targetValue =

                            if (selected)
                                Gold
                            else
                                TextPrimary.copy(
                                    alpha = 0.60f
                                ),

                        label = ""
                    )

                    Box(

                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(
                                RoundedCornerShape(
                                    100.dp
                                )
                            )
                            .clickable {
                                onModeSelected(mode)
                            },

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(

                            text =
                                mode.title.translate(displayLanguage),

                            color =
                                textColor,

                            fontSize = 12.sp,

                            fontWeight =

                                if (selected)
                                    FontWeight.SemiBold
                                else
                                    FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun MeditationCircle(
    minutes: Int,
    seconds: Int,
    running: Boolean,
    mode: SimranMode,
    breathingIn: Boolean,
    colors: PracticeColors,
    displayLanguage: DisplayLanguage
) {

    /*
     * THEME COLORS
     */

    val outerGlow = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            colors.accent.copy(alpha = 0.16f)
        }

        ThemeMode.Dark -> {
            Gold.copy(alpha = 0.16f)
        }

        ThemeMode.Amoled -> {
            Gold.copy(alpha = 0.12f)
        }
    }

    val innerGlow = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            colors.accent.copy(alpha = 0.32f)
        }

        ThemeMode.Dark -> {
            Gold.copy(alpha = 0.24f)
        }

        ThemeMode.Amoled -> {
            Gold.copy(alpha = 0.20f)
        }
    }

    val centerColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Color(0xFFF8F5EF)
        }

        ThemeMode.Dark -> {
            Color(0xFF171717)
        }

        ThemeMode.Amoled -> {
            Color(0xFF050505)
        }
    }

    val textPrimary = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Color(0xFF2B241D)
        }

        else -> {
            Color.White
        }
    }

    val textSecondary = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Color(0xFF6F665A)
        }

        else -> {
            Color.White.copy(alpha = 0.62f)
        }
    }

    /*
     * ANIMATIONS
     */

    val infinite =
        rememberInfiniteTransition(
            label = "MeditationCircle"
        )

    val breathingScale by infinite.animateFloat(

        initialValue = 0.95f,

        targetValue = 1.05f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                durationMillis = 3200,
                easing = EaseInOutCubic
            ),

            repeatMode = RepeatMode.Reverse
        ),

        label = "BreathingScale"
    )

    val haloRotation by infinite.animateFloat(

        initialValue = 0f,

        targetValue = 360f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                durationMillis = 22000,
                easing = LinearEasing
            )
        ),

        label = "HaloRotation"
    )

    val pulseAlpha by infinite.animateFloat(

        initialValue = 0.45f,

        targetValue = 1f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                durationMillis = 1800,
                easing = EaseInOutSine
            ),

            repeatMode = RepeatMode.Reverse
        ),

        label = "PulseAlpha"
    )

    /*
     * UI
     */

    Box(

        modifier = Modifier
            .size(310.dp),

        contentAlignment =
            Alignment.Center
    ) {

        /*
         * OUTER HALO
         */

        Box(

            modifier = Modifier
                .size(300.dp)
                .scale(

                    if (running)
                        breathingScale
                    else
                        1f
                )
                .graphicsLayer {

                    rotationZ =
                        haloRotation
                }
                .clip(CircleShape)
                .background(

                    Brush.sweepGradient(

                        listOf(

                            Color.Transparent,

                            outerGlow,

                            Gold.copy(alpha = 0.10f),

                            outerGlow,

                            Color.Transparent
                        )
                    )
                )
        )

        /*
         * SECONDARY GLOW
         */

        Box(

            modifier = Modifier
                .size(250.dp)
                .scale(

                    if (running)
                        breathingScale * 0.98f
                    else
                        1f
                )
                .clip(CircleShape)
                .background(

                    Brush.radialGradient(

                        colors = listOf(

                            innerGlow,

                            Color.Transparent
                        )
                    )
                )
        )

        /*
         * INNER CORE
         */

        Surface(

            modifier = Modifier
                .size(220.dp),

            shape = CircleShape,

            color = centerColor,

            border = BorderStroke(

                width = 1.dp,

                color =

                    if (running) {

                        Gold.copy(alpha = 0.20f)

                    } else {

                        textSecondary.copy(
                            alpha = 0.10f
                        )
                    }
            ),

            shadowElevation =

                if (running)
                    18.dp
                else
                    6.dp
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                /*
                 * INNER LIGHT
                 */

                Box(

                    modifier = Modifier
                        .fillMaxSize()
                        .background(

                            Brush.radialGradient(

                                colors = listOf(

                                    Color.White.copy(
                                        alpha =

                                            if (
                                                AppTheme.themeMode ==
                                                ThemeMode.Light
                                            ) {
                                                0.10f
                                            } else {
                                                0.04f
                                            }
                                    ),

                                    Color.Transparent
                                )
                            )
                        )
                )

                Column(

                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    /*
                     * MANTRA
                     */

                    Text(

                        text = when {

                            !running -> {

                                "Practice"
                                    .translate(displayLanguage)
                            }

                            mode ==
                                SimranMode.SaasSaas -> {

                                if (breathingIn)
                                    "ਵਾਹਿ"
                                else
                                    "ਗੁਰੂ"
                            }

                            mode ==
                                SimranMode.Dhyaan -> {

                                "ਵਾਹਿਗੁਰੂ"
                            }

                            else -> {

                                "ਅਨਹਦੁ"
                            }
                        },

                        color =

                            if (running)
                                colors.accent
                            else
                                textPrimary,

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold,

                        textAlign =
                            TextAlign.Center
                    )

                    Spacer(
                        Modifier.height(2.dp)
                    )

                    /*
                     * TIMER
                     */

                    Text(

                        text =
                            "%02d:%02d".format(
                                minutes,
                                seconds
                            ),

                        color =
                            textPrimary,

                        fontSize = 22.sp,

                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Spacer(
                        Modifier.height(2.dp)
                    )

                    
                    /*
                     * LIVE PULSE DOT
                     */

                    AnimatedVisibility(
                        visible = running
                    ) {

                        Column {

                            Spacer(
                                Modifier.height(16.dp)
                            )

                            Box(

                                modifier = Modifier
                                    .size(10.dp)
                                    .scale(pulseAlpha)
                                    .clip(CircleShape)
                                    .background(

                                        Brush.radialGradient(

                                            listOf(

                                                Gold,

                                                Gold.copy(
                                                    alpha = 0.40f
                                                )
                                            )
                                        )
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
private fun MeditationActionButton(

    icon: ImageVector,

    text: String,

    onClick: () -> Unit
) {

    val glassColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {

            Color(0xFFF8F8F8).copy(
                alpha = 0.92f
            )
        }

        ThemeMode.Dark -> {

            Color(0xFF1E1E1E).copy(
                alpha = 0.88f
            )
        }

        ThemeMode.Amoled -> {

            Color(0xFF121212).copy(
                alpha = 0.82f
            )
        }
    }

    val contentColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Primary
        }

        else -> {
            Color.White
        }
    }

    Surface(

        onClick = onClick,

        shape = RoundedCornerShape(100.dp),

        color = glassColor
    ) {

        Row(

            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 12.dp
            ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = Gold,

                modifier = Modifier.size(18.dp)
            )

            Spacer(
                Modifier.width(8.dp)
            )

            Text(

                text = text,

                color = contentColor,

                fontWeight =
                    FontWeight.SemiBold
            )
        }
    }
}
