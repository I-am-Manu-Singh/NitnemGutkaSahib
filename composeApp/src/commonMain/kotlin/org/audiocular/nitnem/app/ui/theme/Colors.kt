package org.audiocular.nitnem.app.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/*
 * APP THEME
 */

object AppTheme {

    var themeMode by mutableStateOf(
        ThemeMode.Light
    )

    var accentStyle by mutableStateOf(
        AccentStyle.Emerald
    )

    val colors: ThemeColors
        get() = when (themeMode) {

            ThemeMode.Light ->
                LightThemeColors

            ThemeMode.Dark ->
                DarkThemeColors

            ThemeMode.Amoled ->
                AmoledThemeColors
        }
}

/*
 * THEME MODES
 */

enum class ThemeMode {

    Light,
    Dark,
    Amoled
}

/*
 * ACCENT STYLES
 */

enum class AccentStyle {

    Emerald,
    Sapphire,
    Royal,
    Sunset
}

/*
 * THEME COLORS CONTRACT
 */

interface ThemeColors {

    val background: Color

    val surface: Color

    val surfaceVariant: Color

    val primary: Color

    val secondary: Color

    val accent: Color

    val gold: Color

    val textPrimary: Color

    val textSecondary: Color
}

/*
 * LIGHT THEME
 */

object LightThemeColors : ThemeColors {

    override val background =
        Color(0xFFF8F6F1)

    override val surface =
        Color(0xFFFFFCF6)

    override val surfaceVariant =
        Color(0xFFE8F2EE)

    override val primary =
        Color(0xFF123C36)

    override val secondary =
        Color(0xFF4A5C58)

    override val accent: Color
        get() = when (AppTheme.accentStyle) {

            AccentStyle.Emerald ->
                Color(0xFF2C6E62)

            AccentStyle.Sapphire ->
                Color(0xFF2F6BCE)

            AccentStyle.Royal ->
                Color(0xFF7856D8)

            AccentStyle.Sunset ->
                Color(0xFFE67E3D)
        }

    override val gold =
        Color(0xFFD6A34D)

    override val textPrimary =
        Color(0xFF1E1E1E)

    override val textSecondary =
        Color(0xFF5E5E5E)
}

/*
 * DARK THEME
 */

object DarkThemeColors : ThemeColors {

    override val background =
        Color(0xFF101010)

    override val surface =
        Color(0xFF181818)

    override val surfaceVariant =
        Color(0xFF222222)

    override val primary =
        Color(0xFFF2F2F2)

    override val secondary =
        Color(0xFFAAAAAA)

    override val accent: Color
        get() = when (AppTheme.accentStyle) {

            AccentStyle.Emerald ->
                Color(0xFF57C7A5)

            AccentStyle.Sapphire ->
                Color(0xFF63A8FF)

            AccentStyle.Royal ->
                Color(0xFFAD8CFF)

            AccentStyle.Sunset ->
                Color(0xFFFFB067)
        }

    override val gold =
        Color(0xFFE9C46A)

    override val textPrimary =
        Color(0xFFF0F0F0)

    override val textSecondary =
        Color(0xFFB3B3B3)
}

/*
 * AMOLED THEME
 */

object AmoledThemeColors : ThemeColors {

    override val background =
        Color(0xFF000000)

    override val surface =
        Color(0xFF050505)

    override val surfaceVariant =
        Color(0xFF0D0D0D)

    override val primary =
        Color(0xFFF5F5F5)

    override val secondary =
        Color(0xFFB8B8B8)

    override val accent: Color
        get() = when (AppTheme.accentStyle) {

            AccentStyle.Emerald ->
                Color(0xFF44D7B6)

            AccentStyle.Sapphire ->
                Color(0xFF5DAEFF)

            AccentStyle.Royal ->
                Color(0xFF9B7CFF)

            AccentStyle.Sunset ->
                Color(0xFFFF9E57)
        }

    override val gold =
        Color(0xFFF0C265)

    override val textPrimary =
        Color(0xFFF5F5F5)

    override val textSecondary =
        Color(0xFFB0B0B0)
}

/*
 * GLOBAL COLOR ACCESSORS
 */

val Background: Color
    get() = AppTheme.colors.background

val SurfaceColor: Color
    get() = AppTheme.colors.surface

val SurfaceVariant: Color
    get() = AppTheme.colors.surfaceVariant

val Primary: Color
    get() = AppTheme.colors.primary

val Secondary: Color
    get() = AppTheme.colors.secondary

val Accent: Color
    get() = AppTheme.colors.accent

val Gold: Color
    get() = AppTheme.colors.gold

val TextPrimary: Color
    get() = AppTheme.colors.textPrimary

val TextSecondary: Color
    get() = AppTheme.colors.textSecondary

/*
 * EXTRA HELPERS
 */

val Cream: Color
    get() = SurfaceColor

val Mist: Color
    get() = SurfaceVariant

val DeepGreen: Color
    get() = Primary

val Emerald: Color
    get() = Accent

val Rose: Color
    get() = SurfaceVariant

/*
 * DYNAMIC UI COLORS
 */

val AccentSoft: Color
    get() = Accent.copy(alpha = 0.12f)

val AccentBorder: Color
    get() = Accent.copy(alpha = 0.08f)

val AccentStrong: Color
    get() = Accent.copy(alpha = 0.22f)

val GurmukhiColor: Color
    get() = Primary

val RomanTransliterationColor: Color
    get() = Gold

val TranslationColor: Color
    get() = TextPrimary.copy(alpha = 0.72f)

val WordMeaningColor: Color
    get() = Accent

/*
 * TYPOGRAPHY DEFAULTS
 */

object TypographyDefaults {

    const val GURMUKHI =
        30f

    const val ROMAN =
        19f

    const val TRANSLATION =
        18f

    const val WORD_MEANING =
        16f
}