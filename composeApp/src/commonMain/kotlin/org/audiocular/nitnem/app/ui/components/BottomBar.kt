package org.audiocular.nitnem.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.audiocular.nitnem.app.models.AppTab
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.PlaybackSnapshot
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.basicMarquee
import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
@Composable
fun BottomBar(
    selectedTab: AppTab,
    playback: PlaybackSnapshot,
    currentBani: Bani?,
    language: DisplayLanguage,
    onPlayPause: () -> Unit,
    onExpandPlayer: () -> Unit,
    onToggleMiniPlayer: () -> Unit,
    onTabSelected: (AppTab) -> Unit
) {
Surface(

    modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars),

    color = when (AppTheme.themeMode) {

        ThemeMode.Light ->
            Color.White

        ThemeMode.Dark ->
            Color(0xFF121212)

        ThemeMode.Amoled ->
            Color.Black
    },

    border = BorderStroke(
        1.dp,
        if (AppTheme.themeMode == ThemeMode.Light)
            Color(0xFFEAE2D7)
        else
            Color.White.copy(alpha = 0.04f)
    ),

    tonalElevation = 0.dp,

    shadowElevation =
        if (AppTheme.themeMode == ThemeMode.Light)
            8.dp
        else
            0.dp
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 6.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

            BottomItem(
                selected = selectedTab == AppTab.Home,
                icon = Icons.Filled.Home,
                label = "Home".translate(language),
                onClick = { onTabSelected(AppTab.Home) }
            )

            BottomItem(
                selected = selectedTab == AppTab.Experience,
                icon = Icons.Filled.MenuBook,
                label = "Read".translate(language),
                onClick = { onTabSelected(AppTab.Experience) }
            )
            /*
 * PLAYER CONTAINER
 */

val baniTheme = remember(currentBani?.title) {

    resolveBaniTheme(
        currentBani?.title ?: "Nitnem"
    )
}

val infiniteTransition =
    rememberInfiniteTransition(
        label = ""
    )

val pulseAnim by infiniteTransition.animateFloat(

    initialValue = 0.96f,

    targetValue = 1.03f,

    animationSpec = infiniteRepeatable(

        animation = tween(
            durationMillis = 2600,
            easing = EaseInOutSine
        ),

        repeatMode = RepeatMode.Reverse
    ),

    label = ""
)

val floatingAnim by infiniteTransition.animateFloat(

    initialValue = -4f,

    targetValue = 5f,

    animationSpec = infiniteRepeatable(

        animation = tween(
            durationMillis = 3200,
            easing = EaseInOutSine
        ),

        repeatMode = RepeatMode.Reverse
    ),

    label = ""
)

val rotateAnim by infiniteTransition.animateFloat(

    initialValue = 0f,

    targetValue = 360f,

    animationSpec = infiniteRepeatable(

        animation = tween(
            durationMillis = 28000,
            easing = LinearEasing
        )
    ),

    label = ""
)

Surface(

    modifier = Modifier
        .weight(1.45f)
        .height(42.dp)
        .padding(horizontal = 4.dp)
        .scale(

            if (playback.isPlaying)
                1f
            else
                0.985f
        )
   .clickable(

    indication = null,

    interactionSource =
        remember {
            MutableInteractionSource()
        }

) {
    onToggleMiniPlayer()
},

    shape = RoundedCornerShape(18.dp),

    color = Color.Transparent,

    border = BorderStroke(

        1.dp,

        baniTheme.accent.copy(

            alpha =

                if (playback.isPlaying)
                    0.20f
                else
                    0.08f
        )
    ),

    shadowElevation =

        if (
            playback.isPlaying &&
            AppTheme.themeMode ==
            ThemeMode.Light
        ) {
            10.dp
        } else {
            0.dp
        }
) {

    Box(

        modifier = Modifier
            .fillMaxSize()
            .background(

                Brush.linearGradient(
                    baniTheme.gradient
                )
            )
    ) {

        /*
         * AMBIENT GLOW
         */

        if (playback.isPlaying) {

            Box(

                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseAnim)
                    .background(

                        Brush.radialGradient(

                            colors = listOf(

                                Color.White.copy(alpha = 0.12f),

                                baniTheme.accent.copy(alpha = 0.10f),

                                Color.Transparent
                            )
                        )
                    )
            )
        }

        /*
         * FLOATING ICON
         */

        Icon(

            imageVector =
                baniTheme.ambientIcon,

            contentDescription = null,

            tint =
                Color.White.copy(alpha = 0.08f),

            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
                .size(34.dp)
                .graphicsLayer {

                    rotationZ = rotateAnim

                    translationY = floatingAnim
                }
        )

        /*
         * CONTENT
         */

        Box(

            modifier = Modifier
                .fillMaxSize()
         
        ) {

            /*
             * TEXT CONTENT
             */

            Column(

                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 2.dp
                    ),

                verticalArrangement =
                    Arrangement.Center,

                horizontalAlignment =
                    Alignment.Start
            ) {

                val baseText = (currentBani?.title ?: "Nitnem").translate(language)
                Text(

                    text =
                        // if (playback.isLoading) "$baseText (Loading...)" else 
                        baseText,

                    modifier =
                        Modifier.basicMarquee(),

                    color = Color.White,

                    maxLines = 1,

                    fontSize = 12.sp,

                    fontWeight =
                        FontWeight.Bold
                )
            }

            /*
             * PROGRESS
             */

            Box(

                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .clip(

                        RoundedCornerShape(

                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    )
                    .background(
                        Color.White.copy(alpha = 0.10f)
                    )
            ) {

                Box(

                    modifier = Modifier
                        .fillMaxWidth(

                            (
                                playback.positionMs
                                    .toFloat() /

                                    playback.durationMs
                                        .coerceAtLeast(1L)
                                        .toFloat()
                            ).coerceIn(0f, 1f)
                        )
                        .fillMaxHeight()
                        .background(

                            Brush.horizontalGradient(

                                colors = listOf(
                                    Gold,
                                    Color.White
                                )
                            )
                        )
                )
            }
        }
    }
}
            BottomItem(
                selected = selectedTab == AppTab.Practice,
                icon = Icons.Filled.SelfImprovement,
                label = "Practice".translate(language),
                onClick = { onTabSelected(AppTab.Practice) }
            )

            BottomItem(
                selected = selectedTab == AppTab.Settings,
                icon = Icons.Filled.Settings,
                label = "Settings".translate(language),
                onClick = { onTabSelected(AppTab.Settings) }
            )
        }
    }
}

@Composable
private fun RowScope.BottomItem(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable { onClick() },

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.Start
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected)
                            Accent.copy(alpha = 0.12f)
                        else
                            Color.Transparent
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint =
                        if (selected)
                            Accent
                        else
                            TextPrimary.copy(alpha = 0.55f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Text(
            //     text = label,
            //     fontSize = 9.sp,
            //     color = if (selected) Accent else TextPrimary.copy(alpha = 0.45f)
            // )
        }
    }
}
@Composable
private fun resolveTabIcon(
    tab: AppTab
) = when (tab) {

    AppTab.Home ->
        Icons.Filled.Home

    AppTab.Experience ->
        Icons.Filled.MenuBook

    AppTab.Practice ->
        Icons.Filled.SelfImprovement

    AppTab.Settings ->
        Icons.Filled.Settings
}
private fun formatMillis(
    ms: Long
): String {

    val totalSeconds =
        (ms / 1000)
            .coerceAtLeast(0)

    val minutes =
        totalSeconds / 60

    val seconds =
        totalSeconds % 60

    return buildString {

        append(minutes)

        append(":")

        append(
            seconds.toString()
                .padStart(2, '0')
        )
    }
}