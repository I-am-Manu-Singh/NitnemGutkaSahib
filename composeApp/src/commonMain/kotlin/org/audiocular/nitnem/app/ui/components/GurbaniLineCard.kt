package org.audiocular.nitnem.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.utils.translateHindiTransliteration

@Stable
data class GurbaniLineUiConfig(

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

    val translationColorMode: String,

    val wordMeaningColorMode: String,

    val boldGurmukhi: Boolean,

    val italicRomanTransliteration: Boolean,

    val centerGurmukhi: Boolean,

    val highlightActiveLine: Boolean,

    val showLineProgress: Boolean,

    val compactMode: Boolean,

    val larivaarMode: Boolean
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

    val cardColor by animateColorAsState(

        targetValue = when {

            isActive &&
                config.highlightActiveLine -> {

                colors.primary.copy(alpha = 0.10f)
            }

            selected -> {

                colors.surfaceContainerLow
            }

            else -> {

                colors.surface
            }
        },

        label = ""
    )

    val borderColor by animateColorAsState(

        targetValue = when {

            isActive &&
                config.highlightActiveLine -> {

                colors.primary.copy(alpha = 0.18f)
            }

            else -> {

                colors.outlineVariant.copy(alpha = 0.18f)
            }
        },

        label = ""
    )

    val gurmukhiColor = remember(
        config.gurmukhiColorMode,
        colors
    ) {

        resolveTextColor(

            mode = config.gurmukhiColorMode,

            colors = colors,

            default = colors.onSurface
        )
    }

    val romanTransliterationColor = remember(
        config.romantransliterationColorMode,
        colors
    ) {

        resolveTextColor(

            mode =
                config.romantransliterationColorMode,

            colors = colors,

            default =
                colors.onSurfaceVariant.copy(
                    alpha = 0.82f
                )
        )
    }

    val translationColor = remember(
        config.translationColorMode,
        colors
    ) {

        resolveTextColor(

            mode = config.translationColorMode,

            colors = colors,

            default =
                colors.onSurfaceVariant
        )
    }

    val wordMeaningColor = remember(
        config.wordMeaningColorMode,
        colors
    ) {

        resolveTextColor(

            mode = config.wordMeaningColorMode,

            colors = colors,

            default =
                colors.onSurfaceVariant
        )
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
                 * TOP BAR
                 */

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    AnimatedVisibility(

                        visible =

                            isActive &&
                            config.highlightActiveLine
                    ) {

                        Row(

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(

                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(

                                        colors.primary.copy(
                                            alpha = 0.08f
                                        )
                                    ),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(

                                    imageVector =
                                        Icons.Default.GraphicEq,

                                    contentDescription =
                                        null,

                                    tint =
                                        colors.primary.copy(
                                            alpha = 0.85f
                                        ),

                                    modifier =
                                        Modifier.size(14.dp)
                                )
                            }

                            Spacer(
                                Modifier.width(8.dp)
                            )
                        }
                    }

                    Spacer(
                        Modifier.weight(1f)
                    )

                    Row(

                        horizontalArrangement =
                            Arrangement.spacedBy(4.dp)
                    ) {

                        MinimalActionButton(

                            icon =

                                if (isBookmarked) {
                                    Icons.Default.Bookmark
                                } else {
                                    Icons.Default.BookmarkBorder
                                },

                            tint =

                                if (isBookmarked) {

                                    colors.primary.copy(
                                        alpha = 0.85f
                                    )

                                } else {

                                    colors.onSurfaceVariant.copy(
                                        alpha = 0.52f
                                    )
                                },

                            containerColor =
                                colors.surfaceVariant.copy(
                                    alpha = 0.22f
                                ),

                            onClick = onBookmark
                        )

                        MinimalActionButton(

                            icon =
                                Icons.Default.MenuBook,

                            tint =
                                wordMeaningColor.copy(
                                    alpha = 0.70f
                                ),

                            containerColor =
                                colors.surfaceVariant.copy(
                                    alpha = 0.18f
                                ),

                            onClick = {
                                onMeaningClick(line)
                            }
                        )
                    }
                }

                Spacer(

                    Modifier.height(

                        if (config.compactMode) {
                            12.dp
                        } else {
                            18.dp
                        }
                    )
                )

                /*
                 * GURMUKHI
                 */

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
                    words.forEachIndexed { wordIndex, word ->
                        val cleanWord = remember(word) {
                            word.replace(Regex("[।|.||?;:!,='\"/\\\\]"), "").trim().lowercase()
                        }
                        val meaning = wordMeaningsMap[cleanWord]
                        val hasMeaning = !meaning.isNullOrBlank()

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Text(
                                text = word,
                                color = gurmukhiColor,
                                fontSize = config.gurmukhiFontSize.sp,
                                lineHeight = config.lineSpacing.sp,
                                fontWeight = if (config.boldGurmukhi) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier
                                    .clickable(enabled = hasMeaning) {
                                        activePopupWordIndex = if (activePopupWordIndex == wordIndex) null else wordIndex
                                    }
                                    .drawBehind {
                                        if (hasMeaning) {
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
                                                text = word,
                                                color = colors.primary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = (config.gurmukhiFontSize * 0.75f).sp
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
                            if (line.transliteration.isNotBlank()) line.transliteration else "Transliteration not available",

                        color =
                            romanTransliterationColor,

                        fontSize =
                            config
                                .romanTransliterationFontSize,

                        lineSpacing =
                            config.lineSpacing,

                        italic =
                            config
                                .italicRomanTransliteration
                    )
                }

                /*
                 * HINDI (DEVANAGARI TRANSLITERATION)
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
                            if (line.hindi.isNotBlank()) line.hindi.translateHindiTransliteration() else "Hindi translation not available",

                        color =
                            romanTransliterationColor,

                        fontSize =
                            config.romanTransliterationFontSize,

                        lineSpacing =
                            config.lineSpacing
                    )
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
                            if (line.translation.isNotBlank()) line.translation else "English translation not available",

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
                            if (line.punjabi.isNotBlank()) line.punjabi else "Punjabi translation not available",

                        color =
                            translationColor.copy(alpha = 0.85f),

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

@Composable
private fun MinimalTranslationSection(

    icon: ImageVector,

    text: String,

    color: Color,

    fontSize: Float,

    lineSpacing: Float,

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