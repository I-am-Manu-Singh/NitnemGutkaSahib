package org.audiocular.nitnem.app.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.app.ui.components.MascotBani
import org.audiocular.nitnem.app.ui.components.MascotState

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun OnboardingPagerScreen(
    state: OnboardingState,
    onStateChange: (OnboardingState) -> Unit,
    onNext: () -> Unit
) {
    val displayLanguage = when (state.selectedLanguage) {
        "Punjabi" -> DisplayLanguage.Punjabi
        "Hindi" -> DisplayLanguage.Hindi
        else -> DisplayLanguage.English
    }

    val pagerState = rememberPagerState(
        pageCount = { 5 }
    )

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

    val backgroundGradient = when (AppTheme.themeMode) {
        ThemeMode.Light -> listOf(Cream, Mist, Gold.copy(alpha = 0.10f))
        ThemeMode.Dark -> listOf(Color(0xFF0E1014), Color(0xFF171A20), Color(0xFF1F242B))
        ThemeMode.Amoled -> listOf(Color.Black, Color(0xFF040404), Color(0xFF090909))
    }

    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.68f)
    val cardColor = if (isLight) Color.White.copy(alpha = 0.86f) else Color.White.copy(alpha = 0.06f)
    val borderColor = if (isLight) Color.Black.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.06f)

    // BoxWithConstraints exposes dynamic maximum bounds to accurately compute relative offset alignments
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(backgroundGradient))
    ) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Gold.copy(alpha = if (isLight) 0.24f else 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(screenWidth * 0.8f, screenHeight * 0.1f)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (isLight) Accent.copy(alpha = 0.08f) else Color(0xFF1F242B),
                            Color.Transparent
                        ),
                        center = Offset(screenWidth * 0.2f, screenHeight * 0.8f)
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val headerAlpha by animateFloatAsState(
                targetValue = if (pagerState.currentPage == 4) 0f else 1f,
                animationSpec = tween(300),
                label = "header_fade"
            )

            if (headerAlpha > 0f) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                        .alpha(headerAlpha)
                ) {
                    Spacer(Modifier.height(48.dp))
                    Text(text = "ੴ", fontSize = 54.sp, color = Gold, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text(text = "Personalize Nitnem".translate(displayLanguage), color = primaryText, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(text = "Focused Gurbani experience designed for peace and consistency.".translate(displayLanguage), color = secondaryText, fontSize = 15.sp, lineHeight = 23.sp)
                }
            } else {
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = pagerState.currentPage != 4
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
                        .padding(
                            horizontal = if (page == 4) 0.dp else 26.dp, 
                            vertical = if (page == 4) 0.dp else 8.dp
                        )
                        .scale(scale)
                        .alpha(alpha),
                    shape = if (page == 4) RoundedCornerShape(0.dp) else RoundedCornerShape(34.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (page == 4) Color.Transparent else cardColor
                    ),
                    border = if (page == 4) null else BorderStroke(1.dp, borderColor)
                ) {
                    when (page) {
                        0 -> {
                            SelectionPage(
                                title = "Select App Language".translate(displayLanguage),
                                accent = Color(0xFFFFC857),
                                options = listOf(
                                    SelectionItem("English", "", Icons.Default.Language, state.selectedLanguage == "English"),
                                    SelectionItem("ਪੰਜਾਬੀ", "", Icons.Default.AutoStories, state.selectedLanguage == "Punjabi"),
                                    SelectionItem("हिंदी", "", Icons.Default.Translate, state.selectedLanguage == "Hindi")
                                ),
                                displayLanguage = displayLanguage,
                                onSelect = { language ->
                                    val mappedLanguage = when (language) {
                                        "ਪੰਜਾਬੀ" -> "Punjabi"
                                        "हिंदी" -> "Hindi"
                                        else -> "English"
                                    }
                                    onStateChange(state.copy(selectedLanguage = mappedLanguage))
                                }
                            )
                        }
                        1 -> {
                            SelectionPage(
                                title = "Theme".translate(displayLanguage),
                                accent = Color(0xFFB388FF),
                                options = listOf(
                                    SelectionItem("Light".translate(displayLanguage), "Warm appearance".translate(displayLanguage), Icons.Default.LightMode, state.selectedTheme == "Light"),
                                    SelectionItem("Dark".translate(displayLanguage), "Balanced contrast".translate(displayLanguage), Icons.Default.DarkMode, state.selectedTheme == "Dark"),
                                    SelectionItem("AMOLED".translate(displayLanguage), "Pure black".translate(displayLanguage), Icons.Default.NightlightRound, state.selectedTheme == "AMOLED")
                                ),
                                displayLanguage = displayLanguage,
                                onSelect = { selectedTheme ->
                                    val mode = when (selectedTheme) {
                                        "Light" -> ThemeMode.Light
                                        "AMOLED" -> ThemeMode.Amoled
                                        else -> ThemeMode.Dark
                                    }
                                    AppTheme.themeMode = mode
                                    onStateChange(state.copy(selectedTheme = selectedTheme))
                                }
                            )
                        }
                        2 -> {
                            SelectionPage(
                                title = "Prayer Timings".translate(displayLanguage),
                                accent = Color(0xFFFFD166),
                                options = listOf(
                                    SelectionItem("Amrit Vela", state.amritTime, Icons.Default.WbSunny, false),
                                    SelectionItem("Rehras", state.rehrasTime, Icons.Default.WbTwilight, false),
                                    SelectionItem("Sohila", state.sohilaTime, Icons.Default.NightlightRound, false)
                                ),
                                displayLanguage = displayLanguage,
                                onSelect = { timing ->
                                    timingError = null
                                    selectedTiming = timing
                                    showTimePicker = true
                                }
                            )
                        }
                        3 -> {
                            WidgetConfiguratorPage(
                                state = state,
                                displayLanguage = displayLanguage,
                                onSelectTheme = { theme -> onStateChange(state.copy(widgetTheme = theme)) },
                                onSelectContent = { content -> onStateChange(state.copy(widgetContent = content)) }
                            )
                        }
                        4 -> {
                            FinalSetupPage(
                                onContinue = onNext,
                                displayLanguage = displayLanguage
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            val footerAlpha by animateFloatAsState(
                targetValue = if (pagerState.currentPage == 4) 0f else 1f,
                animationSpec = tween(300),
                label = "footer_fade"
            )

            if (footerAlpha > 0f) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(footerAlpha),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(4) { index ->
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
            } else {
                Spacer(Modifier.height(8.dp))
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
private fun SelectionPage(
    title: String,
    accent: Color,
    options: List<SelectionItem>,
    displayLanguage: DisplayLanguage,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
@Composable
private fun FinalSetupPage(
    onContinue: () -> Unit,
    displayLanguage: DisplayLanguage
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.68f)

    data class BaniSlide(
        val mascotState: MascotState,
        val heading: String,
        val body: String,
        val accentColor: Color
    )

    val slides = remember {
        listOf(
            BaniSlide(MascotState.Reading, "Meet Bani".translate(displayLanguage), "In the quiet, sacred hours of Amrit Vela, a small golden bird named Bani wakes up — her wings painted with the warm orange hues of a breaking dawn, carrying an ancient crimson Gutka Sahib close to her chest.".translate(displayLanguage), Gold),
            BaniSlide(MascotState.Idle, "Your Guardian of Consistency".translate(displayLanguage), "Bani's entire purpose is to guard mindfulness and consistency. She flew from her digital sanctuary to sit right on your home screen — ready to guide you through your daily Nitnem, one line at a time.".translate(displayLanguage), Color(0xFFFFD166)),
            BaniSlide(MascotState.Meditating, "Celebrating Your Streak".translate(displayLanguage), "When you build a prayer streak, a vibrant golden aura ignites around Bani. She meditates in a state of glowing calm — a visual reminder that consistency is its own reward.".translate(displayLanguage), Color(0xFFFFC107)),
            BaniSlide(MascotState.Celebrating, "Your Learning Companion".translate(displayLanguage), "On days when you dive deeper into Gurbani meanings, Bani transforms into an interactive guide — testing your learning through quizzes, leaping with joy for every correct answer.".translate(displayLanguage), Color(0xFF69F0AE)),
            BaniSlide(MascotState.Sleeping, "She Never Forgets".translate(displayLanguage), "If you miss Sohila Sahib before sleep, she waits — awake, book open. If your streak is safe, she sleeps soundly. Together, one day at a time, you and Bani will build an unbroken path of peace.".translate(displayLanguage), Color(0xFF7C83FD))
        )
    }

    var currentSlideIndex by remember { mutableStateOf(0) }
    val currentSlide = slides[currentSlideIndex]
    val isLastSlide = currentSlideIndex == slides.lastIndex

    val slideAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 350),
        label = "slide_fade"
    )

    // Tracks overall movement accumulation during a single swipe gesture instance
    var totalDragAmount by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
            .background(Color.Transparent)
            .run {
                this.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Intercepts parent container click tags */ }
            }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    // Accumulate pixel changes continuously safely
                    totalDragAmount += delta
                },
                onDragStopped = { velocity ->
                    // FIX: Process vector intent ONCE when finger lifts off the panel
                    val minimumSwipeThreshold = 120f // Pixels required to trigger a slide change
                    
                    if (totalDragAmount < -minimumSwipeThreshold || velocity < -400f) {
                        if (currentSlideIndex < slides.lastIndex) {
                            currentSlideIndex++
                        }
                    } else if (totalDragAmount > minimumSwipeThreshold || velocity > 400f) {
                        if (currentSlideIndex > 0) {
                            currentSlideIndex--
                        }
                    }
                    // Reset accumulator tracking instantly for the next interaction run
                    totalDragAmount = 0f
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(230.dp)
                .alpha(slideAlpha),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(currentSlide.accentColor.copy(alpha = 0.32f), currentSlide.accentColor.copy(alpha = 0.0f))
                        )
                    )
            )
            MascotBani(state = currentSlide.mascotState, modifier = Modifier.size(175.dp))
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(slideAlpha)
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(32.dp),
            color = if (isLight) Color.White.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.04f),
            border = BorderStroke(1.dp, if (isLight) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.06f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp), 
                    color = currentSlide.accentColor.copy(alpha = 0.14f),
                    border = BorderStroke(0.5.dp, currentSlide.accentColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${currentSlideIndex + 1} / ${slides.size}", 
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp), 
                        color = currentSlide.accentColor, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(Modifier.height(18.dp))
                Text(text = currentSlide.heading, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = primaryText, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(text = currentSlide.body, fontSize = 14.sp, lineHeight = 22.sp, color = secondaryText, textAlign = TextAlign.Center)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(slides.size) { i ->
                    val isActive = currentSlideIndex == i
                    val dotWidth by animateDpAsState(targetValue = if (isActive) 24.dp else 8.dp, animationSpec = spring(stiffness = 400f), label = "dot_width")
                    Box(
                        modifier = Modifier
                            .height(6.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(if (isActive) currentSlide.accentColor else if (isLight) Color.Black.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.18f))
                    )
                }
            }
            
            Spacer(Modifier.height(28.dp))
            
            if (isLastSlide) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(text = "Begin Your Journey".translate(displayLanguage), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .clickable { currentSlideIndex++ }
                        .padding(horizontal = 26.dp, vertical = 14.dp)
                ) {
                    Text(text = "Next".translate(displayLanguage), color = Gold, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Spacer(Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                }
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
private fun WidgetConfiguratorPage(
    state: OnboardingState,
    displayLanguage: DisplayLanguage,
    onSelectTheme: (String) -> Unit,
    onSelectContent: (String) -> Unit
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val primaryText = if (isLight) Primary else Color.White
    val secondaryText = if (isLight) TextPrimary.copy(alpha = 0.70f) else Color.White.copy(alpha = 0.68f)

    // Standardize saved theme names to simply "Light" and "Dark"
    val currentTheme = if (state.widgetTheme == "Light" || state.widgetTheme == "Classic Gold") "Light" else "Dark"
    val currentContent = if (state.widgetContent == "Mascot Assistant") "Mascot Assistant" else "Meditation & Quote"

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Personalize Widget".translate(displayLanguage), color = primaryText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Configure your homescreen widget to fit your daily style.".translate(displayLanguage), color = secondaryText, fontSize = 13.sp, lineHeight = 18.sp)
        
        WidgetMockPreview(widgetTheme = currentTheme, widgetContent = currentContent, displayLanguage = displayLanguage)
        
        Spacer(Modifier.height(4.dp))
        Text(text = "Widget Theme".translate(displayLanguage), color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WidgetSelectorItem(title = "Light".translate(displayLanguage), selected = currentTheme == "Light", onClick = { onSelectTheme("Light") }, modifier = Modifier.weight(1f))
            WidgetSelectorItem(title = "Dark".translate(displayLanguage), selected = currentTheme == "Dark", onClick = { onSelectTheme("Dark") }, modifier = Modifier.weight(1f))
        }

        Text(text = "Widget Content".translate(displayLanguage), color = primaryText, fontSize = 15.sp, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WidgetSelectorItem(title = "Mascot".translate(displayLanguage), selected = currentContent == "Mascot Assistant", onClick = { onSelectContent("Mascot Assistant") }, modifier = Modifier.weight(1f))
            WidgetSelectorItem(title = "Reflection & Progress".translate(displayLanguage), selected = currentContent == "Meditation & Quote", onClick = { onSelectContent("Meditation & Quote") }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun WidgetMockPreview(
    widgetTheme: String,
    widgetContent: String,
    displayLanguage: DisplayLanguage
) {
    val isDark = AppTheme.themeMode != ThemeMode.Light

    val (bg, border, textPrimary, textSecondary, accentColor) = when (widgetTheme) {
        "Light" -> Quintuple(Color(0xFFFDFBF7), BorderStroke(1.dp, Gold.copy(alpha = 0.5f)), Primary, TextPrimary.copy(alpha = 0.70f), Gold)
        else -> Quintuple(Color(0xFF0F1115), BorderStroke(1.dp, Gold), Color.White, Color.White.copy(alpha = 0.65f), Gold)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().height(148.dp),
        shape = RoundedCornerShape(24.dp),
        color = bg,
        border = border
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            when (widgetContent) {
                "Mascot Assistant" -> {
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Brush.radialGradient(listOf(accentColor.copy(alpha = 0.55f), accentColor.copy(alpha = 0.20f)))), contentAlignment = Alignment.Center) {
                            Text("🦅", fontSize = 28.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Daily Nitnem".translate(displayLanguage), color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(3.dp))
                            Text(text = "Begin your Nitnem today 🌅".translate(displayLanguage), color = textSecondary, fontSize = 11.sp)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Japji".translate(displayLanguage), "Jaap".translate(displayLanguage), "Chaupai".translate(displayLanguage)).forEach { name ->
                                    Box(modifier = Modifier.background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text(name, color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(modifier = Modifier.background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)).padding(horizontal = 8.dp, vertical = 5.dp)) {
                            Text("🔥 0", color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                else -> {
                    // Meditation & Quote
                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Text("✨", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "ੴ Satnam Waheguru", color = textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(2.dp))
                            Text(text = "Reflection • Upcoming: Japji Sahib".translate(displayLanguage), color = textSecondary, fontSize = 11.sp)
                        }
                        Box(modifier = Modifier.background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(100.dp)).padding(horizontal = 10.dp, vertical = 5.dp)) {
                            Text("🔥 0/5", color = textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetSelectorItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLight = AppTheme.themeMode == ThemeMode.Light
    val primaryText = if (isLight) Primary else Color.White
    val accent = Gold

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = if (selected) accent.copy(alpha = 0.16f) else if (isLight) Color.White.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.05f),
        border = BorderStroke(
            if (selected) 1.5.dp else 1.dp,
            if (selected) accent else Color.White.copy(alpha = 0.06f)
        )
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(title, color = primaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        }
    }
}

private data class Quintuple<A, B, C, D, E>(
    val first: A, val second: B, val third: C, val fourth: D, val fifth: E
)