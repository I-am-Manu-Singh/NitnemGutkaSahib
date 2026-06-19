package org.audiocular.nitnem.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.app.ui.theme.Accent
import org.audiocular.nitnem.app.ui.theme.Gold
import org.audiocular.nitnem.app.ui.theme.TextPrimary
import org.audiocular.nitnem.app.ui.theme.TextSecondary
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateHindiTransliteration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GurbaniMeaningSheet(
    line: BaniLine?,
    onDismiss: () -> Unit,
    gurmukhiFontSize: Float,
    lineSpacing: Float,
    boldGurmukhi: Boolean,
    centerGurmukhi: Boolean,
    showEnglishTranslation: Boolean,
    showPunjabiTranslation: Boolean,
    showDevanagariTransliteration: Boolean,
    showWordMeaning: Boolean,
    showRomanTransliteration: Boolean = false,
    larivaarMode: Boolean = false,
    gurmukhiColorMode: String,
    translationColorMode: String,
    wordMeaningColorMode: String,
    displayLanguage: DisplayLanguage = DisplayLanguage.English,
    onToggleEnglish: (Boolean) -> Unit = {},
    onTogglePunjabi: (Boolean) -> Unit = {},
    onToggleHindi: (Boolean) -> Unit = {},
    onToggleWordMeaning: (Boolean) -> Unit = {},
    onToggleRoman: (Boolean) -> Unit = {},
    onToggleCenter: (Boolean) -> Unit = {},
    onToggleLarivaar: (Boolean) -> Unit = {}
) {

    if (line == null) return

    val colors = MaterialTheme.colorScheme

    val gurmukhiColor = resolveTextColor(
        mode = gurmukhiColorMode,
        colors = colors,
        default = TextPrimary
    )

    val translationColor = resolveTextColor(
        mode = translationColorMode,
        colors = colors,
        default = TextPrimary.copy(alpha = 0.72f)
    )

    val wordMeaningColor = resolveTextColor(
        mode = wordMeaningColorMode,
        colors = colors,
        default = Accent
    )

    /*
     * CONTENT AVAILABILITY
     */

    val hasEnglish = remember(line.translation) {
        line.translation.hasMeaningContent()
    }

    val hasPunjabi = remember(line.punjabi) {
        line.punjabi.hasMeaningContent()
    }

    val hasHindi = remember(line.hindi) {
        line.hindi.hasMeaningContent()
    }

    val hasRoman = remember(line.transliteration) {
        line.transliteration.hasMeaningContent()
    }

    val hasWordMeaning = remember(line.wordMeaning) {
        line.wordMeaning.hasMeaningContent()
    }

    val hasAnyExtraContent = remember(
        hasEnglish,
        hasPunjabi,
        hasHindi,
        hasRoman,
        hasWordMeaning
    ) {
        hasEnglish ||
            hasPunjabi ||
            hasHindi ||
            hasRoman ||
            hasWordMeaning
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(

        onDismissRequest = onDismiss,

        sheetState = sheetState
    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(
                    top = 4.dp,
                    bottom = 24.dp
                )
                .verticalScroll(
                    rememberScrollState()
                )
        ) {

            /*
             * HEADER
             */

            Text(

                text = "Line Meaning"
                    .translate(displayLanguage),

                style = MaterialTheme.typography.titleLarge,

                fontWeight = FontWeight.Bold,

                color = Accent,

                modifier =
                    Modifier.padding(bottom = 4.dp)
            )

            Text(

                text =
                    "Tap chips below to toggle visible sections"
                        .translate(displayLanguage),

                fontSize = 12.sp,

                color =
                    colors.onSurfaceVariant.copy(alpha = 0.55f),

                modifier =
                    Modifier.padding(bottom = 14.dp)
            )

            /*
             * TOGGLES
             */

            PremiumMeaningSwitcher(

                showHindi =
                    showDevanagariTransliteration,

                showEnglish =
                    showEnglishTranslation,

                showPunjabi =
                    showPunjabiTranslation,

                showRoman =
                    showRomanTransliteration,

                showCenter =
                    centerGurmukhi,

                showLarivaar =
                    larivaarMode,

                showWordMeaning =
                    showWordMeaning,

                hasEnglish =
                    hasEnglish,

                hasPunjabi =
                    hasPunjabi,

                hasHindi =
                    hasHindi,

                hasRoman =
                    hasRoman,

                hasWordMeaning =
                    hasWordMeaning,

                displayLanguage =
                    displayLanguage,

                onToggleHindi = {

                    onToggleHindi(
                        !showDevanagariTransliteration
                    )
                },

                onToggleEnglish = {

                    onToggleEnglish(
                        !showEnglishTranslation
                    )
                },

                onTogglePunjabi = {

                    onTogglePunjabi(
                        !showPunjabiTranslation
                    )
                },

                onToggleRoman = {

                    onToggleRoman(
                        !showRomanTransliteration
                    )
                },

                onToggleCenter = {

                    onToggleCenter(
                        !centerGurmukhi
                    )
                },

                onToggleLarivaar = {

                    onToggleLarivaar(
                        !larivaarMode
                    )
                },

                onToggleWordMeaning = {

                    onToggleWordMeaning(
                        !showWordMeaning
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            /*
             * GURMUKHI
             */

            val gurmukhiText =

                if (larivaarMode) {

                    line.gurmukhi
                        .replace("\\s+".toRegex(), "")

                } else {

                    line.gurmukhi
                }

            Text(

                text = gurmukhiText,

                fontSize =
                    gurmukhiFontSize.sp,

                lineHeight =
                    lineSpacing.sp,

                color =
                    gurmukhiColor,

                fontWeight =

                    if (boldGurmukhi)
                        FontWeight.Bold
                    else
                        FontWeight.Medium,

                textAlign =

                    if (centerGurmukhi)
                        TextAlign.Center
                    else
                        TextAlign.Start,

                modifier =
                    Modifier.fillMaxWidth()
            )

            /*
             * EMPTY STATE
             */

            if (!hasAnyExtraContent) {

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                HorizontalDivider(
                    color = colors.outlineVariant.copy(alpha = 0.25f)
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(

                    text =
                        "No additional meanings available"
                            .translate(displayLanguage),

                    color =
                        TextSecondary.copy(alpha = 0.7f),

                    fontSize = 13.sp
                )
            }

            /*
             * ROMAN
             */

            if (
                showRomanTransliteration
            ) {

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                MeaningRow(

                    icon = Icons.Default.Language,

                    text = if (hasRoman) line.transliteration else "Transliteration not available".translate(displayLanguage),

                    color =
                        colors.onSurfaceVariant.copy(alpha = 0.82f),

                    fontSize =
                        gurmukhiFontSize * 0.62f,

                    lineSpacing =
                        lineSpacing,

                    italic = true
                )
            }

            /*
             * HINDI
             */

            if (
                showDevanagariTransliteration
            ) {

                SectionSpacer()

                SectionLabel(

                    icon = Icons.Default.AutoStories,

                    label =
                        "Hindi Translation"
                            .translate(displayLanguage)
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                MeaningRow(

                    icon = Icons.Default.AutoStories,

                    text =
                        if (hasHindi) line.hindi.translateHindiTransliteration() else "Hindi translation not available".translate(displayLanguage),

                    color =
                        translationColor,

                    fontSize =
                        gurmukhiFontSize * 0.72f,

                    lineSpacing =
                        lineSpacing,

                    showIcon = false
                )
            }

            /*
             * WORD MEANINGS
             */

            if (
                showWordMeaning
            ) {

                SectionSpacer()

                SectionLabel(

                    icon = Icons.Default.MenuBook,

                    label =
                        "Word Meanings"
                            .translate(displayLanguage)
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(

                    text = if (hasWordMeaning) line.wordMeaning else "Word meanings not available".translate(displayLanguage),

                    fontSize =
                        (gurmukhiFontSize * 0.68f).sp,

                    lineHeight =
                        (lineSpacing * 0.85f).sp,

                    color =
                        wordMeaningColor
                )
            }

            /*
             * ENGLISH
             */

            if (
                showEnglishTranslation
            ) {

                SectionSpacer()

                SectionLabel(

                    icon = Icons.Default.Translate,

                    label =
                        "English Translation"
                            .translate(displayLanguage)
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                MeaningRow(

                    icon = Icons.Default.Translate,

                    text = if (hasEnglish) line.translation else "English translation not available".translate(displayLanguage),

                    color =
                        translationColor,

                    fontSize =
                        gurmukhiFontSize * 0.72f,

                    lineSpacing =
                        lineSpacing,

                    showIcon = false
                )
            }

            /*
             * PUNJABI
             */

            if (
                showPunjabiTranslation
            ) {

                SectionSpacer()

                SectionLabel(

                    icon = Icons.Default.MenuBook,

                    label =
                        "Punjabi Translation"
                            .translate(displayLanguage)
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                MeaningRow(

                    icon = Icons.Default.MenuBook,

                    text = if (hasPunjabi) line.punjabi else "Punjabi translation not available".translate(displayLanguage),

                    color =
                        translationColor,

                    fontSize =
                        gurmukhiFontSize * 0.72f,

                    lineSpacing =
                        lineSpacing,

                    showIcon = false
                )
            }
        }
    }
}

private fun String?.hasMeaningContent(): Boolean {

    return !this.isNullOrBlank() &&
        this.trim().isNotEmpty()
}

@Composable
private fun SectionSpacer() {

    val colors = MaterialTheme.colorScheme

    Spacer(
        modifier = Modifier.height(12.dp)
    )

    HorizontalDivider(
        color = colors.outlineVariant.copy(alpha = 0.25f)
    )

    Spacer(
        modifier = Modifier.height(12.dp)
    )
}

@Composable
private fun SectionLabel(
    icon: ImageVector,
    label: String
) {

    Row(

        verticalAlignment =
            Alignment.CenterVertically,

        horizontalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {

        Icon(

            imageVector = icon,

            contentDescription = null,

            tint = Accent.copy(alpha = 0.72f),

            modifier =
                Modifier.size(14.dp)
        )

        Text(

            text = label,

            style =
                MaterialTheme.typography.labelMedium,

            color = Accent,

            fontWeight =
                FontWeight.SemiBold
        )
    }
}

@Composable
private fun MeaningRow(
    icon: ImageVector,
    text: String,
    color: Color,
    fontSize: Float,
    lineSpacing: Float,
    italic: Boolean = false,
    showIcon: Boolean = true
) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.Top
    ) {

        if (showIcon) {

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
        }

        Text(

            text = text,

            color = color,

            fontSize =
                fontSize.sp,

            lineHeight =
                lineSpacing.sp,

            fontStyle =

                if (italic)
                    FontStyle.Italic
                else
                    FontStyle.Normal,

            modifier =
                Modifier.weight(1f)
        )
    }
}

private fun resolveTextColor(
    mode: String,
    colors: ColorScheme,
    default: Color
): Color {

    return when (mode) {

        "gold" ->
            Color(0xFFD6B25E)

        "accent" ->
            colors.primary

        "muted" ->
            colors.onSurfaceVariant.copy(alpha = 0.72f)

        "white" ->

            if (colors.surface.luminance() > 0.5f)
                colors.onSurface
            else
                Color.White

        "purple" ->
            Color(0xFFB39DFF)

        "blue" ->
            Color(0xFF7AB8FF)

        "green" ->
            Color(0xFF63D8B6)

        "orange" ->
            Color(0xFFFFB36A)

        else ->
            default
    }
}

@Composable
private fun PremiumMeaningSwitcher(

    showHindi: Boolean,

    showEnglish: Boolean,

    showPunjabi: Boolean,

    showRoman: Boolean,

    showCenter: Boolean,

    showLarivaar: Boolean,

    showWordMeaning: Boolean,

    hasEnglish: Boolean,

    hasPunjabi: Boolean,

    hasHindi: Boolean,

    hasRoman: Boolean,

    hasWordMeaning: Boolean,

    displayLanguage: DisplayLanguage,

    onToggleHindi: () -> Unit,

    onToggleEnglish: () -> Unit,

    onTogglePunjabi: () -> Unit,

    onToggleRoman: () -> Unit,

    onToggleCenter: () -> Unit,

    onToggleLarivaar: () -> Unit,

    onToggleWordMeaning: () -> Unit
) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(
                rememberScrollState()
            ),

        horizontalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        if (hasEnglish) {

            PremiumToggleChip(

                icon =
                    Icons.Default.Translate,

                label =
                    "English"
                        .translate(displayLanguage),

                selected =
                    showEnglish,

                onClick =
                    onToggleEnglish
            )
        }

        if (hasPunjabi) {

            PremiumToggleChip(

                icon =
                    Icons.Default.MenuBook,

                label =
                    "Punjabi"
                        .translate(displayLanguage),

                selected =
                    showPunjabi,

                onClick =
                    onTogglePunjabi
            )
        }

        if (hasRoman) {

            PremiumToggleChip(

                icon =
                    Icons.Default.Language,

                label =
                    "Roman"
                        .translate(displayLanguage),

                selected =
                    showRoman,

                onClick =
                    onToggleRoman
            )
        }

        if (hasHindi) {

            PremiumToggleChip(

                icon =
                    Icons.Default.AutoStories,

                label =
                    "Hindi"
                        .translate(displayLanguage),

                selected =
                    showHindi,

                onClick =
                    onToggleHindi
            )
        }

        if (hasWordMeaning) {

            PremiumToggleChip(

                icon =
                    Icons.Default.MenuBook,

                label =
                    "Words"
                        .translate(displayLanguage),

                selected =
                    showWordMeaning,

                onClick =
                    onToggleWordMeaning
            )
        }

        PremiumToggleChip(

            icon =
                Icons.Default.FormatAlignCenter,

            label =
                "Center"
                    .translate(displayLanguage),

            selected =
                showCenter,

            onClick =
                onToggleCenter
        )

        PremiumToggleChip(

            icon =
                Icons.Default.TextFields,

            label =
                "Larivaar"
                    .translate(displayLanguage),

            selected =
                showLarivaar,

            onClick =
                onToggleLarivaar
        )
    }
}

@Composable
fun PremiumToggleChip(

    icon: ImageVector,

    label: String,

    selected: Boolean,

    onClick: () -> Unit
) {

    val backgroundBrush =

        if (selected) {

            Brush.horizontalGradient(

                listOf(

                    Accent.copy(alpha = 0.14f),

                    Gold.copy(alpha = 0.08f)
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

    val borderColor =

        if (selected) {

            Accent.copy(alpha = 0.16f)

        } else {

            TextSecondary.copy(alpha = 0.06f)
        }

    val contentColor =

        if (selected)
            Accent
        else
            TextSecondary

    Surface(

        modifier =
            Modifier.height(34.dp),

        onClick = onClick,

        shape =
            RoundedCornerShape(12.dp),

        color =
            Color.Transparent,

        border = BorderStroke(
            1.dp,
            borderColor
        )
    ) {

        Row(

            modifier = Modifier
                .background(backgroundBrush)
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.spacedBy(6.dp)
        ) {

            Icon(

                imageVector = icon,

                contentDescription = null,

                tint = contentColor,

                modifier =
                    Modifier.size(12.dp)
            )

            Text(

                text = label,

                color = contentColor,

                fontSize = 10.sp,

                lineHeight = 10.sp,

                fontWeight =

                    if (selected)
                        FontWeight.SemiBold
                    else
                        FontWeight.Medium
            )
        }
    }
}