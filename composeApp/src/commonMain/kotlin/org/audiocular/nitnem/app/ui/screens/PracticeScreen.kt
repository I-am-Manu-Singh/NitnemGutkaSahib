
package org.audiocular.nitnem.app.ui.screens

import org.audiocular.nitnem.app.ui.practice.SimranAbhyasTab
import org.audiocular.nitnem.app.ui.practice.AiGurbaniQuizTab
import org.audiocular.nitnem.app.ui.practice.BaniAiAssistantTab
import org.audiocular.nitnem.app.ui.practice.practiceColors
import org.audiocular.nitnem.app.ui.practice.PracticeColors
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
import org.audiocular.nitnem.audio.StreamAudioController
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.NitnemUiState
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate


enum class PracticeTab(
    val title: String,
    val icon: ImageVector
) {
    Quiz("AI Quiz", Icons.Default.AutoStories),
    AiAssist("Bani AI", Icons.Default.SmartToy),
    Meditate("Meditate", Icons.Default.SelfImprovement)
}


/*
 * ─────────────────────────────────────────────────────────────
 * MAIN SCREEN
 * ─────────────────────────────────────────────────────────────
 */
@Composable
fun PracticeScreen(
    audioController: StreamAudioController,
    bani: Bani,
    displayLanguage: DisplayLanguage,
    uiState: NitnemUiState,
    viewModel: NitnemViewModel
) {

    val context =
        LocalContext.current

    val colors =
        practiceColors()

    var tab by rememberSaveable {
        mutableStateOf(
            PracticeTab.Quiz
        )
    }

    var compactHeader by rememberSaveable {
        mutableStateOf(false)
    }

    /*
     * DYNAMIC HEADER CONTENT
     */

    val headerTitle = when (tab) {

        PracticeTab.Quiz -> {
            tab.title
        }

        PracticeTab.AiAssist -> {
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

        PracticeTab.AiAssist -> {
            "Ask Bani anything about Gurbani"
        }

        PracticeTab.Meditate -> {
            "Naam Simran • Breath • Stillness"
        }
    }

    Box(

        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            /*
             * ─────────────────────────────────────────────
             * PREMIUM HEADER
             * ─────────────────────────────────────────────
             */

            Surface(

    modifier = Modifier.fillMaxWidth(),

    shape = RoundedCornerShape(
        bottomStart = 28.dp,
        bottomEnd = 28.dp
    ),

    color = SurfaceColor.copy(alpha = 0.96f),

    tonalElevation = 6.dp,

    shadowElevation = 0.dp
){

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    /*
                     * HEADER CONTENT
                     */

                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                compactHeader =
                                    !compactHeader
                            }
                            .padding(
                                horizontal = 18.dp,
                                vertical = 14.dp
                            ),

                        verticalAlignment =
                            Alignment.CenterVertically
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
                                    modifier = Modifier.padding(top = 2.dp)
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
                                    .padding(
                                        horizontal = 12.dp
                                    ),

                                selectedTab = tab,

                                colors = colors,

                                displayLanguage = displayLanguage,

                                onTabSelected = {
                                    tab = it
                                }
                            )

                            Spacer(
                                Modifier.height(14.dp)
                            )
                        }
                    }
                }
            }

           Spacer(
    Modifier.height(8.dp)
)

            /*
             * ─────────────────────────────────────────────
             * TAB CONTENT
             * ─────────────────────────────────────────────
             */

            AnimatedContent(

                targetState = tab,

                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
transitionSpec = {

    fadeIn(
        animationSpec = tween(
            durationMillis = 220
        )
    ).togetherWith(

        fadeOut(
            animationSpec = tween(
                durationMillis = 180
            )
        )
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

                        PracticeTab.AiAssist -> {

                            BaniAiAssistantTab(
                                bani = bani,
                                colors = colors,
                                displayLanguage = displayLanguage,
                                uiState = uiState
                            )
                        }

                        PracticeTab.Meditate -> {

                            SimranAbhyasTab(
                                colors = colors,
                                displayLanguage =
                                    displayLanguage,
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
