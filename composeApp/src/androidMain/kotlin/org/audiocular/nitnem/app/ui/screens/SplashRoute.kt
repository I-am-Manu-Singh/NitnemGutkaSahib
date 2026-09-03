package org.audiocular.nitnem.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.data.preferences.AppPreferences
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate

@Composable
fun SplashRoute(
    displayLanguage: DisplayLanguage,
    onOpenHome: () -> Unit,
    onOpenOnboarding: () -> Unit
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val prefs = AppPreferences(context)
        val completed = prefs.onboardingCompleted.first()

        if (completed) {
            onOpenHome()
        } else {
            onOpenOnboarding()
        }
    }

    SplashScreen(displayLanguage)
}

@Composable
fun SplashScreen(displayLanguage: DisplayLanguage) {

    val backgroundGradient = when (AppTheme.themeMode) {

        ThemeMode.Light -> {

            listOf(
                Color(0xFFFFFCF8),
                Color(0xFFF9F1E3),
                Color(0xFFF1DFC2),
                Color(0xFFE2BC7A)
            )
        }

        ThemeMode.Dark -> {

            listOf(
                Color(0xFF0B0D10),
                Color(0xFF14181D),
                Color(0xFF1B2127),
                Color(0xFF2A2118)
            )
        }

        ThemeMode.Amoled -> {

            listOf(
                Color.Black,
                Color(0xFF050505),
                Color(0xFF0B0B0B),
                Color(0xFF1A140B)
            )
        }
    }

    val infinite =
        rememberInfiniteTransition(
            label = ""
        )

    val mainGlowScale by infinite.animateFloat(

        initialValue = 0.92f,

        targetValue = 1.12f,

        animationSpec =
            infiniteRepeatable(

                animation = tween(
                    durationMillis = 4200,
                    easing = EaseInOut
                ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label = ""
    )

    val mainGlowAlpha by infinite.animateFloat(

        initialValue = 0.12f,

        targetValue = 0.28f,

        animationSpec =
            infiniteRepeatable(

                animation = tween(
                    durationMillis = 3200
                ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label = ""
    )

    val orbFloat by infinite.animateFloat(

        initialValue = -10f,

        targetValue = 14f,

        animationSpec =
            infiniteRepeatable(

                animation = tween(
                    durationMillis = 3600,
                    easing = EaseInOut
                ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label = ""
    )

    val logoScale by infinite.animateFloat(

        initialValue = 0.98f,

        targetValue = 1.04f,

        animationSpec =
            infiniteRepeatable(

                animation = tween(
                    durationMillis = 2600,
                    easing = EaseInOut
                ),

                repeatMode =
                    RepeatMode.Reverse
            ),

        label = ""
    )

    val shimmer by infinite.animateFloat(

        initialValue = -300f,

        targetValue = 900f,

        animationSpec =
            infiniteRepeatable(

                animation = tween(
                    durationMillis = 4200,
                    easing = LinearEasing
                )
            ),

        label = ""
    )

    Box(

        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    backgroundGradient
                )
            )
            .background(

                if (AppTheme.themeMode == ThemeMode.Light)
                    Color.White.copy(alpha = 0.04f)
                else
                    Color.Transparent
            )
    ) {

        Box(

            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-120).dp)
                .size(420.dp)
                .scale(mainGlowScale)
                .alpha(mainGlowAlpha)
                .background(

                    Brush.radialGradient(

                        colors = listOf(

                            if (AppTheme.themeMode == ThemeMode.Light)
                                Gold.copy(alpha = 0.28f)
                            else
                                Gold.copy(alpha = 0.9f),

                            if (AppTheme.themeMode == ThemeMode.Light)
                                Gold.copy(alpha = 0.12f)
                            else
                                Gold.copy(alpha = 0.35f),

                            Color.Transparent
                        )
                    ),

                    shape = CircleShape
                )
                .blur(120.dp)
        )

        PremiumOrb(

            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(
                    x = (-70).dp,
                    y = 120.dp
                ),

            size = 80.dp,

            alpha = 0.08f
        )

        PremiumOrb(

            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(
                    x = 80.dp,
                    y = 60.dp
                ),

            size = 80.dp,

            alpha = 0.06f
        )

        /*
 * FLOATING ICONS
 */

FloatingPremiumIcon(

    icon = Icons.Default.AutoStories,

    modifier = Modifier
        .align(Alignment.TopStart)
        .padding(
            start = 28.dp,
            top = 110.dp
        )
        .offset(
            y = orbFloat.dp
        ),

    size = 42.dp,

    iconSize = 16.dp
)

FloatingPremiumIcon(

    icon = Icons.Default.GraphicEq,

    modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 22.dp)
        .offset(
            y = (-orbFloat).dp
        ),

    size = 38.dp,

    iconSize = 14.dp
)

FloatingPremiumIcon(

    icon = Icons.Default.NightlightRound,

    modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(
            start = 24.dp,
            bottom = 140.dp
        )
        .offset(
            y = (orbFloat / 2).dp
        ),

    size = 40.dp,

    iconSize = 15.dp
)

FloatingPremiumIcon(

    icon = Icons.Default.AutoStories,

    modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(
            end = 70.dp,
            top = 180.dp
        )
        .offset(
            y = (-orbFloat / 1.5f).dp
        ),

    size = 34.dp,

    iconSize = 12.dp
)

FloatingPremiumIcon(

    icon = Icons.Default.GraphicEq,

    modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(
            end = 46.dp,
            bottom = 180.dp
        )
        .offset(
            y = (orbFloat / 1.8f).dp
        ),

    size = 36.dp,

    iconSize = 13.dp
)

FloatingPremiumIcon(

    icon = Icons.Default.NightlightRound,

    modifier = Modifier
        .align(Alignment.CenterStart)
        .padding(start = 12.dp)
        .offset(
            y = (-orbFloat / 2).dp
        ),

    size = 30.dp,

    iconSize = 11.dp
)

FloatingPremiumIcon(

    icon = Icons.Default.AutoStories,

    modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 80.dp)
        .offset(
            x = 120.dp,
            y = (orbFloat / 2).dp
        ),

    size = 32.dp,

    iconSize = 12.dp
)

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Box(

                contentAlignment =
                    Alignment.Center
            ) {

                Surface(

                    shape = CircleShape,

                    color =

                        if (AppTheme.themeMode == ThemeMode.Light)
                            Color.White.copy(alpha = 0.75f)
                        else
                            Color.White.copy(alpha = 0.05f),

                    border = BorderStroke(

                        1.dp,

                        if (AppTheme.themeMode == ThemeMode.Light)
                            Color(0xFFE6D5BB)
                        else
                            Color.White.copy(alpha = 0.05f)
                    ),

                    tonalElevation = 0.dp
                ) {

                    Box(

                        modifier = Modifier
                            .size(220.dp)
                    )
                }

                Box(

                    modifier = Modifier
                        .size(180.dp)
                        .background(

                            Brush.radialGradient(

                                colors = listOf(

                                    Gold.copy(alpha = 0.22f),

                                    Gold.copy(alpha = 0.10f),

                                    Color.Transparent
                                )
                            ),

                            shape = CircleShape
                        )
                )

                Box(

                    modifier = Modifier
                        .size(180.dp)
                        .graphicsLayer {

                            rotationZ = 18f
                        }
                        .background(

                            Brush.linearGradient(

                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.10f),
                                    Color.Transparent
                                ),

                                start = androidx.compose.ui.geometry.Offset(
                                    shimmer,
                                    0f
                                ),

                                end = androidx.compose.ui.geometry.Offset(
                                    shimmer + 400f,
                                    400f
                                )
                            ),

                            shape = CircleShape
                        )
                )

                Box(

                    modifier = Modifier
                        .size(128.dp)
                        .scale(logoScale)
                        .background(

                            Brush.radialGradient(

                                colors = listOf(

                                    Color.White.copy(alpha = 0.12f),

                                    Gold.copy(alpha = 0.10f),

                                    Color.Transparent
                                )
                            ),

                            shape = CircleShape
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(

                        text = "ੴ",

                        color = Gold,

                        fontSize = 92.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                Modifier.height(42.dp)
            )

            Text(

                text = "Nitnem Gutka".translate(displayLanguage),

                color =

                    if (AppTheme.themeMode == ThemeMode.Light)
                        Color(0xFF231D16)
                    else
                        Color.White,

                fontSize = 38.sp,

                fontWeight =
                    FontWeight.ExtraBold,

                letterSpacing = 0.6.sp
            )

            Spacer(
                Modifier.height(14.dp)
            )

            Text(

                text =
                    "Immersive Gurbani reading,\naudio listening and daily Nitnem.".translate(displayLanguage),

                textAlign =
                    TextAlign.Center,

                color =

                    if (AppTheme.themeMode == ThemeMode.Light)
                        Color(0xFF5A4936)
                    else
                        Color.White.copy(alpha = 0.70f),

                lineHeight = 26.sp,

                fontSize = 15.sp
            )

            Spacer(
                Modifier.height(56.dp)
            )

            PremiumLoader()
        }

        Surface(

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),

            shape = RoundedCornerShape(100.dp),

            color =

                if (AppTheme.themeMode == ThemeMode.Light)
                    Color.White.copy(alpha = 0.78f)
                else
                    Color.White.copy(alpha = 0.06f),

            border = BorderStroke(

                1.dp,

                if (AppTheme.themeMode == ThemeMode.Light)
                    Color(0xFFE5D3B7)
                else
                    Color.White.copy(alpha = 0.04f)
            ),

            tonalElevation = 0.dp
        ) {

            Row(

                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 10.dp
                ),

                verticalAlignment =
                    Alignment.CenterVertically,

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Box(

                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            Gold,
                            CircleShape
                        )
                )

                Text(

                    text =
                        "Peaceful • Focused • Sacred".translate(displayLanguage),

                    color =

                        if (AppTheme.themeMode == ThemeMode.Light)
                            Color(0xFF6F6252)
                        else
                            Color.White.copy(alpha = 0.55f),

                    fontSize = 12.sp,

                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PremiumOrb(
    modifier: Modifier,
    size: Dp,
    alpha: Float
) {

    Box(

        modifier = modifier
            .size(size)
            .background(

                Brush.radialGradient(

                    colors = listOf(
                        Gold.copy(alpha = alpha),
                        Color.Transparent
                    )
                ),

                shape = CircleShape
            )
            .blur(100.dp)
    )
}
@Composable
private fun FloatingPremiumIcon(

    icon: androidx.compose.ui.graphics.vector.ImageVector,

    modifier: Modifier = Modifier,

    size: Dp = 42.dp,

    iconSize: Dp = 16.dp
) {

    Surface(

        modifier = modifier,

        shape = CircleShape,

        color =

            if (AppTheme.themeMode == ThemeMode.Light)
                Color.White.copy(alpha = 0.42f)
            else
                Color.White.copy(alpha = 0.025f),

        border = BorderStroke(

            0.8.dp,

            if (AppTheme.themeMode == ThemeMode.Light)
                Color(0xFFE8D7BE).copy(alpha = 0.55f)
            else
                Color.White.copy(alpha = 0.03f)
        )
    ) {

        Box(

            modifier = Modifier
                .size(size),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint =

                    if (AppTheme.themeMode == ThemeMode.Light)
                        Gold.copy(alpha = 0.72f)
                    else
                        Gold.copy(alpha = 0.13f),

                modifier = Modifier.size(iconSize)
            )
        }
    }
}
@Composable
private fun PremiumLoader() {

    val infinite =
        rememberInfiniteTransition(
            label = ""
        )

    Row(

        horizontalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        repeat(3) { index ->

            val scale by infinite.animateFloat(

                initialValue = 0.7f,

                targetValue = 1.25f,

                animationSpec =
                    infiniteRepeatable(

                        animation = tween(
                            durationMillis = 750,
                            delayMillis =
                                index * 180
                        ),

                        repeatMode =
                            RepeatMode.Reverse
                    ),

                label = ""
            )

            val alpha by infinite.animateFloat(

                initialValue = 0.25f,

                targetValue = 1f,

                animationSpec =
                    infiniteRepeatable(

                        animation = tween(
                            durationMillis = 750,
                            delayMillis =
                                index * 180
                        ),

                        repeatMode =
                            RepeatMode.Reverse
                    ),

                label = ""
            )

            Box(

                modifier = Modifier
                    .size(12.dp)
                    .scale(scale)
                    .alpha(alpha)
                    .background(

                        Brush.radialGradient(

                            colors =

                                if (AppTheme.themeMode == ThemeMode.Light) {

                                    listOf(
                                        Color(0xFFFFD36A),
                                        Color(0xFFE0A93B)
                                    )

                                } else {

                                    listOf(
                                        Color.White,
                                        Gold
                                    )
                                }
                        ),

                        CircleShape
                    )
            )
        }
    }
}