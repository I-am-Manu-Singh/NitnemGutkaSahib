package org.audiocular.nitnem.app.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.app.ui.theme.Accent
import org.audiocular.nitnem.app.ui.theme.TextPrimary
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateHindiTransliteration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush

import org.audiocular.nitnem.app.ui.theme.Gold
import org.audiocular.nitnem.app.ui.theme.SurfaceVariant
import org.audiocular.nitnem.app.ui.theme.TextSecondary

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
    gurmukhiColorMode: String,
    translationColorMode: String,
    wordMeaningColorMode: String,
    displayLanguage: DisplayLanguage = DisplayLanguage.English,
    onToggleEnglish: (Boolean) -> Unit = {},
    onTogglePunjabi: (Boolean) -> Unit = {},
    onToggleHindi: (Boolean) -> Unit = {},
    onToggleWordMeaning: (Boolean) -> Unit = {}
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
ModalBottomSheet(

    onDismissRequest = onDismiss,

    sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

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


            // ── Header ──────────────────────────────────────────────────
            Text(
                text = "Line Meaning".translate(displayLanguage),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Accent,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Tap chips below to toggle visible sections".translate(displayLanguage),
                fontSize = 12.sp,
                color = colors.onSurfaceVariant.copy(alpha = 0.55f),
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // ── Inline settings chips ────────────────────────────────────
           /*
 * PREMIUM TOGGLE SWITCHER
 *
 * Replace your entire Row(...) chips section with this.
 * Current chips look like default Material leftovers.
 * These feel intentional, compact, and premium.
 */

PremiumMeaningSwitcher(

    showHindi = showDevanagariTransliteration,

    showEnglish = showEnglishTranslation,

    showPunjabi = showPunjabiTranslation,

    showWordMeaning = showWordMeaning,

    hasWordMeaning =
        line.wordMeaning.isNotBlank(),

    displayLanguage = displayLanguage,

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

    onToggleWordMeaning = {
        onToggleWordMeaning(
            !showWordMeaning
        )
    }
)

            Spacer(modifier = Modifier.height(20.dp))

            // ── Gurmukhi ─────────────────────────────────────────────────
            Text(
                text = line.gurmukhi,
                fontSize = gurmukhiFontSize.sp,
                lineHeight = lineSpacing.sp,
                color = gurmukhiColor,
                fontWeight = if (boldGurmukhi) FontWeight.Bold else FontWeight.Medium,
                textAlign = if (centerGurmukhi) TextAlign.Center else TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            // ── Roman Transliteration ─────────────────────────────────────
            if (line.transliteration.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                MeaningRow(
                    icon = Icons.Default.Language,
                    text = line.transliteration,
                    color = colors.onSurfaceVariant.copy(alpha = 0.82f),
                    fontSize = (gurmukhiFontSize * 0.62f),
                    lineSpacing = lineSpacing,
                    italic = true
                )
            }

            // ── Hindi Transliteration ─────────────────────────────────────
            if (showDevanagariTransliteration && line.hindi.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.25f))
                Spacer(modifier = Modifier.height(12.dp))
                SectionLabel(
                    icon = Icons.Default.AutoStories,
                    label = "Hindi Translation".translate(displayLanguage)
                )
                Spacer(modifier = Modifier.height(6.dp))
                MeaningRow(
                    icon = Icons.Default.AutoStories,
                    text = line.hindi.translateHindiTransliteration(),
                    color = translationColor,
                    fontSize = (gurmukhiFontSize * 0.72f),
                    lineSpacing = lineSpacing,
                    showIcon = false
                )
            }

            // ── Word Meanings ─────────────────────────────────────────────
            if (showWordMeaning && line.wordMeaning.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.25f))
                Spacer(modifier = Modifier.height(12.dp))
                SectionLabel(
                    icon = Icons.Default.MenuBook,
                    label = "Word Meanings".translate(displayLanguage)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = line.wordMeaning,
                    fontSize = (gurmukhiFontSize * 0.68f).sp,
                    lineHeight = (lineSpacing * 0.85f).sp,
                    color = wordMeaningColor
                )
            }

            // ── English Translation ───────────────────────────────────────
            if (showEnglishTranslation && line.translation.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.25f))
                Spacer(modifier = Modifier.height(12.dp))
                SectionLabel(
                    icon = Icons.Default.Translate,
                    label = "English Translation".translate(displayLanguage)
                )
                Spacer(modifier = Modifier.height(6.dp))
                MeaningRow(
                    icon = Icons.Default.Translate,
                    text = line.translation,
                    color = translationColor,
                    fontSize = (gurmukhiFontSize * 0.72f),
                    lineSpacing = lineSpacing,
                    showIcon = false
                )
            }

            // ── Punjabi Translation ───────────────────────────────────────
            if (showPunjabiTranslation && line.punjabi.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.25f))
                Spacer(modifier = Modifier.height(12.dp))
                SectionLabel(
                    icon = Icons.Default.MenuBook,
                    label = "Punjabi Translation".translate(displayLanguage)
                )
                Spacer(modifier = Modifier.height(6.dp))
                MeaningRow(
                    icon = Icons.Default.MenuBook,
                    text = line.punjabi,
                    color = translationColor,
                    fontSize = (gurmukhiFontSize * 0.72f),
                    lineSpacing = lineSpacing,
                    showIcon = false
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(
    icon: ImageVector,
    label: String
) {
    val colors = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Accent.copy(alpha = 0.72f),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Accent,
            fontWeight = FontWeight.SemiBold
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
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        if (showIcon) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color.copy(alpha = 0.55f),
                modifier = Modifier
                    .padding(top = 3.dp)
                    .size(13.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            color = color,
            fontSize = fontSize.sp,
            lineHeight = lineSpacing.sp,
            fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeaningChip(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Accent.copy(alpha = 0.14f),
            selectedLabelColor = Accent,
            selectedLeadingIconColor = Accent
        )
    )
}

private fun resolveTextColor(
    mode: String,
    colors: ColorScheme,
    default: Color
): Color {
    return when (mode) {
        "gold" -> Color(0xFFD6B25E)
        "accent" -> colors.primary
        "muted" -> colors.onSurfaceVariant.copy(alpha = 0.72f)
        "white" -> if (colors.surface.luminance() > 0.5f) colors.onSurface else Color.White
        "purple" -> Color(0xFFB39DFF)
        "blue" -> Color(0xFF7AB8FF)
        "green" -> Color(0xFF63D8B6)
        "orange" -> Color(0xFFFFB36A)
        else -> default
    }
}

@Composable
private fun PremiumMeaningSwitcher(

    showHindi: Boolean,

    showEnglish: Boolean,

    showPunjabi: Boolean,

    showWordMeaning: Boolean,

    hasWordMeaning: Boolean,

    displayLanguage: DisplayLanguage,

    onToggleHindi: () -> Unit,

    onToggleEnglish: () -> Unit,

    onTogglePunjabi: () -> Unit,

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

        PremiumToggleChip(

            icon =
                Icons.Default.AutoStories,

            label =
                "Hindi".translate(
                    displayLanguage
                ),

            selected =
                showHindi,

            onClick =
                onToggleHindi
        )

        PremiumToggleChip(

            icon =
                Icons.Default.Translate,

            label =
                "English".translate(
                    displayLanguage
                ),

            selected =
                showEnglish,

            onClick =
                onToggleEnglish
        )

        PremiumToggleChip(

            icon =
                Icons.Default.MenuBook,

            label =
                "Punjabi".translate(
                    displayLanguage
                ),

            selected =
                showPunjabi,

            onClick =
                onTogglePunjabi
        )

        if (hasWordMeaning) {

            PremiumToggleChip(

                icon =
                    Icons.Default.Language,

                label =
                    "Meanings".translate(
                        displayLanguage
                    ),

                selected =
                    showWordMeaning,

                onClick =
                    onToggleWordMeaning
            )
        }
    }
}
@Composable
private fun PremiumToggleChip(

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

        modifier = Modifier
            .height(34.dp),

        onClick = onClick,

        shape =
            RoundedCornerShape(12.dp),

        color = Color.Transparent,

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