package org.audiocular.nitnem.app.ui.screens

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
import org.audiocular.nitnem.models.NitnemUiState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import org.audiocular.nitnem.app.models.AppTab
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.app.ui.components.BaniCard
import org.audiocular.nitnem.app.ui.components.HeroCard
import org.audiocular.nitnem.app.ui.components.MascotBani
import org.audiocular.nitnem.app.ui.components.MascotState
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateDigits
import org.audiocular.nitnem.utils.translateAng
import androidx.compose.ui.text.style.TextAlign
import org.audiocular.nitnem.models.Bani
import androidx.compose.runtime.LaunchedEffect
import org.audiocular.nitnem.data.HukamnamaProvider
import org.audiocular.nitnem.audio.StreamAudioController
import androidx.compose.ui.draw.scale
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

sealed interface HomeSheet {

    data object AppInfo : HomeSheet

    data class BaniInfo(val bani: Bani) : HomeSheet

}

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
            "japji-sahib",
            "jaap-sahib",
            "tav-prasad-savaiye",
            "benti-chaupai-sahib",
            "anand-sahib",
            "rehras-sahib",
            "kirtan-sohila"
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
            currentHour in 3..8 -> MascotState.Reading
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


    Column(

        modifier = Modifier
            .fillMaxSize()
           
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 2.dp,
                    bottom = 2.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
                                            MascotBani(
                state = mascotOverrideState ?: baseMascotState,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(60.dp)
                    .clickable {
                        // FIX: Transition the mascot into a global interactive navigation anchor
                        isMascotOverlayVisible = true
                        isAssistantListening = true
                    }
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ੴ ਸਤਿਨਾਮੁ ਵਾਹਿਗੁਰੂ".translate(state.displayLanguage),
                    color = Gold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "ਨਿਤਨੇਮ ਗੁਟਕਾ ਸਾਹਿਬ".translate(state.displayLanguage),
                    color = if (AppTheme.themeMode == ThemeMode.Light)
                        Color(0xFF1A1816)
                    else
                        Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.Transparent,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = "${state.totalXp} XP",
                    color = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Surface(
                onClick = { showInfoSheet = true },
                shape = CircleShape,
                color = Accent.copy(alpha = 0.10f)
            ) {
                Box(
                    modifier = Modifier.size(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Accent.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

                // ── TRANSFORMED THOUGHT BUBBLE ─────────────────────────────────────
        AnimatedVisibility(
            visible = mascotMessage != null
        ) {
            mascotMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 72.dp, end = 18.dp, top = 2.dp, bottom = 6.dp) // Pushed right to align under the mascot avatar
                        .clickable { mascotMessage = null },
                    colors = CardDefaults.cardColors(
                        containerColor = if (AppTheme.themeMode == ThemeMode.Light)
                            Color(0xFFFFF9E6)
                        else
                            Color(0xFF2C2720)
                    ),
                    // Bubble shape pointing up towards the mascot
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
                            text = msg, // Removed the rigid emoji to let translations shine cleanly
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
                                vertical = 4.dp
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
                                    vertical = 8.dp
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
                                    vertical = 8.dp
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

            val artistName =
                remember(
                    bani.id,
                    state.activeTrackId
                ) {

                    val trackId =
                        audioController
                            .getActiveTrackId(
                                bani.id
                            )

                    audioController
                        .getCustomTracks(
                            bani.id
                        )
                        .firstOrNull {
                            it.id == trackId
                        }
                        ?.artist
                        ?: bani.audio?.artist
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

                    displayLanguage =
                        state.displayLanguage,

                    onInfoClick = {
                        showInfoSheet = false
                        selectedInfoBani = bani
                    },

                    onClick = {

                        println("HOME PLAY -> bani=${bani.id}")

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
                        rememberScrollState()
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
    // ── FULL-SCREEN INTERACTIVE MASCOT OVERLAY ──────────────────────────────
    AnimatedVisibility(
        visible = isMascotOverlayVisible,
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(450))
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "global_mascot_breath")
        
        // Crisp, multi-layered structural scaling loops
        val breathingScale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(2800, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "body_breath"
        )
        
        val rippleRadius1 by infiniteTransition.animateFloat(
            initialValue = 120f,
            targetValue = 480f,
            animationSpec = infiniteRepeatable(
                animation = tween(3500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "outer_ripple"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable { 
                    // Safely dismiss overlay and release audio anchors on background tap
                    isMascotOverlayVisible = false
                    isAssistantListening = false
                },
            contentAlignment = Alignment.Center
        ) {
            // ── LAYER 1: CANVAS RIPPLE PROPAGATION ENGINE ────────────────────
                       // ── LAYER 1: CANVAS RIPPLE PROPAGATION ENGINE ────────────────────
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerPoint = Offset(size.width / 2f, size.height / 2.3f)
                
                // First Ripple Ring
                drawCircle(
                    color = Gold.copy(alpha = (1f - (rippleRadius1 / 480f)).coerceIn(0f, 0.25f)),
                    radius = rippleRadius1,
                    center = centerPoint,
                    style = Stroke(width = 2.dp.toPx()) // FIX: Convert DP to Float Pixels safely
                )
                
                // Second Ripple Ring
                drawCircle(
                    color = Accent.copy(alpha = (1f - ((rippleRadius1 + 120f) % 480f / 480f)).coerceIn(0f, 0.15f)),
                    radius = (rippleRadius1 + 120f) % 480f, // FIX: Calculated purely using Float math operations
                    center = centerPoint,
                    style = Stroke(width = 1.5.dp.toPx()) // FIX: Convert DP to Float Pixels safely
                )
            }

            // ── LAYER 2: INTERACTIVE CONTENT CARD DECK ───────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .scale(breathingScale)
                        .size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MascotBani(
                        state = when {
                            isAssistantListening -> MascotState.Listening
                            interactiveInputText.isNotBlank() -> MascotState.Reading
                            else -> MascotState.Meditating
                        },
                        modifier = Modifier.size(160.dp)
                    )
                }

                Spacer(Modifier.height(40.dp))

                Text(
                    text = if (isAssistantListening) "Listening to Voice Input..." else "Processing Database Insights...",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = if (interactiveInputText.isBlank()) 
                        "Speak clearly to analyze structural data, Ang placements, translations, or historical metrics from across our complete catalog rules."
                    else 
                        interactiveInputText,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(48.dp))

                // Action Controls Row Footer
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Manual Cancellation Button
                    IconButton(
                        onClick = { 
                            isMascotOverlayVisible = false
                            isAssistantListening = false
                        },
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss Overlay",
                            tint = Color.White
                        )
                    }

                    // Simulated Vocal Stop/Process Toggle Action Trigger
                    IconButton(
                        onClick = { 
                            isAssistantListening = !isAssistantListening 
                            if (!isAssistantListening) {
                                // Direct routing example link matching any targeted Bani segment or concept string
                                interactiveInputText = "Analyzing 'Japji Sahib' across active database rows..."
                            }
                        },
                        modifier = Modifier
                            .size(68.dp)
                            .background(if (isAssistantListening) Color(0xFFEF5350) else Gold, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isAssistantListening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mic Control Hook",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun PremiumInfoSection(

    icon: ImageVector,

    title: String,

    content: String
) {

    Surface(

        shape = RoundedCornerShape(24.dp),

        color =
            Accent.copy(alpha = 0.05f),

        border =
            BorderStroke(
                1.dp,
                Accent.copy(alpha = 0.08f)
            )
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
               .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            )
        ) {

            Row(

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Box(

                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Accent.copy(alpha = 0.12f)
                        ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector = icon,

                        contentDescription = null,

                        tint = Accent,

                        modifier =
                            Modifier.size(20.dp)
                    )
                }

                Spacer(
                    Modifier.width(12.dp)
                )

                Text(

                    text = title,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize = 17.sp,

                    color =
                        if (AppTheme.themeMode == ThemeMode.Light)
                            Color(0xFF1B1815)
                        else
                            Color.White
                )
            }

            Spacer(
                Modifier.height(16.dp)
            )

            Text(

                text = content,

                color =
                    TextPrimary.copy(alpha = 0.80f),

                lineHeight = 20.sp,

                fontSize = 13.sp
                    )
                }
            }
        }
   
enum class AnalyticsCardType {

    Today,
    Consistency,
    Listening
}

@Composable
private fun PremiumAnalyticsCard(

    modifier: Modifier = Modifier,

    title: String,

    value: String,

    subtitle: String,

    icon: ImageVector,

    type: AnalyticsCardType,

    progress: Float
) {

    val infinite =
        rememberInfiniteTransition(
            label = "analytics_bg"
        )

    val glow by infinite.animateFloat(

        initialValue = 0.04f,

        targetValue = 0.10f,

        animationSpec = infiniteRepeatable(

            animation = tween(2400),

            repeatMode = RepeatMode.Reverse
        ),

        label = "glow"
    )

    val wavePhase by infinite.animateFloat(

        initialValue = 0f,

        targetValue = 2f * PI.toFloat(),

        animationSpec = infiniteRepeatable(

            animation = tween(
                4000,
                easing = LinearEasing
            ),

            repeatMode = RepeatMode.Restart
        ),

        label = "wave"
    )

    val floatPhase by infinite.animateFloat(

        initialValue = 0f,

        targetValue = 1f,

        animationSpec = infiniteRepeatable(

            animation = tween(
                6000,
                easing = LinearEasing
            ),

            repeatMode = RepeatMode.Restart
        ),

        label = "float"
    )
val animatedProgress by animateFloatAsState(

    targetValue = progress.coerceIn(0f, 1f),

    animationSpec = tween(
        durationMillis = 900,
        easing = EaseOutCubic
    ),

    label = "analytics_progress"
)
val scale by animateFloatAsState(

    targetValue =

        if (progress > 0f) 1f else 0.96f,

    animationSpec = spring(

        dampingRatio = 0.7f,

        stiffness = 180f

    ),

    label = "card_scale"

)
        Card(
        modifier = modifier.scale(scale),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            1.dp,
            Accent.copy(alpha = 0.08f)
        )
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            when(type) {
                                AnalyticsCardType.Listening -> {
                                    Accent.copy(alpha = 0.03f + (animatedProgress * 0.08f))
                                }
                                else -> {
                                    Accent.copy(alpha = glow)
                                }
                            },
                            Gold.copy(alpha = 0.03f)
                        )
                    )
                )
                .drawBehind {
                    if (animatedProgress > 0.8f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(Gold.copy(alpha = 0.10f), Color.Transparent)
                            ),
                            radius = this.size.maxDimension * 0.72f
                        )
                    }
                }
        ) {
            /*
             * FIX: BACKGROUND TEXTURE REFLECTION MESH LAYER
             * Draws a high-premium, low-opacity mascot waterprint trace directly 
             * embedded behind card content elements
             */
                       // Subtle character background reflection mask
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 40.dp, start = 32.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                val isLightMode = AppTheme.themeMode == ThemeMode.Light
                
                MascotBani(
                    state = when (type) {
                        AnalyticsCardType.Today -> MascotState.Celebrating
                        AnalyticsCardType.Listening -> MascotState.Listening
                        AnalyticsCardType.Consistency -> MascotState.Meditating
                    },
                    modifier = Modifier
                        .size(110.dp)
                        // Using graphicsLayer bypasses missing alpha function imports safely
                        .graphicsLayer {
                            alpha = if (isLightMode) 0.04f else 0.025f
                        }
                )
            }

            /*
             * CANVAS FX
             */

            Canvas(
                modifier = Modifier.matchParentSize()
            ) {

                val width = size.width
                val height = size.height

                when(type) {

                    /*
                     * TODAY
                     * WATER FILL
                     */

      AnalyticsCardType.Today -> {

    /*
     * LIQUID LEVEL
     */

 val liquidLevel =
    height -
        (
            height * 0.72f * animatedProgress
            )

    /*
     * MOTION
     */

    val phase =
        wavePhase * 0.75f

    val waveAmplitude =
        4f

    val rippleAmplitude =
        1.4f

    val wavelength =
        width * 0.9f

    /*
     * MAIN LIQUID PATH
     */

    val liquidPath = Path()

    liquidPath.moveTo(
        -60f,
        height
    )

    liquidPath.lineTo(
        -60f,
        liquidLevel
    )

    var previousX = -60f

    var previousY = liquidLevel

    var firstPoint = true

    for (
        xInt in -60..(width.toInt() + 60) step 20
    ) {

        val x = xInt.toFloat()

        /*
         * FLATTER WATER MOTION
         */

        val largeWave =
            sin(
                (
                    x / wavelength
                    ) *
                    (2f * PI.toFloat()) +
                    phase
            ) * waveAmplitude

        val smallRipple =
            cos(
                (
                    x / (
                        wavelength * 0.45f
                        )
                    ) *
                    (2f * PI.toFloat()) +
                    (
                        phase * 1.6f
                        )
            ) * rippleAmplitude

        val y =
            liquidLevel +
                largeWave +
                smallRipple

        if (firstPoint) {

            liquidPath.lineTo(
                x,
                y
            )

            firstPoint = false

        } else {

            val midX =
                (previousX + x) / 2f

            val midY =
                (previousY + y) / 2f

            liquidPath.quadraticBezierTo(

                previousX,
                previousY,

                midX,
                midY
            )
        }

        previousX = x
        previousY = y
    }

    liquidPath.lineTo(
        width + 60f,
        height
    )

    liquidPath.close()

    /*
     * FILLED LIQUID
     */

    drawPath(

        path = liquidPath,

        brush = Brush.verticalGradient(

            colors = listOf(

          Accent.copy(alpha = 0.30f),

Gold.copy(alpha = 0.16f)
            ),

            startY = liquidLevel,

            endY = height
        )
    )

    /*
     * SURFACE HIGHLIGHT
     */

    drawPath(

        path = liquidPath,

        brush = Brush.horizontalGradient(

            listOf(

              Gold.copy(alpha = 0.22f),

Accent.copy(alpha = 0.42f),

Gold.copy(alpha = 0.22f)
            )
        ),

        style = Stroke(

            width = 1.4f,

            cap = StrokeCap.Round
        )
    )
}
                    /*
                     * LISTENING
                     * AUDIO BARS
                     */
                    AnalyticsCardType.Listening -> {

                        // Intentionally minimal
                    }
                    /*
                     * CONSISTENCY
                     * EMBERS
                     */

                    AnalyticsCardType.Consistency -> {

                        if (
                            animatedProgress > 0.2f
                        ) {

                            repeat(6) { index ->

                                val emberY =
                                    height * (
                                        0.15f +
                                            (
                                                (
                                                    floatPhase +
                                                        index * 0.12f
                                                    ) % 1f
                                                )
                                        )

                                drawCircle(

                                    color =
                                        Gold.copy(
                                            alpha = 0.22f
                                        ),

                                    radius = 3f,

                                    center = Offset(

                                        width * 0.5f +
                                            sin(
                                                wavePhase +
                                                    index
                                            ) * 20f,

                                        emberY
                                    )
                                )
                            }
                        }
                    }

                }
            }

          Column(

    modifier = Modifier
        .fillMaxSize()
        .padding(
            horizontal = 8.dp,
            vertical = 10.dp
        ),

    horizontalAlignment =
        Alignment.CenterHorizontally
) {

    /*
     * ICON
     */

    Box(

        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .scale(

    if (
        type ==
        AnalyticsCardType.Listening
    ) {

        1f + (
            animatedProgress * 0.06f
        )

    } else {

        1f
    }
)
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

        Icon(

            imageVector = icon,

            contentDescription = null,

            tint = Color.White,

            modifier = Modifier.size(18.dp)
        )
    }

    Spacer(
        Modifier.height(6.dp)
    )

    /*
     * VALUE
     */

    Text(

        text = value,

        color =

            if (
                AppTheme.themeMode ==
                ThemeMode.Light
            ) {

                Color(0xFF1F1B16)

            } else {

                Color.White
            },

        fontSize = 18.sp,

        fontWeight =
            FontWeight.Bold
    )


    Text(

        text = title,

        color =
            TextPrimary.copy(
                alpha = 0.72f
            ),

        fontSize = 12.sp,

        fontWeight =
            FontWeight.SemiBold
    )


    Text(

        text = subtitle,

        color =
            TextPrimary.copy(
                alpha = 0.45f
            ),

        fontSize = 10.sp,

        maxLines = 1,

        overflow =
            TextOverflow.Ellipsis
    )

    Spacer(
        Modifier.weight(1f)
    )

    /*
     * HEATMAP
     */

    if (
        type ==
        AnalyticsCardType.Consistency
    ) {

        MonthlyHeatmap(
            progress =
                animatedProgress
        )

        Spacer(
            Modifier.height(6.dp)
        )
    }
}
        }
    }
}
    @Composable
private fun MonthlyHeatmap(
    progress: Float
) {

    val activeCount =
        (28 * progress).toInt()

    Column(

        verticalArrangement =
            Arrangement.spacedBy(2.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        repeat(4) { row ->

            Row(

                horizontalArrangement =
                    Arrangement.spacedBy(2.dp)
            ) {

                repeat(7) { col ->

                    val index =
                        row * 7 + col

                    val active =
                        index < activeCount

                    val backgroundBrush = if (active) {

                        Brush.linearGradient(

                            listOf(
                                Gold,
                                Accent
                            )
                        )

                    } else {

                        Brush.linearGradient(

                            listOf(
                                Color(0xFF8C8478)
                                    .copy(alpha = 0.20f),

                                Color(0xFF5E584F)
                                    .copy(alpha = 0.12f)
                            )
                        )
                    }

                    Box(

                        modifier = Modifier
                            .size(7.dp)
                            .clip(
                                RoundedCornerShape(2.dp)
                            )
                            .background(
                                brush = backgroundBrush
                            )
                    )
                }
            }
        }
    }
}


@Composable
private fun BaniDetailItem(
    label: String,
    value: String,
    displayLanguage: DisplayLanguage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            color = TextPrimary.copy(alpha = 0.6f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.6f)
        )
    }
}
private fun calculatePlaybackProgress(
    bani: Bani,
    state: NitnemUiState,
    isCurrent: Boolean
): Float {

    return when {

        isCurrent -> {

            if (state.playback.durationMs <= 0L) {

                0f

            } else {

                (
                    state.playback.positionMs.toFloat() /
                        state.playback.durationMs.toFloat()
                ).coerceIn(0f, 1f)
            }
        }

        else -> 0f
    }
}
private fun getMascotMessage(
    state: NitnemUiState,
    displayLanguage: DisplayLanguage
): String {

    val messages = mutableListOf<String>()
    val currentHour: Int = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).hour
    val completedBanis = state.completedBanis.map { it.lowercase() }

    val totalRequiredCount = 5

    if (state.completedBanis.size >= totalRequiredCount) {
        return when (displayLanguage) {
            DisplayLanguage.Punjabi ->
                "ਵਾਹਿਗੁਰੂ! ਤੁਸੀਂ ਅੱਜ ਆਪਣਾ ਸਾਰਾ ਨਿਤਨੇਮ ਪਾਠ ਪੂਰਾ ਕਰ ਲਿਆ ਹੈ। ਬਹੁਤ ਵਧੀਆ!"

            DisplayLanguage.Hindi ->
                "वाहेगुरु! आपने आज अपना सारा नितनेम पाठ पूरा कर लिया है। बहुत बढ़िया!"

            DisplayLanguage.English ->
                "Waheguru! You have completed all your daily Nitnem path today. Splendid dedication!"
        }
    }

    when {

        currentHour in 3..9 &&
            !completedBanis.contains("japji sahib") -> {

            messages.add(
                when (displayLanguage) {
                    DisplayLanguage.Punjabi ->
                        "ਅੰਮ੍ਰਿਤ ਵੇਲਾ ਚੱਲ ਰਿਹਾ ਹੈ। ਆਓ ਅੱਜ ਜਪੁਜੀ ਸਾਹਿਬ ਦਾ ਪਾਠ ਸ਼ੁਰੂ ਕਰੀਏ!"

                    DisplayLanguage.Hindi ->
                        "अमृत वेला चल रहा है। आइए आज जपुजी साहिब का पाठ शुरू करें!"

                    DisplayLanguage.English ->
                        "It's Amrit Vela. Let's start the day with Japji Sahib!"
                }
            )
        }

        currentHour in 6..11 &&
            !completedBanis.contains("jaap sahib") -> {

            messages.add(
                when (displayLanguage) {
                    DisplayLanguage.Punjabi ->
                        "ਕੀ ਤੁਸੀਂ ਅੱਜ ਜਾਪੁ ਸਾਹਿਬ ਜੀ ਦਾ ਪਾਠ ਕੀਤਾ ਹੈ? ਆਓ ਇਸ ਨੂੰ ਸ਼ੁਰੂ ਕਰੀਏ।"

                    DisplayLanguage.Hindi ->
                        "क्या आपने आज जापु साहिब जी का पाठ किया है? आइए इसे शुरू करें।"

                    DisplayLanguage.English ->
                        "Have you read Jaap Sahib today? Let's tune in now."
                }
            )
        }

        currentHour in 16..19 &&
            !completedBanis.contains("rehras sahib") -> {

            messages.add(
                when (displayLanguage) {
                    DisplayLanguage.Punjabi ->
                        "ਸ਼ਾਮ ਹੋ ਗਈ ਹੈ। ਆਓ ਰਹਿਰਾਸ ਸਾਹਿਬ ਜੀ ਦੇ ਪਾਠ ਨਾਲ ਸ਼ਾਂਤੀ ਪ੍ਰਾਪਤ ਕਰੀਏ।"

                    DisplayLanguage.Hindi ->
                        "शाम हो गई है। आइए रहिरास साहिब जी के पाठ से शांति प्राप्त करें।"

                    DisplayLanguage.English ->
                        "It is evening. Let's find peace with Rehras Sahib."
                }
            )
        }

        currentHour >= 20 &&
            !completedBanis.contains("kirtan sohila") -> {

            messages.add(
                when (displayLanguage) {
                    DisplayLanguage.Punjabi ->
                        "ਸੌਣ ਤੋਂ ਪਹਿਲਾਂ ਕੀਰਤਨ ਸੋਹਿਲਾ ਜੀ ਦਾ ਪਾਠ ਕਰਕੇ ਚੰਗੀ ਨੀਂਦ ਅਤੇ ਸ਼ਾਂਤੀ ਪ੍ਰਾਪਤ ਕਰੋ।"

                    DisplayLanguage.Hindi ->
                        "सोने से पहले कीर्तन सोहिला जी का पाठ करके अच्छी नींद और शांति प्राप्त करें।"

                    DisplayLanguage.English ->
                        "Read Kirtan Sohila before sleeping to bring tranquility and protection."
                }
            )
        }
    }

    if (state.streakDays > 0) {

        messages.add(
            when (displayLanguage) {
                DisplayLanguage.Punjabi ->
                    "ਤੁਹਾਡੀ ਲੜੀ ${state.streakDays} ਦਿਨਾਂ ਦੀ ਹੈ! ਅੱਜ ਦਾ ਪਾਠ ਕਰਕੇ ਇਸ ਲੜੀ ਨੂੰ ਸੁਰੱਖਿਅਤ ਕਰੋ।"

                DisplayLanguage.Hindi ->
                    "आपकी निरंतरता ${state.streakDays} दिनों की है! आज का पाठ करके इसे सुरक्षित करें।"

                DisplayLanguage.English ->
                    "You're on a ${state.streakDays}-day streak! Secure it today by finishing your daily path."
            }
        )
    }

    if (state.totalXp > 0) {

        messages.add(
            when (displayLanguage) {
                DisplayLanguage.Punjabi ->
                    "ਤੁਸੀਂ ਅੱਜ ${state.totalXp} XP ਕਮਾ ਲਏ ਹਨ। ਵਾਹਿਗੁਰੂ ਤੁਹਾਡੇ ਅਭਿਆਸ ਵਿੱਚ ਵਾਧਾ ਕਰੇ!"

                DisplayLanguage.Hindi ->
                    "आपने आज ${state.totalXp} XP कमा लिए हैं। वाहेगुरु आपके अभ्यास में वृद्धि करे!"

                DisplayLanguage.English ->
                    "You've earned ${state.totalXp} XP today! Keep engaging with Gurbani to grow."
            }
        )
    }

    messages.add(
        when (displayLanguage) {
            DisplayLanguage.Punjabi ->
                "ਆਪਣੀ ਬਾਣੀ ਦੀ ਸਮਝ ਨੂੰ ਪਰਖਣ ਲਈ ਪ੍ਰੈਕਟਿਸ ਟੈਬ ਵਿੱਚ AI ਗੁਰਬਾਣੀ ਕੁਇਜ਼ ਖੇਡੋ!"

            DisplayLanguage.Hindi ->
                "अपनी बाणी की समझ को परखने के लिए प्रैक्टिस टैब में AI गुरबाणी क्विज़ खेलें!"

            DisplayLanguage.English ->
                "Test your understanding of Gurbani with the AI Quiz in the Practice tab."
        }
    )

    messages.add(
        when (displayLanguage) {
            DisplayLanguage.Punjabi ->
                "ਅੱਜ ਮਨ ਨੂੰ ਸ਼ਾਂਤ ਕਰਨ ਲਈ ਸਿਮਰਨ ਅਭਿਆਸ ਵਿੱਚ ਕੁਝ ਸਮਾਂ ਬਿਤਾਓ।"

            DisplayLanguage.Hindi ->
                "आज मन को शांत करने के लिए सिमरन अभ्यास में कुछ समय बिताएं।"

            DisplayLanguage.English ->
                "Spend a few minutes in Simran Abhyas today."
        }
    )

    if (messages.isEmpty()) {

        messages.add(
            when (displayLanguage) {
                DisplayLanguage.Punjabi ->
                    "ਵਾਹਿਗੁਰੂ ਜੀ ਕਾ ਖਾਲਸਾ ਵਾਹਿਗੁਰੂ ਜੀ ਕੀ ਫਤਿਹ।"

                DisplayLanguage.Hindi ->
                    "वाहेगुरु जी का खालसा, वाहेगुरु जी की फतेह।"

                DisplayLanguage.English ->
                    "Waheguru Ji Ka Khalsa, Waheguru Ji Ki Fateh."
            }
        )
    }

    return messages.random()
}