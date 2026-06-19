package org.audiocular.nitnem.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translateDigits
data class BaniTheme(

    val accent: Color,

    val gradient: List<Color>,

    val icon: ImageVector,

    val ambientIcon: ImageVector =
        Icons.Default.MenuBook,

    val author: String,

    val ang: String,

    val structure: String,

    val about: String = "",

    val significance: String = "",

    /*
     * HERO CARD
     */

    val heroTitle: String = "",

    val heroSubtitle: String = "",

    val heroStatus: String = "",

    val heroTime: String = ""
)
fun resolveBaniTheme(
    title: String
): BaniTheme {

    val normalized =
        title.lowercase()

    val base = when {

        /*
         * 1. JAPJI SAHIB
         */
        "japji" in normalized -> {

            BaniTheme(

                accent = Gold,

                gradient = listOf(
                    Color(0xFF7A4A1E),
                    Color(0xFFD6A34D)
                ),

                icon =
                    Icons.Default.LightMode,

                ambientIcon =
                    Icons.Default.WbTwilight,

                author =
                    "Guru Nanak Dev Ji",

                ang =
                    "Ang 1",

                structure =
                    "38 Pauris, 2 Saloks",

                heroTitle =
                    "Amrit Vela",

                heroSubtitle =
                    "Rise with Simran",

                heroStatus =
                    "Spiritual energy peaks before sunrise.",

                heroTime =
                    "3 AM - 6 AM"
            )
        }

        /*
         * 2. JAAP SAHIB
         */
        "jaap" in normalized -> {

            BaniTheme(

                accent = Accent,

                gradient = listOf(
                    Color(0xFF18453E),
                    Color(0xFF2C6E62)
                ),

                icon =
                    Icons.Default.Bolt,

                ambientIcon =
                    Icons.Default.WbSunny,

                author =
                    "Guru Gobind Singh Ji",

                ang =
                    "Dasam Granth Ang 1",

                structure =
                    "199 Verses",

                heroTitle =
                    "Morning",

                heroSubtitle =
                    "Focused Nitnem",

                heroStatus =
                    "Morning Gurbani builds focus for the day.",

                heroTime =
                    "7 AM - 11 AM"
            )
        }

        /*
         * 3. TAV PRASAD SAVAIYE
         */
        "savaiye" in normalized ||
        "tav prasad" in normalized -> {

            BaniTheme(

                accent = AccentSoft,

                gradient = listOf(
                    Color(0xFF8D6E63),
                    Color(0xFFB0AFAC)
                ),

                icon =
                    Icons.Default.GppGood,

                ambientIcon =
                    Icons.Default.AutoAwesome,

                author =
                    "Guru Gobind Singh Ji",

                ang =
                    "Dasam Granth Ang 13",

                structure =
                    "10 Savaiye",

                heroTitle =
                    "Morning",

                heroSubtitle =
                    "Strength & Wisdom",

                heroStatus =
                    "Reflect on devotion and clarity.",

                heroTime =
                    "7 AM - 11 AM"
            )
        }

        /*
         * 4. BENTI CHAUPAI SAHIB
         */
        "chaupai" in normalized ||
        "benti" in normalized -> {

            BaniTheme(

                accent = Emerald,

                gradient = listOf(
                    Emerald.copy(alpha = 0.72f),
                    Emerald
                ),

                icon =
                    Icons.Default.Security,

                ambientIcon =
                    Icons.Default.Shield,

                author =
                    "Guru Gobind Singh Ji",

                ang =
                    "Dasam Granth Ang 1386",

                structure =
                    "29 Verses",

                heroTitle =
                    "Protection",

                heroSubtitle =
                    "Fearless Spirit",

                heroStatus =
                    "Seek courage and divine protection.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * 5. ANAND SAHIB
         */
        "anand" in normalized -> {

            BaniTheme(

                accent = Accent,

                gradient = listOf(
                    Accent,
                    Gold
                ),

                icon =
                    Icons.Default.Celebration,

                ambientIcon =
                    Icons.Default.LightMode,

                author =
                    "Guru Amar Das Ji",

                ang =
                    "Ang 917",

                structure =
                    "40 Pauris",

                heroTitle =
                    "Afternoon",

                heroSubtitle =
                    "Inner Reflection",

                heroStatus =
                    "Pause and reconnect with Gurbani.",

                heroTime =
                    "12 PM - 3 PM"
            )
        }

        /*
         * 6. REHRAS SAHIB
         */
        "rehras" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFF9575CD),

                gradient = listOf(
                    Color(0xFF6A4FB3),
                    Color(0xFF9575CD)
                ),

                icon =
                    Icons.Default.WbTwilight,

                ambientIcon =
                    Icons.Default.AutoAwesome,

                author =
                    "Multiple Gurus",

                ang =
                    "Ang 8",

                structure =
                    "Compilation",

                heroTitle =
                    "Evening",

                heroSubtitle =
                    "Sunset Prayer",

                heroStatus =
                    "Transition peacefully into the evening.",

                heroTime =
                    "4 PM - 6 PM"
            )
        }

        /*
         * 7. KIRTAN SOHILA
         */
        "sohila" in normalized -> {

            BaniTheme(

                accent = Secondary,

                gradient = listOf(
                    SurfaceVariant,
                    Secondary
                ),

                icon =
                    Icons.Default.NightlightRound,

                ambientIcon =
                    Icons.Default.Stars,

                author =
                    "Multiple Gurus",

                ang =
                    "Ang 12",

                structure =
                    "5 Shabads",

                heroTitle =
                    "Night",

                heroSubtitle =
                    "Peaceful Rest",

                heroStatus =
                    "Prepare the mind for peaceful sleep.",

                heroTime =
                    "Before Sleep"
            )
        }

        /*
         * 8. SUKHMANI SAHIB
         */
        "sukhmani" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFF2E7D32),

                gradient = listOf(
                    Color(0xFF1B5E20),
                    Color(0xFF4CAF50)
                ),

                icon =
                    Icons.Default.Spa,

                ambientIcon =
                    Icons.Default.Spa,

                author =
                    "Guru Arjan Dev Ji",

                ang =
                    "Ang 262",

                structure =
                    "24 Ashtpadis, 24 Saloks",

                heroTitle =
                    "Peace",

                heroSubtitle =
                    "Meditative Calm",

                heroStatus =
                    "Find tranquility through Gurbani.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * 9. ASA DI VAAR
         */
        "asa" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFFE65100),

                gradient = listOf(
                    Color(0xFFBF360C),
                    Color(0xFFFF9800)
                ),

                icon =
                    Icons.Default.GraphicEq,

                ambientIcon =
                    Icons.Default.WbSunny,

                author =
                    "Guru Nanak Dev Ji",

                ang =
                    "Ang 462",

                structure =
                    "24 Pauris, 59 Saloks",

                heroTitle =
                    "Morning Kirtan",

                heroSubtitle =
                    "Spiritual Energy",

                heroStatus =
                    "Begin the day with uplifting Gurbani.",

                heroTime =
                    "Morning"
            )
        }

        /*
         * 10. ARDAS
         */
        "ardas" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFFE57373),

                gradient = listOf(
                    Color(0xFFB84C4C),
                    Color(0xFFE57373)
                ),

                icon =
                    Icons.Default.VolunteerActivism,

                ambientIcon =
                    Icons.Default.Favorite,

                author =
                    "Traditional",

                ang =
                    "Sikh Rehat Maryada",

                structure =
                    "Supplication",

                heroTitle =
                    "Ardas",

                heroSubtitle =
                    "Prayer & Gratitude",

                heroStatus =
                    "Offer devotion with humility.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * 11. SHABAD HAZARE
         */
        "shabad hazare" in normalized ||
        "hazare" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFF4FC3F7),

                gradient = listOf(
                    Color(0xFF0288D1),
                    Color(0xFF4FC3F7)
                ),

                icon =
                    Icons.Default.Mail,

                ambientIcon =
                    Icons.Default.MarkEmailRead,

                author =
                    "Guru Nanak Dev Ji & Guru Arjan Dev Ji",

                ang =
                    "Ang 96",

                structure =
                    "7 Shabads",

                heroTitle =
                    "Divine Letters",

                heroSubtitle =
                    "Love & Devotion",

                heroStatus =
                    "Reflect on longing for the Divine.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * 12. SALOK MAHALLA 9
         */
        "salok mahalla" in normalized ||
        "salok" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFF8D6E63),

                gradient = listOf(
                    Color(0xFF5D4037),
                    Color(0xFF8D6E63)
                ),

                icon =
                    Icons.Default.SelfImprovement,

                ambientIcon =
                    Icons.Default.AutoStories,

                author =
                    "Guru Tegh Bahadur Ji",

                ang =
                    "Ang 1426",

                structure =
                    "57 Saloks",

                heroTitle =
                    "Wisdom",

                heroSubtitle =
                    "Detachment & Courage",

                heroStatus =
                    "Reflect deeply on life and fearlessness.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * 13. BARAH MAHA
         */
        "barah maha" in normalized ||
        "barah" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFFFFB74D),

                gradient = listOf(
                    Color(0xFFE65100),
                    Color(0xFFFFB74D)
                ),

                icon =
                    Icons.Default.CalendarMonth,

                ambientIcon =
                    Icons.Default.CalendarToday,

                author =
                    "Guru Arjan Dev Ji",

                ang =
                    "Ang 133",

                structure =
                    "14 Stanzas",

                heroTitle =
                    "Seasons of Life",

                heroSubtitle =
                    "Spiritual Journey",

                heroStatus =
                    "Experience devotion through changing seasons.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * 14. DAKHNI OANKAR
         */
        "oankar" in normalized ||
        "dakhni" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFF81C784),

                gradient = listOf(
                    Color(0xFF2E7D32),
                    Color(0xFF81C784)
                ),

                icon =
                    Icons.Default.Hub,

                ambientIcon =
                    Icons.Default.AccountTree,

                author =
                    "Guru Nanak Dev Ji",

                ang =
                    "Ang 929",

                structure =
                    "54 Pauris",

                heroTitle =
                    "Divine Oneness",

                heroSubtitle =
                    "Universal Truth",

                heroStatus =
                    "Understand the essence of Ik Onkar.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * 15. SIDHA GOSHT
         */
        "sidha" in normalized ||
        "gosht" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFF90CAF9),

                gradient = listOf(
                    Color(0xFF1565C0),
                    Color(0xFF90CAF9)
                ),

                icon =
                    Icons.Default.Forum,

                ambientIcon =
                    Icons.Default.QuestionAnswer,

                author =
                    "Guru Nanak Dev Ji",

                ang =
                    "Ang 938",

                structure =
                    "73 Pauris",

                heroTitle =
                    "Spiritual Dialogue",

                heroSubtitle =
                    "Wisdom & Questions",

                heroStatus =
                    "Explore profound spiritual discussions.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * 16. BASANT KI VAAR
         */
        "basant" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFFAED581),

                gradient = listOf(
                    Color(0xFF558B2F),
                    Color(0xFFAED581)
                ),

                icon =
                    Icons.Default.Grass,

                ambientIcon =
                    Icons.Default.LocalFlorist,

                author =
                    "Guru Arjan Dev Ji",

                ang =
                    "Ang 1193",

                structure =
                    "3 Pauris",

                heroTitle =
                    "Spring of Spirit",

                heroSubtitle =
                    "Renewal & Growth",

                heroStatus =
                    "Refresh the soul with Gurbani.",

                heroTime =
                    "Spring Morning"
            )
        }

        /*
         * 17. RAMKALI KI VAAR
         */
        "ramkali" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFFBA68C8),

                gradient = listOf(
                    Color(0xFF7B1FA2),
                    Color(0xFFBA68C8)
                ),

                icon =
                    Icons.Default.MusicNote,

                ambientIcon =
                    Icons.Default.LibraryMusic,

                author =
                    "Guru Amar Das Ji",

                ang =
                    "Ang 947",

                structure =
                    "21 Pauris",

                heroTitle =
                    "Transformation",

                heroSubtitle =
                    "Inner Change",

                heroStatus =
                    "Embrace spiritual awakening.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * 18. AARTI
         */
        "aarti" in normalized ||
        "arti" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFFFFD54F),

                gradient = listOf(
                    Color(0xFFF57F17),
                    Color(0xFFFFD54F)
                ),

                icon =
                    Icons.Default.Star,

                ambientIcon =
                    Icons.Default.AutoAwesome,

                author =
                    "Guru Nanak Dev Ji & Bhagats",

                ang =
                    "Ang 663",

                structure =
                    "5 Shabads",

                heroTitle =
                    "Divine Praise",

                heroSubtitle =
                    "Cosmic Worship",

                heroStatus =
                    "See the universe as divine worship.",

                heroTime =
                    "Evening"
            )
        }

        /*
         * 19. CHANDI DI VAAR
         */
        "chandi" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFFE57373),

                gradient = listOf(
                    Color(0xFFB71C1C),
                    Color(0xFFE57373)
                ),

                icon =
                    Icons.Default.Shield,

                ambientIcon =
                    Icons.Default.GppGood,

                author =
                    "Guru Gobind Singh Ji",

                ang =
                    "Dasam Granth Ang 119",

                structure =
                    "55 Pauris",

                heroTitle =
                    "Warrior Spirit",

                heroSubtitle =
                    "Strength & Courage",

                heroStatus =
                    "Awaken fearless determination.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * 20. RAGMALA
         */
        "ragmala" in normalized -> {

            BaniTheme(

                accent =
                    Color(0xFFB0BEC5),

                gradient = listOf(
                    Color(0xFF37474F),
                    Color(0xFFB0BEC5)
                ),

                icon =
                    Icons.Default.FormatListBulleted,

                ambientIcon =
                    Icons.Default.QueueMusic,

                author =
                    "Compilation",

                ang =
                    "Ang 1429",

                structure =
                    "Bhog",

                heroTitle =
                    "Raag Journey",

                heroSubtitle =
                    "Musical Essence",

                heroStatus =
                    "Experience the flow of divine raags.",

                heroTime =
                    "Any Time"
            )
        }

        /*
         * DEFAULT
         */
        else -> {

            BaniTheme(

                accent = Accent,

                gradient = listOf(
                    Primary,
                    Accent
                ),

                icon =
                    Icons.Default.MenuBook,

                ambientIcon =
                    Icons.Default.AutoStories,

                author =
                    "Traditional Gurbani Recitation",

                ang =
                    "Gurbani",

                structure =
                    "Compilation",

                heroTitle =
                    "Nitnem",

                heroSubtitle =
                    "Daily Gurbani",

                heroStatus =
                    "Stay connected with Gurbani.",

                heroTime =
                    "Any Time"
            )
        }
    }

    return base.copy(

        about =
            getBaniAbout(
                normalized
            ),

        significance =
            getBaniSignificance(
                normalized
            )
    )
}
private fun getBaniAbout(normalized: String): String {
    return when {
        "japji" in normalized -> "Japji Sahib About"
        "jaap" in normalized -> "Jaap Sahib About"
        "savaiye" in normalized || "tav prasad" in normalized -> "Tav Prasad Savaiye About"
        "chaupai" in normalized || "benti" in normalized -> "Benti Chaupai Sahib About"
        "anand" in normalized -> "Anand Sahib About"
        "rehras" in normalized -> "Rehras Sahib About"
        "sohila" in normalized -> "Kirtan Sohila About"
        "sukhmani" in normalized -> "Sukhmani Sahib About"
        "asa" in normalized -> "Asa Di Vaar About"
        "ardas" in normalized -> "Ardas About"
        "shabad hazare" in normalized || "hazare" in normalized -> "Shabad Hazare About"
        "salok mahalla" in normalized || "salok" in normalized -> "Salok Mahalla 9 About"
        "barah maha" in normalized || "barah" in normalized -> "Barah Maha About"
        "oankar" in normalized || "dakhni" in normalized -> "Dakhni Oankar About"
        "sidha" in normalized || "gosht" in normalized -> "Sidha Gosht About"
        "basant" in normalized -> "Basant Ki Vaar About"
        "ramkali" in normalized -> "Ramkali Ki Vaar About"
        "aarti" in normalized || "arti" in normalized -> "Aarti About"
        "chandi" in normalized -> "Chandi Di Vaar About"
        "ragmala" in normalized -> "Ragmala About"
        else -> ""
    }
}

private fun getBaniSignificance(normalized: String): String {
    return when {
        "japji" in normalized -> "Japji Sahib Significance"
        "jaap" in normalized -> "Jaap Sahib Significance"
        "savaiye" in normalized || "tav prasad" in normalized -> "Tav Prasad Savaiye Significance"
        "chaupai" in normalized || "benti" in normalized -> "Benti Chaupai Sahib Significance"
        "anand" in normalized -> "Anand Sahib Significance"
        "rehras" in normalized -> "Rehras Sahib Significance"
        "sohila" in normalized -> "Kirtan Sohila Significance"
        "sukhmani" in normalized -> "Sukhmani Sahib Significance"
        "asa" in normalized -> "Asa Di Vaar Significance"
        "ardas" in normalized -> "Ardas Significance"
        "shabad hazare" in normalized || "hazare" in normalized -> "Shabad Hazare Significance"
        "salok mahalla" in normalized || "salok" in normalized -> "Salok Mahalla 9 Significance"
        "barah maha" in normalized || "barah" in normalized -> "Barah Maha Significance"
        "oankar" in normalized || "dakhni" in normalized -> "Dakhni Oankar Significance"
        "sidha" in normalized || "gosht" in normalized -> "Sidha Gosht Significance"
        "basant" in normalized -> "Basant Ki Vaar Significance"
        "ramkali" in normalized -> "Ramkali Ki Vaar Significance"
        "aarti" in normalized || "arti" in normalized -> "Aarti Significance"
        "chandi" in normalized -> "Chandi Di Vaar Significance"
        "ragmala" in normalized -> "Ragmala Significance"
        else -> ""
    }
}

data class BaniProgressInfo(
    val label: String,
    val current: Int,
    val total: Int,
    val stanzaType: String = ""
) {
    fun getLocalizedLabel(language: DisplayLanguage): String {
        if (stanzaType.isBlank()) return label.translateDigits(language)
        val translatedType = org.audiocular.nitnem.utils.Translations.translate(stanzaType, language)
        return "$translatedType $current / $total".translateDigits(language)
    }
}

fun calculateBaniProgress(
    title: String,
    lines: List<BaniLine>,
    currentLineIndex: Int
): BaniProgressInfo? {
    if (lines.isEmpty()) return null
    val safeIndex = currentLineIndex.coerceIn(0, lines.size - 1)
    val normalized = title.lowercase()

    if ("ardas" in normalized) return null

    val segments = getBaniSegments(title, lines)
    if (segments.isEmpty()) return null

    val segmentIndex = segments.indexOfFirst { safeIndex in it.startIndex..it.endIndex }
    val finalIndex = if (segmentIndex != -1) segmentIndex else {
        val closest = segments.minByOrNull { 
            if (safeIndex < it.startIndex) it.startIndex - safeIndex 
            else safeIndex - it.endIndex 
        }
        closest?.let { segments.indexOf(it) } ?: 0
    }

    val total = segments.size
    val current = finalIndex + 1
    val segment = segments[finalIndex]

    val labelParts = segment.label.split(" ")
    val stanzaType = if (labelParts.size > 1) labelParts[0] else when {
        "sukhmani" in normalized -> "Ashtpadi"
        "japji" in normalized -> "Pauri"
        "jaap" in normalized -> "Verse"
        "savaiye" in normalized || "tav prasad" in normalized -> "Savaiya"
        "chaupai" in normalized || "benti" in normalized -> "Verse"
        "anand" in normalized -> "Pauri"
        "rehras" in normalized -> "Shabad"
        "sohila" in normalized -> "Shabad"
        "asa" in normalized -> "Pauri"
        "shabad hazare" in normalized || "hazare" in normalized -> "Shabad"
        "salok mahalla" in normalized || "salok" in normalized -> "Salok"
        "barah maha" in normalized || "barah" in normalized -> "Stanza"
        "oankar" in normalized || "dakhni" in normalized -> "Pauri"
        "sidha" in normalized || "gosht" in normalized -> "Pauri"
        "basant" in normalized -> "Pauri"
        "ramkali" in normalized -> "Pauri"
        "aarti" in normalized || "arti" in normalized -> "Shabad"
        "chandi" in normalized -> "Pauri"
        "ragmala" in normalized -> "Stanza"
        else -> "Stanza"
    }

    return BaniProgressInfo("$stanzaType $current / $total", current, total, stanzaType)
}

private fun parseGurmukhiNumber(s: String): Int {
    var result = 0
    for (char in s) {
        val digit = when (char) {
            '੦' -> 0
            '੧' -> 1
            '੨' -> 2
            '੩' -> 3
            '੪' -> 4
            '੫' -> 5
            '੬' -> 6
            '੭' -> 7
            '੮' -> 8
            '੯' -> 9
            else -> -1
        }
        if (digit != -1) {
            result = result * 10 + digit
        }
    }
    return result
}

data class BaniSegment(
    val label: String,
    val startIndex: Int,
    val endIndex: Int
)

fun getBaniSegments(title: String, lines: List<BaniLine>): List<BaniSegment> {
    if (lines.isEmpty()) return emptyList()
    val normalized = title.lowercase()

    // 1. Sukhmani Sahib (Ashtpadi chunks)
    if ("sukhmani" in normalized) {
        val segments = mutableListOf<BaniSegment>()
        var startIdx = 0
        var ashtpadiCount = 1
        for (i in lines.indices) {
            val gurmukhi = lines[i].gurmukhi
            if (i > 0 && (gurmukhi.contains("ਸਲੋਕੁ") || gurmukhi.contains("ਸਲੋਕ"))) {
                segments.add(BaniSegment("Ashtpadi $ashtpadiCount", startIdx, i - 1))
                startIdx = i
                ashtpadiCount++
            }
        }
        segments.add(BaniSegment("Ashtpadi $ashtpadiCount", startIdx, lines.size - 1))
        return segments
    }

    // Determine stanza type based on title
    val stanzaType = when {
        "japji" in normalized -> "Pauri"
        "jaap" in normalized -> "Verse"
        "savaiye" in normalized || "tav prasad" in normalized -> "Savaiya"
        "chaupai" in normalized || "benti" in normalized -> "Verse"
        "anand" in normalized -> "Pauri"
        "rehras" in normalized -> "Shabad"
        "sohila" in normalized -> "Shabad"
        "asa" in normalized -> "Pauri"
        "shabad hazare" in normalized || "hazare" in normalized -> "Shabad"
        "salok mahalla" in normalized || "salok" in normalized -> "Salok"
        "barah maha" in normalized || "barah" in normalized -> "Stanza"
        "oankar" in normalized || "dakhni" in normalized -> "Pauri"
        "sidha" in normalized || "gosht" in normalized -> "Pauri"
        "basant" in normalized -> "Pauri"
        "ramkali" in normalized -> "Pauri"
        "aarti" in normalized || "arti" in normalized -> "Shabad"
        "chandi" in normalized -> "Pauri"
        "ragmala" in normalized -> "Stanza"
        else -> "Stanza"
    }

    // Find stanza endings
    val stanzaRegex = Regex("॥\\s*([੦੧੨੩੪੫੬੭੮੯]+)\\s*॥")
    val endings = mutableListOf<Int>()
    for (i in lines.indices) {
        val gurmukhi = lines[i].gurmukhi
        if (stanzaRegex.containsMatchIn(gurmukhi)) {
            endings.add(i)
        }
    }

    if (endings.isEmpty()) {
        // Fallback to chunks of 5 lines if no stanza markers are found
        return lines.chunked(5).mapIndexed { idx, chunk ->
            val start = idx * 5
            val end = minOf(start + chunk.size - 1, lines.size - 1)
            BaniSegment("Lines ${start + 1}-${end + 1}", start, end)
        }
    }

    val segments = mutableListOf<BaniSegment>()
    var startIdx = 0
    var count = 1
    for (endIdx in endings) {
        segments.add(BaniSegment("$stanzaType $count", startIdx, endIdx))
        startIdx = endIdx + 1
        count++
    }
    if (startIdx < lines.size) {
        segments.add(BaniSegment("$stanzaType $count", startIdx, lines.size - 1))
    }
    return segments
}