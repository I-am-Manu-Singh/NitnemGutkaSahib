package org.audiocular.nitnem.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.audiocular.nitnem.app.ui.theme.AppTheme
import org.audiocular.nitnem.app.ui.theme.Gold
import org.audiocular.nitnem.app.ui.theme.ThemeMode

@Composable
fun NitnemLogo(
    modifier: Modifier = Modifier,
    baseSize: Dp = 220.dp,
    showShimmer: Boolean = true,
    showGlow: Boolean = true
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light

    val infinite = rememberInfiniteTransition(label = "logo_infinite")

    val logoScale by infinite.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    val shimmer by infinite.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing)
        ),
        label = "shimmer"
    )

    val mainGlowScale by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    val mainGlowAlpha by infinite.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val scaleFactor = baseSize.value / 220f

    Box(
        modifier = modifier.size(baseSize),
        contentAlignment = Alignment.Center
    ) {
        if (showGlow) {
            Box(
                modifier = Modifier
                    .size(baseSize * 1.5f)
                    .scale(mainGlowScale)
                    .alpha(mainGlowAlpha)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                if (isLight) Gold.copy(alpha = 0.28f) else Gold.copy(alpha = 0.9f),
                                if (isLight) Gold.copy(alpha = 0.12f) else Gold.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
                    .blur(120.dp * scaleFactor)
            )
        }

        Surface(
            shape = CircleShape,
            color = if (isLight) Color.White.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.05f),
            border = BorderStroke(
                1.dp,
                if (isLight) Color(0xFFE6D5BB) else Color.White.copy(alpha = 0.05f)
            ),
            tonalElevation = 0.dp
        ) {
            Box(modifier = Modifier.size(baseSize))
        }

        Box(
            modifier = Modifier
                .size(baseSize * (180f / 220f))
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

        if (showShimmer) {
            Box(
                modifier = Modifier
                    .size(baseSize * (180f / 220f))
                    .graphicsLayer { rotationZ = 18f }
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(shimmer * scaleFactor, 0f),
                            end = androidx.compose.ui.geometry.Offset((shimmer + 400f) * scaleFactor, 400f * scaleFactor)
                        ),
                        shape = CircleShape
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(baseSize * (128f / 220f))
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
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ੴ",
                color = Gold,
                fontSize = (92 * scaleFactor).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
