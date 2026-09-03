package org.audiocular.nitnem.app.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.PlaybackSnapshot
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateDigits
import org.audiocular.nitnem.utils.translateAng
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThinMiniPlayer(

    modifier: Modifier = Modifier,

    bani: Bani?,
    onSeekTo: (Long) -> Unit,
    playback: PlaybackSnapshot,

    currentLine: Int = 0,

    totalLines: Int = 0,

    visible: Boolean = true,
    repeatMode: String = "OFF",
    bookmarked: Boolean = false,
    displayLanguage: DisplayLanguage = DisplayLanguage.English,
    onPlayPause: () -> Unit,
    onSeekForward: () -> Unit,
    onSeekBack: () -> Unit,
    onRepeatToggle: () -> Unit = {},
    onSpeedToggle: () -> Unit = {},
    onBookmark: () -> Unit = {},
    isMuted: Boolean = false,
    onMuteToggle: () -> Unit = {},
    onOpenPlayer: () -> Unit
) {

    AnimatedVisibility(
        visible = visible
    ) {

        val isLight =
            AppTheme.themeMode == ThemeMode.Light

        val containerColor = when (
            AppTheme.themeMode
        ) {

            ThemeMode.Light ->
                Color(0xFFFFFCF7)

            ThemeMode.Dark ->
                Color(0xFF141414)

            ThemeMode.Amoled ->
                Color.Black
        }

        val gradientBrush = when (
            AppTheme.themeMode
        ) {

            ThemeMode.Light -> {

                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF8EE),
                        Color(0xFFF6EBDD)
                    )
                )
            }

            ThemeMode.Dark -> {

                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF111111)
                    )
                )
            }

            ThemeMode.Amoled -> {

                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0B0B0B),
                        Color.Black
                    )
                )
            }
        }

        val titleColor = when (
            AppTheme.themeMode
        ) {

            ThemeMode.Light ->
                Primary

            else ->
                Color.White
        }

        val subtitleColor =
            titleColor.copy(alpha = 0.64f)

        val borderColor = when (
            AppTheme.themeMode
        ) {

            ThemeMode.Light ->
                Color(0xFFE9DDCC)

            else ->
                Color.White.copy(alpha = 0.06f)
        }

        val buttonBg = when (
            AppTheme.themeMode
        ) {

            ThemeMode.Light ->
                Color.White

            ThemeMode.Dark ->
                Color(0xFF1E1E1E)

            ThemeMode.Amoled ->
                Color(0xFF121212)
        }

        val progressColor by animateColorAsState(

            targetValue =

                if (
    playback.isPlaying
)
                    Accent
                else
                    Gold,

            label = ""
        )

        val progress = remember(
            playback.positionMs,
            playback.durationMs
        ) {

            if (playback.durationMs <= 0L) {

                0f

            } else {

                (
                    playback.positionMs.toFloat() /
                        playback.durationMs.toFloat()
                ).coerceIn(0f, 1f)
            }
        }

        val progressLabel = remember(
            bani?.id,
            currentLine,
            totalLines,
            displayLanguage
        ) {

            if (bani != null) {

                val theme =
                    resolveBaniTheme(
                        bani.title
                    )

                val progressInfo =
                    calculateBaniProgress(
                        bani.title,
                        bani.lines,
                        currentLine
                    )

                if (progressInfo != null) {

                    val localizedAng =
                        theme.ang.translateAng(
                            displayLanguage
                        )

                    " • $localizedAng • ${
                        progressInfo.getLocalizedLabel(
                            displayLanguage
                        )
                    }"

                } else {

                    val lineNum =
                        (
                            currentLine + 1
                        ).coerceAtLeast(1)

                    val total =
                        bani.lines.size

                    " • ${
                        theme.ang.translateAng(
                            displayLanguage
                        )
                    } • ${
                        "Line".translate(
                            displayLanguage
                        )
                    } $lineNum/$total"
                        .translateDigits(
                            displayLanguage
                        )
                }

            } else {

                val lineNum =
                    (
                        currentLine + 1
                    ).coerceAtLeast(1)

                " • ${
                    "Line".translate(
                        displayLanguage
                    )
                } $lineNum/$totalLines"
                    .translateDigits(
                        displayLanguage
                    )
            }
        }
        LaunchedEffect(playback.isLoading, playback.isReady) {

            println(
                "UI PLAYBACK -> " +
                        "loading=${playback.isLoading}, " +
                        "ready=${playback.isReady}"
            )
        }
               Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(78.dp)
                // FIX: Cleared raw y-axis offsets that were causing compilation bleed 
                // and alignment tracking calculation bugs in parent layouts
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(
                        topStart = 26.dp,
                        topEnd = 26.dp
                    )
                ),
            shape = RoundedCornerShape(
                topStart = 26.dp,
                topEnd = 26.dp
            ),
            color = containerColor,
            border = BorderStroke(
                1.dp,
                borderColor
            ),
            tonalElevation = 0.dp
        )  {

            Column(

                modifier = Modifier
                    .background(gradientBrush)
            ) {

                /*
                 * PROGRESS BAR
                 */

                var sliderPosition by remember {
                    mutableFloatStateOf(progress)
                }

                var isDragging by remember {
                    mutableStateOf(false)
                }

                LaunchedEffect(progress) {
                    if (!isDragging) {
                        sliderPosition = progress
                    }
                }

                Slider(
                    value = sliderPosition,
                    onValueChange = {
                        isDragging = true
                        sliderPosition = it
                    },
                    onValueChangeFinished = {
                        isDragging = false
                        onSeekTo(
                            (sliderPosition * playback.durationMs).toLong()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp), // smaller touch area

                    thumb = {
                        Box(
                            Modifier
                                .size(4.dp)
                                .background(
                                    progressColor,
                                    CircleShape
                                )
                        )
                    },

                    track = { sliderState ->
                        SliderDefaults.Track(
                            sliderState = sliderState,
                            drawStopIndicator = null,
                            thumbTrackGapSize = 0.dp,

                            modifier = Modifier.height(6.dp), // Spotify style

                            colors = SliderDefaults.colors(
                                activeTrackColor = progressColor,
                                inactiveTrackColor =
                                    if (isLight)
                                        Color(0xFFE7D7C1)
                                    else
                                        Color.White.copy(alpha = 0.08f)
                            )
                        )
                    }
                )

                /*
                 * MAIN CONTENT
                 */

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(74.dp)
                        .animateContentSize()
                        .padding(
                            start = 12.dp,
                            end = 8.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    /*
                     * PLAY BUTTON
                     */

                    val rotateAnim =
                        if (
                                playback.isPlaying
                            ) {

                            val infiniteTransition =
                                rememberInfiniteTransition(
                                    label = ""
                                )

                            infiniteTransition.animateFloat(

                                initialValue = 0f,

                                targetValue = 360f,

                                animationSpec =
                                    infiniteRepeatable(

                                        animation = tween(
                                            durationMillis = 9000,
                                            easing = LinearEasing
                                        )
                                    ),

                                label = ""
                            ).value

                        } else {
                            0f
                        }

                    val pulseAnim =
                        if (
    playback.isPlaying
) {

                            val infiniteTransition =
                                rememberInfiniteTransition(
                                    label = ""
                                )

                            infiniteTransition.animateFloat(

                                initialValue = 0.94f,

                                targetValue = 1.04f,

                                animationSpec =
                                    infiniteRepeatable(

                                        animation = tween(
                                            durationMillis = 1600,
                                            easing = EaseInOut
                                        ),

                                        repeatMode =
                                            RepeatMode.Reverse
                                    ),

                                label = ""
                            ).value

                        } else {
                            1f
                        }

                    Box(

                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(

                                Brush.radialGradient(
                                    listOf(
                                        Accent,
                                        Gold
                                    )
                                )
                            )
                            .clickable(

                                indication = null,

                                interactionSource =
                                    remember {
                                        MutableInteractionSource()
                                    }

                            ) {
                                onPlayPause()
                            },

                        contentAlignment =
                            Alignment.Center
                    ) {

                        if (
                                playback.isPlaying
                            ) {

                            Box(

                                modifier = Modifier
                                    .size(68.dp)
                                    .rotate(rotateAnim)
                                    .scale(pulseAnim)
                                    .background(

                                        brush = Brush.sweepGradient(

                                            listOf(

                                                Accent.copy(alpha = 0.04f),

                                                Gold.copy(alpha = 0.12f),

                                                Accent.copy(alpha = 0.04f)
                                            )
                                        ),

                                        shape =
                                            RoundedCornerShape(24.dp)
                                    )
                            )
                        }

                        Box(

                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
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

                            if (!playback.isReady && playback.isLoading){

                                CircularProgressIndicator(

                                    modifier =
                                        Modifier.size(18.dp),

                                    color = Color.White,

                                    strokeWidth = 2.dp
                                )

                            } else {

                                Icon(

                                    imageVector =

                                        if (
    playback.isPlaying
)
                                            Icons.Default.Pause
                                        else
                                            Icons.Default.PlayArrow,

                                    contentDescription =

                                      if (
    playback.isPlaying
)
                                            "Pause"
                                        else
                                            "Play",

                                    tint = Color.White,

                                    modifier =
                                        Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(
                        Modifier.width(12.dp)
                    )

                    /*
                     * CENTER CONTENT
                     */

                    Column(

                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = null,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                }
                            ) {
                                onOpenPlayer()
                            }
                            .padding(end = 4.dp),

                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        /*
                         * TITLE
                         */

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                           if (
    playback.isPlaying
){

                                Box(

                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Accent)
                                )

                                Spacer(
                                    Modifier.width(5.dp)
                                )
                            }

                            Text(

                                text =
                                    (bani?.title ?: "Nitnem")
                                        .translate(displayLanguage),

                                color = titleColor,

                                fontWeight =
                                    FontWeight.Bold,

                                fontSize = 13.sp,

                                maxLines = 1,

                                overflow =
                                    TextOverflow.Ellipsis
                            )
                        }

                        Spacer(
                            Modifier.height(1.dp)
                        )

                        /*
                         * TIME + PROGRESS
                         */

                        Row(

                            modifier = Modifier.fillMaxWidth(),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(

                                text =
                                    "${formatTime(playback.positionMs)} / ${
                                        formatTime(playback.durationMs)
                                    }",

                                color = subtitleColor,

                                fontSize = 10.sp,

                                fontWeight =
                                    FontWeight.SemiBold,

                                maxLines = 1
                            )

                            Spacer(
                                Modifier.width(6.dp)
                            )

                            Text(

                                text = progressLabel,

                                modifier = Modifier.weight(1f),

                                color =
                                    subtitleColor.copy(
                                        alpha = 0.80f
                                    ),

                                fontSize = 8.sp,

                                maxLines = 1,

                                overflow =
                                    TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(
                        Modifier.width(4.dp)
                    )

                    /*
                     * ACTIONS
                     */

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Playback Speed Toggle Button (e.g. 1.0x -> 1.25x -> 1.5x -> 2.0x -> 0.75x)
                        val speedText = if (playback.speed == 1f) "1x" else if (playback.speed == 1.25f) "1.2x" else if (playback.speed == 1.5f) "1.5x" else if (playback.speed == 2f) "2x" else "${playback.speed}x"
                        Surface(
                            modifier = Modifier.size(28.dp),
                            onClick = onSpeedToggle,
                            shape = CircleShape,
                            color = if (playback.speed != 1.0f) Accent.copy(alpha = 0.16f) else buttonBg,
                            border = BorderStroke(
                                1.dp,
                                if (AppTheme.themeMode == ThemeMode.Light) Color(0xFFECE2D4) else Color.White.copy(alpha = 0.05f)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = speedText,
                                    color = if (playback.speed != 1.0f) Accent else titleColor.copy(alpha = 0.75f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Repeat Mode Cycle Button (OFF -> ONE -> ALL -> OFF)
                        val isRepeatActive = repeatMode != "OFF"
                        val repeatIcon = when (repeatMode) {
                            "ONE" -> Icons.Default.RepeatOne
                            "ALL" -> Icons.Default.Repeat
                            else -> Icons.Outlined.Repeat
                        }
                        MiniPlayerIconButton(
                            icon = repeatIcon,
                            containerColor = if (isRepeatActive) Accent.copy(alpha = 0.14f) else buttonBg,
                            iconTint = if (isRepeatActive) Accent else titleColor.copy(alpha = 0.72f),
                            contentDescription = "Repeat: $repeatMode",
                            onClick = onRepeatToggle
                        )

                        MiniPlayerIconButton(
                            icon = Icons.Default.Replay10,
                            containerColor = buttonBg,
                            iconTint = titleColor.copy(alpha = 0.72f),
                            contentDescription = "Back 10 seconds",
                            enabled = playback.isReady && !playback.isLoading,
                            onClick = onSeekBack
                        )

                        MiniPlayerIconButton(
                            icon = Icons.Default.Forward10,
                            containerColor = buttonBg,
                            iconTint = titleColor.copy(alpha = 0.72f),
                            contentDescription = "Forward 10 seconds",
                            enabled = playback.isReady && !playback.isLoading,
                            onClick = onSeekForward
                        )

                        MiniPlayerIconButton(
                            icon = if (bookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            containerColor = if (bookmarked) Gold.copy(alpha = 0.16f) else buttonBg,
                            iconTint = if (bookmarked) Gold else titleColor.copy(alpha = 0.72f),
                            contentDescription = "Bookmark",
                            onClick = onBookmark
                        )

                        MiniPlayerIconButton(
                            icon = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            containerColor = if (isMuted) Gold.copy(alpha = 0.16f) else buttonBg,
                            iconTint = if (isMuted) Gold else titleColor.copy(alpha = 0.72f),
                            contentDescription = "Mute Toggle",
                            onClick = onMuteToggle
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun MiniPlayerIconButton(

    icon: ImageVector,

    containerColor: Color,

    iconTint: Color,

    contentDescription: String,

    enabled: Boolean = true,

    onClick: () -> Unit
) {

    Surface(

        modifier = Modifier.size(28.dp),

        onClick = {
            if (enabled) {
                onClick()
            }
        },

        enabled = enabled,

        shape = CircleShape,

        color =
            if (enabled) {
                containerColor
            } else {
                containerColor.copy(alpha = 0.4f)
            },

        border = BorderStroke(

            1.dp,

            if (
                AppTheme.themeMode ==
                ThemeMode.Light
            ) {

                Color(0xFFECE2D4)

            } else {

                Color.White.copy(alpha = 0.05f)
            }
        )
    ) {

        Box(
            contentAlignment = Alignment.Center
        ) {

            Icon(

                imageVector = icon,

                contentDescription = contentDescription,

                tint =
                    if (enabled) {
                        iconTint
                    } else {
                        iconTint.copy(alpha = 0.4f)
                    },

                modifier = Modifier.size(14.dp)
            )
        }
    }
}
private fun formatTime(
    ms: Long
): String {

    val totalSeconds =
        (ms / 1000)
            .coerceAtLeast(0)

    val hours =
        totalSeconds / 3600

    val minutes =
        (totalSeconds % 3600) / 60

    val seconds =
        totalSeconds % 60

    return if (hours > 0) {

        String.format(
            "%d:%02d:%02d",
            hours,
            minutes,
            seconds
        )

    } else {

        String.format(
            "%d:%02d",
            minutes,
            seconds
        )
    }
}