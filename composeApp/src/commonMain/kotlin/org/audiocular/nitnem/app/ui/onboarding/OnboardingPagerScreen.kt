package org.audiocular.nitnem.app.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

    // Sync pager state from VM
    LaunchedEffect(state.page) {
        if (pagerState.currentPage != state.page) {
            pagerState.animateScrollToPage(state.page)
        }
    }

    // Sync VM state from pager swipes
    LaunchedEffect(pagerState.currentPage) {
        if (state.page != pagerState.currentPage) {
            onStateChange(state.copy(page = pagerState.currentPage))
        }
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    var selectedTiming by remember {
        mutableStateOf("")
    }

    /*
     * FIX:
     * STORE INVALID STATE MESSAGE
     *
     * Because humans absolutely insist
     * on setting Amrit Vela at 7 PM.
     */

    var timingError by remember {
        mutableStateOf<String?>(null)
    }

    val isLight =
        AppTheme.themeMode == ThemeMode.Light

    val backgroundGradient = when (AppTheme.themeMode) {

        ThemeMode.Light -> {

            listOf(
                Cream,
                Mist,
                Gold.copy(alpha = 0.10f)
            )
        }

        ThemeMode.Dark -> {

            listOf(
                Color(0xFF0E1014),
                Color(0xFF171A20),
                Color(0xFF1F242B)
            )
        }

        ThemeMode.Amoled -> {

            listOf(
                Color.Black,
                Color(0xFF040404),
                Color(0xFF090909)
            )
        }
    }

    val primaryText =
        if (isLight)
            Primary
        else
            Color.White

    val secondaryText =
        if (isLight)
            TextPrimary.copy(alpha = 0.70f)
        else
            Color.White.copy(alpha = 0.68f)

    val cardColor =
        if (isLight)
            Color.White.copy(alpha = 0.86f)
        else
            Color.White.copy(alpha = 0.06f)

    val borderColor =
        if (isLight)
            Color.Black.copy(alpha = 0.05f)
        else
            Color.White.copy(alpha = 0.06f)

    Box(

        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    backgroundGradient
                )
            )
    ) {

        /*
         * BACKGROUND GLOW
         */

        Box(

            modifier = Modifier
                .size(360.dp)
                .offset(
                    x = 180.dp,
                    y = (-40).dp
                )
                .background(

                    Brush.radialGradient(

                        colors = listOf(

                            Gold.copy(alpha = 0.16f),

                            Gold.copy(alpha = 0.05f),

                            Color.Transparent
                        )
                    ),

                    shape = CircleShape
                )
        )

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp)
        ) {

            Spacer(
                Modifier.height(48.dp)
            )

            Text(

                text = "ੴ",

                fontSize = 54.sp,

                color = Gold,

                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(12.dp)
            )

            Text(

                text = "Personalize Nitnem".translate(displayLanguage),

                color = primaryText,

                fontSize = 30.sp,

                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(

                text =
                    "Focused Gurbani experience designed for peace and consistency.".translate(displayLanguage),

                color = secondaryText,

                fontSize = 15.sp,

                lineHeight = 23.sp
            )

            Spacer(
                Modifier.height(28.dp)
            )

            HorizontalPager(

                state = pagerState,

                modifier = Modifier.weight(1f)
            ) { page ->

                val scale by animateFloatAsState(

                    targetValue =

                        if (pagerState.currentPage == page)
                            1f
                        else
                            0.96f,

                    label = ""
                )

                val alpha by animateFloatAsState(

                    targetValue =

                        if (pagerState.currentPage == page)
                            1f
                        else
                            0.50f,

                    label = ""
                )

                Card(

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 4.dp,
                            vertical = 8.dp
                        )
                        .scale(scale)
                        .alpha(alpha),

                    shape = RoundedCornerShape(34.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor = cardColor
                        ),

                    border =
                        BorderStroke(
                            1.dp,
                            borderColor
                        )
                ) {

                    when (page) {

                        /*
                         * LANGUAGE
                         */

                        0 -> {

                            SelectionPage(

                                title = "Select App Language".translate(displayLanguage),

                                accent =
                                    Color(0xFFFFC857),

                                options = listOf(

                                    SelectionItem(
                                        "English",
                                        "",
                                        Icons.Default.Language,
                                        state.selectedLanguage == "English"
                                    ),

                                    SelectionItem(
                                        "ਪੰਜਾਬੀ",
                                        "",
                                        Icons.Default.AutoStories,
                                        state.selectedLanguage == "Punjabi"
                                    ),

                                    SelectionItem(
                                        "हिंदी",
                                        "",
                                        Icons.Default.Translate,
                                        state.selectedLanguage == "Hindi"
                                    )
                                ),

                                displayLanguage = displayLanguage,

                                onSelect = { language ->

                                    val mappedLanguage =
                                        when (language) {

                                            "ਪੰਜਾਬੀ" -> "Punjabi"

                                            "हिंदी" -> "Hindi"

                                            else -> "English"
                                        }

                                    onStateChange(
                                        state.copy(
                                            selectedLanguage =
                                                mappedLanguage
                                        )
                                    )
                                }
                            )
                        }

                        /*
                         * THEME
                         */

                        1 -> {

                            SelectionPage(

                                title = "Theme".translate(displayLanguage),

                                accent =
                                    Color(0xFFB388FF),

                                options = listOf(

                                    SelectionItem(
                                        "Light",
                                        "Warm appearance".translate(displayLanguage),
                                        Icons.Default.LightMode,
                                        state.selectedTheme == "Light"
                                    ),

                                    SelectionItem(
                                        "Dark",
                                        "Balanced contrast".translate(displayLanguage),
                                        Icons.Default.DarkMode,
                                        state.selectedTheme == "Dark"
                                    ),

                                    SelectionItem(
                                        "AMOLED",
                                        "Pure black".translate(displayLanguage),
                                        Icons.Default.NightlightRound,
                                        state.selectedTheme == "AMOLED"
                                    )
                                ),

                                displayLanguage = displayLanguage,

                                onSelect = { selectedTheme ->

                                    val mode = when (selectedTheme) {

                                        "Light" ->
                                            ThemeMode.Light

                                        "AMOLED" ->
                                            ThemeMode.Amoled

                                        else ->
                                            ThemeMode.Dark
                                    }

                                    AppTheme.themeMode = mode

                                    onStateChange(
                                        state.copy(
                                            selectedTheme =
                                                selectedTheme
                                        )
                                    )
                                }
                            )
                        }

                        /*
                         * PRAYER TIMINGS
                         */

                        2 -> {

                            SelectionPage(

                                title = "Prayer Timings".translate(displayLanguage),

                                accent =
                                    Color(0xFFFFD166),

                                options = listOf(

                                    SelectionItem(
                                        "Amrit Vela",
                                        state.amritTime,
                                        Icons.Default.WbSunny,
                                        false
                                    ),

                                    SelectionItem(
                                        "Rehras",
                                        state.rehrasTime,
                                        Icons.Default.WbTwilight,
                                        false
                                    ),

                                    SelectionItem(
                                        "Sohila",
                                        state.sohilaTime,
                                        Icons.Default.NightlightRound,
                                        false
                                    )
                                ),

                                displayLanguage = displayLanguage,

                                onSelect = { timing ->

                                    timingError = null

                                    selectedTiming = timing

                                    showTimePicker = true
                                }
                            )
                        }

                        /*
                         * WIDGET CONFIGURATOR
                         */

                        3 -> {

                            WidgetConfiguratorPage(
                                state = state,
                                displayLanguage = displayLanguage,
                                onSelectTheme = { theme ->
                                    onStateChange(state.copy(widgetTheme = theme))
                                },
                                onSelectContent = { content ->
                                    onStateChange(state.copy(widgetContent = content))
                                }
                            )
                        }

                        /*
                         * FINAL
                         */

                        4 -> {

                            FinalSetupPage(
                                onContinue = onNext,
                                displayLanguage = displayLanguage
                            )
                        }
                    }
                }
            }

            Spacer(
                Modifier.height(20.dp)
            )

            /*
             * PAGE INDICATORS
             */

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.Center
            ) {

                repeat(5) { index ->

                    Box(

                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .height(8.dp)
                            .width(

                                if (
                                    pagerState.currentPage == index
                                )
                                    30.dp
                                else
                                    8.dp
                            )
                            .clip(CircleShape)
                            .background(

                                if (
                                    pagerState.currentPage == index
                                )
                                    Gold
                                else
                                    Color.White.copy(alpha = 0.14f)
                            )
                    )
                }
            }

            Spacer(
                Modifier.height(24.dp)
            )
        }

        /*
         * TIME PICKER
         */

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

                shape =
                    RoundedCornerShape(28.dp),

                containerColor =

                    if (isLight)
                        Color.White
                    else
                        Color(0xFF1A1A1A),

                confirmButton = {

                    TextButton(

                        onClick = {

                            val hour =
                                timeState.hour

                            val valid = when (selectedTiming) {

                                "Amrit Vela" ->
                                    hour in 2..8

                                "Rehras" ->
                                    hour in 16..21

                                "Sohila" ->
                                    hour in 20..23 ||
                                        hour in 0..2

                                else -> true
                            }

                            if (!valid) {

                                timingError =
                                    when (selectedTiming) {

                                        "Amrit Vela" ->
                                            "Amrit Vela should be between 2 AM and 8 AM".translate(displayLanguage)

                                        "Rehras" ->
                                            "Rehras should be between 4 PM and 9 PM".translate(displayLanguage)

                                        else ->
                                            "Sohila should be between 8 PM and 2 AM".translate(displayLanguage)
                                    }

                                return@TextButton
                            }

                            timingError = null

                            /*
                             * FIX:
                             * ACTUAL 12H FORMAT
                             *
                             * Before this your app stored
                             * cursed military clock strings
                             * while pretending to be 12-hour UI.
                             */

                            val formattedHour =

                                when {

                                    hour == 0 -> 12

                                    hour > 12 -> hour - 12

                                    else -> hour
                                }

                            val amPm =

                                if (hour >= 12)
                                    "PM"
                                else
                                    "AM"

                            val formattedTime =
                                String.format(

                                    "%02d:%02d %s",

                                    formattedHour,

                                    timeState.minute,

                                    amPm
                                )

                            when (selectedTiming) {

                                "Amrit Vela" -> {

                                    onStateChange(
                                        state.copy(
                                            amritTime =
                                                formattedTime
                                        )
                                    )
                                }

                                "Rehras" -> {

                                    onStateChange(
                                        state.copy(
                                            rehrasTime =
                                                formattedTime
                                        )
                                    )
                                }

                                "Sohila" -> {

                                    onStateChange(
                                        state.copy(
                                            sohilaTime =
                                                formattedTime
                                        )
                                    )
                                }
                            }

                            showTimePicker = false
                        }
                    ) {

                         Text(

                            text = "Done".translate(displayLanguage),

                            color = Gold
                        )
                    }
                },

                dismissButton = {

                    TextButton(

                        onClick = {

                            timingError = null

                            showTimePicker = false
                        }
                    ) {

                         Text(

                            text = "Cancel".translate(displayLanguage),

                            color = secondaryText
                        )
                    }
                },

                title = {

                    Column {

                        Text(

                            text = selectedTiming.translate(displayLanguage),

                            color = primaryText,

                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            Modifier.height(6.dp)
                        )

                        Text(

                            text =
                                when (selectedTiming) {

                                    "Amrit Vela" ->
                                        "Allowed: 2 AM - 8 AM".translate(displayLanguage)

                                    "Rehras" ->
                                        "Allowed: 4 PM - 9 PM".translate(displayLanguage)

                                    else ->
                                        "Allowed: 8 PM - 2 AM".translate(displayLanguage)
                                },

                            color = secondaryText,

                            fontSize = 12.sp
                        )

                        timingError?.let {

                            Spacer(
                                Modifier.height(8.dp)
                            )

                            Text(

                                text = it,

                                color = Color.Red,

                                fontSize = 12.sp,

                                fontWeight =
                                    FontWeight.Medium
                            )
                        }
                    }
                },

                text = {

                    Box(

                        modifier =
                            Modifier.fillMaxWidth(),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        TimePicker(
                            state = timeState
                        )
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

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        Text(

            text = title,

            color =
                if (AppTheme.themeMode == ThemeMode.Light)
                    Primary
                else
                    Color.White,

            fontSize = 27.sp,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            Modifier.height(6.dp)
        )

        options.forEach { item ->

            SelectionCard(

                item = item,

                accent = accent,

                displayLanguage = displayLanguage,

                onClick = {

                    onSelect(item.title)
                }
            )
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

    val isLight =
        AppTheme.themeMode == ThemeMode.Light

    val primaryText =
        if (isLight) Primary else Color.White

    val secondaryText =
        if (isLight)
            TextPrimary.copy(alpha = 0.70f)
        else
            Color.White.copy(alpha = 0.68f)

    Surface(

        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },

        shape = RoundedCornerShape(26.dp),

        color =
            if (item.selected)
                accent.copy(alpha = 0.16f)
            else if (isLight)
                Color.White.copy(alpha = 0.72f)
            else
                Color.White.copy(alpha = 0.05f),

        border =
            BorderStroke(
                if (item.selected) 1.5.dp else 1.dp,
                if (item.selected)
                    accent
                else
                    Color.White.copy(alpha = 0.06f)
            )
    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 16.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(

                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        accent.copy(alpha = 0.14f)
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector = item.icon,

                    contentDescription = null,

                    tint = accent,

                    modifier =
                        Modifier.size(24.dp)
                )
            }

            Spacer(
                Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = item.title.translate(displayLanguage),

                    color = primaryText,

                    fontWeight = FontWeight.Bold,

                    fontSize = 16.sp
                )

                if (item.description.isNotEmpty()) {

                    Spacer(
                        Modifier.height(4.dp)
                    )

                    Text(

                        text = item.description.translate(displayLanguage),

                        color = secondaryText,

                        fontSize = 13.sp
                    )
                }
            }

            if (item.selected) {

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = accent
                )

            } else {

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = secondaryText
                )
            }
        }
    }
}

@Composable
private fun FinalSetupPage(
    onContinue: () -> Unit,
    displayLanguage: DisplayLanguage
) {

    val isLight =
        AppTheme.themeMode == ThemeMode.Light

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Box(

            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(
                    Gold.copy(alpha = 0.16f)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(

                text = "ੴ",

                fontSize = 54.sp,

                color = Gold,

                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            Modifier.height(28.dp)
        )

        Text(

            text = "Ready".translate(displayLanguage),

            fontSize = 32.sp,

            fontWeight = FontWeight.Bold,

            color =
                if (isLight)
                    Primary
                else
                    Color.White
        )

        Spacer(
            Modifier.height(14.dp)
        )

        Text(

            text =
                "Your Nitnem experience has been prepared.".translate(displayLanguage),

            textAlign = TextAlign.Center,

            color =
                if (isLight)
                    TextPrimary.copy(alpha = 0.72f)
                else
                    Color.White.copy(alpha = 0.68f),

            fontSize = 15.sp,

            lineHeight = 24.sp
        )

        Spacer(
            Modifier.height(42.dp)
        )

        FilledIconButton(

            onClick = onContinue,

            modifier =
                Modifier.size(76.dp),

            shape = CircleShape,

            colors =
                IconButtonDefaults.filledIconButtonColors(
                    containerColor = Gold
                )
        ) {

            Icon(

                imageVector =
                    Icons.Default.ArrowForward,

                contentDescription = null,

                tint = Color.White,

                modifier =
                    Modifier.size(30.dp)
            )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(Color.Transparent),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Personalize Widget".translate(displayLanguage),
            color = primaryText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Configure your homescreen widget to fit your daily style.".translate(displayLanguage),
            color = secondaryText,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        // Live Mock Preview Card
        WidgetMockPreview(
            widgetTheme = state.widgetTheme,
            widgetContent = state.widgetContent,
            displayLanguage = displayLanguage
        )

        Spacer(Modifier.height(4.dp))

        // Select Widget Theme Options
        Text(
            text = "Widget Theme".translate(displayLanguage),
            color = primaryText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WidgetSelectorItem(
                title = "Classic Gold".translate(displayLanguage),
                selected = state.widgetTheme == "Classic Gold",
                onClick = { onSelectTheme("Classic Gold") },
                modifier = Modifier.weight(1f)
            )
            WidgetSelectorItem(
                title = "AMOLED Black".translate(displayLanguage),
                selected = state.widgetTheme == "AMOLED Black",
                onClick = { onSelectTheme("AMOLED Black") },
                modifier = Modifier.weight(1f)
            )
            WidgetSelectorItem(
                title = "App Theme".translate(displayLanguage),
                selected = state.widgetTheme == "App Theme",
                onClick = { onSelectTheme("App Theme") },
                modifier = Modifier.weight(1f)
            )
        }

        // Select Widget Content Options
        Text(
            text = "Widget Content".translate(displayLanguage),
            color = primaryText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WidgetSelectorItem(
                title = "Next Bani".translate(displayLanguage),
                selected = state.widgetContent == "Next Bani",
                onClick = { onSelectContent("Next Bani") },
                modifier = Modifier.weight(1f)
            )
            WidgetSelectorItem(
                title = "Daily Streak".translate(displayLanguage),
                selected = state.widgetContent == "Daily Streak",
                onClick = { onSelectContent("Daily Streak") },
                modifier = Modifier.weight(1f)
            )
            WidgetSelectorItem(
                title = "Reflection".translate(displayLanguage),
                selected = state.widgetContent == "Reflection",
                onClick = { onSelectContent("Reflection") },
                modifier = Modifier.weight(1f)
            )
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
        "Classic Gold" -> Quintuple(
            Color(0xFF0F1115),
            BorderStroke(1.dp, Gold),
            Color.White,
            Color.White.copy(alpha = 0.65f),
            Gold
        )
        "AMOLED Black" -> Quintuple(
            Color.Black,
            BorderStroke(1.dp, Color(0xFF222222)),
            Color.White,
            Color.White.copy(alpha = 0.60f),
            Color.White
        )
        else -> Quintuple( // App Theme
            if (isDark) Color(0xFF1E1E1E) else Color.White,
            BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f)),
            if (isDark) Color.White else Primary,
            if (isDark) Color.White.copy(alpha = 0.60f) else TextPrimary.copy(alpha = 0.70f),
            Accent
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(24.dp),
        color = bg,
        border = border
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (widgetContent) {
                "Next Bani" -> {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌅", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rehras Sahib".translate(displayLanguage),
                                color = textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Upcoming: Evening Nitnem".translate(displayLanguage),
                                color = textSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("🔥 0/7", color = textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "Daily Streak" -> {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔥", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "5 Day Streak".translate(displayLanguage),
                                color = textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Your journey continues today.".translate(displayLanguage),
                                color = textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                "Reflection" -> {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✨", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ੴ Satnam Waheguru",
                                color = textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Today's Reflection".translate(displayLanguage),
                                color = textSecondary,
                                fontSize = 11.sp
                            )
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
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = if (selected) accent.copy(alpha = 0.16f) else if (isLight) Color.White.copy(alpha = 0.72f) else Color.White.copy(alpha = 0.05f),
        border = BorderStroke(
            if (selected) 1.5.dp else 1.dp,
            if (selected) accent else Color.White.copy(alpha = 0.06f)
        )
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = primaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)