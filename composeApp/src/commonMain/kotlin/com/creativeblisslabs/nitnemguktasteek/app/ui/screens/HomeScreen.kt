package com.creativeblisslabs.nitnemguktasteek.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.draw.drawBehind
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin
import kotlin.math.cos
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.PI
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.creativeblisslabs.nitnemguktasteek.models.NitnemUiState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.clickable
import com.creativeblisslabs.nitnemguktasteek.app.models.AppTab
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.BaniCard
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.HeroCard
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.MascotBani
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.MascotState
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.WeeklyStreakDialog
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.FamilyLeaderboardDialog
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.GurbaniMeaningSheet
import com.creativeblisslabs.nitnemguktasteek.app.ui.components.RollingNumberText
import com.creativeblisslabs.nitnemguktasteek.domain.social.FamilyLeaderboardRepository
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.viewmodel.NitnemViewModel
import com.creativeblisslabs.nitnemguktasteek.app.ui.practice.BaniAiAssistantTab
import com.creativeblisslabs.nitnemguktasteek.app.ui.practice.practiceColors
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.utils.translateDigits
import com.creativeblisslabs.nitnemguktasteek.utils.translateAng
import androidx.compose.ui.text.style.TextAlign
import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.BaniLine
import androidx.compose.runtime.LaunchedEffect
import com.creativeblisslabs.nitnemguktasteek.data.HukamnamaProvider
import com.creativeblisslabs.nitnemguktasteek.audio.StreamAudioController
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NitnemViewModel,
    audioController: StreamAudioController
) {

    val state by viewModel.uiState.collectAsState()

val context = LocalContext.current

    LaunchedEffect(state.hukamnama) {
        if (state.hukamnama == null) {
            val hukamnama =
                HukamnamaProvider.getDailyHukamnama(context)

            viewModel.setHukamnama(hukamnama)
        }
    }
    var showInfoSheet by remember {
        mutableStateOf(false)
    }

    var showWeeklyStreakDialog by remember { mutableStateOf(false) }
    var showLeaderboardDialog by remember { mutableStateOf(false) }
    var showSevaSheet by remember { mutableStateOf(false) }
    var streakToDisplay by remember { mutableStateOf(state.streakDays) }
    val lastStreak = remember { mutableStateOf(state.streakDays) }
    val scope = rememberCoroutineScope()
    val localPrefs = remember { com.creativeblisslabs.nitnemguktasteek.data.preferences.LocalPreferences(context) }

    // Check on first app open each day (or after onboarding) to present streak overview
    LaunchedEffect(state.streakDays) {
        streakToDisplay = state.streakDays
        val hasShownToday = localPrefs.hasShownDailyStreakToday()
        if (!hasShownToday) {
            showWeeklyStreakDialog = true
            localPrefs.markDailyStreakShownToday()
        } else if (state.streakDays > lastStreak.value && state.streakDays > 0 && state.streakDays % 7 == 0) {
            showWeeklyStreakDialog = true
        }
        lastStreak.value = state.streakDays
    }

    var mascotMessage by remember { mutableStateOf<String?>(null) }
    var mascotOverrideState by remember { mutableStateOf<MascotState?>(null) }

    // FIX: AUTOMATIC DISMISSAL TIMER LIFE-CYCLE STEP
    // Watches the message string state; runs a non-blocking coroutine delay to clear text
    LaunchedEffect(mascotMessage) {
        if (mascotMessage != null) {
            kotlinx.coroutines.delay(6000) // Dismisses automatically after exactly 6 seconds
            mascotMessage = null
        }
    }

    LaunchedEffect(mascotOverrideState) {
        if (mascotOverrideState != null) {
            kotlinx.coroutines.delay(5000)
            mascotOverrideState = null
        }
    }

    var selectedInfoBani by remember {
        mutableStateOf<Bani?>(null)
    }
    
    val infoSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val nitnemBaniIds = remember {
        setOf(
            "morning-nitnem",
            "japji-sahib",
            "jaap-sahib",
            "tav-prasad-savaiye",
            "benti-chaupai-sahib",
            "anand-sahib",
            "rehras-sahib",
            "kirtan-sohila",
            "ardas"
        )
    }

    val nitnemBanis = remember(state.banis) {
        state.banis.filter { it.id in nitnemBaniIds }
    }

    val otherBanis = remember(state.banis) {
        state.banis.filter { it.id !in nitnemBaniIds }
    }
   
    val completedNitnemCount by remember(state.completedBanis) {
        derivedStateOf {
            state.completedBanis.count { it in nitnemBaniIds }
        }
    }
    var isMascotOverlayVisible by remember { mutableStateOf(false) }
    var isGlobalSearchVisible by remember { mutableStateOf(false) }
    var globalSearchQuery by remember { mutableStateOf("") }
    var selectedSearchVerseLine by remember { mutableStateOf<BaniLine?>(null) }
    var interactiveInputText by remember { mutableStateOf("") }
    var isAssistantListening by remember { mutableStateOf(false) }
    val overallProgress by remember(
        completedNitnemCount,
        nitnemBanis
    ) {

        derivedStateOf {

            if (nitnemBanis.isEmpty()) {
                0f
            } else {

                (
                    completedNitnemCount.toFloat() /
                        nitnemBanis.size.toFloat()
                ).coerceIn(0f, 1f)
            }
        }
    }

    val currentHour = remember {
        try {
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        } catch (e: Exception) {
            12
        }
    }

     val baseMascotState = remember(state.playback.isPlaying, overallProgress, currentHour) {
        when {
            state.playback.isPlaying -> MascotState.Listening
            overallProgress >= 1f -> MascotState.Celebrating
            currentHour in 3..5 -> MascotState.AmritVela
            currentHour in 6..9 -> MascotState.Reading
            currentHour in 22..23 || currentHour in 0..2 -> MascotState.Sleeping
            else -> MascotState.Happy
        }
    }

    val totalCompletedMinutes by remember(
        state.completedBanis,
        state.banis
    ) {

        derivedStateOf {

            state.banis
                .filter {
                    state.completedBanis.contains(it.id)
                }
                .sumOf {
                    it.estimatedMinutes
                }
        }
    }
   
    val readingValue by remember(
        totalCompletedMinutes
    ) {

        derivedStateOf {

            when {

                totalCompletedMinutes == 0 ->
                    "0m"

                totalCompletedMinutes < 60 ->
                    "${totalCompletedMinutes}m"

                totalCompletedMinutes % 60 == 0 ->
                    "${totalCompletedMinutes / 60}h"

                else -> {

                    val hours =
                        totalCompletedMinutes / 60f

                    "${String.format("%.1f", hours)}h"
                }
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
/*
 * HEADER
 */
Surface(
    modifier = Modifier.fillMaxWidth(), 
    color = Color.Transparent, 
    tonalElevation = 0.dp,
    shadowElevation = 0.dp
) {
    Column {
        // Main Sleek Single Row Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 4.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Mascot
            MascotBani(
                state = mascotOverrideState ?: baseMascotState,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(46.dp)
                    .clickable {
                        isMascotOverlayVisible = true
                        isAssistantListening = true
                    }
            )
            
            // 2. App Title (compact, single row)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ੴ ਸਤਿਨਾਮੁ ਵਾਹਿਗੁਰੂ".translate(state.displayLanguage),
                    color = Gold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = "ਨਿਤਨੇਮ ਗੁਟਕਾ ਸਾਹਿਬ".translate(state.displayLanguage),
                    color = if (AppTheme.themeMode == ThemeMode.Light)
                        Color(0xFF1A1816)
                    else
                        Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Spacer(Modifier.width(6.dp))

            // 3. Stats Pill (XP & Streak) - Tapping XP opens Leaderboard, Tapping Streak opens Weekly Streak
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    // .background(Accent.copy(alpha = 0.08f))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                // XP (Opens Real Sangat Leaderboard & Group Standings)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "XP",
                        tint = Accent,
                        modifier = Modifier.size(13.dp)
                    )
                    RollingNumberText(
                        value = state.totalXp,
                        suffix = " XP",
                        color = Accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(width = 1.dp, height = 10.dp)
                        .background(Accent.copy(alpha = 0.25f))
                )

                // Streak (Opens Weekly Streak & Monthly Heatmap)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable {
                            streakToDisplay = state.streakDays
                            showWeeklyStreakDialog = true
                        }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = Accent,
                        modifier = Modifier.size(13.dp)
                    )
                    RollingNumberText(
                        value = state.streakDays,
                        suffix = " D",
                        color = Accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            // 4. Action Buttons (Search & Info)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { isGlobalSearchVisible = true },
                    shape = CircleShape,
                    color = Accent.copy(alpha = 0.10f)
                ) {
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Global Search",
                            tint = Accent.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Surface(
                    onClick = { showLeaderboardDialog = true },
                    shape = CircleShape,
                    color = Accent.copy(alpha = 0.10f)
                ) {
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = "Show Leaderboard",
                            tint = Accent.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                var sevaScale by remember { mutableStateOf(1f) }
                val animatedSevaScale by animateFloatAsState(
                    targetValue = sevaScale,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = "SevaScaleBounce"
                )

                Surface(
                    onClick = {
                        scope.launch {
                            sevaScale = 0.82f
                            delay(120)
                            sevaScale = 1f
                            showSevaSheet = true
                        }
                    },
                    shape = CircleShape,
                    color = Gold.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f)),
                    modifier = Modifier.scale(animatedSevaScale)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .semantics { contentDescription = "Seva & Patronage Tiers" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ੴ",
                            color = Gold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Surface(
                    onClick = { showInfoSheet = true },
                    shape = CircleShape,
                    color = Accent.copy(alpha = 0.10f)
                ) {
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Show Info",
                            tint = Accent.copy(alpha = 0.85f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // ── TRANSFORMED THOUGHT BUBBLE (Stays underneath the main content) ─────────────────────────────────────
        AnimatedVisibility(
            visible = mascotMessage != null
        ) {
            mascotMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 66.dp, end = 18.dp, top = 2.dp, bottom = 6.dp) // Adjusted to match smaller mascot sizes
                        .clickable { mascotMessage = null },
                    colors = CardDefaults.cardColors(
                        containerColor = if (AppTheme.themeMode == ThemeMode.Light)
                            Color(0xFFFFF9E6)
                        else
                            Color(0xFF2C2720)
                    ),
                    shape = RoundedCornerShape(
                        topStart = 4.dp, topEnd = 16.dp, 
                        bottomStart = 16.dp, bottomEnd = 16.dp
                    ),
                    border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = msg,
                            color = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFF5D4037) else Color(0xFFE0F7FA),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Gold.copy(alpha = 0.6f),
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { mascotMessage = null }
                        )
                    }
                }
            }
        }
    }
}

        /*
         * CONTENT
         */

        if (state.banis.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Gold,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Loading Gurbani...".translate(state.displayLanguage),
                        color = if (AppTheme.themeMode == ThemeMode.Light) Primary else Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } 
            else {

    var selectedTab by remember {
        mutableStateOf(0)
    }

    val displayedBanis =
        if (selectedTab == 0) {
            nitnemBanis
        } else {
            otherBanis
        }


    LazyColumn(

        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 8.dp,
            bottom = 130.dp
        ),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)

    ) {

        /*
         * HERO
         */

        item {

            HeroCard(

                streak = state.streakDays,

                continueTitle =
                    state.currentBani?.title
                        ?: "Japji Sahib",

                progress = overallProgress,

                completedToday =
                    overallProgress >= 1f,

                completedCount =
                    completedNitnemCount,

                totalPaaths =
                    nitnemBanis.size,

                displayLanguage =
                    state.displayLanguage,

                hukamnama =
                    state.hukamnama,

                uiState = state,

                viewModel = viewModel
            )
        }

        /*
         * ANALYTICS
         */

        item {

            val consistencyProgress =
                (
                    state.monthlyConsistencyDays.toFloat() /
                        30f
                ).coerceIn(0f, 1f)

            val currentPlaybackMinutes =
                (
                    state.playback.positionMs /
                        60000L
                ).toInt()

            val listeningMinutes =
                totalCompletedMinutes +
                    currentPlaybackMinutes

            val listeningProgress =
                (
                    listeningMinutes.toFloat() /
                        180f
                ).coerceIn(0f, 1f)

            val listeningValue =
                when {

                    listeningMinutes < 60 -> {
                        "${listeningMinutes}m"
                    }

                    else -> {

                        val hours =
                            listeningMinutes / 60f

                        "${String.format("%.1f", hours)}h"
                    }
                }

            Row(

    modifier = Modifier.fillMaxWidth(),

    horizontalArrangement =
        Arrangement.spacedBy(10.dp)
) {

    PremiumAnalyticsCard(

        modifier = Modifier
            .weight(1f)
            .height(200.dp),

        title =
            "Today"
                .translate(state.displayLanguage),

        value =
            if (nitnemBanis.isEmpty())
                "0"
            else
                "$completedNitnemCount/${nitnemBanis.size}",

        subtitle =
            "Paaths"
                .translate(state.displayLanguage),

        icon =
            Icons.Default.AutoStories,

        type =
            AnalyticsCardType.Today,

        progress =
            overallProgress
    )

    PremiumAnalyticsCard(

        modifier = Modifier
            .weight(1f)
            .height(200.dp),

        title =
            "Listening"
                .translate(state.displayLanguage),

        value =
            listeningValue,

        subtitle =
            "Time"
                .translate(state.displayLanguage),

        icon =
            Icons.Default.Headphones,

        type =
            AnalyticsCardType.Listening,

        progress =
            listeningProgress
    )

    PremiumAnalyticsCard(

        modifier = Modifier
            .weight(1f)
            .height(200.dp),

        title =
            "Consistency"
                .translate(state.displayLanguage),

        value =
            "${state.monthlyConsistencyDays}",

        subtitle =
            "Days"
                .translate(state.displayLanguage),

        icon =
            Icons.Default.LocalFireDepartment,

        type =
            AnalyticsCardType.Consistency,

        progress =
            consistencyProgress
    )
}
        }

        /*
         * TOP SWITCHER
         */

        item {

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),

                horizontalArrangement =
                    Arrangement.Start
            ) {

                Row(

                    modifier =
                        Modifier
                            .clip(
                                RoundedCornerShape(18.dp)
                            )
//                            .background(
//                                Accent.copy(alpha = 0.05f)
//                            )
                            .padding(
                                horizontal = 6.dp,
                                vertical = 2.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    /*
                     * NITNEM TAB
                     */

                    Column(

                        modifier =
                            Modifier
                                .width(118.dp)
                                .clickable {
                                    selectedTab = 0
                                }
                                .padding(
                                    vertical = 2.dp
                                ),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(

                            text =
                                "Nitnem"
                                    .translate(
                                        state.displayLanguage
                                    ),

                            color =

                                if (selectedTab == 0)
                                    Accent
                                else
                                    TextPrimary.copy(
                                        alpha = 0.58f
                                    ),

                            fontWeight =

                                if (selectedTab == 0)
                                    FontWeight.Bold
                                else
                                    FontWeight.Medium,

                            fontSize = 14.sp
                        )

                        Spacer(
                            Modifier.height(8.dp)
                        )

                        Box(

                            modifier =
                                Modifier
                                    .height(3.dp)
                                    .fillMaxWidth(0.62f)
                                    .clip(
                                        RoundedCornerShape(50)
                                    )
                                    .background(

                                        brush =

                                            if (selectedTab == 0) {

                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Gold,
                                                        Accent
                                                    )
                                                )

                                            } else {

                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color.Transparent,
                                                        Color.Transparent
                                                    )
                                                )
                                            }
                                    )
                        )
                    }

                    /*
                     * OTHER TAB
                     */

                    Column(

                        modifier =
                            Modifier
                                .width(118.dp)
                                .clickable {
                                    selectedTab = 1
                                }
                                .padding(
                                    vertical = 2.dp
                                ),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(

                            text =
                                "Other Banis"
                                    .translate(
                                        state.displayLanguage
                                    ),

                            color =

                                if (selectedTab == 1)
                                    Accent
                                else
                                    TextPrimary.copy(
                                        alpha = 0.58f
                                    ),

                            fontWeight =

                                if (selectedTab == 1)
                                    FontWeight.Bold
                                else
                                    FontWeight.Medium,

                            fontSize = 14.sp,

                            maxLines = 1
                        )

                        Spacer(
                            Modifier.height(8.dp)
                        )

                        Box(

                            modifier =
                                Modifier
                                    .height(3.dp)
                                    .fillMaxWidth(0.62f)
                                    .clip(
                                        RoundedCornerShape(50)
                                    )
                                    .background(

                                        brush =

                                            if (selectedTab == 1) {

                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Gold,
                                                        Accent
                                                    )
                                                )

                                            } else {

                                                Brush.horizontalGradient(
                                                    listOf(
                                                        Color.Transparent,
                                                        Color.Transparent
                                                    )
                                                )
                                            }
                                    )
                        )
                    }
                }
            }
        }
        /*
         * BANI LIST
         */

        items(

            items = displayedBanis,

            key = { bani ->
                bani.id
            }

        ) { bani ->

            val isCurrent =
                state.currentBani?.id ==
                    bani.id

            val playbackProgress =
                calculatePlaybackProgress(
                    bani,
                    state,
                    isCurrent
                )

            val activeTrack = remember(
                bani.id,
                state.activeTrackId
            ) {
                val trackId = audioController.getActiveTrackId(bani.id)
                val custom = audioController.getCustomTracks(bani.id).firstOrNull { it.id == trackId }
                custom ?: com.creativeblisslabs.nitnemguktasteek.data.CuratedAudioCatalog.getTracksForBani(bani.id).firstOrNull { it.id == trackId } ?: bani.audio
            }

            val artistName = activeTrack?.artist ?: bani.audio?.artist
            val hasAudioFile = activeTrack != null
            val hasSyncedTiming = remember(activeTrack?.id, state.activeTrackId) {
                activeTrack != null && audioController.hasTimingFile(bani.id, activeTrack.id)
            }

            AnimatedVisibility(

                visible = true,

                enter =
                    fadeIn() +
                        slideInVertically(
                            initialOffsetY = {
                                it / 3
                            }
                        )
            ) {

                BaniCard(

                    bani = bani,

                    selected = isCurrent,

                    progress =
                        playbackProgress,

                    artistName =
                        artistName,

                    hasSyncedTiming =
                        hasSyncedTiming,

                    hasAudioFile =
                        hasAudioFile,

                    displayLanguage =
                        state.displayLanguage,

                    onInfoClick = {
                        showInfoSheet = false
                        selectedInfoBani = bani
                    },

                    onClick = {
                        println("HOME PLAY -> bani=${bani.id}")

                        if (hasAudioFile) {
                            viewModel.selectExperienceTab(
                                com.creativeblisslabs.nitnemguktasteek.app.models.ExperienceTab.Listen
                            )
                        } else {
                            viewModel.selectExperienceTab(
                                com.creativeblisslabs.nitnemguktasteek.app.models.ExperienceTab.Read
                            )
                        }

                        viewModel.selectBani(
                            bani.id
                        )

                        viewModel.selectTab(
                            AppTab.Experience
                        )
                    }
                )
            }
        }
    }


    /*
     * BOTTOM SHEET
     */

    if (showInfoSheet) {

  ModalBottomSheet(

    onDismissRequest = {
        selectedInfoBani = null
showInfoSheet = false
    },

    sheetState = sheetState,

    shape = RoundedCornerShape(
        topStart = 32.dp,
        topEnd = 32.dp
    ),

    containerColor = SurfaceColor,

    tonalElevation = 0.dp
){

            Spacer(
                Modifier.height(12.dp)
            )

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(
                        state = rememberScrollState(),
                        enabled = !isMascotOverlayVisible && !isGlobalSearchVisible
                    )
                    .padding(
                        horizontal = 22.dp,
                        vertical = 12.dp
                    )
            ) {

                
            Text(
                text = "ਨਿਤਨੇਮ ਗੁਟਕਾ ਸਾਹਿਬ".translate(state.displayLanguage),
                color = if (AppTheme.themeMode == ThemeMode.Light)
                    Color(0xFF1A1816)
                else
                    Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

                Spacer(
                    Modifier.height(10.dp)
                )

                Text(

                    text =
                        "Peaceful Gurbani reading and listening.".translate(state.displayLanguage),

                    color =
                        TextPrimary.copy(alpha = 0.75f),

                    fontSize = 14.sp
                )

                Spacer(
                    Modifier.height(24.dp)
                )

                PremiumInfoSection(

                    icon =
                        Icons.Default.AutoStories,

                    title =
                        "Gurbani Reading".translate(state.displayLanguage),

                    content =
                        "Adjust fonts, translations, transliterations, and themes for comfortable paath.".translate(state.displayLanguage)
                )

                Spacer(
                    Modifier.height(14.dp)
                )
                PremiumInfoSection(

                    icon =
                        Icons.Default.Headphones,

                    title =
                        "Gurbani Listening"
                            .translate(state.displayLanguage),

                    content =
                        "Enjoy synced Gurbani playback for better pronunciation and immersion, or import and sync your own custom audio files with banis."
                            .translate(state.displayLanguage)
                )

                Spacer(
                    Modifier.height(14.dp)
                )

                PremiumInfoSection(

                    icon =
                        Icons.Default.LocalFireDepartment,

                    title =
                        "Daily Progress".translate(state.displayLanguage),

                    content =
                        "Track your paath streaks and consistency with daily reminders and notifications to keep you on track.".translate(state.displayLanguage)
                )
                
                Spacer(
                    Modifier.height(14.dp)
                )

                PremiumInfoSection(

                    icon =
                        Icons.Default.AutoAwesome,

                    title =
                        "AI Gurbani Quiz".translate(state.displayLanguage),

                    content =
                        "Test your knowledge with gamified daily Gurbani questions.".translate(state.displayLanguage)
                )

                 Spacer(
                    Modifier.height(14.dp)
                )

                PremiumInfoSection(

                    icon =
                        Icons.Default.Source,

                    title =
                        "Gurbani Database".translate(state.displayLanguage),

                    content =
                        "All Gurbani texts & translations, transliterations are taken from https://www.shabados.com/".translate(state.displayLanguage)
                )
                Spacer(
                    Modifier.height(14.dp)
                )
      PremiumInfoSection(

                    icon =
                        Icons.Default.Podcasts,

                    title =
                        "Stay connected with Friends & Family".translate(state.displayLanguage),

                    content =
                        "Track your progress and compete with others on the leaderboard.".translate(state.displayLanguage)
                )

                 Spacer(
                    Modifier.height(14.dp)
                )
                PremiumInfoSection(

                    icon =
                        Icons.Default.CalendarToday,

                    title =
                        "Daily Hukamnama".translate(state.displayLanguage),

                    content =
                       (
                            "Parsed from".translate(state.displayLanguage) +
                            " https://data.gurbaninow.com/" )
                )

                Spacer(
                    Modifier.height(30.dp)
                )

            }
        }
    }

    selectedSearchVerseLine?.let { line ->
        GurbaniMeaningSheet(
            line = line,
            onDismiss = { selectedSearchVerseLine = null },
            gurmukhiFontSize = state.gurmukhiFontSize,
            lineSpacing = state.lineSpacing,
            boldGurmukhi = state.boldGurmukhi,
            centerGurmukhi = state.centerGurmukhi,
            showGurmukhi = state.showGurmukhi,
            showEnglishTranslation = state.showEnglishTranslation,
            showPunjabiTranslation = state.showPunjabiTranslation,
            showDevanagariTransliteration = state.showDevanagariTransliteration,
            showWordMeaning = state.showWordMeaning,
            showRomanTransliteration = state.showRomanTransliteration,
            larivaarMode = state.larivaarMode,
            gurmukhiColorMode = state.gurmukhiColorMode,
            translationColorMode = state.translationColorMode,
            wordMeaningColorMode = state.wordMeaningColorMode,
            displayLanguage = state.displayLanguage,
            onToggleGurmukhi = { viewModel.setShowGurmukhi(it) },
            onToggleEnglish = { viewModel.setShowEnglishTranslation(it) },
            onTogglePunjabi = { viewModel.setShowPunjabiTranslation(it) },
            onToggleHindi = { viewModel.setShowDevanagariTransliteration(it) },
            onToggleWordMeaning = { viewModel.setShowWordMeaning(it) },
            onToggleRoman = { viewModel.setShowRomanTransliteration(it) },
            onToggleCenter = { viewModel.setCenterGurmukhi(it) },
            onToggleLarivaar = { viewModel.setLarivaarMode(it) }
        )
    }

    if (selectedInfoBani != null) {
        selectedInfoBani?.let { selectedBani ->
        ModalBottomSheet(
            onDismissRequest = {
                selectedInfoBani = null
            },
            sheetState = infoSheetState,
            shape = RoundedCornerShape(
                topStart = 32.dp,
                topEnd = 32.dp
            ),
            containerColor = SurfaceColor,
            tonalElevation = 0.dp
        ) {
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = 22.dp,
                        vertical = 12.dp
                    )
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val theme = resolveBaniTheme(selectedBani.title)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(theme.accent.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = theme.icon,
                            contentDescription = null,
                            tint = theme.accent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedBani.title.translate(state.displayLanguage),
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Bani Information".translate(state.displayLanguage),
                            color = TextPrimary.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Bani Details
                BaniDetailItem(
                    label = "Author".translate(state.displayLanguage),
                    value = selectedBani.author.translate(state.displayLanguage),
                    displayLanguage = state.displayLanguage
                )
                HorizontalDivider(color = TextPrimary.copy(alpha = 0.06f), modifier = Modifier.padding(vertical = 12.dp))

                BaniDetailItem(
                    label = "Source Ang".translate(state.displayLanguage),
                    value = selectedBani.ang.translateAng(state.displayLanguage),
                    displayLanguage = state.displayLanguage
                )
                HorizontalDivider(color = TextPrimary.copy(alpha = 0.06f), modifier = Modifier.padding(vertical = 12.dp))

                BaniDetailItem(
                    label = "Structure".translate(state.displayLanguage),
                    value = selectedBani.structure.translate(state.displayLanguage),
                    displayLanguage = state.displayLanguage
                )
                HorizontalDivider(color = TextPrimary.copy(alpha = 0.06f), modifier = Modifier.padding(vertical = 12.dp))

                BaniDetailItem(
                    label = "Estimated Time".translate(state.displayLanguage),
                    value = "${selectedBani.estimatedMinutes} ${"minutes".translate(state.displayLanguage)}".translateDigits(state.displayLanguage),
                    displayLanguage = state.displayLanguage
                )

                val baniTheme = remember(selectedBani.title) { resolveBaniTheme(selectedBani.title) }

                if (baniTheme.about.isNotBlank()) {
                    HorizontalDivider(color = TextPrimary.copy(alpha = 0.06f), modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        text = "About".translate(state.displayLanguage),
                        color = Accent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = baniTheme.about.translate(state.displayLanguage),
                        color = TextPrimary.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }

                if (baniTheme.significance.isNotBlank()) {
                    HorizontalDivider(color = TextPrimary.copy(alpha = 0.06f), modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        text = "Significance".translate(state.displayLanguage),
                        color = Accent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = baniTheme.significance.translate(state.displayLanguage),
                        color = TextPrimary.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }

                selectedBani.source?.let { source ->

    HorizontalDivider(
        color = TextPrimary.copy(alpha = 0.06f),
        modifier = Modifier.padding(vertical = 12.dp)
    )

    BaniDetailItem(
        label = "Source".translate(state.displayLanguage),
        value = buildString {
            append(source.text)
        },
        displayLanguage = state.displayLanguage
    )
}

                Spacer(Modifier.height(30.dp))
                }
            }
        }
    }
    }
    } // Closes the root Column container!
    // ── FULL-SCREEN INTERACTIVE MASCOT OVERLAY ──────────────────────────────
    AnimatedVisibility(
        visible = isMascotOverlayVisible,
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(450))
    ) {
        val colors = practiceColors()
        val currentBani = state.currentBani ?: state.banis.firstOrNull()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .imePadding() // Moves the root container up dynamically with the keyboard cleanly
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isMascotOverlayVisible = false
                    isAssistantListening = false
                }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f) // Keeps the sheet nicely proportioned as a bottom sheet overlay
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    // REMOVED .imePadding() from here since the root Box handles it now
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // Prevent click-through dismissal
                    },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFFF7F5F0).copy(alpha = 0.98f) else Color(0xFF1C1A17).copy(alpha = 0.98f)
                ),
                border = BorderStroke(1.5.dp, Brush.verticalGradient(listOf(Gold.copy(alpha = 0.4f), Color.Transparent))),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    // Header with close button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MascotBani(
                                state = MascotState.Meditating,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Bani AI Assistant".translate(state.displayLanguage),
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        
                        IconButton(
                            onClick = {
                                isMascotOverlayVisible = false
                                isAssistantListening = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Assistant",
                                tint = colors.textPrimary
                            )
                        }
                    }
                    
                    if (currentBani != null) {
                        BaniAiAssistantTab(
                            bani = currentBani,
                            colors = colors,
                            displayLanguage = state.displayLanguage,
                            uiState = state,
                            onShowVerseDetails = { line ->
                                selectedSearchVerseLine = line
                            }
                        )
                    }
                }
            }
        }
    }

    if (isGlobalSearchVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isGlobalSearchVisible = false
                },
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // Prevent click-through dismissal
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFFF7F5F0) else Color(0xFF1C1A17)
                ),
                border = BorderStroke(1.5.dp, Gold.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Global Search".translate(state.displayLanguage),
                            color = Gold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { isGlobalSearchVisible = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Search",
                                tint = if (AppTheme.themeMode == ThemeMode.Light) Color.Black else Color.White
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = globalSearchQuery,
                        onValueChange = { globalSearchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Search Gurmukhi, English, Hindi...".translate(state.displayLanguage),
                                color = (if (AppTheme.themeMode == ThemeMode.Light) Color.Black else Color.White).copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = (if (AppTheme.themeMode == ThemeMode.Light) Color.Black else Color.White).copy(alpha = 0.2f),
                            focusedTextColor = if (AppTheme.themeMode == ThemeMode.Light) Color.Black else Color.White,
                            unfocusedTextColor = if (AppTheme.themeMode == ThemeMode.Light) Color.Black else Color.White
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    val searchMatches = remember(globalSearchQuery) {
                        if (globalSearchQuery.length < 3) emptyList()
                        else {
                            val lowerQuery = globalSearchQuery.lowercase().trim()
                            val matches = mutableListOf<Pair<BaniLine, String>>()
                            outer@ for (dbBani in com.creativeblisslabs.nitnemguktasteek.data.generated.GeneratedBaniCatalog.banis) {
                                for (line in dbBani.lines) {
                                    if (line.gurmukhi.lowercase().contains(lowerQuery) ||
                                        line.translation.lowercase().contains(lowerQuery) ||
                                        line.hindi.lowercase().contains(lowerQuery) ||
                                        line.punjabi.lowercase().contains(lowerQuery) ||
                                        line.transliteration.lowercase().contains(lowerQuery)) {
                                        matches.add(line to dbBani.title)
                                        if (matches.size >= 4) break@outer // Limit to 4 to prevent scrolling!
                                    }
                                }
                            }
                            matches
                        }
                    }
                    
                    if (searchMatches.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            searchMatches.forEach { (line, baniTitle) ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedSearchVerseLine = line
                                            isGlobalSearchVisible = false
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (AppTheme.themeMode == ThemeMode.Light) Color(0xFFFAFAFA) else Color(0xFF252321),
                                    border = BorderStroke(1.dp, (if (AppTheme.themeMode == ThemeMode.Light) Color.Black else Color.White).copy(alpha = 0.05f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = baniTitle,
                                                color = Gold,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = null,
                                                tint = Gold.copy(alpha = 0.7f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = line.gurmukhi,
                                            color = if (AppTheme.themeMode == ThemeMode.Light) Color.Black else Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = line.translation,
                                            color = (if (AppTheme.themeMode == ThemeMode.Light) Color.Black else Color.White).copy(alpha = 0.7f),
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    } else if (globalSearchQuery.length >= 3) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No matching verses found.".translate(state.displayLanguage),
                            color = (if (AppTheme.themeMode == ThemeMode.Light) Color.Black else Color.White).copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    if (showWeeklyStreakDialog) {
        WeeklyStreakDialog(
            streakDays = streakToDisplay,
            displayLanguage = state.displayLanguage,
            onDismiss = { showWeeklyStreakDialog = false }
        )
    }

    if (showLeaderboardDialog) {
        FamilyLeaderboardDialog(
            uiState = state,
            displayLanguage = state.displayLanguage,
            onDismiss = { showLeaderboardDialog = false }
        )
    }

    if (showSevaSheet) {
        com.creativeblisslabs.nitnemguktasteek.app.ui.components.SevaSupporterModalSheet(
            displayLanguage = state.displayLanguage,
            onDismiss = { showSevaSheet = false }
        )
    }
}
