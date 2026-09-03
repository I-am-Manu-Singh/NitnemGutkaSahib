package org.audiocular.nitnem.app.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import kotlin.random.Random
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.app.ui.components.MascotBani
import org.audiocular.nitnem.app.ui.components.MascotState
import org.audiocular.nitnem.app.ui.components.NitnemLogo
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun OnboardingPagerScreen(
    state: OnboardingState,
    onStateChange: (OnboardingState) -> Unit,
    onGoogleSignIn: () -> Unit = {},
    onNext: () -> Unit
) {
    val displayLanguage = when (state.selectedLanguage) {
        "Punjabi" -> DisplayLanguage.Punjabi
        "Hindi" -> DisplayLanguage.Hindi
        else -> DisplayLanguage.English
    }

    val pagerState = rememberPagerState(
        pageCount = { 4 }
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.page) {
        if (pagerState.currentPage != state.page) {
            pagerState.animateScrollToPage(state.page)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (state.page != pagerState.currentPage) {
            onStateChange(state.copy(page = pagerState.currentPage))
        }
    }

    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTiming by remember { mutableStateOf("") }
    var timingError by remember { mutableStateOf<String?>(null) }

    val isLight = AppTheme.themeMode == ThemeMode.Light
    val backgroundColor = if (isLight) Background else Color(0xFF0D1117)
    val cardColor = if (isLight) SurfaceColor.copy(alpha = 0.95f) else SurfaceColor.copy(alpha = 0.65f)
    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.68f)
    val borderColor = if (isLight) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.08f)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Gold.copy(alpha = if (isLight) 0.08f else 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(screenWidth.value * 0.2f, screenHeight.value * 0.9f)
                    )
                )
        )

        if (pagerState.currentPage == 3) {
            // Full-screen immersive 3D village & story experience
            FinalSetupPage(
                onContinue = onNext,
                displayLanguage = displayLanguage
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                // Header for setup pages
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 26.dp, vertical = 16.dp)
                ) {
                    Text(text = "Nitnem Gutka".translate(displayLanguage), color = primaryText, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    Text(text = "Focused Gurbani experience designed for peace and consistency.".translate(displayLanguage), color = secondaryText, fontSize = 15.sp, lineHeight = 23.sp)
                }

                Spacer(Modifier.height(16.dp))

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    userScrollEnabled = true
                ) { page ->
                    val scale by animateFloatAsState(
                        targetValue = if (pagerState.currentPage == page) 1f else 0.96f,
                        label = "page_scale"
                    )
                    val alpha by animateFloatAsState(
                        targetValue = if (pagerState.currentPage == page) 1f else 0.50f,
                        label = "page_alpha"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 26.dp, vertical = 8.dp)
                            .scale(scale)
                            .alpha(alpha),
                        shape = RoundedCornerShape(34.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        when (page) {
                            0 -> {
                                LanguageSelectionPage(
                                    state = state,
                                    displayLanguage = displayLanguage,
                                    onStateChange = onStateChange
                                )
                            }
                            1 -> {
                                PreferencesAndWidgetSetupPage(
                                    state = state,
                                    displayLanguage = displayLanguage,
                                    onStateChange = onStateChange,
                                    onOpenTimePicker = { timing ->
                                        timingError = null
                                        selectedTiming = timing
                                        showTimePicker = true
                                    }
                                )
                            }
                            2 -> {
                                AccountAndCloudSyncPage(
                                    displayLanguage = displayLanguage,
                                    onGoogleSignIn = onGoogleSignIn,
                                    onContinueWithoutLogin = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(3)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .height(8.dp)
                                .width(if (pagerState.currentPage == index) 30.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (pagerState.currentPage == index) Gold else Color.White.copy(alpha = 0.14f))
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        if (showTimePicker) {
            val defaultHour = when (selectedTiming) {
                "Amrit Vela" -> 4
                "Rehras" -> 18
                else -> 22
            }

            val timeState = rememberTimePickerState(
                initialHour = defaultHour,
                initialMinute = 0,
                is24Hour = false
            )

            AlertDialog(
                onDismissRequest = {
                    timingError = null
                    showTimePicker = false
                },
                shape = RoundedCornerShape(28.dp),
                containerColor = if (isLight) Color.White else Color(0xFF1A1A1A),
                confirmButton = {
                    TextButton(
                        onClick = {
                            val hour = timeState.hour
                            val valid = when (selectedTiming) {
                                "Amrit Vela" -> hour in 2..8
                                "Rehras" -> hour in 16..21
                                "Sohila" -> hour in 20..23 || hour in 0..2
                                else -> true
                            }

                            if (!valid) {
                                timingError = when (selectedTiming) {
                                    "Amrit Vela" -> "Amrit Vela should be between 2 AM and 8 AM".translate(displayLanguage)
                                    "Rehras" -> "Rehras should be between 4 PM and 9 PM".translate(displayLanguage)
                                    else -> "Sohila should be between 8 PM and 2 AM".translate(displayLanguage)
                                }
                                return@TextButton
                            }

                            timingError = null
                            val formattedHour = when {
                                hour == 0 -> 12
                                hour > 12 -> hour - 12
                                else -> hour
                            }
                            val amPm = if (hour >= 12) "PM" else "AM"
                            val formattedTime = String.format("%02d:%02d %s", formattedHour, timeState.minute, amPm)

                            when (selectedTiming) {
                                "Amrit Vela" -> onStateChange(state.copy(amritTime = formattedTime))
                                "Rehras" -> onStateChange(state.copy(rehrasTime = formattedTime))
                                "Sohila" -> onStateChange(state.copy(sohilaTime = formattedTime))
                            }
                            showTimePicker = false
                        }
                    ) {
                        Text(text = "Done".translate(displayLanguage), color = Gold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            timingError = null
                            showTimePicker = false
                        }
                    ) {
                        Text(text = "Cancel".translate(displayLanguage), color = secondaryText)
                    }
                },
                title = {
                    Column {
                        Text(text = selectedTiming.translate(displayLanguage), color = primaryText, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = when (selectedTiming) {
                                "Amrit Vela" -> "Allowed: 2 AM - 8 AM".translate(displayLanguage)
                                "Rehras" -> "Allowed: 4 PM - 9 PM".translate(displayLanguage)
                                else -> "Allowed: 8 PM - 2 AM".translate(displayLanguage)
                            },
                            color = secondaryText,
                            fontSize = 12.sp
                        )
                        timingError?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(text = it, color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                },
                text = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TimePicker(state = timeState)
                    }
                }
            )
        }
    }
}
@Composable
private fun LanguageSelectionPage(
    state: OnboardingState,
    displayLanguage: DisplayLanguage,
    onStateChange: (OnboardingState) -> Unit
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.68f)
    val accent = Color(0xFFFFC857)

    val options = listOf(
        SelectionItem("English", "", Icons.Default.Language, state.selectedLanguage == "English"),
        SelectionItem("ਪੰਜਾਬੀ", "", Icons.Default.AutoStories, state.selectedLanguage == "Punjabi"),
        SelectionItem("हिंदी", "", Icons.Default.Translate, state.selectedLanguage == "Hindi")
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Select App Language".translate(displayLanguage),
            color = primaryText,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Choose your preferred interface language and reading display settings.".translate(displayLanguage),
            color = secondaryText,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(Modifier.height(4.dp))

        options.forEach { item ->
            SelectionCard(
                item = item,
                accent = accent,
                displayLanguage = displayLanguage,
                onClick = {
                    val mappedLanguage = when (item.title) {
                        "ਪੰਜਾਬੀ" -> "Punjabi"
                        "हिंदी" -> "Hindi"
                        else -> "English"
                    }
                    // Auto-enable recommended toggles for the newly selected language
                    val updatedState = when (mappedLanguage) {
                        "Hindi" -> state.copy(
                            selectedLanguage = "Hindi",
                            showDevanagariTransliteration = true,
                            showRomanTransliteration = false,
                            showEnglishTranslation = false,
                            showPunjabiTranslation = false
                        )
                        "Punjabi" -> state.copy(
                            selectedLanguage = "Punjabi",
                            showDevanagariTransliteration = false,
                            showRomanTransliteration = false,
                            showEnglishTranslation = false,
                            showPunjabiTranslation = true
                        )
                        else -> state.copy(
                            selectedLanguage = "English",
                            showDevanagariTransliteration = false,
                            showRomanTransliteration = true,
                            showEnglishTranslation = true,
                            showPunjabiTranslation = false
                        )
                    }
                    onStateChange(updatedState)
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        // Dynamic Reading Display Setting Toggles based on selected language
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = if (isLight) Color.White.copy(alpha = 0.60f) else Color.White.copy(alpha = 0.04f),
            border = BorderStroke(1.dp, if (isLight) Color.Black.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Reading Display Settings".translate(displayLanguage),
                    color = primaryText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                when (state.selectedLanguage) {
                    "Hindi" -> {
                        LanguageToggleRow(
                            title = "Gurmukhi Script".translate(displayLanguage),
                            subtitle = "Show original Gurmukhi verses".translate(displayLanguage),
                            checked = state.showGurmukhi,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(showGurmukhi = it)) }
                        )
                        LanguageToggleRow(
                            title = "Hindi Transliteration".translate(displayLanguage),
                            subtitle = "Devanagari script for Gurbani".translate(displayLanguage),
                            checked = state.showDevanagariTransliteration,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(showDevanagariTransliteration = it)) }
                        )
                        LanguageToggleRow(
                            title = "Hindi Translation".translate(displayLanguage),
                            subtitle = "Steek / Teeka in Hindi".translate(displayLanguage),
                            checked = state.showPunjabiTranslation || state.showDevanagariTransliteration,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(showPunjabiTranslation = it, showDevanagariTransliteration = true)) }
                        )
                        LanguageToggleRow(
                            title = "Word Meanings".translate(displayLanguage),
                            subtitle = "Show Viakhya / meanings".translate(displayLanguage),
                            checked = state.showWordMeaning,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(showWordMeaning = it)) }
                        )
                    }
                    "Punjabi" -> {
                        LanguageToggleRow(
                            title = "Gurmukhi Script".translate(displayLanguage),
                            subtitle = "Show original Gurmukhi verses".translate(displayLanguage),
                            checked = state.showGurmukhi,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(showGurmukhi = it)) }
                        )
                        LanguageToggleRow(
                            title = "Punjabi Translation".translate(displayLanguage),
                            subtitle = "Steek / Teeka in Gurmukhi".translate(displayLanguage),
                            checked = state.showPunjabiTranslation,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(showPunjabiTranslation = it)) }
                        )
                        LanguageToggleRow(
                            title = "Larivaar Mode".translate(displayLanguage),
                            subtitle = "Continuous Gurmukhi text".translate(displayLanguage),
                            checked = state.larivaarMode,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(larivaarMode = it)) }
                        )
                        LanguageToggleRow(
                            title = "Word Meanings".translate(displayLanguage),
                            subtitle = "Pad Arth / Meanings".translate(displayLanguage),
                            checked = state.showWordMeaning,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(showWordMeaning = it)) }
                        )
                    }
                    else -> {
                        // English
                        LanguageToggleRow(
                            title = "Gurmukhi Script".translate(displayLanguage),
                            subtitle = "Show original Gurmukhi verses".translate(displayLanguage),
                            checked = state.showGurmukhi,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(showGurmukhi = it)) }
                        )
                        LanguageToggleRow(
                            title = "Roman Transliteration".translate(displayLanguage),
                            subtitle = "English phonetics for pronunciation".translate(displayLanguage),
                            checked = state.showRomanTransliteration,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(showRomanTransliteration = it)) }
                        )
                        LanguageToggleRow(
                            title = "English Translation".translate(displayLanguage),
                            subtitle = "English verse-by-verse translation".translate(displayLanguage),
                            checked = state.showEnglishTranslation,
                            accent = accent,
                            onCheckedChange = { onStateChange(state.copy(showEnglishTranslation = it)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    accent: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.60f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                color = primaryText,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = secondaryText,
                fontSize = 11.5.sp,
                lineHeight = 15.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accent,
                uncheckedThumbColor = if (isLight) Color(0xFFBDBDBD) else Color(0xFF757575),
                uncheckedTrackColor = if (isLight) Color(0xFFE0E0E0) else Color(0xFF424242)
            ),
            modifier = Modifier.scale(0.85f)
        )
    }
}

@Composable
private fun SelectionPage(
    title: String,
    accent: Color,
    options: List<SelectionItem>,
    displayLanguage: DisplayLanguage,
    onSelect: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = title,
            color = if (AppTheme.themeMode == ThemeMode.Light) Primary else Color.White,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        options.forEach { item ->
            SelectionCard(item = item, accent = accent, displayLanguage = displayLanguage, onClick = { onSelect(item.title) })
        }
    }
}

@Composable
private fun SelectionCard(
    item: SelectionItem,
    accent: Color,
    displayLanguage: DisplayLanguage,
    onClick: () -> Unit
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.68f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(26.dp),
        color = if (item.selected) accent.copy(alpha = 0.16f) else if (isLight) Color.White.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.05f),
        border = BorderStroke(
            if (item.selected) 1.5.dp else 1.dp,
            if (item.selected) accent else Color.White.copy(alpha = 0.06f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = item.icon, contentDescription = null, tint = accent, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title.translate(displayLanguage), color = primaryText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (item.description.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(text = item.description.translate(displayLanguage), color = secondaryText, fontSize = 13.sp)
                }
            }
            if (item.selected) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = accent)
            } else {
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = secondaryText)
            }
        }
    }
}



private data class SelectionItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val selected: Boolean
)

@Composable
private fun PreferencesAndWidgetSetupPage(
    state: OnboardingState,
    displayLanguage: DisplayLanguage,
    onStateChange: (OnboardingState) -> Unit,
    onGoogleSignIn: () -> Unit = {},
    onContinueWithoutLogin: () -> Unit = {},
    onOpenTimePicker: (String) -> Unit
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.68f)
    val pinWidget = org.audiocular.nitnem.utils.rememberPinWidgetLauncher()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Personalize Experience".translate(displayLanguage),
            color = primaryText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // 1. THEME SECTION
        Text(
            text = "App Theme".translate(displayLanguage),
            color = primaryText,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val themes = listOf(
                Triple("Light", "Light".translate(displayLanguage), Icons.Default.LightMode),
                Triple("Dark", "Dark".translate(displayLanguage), Icons.Default.DarkMode),
                Triple("AMOLED", "AMOLED".translate(displayLanguage), Icons.Default.NightlightRound)
            )
            themes.forEach { (key, label, icon) ->
                val selected = state.selectedTheme == key
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val mode = when (key) {
                                "Light" -> ThemeMode.Light
                                "AMOLED" -> ThemeMode.Amoled
                                else -> ThemeMode.Dark
                            }
                            AppTheme.themeMode = mode
                            onStateChange(state.copy(selectedTheme = key))
                        },
                    shape = RoundedCornerShape(18.dp),
                    color = if (selected) Gold.copy(alpha = 0.20f) else if (isLight) Color.White.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(if (selected) 1.5.dp else 1.dp, if (selected) Gold else if (isLight) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = if (selected) Gold else secondaryText, modifier = Modifier.size(20.dp))
                        Text(text = label, color = if (selected) Gold else primaryText, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // 2. PRAYER TIMINGS
        Text(
            text = "Prayer Reminders".translate(displayLanguage),
            color = primaryText,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        val timings = listOf(
            Triple("Amrit Vela", state.amritTime, Icons.Default.WbSunny),
            Triple("Rehras", state.rehrasTime, Icons.Default.WbTwilight),
            Triple("Sohila", state.sohilaTime, Icons.Default.NightlightRound)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            timings.forEach { (name, timeStr, icon) ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenTimePicker(name) },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isLight) Color.White.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, if (isLight) Color.Black.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(imageVector = icon, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
                            Text(text = name.translate(displayLanguage), color = primaryText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Gold.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = timeStr,
                                color = Gold,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // 3. HOME SCREEN WIDGET
        Text(
            text = "Home Screen Widget".translate(displayLanguage),
            color = primaryText,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = if (isLight) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.06f),
            border = BorderStroke(1.dp, Gold.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Widgets, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                    Column {
                        Text(
                            text = "Daily Nitnem Mascot Widget".translate(displayLanguage),
                            color = primaryText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Stay connected with live paath progress right on your home screen.".translate(displayLanguage),
                            color = secondaryText,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Button(
                    onClick = pinWidget,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Add Widget to Home Screen".translate(displayLanguage),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountAndCloudSyncPage(
    displayLanguage: DisplayLanguage,
    onGoogleSignIn: () -> Unit,
    onContinueWithoutLogin: () -> Unit
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.68f)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Account & Cloud Sync".translate(displayLanguage),
            color = primaryText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Choose how you would like to use Nitnem Gutka Sahib.".translate(displayLanguage),
            color = secondaryText,
            fontSize = 13.5.sp,
            lineHeight = 18.sp
        )

        // Option A: Sign in with Google
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = if (isLight) Color(0xFFF7F8FA) else Color.White.copy(alpha = 0.05f),
            border = BorderStroke(1.2.dp, Gold.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Gold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sign in with Google".translate(displayLanguage),
                            color = primaryText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Cloud backup & community features".translate(displayLanguage),
                            color = secondaryText,
                            fontSize = 12.sp
                        )
                    }
                }

                // Benefits of Logging In
                Column(
                    modifier = Modifier.padding(start = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Automatic data & streak restoration across all devices".translate(displayLanguage),
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Keep track of friends & family in real-time Sangat leaderboards".translate(displayLanguage),
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Sync recitation minutes, XP milestones & prayer history".translate(displayLanguage),
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                    }
                }

                Button(
                    onClick = onGoogleSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Sign in with Google".translate(displayLanguage),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Option B: Continue Without Login
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = if (isLight) Color(0xFFFAFAFA) else Color.White.copy(alpha = 0.03f),
            border = BorderStroke(1.dp, if (isLight) Color.Black.copy(alpha = 0.07f) else Color.White.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isLight) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = primaryText.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Continue Without Login".translate(displayLanguage),
                            color = primaryText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "100% Private, offline-first experience".translate(displayLanguage),
                            color = secondaryText,
                            fontSize = 12.sp
                        )
                    }
                }

                // Benefits of Without Sign In
                Column(
                    modifier = Modifier.padding(start = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "100% Privacy with 0 cloud tracking or personal data collection".translate(displayLanguage),
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Works entirely offline — all bookmarks & streaks saved locally on your phone".translate(displayLanguage),
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "No account, email, or credentials required to read or listen".translate(displayLanguage),
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                    }
                }

                OutlinedButton(
                    onClick = onContinueWithoutLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isLight) Color.Black.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.20f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = primaryText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Continue Offline / Private Mode".translate(displayLanguage),
                        color = primaryText,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp
                    )
                }
            }
        }
    }
}


