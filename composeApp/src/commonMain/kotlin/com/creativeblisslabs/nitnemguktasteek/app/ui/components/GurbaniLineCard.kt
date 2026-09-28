package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import com.creativeblisslabs.nitnemguktasteek.models.BaniLine
import com.creativeblisslabs.nitnemguktasteek.utils.translate
import com.creativeblisslabs.nitnemguktasteek.utils.translateHindiTransliteration
import com.creativeblisslabs.nitnemguktasteek.utils.toHindiTranslation

@Stable
data class GurbaniLineUiConfig(

    val showGurmukhi: Boolean = true,

    val gurmukhiFontSize: Float,

    val romanTransliterationFontSize: Float,

    val translationFontSize: Float,

    val wordMeaningFontSize: Float,

    val lineSpacing: Float,

    val showTranslation: Boolean,

    val showRomanTransliteration: Boolean,

    val showEnglishTranslation: Boolean,

    val showPunjabiTranslation: Boolean,

    val showDevanagariTransliteration: Boolean,

    val showWordMeaning: Boolean,

    val gurmukhiColorMode: String,

    val romantransliterationColorMode: String,

    val devanagariTransliterationColorMode: String = "default",

    val punjabiTranslationColorMode: String = "default",

    val translationColorMode: String,

    val wordMeaningColorMode: String,

    val boldGurmukhi: Boolean,

    val italicRomanTransliteration: Boolean,

    val centerGurmukhi: Boolean,

    val highlightActiveLine: Boolean,

    val showLineProgress: Boolean,

    val compactMode: Boolean,

    val larivaarMode: Boolean,

    val larivaarAssistMode: Boolean = false,

    val showVishraam: Boolean = true,

    val blackAndWhiteAodMode: Boolean = false,

    val gurmukhiFont: com.creativeblisslabs.nitnemguktasteek.app.ui.theme.GurmukhiFontFamily = com.creativeblisslabs.nitnemguktasteek.app.ui.theme.GurmukhiFontFamily.Default,

    val romanFont: com.creativeblisslabs.nitnemguktasteek.app.ui.theme.RomanFontFamily = com.creativeblisslabs.nitnemguktasteek.app.ui.theme.RomanFontFamily.Default,

    val devanagariFont: com.creativeblisslabs.nitnemguktasteek.app.ui.theme.DevanagariFontFamily = com.creativeblisslabs.nitnemguktasteek.app.ui.theme.DevanagariFontFamily.Default,

    val displayLanguage: com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage = com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage.English
)

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun GurbaniLineCard(

    line: BaniLine,

    config: GurbaniLineUiConfig,

    isActive: Boolean,

    selected: Boolean = false,

    lineProgress: Float = 0f,

    isBookmarked: Boolean = false,

    onBookmark: () -> Unit = {},

    onMeaningClick: (BaniLine) -> Unit = {},

    onClick: () -> Unit
) {

    val colors = MaterialTheme.colorScheme

    val contentPadding =
        if (config.compactMode) {
            16.dp
        } else {
            22.dp
        }

    val sectionSpacing =
        if (config.compactMode) {
            8.dp
        } else {
            12.dp
        }

    val cardShape =
        RoundedCornerShape(

            if (config.compactMode) {
                20.dp
            } else {
                26.dp
            }
        )

    val animatedProgress by animateFloatAsState(

        targetValue =

            if (
                isActive &&
                config.showLineProgress
            ) {

                lineProgress.coerceIn(0f, 1f)

            } else {
                0f
            },

        label = ""
    )

    val breathingTransition = rememberInfiniteTransition(label = "GuidanceHalo")
    val haloScale by breathingTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloScale"
    )
    val haloAlpha by breathingTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HaloAlpha"
    )

    val cardColor by animateColorAsState(
        targetValue = when {
            config.blackAndWhiteAodMode -> Color.Black
            isActive && config.highlightActiveLine -> colors.primary.copy(alpha = 0.10f)
            selected -> colors.surfaceContainerLow
            else -> colors.surface
        },
        label = ""
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            config.blackAndWhiteAodMode -> if (isActive) Color.White.copy(alpha = 0.40f) else Color.Transparent
            isActive && config.highlightActiveLine -> colors.primary.copy(alpha = 0.18f)
            else -> colors.outlineVariant.copy(alpha = 0.18f)
        },
        label = ""
    )

    val gurmukhiColor = remember(
        config.gurmukhiColorMode,
        config.blackAndWhiteAodMode,
        colors
    ) {
        if (config.blackAndWhiteAodMode) {
            Color.White
        } else {
            resolveTextColor(
                mode = config.gurmukhiColorMode,
                colors = colors,
                default = colors.onSurface
            )
        }
    }

    val romanTransliterationColor = remember(
        config.romantransliterationColorMode,
        config.blackAndWhiteAodMode,
        colors
    ) {
        if (config.blackAndWhiteAodMode) {
            Color.White.copy(alpha = 0.90f)
        } else {
            resolveTextColor(
                mode = config.romantransliterationColorMode,
                colors = colors,
                default = colors.onSurfaceVariant.copy(alpha = 0.82f)
            )
        }
    }

    val devanagariTransliterationColor = remember(
        config.devanagariTransliterationColorMode,
        config.blackAndWhiteAodMode,
        colors
    ) {
        if (config.blackAndWhiteAodMode) {
            Color.White.copy(alpha = 0.90f)
        } else {
            resolveTextColor(
                mode = config.devanagariTransliterationColorMode,
                colors = colors,
                default = colors.onSurfaceVariant.copy(alpha = 0.82f)
            )
        }
    }

    val punjabiTranslationColor = remember(
        config.punjabiTranslationColorMode,
        config.blackAndWhiteAodMode,
        colors
    ) {
        if (config.blackAndWhiteAodMode) {
            Color.White.copy(alpha = 0.82f)
        } else {
            resolveTextColor(
                mode = config.punjabiTranslationColorMode,
                colors = colors,
                default = colors.onSurfaceVariant
            )
        }
    }

    val translationColor = remember(
        config.translationColorMode,
        config.blackAndWhiteAodMode,
        colors
    ) {
        if (config.blackAndWhiteAodMode) {
            Color.White.copy(alpha = 0.82f)
        } else {
            resolveTextColor(
                mode = config.translationColorMode,
                colors = colors,
                default = colors.onSurfaceVariant
            )
        }
    }

    val wordMeaningColor = remember(
        config.wordMeaningColorMode,
        config.blackAndWhiteAodMode,
        colors
    ) {
        if (config.blackAndWhiteAodMode) {
            Color.White.copy(alpha = 0.82f)
        } else {
            resolveTextColor(
                mode = config.wordMeaningColorMode,
                colors = colors,
                default = colors.onSurfaceVariant
            )
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isActive && !config.blackAndWhiteAodMode) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(haloScale),
                shape = cardShape,
                color = colors.primary.copy(alpha = haloAlpha),
                border = BorderStroke(1.2.dp, colors.primary.copy(alpha = haloAlpha * 1.5f))
            ) {}
        }

    Surface(

        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),

        shape = cardShape,

        color = cardColor,

        tonalElevation =

            if (isActive) {
                2.dp
            } else {
                0.dp
            },

        border = BorderStroke(
            1.dp,
            borderColor
        )
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
                    .padding(

                        bottom =

                            if (
                                isActive &&
                                config.showLineProgress
                            ) {
                                10.dp
                            } else {
                                0.dp
                            }
                    )
            ) {

                /*
                 * TOP BAR (Hidden in Black & White mode for pure distraction-free text)
                 */

                if (!config.blackAndWhiteAodMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedVisibility(
                            visible = isActive && config.highlightActiveLine
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(colors.primary.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = colors.primary.copy(alpha = 0.85f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            MinimalActionButton(
                                icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                tint = if (isBookmarked) colors.primary.copy(alpha = 0.85f) else colors.onSurfaceVariant.copy(alpha = 0.52f),
                                containerColor = colors.surfaceVariant.copy(alpha = 0.22f),
                                onClick = onBookmark
                            )

                            MinimalActionButton(
                                icon = Icons.Default.MenuBook,
                                tint = wordMeaningColor.copy(alpha = 0.70f),
                                containerColor = colors.surfaceVariant.copy(alpha = 0.18f),
                                onClick = { onMeaningClick(line) }
                            )
                        }
                    }

                    Spacer(
                        Modifier.height(
                            if (config.compactMode) 12.dp else 18.dp
                        )
                    )
                }

                /*
                 * GURMUKHI
                 */

                if (config.showGurmukhi) {
                    val words = remember(line.gurmukhi) {
                        line.gurmukhi.split(" ").filter { it.isNotBlank() }
                    }

                    val wordMeaningsMap = remember(line.wordMeaning) {
                        parseWordMeanings(line.wordMeaning)
                    }

                    var activePopupWordIndex by remember { mutableStateOf<Int?>(null) }

                    LaunchedEffect(activePopupWordIndex) {
                        if (activePopupWordIndex != null) {
                            kotlinx.coroutines.delay(4000)
                            activePopupWordIndex = null
                        }
                    }

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (config.centerGurmukhi) Arrangement.Center else Arrangement.Start,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        words.forEachIndexed { wordIndex, rawWord ->
                            val cleanWord = remember(rawWord) {
                                rawWord.replace(Regex("[।|.||?;:!,='\"/\\\\]"), "").trim().lowercase()
                            }
                            val meaning = wordMeaningsMap[cleanWord]
                            val hasMeaning = !meaning.isNullOrBlank()

                            val effectiveWordColor = when {
                                config.larivaarMode || config.larivaarAssistMode -> {
                                    // Subtle same-shade difference (contrast in tone/weight) rather than harsh multi-color shifts
                                    if (wordIndex % 2 == 0) gurmukhiColor
                                    else gurmukhiColor.copy(alpha = 0.74f)
                                }
                                else -> gurmukhiColor
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.wrapContentSize()
                            ) {
                                val isHeavyPause = config.showVishraam && !config.blackAndWhiteAodMode && rawWord.contains("॥")
                                val isMediumPause = config.showVishraam && !config.blackAndWhiteAodMode && !isHeavyPause && rawWord.contains("।")

                                val displayWordColor = when {
                                    isHeavyPause -> Color(0xFFE5A93C) // Golden heavy vishram
                                    isMediumPause -> Color(0xFFD97706) // Warm amber medium vishram
                                    else -> effectiveWordColor
                                }

                                Text(
                                    text = rawWord,
                                    color = displayWordColor,
                                    fontSize = config.gurmukhiFontSize.sp,
                                    lineHeight = config.lineSpacing.sp,
                                    fontWeight = if (config.boldGurmukhi) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = com.creativeblisslabs.nitnemguktasteek.app.ui.theme.resolveGurmukhiFont(config.gurmukhiFont),
                                    modifier = Modifier
                                        .clickable(enabled = hasMeaning) {
                                            activePopupWordIndex = if (activePopupWordIndex == wordIndex) null else wordIndex
                                        }
                                        .drawBehind {
                                            if (hasMeaning && !config.blackAndWhiteAodMode) {
                                                val strokeWidth = 1.2.dp.toPx()
                                                val y = size.height - 1.dp.toPx()
                                                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 3f), 0f)
                                                drawLine(
                                                    color = colors.primary.copy(alpha = 0.5f),
                                                    start = Offset(0f, y),
                                                    end = Offset(size.width, y),
                                                    strokeWidth = strokeWidth,
                                                    pathEffect = pathEffect
                                                )
                                            }
                                        }
                                        .padding(horizontal = if (config.larivaarMode) 0.dp else 2.dp)
                                )

                                if (activePopupWordIndex == wordIndex && hasMeaning) {
                                    Popup(
                                        alignment = Alignment.TopCenter,
                                        offset = IntOffset(0, -50),
                                        onDismissRequest = { activePopupWordIndex = null }
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = colors.surfaceContainerHigh,
                                            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.24f)),
                                            tonalElevation = 6.dp,
                                            modifier = Modifier.widthIn(max = 220.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = rawWord,
                                                    color = colors.primary,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = (config.gurmukhiFontSize * 0.75f).sp,
                                                    fontFamily = com.creativeblisslabs.nitnemguktasteek.app.ui.theme.resolveGurmukhiFont(config.gurmukhiFont)
                                                )
                                                Spacer(Modifier.height(4.dp))
                                                Text(
                                                    text = meaning!!,
                                                    color = colors.onSurface,
                                                    fontSize = (config.gurmukhiFontSize * 0.60f).sp,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                /*
                 * ROMAN
                 */

                if (
                    config.showRomanTransliteration
                ) {

                    Spacer(
                        Modifier.height(sectionSpacing)
                    )

                    MinimalTranslationSection(

                        icon =
                            Icons.Default.Language,

                        text =
                            if (line.transliteration.isNotBlank()) line.transliteration else "Transliteration not available".translate(config.displayLanguage),

                        color =
                            romanTransliterationColor,

                        fontSize =
                            config
                                .romanTransliterationFontSize,

                        lineSpacing =
                            config.lineSpacing,

                        fontFamily =
                            com.creativeblisslabs.nitnemguktasteek.app.ui.theme.resolveRomanFont(config.romanFont),

                        italic =
                            config
                                .italicRomanTransliteration
                    )
                }

                /*
                 * HINDI (DEVANAGARI TRANSLITERATION & TEEKA)
                 */

                if (
                    config.showDevanagariTransliteration
                ) {

                    Spacer(
                        Modifier.height(sectionSpacing)
                    )

                    MinimalTranslationSection(

                        icon =
                            Icons.Default.AutoStories,

                        text =
                            if (line.hindi.isNotBlank()) line.hindi.translateHindiTransliteration() else "Hindi Transliteration not available".translate(config.displayLanguage),

                        color =
                            devanagariTransliterationColor,

                        fontSize =
                            config.romanTransliterationFontSize,

                        lineSpacing =
                            config.lineSpacing,

                        fontFamily =
                            com.creativeblisslabs.nitnemguktasteek.app.ui.theme.resolveDevanagariFont(config.devanagariFont)
                    )

                    // Hindi Teeka (Translation/Meaning)
                    val hindiTeeka = remember(line.punjabi, line.translation) {
                        if (line.punjabi.isNotBlank()) line.punjabi.toHindiTranslation()
                        else if (line.translation.isNotBlank()) line.translation
                        else ""
                    }
                    if (hindiTeeka.isNotBlank()) {
                        Spacer(
                            Modifier.height(sectionSpacing * 0.7f)
                        )
                        MinimalTranslationSection(
                            icon = Icons.Default.Translate,
                            text = hindiTeeka,
                            color = devanagariTransliterationColor.copy(alpha = 0.85f),
                            fontSize = config.translationFontSize,
                            lineSpacing = config.lineSpacing,
                            fontFamily = com.creativeblisslabs.nitnemguktasteek.app.ui.theme.resolveDevanagariFont(config.devanagariFont)
                        )
                    }
                }

                /*
                 * ENGLISH TRANSLATION
                 */

                if (
                    config.showEnglishTranslation
                ) {

                    Spacer(
                        Modifier.height(sectionSpacing)
                    )

                    MinimalTranslationSection(

                        icon =
                            Icons.Default.Translate,

                        text =
                            if (line.translation.isNotBlank()) line.translation else "English translation not available".translate(config.displayLanguage),

                        color =
                            translationColor.copy(alpha = 0.85f),

                        fontSize =
                            config.translationFontSize,

                        lineSpacing =
                            config.lineSpacing
                    )
                }

                /*
                 * PUNJABI TRANSLATION
                 */

                if (
                    config.showPunjabiTranslation
                ) {

                    Spacer(
                        Modifier.height(sectionSpacing)
                    )

                    MinimalTranslationSection(

                        icon =
                            Icons.Default.MenuBook,

                        text =
                            if (line.punjabi.isNotBlank()) line.punjabi else "Punjabi translation not available".translate(config.displayLanguage),

                        color =
                            punjabiTranslationColor,

                        fontSize =
                            config.translationFontSize,

                        lineSpacing =
                            config.lineSpacing
                    )
                }
            }

            /*
             * PROGRESS
             */

            AnimatedVisibility(

                visible =

                    isActive &&
                    config.showLineProgress,

                modifier = Modifier
                    .align(
                        Alignment.BottomCenter
                    )
            ) {

                LinearProgressIndicator(

                    progress = {
                        animatedProgress
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(

                            if (
                                config.compactMode
                            ) {
                                3.dp
                            } else {
                                4.dp
                            }
                        ),

                    color = colors.primary,

                    trackColor =
                        colors.primary.copy(
                            alpha = 0.08f
                        )
                )
            }
        }
    }
    }
}

@Composable
private fun MinimalTranslationSection(

    icon: ImageVector,

    text: String,

    color: Color,

    fontSize: Float,

    lineSpacing: Float,

    fontFamily: androidx.compose.ui.text.font.FontFamily = androidx.compose.ui.text.font.FontFamily.Default,

    italic: Boolean = false
) {

    Row(

        modifier = Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.Top
    ) {

        Icon(

            imageVector = icon,

            contentDescription = null,

            tint =
                color.copy(alpha = 0.55f),

            modifier = Modifier
                .padding(top = 3.dp)
                .size(13.dp)
        )

        Spacer(
            Modifier.width(8.dp)
        )

        Text(

            text = text,

            color = color,

            fontSize = fontSize.sp,

            lineHeight = lineSpacing.sp,

            fontFamily = fontFamily,

            fontStyle =

                if (italic) {
                    FontStyle.Italic
                } else {
                    FontStyle.Normal
                },

            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MinimalActionButton(

    icon: ImageVector,

    tint: Color,

    containerColor: Color,

    onClick: () -> Unit
) {

    Surface(

        modifier =
            Modifier.size(32.dp),

        onClick = onClick,

        shape =
            RoundedCornerShape(10.dp),

        color = containerColor
    ) {

        Box(
            contentAlignment =
                Alignment.Center
        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = tint,

                modifier =
                    Modifier.size(15.dp)
            )
        }
    }
}

private fun resolveTextColor(

    mode: String,

    colors: ColorScheme,

    default: Color
): Color {

    return when (mode) {

        "gold" -> {
            Color(0xFFD6B25E)
        }

        "accent" -> {
            colors.primary
        }

        "muted" -> {

            colors.onSurfaceVariant.copy(
                alpha = 0.72f
            )
        }

        "white" -> {

            if (
                colors.surface.luminance() > 0.5f
            ) {

                colors.onSurface

            } else {

                Color.White
            }
        }

        "purple" -> {
            Color(0xFFB39DFF)
        }

        "blue" -> {
            Color(0xFF7AB8FF)
        }

        "green" -> {
            Color(0xFF63D8B6)
        }

        "orange" -> {
            Color(0xFFFFB36A)
        }

        else -> default
    }
}

private fun parseWordMeanings(meaningText: String): Map<String, String> {
    val result = mutableMapOf<String, String>()
    if (meaningText.isBlank()) return result
    
    // Split by Gurbani full stop '।' or semicolon ';' or newline '\n'
    val entries = meaningText.split(Regex("[।;\n]"))
    for (entry in entries) {
        if (entry.isBlank()) continue
        // Split word and meaning by '=' or '-'
        val parts = entry.split(Regex("[=-]"), 2)
        if (parts.size == 2) {
            val rawWord = parts[0].trim()
            val rawMeaning = parts[1].trim()
            if (rawWord.isNotEmpty() && rawMeaning.isNotEmpty()) {
                // Normalize word: strip common trailing punctuation or symbols
                val cleanWordKey = rawWord.replace(Regex("[।|.||?;:!,='\"/\\\\]"), "").trim().lowercase()
                result[cleanWordKey] = rawMeaning
            }
        }
    }
    return result
}