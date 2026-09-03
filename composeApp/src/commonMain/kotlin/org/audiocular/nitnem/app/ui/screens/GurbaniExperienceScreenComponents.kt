package org.audiocular.nitnem.app.ui.screens

import android.widget.Toast
import org.audiocular.nitnem.utils.timingKey
import androidx.compose.animation.core.animateDpAsState
import org.audiocular.nitnem.app.models.ExperienceTab
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.audiocular.nitnem.audio.StreamAudioController
import org.audiocular.nitnem.app.sync.TimingLoader
import org.audiocular.nitnem.app.ui.components.GurbaniLineCard
import org.audiocular.nitnem.app.ui.components.GurbaniLineUiConfig
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.app.notifications.rememberNotificationBridge
import org.audiocular.nitnem.audiosync.AudioLineSynchronizer
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateDigits
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.WindowInsets
import org.audiocular.nitnem.app.ui.components.GurbaniMeaningSheet
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.lazy.LazyListState
import org.audiocular.nitnem.data.CuratedAudioCatalog
import androidx.compose.ui.input.pointer.pointerInput


@Composable
fun EmptyBookmarks(displayLanguage: DisplayLanguage) {

    Box(

        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),

        contentAlignment =
            Alignment.Center
    ) {

        Column(

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(

                imageVector =
                    Icons.Filled.Bookmarks,

                contentDescription = null,

                tint =
                    TextPrimary.copy(alpha = 0.25f),

                modifier =
                    Modifier.size(56.dp)
            )

            Spacer(
                Modifier.height(16.dp)
            )

          Text(

    text =
        "No bookmarks yet".translate(displayLanguage),

    color =
        TextPrimary.copy(alpha = 0.4f),

    fontSize = 17.sp
)
        }
    }
}

@Composable
fun PremiumTabSwitcher(
    modifier: Modifier = Modifier,
    selectedTab: ExperienceTab,
    playbackActive: Boolean,
    displayLanguage: DisplayLanguage,
    onTabSelected: (ExperienceTab) -> Unit
) {

    val tabs =
        ExperienceTab.entries

    val selectedIndex =
        tabs.indexOf(selectedTab)

    val containerColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Primary.copy(alpha = 0.06f)
        }

        ThemeMode.Dark -> {
            Color.White.copy(alpha = 0.08f)
        }

        ThemeMode.Amoled -> {
            Color.White.copy(alpha = 0.05f)
        }
    }

    Surface(

        modifier = modifier,

        shape = RoundedCornerShape(100.dp),

        color = containerColor
    ) {

        BoxWithConstraints(

            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(4.dp)
        ) {

            val tabWidth =
                maxWidth / tabs.size

            val animatedOffset by animateDpAsState(

                targetValue =
                    tabWidth * selectedIndex,

                label = ""
            )

            /*
             * SLIDING ACTIVE PILL
             */
            Box(

                modifier = Modifier
                    .offset(x = animatedOffset)
                    .width(tabWidth)
                    .fillMaxHeight()
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(

                        Brush.horizontalGradient(

                            listOf(

                                Gold.copy(alpha = 0.22f),

                                Gold.copy(alpha = 0.14f)
                            )
                        )
                    )
            )

            /*
             * TAB ROW
             */
            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                tabs.forEach { tab ->

                    val selected =
                        selectedTab == tab

                    val contentColor by animateColorAsState(

                        targetValue =

                            if (selected)
                                Gold
                            else
                                TextPrimary.copy(alpha = 0.60f),

                        label = ""
                    )

                    val icon = when (tab) {

                        ExperienceTab.Read ->
                            Icons.Default.MenuBook

                        ExperienceTab.Listen ->

                            if (playbackActive)
                                Icons.Default.GraphicEq
                            else
                                Icons.Default.PlayCircleFilled

                        ExperienceTab.Bookmarks ->
                            Icons.Default.Bookmark
                    }

                    Box(

                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(100.dp))
                            .clickable {
                                onTabSelected(tab)
                            },

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Row(

                            horizontalArrangement =
                                Arrangement.Center,

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Icon(

                                imageVector = icon,

                                contentDescription = null,

                                tint = contentColor,

                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(
                                Modifier.width(6.dp)
                            )

                            Text(

                                text = tab.label.translate(displayLanguage),

                                color = contentColor,

                                fontSize = 12.sp,

                                fontWeight =

                                    if (selected)
                                        FontWeight.SemiBold
                                    else
                                        FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SegmentJumpBar(

    segments: List<BaniSegment>,

    currentSegmentIndex: Int,

    listState: LazyListState,

    displayLanguage: DisplayLanguage,

    onJumpToIndex: (Int) -> Unit
) {

    val activeContainer = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Accent.copy(alpha = 0.14f)
        }

        ThemeMode.Dark -> {
            Gold.copy(alpha = 0.14f)
        }

        ThemeMode.Amoled -> {
            Gold.copy(alpha = 0.12f)
        }
    }

    val inactiveContainer = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Color.Black.copy(alpha = 0.04f)
        }

        ThemeMode.Dark -> {
            Color.White.copy(alpha = 0.05f)
        }

        ThemeMode.Amoled -> {
            Color.White.copy(alpha = 0.03f)
        }
    }

    val activeTextColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Accent
        }

        else -> {
            Gold
        }
    }

    val inactiveTextColor = when (AppTheme.themeMode) {

        ThemeMode.Light -> {
            Color(0xFF666666)
        }

        ThemeMode.Dark -> {
            Color.White.copy(alpha = 0.52f)
        }

        ThemeMode.Amoled -> {
            Color.White.copy(alpha = 0.44f)
        }
    }

    LazyRow(

        state = listState,

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 2.dp
            ),

        horizontalArrangement =
            Arrangement.spacedBy(10.dp),

        contentPadding =
            PaddingValues(horizontal = 6.dp)
    ) {

        itemsIndexed(segments) { groupIdx, segment ->

            val isActive =
                currentSegmentIndex == groupIdx

            val animatedContainer by animateColorAsState(

                targetValue =

                    if (isActive)
                        activeContainer
                    else
                        inactiveContainer,

                label = ""
            )

            val animatedTextColor by animateColorAsState(

                targetValue =

                    if (isActive)
                        activeTextColor
                    else
                        inactiveTextColor,

                label = ""
            )

            val animatedScale by animateFloatAsState(

                targetValue =

                    if (isActive)
                        1f
                    else
                        0.94f,

                label = ""
            )

            Surface(

                modifier = Modifier
                    .graphicsLayer {

                        scaleX = animatedScale
                        scaleY = animatedScale
                    }
                    .combinedClickable(

                        onClick = {

                            onJumpToIndex(
                                segment.startIndex
                            )
                        },

                        onDoubleClick = {

                            onJumpToIndex(0)
                        }
                    ),

                shape = RoundedCornerShape(18.dp),

                color = animatedContainer,

                border = BorderStroke(

                    1.dp,

                    if (isActive) {

                        activeTextColor.copy(
                            alpha = 0.16f
                        )

                    } else {

                        Color.Transparent
                    }
                ),

                tonalElevation =

                    if (isActive)
                        2.dp
                    else
                        0.dp
            ) {

                Row(

                    modifier = Modifier
                        .padding(
                            horizontal =

                                if (isActive)
                                    16.dp
                                else
                                    13.dp,

                            vertical = 5.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    /*
                     * ACTIVE DOT
                     */
                    AnimatedVisibility(
                        visible = isActive
                    ) {

                        Box(

                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(activeTextColor)
                        )
                    }

                    Text(

                        text = remember(segment.label, displayLanguage) {
                            val parts = segment.label.split(" ")
                            if (parts.size == 2) {
                                val prefix = parts[0].translate(displayLanguage)
                                "$prefix ${parts[1]}".translateDigits(displayLanguage)
                            } else {
                                segment.label.translate(displayLanguage).translateDigits(displayLanguage)
                            }
                        },

                        fontSize = 12.sp,

                        fontWeight =

                            if (isActive)
                                FontWeight.SemiBold
                            else
                                FontWeight.Medium,

                        color = animatedTextColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FloatingJumpButton(

    icon: androidx.compose.ui.graphics.vector.ImageVector,

    onClick: () -> Unit,

    onDoubleTap: () -> Unit
) {

    Surface(

        modifier = Modifier
            .size(42.dp)
            .combinedClickable(

                onClick = onClick,

                onDoubleClick = onDoubleTap
            ),

        shape = CircleShape,

        color = SurfaceColor,

        tonalElevation = 8.dp,

        shadowElevation = 8.dp,

        border = BorderStroke(
            1.dp,
            Gold.copy(alpha = 0.14f)
        )
    ) {

        Box(
            contentAlignment =
                Alignment.Center
        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = Gold
            )
        }
    }
}