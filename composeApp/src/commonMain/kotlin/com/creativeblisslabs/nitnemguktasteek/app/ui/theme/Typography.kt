package com.creativeblisslabs.nitnemguktasteek.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import nitnemguktasteek.composeapp.generated.resources.*
import org.jetbrains.compose.resources.Font

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
    GurbaniAkhar("Gurbani Akhar Heavy", "premium", "Classic bold Gutka Steek print calligraphy"),
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
    Default("Standard Sans", "free", "Clean modern transliteration typeface"),
    Serif("Classical Serif (Lora)", "supporter", "Spiritual book-grade classical serif"),
    Monospace("Balanced Mono (Roboto)", "premium", "Precision character-aligned transliteration"),
    Rounded("Mukta Bold", "free", "Soft prominent letterforms")
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
    YatraOne("Vedic Shirorekha (Yatra)", "supporter", "Devotional classical Shirorekha Devanagari"),
    RozhaOne("Poetic Teeka Serif (Rozha)", "premium", "High-contrast elegant Teeka lettering"),
    Mukta("Mukta Devanagari Bold", "free", "Modern ultra-clear open Hindi font")
}

/**
 * Resolves Compose FontFamily for Gurmukhi
 */
@Composable
fun resolveGurmukhiFont(family: GurmukhiFontFamily): FontFamily {
    return when (family) {
        GurmukhiFontFamily.Default -> FontFamily(Font(Res.font.mukta_gurmukhi))
        GurmukhiFontFamily.AnmolLipi -> FontFamily(Font(Res.font.anmol_lipi))
        GurmukhiFontFamily.GurbaniAkhar -> FontFamily(Font(Res.font.gurbani_akhar))
        GurmukhiFontFamily.Raaj -> FontFamily(Font(Res.font.mukta_gurmukhi_bold))
        GurmukhiFontFamily.OpenGurbani -> FontFamily(Font(Res.font.mukta_gurmukhi))
    }
}

/**
 * Resolves Compose FontFamily for Gurmukhi by string key
 */
@Composable
fun resolveGurmukhiFontByKey(key: String): FontFamily {
    val matched = GurmukhiFontFamily.entries.firstOrNull { it.name.equals(key, ignoreCase = true) }
        ?: GurmukhiFontFamily.Default
    return resolveGurmukhiFont(matched)
}

/**
 * Resolves Compose FontFamily for Roman transliteration
 */
@Composable
fun resolveRomanFont(family: RomanFontFamily): FontFamily {
    return when (family) {
        RomanFontFamily.Default -> FontFamily(Font(Res.font.mukta_regular))
        RomanFontFamily.Serif -> FontFamily(Font(Res.font.lora_regular))
        RomanFontFamily.Monospace -> FontFamily(Font(Res.font.roboto_mono))
        RomanFontFamily.Rounded -> FontFamily(Font(Res.font.mukta_bold))
    }
}

/**
 * Resolves Compose FontFamily for Roman transliteration by string key
 */
@Composable
fun resolveRomanFontByKey(key: String): FontFamily {
    val matched = RomanFontFamily.entries.firstOrNull { it.name.equals(key, ignoreCase = true) }
        ?: RomanFontFamily.Default
    return resolveRomanFont(matched)
}

/**
 * Resolves Compose FontFamily for Devanagari
 */
@Composable
fun resolveDevanagariFont(family: DevanagariFontFamily): FontFamily {
    return when (family) {
        DevanagariFontFamily.Default -> FontFamily(Font(Res.font.mukta_regular))
        DevanagariFontFamily.YatraOne -> FontFamily(Font(Res.font.yatra_one))
        DevanagariFontFamily.RozhaOne -> FontFamily(Font(Res.font.rozha_one))
        DevanagariFontFamily.Mukta -> FontFamily(Font(Res.font.mukta_bold))
    }
}

/**
 * Resolves Compose FontFamily for Devanagari by string key
 */
@Composable
fun resolveDevanagariFontByKey(key: String): FontFamily {
    val matched = DevanagariFontFamily.entries.firstOrNull { it.name.equals(key, ignoreCase = true) }
        ?: DevanagariFontFamily.Default
    return resolveDevanagariFont(matched)
}
