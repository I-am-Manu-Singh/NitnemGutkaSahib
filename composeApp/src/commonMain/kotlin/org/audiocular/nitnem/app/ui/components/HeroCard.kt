package org.audiocular.nitnem.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlinx.coroutines.flow.update
import kotlin.math.sin
import kotlin.math.cos
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalClipboardManager
import org.audiocular.nitnem.data.HukamnamaLine
import androidx.compose.ui.text.AnnotatedString
import kotlin.math.PI
import kotlin.math.absoluteValue
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Calendar
import org.audiocular.nitnem.app.ui.theme.*
import androidx.compose.foundation.BorderStroke
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateDigits
import org.audiocular.nitnem.utils.translateAng
import org.audiocular.nitnem.utils.translateRaag
import org.audiocular.nitnem.utils.translateWriter
import androidx.compose.animation.AnimatedVisibility
import org.audiocular.nitnem.data.Hukamnama
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import org.audiocular.nitnem.models.NitnemUiState
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import android.content.Context
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.text.font.FontStyle

private data class HeroPrayerUi(

    val title: String,

    val subtitle: String,

    val status: String,

    val prayer: String,

    val icon: ImageVector,

    val ambientIcon: ImageVector,

    val accent: Color,

    val colors: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HeroCard(
    streak: Int,
    continueTitle: String,
    progress: Float,
    completedToday: Boolean = false,
    completedCount: Int,
    totalPaaths: Int,
    displayLanguage: DisplayLanguage =
        DisplayLanguage.English,
    hukamnama: Hukamnama?,
    uiState: NitnemUiState,
    viewModel: NitnemViewModel
) {

    /*
     * TIME
     */

    var hour by remember {

        mutableStateOf(
            Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY)
        )
    }
        var showDetailsSheet by remember {
            mutableStateOf(false)
        }
    LaunchedEffect(Unit) {

        while (true) {

            hour =
                Calendar.getInstance()
                    .get(Calendar.HOUR_OF_DAY)

            delay(60000)
        }
    }

    /*
     * EXPERIENCE CONFIG
     */
    val baniTitle = when (hour) {

    in 3..6 ->
        "Japji Sahib"

    in 7..11 ->
        "Jaap Sahib"

    in 12..15 ->
        "Anand Sahib"

    in 16..18 ->
        "Rehras Sahib"

    else ->
        "Kirtan Sohila"
}
    val baniTheme = remember(baniTitle) {

    resolveBaniTheme(

        baniTitle

    )

}
val prayerInfo = HeroPrayerUi(

    title =
        baniTheme.heroTitle,

    subtitle =
        baniTheme.heroSubtitle,

    status =
        baniTheme.heroStatus,

    prayer =
        baniTitle,

    icon =
        baniTheme.icon,

    ambientIcon =
        baniTheme.ambientIcon,

    accent =
        baniTheme.accent,

    colors =
        baniTheme.gradient
)
    val hukamnamaAccent: Color = when (AppTheme.themeMode) {

    ThemeMode.Light ->
        prayerInfo.accent.copy(alpha = 0.95f)

    ThemeMode.Dark ->
        prayerInfo.accent.copy(alpha = 0.92f)

    ThemeMode.Amoled ->
        prayerInfo.accent
}
    val hukamnamaWhite =
    Color(0xFFF8F6F2)
    /*
     * ANIMATION
     */

    val infinite =
        rememberInfiniteTransition(
            label = ""
        )

    val swayRotation1 by infinite.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 6500,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway1"
    )

    val swayRotation2 by infinite.animateFloat(
        initialValue = 8f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 8500,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway2"
    )

    val pulse by infinite.animateFloat(

        initialValue = 0.97f,

        targetValue = 1.03f,

        animationSpec = infiniteRepeatable(

            animation = tween(2400),

            repeatMode = RepeatMode.Reverse
        ),

        label = ""
    )

    val floatingOffset1 by infinite.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3200,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )

    val floatingOffset2 by infinite.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4400,
                easing = EaseInOut
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )

    val twinkleTime by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle"
    )

    val cloudProgress1 by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(42000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud1"
    )

    val cloudProgress2 by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(64000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloud2"
    )

    val animatedProgress by animateFloatAsState(

        targetValue = progress,

        animationSpec = tween(900),

        label = ""
    )

    /*
     * COLORS
     */

    val primaryText = Color.White

    val secondaryText =
        Color.White.copy(alpha = 0.80f)

    val glassSurface = when (
        AppTheme.themeMode
    ) {

        ThemeMode.Light ->
            Color.White.copy(alpha = 0.16f)

        ThemeMode.Dark ->
            Color.Black.copy(alpha = 0.18f)

        ThemeMode.Amoled ->
            Color.Black.copy(alpha = 0.24f)
    }

    val glassBorder = when (
        AppTheme.themeMode
    ) {

        ThemeMode.Light ->
            Color.White.copy(alpha = 0.14f)

        ThemeMode.Dark ->
            Color.White.copy(alpha = 0.05f)

        ThemeMode.Amoled ->
            Color.White.copy(alpha = 0.04f)
    }

    /*
     * VALUES
     */

    val currentPrayer =
        prayerInfo.prayer.ifBlank {
            "Japji Sahib"
        }

    val recommendationValue = when {

        completedToday -> {

            val raw = when (displayLanguage) {

                DisplayLanguage.Hindi ->
                    "$completedCount / $totalPaaths पूर्ण"

                DisplayLanguage.Punjabi ->
                    "$completedCount / $totalPaaths ਪੂਰੇ"

                DisplayLanguage.English ->
                    "$completedCount / $totalPaaths Done"
            }
            raw.translateDigits(displayLanguage)
        }

        else -> {

            continueTitle.ifBlank {
                currentPrayer
            }.translate(displayLanguage)
        }
    }

val hukamnamaText = remember(hukamnama, uiState.larivaarMode, displayLanguage) {

    hukamnama
        ?.lines
        ?.joinToString("\n") {

            val text = when (displayLanguage) {
                DisplayLanguage.Hindi -> if (it.transliterationHi.isNotBlank()) it.transliterationHi else it.gurmukhi
                else -> if (uiState.larivaarMode) it.larivaar else it.gurmukhi
            }
            text.trim()
        }

        ?: ""
}

    /*
     * CARD
     */

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp),

        shape = RoundedCornerShape(34.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Box(

            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        prayerInfo.colors
                    )
                )
        ) {

            /*
             * BACKGROUND GLOW
             */

            Box(

                modifier = Modifier
                    .size(300.dp)
                    .offset(
                        x = 130.dp,
                        y = (-90).dp
                    )
                    .scale(pulse)
                    .background(

                        Brush.radialGradient(

                            colors = listOf(

                                Color.White.copy(alpha = 0.14f),

                                prayerInfo.accent.copy(alpha = 0.10f),

                                Color.Transparent
                            )
                        ),

                        shape = CircleShape
                    )
            )
                  Box(

    modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)
        .background(

            Brush.verticalGradient(

                listOf(

                    Color.White.copy(alpha = 0.08f),

                    Color.Transparent
                )
            )
        )
)
            /*
             * STARS & CLOUDS BACKGROUND EFFECTS
             */
            Canvas(modifier = Modifier.matchParentSize()) {
                val width = size.width
                val height = size.height
                if (width > 0f && height > 0f) {
                    // Full height sky gradient (no separate cut!)
                    val skyColors = when {
                        // Amrit Vela: deep twilight
                        hour in 3..5 -> listOf(
                            Color(0xFF030712), // Deep Space Black
                            Color(0xFF0F172A), // Midnight Navy
                            Color(0xFF1E1B4B), // Dark Indigo
                            Color(0xFF31102F)  // Dawn Horizon Wine
                        )
                        // Sunrise: golden-orange-purple
                        hour in 6..8 -> listOf(
                            Color(0xFF4C1D95), // Deep Purple
                            Color(0xFF8B5CF6), // Violet
                            Color(0xFFEC4899), // Pink
                            Color(0xFFF59E0B)  // Sunrise Gold
                        )
                        // Day: bright warm blues
                        hour in 9..16 -> listOf(
                            Color(0xFF0284C7), // Sky Blue
                            Color(0xFF38BDF8), // Warm Blue
                            Color(0xFF7DD3FC), // Light Blue
                            Color(0xFFE0F2FE)  // Soft Blue
                        )
                        // Sunset: dramatic crimson/purple/amber
                        hour in 17..18 -> listOf(
                            Color(0xFF31102F), // Dusk Wine
                            Color(0xFFBE123C), // Crimson
                            Color(0xFFF59E0B), // Sunset Orange
                            Color(0xFFFEF08A)  // Light Amber
                        )
                        // Twilight / Early Night: dark violet-indigo
                        hour in 19..20 -> listOf(
                            Color(0xFF0B0F19), // Dark Blue
                            Color(0xFF1E1B4B), // Indigo
                            Color(0xFF111827), // Grey-black
                            Color(0xFF030712)
                        )
                        // Late Night: space black-blue
                        else -> listOf(
                            Color(0xFF030712), // Cosmic Black
                            Color(0xFF080E1E), // Deep Space
                            Color(0xFF0F172A), // Navy
                            Color(0xFF1E293B)  // Slate Blue
                        )
                    }

                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = skyColors,
                            startY = 0f,
                            endY = height
                        ),
                        size = size
                    )
drawRect(

    brush = Brush.verticalGradient(

        colors = listOf(

            Color.Transparent,

            prayerInfo.accent.copy(alpha = 0.08f)

        ),

        startY = height * 0.55f,

        endY = height
    ),

    size = size
)
                    // Draw Sun or Moon based on time
                  val sunMoonX = width * 0.9f;
                    val sunMoonY = height * 0.14f
                    val isNight = hour >= 19 || hour < 6
                    
                    if (isNight) {
                        // --- REALISTIC GLOWING MOON ---
                       drawCircle(
    color = Color.White.copy(alpha = 0.08f),
    radius = 42f,
    center = Offset(sunMoonX, sunMoonY)
)

drawCircle(
    color = Color.White.copy(alpha = 0.15f),
    radius = 32f,
    center = Offset(sunMoonX, sunMoonY)
)

drawCircle(
    color = Color(0xFFF8FAFC),
    radius = 22f,
    center = Offset(sunMoonX, sunMoonY)
)
                        // Lunar Craters (subtle dark gray organic details for realism)
                   val craterColor =
    Color(0xFFCBD5E1).copy(alpha = 0.35f)

drawCircle(
    craterColor,
    radius = 3f,
    center = Offset(
        sunMoonX - 7f,
        sunMoonY - 5f
    )
)

drawCircle(
    craterColor,
    radius = 4f,
    center = Offset(
        sunMoonX + 5f,
        sunMoonY + 7f
    )
)

drawCircle(
    craterColor,
    radius = 2.5f,
    center = Offset(
        sunMoonX - 3f,
        sunMoonY + 10f
    )
)
                        // Draw Twinkling Stars (scattered realistic stars)
                       val stars = listOf(

    Triple(0.10f, 0.10f, 1.2f),
Triple(0.16f, 0.18f, 1.5f),
Triple(0.22f, 0.08f, 1f),
Triple(0.28f, 0.22f, 1.8f),
Triple(0.34f, 0.14f, 1.1f),
Triple(0.40f, 0.06f, 1.6f),
Triple(0.48f, 0.18f, 1.2f),
Triple(0.54f, 0.10f, 1f),
Triple(0.60f, 0.24f, 1.5f),
Triple(0.66f, 0.08f, 1.2f),
Triple(0.72f, 0.16f, 1f),
Triple(0.78f, 0.06f, 1.5f),
Triple(0.84f, 0.20f, 1.1f),
Triple(0.90f, 0.12f, 1.3f)
)

stars.forEachIndexed { index, star ->

    val twinkle =

        if (index % 7 == 0) {

            val alpha =
                0.3f +
                0.7f * kotlin.math.abs(
                    sin(
                        (twinkleTime + index * 25f)
                            * PI.toFloat() / 180f
                    )
                )

            alpha.coerceIn(0.2f, 1f)

        } else {

            0.65f
        }

    drawCircle(

        color = Color.White.copy(alpha = twinkle),

       radius = star.third * 0.75f,

        center = Offset(

            width * star.first,

            height * star.second
        )
    )
}
                    } else {
                        // --- REALISTIC GLOWING SUN ---
                        val sunColor = when {
                            hour in 6..8 -> Color(0xFFF59E0B) // Sunrise Gold
                            hour in 17..18 -> Color(0xFFE11D48) // Sunset Rose/Red
                            else -> Color.White // Noon White
                        }
                        val coronaColor = when {
                            hour in 6..8 -> Color(0xFFFF8C00).copy(alpha = 0.2f)
                            hour in 17..18 -> Color(0xFFF43F5E).copy(alpha = 0.25f)
                            else -> Color(0xFFFFD700).copy(alpha = 0.15f * pulse)
                        }
                        
                        // Radiating Outer Corona Halo
                        drawCircle(
                            color = coronaColor,
                            radius = 56f,
                            center = Offset(sunMoonX, sunMoonY)
                        )
                        drawCircle(
                            color = sunColor.copy(alpha = 0.3f),
                            radius = 36f,
                            center = Offset(sunMoonX, sunMoonY)
                        )
                        // Core Sun Body
drawCircle(
    color = Color.White.copy(alpha = 0.10f),
    radius = 55f,
    center = Offset(sunMoonX, sunMoonY)
)

drawCircle(
    color = Color(0xFFFFD37A).copy(alpha = 0.18f),
    radius = 38f,
    center = Offset(sunMoonX, sunMoonY)
)

drawCircle(
    color = Color(0xFFFFF4C2),
    radius = 18f,
    center = Offset(sunMoonX, sunMoonY)
)

                       
                    }

                   // CLOUD 1
val cloud1X = -140f + (width + 280f) * cloudProgress1
val cloud1Y = height * 0.10f

val cloud1Color =
    if (isNight)
        Color.White.copy(alpha = 0.04f)
    else
        Color.White.copy(alpha = 0.10f)

val cloud1 = Path().apply {

    moveTo(cloud1X - 35f, cloud1Y + 10f)

    quadraticBezierTo(
        cloud1X - 20f,
        cloud1Y - 15f,
        cloud1X + 5f,
        cloud1Y - 8f
    )

    quadraticBezierTo(
        cloud1X + 30f,
        cloud1Y - 28f,
        cloud1X + 55f,
        cloud1Y - 6f
    )

    quadraticBezierTo(
        cloud1X + 85f,
        cloud1Y - 18f,
        cloud1X + 105f,
        cloud1Y + 6f
    )

    lineTo(cloud1X + 140f, cloud1Y + 18f)

    lineTo(cloud1X - 35f, cloud1Y + 18f)

    close()
}
drawPath(
    path = cloud1,
    color = cloud1Color
)
// CLOUD 2
val cloud2X =
    (width + 140f) -
        (width + 280f) * cloudProgress2

val cloud2Y = height * 0.16f

val cloud2Color =
    if (isNight)
        Color.White.copy(alpha = 0.05f)
    else
        Color.White.copy(alpha = 0.12f)

val cloud2 = Path().apply {

    moveTo(cloud2X - 50f, cloud2Y + 12f)

    quadraticBezierTo(
        cloud2X - 25f,
        cloud2Y - 20f,
        cloud2X + 10f,
        cloud2Y - 10f
    )

    quadraticBezierTo(
        cloud2X + 45f,
        cloud2Y - 38f,
        cloud2X + 80f,
        cloud2Y - 10f
    )

    quadraticBezierTo(
        cloud2X + 120f,
        cloud2Y - 22f,
        cloud2X + 145f,
        cloud2Y + 8f
    )

    lineTo(cloud2X + 190f, cloud2Y + 22f)

    lineTo(cloud2X - 50f, cloud2Y + 22f)

    close()
}

drawPath(
    path = cloud2,
    color = cloud2Color
)
// CLOUD 3 (small distant cloud)

val cloud3X =
    -80f + (width + 160f) * cloudProgress2

val cloud3Y =
    height * 0.30f

val cloud3Color =
    if (isNight)
        Color.White.copy(alpha = 0.025f)
    else
        Color.White.copy(alpha = 0.08f)

val cloud3 = Path().apply {

    moveTo(cloud3X - 20f, cloud3Y + 6f)

    quadraticBezierTo(
        cloud3X - 10f,
        cloud3Y - 10f,
        cloud3X + 5f,
        cloud3Y - 4f
    )

    quadraticBezierTo(
        cloud3X + 18f,
        cloud3Y - 18f,
        cloud3X + 35f,
        cloud3Y - 4f
    )

    quadraticBezierTo(
        cloud3X + 55f,
        cloud3Y - 10f,
        cloud3X + 70f,
        cloud3Y + 4f
    )

    lineTo(cloud3X + 80f, cloud3Y + 10f)

    lineTo(cloud3X - 20f, cloud3Y + 10f)

    close()
}

drawPath(
    path = cloud3,
    color = cloud3Color
)

                }
            }
          
            /*
             * AMBIENT ICONS (No-clash, gentle sway)
             */

            Icon(

                imageVector =
                    Icons.Default.AutoAwesome,

                contentDescription = null,

                tint =
                    Color.White.copy(alpha = 0.05f),

                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        bottom = 40.dp,
                        start = 16.dp
                    )
                    .size(56.dp)
                    .rotate(swayRotation2)
                    .offset(
                        y = floatingOffset2.dp
                    )
            )

            /*
             * MAIN CONTENT
             */

                Box(
            modifier = Modifier.fillMaxSize()
                    ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                      .padding(
    horizontal = 20.dp,
    vertical = 22.dp
)
                    
                ) {

                /*
                 * HEADER
                 */

                Row(

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box(

                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                Color.White.copy(alpha = 0.16f)
                            )
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.08f),
                                CircleShape
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(

                            imageVector =
                                prayerInfo.icon,

                            contentDescription = null,

                            tint = Color.White,

                            modifier =
                                Modifier.size(28.dp)
                        )
                    }

                    Spacer(
                        Modifier.width(14.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Row(

    verticalAlignment =
        Alignment.CenterVertically
) {

    Text(

        text =
            prayerInfo.title
                .translate(
                    displayLanguage
                ),

        color =
            primaryText,

        fontWeight =
            FontWeight.Bold,

        fontSize = 18.sp,

        maxLines = 1,

        overflow =
            TextOverflow.Ellipsis
    )

    Spacer(
        Modifier.width(6.dp)
    )

    Text(

        text = "•",

        color =
            secondaryText.copy(
                alpha = 0.7f
            ),

        fontSize = 12.sp,

        fontWeight =
            FontWeight.Medium
    )

    Spacer(
        Modifier.width(6.dp)
    )

    Text(

        text =
            prayerInfo.subtitle
                .translate(
                    displayLanguage
                ),

        color =
            secondaryText,

        fontSize = 12.sp,

        fontWeight =
            FontWeight.Medium,

        maxLines = 1,

        overflow =
            TextOverflow.Ellipsis
    )
}

                        Spacer(
                            Modifier.height(3.dp)
                        )
                        
             Text(

    text =
        prayerInfo.status
            .translate(displayLanguage),

    color =
        secondaryText,

    fontSize = 13.sp
)
                        
                    }


                }

                Spacer(
                    Modifier.height(10.dp)
                )

                
/*
 * INFO ROW
 */

Row(

    modifier =
        Modifier.fillMaxWidth(),

    horizontalArrangement =
        Arrangement.spacedBy(12.dp)
) {
    PremiumInfoCard(

    modifier =
        Modifier.weight(1f),

    title =
        baniTheme.heroTime
            .translate(displayLanguage)
            .translateDigits(displayLanguage),

    value =
        currentPrayer
            .translate(displayLanguage),

    icon =
        prayerInfo.icon,

    accent =
        prayerInfo.accent,

    primaryText =
        primaryText,

    secondaryText =
        secondaryText,

    glassSurface =
        glassSurface,

    glassBorder =
        glassBorder,

    displayLanguage =
        displayLanguage
)
    PremiumInfoCard(

        modifier =
            Modifier.weight(1f),

        title =
            "Your Paath"
                .translate(
                    displayLanguage
                ),

        value =
            if (completedToday) {

                when (displayLanguage) {

                    DisplayLanguage.Hindi ->
                        "आज पूर्ण"

                    DisplayLanguage.Punjabi ->
                        "ਅੱਜ ਪੂਰਾ"

                    DisplayLanguage.English ->
                        "Completed Today"
                }

            } else {

                continueTitle.ifBlank {
                    "Select Paath"
                }.translate(displayLanguage)
            },

        icon =
            if (completedToday)
                Icons.Default.CheckCircle
            else
                Icons.Default.MenuBook,

        accent =
            prayerInfo.accent,

        primaryText =
            primaryText,

        secondaryText =
            secondaryText,

        glassSurface =
            glassSurface,

        glassBorder =
            glassBorder,

        displayLanguage =
            displayLanguage
    )
}



                Spacer(
                    Modifier.height(12.dp)
                )
/*
 * HUKAMNAMA
 */

if (hukamnama != null) {

    Surface(

        shape =
            RoundedCornerShape(24.dp),

        color =
            glassSurface,

        border = BorderStroke(
            1.dp,
            glassBorder
        )
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
               .padding(
    horizontal = 16.dp,
    vertical = 5.dp
)
        ) {

          /*
 * HEADER
 */
Row(

    modifier =
        Modifier.fillMaxWidth(),

    verticalAlignment =
        Alignment.CenterVertically
) {

    /*
     * LEFT CONTENT
     */

    Row(

        modifier =
            Modifier.weight(1f),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        /*
         * LIVE / OFFLINE CHIP
         */

        Surface(

            shape =
                RoundedCornerShape(18.dp),

            color =
                Color.White.copy(
                    alpha = 0.10f
                ),

            border = BorderStroke(

                1.dp,

                Color.White.copy(
                    alpha = 0.16f
                )
            )
        ) {

            Text(

                text =

                    if (hukamnama.isFromApi) {

                        "LIVE".translate(displayLanguage)

                    } else {

                        "OFFLINE".translate(displayLanguage)
                    },

                color = Color.White.copy(alpha = 0.50f),

                fontSize = 9.sp,

                lineHeight = 10.sp,

                fontWeight =
                    FontWeight.ExtraBold,

                letterSpacing = 0.8.sp,

                modifier =
                    Modifier.padding(

                        horizontal = 7.dp,

                        vertical = 3.dp
                    )
            )
        }

        Spacer(
            Modifier.width(10.dp)
        )

        /*
         * TITLE
         */

        Text(

            text =
                "Daily Hukamnama"
                    .translate(
                        displayLanguage
                    ),

          color = Color.White.copy(alpha = 0.50f),

            fontWeight =
                FontWeight.Bold,

            fontSize = 13.sp,

            maxLines = 1,

            overflow =
                TextOverflow.Ellipsis
        )
    }

    /*
     * INFO BUTTON
     */

    Surface(

        modifier =
            Modifier.size(24.dp),

        onClick = {

            showDetailsSheet = true
        },

        shape = CircleShape,

        color =
            Color.White.copy(
                alpha = 0.05f
            ),

        border = BorderStroke(

            1.dp,

            Color.White.copy(
                alpha = 0.16f
            )
        )
    ) {

        Box(

            contentAlignment =
                Alignment.Center
        ) {

            Icon(

                imageVector =
                    Icons.Default.Info,

                contentDescription = null,

                tint =
                    Color.White.copy(
                        alpha = 0.50f
                    ),

                modifier =
                    Modifier.size(15.dp)
            )
        }
    }
}

           

            /*
             * SCROLLABLE HUKAMNAMA TEXT
             */
Box(

    modifier = Modifier
        .fillMaxWidth()
        .heightIn(
            min = 60.dp,
           max = 110.dp
        )
        .verticalScroll(
            rememberScrollState()
        )
) {

   Text(

    text = hukamnamaText,

    modifier = Modifier
        .fillMaxWidth(),

   color = hukamnamaWhite,
    fontSize = 15.sp,

    fontWeight =
        FontWeight.ExtraBold,

    lineHeight = 26.sp,

    textAlign =
        TextAlign.Center,

    style = TextStyle(

        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),

          shadow = Shadow(

        color =

            Accent.copy(alpha = 0.35f),

        blurRadius = 16f

    ),
        letterSpacing = 0.2.sp
    )
)
}
        }
    }
} else {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = glassSurface,
        border = BorderStroke(1.dp, glassBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = Gold,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Daily Hukamnama".translate(displayLanguage),
                        color = Color.White.copy(alpha = 0.50f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = "Fetching Hukamnama...".translate(displayLanguage),
                color = Color.White.copy(alpha = 0.70f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )
        }
    }
}
                    
        }
                    /*
        * BOTTOM PROGRESS
        */

        Box(

    modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .height(4.dp)
        .clip(
            RoundedCornerShape(
                bottomStart = 34.dp,
                bottomEnd = 34.dp
            )
        )
        .background(
                    Color.White.copy(alpha = 0.22f)
                )
        ) {

            Box(

        modifier = Modifier
            .fillMaxWidth(
                animatedProgress.coerceIn(
                    0f,
                    1f
                )
            )
            .fillMaxHeight()
            .background(

                Brush.horizontalGradient(

                    colors = listOf(
                        Gold,
                        prayerInfo.accent
                    )
                )
            )
            )
        }
    }
}

if (hukamnama != null && showDetailsSheet) {

    val current = hukamnama

    val sheetBackground = when (AppTheme.themeMode) {

        ThemeMode.Light ->
            Color(0xFFF8F6F2)

        ThemeMode.Dark ->
            Color(0xFF14161A)

        ThemeMode.Amoled ->
            Color.Black
    }

    val cardBackground = when (AppTheme.themeMode) {

        ThemeMode.Light ->
            Color.White.copy(alpha = 0.92f)

        ThemeMode.Dark ->
            Color.White.copy(alpha = 0.05f)

        ThemeMode.Amoled ->
            Color.White.copy(alpha = 0.03f)
    }

    val primarySheetText = when (AppTheme.themeMode) {

        ThemeMode.Light ->
            Color(0xFF1B1B1B)

        else ->
            Color.White
    }

    val secondarySheetText =
        primarySheetText.copy(alpha = 0.72f)

    val hukamnamaAccent: Color = Gold

    val colors = MaterialTheme.colorScheme

    val gurmukhiColor = resolveTextColor(

        mode = uiState.gurmukhiColorMode,

        colors = colors,

        default = hukamnamaAccent
    )

    val romanColor = resolveTextColor(

        mode = uiState.romanTransliterationColorMode,

        colors = colors,

        default = secondarySheetText.copy(alpha = 0.82f)
    )

    val translationColor = resolveTextColor(

        mode = uiState.translationColorMode,

        colors = colors,

        default = secondarySheetText.copy(alpha = 0.90f)
    )

    val punjabiColor = resolveTextColor(

        mode = uiState.translationColorMode,

        colors = colors,

        default = secondarySheetText
    )

    ModalBottomSheet(

        onDismissRequest = {
            showDetailsSheet = false
        },

        containerColor = sheetBackground,

        shape = RoundedCornerShape(
            topStart = 34.dp,
            topEnd = 34.dp
        ),

        dragHandle = {

            Box(

                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(
                        width = 54.dp,
                        height = 5.dp
                    )
                    .clip(CircleShape)
                    .background(
                        Gold.copy(alpha = 0.35f)
                    )
            )
        }
    ) {
val clipboardManager =
    LocalClipboardManager.current

var selectedMeaningLine by remember {
    mutableStateOf<HukamnamaLine?>(null)
}
        Column(

            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                )
        ) {

            /*
             * HEADER
             */

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(

                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Gold.copy(alpha = 0.14f)
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.MenuBook,

                        contentDescription = null,

                        tint = Gold,

                        modifier =
                            Modifier.size(26.dp)
                    )
                }

                Spacer(
                    Modifier.width(10.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(

                        text =
                            "Daily Hukamnama"
                                .translate(displayLanguage),

                        color =
                            primarySheetText,

                        fontSize = 22.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(2.dp)
                    )

                    Text(

                        text = remember(
                            current.date,
                            displayLanguage
                        ) {

                            current.date
                                .split(", ")
                                .map { part ->

                                    part.split(" ")
                                        .map { sub ->
                                            sub.translate(
                                                displayLanguage
                                            )
                                        }
                                        .joinToString(" ")
                                }
                                .joinToString(", ")
                                .translateDigits(
                                    displayLanguage
                                )
                        },

                        color =
                            secondarySheetText,

                        fontSize = 12.sp
                    )
                }
            }

            Spacer(
                Modifier.height(14.dp)
            )

            /*
             * META CHIPS
             */

            FlowRow(

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                MetaChip(

                    label =

                        if (current.isFromApi) {

                            "${"Ang".translate(displayLanguage)} ${current.ang}"
                                .translateDigits(displayLanguage)

                        } else {

                            current.ang
                                .translateAng(displayLanguage)
                        }
                )

                MetaChip(

                    label =
                        translateRaag(
                            current.raag,
                            displayLanguage
                        )
                )

                MetaChip(

                    label =
                        translateWriter(
                            current.writer,
                            displayLanguage
                        )
                )
            }

            Spacer(
                Modifier.height(16.dp)
            )

            /*
             * TOGGLES
             */

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

               

                PremiumToggleChip(

    icon = Icons.Default.Translate,

    label =
        "English"
            .translate(displayLanguage),

    selected =
        uiState.showEnglishTranslation,

    onClick = {

        viewModel.setShowEnglishTranslation(
            !uiState.showEnglishTranslation
        )
    }
)

PremiumToggleChip(

    icon = Icons.Default.MenuBook,

    label =
        "Punjabi"
            .translate(displayLanguage),

    selected =
        uiState.showPunjabiTranslation,

    onClick = {

        viewModel.setShowPunjabiTranslation(
            !uiState.showPunjabiTranslation
        )
    }
)

PremiumToggleChip(

    icon = Icons.Default.Language,

    label =
        "Roman"
            .translate(displayLanguage),

    selected =
        uiState.showRomanTransliteration,

    onClick = {

        viewModel.setShowRomanTransliteration(
            !uiState.showRomanTransliteration
        )
    }
)

PremiumToggleChip(

    icon = Icons.Default.AutoStories,

    label =
        "Hindi"
            .translate(displayLanguage),

    selected =
        uiState.showDevanagariTransliteration,

    onClick = {

        viewModel.setShowDevanagariTransliteration(
            !uiState.showDevanagariTransliteration
        )
    }
)

PremiumToggleChip(

    icon = Icons.Default.FormatAlignCenter,

    label =
        "Center"
            .translate(displayLanguage),

    selected =
        uiState.centerGurmukhi,

    onClick = {

        viewModel.setCenterGurmukhi(
            !uiState.centerGurmukhi
        )
    }
)

PremiumToggleChip(

    icon = Icons.Default.TextFields,

    label =
        "Larivaar"
            .translate(displayLanguage),

    selected =
        uiState.larivaarMode,

    onClick = {

        viewModel.setLarivaarMode(
            !uiState.larivaarMode
        )
    }
)

PremiumToggleChip(
    icon = Icons.Default.Language,
    label = "Meanings".translate(displayLanguage),
    selected = uiState.showWordMeaning,
    onClick = {
        viewModel.setShowWordMeaning(!uiState.showWordMeaning)
    }
)
            }

            Spacer(
                Modifier.height(18.dp)
            )

            /*
             * FULL HUKAMNAMA
             */

            current.lines.forEachIndexed { index, line ->
 val gurmukhiText =

                if (
                    uiState.larivaarMode &&
                    line.larivaar.isNotBlank()
                ) {

                    line.larivaar

                } else {

                    line.gurmukhi
                }

                Surface(

                    shape = RoundedCornerShape(
                        if (uiState.compactMode)
                            18.dp
                        else
                            24.dp
                    ),

                    color = cardBackground,

                    border = BorderStroke(

                        1.dp,

                        hukamnamaAccent.copy(
                            alpha = 0.08f
                        )
                    )
                ) {

                    Column(

    modifier = Modifier
        .fillMaxWidth()
        .padding(

            horizontal =

                if (uiState.compactMode)
                    12.dp
                else
                    16.dp,

            vertical =

                if (uiState.compactMode)
                    12.dp
                else
                    16.dp
        )
) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.Top
    ) {

        /*
         * MAIN CONTENT
         */

        Column(

            modifier = Modifier.weight(1f),

            verticalArrangement =
                Arrangement.spacedBy(

                    if (uiState.compactMode)
                        8.dp
                    else
                        12.dp
                )
        ) {

           
            /*
             * GURMUKHI
             */

            if (gurmukhiText.isNotBlank()) {

                Text(

                    text = gurmukhiText,

                    modifier =
                        Modifier.fillMaxWidth(),

                    color = gurmukhiColor,

                    textAlign =

                        if (uiState.centerGurmukhi)
                            TextAlign.Center
                        else
                            TextAlign.Start,

                    fontSize =
                        uiState.gurmukhiFontSize.sp,

                    fontWeight =

                        if (uiState.boldGurmukhi)
                            FontWeight.Bold
                        else
                            FontWeight.SemiBold,

                    lineHeight =
                        uiState.lineSpacing.sp,

                    style = TextStyle(

                        shadow = Shadow(

                            color =
                                gurmukhiColor.copy(
                                    alpha = 0.16f
                                ),

                            blurRadius = 8f
                        )
                    )
                )
            }

            /*
             * HINDI
             */

            if (
                uiState.showDevanagariTransliteration &&
                line.transliterationHi.isNotBlank()
            ) {

                Text(

                    text =
                        line.transliterationHi,

                    modifier =
                        Modifier.fillMaxWidth(),

                    color =
                        romanColor.copy(alpha = 0.90f),

                    textAlign =

                        if (uiState.centerGurmukhi)
                            TextAlign.Center
                        else
                            TextAlign.Start,

                    fontSize =
                        uiState.romanTransliterationFontSize.sp,

                    lineHeight =
                        (uiState.romanTransliterationFontSize * 1.45f).sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }

            /*
             * ROMAN
             */

            if (
                uiState.showRomanTransliteration &&
                line.transliteration.isNotBlank()
            ) {

                Text(

                    text =
                        line.transliteration,

                    modifier =
                        Modifier.fillMaxWidth(),

                    color =
                        romanColor.copy(alpha = 0.82f),

                    textAlign =

                        if (uiState.centerGurmukhi)
                            TextAlign.Center
                        else
                            TextAlign.Start,

                    fontSize =
                        uiState.romanTransliterationFontSize.sp,

                    fontStyle =

                        if (uiState.italicRomanTransliteration)
                            FontStyle.Italic
                        else
                            FontStyle.Normal,

                    lineHeight =
                        (uiState.romanTransliterationFontSize * 1.42f).sp
                )
            }

            /*
             * ENGLISH
             */

            if (
                uiState.showEnglishTranslation &&
                line.translationEn.isNotBlank()
            ) {

                Surface(

                    shape =
                        RoundedCornerShape(18.dp),

                    color =
                        hukamnamaAccent.copy(
                            alpha = 0.05f
                        )
                ) {

                    Text(

                        text =
                            line.translationEn,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 12.dp
                                ),

                        color =
                            translationColor,

                        textAlign =

                            if (uiState.centerGurmukhi)
                                TextAlign.Center
                            else
                                TextAlign.Start,

                        fontSize =
                            uiState.translationFontSize.sp,

                        lineHeight =
                            (uiState.translationFontSize * 1.5f).sp
                    )
                }
            }

            /*
             * PUNJABI
             */

            if (
                uiState.showPunjabiTranslation &&
                line.translationPa.isNotBlank()
            ) {

                Surface(

                    shape =
                        RoundedCornerShape(18.dp),

                    color =
                        hukamnamaAccent.copy(
                            alpha = 0.04f
                        )
                ) {

                    Text(

                        text =
                            line.translationPa,

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 12.dp
                                ),

                        color =
                            punjabiColor,

                        textAlign =

                            if (uiState.centerGurmukhi)
                                TextAlign.Center
                            else
                                TextAlign.Start,

                        fontSize =
                            uiState.translationFontSize.sp,

                        lineHeight =
                            (uiState.translationFontSize * 1.5f).sp
                    )
                }
            }
        }

        // Spacer(
        //     Modifier.width(12.dp)
        // )

        // /*
        //  * ACTION RAIL
        //  */

        // Column(

        //     verticalArrangement =
        //         Arrangement.spacedBy(10.dp),

        //     horizontalAlignment =
        //         Alignment.CenterHorizontally
        // ) {

        //     val actionContainer =

        //         Modifier
        //             .size(38.dp)
        //             .clip(CircleShape)
        //             .background(
        //                 hukamnamaAccent.copy(
        //                     alpha = 0.08f
        //                 )
        //             )

        //     /*
        //      * BOOKMARK
        //      */

        //     Box(

        //         modifier =
        //             actionContainer
        //                 .clickable {

        //                     viewModel.toggleBookmark(
        //                        "hukamnama-$index"
        //                     )
        //                 },

        //         contentAlignment =
        //             Alignment.Center
        //     ) {

        //         Icon(

        //             imageVector =

        //                 if (
        //                     uiState.bookmarkedLineIds.contains(
        //                        "hukamnama-$index"
        //                     )
        //                 ) {
        //                     Icons.Default.Bookmark
        //                 } else {
        //                     Icons.Default.BookmarkBorder
        //                 },

        //             contentDescription = null,

        //             tint =
        //                 hukamnamaAccent,

        //             modifier =
        //                 Modifier.size(18.dp)
        //         )
        //     }

        //     /*
        //      * MEANING
        //      */

        //     Box(

        //         modifier =
        //             actionContainer
        //                 .clickable {

        //                     selectedMeaningLine =
        //                         line
        //                 },

        //         contentAlignment =
        //             Alignment.Center
        //     ) {

        //         Icon(

        //             imageVector =
        //                 Icons.Default.Lightbulb,

        //             contentDescription = null,

        //             tint =
        //                 hukamnamaAccent,

        //             modifier =
        //                 Modifier.size(18.dp)
        //         )
        //     }

        //     /*
        //      * COPY
        //      */

        //     Box(

        //         modifier =
        //             actionContainer
        //                 .clickable {

        //                     clipboardManager.setText(

        //                         AnnotatedString(
        //                             gurmukhiText
        //                         )
        //                     )
        //                 },

        //         contentAlignment =
        //             Alignment.Center
        //     ) {

        //         Icon(

        //             imageVector =
        //                 Icons.Default.ContentCopy,

        //             contentDescription = null,

        //             tint =
        //                 hukamnamaAccent,

        //             modifier =
        //                 Modifier.size(17.dp)
        //         )
        //     }
        // }
    }
}
                }

                if (index != current.lines.lastIndex) {

                    Spacer(
                        Modifier.height(
                            if (uiState.compactMode)
                                8.dp
                            else
                                12.dp
                        )
                    )
                }
            }

            Spacer(
                Modifier.height(28.dp)
            )
        }
    }
}

    }
}
        
@Composable
private fun PremiumInfoCard(

    modifier: Modifier = Modifier,

    title: String,

    value: String,

    icon: ImageVector,

    accent: Color,

    primaryText: Color,

    secondaryText: Color,

    glassSurface: Color,

    glassBorder: Color,

    displayLanguage: DisplayLanguage =
        DisplayLanguage.English
) {

    Surface(

        modifier = modifier,

        shape =
            RoundedCornerShape(20.dp),

        color =
            glassSurface,

        border = BorderStroke(
            1.dp,
            glassBorder
        )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp)
                .padding(

                    horizontal = 14.dp,

                    vertical = 8.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            /*
             * ICON
             */

            Box(

                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(

                        when (
                            AppTheme.themeMode
                        ) {

                            ThemeMode.Light ->

                                Color.White.copy(
                                    alpha = 0.14f
                                )

                            ThemeMode.Dark ->

                                Color.Black.copy(
                                    alpha = 0.28f
                                )

                            ThemeMode.Amoled ->

                                Color(0xFF0D0D0D)
                        }
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector = icon,

                    contentDescription = null,

                    tint = when (

                        AppTheme.themeMode
                    ) {

                        ThemeMode.Light ->

                            Color.White

                        else ->

                            accent
                    },

                    modifier =
                        Modifier.size(20.dp)
                )
            }

            Spacer(
                Modifier.width(10.dp)
            )

            /*
             * CONTENT
             */

            Column(

                modifier =
                    Modifier.weight(1f),

                verticalArrangement =
                    Arrangement.Center
            ) {

                if (title.isNotBlank()) {

                    Text(

                        text = title,

                        color = secondaryText,

                        fontSize = 10.sp,

                        fontWeight =
                            FontWeight.Medium,

                        maxLines = 1,

                        overflow =
                            TextOverflow.Ellipsis
                    )

                    Spacer(
                        Modifier.height(2.dp)
                    )
                }

                val finalValue =
                    value.ifBlank {

                        "Nitnem"
                            .translate(
                                displayLanguage
                            )
                    }.translateDigits(
                        displayLanguage
                    )

                val mainText =
                    finalValue.substringBefore(
                        "\n"
                    )

                val subText =
                    finalValue.substringAfter(
                        "\n",
                        ""
                    )

                /*
                 * MAIN TEXT
                 */

                Text(

                    text = mainText,

                    color = primaryText,

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    maxLines = 1,

                    overflow =
                        TextOverflow.Ellipsis
                )

                /*
                 * SUB TEXT
                 */

                if (subText.isNotBlank()) {

                    Spacer(
                        Modifier.height(1.dp)
                    )

                    Text(

                        text = subText,

                        color =
                            secondaryText.copy(
                                alpha = 0.72f
                            ),

                        fontSize = 9.sp,

                        fontWeight =
                            FontWeight.Medium,

                        maxLines = 1,

                        overflow =
                            TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
@Composable
private fun DetailRow(
    label: String,
    value: String,
    labelColor: Color = Gold,
    valueColor: Color = Color.White
) {

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(

            text = "$label:",

            color = labelColor,

            fontSize = 12.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            Modifier.width(8.dp)
        )

        Text(

            text = value,

            color = valueColor,

            fontSize = 12.sp
        )
    }
}

@Composable
private fun SettingsRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    size: Float,
    onSizeChange: (Float) -> Unit,
    minSize: Float,
    maxSize: Float,
    primaryColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Gold)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = primaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (size > minSize) onSizeChange(size - 1f) },
                enabled = checked
            ) {
                Text(
                    text = "—",
                    color = if (checked) Gold else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${size.toInt()}sp",
                color = if (checked) primaryColor else Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(
                onClick = { if (size < maxSize) onSizeChange(size + 1f) },
                enabled = checked
            ) {
                Text(
                    text = "+",
                    color = if (checked) Gold else Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun resolveTextColor(
    mode: String,
    colors: ColorScheme,
    default: Color
): Color {
    return when (mode) {
        "gold" -> Color(0xFFD6B25E)
        "accent" -> colors.primary
        "muted" -> colors.onSurfaceVariant.copy(alpha = 0.72f)
        "white" -> if (colors.surface.luminance() > 0.5f) colors.onSurface else Color.White
        "purple" -> Color(0xFFB39DFF)
        "blue" -> Color(0xFF7AB8FF)
        "green" -> Color(0xFF63D8B6)
        "orange" -> Color(0xFFFFB36A)
        else -> default
    }
}

@Composable
private fun MetaChip(
    label: String
) {

    Surface(

        shape = RoundedCornerShape(14.dp),

        color = Gold.copy(alpha = 0.10f),

        border = BorderStroke(
            1.dp,
            Gold.copy(alpha = 0.14f)
        )
    ) {

        Text(

            text = label,

            color = Gold,

            fontSize = 10.sp,

            fontWeight = FontWeight.SemiBold,

            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
        )
    }
}