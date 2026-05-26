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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.sin
import kotlin.math.cos
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
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

val hukamnamaText = remember(hukamnama, uiState.larivaarMode) {

    hukamnama
        ?.lines
        ?.joinToString("\n") {

            val text = if (uiState.larivaarMode) it.larivaar else it.gurmukhi
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

            /*
             * STARS & CLOUDS BACKGROUND EFFECTS
             */
            Canvas(modifier = Modifier.matchParentSize()) {
                val width = size.width
                val height = size.height
                if (width > 0f && height > 0f) {
                    
                    // Draw 6 Twinkling Stars
                    val starPositions = listOf(
                        Offset(0.12f, 0.22f),
                        Offset(0.45f, 0.15f),
                        Offset(0.78f, 0.28f),
                        Offset(0.28f, 0.65f),
                        Offset(0.60f, 0.78f),
                        Offset(0.88f, 0.60f)
                    )
                    
                    starPositions.forEachIndexed { i, pos ->
                        val angle = (twinkleTime + i * 60f) % 360f
                        val rad = angle * PI.toFloat() / 180f
                        val starAlpha = (0.15f + 0.5f * sin(rad).absoluteValue).coerceIn(0f, 1f)
                        
                        val starSize = if (i % 2 == 0) 10f else 7f
                        val x = pos.x * width
                        val y = pos.y * height
                        
                        val starPath = Path().apply {
                            moveTo(x, y - starSize)
                            quadraticTo(x, y, x + starSize, y)
                            quadraticTo(x, y, x, y + starSize)
                            quadraticTo(x, y, x - starSize, y)
                            quadraticTo(x, y, x, y - starSize)
                            close()
                        }
                        drawPath(starPath, color = Color.White.copy(alpha = starAlpha))
                    }
                    
                    // Draw drifting Cloud 1 (Left to Right)
                    val cloud1X = -120f + (width + 240f) * cloudProgress1
                    val cloud1Y = height * 0.35f
                    val c1Color = Color.White.copy(alpha = 0.05f)
                    drawCircle(c1Color, radius = 22f, center = Offset(cloud1X, cloud1Y))
                    drawCircle(c1Color, radius = 30f, center = Offset(cloud1X + 24f, cloud1Y - 8f))
                    drawCircle(c1Color, radius = 26f, center = Offset(cloud1X + 48f, cloud1Y))
                    drawCircle(c1Color, radius = 18f, center = Offset(cloud1X - 20f, cloud1Y + 4f))
                    
                    // Draw drifting Cloud 2 (Right to Left)
                    val cloud2X = (width + 120f) - (width + 240f) * cloudProgress2
                    val cloud2Y = height * 0.72f
                    val c2Color = Color.White.copy(alpha = 0.06f)
                    drawCircle(c2Color, radius = 26f, center = Offset(cloud2X, cloud2Y))
                    drawCircle(c2Color, radius = 36f, center = Offset(cloud2X + 28f, cloud2Y - 10f))
                    drawCircle(c2Color, radius = 30f, center = Offset(cloud2X + 56f, cloud2Y))
                    drawCircle(c2Color, radius = 20f, center = Offset(cloud2X - 24f, cloud2Y + 6f))
                }
            }

            /*
             * AMBIENT ICONS (No-clash, gentle sway)
             */

            Icon(

                imageVector =
                    prayerInfo.ambientIcon,

                contentDescription = null,

                tint =
                    Color.White.copy(alpha = 0.07f),

                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = 80.dp,
                        end = 16.dp
                    )
                    .size(90.dp)
                    .rotate(swayRotation1)
                    .offset(
                        y = floatingOffset1.dp
                    )
            )

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
                        .padding(18.dp)
                    
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

                    Row(

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(

                            imageVector =
                                Icons.Default.LocalFireDepartment,

                            contentDescription = null,

                            tint = Gold,

                            modifier =
                                Modifier.size(25.dp)
                        )

                        Spacer(
                            Modifier.height(8.dp)
                        )

                        Text(

                            text =
                                streak.toString(),

                            color = primaryText,

                            fontWeight =
                                FontWeight.Bold,

                            fontSize = 15.sp
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
            max = 140.dp
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
                    Color.White.copy(alpha = 0.10f)
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

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(
                    rememberScrollState()
                )
        .padding(
            horizontal = 16.dp,
            vertical = 2.dp
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
                    Modifier.width(6.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(

                        text = "Daily Hukamnama".translate(displayLanguage),

                        color = primarySheetText,

                        fontSize = 22.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(3.dp)
                    )

                    Text(

                        text = remember(current.date, displayLanguage) {
                            current.date.split(", ").map { part ->
                                part.split(" ").map { sub ->
                                    sub.translate(displayLanguage)
                                }.joinToString(" ")
                            }.joinToString(", ").translateDigits(displayLanguage)
                        },

                        color = secondarySheetText,

                        fontSize = 12.sp
                    )
                }
            }

            Spacer(
                Modifier.height(6.dp)
            )

            /*
             * META CARD
             */


                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {

                    FlowRow(

    horizontalArrangement =
        Arrangement.spacedBy(6.dp),

    verticalArrangement =
        Arrangement.spacedBy(2.dp)
) {

    MetaChip(
        label = if (current.isFromApi) {
            "${"Ang".translate(displayLanguage)} ${current.ang}".translateDigits(displayLanguage)
        } else {
            current.ang.translateAng(displayLanguage)
        }
    )

    MetaChip(
        label = translateRaag(current.raag, displayLanguage)
    )

    MetaChip(
        label = translateWriter(current.writer, displayLanguage)
    )
}
                }
            

            Spacer(
                Modifier.height(6.dp)
            )

/*
 * FULL HUKAMNAMA
 */

val colors = MaterialTheme.colorScheme
val gurmukhiColor = resolveTextColor(
    mode = uiState.gurmukhiColorMode,
    colors = colors,
    default = hukamnamaAccent
)
val romanColor = resolveTextColor(
    mode = uiState.romanTransliterationColorMode,
    colors = colors,
    default = secondarySheetText.copy(alpha = 0.8f)
)
val translationColor = resolveTextColor(
    mode = uiState.translationColorMode,
    colors = colors,
    default = secondarySheetText.copy(alpha = 0.88f)
)
val punjabiColor = resolveTextColor(
    mode = uiState.translationColorMode,
    colors = colors,
    default = secondarySheetText
)

current.lines.forEachIndexed { index, line ->

    Surface(

        shape = RoundedCornerShape(if (uiState.compactMode) 18.dp else 24.dp),

        color = cardBackground,

        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.04f)
        )
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(if (uiState.compactMode) 6.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(if (uiState.compactMode) 4.dp else 8.dp)
        ) {

            val gurmukhiText = if (uiState.larivaarMode) line.larivaar else line.gurmukhi
            if (gurmukhiText.isNotBlank()) {
                Text(
                    text = gurmukhiText,
                    modifier = Modifier.fillMaxWidth(),
                    color = gurmukhiColor,
                    textAlign = if (uiState.centerGurmukhi) TextAlign.Center else TextAlign.Start,
                    fontSize = uiState.gurmukhiFontSize.sp,
                    fontWeight = if (uiState.boldGurmukhi) FontWeight.Bold else FontWeight.Medium,
                    lineHeight = uiState.lineSpacing.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = gurmukhiColor.copy(alpha = 0.30f),
                            blurRadius = 10f
                        )
                    )
                )
            }

            if (uiState.showDevanagariTransliteration && line.transliterationHi.isNotBlank()) {
                Text(
                    text = line.transliterationHi,
                    modifier = Modifier.fillMaxWidth(),
                    color = romanColor,
                    textAlign = if (uiState.centerGurmukhi) TextAlign.Center else TextAlign.Start,
                    fontSize = uiState.romanTransliterationFontSize.sp,
                    lineHeight = (uiState.romanTransliterationFontSize * 1.3f).sp
                )
            }

            if (uiState.showRomanTransliteration && line.transliteration.isNotBlank()) {
                Text(
                    text = line.transliteration,
                    modifier = Modifier.fillMaxWidth(),
                    color = romanColor,
                    textAlign = if (uiState.centerGurmukhi) TextAlign.Center else TextAlign.Start,
                    fontSize = uiState.romanTransliterationFontSize.sp,
                    fontStyle = if (uiState.italicRomanTransliteration) FontStyle.Italic else FontStyle.Normal,
                    lineHeight = (uiState.romanTransliterationFontSize * 1.3f).sp
                )
            }

            if (uiState.showEnglishTranslation && line.translationEn.isNotBlank()) {
                Text(
                    text = line.translationEn,
                    modifier = Modifier.fillMaxWidth(),
                    color = translationColor,
                    textAlign = if (uiState.centerGurmukhi) TextAlign.Center else TextAlign.Start,
                    fontSize = uiState.translationFontSize.sp,
                    lineHeight = (uiState.translationFontSize * 1.3f).sp
                )
            }

            if (uiState.showPunjabiTranslation && line.translationPa.isNotBlank()) {
                Text(
                    text = line.translationPa,
                    modifier = Modifier.fillMaxWidth(),
                    color = punjabiColor,
                    textAlign = if (uiState.centerGurmukhi) TextAlign.Center else TextAlign.Start,
                    fontSize = uiState.translationFontSize.sp,
                    lineHeight = (uiState.translationFontSize * 1.5f).sp
                )
            }
        }
    }

    if (index != current.lines.lastIndex) {

        Spacer(
            Modifier.height(if (uiState.compactMode) 6.dp else 10.dp)
        )
    }
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
private fun MetaChip(

    label: String,

    textColor: Color = Gold
) {

    Surface(

        shape =
            RoundedCornerShape(14.dp),

        color =
            Gold.copy(alpha = 0.10f),

        border = BorderStroke(
            1.dp,
            Gold.copy(alpha = 0.14f)
        )
    ) {

        Text(

            text = label,

            color = textColor,

            fontSize = 11.sp,

            fontWeight = FontWeight.SemiBold,

            modifier = Modifier.padding(
                horizontal = 6.dp,
                vertical = 2.dp
            )
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