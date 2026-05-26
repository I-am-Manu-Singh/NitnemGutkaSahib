package org.audiocular.nitnem.app.ui.practice

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.audiocular.nitnem.app.ui.theme.*

/*
 * PRACTICE UI COLORS
 *
 * Refactored to fully use centralized theme system.
 * Because duplicating colors everywhere is how apps
 * slowly become haunted forests of inconsistencies.
 */

data class PracticeColors(

    val accent: Color,

    val card: Color,

    val cardSecondary: Color,

    val textPrimary: Color,

    val textSecondary: Color,

    val success: Color,

    val error: Color,

    val border: Color,

    val glow: Color
)

@Composable
fun practiceColors(): PracticeColors {

    return PracticeColors(

        /*
         * CORE
         */

        accent = Accent,

        card = SurfaceColor,

        cardSecondary = SurfaceVariant,

        textPrimary = TextPrimary,

        textSecondary = TextSecondary,

        /*
         * STATES
         */

        success = when (AppTheme.themeMode) {

            ThemeMode.Light ->
                Color(0xFF239B63)

            ThemeMode.Dark,
            ThemeMode.Amoled ->
                Color(0xFF46D39A)
        },

        error = when (AppTheme.themeMode) {

            ThemeMode.Light ->
                Color(0xFFD84B4B)

            ThemeMode.Dark,
            ThemeMode.Amoled ->
                Color(0xFFFF7070)
        },

        /*
         * EXTRA PREMIUM HELPERS
         */

        border = AccentBorder,

        glow = AccentSoft
    )
}