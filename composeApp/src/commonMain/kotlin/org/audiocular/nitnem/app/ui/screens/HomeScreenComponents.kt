package org.audiocular.nitnem.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.draw.drawBehind
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin
import kotlin.math.cos
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.PI
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.interaction.MutableInteractionSource
import org.audiocular.nitnem.models.NitnemUiState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.audiocular.nitnem.app.ui.components.RollingNumberText
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.clickable
import org.audiocular.nitnem.app.models.AppTab
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.app.ui.components.BaniCard
import org.audiocular.nitnem.app.ui.components.HeroCard
import org.audiocular.nitnem.app.ui.components.MascotBani
import org.audiocular.nitnem.app.ui.components.MascotState
import org.audiocular.nitnem.app.ui.components.WeeklyStreakDialog
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import org.audiocular.nitnem.app.ui.practice.BaniAiAssistantTab
import org.audiocular.nitnem.app.ui.practice.practiceColors
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateDigits
import org.audiocular.nitnem.utils.translateAng
import androidx.compose.ui.text.style.TextAlign
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.BaniLine
import androidx.compose.runtime.LaunchedEffect
import org.audiocular.nitnem.data.HukamnamaProvider
import org.audiocular.nitnem.audio.StreamAudioController
import androidx.compose.ui.draw.scale
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

sealed interface HomeSheet {

    data object AppInfo : HomeSheet

    data class BaniInfo(val bani: Bani) : HomeSheet

}

@Composable
fun PremiumInfoSection(

    icon: ImageVector,

    title: String,

    content: String
) {

    Surface(

        shape = RoundedCornerShape(24.dp),

        color =
            Accent.copy(alpha = 0.05f),

        border =
            BorderStroke(
                1.dp,
                Accent.copy(alpha = 0.08f)
            )
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
               .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            )
        ) {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(

                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Accent.copy(alpha = 0.12f)
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector = icon,

                        contentDescription = null,

                        tint = Accent,

                        modifier =
                            Modifier.size(20.dp)
                    )
                }

                Spacer(
                    Modifier.width(12.dp)
                )

                Text(

                    text = title,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 17.sp,

                    color =
                        if (AppTheme.themeMode == ThemeMode.Light)
                            Color(0xFF1B1815)
                        else
                            Color.White
                )
            }

            Spacer(
                Modifier.height(16.dp)
            )

            Text(

                text = content,

                color =
                    TextPrimary.copy(alpha = 0.80f),

                lineHeight = 20.sp,

                fontSize = 13.sp
                    )
                }
            }
        }
   
enum class AnalyticsCardType {

    Today,
    Consistency,
    Listening
}

@Composable
fun PremiumAnalyticsCard(

    modifier: Modifier = Modifier,

    title: String,

    value: String,

    subtitle: String,

    icon: ImageVector,

    type: AnalyticsCardType,

    progress: Float
) {

    val infinite =
        rememberInfiniteTransition(
            label = "analytics_bg"
        )

    val glow by infinite.animateFloat(

        initialValue = 0.04f,

        targetValue = 0.10f,

        animationSpec = infiniteRepeatable(

            animation = tween(2400),

            repeatMode = RepeatMode.Reverse
        ),

        label = "glow"
    )

    val wavePhase by infinite.animateFloat(

        initialValue = 0f,

        targetValue = 2f * PI.toFloat(),

        animationSpec = infiniteRepeatable(

            animation = tween(
                4000,
                easing = LinearEasing
            ),

            repeatMode = RepeatMode.Restart
        ),

        label = "wave"
    )

    val floatPhase by infinite.animateFloat(

        initialValue = 0f,

        targetValue = 1f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                6000,
                easing = LinearEasing
            ),

            repeatMode = RepeatMode.Restart
        ),

        label = "float"
    )
val animatedProgress by animateFloatAsState(

    targetValue = progress.coerceIn(0f, 1f),

    animationSpec = tween(
        durationMillis = 900,
        easing = EaseOutCubic
    ),

    label = "analytics_progress"
)
val scale by animateFloatAsState(

    targetValue =

        if (progress > 0f) 1f else 0.96f,

    animationSpec = spring(

        dampingRatio = 0.7f,

        stiffness = 180f

    ),

    label = "card_scale"

)
        Card(
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            1.dp,
            Accent.copy(alpha = 0.08f)
        )
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            when(type) {
                                AnalyticsCardType.Listening -> {
                                    Accent.copy(alpha = 0.03f + (animatedProgress * 0.08f))
                                }
                                else -> {
                                    Accent.copy(alpha = glow)
                                }
                            },
                            Gold.copy(alpha = 0.03f)
                        )
                    )
                )
                .drawBehind {
                    if (animatedProgress > 0.8f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Gold.copy(alpha = 0.10f), Color.Transparent)
                            ),
                            radius = this.size.maxDimension * 0.72f
                        )
                    }
                }
        ) {
          /*
             * FIX: DYNAMIC CORNER MASCOT BACKGROUND
             * Places mascot in different corners depending on card type:
             * - Today: Bottom-End (Bottom Right)
             * - Listening: Bottom-Start (Bottom Left, mirrored inwards)
             * - Consistency: Top-End (Top Right, leaves space for heatmap)
             */
            val (mascotAlignment, mascotPadding, mascotSize) = when (type) {
                AnalyticsCardType.Today -> Triple(
                    Alignment.BottomEnd,
                    Modifier.padding(end = 4.dp, bottom = 4.dp),
                    105.dp
                )
                AnalyticsCardType.Listening -> Triple(
                    Alignment.BottomStart,
                    Modifier.padding(start = 4.dp, bottom = 4.dp),
                    105.dp
                )
                AnalyticsCardType.Consistency -> Triple(
                    Alignment.TopEnd,
                    Modifier.padding(end = 0.dp, top = 0.dp),
                    92.dp
                )
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .then(mascotPadding),
                contentAlignment = mascotAlignment
            ) {
                val isLightMode = AppTheme.themeMode == ThemeMode.Light

                MascotBani(
                    state = when (type) {
                        AnalyticsCardType.Today -> MascotState.Celebrating
                        AnalyticsCardType.Listening -> MascotState.Listening
                        AnalyticsCardType.Consistency -> MascotState.Meditating
                    },
                    modifier = Modifier
                        .size(mascotSize)
                        .graphicsLayer {
                            alpha = if (isLightMode) 0.14f else 0.24f
                            
                            when (type) {
                                AnalyticsCardType.Listening -> {
                                    // Mirror image horizontally for Listening analytics card
                                    scaleX = -1f
                                }
                                AnalyticsCardType.Consistency -> {
                                    // Position diagonally peaking in from the top-right edge
                                    translationX = 14f
                                    translationY = -8f
                                    rotationZ = -12f
                                }
                                else -> {}
                            }
                        }
                )
            }

            /*
             * CANVAS FX
             */

            Canvas(
                modifier = Modifier.matchParentSize()
            ) {

                val width = size.width
                val height = size.height

                when(type) {

                    /*
                     * TODAY
                     * WATER FILL
                     */

      AnalyticsCardType.Today -> {

    /*
     * LIQUID LEVEL
     */

 val liquidLevel =
    height -
        (
            height * 0.72f * animatedProgress
            )

    /*
     * MOTION
     */

    val phase =
        wavePhase * 0.75f

    val waveAmplitude =
        4f

    val rippleAmplitude =
        1.4f

    val wavelength =
        width * 0.9f

    /*
     * MAIN LIQUID PATH
     */

    val liquidPath = Path()

    liquidPath.moveTo(
        -60f,
        height
    )

    liquidPath.lineTo(
        -60f,
        liquidLevel
    )

    var previousX = -60f

    var previousY = liquidLevel

    var firstPoint = true

    for (
        xInt in -60..(width.toInt() + 60) step 20
    ) {

        val x = xInt.toFloat()

        /*
         * FLATTER WATER MOTION
         */

        val largeWave =
            sin(
                (
                    x / wavelength
                    ) *
                    (2f * PI.toFloat()) +
                    phase
            ) * waveAmplitude

        val smallRipple =
            cos(
                (
                    x / (
                        wavelength * 0.45f
                        )
                    ) *
                    (2f * PI.toFloat()) +
                    (
                        phase * 1.6f
                        )
            ) * rippleAmplitude

        val y =
            liquidLevel +
                largeWave +
                smallRipple

        if (firstPoint) {

            liquidPath.lineTo(
                x,
                y
            )

            firstPoint = false

        } else {

            val midX =
                (previousX + x) / 2f

            val midY =
                (previousY + y) / 2f

            liquidPath.quadraticBezierTo(

                previousX,
                previousY,

                midX,
                midY
            )
        }

        previousX = x
        previousY = y
    }

    liquidPath.lineTo(
        width + 60f,
        height
    )

    liquidPath.close()

    /*
     * FILLED LIQUID
     */

    drawPath(

        path = liquidPath,

        brush = Brush.verticalGradient(

            colors = listOf(

          Accent.copy(alpha = 0.30f),

Gold.copy(alpha = 0.16f)
            ),

            startY = liquidLevel,

            endY = height
        )
    )

    /*
     * SURFACE HIGHLIGHT
     */

    drawPath(

        path = liquidPath,

        brush = Brush.horizontalGradient(

            listOf(

              Gold.copy(alpha = 0.22f),

Accent.copy(alpha = 0.42f),

Gold.copy(alpha = 0.22f)
            )
        ),

        style = Stroke(

            width = 1.4f,

            cap = StrokeCap.Round
        )
    )
}
                    /*
                     * LISTENING
                     * AUDIO BARS
                     */
                    AnalyticsCardType.Listening -> {

                        // Intentionally minimal
                    }
                    /*
                     * CONSISTENCY
                     * EMBERS
                     */

                    AnalyticsCardType.Consistency -> {

                        if (
                            animatedProgress > 0.2f
                        ) {

                            repeat(6) { index ->

                                val emberY =
                                    height * (
                                        0.15f +
                                            (
                                                (
                                                    floatPhase +
                                                        index * 0.12f
                                                    ) % 1f
                                                )
                                        )

                                drawCircle(

                                    color =
                                        Gold.copy(
                                            alpha = 0.22f
                                        ),

                                    radius = 3f,

                                    center = Offset(

                                        width * 0.5f +
                                            sin(
                                                wavePhase +
                                                    index
                                            ) * 20f,

                                        emberY
                                    )
                                )
                            }
                        }
                    }

                }
            }

          Column(

    modifier = Modifier
        .fillMaxSize()
        .padding(
            horizontal = 8.dp,
            vertical = 10.dp
        ),

    horizontalAlignment =
        Alignment.CenterHorizontally
) {

    /*
     * ICON
     */

    Box(

        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .scale(

    if (
        type ==
        AnalyticsCardType.Listening
    ) {

        1f + (
            animatedProgress * 0.06f
        )

    } else {

        1f
    }
)
            .background(

                Brush.linearGradient(

                    listOf(
                        Accent,
                        Gold
                    )
                )
            ),

        contentAlignment =
            Alignment.Center
    ) {

        Icon(

            imageVector = icon,

            contentDescription = null,

            tint = Color.White,

            modifier = Modifier.size(18.dp)
        )
    }

    Spacer(
        Modifier.height(6.dp)
    )

    /*
     * VALUE
     */

    RollingNumberText(
        value = value,
        color =
            if (
                AppTheme.themeMode ==
                ThemeMode.Light
            ) {
                Color(0xFF1F1B16)
            } else {
                Color.White
            },
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )


    Text(

        text = title,

        color =
            TextPrimary.copy(
                alpha = 0.72f
            ),

        fontSize = 12.sp,

        fontWeight =
            FontWeight.SemiBold
    )


    Text(

        text = subtitle,

        color =
            TextPrimary.copy(
                alpha = 0.45f
            ),

        fontSize = 10.sp,

        maxLines = 1,

        overflow =
            TextOverflow.Ellipsis
    )

    Spacer(
        Modifier.weight(1f)
    )

    /*
     * HEATMAP
     */

    if (
        type ==
        AnalyticsCardType.Consistency
    ) {

        MonthlyHeatmap(
            progress =
                animatedProgress
        )

        Spacer(
            Modifier.height(6.dp)
        )
    }
}
        }
    }
}
    @Composable
fun MonthlyHeatmap(
    progress: Float
) {

    val activeCount =
        (28 * progress).toInt()

    Column(

        verticalArrangement =
            Arrangement.spacedBy(2.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        repeat(4) { row ->

            Row(

                horizontalArrangement =
                    Arrangement.spacedBy(2.dp)
            ) {

                repeat(7) { col ->

                    val index =
                        row * 7 + col

                    val active =
                        index < activeCount

                    val backgroundBrush = if (active) {

                        Brush.linearGradient(

                            listOf(
                                Gold,
                                Accent
                            )
                        )

                    } else {

                        Brush.linearGradient(

                            listOf(
                                Color(0xFF8C8478)
                                    .copy(alpha = 0.20f),

                                Color(0xFF5E584F)
                                    .copy(alpha = 0.12f)
                            )
                        )
                    }

                    Box(

                        modifier = Modifier
                            .size(7.dp)
                            .clip(
                                RoundedCornerShape(2.dp)
                            )
                            .background(
                                brush = backgroundBrush
                            )
                    )
                }
            }
        }
    }
}


@Composable
fun BaniDetailItem(
    label: String,
    value: String,
    displayLanguage: DisplayLanguage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = TextPrimary.copy(alpha = 0.6f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.6f)
        )
    }
}
fun calculatePlaybackProgress(
    bani: Bani,
    state: NitnemUiState,
    isCurrent: Boolean
): Float {

    return when {

        isCurrent -> {

            if (state.playback.durationMs <= 0L) {

                0f

            } else {

                (
                    state.playback.positionMs.toFloat() /
                        state.playback.durationMs.toFloat()
                ).coerceIn(0f, 1f)
            }
        }

        else -> 0f
    }
}
fun getMascotMessage(
    state: NitnemUiState,
    displayLanguage: DisplayLanguage
): String {

    val messages = mutableListOf<String>()
    val currentHour: Int = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).hour
    val completedBanis = state.completedBanis.map { it.lowercase() }

    val totalRequiredCount = 5

    if (state.completedBanis.size >= totalRequiredCount) {
        return when (displayLanguage) {
            DisplayLanguage.Punjabi ->
                "ਵਾਹਿਗੁਰੂ! ਤੁਸੀਂ ਅੱਜ ਆਪਣਾ ਸਾਰਾ ਨਿਤਨੇਮ ਪਾਠ ਪੂਰਾ ਕਰ ਲਿਆ ਹੈ। ਬਹੁਤ ਵਧੀਆ!"

            DisplayLanguage.Hindi ->
                "वाहेगुरु! आपने आज अपना सारा नितनेम पाठ पूरा कर लिया है। बहुत बढ़िया!"

            DisplayLanguage.English ->
                "Waheguru! You have completed all your daily Nitnem path today. Splendid dedication!"
        }
    }

    when {

        currentHour in 3..9 &&
            !completedBanis.contains("japji sahib") -> {

            messages.add(
                when (displayLanguage) {
                    DisplayLanguage.Punjabi ->
                        "ਅੰਮ੍ਰਿਤ ਵੇਲਾ ਚੱਲ ਰਿਹਾ ਹੈ। ਆਓ ਅੱਜ ਜਪੁਜੀ ਸਾਹਿਬ ਦਾ ਪਾਠ ਸ਼ੁਰੂ ਕਰੀਏ!"

                    DisplayLanguage.Hindi ->
                        "अमृत वेला चल रहा है। आइए आज जपुजी साहिब का पाठ शुरू करें!"

                    DisplayLanguage.English ->
                        "It's Amrit Vela. Let's start the day with Japji Sahib!"
                }
            )
        }

        currentHour in 6..11 &&
            !completedBanis.contains("jaap sahib") -> {

            messages.add(
                when (displayLanguage) {
                    DisplayLanguage.Punjabi ->
                        "ਕੀ ਤੁਸੀਂ ਅੱਜ ਜਾਪੁ ਸਾਹਿਬ ਜੀ ਦਾ ਪਾਠ ਕੀਤਾ ਹੈ? ਆਓ ਇਸ ਨੂੰ ਸ਼ੁਰੂ ਕਰੀਏ।"

                    DisplayLanguage.Hindi ->
                        "क्या आपने आज जापु साहिब जी का पाठ किया है? आइए इसे शुरू करें।"

                    DisplayLanguage.English ->
                        "Have you read Jaap Sahib today? Let's tune in now."
                }
            )
        }

        currentHour in 16..19 &&
            !completedBanis.contains("rehras sahib") -> {

            messages.add(
                when (displayLanguage) {
                    DisplayLanguage.Punjabi ->
                        "ਸ਼ਾਮ ਹੋ ਗਈ ਹੈ। ਆਓ ਰਹਿਰਾਸ ਸਾਹਿਬ ਜੀ ਦੇ ਪਾਠ ਨਾਲ ਸ਼ਾਂਤੀ ਪ੍ਰਾਪਤ ਕਰੀਏ।"

                    DisplayLanguage.Hindi ->
                        "शाम हो गई है। आइए रहिरास साहिब जी के पाठ से शांति प्राप्त करें।"

                    DisplayLanguage.English ->
                        "It is evening. Let's find peace with Rehras Sahib."
                }
            )
        }

        currentHour >= 20 &&
            !completedBanis.contains("kirtan sohila") -> {

            messages.add(
                when (displayLanguage) {
                    DisplayLanguage.Punjabi ->
                        "ਸੌਣ ਤੋਂ ਪਹਿਲਾਂ ਕੀਰਤਨ ਸੋਹਿਲਾ ਜੀ ਦਾ ਪਾਠ ਕਰਕੇ ਚੰਗੀ ਨੀਂਦ ਅਤੇ ਸ਼ਾਂਤੀ ਪ੍ਰਾਪਤ ਕਰੋ।"

                    DisplayLanguage.Hindi ->
                        "सोने से पहले कीर्तन सोहिला जी का पाठ करके अच्छी नींद और शांति प्राप्त करें।"

                    DisplayLanguage.English ->
                        "Read Kirtan Sohila before sleeping to bring tranquility and protection."
                }
            )
        }
    }

    if (state.streakDays > 0) {

        messages.add(
            when (displayLanguage) {
                DisplayLanguage.Punjabi ->
                    "ਤੁਹਾਡੀ ਲੜੀ ${state.streakDays} ਦਿਨਾਂ ਦੀ ਹੈ! ਅੱਜ ਦਾ ਪਾਠ ਕਰਕੇ ਇਸ ਲੜੀ ਨੂੰ ਸੁਰੱਖਿਅਤ ਕਰੋ।"

                DisplayLanguage.Hindi ->
                    "आपकी निरंतरता ${state.streakDays} दिनों की है! आज का पाठ करके इसे सुरक्षित करें।"

                DisplayLanguage.English ->
                    "You're on a ${state.streakDays}-day streak! Secure it today by finishing your daily path."
            }
        )
    }

    if (state.totalXp > 0) {

        messages.add(
            when (displayLanguage) {
                DisplayLanguage.Punjabi ->
                    "ਤੁਸੀਂ ਅੱਜ ${state.totalXp} XP ਕਮਾ ਲਏ ਹਨ। ਵਾਹਿਗੁਰੂ ਤੁਹਾਡੇ ਅਭਿਆਸ ਵਿੱਚ ਵਾਧਾ ਕਰੇ!"

                DisplayLanguage.Hindi ->
                    "आपने आज ${state.totalXp} XP कमा लिए हैं। वाहेगुरु आपके अभ्यास में वृद्धि करे!"

                DisplayLanguage.English ->
                    "You've earned ${state.totalXp} XP today! Keep engaging with Gurbani to grow."
            }
        )
    }

    messages.add(
        when (displayLanguage) {
            DisplayLanguage.Punjabi ->
                "ਆਪਣੀ ਬਾਣੀ ਦੀ ਸਮਝ ਨੂੰ ਪਰਖਣ ਲਈ ਪ੍ਰੈਕਟਿਸ ਟੈਬ ਵਿੱਚ AI ਗੁਰਬਾਣੀ ਕੁਇਜ਼ ਖੇਡੋ!"

            DisplayLanguage.Hindi ->
                "अपनी बाणी की समझ को परखने के लिए प्रैक्टिस टैब में AI गुरबाणी क्विज़ खेलें!"

            DisplayLanguage.English ->
                "Test your understanding of Gurbani with the AI Quiz in the Practice tab."
        }
    )

    messages.add(
        when (displayLanguage) {
            DisplayLanguage.Punjabi ->
                "ਅੱਜ ਮਨ ਨੂੰ ਸ਼ਾਂਤ ਕਰਨ ਲਈ ਸਿਮਰਨ ਅਭਿਆਸ ਵਿੱਚ ਕੁਝ ਸਮਾਂ ਬਿਤਾਓ।"

            DisplayLanguage.Hindi ->
                "आज मन को शांत करने के लिए सिमरन अभ्यास में कुछ समय बिताएं।"

            DisplayLanguage.English ->
                "Spend a few minutes in Simran Abhyas today."
        }
    )

    if (messages.isEmpty()) {

        messages.add(
            when (displayLanguage) {
                DisplayLanguage.Punjabi ->
                    "ਵਾਹਿਗੁਰੂ ਜੀ ਕਾ ਖਾਲਸਾ ਵਾਹਿਗੁਰੂ ਜੀ ਕੀ ਫਤਿਹ।"

                DisplayLanguage.Hindi ->
                    "वाहेगुरु जी का खालसा, वाहेगुरु जी की फतेह।"

                DisplayLanguage.English ->
                    "Waheguru Ji Ka Khalsa, Waheguru Ji Ki Fateh."
            }
        )
    }

    return messages.random()
}