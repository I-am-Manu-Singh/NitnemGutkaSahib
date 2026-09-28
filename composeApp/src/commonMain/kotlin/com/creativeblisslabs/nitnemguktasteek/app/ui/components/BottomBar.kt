package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.creativeblisslabs.nitnemguktasteek.app.models.AppTab
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.PlaybackSnapshot
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.basicMarquee
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

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

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val isLandscape = maxWidth > 600.dp
        val barHeight = if (isLandscape) 58.dp else 68.dp
        val horizontalPadding = if (isLandscape) 32.dp else 6.dp

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .padding(horizontal = horizontalPadding),
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

/*
 * MINI PLAYER
 */

Surface(

    modifier = Modifier
        .weight(1.45f)
        .height(42.dp)
        .padding(horizontal = 4.dp)
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

    color = baniTheme.accent,

    border = BorderStroke(

        1.dp,

        Color.White.copy(

            alpha =

                if (
    playback.isPlaying
)
                    0.20f
                else
                    0.10f
        )
    ),

    shadowElevation =

        if (
            playback.isPlaying &&
            AppTheme.themeMode ==
            ThemeMode.Light
        ) {
            6.dp
        } else {
            0.dp
        }
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
      
        val baseText =
            (currentBani?.title ?: "Nitnem")
                .translate(language)

        Box(

            modifier = Modifier
                .fillMaxSize(),

            contentAlignment = Alignment.Center
        ) {

            Text(

                text = baseText,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
                    .basicMarquee(),

                color = Color.White,

                maxLines = 1,

                textAlign = TextAlign.Center,

                fontSize = 12.sp,

                fontWeight = FontWeight.Bold
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
                    Color.White.copy(alpha = 0.15f)
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

@Composable
fun AppNavigationRail(
    selectedTab: AppTab,
    language: DisplayLanguage,
    onTabSelected: (AppTab) -> Unit,
    playback: PlaybackSnapshot? = null,
    currentBani: Bani? = null,
    onToggleMiniPlayer: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(76.dp)
            .windowInsetsPadding(WindowInsets.statusBars),
        color = when (AppTheme.themeMode) {
            ThemeMode.Light -> Color.White
            ThemeMode.Dark -> Color(0xFF121212)
            ThemeMode.Amoled -> Color.Black
        },
        border = BorderStroke(
            1.dp,
            if (AppTheme.themeMode == ThemeMode.Light) Color(0xFFEAE2D7)
            else Color.White.copy(alpha = 0.04f)
        ),
        shadowElevation = if (AppTheme.themeMode == ThemeMode.Light) 6.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically)
        ) {
            val topTabs = listOf(
                AppTab.Home to Icons.Filled.Home,
                AppTab.Experience to Icons.Filled.MenuBook
            )
            val bottomTabs = listOf(
                AppTab.Practice to Icons.Filled.SelfImprovement,
                AppTab.Settings to Icons.Filled.Settings
            )

            topTabs.forEach { (tab, icon) ->
                val selected = selectedTab == tab
                Surface(
                    onClick = { onTabSelected(tab) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (selected) Accent.copy(alpha = 0.15f) else Color.Transparent,
                    border = BorderStroke(1.dp, if (selected) Accent.copy(alpha = 0.25f) else Color.Transparent),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.name.translate(language),
                                tint = if (selected) Accent else TextPrimary.copy(alpha = 0.55f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // ── MIDDLE MINI PLAYER TOGGLE BUTTON IN RAIL ──
            if (onToggleMiniPlayer != null) {
                val baniTheme = remember(currentBani?.title) {
                    resolveBaniTheme(currentBani?.title ?: "Nitnem")
                }
                val isPlaying = playback?.isPlaying == true
                val prayerAccent = baniTheme.accent
                val iconGradient = baniTheme.gradient
                val leadingIcon = baniTheme.icon

                Surface(
                    modifier = Modifier
                        .size(54.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onToggleMiniPlayer()
                        },
                    shape = RoundedCornerShape(16.dp),
                    // Fixed the glass box background issue by using solid/clean translucent background per theme
                    color = prayerAccent,
                    border = BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = if (isPlaying) 0.20f else 0.10f)
                    ),
                    shadowElevation = if (isPlaying && AppTheme.themeMode == ThemeMode.Light) 4.dp else 0.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val baseText = (currentBani?.title ?: "Nitnem").translate(language)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(iconGradient)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = leadingIcon,
                                        contentDescription = "Bani Icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(Modifier.height(0.dp))
                                Text(
                                    text = baseText,
                                    color = Color.White, // Unified portrait text color consistency
                                    maxLines = 1,
                                    textAlign = TextAlign.Center,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Progress indicator at the bottom edge
                        if (playback != null && playback.durationMs > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .fillMaxWidth()
                                    .height(2.5.dp)
                                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(
                                            (playback.positionMs.toFloat() / playback.durationMs.toFloat()).coerceIn(0f, 1f)
                                        )
                                        .fillMaxHeight()
                                        .background(Brush.horizontalGradient(iconGradient))
                                )
                            }
                        }
                    }
                }
            }

            bottomTabs.forEach { (tab, icon) ->
                val selected = selectedTab == tab
                Surface(
                    onClick = { onTabSelected(tab) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (selected) Accent.copy(alpha = 0.15f) else Color.Transparent,
                    border = BorderStroke(1.dp, if (selected) Accent.copy(alpha = 0.25f) else Color.Transparent),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.name.translate(language),
                                tint = if (selected) Accent else TextPrimary.copy(alpha = 0.55f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}