package org.audiocular.nitnem.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
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
import kotlin.math.PI
import kotlin.math.absoluteValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import org.audiocular.nitnem.app.models.AppTab
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.app.ui.components.BaniCard
import org.audiocular.nitnem.app.ui.components.HeroCard
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateDigits
import org.audiocular.nitnem.utils.translateAng
import androidx.compose.ui.text.style.TextAlign
import org.audiocular.nitnem.models.Bani
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import org.audiocular.nitnem.data.HukamnamaProvider
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NitnemViewModel
) {

    val state by viewModel.uiState.collectAsState()

val context = LocalContext.current

LaunchedEffect(Unit) {

    val hukamnama =
        HukamnamaProvider
            .getDailyHukamnama(context)

    viewModel.setHukamnama(
        hukamnama
    )
}
    var showInfoSheet by remember {
        mutableStateOf(false)
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
    // 1. Remove the background modifier from here
    modifier = Modifier.fillMaxWidth(), 
    
    // 2. Set the color parameter directly to Transparent
    color = Color.Transparent, 

    tonalElevation = 0.dp,
    shadowElevation = 0.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 18.dp,
                end = 18.dp,
                top = 4.dp,
                bottom = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        } else {
            LazyColumn(

            modifier = Modifier
                .fillMaxSize(),

            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 8.dp,
                bottom = 130.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

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

            item {
                val todayProgress = if (nitnemBanis.isNotEmpty()) (completedNitnemCount.toFloat() / nitnemBanis.size.toFloat()).coerceIn(0f, 1f) else 0f
                val consistencyProgress = (state.monthlyConsistencyDays.toFloat() / 30f).coerceIn(0f, 1f)
                val listeningProgress = (totalCompletedMinutes.toFloat() / 60f).coerceIn(0f, 1f)

                Row(

                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    PremiumAnalyticsCard(

                        modifier =
                            Modifier.weight(1f),

                        title = "Today".translate(state.displayLanguage),

                        value =
                            "$completedNitnemCount/${nitnemBanis.size}",

                        subtitle = "Paaths".translate(state.displayLanguage),

                        icon =
                            Icons.Default.AutoStories,

                        type = AnalyticsCardType.Today,

                        progress = todayProgress
                    )

                    PremiumAnalyticsCard(

                        modifier =
                            Modifier.weight(1f),

                        title = "Consistency".translate(state.displayLanguage),

                        value =
                            "${state.monthlyConsistencyDays}",

                        subtitle = "Days".translate(state.displayLanguage),

                        icon =
                            Icons.Default.LocalFireDepartment,

                        type = AnalyticsCardType.Consistency,

                        progress = consistencyProgress
                    )

                    PremiumAnalyticsCard(

                        modifier =
                            Modifier.weight(1f),

                        title = "Listening".translate(state.displayLanguage),

                        value = readingValue,

                        subtitle = "Time".translate(state.displayLanguage),

                        icon =
                            Icons.Default.Headphones,

                        type = AnalyticsCardType.Listening,

                        progress = listeningProgress
                    )
                }
            }

// // HukamnamaSection(

// //     displayLanguage = state.displayLanguage,

// //     context = context
// // )
//             }

            item {

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(

                        text = "Nitnem Banis".translate(state.displayLanguage),

                        modifier =
                            Modifier.weight(1f),

                        color =
                            if (AppTheme.themeMode == ThemeMode.Light)
                                Color(0xFF1F1B16)
                            else
                                Color.White,

                        fontWeight =
                            FontWeight.Bold,

                        fontSize = 21.sp
                    )

                    Text(

                        text =
                            "${nitnemBanis.size} " + "Paaths".translate(state.displayLanguage),

                        color =
                            TextPrimary.copy(alpha = 0.58f),

                        fontSize = 13.sp
                    )
                }
            }

            items(

                items = nitnemBanis,

                key = { it.id }

            ) { bani ->

                val isCurrent =
                    state.currentBani?.id == bani.id

                val playbackProgress = remember(

                    state.playback.positionMs,

                    state.playback.durationMs,

                    state.completedBanis,

                    bani.id,

                    isCurrent
                ) {

                    when {

                        state.completedBanis.contains(
                            bani.id
                        ) -> 1f

                        isCurrent -> {

                            if (
                                state.playback.durationMs <= 0L
                            ) {

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

                        progress = playbackProgress,

                        artistName =
                            "Bhai Jarnail Singh Ji",

                        displayLanguage = state.displayLanguage,

                        onInfoClick = {
                            selectedInfoBani = bani
                        },

                        onClick = {

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

            item {

                Row(

                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(top = 8.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(

                        text = "Other Banis".translate(state.displayLanguage),

                        modifier =
                            Modifier.weight(1f),

                        color =
                            if (AppTheme.themeMode == ThemeMode.Light)
                                Color(0xFF1F1B16)
                            else
                                Color.White,

                        fontWeight =
                            FontWeight.Bold,

                        fontSize = 21.sp
                    )

                    Text(

                        text =
                            "${otherBanis.size} " + "Paaths".translate(state.displayLanguage),

                        color =
                            TextPrimary.copy(alpha = 0.58f),

                        fontSize = 13.sp
                    )
                }
            }

            items(

                items = otherBanis,

                key = { it.id }

            ) { bani ->

                val isCurrent =
                    state.currentBani?.id == bani.id

                val playbackProgress = remember(

                    state.playback.positionMs,

                    state.playback.durationMs,

                    state.completedBanis,

                    bani.id,

                    isCurrent
                ) {

                    when {

                        state.completedBanis.contains(
                            bani.id
                        ) -> 1f

                        isCurrent -> {

                            if (
                                state.playback.durationMs <= 0L
                            ) {

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

                        progress = playbackProgress,

                        artistName =
                            "Bhai Jarnail Singh Ji",

                        displayLanguage = state.displayLanguage,

                        onInfoClick = {
                            selectedInfoBani = bani
                        },

                        onClick = {

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

            item {

                Spacer(
                    Modifier.height(30.dp)
                )
            }
        }
        }
    }

    /*
     * BOTTOM SHEET
     */

    if (showInfoSheet) {

  ModalBottomSheet(

    onDismissRequest = {
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
                        "Parsed from https://data.gurbaninow.com/".translate(state.displayLanguage)
                )

                Spacer(
                    Modifier.height(30.dp)
                )

            }
        }
    }

    if (selectedInfoBani != null) {
        val selectedBani = selectedInfoBani!!
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

                selectedBani.audio?.let { audio ->
                    HorizontalDivider(color = TextPrimary.copy(alpha = 0.06f), modifier = Modifier.padding(vertical = 12.dp))
                    val localizedArtist = audio.artist.translate(state.displayLanguage)
                    val localizedLicenseLabel = "License".translate(state.displayLanguage)
                    BaniDetailItem(
                        label = "Credits & Attribution".translate(state.displayLanguage),
                        value = "$localizedArtist\n${audio.attribution}\n$localizedLicenseLabel: ${audio.license}",
                        displayLanguage = state.displayLanguage
                    )
                }

                Spacer(Modifier.height(30.dp))
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
    val infinite = rememberInfiniteTransition(label = "analytics_bg")

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
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val floatPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbles"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1200, easing = EaseOutQuad),
        label = "progress"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
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
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Accent.copy(alpha = glow),
                            Gold.copy(alpha = 0.025f)
                        )
                    )
                )
                .clip(RoundedCornerShape(22.dp))
        ) {
            // Background Canvas Animations
            Canvas(modifier = Modifier.matchParentSize()) {
                val width = size.width
                val height = size.height
                if (width > 0f && height > 0f) {
                    when (type) {
                        AnalyticsCardType.Consistency -> {
                            val maxPlantHeight = height * 0.75f
                            val currentPlantHeight = maxPlantHeight * animatedProgress
                            
                            if (currentPlantHeight > 0f) {
                                val path = Path()
                                path.moveTo(width / 2f, height)
                                
                                val swayOffset = sin(wavePhase) * 10f * animatedProgress
                                val ctrlX = width / 2f + swayOffset * 0.5f
                                val ctrlY = height - currentPlantHeight * 0.5f
                                val endX = width / 2f + swayOffset
                                val endY = height - currentPlantHeight
                                
                                path.quadraticTo(ctrlX, ctrlY, endX, endY)
                                
                                drawPath(
                                    path = path,
                                    color = Accent.copy(alpha = 0.35f),
                                    style = Stroke(
                                        width = 6f,
                                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                                    )
                                )
                                drawPath(
                                    path = path,
                                    color = Gold.copy(alpha = 0.75f),
                                    style = Stroke(
                                        width = 2.5f,
                                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                                    )
                                )
                                
                                val leafStages = listOf(0.3f, 0.6f, 0.9f)
                                leafStages.forEachIndexed { index, stage ->
                                    if (animatedProgress >= stage) {
                                        val t = stage
                                        val leafX = (1-t)*(1-t)*(width/2f) + 2*(1-t)*t*ctrlX + t*t*endX
                                        val leafY = (1-t)*(1-t)*height + 2*(1-t)*t*ctrlY + t*t*endY
                                        
                                        val isLeft = index % 2 == 0
                                        val leafLength = 18f
                                        
                                        val leafPath = Path().apply {
                                            moveTo(leafX, leafY)
                                            val tipX = leafX + (if (isLeft) -leafLength else leafLength)
                                            val tipY = leafY - leafLength * 0.4f
                                            
                                            val cp1x = leafX + (if (isLeft) -leafLength * 0.2f else leafLength * 0.2f)
                                            val cp1y = leafY - leafLength * 0.8f
                                            val cp2x = leafX + (if (isLeft) -leafLength * 0.8f else leafLength * 0.8f)
                                            val cp2y = leafY - leafLength * 0.1f
                                            
                                            quadraticTo(cp1x, cp1y, tipX, tipY)
                                            quadraticTo(cp2x, cp2y, leafX, leafY)
                                            close()
                                        }
                                        drawPath(
                                            path = leafPath,
                                            brush = Brush.radialGradient(
                                                colors = listOf(Gold.copy(alpha = 0.8f), Emerald.copy(alpha = 0.35f)),
                                                center = Offset(leafX, leafY),
                                                radius = leafLength * 1.5f
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        AnalyticsCardType.Listening -> {
                            val waterHeight = height * animatedProgress
                            if (waterHeight > 0f) {
                                val wavePath = Path()
                                wavePath.moveTo(0f, height)
                                wavePath.lineTo(0f, height - waterHeight)
                                
                                val waveFrequency = 0.04f
                                val waveAmplitude = 6f
                                for (x in 0..width.toInt() step 4) {
                                    val xF = x.toFloat()
                                    val yF = (height - waterHeight) + sin(xF * waveFrequency + wavePhase) * waveAmplitude
                                    wavePath.lineTo(xF, yF)
                                }
                                wavePath.lineTo(width, height)
                                wavePath.close()
                                
                                drawPath(
                                    path = wavePath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Accent.copy(alpha = 0.22f),
                                            Accent.copy(alpha = 0.02f)
                                        ),
                                        startY = height - waterHeight - waveAmplitude,
                                        endY = height
                                    )
                                )
                                
                                val wavePath2 = Path()
                                wavePath2.moveTo(0f, height)
                                wavePath2.lineTo(0f, height - waterHeight + 2f)
                                for (x in 0..width.toInt() step 4) {
                                    val xF = x.toFloat()
                                    val yF = (height - waterHeight) + cos(xF * waveFrequency + wavePhase + 1.2f) * (waveAmplitude - 2f)
                                    wavePath2.lineTo(xF, yF)
                                }
                                wavePath2.lineTo(width, height)
                                wavePath2.close()
                                
                                drawPath(
                                    path = wavePath2,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Gold.copy(alpha = 0.15f),
                                            Color.Transparent
                                        ),
                                        startY = height - waterHeight - waveAmplitude,
                                        endY = height
                                    )
                                )
                            }
                        }
                        AnalyticsCardType.Today -> {
                            val bubbleCount = 4
                            for (i in 0 until bubbleCount) {
                                val sizeSeed = (i + 1) * 2.5f + 3f
                                val xPos = width * (0.15f + 0.7f * (i.toFloat() / (bubbleCount - 1).coerceAtLeast(1)))
                                val phaseShift = i * 0.25f
                                val bubbleProgress = (floatPhase + phaseShift) % 1f
                                
                                val bubbleY = height - (height * bubbleProgress)
                                val sway = sin(bubbleProgress * 2f * PI.toFloat() + i) * 5f
                                val bubbleAlpha = (1f - bubbleProgress) * 0.22f * animatedProgress
                                
                                if (bubbleAlpha > 0f) {
                                    drawCircle(
                                        color = Accent.copy(alpha = bubbleAlpha),
                                        radius = sizeSeed,
                                        center = Offset(xPos + sway, bubbleY),
                                        style = Stroke(width = 1.2f)
                                    )
                                    drawCircle(
                                        color = Gold.copy(alpha = bubbleAlpha * 0.25f),
                                        radius = sizeSeed * 0.5f,
                                        center = Offset(xPos + sway - sizeSeed * 0.2f, bubbleY - sizeSeed * 0.2f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 16.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Accent,
                                    Gold
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = value,
                    color = if (AppTheme.themeMode == ThemeMode.Light)
                        Color(0xFF1F1B16)
                    else
                        Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = title,
                    color = TextPrimary.copy(alpha = 0.72f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = TextPrimary.copy(alpha = 0.45f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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