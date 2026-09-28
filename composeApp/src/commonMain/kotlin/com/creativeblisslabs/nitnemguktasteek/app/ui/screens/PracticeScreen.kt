
package com.creativeblisslabs.nitnemguktasteek.app.ui.screens

import com.creativeblisslabs.nitnemguktasteek.app.ui.practice.SimranAbhyasTab
import com.creativeblisslabs.nitnemguktasteek.app.ui.practice.AiGurbaniQuizTab
import com.creativeblisslabs.nitnemguktasteek.app.ui.practice.BaniAiAssistantTab
import com.creativeblisslabs.nitnemguktasteek.app.ui.practice.FamilyLeaderboardTab
import com.creativeblisslabs.nitnemguktasteek.app.ui.practice.practiceColors
import com.creativeblisslabs.nitnemguktasteek.app.ui.practice.PracticeColors
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.AnimatedVisibility
import com.creativeblisslabs.nitnemguktasteek.audio.StreamAudioController
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState
import com.creativeblisslabs.nitnemguktasteek.viewmodel.NitnemViewModel
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay


enum class PracticeTab(
    val title: String,
    val icon: ImageVector
) {
    Quiz("AI Quiz", Icons.Default.AutoStories),
    Meditate("Meditate", Icons.Default.SelfImprovement)
}


@Composable
fun PracticeScreen(
    audioController: StreamAudioController,
    bani: Bani,
    displayLanguage: DisplayLanguage,
    uiState: NitnemUiState,
    viewModel: NitnemViewModel
) {
    val context = LocalContext.current
    val colors = practiceColors()

    var tab by rememberSaveable {
        mutableStateOf(
            PracticeTab.Quiz
        )
    }

    var compactHeader by rememberSaveable {
        mutableStateOf(false)
    }

    // Auto-hide top header in Meditate tab when fullScreenReadingMode is enabled
    var showLocalChrome by remember { mutableStateOf(true) }
    var localInteractionTick by remember { mutableStateOf(0) }
    LaunchedEffect(uiState.fullScreenReadingMode, tab, localInteractionTick) {
        if (uiState.fullScreenReadingMode && tab == PracticeTab.Meditate) {
            showLocalChrome = true
            delay(4_000L)
            showLocalChrome = false
        } else {
            showLocalChrome = true
        }
    }

    /*
     * DYNAMIC HEADER CONTENT
     */

    val headerTitle = when (tab) {
        PracticeTab.Quiz -> {
            tab.title
        }
        PracticeTab.Meditate -> {
            tab.title
        }
    }

    val headerSubtitle = when (tab) {
        PracticeTab.Quiz -> {
            "Practice Gurbani Through Interactive Questions"
        }
        PracticeTab.Meditate -> {
            "Naam Simran • Breath • Stillness"
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        val isLandscape = maxWidth > maxHeight && maxWidth > 600.dp

        if (isLandscape) {
            // ── LANDSCAPE DUAL-PANE SPLIT ──
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Pane: Title, Subtitle, Stats, and Vertical Tab Switcher
                Column(
                    modifier = Modifier
                        .weight(0.36f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Single Row containing Header Texts on the left and Stats on the right
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Header Texts (Takes up remaining horizontal space dynamically)
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = headerTitle.translate(displayLanguage),
                                color = Primary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = headerSubtitle.translate(displayLanguage),
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp)) // Safe spacing between text and stats

                        // 2. Hearts and XP stats (Clean, single-row badge wrapper)
                        val activeSession = uiState.activeQuizSession
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Primary.copy(alpha = 0.05f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Hearts Container
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    val currentHearts = activeSession?.hearts ?: 5
                                    repeat(5) { index ->
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = if (index < currentHearts) Color(0xFFE53935) else colors.textSecondary.copy(alpha = 0.22f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                
                                // XP Text Counter
                                Text(
                                    text = "${uiState.totalXp} XP",
                                    color = colors.accent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Vertical Tab Selector
                    PracticeTab.entries.forEach { practiceTab ->
                        val selected = tab == practiceTab
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = if (selected) Gold.copy(alpha = 0.16f) else SurfaceColor,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selected) Gold.copy(alpha = 0.45f) else Gold.copy(alpha = 0.08f)
                            ),
                            onClick = { tab = practiceTab }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = practiceTab.icon,
                                    contentDescription = null,
                                    tint = if (selected) Gold else TextPrimary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = practiceTab.title.translate(displayLanguage),
                                    color = if (selected) Gold else TextPrimary,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // Right Pane: Active Tab Content
                Box(
                    modifier = Modifier
                        .weight(0.64f)
                        .fillMaxHeight()
                ) {
                    AnimatedContent(
                        targetState = tab,
                        modifier = Modifier.fillMaxSize(),
                        transitionSpec = {
                            fadeIn(animationSpec = tween(durationMillis = 220)).togetherWith(
                                fadeOut(animationSpec = tween(durationMillis = 180))
                            )
                        },
                        label = "PracticeTabsLandscape"
                    ) { selected ->
                        when (selected) {
                            PracticeTab.Quiz -> {
                                AiGurbaniQuizTab(
                                    bani = bani,
                                    colors = colors,
                                    displayLanguage = displayLanguage,
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                            }
                            PracticeTab.Meditate -> {
                                SimranAbhyasTab(
                                    colors = colors,
                                    displayLanguage = displayLanguage,
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ── PORTRAIT LAYOUT ──
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                /*
                 * ─────────────────────────────────────────────
                 * PREMIUM HEADER
                 * ─────────────────────────────────────────────
                 */

                AnimatedVisibility(
                    visible = showLocalChrome,
                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { -it },
                    exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically { -it }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(
                            bottomStart = 28.dp,
                            bottomEnd = 28.dp
                        ),
                        color = SurfaceColor.copy(alpha = 0.96f),
                        tonalElevation = 6.dp,
                        shadowElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            /*
                             * HEADER CONTENT
                             */
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { compactHeader = !compactHeader }
                                    .padding(horizontal = 18.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val activeSession = uiState.activeQuizSession

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = headerTitle.translate(displayLanguage),
                                        color = Primary,
                                        fontSize = if (compactHeader) 17.sp else 25.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    AnimatedVisibility(
                                        visible = !compactHeader
                                    ) {
                                        Text(
                                            text = headerSubtitle.translate(displayLanguage),
                                            color = TextSecondary,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            modifier = Modifier.padding(top = 2.dp, end = 6.dp)
                                        )
                                    }
                                }

                                // Hearts and XP overlay for AI Quiz top bar
                                if (tab == PracticeTab.Quiz && activeSession != null && !activeSession.quizFinished) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(end = 12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Hearts
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            repeat(5) { index ->
                                                Icon(
                                                    imageVector = Icons.Default.Favorite,
                                                    contentDescription = null,
                                                    tint = if (index < activeSession.hearts) Color(0xFFE53935) else colors.textSecondary.copy(alpha = 0.22f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        // XP Badge
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = Color.Transparent
                                        ) {
                                            Text(
                                                text = "${uiState.totalXp} XP",
                                                color = colors.accent,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = Gold.copy(alpha = 0.12f)
                                ) {
                                    Box(
                                        modifier = Modifier.size(34.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (compactHeader) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                            contentDescription = null,
                                            tint = Gold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            /*
                             * TAB SWITCHER
                             */
                            AnimatedVisibility(
                                visible = !compactHeader
                            ) {
                                Column {
                                    PracticeTabSwitcher(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp),
                                        selectedTab = tab,
                                        colors = colors,
                                        displayLanguage = displayLanguage,
                                        onTabSelected = { tab = it }
                                    )

                                    Spacer(Modifier.height(14.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                /*
                 * ─────────────────────────────────────────────
                 * TAB CONTENT
                 * ─────────────────────────────────────────────
                 */
                AnimatedContent(
                    targetState = tab,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .pointerInput(uiState.fullScreenReadingMode, tab) {
                            if (uiState.fullScreenReadingMode && tab == PracticeTab.Meditate) {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitPointerEvent()
                                        localInteractionTick++
                                    }
                                }
                            }
                        },
                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(durationMillis = 220)
                        ).togetherWith(
                            fadeOut(animationSpec = tween(durationMillis = 180))
                        )
                    },
                    label = "PracticeTabs"
                ) { selected ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        when (selected) {
                            PracticeTab.Quiz -> {
                                AiGurbaniQuizTab(
                                    bani = bani,
                                    colors = colors,
                                    displayLanguage = displayLanguage,
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                            }
                            PracticeTab.Meditate -> {
                                SimranAbhyasTab(
                                    colors = colors,
                                    displayLanguage = displayLanguage,
                                    uiState = uiState,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
    
@Composable
fun PracticeTabSwitcher(
    modifier: Modifier = Modifier,
    selectedTab: PracticeTab,
    colors: PracticeColors,
    displayLanguage: DisplayLanguage,
    onTabSelected: (PracticeTab) -> Unit
) {

    val tabs =
        PracticeTab.entries

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
             * ACTIVE PILL
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

                                imageVector =
                                    tab.icon,

                                contentDescription = null,

                                tint = contentColor,

                                modifier =
                                    Modifier.size(16.dp)
                            )

                            Spacer(
                                Modifier.width(6.dp)
                            )

                            Text(

                                text = tab.title.translate(displayLanguage),

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
