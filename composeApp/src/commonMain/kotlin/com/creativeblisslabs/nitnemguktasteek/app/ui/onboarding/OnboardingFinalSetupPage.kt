package com.creativeblisslabs.nitnemguktasteek.app.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import kotlin.random.Random
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.MascotBani
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.MascotState
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.NitnemLogo
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun FinalSetupPage(
    onContinue: () -> Unit,
    displayLanguage: DisplayLanguage
) {

    // 1. Declare this state property at the top of your Composable screen
    var themeOverrideHour by remember { mutableStateOf<Int?>(null) }
    // TIME-OF-DAY ATMOSPHERIC ENGINE
    val systemCurrentHour: Int = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
    }

    // 2. Derive the active currentHour based on whether an override is active
    val currentHour = themeOverrideHour ?: systemCurrentHour
    val activeHour = currentHour

    val isAmritVela = currentHour in 3..5
    val isDay = currentHour in 6..16
    val isEvening = currentHour in 17..20
    val isNight = currentHour >= 21 || currentHour < 3

    val isLight = AppTheme.themeMode == ThemeMode.Light
    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.68f)

    data class BaniSlide(
        val mascotState: MascotState,
        val heading: String,
        val body: String,
        val accentColor: Color
    )

    val slides = remember(displayLanguage) {
        listOf(
            BaniSlide(
                mascotState = MascotState.Idle,
                heading = "Meet Bani".translate(displayLanguage),
                body = "In the quiet, sacred hours of Amrit Vela, a small golden bird named Bani wakes up — her wings painted with the warm orange hues of a breaking dawn, carrying an ancient crimson Gutka Steek close to her chest.".translate(displayLanguage),
                accentColor = Gold
            ),
            BaniSlide(
                mascotState = MascotState.Thinking,
                heading = "Your Guardian of Consistency".translate(displayLanguage),
                body = "Bani's entire purpose is to guard mindfulness and consistency. She flew from her digital sanctuary to sit right on your home screen — ready to guide you through your daily Nitnem, one line at a time.".translate(displayLanguage),
                accentColor = Color(0xFFFFD166)
            ),
            BaniSlide(
                mascotState = MascotState.Reading,
                heading = "A Joyful Companion".translate(displayLanguage),
                body = "Whenever you open your Gutka Steek with a smile, Bani flutters her golden wings with pure joy, cheering you on as you read each sacred verse of Gurbani.".translate(displayLanguage),
                accentColor = Color(0xFFFFB703)
            ),
            BaniSlide(
                mascotState = MascotState.Happy,
                heading = "Exploring Meanings".translate(displayLanguage),
                body = "Wondering what a line of Gurbani means? Bani taps her tiny beak in deep thought, looking into her magical library to find the translation and word references just for you.".translate(displayLanguage),
                accentColor = Color(0xFFFB8500)
            ),
            BaniSlide(
                mascotState = MascotState.Meditating,
                heading = "Peaceful Focus".translate(displayLanguage),
                body = "When it is time for Simran, Bani closes her eyes and sits in quiet meditation, helping your heart find calmness, peace, and deep concentration.".translate(displayLanguage),
                accentColor = Color(0xFFFFC107)
            ),
            BaniSlide(
                mascotState = MascotState.Listening,
                heading = "Listening to Your Voice".translate(displayLanguage),
                body = "Bani cups her tiny ear to listen closely when you use your voice or practice reciting your prayers out loud, making sure every word is heard with love.".translate(displayLanguage),
                accentColor = Color(0xFF8ECAE6)
            ),
            BaniSlide(
                mascotState = MascotState.Sleeping,
                heading = "Resting After Sohila".translate(displayLanguage),
                body = "If you complete your Kirtan Sohila before bedtime, Bani sleeps soundly and peacefully under the starry night sky, knowing your sleep is guarded by peace.".translate(displayLanguage),
                accentColor = Color(0xFF7C83FD)
            ),
            BaniSlide(
                mascotState = MascotState.Namaste,
                heading = "Gentle Guidance".translate(displayLanguage),
                body = "If a prayer is missed or a mistake is made, Bani tilts her head with a caring look, reminding you gently that every new morning is a fresh start to try again.".translate(displayLanguage),
                accentColor = Color(0xFFEF5350)
            ),
            BaniSlide(
                mascotState = MascotState.Celebrating,
                heading = "Celebrating Your Streak".translate(displayLanguage),
                body = "When you build a prayer streak, a vibrant golden aura ignites around Bani. She leaps with joy in a state of glowing celebration — because consistency is its own reward!".translate(displayLanguage),
                accentColor = Color(0xFF69F0AE)
            ),
            BaniSlide(
                mascotState = MascotState.Reflecting,
                heading = "Wisdom of the Gurus".translate(displayLanguage),
                body = "Bani looks back through history with wide, curious eyes, sharing timeless lessons and stories from the lives of the Gurus to inspire your everyday actions.".translate(displayLanguage),
                accentColor = Color(0xFFAB47BC)
            ),
            BaniSlide(
                mascotState = MascotState.AmritVela,
                heading = "The Early Morning Blessing".translate(displayLanguage),
                body = "Waking up before the sun? Bani puts on her brightest morning glow to greet the Amrit Vela hours with you, filling your day with spiritual energy and fresh hope.".translate(displayLanguage),
                accentColor = Color(0xFFFFEE93)
            ),
            BaniSlide(
                mascotState = MascotState.Simran,
                heading = "Chanting Waheguru".translate(displayLanguage),
                body = "With every bead counted and every breath focused, Bani whispers 'Waheguru' right alongside you, turning your daily practice into a magical journey of love and light.".translate(displayLanguage),
                accentColor = Color(0xFF48CAE4)
            )
        )
      
    }

    var currentSlideIndex by remember { mutableStateOf(0) }
    var customMascotStateOverride by remember { mutableStateOf<MascotState?>(null) }
    val currentSlide = slides[currentSlideIndex]
    val isLastSlide = currentSlideIndex == slides.lastIndex

    // Animation Sequence:
    //   0.00 → 0.42  Emerge briskly and naturally from cottage, taking a short step onto the yard path
    //   0.42 → 0.48  Gentle pause / look around
    //   0.48 → 1.00  Takes flight gracefully into the sky
    val animProgress = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, animationSpec = tween(durationMillis = 9800, easing = LinearEasing))
    }
    val progress = animProgress.value

    val totalWalkProgress = (progress / 0.42f).coerceIn(0f, 1f)
    val doorOpenProgress = (progress / 0.10f).coerceIn(0f, 1f)
    
    // Smooth soaring flight interpolation
    val rawFlyPhase = ((progress - 0.46f) / 0.54f).coerceIn(0f, 1f)
    val flyPhase = (rawFlyPhase * rawFlyPhase * (3f - 2f * rawFlyPhase)) // smoothstep S-curve
    
    // Scale: starts in doorway (0.22), increases by half while running/walking (to 0.52),
    // and increases the other half while soaring into the sky to a compact final sky size (0.82)
    val mascotScale = 0.22f + (totalWalkProgress * 0.30f) + (flyPhase * 0.30f)

    // ── 3D 360-DEGREE SPATIAL CAMERA & SLIDE ENGINE ──
    var userPanAngle by remember { mutableStateOf(0f) }
    var userTiltY by remember { mutableStateOf(0f) }
    var panVelocityX by remember { mutableStateOf(0f) }
    var isActivelyDragging by remember { mutableStateOf(false) }
    var joystickThumbOffset by remember { mutableStateOf(Offset.Zero) }

    val isPanningOrJoystickActive = isActivelyDragging || joystickThumbOffset != Offset.Zero
    val currentMascotState = when {
        isPanningOrJoystickActive && flyPhase > 0.1f -> MascotState.FlyingPanning
        customMascotStateOverride != null             -> customMascotStateOverride!!
        progress < 0.42f                              -> MascotState.Walking
        progress < 0.58f                              -> MascotState.Flying
        else                                          -> currentSlide.mascotState
    }
    val slideAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (progress >= 0.70f) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "slide_fade"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        
        // ANIMATED ELEMENTS: continuous loops for clouds, animal walking cycles, breathing/pecking, leaves
        val infiniteTransition = rememberInfiniteTransition(label = "village_idle")
        val cloudSwayX by infiniteTransition.animateFloat(
            initialValue = -w * 0.12f,
            targetValue = w * 0.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 18000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "clouds_sway"
        )
        val cloud2SwayX by infiniteTransition.animateFloat(
            initialValue = w * 0.10f,
            targetValue = -w * 0.10f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 22000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "clouds2_sway"
        )
        val animalBob by infiniteTransition.animateFloat(
            initialValue = -2.5f,
            targetValue = 2.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "animal_bob"
        )
        // Natural continuous 0..360 degree roaming phase (Very calm, gentle, slow pasture strolling)
        val cowRoamPhase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * kotlin.math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 36000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "cow_roam"
        )
        val cowWalkCycle = kotlin.math.sin(cowRoamPhase).toFloat() * 6f
        val goatRoamPhase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * kotlin.math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 30000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "goat_roam"
        )
        val cowLegCycle by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "cow_leg"
        )
        val chickenPeck by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 7f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 450, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "chicken_peck"
        )
        val chickenWalkCycle by infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "chicken_walk"
        )
        val cropWindSway by infiniteTransition.animateFloat(
            initialValue = -3.5f,
            targetValue = 3.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "crop_wind"
        )
        val windmillRotorDeg by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "windmill_rotor"
        )



        val skyBrush = remember(currentHour) {
            when {
                isAmritVela -> Brush.verticalGradient(
                    0.0f to Color(0xFF030712),
                    0.30f to Color(0xFF0F172A),
                    0.60f to Color(0xFF31102F),
                    0.85f to Color(0xFF831843),
                    1.0f to Color(0xFFFDE68A)
                )
                currentHour in 6..8 -> Brush.verticalGradient(
                    0.0f to Color(0xFF311042),
                    0.30f to Color(0xFF6B21A8),
                    0.55f to Color(0xFFC026D3),
                    0.75f to Color(0xFFFB923C),
                    1.0f to Color(0xFFFEF08A)
                )
                isDay -> Brush.verticalGradient(
                    0.0f to Color(0xFF0284C7),
                    0.35f to Color(0xFF38BDF8),
                    0.70f to Color(0xFF7DD3FC),
                    1.0f to Color(0xFFE0F2FE)
                )
                isEvening -> Brush.verticalGradient(
                    0.0f to Color(0xFF1E1B4B),
                    0.30f to Color(0xFF4C0519),
                    0.60f to Color(0xFF9F1239),
                    0.80f to Color(0xFFEA580C),
                    1.0f to Color(0xFFFDE047)
                )
                else -> Brush.verticalGradient(
                    0.0f to Color(0xFF020617),
                    0.40f to Color(0xFF0B1120),
                    0.75f to Color(0xFF172554),
                    1.0f to Color(0xFF1E293B)
                )
            }
        }

        val sunCenter = Offset(w * 0.22f, h * 0.12f)
        val sunRadius = w * 0.11f

        val rayPaths = remember(w, h) {
            List(12) { i ->
                val angle = (10.0 + i * 14.0) * PI / 180.0
                Path().apply {
                    moveTo(w * 0.22f, h * 0.12f)
                    lineTo(w * 0.22f + cos(angle).toFloat() * 1200f, h * 0.12f + sin(angle).toFloat() * 1200f)
                    lineTo(w * 0.22f + cos(angle + 0.07).toFloat() * 1200f, h * 0.12f + sin(angle + 0.07).toFloat() * 1200f)
                    close()
                }
            }
        }

        val mountainBrushes = remember(w, h, currentHour) {
            when {
                isNight || isAmritVela -> listOf(
                    Brush.verticalGradient(listOf(Color(0xFF131C2E), Color(0xFF1E293B)), startY = h * 0.22f, endY = h * 0.44f),
                    Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF283548)), startY = h * 0.24f, endY = h * 0.48f)
                )
                isEvening -> listOf(
                    Brush.verticalGradient(listOf(Color(0xFF3B2338), Color(0xFF53314B)), startY = h * 0.22f, endY = h * 0.44f),
                    Brush.verticalGradient(listOf(Color(0xFF4C2A44), Color(0xFF653B5D)), startY = h * 0.24f, endY = h * 0.48f)
                )
                else -> listOf(
                    Brush.verticalGradient(listOf(Color(0xFF4A6B82), Color(0xFF6B8CA3)), startY = h * 0.22f, endY = h * 0.44f),
                    Brush.verticalGradient(listOf(Color(0xFF38576E), Color(0xFF547894)), startY = h * 0.24f, endY = h * 0.48f)
                )
            }
        }

        val mountainPaths = remember(w, h) {
            val periodW = w * 3.0f
            val overlap = 20f
            // Deep, towering mountain ridge layers across expanded 3.0w domain with seam overlap padding
            val backRange = Path().apply {
                moveTo(-overlap, h * 0.44f)
                lineTo(0f, h * 0.44f)
                lineTo(w * 0.30f, h * 0.22f)
                lineTo(w * 0.65f, h * 0.36f)
                lineTo(w * 1.05f, h * 0.20f)
                lineTo(w * 1.45f, h * 0.34f)
                lineTo(w * 1.85f, h * 0.18f)
                lineTo(w * 2.25f, h * 0.35f)
                lineTo(w * 2.65f, h * 0.24f)
                lineTo(periodW, h * 0.44f)
                lineTo(periodW + overlap, h * 0.44f)
                lineTo(periodW + overlap, h + overlap)
                lineTo(-overlap, h + overlap)
                close()
            }
            val frontRange = Path().apply {
                moveTo(-overlap, h * 0.48f)
                lineTo(0f, h * 0.48f)
                lineTo(w * 0.35f, h * 0.30f)
                lineTo(w * 0.75f, h * 0.42f)
                lineTo(w * 1.15f, h * 0.28f)
                lineTo(w * 1.60f, h * 0.42f)
                lineTo(w * 2.05f, h * 0.26f)
                lineTo(w * 2.50f, h * 0.40f)
                lineTo(periodW, h * 0.48f)
                lineTo(periodW + overlap, h * 0.48f)
                lineTo(periodW + overlap, h + overlap)
                lineTo(-overlap, h + overlap)
                close()
            }
            listOf(backRange, frontRange)
        }

        val hillBrushes = remember(w, h, currentHour) {
            when {
                isNight || isAmritVela -> listOf(
                    Brush.verticalGradient(listOf(Color(0xFF052E16), Color(0xFF064E3B)), startY = h * 0.40f, endY = h * 0.65f),
                    Brush.verticalGradient(listOf(Color(0xFF064E3B), Color(0xFF065F46)), startY = h * 0.48f, endY = h * 0.75f),
                    Brush.verticalGradient(listOf(Color(0xFF065F46), Color(0xFF047857)), startY = h * 0.55f, endY = h * 0.85f),
                    Brush.verticalGradient(listOf(Color(0xFF047857), Color(0xFF059669)), startY = h * 0.62f, endY = h)
                )
                isEvening -> listOf(
                    Brush.verticalGradient(listOf(Color(0xFF1E3A1E), Color(0xFF2E5E2E)), startY = h * 0.40f, endY = h * 0.65f),
                    Brush.verticalGradient(listOf(Color(0xFF2E5E2E), Color(0xFF4D7C0F)), startY = h * 0.48f, endY = h * 0.75f),
                    Brush.verticalGradient(listOf(Color(0xFF4D7C0F), Color(0xFF65A30D)), startY = h * 0.55f, endY = h * 0.85f),
                    Brush.verticalGradient(listOf(Color(0xFF65A30D), Color(0xFF84CC16)), startY = h * 0.62f, endY = h)
                )
                else -> listOf(
                    Brush.verticalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32)), startY = h * 0.40f, endY = h * 0.65f),
                    Brush.verticalGradient(listOf(Color(0xFF2E7D32), Color(0xFF388E3C)), startY = h * 0.48f, endY = h * 0.75f),
                    Brush.verticalGradient(listOf(Color(0xFF388E3C), Color(0xFF4CAF50)), startY = h * 0.55f, endY = h * 0.85f),
                    Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFF81C784)), startY = h * 0.62f, endY = h)
                )
            }
        }

        val hillPaths = remember(w, h) {
            val periodW = w * 3.0f
            val overlap = 60f
            // Rich tiered hill contours with horizontal zero-tangent at 0 and periodW to guarantee 100% seam-free panoramic joining
            listOf(
                Path().apply {
                    moveTo(-overlap, h * 0.43f)
                    lineTo(0f, h * 0.43f)
                    cubicTo(w * 0.50f, h * 0.43f, w * 1.00f, h * 0.46f, w * 1.50f, h * 0.40f)
                    cubicTo(w * 2.00f, h * 0.35f, w * 2.50f, h * 0.43f, periodW, h * 0.43f)
                    lineTo(periodW + overlap, h * 0.43f)
                    lineTo(periodW + overlap, h + overlap)
                    lineTo(-overlap, h + overlap)
                    close()
                },
                Path().apply {
                    moveTo(-overlap, h * 0.50f)
                    lineTo(0f, h * 0.50f)
                    cubicTo(w * 0.50f, h * 0.50f, w * 1.00f, h * 0.46f, w * 1.50f, h * 0.51f)
                    cubicTo(w * 2.00f, h * 0.55f, w * 2.50f, h * 0.50f, periodW, h * 0.50f)
                    lineTo(periodW + overlap, h * 0.50f)
                    lineTo(periodW + overlap, h + overlap)
                    lineTo(-overlap, h + overlap)
                    close()
                },
                Path().apply {
                    moveTo(-overlap, h * 0.60f)
                    lineTo(0f, h * 0.60f)
                    cubicTo(w * 0.50f, h * 0.60f, w * 1.00f, h * 0.64f, w * 1.50f, h * 0.58f)
                    cubicTo(w * 2.00f, h * 0.54f, w * 2.50f, h * 0.60f, periodW, h * 0.60f)
                    lineTo(periodW + overlap, h * 0.60f)
                    lineTo(periodW + overlap, h + overlap)
                    lineTo(-overlap, h + overlap)
                    close()
                },
                Path().apply {
                    moveTo(-overlap, h * 0.71f)
                    lineTo(0f, h * 0.71f)
                    cubicTo(w * 0.50f, h * 0.71f, w * 1.00f, h * 0.66f, w * 1.50f, h * 0.72f)
                    cubicTo(w * 2.00f, h * 0.76f, w * 2.50f, h * 0.71f, periodW, h * 0.71f)
                    lineTo(periodW + overlap, h * 0.71f)
                    lineTo(periodW + overlap, h + overlap)
                    lineTo(-overlap, h + overlap)
                    close()
                }
            )
        }

        val grassTuftTextures = remember(w, h) {
            List(4) { layer ->
                val layerMinY = h * (0.48f + (layer * 0.08f))
                val random = Random(layer + 42)
                List(28) {
                    val baseOffset = Offset(
                        random.nextFloat() * (w * 3.0f),
                        layerMinY + (random.nextFloat() * (h - layerMinY) * 0.2f)
                    )
                    Path().apply {
                        moveTo(baseOffset.x, baseOffset.y)
                        quadraticTo(baseOffset.x - 4f, baseOffset.y - 12f, baseOffset.x - 8f, baseOffset.y - 16f)
                        quadraticTo(baseOffset.x - 2f, baseOffset.y - 10f, baseOffset.x, baseOffset.y)
                        quadraticTo(baseOffset.x + 1f, baseOffset.y - 16f, baseOffset.x + 2f, baseOffset.y - 22f)
                        quadraticTo(baseOffset.x + 3f, baseOffset.y - 12f, baseOffset.x, baseOffset.y)
                        quadraticTo(baseOffset.x + 5f, baseOffset.y - 10f, baseOffset.x + 10f, baseOffset.y - 14f)
                        quadraticTo(baseOffset.x + 3f, baseOffset.y - 6f, baseOffset.x, baseOffset.y)
                    }
                }
            }
        }

        val pondOffset = Offset(w * 0.12f, h * 0.74f)
        val pondSize = Size(w * 0.32f, h * 0.11f)
        val pondElements = remember(pondOffset, pondSize) {
            object {
                val lilyPads = listOf(Offset(0.18f, 0.35f), Offset(0.26f, 0.52f), Offset(0.68f, 0.42f), Offset(0.48f, 0.72f), Offset(0.80f, 0.55f))
                val fishList = listOf(
                    // (relX, relY, colorIndex, baseAngle, speedScale)
                    PondFish(0.32f, 0.48f, 0, -20f, 1.2f),
                    PondFish(0.55f, 0.35f, 1, 35f, 0.9f),
                    PondFish(0.68f, 0.62f, 2, -15f, 1.1f),
                    PondFish(0.42f, 0.70f, 0, 40f, 0.8f),
                    PondFish(0.78f, 0.45f, 1, -10f, 1.0f)
                )
                val reeds = listOf(0.08f, 0.14f, 0.78f, 0.86f, 0.92f)
            }
        }

        val pondBrush = remember(w, h, currentHour) {
            val pColors = if (isNight || isAmritVela) listOf(Color(0xFF0E7490), Color(0xFF083344)) else listOf(Color(0xFF00ACC1), Color(0xFF004D40))
            Brush.radialGradient(
                colors = pColors,
                center = Offset(pondOffset.x + (pondSize.width * 0.5f), pondOffset.y + (pondSize.height * 0.5f)),
                radius = pondSize.width * 0.6f
            )
        }

        // Cozy rustic village mud cottage (Situated further back in the middle landscape)
        val hutX = w * 1.12f
        val hutY = h * 0.395f
        val hutW = w * 0.28f
        val hutH = h * 0.138f
        val doorW = hutW * 0.32f
        val doorH = hutH * 0.72f
        val doorX = hutX + (hutW * 0.70f - doorW) / 2f
        val doorY = hutY + hutH - doorH

        val hutPaths = remember(w, h) {
            val mudFront = Path().apply {
                moveTo(hutX, hutY)
                lineTo(hutX + hutW * 0.70f, hutY)
                lineTo(hutX + hutW * 0.70f, hutY + hutH)
                lineTo(hutX, hutY + hutH)
                close()
            }
            val mudSide = Path().apply {
                moveTo(hutX + hutW * 0.70f, hutY)
                lineTo(hutX + hutW, hutY - 14f)
                lineTo(hutX + hutW, hutY + hutH - 14f)
                lineTo(hutX + hutW * 0.70f, hutY + hutH)
                close()
            }
            val doorPath = Path().apply {
                moveTo(doorX, doorY + doorH)
                lineTo(doorX, doorY + doorW * 0.45f)
                quadraticTo(doorX + doorW * 0.5f, doorY - 10f, doorX + doorW, doorY + doorW * 0.45f)
                lineTo(doorX + doorW, doorY + doorH)
                close()
            }
            val roofBase = Path().apply {
                moveTo(hutX - 18f, hutY + 6f)
                lineTo(hutX + hutW * 0.38f, hutY - 42f)
                lineTo(hutX + hutW + 18f, hutY - 10f)
                lineTo(hutX + hutW * 0.78f, hutY + 14f)
                close()
            }
            listOf(mudFront, mudSide, doorPath, roofBase)
        }

        // Road connects seamlessly to the hut doorway threshold and curves down
        val roadLeftEdge = Path().apply {
            moveTo(hutX + 2f, hutY + hutH)
            cubicTo(w * 1.06f, h * 0.62f, w * 0.90f, h * 0.82f, w * 0.65f, h * 1.05f)
        }
        val roadRightEdge = Path().apply {
            moveTo(hutX + hutW * 0.70f - 2f, hutY + hutH)
            cubicTo(w * 1.20f, h * 0.62f, w * 1.12f, h * 0.82f, w * 1.02f, h * 1.05f)
        }

        val roadPath = remember(w, h) {
            Path().apply {
                moveTo(hutX + 2f, hutY + hutH)
                cubicTo(w * 1.06f, h * 0.62f, w * 0.90f, h * 0.82f, w * 0.65f, h * 1.05f)
                lineTo(w * 1.02f, h * 1.05f)
                cubicTo(w * 1.12f, h * 0.82f, w * 1.20f, h * 0.62f, hutX + hutW * 0.70f - 2f, hutY + hutH)
                close()
            }
        }

        val roadBrush = remember(w, h, currentHour) {
            val rColors = if (isNight || isAmritVela) {
                listOf(Color(0xFF231A12), Color(0xFF2E2218), Color(0xFF1E150F), Color(0xFF160F0A))
            } else if (isEvening) {
                listOf(Color(0xFF6E5039), Color(0xFF5E412A), Color(0xFF4F331F), Color(0xFF3D2516))
            } else {
                // Soft warm earthy loam and village trail tone
                listOf(Color(0xFF9E8166), Color(0xFF8B6B4D), Color(0xFF755537), Color(0xFF5E4025))
            }
            Brush.verticalGradient(
                rColors,
                startY = hutY + hutH,
                endY = h
            )
        }

        val roadNeedleGrassTufts = remember(w, h) {
            val random = Random(334)
            List(16) { i ->
                val t = 0.08f + (i / 15f) * 0.84f
                val omt = 1f - t
                val lx = (omt * omt * omt * (hutX + 2f)) + (3 * omt * omt * t * (w * 1.06f)) + (3 * omt * t * t * (w * 0.90f)) + (t * t * t * (w * 0.65f))
                val rx = (omt * omt * omt * (hutX + hutW * 0.70f - 2f)) + (3 * omt * omt * t * (w * 1.20f)) + (3 * omt * t * t * (w * 1.12f)) + (t * t * t * (w * 1.02f))
                val cy = (hutY + hutH) * (1f - t) + (h * 1.05f) * t
                // Subtle edge sprouting and delicate center blade
                val cx = when (i % 3) {
                    0 -> lx + 3f + random.nextFloat() * ((rx - lx) * 0.15f)
                    1 -> rx - 3f - random.nextFloat() * ((rx - lx) * 0.15f)
                    else -> lx + (rx - lx) * (0.45f + random.nextFloat() * 0.10f)
                }
                val bladeLen = 6f + t * 14f
                val tilt = (random.nextFloat() - 0.5f) * 8f
                // Single/Double thin needle grass path
                val needlePath = Path().apply {
                    moveTo(cx, cy)
                    quadraticTo(cx + tilt * 0.5f, cy - bladeLen * 0.5f, cx + tilt, cy - bladeLen)
                    moveTo(cx + 2f, cy)
                    quadraticTo(cx + 2f - tilt * 0.4f, cy - bladeLen * 0.4f, cx + 2f - tilt * 0.8f, cy - bladeLen * 0.8f)
                }
                needlePath
            }
        }

        val cobblestoneList = remember(w, h) {
            val random = Random(88)
            List(12) { i ->
                val t = 0.10f + (i / 11f) * 0.80f
                val omt = 1f - t
                val lx = (omt * omt * omt * (hutX + 2f)) + (3 * omt * omt * t * (w * 1.06f)) + (3 * omt * t * t * (w * 0.90f)) + (t * t * t * (w * 0.65f))
                val rx = (omt * omt * omt * (hutX + hutW * 0.70f - 2f)) + (3 * omt * omt * t * (w * 1.20f)) + (3 * omt * t * t * (w * 1.12f)) + (t * t * t * (w * 1.02f))
                val cy = (hutY + hutH) * (1f - t) + (h * 1.05f) * t
                val cx = lx + (rx - lx) * (0.25f + random.nextFloat() * 0.5f)
                val sizeW = 2.5f + t * 6f
                val sizeH = 1.5f + t * 3.5f
                Triple(Offset(cx, cy), Size(sizeW, sizeH), random.nextInt(2))
            }
        }

        // ── 360-DEGREE EXPANSIVE WORLD ARCHITECTURE (TILE-NORMALIZED [0, periodW = 3.0w]) ──
        // Trees positioned strictly on lower hill tiers & pasture crests (h * 0.46f to h * 0.54f), below mountains
        val treeData = remember(w, h) {
            listOf(
                // Far Left Meadow (Elevated at h * 0.48f to h * 0.52f)
                Triple(w * 0.08f, h * 0.48f, 0.90f),
                Triple(w * 0.18f, h * 0.47f, 1.10f),
                Triple(w * 0.28f, h * 0.49f, 0.95f),

                // Hill slope above Mustard plot
                Triple(w * 0.65f, h * 0.46f, 0.85f),
                Triple(w * 0.78f, h * 0.47f, 0.90f),

                // Forest Grove between Field & Meadow (Far from plots)
                Triple(w * 0.98f, h * 0.52f, 0.95f),
                Triple(w * 1.05f, h * 0.50f, 1.05f),

                // Behind & Far to the Right of Cottage (Pasture clearing past hut)
                Triple(w * 1.48f, h * 0.53f, 0.92f),
                Triple(w * 1.56f, h * 0.51f, 0.85f),

                // Slope above Mid-Right Wheat Plot
                Triple(w * 1.68f, h * 0.47f, 0.88f),
                Triple(w * 1.82f, h * 0.46f, 0.85f),
                Triple(w * 1.95f, h * 0.48f, 0.88f),

                // Pasture Woodland beyond Windmill (Far Right Hill slopes)
                Triple(w * 2.44f, h * 0.50f, 0.95f),
                Triple(w * 2.58f, h * 0.48f, 1.10f),
                Triple(w * 2.72f, h * 0.49f, 1.05f),
                Triple(w * 2.85f, h * 0.51f, 1.10f)
            )
        }

        val treePaths = remember(w, h, treeData) {
            treeData.map { (tx, ty, tScale) ->
                Path().apply {
                    moveTo(tx - 10f * tScale, ty)
                    lineTo(tx + 10f * tScale, ty)
                    lineTo(tx + 6f * tScale, ty - 100f * tScale)
                    lineTo(tx - 6f * tScale, ty - 100f * tScale)
                    close()
                }
            }
        }

        val flowerPoints = remember(w, h) {
            val random = Random(99)
            val points = mutableListOf<Offset>()
            var attempts = 0
            // Flowers strictly bloom in the front village meadow and surrounding the front pathway
            while (points.size < 32 && attempts < 900) {
                attempts++
                // Front region: centered around cottage yard and front meadow (w * 0.40f to w * 1.75f)
                val fx = w * 0.40f + random.nextFloat() * (w * 1.35f)
                // Front ground foreground layer (h * 0.72f to h * 0.98f)
                val fy = h * 0.72f + random.nextFloat() * (h * 0.26f)
                
                // Exact road envelope bounds check with safety margin (no flowers on road)
                val t = ((fy - (hutY + hutH)) / (h * 1.05f - (hutY + hutH))).coerceIn(0f, 1f)
                val omt = 1f - t
                val roadLx = (omt * omt * omt * (hutX + 2f)) + (3 * omt * omt * t * (w * 1.06f)) + (3 * omt * t * t * (w * 0.90f)) + (t * t * t * (w * 0.65f))
                val roadRx = (omt * omt * omt * (hutX + hutW * 0.70f - 2f)) + (3 * omt * omt * t * (w * 1.20f)) + (3 * omt * t * t * (w * 1.12f)) + (t * t * t * (w * 1.02f))
                val onRoad = fx in (roadLx - 24f)..(roadRx + 24f)

                // Pond / River / Stream water bounds check (No flowers in water)
                val dx = (fx - (pondOffset.x + pondSize.width * 0.5f)) / (pondSize.width * 0.55f)
                val dy = (fy - (pondOffset.y + pondSize.height * 0.5f)) / (pondSize.height * 0.55f)
                val inPond = (dx * dx + dy * dy) <= 1.0f
                val inStream = fy in (h * 0.74f)..(h * 0.83f)

                // Spacing check: ensure plants don't cluster on top of each other
                val tooClose = points.any { pt ->
                    val distSq = (pt.x - fx) * (pt.x - fx) + (pt.y - fy) * (pt.y - fy)
                    distSq < 1800f // ~42px radius separation
                }

                if (!onRoad && !inPond && !inStream && !tooClose) {
                    points.add(Offset(fx, fy))
                }
            }
            points
        }

        // Lush Multi-Tiered Grass & Flowering Shrubs Patches across world (Densely populated around hut and animal grazing meadows)
        val bushPositions = remember(w, h) {
            listOf(
                // Far Left Meadow Grazing Glades
                Offset(w * 0.08f, h * 0.65f),
                Offset(w * 0.12f, h * 0.63f),
                Offset(w * 0.16f, h * 0.66f),
                Offset(w * 0.22f, h * 0.64f),
                Offset(w * 0.28f, h * 0.67f),
                Offset(w * 0.32f, h * 0.64f),
                Offset(w * 0.36f, h * 0.67f),
                
                // Hut Area & Yard Meadow (Dense lush green grazing clumps all around the cottage yard)
                Offset(w * 0.84f, h * 0.63f),
                Offset(w * 0.88f, h * 0.61f),
                Offset(w * 0.92f, h * 0.64f),
                Offset(w * 0.96f, h * 0.62f),
                Offset(w * 1.34f, h * 0.59f),
                Offset(w * 1.38f, h * 0.62f),
                Offset(w * 1.42f, h * 0.60f),
                Offset(w * 1.46f, h * 0.63f),
                Offset(w * 1.50f, h * 0.61f),
                Offset(w * 1.54f, h * 0.64f),

                // Mid-Right Pasture & Well Meadow
                Offset(w * 1.62f, h * 0.66f),
                Offset(w * 1.68f, h * 0.64f),
                Offset(w * 1.74f, h * 0.67f),
                Offset(w * 1.80f, h * 0.64f),
                Offset(w * 1.86f, h * 0.66f),
                Offset(w * 1.92f, h * 0.64f),

                // Far-Right Pasture Woodland & Windmill Glades
                Offset(w * 2.14f, h * 0.67f),
                Offset(w * 2.20f, h * 0.69f),
                Offset(w * 2.40f, h * 0.66f),
                Offset(w * 2.46f, h * 0.68f),
                Offset(w * 2.72f, h * 0.65f),
                Offset(w * 2.78f, h * 0.67f),
                Offset(w * 2.84f, h * 0.65f)
            )
        }

        // Animal Pasture Positions: Clustered in main area & meadows, walking in dedicated paths forward & back with full body turning
        val cowPositions = remember(w, h) {
            listOf(
                // Cluster 1: Left Pasture Grass Patch (w * 0.32f, h * 0.64f)
                Offset(w * 0.30f, h * 0.64f),
                Offset(w * 0.36f, h * 0.65f),

                // Cluster 2: Main Cottage Area Meadow (w * 0.88f to w * 0.94f)
                Offset(w * 0.88f, h * 0.63f),
                Offset(w * 0.94f, h * 0.65f),

                // Cluster 3: Main Cottage Right Clearing Pasture (w * 1.38f to w * 1.48f)
                Offset(w * 1.38f, h * 0.62f),
                Offset(w * 1.44f, h * 0.64f),
                Offset(w * 1.50f, h * 0.62f),

                // Cluster 4: Mid-Right Meadow Grass Patch (w * 1.84f, h * 0.65f)
                Offset(w * 1.80f, h * 0.65f),
                Offset(w * 1.86f, h * 0.66f)
            )
        }
        val riverDrinkingAnimals = remember(w, h) {
            listOf(
                // Cow drinking at River bank (Left zone at w * 0.52f, h * 0.77f)
                Triple(Offset(w * 0.52f, h * 0.77f), "cow", 0.78f),
                // Deer drinking at River bank (Mid zone at w * 1.52f, h * 0.77f)
                Triple(Offset(w * 1.52f, h * 0.77f), "deer", 0.72f),
                // Goat drinking at River bank (Far-right zone at w * 2.38f, h * 0.77f)
                Triple(Offset(w * 2.38f, h * 0.77f), "goat", 0.65f)
            )
        }
        val goatPositions = remember(w, h) {
            listOf(
                // Near Far-Left Grass Cluster
                Offset(w * 0.14f, h * 0.63f),
                Offset(w * 0.18f, h * 0.65f),

                // Main Yard Cottage Meadow Goats
                Offset(w * 0.82f, h * 0.65f),
                Offset(w * 1.34f, h * 0.61f),

                // Near Far-Right Pasture Grass Cluster
                Offset(w * 2.14f, h * 0.68f),
                Offset(w * 2.48f, h * 0.67f)
            )
        }
        val sheepPositions = remember(w, h) {
            listOf(
                // Clustered near Mid-Right Pasture Grass Patch
                Offset(w * 1.64f, h * 0.66f),
                Offset(w * 1.70f, h * 0.67f),

                // Clustered near Far-Right Meadow Grass Patch
                Offset(w * 2.76f, h * 0.65f),
                Offset(w * 2.82f, h * 0.66f)
            )
        }
        val chickenPositions = remember(w, h) {
            listOf(
                Offset(w * 0.94f, h * 0.630f),
                Offset(w * 0.98f, h * 0.625f),
                Offset(w * 1.34f, h * 0.615f),
                Offset(w * 1.37f, h * 0.620f)
            )
        }
        val duckPositions = remember(w, h) {
            listOf(
                Offset(pondOffset.x + pondSize.width * 0.35f, pondOffset.y + pondSize.height * 0.45f),
                Offset(pondOffset.x + pondSize.width * 0.58f, pondOffset.y + pondSize.height * 0.58f)
            )
        }

        // Continuous rotation angle driven by current slide index plus interactive finger drag
        val baseSlideAngle = (currentSlideIndex.toFloat() / slides.size.toFloat()) * 360f
        val animatedRotationDeg = baseSlideAngle + userPanAngle
        val animatedTiltY = (userTiltY * 0.20f).coerceIn(-30f, 30f)

        val panoramaLoopWidth = w * 3.0f
        // Center the rustic cottage hut (hutCenterX = w * 1.10f in 3.0w space) directly in screen center (w * 0.50f) on initial open
        val hutCenterOffset = w * 0.60f
        val rawPanX = ((animatedRotationDeg / 360f) * panoramaLoopWidth) + hutCenterOffset
        val continuousTranslationX = -((rawPanX % panoramaLoopWidth + panoramaLoopWidth) % panoramaLoopWidth)

        // Base background fill to ensure zero void
        val baseBgColor = if (isNight || isAmritVela) Color(0xFF030712) else if (isEvening) Color(0xFF1E1B4B) else Color(0xFF0C4A6E)

        // Continuous panning loop when holding touch on left or right sides of screen
        var screenHoldPanDirection by remember { mutableStateOf(0f) } // -1f for left hold, +1f for right hold, 0f idle
        LaunchedEffect(screenHoldPanDirection) {
            while (screenHoldPanDirection != 0f) {
                val step = screenHoldPanDirection * 2.2f
                userPanAngle += step
                panVelocityX = step
                kotlinx.coroutines.delay(16)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(baseBgColor)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val touchX = down.position.x
                        // Detect left/right hold zone (32% edges) vs center drag zone
                        val initialHoldDir = when {
                            touchX < size.width * 0.32f -> -1f
                            touchX > size.width * 0.68f -> 1f
                            else -> 0f
                        }
                        screenHoldPanDirection = initialHoldDir
                        isActivelyDragging = initialHoldDir == 0f

                        var lastX = touchX
                        var lastY = down.position.y

                        do {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: break
                            if (change.pressed) {
                                val dx = change.position.x - lastX
                                val dy = change.position.y - lastY
                                // Only treat as a drag if movement is meaningful (avoids interfering with hold)
                                if (kotlin.math.abs(dx) > 3f || kotlin.math.abs(dy) > 3f) {
                                    // Cancel hold-pan and switch to direct finger drag
                                    screenHoldPanDirection = 0f
                                    val dAngle = -(dx / size.width) * 180f
                                    userPanAngle += dAngle
                                    panVelocityX = dAngle
                                    userTiltY = (userTiltY - dy).coerceIn(-100f, 100f)
                                    isActivelyDragging = true
                                    // Mirror drag on joystick thumb for visual feedback
                                    val joystickClamp = 18f
                                    joystickThumbOffset = Offset(
                                        x = (-dx * 0.55f).coerceIn(-joystickClamp, joystickClamp),
                                        y = (-dy * 0.28f).coerceIn(-joystickClamp, joystickClamp)
                                    )
                                    lastX = change.position.x
                                    lastY = change.position.y
                                }
                            }
                        } while (event.changes.any { it.pressed })

                        // Finger released — stop all panning and reset joystick
                        screenHoldPanDirection = 0f
                        isActivelyDragging = false
                        panVelocityX = 0f
                        joystickThumbOffset = Offset.Zero
                    }
                }
        ) {
            // ── 1. FIXED SKY LAYER (SUN, MOON, STARS, CLOUDS FIXED CENTERED ON SCREEN) ──
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Sky gradient
                drawRect(
                    brush = skyBrush,
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height)
                )

                if (isNight || isAmritVela) {
                    val starPositions = listOf(
                        Offset(w * 0.12f, h * 0.06f), Offset(w * 0.32f, h * 0.09f),
                        Offset(w * 0.48f, h * 0.04f), Offset(w * 0.68f, h * 0.07f),
                        Offset(w * 0.85f, h * 0.05f), Offset(w * 0.92f, h * 0.11f),
                        Offset(w * 0.25f, h * 0.15f), Offset(w * 0.78f, h * 0.14f),
                        Offset(w * 0.05f, h * 0.18f), Offset(w * 0.95f, h * 0.20f)
                    )
                    starPositions.forEachIndexed { sIdx, spos ->
                        val sAlpha = 0.35f + ((sIdx % 3) * 0.25f)
                        drawCircle(color = Color.White.copy(alpha = sAlpha), radius = 2f, center = spos)
                    }
                }

                // Fluffy Drifting Clouds
                val cloudColor = if (isNight || isAmritVela) Color(0xFF334155).copy(alpha = 0.40f) else Color.White.copy(alpha = 0.55f)
                val c1BaseX = w * 0.15f + cloudSwayX
                val c1BaseY = h * 0.08f
                val cloud1Path = Path().apply {
                    moveTo(c1BaseX, c1BaseY + 12f)
                    cubicTo(c1BaseX - 10f, c1BaseY, c1BaseX + 6f, c1BaseY - 14f, c1BaseX + 22f, c1BaseY - 10f)
                    cubicTo(c1BaseX + 30f, c1BaseY - 26f, c1BaseX + 54f, c1BaseY - 24f, c1BaseX + 64f, c1BaseY - 8f)
                    cubicTo(c1BaseX + 78f, c1BaseY - 12f, c1BaseX + 90f, c1BaseY + 4f, c1BaseX + 82f, c1BaseY + 16f)
                    cubicTo(c1BaseX + 88f, c1BaseY + 24f, c1BaseX + 74f, c1BaseY + 30f, c1BaseX + 60f, c1BaseY + 26f)
                    cubicTo(c1BaseX + 44f, c1BaseY + 28f, c1BaseX + 16f, c1BaseY + 28f, c1BaseX, c1BaseY + 12f)
                    close()
                }
                drawPath(path = cloud1Path, color = cloudColor)

                val c2BaseX = w * 0.62f + cloud2SwayX
                val c2BaseY = h * 0.12f
                val cloud2Path = Path().apply {
                    moveTo(c2BaseX, c2BaseY + 8f)
                    cubicTo(c2BaseX - 8f, c2BaseY, c2BaseX + 4f, c2BaseY - 10f, c2BaseX + 18f, c2BaseY - 6f)
                    cubicTo(c2BaseX + 24f, c2BaseY - 18f, c2BaseX + 44f, c2BaseY - 16f, c2BaseX + 52f, c2BaseY - 4f)
                    cubicTo(c2BaseX + 64f, c2BaseY - 6f, c2BaseX + 72f, c2BaseY + 6f, c2BaseX + 65f, c2BaseY + 14f)
                    cubicTo(c2BaseX + 48f, c2BaseY + 20f, c2BaseX + 12f, c2BaseY + 20f, c2BaseX, c2BaseY + 8f)
                    close()
                }
                drawPath(path = cloud2Path, color = cloudColor.copy(alpha = 0.45f))

                // Sun & Moon (Centered on Screen)
                if (isNight || isAmritVela) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFFE2E8F0).copy(alpha = 0.45f), Color(0xFF94A3B8).copy(alpha = 0.15f), Color.Transparent),
                            center = sunCenter,
                            radius = sunRadius * 1.6f
                        ),
                        radius = sunRadius * 1.6f,
                        center = sunCenter
                    )
                    drawCircle(color = Color(0xFFF8FAFC), radius = sunRadius, center = sunCenter)
                    drawCircle(
                        color = Color(0xFF0F172A),
                        radius = sunRadius * 0.88f,
                        center = Offset(sunCenter.x - sunRadius * 0.35f, sunCenter.y - sunRadius * 0.25f)
                    )
                } else {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFFFEF08A).copy(alpha = 0.65f), Color(0xFFFBBF24).copy(alpha = 0.25f), Color.Transparent),
                            center = sunCenter,
                            radius = sunRadius * 1.7f
                        ),
                        radius = sunRadius * 1.7f,
                        center = sunCenter
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFFFFFFFF), Color(0xFFFEF08A), Color(0xFFF59E0B)),
                            center = Offset(sunCenter.x - sunRadius * 0.2f, sunCenter.y - sunRadius * 0.2f),
                            radius = sunRadius
                        ),
                        radius = sunRadius,
                        center = sunCenter
                    )
                    rayPaths.forEach { path ->
                        drawPath(path = path, color = Color(0xFFFFFBEB).copy(alpha = 0.14f))
                    }
                }

                if (isDay || isAmritVela) {
                    val birdPositions = listOf(
                        Offset(w * 0.42f, h * 0.07f), Offset(w * 0.50f, h * 0.05f),
                        Offset(w * 0.58f, h * 0.08f), Offset(w * 0.65f, h * 0.04f)
                    )
                    birdPositions.forEach { b ->
                        val bColor = if (isNight || isAmritVela) Color(0xFF94A3B8) else Color(0xFF37474F)
                        drawLine(color = bColor.copy(alpha = 0.65f), start = b, end = Offset(b.x - 12f, b.y - 6f), strokeWidth = 2f, cap = StrokeCap.Round)
                        drawLine(color = bColor.copy(alpha = 0.65f), start = b, end = Offset(b.x + 12f, b.y - 6f), strokeWidth = 2f, cap = StrokeCap.Round)
                    }
                }
            }

            // ── 2. ROTATING 3D LANDSCAPE CANVAS (SEAMLESS 360° CYLINDRICAL WRAP) ──
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Pure smooth horizontal translation without 3D perspective distortion
                        translationX = continuousTranslationX
                        translationY = animatedTiltY * 0.4f
                        scaleX = 1.0f
                        scaleY = 1.0f
                    }
            ) {
                // Multi-buffer panoramic wrap across [-2W, -W, 0, +W, +2W]
                // 100% infinite continuous 360 rotation in both directions with clean seamless tile alignment
                val periodW = panoramaLoopWidth
                listOf(-periodW * 2f, -periodW, 0f, periodW, periodW * 2f).forEach { bufferX ->
                    withTransform({
                        translate(left = bufferX, top = 0f)
                    }) {
                        mountainPaths.forEachIndexed { i, path ->
                            drawPath(path = path, brush = mountainBrushes[i])
                        }

                        // ── 3. Hills & Dense Multi-Blade Individual Grass Texturing ─────────────
                        hillPaths.forEachIndexed { layer, path ->
                            drawPath(path = path, brush = hillBrushes[layer])
                            
                            val grassBase = when {
                                isNight || isAmritVela -> Color(0xFF022C22)
                                isEvening -> Color(0xFF365314)
                                else -> when(layer) {
                                    0 -> Color(0xFF1B5E20)
                                    1 -> Color(0xFF2E7D32)
                                    2 -> Color(0xFF388E3C)
                                    else -> Color(0xFF1B5E20)
                                }
                            }

                            // Draw organic individual grass tufts
                            grassTuftTextures[layer].forEach { tuftPath ->
                                drawPath(path = tuftPath, color = grassBase.copy(alpha = 0.45f), style = androidx.compose.ui.graphics.drawscope.Fill)
                            }
                        }

                        // ── 3b. Natural Organic Crop Patches in Designated Open Zones ─────────
                        // 5 distinct agricultural plots with natural curved organic boundaries and gentle slopes
                        // ── 3b. Natural Organic Crop Patches in Designated Non-Overlapping Open Zones ──
                        val cropPatches = listOf(
                            // Patch 1: Far Left Golden Wheat Plot (w * 0.16f to w * 0.46f, h * 0.52f to h * 0.58f)
                            Triple(
                                Path().apply {
                                    moveTo(w * 0.18f, h * 0.53f)
                                    quadraticTo(w * 0.32f, h * 0.515f, w * 0.46f, h * 0.535f)
                                    quadraticTo(w * 0.47f, h * 0.565f, w * 0.44f, h * 0.585f)
                                    quadraticTo(w * 0.30f, h * 0.595f, w * 0.16f, h * 0.575f)
                                    close()
                                },
                                if (isNight || isAmritVela) Color(0xFF1E130B) else Color(0xFF6D4C3D),
                                Triple(0, w * 0.17f, w * 0.45f) // (type: 0-wheat)
                            ),
                            // Patch 2: Upper Left Sarson Mustard Patch (w * 0.55f to w * 0.85f, h * 0.48f to h * 0.54f)
                            Triple(
                                Path().apply {
                                    moveTo(w * 0.57f, h * 0.49f)
                                    quadraticTo(w * 0.71f, h * 0.475f, w * 0.85f, h * 0.495f)
                                    quadraticTo(w * 0.86f, h * 0.525f, w * 0.83f, h * 0.545f)
                                    quadraticTo(w * 0.69f, h * 0.555f, w * 0.55f, h * 0.535f)
                                    close()
                                },
                                if (isNight || isAmritVela) Color(0xFF28190E) else Color(0xFF5A3825),
                                Triple(1, w * 0.56f, w * 0.84f) // (type: 1-mustard)
                            ),
                            // Patch 3: Lower Center-Left Vegetable Plot (w * 0.58f to w * 0.86f, h * 0.64f to h * 0.70f)
                            Triple(
                                Path().apply {
                                    moveTo(w * 0.60f, h * 0.65f)
                                    quadraticTo(w * 0.73f, h * 0.635f, w * 0.86f, h * 0.655f)
                                    quadraticTo(w * 0.87f, h * 0.680f, w * 0.84f, h * 0.700f)
                                    quadraticTo(w * 0.71f, h * 0.710f, w * 0.58f, h * 0.690f)
                                    close()
                                },
                                if (isNight || isAmritVela) Color(0xFF180F07) else Color(0xFF4E2E1A),
                                Triple(2, w * 0.59f, w * 0.85f) // (type: 2-vegetable)
                            ),
                            // Patch 4: Mid-Right Open Slope Golden Wheat (w * 1.58f to w * 1.88f, h * 0.50f to h * 0.56f)
                            Triple(
                                Path().apply {
                                    moveTo(w * 1.60f, h * 0.51f)
                                    quadraticTo(w * 1.74f, h * 0.495f, w * 1.88f, h * 0.515f)
                                    quadraticTo(w * 1.89f, h * 0.540f, w * 1.86f, h * 0.560f)
                                    quadraticTo(w * 1.72f, h * 0.570f, w * 1.56f, h * 0.550f)
                                    close()
                                },
                                if (isNight || isAmritVela) Color(0xFF1E130B) else Color(0xFF6D4C3D),
                                Triple(0, w * 1.57f, w * 1.87f) // (type: 0-wheat)
                            ),
                            // Patch 5: Far Right Meadow Yellow Mustard (w * 2.02f to w * 2.32f, h * 0.53f to h * 0.60f)
                            Triple(
                                Path().apply {
                                    moveTo(w * 2.04f, h * 0.54f)
                                    quadraticTo(w * 2.18f, h * 0.525f, w * 2.32f, h * 0.545f)
                                    quadraticTo(w * 2.33f, h * 0.575f, w * 2.29f, h * 0.600f)
                                    quadraticTo(w * 2.15f, h * 0.610f, w * 2.00f, h * 0.590f)
                                    close()
                                },
                                if (isNight || isAmritVela) Color(0xFF28190E) else Color(0xFF5A3825),
                                Triple(1, w * 2.01f, w * 2.31f) // (type: 1-mustard)
                            ),
                            // Patch 6: Tall Emerald Sugarcane & Corn Field (w * 2.45f to w * 2.75f, h * 0.55f to h * 0.62f)
                            Triple(
                                Path().apply {
                                    moveTo(w * 2.47f, h * 0.56f)
                                    quadraticTo(w * 2.61f, h * 0.545f, w * 2.75f, h * 0.565f)
                                    quadraticTo(w * 2.76f, h * 0.595f, w * 2.72f, h * 0.620f)
                                    quadraticTo(w * 2.58f, h * 0.630f, w * 2.43f, h * 0.610f)
                                    close()
                                },
                                if (isNight || isAmritVela) Color(0xFF132A13) else Color(0xFF3E2723),
                                Triple(3, w * 2.44f, w * 2.74f) // (type: 3-sugarcane)
                            )
                        )

                        cropPatches.forEachIndexed { pIdx, (patchPath, soilColor, config) ->
                            val (cropType, startFieldX, endFieldX) = config
                            val cropBounds = when (pIdx) {
                                0 -> Pair(h * 0.515f, h * 0.595f)
                                1 -> Pair(h * 0.475f, h * 0.555f)
                                2 -> Pair(h * 0.635f, h * 0.710f)
                                3 -> Pair(h * 0.495f, h * 0.570f)
                                4 -> Pair(h * 0.525f, h * 0.610f)
                                else -> Pair(h * 0.545f, h * 0.630f)
                            }
                            val topY = cropBounds.first
                            val botY = cropBounds.second
                            val patchHeight = botY - topY

                            // Base Soil fill
                            drawPath(path = patchPath, color = soilColor.copy(alpha = 0.95f))

                            // Clip furrow lines and crops strictly inside each localized patch boundary
                            withTransform({
                                clipPath(patchPath)
                            }) {
                                // Soil texture furrow lines across the whole patch height
                                val furrowCount = 8
                                repeat(furrowCount) { furrowIdx ->
                                    val fy = topY + (furrowIdx / (furrowCount - 1f)) * patchHeight
                                    drawLine(
                                        color = Color.Black.copy(alpha = 0.24f),
                                        start = Offset(startFieldX - 20f, fy),
                                        end = Offset(endFieldX + 20f, fy + 2.5f),
                                        strokeWidth = 2.2f
                                    )
                                }

                                val wheatRandom = Random(1234 + pIdx)
                                val rows = 8
                                val cols = 34
                                val fieldWidth = endFieldX - startFieldX

                                repeat(rows) { row ->
                                    val rowY = topY + 4f + (row / (rows - 1f)) * (patchHeight + 6f)
                                    val staggerOffset = if (row % 2 == 1) fieldWidth * 0.015f else 0f
                                    repeat(cols) { col ->
                                        // Random natural thinning of crops for organic appearance
                                        if (wheatRandom.nextFloat() < 0.16f) return@repeat

                                        val colX = startFieldX + staggerOffset + (col / (cols - 1f)) * fieldWidth + (wheatRandom.nextFloat() - 0.5f) * 4f
                                        val stalkHeight = (13f + row * 2.0f + wheatRandom.nextFloat() * 4f)
                                        val sway = cropWindSway * (0.6f + wheatRandom.nextFloat() * 0.6f)
                                        val stalkCurve = (wheatRandom.nextFloat() - 0.4f) * 3.5f + sway

        when (cropType) {
            0 -> {
                // Dense Golden Wheat
                val stalkColor = if (isNight || isAmritVela) Color(0xFF854D0E) else if ((col + row) % 2 == 0) Color(0xFFFBBF24) else Color(0xFFF59E0B)
                val earColor = if (isNight || isAmritVela) Color(0xFFA16207) else Color(0xFFFEF08A)

                val stalkPath = Path().apply {
                    moveTo(colX, rowY)
                    quadraticTo(colX + stalkCurve * 0.5f, rowY - stalkHeight * 0.6f, colX + stalkCurve, rowY - stalkHeight)
                }
                drawPath(path = stalkPath, color = stalkColor, style = Stroke(width = 2.4f, cap = StrokeCap.Round))

                val headTipX = colX + stalkCurve
                val headTipY = rowY - stalkHeight
                drawOval(color = earColor, topLeft = Offset(headTipX - 3f, headTipY - 7f), size = Size(6f, 8f))
                drawOval(color = stalkColor, topLeft = Offset(headTipX - 4.5f, headTipY - 3.5f), size = Size(4.5f, 6f))
                drawOval(color = stalkColor, topLeft = Offset(headTipX + 0.5f, headTipY - 3.5f), size = Size(4.5f, 6f))
            }
            1 -> {
                // Vibrant Yellow Sarson/Mustard Flowers
                val stemColor = if (isNight || isAmritVela) Color(0xFF065F46) else Color(0xFF2E7D32)
                val bloomColor = if (isNight || isAmritVela) Color(0xFFEAB308) else Color(0xFFFFEB3B)
                val flowerPath = Path().apply {
                    moveTo(colX, rowY)
                    quadraticTo(colX + stalkCurve * 0.5f, rowY - stalkHeight * 0.5f, colX + stalkCurve, rowY - stalkHeight)
                }
                drawPath(path = flowerPath, color = stemColor, style = Stroke(width = 2.2f, cap = StrokeCap.Round))
                val fx = colX + stalkCurve
                val fy = rowY - stalkHeight
                drawCircle(color = bloomColor, radius = 4.2f, center = Offset(fx, fy))
                drawCircle(color = Color(0xFFF57F17), radius = 2.2f, center = Offset(fx, fy))
                drawCircle(color = bloomColor.copy(alpha = 0.85f), radius = 2.8f, center = Offset(fx - 3.2f, fy + 3f))
                drawCircle(color = bloomColor.copy(alpha = 0.85f), radius = 2.8f, center = Offset(fx + 3.2f, fy + 3f))
            }
            2 -> {
                // Rich Ruby Vegetables & Tomatoes
                val leafColor = if (isNight || isAmritVela) Color(0xFF047857) else Color(0xFF388E3C)
                val fruitColor = if ((col + row) % 3 == 0) Color(0xFFE53935) else Color(0xFFFF7043)
                drawOval(color = leafColor, topLeft = Offset(colX - 7f + stalkCurve * 0.3f, rowY - 7f), size = Size(15f, 9f))
                drawCircle(color = fruitColor, radius = 3.2f, center = Offset(colX + stalkCurve * 0.3f, rowY - 3.5f))
            }
            3 -> {
                // Tall Emerald Sugarcane / Corn
                val caneColor = if (isNight || isAmritVela) Color(0xFF14532D) else Color(0xFF15803D)
                val leafColor = if (isNight || isAmritVela) Color(0xFF166534) else Color(0xFF4ADE80)
                drawLine(color = caneColor, start = Offset(colX, rowY), end = Offset(colX + stalkCurve, rowY - stalkHeight * 1.3f), strokeWidth = 3f, cap = StrokeCap.Round)
                drawLine(color = leafColor, start = Offset(colX + stalkCurve * 0.5f, rowY - stalkHeight * 0.7f), end = Offset(colX + stalkCurve * 0.5f - 6f, rowY - stalkHeight * 0.5f), strokeWidth = 1.6f, cap = StrokeCap.Round)
                drawLine(color = leafColor, start = Offset(colX + stalkCurve * 0.7f, rowY - stalkHeight * 0.9f), end = Offset(colX + stalkCurve * 0.7f + 6f, rowY - stalkHeight * 0.7f), strokeWidth = 1.6f, cap = StrokeCap.Round)
            }
            else -> {
                // FIX: Rich, Detailed Lavender Blooms drawn with dynamic paths & layered ovals
                val lavStemColor = if (isNight || isAmritVela) Color(0xFF14532D) else Color(0xFF2E7D32)
                val mainBloomColor = if (isNight || isAmritVela) Color(0xFF6D28D9) else Color(0xFFA855F7)
                val brightBloomColor = if (isNight || isAmritVela) Color(0xFF7C3AED) else Color(0xFFC084FC)

                // Draw organic curved main stem path
                val stemPath = Path().apply {
                    moveTo(colX, rowY)
                    quadraticTo(colX + stalkCurve * 0.5f, rowY - stalkHeight * 0.5f, colX + stalkCurve, rowY - stalkHeight)
                }
                drawPath(path = stemPath, color = lavStemColor, style = Stroke(width = 2.0f, cap = StrokeCap.Round))

                val lx = colX + stalkCurve
                val ly = rowY - stalkHeight

                // Central high lavender flower spike
                drawOval(color = mainBloomColor, topLeft = Offset(lx - 2.5f, ly - 8f), size = Size(5f, 10f))
                
                // Left & Right flanking blossom layers
                drawOval(color = brightBloomColor, topLeft = Offset(lx - 5f, ly - 4f), size = Size(4f, 6f))
                drawOval(color = brightBloomColor, topLeft = Offset(lx + 1f, ly - 4f), size = Size(4f, 6f))
                
                // Lower blossom tier for extra fullness and density matching the wheat
                drawOval(color = mainBloomColor, topLeft = Offset(lx - 4f, ly + 1f), size = Size(3.5f, 5.5f))
                drawOval(color = mainBloomColor, topLeft = Offset(lx + 0.5f, ly + 1f), size = Size(3.5f, 5.5f))
            }
        }
    }
}
                            }

                            // Smooth organic embankment ridge border for the patch
                            drawPath(
                                path = patchPath,
                                color = soilColor.copy(alpha = 0.50f),
                                style = Stroke(width = 2.5f)
                            )
                        }

                        // ── 4. CONTINUOUS PANORAMIC STREAM, RIVER & POND ECOSYSTEM (SEAMLESS 360° LOOP) ──
                        val streamOverlap = 60f
                        val streamPath = Path().apply {
                            // Perfect continuous periodic cubic spline with zero horizontal boundary tangent
                            moveTo(-streamOverlap, h * 0.785f)
                            lineTo(0f, h * 0.785f)
                            cubicTo(w * 0.50f, h * 0.785f, w * 1.00f, h * 0.815f, w * 1.50f, h * 0.775f)
                            cubicTo(w * 2.00f, h * 0.735f, w * 2.50f, h * 0.785f, periodW, h * 0.785f)
                            lineTo(periodW + streamOverlap, h * 0.785f)
                            lineTo(periodW + streamOverlap, h * 0.845f)
                            lineTo(periodW, h * 0.845f)
                            cubicTo(w * 2.50f, h * 0.845f, w * 2.00f, h * 0.795f, w * 1.50f, h * 0.835f)
                            cubicTo(w * 1.00f, h * 0.875f, w * 0.50f, h * 0.845f, 0f, h * 0.845f)
                            lineTo(-streamOverlap, h * 0.845f)
                            close()
                        }
                        drawPath(path = streamPath, brush = pondBrush)

                        // ── 4a. LOTUS LAKE & POND ──
                        drawOval(brush = pondBrush, topLeft = pondOffset, size = pondSize)
                        drawOval(
                            color = (if (isNight || isAmritVela) Color(0xFF0F766E) else Color(0xFF80CBC4)).copy(alpha = 0.30f),
                            topLeft = Offset(pondOffset.x - 2f, pondOffset.y - 1.5f),
                            size = Size(pondSize.width + 4f, pondSize.height + 3f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
                        )

                        // Lily Pads & Lotus Blossoms
                        pondElements.lilyPads.forEach { relPos ->
                            val padCenter = Offset(pondOffset.x + pondSize.width * relPos.x, pondOffset.y + pondSize.height * relPos.y)
                            drawOval(
                                color = Color(0xFF2E7D32).copy(alpha = 0.90f),
                                topLeft = Offset(padCenter.x - 7f, padCenter.y - 4.5f),
                                size = Size(14f, 9f)
                            )
                            // Lotus Pink Blossom
                            drawCircle(color = Color(0xFFF48FB1), radius = 3.5f, center = Offset(padCenter.x, padCenter.y - 3.5f))
                            drawCircle(color = Color(0xFFFFFFFF), radius = 1.5f, center = Offset(padCenter.x, padCenter.y - 3.5f))
                        }

                        // Floating Water Lilies & Lotuses along the flowing river path
                        listOf(
                            Offset(w * 0.22f, h * 0.77f),
                            Offset(w * 0.72f, h * 0.75f),
                            Offset(w * 1.25f, h * 0.78f),
                            Offset(w * 1.82f, h * 0.76f),
                            Offset(w * 2.45f, h * 0.78f)
                        ).forEachIndexed { lIdx, rPad ->
                            drawOval(
                                color = Color(0xFF2E7D32).copy(alpha = 0.88f),
                                topLeft = Offset(rPad.x - 8f, rPad.y - 4.5f),
                                size = Size(16f, 9f)
                            )
                            if (lIdx % 2 == 0) {
                                drawCircle(color = Color(0xFFF48FB1), radius = 4f, center = Offset(rPad.x, rPad.y - 3.5f))
                                drawCircle(color = Color.White, radius = 1.6f, center = Offset(rPad.x, rPad.y - 3.5f))
                            }
                        }

                        // River & Pond Water Reeds
                        pondElements.reeds.forEach { xPct ->
                            val rx = pondOffset.x + (pondSize.width * xPct)
                            val ry = pondOffset.y + (pondSize.height * 0.15f)
                            val stalkPath = Path().apply {
                                moveTo(rx, ry + 15f)
                                quadraticTo(rx + 4f, ry, rx + 8f, ry - 25f)
                            }
                            drawPath(path = stalkPath, color = Color(0xFF33691E), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                            drawOval(color = Color(0xFF5D4037), topLeft = Offset(rx + 5f, ry - 28f), size = Size(5f, 14f))
                        }

                val hayX = w * 1.35f
                val hayY = h * 0.62f
                val hayPath = Path().apply {
                    moveTo(hayX - 22f, hayY + 18f)
                    quadraticTo(hayX - 24f, hayY - 14f, hayX, hayY - 24f)
                    quadraticTo(hayX + 24f, hayY - 14f, hayX + 22f, hayY + 18f)
                    close()
                }
                drawPath(path = hayPath, brush = Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFFB300), Color(0xFFF57F17)), startY = hayY - 24f, endY = hayY + 18f))
                repeat(3) { i ->
                    drawOval(color = Color(0xFFE65100).copy(alpha = 0.25f), topLeft = Offset(hayX - 16f + i * 3f, hayY - 12f + i * 9f), size = Size(32f - i * 6f, 6f), style = Stroke(width = 1.5f))
                }

                // ── 4b. Village Hut: Exterior Mud Walls, Doorway Interior & Thatched Roof ──
                // Hut Front Wall (Plastered warm mud masonry)
                drawPath(
                    path = hutPaths[0],
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF8D6E63), Color(0xFF6D4C41), Color(0xFF5D4037)),
                        start = Offset(hutX, hutY), end = Offset(hutX + hutW * 0.70f, hutY + hutH)
                    )
                )
                // Hut Side Wall (Perspective timber & earth)
                drawPath(
                    path = hutPaths[1],
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF5D4037), Color(0xFF4E342E), Color(0xFF3E2723)),
                        start = Offset(hutX + hutW * 0.70f, hutY), end = Offset(hutX + hutW, hutY + hutH)
                    )
                )

                // ── HUT INTERIOR VISIBLE THROUGH DOORWAY ──
                val doorInnerPath = Path().apply {
                    moveTo(doorX, doorY + doorH)
                    lineTo(doorX, doorY + doorW * 0.45f)
                    quadraticTo(doorX + doorW * 0.5f, doorY - 10f, doorX + doorW, doorY + doorW * 0.45f)
                    lineTo(doorX + doorW, doorY + doorH)
                    close()
                }
                // Warm glowing interior room (timber floor, prayer lamp / diya glow, back wall)
                drawPath(path = doorInnerPath, brush = Brush.verticalGradient(
                    listOf(Color(0xFF2A1810), Color(0xFF4E2A15), Color(0xFF6D381A)),
                    startY = doorY, endY = doorY + doorH
                ))
                // Interior earthen diya lamp glow
                drawCircle(
                    color = Color(0xFFFFD54F).copy(alpha = 0.85f),
                    radius = 4.5f,
                    center = Offset(doorX + doorW * 0.5f, doorY + doorH * 0.65f)
                )
                drawCircle(
                    color = Color(0xFFFF9800).copy(alpha = 0.35f),
                    radius = 12f,
                    center = Offset(doorX + doorW * 0.5f, doorY + doorH * 0.65f)
                )

                // Hut Door (Gradually swings open inwards into the room)
                val doorSwing = 1f - doorOpenProgress * 0.78f // 1.0 = fully closed, 0.22 = fully ajar open
                val doorLeaf = Path().apply {
                    moveTo(doorX, doorY + doorW * 0.45f)
                    lineTo(doorX + doorW * doorSwing, doorY + doorW * 0.40f * doorSwing)
                    lineTo(doorX + doorW * doorSwing, doorY + doorH - 4f * (1f - doorSwing))
                    lineTo(doorX, doorY + doorH)
                    close()
                }
                drawPath(
                    path = doorLeaf,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF6D4C41), Color(0xFF4E342E), Color(0xFF3E2723)),
                        start = Offset(doorX, doorY), end = Offset(doorX + doorW * doorSwing, doorY + doorH)
                    )
                )
                // Door Brass Handle
                drawCircle(color = Color(0xFFFFD54F), radius = 2.2f, center = Offset(doorX + doorW * doorSwing * 0.8f, doorY + doorH * 0.55f))

                // ── BANI THE BIRD COTTAGE HOUSE PLATE ──
                val signPostX = doorX - 16f
                val signPostY = doorY + doorH * 0.50f
                // Wooden Post
                drawLine(color = Color(0xFF4E342E), start = Offset(signPostX, doorY + doorH), end = Offset(signPostX, signPostY - 18f), strokeWidth = 2.5f, cap = StrokeCap.Round)
                // Hanging Wooden Plaque
                val plaqueLeft = signPostX - 14f
                val plaqueTop = signPostY - 16f
                val plaqueWidth = 30f
                val plaqueHeight = 18f
                drawRoundRect(color = Color(0xFF6D4C41), topLeft = Offset(plaqueLeft, plaqueTop), size = Size(plaqueWidth, plaqueHeight), cornerRadius = CornerRadius(3f, 3f))
                drawRoundRect(color = Color(0xFF3E2723), topLeft = Offset(plaqueLeft, plaqueTop), size = Size(plaqueWidth, plaqueHeight), cornerRadius = CornerRadius(3f, 3f), style = Stroke(width = 1f))
                // Plaque details (Cute Golden Bani House Emblem)
                drawCircle(color = Color(0xFFFFD54F), radius = 3.5f, center = Offset(signPostX + 1f, signPostY - 7f))
                drawCircle(color = Color(0xFF2E170F), radius = 1.8f, center = Offset(signPostX + 1f, signPostY - 7f))
                // Hanging ropes
                drawLine(color = Color(0xFFBCAAA4), start = Offset(signPostX - 8f, signPostY - 16f), end = Offset(signPostX, signPostY - 20f), strokeWidth = 1f)
                drawLine(color = Color(0xFFBCAAA4), start = Offset(signPostX + 10f, signPostY - 16f), end = Offset(signPostX, signPostY - 20f), strokeWidth = 1f)

                // Hut Thatched Straw/Clay Roof
                drawPath(
                    path = hutPaths[3],
                    brush = Brush.linearGradient(
                        if (isNight || isAmritVela) listOf(Color(0xFF3E2723), Color(0xFF2E170F), Color(0xFF1D0E09))
                        else listOf(Color(0xFF8D6E63), Color(0xFF6D4C41), Color(0xFF4E342E)),
                        start = Offset(hutX, hutY - 40f), end = Offset(hutX + hutW, hutY + 15f)
                    )
                )

                // Thatched Roof Straw Texture Ridges
                repeat(9) { i ->
                    val roofT = (i + 1) / 10f
                    val rx1 = (hutX - 18f) * (1f - roofT) + (hutX + hutW * 0.38f) * roofT
                    val ry1 = (hutY + 6f) * (1f - roofT) + (hutY - 42f) * roofT
                    val rx2 = (hutX + hutW * 0.78f) * (1f - roofT) + (hutX + hutW + 18f) * roofT
                    val ry2 = (hutY + 14f) * (1f - roofT) + (hutY - 10f) * roofT
                    drawLine(
                        color = Color(0xFF3E2723).copy(alpha = 0.40f),
                        start = Offset(rx1, ry1),
                        end = Offset(rx2, ry2),
                        strokeWidth = 2f
                    )
                }

                // Hut Timber Window on upper right front
                val winX = hutX + hutW * 0.45f
                val winY = hutY + hutH * 0.20f
                drawRect(color = Color(0xFF3E2723), topLeft = Offset(winX - 1f, winY - 1f), size = Size(18f, 16f))
                drawRect(color = Color(0xFFFFE082).copy(alpha = 0.90f), topLeft = Offset(winX, winY), size = Size(16f, 14f))
                // Window Panes Frame Cross
                drawLine(color = Color(0xFF4E342E), start = Offset(winX + 8f, winY), end = Offset(winX + 8f, winY + 14f), strokeWidth = 1.5f)
                drawLine(color = Color(0xFF4E342E), start = Offset(winX, winY + 7f), end = Offset(winX + 16f, winY + 7f), strokeWidth = 1.5f)

                // Rustic Stone Chimney on Roof with gentle rising smoke puffs
                val chimX = hutX + hutW * 0.38f
                val chimY = hutY - 40f
                drawRect(color = Color(0xFF5D4037), topLeft = Offset(chimX - 6f, chimY - 14f), size = Size(12f, 16f))
                drawRect(color = Color(0xFF4E342E), topLeft = Offset(chimX - 8f, chimY - 16f), size = Size(16f, 4f))
                // Chimney Smoke Particles
                val smokeOffset1 = (cowWalkCycle * 0.8f)
                val smokeOffset2 = (cowWalkCycle * 1.4f)
                drawCircle(color = Color.White.copy(alpha = 0.30f), radius = 5f, center = Offset(chimX + 1f + smokeOffset1 * 0.4f, chimY - 24f - smokeOffset1))
                drawCircle(color = Color.White.copy(alpha = 0.22f), radius = 7.5f, center = Offset(chimX + 3f + smokeOffset2 * 0.5f, chimY - 36f - smokeOffset2))
                drawCircle(color = Color.White.copy(alpha = 0.14f), radius = 10f, center = Offset(chimX + 6f + smokeOffset2 * 0.6f, chimY - 50f - smokeOffset2 * 1.3f))

                // ── 4c. HISTORIC WINDMILL (Right Horizon Sector at w * 2.35f) ──
                val wmX = w * 2.35f
                val wmY = h * 0.52f
                val wmTower = Path().apply {
                    moveTo(wmX - 18f, wmY)
                    lineTo(wmX - 10f, wmY - 60f)
                    lineTo(wmX + 10f, wmY - 60f)
                    lineTo(wmX + 18f, wmY)
                    close()
                }
                drawPath(path = wmTower, color = Color(0xFF5D4037))
                drawPath(path = wmTower, color = Color(0xFF3E2723), style = Stroke(width = 1.5f))
                val wmCap = Path().apply {
                    moveTo(wmX - 13f, wmY - 60f)
                    lineTo(wmX, wmY - 74f)
                    lineTo(wmX + 13f, wmY - 60f)
                    close()
                }
                drawPath(path = wmCap, color = Color(0xFF3E2723))

                // Rotating 4-Blade Wooden Windmill Rotor
                val wmRotorCenter = Offset(wmX, wmY - 60f)
                val rotorRad = windmillRotorDeg * (kotlin.math.PI / 180.0)
                for (b in 0 until 4) {
                    val bAngle = rotorRad + (b * (Math.PI / 2.0))
                    val bxTip = wmRotorCenter.x + kotlin.math.cos(bAngle).toFloat() * 44f
                    val byTip = wmRotorCenter.y + kotlin.math.sin(bAngle).toFloat() * 44f
                    drawLine(color = Color(0xFFD7CCC8), start = wmRotorCenter, end = Offset(bxTip, byTip), strokeWidth = 2.5f, cap = StrokeCap.Round)
                    val sailNormalAngle = bAngle + (Math.PI / 2.0)
                    val sailOffX = kotlin.math.cos(sailNormalAngle).toFloat() * 6f
                    val sailOffY = kotlin.math.sin(sailNormalAngle).toFloat() * 6f
                    val sailMidX = wmRotorCenter.x + kotlin.math.cos(bAngle).toFloat() * 24f
                    val sailMidY = wmRotorCenter.y + kotlin.math.sin(bAngle).toFloat() * 24f
                    val sailPath = Path().apply {
                        moveTo(sailMidX, sailMidY)
                        lineTo(bxTip, byTip)
                        lineTo(bxTip + sailOffX, byTip + sailOffY)
                        lineTo(sailMidX + sailOffX, sailMidY + sailOffY)
                        close()
                    }
                    drawPath(path = sailPath, color = Color(0xFFFAFAFA).copy(alpha = 0.85f))
                    drawPath(path = sailPath, color = Color(0xFF8D6E63), style = Stroke(width = 1f))
                }
                drawCircle(color = Color(0xFF2E170F), radius = 3.5f, center = wmRotorCenter)

                // ── 4d. ANCIENT STONE WATER WELL (Right Foreground Pasture at w * 1.55f) ──
                val wellCenter = Offset(w * 1.55f, h * 0.63f)
                drawOval(
                    color = Color(0xFF78909C),
                    topLeft = Offset(wellCenter.x - 16f, wellCenter.y - 8f),
                    size = Size(32f, 16f)
                )
                drawOval(
                    color = Color(0xFF37474F),
                    topLeft = Offset(wellCenter.x - 12f, wellCenter.y - 6f),
                    size = Size(24f, 12f)
                )
                drawOval(
                    color = Color(0xFF0288D1),
                    topLeft = Offset(wellCenter.x - 10f, wellCenter.y - 4f),
                    size = Size(20f, 8f)
                )
                drawLine(color = Color(0xFF5D4037), start = Offset(wellCenter.x - 12f, wellCenter.y), end = Offset(wellCenter.x - 12f, wellCenter.y - 28f), strokeWidth = 2.5f)
                drawLine(color = Color(0xFF5D4037), start = Offset(wellCenter.x + 12f, wellCenter.y), end = Offset(wellCenter.x + 12f, wellCenter.y - 28f), strokeWidth = 2.5f)
                val wellRoof = Path().apply {
                    moveTo(wellCenter.x - 18f, wellCenter.y - 26f)
                    lineTo(wellCenter.x, wellCenter.y - 36f)
                    lineTo(wellCenter.x + 18f, wellCenter.y - 26f)
                    close()
                }
                drawPath(path = wellRoof, color = Color(0xFF8D6E63))
                drawLine(color = Color(0xFFE0E0E0), start = Offset(wellCenter.x, wellCenter.y - 30f), end = Offset(wellCenter.x, wellCenter.y - 12f), strokeWidth = 1.2f)
                drawRect(color = Color(0xFF6D4C41), topLeft = Offset(wellCenter.x - 3.5f, wellCenter.y - 12f), size = Size(7f, 6f))

                // ── 4e. GOLDEN HAYSTACK & CHARA CLUSTERS (Situated Strictly in Open Pastoral Clearings) ──
                listOf(
                    // Far-Left Open Meadow Clearing (Past the Mustard plot)
                    Triple(w * 0.05f, h * 0.63f, 0.90f),
                    Triple(w * 0.07f, h * 0.65f, 0.75f),

                    // Hut Back-Meadow Clearing (Behind cottage trees)
                    Triple(w * 0.98f, h * 0.61f, 0.85f),
                    Triple(w * 1.02f, h * 0.63f, 0.70f),

                    // Mid-Right Pasture Clearing (Near well, away from wheat)
                    Triple(w * 1.58f, h * 0.62f, 1.00f),
                    Triple(w * 1.64f, h * 0.64f, 0.80f),

                    // Windmill Back Range Clearing
                    Triple(w * 2.00f, h * 0.62f, 0.90f),
                    Triple(w * 2.86f, h * 0.63f, 0.85f),
                    Triple(w * 2.92f, h * 0.65f, 0.75f)
                ).forEach { (hx, hy, hScale) ->
                    val hsW = 28f * hScale
                    val hsH = 34f * hScale
                    val hayColor = if (isNight || isAmritVela) Color(0xFF78350F) else Color(0xFFD97706)
                    val hayHighlight = if (isNight || isAmritVela) Color(0xFF92400E) else Color(0xFFFBBF24)
                    val hsPath = Path().apply {
                        moveTo(hx - hsW * 0.5f, hy)
                        quadraticTo(hx - hsW * 0.45f, hy - hsH * 0.65f, hx, hy - hsH)
                        quadraticTo(hx + hsW * 0.45f, hy - hsH * 0.65f, hx + hsW * 0.5f, hy)
                        close()
                    }
                    drawPath(path = hsPath, color = hayColor)
                    drawPath(path = hsPath, color = hayHighlight, style = Stroke(width = 1.5f))
                }

                // ── 4f. WOODEN PASTURE FENCING (Far-Right / Back Range w * 2.10f to w * 2.55f) ──
                listOf(w * 2.10f, w * 2.22f, w * 2.48f, w * 2.58f).forEach { postX ->
                    val postY = h * 0.62f
                    drawLine(color = Color(0xFF5D4037), start = Offset(postX, postY + 12f), end = Offset(postX, postY - 14f), strokeWidth = 3f, cap = StrokeCap.Round)
                }
                drawLine(color = Color(0xFF6D4C41), start = Offset(w * 2.10f, h * 0.62f - 8f), end = Offset(w * 2.22f, h * 0.62f - 8f), strokeWidth = 2f)
                drawLine(color = Color(0xFF6D4C41), start = Offset(w * 2.10f, h * 0.62f + 2f), end = Offset(w * 2.22f, h * 0.62f + 2f), strokeWidth = 2f)
                drawLine(color = Color(0xFF6D4C41), start = Offset(w * 2.48f, h * 0.62f - 8f), end = Offset(w * 2.58f, h * 0.62f - 8f), strokeWidth = 2f)
                drawLine(color = Color(0xFF6D4C41), start = Offset(w * 2.48f, h * 0.62f + 2f), end = Offset(w * 2.58f, h * 0.62f + 2f), strokeWidth = 2f)

                // ── 5. Natural Forest Soil Road & Living Green Moss / Light Grass Patches ──
                drawPath(brush = roadBrush, path = roadPath)

                // Road Light Grass & Soil Blend: fine grassy sheen and organic moss patches across the pathway
                drawPath(
                    path = roadPath,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFF4E311B).copy(alpha = 0.25f),
                            Color(0xFF33691E).copy(alpha = 0.20f),
                            Color(0xFF558B2F).copy(alpha = 0.18f),
                            Color(0xFF2E7D32).copy(alpha = 0.22f)
                        ),
                        startY = doorY + doorH, endY = h
                    )
                )

                // Delicate needle grass blades sprouting naturally along the trail
                val grassBladeColor = if (isNight || isAmritVela) Color(0xFF047857).copy(alpha = 0.65f) else Color(0xFF2E7D32).copy(alpha = 0.70f)
                roadNeedleGrassTufts.forEach { tuftPath ->
                    drawPath(
                        path = tuftPath,
                        color = grassBladeColor,
                        style = Stroke(width = 1.3f, cap = StrokeCap.Round)
                    )
                }

                cobblestoneList.forEach { (offset, size, variant) ->
                    val stoneColor = when (variant) {
                        0 -> Color(0xFF4E342E).copy(alpha = 0.35f)
                        1 -> Color(0xFF6D4C41).copy(alpha = 0.30f)
                        else -> Color(0xFFBCAAA4).copy(alpha = 0.25f)
                    }
                    drawOval(color = stoneColor, topLeft = offset, size = size)
                }

                // ── 5b. RUSTIC POST-AND-RAIL TIMBER FENCES & ROAD HEDGES (SIMULTANEOUSLY CALCULATED WITH ROAD) ──
                val leftBarricadeCount = 10
                val pLeftY0 = hutY + hutH
                val pLeftY1 = h * 0.62f
                val pLeftY2 = h * 0.82f
                val pLeftY3 = h * 1.05f

                val pRightY0 = hutY + hutH
                val pRightY1 = h * 0.62f
                val pRightY2 = h * 0.82f
                val pRightY3 = h * 1.05f

                // Left road boundary posts & rail paths
                val leftRailTop = Path()
                val leftRailMid = Path()

                repeat(leftBarricadeCount) { i ->
                    val t = 0.05f + (i / (leftBarricadeCount - 1f)) * 0.90f
                    val omt = 1f - t
                    // Exact road left boundary bezier: p0=(hutX+2), p1=(w*1.06), p2=(w*0.90), p3=(w*0.65)
                    val bx = (omt * omt * omt * (hutX + 2f)) + (3 * omt * omt * t * (w * 1.06f)) + (3 * omt * t * t * (w * 0.90f)) + (t * t * t * (w * 0.65f))
                    val by = (omt * omt * omt * pLeftY0) + (3 * omt * omt * t * pLeftY1) + (3 * omt * t * t * pLeftY2) + (t * t * t * pLeftY3)
                    val postHeight = 12f + t * 24f
                    val postWidth = 3.0f + t * 2.5f

                    // Post anchored right on exact road border coordinate
                    drawLine(
                        color = Color(0xFF4E342E),
                        start = Offset(bx, by + 2f),
                        end = Offset(bx, by - postHeight),
                        strokeWidth = postWidth,
                        cap = StrokeCap.Round
                    )
                    drawCircle(color = Color(0xFF2E170F), radius = postWidth * 0.5f, center = Offset(bx, by - postHeight))

                    // Roadside Wildflowers & Small Border Stones along Left Fence
                    val flowerStemColor = if (isNight || isAmritVela) Color(0xFF065F46) else Color(0xFF2E7D32)
                    val flowerPetalColor = if (i % 2 == 0) Color(0xFFFFF59D) else Color(0xFFFFFFFF) // Chamomile / Buttercup
                    val flX = bx - 6f - t * 4f
                    val flY = by - postHeight * 0.35f
                    
                    // Thin flower stem
                    drawLine(color = flowerStemColor, start = Offset(flX, by), end = Offset(flX, flY), strokeWidth = 1.4f, cap = StrokeCap.Round)
                    // Flower blossom
                    drawCircle(color = flowerPetalColor, radius = 2.5f + t * 1.2f, center = Offset(flX, flY))
                    drawCircle(color = Color(0xFFFFA000), radius = 1.2f + t * 0.5f, center = Offset(flX, flY))

                    if (i == 0) {
                        leftRailTop.moveTo(bx, by - postHeight * 0.80f)
                        leftRailMid.moveTo(bx, by - postHeight * 0.45f)
                    } else {
                        leftRailTop.lineTo(bx, by - postHeight * 0.80f)
                        leftRailMid.lineTo(bx, by - postHeight * 0.45f)
                    }
                }
                drawPath(path = leftRailTop, color = Color(0xFF5D4037), style = Stroke(width = 2.4f, cap = StrokeCap.Round))
                drawPath(path = leftRailMid, color = Color(0xFF6D4C41), style = Stroke(width = 1.9f, cap = StrokeCap.Round))

                // Right road boundary posts & rail paths
                val rightBarricadeCount = 10
                val rightRailTop = Path()
                val rightRailMid = Path()

                repeat(rightBarricadeCount) { i ->
                    val t = 0.05f + (i / (rightBarricadeCount - 1f)) * 0.90f
                    val omt = 1f - t
                    // Exact road right boundary bezier: p0=(hutX+hutW*0.70-2), p1=(w*1.20), p2=(w*1.12), p3=(w*1.02)
                    val bx = (omt * omt * omt * (hutX + hutW * 0.70f - 2f)) + (3 * omt * omt * t * (w * 1.20f)) + (3 * omt * t * t * (w * 1.12f)) + (t * t * t * (w * 1.02f))
                    val by = (omt * omt * omt * pRightY0) + (3 * omt * omt * t * pRightY1) + (3 * omt * t * t * pRightY2) + (t * t * t * pRightY3)
                    val postHeight = 12f + t * 24f
                    val postWidth = 3.0f + t * 2.5f

                    drawLine(
                        color = Color(0xFF4E342E),
                        start = Offset(bx, by + 2f),
                        end = Offset(bx, by - postHeight),
                        strokeWidth = postWidth,
                        cap = StrokeCap.Round
                    )
                    drawCircle(color = Color(0xFF2E170F), radius = postWidth * 0.5f, center = Offset(bx, by - postHeight))

                    // Roadside Wildflowers & Small Border Stones along Right Fence
                    val rFlowerStemColor = if (isNight || isAmritVela) Color(0xFF065F46) else Color(0xFF2E7D32)
                    val rFlowerPetalColor = if (i % 2 == 1) Color(0xFFFFCC80) else Color(0xFFF48FB1) // Coral / Pink Blossom
                    val rFlX = bx + 5f + t * 3f
                    val rFlY = by - postHeight * 0.35f
                    
                    // Thin flower stem
                    drawLine(color = rFlowerStemColor, start = Offset(rFlX, by), end = Offset(rFlX, rFlY), strokeWidth = 1.4f, cap = StrokeCap.Round)
                    // Flower blossom
                    drawCircle(color = rFlowerPetalColor, radius = 2.5f + t * 1.2f, center = Offset(rFlX, rFlY))
                    drawCircle(color = Color(0xFFFFB300), radius = 1.2f + t * 0.5f, center = Offset(rFlX, rFlY))

                    if (i == 0) {
                        rightRailTop.moveTo(bx, by - postHeight * 0.80f)
                        rightRailMid.moveTo(bx, by - postHeight * 0.45f)
                    } else {
                        rightRailTop.lineTo(bx, by - postHeight * 0.80f)
                        rightRailMid.lineTo(bx, by - postHeight * 0.45f)
                    }
                }
                drawPath(path = rightRailTop, color = Color(0xFF5D4037), style = Stroke(width = 2.4f, cap = StrokeCap.Round))
                drawPath(path = rightRailMid, color = Color(0xFF6D4C41), style = Stroke(width = 1.9f, cap = StrokeCap.Round))

                // Road Wooden Milestone / Signpost on lower road edge
                val signX = w * 0.47f
                val signY = h * 0.86f
                drawLine(color = Color(0xFF4E342E), start = Offset(signX, signY), end = Offset(signX, signY - 24f), strokeWidth = 3f, cap = StrokeCap.Round)
                drawRoundRect(color = Color(0xFF8D6E63), topLeft = Offset(signX - 10f, signY - 24f), size = Size(20f, 10f), cornerRadius = CornerRadius(2f))
                drawLine(color = Color(0xFF3E2723), start = Offset(signX - 7f, signY - 19f), end = Offset(signX + 7f, signY - 19f), strokeWidth = 1.2f)

                treeData.forEachIndexed { index, (tx, ty, tScale) ->
                    drawPath(path = treePaths[index], brush = Brush.verticalGradient(
                        listOf(Color(0xFF5D4037), Color(0xFF3E2723)), startY = ty - 100f * tScale, endY = ty
                    ))
                    val foliageBase = if (isNight || isAmritVela) Color(0xFF064E3B) else Color(0xFF1B5E20)
                    val foliageMid  = if (isNight || isAmritVela) Color(0xFF047857) else Color(0xFF2E7D32)
                    val foliageTop  = if (isNight || isAmritVela) Color(0xFF059669) else Color(0xFF388E3C)
                    drawCircle(color = foliageBase, radius = 34f * tScale, center = Offset(tx, ty - 60f * tScale))
                    drawCircle(color = foliageMid, radius = 28f * tScale, center = Offset(tx - 12f * tScale, ty - 80f * tScale))
                    drawCircle(color = foliageMid, radius = 28f * tScale, center = Offset(tx + 12f * tScale, ty - 80f * tScale))
                    drawCircle(color = foliageTop, radius = 24f * tScale, center = Offset(tx, ty - 100f * tScale))

                    // Orchard Apple Fruit Accents on trees
                    if (index % 2 == 0) {
                        listOf(
                            Offset(tx - 10f * tScale, ty - 65f * tScale),
                            Offset(tx + 12f * tScale, ty - 72f * tScale),
                            Offset(tx - 4f * tScale, ty - 92f * tScale),
                            Offset(tx + 8f * tScale, ty - 86f * tScale)
                        ).forEach { fruitPos ->
                            drawCircle(color = Color(0xFFE53935), radius = 3.2f * tScale, center = fruitPos)
                            drawCircle(color = Color(0xFFFFEB3B), radius = 1.0f * tScale, center = Offset(fruitPos.x - 1f, fruitPos.y - 1f))
                        }
                    }
                }

                flowerPoints.forEach { pt ->
                    val flowerColors = listOf(Color(0xFFE53935), Color(0xFFFB8C00), Color(0xFFE91E63), Color(0xFFFFD54F), Color(0xFF8E24AA))
                    val colorIndex = (kotlin.math.abs((pt.x * 10 + pt.y).toInt()) % flowerColors.size)
                    val primaryColor = flowerColors[colorIndex]
                    val secondaryColor = flowerColors[(colorIndex + 1) % flowerColors.size]

                    val mainStemHeight = 32f
                    val stemBaseY = pt.y + mainStemHeight
                    val windSway = cropWindSway * 0.8f

                    // 0. Rich Earthy Soil Mound / Flowerbed patch below flower stem base
                    val soilCol = if (isNight || isAmritVela) Color(0xFF2E1C14) else Color(0xFF5D4037)
                    drawOval(
                        color = soilCol.copy(alpha = 0.85f),
                        topLeft = Offset(pt.x - 14f, stemBaseY - 3f),
                        size = Size(28f, 7f)
                    )
                    drawOval(
                        color = soilCol.copy(alpha = 0.50f),
                        topLeft = Offset(pt.x - 18f, stemBaseY - 1f),
                        size = Size(36f, 5f)
                    )

                    // 1. Sturdy Main Plant Stem
                    val stemPath = Path().apply {
                        moveTo(pt.x, stemBaseY)
                        cubicTo(pt.x + 3f + windSway * 0.3f, stemBaseY - mainStemHeight * 0.4f, pt.x + windSway * 0.7f, pt.y + mainStemHeight * 0.3f, pt.x + windSway, pt.y)
                    }
                    drawPath(path = stemPath, color = Color(0xFF2E7D32).copy(alpha = 0.95f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.2f, cap = StrokeCap.Round))

                    // 2. Branching Side Shoots & Foliage Leaves
                    val branch1Y = stemBaseY - mainStemHeight * 0.45f
                    val branch1EndX = pt.x - 12f + windSway * 0.5f
                    val branch1EndY = branch1Y - 10f
                    val branchPath1 = Path().apply {
                        moveTo(pt.x + windSway * 0.4f, branch1Y)
                        quadraticTo(pt.x - 6f + windSway * 0.4f, branch1Y - 4f, branch1EndX, branch1EndY)
                    }
                    drawPath(path = branchPath1, color = Color(0xFF388E3C), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.0f, cap = StrokeCap.Round))

                    val branch2Y = stemBaseY - mainStemHeight * 0.65f
                    val branch2EndX = pt.x + 13f + windSway * 0.6f
                    val branch2EndY = branch2Y - 11f
                    val branchPath2 = Path().apply {
                        moveTo(pt.x + windSway * 0.6f, branch2Y)
                        quadraticTo(pt.x + 7f + windSway * 0.5f, branch2Y - 4f, branch2EndX, branch2EndY)
                    }
                    drawPath(path = branchPath2, color = Color(0xFF388E3C), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.0f, cap = StrokeCap.Round))

                    // Green plant leaves along the stem
                    drawOval(color = Color(0xFF43A047), topLeft = Offset(pt.x - 7f + windSway * 0.3f, branch1Y + 2f), size = Size(10f, 5f))
                    drawOval(color = Color(0xFF43A047), topLeft = Offset(pt.x + 1f + windSway * 0.5f, branch2Y + 2f), size = Size(10f, 5f))

                    // 3. Multiple Dainty, Refined Flowers on Single Plant (Top + Left branch + Right branch)
                    listOf(
                        Triple(Offset(pt.x + windSway, pt.y), primaryColor, 3.2f),
                        Triple(Offset(branch1EndX, branch1EndY), secondaryColor, 2.6f),
                        Triple(Offset(branch2EndX, branch2EndY), primaryColor, 2.8f)
                    ).forEach { (pos, fColor, petalRad) ->
                        repeat(5) { p ->
                            val pAngle = p * (2 * Math.PI / 5)
                            val px = pos.x + kotlin.math.cos(pAngle).toFloat() * (petalRad * 1.35f)
                            val py = pos.y + kotlin.math.sin(pAngle).toFloat() * (petalRad * 1.35f)
                            drawCircle(color = fColor.copy(alpha = 0.95f), radius = petalRad, center = Offset(px, py))
                        }
                        drawCircle(color = Color(0xFFFFEE58), radius = petalRad * 0.75f, center = pos)
                        drawCircle(color = Color(0xFFE65100), radius = petalRad * 0.35f, center = pos)
                    }
                }

                // Lush Multi-Tiered Grass & Flowering Shrubs Patches
                bushPositions.forEachIndexed { bIdx, bp ->
                    val bushSway = cropWindSway * 0.4f
                    val bBaseColor = if (bIdx % 2 == 0) Color(0xFF2E7D32) else Color(0xFF1B5E20)
                    val bTopColor = if (bIdx % 2 == 0) Color(0xFF4CAF50) else Color(0xFF388E3C)
                    drawCircle(color = bBaseColor.copy(alpha = 0.90f), radius = 15f, center = Offset(bp.x + bushSway, bp.y))
                    drawCircle(color = bTopColor.copy(alpha = 0.85f), radius = 11f, center = Offset(bp.x + 8f + bushSway, bp.y - 5f))
                    drawCircle(color = bTopColor.copy(alpha = 0.80f), radius = 9f, center = Offset(bp.x - 7f + bushSway, bp.y - 3f))
                    // Wild berry accents on some shrubs
                    if (bIdx % 3 == 0) {
                        drawCircle(color = Color(0xFFE91E63), radius = 2.2f, center = Offset(bp.x + 4f, bp.y - 4f))
                        drawCircle(color = Color(0xFFE91E63), radius = 2.2f, center = Offset(bp.x - 3f, bp.y - 1f))
                    }
                }

                // ── 5c. VIBRANT RIVER & POND WATER ECOSYSTEM (FISHES, SWANS, DUCKS, RIPPLES) ──
                // River fishes swimming strictly inside the flowing river path (h * 0.81f)
                val riverFishPositions = listOf(
                    Triple(w * 0.35f, h * 0.81f, 0),
                    Triple(w * 0.85f, h * 0.82f, 1),
                    Triple(w * 1.45f, h * 0.81f, 2),
                    Triple(w * 1.95f, h * 0.81f, 0),
                    Triple(w * 2.55f, h * 0.81f, 1)
                )

                riverFishPositions.forEachIndexed { rIdx, (rx, ry, cType) ->
                    val fishSwimX = rx + (cowWalkCycle * (0.8f + rIdx * 0.2f) * 0.6f)
                    val fishSwimY = ry + (if (rIdx % 2 == 0) animalBob * 0.2f else -animalBob * 0.2f)
                    val fCol = when (cType) {
                        0 -> Color(0xFFFF7043)
                        1 -> Color(0xFFFFCA28)
                        else -> Color(0xFFEF5350)
                    }
                    drawOval(color = fCol.copy(alpha = 0.90f), topLeft = Offset(fishSwimX - 7f, fishSwimY - 3.5f), size = Size(14f, 7f))
                    drawOval(color = Color.White.copy(alpha = 0.85f), topLeft = Offset(fishSwimX - 2f, fishSwimY - 2.5f), size = Size(4.5f, 3.5f))
                    // Swimming tail
                    val rTailWag = cropWindSway * 1.4f
                    val rTail = Path().apply {
                        moveTo(fishSwimX - 7f, fishSwimY)
                        lineTo(fishSwimX - 13f, fishSwimY - 4f + rTailWag)
                        lineTo(fishSwimX - 13f, fishSwimY + 4f + rTailWag)
                        close()
                    }
                    drawPath(path = rTail, color = fCol.copy(alpha = 0.85f))
                    // Ripple
                    drawCircle(color = Color.White.copy(alpha = 0.25f), radius = 5f, center = Offset(fishSwimX - 14f, fishSwimY))
                }

                // SWANS & DUCKS: Peaceful swimming water birds on river and pond
                val waterBirds = listOf(
                    // (x, y, isSwan, facingRight, bobSpeed)
                    WaterBird(w * 0.65f, h * 0.80f, true, true, 1.0f),   // Swan on river
                    WaterBird(w * 1.75f, h * 0.81f, true, false, 0.9f),  // Swan on river
                    WaterBird(pondOffset.x + pondSize.width * 0.35f, pondOffset.y + pondSize.height * 0.35f, true, true, 1.1f), // Graceful Swan on pond
                    WaterBird(pondOffset.x + pondSize.width * 0.70f, pondOffset.y + pondSize.height * 0.40f, false, false, 1.3f), // Duck on pond
                    WaterBird(w * 2.38f, h * 0.80f, false, true, 1.2f)   // Duck on river
                )

                waterBirds.forEachIndexed { wbIdx, (bx, by, isSwan, fRight, speed) ->
                    val dir = if (fRight) 1f else -1f
                    val birdBobY = by + (if (wbIdx % 2 == 0) animalBob * 0.4f * speed else -animalBob * 0.4f * speed)
                    val birdDriftX = bx + (cowWalkCycle * 0.3f * dir)

                    if (isSwan) {
                        // ── GRACEFUL WHITE SWAN ──
                        // Water ripple ring beneath swan
                        drawOval(color = Color.White.copy(alpha = 0.30f), topLeft = Offset(birdDriftX - 14f, birdBobY + 3f), size = Size(28f, 6f), style = Stroke(width = 1.2f))
                        
                        // Swan Body
                        val swanBody = Path().apply {
                            moveTo(birdDriftX - 10f * dir, birdBobY)
                            cubicTo(birdDriftX - 12f * dir, birdBobY + 6f, birdDriftX + 6f * dir, birdBobY + 7f, birdDriftX + 11f * dir, birdBobY + 2f)
                            cubicTo(birdDriftX + 8f * dir, birdBobY - 2f, birdDriftX - 4f * dir, birdBobY - 3f, birdDriftX - 10f * dir, birdBobY)
                            close()
                        }
                        drawPath(path = swanBody, color = Color.White)
                        drawPath(path = swanBody, color = Color(0xFFB0BEC5), style = Stroke(width = 1f))

                        // Swan Elegant Curved S-Neck & Head
                        val neckPath = Path().apply {
                            moveTo(birdDriftX + 7f * dir, birdBobY + 1f)
                            cubicTo(birdDriftX + 13f * dir, birdBobY - 5f, birdDriftX + 11f * dir, birdBobY - 14f, birdDriftX + 6f * dir, birdBobY - 15f)
                            cubicTo(birdDriftX + 9f * dir, birdBobY - 16f, birdDriftX + 13f * dir, birdBobY - 13f, birdDriftX + 11f * dir, birdBobY + 1f)
                            close()
                        }
                        drawPath(path = neckPath, color = Color.White)

                        // Head & Orange Beak
                        val headCenter = Offset(birdDriftX + 6.5f * dir, birdBobY - 15f)
                        drawCircle(color = Color.White, radius = 2.8f, center = headCenter)
                        // Orange Beak
                        val beakPath = Path().apply {
                            moveTo(headCenter.x + 2f * dir, headCenter.y - 1f)
                            lineTo(headCenter.x + 6.5f * dir, headCenter.y + 0.5f)
                            lineTo(headCenter.x + 2f * dir, headCenter.y + 2f)
                            close()
                        }
                        drawPath(path = beakPath, color = Color(0xFFFF9800))
                        // Eye dot
                        drawCircle(color = Color(0xFF212121), radius = 0.8f, center = Offset(headCenter.x + 1f * dir, headCenter.y - 0.5f))
                    } else {
                        // ── CUTE MALLARD / DUCK ──
                        drawOval(color = Color.White.copy(alpha = 0.25f), topLeft = Offset(birdDriftX - 10f, birdBobY + 2f), size = Size(20f, 5f), style = Stroke(width = 1.0f))
                        // Duck Body (Warm Mallard Brown)
                        drawOval(color = Color(0xFF8D6E63), topLeft = Offset(birdDriftX - 8f, birdBobY - 2f), size = Size(16f, 8f))
                        // Emerald Green Head
                        val dHeadX = birdDriftX + 7f * dir
                        val dHeadY = birdBobY - 5f
                        drawCircle(color = Color(0xFF00796B), radius = 3.5f, center = Offset(dHeadX, dHeadY))
                        // Yellow Beak
                        val dBeak = Path().apply {
                            moveTo(dHeadX + 2f * dir, dHeadY - 1f)
                            lineTo(dHeadX + 5.5f * dir, dHeadY)
                            lineTo(dHeadX + 2f * dir, dHeadY + 1.5f)
                            close()
                        }
                        drawPath(path = dBeak, color = Color(0xFFFFD54F))
                    }
                }

                // ── VIBRANT SWIMMING KOI & WATER FISHES IN POND ──
                pondElements.fishList.forEachIndexed { fIdx, (relX, relY, colorType, baseAngle, speedScale) ->
                    val fishSwimX = pondOffset.x + (pondSize.width * 0.5f) + ((relX - 0.5f) * pondSize.width * 0.70f) + (cowWalkCycle * speedScale * 0.20f)
                    val fishSwimY = pondOffset.y + (pondSize.height * 0.5f) + ((relY - 0.5f) * pondSize.height * 0.60f) + (if (fIdx % 2 == 0) animalBob * 0.2f else -animalBob * 0.2f)
                    val fishPrimary = when (colorType) {
                        0 -> Color(0xFFFF5722) // Flame Orange Koi
                        1 -> Color(0xFFFFEB3B) // Golden Yellow Koi
                        else -> Color(0xFFE91E63) // Ruby Crimson Koi
                    }
                    val fishSecondary = Color(0xFFFFFFFF)

                    // Fish Body Oval
                    drawOval(
                        color = fishPrimary.copy(alpha = 0.95f),
                        topLeft = Offset(fishSwimX - 6f, fishSwimY - 3f),
                        size = Size(12f, 6f)
                    )
                    // White Spot / Dorsal pattern
                    drawOval(
                        color = fishSecondary.copy(alpha = 0.85f),
                        topLeft = Offset(fishSwimX - 2f, fishSwimY - 2f),
                        size = Size(4f, 3f)
                    )
                    // Animated Wagging Tail Fin
                    val tailWag = cropWindSway * 1.2f
                    val tailPath = Path().apply {
                        moveTo(fishSwimX - 6f, fishSwimY)
                        lineTo(fishSwimX - 11f, fishSwimY - 3.5f + tailWag)
                        lineTo(fishSwimX - 11f, fishSwimY + 3.5f + tailWag)
                        close()
                    }
                    drawPath(path = tailPath, color = fishPrimary.copy(alpha = 0.85f))
                    // Subtle swimming wake ripple
                    drawCircle(
                        color = Color.White.copy(alpha = 0.20f),
                        radius = 4f,
                        center = Offset(fishSwimX - 12f, fishSwimY)
                    )
                }

                // ── 6. ANIMALS WITH WALKING, GRAZING & RIVER WATER DRINKING ──
                // Animals drinking at the river bank with naturally lowered neck and snout
                riverDrinkingAnimals.forEach { (pos, animalType, scale) ->
                    val drinkBob = kotlin.math.sin(cowRoamPhase * 2f).toFloat() * 1.5f
                    val aX = pos.x
                    val aY = pos.y + drinkBob

                    when (animalType) {
                        "deer", "goat" -> {
                            // Body
                            drawOval(color = Color(0xFF8D6E63), topLeft = Offset(aX - 14f * scale, aY - 12f * scale), size = Size(28f * scale, 16f * scale))
                            // Lowered Neck extending down towards river water
                            drawLine(color = Color(0xFF8D6E63), start = Offset(aX + 8f * scale, aY - 6f * scale), end = Offset(aX + 18f * scale, aY + 4f * scale), strokeWidth = 5f * scale, cap = StrokeCap.Round)
                            // Head touching water
                            drawCircle(color = Color(0xFF6D4C41), radius = 5.5f * scale, center = Offset(aX + 20f * scale, aY + 5f * scale))
                            // Ears & Horns
                            drawLine(color = Color(0xFF5D4037), start = Offset(aX + 18f * scale, aY + 2f * scale), end = Offset(aX + 14f * scale, aY - 6f * scale), strokeWidth = 1.8f * scale, cap = StrokeCap.Round)
                            // Water ripple at drinking mouth
                            drawCircle(color = Color.White.copy(alpha = 0.35f), radius = 3.5f * scale, center = Offset(aX + 22f * scale, aY + 7f * scale), style = Stroke(width = 1f))
                            // Legs firmly on riverbank grass
                            listOf(-8f, 0f, 8f).forEach { lx ->
                                drawLine(color = Color(0xFF4E342E), start = Offset(aX + lx * scale, aY - 2f * scale), end = Offset(aX + lx * scale, aY + 12f * scale), strokeWidth = 2.2f * scale, cap = StrokeCap.Round)
                            }
                        }
                        else -> { // Cow drinking at River
                            // Body
                            drawOval(color = Color(0xFFF5F5F5), topLeft = Offset(aX - 18f * scale, aY - 14f * scale), size = Size(36f * scale, 20f * scale))
                            drawOval(color = Color(0xFF212121), topLeft = Offset(aX - 8f * scale, aY - 10f * scale), size = Size(14f * scale, 12f * scale))
                            // Lowered neck to water
                            drawLine(color = Color(0xFFF5F5F5), start = Offset(aX + 10f * scale, aY - 6f * scale), end = Offset(aX + 22f * scale, aY + 5f * scale), strokeWidth = 7f * scale, cap = StrokeCap.Round)
                            // Head touching water
                            drawCircle(color = Color(0xFFF5F5F5), radius = 7.5f * scale, center = Offset(aX + 24f * scale, aY + 7f * scale))
                            drawOval(color = Color(0xFFF48FB1), topLeft = Offset(aX + 24f * scale, aY + 7f * scale), size = Size(6f * scale, 5f * scale))
                            // Water ripple at snout
                            drawCircle(color = Color.White.copy(alpha = 0.35f), radius = 4.5f * scale, center = Offset(aX + 26f * scale, aY + 9f * scale), style = Stroke(width = 1f))
                            // Legs on bank
                            listOf(-10f, -2f, 6f, 12f).forEach { lx ->
                                drawLine(color = Color(0xFF212121), start = Offset(aX + lx * scale, aY), end = Offset(aX + lx * scale, aY + 16f * scale), strokeWidth = 2.8f * scale, cap = StrokeCap.Round)
                            }
                        }
                    }
                }

                // COWS (Grazing & Walking with body orientation always facing travel direction)
                cowPositions.forEachIndexed { idx, c ->
                    val phaseOffset = idx * 1.57f
                    val currentPhase = cowRoamPhase + phaseOffset
                    val moveVelX = kotlin.math.cos(currentPhase).toFloat() // +1 moving right, -1 moving left
                    val facingRight = moveVelX >= 0f
                    val dirSign = if (facingRight) 1f else -1f

                    val roamDistX = kotlin.math.sin(currentPhase).toFloat() * 6f
                    val currentCX = c.x + roamDistX
                    val bodyBobY = (if (idx % 2 == 0) animalBob * 0.4f else -animalBob * 0.4f)
                    val headGrazeBob = (if (idx % 3 == 0) animalBob * 1.0f else 0f)
                    val currentCY = c.y + bodyBobY
                    val legShift = cowLegCycle * 1.5f * dirSign

                    // Body
                    drawOval(
                        color = Color(0xFFF5F5F5),
                        topLeft = Offset(currentCX - 18f, currentCY - 10f),
                        size = Size(36f, 20f)
                    )
                    // Black Spots on body (anchored rigidly to body)
                    drawOval(
                        color = Color(0xFF212121),
                        topLeft = Offset(currentCX - 8f, currentCY - 7f),
                        size = Size(14f, 12f)
                    )
                    drawCircle(color = Color(0xFF212121), radius = 3.5f, center = Offset(currentCX + 6f * dirSign, currentCY + 1f))

                    // Attached Neck & Head (Bobs naturally with body & grazing, always faces movement)
                    val headCenterX = currentCX + (16f * dirSign)
                    val headCenterY = currentCY - 4f + headGrazeBob
                    drawCircle(color = Color(0xFFF5F5F5), radius = 8.5f, center = Offset(headCenterX, headCenterY))
                    drawOval(color = Color(0xFFF48FB1), topLeft = Offset(headCenterX + (dirSign * 2f) - 3.5f, headCenterY - 1.5f), size = Size(7f, 6f))
                    // Horns & Ears
                    drawLine(color = Color(0xFFD7CCC8), start = Offset(headCenterX - 2f * dirSign, headCenterY - 6f), end = Offset(headCenterX - 5f * dirSign, headCenterY - 12f), strokeWidth = 2.2f, cap = StrokeCap.Round)
                    drawLine(color = Color(0xFFD7CCC8), start = Offset(headCenterX + 2f * dirSign, headCenterY - 6f), end = Offset(headCenterX + 5f * dirSign, headCenterY - 12f), strokeWidth = 2.2f, cap = StrokeCap.Round)

                    // 4 Articulated Legs firmly anchored under body bottom (currentCY + 8f)
                    listOf(-12f, -4f, 4f, 12f).forEachIndexed { lIdx, lx ->
                        val lShift = if (lIdx % 2 == 0) legShift else -legShift
                        drawLine(
                            color = Color(0xFF212121),
                            start = Offset(currentCX + lx, currentCY + 8f),
                            end = Offset(currentCX + lx + lShift, currentCY + 20f),
                            strokeWidth = 3.2f,
                            cap = StrokeCap.Round
                        )
                    }
                }

                // GOATS (Playful grazing & prancing, turning smoothly to face forward)
                goatPositions.forEachIndexed { idx, g ->
                    val gPhaseOffset = idx * 2.1f
                    val gPhase = goatRoamPhase + gPhaseOffset
                    val gVelX = kotlin.math.cos(gPhase).toFloat()
                    val facingRight = gVelX >= 0f
                    val dirSign = if (facingRight) 1f else -1f

                    val gRoamDistX = kotlin.math.sin(gPhase).toFloat() * 5f
                    val currentGX = g.x + gRoamDistX
                    val gHeadGraze = (if (idx % 2 == 0) animalBob * 0.8f else -animalBob * 0.8f)
                    val currentGY = g.y + (if (idx % 2 == 1) animalBob * 0.4f else -animalBob * 0.4f)
                    val gLegOffset = cowLegCycle * 1.5f * dirSign

                    // Body
                    drawOval(
                        color = Color(0xFFEFEBE9),
                        topLeft = Offset(currentGX - 13f, currentGY - 7f),
                        size = Size(26f, 14f)
                    )
                    // Head & Snout firmly connected to body edge
                    val gHeadX = currentGX + (13f * dirSign)
                    val gHeadY = currentGY - 3f + gHeadGraze
                    drawCircle(color = Color(0xFFEFEBE9), radius = 5.8f, center = Offset(gHeadX, gHeadY))
                    // Beard
                    drawLine(color = Color(0xFFD7CCC8), start = Offset(gHeadX + 2f * dirSign, gHeadY), end = Offset(gHeadX + 4f * dirSign, gHeadY + 5f), strokeWidth = 1.8f, cap = StrokeCap.Round)
                    // Horns
                    drawLine(color = Color(0xFFBCAAA4), start = Offset(gHeadX - 1.5f * dirSign, gHeadY - 4f), end = Offset(gHeadX - 4f * dirSign, gHeadY - 11f), strokeWidth = 1.8f, cap = StrokeCap.Round)
                    drawLine(color = Color(0xFFBCAAA4), start = Offset(gHeadX + 1.5f * dirSign, gHeadY - 4f), end = Offset(gHeadX + 4f * dirSign, gHeadY - 11f), strokeWidth = 1.8f, cap = StrokeCap.Round)

                    // 4 Legs anchored to body bottom (currentGY + 6f)
                    listOf(-9f, -3f, 3f, 9f).forEachIndexed { lIdx, lx ->
                        val lShift = if (lIdx % 2 == 0) gLegOffset else -gLegOffset
                        drawLine(
                            color = Color(0xFF8D6E63),
                            start = Offset(currentGX + lx, currentGY + 6f),
                            end = Offset(currentGX + lx + lShift, currentGY + 16f),
                            strokeWidth = 2.6f,
                            cap = StrokeCap.Round
                        )
                    }
                }
                    }
                }
            }

            // ── MATHEMATICALLY EXACT ROAD CENTER SPLINE ──
            // Mascot walks out from the doorway and follows the exact midpoint (lx + rx)/2 of the road edges
            // Ultra-short cozy travel distance (walkT = 0.14f) right outside the door threshold before flight
            val walkT = (totalWalkProgress * 0.14f).coerceIn(0f, 1f)
            val omt = 1f - walkT

            // Left road edge bezier: (hutX+2) -> (w*1.06) -> (w*0.90) -> (w*0.65)
            val roadLx = (omt * omt * omt * (hutX + 2f)) + (3 * omt * omt * walkT * (w * 1.06f)) + (3 * omt * walkT * walkT * (w * 0.90f)) + (walkT * walkT * walkT * (w * 0.65f))
            val roadLy = (omt * omt * omt * (hutY + hutH)) + (3 * omt * omt * walkT * (h * 0.62f)) + (3 * omt * walkT * walkT * (h * 0.82f)) + (walkT * walkT * walkT * (h * 1.05f))

            // Right road edge bezier: (hutX+hutW*0.70-2) -> (w*1.20) -> (w*1.12) -> (w*1.02)
            val roadRx = (omt * omt * omt * (hutX + hutW * 0.70f - 2f)) + (3 * omt * omt * walkT * (w * 1.20f)) + (3 * omt * walkT * walkT * (w * 1.12f)) + (walkT * walkT * walkT * (w * 1.02f))
            val roadRy = (omt * omt * omt * (hutY + hutH)) + (3 * omt * omt * walkT * (h * 0.62f)) + (3 * omt * walkT * walkT * (h * 0.82f)) + (walkT * walkT * walkT * (h * 1.05f))

            // Exact center coordinates between the two road edges
            val roadWalkX = (roadLx + roadRx) * 0.5f
            val roadWalkY = (roadLy + roadRy) * 0.5f - 8f // Offset up slightly so mascot feet touch the ground

            // Mascot stays attached to the ground landscape coordinates while walking, then soars up to center sky in flight
            val screenWalkX = roadWalkX + continuousTranslationX
            val fixedMascotCenterX = screenWalkX + (flyPhase * (w * 0.50f - screenWalkX))
            val fixedMascotCenterY = roadWalkY - (flyPhase * (roadWalkY - h * 0.16f))

            // Instant 3D side profile flight banking, body tilt & perspective orientation towards pan direction
            val panHeadingFactor = if (isPanningOrJoystickActive) {
                val joyX = (joystickThumbOffset.x / 18f).coerceIn(-1f, 1f)
                val velX = (panVelocityX * 0.5f).coerceIn(-1f, 1f)
                if (joyX != 0f) joyX else velX
            } else 0f

            val panTiltZ = (panHeadingFactor * 18f).coerceIn(-22f, 22f) // Lean/tilt into pan direction
            val panYawY = (panHeadingFactor * 15f).coerceIn(-20f, 20f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
              Box(
                    modifier = Modifier
                        .size(180.dp)
                        .graphicsLayer {
                            translationX = fixedMascotCenterX - (180.dp.toPx() / 2f)
                            translationY = fixedMascotCenterY - (180.dp.toPx() / 2f)
                            val flipDir = if (panHeadingFactor < -0.05f) -1f else 1f
                            scaleX = mascotScale * flipDir
                            scaleY = mascotScale
                            rotationZ = if (flyPhase > 0.2f) panTiltZ else 0f
                            rotationY = if (flyPhase > 0.2f) panYawY else 0f
                            cameraDistance = 12f * density
                        }
                        .pointerInput(activeHour) {
                            detectTapGestures(
                                onDoubleTap = {
                                    themeOverrideHour = when (activeHour) {
                                        in 3..5   -> 9   // Amrit Vela -> Day
                                        in 6..16  -> 18  // Day -> Evening
                                        in 17..20 -> 23  // Evening -> Night
                                        else      -> 3   // Night -> Amrit Vela
                                    }
                                },
                                onTap = {
                                    currentSlideIndex = (currentSlideIndex + 1) % slides.size
                                    customMascotStateOverride = slides[currentSlideIndex].mascotState
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                                    val auraAlpha = if (progress < 0.55f) 0.08f else if (progress < 0.70f) 0.22f else flyPhase * 0.55f
                    val auraColor = if (flyPhase > 0f) Gold else currentSlide.accentColor
                    Box(
                        modifier = Modifier
                            .size((160f + flyPhase * 70f).dp)
                            .background(Brush.radialGradient(
                                colors = listOf(auraColor.copy(alpha = auraAlpha), Color.Transparent)
                            ))
                    )
                    MascotBani(state = currentMascotState, modifier = Modifier.size(180.dp))
                }
            }

            // FOREGROUND COLUMN: info card + navigation buttons (visible when walking finishes)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                if (slideAlpha > 0f) {
                    Spacer(Modifier.height(8.dp))
                    
     Surface(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .alpha(slideAlpha)
        .padding(vertical = 4.dp),
    shape = RoundedCornerShape(32.dp),
    color = if (isLight) Color.White.copy(alpha = 0.45f) else Color(0xFF1E222B).copy(alpha = 0.88f),
    border = BorderStroke(
        width = 1.dp, 
        color = if (isLight) Color.White.copy(alpha = 0.50f) else Color.White.copy(alpha = 0.20f)
    )
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Content Area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = currentSlide.heading,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                // FIX: Fallback to solid contrasting text color if your primary text carries custom transparency values
                color = primaryText.copy(alpha = 1.0f), 
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = currentSlide.body,
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
                // FIX: Secondary text must be highly visible; never let alpha go below 0.85f on translucent background planes
                color = secondaryText.copy(alpha = 0.88f),
                textAlign = TextAlign.Center,
                maxLines = 4
            )
        }
    }
}
          
                    Spacer(Modifier.height(4.dp))

                    // Bottom Navigation Area with adaptive compact height
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(slideAlpha)
                            .padding(bottom = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLastSlide) {
                            // Final slide: "Begin Your Journey" button on top + dots & counter below
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Centered dots + counter for final slide
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        repeat(slides.size) { i ->
                                            val isActive = currentSlideIndex == i
                                            val dotWidth by animateDpAsState(
                                                targetValue = if (isActive) 24.dp else 7.dp,
                                                animationSpec = spring(stiffness = 400f),
                                                label = "dot_width"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .height(6.dp)
                                                    .width(dotWidth)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isActive) currentSlide.accentColor 
                                                        else if (isLight) Color.Black.copy(alpha = 0.15f) 
                                                        else Color.White.copy(alpha = 0.25f)
                                                    )
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isLight) Color.White.copy(alpha = 0.90f) else Color(0xFF121212).copy(alpha = 0.90f),
                                        border = BorderStroke(1.dp, currentSlide.accentColor.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = "${currentSlideIndex + 1} / ${slides.size}",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                                            color = currentSlide.accentColor.copy(alpha = 1.0f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Previous button
                                    Surface(
                                        onClick = {
                                            currentSlideIndex--
                                            customMascotStateOverride = slides[currentSlideIndex].mascotState
                                        },
                                        shape = CircleShape,
                                        color = if (isLight) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f),
                                        border = BorderStroke(1.dp, if (isLight) Color.Black.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.20f)),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "Previous".translate(displayLanguage),
                                                tint = if (isLight) Primary else Color.White.copy(alpha = 0.85f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    // Full width Begin button
                                    Button(
                                        onClick = onContinue,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 12.dp)
                                            .height(48.dp),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                    ) {
                                        Text(
                                            text = "Begin Your Journey".translate(displayLanguage),
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                
                            }
                        } else {
                            // Slides 1 to N-1: Single compact horizontal row (Prev - Middle Indicators - Next)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // ── LEFT: Previous Button ──
                                Box(
                                    modifier = Modifier.width(48.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (currentSlideIndex > 0) {
                                        Surface(
                                            onClick = {
                                                currentSlideIndex--
                                                customMascotStateOverride = slides[currentSlideIndex].mascotState
                                            },
                                            shape = CircleShape,
                                            color = if (isLight) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f),
                                            border = BorderStroke(1.dp, if (isLight) Color.Black.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.20f)),
                                            modifier = Modifier.size(48.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowBack,
                                                    contentDescription = "Previous".translate(displayLanguage),
                                                    tint = if (isLight) Primary else Color.White.copy(alpha = 0.85f),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // ── MIDDLE: Compact Stacked Dots + Slide Counter Badge ──
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        repeat(slides.size) { i ->
                                            val isActive = currentSlideIndex == i
                                            val dotWidth by animateDpAsState(
                                                targetValue = if (isActive) 22.dp else 6.dp,
                                                animationSpec = spring(stiffness = 400f),
                                                label = "dot_width"
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .height(5.dp)
                                                    .width(dotWidth)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isActive) currentSlide.accentColor 
                                                        else if (isLight) Color.Black.copy(alpha = 0.15f) 
                                                        else Color.White.copy(alpha = 0.25f)
                                                    )
                                            )
                                        }
                                    }
                                                        Spacer(Modifier.height(4.dp))

                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isLight) Color.White.copy(alpha = 0.90f) else Color(0xFF121212).copy(alpha = 0.90f),
                                        border = BorderStroke(1.dp, currentSlide.accentColor.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = "${currentSlideIndex + 1} / ${slides.size}",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                            color = currentSlide.accentColor.copy(alpha = 1.0f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // ── RIGHT: Next Button ──
                                Surface(
                                    onClick = {
                                        currentSlideIndex++
                                        customMascotStateOverride = slides[currentSlideIndex].mascotState
                                    },
                                    shape = CircleShape,
                                    color = Gold,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = "Next".translate(displayLanguage),
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Top-Right Small Skip Button
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .clickable { onContinue() },
                shape = RoundedCornerShape(100.dp),
                color = if (isLight) Color.White.copy(alpha = 0.75f) else Color.Black.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, if (isLight) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Skip".translate(displayLanguage),
                        color = if (isLight) Primary else Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = if (isLight) Primary else Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Top-Left Floating 360° Panorama Joystick Controller
            // ── COMPACT 360-DEGREE VIRTUAL JOYSTICK & CONTINUOUS HOLD PAN (BOTTOM RIGHT) ──
            val joystickRadius = 28.dp
            val joystickRadiusPx = with(androidx.compose.ui.platform.LocalDensity.current) { joystickRadius.toPx() }

            // Continuous smooth panning loop while holding joystick thumb deflected
            LaunchedEffect(joystickThumbOffset) {
                if (joystickThumbOffset != Offset.Zero) {
                    while (true) {
                        val normX = (joystickThumbOffset.x / joystickRadiusPx).coerceIn(-1f, 1f)
                        val normY = (joystickThumbOffset.y / joystickRadiusPx).coerceIn(-1f, 1f)
                        val panSpeed = normX * 4.5f
                        val tiltSpeed = normY * 2.2f
                        userPanAngle += panSpeed
                        panVelocityX = panSpeed
                        userTiltY = (userTiltY - tiltSpeed).coerceIn(-100f, 100f)
                        kotlinx.coroutines.delay(16) // ~60fps silky smooth continuous rotation
                    }
                } else {
                    panVelocityX = 0f
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 90.dp, end = 16.dp)
                    .size(60.dp),
                shape = CircleShape,
                color = if (isLight) Color.White.copy(alpha = 0.70f) else Color.Black.copy(alpha = 0.50f),
                border = BorderStroke(1.2.dp, if (isLight) Gold.copy(alpha = 0.60f) else Color.White.copy(alpha = 0.30f)),
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val centerOffset = Offset(size.width / 2f, size.height / 2f)
                                    val rel = offset - centerOffset
                                    val dist = rel.getDistance()
                                    val maxDist = joystickRadiusPx * 0.85f
                                    joystickThumbOffset = if (dist > maxDist) {
                                        rel * (maxDist / dist)
                                    } else {
                                        rel
                                    }
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    val centerOffset = Offset(size.width / 2f, size.height / 2f)
                                    val rel = change.position - centerOffset
                                    val dist = rel.getDistance()
                                    val maxDist = joystickRadiusPx * 0.85f
                                    joystickThumbOffset = if (dist > maxDist) {
                                        rel * (maxDist / dist)
                                    } else {
                                        rel
                                    }
                                },
                                onDragEnd = {
                                    joystickThumbOffset = Offset.Zero
                                },
                                onDragCancel = {
                                    joystickThumbOffset = Offset.Zero
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Cardinal Direction Notches
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val c = center
                        val r = size.minDimension / 2f - 4f
                        val tickColor = if (isLight) Color(0xFF6D4C41).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.40f)
                        drawLine(color = tickColor, start = Offset(c.x, c.y - r), end = Offset(c.x, c.y - r + 4f), strokeWidth = 1.8f)
                        drawLine(color = tickColor, start = Offset(c.x, c.y + r), end = Offset(c.x, c.y + r - 4f), strokeWidth = 1.8f)
                        drawLine(color = tickColor, start = Offset(c.x - r, c.y), end = Offset(c.x - r + 4f, c.y), strokeWidth = 1.8f)
                        drawLine(color = tickColor, start = Offset(c.x + r, c.y), end = Offset(c.x + r - 4f, c.y), strokeWidth = 1.8f)
                    }

                    // Joystick Draggable Thumb Knob
                    Surface(
                        modifier = Modifier
                            .offset(
                                x = with(androidx.compose.ui.platform.LocalDensity.current) { joystickThumbOffset.x.toDp() },
                                y = with(androidx.compose.ui.platform.LocalDensity.current) { joystickThumbOffset.y.toDp() }
                            )
                            .size(22.dp),
                        shape = CircleShape,
                        color = Gold,
                        border = BorderStroke(1.2.dp, Color.White.copy(alpha = 0.90f)),
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "360 Joystick",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class PondFish(
    val relX: Float,
    val relY: Float,
    val colorIndex: Int,
    val baseAngle: Float,
    val speedScale: Float
)

data class WaterBird(
    val x: Float,
    val y: Float,
    val isSwan: Boolean,
    val facingRight: Boolean,
    val speed: Float
)