package com.creativeblisslabs.nitnemguktasteek.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * GURMUKHI FONT FAMILIES
 */
enum class GurmukhiFontFamily(
    val title: String,
    val requiredTier: String = "free",
    val description: String = ""
) {
    Default("Standard Gurmukhi", "free", "System default optimized for clear legibility"),
    AnmolLipi("Anmol Lipi", "supporter", "Traditional, graceful Sikh scripture typeface"),
    GurbaniAkhar("Gurbani Akhar Heavy", "premium", "Classic bold Gutka Sahib print calligraphy"),
    Raaj("Raaj Royal Calligraphy", "premium", "Elegant royal Gurmukhi strokes for deep focus"),
    OpenGurbani("Open Gurbani Akhar", "free", "Modern balanced open Gurmukhi typeface")
}

/**
 * ROMAN TRANSLITERATION FONT FAMILIES
 */
enum class RomanFontFamily(
    val title: String,
    val requiredTier: String = "free",
    val description: String = ""
) {
    Default("Standard Sans", "free", "System default clean sans-serif"),
    Serif("Classical Serif", "supporter", "Spiritual book-grade classical serif"),
    Monospace("Balanced Mono", "premium", "Precision character-aligned transliteration"),
    Rounded("Gentle Rounded", "free", "Soft rounded friendly letterforms")
}

/**
 * DEVANAGARI (HINDI) FONT FAMILIES
 */
enum class DevanagariFontFamily(
    val title: String,
    val requiredTier: String = "free",
    val description: String = ""
) {
    Default("Standard Devanagari", "free", "Clean modern Hindi scripture typeface"),
    YatraOne("Vedic Shirorekha", "supporter", "Devotional classical Shirorekha Devanagari"),
    RozhaOne("Poetic Teeka Serif", "premium", "High-contrast elegant Teeka lettering"),
    Mukta("Mukta Devanagari", "free", "Modern ultra-clear open Hindi font")
}

/**
 * Resolves standard Compose FontFamily for Gurmukhi
 */
fun resolveGurmukhiFont(family: GurmukhiFontFamily): FontFamily {
    return when (family) {
        GurmukhiFontFamily.Default -> FontFamily.Default
        GurmukhiFontFamily.AnmolLipi -> FontFamily.Serif
        GurmukhiFontFamily.GurbaniAkhar -> FontFamily.Default
        GurmukhiFontFamily.Raaj -> FontFamily.Cursive
        GurmukhiFontFamily.OpenGurbani -> FontFamily.SansSerif
    }
}

/**
 * Resolves standard Compose FontFamily for Roman transliteration
 */
fun resolveRomanFont(family: RomanFontFamily): FontFamily {
    return when (family) {
        RomanFontFamily.Default -> FontFamily.Default
        RomanFontFamily.Serif -> FontFamily.Serif
        RomanFontFamily.Monospace -> FontFamily.Monospace
        RomanFontFamily.Rounded -> FontFamily.SansSerif
    }
}

/**
 * Resolves standard Compose FontFamily for Devanagari
 */
fun resolveDevanagariFont(family: DevanagariFontFamily): FontFamily {
    return when (family) {
        DevanagariFontFamily.Default -> FontFamily.Default
        DevanagariFontFamily.YatraOne -> FontFamily.Serif
        DevanagariFontFamily.RozhaOne -> FontFamily.Serif
        DevanagariFontFamily.Mukta -> FontFamily.SansSerif
    }
}
