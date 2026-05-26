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

@Composable
fun ThinMiniPlayer(

    modifier: Modifier = Modifier,

    bani: Bani?,

    playback: PlaybackSnapshot,

    currentLine: Int = 0,

    totalLines: Int = 0,

    visible: Boolean = true,

    repeatEnabled: Boolean = false,

    bookmarked: Boolean = false,

    displayLanguage: DisplayLanguage = DisplayLanguage.English,

    onPlayPause: () -> Unit,

    onSeekForward: () -> Unit,

    onSeekBack: () -> Unit,

    onRepeatLine: () -> Unit,

    onBookmark: () -> Unit = {},

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

                if (playback.isPlaying)
                    Accent
                else
                    Gold,

            label = ""
        )

        Surface(

            modifier = modifier
                .fillMaxWidth()
                .height(78.dp)
                .offset(y = 2.dp)
                .scale(

                    if (playback.isPlaying)
                        1f
                    else
                        0.985f
                )
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(
                        topStart = 26.dp,
                        topEnd = 26.dp
                    )
                )
                .clickable {
                    onOpenPlayer()
                },

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
        ) {

            Column(

                modifier = Modifier
                    .background(gradientBrush)
            ) {

                /*
                 * PROGRESS BAR
                 */

                LinearProgressIndicator(

                    progress = {
                        playback.progress
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),

                    color = progressColor,

                    trackColor =

                        if (isLight)
                            Color(0xFFE7D7C1)
                        else
                            Color.White.copy(alpha = 0.08f)
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

                    val infiniteTransition =
                        rememberInfiniteTransition()

                    val rotateAnim by infiniteTransition.animateFloat(

                        initialValue = 0f,

                        targetValue = 360f,

                        animationSpec = infiniteRepeatable(

                            animation = tween(
                                durationMillis = 9000,
                                easing = LinearEasing
                            )
                        ),

                        label = ""
                    )

                    val pulseAnim by infiniteTransition.animateFloat(

                        initialValue = 0.94f,

                        targetValue = 1.04f,

                        animationSpec = infiniteRepeatable(

                            animation = tween(
                                durationMillis = 1600,
                                easing = EaseInOut
                            ),

                            repeatMode = RepeatMode.Reverse
                        ),

                        label = ""
                    )

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

                        if (playback.isPlaying) {

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

                            if (playback.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(

                                    imageVector =

                                        if (playback.isPlaying)
                                            Icons.Default.Pause
                                        else
                                            Icons.Default.PlayArrow,

                                    contentDescription = null,

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
                            .padding(end = 4.dp),

                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        val progressLabel = remember(
                            bani,
                            currentLine,
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

                                    val localizedAng = theme.ang.translateAng(displayLanguage)
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

                                    " • ${theme.ang.translateAng(displayLanguage)} • ${"Line".translate(displayLanguage)} $lineNum/$total".translateDigits(displayLanguage)
                                }

                            } else {

                                val lineNum =
                                    (
                                        currentLine + 1
                                    ).coerceAtLeast(1)

                                " • ${"Line".translate(displayLanguage)} $lineNum/$totalLines".translateDigits(displayLanguage)
                            }
                        }
/*
 * TITLE
 */

Row(

    modifier = Modifier.fillMaxWidth(),

    verticalAlignment =
        Alignment.CenterVertically
) {

    if (playback.isPlaying) {

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

                        Spacer(
                            Modifier.height(1.dp)
                        )

                    }

                    Spacer(
                        Modifier.width(4.dp)
                    )

                    /*
                     * ACTIONS
                     */

                    Row(

                        horizontalArrangement =
                            Arrangement.spacedBy(3.dp),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        MiniPlayerIconButton(

                            icon =

                                if (repeatEnabled)
                                    Icons.Default.Repeat
                                else
                                    Icons.Outlined.Repeat,

                            containerColor =

                                if (repeatEnabled)
                                    Accent.copy(alpha = 0.14f)
                                else
                                    buttonBg,

                            iconTint =

                                if (repeatEnabled)
                                    Accent
                                else
                                    titleColor.copy(alpha = 0.72f),

                            onClick =
                                onRepeatLine
                        )

                        MiniPlayerIconButton(

                            icon =
                                Icons.Default.Replay10,

                            containerColor =
                                buttonBg,

                            iconTint =
                                titleColor.copy(alpha = 0.72f),

                            onClick =
                                onSeekBack
                        )

                        MiniPlayerIconButton(

                            icon =
                                Icons.Default.Forward10,

                            containerColor =
                                buttonBg,

                            iconTint =
                                titleColor.copy(alpha = 0.72f),

                            onClick =
                                onSeekForward
                        )

                        MiniPlayerIconButton(

                            icon =

                                if (bookmarked)
                                    Icons.Default.Bookmark
                                else
                                    Icons.Outlined.BookmarkBorder,

                            containerColor =

                                if (bookmarked)
                                    Gold.copy(alpha = 0.16f)
                                else
                                    buttonBg,

                            iconTint =

                                if (bookmarked)
                                    Gold
                                else
                                    titleColor.copy(alpha = 0.72f),

                            onClick =
                                onBookmark
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

    onClick: () -> Unit
) {

    Surface(

        modifier = Modifier.size(28.dp),

        onClick = onClick,

        shape = CircleShape,

        color = containerColor,

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

            contentAlignment =
                Alignment.Center
        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = iconTint,

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

private val PlaybackSnapshot.progress: Float

    get() =

        if (durationMs <= 0L)

            0f

        else

            (
                positionMs.toFloat() /
                    durationMs.toFloat()
            ).coerceIn(0f, 1f)