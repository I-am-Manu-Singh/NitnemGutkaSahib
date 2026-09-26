package com.creativeblisslabs.nitnemguktasteek.utils

import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage

fun String.translate(language: DisplayLanguage): String {
    return Translations.translate(this, language)
}

fun String.translateDigits(language: DisplayLanguage): String {
    if (language == DisplayLanguage.English || language == DisplayLanguage.Hindi) return this
    val digitsMap = when (language) {
        DisplayLanguage.Punjabi -> mapOf(
            '0' to '੦', '1' to '੧', '2' to '੨', '3' to '੩', '4' to '੪',
            '5' to '੫', '6' to '੬', '7' to '੭', '8' to '੮', '9' to '੯'
        )
        else -> emptyMap()
    }
    return this.map { digitsMap[it] ?: it }.joinToString("")
}

fun String.translateHindiTransliteration(): String {
    val digitMap = mapOf(
        '०' to '0', '१' to '1', '२' to '2', '३' to '3', '४' to '4',
        '५' to '5', '६' to '6', '७' to '7', '८' to '8', '९' to '9',
        '੦' to '0', '੧' to '1', '੨' to '2', '੩' to '3', '੪' to '4',
        '੫' to '5', '੬' to '6', '੭' to '7', '੮' to '8', '੯' to '9'
    )
    return this.map { digitMap[it] ?: it }.joinToString("")
}

fun String.translateAng(language: DisplayLanguage): String {
    val dictTranslation = Translations.translate(this, language)
    if (dictTranslation != this) return dictTranslation
    
    val translatedAng = "Ang".translate(language)
    val translatedDasam = "Dasam Granth".translate(language)
    return this.replace("Dasam Granth", translatedDasam)
        .replace("Ang", translatedAng)
        .translateDigits(language)
}

fun translateRaag(raag: String, lang: DisplayLanguage): String {
    val trimmed = raag.trim()
    val startsWithRaag = trimmed.startsWith("raag", ignoreCase = true)
    
    // Determine the part to translate
    val cleanRaag = if (startsWithRaag) {
        if (trimmed.startsWith("raag ", ignoreCase = true)) {
            trimmed.substring(5).trim()
        } else {
            trimmed.substring(4).trim()
        }
    } else {
        trimmed
    }
    
    val normalized = cleanRaag.lowercase()
        .replace("dhanaasaree", "dhanasari")
        .replace("dhanaasari", "dhanasari")
        .replace("dhanasari", "dhanasari")
        .replace("raamkalee", "ramkali")
        .replace("ramkalee", "ramkali")
        .replace("bilaval", "bilawal")
        .replace("bilaaval", "bilawal")
        .replace("sorath", "sorath")
        .replace("sorathh", "sorath")
        .replace("asa", "asa")
        .replace("aasa", "asa")
        .replace("maajh", "majh")
        .replace("gauree", "gauri")
        .replace("gauri", "gauri")
        .replace("kalyan", "kalyan")
        .replace("kalyaan", "kalyan")
        .replace("devgandhari", "devgandhari")
        .replace("devgandhaaree", "devgandhari")
        .replace("jaitsiri", "jaitsiri")
        .replace("jaitsiree", "jaitsiri")
        .replace("tododi", "todi")
        .replace("todi", "todi")
        .replace("toodi", "todi")
        .replace("bairari", "bairari")
        .replace("bairaree", "bairari")
        .replace("tilang", "tilang")
        .replace("suhi", "suhi")
        .replace("soohee", "suhi")
        .replace("suhee", "suhi")
        .replace("sarang", "sarang")
        .replace("saarang", "sarang")
        .replace("malhar", "malhar")
        .replace("malhaar", "malhar")
        .replace("kanara", "kanara")
        .replace("kaanaara", "kanara")
        .replace("jaijavanti", "jaijavanti")
        .replace("jaijaavantee", "jaijavanti")
        .replace("kedara", "kedara")
        .replace("kedaara", "kedara")
        .replace("bhairao", "bhairao")
        .replace("bhairon", "bhairao")
        .replace("gond", "gond")
        .replace("goond", "gond")
        .replace("vadahans", "wadahans")
        .replace("wadahans", "wadahans")
        .replace("vadhans", "wadahans")
        .replace("wadhans", "wadahans")
        .replace("mallaar", "malhar")
        .replace("nat narayan", "nat narayan")
        .replace("nat naraayan", "nat narayan")
        .replace("maru", "maru")
        .replace("maaroo", "maru")
        .replace("tukhari", "tukhari")
        .replace("tukhaaree", "tukhari")
        .replace("prabhati", "prabhati")
        .replace("prabhaatee", "prabhati")
        .replace("gujri", "gujri")
        .replace("goojree", "gujri")
        .replace("gujree", "gujri")
        .replace("siri", "sri")
        .replace("sree", "sri")
        .replace("shree", "sri")
        .replace("sri", "sri")

    val lookupKey = if (normalized.isNotEmpty()) {
        normalized.substring(0, 1).uppercase() + normalized.substring(1)
    } else {
        ""
    }
    
    val translated = Translations.translate(lookupKey, lang)
    val finalRaag = if (translated != lookupKey) {
        translated
    } else {
        when (lang) {
            DisplayLanguage.Punjabi -> when (lookupKey) {
                "Dhanasari" -> "ਧਨਾਸਰੀ"
                "Ramkali" -> "ਰਾਮਕਲੀ"
                "Bilawal" -> "ਬਿਲਾਵਲ"
                "Sorath" -> "ਸੋਰਠਿ"
                "Asa" -> "ਆਸਾ"
                "Majh" -> "ਮਾਝ"
                "Gauri" -> "ਗਉੜੀ"
                "Kalyan" -> "ਕਲਿਆਣ"
                "Devgandhari" -> "ਦੇਵਗੰਧਾਰੀ"
                "Jaitsiri" -> "ਜੈਤਸਰੀ"
                "Todi" -> "ਟੋਡੀ"
                "Bairari" -> "ਬੈਰਾੜੀ"
                "Tilang" -> "ਤਿਲੰਗ"
                "Suhi" -> "ਸੂਹੀ"
                "Sarang" -> "ਸਾਰੰਗ"
                "Malhar" -> "ਮਲਹਾਰ"
                "Kanara" -> "ਕਾਨੜਾ"
                "Jaijavanti" -> "ਜੈਜਾਵੰਤੀ"
                "Kedara" -> "ਕੇਦਾਰਾ"
                "Bhairao" -> "ਭੈਰਉ"
                "Gond" -> "ਗੋਂਡ"
                "Wadahans" -> "ਵਡਹੰਸ"
                "Nat narayan" -> "ਨਟ ਨਾਰਾਇਣ"
                "Maru" -> "ਮਾਰੂ"
                "Tukhari" -> "ਤੁਖਾਰੀ"
                "Prabhati" -> "ਪ੍ਰਭਾਤੀ"
                "Gujri" -> "ਗੂਜਰੀ"
                "Sri" -> "ਸ੍ਰੀ"
                else -> lookupKey
            }
            DisplayLanguage.Hindi -> when (lookupKey) {
                "Dhanasari" -> "धनासरी"
                "Ramkali" -> "रामकली"
                "Bilawal" -> "बिलावल"
                "Sorath" -> "सोरठि"
                "Asa" -> "आसा"
                "Majh" -> "माझ"
                "Gauri" -> "गउड़ी"
                "Kalyan" -> "कल्याण"
                "Devgandhari" -> "देवगंधारी"
                "Jaitsiri" -> "जैतसरी"
                "Todi" -> "टोडी"
                "Bairari" -> "बैराड़ी"
                "Tilang" -> "तिलंग"
                "Suhi" -> "सूही"
                "Sarang" -> "सारंग"
                "Malhar" -> "मल्हार"
                "Kanara" -> "कानड़ा"
                "Jaijavanti" -> "जैजावंती"
                "Kedara" -> "केदारा"
                "Bhairao" -> "भैरउ"
                "Gond" -> "गोंड"
                "Wadahans" -> "वडहंस"
                "Nat narayan" -> "नट नारायण"
                "Maru" -> "मारू"
                "Tukhari" -> "तुखारी"
                "Prabhati" -> "प्रभाती"
                "Gujri" -> "गूजरी"
                "Sri" -> "श्री"
                else -> lookupKey
            }
            else -> lookupKey
        }
    }
    
    return if (startsWithRaag) {
        val raagPrefix = when (lang) {
            DisplayLanguage.Punjabi -> "ਰਾਗ "
            DisplayLanguage.Hindi -> "राग "
            else -> "Raag "
        }
        "$raagPrefix$finalRaag"
    } else {
        finalRaag
    }
}

fun translateWriter(writer: String, lang: DisplayLanguage): String {
    var normalized = writer.trim()
    if (normalized.endsWith(" Ji", ignoreCase = true)) {
        normalized = normalized.substring(0, normalized.length - 3).trim()
    } else if (normalized.endsWith(" Jee", ignoreCase = true)) {
        normalized = normalized.substring(0, normalized.length - 4).trim()
    }
    
    val lower = normalized.lowercase()
    val cleanName = when {
        lower.contains("nanak") -> "Guru Nanak Dev"
        lower.contains("angad") -> "Guru Angad Dev"
        lower.contains("amar") -> "Guru Amar Das"
        lower.contains("ram") -> "Guru Ram Das"
        lower.contains("arjan") -> "Guru Arjan Dev"
        lower.contains("tegh") -> "Guru Tegh Bahadur"
        lower.contains("gobind") -> "Guru Gobind Singh"
        lower.contains("kabir") -> "Bhagat Kabir"
        lower.contains("ravidas") -> "Bhagat Ravidas"
        lower.contains("farid") -> "Sheikh Farid"
        lower.contains("namdev") -> "Bhagat Namdev"
        lower.contains("bhatt") -> normalized
        else -> normalized
    }

    val translated = Translations.translate(cleanName, lang)
    
    return when (lang) {
        DisplayLanguage.Punjabi -> {
            val suffix = " ਜੀ"
            if (translated.endsWith(suffix.trim())) translated else "$translated$suffix"
        }
        DisplayLanguage.Hindi -> {
            val suffix = " जी"
            if (translated.endsWith(suffix.trim())) translated else "$translated$suffix"
        }
        else -> {
            val suffix = " Ji"
            if (translated.endsWith(suffix.trim())) translated else "$translated$suffix"
        }
    }
}

object Translations {
    fun translate(key: String, language: DisplayLanguage): String {
        val translations = map[key] ?: return key
        return translations[language] ?: translations[DisplayLanguage.English] ?: key
    }

    private val map = mapOf(
        "Weekly Streak!" to mapOf(
            DisplayLanguage.Hindi to "साप्ताहिक निरंतरता!",
            DisplayLanguage.Punjabi to "ਹਫਤਾਵਾਰੀ ਲੜੀ!"
        ),
        "You've stayed consistent for" to mapOf(
            DisplayLanguage.Hindi to "आप निरंतर बने रहे हैं",
            DisplayLanguage.Punjabi to "ਤੁਸੀਂ ਲਗਾਤਾਰ ਪਾਠ ਕੀਤਾ ਹੈ"
        ),
        "Keep up the daily Nitnem to grow your streak and earn more XP." to mapOf(
            DisplayLanguage.Hindi to "अपनी निरंतरता बढ़ाने और अधिक XP अर्जित करने के लिए दैनिक नितनेम जारी रखें।",
            DisplayLanguage.Punjabi to "ਆਪਣੀ ਲੜੀ ਨੂੰ ਵਧਾਉਣ ਅਤੇ ਹੋਰ XP ਕਮਾਉਣ ਲਈ ਰੋਜ਼ਾਨਾ ਨਿਤਨੇਮ ਜਾਰੀ ਰੱਖੋ।"
        ),
        "Weekly Streak!" to mapOf(
            DisplayLanguage.Hindi to "साप्ताहिक निरंतरता!",
            DisplayLanguage.Punjabi to "ਹਫਤਾਵਾਰੀ ਲੜੀ!"
        ),
        "View Results" to mapOf(
            DisplayLanguage.Hindi to "परिणाम देखें",
            DisplayLanguage.Punjabi to "ਨਤੀਜਾ ਦੇਖੋ"
        ),
        "Score" to mapOf(
            DisplayLanguage.Hindi to "अंक",
            DisplayLanguage.Punjabi to "ਅੰਕ"
        ),
        "View Score" to mapOf(
            DisplayLanguage.Hindi to "अंक देखें",
            DisplayLanguage.Punjabi to "ਸਕੋਰ ਵੇਖੋ"
        ),
        "+%d XP" to mapOf(
            DisplayLanguage.Hindi to "+%d XP",
            DisplayLanguage.Punjabi to "+%d XP"
        ),
        "5 Day Streak" to mapOf(
            DisplayLanguage.Hindi to "5 दिनों की निरंतरता",
            DisplayLanguage.Punjabi to "5 ਦਿਨਾਂ ਦੀ ਲੜੀ"
        ),
        "Aarti" to mapOf(
            DisplayLanguage.Hindi to "आरती",
            DisplayLanguage.Punjabi to "ਆਰਤੀ"
        ),
        "Aarti About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Nanak Dev Ji and other Bhagats, describing the cosmic worship of the Creator by the sky, stars, wind, and forests.",
            DisplayLanguage.Hindi to "गुरु नानक देव जी और अन्य भक्तों द्वारा रचित, जो आकाश, तारों, हवा और जंगलों द्वारा सृष्टिकर्ता की ब्रह्मांडीय पूजा का वर्णन करती है।"
        ),
        "Aarti Significance" to mapOf(
            DisplayLanguage.English to "Removes localized idol worship and opens the eyes to the grand, eternal adoration of God taking place across the entire universe.",
            DisplayLanguage.Hindi to "स्थानीय मूर्ति पूजा को दूर करता है और पूरे ब्रह्मांड में हो रही ईश्वर की महान, शाश्वत आराधना के प्रति आंखें खोलता है।",
            DisplayLanguage.Punjabi to "ਇਹ ਬਾਣੀ ਪੱਥਰਾਂ ਦੀ ਆਰਤੀ ਕਰਨ ਦੀ ਬਜਾਏ, ਸਾਰੀ ਕਾਇਨਾਤ ਵਿੱਚ ਹੋ ਰਹੀ ਅਕਾਲ ਪੁਰਖ ਦੀ ਵਿਰਾਟ ਆਰਤੀ ਨੂੰ ਦੇਖਣ ਦੀ ਸੂਝ ਬਖ਼ਸ਼ਦੀ ਹੈ।"
        ),
        "About" to mapOf(
            DisplayLanguage.Hindi to "विवरण",
            DisplayLanguage.Punjabi to "ਬਾਰੇ"
        ),
        "Accent Theme" to mapOf(
            DisplayLanguage.Hindi to "एक्सेंट थीम",
            DisplayLanguage.Punjabi to "ਰੰਗ ਦੀ ਥੀਮ"
        ),
        "Adjust fonts, translations, transliterations, and themes for comfortable paath." to mapOf(
            DisplayLanguage.Hindi to "सुविधाजनक पाठ के लिए फ़ॉन्ट, अनुवाद, लिप्यंतरण और थीम समायोजित करें।",
            DisplayLanguage.Punjabi to "ਸੁਖਾਲੇ ਪਾਠ ਲਈ ਅੱਖਰ, ਅਨੁਵਾਦ, ਲਿਪੀਅੰਤਰਨ ਅਤੇ ਥੀਮ ਬਦਲੋ।"
        ),
        "Afternoon" to mapOf(
            DisplayLanguage.Hindi to "दोपहर",
            DisplayLanguage.Punjabi to "ਦੁਪਹਿਰ"
        ),
        "Afternoon Reflection" to mapOf(
            DisplayLanguage.Hindi to "दोपहर का विचार",
            DisplayLanguage.Punjabi to "ਦੁਪਹਿਰ ਦਾ ਵਿਚਾਰ"
        ),
        "AI Gurbani Quiz" to mapOf(
            DisplayLanguage.Hindi to "AI गुरबानी क्विज",
            DisplayLanguage.Punjabi to "AI ਗੁਰਬਾਣੀ ਕਵਿਜ਼"
        ),
        "AI Quiz" to mapOf(
            DisplayLanguage.Hindi to "AI क्विज",
            DisplayLanguage.Punjabi to "AI ਕਵਿਜ਼"
        ),
        "Naam Simran • Breath • Stillness" to mapOf(
            DisplayLanguage.Hindi to "नाम सिमरन • श्वास • शांति",
            DisplayLanguage.Punjabi to "ਨਾਮ ਸਿਮਰਨ • ਸਾਹ • ਸ਼ਾਂਤੀ"
        ),
     "Practice Gurbani Through Interactive Questions" to mapOf(
    DisplayLanguage.Hindi to "इंटरैक्टिव प्रश्नों के माध्यम से गुरबाणी का अभ्यास करें",
    DisplayLanguage.Punjabi to "ਇੰਟਰਐਕਟਿਵ ਪ੍ਰਸ਼ਨਾਂ ਰਾਹੀਂ ਗੁਰਬਾਣੀ ਦਾ ਅਭਿਆਸ ਕਰੋ"
),
"Audio Guidance" to mapOf(
    DisplayLanguage.Hindi to "ऑडियो मार्गदर्शन",
    DisplayLanguage.Punjabi to "ਆਡੀਓ ਮਾਰਗਦਰਸ਼ਨ"
),
"Voice guidance is on" to mapOf(
    DisplayLanguage.Hindi to "आवाज़ मार्गदर्शन चालू है",
    DisplayLanguage.Punjabi to "ਆਵਾਜ਼ ਮਾਰਗਦਰਸ਼ਨ ਚਾਲੂ ਹੈ"
),
"Guidance muted" to mapOf(
    DisplayLanguage.Hindi to "गाइडेंस म्यूट हो गया",
    DisplayLanguage.Punjabi to "ਗਾਈਡੈਂਸ ਮਿਊਟ ਹੋ ਗਿਆ"
),
"Saas Saas" to mapOf(
    DisplayLanguage.Hindi to "साँस साँस",
    DisplayLanguage.Punjabi to "ਸਾਸ ਸਾਸ"
),
"Breath Focus" to mapOf(
    DisplayLanguage.Hindi to "श्वास ध्यान",
    DisplayLanguage.Punjabi to "ਸਾਹ ਧਿਆਨ"
),
"Inhale 'Wahe' mentally and exhale 'Guru'. Synchronize awareness with each breath cycle." to mapOf(
    DisplayLanguage.Hindi to "श्वास लेते समय 'वाहे' और छोड़ते समय 'गुरु' का मानसिक जाप करें। प्रत्येक श्वास चक्र के साथ जागरूकता को समक्रमित करें।",
    DisplayLanguage.Punjabi to "ਸਾਹ ਲੈਂਦਿਆਂ 'ਵਾਹਿ' ਅਤੇ ਛੱਡਦਿਆਂ 'ਗੁਰੂ' ਦਾ ਮਾਨਸਿਕ ਜਾਪ ਕਰੋ। ਹਰ ਸਾਹ ਚੱਕਰ ਦੇ ਨਾਲ ਜਾਗਰੂਕਤਾ ਨੂੰ ਸਮਕਾਲੀ ਕਰੋ।"
),

"Naam Listening" to mapOf(
    DisplayLanguage.Hindi to "नाम सुनना",
    DisplayLanguage.Punjabi to "ਨਾਮ ਸੁਣਨਾ"
),
"Recite Waheguru gently and listen deeply to the internal resonance of the Shabad." to mapOf(
    DisplayLanguage.Hindi to "श्वास लेते समय 'वाहे' और छोड़ते समय 'गुरु' का मानसिक जाप करें। प्रत्येक श्वास चक्र के साथ जागरूकता को समक्रमित करें।",
    DisplayLanguage.Punjabi to "ਸਾਹ ਲੈਂਦਿਆਂ 'ਵਾਹਿ' ਅਤੇ ਛੱਡਦਿਆਂ 'ਗੁਰੂ' ਦਾ ਮਾਨਸਿਕ ਜਾਪ ਕਰੋ। ਹਰ ਸਾਹ ਚੱਕਰ ਦੇ ਨਾਲ ਜਾਗਰੂਕਤਾ ਨੂੰ ਸਮਕਾਲੀ ਕਰੋ।"
),
"MoonMantar" to mapOf(
    DisplayLanguage.Hindi to "मूल मन्त्र",
    DisplayLanguage.Punjabi to "ਮੂਲ ਮੰਤਰ"
),
"Stillness" to mapOf(
    DisplayLanguage.Hindi to "शांति",
    DisplayLanguage.Punjabi to "ਸ਼ਾਂਤੀ"
),
"Remain internally silent and observe the subtle vibration of Naam within." to mapOf(
    DisplayLanguage.Hindi to "श्वास लेते समय 'वाहे' और छोड़ते समय 'गुरु' का मानसिक जाप करें। प्रत्येक श्वास चक्र के साथ जागरूकता को समक्रमित करें।",
    DisplayLanguage.Punjabi to "ਸਾਹ ਲੈਂਦਿਆਂ 'ਵਾਹਿ' ਅਤੇ ਛੱਡਦਿਆਂ 'ਗੁਰੂ' ਦਾ ਮਾਨਸਿਕ ਜਾਪ ਕਰੋ। ਹਰ ਸਾਹ ਚੱਕਰ ਦੇ ਨਾਲ ਜਾਗਰੂਕਤਾ ਨੂੰ ਸਮਕਾਲੀ ਕਰੋ।"
),
"Mala Counter" to mapOf(
    DisplayLanguage.Hindi to "माला काउंटर",
    DisplayLanguage.Punjabi to "ਮਾਲਾ ਕਾਊਂਟਰ"
),
"Naam Jaap" to mapOf(
    DisplayLanguage.Hindi to "नाम जाप",
    DisplayLanguage.Punjabi to "ਨਾਮ ਜਾਪ"
),
"Tap the orb once for each Waheguru repetition. Complete a full Mala of 108 repetitions while maintaining focus on the Naam rather than the count." to mapOf(
    DisplayLanguage.Hindi to "प्रत्येक 'वाहेगुरु' दोहराव के लिए एक बार गोलाकार को टैप करें। गिनती के बजाय नाम पर ध्यान केंद्रित करते हुए 108 दोहराव का एक पूर्ण माला पूरा करें।",
    DisplayLanguage.Punjabi to "ਹਰ 'ਵਾਹਿਗੁਰੂ' ਦੁਹਰਾਓ ਲਈ ਇੱਕ ਵਾਰ ਗੋਲਾਕਾਰ ਨੂੰ ਟੈਪ ਕਰੋ। ਗਿਣਤੀ ਦੇ ਬਜਾਏ ਨਾਮ 'ਤੇ ਧਿਆਨ ਕੇਂਦਰਿਤ ਕਰਦਿਆਂ 108 ਦੁਹਰਾਓ ਦਾ ਇੱਕ ਪੂਰਾ ਮਾਲਾ ਪੂਰਾ ਕਰੋ।"
),
 
        "Meditate" to mapOf(
            DisplayLanguage.Hindi to "ध्यान",
            DisplayLanguage.Punjabi to "ਧਿਆਨ"
        ),
        "All Gurbani texts & translations, transliterations are taken from https://www.shabados.com/" to mapOf(
            DisplayLanguage.Hindi to "सभी गुरबानी पाठ, अनुवाद और लिप्यंतरण https://www.shabados.com/ से लिए गए हैं",
            DisplayLanguage.Punjabi to "ਸਾਰੇ ਗੁਰਬਾਣੀ ਪਾਠ, ਅਨੁਵਾਦ ਅਤੇ ਲਿਪੀਅੰਤਰਨ https://www.shabados.com/ ਤੋਂ ਲਏ ਗਏ ਹਨ"
        ),
        "Allowed: 2 AM - 8 AM" to mapOf(
            DisplayLanguage.Hindi to "अनुमति: सुबह 2 - 8 बजे",
            DisplayLanguage.Punjabi to "ਮਨਜ਼ੂਰ ਸਮਾਂ: ਸਵੇਰੇ 2 ਤੋਂ 8 ਵਜੇ"
        ),
        "Allowed: 4 PM - 9 PM" to mapOf(
            DisplayLanguage.Hindi to "अनुमति: शाम 4 - रात 9 बजे",
            DisplayLanguage.Punjabi to "ਮਨਜ਼ੂਰ ਸਮਾਂ: ਸ਼ਾਮ 4 ਤੋਂ ਰਾਤ 9 ਵਜੇ"
        ),
        "Allowed: 8 PM - 2 AM" to mapOf(
            DisplayLanguage.Hindi to "अनुमति: रात 8 - सुबह 2 बजे",
            DisplayLanguage.Punjabi to "ਮਨਜ਼ੂਰ ਸਮਾਂ: ਰਾਤ 8 ਤੋਂ ਸਵੇਰੇ 2 ਵਜੇ"
        ),
        "Ambient Mode" to mapOf(
            DisplayLanguage.Hindi to "एम्बिएंट मोड",
            DisplayLanguage.Punjabi to "ਐਂਬੀਐਂਟ ਮੋਡ"
        ),
        "AMOLED Black" to mapOf(
            DisplayLanguage.Hindi to "अमोलेड ब्लैक",
            DisplayLanguage.Punjabi to "AMOLED ਬਲੈਕ"
        ),
        "Amrit Vela" to mapOf(
            DisplayLanguage.Hindi to "अमृत वेला",
            DisplayLanguage.Punjabi to "ਅੰਮ੍ਰਿਤ ਵੇਲਾ"
        ),
        "Amrit Vela should be between 2 AM and 8 AM" to mapOf(
            DisplayLanguage.Hindi to "अमृत वेला सुबह 2 से 8 बजे के बीच होना चाहिए",
            DisplayLanguage.Punjabi to "ਅੰਮ੍ਰਿਤ ਵੇਲਾ ਸਵੇਰੇ 2 ਤੋਂ 8 ਵਜੇ ਦੇ ਵਿਚਕਾਰ ਹੋਣਾ ਚਾਹੀਦਾ ਹੈ"
        ),
        "Anand Sahib" to mapOf(
            DisplayLanguage.Hindi to "आनंद साहिब",
            DisplayLanguage.Punjabi to "ਅਨੰਦੁ ਸਾਹਿਬ"
        ),
        "Anand Sahib About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Amar Das Ji, the third Guru, in Ramkali Raag, celebrating the state of pure spiritual ecstasy.",
            DisplayLanguage.Hindi to "तीसरे गुरु, गुरु अमरदास जी द्वारा रामकली राग में रचित, जो शुद्ध आध्यात्मिक आनंद की स्थिति का उत्सव मनाती है।",
            DisplayLanguage.Punjabi to "ਤੀਜੇ ਪਾਤਸ਼ਾਹ ਸ੍ਰੀ ਗੁਰੂ ਅਮਰਦਾਸ ਜੀ ਦੁਆਰਾ ਰਾਮਕਲੀ ਰਾਗ ਵਿੱਚ ਉਚਾਰਨ ਕੀਤੀ ਗਈ, ਜੋ ਆਤਮਿਕ ਅਨੰਦ ਦੀ ਅਵਸਥਾ ਨੂੰ ਬਿਆਨ ਕਰਦੀ ਹੈ।"
        ),
        "Anand Sahib Significance" to mapOf(
            DisplayLanguage.English to "Recited in the morning and evening. It guides the devotee to internal bliss, detachment, and contentment.",
            DisplayLanguage.Hindi to "सुबह और शाम को पाठ किया जाता है। यह भक्त को आंतरिक आनंद, वैराग्य और संतोष की ओर ले जाता है।",
            DisplayLanguage.Punjabi to "ਹਰ ਧਾਰਮਿਕ ਕਾਰਜ ਦੇ ਅੰਤ ਵਿੱਚ ਅਤੇ ਸਵੇਰ/ਸੁਖਮਨੀ ਸਾਹਿਬ ਦੇ ਪਾਠ ਦੇ ਭੋਗ ਵੇਲੇ ਪੜ੍ਹੀ ਜਾਂਦੀ ਹੈ। ਇਹ ਮਨ ਨੂੰ ਅੰਦਰੂਨੀ ਖੇੜਾ, ਸੰਤੋਖ ਅਤੇ ਸਹਿਜ ਅਵਸਥਾ ਬਖ਼ਸ਼ਦੀ ਹੈ।"
        ),
        "Ang" to mapOf(
            DisplayLanguage.Hindi to "अंग",
            DisplayLanguage.Punjabi to "ਅੰਗ"
        ),
        "Ang 1" to mapOf(
            DisplayLanguage.Hindi to "अंग 1",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧"
        ),
        "Ang 1193" to mapOf(
            DisplayLanguage.Hindi to "अंग 1193",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧੧੯੩"
        ),
        "Ang 12" to mapOf(
            DisplayLanguage.Hindi to "अंग 12",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧੨"
        ),
        "Ang 133" to mapOf(
            DisplayLanguage.Hindi to "अंग 133",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧੩੩"
        ),
        "Ang 1426" to mapOf(
            DisplayLanguage.Hindi to "अंग 1426",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧੪੨੬"
        ),
        "Ang 1429" to mapOf(
            DisplayLanguage.Hindi to "अंग 1429",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧੪੨੯"
        ),
        "Ang 262" to mapOf(
            DisplayLanguage.Hindi to "अंग 262",
            DisplayLanguage.Punjabi to "ਅੰਗ ੨੬੨"
        ),
        "Ang 462" to mapOf(
            DisplayLanguage.Hindi to "अंग 462",
            DisplayLanguage.Punjabi to "ਅੰਗ ੪੬੨"
        ),
        "Ang 663" to mapOf(
            DisplayLanguage.Hindi to "अंग 663",
            DisplayLanguage.Punjabi to "ਅੰਗ ੬੬੩"
        ),
        "Ang 8" to mapOf(
            DisplayLanguage.Hindi to "अंग 8",
            DisplayLanguage.Punjabi to "ਅੰਗ ੮"
        ),
        "Ang 917" to mapOf(
            DisplayLanguage.Hindi to "अंग 917",
            DisplayLanguage.Punjabi to "ਅੰਗ ੯੧੭"
        ),
        "Ang 929" to mapOf(
            DisplayLanguage.Hindi to "अंग 929",
            DisplayLanguage.Punjabi to "ਅੰਗ ੯੨੯"
        ),
        "Ang 938" to mapOf(
            DisplayLanguage.Hindi to "अंग 938",
            DisplayLanguage.Punjabi to "ਅੰਗ ੯੩੮"
        ),
        "Ang 947" to mapOf(
            DisplayLanguage.Hindi to "अंग 947",
            DisplayLanguage.Punjabi to "ਅੰਗ ੯੪੭"
        ),
        "Ang 96" to mapOf(
            DisplayLanguage.Hindi to "अंग 96",
            DisplayLanguage.Punjabi to "ਅੰਗ ੯੬"
        ),
        "App Theme" to mapOf(
            DisplayLanguage.Hindi to "앱 थीम",
            DisplayLanguage.Punjabi to "ਐਪ ਥੀਮ"
        ),
        "Arrange Gurbani Line" to mapOf(
            DisplayLanguage.Hindi to "गुरबाणी पंक्ति को सही क्रम में व्यवस्थित करें",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਤੁਕ ਨੂੰ ਸਹੀ ਕ੍ਰਮ ਵਿੱਚ ਲਾਓ"
        ),
  
        "Artist Name" to mapOf(
            DisplayLanguage.Hindi to "कलाकार का नाम",
            DisplayLanguage.Punjabi to "ਕਲਾਕਾਰ ਦਾ ਨਾਮ"
        ),
        "Artist Name cannot be empty" to mapOf(
            DisplayLanguage.Hindi to "कलाकार का नाम खाली नहीं हो सकता",
            DisplayLanguage.Punjabi to "ਕਲਾਕਾਰ ਦਾ ਨਾਮ ਖਾਲੀ ਨਹੀਂ ਹੋ ਸਕਦਾ"
        ),
        "Asa" to mapOf(
            DisplayLanguage.Hindi to "आसा",
            DisplayLanguage.Punjabi to "ਆਸਾ"
        ),

        "Asa Di Vaar" to mapOf(
            DisplayLanguage.Hindi to "आसा दी वार",
            DisplayLanguage.Punjabi to "ਆਸਾ ਕੀ ਵਾਰ"
        ),
        "Asa Di Vaar About" to mapOf(
            DisplayLanguage.English to "A heroic ballad written by Guru Nanak Dev Ji, sung in the morning in Raag Asa, with Saloks added by Guru Angad Dev Ji.",
            DisplayLanguage.Hindi to "गुरु नानक देव जी द्वारा रचित एक वीर गाथा, जिसे सुबह राग आसा में गाया जाता है, जिसमें गुरु अंगद देव जी द्वारा सलोक जोड़े गए हैं।",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਦੁਆਰਾ ਉਚਾਰਨ ਕੀਤੀ ਗਈ ਵਾਰ, ਜੋ ਅੰਮ੍ਰਿਤ ਵੇਲੇ ਆਸਾ ਰਾਗ ਵਿੱਚ ਗਾਈ ਜਾਂਦੀ ਹੈ, ਅਤੇ ਇਸ ਵਿੱਚ ਗੁਰੂ ਅੰਗਦ ਦੇਵ ਜੀ ਦੇ ਸਲੋਕ ਵੀ ਸ਼ਾਮਿਲ ਹਨ।"
        ),

        "Asa Di Vaar Significance" to mapOf(
            DisplayLanguage.English to "Exposes social hypocrisy, ritualism, and ego. It inspires selfless service, humility, and moral integrity.",
            DisplayLanguage.Hindi to "सामाजिक पाखंड, कर्मकांड और अहंकार को उजागर करता है। यह निस्वार्थ सेवा, विनम्रता और नैतिक अखंडता को प्रेरित करता है।",
            DisplayLanguage.Punjabi to "ਇਹ ਬਾਣੀ ਸਮਾਜਿਕ ਪਾਖੰਡ, ਕਰਮਕਾਂਡ ਅਤੇ ਹਉਮੈ ਦਾ ਖੰਡਨ ਕਰਦੀ ਹੈ। ਇਹ ਮਨੁੱਖ ਨੂੰ ਨਿਮਰਤਾ, ਨਿਸ਼ਕਾਮ ਸੇਵਾ ਅਤੇ ਉੱਚੇ-ਸੁੱਚੇ ਇਖ਼ਲਾਕ ਵਾਲਾ ਜੀਵਨ ਜਿਊਣ ਲਈ ਪ੍ਰੇਰਦੀ ਹੈ।"
        ),
                "Basant Ki Vaar" to mapOf(
    DisplayLanguage.English to "Basant Ki Vaar",

    DisplayLanguage.Hindi to "बसंत की वार",

    DisplayLanguage.Punjabi to "ਬਸੰਤ ਕੀ ਵਾਰ"
),
        "Basant Ki Vaar About" to mapOf(
    DisplayLanguage.English to "Basant Ki Vaar is a composition of Guru Arjan Dev Ji recorded in Sri Guru Granth Sahib Ji under Raag Basant. Through poetic verses and saloks, it reflects upon the fleeting nature of worldly attachments, the importance of humility, and the transformative power of devotion to the Divine. The Bani uses the symbolism of spring (Basant) to illustrate spiritual renewal and the blossoming of divine awareness within the human heart.",

    DisplayLanguage.Hindi to "बसंत की वार गुरु अर्जन देव जी द्वारा रचित एक बाणी है जो श्री गुरु ग्रंथ साहिब जी में राग बसंत के अंतर्गत दर्ज है। अपने पदों और सलोकों के माध्यम से यह संसारिक मोह-माया की क्षणभंगुरता, विनम्रता के महत्व और परमात्मा की भक्ति की परिवर्तनकारी शक्ति पर प्रकाश डालती है। यह बाणी बसंत ऋतु के प्रतीक के माध्यम से आत्मिक नवजीवन और हृदय में दिव्य चेतना के प्रस्फुटन को दर्शाती है।",

    DisplayLanguage.Punjabi to "ਬਸੰਤ ਕੀ ਵਾਰ ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ ਦੁਆਰਾ ਰਚਿਤ ਬਾਣੀ ਹੈ ਜੋ ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ ਵਿੱਚ ਰਾਗ ਬਸੰਤ ਅਧੀਨ ਦਰਜ ਹੈ। ਇਸ ਦੇ ਪਉੜੀਆਂ ਅਤੇ ਸਲੋਕ ਸੰਸਾਰਕ ਮੋਹ-ਮਾਇਆ ਦੀ ਨਾਸਵੰਤਤਾ, ਨਿਮਰਤਾ ਦੇ ਮਹੱਤਵ ਅਤੇ ਪਰਮਾਤਮਾ ਦੀ ਭਗਤੀ ਦੀ ਰੂਪਾਂਤਰਕ ਸ਼ਕਤੀ ਉੱਤੇ ਚਾਨਣ ਪਾਂਦੇ ਹਨ। ਇਹ ਬਾਣੀ ਬਸੰਤ ਰੁੱਤ ਦੇ ਪ੍ਰਤੀਕ ਰਾਹੀਂ ਆਤਮਕ ਨਵੀਂਕਰਨ ਅਤੇ ਮਨ ਅੰਦਰ ਦਿਵਿਆ ਚੇਤਨਾ ਦੇ ਖਿੜਨ ਨੂੰ ਦਰਸਾਉਂਦੀ ਹੈ।"
),
"Basant Ki Vaar Significance" to mapOf(
    DisplayLanguage.English to "Recited to inspire spiritual renewal and detachment from worldly distractions, Basant Ki Vaar encourages the seeker to cultivate humility, remembrance of the Divine, and inner purity. Its teachings remind Sikhs that just as spring brings new life to nature, devotion to Waheguru brings spiritual awakening and lasting joy. The Bani motivates one to overcome ego and align life with the Guru's wisdom.",

    DisplayLanguage.Hindi to "आध्यात्मिक नवजीवन और संसारिक विकर्षणों से विरक्ति की प्रेरणा देने के लिए पाठ की जाने वाली बसंत की वार साधक को विनम्रता, प्रभु-स्मरण और आंतरिक पवित्रता विकसित करने के लिए प्रेरित करती है। इसकी शिक्षाएँ स्मरण कराती हैं कि जैसे बसंत प्रकृति में नया जीवन लाता है, वैसे ही वाहेगुरु की भक्ति आत्मिक जागृति और स्थायी आनंद प्रदान करती है। यह बाणी अहंकार पर विजय प्राप्त कर गुरु की बुद्धि के अनुसार जीवन जीने की प्रेरणा देती है।",

    DisplayLanguage.Punjabi to "ਆਤਮਕ ਨਵੀਂਕਰਨ ਅਤੇ ਸੰਸਾਰਕ ਭਟਕਾਵਾਂ ਤੋਂ ਉਪਰ ਉੱਠਣ ਦੀ ਪ੍ਰੇਰਣਾ ਦੇਣ ਲਈ ਪਾਠ ਕੀਤੀ ਜਾਣ ਵਾਲੀ ਬਸੰਤ ਕੀ ਵਾਰ ਸਾਧਕ ਨੂੰ ਨਿਮਰਤਾ, ਪ੍ਰਭੂ-ਸਿਮਰਨ ਅਤੇ ਅੰਦਰੂਨੀ ਪਵਿਤ੍ਰਤਾ ਵਿਕਸਿਤ ਕਰਨ ਲਈ ਉਤਸ਼ਾਹਿਤ ਕਰਦੀ ਹੈ। ਇਸ ਦੀਆਂ ਸਿੱਖਿਆਵਾਂ ਯਾਦ ਦਿਵਾਉਂਦੀਆਂ ਹਨ ਕਿ ਜਿਵੇਂ ਬਸੰਤ ਰੁੱਤ ਕੁਦਰਤ ਵਿੱਚ ਨਵਾਂ ਜੀਵਨ ਲਿਆਉਂਦੀ ਹੈ, ਤਿਵੇਂ ਵਾਹਿਗੁਰੂ ਦੀ ਭਗਤੀ ਆਤਮਕ ਜਾਗਰਤੀ ਅਤੇ ਸਦੀਵੀ ਆਨੰਦ ਪ੍ਰਦਾਨ ਕਰਦੀ ਹੈ। ਇਹ ਬਾਣੀ ਮਨੁੱਖ ਨੂੰ ਹਉਮੈ ਉੱਤੇ ਜਿੱਤ ਪ੍ਰਾਪਤ ਕਰਕੇ ਗੁਰੂ ਦੀ ਮਤ ਅਨੁਸਾਰ ਜੀਵਨ ਜੀਊਣ ਲਈ ਪ੍ਰੇਰਿਤ ਕਰਦੀ ਹੈ।"
),
        "Audio" to mapOf(
            DisplayLanguage.Hindi to "ऑडियो",
            DisplayLanguage.Punjabi to "ਆਡੀਓ"
        ),
        "Vocal guidance is on" to mapOf(
            DisplayLanguage.Hindi to "वोकल गाइडेंस चालू है",
            DisplayLanguage.Punjabi to "ਵੋਕਲ ਗਾਈਡੈਂਸ ਚਾਲੂ ਹੈ"
        ),
        "Audio Muted" to mapOf(
            DisplayLanguage.Hindi to "ऑडियो म्यूट किया गया",
            DisplayLanguage.Punjabi to "ਆਡੀਓ ਮਿਊਟ ਕੀਤਾ ਗਿਆ"
        ),
        "Audio Track" to mapOf(
            DisplayLanguage.Hindi to "ऑडियो ट्रैक",
            DisplayLanguage.Punjabi to "ਆਡੀਓ ਟਰੈਕ"
        ),
        "Author" to mapOf(
            DisplayLanguage.Hindi to "लेखक",
            DisplayLanguage.Punjabi to "ਲੇਖਕ"
        ),
        "Auto Scroll" to mapOf(
            DisplayLanguage.Hindi to "ऑटो स्क्रॉल",
            DisplayLanguage.Punjabi to "ਆਟੋ ਸਕ੍ਰੋਲ"
        ),
        "Auto Scroll Speed" to mapOf(
            DisplayLanguage.Hindi to "ऑटो स्क्रॉल गति",
            DisplayLanguage.Punjabi to "ਆਟੋ ਸਕ੍ਰੋਲ ਦੀ ਗਤੀ"
        ),
        "Auto Scroll Started" to mapOf(
            DisplayLanguage.Hindi to "ऑटो स्क्रॉल शुरू",
            DisplayLanguage.Punjabi to "ਆਟੋ ਸਕ੍ਰੋਲ ਸ਼ੁਰੂ"
        ),
        "Balanced contrast" to mapOf(
            DisplayLanguage.Hindi to "संतुलित कंट्रास्ट",
            DisplayLanguage.Punjabi to "ਸੰਤੁਲਿਤ ਕੰਟਰਾਸਟ"
        ),
        "Bani" to mapOf(
            DisplayLanguage.Hindi to "बाणी",
            DisplayLanguage.Punjabi to "ਬਾਣੀ"
        ),
        "Bani Information" to mapOf(
            DisplayLanguage.Hindi to "बाणी जानकारी",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਦੀ ਜਾਣਕਾਰੀ"
        ),
        "Chaupai" to mapOf(
            DisplayLanguage.Hindi to "चौपई",
            DisplayLanguage.Punjabi to "ਚੌਪਈ"
        ),
        "Japji" to mapOf(
            DisplayLanguage.Hindi to "जपुजी",
            DisplayLanguage.Punjabi to "ਜਪੁਜੀ"
        ),
        "Jaap" to mapOf(
            DisplayLanguage.Hindi to "जाप",
            DisplayLanguage.Punjabi to "ਜਾਪ"
        ),
        "Anand" to mapOf(
            DisplayLanguage.Hindi to "आनंद",
            DisplayLanguage.Punjabi to "ਆਨੰਦ"
        ),
        "Rehras" to mapOf(
            DisplayLanguage.Hindi to "रेहरास",
            DisplayLanguage.Punjabi to "ਰੇਹਰਾਸ"
        ),
        "Sohila" to mapOf(
            DisplayLanguage.Hindi to "सोहिला",
            DisplayLanguage.Punjabi to "ਸੋਹਿਲਾ"
        ),
        "Benti Chaupai Sahib" to mapOf(
            DisplayLanguage.Hindi to "बेनती चौपई साहिब",
            DisplayLanguage.Punjabi to "ਬੇਨਤੀ ਚੌਪਈ ਸਾਹਿਬ"
        ),
        "Benti Chaupai Sahib About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Gobind Singh Ji, this prayer is a humble request for divine protection, peace, and spiritual strength.",
            DisplayLanguage.Hindi to "गुरु गोबिंद सिंह जी द्वारा रचित, यह प्रार्थना ईश्वरीय सुरक्षा, शांति और आध्यात्मिक शक्ति के लिए एक विनम्र अनुरोध है।",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਗੋਬਿੰਦ ਸਿੰਘ ਜੀ ਦੁਆਰਾ ਉਚਾਰਨ ਕੀਤੀ ਗਈ, ਇਹ ਪ੍ਰਾਰਥਨਾ ਅਕਾਲ ਪੁਰਖ ਅੱਗੇ ਰੱਖਿਆ, ਸ਼ਾਂਤੀ ਅਤੇ ਆਤਮਿਕ ਬਲ ਲਈ ਜੋਦੜੀ ਹੈ।"
        ),
        "Benti Chaupai Sahib Significance" to mapOf(
            DisplayLanguage.English to "Recited in the morning and evening. It serves as a shield of defense against negative energies, anxiety, and internal/external obstacles.",
            DisplayLanguage.Hindi to "सुबह और शाम को पाठ किया जाता है। यह नकारात्मक ऊर्जाओं, चिंता और आंतरिक/बाहरी बाधाओं के खिलाफ एक रक्षा कवच के रूप में कार्य करता है।",
            DisplayLanguage.Punjabi to "ਸਵੇਰ ਅਤੇ ਸ਼ਾਮ ਵੇਲੇ ਪੜ੍ਹੀ ਜਾਂਦੀ ਹੈ। ਇਹ ਮਨੁੱਖੀ ਮਨ ਨੂੰ ਨਕਾਰਾਤਮਕ ਊਰਜਾ, ਡਰ ਅਤੇ ਅੰਦਰੂਨੀ/ਬਾਹਰੀ ਵਿਕਾਰਾਂ ਤੋਂ ਬਚਾਉਣ ਲਈ ਇੱਕ ਢਾਲ ਦਾ ਕੰਮ ਕਰਦੀ ਹੈ।"
        ),
        "Bhagat Kabir" to mapOf(
            DisplayLanguage.Hindi to "भगत कबीर",
            DisplayLanguage.Punjabi to "ਭਗਤ ਕਬੀਰ"
        ),
        "Bhagat Kabir Ji" to mapOf(
            DisplayLanguage.Hindi to "भगत कबीर जी",
            DisplayLanguage.Punjabi to "ਭਗਤ ਕਬੀਰ ਜੀ"
        ),
        "Bhagat Namdev" to mapOf(
            DisplayLanguage.Hindi to "भगत नामदेव",
            DisplayLanguage.Punjabi to "ਭਗਤ ਨਾਮਦੇਵ"
        ),
        "Bhagat Namdev Ji" to mapOf(
            DisplayLanguage.Hindi to "भगत नामदेव जी",
            DisplayLanguage.Punjabi to "ਭਗਤ ਨਾਮਦੇਵ ਜੀ"
        ),
        "Bhagat Ravidas" to mapOf(
            DisplayLanguage.Hindi to "भगत रविदास",
            DisplayLanguage.Punjabi to "ਭਗਤ ਰਵਿਦਾਸ"
        ),
        "Bhagat Ravidas Ji" to mapOf(
            DisplayLanguage.Hindi to "भगत रविदास जी",
            DisplayLanguage.Punjabi to "ਭਗਤ ਰਵਿਦਾਸ ਜੀ"
        ),
        "Bold" to mapOf(
            DisplayLanguage.Hindi to "गहरे अक्षर",
            DisplayLanguage.Punjabi to "ਗੂੜ੍ਹੇ ਅੱਖਰ"
        ),
        "Cancel" to mapOf(
            DisplayLanguage.Hindi to "रद्द करें",
            DisplayLanguage.Punjabi to "ਰੱਦ ਕਰੋ"
        ),
        "Center" to mapOf(
            DisplayLanguage.Hindi to "मध्य संरेखित",
            DisplayLanguage.Punjabi to "ਵਿਚਕਾਰ ਸੰਰੇਖਿਤ"
        ),
        "Choose the Gurbani and recitation track you want to synchronize." to mapOf(
            DisplayLanguage.Hindi to "उस गुरबाणी और पाठ ट्रैक का चयन करें जिसे आप सिंक करना चाहते हैं।",
            DisplayLanguage.Punjabi to "ਉਹ ਗੁਰਬਾਣੀ ਅਤੇ ਪਾਠ ਟਰੈਕ ਚੁਣੋ ਜਿਸਨੂੰ ਤੁਸੀਂ ਸਿੰਕ ਕਰਨਾ ਚਾਹੁੰਦੇ ਹੋ।"
        ),
        "Classic Gold" to mapOf(
            DisplayLanguage.Hindi to "क्लासिक गोल्ड",
            DisplayLanguage.Punjabi to "ਕਲਾਸਿਕ ਗੋਲਡ"
        ),
        "Compact" to mapOf(
            DisplayLanguage.Hindi to "सघन मोड",
            DisplayLanguage.Punjabi to "ਸੰਘਣਾ ਮੋਡ"
        ),
        "Completed %d minutes of Simran!\nEarned +%d XP" to mapOf(
            DisplayLanguage.Hindi to "%d मिनट सिमरन पूरा किया!\n+%d XP अर्जित किए",
            DisplayLanguage.Punjabi to "%d ਮਿੰਟ ਸਿਮਰਨ ਪੂਰਾ ਕੀਤਾ!\n+%d XP ਪ੍ਰਾਪਤ ਕੀਤੇ"
        ),
        "Completed a full Mala of %d repetitions!\nEarned +5 XP" to mapOf(
            DisplayLanguage.Hindi to "%d जप की एक पूरी माला पूरी की!\n+5 XP अर्जित किए",
            DisplayLanguage.Punjabi to "%d ਜਪ ਦੀ ਇੱਕ ਮਾਲਾ ਪੂਰੀ ਕੀਤੀ!\n+5 XP ਪ੍ਰਾਪਤ ਕੀਤੇ"
        ),
        "Completed Malas" to mapOf(
            DisplayLanguage.Hindi to "पूरी की गई मालाएं",
            DisplayLanguage.Punjabi to "ਪੂਰੀਆਂ ਹੋਈਆਂ ਮਾਲਾਵਾਂ"
        ),
        "Configure your homescreen widget to fit your daily style." to mapOf(
            DisplayLanguage.Hindi to "अपनी दैनिक शैली के अनुसार होमस्क्रीन विजेट को कॉन्फ़िगर करें।",
            DisplayLanguage.Punjabi to "ਆਪਣੀ ਰੋਜ਼ਾਨਾ ਸ਼ੈਲੀ ਦੇ ਅਨੁਸਾਰ ਹੋਮਸਕ੍ਰੀਨ ਵਿਜੇਟ ਨੂੰ ਸੰਰਚਿਤ ਕਰੋ।"
        ),
        "Consistency" to mapOf(
            DisplayLanguage.Hindi to "निरंतरता",
            DisplayLanguage.Punjabi to "ਲਗਾਤਾਰਤਾ"
        ),
        "Continue" to mapOf(
            DisplayLanguage.Hindi to "जारी रखें",
            DisplayLanguage.Punjabi to "ਜਾਰੀ ਰੱਖੋ"
        ),
        "Correct" to mapOf(
            DisplayLanguage.Hindi to "सही",
            DisplayLanguage.Punjabi to "ਸਹੀ"
        ),
        "A: " to mapOf(
            DisplayLanguage.Hindi to "अ: ",
            DisplayLanguage.Punjabi to "ਅ:"
        ),
        "Credits & Attribution" to mapOf(
            DisplayLanguage.Hindi to "क्रेडिट और आभार",
            DisplayLanguage.Punjabi to "ਕ੍ਰੈਡਿਟ ਅਤੇ ਸਰੋਤ ਸਿਹਰਾ"
        ),
        "Custom reminder schedule" to mapOf(
            DisplayLanguage.Hindi to "कस्टम अनुस्मारक अनुसूची",
            DisplayLanguage.Punjabi to "ਨੋਟੀਫਿਕੇਸ਼ਨ ਦਾ ਸਮਾਂ"
        ),
        "Daily Hukamnama" to mapOf(
            DisplayLanguage.Hindi to "रोज़ का हुकमनामा",
            DisplayLanguage.Punjabi to "ਰੋਜ਼ ਦਾ ਹੁਕਮਨਾਮਾ"
        ),
        "Daily Progress" to mapOf(
            DisplayLanguage.Hindi to "दैनिक प्रगति",
            DisplayLanguage.Punjabi to "ਰੋਜ਼ਾਨਾ ਪ੍ਰਗਤੀ"
        ),
        "Daily Streak" to mapOf(
            DisplayLanguage.Hindi to "दैनिक निरंतरता",
            DisplayLanguage.Punjabi to "ਰੋਜ਼ਾਨਾ ਲੜੀ"
        ),
        "Dark Mode" to mapOf(
            DisplayLanguage.Hindi to "डार्क मोड",
            DisplayLanguage.Punjabi to "ਡਾਰਕ ਮੋਡ"
        ),
        "Dasam Granth" to mapOf(
            DisplayLanguage.Hindi to "दशम ग्रंथ",
            DisplayLanguage.Punjabi to "ਦਸਮ ਗ੍ਰੰਥ"
        ),
        "Dasam Granth Ang 1" to mapOf(
            DisplayLanguage.Hindi to "दशम ग्रंथ अंग 1",
            DisplayLanguage.Punjabi to "ਦਸਮ ਗ੍ਰੰਥ ਅੰਗ ੧"
        ),
        "Dasam Granth Ang 119" to mapOf(
            DisplayLanguage.Hindi to "दशम ग्रंथ अंग 119",
            DisplayLanguage.Punjabi to "ਦਸਮ ਗ੍ਰੰਥ ਅੰਗ ੧੧੯"
        ),
        "Dasam Granth Ang 13" to mapOf(
            DisplayLanguage.Hindi to "दशम ग्रंथ अंग 13",
            DisplayLanguage.Punjabi to "ਦਸਮ ਗ੍ਰੰਥ ਅੰਗ ੧੩"
        ),
        "Dasam Granth Ang 1386" to mapOf(
            DisplayLanguage.Hindi to "दशम ग्रंथ अंग 1386",
            DisplayLanguage.Punjabi to "ਦਸਮ ਗ੍ਰੰਥ ਅੰਗ ੧੩੮੬"
        ),
        "Days" to mapOf(
            DisplayLanguage.Hindi to "दिन",
            DisplayLanguage.Punjabi to "ਦਿਨ"
        ),
        "Default" to mapOf(
            DisplayLanguage.Hindi to "डिफ़ॉल्ट",
            DisplayLanguage.Punjabi to "ਡਿਫੌਲਟ"
        ),
        "Devgandhari" to mapOf(
            DisplayLanguage.Hindi to "देवगंधारी",
            DisplayLanguage.Punjabi to "ਦੇਵਗੰਧਾਰੀ"
        ),
        "Dhanasari" to mapOf(
            DisplayLanguage.Hindi to "धनासरी",
            DisplayLanguage.Punjabi to "ਧਨਾਸਰੀ"
        ),
        "Display Language" to mapOf(
            DisplayLanguage.Hindi to "प्रदर्शन भाषा",
            DisplayLanguage.Punjabi to "ਐਪ ਦੀ ਭਾਸ਼ਾ"
        ),
        "Done" to mapOf(
            DisplayLanguage.Hindi to "हो गया",
            DisplayLanguage.Punjabi to "ਠੀਕ ਹੈ"
        ),
        "Downloaded / Imported Audio Files" to mapOf(
            DisplayLanguage.Hindi to "डाउनलोड / आयातित ऑडियो फाइलें",
            DisplayLanguage.Punjabi to "ਡਾਊਨਲੋਡ / ਇੰਪੋਰਟ ਕੀਤੀਆਂ ਆਡੀਓ ਫਾਈਲਾਂ"
        ),
      
        "Enable Notifications" to mapOf(
            DisplayLanguage.Hindi to "सूचनाएं सक्षम करें",
            DisplayLanguage.Punjabi to "ਨੋਟੀਫਿਕੇਸ਼ਨ ਚਾਲੂ ਕਰੋ"
        ),
        "English" to mapOf(
            DisplayLanguage.Hindi to "अंग्रेज़ी",
            DisplayLanguage.Punjabi to "ਅੰਗਰੇਜ਼ੀ"
        ),
        "English Translation" to mapOf(
            DisplayLanguage.Hindi to "अंग्रेजी अनुवाद",
            DisplayLanguage.Punjabi to "ਅੰਗਰੇਜ਼ੀ ਅਨੁਵਾਦ"
        ),
        "English translation not available" to mapOf(
            DisplayLanguage.Hindi to "अंग्रेजी अनुवाद उपलब्ध नहीं है",
            DisplayLanguage.Punjabi to "ਅੰਗਰੇਜ਼ੀ ਅਨੁਵਾਦ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
        ),
        "Enjoy synced Gurbani playback for better pronunciation and immersion, or import and sync your own custom audio files with banis." to mapOf(
            DisplayLanguage.Hindi to "बेहतर उच्चारण और मन लगाने के लिए सिंक किए गए गुरबानी प्लेबैक का आनंद लें, या अपनी खुद की कस्टम ऑडियो फाइलों को आयात और सिंक करें।",
            DisplayLanguage.Punjabi to "ਬਿਹਤਰ ਉਚਾਰਨ ਅਤੇ ਮਨ ਜੋੜਨ ਲਈ ਸਿੰਕ ਕੀਤੇ ਗੁਰਬਾਣੀ ਪਲੇਬੈਕ ਦਾ ਅਨੰਦ ਲਓ, ਜਾਂ ਬਾਣੀਆਂ ਨਾਲ ਆਪਣੀਆਂ ਖੁਦ ਦੀਆਂ ਕਸਟਮ ਆਡੀਓ ਫਾਈਲਾਂ ਇੰਪੋਰਟ ਅਤੇ ਸਿੰਕ ਕਰੋ।"
        ),
        "Enter the name of the artist / raagi for this audio file:" to mapOf(
            DisplayLanguage.Hindi to "इस ऑडियो फ़ाइल के लिए कलाकार / रागी का नाम दर्ज करें:",
            DisplayLanguage.Punjabi to "ਇਸ ਆਡੀਓ ਫਾਈਲ ਲਈ ਕਲਾਕਾਰ / ਰਾਗੀ ਦਾ ਨਾਮ ਦਰਜ ਕਰੋ:"
        ),
        "Estimated Time" to mapOf(
            DisplayLanguage.Hindi to "अनुमानित समय",
            DisplayLanguage.Punjabi to "ਅਨੁਮਾਨਿਤ ਸਮਾਂ"
        ),
        "Evening" to mapOf(
            DisplayLanguage.Hindi to "शाम",
            DisplayLanguage.Punjabi to "ਸ਼ਾਮ"
        ),
        "Evening paath restores mental calm." to mapOf(
            DisplayLanguage.Hindi to "शाम का पाठ मानसिक शांति बहाल करता है।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਮ ਦਾ ਪਾਠ ਮਾਨਸਿਕ ਸ਼ਾਂਤੀ ਪ੍ਰਦਾਨ ਕਰਦਾ ਹੈ।"
        ),
        "Exhale • Guru" to mapOf(
            DisplayLanguage.Hindi to "श्वास बाहर • गुरु",
            DisplayLanguage.Punjabi to "ਸੁਆਸ ਬਾਹਰ • ਗੁਰੂ"
        ),
        "Daily Nitnem" to mapOf(
            DisplayLanguage.Hindi to "दैनिक नितनेम",
            DisplayLanguage.Punjabi to "ਰੋਜ਼ਾਨਾ ਨਿਤਨੇਮ"
        ),
        "Begin your Nitnem today" to mapOf(
            DisplayLanguage.Hindi to "आज ही अपना नितनेम शुरू करें",
            DisplayLanguage.Punjabi to "ਅੱਜ ਤੋਂ ਆਪਣਾ ਨਿਤਨੇਮ ਸ਼ੁਰੂ ਕਰੋ"
        ),
                "False" to mapOf(
            DisplayLanguage.Hindi to "गलत",
            DisplayLanguage.Punjabi to "ਗਲਤ"
        ),
        "Fetching Hukamnama..." to mapOf(
            DisplayLanguage.Hindi to "हुकमनामा प्राप्त किया जा रहा है...",
            DisplayLanguage.Punjabi to "ਹੁਕਮਨਾਮਾ ਲੋਡ ਕੀਤਾ ਜਾ ਰਿਹਾ ਹੈ..."
        ),
        "Final Score" to mapOf(
            DisplayLanguage.Hindi to "अंतिम स्कोर",
            DisplayLanguage.Punjabi to "ਕੁੱਲ ਅੰਕ"
        ),
        "Finish Quiz" to mapOf(
            DisplayLanguage.Hindi to "प्रश्नोत्तरी समाप्त करें",
            DisplayLanguage.Punjabi to "ਕਵਿਜ਼ ਖ਼ਤਮ ਕਰੋ"
        ),
        "Fix Mistakes" to mapOf(
            DisplayLanguage.Hindi to "गलतियां सुधारें",
            DisplayLanguage.Punjabi to "ਗਲਤੀਆਂ ਸੁਧਾਰੋ"
        ),
        "Focused Gurbani experience designed for peace and consistency." to mapOf(
            DisplayLanguage.Hindi to "शांति और निरंतरता के लिए डिज़ाइन किया गया केंद्रित गुरबानी अनुभव।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤੀ ਅਤੇ ਲਗਾਤਾਰਤਾ ਲਈ ਤਿਆਰ ਕੀਤਾ ਗਿਆ ਗੁਰਬਾਣੀ ਅਨੁਭਵ।"
        ),
        "Font Size" to mapOf(
            DisplayLanguage.Hindi to "अक्षर आकार",
            DisplayLanguage.Punjabi to "ਅੱਖਰਾਂ ਦਾ ਆਕਾਰ"
        ),
        "Gauri" to mapOf(
            DisplayLanguage.Hindi to "गउड़ी",
            DisplayLanguage.Punjabi to "ਗਉੜੀ"
        ),
        "Gond" to mapOf(
            DisplayLanguage.Hindi to "गोंड",
            DisplayLanguage.Punjabi to "ਗੋਂਡ"
        ),
     
        "Great job! Keep it up!" to mapOf(
            DisplayLanguage.Hindi to "बहुत बढ़िया! इसे बनाए रखें!",
            DisplayLanguage.Punjabi to "ਬਹੁਤ ਵਧੀਆ! ਇਸੇ ਤਰ੍ਹਾਂ ਕਰਦੇ ਰਹੋ!"
        ),
        "Gujri" to mapOf(
            DisplayLanguage.Hindi to "गूजरी",
            DisplayLanguage.Punjabi to "ਗੂਜਰੀ"
        ),
        "Gurbani Database" to mapOf(
            DisplayLanguage.Hindi to "गुरबानी डेटाबेस",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਡੇਟਾਬੇਸ"
        ),
       "Stay connected with Friends & Family" to mapOf(
    DisplayLanguage.Hindi to "मित्रों और परिवार के साथ जुड़े रहें",
    DisplayLanguage.Punjabi to "ਮਿੱਤਰਾਂ ਅਤੇ ਪਰਿਵਾਰ ਨਾਲ ਜੁੜੇ ਰਹੋ"
),

"Track your progress and compete with others on the leaderboard." to mapOf(
    DisplayLanguage.Hindi to "अपनी प्रगति को ट्रैक करें और लीडरबोर्ड पर दूसरों के साथ प्रतिस्पर्धा करें।",
    DisplayLanguage.Punjabi to "ਆਪਣੀ ਪ੍ਰਗਤੀ ਨੂੰ ਟ੍ਰੈਕ ਕਰੋ ਅਤੇ ਲੀਡਰਬੋਰਡ 'ਤੇ ਦੂਜਿਆਂ ਨਾਲ ਮੁਕਾਬਲਾ ਕਰੋ।"
),
        "Gurbani Listening" to mapOf(
            DisplayLanguage.Hindi to "गुरबानी श्रवण",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਸੁਣਨਾ"
        ),
        "Gurbani Reading" to mapOf(
            DisplayLanguage.Hindi to "गुरबानी पठन",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਪੜ੍ਹਨਾ"
        ),

        "Gurmukhi" to mapOf(
            DisplayLanguage.Hindi to "गुरमुखी",
            DisplayLanguage.Punjabi to "ਗੁਰਮੁਖੀ"
        ),
        "Gurmukhi Text" to mapOf(
            DisplayLanguage.Hindi to "गुरमुखी पाठ",
            DisplayLanguage.Punjabi to "ਗੁਰਮੁਖੀ ਲਿਖਤ"
        ),
        "GurSaathi" to mapOf(
            DisplayLanguage.Hindi to "गुरसाथी",
            DisplayLanguage.Punjabi to "ਗੁਰਸਾਥੀ"
        ),
        "Guru Amar Das" to mapOf(
            DisplayLanguage.Hindi to "गुरु अमर दास",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਅਮਰਦਾਸ"
        ),
        "Guru Amar Das Ji" to mapOf(
            DisplayLanguage.Hindi to "गुरु अमर दास जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਅਮਰ ਦਾਸ ਜੀ"
        ),
        "Guru Angad Dev" to mapOf(
            DisplayLanguage.Hindi to "गुरु अंगद देव",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਅੰਗਦ ਦੇਵ"
        ),
        "Guru Arjan Dev" to mapOf(
            DisplayLanguage.Hindi to "गुरु अर्जुन देव",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਅਰਜਨ ਦੇਵ"
        ),
        "Guru Arjan Dev Ji" to mapOf(
            DisplayLanguage.Hindi to "गुरु अर्जुन देव जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ"
        ),
        "Guru Gobind Singh" to mapOf(
            DisplayLanguage.Hindi to "गुरु गोबिंद सिंह",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਗੋਬਿੰਦ ਸਿੰਘ"
        ),
        "Guru Gobind Singh Ji" to mapOf(
            DisplayLanguage.Hindi to "गुरु गोबिंद सिंह जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਗੋਬਿੰਦ ਸਿੰਘ ਜੀ"
        ),
        "Guru Nanak Dev" to mapOf(
            DisplayLanguage.Hindi to "गुरु नानक देव",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਨਾਨਕ ਦੇਵ"
        ),
        "Guru Nanak Dev Ji" to mapOf(
            DisplayLanguage.Hindi to "गुरु नानक देव जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ"
        ),
        "Guru Nanak Dev Ji & Bhagats" to mapOf(
            DisplayLanguage.Hindi to "गुरु नानक देव जी और भगत",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਅਤੇ ਭਗਤ ਸਾਹਿਬਾਨ"
        ),
        "Guru Nanak Dev Ji & Guru Arjan Dev Ji" to mapOf(
            DisplayLanguage.Hindi to "गुरु नानक देव जी और गुरु अर्जुन देव जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਅਤੇ ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ"
        ),
        "Guru Ram Das" to mapOf(
            DisplayLanguage.Hindi to "गुरु राम दास",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਰਾਮਦਾਸ"
        ),
        "Guru Ram Das Ji" to mapOf(
            DisplayLanguage.Hindi to "गुरु राम दास जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਰਾਮਦਾਸ ਜੀ"
        ),
        "Guru Tegh Bahadur" to mapOf(
            DisplayLanguage.Hindi to "गुरु तेग बहादुर",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਤੇਗ ਬਹਾਦਰ"
        ),
        "Guru Tegh Bahadur Ji" to mapOf(
            DisplayLanguage.Hindi to "गुरु तेग बहादुर जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਤੇਗ ਬਹਾਦਰ ਜੀ"
        ),
        "Highlight" to mapOf(
            DisplayLanguage.Hindi to "सक्रिय पंक्ति उजागर करें",
            DisplayLanguage.Punjabi to "ਚੱਲ ਰਹੀ ਲਾਈਨ ਉਜਾਗਰ ਕਰੋ"
        ),
        "Light" to mapOf(
            DisplayLanguage.Hindi to "हल्का",
            DisplayLanguage.Punjabi to "ਹਲਕਾ"
        ),
    "Dark" to mapOf(
    DisplayLanguage.Hindi to "डार्क", 
    DisplayLanguage.Punjabi to "ਡਾਰਕ" 
),
"AMOLED" to mapOf(
    DisplayLanguage.Hindi to "अमोलेड", 
    DisplayLanguage.Punjabi to "ਅਮੋਲੇਡ" 
),
        "Hindi" to mapOf(
            DisplayLanguage.Hindi to "हिंदी",
            DisplayLanguage.Punjabi to "ਹਿੰਦੀ"
        ),
        "Hindi Transliteration" to mapOf(
            DisplayLanguage.Hindi to "हिंदी लिप्यंतरण",
            DisplayLanguage.Punjabi to "ਹਿੰਦੀ ਅਨੁਵਾਦ"
        ),
        "Hindi Transliteration not available" to mapOf(
            DisplayLanguage.Hindi to "हिंदी लिप्यंतरण उपलब्ध नहीं है",
            DisplayLanguage.Punjabi to "ਹਿੰਦੀ ਅਨੁਵਾਦ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
        ),
        "Hindi Transliteration" to mapOf(
            DisplayLanguage.Hindi to "हिंदी लिप्यंतरण",
            DisplayLanguage.Punjabi to "ਹਿੰਦੀ ਲਿਪੀਅੰਤਰਨ"
        ),
        "Hint / Tranliteration" to mapOf(
            DisplayLanguage.Hindi to "संकेत/लिप्यंतरण",
            DisplayLanguage.Punjabi to "ਇਸ਼ਾਰਾ/ਅਨੁਵਾਦ"
        ),

        "Home" to mapOf(
            DisplayLanguage.Hindi to "मुख्य",
            DisplayLanguage.Punjabi to "ਮੁੱਖ"
        ),
        "Immersive Gurbani reading,\naudio listening and daily Nitnem." to mapOf(
            DisplayLanguage.Hindi to "तल्लीन गुरबाणी पठन,\nऑडियो श्रवण और दैनिक नितनेम।",
            DisplayLanguage.Punjabi to "ਲੀਨ ਗੁਰਬਾਣੀ ਪਾਠ,\nਆਡੀਓ ਸਰਵਣ ਅਤੇ ਰੋਜ਼ਾਨਾ ਨਿਤਨੇਮ।"
        ),
        "Import failed" to mapOf(
            DisplayLanguage.Hindi to "आयात विफल रहा",
            DisplayLanguage.Punjabi to "ਇੰਪੋਰਟ ਅਸਫਲ ਰਿਹਾ"
        ),
        "Import Gurbani Audio" to mapOf(
            DisplayLanguage.Hindi to "गुरबाणी ऑडियो आयात करें",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਆਡੀਓ ਇੰਪੋਰਟ ਕਰੋ"
        ),
        "Imported successfully!" to mapOf(
            DisplayLanguage.Hindi to "सफलतापूर्वक आयात किया गया!",
            DisplayLanguage.Punjabi to "ਸਫਲਤਾਪੂਰਵਕ ਇੰਪੋਰਟ ਹੋ ਗਿਆ!"
        ),

    "Traditional" to mapOf(
            DisplayLanguage.English to "Traditional",
            DisplayLanguage.Hindi to "पारंपरिक",
            DisplayLanguage.Punjabi to "ਪਰੰਪਰਾਗਤ"
        ),

        "Inhale • Wahe" to mapOf(
            DisplayLanguage.Hindi to "श्वास अंदर • वाहे",
            DisplayLanguage.Punjabi to "ਸੁਆਸ ਅੰਦਰ • ਵਾਹਿ"
        ),
        "Internal stillness" to mapOf(
            DisplayLanguage.Hindi to "आंतरिक स्थिरता",
            DisplayLanguage.Punjabi to "ਅੰਦਰੂਨੀ ਟਿਕਾਅ"
        ),
        "Jaap Sahib" to mapOf(
            DisplayLanguage.Hindi to "जाप साहिब",
            DisplayLanguage.Punjabi to "ਜਾਪੁ ਸਾਹਿਬ"
        ),
        "Jaap Sahib About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Gobind Singh Ji, the tenth Guru, and is the opening Bani of the Sri Dasam Granth Sahib.",
            DisplayLanguage.Hindi to "दसवें गुरु, गुरु गोबिंद सिंह जी द्वारा रचित, यह श्री दसम ग्रंथ साहिब की प्रारंभिक बाणी है।",
            DisplayLanguage.Punjabi to "ਦਸਵੇਂ ਪਾਤਸ਼ਾਹ ਸ੍ਰੀ ਗੁਰੂ ਗੋਬਿੰਦ ਸਿੰਘ ਜੀ ਦੀ ਉਚਾਰਨ ਕੀਤੀ ਹੋਈ, ਇਹ ਸ੍ਰੀ ਦਸਮ ਗ੍ਰੰਥ ਸਾਹਿਬ ਦੀ ਆਰੰਭਿਕ ਬਾਣੀ ਹੈ।"
        ),
        "Jaap Sahib Significance" to mapOf(
            DisplayLanguage.English to "Recited in the morning. It is a rhythmic declaration of the countless attributes, forms, and formless nature of God, building high spiritual courage.",
            DisplayLanguage.Hindi to "सुबह पाठ किया जाता है। यह ईश्वर के अनगिनत गुणों, रूपों और निराकार स्वभाव की एक लयबद्ध घोषणा है, जो उच्च आध्यात्मिक साहस का निर्माण करती है।",
            DisplayLanguage.Punjabi to "ਸਵੇਰ ਵੇਲੇ ਪੜ੍ਹੀ ਜਾਂਦੀ ਹੈ। ਇਹ ਅਕਾਲ ਪੁਰਖ ਦੇ ਅਨੇਕਾਂ ਨਾਮਾਂ, ਰੂਪਾਂ ਅਤੇ ਨਿਰਗੁਣ ਸਰੂਪ ਦਾ ਸਿਮਰਨ ਕਰਵਾ ਕੇ ਮਨ ਵਿੱਚ ਬੀਰ ਰਸ ਅਤੇ ਆਤਮਿਕ ਬਲ ਭਰਦੀ ਹੈ।"
        ),
        "Jaijavanti" to mapOf(
            DisplayLanguage.Hindi to "जैजावंती",
            DisplayLanguage.Punjabi to "ਜੈਜਾਵੰਤੀ"
        ),
        "Japji Sahib" to mapOf(
            DisplayLanguage.Hindi to "जपजी साहिब",
            DisplayLanguage.Punjabi to "ਜਪੁਜੀ ਸਾਹਿਬ"
        ),
        "Japji Sahib About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Nanak Dev Ji, this is the opening Bani of Sri Guru Granth Sahib Ji and contains the foundational philosophy of Sikhism.",
            DisplayLanguage.Hindi to "गुरु नानक देव जी द्वारा रचित, यह श्री गुरु ग्रंथ साहिब जी की प्रारंभिक बाणी है और इसमें सिख धर्म का मूलभूत दर्शन है।",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਦੁਆਰਾ ਉਚਾਰਨ ਕੀਤੀ ਗਈ, ਇਹ ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ ਦੀ ਆਰੰਭਿਕ ਬਾਣੀ ਹੈ ਅਤੇ ਸਿੱਖ ਧਰਮ ਦੇ ਮੂਲ ਫਲਸਫੇ ਨੂੰ ਦਰਸਾਉਂਦੀ ਹੈ।"
        ),
     "Japji Sahib Significance" to mapOf(
    DisplayLanguage.English to "Recited at Amrit Vela as part of the daily Nitnem, Japji Sahib helps seekers understand the Divine, overcome ego, cultivate truthful living, and deepen their connection with Waheguru through Naam Simran. It is regarded as the essence of Sikh philosophy and a guide for spiritual growth.",

    DisplayLanguage.Hindi to "अमृत वेला में दैनिक नितनेम के रूप में पाठ किया जाने वाला जपुजी साहिब साधक को परमात्मा को समझने, अहंकार पर विजय पाने, सत्यपूर्ण जीवन जीने और नाम सिमरन के माध्यम से वाहेगुरु के साथ गहरा संबंध बनाने में सहायता करता है। इसे सिख दर्शन का सार और आध्यात्मिक उन्नति का मार्गदर्शक माना जाता है।",

    DisplayLanguage.Punjabi to "ਅੰਮ੍ਰਿਤ ਵੇਲੇ ਨਿੱਤਨੇਮ ਦੇ ਹਿੱਸੇ ਵਜੋਂ ਪਾਠ ਕੀਤਾ ਜਾਣ ਵਾਲਾ ਜਪੁਜੀ ਸਾਹਿਬ ਸਾਧਕ ਨੂੰ ਪਰਮਾਤਮਾ ਨੂੰ ਸਮਝਣ, ਹਉਮੈ ਨੂੰ ਜਿੱਤਣ, ਸੱਚਾ ਜੀਵਨ ਜੀਊਣ ਅਤੇ ਨਾਮ ਸਿਮਰਨ ਰਾਹੀਂ ਵਾਹਿਗੁਰੂ ਨਾਲ ਡੂੰਘਾ ਸੰਬੰਧ ਜੋੜਣ ਵਿੱਚ ਸਹਾਇਤਾ ਕਰਦਾ ਹੈ। ਇਸ ਨੂੰ ਸਿੱਖ ਦਰਸ਼ਨ ਦਾ ਨਿਚੋੜ ਅਤੇ ਆਤਮਕ ਉੱਨਤੀ ਦਾ ਮਾਰਗਦਰਸ਼ਕ ਮੰਨਿਆ ਜਾਂਦਾ ਹੈ।"
),
"Supplication" to mapOf(
    DisplayLanguage.English to "Supplication",
    DisplayLanguage.Hindi to "विनती",
    DisplayLanguage.Punjabi to "ਬੇਨਤੀ"
),
"Sikh Rehat Maryada" to mapOf(
    DisplayLanguage.English to "Sikh Rehat Maryada",
    DisplayLanguage.Hindi to "सिख रहत मर्यादा",
    DisplayLanguage.Punjabi to "ਸਿੱਖ ਰਹਿਤ ਮਰਯਾਦਾ"
),
"Match the correct translation for the line above" to mapOf(
    DisplayLanguage.English to "Match the correct translation for the line above",
    DisplayLanguage.Hindi to "ऊपर दी गई पंक्ति का सही अनुवाद मिलाएं।",
    DisplayLanguage.Punjabi to "ਉਪਰੋਕਤ ਕਤਾਰ ਦਾ ਸਹੀ ਅਨੁਵਾਦ ਮਿਲਾਓ।"
),
"Ardas" to mapOf(
    DisplayLanguage.English to "Ardas",

    DisplayLanguage.Hindi to "अरदास",

    DisplayLanguage.Punjabi to "ਅਰਦਾਸ"
),
"Ardas About" to mapOf(
    DisplayLanguage.English to "Ardas is the formal Sikh prayer of supplication and remembrance. It was compiled from the teachings and traditions of the Sikh Gurus and Panth, expressing gratitude to Waheguru, remembering the sacrifices of Sikh martyrs, and seeking divine guidance, strength, and blessings.",

    DisplayLanguage.Hindi to "अरदास सिख धर्म की औपचारिक प्रार्थना और विनती है। यह सिख गुरुओं और पंथ की परंपराओं से संकलित है, जिसमें वाहेगुरु के प्रति कृतज्ञता व्यक्त की जाती है, सिख शहीदों के बलिदानों को स्मरण किया जाता है तथा दिव्य मार्गदर्शन, शक्ति और आशीर्वाद की प्रार्थना की जाती है।",

    DisplayLanguage.Punjabi to "ਅਰਦਾਸ ਸਿੱਖ ਧਰਮ ਦੀ ਰਸਮੀ ਬੇਨਤੀ ਅਤੇ ਪ੍ਰਾਰਥਨਾ ਹੈ। ਇਹ ਸਿੱਖ ਗੁਰੂ ਸਾਹਿਬਾਨ ਅਤੇ ਪੰਥ ਦੀਆਂ ਪਰੰਪਰਾਵਾਂ ਤੋਂ ਸੰਕਲਿਤ ਕੀਤੀ ਗਈ ਹੈ, ਜਿਸ ਵਿੱਚ ਵਾਹਿਗੁਰੂ ਦਾ ਸ਼ੁਕਰਾਨਾ ਕੀਤਾ ਜਾਂਦਾ ਹੈ, ਸਿੱਖ ਸ਼ਹੀਦਾਂ ਦੇ ਬਲਿਦਾਨਾਂ ਨੂੰ ਯਾਦ ਕੀਤਾ ਜਾਂਦਾ ਹੈ ਅਤੇ ਦਿਵਿਆ ਮਾਰਗਦਰਸ਼ਨ, ਤਾਕਤ ਅਤੇ ਅਸੀਸਾਂ ਦੀ ਅਰਦਾਸ ਕੀਤੀ ਜਾਂਦੀ ਹੈ।"
),
"Ardas Significance" to mapOf(
    DisplayLanguage.English to "Recited before and after important activities, prayers, and ceremonies, Ardas helps Sikhs express gratitude, seek guidance, remember the sacrifices of the Guru's Sikhs, and place their trust in Waheguru's will. It strengthens humility, faith, and the spirit of collective remembrance.",

    DisplayLanguage.Hindi to "महत्वपूर्ण कार्यों, प्रार्थनाओं और धार्मिक समारोहों से पहले तथा बाद में की जाने वाली अरदास सिखों को कृतज्ञता व्यक्त करने, मार्गदर्शन प्राप्त करने, गुरु के सिखों के बलिदानों को स्मरण करने और वाहेगुरु की रज़ा में विश्वास रखने में सहायता करती है। यह विनम्रता, श्रद्धा और सामूहिक स्मरण की भावना को मजबूत करती है।",

    DisplayLanguage.Punjabi to "ਮਹੱਤਵਪੂਰਨ ਕਾਰਜਾਂ, ਪ੍ਰਾਰਥਨਾਵਾਂ ਅਤੇ ਧਾਰਮਿਕ ਸਮਾਗਮਾਂ ਤੋਂ ਪਹਿਲਾਂ ਅਤੇ ਬਾਅਦ ਕੀਤੀ ਜਾਣ ਵਾਲੀ ਅਰਦਾਸ ਸਿੱਖਾਂ ਨੂੰ ਸ਼ੁਕਰਾਨਾ ਕਰਨ, ਮਾਰਗਦਰਸ਼ਨ ਮੰਗਣ, ਗੁਰੂ ਦੇ ਸਿੱਖਾਂ ਦੇ ਬਲਿਦਾਨਾਂ ਨੂੰ ਯਾਦ ਕਰਨ ਅਤੇ ਵਾਹਿਗੁਰੂ ਦੀ ਰਜ਼ਾ ਵਿੱਚ ਭਰੋਸਾ ਰੱਖਣ ਵਿੱਚ ਸਹਾਇਤਾ ਕਰਦੀ ਹੈ। ਇਹ ਨਿਮਰਤਾ, ਵਿਸ਼ਵਾਸ ਅਤੇ ਸਾਂਝੀ ਯਾਦ ਦੀ ਭਾਵਨਾ ਨੂੰ ਮਜ਼ਬੂਤ ਕਰਦੀ ਹੈ।"
),
        "Kalyan" to mapOf(
            DisplayLanguage.Hindi to "कल्याण",
            DisplayLanguage.Punjabi to "ਕਲਿਆਨ"
        ),
        "Kanara" to mapOf(
            DisplayLanguage.Hindi to "कानड़ा",
            DisplayLanguage.Punjabi to "ਕਾਨੜਾ"
        ),
        "Kedara" to mapOf(
            DisplayLanguage.Hindi to "केदारा",
            DisplayLanguage.Punjabi to "ਕੇਦਾਰਾ"
        ),
             "Kirtan Sohila" to mapOf(
            DisplayLanguage.Hindi to "कीर्तन सोहिला",
            DisplayLanguage.Punjabi to "ਕੀਰਤਨ ਸੋਹਿਲਾ"
        ),
        "Kirtan Sohila About" to mapOf(
            DisplayLanguage.English to "The bedtime prayer of the Sikhs, containing five beautiful hymns by Guru Nanak Dev Ji, Guru Ram Das Ji, and Guru Arjan Dev Ji.",
            DisplayLanguage.Hindi to "सिखों की रात की प्रार्थना, जिसमें गुरु नानक देव जी, गुरु राम दास जी और गुरु अर्जुन देव जी के पांच सुंदर भजन शामिल हैं।",
            DisplayLanguage.Punjabi to "ਸਿੱਖਾਂ ਦੀ ਰਾਤ ਵੇਲੇ ਸੌਣ ਤੋਂ ਪਹਿਲਾਂ ਪੜ੍ਹਨ ਵਾਲੀ ਬਾਣੀ, ਜਿਸ ਵਿੱਚ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ, ਗੁਰੂ ਰਾਮਦਾਸ ਜੀ ਅਤੇ ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ ਦੇ ਪੰਜ ਪਾਵਨ ਸ਼ਬਦ ਹਨ।"
        ),
        "Kirtan Sohila Significance" to mapOf(
            DisplayLanguage.English to "Recited before sleeping. It protects the subtle body, removes fear of the unknown, and surrounds the soul with peaceful, divine vibrations.",
            DisplayLanguage.Hindi to "सोने से पहले पाठ किया जाता है। यह सूक्ष्म शरीर की रक्षा करता है, अज्ञात के भय को दूर करता है, और आत्मा को शांतिपूर्ण, दिव्य तरंगों से घेरता है।",
            DisplayLanguage.Punjabi to "ਰਾਤ ਨੂੰ ਸੌਣ ਵੇਲੇ ਪੜ੍ਹੀ ਜਾਂਦੀ ਹੈ। ਇਹ ਮਨੁੱਖੀ ਮਨ ਵਿੱਚੋਂ ਡਰਾਉਣੇ ਸੁਪਨੇ, ਮੌਤ ਦਾ ਡਰ ਦੂਰ ਕਰਦੀ ਹੈ ਅਤੇ ਆਤਮਾ ਦੁਆਲੇ ਅਕਾਲ ਪੁਰਖ ਦੀ ਰੱਖਿਆ ਦਾ ਕੋਟ ਬਣਾਉਂਦੀ ਹੈ।"
        ),
        "Larivaar" to mapOf(
            DisplayLanguage.Hindi to "लड़ीवार",
            DisplayLanguage.Punjabi to "ਲੜੀਵਾਰ"
        ),
        "Let's learn Gurbani" to mapOf(
            DisplayLanguage.Hindi to "आइए गुरबाणी सीखें",
            DisplayLanguage.Punjabi to "ਆਓ ਗੁਰਬਾਣੀ ਸਿੱਖੀਏ"
        ),
        "License" to mapOf(
            DisplayLanguage.Hindi to "लाइसेंस",
            DisplayLanguage.Punjabi to "ਲਾਇਸੈਂਸ"
        ),
        "Line" to mapOf(
            DisplayLanguage.Hindi to "पंक्ति",
            DisplayLanguage.Punjabi to "ਲਾਈਨ"
        ),
        "Line Meaning" to mapOf(
            DisplayLanguage.Hindi to "पंक्ति का अर्थ",
            DisplayLanguage.Punjabi to "ਤੁਕ ਦਾ ਅਰਥ"
        ),
        "Line Spacing" to mapOf(
            DisplayLanguage.Hindi to "पंक्ति रिक्ति",
            DisplayLanguage.Punjabi to "ਲਾਈਨਾਂ ਵਿੱਚ ਦੂਰੀ"
        ),
        "Listen inwardly" to mapOf(
            DisplayLanguage.Hindi to "भीतर सुनें",
            DisplayLanguage.Punjabi to "ਅੰਦਰ ਧਿਆਨ ਨਾਲ ਸੁਣੋ"
        ),
        "Listening" to mapOf(
            DisplayLanguage.Hindi to "श्रवण",
            DisplayLanguage.Punjabi to "ਸੁਣਨਾ"
        ),
        "LIVE" to mapOf(
            DisplayLanguage.Hindi to "लाइव",
            DisplayLanguage.Punjabi to "ਲਾਈਵ"
        ),
        "Loading Gurbani..." to mapOf(
            DisplayLanguage.Hindi to "गुरबाणी लोड हो रही है...",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਲੋਡ ਹੋ ਰਹੀ ਹੈ..."
        ),
        "Majh" to mapOf(
            DisplayLanguage.Hindi to "माझ",
            DisplayLanguage.Punjabi to "ਮਾਝ"
        ),
        "Malhar" to mapOf(
            DisplayLanguage.Hindi to "मलार",
            DisplayLanguage.Punjabi to "ਮਲਾਰ"
        ),
        "Manage" to mapOf(
            DisplayLanguage.Hindi to "बदलें",
            DisplayLanguage.Punjabi to "ਸਮਾਂ ਬਦਲੋ"
        ),
        "Maru" to mapOf(
            DisplayLanguage.Hindi to "मारू",
            DisplayLanguage.Punjabi to "ਮਾਰੂ"
        ),
        "Meanings" to mapOf(
            DisplayLanguage.Hindi to "शब्दार्थ",
            DisplayLanguage.Punjabi to "ਸ਼ਬਦਾਰਥ"
        ),
        "mins" to mapOf(
            DisplayLanguage.Hindi to "मिनट",
            DisplayLanguage.Punjabi to "ਮਿੰਟ"
        ),
        "minutes" to mapOf(
            DisplayLanguage.Hindi to "मिनट",
            DisplayLanguage.Punjabi to "ਮਿੰਟ"
        ),
        "Mistakes" to mapOf(
            DisplayLanguage.Hindi to "गलतियां",
            DisplayLanguage.Punjabi to "ਗਲਤੀਆਂ"
        ),
        "Morning" to mapOf(
            DisplayLanguage.Hindi to "सुबह",
            DisplayLanguage.Punjabi to "ਸਵੇਰ"
        ),
        "Morning Gurbani builds focus for the day." to mapOf(
            DisplayLanguage.Hindi to "सुबह की गुरबाणी दिन भर के लिए ध्यान केंद्रित करती है।",
            DisplayLanguage.Punjabi to "ਸਵੇਰ ਦੀ ਗੁਰਬਾਣੀ ਦਿਨ ਭਰ ਲਈ ਮਨ ਦਾ ਟਿਕਾਅ ਬਣਾਉਂਦੀ ਹੈ।"
        ),
        "Morning Kirtan" to mapOf(
            DisplayLanguage.Hindi to "सुबह का कीर्तन",
            DisplayLanguage.Punjabi to "ਸਵੇਰ ਦਾ ਕੀਰਤਨ"
        ),
        "Morning Nitnem" to mapOf(
            DisplayLanguage.Hindi to "सुबह का नितनेम",
            DisplayLanguage.Punjabi to "ਸਵੇਰ ਦਾ ਨਿਤਨੇਮ"
        ),
        "Next Bani" to mapOf(
            DisplayLanguage.Hindi to "अगली बाणी",
            DisplayLanguage.Punjabi to "ਅਗਲੀ ਬਾਣੀ"
        ),
         "Next Line" to mapOf(
            DisplayLanguage.Hindi to "अगली पंक्ति",
            DisplayLanguage.Punjabi to "ਅਗਲੀ ਲਾਈਨ"
        ),

        "What is the next line of this Gurbani Paath?" to mapOf(
            DisplayLanguage.Hindi to "इस गुरबाणी पाठ की अगली पंक्ति क्या है?",
            DisplayLanguage.Punjabi to "ਇਸ ਗੁਰਬਾਣੀ ਪਾਠ ਦੀ ਅਗਲੀ ਲਾਈਨ ਕੀ ਹੈ?"
        ),
        "Night" to mapOf(
            DisplayLanguage.Hindi to "रात",
            DisplayLanguage.Punjabi to "ਰਾਤ"
        ),
        "Before Sleep" to mapOf(
            DisplayLanguage.Hindi to "सोने से पहले",
            DisplayLanguage.Punjabi to "ਸੌਣ ਤੋਂ ਪਹਿਲਾਂ"
        ),
        "Sunset Prayer" to mapOf(
    DisplayLanguage.English to "Sunset Prayer",
    DisplayLanguage.Hindi to "संध्या प्रार्थना",
    DisplayLanguage.Punjabi to "ਸੰਝ ਦੀ ਅਰਦਾਸ"
),

"Transition peacefully into the evening." to mapOf(
    DisplayLanguage.English to "Transition peacefully into the evening.",
    DisplayLanguage.Hindi to "शाम में शांतिपूर्वक प्रवेश करें।",
    DisplayLanguage.Punjabi to "ਸ਼ਾਮ ਦੇ ਸਮੇਂ ਵਿੱਚ ਸ਼ਾਂਤੀ ਨਾਲ ਪ੍ਰਵੇਸ਼ ਕਰੋ।"
),

"Rise with Simran" to mapOf(
    DisplayLanguage.English to "Rise with Simran",
    DisplayLanguage.Hindi to "सिमरन के साथ दिन की शुरुआत करें",
    DisplayLanguage.Punjabi to "ਸਿਮਰਨ ਨਾਲ ਦਿਨ ਦੀ ਸ਼ੁਰੂਆਤ ਕਰੋ"
),

"Spiritual energy peaks before sunrise." to mapOf(
    DisplayLanguage.English to "Spiritual energy peaks before sunrise.",
    DisplayLanguage.Hindi to "सूर्योदय से पहले आध्यात्मिक ऊर्जा अपने चरम पर होती है।",
    DisplayLanguage.Punjabi to "ਸੂਰਜ ਚੜ੍ਹਨ ਤੋਂ ਪਹਿਲਾਂ ਆਤਮਕ ਊਰਜਾ ਆਪਣੇ ਚਰਮ 'ਤੇ ਹੁੰਦੀ ਹੈ।"
),

"Focused Nitnem" to mapOf(
    DisplayLanguage.English to "Focused Nitnem",
    DisplayLanguage.Hindi to "एकाग्र ਨਿਤਨੇਮ",
    DisplayLanguage.Punjabi to "ਇਕਾਗਰ ਨਿਤਨੇਮ"
),

"Strength & Wisdom" to mapOf(
    DisplayLanguage.English to "Strength & Wisdom",
    DisplayLanguage.Hindi to "शक्ति और ज्ञान",
    DisplayLanguage.Punjabi to "ਤਾਕਤ ਅਤੇ ਗਿਆਨ"
),

"Reflect on devotion and clarity." to mapOf(
    DisplayLanguage.English to "Reflect on devotion and clarity.",
    DisplayLanguage.Hindi to "भक्ति और स्पष्टता पर मनन करें।",
    DisplayLanguage.Punjabi to "ਭਗਤੀ ਅਤੇ ਸਪਸ਼ਟਤਾ ਬਾਰੇ ਮਨਨ ਕਰੋ।"
),

"Fearless Spirit" to mapOf(
    DisplayLanguage.English to "Fearless Spirit",
    DisplayLanguage.Hindi to "निडर आत्मा",
    DisplayLanguage.Punjabi to "ਨਿਡਰ ਆਤਮਾ"
),

"Seek courage and divine protection." to mapOf(
    DisplayLanguage.English to "Seek courage and divine protection.",
    DisplayLanguage.Hindi to "साहस और दैवी सुरक्षा की प्रार्थना करें।",
    DisplayLanguage.Punjabi to "ਹਿੰਮਤ ਅਤੇ ਦਿਵਿਆ ਰੱਖਿਆ ਦੀ ਅਰਦਾਸ ਕਰੋ।"
),

"Inner Reflection" to mapOf(
    DisplayLanguage.English to "Inner Reflection",
    DisplayLanguage.Hindi to "आत्मिक चिंतन",
    DisplayLanguage.Punjabi to "ਅੰਦਰੂਨੀ ਚਿੰਤਨ"
),

"Pause and reconnect with Gurbani." to mapOf(
    DisplayLanguage.English to "Pause and reconnect with Gurbani.",
    DisplayLanguage.Hindi to "थोड़ा ठहरें और गुरबाणी से पुनः जुड़ें।",
    DisplayLanguage.Punjabi to "ਥੋੜ੍ਹਾ ਰੁਕੋ ਅਤੇ ਗੁਰਬਾਣੀ ਨਾਲ ਮੁੜ ਜੁੜੋ।"
),

"Prepare the mind for peaceful sleep." to mapOf(
    DisplayLanguage.English to "Prepare the mind for peaceful sleep.",
    DisplayLanguage.Hindi to "मन को शांतिपूर्ण नींद के लिए तैयार करें।",
    DisplayLanguage.Punjabi to "ਮਨ ਨੂੰ ਸ਼ਾਂਤਮਈ ਨੀਂਦ ਲਈ ਤਿਆਰ ਕਰੋ।"
),

"Peace" to mapOf(
    DisplayLanguage.English to "Peace",
    DisplayLanguage.Hindi to "शांति",
    DisplayLanguage.Punjabi to "ਸ਼ਾਂਤੀ"
),

"Meditative Calm" to mapOf(
    DisplayLanguage.English to "Meditative Calm",
    DisplayLanguage.Hindi to "ध्यानमय शांति",
    DisplayLanguage.Punjabi to "ਧਿਆਨਮਈ ਸ਼ਾਂਤੀ"
),

"Find tranquility through Gurbani." to mapOf(
    DisplayLanguage.English to "Find tranquility through Gurbani.",
    DisplayLanguage.Hindi to "गुरबाणी के माध्यम से शांति प्राप्त करें।",
    DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਰਾਹੀਂ ਅੰਦਰੂਨੀ ਸ਼ਾਂਤੀ ਪ੍ਰਾਪਤ ਕਰੋ।"
),

"Any Time" to mapOf(
    DisplayLanguage.English to "Any Time",
    DisplayLanguage.Hindi to "किसी भी समय",
    DisplayLanguage.Punjabi to "ਕਿਸੇ ਵੀ ਵੇਲੇ"
),

"Spiritual Energy" to mapOf(
    DisplayLanguage.English to "Spiritual Energy",
    DisplayLanguage.Hindi to "आध्यात्मिक ऊर्जा",
    DisplayLanguage.Punjabi to "ਆਤਮਕ ਊਰਜਾ"
),
"Prayer & Gratitude" to mapOf(
    DisplayLanguage.English to "Prayer & Gratitude",
    DisplayLanguage.Hindi to "प्रार्थना और कृतज्ञता",
    DisplayLanguage.Punjabi to "ਅਰਦਾਸ ਅਤੇ ਕ੍ਰਿਤਜਤਾ"
),

"Offer devotion with humility." to mapOf(
    DisplayLanguage.English to "Offer devotion with humility.",
    DisplayLanguage.Hindi to "विनम्रता के साथ भक्ति अर्पित करें।",
    DisplayLanguage.Punjabi to "ਨਿਮਰਤਾ ਨਾਲ ਭਗਤੀ ਅਰਪਣ ਕਰੋ।"
),

"Divine Letters" to mapOf(
    DisplayLanguage.English to "Divine Letters",
    DisplayLanguage.Hindi to "दिव्य पत्र",
    DisplayLanguage.Punjabi to "ਦਿਵਿਆ ਪੱਤਰ"
),

"Love & Devotion" to mapOf(
    DisplayLanguage.English to "Love & Devotion",
    DisplayLanguage.Hindi to "ਪ੍ਰੇਮ और भक्ति",
    DisplayLanguage.Punjabi to "ਪ੍ਰੇਮ ਅਤੇ ਭਗਤੀ"
),

"Reflect on longing for the Divine." to mapOf(
    DisplayLanguage.English to "Reflect on longing for the Divine.",
    DisplayLanguage.Hindi to "परमात्मा की लालसा पर मनन करें।",
    DisplayLanguage.Punjabi to "ਪ੍ਰਭੂ ਦੀ ਲਾਲਸਾ ਉੱਤੇ ਮਨਨ ਕਰੋ।"
),

"Wisdom" to mapOf(
    DisplayLanguage.English to "Wisdom",
    DisplayLanguage.Hindi to "ज्ञान",
    DisplayLanguage.Punjabi to "ਗਿਆਨ"
),

"Detachment & Courage" to mapOf(
    DisplayLanguage.English to "Detachment & Courage",
    DisplayLanguage.Hindi to "वैराग्य और साहस",
    DisplayLanguage.Punjabi to "ਵੈਰਾਗ ਅਤੇ ਹਿੰਮਤ"
),

"Reflect deeply on life and fearlessness." to mapOf(
    DisplayLanguage.English to "Reflect deeply on life and fearlessness.",
    DisplayLanguage.Hindi to "जीवन और निडरता पर गहराई से चिंतन करें।",
    DisplayLanguage.Punjabi to "ਜੀਵਨ ਅਤੇ ਨਿਡਰਤਾ ਬਾਰੇ ਡੂੰਘਾ ਮਨਨ ਕਰੋ।"
),

"Seasons of Life" to mapOf(
    DisplayLanguage.English to "Seasons of Life",
    DisplayLanguage.Hindi to "जीवन के मौसम",
    DisplayLanguage.Punjabi to "ਜੀਵਨ ਦੀਆਂ ਰੁੱਤਾਂ"
),

"Spiritual Journey" to mapOf(
    DisplayLanguage.English to "Spiritual Journey",
    DisplayLanguage.Hindi to "आध्यात्मिक यात्रा",
    DisplayLanguage.Punjabi to "ਆਤਮਕ ਯਾਤਰਾ"
),

"Experience devotion through changing seasons." to mapOf(
    DisplayLanguage.English to "Experience devotion through changing seasons.",
    DisplayLanguage.Hindi to "बदलती ऋतुओं के माध्यम से भक्ति का अनुभव करें।",
    DisplayLanguage.Punjabi to "ਬਦਲਦੀਆਂ ਰੁੱਤਾਂ ਰਾਹੀਂ ਭਗਤੀ ਦਾ ਅਨੁਭਵ ਕਰੋ।"
),

"Divine Oneness" to mapOf(
    DisplayLanguage.English to "Divine Oneness",
    DisplayLanguage.Hindi to "ईश्वरीय एकता",
    DisplayLanguage.Punjabi to "ਦਿਵਿਆ ਏਕਤਾ"
),

"Universal Truth" to mapOf(
    DisplayLanguage.English to "Universal Truth",
    DisplayLanguage.Hindi to "सार्वभौमिक सत्य",
    DisplayLanguage.Punjabi to "ਵਿਸ਼ਵਵਿਆਪੀ ਸੱਚ"
),

"Understand the essence of Ik Onkar." to mapOf(
    DisplayLanguage.English to "Understand the essence of Ik Onkar.",
    DisplayLanguage.Hindi to "इक ओंकार के सार को समझें।",
    DisplayLanguage.Punjabi to "ਇਕ ਓਅੰਕਾਰ ਦੇ ਮੂਲ ਭਾਵ ਨੂੰ ਸਮਝੋ।"
),

"Spiritual Dialogue" to mapOf(
    DisplayLanguage.English to "Spiritual Dialogue",
    DisplayLanguage.Hindi to "आध्यात्मिक संवाद",
    DisplayLanguage.Punjabi to "ਆਤਮਕ ਸੰਵਾਦ"
),

"Wisdom & Questions" to mapOf(
    DisplayLanguage.English to "Wisdom & Questions",
    DisplayLanguage.Hindi to "ज्ञान और प्रश्न",
    DisplayLanguage.Punjabi to "ਗਿਆਨ ਅਤੇ ਪ੍ਰਸ਼ਨ"
),

"Explore profound spiritual discussions." to mapOf(
    DisplayLanguage.English to "Explore profound spiritual discussions.",
    DisplayLanguage.Hindi to "गहन आध्यात्मिक चर्चाओं का अन्वेषण करें।",
    DisplayLanguage.Punjabi to "ਗਹਿਰੀਆਂ ਆਤਮਕ ਵਿਚਾਰ-ਚਰਚਾਵਾਂ ਨੂੰ ਜਾਣੋ।"
),

"Spring of Spirit" to mapOf(
    DisplayLanguage.English to "Spring of Spirit",
    DisplayLanguage.Hindi to "आत्मा का बसंत",
    DisplayLanguage.Punjabi to "ਆਤਮਾ ਦਾ ਬਸੰਤ"
),

"Renewal & Growth" to mapOf(
    DisplayLanguage.English to "Renewal & Growth",
    DisplayLanguage.Hindi to "नवੀਂकरण और विकास",
    DisplayLanguage.Punjabi to "ਨਵੀਂਕਰਨ ਅਤੇ ਵਿਕਾਸ"
),

"Refresh the soul with Gurbani." to mapOf(
    DisplayLanguage.English to "Refresh the soul with Gurbani.",
    DisplayLanguage.Hindi to "गुरबाणी से आत्मा को ताज़गी दें।",
    DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਨਾਲ ਆਤਮਾ ਨੂੰ ਤਾਜ਼ਗੀ ਬਖ਼ਸ਼ੋ।"
),

"Transformation" to mapOf(
    DisplayLanguage.English to "Transformation",
    DisplayLanguage.Hindi to "रूपांतरण",
    DisplayLanguage.Punjabi to "ਰੂਪਾਂਤਰਣ"
),

"Inner Change" to mapOf(
    DisplayLanguage.English to "Inner Change",
    DisplayLanguage.Hindi to "आंतरिक परिवर्तन",
    DisplayLanguage.Punjabi to "ਅੰਦਰੂਨੀ ਬਦਲਾਅ"
),

"Embrace spiritual awakening." to mapOf(
    DisplayLanguage.English to "Embrace spiritual awakening.",
    DisplayLanguage.Hindi to "आध्यात्मिक जागृति को अपनाएँ।",
    DisplayLanguage.Punjabi to "ਆਤਮਕ ਜਾਗਰਤੀ ਨੂੰ ਅਪਣਾਓ।"
),

"Divine Praise" to mapOf(
    DisplayLanguage.English to "Divine Praise",
    DisplayLanguage.Hindi to "ईश्वरीय स्तुति",
    DisplayLanguage.Punjabi to "ਦਿਵਿਆ ਉਸਤਤ"
),

"Cosmic Worship" to mapOf(
    DisplayLanguage.English to "Cosmic Worship",
    DisplayLanguage.Hindi to "ब्रह्मांडीय उपासना",
    DisplayLanguage.Punjabi to "ਬ੍ਰਹਿਮੰਡੀ ਉਪਾਸਨਾ"
),

"See the universe as divine worship." to mapOf(
    DisplayLanguage.English to "See the universe as divine worship.",
    DisplayLanguage.Hindi to "समस्त ब्रह्मांड को ईश्वरीय उपासना के रूप में देखें।",
    DisplayLanguage.Punjabi to "ਸਾਰੇ ਬ੍ਰਹਿਮੰਡ ਨੂੰ ਦਿਵਿਆ ਉਪਾਸਨਾ ਵਜੋਂ ਵੇਖੋ।"
),

"Warrior Spirit" to mapOf(
    DisplayLanguage.English to "Warrior Spirit",
    DisplayLanguage.Hindi to "योद्धा भावना",
    DisplayLanguage.Punjabi to "ਯੋਧਾ ਭਾਵਨਾ"
),

"Strength & Courage" to mapOf(
    DisplayLanguage.English to "Strength & Courage",
    DisplayLanguage.Hindi to "शक्ति और साहस",
    DisplayLanguage.Punjabi to "ਤਾਕਤ ਅਤੇ ਹਿੰਮਤ"
),

"Awaken fearless determination." to mapOf(
    DisplayLanguage.English to "Awaken fearless determination.",
    DisplayLanguage.Hindi to "निडर संकल्प को जागृत करें।",
    DisplayLanguage.Punjabi to "ਨਿਡਰ ਦ੍ਰਿੜਤਾ ਨੂੰ ਜਗਾਓ।"
),

"Raag Journey" to mapOf(
    DisplayLanguage.English to "Raag Journey",
    DisplayLanguage.Hindi to "राग यात्रा",
    DisplayLanguage.Punjabi to "ਰਾਗ ਯਾਤਰਾ"
),

"Musical Essence" to mapOf(
    DisplayLanguage.English to "Musical Essence",
    DisplayLanguage.Hindi to "संगीतमय सार",
    DisplayLanguage.Punjabi to "ਸੰਗੀਤਕ ਸਾਰ"
),

"Experience the flow of divine raags." to mapOf(
    DisplayLanguage.English to "Experience the flow of divine raags.",
    DisplayLanguage.Hindi to "दिव्य रागों के प्रवाह का अनुभव करें।",
    DisplayLanguage.Punjabi to "ਦਿਵਿਆ ਰਾਗਾਂ ਦੇ ਪ੍ਰਵਾਹ ਦਾ ਅਨੁਭਵ ਕਰੋ।"
),

"Traditional Gurbani Recitation" to mapOf(
    DisplayLanguage.English to "Traditional Gurbani Recitation",
    DisplayLanguage.Hindi to "परंपरागत गुरबाणी पाठ",
    DisplayLanguage.Punjabi to "ਪਰੰਪਰਾਗਤ ਗੁਰਬਾਣੀ ਪਾਠ"
),

"Gurbani" to mapOf(
    DisplayLanguage.English to "Gurbani",
    DisplayLanguage.Hindi to "गुरबाणी",
    DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ"
),

"Daily Gurbani" to mapOf(
    DisplayLanguage.English to "Daily Gurbani",
    DisplayLanguage.Hindi to "दैनिक गुरबाणी",
    DisplayLanguage.Punjabi to "ਰੋਜ਼ਾਨਾ ਗੁਰਬਾਣੀ"
),

"Stay connected with Gurbani." to mapOf(
    DisplayLanguage.English to "Stay connected with Gurbani.",
    DisplayLanguage.Hindi to "गुरबाणी से जुड़े रहें।",
    DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਨਾਲ ਜੁੜੇ ਰਹੋ।"
),

"Begin the day with uplifting Gurbani." to mapOf(
    DisplayLanguage.English to "Begin the day with uplifting Gurbani.",
    DisplayLanguage.Hindi to "प्रेरणादायक गुरबाणी के साथ दिन की शुरुआत करें।",
    DisplayLanguage.Punjabi to "ਪ੍ਰੇਰਣਾਦਾਇਕ ਗੁਰਬਾਣੀ ਨਾਲ ਦਿਨ ਦੀ ਸ਼ੁਰੂਆਤ ਕਰੋ।"
),
        "Peaceful Rest" to mapOf(
            DisplayLanguage.Hindi to "शांतिपूर्ण आराम",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤਮਈ ਆਰਾਮ"
        ),
        "Night prayer slows the mind before sleep." to mapOf(
            DisplayLanguage.Hindi to "रात की प्रार्थना सोने से पहले मन को शांत करती है।",
            DisplayLanguage.Punjabi to "ਰਾਤ ਦਾ ਪਾਠ ਸੌਣ ਤੋਂ ਪਹਿਲਾਂ ਮਨ ਦੀ ਗਤੀ ਨੂੰ ਸ਼ਾਂਤ ਕਰਦਾ ਹੈ।"
        ),
        "Nitnem" to mapOf(
            DisplayLanguage.Hindi to "नितनेम",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ"
        ),
        "Nitnem Gutka" to mapOf(
            DisplayLanguage.Hindi to "नितनेम गुटका",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਗੁਟਕਾ"
        ),
        "No bookmarks yet" to mapOf(
            DisplayLanguage.Hindi to "अभी तक कोई बुकमार्क नहीं",
            DisplayLanguage.Punjabi to "ਅਜੇ ਕੋਈ ਬੁੱਕਮਾਰਕ ਨਹੀਂ"
        ),
        "No quiz available" to mapOf(
            DisplayLanguage.Hindi to "कोई प्रश्नोत्तरी उपलब्ध नहीं है",
            DisplayLanguage.Punjabi to "ਕੋਈ ਕਵਿਜ਼ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
        ),
        "Not Synced" to mapOf(
            DisplayLanguage.Hindi to "सिंक नहीं हुआ",
            DisplayLanguage.Punjabi to "ਸਿੰਕ ਨਹੀਂ ਹੋਇਆ"
        ),
        "OFFLINE" to mapOf(
            DisplayLanguage.Hindi to "ऑफ़लाइन",
            DisplayLanguage.Punjabi to "ਔਫਲਾਈਨ"
        ),
        "Online Streaming Tracks / Links" to mapOf(
            DisplayLanguage.Hindi to "ऑनलाइन स्ट्रीमिंग ट्रैक / लिंक",
            DisplayLanguage.Punjabi to "ਆਨਲਾਈਨ ਸਟ੍ਰੀਮਿੰਗ ਟਰੈਕ / ਲਿੰਕ"
        ),
        "Other Banis" to mapOf(
            DisplayLanguage.Hindi to "अन्य बाणियां",
            DisplayLanguage.Punjabi to "ਹੋਰ ਬਾਣੀਆਂ"
        ),

        "Emerald" to mapOf(
            DisplayLanguage.Hindi to "पन्ना",
            DisplayLanguage.Punjabi to "ਪੰਨਾ"
        ),
        "Sapphire" to mapOf(
            DisplayLanguage.Hindi to "नीलम",
            DisplayLanguage.Punjabi to "ਨੀਲਮ"
        ),
        
        "Royal" to mapOf(
            DisplayLanguage.Hindi to "रॉयल",
            DisplayLanguage.Punjabi to "ਰਾਇਲ"
        ),
        "Sunset" to mapOf(
            DisplayLanguage.Hindi to "सूर्यास्त",
            DisplayLanguage.Punjabi to "ਸੂਰਜ ਅਸਤ"
        ),
        "Ruby" to mapOf(
            DisplayLanguage.Hindi to "रूबी",
            DisplayLanguage.Punjabi to "ਰੂਬੀ"
        ),
        "You have no hearts remaining." to mapOf(
            DisplayLanguage.Hindi to "आपके पास कोई दिल बाकी नहीं है।",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡੇ ਕੋਲ ਕੋਈ ਦਿਲ ਬਾਕੀ ਨਹੀਂ ਰਿਹਾ।"
        ),
        "Restart the quiz or continue in No-Hearts mode." to mapOf(
            DisplayLanguage.Hindi to "क्विज को फिर से शुरू करें या बिना दिल के मोड में जारी रखें।",
            DisplayLanguage.Punjabi to "ਕੁਇਜ਼ ਨੂੰ ਦੁਬਾਰਾ ਸ਼ੁਰੂ ਕਰੋ ਜਾਂ ਦਿਲ-ਰਹਿਤ ਮੋਡ ਵਿੱਚ ਜਾਰੀ ਰੱਖੋ।"
        ),

        "Out of Lives!" to mapOf(
            DisplayLanguage.Hindi to "जीवन समाप्त!",
            DisplayLanguage.Punjabi to "ਜੀਵਨ ਖਤਮ!"
        ),
        "Outstanding Gurbani knowledge!" to mapOf(
            DisplayLanguage.Hindi to "गुरबाणी का उत्कृष्ट ज्ञान!",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਦਾ ਬਹੁਤ ਵਧੀਆ ਗਿਆਨ!"
        ),
        "Paaths" to mapOf(
            DisplayLanguage.Hindi to "पाठ",
            DisplayLanguage.Punjabi to "ਪਾਠ"
        ),
        "Parsed from" to mapOf(
            DisplayLanguage.Hindi to "स्रोत",
            DisplayLanguage.Punjabi to "ਸਰੋਤ"
        ),
        "Pause" to mapOf(
            DisplayLanguage.Hindi to "विराम",
            DisplayLanguage.Punjabi to "ਰੋਕੋ"
        ),
        "Peaceful Gurbani reading and listening." to mapOf(
            DisplayLanguage.Hindi to "शांतिपूर्ण गुरबानी पठन और श्रवण।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤਮਈ ਗੁਰਬਾਣੀ ਪੜ੍ਹਨਾ ਅਤੇ ਸੁਣਨਾ।"
        ),
        "Peaceful • Focused • Sacred" to mapOf(
            DisplayLanguage.Hindi to "शांत • केंद्रित • पवित्र",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤਮਈ • ਕੇਂਦਰਿਤ • ਪਵਿੱਤਰ"
        ),
        "Perfect Score!" to mapOf(
            DisplayLanguage.Hindi to "उत्कृष्ट स्कोर!",
            DisplayLanguage.Punjabi to "ਪੂਰੇ ਅੰਕ!"
        ),
        "Perfect time for Japji Sahib and simran." to mapOf(
            DisplayLanguage.Hindi to "जपजी साहिब और सिमरन के लिए सही समय।",
            DisplayLanguage.Punjabi to "ਜਪੁਜੀ ਸਾਹਿਬ ਅਤੇ ਸਿਮਰਨ ਲਈ ਉੱਤਮ ਸਮਾਂ।"
        ),
        "Personalise your Nitnem experience" to mapOf(
            DisplayLanguage.Hindi to "अपने नितनेम अनुभव को अनुकूलित करें",
            DisplayLanguage.Punjabi to "ਆਪਣੇ ਨਿਤਨੇਮ ਅਨੁਭਵ ਨੂੰ ਨਿੱਜੀ ਬਣਾਓ"
        ),
        "Personalize Nitnem" to mapOf(
            DisplayLanguage.Hindi to "नितनेम को अनुकूलित करें",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਨਿੱਜੀ ਬਣਾਓ"
        ),
        "Personalize Widget" to mapOf(
            DisplayLanguage.Hindi to "विजेट को कस्टमाइज़ करें",
            DisplayLanguage.Punjabi to "ਵਿਜੇਟ ਨੂੰ ਨਿੱਜੀ ਬਣਾਓ"
        ),
        "Play" to mapOf(
            DisplayLanguage.Hindi to "चलाएं",
            DisplayLanguage.Punjabi to "ਚਲਾਓ"
        ),
        "Play the Recitation" to mapOf(
            DisplayLanguage.Hindi to "पाठ चलाएं",
            DisplayLanguage.Punjabi to "ਪਾਠ ਚਲਾਓ"
        ),
      
        "Reading" to mapOf(
    DisplayLanguage.English to "Reading",
    DisplayLanguage.Hindi to "पाठ",
    DisplayLanguage.Punjabi to "ਪਾਠ"
),

"Playback" to mapOf(
    DisplayLanguage.English to "Playback",
    DisplayLanguage.Hindi to "प्लेबैक",
    DisplayLanguage.Punjabi to "ਪਲੇਬੈਕ"
),

"Appearance" to mapOf(
    DisplayLanguage.English to "Appearance",
    DisplayLanguage.Hindi to "रूप-रंग",
    DisplayLanguage.Punjabi to "ਦਿੱਖ"
),

"Account & Data" to mapOf(
    DisplayLanguage.English to "Account & Data",
    DisplayLanguage.Hindi to "अकाउंट और डेटा",
    DisplayLanguage.Punjabi to "ਅਕਾਊਂਟ ਅਤੇ ਡਾਟਾ"
),

"Manage Audio" to mapOf(
    DisplayLanguage.English to "Manage Audio",
    DisplayLanguage.Hindi to "ऑडियो प्रबंधन",
    DisplayLanguage.Punjabi to "ਆਡੀਓ ਪ੍ਰਬੰਧਨ"
),
        "Playback Speed" to mapOf(
            DisplayLanguage.Hindi to "प्लेबैक गति",
            DisplayLanguage.Punjabi to "ਪਲੇਬੈਕ ਦੀ ਗਤੀ"
        ),
        "Prabhati" to mapOf(
            DisplayLanguage.Hindi to "प्रभाती",
            DisplayLanguage.Punjabi to "ਪ੍ਰਭਾਤੀ"
        ),
        "Practice" to mapOf(
            DisplayLanguage.Hindi to "अभ्यास",
            DisplayLanguage.Punjabi to "ਅਭਿਆਸ"
        ),
        "Practice and try again." to mapOf(
            DisplayLanguage.Hindi to "अभ्यास करें और पुनः प्रयास करें।",
            DisplayLanguage.Punjabi to "ਅਭਿਆਸ ਕਰੋ ਅਤੇ ਦੁਬਾਰਾ ਕੋਸ਼ਿਸ਼ ਕਰੋ।"
        ),
        "Prayer Reminders" to mapOf(
            DisplayLanguage.Hindi to "प्रार्थना अनुस्मारक",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਦੇ ਰੀਮਾਈਂਡਰ"
        ),
        "Prayer Timings" to mapOf(
            DisplayLanguage.Hindi to "प्रार्थना का समय",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਦਾ ਸਮਾਂ"
        ),
        "Protection" to mapOf(
            DisplayLanguage.Hindi to "सुरक्षा",
            DisplayLanguage.Punjabi to "ਰੱਖਿਆ"
        ),
        "Punjabi" to mapOf(
            DisplayLanguage.Hindi to "पंजाबी",
            DisplayLanguage.Punjabi to "ਪੰਜਾਬੀ"
        ),
        "Punjabi Translation" to mapOf(
            DisplayLanguage.Hindi to "पंजाबी अनुवाद",
            DisplayLanguage.Punjabi to "ਪੰਜਾਬੀ ਅਨੁਵਾਦ"
        ),
        "Punjabi translation not available" to mapOf(
            DisplayLanguage.Hindi to "पंजाबी अनुवाद उपलब्ध नहीं है",
            DisplayLanguage.Punjabi to "ਪੰਜਾਬੀ ਅਨੁਵਾਦ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
        ),
        "Pure black" to mapOf(
            DisplayLanguage.Hindi to "गहरा काला",
            DisplayLanguage.Punjabi to "ਗੂੜ੍ਹਾ ਕਾਲਾ"
        ),
        "Sync Studio Guide" to mapOf(
            DisplayLanguage.English to "Sync Studio Guide",
            DisplayLanguage.Hindi to "सिंक स्टूडियो गाइड",
            DisplayLanguage.Punjabi to "ਸਿੰਕ ਸਟੂਡੀਓ ਗਾਈਡ"
        ),
        "Create perfectly synced Gurbani timings" to mapOf(
            DisplayLanguage.English to "Create perfectly synced Gurbani timings",
            DisplayLanguage.Hindi to "पूरी तरह से सिंक गुरबानी समय बनाएं",
            DisplayLanguage.Punjabi to "ਪੂਰੀ ਤਰ੍ਹਾਂ ਸਿੰਕ ਗੁਰਬਾਣੀ ਸਮਾਂ ਬਣਾਓ"
        ),
       "Use headphones for precise synchronization" to mapOf(
            DisplayLanguage.English to "Use headphones for precise synchronization",
            DisplayLanguage.Hindi to "सटीक सिंक्रनाइज़ेशन के लिए हेडफ़ोन का उपयोग करें",
            DisplayLanguage.Punjabi to "ਸਟੀਕ ਸਿੰਕਰੋਨਾਈਜ਼ੇਸ਼ਨ ਲਈ ਹੈੱਡਫੋਨ ਦੀ ਵਰਤੋਂ ਕਰੋ"
       ),
   
        "Mascot" to mapOf(
            DisplayLanguage.English to "Mascot",
            DisplayLanguage.Hindi to "मस्कट",
            DisplayLanguage.Punjabi to "ਮਸਕਟ"
        ),
        "Streak" to mapOf(
            DisplayLanguage.English to "Streak",
            DisplayLanguage.Hindi to "स्ट्रीक",
            DisplayLanguage.Punjabi to "ਸਟ੍ਰੀਕ"
        ),
        "Quiz Complete" to mapOf(
            DisplayLanguage.Hindi to "प्रश्नोत्तरी पूर्ण",
            DisplayLanguage.Punjabi to "ਕਵਿਜ਼ ਪੂਰੀ ਹੋਈ"
        ),
        "Ramkali" to mapOf(
            DisplayLanguage.Hindi to "रामकली",
            DisplayLanguage.Punjabi to "ਰਾਮਕਲੀ"
        ),
        "Ramkali Ki Vaar" to mapOf(
            DisplayLanguage.Hindi to "रामकली की वार",
            DisplayLanguage.Punjabi to "ਰਾਮਕਲੀ ਕੀ ਵਾਰ"
        ),
        "Ramkali Ki Vaar About" to mapOf(
            DisplayLanguage.English to "A Vaar composed by Guru Amar Das Ji and Satta & Balwand, praising the glory of the Gurus and the succession of the light.",
            DisplayLanguage.Hindi to "गुरु अमर दास जी और सत्ता व बलवंड द्वारा रचित एक वार, जिसमें गुरुओं की महिमा और दिव्य प्रकाश के उत्तराधिकार की प्रशंसा की गई है।",
            DisplayLanguage.Punjabi to "ਤੀਜੇ ਪਾਤਸ਼ਾਹ ਸ੍ਰੀ ਗੁਰੂ ਅਮਰਦਾਸ ਜੀ ਅਤੇ ਰਾਗੀ ਸੱਤਾ ਤੇ ਬਲਵੰਡ ਜੀ ਦੁਆਰਾ ਉਚਾਰਨ ਕੀਤੀ ਗਈ ਵਾਰ, ਜੋ ਗੁਰੂ ਸਾਹਿਬਾਨ ਦੀ ਮਹਿਮਾ ਅਤੇ ਗੁਰਿਆਈ ਦੀ ਜੋਤ ਨੂੰ ਬਿਆਨ ਕਰਦੀ ਹੈ।"
        ),
        "Ramkali Ki Vaar Significance" to mapOf(
            DisplayLanguage.English to "Builds deep faith in the lineage of the Gurus and the power of the Guru's Shabad.",
            DisplayLanguage.Hindi to "गुरुओं की वंशावली और गुरु के शबद की शक्ति में गहरा विश्वास पैदा करता है।",
            DisplayLanguage.Punjabi to "ਇਹ ਬਾਣੀ ਗੁਰੂ ਸਾਹਿਬਾਨ ਦੀ ਜੋਤ ਅਤੇ ਗੁਰੂ ਦੇ ਸ਼ਬਦ ਦੀ ਅਥਾਹ ਸ਼ਕਤੀ ਵਿੱਚ ਅਟੁੱਟ ਵਿਸ਼ਵਾਸ ਪੈਦਾ ਕਰਦੀ ਹੈ।"
        ),
        "Read" to mapOf(
            DisplayLanguage.Hindi to "पाठ",
            DisplayLanguage.Punjabi to "ਪਾਠ"
        ),
        "Ready" to mapOf(
            DisplayLanguage.Hindi to "तैयार",
            DisplayLanguage.Punjabi to "ਤਿਆਰ"
        ),
        "Ready to begin" to mapOf(
            DisplayLanguage.Hindi to "शुरू करने के लिए तैयार",
            DisplayLanguage.Punjabi to "ਸ਼ੁਰੂ ਕਰਨ ਲਈ ਤਿਆਰ"
        ),
        "Reflection" to mapOf(
            DisplayLanguage.Hindi to "विचार",
            DisplayLanguage.Punjabi to "ਵਿਚਾਰ"
        ),
        "Rehras Sahib" to mapOf(
            DisplayLanguage.Hindi to "रहरास साहिब",
            DisplayLanguage.Punjabi to "ਰਹਰਾਸਿ ਸਾਹਿਬ"
        ),
        "Rehras Sahib About" to mapOf(
            DisplayLanguage.English to "The evening prayer of the Sikhs, consisting of hymns from Guru Nanak Dev Ji, Guru Amar Das Ji, Guru Ram Das Ji, Guru Arjan Dev Ji, and Guru Gobind Singh Ji.",
            DisplayLanguage.Hindi to "सिखों की शाम की प्रार्थना, जिसमें गुरु नानक देव जी, गुरु अमर दास जी, गुरु राम दास जी, गुरु अर्जुन देव जी और गुरु गोबिंद सिंह जी के भजन शामिल हैं।",
            DisplayLanguage.Punjabi to "ਸਿੱਖਾਂ ਦੀ ਸ਼ਾਮ ਦੀ ਨਿਤਨੇਮ ਦੀ ਬਾਣੀ, ਜਿਸ ਵਿੱਚ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ, ਗੁਰੂ ਅਮਰਦਾਸ ਜੀ, ਗੁਰੂ ਰਾਮਦਾਸ ਜੀ, ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ ਅਤੇ ਗੁਰੂ ਗੋਬਿੰਦ ਸਿੰਘ ਜੀ ਦੇ ਸ਼ਬਦ ਸ਼ਾਮਿਲ ਹਨ।"
        ),
        "Rehras Sahib Significance" to mapOf(
            DisplayLanguage.English to "Recited at sunset. It helps the mind unwind after work, reconnect with the Divine, and gain inner stability and gratitude.",
            DisplayLanguage.Hindi to "सूर्यास्त के समय पाठ किया जाता है। यह काम के बाद मन को शांत करने, परमात्मा से फिर जुड़ने और आंतरिक स्थिरता और कृतज्ञता प्राप्त करने में मदद करता है।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਮ ਵੇਲੇ ਸੂਰਜ ਡੁੱਬਣ ਸਮੇਂ ਪੜ੍ਹੀ ਜਾਂਦੀ ਹੈ। ਇਹ ਦਿਨ ਭਰ ਦੇ ਕੰਮਾਂ ਪਿੱਛੋਂ ਮਨ ਨੂੰ ਥਕਾਵਟ ਤੋਂ ਮੁਕਤ ਕਰਕੇ ਅਕਾਲ ਪੁਰਖ ਨਾਲ ਜੋੜਦੀ ਹੈ ਅਤੇ ਆਤਮਿਕ ਸ਼ਾਂਤੀ ਦਿੰਦੀ ਹੈ।"
        ),
        "Rehras should be between 4 PM and 9 PM" to mapOf(
            DisplayLanguage.Hindi to "रहरास शाम 4 से रात 9 बजे के बीच होना चाहिए",
            DisplayLanguage.Punjabi to "ਰਹਿਰਾਸ ਸਾਹਿਬ ਸ਼ਾਮ 4 ਤੋਂ ਰਾਤ 9 ਵਜੇ ਦੇ ਵਿਚਕਾਰ ਹੋਣਾ ਚਾਹੀਦਾ ਹੈ"
        ),
        "Repeat Mode" to mapOf(
            DisplayLanguage.Hindi to "पुनरावृत्ति मोड",
            DisplayLanguage.Punjabi to "ਦੁਹਰਾਓ ਮੋਡ"
        ),
        "Requires active internet connection" to mapOf(
            DisplayLanguage.Hindi to "सक्रिय इंटरनेट कनेक्शन की आवश्यकता है",
            DisplayLanguage.Punjabi to "ਆਨਲਾਈਨ ਚਲਾਉਣ ਲਈ ਇੰਟਰਨੈੱਟ ਦੀ ਲੋੜ ਹੈ"
        ),
        "Restart" to mapOf(
            DisplayLanguage.Hindi to "पुनः प्रारंभ करें",
            DisplayLanguage.Punjabi to "ਦੁਬਾਰਾ ਸ਼ੁਰੂ ਕਰੋ"
        ),
        "Restart Quiz" to mapOf(
            DisplayLanguage.Hindi to "प्रश्नोत्तरी पुनरारंभ करें",
            DisplayLanguage.Punjabi to "ਕਵਿਜ਼ ਮੁੜ-ਸ਼ੁਰੂ ਕਰੋ"
        ),
        "Review Mistakes" to mapOf(
            DisplayLanguage.Hindi to "गलतियों की समीक्षा करें",
            DisplayLanguage.Punjabi to "ਗਲਤੀਆਂ ਦੀ ਸਮੀਖਿਆ ਕਰੋ"
        ),
        "Roman Size" to mapOf(
            DisplayLanguage.Hindi to "रोमन आकार",
            DisplayLanguage.Punjabi to "ਰੋਮਨ ਆਕਾਰ"
        ),
        "Roman Transliteration" to mapOf(
            DisplayLanguage.Hindi to "रोमन लिप्यंतरण",
            DisplayLanguage.Punjabi to "ਰੋਮਨ ਲਿਪੀਅੰਤਰਨ"
        ),
        "Sarang" to mapOf(
            DisplayLanguage.Hindi to "सारंग",
            DisplayLanguage.Punjabi to "ਸਾਰੰਗ"
        ),
        "Save" to mapOf(
            DisplayLanguage.Hindi to "सहेजें",
            DisplayLanguage.Punjabi to "ਸੰਭਾਲੋ"
        ),
        "Save failed" to mapOf(
            DisplayLanguage.Hindi to "सहेजना विफल रहा",
            DisplayLanguage.Punjabi to "ਸੰਭਾਲਣਾ ਅਸਫਲ ਰਿਹਾ"
        ),
        "Save Timing File" to mapOf(
            DisplayLanguage.Hindi to "टाइमिंग फ़ाइल सहेजें",
            DisplayLanguage.Punjabi to "ਟਾਈਮਿੰਗ ਫਾਈਲ ਸੇਵ ਕਰੋ"
        ),
        "Saved" to mapOf(
            DisplayLanguage.Hindi to "सहेजे गए",
            DisplayLanguage.Punjabi to "ਸੰਭਾਲੇ ਹੋਏ"
        ),
        "Select App Language" to mapOf(
            DisplayLanguage.Hindi to "ऐप की भाषा चुनें",
            DisplayLanguage.Punjabi to "ਐਪ ਦੀ ਭਾਸ਼ਾ ਚੁਣੋ"
        ),
        "Select Audio File" to mapOf(
            DisplayLanguage.Hindi to "ऑडियो फ़ाइल चुनें",
            DisplayLanguage.Punjabi to "ਆਡੀਓ ਫਾਈਲ ਚੁਣੋ"
        ),
        "Select Bani" to mapOf(
            DisplayLanguage.Hindi to "बाणी चुनें",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਚੁਣੋ"
        ),
        "Select Bani & Audio" to mapOf(
            DisplayLanguage.Hindi to "बाणी और ऑडियो चुनें",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਅਤੇ ਆਡੀਓ ਚੁਣੋ"
        ),
        "Session complete. Remain still for a moment." to mapOf(
            DisplayLanguage.Hindi to "सत्र पूरा हुआ। एक क्षण के लिए स्थिर रहें।",
            DisplayLanguage.Punjabi to "ਸੈਸ਼ਨ ਪੂਰਾ ਹੋਇਆ। ਇੱਕ ਪਲ ਲਈ ਸਥਿਰ ਰਹੋ।"
        ),
        "Settings" to mapOf(
            DisplayLanguage.Hindi to "सेटिंग्स",
            DisplayLanguage.Punjabi to "ਸੈਟਿੰਗਾਂ"
        ),

        "Shabad Hazare" to mapOf(
            DisplayLanguage.Hindi to "शब्द हजारे",
            DisplayLanguage.Punjabi to "ਸ਼ਬਦ ਹਜ਼ਾਰੇ"
        ),
        "Shabad Hazare About" to mapOf(
            DisplayLanguage.English to "Hymns of longing and love composed by Guru Nanak Dev Ji and Guru Arjan Dev Ji, expressing the intense yearning of the soul for Waheguru.",
            DisplayLanguage.Hindi to "गुरु नानक देव जी और गुरु अर्जुन देव जी द्वारा रचित विरह और प्रेम के भजन, जो वाहेगुरु के लिए आत्मा की तीव्र लालसा को व्यक्त करते हैं।",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਅਤੇ ਸ੍ਰੀ ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ ਦੇ ਉਚਾਰਨ ਕੀਤੇ ਹੋਏ ਪ੍ਰੇਮ ਅਤੇ ਬਿਰਹੜੇ ਦੇ ਸ਼ਬਦ, ਜੋ ਪਰਮਾਤਮਾ ਨਾਲ ਮਿਲਾਪ ਦੀ ਤੜਫ ਨੂੰ ਬਿਆਨ ਕਰਦੇ ਹਨ।"
        ),
        "Shabad Hazare Significance" to mapOf(
            DisplayLanguage.English to "Recited to cultivate deep love, emotional connection, and direct spiritual relationship with the Creator.",
            DisplayLanguage.Hindi to "सृष्टिकर्ता के साथ गहरा प्रेम, भावनात्मक संबंध और सीधा आध्यात्मिक संबंध विकसित करने के लिए पाठ किया जाता है।",
            DisplayLanguage.Punjabi to "ਪ੍ਰਭੂ ਚਰਨਾਂ ਵਿੱਚ ਪ੍ਰੇਮ ਪੈਦਾ ਕਰਨ, ਆਤਮਿਕ ਖਿੱਚ ਅਤੇ ਸੱਚੇ ਮਿਲਾਪ ਦੀ ਭਾਵਨਾ ਨੂੰ ਮਨ ਵਿੱਚ ਵਸਾਉਣ ਲਈ ਇਸਦਾ ਪਾਠ ਕੀਤਾ ਜਾਂਦਾ ਹੈ।"
        ),
        "Sheikh Farid" to mapOf(
            DisplayLanguage.Hindi to "शेख फरीद",
            DisplayLanguage.Punjabi to "ਸ਼ੇਖ ਫਰੀਦ"
        ),
        "Sheikh Farid Ji" to mapOf(
            DisplayLanguage.Hindi to "शेख फरीद जी",
            DisplayLanguage.Punjabi to "ਸੇਖ ਫਰੀਦ ਜੀ"
        ),
        "Significance" to mapOf(
            DisplayLanguage.Hindi to "महत्व",
            DisplayLanguage.Punjabi to "ਮਹੱਤਵ"
        ),
        "Sohila should be between 8 PM and 2 AM" to mapOf(
            DisplayLanguage.Hindi to "सोहिला रात 8 से सुबह 2 बजे के बीच होना चाहिए",
            DisplayLanguage.Punjabi to "ਸੋਹਿਲਾ ਸਾਹਿਬ ਰਾਤ 8 ਤੋਂ ਸਵੇਰੇ 2 ਵਜੇ ਦੇ ਵਿਚਕਾਰ ਹੋਣਾ ਚਾਹੀਦਾ ਹੈ"
        ),
        "Soon to be Night" to mapOf(
            DisplayLanguage.Hindi to "संध्या काल",
            DisplayLanguage.Punjabi to "ਰਾਤ ਹੋਣ ਵਾਲੀ ਹੈ"
        ),
        "Sorath" to mapOf(
            DisplayLanguage.Hindi to "सोरठि",
            DisplayLanguage.Punjabi to "ਸੋਰਠਿ"
        ),
        "Source Ang" to mapOf(
            DisplayLanguage.Hindi to "स्रोत अंग",
            DisplayLanguage.Punjabi to "ਸਰੋਤ ਅੰਗ"
        ),
        "Speed" to mapOf(
            DisplayLanguage.Hindi to "गति",
            DisplayLanguage.Punjabi to "ਗਤੀ"
        ),
        "Spring Morning" to mapOf(
            DisplayLanguage.Hindi to "वसंत की सुबह",
            DisplayLanguage.Punjabi to "ਬਸੰਤ की सवेर"
        ),
        "Sri Guru Granth Sahib Ji" to mapOf(
            DisplayLanguage.Hindi to "श्री गुरु ग्रंथ साहिब जी",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ"
        ),
        "Sri Raag" to mapOf(
            DisplayLanguage.Hindi to "श्री राग",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਰਾਗ"
        ),
        "Start playback and carefully listen to the reciter." to mapOf(
            DisplayLanguage.Hindi to "ऑडियो चलाएं और पाठी को ध्यान से सुनें।",
            DisplayLanguage.Punjabi to "ਆਡੀਓ ਚਲਾਓ ਅਤੇ ਪਾਠੀ ਨੂੰ ਧਿਆਨ ਨਾਲ ਸੁਣੋ।"
        ),
        "Stored offline on your device" to mapOf(
            DisplayLanguage.Hindi to "आपके डिवाइस पर ऑफ़लाइन संग्रहीत",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡੇ ਡਿਵਾਈਸ 'ਤੇ ਆਫਲਾਈਨ ਸਟੋਰ ਕੀਤੀਆਂ"
        ),
        "Structure" to mapOf(
            DisplayLanguage.Hindi to "संरचना",
            DisplayLanguage.Punjabi to "ਬਣਤਰ"
        ),
        "Style" to mapOf(
            DisplayLanguage.Hindi to "शैली",
            DisplayLanguage.Punjabi to "ਸ਼ੈਲੀ"
        ),
        "Suhi" to mapOf(
            DisplayLanguage.Hindi to "सूही",
            DisplayLanguage.Punjabi to "ਸੂਹੀ"
        ),
        "Sukhmani Sahib" to mapOf(
            DisplayLanguage.Hindi to "सुखमनी साहिब",
            DisplayLanguage.Punjabi to "ਸੁਖਮਨੀ ਸਾਹਿਬ"
        ),
        "Sukhmani Sahib About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Arjan Dev Ji, the fifth Guru, meaning the 'Treasure of Peace' or 'Peace of the Mind', written in Raag Gauri.",
            DisplayLanguage.Hindi to "पांचवें गुरु, गुरु अर्जुन देव जी द्वारा गौरी राग में रचित, जिसका अर्थ है 'शांति की मणि' या 'मन की शांति'।",
            DisplayLanguage.Punjabi to "ਪੰਜਵੇਂ ਪਾਤਸ਼ਾਹ ਸ੍ਰੀ ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ ਦੁਆਰਾ ਗਉੜੀ ਰਾਗ ਵਿੱਚ ਉਚਾਰਨ ਕੀਤੀ ਗਈ ਮਹਾਨ ਬਾਣੀ, ਜਿਸਦਾ ਅਰਥ ਹੈ 'ਸੁਖਾਂ ਦੀ ਮਣੀ' ਜਾਂ 'ਮਨ ਨੂੰ ਸ਼ਾਂਤੀ ਦੇਣ ਵਾਲੀ ਬਾਣੀ'।"
        ),
        "Sukhmani Sahib Significance" to mapOf(
    DisplayLanguage.English to "Recited for spiritual peace and inner well-being, Sukhmani Sahib helps calm the mind, reduce anxiety, strengthen faith, and deepen devotion to Waheguru. Composed by Guru Arjan Dev Ji, it encourages remembrance of the Divine, virtuous living, and the cultivation of contentment, wisdom, and inner joy.",

    DisplayLanguage.Hindi to "आत्मिक शांति और मानसिक सुख के लिए पाठ किया जाने वाला सुखमनी साहिब मन को शांत करने, चिंता को कम करने, श्रद्धा को मजबूत करने और वाहेगुरु के प्रति भक्ति को गहरा करने में सहायता करता है। गुरु अर्जन देव जी द्वारा रचित यह बाणी परमात्मा के स्मरण, सद्गुणों से युक्त जीवन तथा संतोष, ज्ञान और आंतरिक आनंद को विकसित करने की प्रेरणा देती है।",

    DisplayLanguage.Punjabi to "ਆਤਮਕ ਸ਼ਾਂਤੀ ਅਤੇ ਮਨ ਦੀ ਸੁਖ-ਸ਼ਾਂਤੀ ਲਈ ਪਾਠ ਕੀਤਾ ਜਾਣ ਵਾਲਾ ਸੁਖਮਨੀ ਸਾਹਿਬ ਮਨ ਨੂੰ ਸ਼ਾਂਤ ਕਰਨ, ਚਿੰਤਾ ਘਟਾਉਣ, ਵਿਸ਼ਵਾਸ ਮਜ਼ਬੂਤ ਕਰਨ ਅਤੇ ਵਾਹਿਗੁਰੂ ਪ੍ਰਤੀ ਭਗਤੀ ਨੂੰ ਡੂੰਘਾ ਕਰਨ ਵਿੱਚ ਸਹਾਇਤਾ ਕਰਦਾ ਹੈ। ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ ਦੁਆਰਾ ਰਚਿਤ ਇਹ ਬਾਣੀ ਪਰਮਾਤਮਾ ਦੇ ਸਿਮਰਨ, ਗੁਣਵਾਨ ਜੀਵਨ ਅਤੇ ਸੰਤੋਖ, ਗਿਆਨ ਤੇ ਅੰਦਰੂਨੀ ਆਨੰਦ ਦੇ ਵਿਕਾਸ ਲਈ ਪ੍ਰੇਰਿਤ ਕਰਦੀ ਹੈ।"
),
"Roman" to mapOf(
    DisplayLanguage.English to "Roman",

    DisplayLanguage.Hindi to "रोमन",

    DisplayLanguage.Punjabi to "ਰੋਮਨ"
),
"Listen" to mapOf(
    DisplayLanguage.English to "Listen",

    DisplayLanguage.Hindi to "सुनो",

    DisplayLanguage.Punjabi to "ਸੁਣੋ"
),
"Pauri" to mapOf(
    DisplayLanguage.English to "Pauri",

    DisplayLanguage.Hindi to "पउड़ी",

    DisplayLanguage.Punjabi to "ਪਉੜੀ"
),
"Pauris" to mapOf(
    DisplayLanguage.English to "Pauris",

    DisplayLanguage.Hindi to "पउड़ी",

    DisplayLanguage.Punjabi to "ਪਉੜੀ"
),
"Saloks" to mapOf(
    DisplayLanguage.English to "Saloks",

    DisplayLanguage.Hindi to "सलोक",

    DisplayLanguage.Punjabi to "ਸਲੋਕ"
),
"Shabads" to mapOf(
    DisplayLanguage.English to "Shabads",

    DisplayLanguage.Hindi to "शबद",

    DisplayLanguage.Punjabi to "ਸ਼ਬਦ"
),
"Verse" to mapOf(
    DisplayLanguage.English to "Verse",

    DisplayLanguage.Hindi to "पद",

    DisplayLanguage.Punjabi to "ਪਉੜੀ"
),
"Verses" to mapOf(
    DisplayLanguage.English to "Verses",

    DisplayLanguage.Hindi to "पद",

    DisplayLanguage.Punjabi to "ਪਉੜੀ"
),
"Multiple Gurus" to mapOf(
    DisplayLanguage.English to "Multiple Gurus",
    DisplayLanguage.Hindi to "कई गुरु",
    DisplayLanguage.Punjabi to "ਕਈ ਗੁਰੂ"
),

"Compilation" to mapOf(
    DisplayLanguage.English to "Compilation",

    DisplayLanguage.Hindi to "संकलन",

    DisplayLanguage.Punjabi to "ਸੰਕਲਨ"
),
"Salok Mahalla 9" to mapOf(
    DisplayLanguage.English to "Salok Mahalla 9",

    DisplayLanguage.Hindi to "सलोक महला ९",

    DisplayLanguage.Punjabi to "ਸਲੋਕ ਮਹਲਾ ੯"
),
"Salok Mahalla 9 About" to mapOf(
    DisplayLanguage.English to "Salok Mahalla 9 is a collection of 57 Saloks composed by Guru Tegh Bahadur Ji, the Ninth Sikh Guru. These verses offer profound spiritual wisdom on detachment from worldly attachments, remembrance of the Divine, acceptance of God's will, and living a virtuous life amidst life's challenges.",

    DisplayLanguage.Hindi to "सलोक महला ९, नौवें सिख गुरु गुरु तेग बहादुर जी द्वारा रचित 57 सलोकों का संग्रह है। ये श्लोक सांसारिक मोह से विरक्ति, परमात्मा के स्मरण, ईश्वर की रज़ा को स्वीकार करने और जीवन की चुनौतियों के बीच सद्गुणों से युक्त जीवन जीने की गहन आध्यात्मिक शिक्षा प्रदान करते हैं।",

    DisplayLanguage.Punjabi to "ਸਲੋਕ ਮਹਲਾ ੯ ਨੌਵੇਂ ਪਾਤਸ਼ਾਹ ਗੁਰੂ ਤੇਗ ਬਹਾਦੁਰ ਜੀ ਦੁਆਰਾ ਰਚੇ ਗਏ 57 ਸਲੋਕਾਂ ਦਾ ਸੰਗ੍ਰਹਿ ਹੈ। ਇਹ ਸਲੋਕ ਸੰਸਾਰਿਕ ਮੋਹ ਤੋਂ ਵਿਰਕਤੀ, ਪਰਮਾਤਮਾ ਦੇ ਸਿਮਰਨ, ਪ੍ਰਭੂ ਦੀ ਰਜ਼ਾ ਨੂੰ ਸਵੀਕਾਰ ਕਰਨ ਅਤੇ ਜੀਵਨ ਦੀਆਂ ਚੁਣੌਤੀਆਂ ਵਿਚ ਗੁਣਵਾਨ ਜੀਵਨ ਜੀਊਣ ਬਾਰੇ ਡੂੰਘੀ ਆਤਮਕ ਸਿੱਖਿਆ ਦਿੰਦੇ ਹਨ।"
),
"Salok Mahalla 9 Significance" to mapOf(
    DisplayLanguage.English to "Recited to reflect on the impermanence of life and the importance of spiritual awareness, Salok Mahalla 9 helps cultivate detachment, resilience, contentment, and unwavering faith in Waheguru. Its teachings inspire individuals to rise above fear, greed, and worldly distractions while remaining steadfast in truth and devotion.",

    DisplayLanguage.Hindi to "जीवन की नश्वरता और आध्यात्मिक जागरूकता के महत्व पर चिंतन करने के लिए पाठ किए जाने वाले सलोक महला ९ साधक को वैराग्य, धैर्य, संतोष और वाहेगुरु में अटूट विश्वास विकसित करने में सहायता करते हैं। इसकी शिक्षाएँ भय, लोभ और सांसारिक विकर्षणों से ऊपर उठकर सत्य और भक्ति में दृढ़ रहने की प्रेरणा देती हैं।",

    DisplayLanguage.Punjabi to "ਜੀਵਨ ਦੀ ਨਾਸ਼ਵੰਤਾ ਅਤੇ ਆਤਮਕ ਜਾਗਰੂਕਤਾ ਦੇ ਮਹੱਤਵ ਉੱਤੇ ਮਨਨ ਕਰਨ ਲਈ ਪਾਠ ਕੀਤੇ ਜਾਣ ਵਾਲੇ ਸਲੋਕ ਮਹਲਾ ੯ ਵੈਰਾਗ, ਧੀਰਜ, ਸੰਤੋਖ ਅਤੇ ਵਾਹਿਗੁਰੂ ਵਿੱਚ ਅਡੋਲ ਵਿਸ਼ਵਾਸ ਪੈਦਾ ਕਰਨ ਵਿੱਚ ਸਹਾਇਤਾ ਕਰਦੇ ਹਨ। ਇਸ ਦੀਆਂ ਸਿੱਖਿਆਵਾਂ ਮਨੁੱਖ ਨੂੰ ਡਰ, ਲੋਭ ਅਤੇ ਸੰਸਾਰਿਕ ਭਟਕਣਾਂ ਤੋਂ ਉੱਪਰ ਉੱਠ ਕੇ ਸੱਚ ਅਤੇ ਭਗਤੀ ਵਿੱਚ ਦ੍ਰਿੜ੍ਹ ਰਹਿਣ ਦੀ ਪ੍ਰੇਰਣਾ ਦਿੰਦੀਆਂ ਹਨ।"
),
"Barah Maha" to mapOf(
    DisplayLanguage.English to "Barah Maha",

    DisplayLanguage.Hindi to "बारह माहा",

    DisplayLanguage.Punjabi to "ਬਾਰਹ ਮਾਹਾ"
),
"Barah Maha About" to mapOf(
    DisplayLanguage.English to "Barah Maha, composed by Guru Nanak Dev Ji and included in Sri Guru Granth Sahib Ji, is a poetic composition that uses the twelve months of the year to describe the soul's longing for union with the Divine. Through seasonal imagery, it conveys spiritual teachings about devotion, separation, and the joy of experiencing Waheguru's presence.",

    DisplayLanguage.Hindi to "बारह माहा, गुरु नानक देव जी द्वारा रचित और श्री गुरु ग्रंथ साहिब जी में संकलित एक काव्यात्मक रचना है। इसमें वर्ष के बारह महीनों के माध्यम से जीवात्मा की परमात्मा से मिलन की लालसा का वर्णन किया गया है। ऋतुओं के प्रतीकों द्वारा यह भक्ति, विरह और वाहेगुरु की उपस्थिति के आनंद से संबंधित आध्यात्मिक शिक्षाएँ प्रदान करती है।",

    DisplayLanguage.Punjabi to "ਬਾਰਹ ਮਾਹਾ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਦੁਆਰਾ ਰਚਿਤ ਅਤੇ ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ ਵਿੱਚ ਦਰਜ ਇਕ ਕਾਵਿ ਰਚਨਾ ਹੈ। ਇਸ ਵਿੱਚ ਸਾਲ ਦੇ ਬਾਰਾਂ ਮਹੀਨਿਆਂ ਰਾਹੀਂ ਜੀਵ-ਆਤਮਾ ਦੀ ਪਰਮਾਤਮਾ ਨਾਲ ਮਿਲਾਪ ਦੀ ਤੜਪ ਦਾ ਵਰਣਨ ਕੀਤਾ ਗਿਆ ਹੈ। ਰੁੱਤਾਂ ਦੇ ਪ੍ਰਤੀਕਾਂ ਰਾਹੀਂ ਇਹ ਭਗਤੀ, ਵਿਛੋੜੇ ਅਤੇ ਵਾਹਿਗੁਰੂ ਦੀ ਹਜ਼ੂਰੀ ਦੇ ਆਨੰਦ ਬਾਰੇ ਆਤਮਕ ਉਪਦੇਸ਼ ਦਿੰਦੀ ਹੈ।"
),
"Barah Maha Significance" to mapOf(
    DisplayLanguage.English to "Recited to reflect on the soul's relationship with the Divine throughout every season of life, Barah Maha helps cultivate devotion, patience, gratitude, and longing for Waheguru. Its teachings remind seekers that true fulfillment comes not from changing circumstances but from remaining connected to the Divine through love and remembrance.",

    DisplayLanguage.Hindi to "जीवन के प्रत्येक चरण में आत्मा और परमात्मा के संबंध पर चिंतन करने के लिए पाठ किया जाने वाला बारह माहा भक्ति, धैर्य, कृतज्ञता और वाहेगुरु के प्रति प्रेमपूर्ण लालसा को विकसित करने में सहायता करता है। इसकी शिक्षाएँ स्मरण कराती हैं कि सच्ची तृप्ति परिस्थितियों के बदलने से नहीं, बल्कि प्रेम और स्मरण के माध्यम से परमात्मा से जुड़े रहने से प्राप्त होती है।",

    DisplayLanguage.Punjabi to "ਜੀਵਨ ਦੀ ਹਰ ਰੁੱਤ ਅਤੇ ਹਰ ਹਾਲਤ ਵਿੱਚ ਆਤਮਾ ਅਤੇ ਪਰਮਾਤਮਾ ਦੇ ਸੰਬੰਧ ਉੱਤੇ ਮਨਨ ਕਰਨ ਲਈ ਪਾਠ ਕੀਤਾ ਜਾਣ ਵਾਲਾ ਬਾਰਹ ਮਾਹਾ ਭਗਤੀ, ਧੀਰਜ, ਸ਼ੁਕਰਾਨਾ ਅਤੇ ਵਾਹਿਗੁਰੂ ਪ੍ਰਤੀ ਪ੍ਰੇਮ ਭਰੀ ਤੜਪ ਪੈਦਾ ਕਰਨ ਵਿੱਚ ਸਹਾਇਤਾ ਕਰਦਾ ਹੈ। ਇਸ ਦੀਆਂ ਸਿੱਖਿਆਵਾਂ ਯਾਦ ਦਿਵਾਉਂਦੀਆਂ ਹਨ ਕਿ ਅਸਲ ਸੰਤੁਸ਼ਟੀ ਹਾਲਾਤਾਂ ਦੇ ਬਦਲਣ ਨਾਲ ਨਹੀਂ, ਸਗੋਂ ਪ੍ਰੇਮ ਅਤੇ ਸਿਮਰਨ ਰਾਹੀਂ ਪਰਮਾਤਮਾ ਨਾਲ ਜੁੜੇ ਰਹਿਣ ਨਾਲ ਪ੍ਰਾਪਤ ਹੁੰਦੀ ਹੈ।"
),
"Dakhni Oankar" to mapOf(
    DisplayLanguage.English to "Dakhni Oankar",

    DisplayLanguage.Hindi to "दखणी ओअंकार",

    DisplayLanguage.Punjabi to "ਦਖਣੀ ਓਅੰਕਾਰ"
),
"Dakhni Oankar About" to mapOf(
    DisplayLanguage.English to "Dakhni Oankar is a composition by Guru Nanak Dev Ji found in Sri Guru Granth Sahib Ji under Raag Ramkali. Through a series of verses arranged according to the letters of the Gurmukhi alphabet, it explains the nature of the One Supreme Reality, the importance of spiritual wisdom, and the path of realizing the Divine through Naam and righteous living.",

    DisplayLanguage.Hindi to "दखणी ओअंकार गुरु नानक देव जी द्वारा रचित बाणी है, जो श्री गुरु ग्रंथ साहिब जी में राग रामकली के अंतर्गत दर्ज है। गुरमुखी वर्णमाला के अक्षरों के अनुसार रचित इस बाणी में एक परम सत्य के स्वरूप, आध्यात्मिक ज्ञान के महत्व तथा नाम और धर्मपूर्ण जीवन के माध्यम से परमात्मा की प्राप्ति के मार्ग का वर्णन किया गया है।",

    DisplayLanguage.Punjabi to "ਦਖਣੀ ਓਅੰਕਾਰ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਦੀ ਰਚਿਤ ਬਾਣੀ ਹੈ ਜੋ ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ ਵਿੱਚ ਰਾਗ ਰਾਮਕਲੀ ਅਧੀਨ ਦਰਜ ਹੈ। ਗੁਰਮੁਖੀ ਵਰਣਮਾਲਾ ਦੇ ਅੱਖਰਾਂ ਅਨੁਸਾਰ ਰਚੀ ਇਸ ਬਾਣੀ ਵਿੱਚ ਇਕ ਪਰਮ ਸੱਚ ਦੇ ਸਰੂਪ, ਆਤਮਕ ਗਿਆਨ ਦੀ ਮਹੱਤਤਾ ਅਤੇ ਨਾਮ ਤੇ ਧਰਮਮਈ ਜੀਵਨ ਰਾਹੀਂ ਪਰਮਾਤਮਾ ਦੀ ਪ੍ਰਾਪਤੀ ਦੇ ਮਾਰਗ ਨੂੰ ਸਮਝਾਇਆ ਗਿਆ ਹੈ।"
),
"Dakhni Oankar Significance" to mapOf(
    DisplayLanguage.English to "Recited to reflect upon the oneness of the Divine and the purpose of human life, Dakhni Oankar helps cultivate spiritual understanding, humility, and devotion to Waheguru. Its teachings guide seekers beyond rituals and external appearances toward inner transformation, remembrance of Naam, and realization of the One Creator present in all.",

    DisplayLanguage.Hindi to "एक परमात्मा की एकता और मानव जीवन के उद्देश्य पर चिंतन करने के लिए पाठ की जाने वाली दखणी ओअंकार आध्यात्मिक समझ, विनम्रता और वाहेगुरु के प्रति भक्ति को विकसित करने में सहायता करती है। इसकी शिक्षाएँ साधक को कर्मकांडों और बाहरी दिखावे से ऊपर उठाकर आंतरिक परिवर्तन, नाम सिमरन और सर्वव्यापी एक परमात्मा की अनुभूति की ओर मार्गदर्शन करती हैं।",

    DisplayLanguage.Punjabi to "ਇਕ ਪਰਮਾਤਮਾ ਦੀ ਏਕਤਾ ਅਤੇ ਮਨੁੱਖੀ ਜੀਵਨ ਦੇ ਮਕਸਦ ਉੱਤੇ ਮਨਨ ਕਰਨ ਲਈ ਪਾਠ ਕੀਤੀ ਜਾਣ ਵਾਲੀ ਦਖਣੀ ਓਅੰਕਾਰ ਆਤਮਕ ਸਮਝ, ਨਿਮਰਤਾ ਅਤੇ ਵਾਹਿਗੁਰੂ ਪ੍ਰਤੀ ਭਗਤੀ ਪੈਦਾ ਕਰਨ ਵਿੱਚ ਸਹਾਇਤਾ ਕਰਦੀ ਹੈ। ਇਸ ਦੀਆਂ ਸਿੱਖਿਆਵਾਂ ਸਾਧਕ ਨੂੰ ਕਰਮਕਾਂਡਾਂ ਅਤੇ ਬਾਹਰੀ ਦਿਖਾਵੇ ਤੋਂ ਉੱਪਰ ਉਠਾ ਕੇ ਅੰਦਰੂਨੀ ਬਦਲਾਅ, ਨਾਮ ਸਿਮਰਨ ਅਤੇ ਸਰਬਵਿਆਪਕ ਇਕ ਕਰਤਾਰ ਦੀ ਪਛਾਣ ਵੱਲ ਪ੍ਰੇਰਿਤ ਕਰਦੀਆਂ ਹਨ।"
),
"Sidha Gosht About" to mapOf(
    DisplayLanguage.English to "Sidha Gosht is a profound spiritual dialogue composed by Guru Nanak Dev Ji and recorded in Sri Guru Granth Sahib Ji. It presents a series of questions posed by Siddhas and Yogis, along with Guru Nanak Dev Ji's responses, explaining the Sikh path of spirituality, the importance of Naam, and living a God-centered life while remaining engaged in the world.",

    DisplayLanguage.Hindi to "सिध गोष्ट गुरु नानक देव जी द्वारा रचित एक गहन आध्यात्मिक संवाद है, जो श्री गुरु ग्रंथ साहिब जी में दर्ज है। इसमें सिद्धों और योगियों द्वारा पूछे गए प्रश्नों तथा गुरु नानक देव जी के उत्तरों के माध्यम से सिख आध्यात्मिक मार्ग, नाम की महत्ता और संसार में रहते हुए परमात्मा-केंद्रित जीवन जीने की शिक्षा दी गई है।",

    DisplayLanguage.Punjabi to "ਸਿਧ ਗੋਸਟ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਦੁਆਰਾ ਰਚਿਆ ਗਿਆ ਇੱਕ ਡੂੰਘਾ ਆਤਮਕ ਸੰਵਾਦ ਹੈ ਜੋ ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ ਵਿੱਚ ਦਰਜ ਹੈ। ਇਸ ਵਿੱਚ ਸਿੱਧਾਂ ਅਤੇ ਜੋਗੀਆਂ ਵੱਲੋਂ ਪੁੱਛੇ ਗਏ ਪ੍ਰਸ਼ਨਾਂ ਅਤੇ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਦੇ ਉੱਤਰਾਂ ਰਾਹੀਂ ਸਿੱਖ ਆਤਮਕ ਮਾਰਗ, ਨਾਮ ਦੀ ਮਹੱਤਤਾ ਅਤੇ ਸੰਸਾਰ ਵਿੱਚ ਰਹਿੰਦੇ ਹੋਏ ਪਰਮਾਤਮਾ-ਕੇਂਦਰਿਤ ਜੀਵਨ ਜੀਊਣ ਦੀ ਸਿੱਖਿਆ ਦਿੱਤੀ ਗਈ ਹੈ।"
),
"Sidha Gosht" to mapOf(
    DisplayLanguage.English to "Sidha Gosht",

    DisplayLanguage.Hindi to "सिद्ध गोश्त",

    DisplayLanguage.Punjabi to "ਸਿਧ ਗੋਸਟ"
),
"Sidha Gosht Significance" to mapOf(
    DisplayLanguage.English to "Recited to reflect on spiritual wisdom and the purpose of human life, Sidh Gosht helps seekers understand the role of Naam, humility, and divine awareness in achieving spiritual growth. Its teachings emphasize that liberation is attained not through renunciation alone, but through remembrance of Waheguru, truthful living, and selfless service while actively participating in society.",

    DisplayLanguage.Hindi to "आध्यात्मिक ज्ञान और मानव जीवन के उद्देश्य पर चिंतन करने के लिए पाठ की जाने वाली सिध गोष्ट साधक को नाम, विनम्रता और ईश्वरीय चेतना की भूमिका को समझने में सहायता करती है। इसकी शिक्षाएँ बताती हैं कि मुक्ति केवल संसार त्यागने से नहीं, बल्कि वाहेगुरु के स्मरण, सत्यपूर्ण जीवन और निःस्वार्थ सेवा के माध्यम से प्राप्त होती है।",

    DisplayLanguage.Punjabi to "ਆਤਮਕ ਗਿਆਨ ਅਤੇ ਮਨੁੱਖੀ ਜੀਵਨ ਦੇ ਮਕਸਦ ਉੱਤੇ ਮਨਨ ਕਰਨ ਲਈ ਪਾਠ ਕੀਤੀ ਜਾਣ ਵਾਲੀ ਸਿਧ ਗੋਸਟ ਸਾਧਕ ਨੂੰ ਨਾਮ, ਨਿਮਰਤਾ ਅਤੇ ਪ੍ਰਭੂ-ਚੇਤਨਾ ਦੀ ਮਹੱਤਤਾ ਸਮਝਣ ਵਿੱਚ ਸਹਾਇਤਾ ਕਰਦੀ ਹੈ। ਇਸ ਦੀਆਂ ਸਿੱਖਿਆਵਾਂ ਦੱਸਦੀਆਂ ਹਨ ਕਿ ਮੁਕਤੀ ਕੇਵਲ ਸੰਸਾਰ ਤਿਆਗਣ ਨਾਲ ਨਹੀਂ, ਸਗੋਂ ਵਾਹਿਗੁਰੂ ਦੇ ਸਿਮਰਨ, ਸੱਚੇ ਜੀਵਨ ਅਤੇ ਨਿਸ਼ਕਾਮ ਸੇਵਾ ਰਾਹੀਂ ਪ੍ਰਾਪਤ ਹੁੰਦੀ ਹੈ ਜਦੋਂ ਮਨੁੱਖ ਸਮਾਜ ਵਿੱਚ ਰਹਿੰਦਾ ਹੋਇਆ ਧਾਰਮਿਕ ਜੀਵਨ ਜੀਊਂਦਾ ਹੈ।"
),
"Chandi Di Vaar" to mapOf(
    DisplayLanguage.English to "Chandi Di Vaar",

    DisplayLanguage.Hindi to "चंडी दी वार",

    DisplayLanguage.Punjabi to "ਚੰਡੀ ਦੀ ਵਾਰ"
),
"Chandi Di Vaar About" to mapOf(
    DisplayLanguage.English to "Chandi Di Vaar, also known as Vaar Sri Bhagauti Ji Ki, was composed by Guru Gobind Singh Ji and is recorded in Dasam Granth Sahib. Written in the style of a Punjabi vaar, it narrates the battles between the forces of righteousness and evil through the symbolic account of Chandi's victories over oppressive demons. The Bani uses powerful martial imagery to inspire courage, spiritual strength, and the triumph of divine justice.",

    DisplayLanguage.Hindi to "चंडी दी वार, जिसे वार श्री भगौती जी की भी कहा जाता है, गुरु गोबिंद सिंह जी द्वारा रचित है और दशम ग्रंथ साहिब में दर्ज है। पंजाबी वार शैली में लिखी गई यह रचना चंडी द्वारा अत्याचारी दैत्यों पर प्राप्त विजयों के प्रतीकात्मक वर्णन के माध्यम से धर्म और अधर्म के संघर्ष को प्रस्तुत करती है। यह बाणी साहस, आध्यात्मिक शक्ति और दैवी न्याय की विजय का संदेश देती है।",

    DisplayLanguage.Punjabi to "ਚੰਡੀ ਦੀ ਵਾਰ, ਜਿਸਨੂੰ ਵਾਰ ਸ੍ਰੀ ਭਗਉਤੀ ਜੀ ਕੀ ਵੀ ਕਿਹਾ ਜਾਂਦਾ ਹੈ, ਗੁਰੂ ਗੋਬਿੰਦ ਸਿੰਘ ਜੀ ਦੁਆਰਾ ਰਚਿਤ ਹੈ ਅਤੇ ਦਸਮ ਗ੍ਰੰਥ ਸਾਹਿਬ ਵਿੱਚ ਦਰਜ ਹੈ। ਪੰਜਾਬੀ ਵਾਰ ਦੀ ਸ਼ੈਲੀ ਵਿੱਚ ਲਿਖੀ ਗਈ ਇਹ ਰਚਨਾ ਚੰਡੀ ਵੱਲੋਂ ਅਤਿਆਚਾਰੀ ਦੈਤਾਂ ਉੱਤੇ ਪ੍ਰਾਪਤ ਕੀਤੀਆਂ ਜਿੱਤਾਂ ਦੇ ਪ੍ਰਤੀਕਾਤਮਕ ਵਰਣਨ ਰਾਹੀਂ ਧਰਮ ਅਤੇ ਅਧਰਮ ਦੇ ਸੰਘਰਸ਼ ਨੂੰ ਦਰਸਾਉਂਦੀ ਹੈ। ਇਹ ਬਾਣੀ ਸਾਹਸ, ਆਤਮਕ ਤਾਕਤ ਅਤੇ ਦਿਵਿਆ ਨਿਆਂ ਦੀ ਜਿੱਤ ਦਾ ਸੰਦੇਸ਼ ਦਿੰਦੀ ਹੈ।"
),
"Chandi Di Vaar Significance" to mapOf(
    DisplayLanguage.English to "Recited to cultivate courage, resilience, and the spirit of righteousness, Chandi Di Vaar inspires Sikhs to overcome fear, injustice, and inner weaknesses. Its teachings remind the seeker that true victory comes through reliance on the Divine, moral discipline, and fearless commitment to truth. The Bani strengthens the warrior spirit while emphasizing that all power ultimately belongs to Waheguru.",

    DisplayLanguage.Hindi to "साहस, दृढ़ता और धर्म की भावना को विकसित करने के लिए पाठ की जाने वाली चंडी दी वार सिखों को भय, अन्याय और आंतरिक कमजोरियों पर विजय प्राप्त करने की प्रेरणा देती है। इसकी शिक्षाएँ स्मरण कराती हैं कि वास्तविक विजय परमात्मा पर भरोसा, नैतिक अनुशासन और सत्य के प्रति निडर प्रतिबद्धता से प्राप्त होती है। यह बाणी योद्धा भावना को सशक्त बनाती है और यह भी बताती है कि समस्त शक्ति अंततः वाहेगुरु की ही है।",

    DisplayLanguage.Punjabi to "ਹਿੰਮਤ, ਦ੍ਰਿੜਤਾ ਅਤੇ ਧਰਮ ਦੀ ਭਾਵਨਾ ਨੂੰ ਵਿਕਸਿਤ ਕਰਨ ਲਈ ਪਾਠ ਕੀਤੀ ਜਾਣ ਵਾਲੀ ਚੰਡੀ ਦੀ ਵਾਰ ਸਿੱਖਾਂ ਨੂੰ ਡਰ, ਅਨਿਆਂ ਅਤੇ ਅੰਦਰੂਨੀ ਕਮਜ਼ੋਰੀਆਂ ਉੱਤੇ ਜਿੱਤ ਪ੍ਰਾਪਤ ਕਰਨ ਲਈ ਪ੍ਰੇਰਿਤ ਕਰਦੀ ਹੈ। ਇਸ ਦੀਆਂ ਸਿੱਖਿਆਵਾਂ ਯਾਦ ਦਿਵਾਉਂਦੀਆਂ ਹਨ ਕਿ ਅਸਲ ਜਿੱਤ ਪਰਮਾਤਮਾ ਉੱਤੇ ਭਰੋਸੇ, ਨੈਤਿਕ ਅਨੁਸ਼ਾਸਨ ਅਤੇ ਸੱਚ ਪ੍ਰਤੀ ਨਿਡਰ ਵਚਨਬੱਧਤਾ ਰਾਹੀਂ ਮਿਲਦੀ ਹੈ। ਇਹ ਬਾਣੀ ਸੰਤ-ਸਿਪਾਹੀ ਦੀ ਭਾਵਨਾ ਨੂੰ ਮਜ਼ਬੂਤ ਕਰਦੀ ਹੈ ਅਤੇ ਇਹ ਵੀ ਦਰਸਾਉਂਦੀ ਹੈ ਕਿ ਸਾਰੀ ਤਾਕਤ ਅੰਤ ਵਿੱਚ ਵਾਹਿਗੁਰੂ ਦੀ ਹੀ ਹੈ।"
),
  "Sri Guru Granth Sahib Paath Bhog (Ragmala)" to mapOf(
            DisplayLanguage.Hindi to "श्री गुरु ग्रंथ साहिब पाठ भोग (रागमाला)",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਪਾਠ ਭੋਗ (ਰਾਗਮਾਲਾ)"
        ),
"Ragmala About" to mapOf(
    DisplayLanguage.English to "Ragmala is the composition traditionally recited at the conclusion of a complete Sri Guru Granth Sahib Ji Paath and Bhog. It appears at the end of Sri Guru Granth Sahib Ji and presents a poetic description of various raags and their relationships. During a Bhog ceremony, the final portion of the Paath is completed, Ardas is offered, and Hukamnama is taken, marking the respectful conclusion of the recitation.",

    DisplayLanguage.Hindi to "रागमाला वह रचना है जिसका पाठ परंपरागत रूप से संपूर्ण श्री गुरु ग्रंथ साहिब जी के पाठ और भोग के समापन पर किया जाता है। यह श्री गुरु ग्रंथ साहिब जी के अंत में स्थित है और विभिन्न रागों तथा उनके संबंधों का काव्यात्मक वर्णन प्रस्तुत करती है। भोग समारोह के दौरान पाठ का अंतिम भाग पूर्ण किया जाता है, अरदास की जाती है और हुकमनामा लिया जाता है, जिससे पाठ का सम्मानपूर्वक समापन होता है।",

    DisplayLanguage.Punjabi to "ਰਾਗਮਾਲਾ ਉਹ ਰਚਨਾ ਹੈ ਜੋ ਪਰੰਪਰਾਗਤ ਤੌਰ 'ਤੇ ਸੰਪੂਰਨ ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ ਦੇ ਪਾਠ ਅਤੇ ਭੋਗ ਦੇ ਸਮਾਪਨ ਸਮੇਂ ਪੜ੍ਹੀ ਜਾਂਦੀ ਹੈ। ਇਹ ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ ਦੇ ਅੰਤ ਵਿੱਚ ਦਰਜ ਹੈ ਅਤੇ ਵੱਖ-ਵੱਖ ਰਾਗਾਂ ਅਤੇ ਉਨ੍ਹਾਂ ਦੇ ਆਪਸੀ ਸੰਬੰਧਾਂ ਦਾ ਕਾਵਿ-ਰੂਪ ਵਰਣਨ ਕਰਦੀ ਹੈ। ਭੋਗ ਸਮੇਂ ਪਾਠ ਦਾ ਅੰਤਿਮ ਭਾਗ ਸੰਪੂਰਨ ਕੀਤਾ ਜਾਂਦਾ ਹੈ, ਅਰਦਾਸ ਕੀਤੀ ਜਾਂਦੀ ਹੈ ਅਤੇ ਹੁਕਮਨਾਮਾ ਲਿਆ ਜਾਂਦਾ ਹੈ, ਜਿਸ ਨਾਲ ਪਾਠ ਦਾ ਸਤਿਕਾਰਪੂਰਵਕ ਸਮਾਪਨ ਹੁੰਦਾ ਹੈ।"
),
"Ragmala Significance" to mapOf(
    DisplayLanguage.English to "Recited at the completion of a Sri Guru Granth Sahib Ji Paath, the Bhog ceremony signifies gratitude, spiritual fulfillment, and reflection upon the Guru's teachings. It marks the successful completion of the recitation and provides an opportunity for Sangat to seek guidance through Ardas and Hukamnama. The occasion reminds Sikhs to carry the wisdom of Gurbani into daily life and continue living according to the Guru's teachings.",

    DisplayLanguage.Hindi to "श्री गुरु ग्रंथ साहिब जी के पाठ की पूर्णता पर किया जाने वाला भोग समारोह कृतज्ञता, आध्यात्मिक संतोष और गुरु की शिक्षाओं पर चिंतन का प्रतीक है। यह पाठ की सफल पूर्णता को दर्शाता है और संगत को अरदास तथा हुकमनामे के माध्यम से मार्गदर्शन प्राप्त करने का अवसर प्रदान करता है। यह अवसर सिखों को गुरबाणी की शिक्षाओं को अपने दैनिक जीवन में अपनाने और गुरु के मार्ग पर चलने की प्रेरणा देता है।",

    DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ ਦੇ ਪਾਠ ਦੀ ਸੰਪੂਰਨਤਾ ਉੱਤੇ ਕੀਤਾ ਜਾਣ ਵਾਲਾ ਭੋਗ ਸਮਾਗਮ ਕ੍ਰਿਤਜਤਾ, ਆਤਮਕ ਤ੍ਰਿਪਤੀ ਅਤੇ ਗੁਰੂ ਦੀਆਂ ਸਿੱਖਿਆਵਾਂ ਉੱਤੇ ਮਨਨ ਦਾ ਪ੍ਰਤੀਕ ਹੈ। ਇਹ ਪਾਠ ਦੀ ਸਫਲ ਸੰਪੂਰਨਤਾ ਨੂੰ ਦਰਸਾਉਂਦਾ ਹੈ ਅਤੇ ਸੰਗਤ ਨੂੰ ਅਰਦਾਸ ਅਤੇ ਹੁਕਮਨਾਮੇ ਰਾਹੀਂ ਮਾਰਗਦਰਸ਼ਨ ਪ੍ਰਾਪਤ ਕਰਨ ਦਾ ਮੌਕਾ ਦਿੰਦਾ ਹੈ। ਇਹ ਮੌਕਾ ਸਿੱਖਾਂ ਨੂੰ ਗੁਰਬਾਣੀ ਦੀ ਸਿਆਣਪ ਨੂੰ ਆਪਣੇ ਰੋਜ਼ਾਨਾ ਜੀਵਨ ਵਿੱਚ ਅਪਣਾਉਣ ਅਤੇ ਗੁਰੂ ਦੀਆਂ ਸਿੱਖਿਆਵਾਂ ਅਨੁਸਾਰ ਜੀਵਨ ਬਿਤਾਉਣ ਦੀ ਪ੍ਰੇਰਣਾ ਦਿੰਦਾ ਹੈ।"
),
"Fill Missing Word" to mapOf(
    DisplayLanguage.English to "Fill Missing Word",
    DisplayLanguage.Hindi to "लुप्त शब्द भरें",
    DisplayLanguage.Punjabi to "ਗੁੰਮ ਹੋਏ ਸ਼ਬਦ ਨੂੰ ਭਰੋ"
),

"Translation Match" to mapOf(
    DisplayLanguage.English to "Translation Match",
    DisplayLanguage.Hindi to "अनुवाद मिलान",
    DisplayLanguage.Punjabi to "ਅਨੁਵਾਦ ਮਿਲਾਓ"
),

"Next Line" to mapOf(
    DisplayLanguage.English to "Next Line",
    DisplayLanguage.Hindi to "अगली ਪंक्ति",
    DisplayLanguage.Punjabi to "ਅਗਲੀ ਪੰਕਤੀ"
),

"True or False" to mapOf(
    DisplayLanguage.English to "True or False",
    DisplayLanguage.Hindi to "सही या गलत",
    DisplayLanguage.Punjabi to "ਸਹੀ ਜਾਂ ਗਲਤ"
),

"Arrange Sequence" to mapOf(
    DisplayLanguage.English to "Arrange Sequence",
    DisplayLanguage.Hindi to "क्रम व्यवस्थित करें",
    DisplayLanguage.Punjabi to "ਕ੍ਰਮ ਅਨੁਸਾਰ ਲਗਾਓ"
),

"Word Meaning" to mapOf(
    DisplayLanguage.English to "Word Meaning",
    DisplayLanguage.Hindi to "शब्द का अर्थ",
    DisplayLanguage.Punjabi to "ਸ਼ਬਦ ਦਾ ਅਰਥ"
),

"Bani Author" to mapOf(
    DisplayLanguage.English to "Bani Author",
    DisplayLanguage.Hindi to "बाणी के रचयिता",
    DisplayLanguage.Punjabi to "ਬਾਣੀ ਦੇ ਰਚਨਹਾਰ"
),

"Bani Ang (Page)" to mapOf(
    DisplayLanguage.English to "Bani Ang (Page)",
    DisplayLanguage.Hindi to "बाणी अंग (पृष्ठ)",
    DisplayLanguage.Punjabi to "ਬਾਣੀ ਅੰਗ (ਪੰਨਾ)"
),

"Great job!" to mapOf(
    DisplayLanguage.English to "Great job!",
    DisplayLanguage.Hindi to "बहुत बढ़िया!",
    DisplayLanguage.Punjabi to "ਸ਼ਾਬਾਸ਼!"
),

"Keep trying" to mapOf(
    DisplayLanguage.English to "Keep trying",
    DisplayLanguage.Hindi to "कोशिश जारी रखें",
    DisplayLanguage.Punjabi to "ਕੋਸ਼ਿਸ਼ ਜਾਰੀ ਰੱਖੋ"
),

"Out of hearts" to mapOf(
    DisplayLanguage.English to "Out of hearts",
    DisplayLanguage.Hindi to "दिल समाप्त हो गए",
    DisplayLanguage.Punjabi to "ਦਿਲ ਖਤਮ ਹੋ ਗਏ"
),



"Q: " to mapOf(
    DisplayLanguage.English to "Q: ",
    DisplayLanguage.Hindi to "प्र: ",
    DisplayLanguage.Punjabi to "ਪ੍ਰ: "
),
"Fill the missing word in the line above" to mapOf(
    DisplayLanguage.English to "Fill the missing word in the line above",
    DisplayLanguage.Hindi to "ऊपर दी गई पंक्ति में लुप्त शब्द भरें",
    DisplayLanguage.Punjabi to "ਉਪਰ ਦਿੱਤੀ ਗਈ ਲਾਈਨ ਵਿੱਚ ਗੁੰਮ ਹੋਏ ਸ਼ਬਦ ਨੂੰ ਭਰੋ"
),

        "Sync Studio" to mapOf(
            DisplayLanguage.Hindi to "सिंक स्टूडियो",
            DisplayLanguage.Punjabi to "ਸਿੰਕ ਸਟੂਡੀਓ"
        ),
        "Tap a line exactly when the reciter begins reading it." to mapOf(
            DisplayLanguage.Hindi to "जब पाठी पंक्ति पढ़ना शुरू करे, ठीक उसी समय टैप करें।",
            DisplayLanguage.Punjabi to "ਜਦੋਂ ਪਾਠੀ ਲਾਈਨ ਪੜ੍ਹਨੀ ਸ਼ੁਰੂ ਕਰੇ, ਠੀਕ ਉਸੇ ਸਮੇਂ ਟੈਪ ਕਰੋ।"
        ),
        "Tap chips below to toggle visible sections" to mapOf(
            DisplayLanguage.Hindi to "दृश्य अनुभागों को टॉगल करने के लिए नीचे दिए गए चिप्स पर टैप करें",
            DisplayLanguage.Punjabi to "ਦਿੱਖ ਭਾਗਾਂ ਨੂੰ ਬਦਲਣ ਲਈ ਹੇਠਾਂ ਦਿੱਤੇ ਚਿਪਸ 'ਤੇ ਟੈਪ ਕਰੋ"
        ),
        "Tap Each Gurbani Line" to mapOf(
            DisplayLanguage.Hindi to "प्रत्येक गुरबाणी पंक्ति पर टैप करें",
            DisplayLanguage.Punjabi to "ਹਰ ਗੁਰਬਾਣੀ ਲਾਈਨ 'ਤੇ ਟੈਪ ਕਰੋ"
        ),
        "Tap words below to arrange..." to mapOf(
            DisplayLanguage.Hindi to "सही क्रम में व्यवस्थित करने के लिए नीचे दिए गए शब्दों पर टैप करें...",
            DisplayLanguage.Punjabi to "ਹੇਠਾਂ ਦਿੱਤੇ ਸ਼ਬਦਾਂ 'ਤੇ ਟੈਪ ਕਰੋ..."
        ),
         "Savaiye" to mapOf(
            DisplayLanguage.Hindi to "सवैये",
            DisplayLanguage.Punjabi to "ਸਵਯੇ"
        ),
           "Savaiya" to mapOf(
            DisplayLanguage.Hindi to "सवैये",
            DisplayLanguage.Punjabi to "ਸਵਯੇ"
        ),
        "Tav Prasad Savaiye" to mapOf(
            DisplayLanguage.Hindi to "तव प्रसाद सवैये",
            DisplayLanguage.Punjabi to "ਤ੍ਵ ਪ੍ਰਸਾਦਿ ਸਵਯੇ"
        ),
        "Tav Prasad Savaiye About" to mapOf(
            DisplayLanguage.English to "A short Bani of 10 stanzas composed by Guru Gobind Singh Ji, part of Akal Ustat in the Dasam Granth.",
            DisplayLanguage.Hindi to "गुरु गोबिंद सिंह जी द्वारा रचित 10 छंदों की एक लघु बाणी, दसम ग्रंथ में अकाल उस्तत का हिस्सा है।",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਗੋਬਿੰਦ ਸਿੰਘ ਜੀ ਦੁਆਰਾ ਉਚਾਰਨ ਕੀਤੇ ਗਏ 10 ਸਵਈਏ, ਜੋ ਦਸਮ ਗ੍ਰੰਥ ਦੀ ਅਕਾਲ ਉਸਤਤਿ ਬਾਣੀ ਦਾ ਹਿੱਸਾ ਹਨ।"
        ),
        "Tav Prasad Savaiye Significance" to mapOf(
            DisplayLanguage.English to "Recited in the morning. It highlights that hollow ritualism without pure love for God is spiritually empty, emphasizing true devotion.",
            DisplayLanguage.Hindi to "सुबह पाठ किया जाता है। यह इस बात पर प्रकाश डालता है कि ईश्वर के प्रति शुद्ध प्रेम के बिना खोखले कर्मकांड आध्यात्मिक रूप से शून्य हैं, और सच्चे प्रेम पर जोर देते हैं।",
            DisplayLanguage.Punjabi to "ਸਵੇਰ ਵੇਲੇ ਪੜ੍ਹੀ ਜਾਂਦੀ ਹੈ। ਇਹ ਸਪੱਸ਼ਟ ਕਰਦੀ ਹੈ ਕਿ ਸੱਚੇ ਪ੍ਰੇਮ ਤੋਂ ਬਿਨਾਂ ਕੀਤੇ ਗਏ ਬਾਹਰੀ ਕਰਮਕਾਂਡ ਵਿਅਰਥ ਹਨ, ਅਤੇ ਕੇਵਲ ਸੱਚਾ ਪ੍ਰੇਮ ਹੀ ਪ੍ਰਭੂ-ਮਿਲਾਪ ਦਾ ਸਾਧਨ ਹੈ।"
        ),
        "Test your knowledge with gamified daily Gurbani questions." to mapOf(
            DisplayLanguage.Hindi to "रोमांचक दैनिक गुरबानी प्रश्नों के साथ अपने ज्ञान का परीक्षण करें।",
            DisplayLanguage.Punjabi to "ਰੋਜ਼ਾਨਾ ਰੋਮਾਂਚਕ ਗੁਰਬਾਣੀ ਪ੍ਰਸ਼ਨਾਂ ਨਾਲ ਆਪਣੇ ਗਿਆਨ ਦੀ ਪਰਖ ਕਰੋ।"
        ),
        "Text Colors" to mapOf(
            DisplayLanguage.Hindi to "पाठ के रंग",
            DisplayLanguage.Punjabi to "ਲਿਖਤ ਦੇ ਰੰਗ"
        ),
        "Theme" to mapOf(
            DisplayLanguage.Hindi to "थीम",
            DisplayLanguage.Punjabi to "ਥੀਮ"
        ),
        "Time" to mapOf(
            DisplayLanguage.Hindi to "समय",
            DisplayLanguage.Punjabi to "ਸਮਾਂ"
        ),
        "Today" to mapOf(
            DisplayLanguage.Hindi to "आज",
            DisplayLanguage.Punjabi to "ਅੱਜ"
        ),
        "Today's Reflection" to mapOf(
            DisplayLanguage.Hindi to "आज का विचार",
            DisplayLanguage.Punjabi to "ਅੱਜ ਦਾ ਵਿਚਾਰ"
        ),
        "Todi" to mapOf(
            DisplayLanguage.Hindi to "टोडी",
            DisplayLanguage.Punjabi to "ਟੋਡੀ"
        ),
        "Track your paath streaks and consistency with daily reminders and notifications to keep you on track." to mapOf(
            DisplayLanguage.Hindi to "आपको ट्रैक पर रखने के लिए दैनिक रिमाइंडर्स और नोटिफिकेशन के साथ अपने पाठ के दैनिक क्रम और निरंतरता को ट्रैक करें।",
            DisplayLanguage.Punjabi to "ਤੁਹਾਨੂੰ ਸਹੀ ਰਸਤੇ 'ਤੇ ਰੱਖਣ ਲਈ ਰੋਜ਼ਾਨਾ ਰੀਮਾਈਂਡਰਾਂ ਅਤੇ ਨੋਟੀਫਿਕੇਸ਼ਨਾਂ ਨਾਲ ਆਪਣੇ ਪਾਠ ਦੇ ਦਿਨਾਂ ਦੇ ਕ੍ਰਮ ਅਤੇ ਲਗਾਤਾਰਤਾ ਨੂੰ ਟ੍ਰੈਕ ਕਰੋ।"
        ),
        "Translation" to mapOf(
            DisplayLanguage.Hindi to "अनुवाद",
            DisplayLanguage.Punjabi to "ਅਨੁਵਾਦ"
        ),
          "Translation:" to mapOf(
            DisplayLanguage.Hindi to "अनुवाद:",
            DisplayLanguage.Punjabi to "ਅਨੁਵਾਦ:"
        ),
        "Translation Size" to mapOf(
            DisplayLanguage.Hindi to "अनुवाद आकार",
            DisplayLanguage.Punjabi to "ਅਨੁਵਾਦ ਆਕਾਰ"
        ),
        "Translations" to mapOf(
            DisplayLanguage.Hindi to "अनुवाद",
            DisplayLanguage.Punjabi to "ਅਨੁਵਾਦ"
        ),
        "Transliteration not available" to mapOf(
            DisplayLanguage.Hindi to "लिप्यंतरण उपलब्ध नहीं है",
            DisplayLanguage.Punjabi to "ਲਿਪੀਅੰਤਰਨ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
        ),
        "True" to mapOf(
            DisplayLanguage.Hindi to "सही",
            DisplayLanguage.Punjabi to "ਸਹੀ"
        ),
        "Tukhari" to mapOf(
            DisplayLanguage.Hindi to "तुखारी",
            DisplayLanguage.Punjabi to "ਤੁਖਾਰੀ"
        ),
        "Undo" to mapOf(
            DisplayLanguage.Hindi to "पूर्ववत करें",
            DisplayLanguage.Punjabi to "ਵਾਪਸ ਲਓ"
        ),
        "Upcoming: Evening Nitnem" to mapOf(
            DisplayLanguage.Hindi to "अगला: शाम का नितनेम",
            DisplayLanguage.Punjabi to "ਅਗਲਾ: ਸ਼ਾਮ ਦਾ ਨਿਤਨੇਮ"
        ),
        "Use Undo or long-press a line to remove incorrect timings." to mapOf(
            DisplayLanguage.Hindi to "गलत टाइमिंग हटाने के लिए अनडू (Undo) का उपयोग करें या पंक्ति को देर तक दबाकर रखें।",
            DisplayLanguage.Punjabi to "ਗਲਤ ਟਾਈਮਿੰਗ ਹਟਾਉਣ ਲਈ ਅਣਡੂ (Undo) ਦੀ ਵਰਤੋਂ ਕਰੋ ਜਾਂ ਲਾਈਨ ਨੂੰ ਦੇਰ ਤੱਕ ਦਬਾ ਕੇ ਰੱਖੋ।"
        ),
        "Visible Content" to mapOf(
            DisplayLanguage.Hindi to "दृश्य सामग्री",
            DisplayLanguage.Punjabi to "ਦਿੱਖ ਸਮੱਗਰੀ"
        ),
        "Wadahans" to mapOf(
            DisplayLanguage.Hindi to "वडहंसु",
            DisplayLanguage.Punjabi to "ਵਡਹੰਸੁ"
        ),
        "Warm appearance" to mapOf(
            DisplayLanguage.Hindi to "गर्म रंग",
            DisplayLanguage.Punjabi to "ਨਿੱਘਾ ਰੰਗ"
        ),
        "What is the meaning / explanation of this line?" to mapOf(
            DisplayLanguage.Hindi to "इस पंक्ति का अर्थ / व्याख्या क्या है?",
            DisplayLanguage.Punjabi to "ਇਸ ਲਾਈਨ ਦਾ ਅਰਥ / ਵਿਆਖਿਆ ਕੀ ਹੈ?"
        ),
        "What is the starting Ang (Page) of this Bani?" to mapOf(
            DisplayLanguage.Hindi to "इस बाणी का शुरुआती अंग (पृष्ठ) क्या है?",
            DisplayLanguage.Punjabi to "ਇਸ ਬਾਣੀ ਦਾ ਸ਼ੁਰੂਆਤੀ ਅੰਗ (ਪੰਨਾ) ਕੀ ਹੈ?"
        ),
        "Who is the author / writer of this Bani?" to mapOf(
            DisplayLanguage.Hindi to "इस बाणी के रचयिता कौन हैं?",
            DisplayLanguage.Punjabi to "ਇਸ ਬਾਣੀ ਦੇ ਲਿਖਾਰੀ / ਰਚਨਹਾਰ ਕੌਣ ਹਨ?"
        ),
        "Widget Content" to mapOf(
            DisplayLanguage.Hindi to "विजेट सामग्री",
            DisplayLanguage.Punjabi to "ਵਿਜੇਟ ਸਮੱਗਰੀ"
        ),
        "Widget Theme" to mapOf(
            DisplayLanguage.Hindi to "विजेट थीम",
            DisplayLanguage.Punjabi to "ਵਿਜੇਟ ਥੀਮ"
        ),
        "Word Meanings" to mapOf(
            DisplayLanguage.Hindi to "शब्दार्थ",
            DisplayLanguage.Punjabi to "ਸ਼ਬਦਾਰਥ"
        ),
        "Word meanings not available" to mapOf(
            DisplayLanguage.Hindi to "शब्दार्थ उपलब्ध नहीं हैं",
            DisplayLanguage.Punjabi to "ਸ਼ਬਦਾਰਥ ਉਪਲਬਧ ਨਹੀਂ ਹਨ"
        ),
        "Words" to mapOf(
            DisplayLanguage.Hindi to "शब्द",
            DisplayLanguage.Punjabi to "ਸ਼ਬਦ"
        ),
        "You earned %d XP" to mapOf(
            DisplayLanguage.Hindi to "आपने %d XP अर्जित किए",
            DisplayLanguage.Punjabi to "ਤੁਸੀਂ %d XP ਪ੍ਰਾਪਤ ਕੀਤੇ"
        ),
        "Your journey continues today." to mapOf(
            DisplayLanguage.Hindi to "आपकी यात्रा आज भी जारी है।",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡੀ ਯਾਤਰਾ ਅੱਜ ਵੀ ਜਾਰੀ ਹੈ।"
        ),
        "Your Nitnem experience has been prepared." to mapOf(
            DisplayLanguage.Hindi to "आपका नितनेम अनुभव तैयार कर दिया गया है।",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡਾ ਨਿਤਨੇਮ ਅਨੁਭਵ ਤਿਆਰ ਕਰ ਦਿੱਤਾ ਗਿਆ ਹੈ।"
        ),
        "Your Paath" to mapOf(
            DisplayLanguage.Hindi to "आपका पाठ",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡਾ ਪਾਠ"
        ),
        "Your synced timing file will automatically power live highlighting in Gurbani Experience mode." to mapOf(
            DisplayLanguage.Hindi to "आपकी सिंक की गई टाइमिंग फ़ाइल गुरबाणी अनुभव मोड में अपने आप लाइव हाइलाइटिंग शुरू कर देगी।",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡੀ ਸਿੰਕ ਕੀਤੀ ਟਾਈਮਿੰਗ ਫਾਈਲ ਗੁਰਬਾਣੀ ਅਨੁਭਵ ਮੋਡ ਵਿੱਚ ਆਪਣੇ ਆਪ ਲਾਈਵ ਹਾਈਲਾਈਟਿੰਗ ਨੂੰ ਚਲਾਏਗੀ।"
        ),
        "Sign in with Google" to mapOf(
            DisplayLanguage.Hindi to "Google से साइन इन करें",
            DisplayLanguage.Punjabi to "Google ਨਾਲ ਸਾਈਨ ਇਨ ਕਰੋ"
        ),
        "Continue Without Login" to mapOf(
            DisplayLanguage.Hindi to "बिना लॉगिन जारी रखें",
            DisplayLanguage.Punjabi to "ਬਿਨਾਂ ਲੌਗਇਨ ਜਾਰੀ ਰੱਖੋ"
        ),
        "Continue Offline / Private Mode" to mapOf(
            DisplayLanguage.Hindi to "ऑफ़लाइन / निजी मोड जारी रखें",
            DisplayLanguage.Punjabi to "ਔਫਲਾਈਨ / ਨਿੱਜੀ ਮੋਡ ਜਾਰੀ ਰੱਖੋ"
        ),
        "Account & Cloud Sync" to mapOf(
            DisplayLanguage.Hindi to "खाता और क्लाउड सिंक",
            DisplayLanguage.Punjabi to "ਖਾਤਾ ਅਤੇ ਕਲਾਉਡ ਸਿੰਕ"
        ),
        "Cloud backup & community features" to mapOf(
            DisplayLanguage.Hindi to "क्लाउड बैकअप और संगत सुविधाएं",
            DisplayLanguage.Punjabi to "ਕਲਾਉਡ ਬੈਕਅੱਪ ਅਤੇ ਸੰਗਤ ਵਿਸ਼ੇਸ਼ਤਾਵਾਂ"
        ),
        "Automatic data & streak restoration across all devices" to mapOf(
            DisplayLanguage.Hindi to "सभी डिवाइसों पर स्वचालित डेटा और लड़ी (स्ट्रीक) पुनर्स्थापना",
            DisplayLanguage.Punjabi to "ਸਾਰੇ ਡਿਵਾਈਸਾਂ 'ਤੇ ਆਟੋਮੈਟਿਕ ਡੇਟਾ ਅਤੇ ਸਟ੍ਰੀਕ ਰੀਸਟੋਰ"
        ),
        "Keep track of friends & family in real-time Sangat leaderboards" to mapOf(
            DisplayLanguage.Hindi to "रीयल-टाइम संगत लीडरबोर्ड में मित्रों और परिवार से जुड़े रहें",
            DisplayLanguage.Punjabi to "ਰੀਅਲ-ਟਾਈਮ ਸੰਗਤ ਲੀਡਰਬੋਰਡਾਂ ਵਿੱਚ ਦੋਸਤਾਂ ਅਤੇ ਪਰਿਵਾਰ ਨਾਲ ਜੁੜੇ ਰਹੋ"
        ),
        "Sync recitation minutes, XP milestones & prayer history" to mapOf(
            DisplayLanguage.Hindi to "पाठ के मिनट, XP उपलब्धियां और प्रार्थना इतिहास सिंक करें",
            DisplayLanguage.Punjabi to "ਪਾਠ ਦੇ ਮਿੰਟ, XP ਪ੍ਰਾਪਤੀਆਂ ਅਤੇ ਇਤਿਹਾਸ ਸਿੰਕ ਕਰੋ"
        ),
        "100% Private, offline-first experience" to mapOf(
            DisplayLanguage.Hindi to "100% निजी, ऑफ़लाइन-प्रथम अनुभव",
            DisplayLanguage.Punjabi to "100% ਨਿੱਜੀ, ਔਫਲਾਈਨ ਅਨੁਭਵ"
        ),
        "100% Privacy with 0 cloud tracking or personal data collection" to mapOf(
            DisplayLanguage.Hindi to "शून्य क्लाउड ट्रैकिंग और संपूर्ण डेटा गोपनीयता",
            DisplayLanguage.Punjabi to "ਜ਼ੀਰੋ ਕਲਾਉਡ ਟਰੈਕਿੰਗ ਅਤੇ ਪੂਰੀ ਡੇਟਾ ਗੋਪਨੀਯਤਾ"
        ),
        "Works entirely offline — all bookmarks & streaks saved locally on your phone" to mapOf(
            DisplayLanguage.Hindi to "पूरी तरह से ऑफ़लाइन — सभी बुकमार्क और स्ट्रीक फोन पर सुरक्षित",
            DisplayLanguage.Punjabi to "ਪੂਰੀ ਤਰ੍ਹਾਂ ਔਫਲਾਈਨ — ਸਾਰੇ ਬੁੱਕਮਾਰਕ ਅਤੇ ਸਟ੍ਰੀਕ ਫ਼ੋਨ 'ਤੇ ਸੁਰੱਖਿਅਤ"
        ),
        "No account, email, or credentials required to read or listen" to mapOf(
            DisplayLanguage.Hindi to "पढ़ने या सुनने के लिए किसी खाते या ईमेल की आवश्यकता नहीं",
            DisplayLanguage.Punjabi to "ਪੜ੍ਹਨ ਜਾਂ ਸੁਣਨ ਲਈ ਕਿਸੇ ਖਾਤੇ ਜਾਂ ਈਮੇਲ ਦੀ ਲੋੜ ਨਹੀਂ"
        ),
        "Black & White AOD Mode" to mapOf(
            DisplayLanguage.Hindi to "ब्लैक एंड व्हाइट AOD मोड",
            DisplayLanguage.Punjabi to "ਬਲੈਕ ਐਂਡ ਵ੍ਹਾਈਟ AOD ਮੋਡ"
        ),
        "Ultra-minimalist AOD mode: pure black background, crisp white text, and only essential elements on screen in Reading & Meditation views." to mapOf(
            DisplayLanguage.Hindi to "अल्ट्रा-मिनिमलिस्ट AOD मोड: शुद्ध काला बैकग्राउंड, स्पष्ट सफेद पाठ और स्क्रीन पर केवल आवश्यक तत्व।",
            DisplayLanguage.Punjabi to "ਅਲਟਰਾ-ਮਿਨੀਮਲਿਸਟ AOD ਮੋਡ: ਸ਼ੁੱਧ ਕਾਲਾ ਬੈਕਗ੍ਰਾਊਂਡ, ਸਪਸ਼ਟ ਚਿੱਟਾ ਟੈਕਸਟ ਅਤੇ ਸਕ੍ਰੀਨ 'ਤੇ ਸਿਰਫ਼ ਜ਼ਰੂਰੀ ਤੱਤ।"
        ),
        "Full-Screen Reading Mode" to mapOf(
            DisplayLanguage.Hindi to "फुल-स्क्रीन पठन मोड",
            DisplayLanguage.Punjabi to "ਫੁੱਲ-ਸਕ੍ਰੀਨ ਪਾਠ ਮੋਡ"
        ),
        "Auto-hides top header & bottom bar after 4s in Gurbani & Meditation views. Tap screen to restore." to mapOf(
            DisplayLanguage.Hindi to "गुरबाणी और ध्यान दृश्यों में 4 सेकंड के बाद हेडर और बॉटम बार स्वतः छिप जाते हैं। वापस लाने के लिए स्क्रीन पर टैप करें।",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਅਤੇ ਅਭਿਆਸ ਦ੍ਰਿਸ਼ਾਂ ਵਿੱਚ 4 ਸਕਿੰਟਾਂ ਬਾਅਦ ਹੈਡਰ ਅਤੇ ਹੇਠਲੀ ਪੱਟੀ ਆਪਣੇ ਆਪ ਲੁਕ ਜਾਂਦੇ ਹਨ। ਮੁੜ ਲਿਆਉਣ ਲਈ ਸਕ੍ਰੀਨ 'ਤੇ ਟੈਪ ਕਰੋ।"
        ),
        "Audio & Timing Asset Manager" to mapOf(
            DisplayLanguage.Hindi to "ऑडियो और टाइमिंग एसेट प्रबंधक",
            DisplayLanguage.Punjabi to "ਆਡੀਓ ਅਤੇ ਟਾਈਮਿੰਗ ਐਸੇਟ ਮੈਨੇਜਰ"
        ),
        "Batch download high quality audio & sync files from GitHub repository for offline listening" to mapOf(
            DisplayLanguage.Hindi to "ऑफ़लाइन सुनने के लिए GitHub रिपॉजिटरी से उच्च गुणवत्ता वाले ऑडियो और सिंक फ़ाइलें डाउनलोड करें",
            DisplayLanguage.Punjabi to "ਔਫਲਾਈਨ ਸੁਣਨ ਲਈ GitHub ਰਿਪੋਜ਼ਟਰੀ ਤੋਂ ਉੱਚ ਗੁਣਵੱਤਾ ਵਾਲੇ ਆਡੀਓ ਅਤੇ ਸਿੰਕ ਫਾਈਲਾਂ ਡਾਊਨਲੋਡ ਕਰੋ"
        ),
        "Audio & Timing Manager" to mapOf(
            DisplayLanguage.Hindi to "ऑडियो और टाइमिंग प्रबंधक",
            DisplayLanguage.Punjabi to "ਆਡੀਓ ਅਤੇ ਟਾਈਮਿੰਗ ਮੈਨੇਜਰ"
        ),
        "On-Demand GitHub Downloads & Stream" to mapOf(
            DisplayLanguage.Hindi to "ऑन-डिमांड GitHub डाउनलोड और लाइव स्ट्रीम",
            DisplayLanguage.Punjabi to "ਆਨ-ਡਿਮਾਂਡ GitHub ਡਾਊਨਲੋਡ ਅਤੇ ਲਾਈਵ ਸਟ੍ਰੀਮ"
        ),
        "Daily Nitnem" to mapOf(
            DisplayLanguage.Hindi to "दैनिक नितनेम",
            DisplayLanguage.Punjabi to "ਰੋਜ਼ਾਨਾ ਨਿਤਨੇਮ"
        ),
        "All Banis" to mapOf(
            DisplayLanguage.Hindi to "सभी बाणियां",
            DisplayLanguage.Punjabi to "ਸਾਰੀਆਂ ਬਾਣੀਆਂ"
        ),
        "Download Selected" to mapOf(
            DisplayLanguage.Hindi to "चयनित डाउनलोड करें",
            DisplayLanguage.Punjabi to "ਚੁਣੇ ਹੋਏ ਡਾਊਨਲੋਡ ਕਰੋ"
        ),
        "Online Stream" to mapOf(
            DisplayLanguage.Hindi to "ऑनलाइन स्ट्रीम",
            DisplayLanguage.Punjabi to "ਔਨਲਾਈਨ ਸਟ੍ਰੀਮ"
        ),
        "Live Timing Sync available" to mapOf(
            DisplayLanguage.Hindi to "लाइव टाइमिंग सिंक उपलब्ध",
            DisplayLanguage.Punjabi to "ਲਾਈਵ ਟਾਈਮਿੰਗ ਸਿੰਕ ਉਪਲਬਧ"
        ),
        "Nitnem Daily Journey" to mapOf(
            DisplayLanguage.Hindi to "नितनेम दैनिक यात्रा",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਰੋਜ਼ਾਨਾ ਯਾਤਰਾ"
        ),
        "Nitnem Completed!" to mapOf(
            DisplayLanguage.Hindi to "नितनेम पूर्ण हुआ!",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਸੰਪੂਰਨ ਹੋਇਆ!"
        ),
        "Banis Completed" to mapOf(
            DisplayLanguage.Hindi to "बाणियां पूर्ण हुईं",
            DisplayLanguage.Punjabi to "ਬਾਣੀਆਂ ਸੰਪੂਰਨ"
        ),
        "Waheguru Ji! You Won Today's Journey!" to mapOf(
            DisplayLanguage.Hindi to "वाहेगुरु जी! आपने आज की यात्रा पूर्ण की!",
            DisplayLanguage.Punjabi to "ਵਾਹਿਗੁਰੂ ਜੀ! ਤੁਸੀਂ ਅੱਜ ਦੀ ਯਾਤਰਾ ਜਿੱਤ ਲਈ!"
        ),
        "All 7 Banis recited today. Blissful peace & blessings!" to mapOf(
            DisplayLanguage.Hindi to "आज सभी 7 बाणियों का पाठ संपन्न हुआ। अनहद शांति और कृपा!",
            DisplayLanguage.Punjabi to "ਅੱਜ ਸਾਰੀਆਂ 7 ਬਾਣੀਆਂ ਦਾ ਪਾਠ ਸੰਪੂਰਨ ਹੋਇਆ। ਅਨੰਦ ਅਤੇ ਸ਼ਾਂਤੀ!"
        ),
        "Climb the Nitnem path step by step towards peace." to mapOf(
            DisplayLanguage.Hindi to "शांति की ओर नितनेम मार्ग पर कदम दर कदम आगे बढ़ें।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤੀ ਵੱਲ ਨਿਤਨੇਮ ਮਾਰਗ 'ਤੇ ਕਦਮ ਦਰ ਕਦਮ ਅੱਗੇ ਵਧੋ।"
        ),
        "Earned Today" to mapOf(
            DisplayLanguage.Hindi to "आज अर्जित",
            DisplayLanguage.Punjabi to "ਅੱਜ ਕਮਾਏ"
        ),
        "Waheguru" to mapOf(
            DisplayLanguage.Hindi to "वाहेगुरु",
            DisplayLanguage.Punjabi to "ਵਾਹਿਗੁਰੂ"
        ),
        "Continue Paath" to mapOf(
            DisplayLanguage.Hindi to "पाठ जारी रखें",
            DisplayLanguage.Punjabi to "ਪਾਠ ਜਾਰੀ ਰੱਖੋ"
        ),
        "Visible Content" to mapOf(
            DisplayLanguage.Hindi to "दृश्य सामग्री",
            DisplayLanguage.Punjabi to "ਦ੍ਰਿਸ਼ਮਾਨ ਸਮੱਗਰੀ"
        ),
        "Text Colors" to mapOf(
            DisplayLanguage.Hindi to "पाठ रंग",
            DisplayLanguage.Punjabi to "ਟੈਕਸਟ ਰੰਗ"
        ),
        "Display" to mapOf(
            DisplayLanguage.Hindi to "डिस्प्ले",
            DisplayLanguage.Punjabi to "ਡਿਸਪਲੇ"
        ),
        "ਨਿਤਨੇਮ ਗੁਟਕਾ ਸਾਹਿਬ" to mapOf(
            DisplayLanguage.English to "Nitnem Gutka Sahib",
            DisplayLanguage.Hindi to "नितनेम गुटका साहिब",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਗੁਟਕਾ ਸਾਹਿਬ"
        ),
        "ਨਿਤਨੇਮ ਗੁਟਕਾ ਟੀਕਾ" to mapOf(
            DisplayLanguage.English to "Nitnem Gutka Steek",
            DisplayLanguage.Hindi to "नितनेम गुटका टीका",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਗੁਟਕਾ ਟੀਕਾ"
        ),
        "ੴ ਸਤਿਨਾਮੁ ਵਾਹਿਗੁਰੂ" to mapOf(
            DisplayLanguage.English to "Ek Onkar Satnam Waheguru",
            DisplayLanguage.Hindi to "ੴ सतनाम वाहेगुरु",
            DisplayLanguage.Punjabi to "ੴ ਸਤਿਨਾਮੁ ਵਾਹਿਗੁਰੂ"
        ),
        "Repeat One" to mapOf(
            DisplayLanguage.Hindi to "एक दोहराएं",
            DisplayLanguage.Punjabi to "ਇੱਕ ਦੁਹਰਾਓ"
        ),
        "Repeat All" to mapOf(
            DisplayLanguage.Hindi to "सभी दोहराएं",
            DisplayLanguage.Punjabi to "ਸਾਰੇ ਦੁਹਰਾਓ"
        ),
        "Off" to mapOf(
            DisplayLanguage.Hindi to "बंद",
            DisplayLanguage.Punjabi to "ਬੰਦ"
        ),
        "Real-time Leaderboard & Streaks" to mapOf(
            DisplayLanguage.Hindi to "रीयल-टाइम लीडरबोर्ड और स्ट्रीक्स",
            DisplayLanguage.Punjabi to "ਰੀਅਲ-ਟਾਈਮ ਲੀਡਰਬੋਰਡ ਅਤੇ ਸਟ੍ਰੀਕਸ"
        ),
        "Sangat Member Rankings" to mapOf(
            DisplayLanguage.Hindi to "संगत सदस्य रैंकिंग",
            DisplayLanguage.Punjabi to "ਸੰਗਤ ਮੈਂਬਰ ਦਰਜਾਬੰਦੀ"
        ),
        "Transliteration not available" to mapOf(
            DisplayLanguage.Hindi to "लिप्यंतरण उपलब्ध नहीं है",
            DisplayLanguage.Punjabi to "ਲਿਪੀਅੰਤਰਨ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
        ),
        "Hindi Transliteration not available" to mapOf(
            DisplayLanguage.Hindi to "हिंदी लिप्यंतरण उपलब्ध नहीं है",
            DisplayLanguage.Punjabi to "ਹਿੰਦੀ ਲਿਪੀਅੰਤਰਨ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
        ),
        "English translation not available" to mapOf(
            DisplayLanguage.Hindi to "अंग्रेजी अनुवाद उपलब्ध नहीं है",
            DisplayLanguage.Punjabi to "ਅੰਗਰੇਜ਼ੀ ਅਨੁਵਾਦ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
        ),
        "Punjabi translation not available" to mapOf(
            DisplayLanguage.Hindi to "पंजाबी अनुवाद उपलब्ध नहीं है",
            DisplayLanguage.Punjabi to "ਪੰਜਾਬੀ ਅਨੁਵਾਦ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
        ),
        "Signed in as" to mapOf(
            DisplayLanguage.Hindi to "के रूप में साइन इन हैं",
            DisplayLanguage.Punjabi to "ਵਜੋਂ ਸਾਈਨ ਇਨ ਕੀਤਾ ਗਿਆ"
        ),
        "Sign In Error" to mapOf(
            DisplayLanguage.Hindi to "साइन इन त्रुटि",
            DisplayLanguage.Punjabi to "ਸਾਈਨ ਇਨ ਗਲਤੀ"
        ),
        "Signed out successfully" to mapOf(
            DisplayLanguage.Hindi to "सफलतापूर्वक साइन आउट किया गया",
            DisplayLanguage.Punjabi to "ਸਫਲਤਾਪੂਰਵਕ ਸਾਈਨ ਆਊਟ ਹੋ ਗਏ"
        ),
        "Suggested questions" to mapOf(
            DisplayLanguage.Hindi to "सुझाए गए प्रश्न",
            DisplayLanguage.Punjabi to "ਸੁਝਾਏ ਗਏ ਸਵਾਲ"
        ),
        "What does Japji Sahib teach?" to mapOf(
            DisplayLanguage.Hindi to "जपजी साहिब क्या सिखाता है?",
            DisplayLanguage.Punjabi to "ਜਪੁਜੀ ਸਾਹਿਬ ਕੀ ਸਿਖਾਉਂਦਾ ਹੈ?"
        ),
        "What is the meaning of Waheguru?" to mapOf(
            DisplayLanguage.Hindi to "वाहेगुरु का क्या अर्थ है?",
            DisplayLanguage.Punjabi to "ਵਾਹਿਗੁਰੂ ਦਾ ਕੀ ਅਰਥ ਹੈ?"
        ),
        "What is the opening line of Jaap Sahib?" to mapOf(
            DisplayLanguage.Hindi to "जाप साहिब की शुरुआती पंक्ति क्या है?",
            DisplayLanguage.Punjabi to "ਜਾਪੁ ਸਾਹਿਬ ਦੀ ਸ਼ੁਰੂਆਤੀ ਪੰਕਤੀ ਕੀ ਹੈ?"
        ),
        "What is Amrit Vela?" to mapOf(
            DisplayLanguage.Hindi to "अमृत वेला क्या है?",
            DisplayLanguage.Punjabi to "ਅੰਮ੍ਰਿਤ ਵੇਲਾ ਕੀ ਹੈ?"
        ),
        "Who composed Chaupai Sahib?" to mapOf(
            DisplayLanguage.Hindi to "चौपाई साहिब की रचना किसने की?",
            DisplayLanguage.Punjabi to "ਚੌਪਈ ਸਾਹਿਬ ਦੀ ਰਚਨਾ ਕਿਸਨੇ ਕੀਤੀ?"
        ),
        "What does Rehras Sahib celebrate?" to mapOf(
            DisplayLanguage.Hindi to "रह्रास साहिब क्या संदेश देता है?",
            DisplayLanguage.Punjabi to "ਰਹਿਰਾਸ ਸਾਹਿਬ ਕੀ ਸੰਦੇਸ਼ ਦਿੰਦਾ ਹੈ?"
        ),
        "Verse matches (tap to view meanings):" to mapOf(
            DisplayLanguage.Hindi to "पंक्ति मिलान (अर्थ देखने के लिए टैप करें):",
            DisplayLanguage.Punjabi to "ਪੰਕਤੀ ਮਿਲਾਨ (ਅਰਥ ਵੇਖਣ ਲਈ ਟੈਪ ਕਰੋ):"
        ),
        "Ask about Gurbani..." to mapOf(
            DisplayLanguage.Hindi to "गुरबाणी के बारे में पूछें...",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਬਾਰੇ ਪੁੱਛੋ..."
        ),
        "Listening to voice run..." to mapOf(
            DisplayLanguage.Hindi to "आवाज़ सुनी जा रही है...",
            DisplayLanguage.Punjabi to "ਆਵਾਜ਼ ਸੁਣੀ ਜਾ ਰਹੀ ਹੈ..."
        ),
        "Nitnem Daily Journey" to mapOf(
            DisplayLanguage.Hindi to "दैनिक नितनेम यात्रा",
            DisplayLanguage.Punjabi to "ਰੋਜ਼ਾਨਾ ਨਿਤਨੇਮ ਯਾਤਰਾ"
        ),
        "Nitnem Completed!" to mapOf(
            DisplayLanguage.Hindi to "नितनेम पूरा हुआ!",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਪੂਰਾ ਹੋਇਆ!"
        ),
        "Banis Completed" to mapOf(
            DisplayLanguage.Hindi to "बाणियां पूर्ण हुईं",
            DisplayLanguage.Punjabi to "ਬਾਣੀਆਂ ਸੰਪੂਰਨ ਹੋਈਆਂ"
        ),
        "Waheguru Ji! You Won Today's Journey!" to mapOf(
            DisplayLanguage.Hindi to "वाहेगुरु जी! आपने आज की यात्रा पूर्ण की!",
            DisplayLanguage.Punjabi to "ਵਾਹਿਗੁਰੂ ਜੀ! ਤੁਸੀਂ ਅੱਜ ਦੀ ਯਾਤਰਾ ਜਿੱਤ ਲਈ ਹੈ!"
        ),
        "All 7 Banis recited today. Blissful peace & blessings!" to mapOf(
            DisplayLanguage.Hindi to "आज सभी 7 बाणियों का पाठ किया गया। असीम शांति और कृपा!",
            DisplayLanguage.Punjabi to "ਅੱਜ ਸਾਰੀਆਂ ੭ ਬਾਣੀਆਂ ਦਾ ਪਾਠ ਕੀਤਾ ਗਿਆ। ਅਥਾਹ ਸ਼ਾਂਤੀ ਅਤੇ ਬਖਸ਼ਿਸ਼!"
        ),
        "Climb the Nitnem path step by step towards peace." to mapOf(
            DisplayLanguage.Hindi to "शांति की ओर नितनेम पथ पर कदम दर कदम आगे बढ़ें।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤੀ ਵੱਲ ਨਿਤਨੇਮ ਦੇ ਮਾਰਗ 'ਤੇ ਕਦਮ-ਦਰ-ਕਦਮ ਅੱਗੇ ਵਧੋ।"
        ),
        "Advancing on Spiritual Ladder" to mapOf(
            DisplayLanguage.Hindi to "आध्यात्मिक सीढ़ी पर अग्रसर",
            DisplayLanguage.Punjabi to "ਰੂਹਾਨੀ ਪੌੜੀ 'ਤੇ ਅੱਗੇ ਵਧ ਰਹੇ ਹੋ"
        ),
        "Anand Sahib & Ardas" to mapOf(
            DisplayLanguage.Hindi to "आनंद साहिब व अरदास",
            DisplayLanguage.Punjabi to "ਅਨੰਦੁ ਸਾਹਿਬ ਅਤੇ ਅਰਦਾਸ"
        ),
        "Rehras & Ardas" to mapOf(
            DisplayLanguage.Hindi to "रह्रास साहिब व अरदास",
            DisplayLanguage.Punjabi to "ਰਹਿਰਾਸ ਸਾਹਿਬ ਅਤੇ ਅਰਦਾਸ"
        ),
        "Bonus!" to mapOf(
            DisplayLanguage.Hindi to "बोनस!",
            DisplayLanguage.Punjabi to "ਬੋਨਸ!"
        ),
        "Per Bani" to mapOf(
            DisplayLanguage.Hindi to "प्रति बाणी",
            DisplayLanguage.Punjabi to "ਪ੍ਰਤੀ ਬਾਣੀ"
        ),
        "Meet Bani" to mapOf(
            DisplayLanguage.Hindi to "मिलिए बाणी से",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਨਾਲ ਮਿਲੋ"
        ),
        "In the quiet, sacred hours of Amrit Vela, a small golden bird named Bani wakes up — her wings painted with the warm orange hues of a breaking dawn, carrying an ancient crimson Gutka Sahib close to her chest." to mapOf(
            DisplayLanguage.Hindi to "अमृत वेले के शांत और पवित्र पहर में, बाणी नाम की एक नन्ही सुनहरी चिड़िया जागती है — उसके पंख भोर की सुनहरी लाली से सजे हैं, और वह अपने सीने से एक पावन गुटका साहिब लगाए रखती है।",
            DisplayLanguage.Punjabi to "ਅੰਮ੍ਰਿਤ ਵੇਲੇ ਦੇ ਸ਼ਾਂਤ ਅਤੇ ਪਵਿੱਤਰ ਪਹਿਰ ਵਿੱਚ, ਬਾਣੀ ਨਾਮ ਦੀ ਇੱਕ ਨਿੱਕੀ ਸੁਨਹਿਰੀ ਚਿੜੀ ਜਾਗਦੀ ਹੈ — ਉਸਦੇ ਖੰਭ ਪ੍ਰਭਾਤ ਦੀ ਨਿੱਘੀ ਲਾਲੀ ਨਾਲ ਰੰਗੇ ਹੋਏ ਹਨ ਅਤੇ ਉਹ ਆਪਣੇ ਹਿਰਦੇ ਨਾਲ ਇੱਕ ਪਾਵਨ ਗੁਟਕਾ ਸਾਹਿਬ ਲਗਾਈ ਰੱਖਦੀ ਹੈ।"
        ),
        "Your Guardian of Consistency" to mapOf(
            DisplayLanguage.Hindi to "आपकी निरंतरता की संरक्षिका",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡੀ ਨਿਰੰਤਰਤਾ ਦੀ ਰਾਖੀ"
        ),
        "Bani's entire purpose is to guard mindfulness and consistency. She flew from her digital sanctuary to sit right on your home screen — ready to guide you through your daily Nitnem, one line at a time." to mapOf(
            DisplayLanguage.Hindi to "बाणी का मुख्य उद्देश्य आपकी एकाग्रता और निरंतरता का ध्यान रखना है। वह आपके दैनिक नितनेम में आपका मार्गदर्शन करने के लिए सीधे आपकी होम स्क्रीन पर आकर बैठती है।",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਦਾ ਮਕਸਦ ਤੁਹਾਡੀ ਇਕਾਗਰਤਾ ਅਤੇ ਲਗਾਤਾਰਤਾ ਦੀ ਰਾਖੀ ਕਰਨਾ ਹੈ। ਉਹ ਤੁਹਾਡੀ ਹੋਮ ਸਕ੍ਰੀਨ 'ਤੇ ਆਣ ਬੈਠਦੀ ਹੈ — ਤੁਹਾਨੂੰ ਰੋਜ਼ਾਨਾ ਨਿਤਨੇਮ ਦੀ ਹਰ ਤੁਕ ਵਿੱਚ ਅਗਵਾਈ ਦੇਣ ਲਈ।"
        ),
        "A Joyful Companion" to mapOf(
            DisplayLanguage.Hindi to "एक आनंदमयी साथी",
            DisplayLanguage.Punjabi to "ਇੱਕ ਅਨੰਦਮਈ ਸਾਥੀ"
        ),
        "Whenever you open your Gutka Sahib with a smile, Bani flutters her golden wings with pure joy, cheering you on as you read each sacred verse of Gurbani." to mapOf(
            DisplayLanguage.Hindi to "जब भी आप मुस्कान के साथ गुटका साहिब खोलते हैं, बाणी खुशी से अपने सुनहरे पंख फड़फड़ाती है और गुरबाणी की प्रत्येक पावन पंक्ति पढ़ते समय आपका उत्साह बढ़ाती है।",
            DisplayLanguage.Punjabi to "ਜਦੋਂ ਵੀ ਤੁਸੀਂ ਮੁਸਕਰਾਹਟ ਨਾਲ ਗੁਟਕਾ ਸਾਹਿਬ ਖੋਲ੍ਹਦੇ ਹੋ, ਬਾਣੀ ਪੂਰੇ ਚਾਅ ਨਾਲ ਆਪਣੇ ਸੁਨਹਿਰੇ ਖੰਭ ਫੜਫੜਾਉਂਦੀ ਹੈ ਅਤੇ ਗੁਰਬਾਣੀ ਦੀ ਹਰ ਪਾਵਨ ਤੁਕ ਪੜ੍ਹਨ 'ਤੇ ਤੁਹਾਡਾ ਉਤਸ਼ਾਹ ਵਧਾਉਂਦੀ ਹੈ।"
        ),
        "Exploring Meanings" to mapOf(
            DisplayLanguage.Hindi to "अर्थों की खोज",
            DisplayLanguage.Punjabi to "ਅਰਥਾਂ ਦੀ ਖੋਜ"
        ),
        "Wondering what a line of Gurbani means? Bani taps her tiny beak in deep thought, looking into her magical library to find the translation and word references just for you." to mapOf(
            DisplayLanguage.Hindi to "सोच रहे हैं कि गुरबाणी की किसी पंक्ति का क्या अर्थ है? बाणी सोच-विचार कर आपके लिए सही अनुवाद और शब्दार्थ खोज लाती है।",
            DisplayLanguage.Punjabi to "ਸੋਚ ਰਹੇ ਹੋ ਕਿ ਗੁਰਬਾਣੀ ਦੀ ਕਿਸੇ ਤੁਕ ਦਾ ਕੀ ਅਰਥ ਹੈ? ਬਾਣੀ ਡੂੰਘੇ ਵਿਚਾਰ ਵਿੱਚ ਆਪਣੀ ਚੁੰਝ ਛੁਹਾਉਂਦੀ ਹੈ ਅਤੇ ਤੁਹਾਡੇ ਲਈ ਸਹੀ ਅਰਥ ਅਤੇ ਵਿਆਖਿਆ ਲੱਭ ਕੇ ਲਿਆਉਂਦੀ ਹੈ।"
        ),
        "Peaceful Focus" to mapOf(
            DisplayLanguage.Hindi to "शांत एकाग्रता",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤ ਇਕਾਗਰਤਾ"
        ),
        "When it is time for Simran, Bani closes her eyes and sits in quiet meditation, helping your heart find calmness, peace, and deep concentration." to mapOf(
            DisplayLanguage.Hindi to "जब सिमरन का समय होता है, बाणी अपनी आंखें बंद करके शांत ध्यान में बैठ जाती है, जिससे आपके मन को शांति, सुकून और गहरा ध्यान प्राप्त होता है।",
            DisplayLanguage.Punjabi to "ਜਦੋਂ ਸਿਮਰਨ ਦਾ ਵੇਲਾ ਹੁੰਦਾ ਹੈ, ਬਾਣੀ ਅੱਖਾਂ ਮੀਟ ਕੇ ਸ਼ਾਂਤ ਧਿਆਨ ਵਿੱਚ ਬੈਠ ਜਾਂਦੀ ਹੈ, ਜਿਸ ਨਾਲ ਤੁਹਾਡੇ ਹਿਰਦੇ ਨੂੰ ਠੰਢ, ਸ਼ਾਂਤੀ ਅਤੇ ਡੂੰਘੀ ਇਕਾਗਰਤਾ ਮਿਲਦੀ ਹੈ।"
        ),
        "Listening to Your Voice" to mapOf(
            DisplayLanguage.Hindi to "आपकी आवाज़ सुनना",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡੀ ਆਵਾਜ਼ ਸੁਣਨਾ"
        ),
        "Bani cups her tiny ear to listen closely when you use your voice or practice reciting your prayers out loud, making sure every word is heard with love." to mapOf(
            DisplayLanguage.Hindi to "जब आप बोलकर पाठ करते हैं, तो बाणी ध्यान से सुनने के लिए अपने कान लगाती है, ताकि हर शब्द प्यार और आदर से सुना जा सके।",
            DisplayLanguage.Punjabi to "ਜਦੋਂ ਤੁਸੀਂ ਬੋਲ ਕੇ ਪਾਠ ਕਰਦੇ ਹੋ, ਬਾਣੀ ਧਿਆਨ ਨਾਲ ਸੁਣਨ ਲਈ ਕੰਨ ਲਗਾਉਂਦੀ ਹੈ, ਤਾਂ ਜੋ ਹਰ ਸ਼ਬਦ ਪਿਆਰ ਤੇ ਸਤਿਕਾਰ ਨਾਲ ਸੁਣਿਆ ਜਾਵੇ।"
        ),
        "Resting After Sohila" to mapOf(
            DisplayLanguage.Hindi to "सोहिला के बाद विश्राम",
            DisplayLanguage.Punjabi to "ਸੋਹਿਲਾ ਸਾਹਿਬ ਤੋਂ ਬਾਅਦ ਆਰਾਮ"
        ),
        "If you complete your Kirtan Sohila before bedtime, Bani sleeps soundly and peacefully under the starry night sky, knowing your sleep is guarded by peace." to mapOf(
            DisplayLanguage.Hindi to "यदि आप सोने से पहले कीर्तन सोहिला का पाठ पूरा कर लेते हैं, तो बाणी तारों भरे आसमान के नीचे चैन से सोती है, यह जानते हुए कि आपकी नींद में शांति की पहरेदारी है।",
            DisplayLanguage.Punjabi to "ਜੇਕਰ ਤੁਸੀਂ ਸੌਣ ਤੋਂ ਪਹਿਲਾਂ ਕੀਰਤਨ ਸੋਹਿਲਾ ਸਾਹਿਬ ਦਾ ਪਾਠ ਕਰ ਲੈਂਦੇ ਹੋ, ਤਾਂ ਬਾਣੀ ਤਾਰਿਆਂ ਭਰੇ ਅਸਮਾਨ ਹੇਠ ਚੈਨ ਨਾਲ ਸੌਂਦੀ ਹੈ, ਇਹ ਜਾਣਦਿਆਂ ਕਿ ਤੁਹਾਡੀ ਨੀਂਦ ਅਮਨ-ਸ਼ਾਂਤੀ ਨਾਲ ਭਰੀ ਹੈ।"
        ),
        "Gentle Guidance" to mapOf(
            DisplayLanguage.Hindi to "स्नेहपूर्ण मार्गदर्शन",
            DisplayLanguage.Punjabi to "ਨਿਮਰ ਅਗਵਾਈ"
        ),
        "If a prayer is missed or a mistake is made, Bani tilts her head with a caring look, reminding you gently that every new morning is a fresh start to try again." to mapOf(
            DisplayLanguage.Hindi to "यदि कोई पाठ छूट जाए या कोई भूल हो जाए, तो बाणी स्नेहपूर्वक आपको याद दिलाती है कि हर नई सुबह फिर से शुरुआत करने का एक नया अवसर है।",
            DisplayLanguage.Punjabi to "ਜੇਕਰ ਕੋਈ ਪਾਠ ਰਹਿ ਜਾਵੇ ਜਾਂ ਭੁੱਲ ਹੋ ਜਾਵੇ, ਤਾਂ ਬਾਣੀ ਪਿਆਰ ਨਾਲ ਯਾਦ ਦਿਵਾਉਂਦੀ ਹੈ ਕਿ ਹਰ ਨਵੀਂ ਸਵੇਰ ਮੁੜ ਯਤਨ ਕਰਨ ਦਾ ਇੱਕ ਤਾਜ਼ਾ ਮੌਕਾ ਹੈ।"
        ),
        "Celebrating Your Streak" to mapOf(
            DisplayLanguage.Hindi to "आपकी निरंतरता का उत्सव",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡੀ ਲੜੀ ਦਾ ਜਸ਼ਨ"
        ),
        "When you build a prayer streak, a vibrant golden aura ignites around Bani. She leaps with joy in a state of glowing celebration — because consistency is its own reward!" to mapOf(
            DisplayLanguage.Hindi to "जब आप नियमित पाठ की निरंतरता बनाते हैं, तो बाणी के चारों ओर एक सुनहरा प्रकाश छा जाता है। वह खुशी से झूम उठती है — क्योंकि निरंतरता अपने आप में सबसे बड़ा फल है!",
            DisplayLanguage.Punjabi to "ਜਦੋਂ ਤੁਸੀਂ ਰੋਜ਼ਾਨਾ ਪਾਠ ਦੀ ਲੜੀ ਬਣਾਉਂਦੇ ਹੋ, ਬਾਣੀ ਦੁਆਲੇ ਸੁਨਹਿਰੀ ਨੂਰ ਚਮਕ ਉੱਠਦਾ ਹੈ। ਉਹ ਖੁਸ਼ੀ ਨਾਲ ਝੂਮ ਉੱਠਦੀ ਹੈ — ਕਿਉਂਕਿ ਨਿਰੰਤਰਤਾ ਆਪਣੇ ਆਪ ਵਿੱਚ ਇੱਕ ਵੱਡਾ ਫਲ ਹੈ!"
        ),
        "Wisdom of the Gurus" to mapOf(
            DisplayLanguage.Hindi to "गुरुओं का ज्ञान",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਸਾਹਿਬਾਨ ਦੀ ਸਿੱਖਿਆ"
        ),
        "Bani looks back through history with wide, curious eyes, sharing timeless lessons and stories from the lives of the Gurus to inspire your everyday actions." to mapOf(
            DisplayLanguage.Hindi to "बाणी इतिहास के पन्नों से गुरु साहिबान के जीवन की अमर साखियाँ और उपदेश साझा करती है, जो आपके दैनिक जीवन को प्रेरित करते हैं।",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਇਤਿਹਾਸ ਦੇ ਸੁਨਹਿਰੀ ਪੰਨਿਆਂ ਤੋਂ ਗੁਰੂ ਸਾਹਿਬਾਨ ਦੇ ਜੀਵਨ ਦੀਆਂ ਅਮਰ ਸਿੱਖਿਆਵਾਂ ਸਾਂਝੀਆਂ ਕਰਦੀ ਹੈ ਜੋ ਤੁਹਾਡੇ ਰੋਜ਼ਾਨਾ ਜੀਵਨ ਨੂੰ ਰੋਸ਼ਨੀ ਦਿੰਦੀਆਂ ਹਨ।"
        ),
        "The Early Morning Blessing" to mapOf(
            DisplayLanguage.Hindi to "अमृत वेले की बख्शीश",
            DisplayLanguage.Punjabi to "ਅੰਮ੍ਰਿਤ ਵੇਲੇ ਦੀ ਦਾਤ"
        ),
        "Waking up before the sun? Bani puts on her brightest morning glow to greet the Amrit Vela hours with you, filling your day with spiritual energy and fresh hope." to mapOf(
            DisplayLanguage.Hindi to "सूरज निकलने से पहले उठ रहे हैं? बाणी आपके साथ अमृत वेले का स्वागत करती है, जिससे आपका पूरा दिन आत्मिक ऊर्जा और नई आशा से भर जाता है।",
            DisplayLanguage.Punjabi to "ਸੂਰਜ ਚੜ੍ਹਨ ਤੋਂ ਪਹਿਲਾਂ ਜਾਗ ਰਹੇ ਹੋ? ਬਾਣੀ ਤੁਹਾਡੇ ਨਾਲ ਅੰਮ੍ਰਿਤ ਵੇਲੇ ਦਾ ਸੁਆਗਤ ਕਰਦੀ ਹੈ, ਜਿਸ ਨਾਲ ਤੁਹਾਡਾ ਸਾਰਾ ਦਿਨ ਆਤਮਕ ਊਰਜਾ ਅਤੇ ਨਵੀਂ ਉਮੀਦ ਨਾਲ ਭਰ ਜਾਂਦਾ ਹੈ।"
        ),
        "Chanting Waheguru" to mapOf(
            DisplayLanguage.Hindi to "वाहेगुरु सिमरन",
            DisplayLanguage.Punjabi to "ਵਾਹਿਗੁਰੂ ਸਿਮਰਨ"
        ),
        "With every bead counted and every breath focused, Bani whispers 'Waheguru' right alongside you, turning your daily practice into a magical journey of love and light." to mapOf(
            DisplayLanguage.Hindi to "प्रत्येक मनके और प्रत्येक श्वास के साथ, बाणी आपके साथ 'वाहेगुरु' का जाप करती है, जिससे आपका दैनिक अभ्यास प्रेम और प्रकाश की यात्रा बन जाता है।",
            DisplayLanguage.Punjabi to "ਹਰ ਮਣਕੇ ਅਤੇ ਹਰ ਸੁਆਸ ਦੇ ਨਾਲ, ਬਾਣੀ ਤੁਹਾਡੇ ਨਾਲ 'ਵਾਹਿਗੁਰੂ' ਦਾ ਜਾਪ ਕਰਦੀ ਹੈ, ਜਿਸ ਨਾਲ ਤੁਹਾਡਾ ਰੋਜ਼ਾਨਾ ਅਭਿਆਸ ਪਿਆਰ ਅਤੇ ਨੂਰ ਦੀ ਯਾਤਰਾ ਬਣ ਜਾਂਦਾ ਹੈ।"
        )
    )
}

/**
 * Converts Gurmukhi script text to Devanagari with proper conjuncts, vowel signs, and independent vowels.
 */
fun String.gurmukhiToDevanagari(): String {
    if (this.isBlank()) return this

    // 1. Map whole independent vowels / vowel bearer clusters and multi-character Devanagari signs first
    var text = this
        .replace("ੴ", "ੴ")
        .replace("ਆ", "आ")
        .replace("ਇ", "इ")
        .replace("ਈ", "ई")
        .replace("ਉ", "उ")
        .replace("ਊ", "ऊ")
        .replace("ਏ", "ए")
        .replace("ਐ", "ऐ")
        .replace("ਓ", "ओ")
        .replace("ਔ", "औ")
        .replace("ਅੰ", "अं")
        .replace("ਅਂ", "अं")
        .replace("ਅ", "अ")
        .replace("ੲੀ", "ई")
        .replace("ੲਿ", "इ")
        .replace("ੲੇ", "ए")
        .replace("ੲ", "इ")
        .replace("ੳੂ", "ऊ")
        .replace("ੳੁ", "उ")
        .replace("ੳੋ", "ओ")
        .replace("ੳ", "उ")
        .replace("ੜ", "ड़")
        .replace("ਜ਼", "ज़")
        .replace("ਫ਼", "फ़")
        .replace("ਖ਼", "ख़")
        .replace("ਗ਼", "ग़")
        .replace("੍ਰ", "्र") // Pair Rara
        .replace("੍ਹ", "्ह") // Pair Haha
        .replace("੍ਵ", "्व") // Pair Vava
        .replace("ੵ", "्य") // Pair Yaya
        .replace("ੑ", "्")

    val charMap = mapOf(
        'ਸ' to 'स', 'ਹ' to 'ह',
        'ਕ' to 'क', 'ਖ' to 'ख', 'ਗ' to 'ग', 'ਘ' to 'घ', 'ਙ' to 'ङ',
        'ਚ' to 'च', 'ਛ' to 'छ', 'ਜ' to 'ज', 'ਝ' to 'झ', 'ਞ' to 'ञ',
        'ਟ' to 'ट', 'ਠ' to 'ठ', 'ਡ' to 'ड', 'ਢ' to 'ढ', 'ਣ' to 'ण',
        'ਤ' to 'त', 'ਥ' to 'थ', 'ਦ' to 'द', 'ਧ' to 'ध', 'ਨ' to 'न',
        'ਪ' to 'प', 'ਫ' to 'फ', 'ਬ' to 'ब', 'ਭ' to 'भ', 'ਮ' to 'म',
        'ਯ' to 'य', 'ਰ' to 'र', 'ਲ' to 'ल', 'ਵ' to 'व',
        'ਸ਼' to 'श', 'ਲ਼' to 'ल',
        'ਾ' to 'ा', 'ਿ' to 'ि', 'ੀ' to 'ी', 'ੁ' to 'ु', 'ੂ' to 'ू',
        'ੇ' to 'े', 'ੈ' to 'ै', 'ੋ' to 'ो', 'ੌ' to 'ौ',
        'ਂ' to 'ं', 'ੰ' to 'ं', '਼' to '़', 'ੑ' to '्'
    )

    val sb = StringBuilder()
    var i = 0
    while (i < text.length) {
        val ch = text[i]
        if (ch == 'ੱ' && i + 1 < text.length) {
            // Adhak indicates double consonant in Devanagari (consonant + virama)
            val nextCh = text[i + 1]
            val devNext = charMap[nextCh] ?: nextCh
            sb.append(devNext).append('्')
        } else {
            sb.append(charMap[ch] ?: ch)
        }
        i++
    }
    return sb.toString()
}

/**
 * Translates English Steek or Punjabi Viakhya into Hindi translation text.
 */
fun String.toHindiTranslation(): String {
    if (this.isBlank()) return this
    // If the text contains Punjabi Gurmukhi Viakhya, perform high-fidelity transliteration + Hindi vocabulary mapping
    val isGurmukhi = this.any { it in '\u0A00'..'\u0A7F' }
    if (isGurmukhi) {
        val punjabiToHindiMap = listOf(
            "ਵਿੱਚ" to "में", "ਵਿਚ" to "में", "ਹੈ" to "है", "ਹਨ" to "हैं", "ਦਾ" to "का", "ਦੀ" to "की", "ਦੇ" to "के",
            "ਨੂੰ" to "को", "ਤੋਂ" to "से", "ਨਾਲ" to "साथ", "ਉਹ" to "वह", "ਜੋ" to "जो", "ਕਿ" to "कि", "ਇਹ" to "यह",
            "ਕਰਦਾ" to "करता", "ਕਰਦੀ" to "करती", "ਕਰਦੇ" to "करते", "ਨਾਮ" to "नाम", "ਪਰਮਾਤਮਾ" to "परमात्मा",
            "ਵਾਹਿਗੁਰੂ" to "वाहेगुरु", "ਗੁਰੂ" to "गुरु", "ਸਤਿਗੁਰੂ" to "सतगुरु", "ਕਿਰਪਾ" to "कृपा",
            "ਸਦਾ" to "सदा", "ਹਮੇਸ਼ਾ" to "हमेशा", "ਸਭ" to "सब", "ਵਡਿਆਈ" to "महिमा",
            "ਅੰਦਰ" to "भीतर", "ਬਾਹਰ" to "बाहर", "ਜੀਵ" to "जीव", "ਮਨ" to "मन"
        )
        var result = this
        for ((pa, hi) in punjabiToHindiMap) {
            result = result.replace(Regex("\\b$pa\\b"), hi)
        }
        return result.gurmukhiToDevanagari()
    }

    return this
}
