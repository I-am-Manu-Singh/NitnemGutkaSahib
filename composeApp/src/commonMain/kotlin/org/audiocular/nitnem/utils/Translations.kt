package org.audiocular.nitnem.utils

import org.audiocular.nitnem.app.models.DisplayLanguage

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
    val normalized = raag.trim().lowercase()
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

    val lookupKey = normalized.substring(0, 1).uppercase() + normalized.substring(1)
    
    val translated = Translations.translate(lookupKey, lang)
    if (translated != lookupKey) return translated
    
    return when (lang) {
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
        "Guru Nanak Dev" to mapOf(
            DisplayLanguage.English to "Guru Nanak Dev",
            DisplayLanguage.Hindi to "गुरु नानक देव",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਨਾਨਕ ਦੇਵ"
        ),
        "Guru Angad Dev" to mapOf(
            DisplayLanguage.English to "Guru Angad Dev",
            DisplayLanguage.Hindi to "गुरु अंगद देव",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਅੰਗਦ ਦੇਵ"
        ),
        "Guru Amar Das" to mapOf(
            DisplayLanguage.English to "Guru Amar Das",
            DisplayLanguage.Hindi to "गुरु अमर दास",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਅਮਰਦਾਸ"
        ),
        "Guru Ram Das" to mapOf(
            DisplayLanguage.English to "Guru Ram Das",
            DisplayLanguage.Hindi to "गुरु राम दास",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਰਾਮਦਾਸ"
        ),
        "Guru Arjan Dev" to mapOf(
            DisplayLanguage.English to "Guru Arjan Dev",
            DisplayLanguage.Hindi to "गुरु अर्जुन देव",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਅਰਜਨ ਦੇਵ"
        ),
        "Guru Tegh Bahadur" to mapOf(
            DisplayLanguage.English to "Guru Tegh Bahadur",
            DisplayLanguage.Hindi to "गुरु तेग बहादुर",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਤੇਗ ਬਹਾਦਰ"
        ),
        "Guru Gobind Singh" to mapOf(
            DisplayLanguage.English to "Guru Gobind Singh",
            DisplayLanguage.Hindi to "गुरु गोबिंद सिंह",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਗੋਬਿੰਦ ਸਿੰਘ"
        ),
        "Bhagat Kabir" to mapOf(
            DisplayLanguage.English to "Bhagat Kabir",
            DisplayLanguage.Hindi to "भगत कबीर",
            DisplayLanguage.Punjabi to "ਭਗਤ ਕਬੀਰ"
        ),
        "Bhagat Ravidas" to mapOf(
            DisplayLanguage.English to "Bhagat Ravidas",
            DisplayLanguage.Hindi to "भगत रविदास",
            DisplayLanguage.Punjabi to "ਭਗਤ ਰਵਿਦਾਸ"
        ),
        "Sheikh Farid" to mapOf(
            DisplayLanguage.English to "Sheikh Farid",
            DisplayLanguage.Hindi to "शेख फरीद",
            DisplayLanguage.Punjabi to "ਸ਼ੇਖ ਫਰੀਦ"
        ),
        "Bhagat Namdev" to mapOf(
            DisplayLanguage.English to "Bhagat Namdev",
            DisplayLanguage.Hindi to "भगत नामदेव",
            DisplayLanguage.Punjabi to "ਭਗਤ ਨਾਮਦੇਵ"
        ),
        "Transition peacefully into the evening." to mapOf(
            DisplayLanguage.English to "Transition peacefully into the evening.",
            DisplayLanguage.Hindi to "शांति से शाम में प्रवेश करें।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤਮਈ ਚਿੱਤ ਨਾਲ ਸ਼ਾਮ ਵਿੱਚ ਪ੍ਰਵੇਸ਼ ਕਰੋ।"
        ),
        "Prepare the mind for peaceful rest." to mapOf(
            DisplayLanguage.English to "Prepare the mind for peaceful rest.",
            DisplayLanguage.Hindi to "शांतिपूर्ण विश्राम के लिए मन को तैयार करें।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤਮਈ ਆਰਾਮ ਲਈ ਮਨ ਨੂੰ ਤਿਆਰ ਕਰੋ।"
        ),
        "Reflect on devotion and clarity." to mapOf(
            DisplayLanguage.English to "Reflect on devotion and clarity.",
            DisplayLanguage.Hindi to "भक्ति और स्पष्टता पर विचार करें।",
            DisplayLanguage.Punjabi to "ਭਗਤੀ ਅਤੇ ਸਪੱਸ਼ਟਤਾ 'ਤੇ ਵਿਚਾਰ ਕਰੋ।"
        ),
        "Seek courage and divine protection." to mapOf(
            DisplayLanguage.English to "Seek courage and divine protection.",
            DisplayLanguage.Hindi to "साहस और ईश्वरीय सुरक्षा प्राप्त करें।",
            DisplayLanguage.Punjabi to "ਸਾਹਸ ਅਤੇ ਅਕਾਲ ਪੁਰਖ ਦੀ ਰੱਖਿਆ ਲਈ ਜੋਦੜੀ ਕਰੋ।"
        ),
        "Rise with Simran" to mapOf(
            DisplayLanguage.English to "Rise with Simran",
            DisplayLanguage.Hindi to "सिमरन के साथ जागें",
            DisplayLanguage.Punjabi to "ਸਿਮਰਨ ਨਾਲ ਜਾਗੋ"
        ),
        "Focused Nitnem" to mapOf(
            DisplayLanguage.English to "Focused Nitnem",
            DisplayLanguage.Hindi to "एकाग्र नितनेम",
            DisplayLanguage.Punjabi to "ਇਕਾਗਰ ਨਿਤਨੇਮ"
        ),
        "Strength & Wisdom" to mapOf(
            DisplayLanguage.English to "Strength & Wisdom",
            DisplayLanguage.Hindi to "शक्ति और विवेक",
            DisplayLanguage.Punjabi to "ਬਲ ਅਤੇ ਬੁੱਧੀ"
        ),
        "Fearless Spirit" to mapOf(
            DisplayLanguage.English to "Fearless Spirit",
            DisplayLanguage.Hindi to "निर्भय आत्मा",
            DisplayLanguage.Punjabi to "ਨਿਰਭਉ ਆਤਮਾ"
        ),
        "Inner Reflection" to mapOf(
            DisplayLanguage.English to "Inner Reflection",
            DisplayLanguage.Hindi to "आंतरिक विचार",
            DisplayLanguage.Punjabi to "ਆਤਮਿਕ ਵਿਚਾਰ"
        ),
        "Sunset Prayer" to mapOf(
            DisplayLanguage.English to "Sunset Prayer",
            DisplayLanguage.Hindi to "शाम की प्रार्थना",
            DisplayLanguage.Punjabi to "ਸ਼ਾਮ ਦਾ ਪਾਠ"
        ),
        "Peaceful Rest" to mapOf(
            DisplayLanguage.English to "Peaceful Rest",
            DisplayLanguage.Hindi to "शांतिपूर्ण विश्राम",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤਮਈ ਆਰਾਮ"
        ),
        "Morning" to mapOf(
            DisplayLanguage.English to "Morning",
            DisplayLanguage.Hindi to "सुबह",
            DisplayLanguage.Punjabi to "ਸਵੇਰ"
        ),
        "Afternoon" to mapOf(
            DisplayLanguage.English to "Afternoon",
            DisplayLanguage.Hindi to "दोपहर",
            DisplayLanguage.Punjabi to "ਦੁਪਹਿਰ"
        ),
        "Evening" to mapOf(
            DisplayLanguage.English to "Evening",
            DisplayLanguage.Hindi to "शाम",
            DisplayLanguage.Punjabi to "ਸ਼ਾਮ"
        ),
        "Night" to mapOf(
            DisplayLanguage.English to "Night",
            DisplayLanguage.Hindi to "रात",
            DisplayLanguage.Punjabi to "ਰਾਤ"
        ),
        "Protection" to mapOf(
            DisplayLanguage.English to "Protection",
            DisplayLanguage.Hindi to "सुरक्षा",
            DisplayLanguage.Punjabi to "ਰੱਖਿਆ"
        ),
        "Select Bani" to mapOf(
            DisplayLanguage.English to "Select Bani",
            DisplayLanguage.Hindi to "बाणी चुनें",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਚੁਣੋ"
        ),
        "Audio Tracks" to mapOf(
            DisplayLanguage.English to "Audio Tracks",
            DisplayLanguage.Hindi to "ऑडियो ट्रैक",
            DisplayLanguage.Punjabi to "ਆਡੀਓ ਟਰੈਕ"
        ),
        "Downloaded / Imported Audio Files" to mapOf(
            DisplayLanguage.English to "Downloaded / Imported Audio Files",
            DisplayLanguage.Hindi to "डाउनलोड / आयातित ऑडियो फाइलें",
            DisplayLanguage.Punjabi to "ਡਾਊਨਲੋਡ / ਇੰਪੋਰਟ ਕੀਤੀਆਂ ਆਡੀਓ ਫਾਈਲਾਂ"
        ),
        "Stored offline on your device" to mapOf(
            DisplayLanguage.English to "Stored offline on your device",
            DisplayLanguage.Hindi to "आपके डिवाइस पर ऑफ़लाइन संग्रहीत",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡੇ ਡਿਵਾਈਸ 'ਤੇ ਆਫਲਾਈਨ ਸਟੋਰ ਕੀਤੀਆਂ"
        ),
        "No offline audio files. Download the default track or import custom audio below." to mapOf(
            DisplayLanguage.English to "No offline audio files. Download the default track or import custom audio below.",
            DisplayLanguage.Hindi to "कोई ऑफ़लाइन ऑडियो फ़ाइल नहीं है। डिफ़ॉल्ट ट्रैक डाउनलोड करें या नीचे कस्टम ऑडियो आयात करें।",
            DisplayLanguage.Punjabi to "ਕੋਈ ਆਫਲਾਈਨ ਆਡੀਓ ਫਾਈਲ ਨਹੀਂ ਹੈ। ਡਿਫਾਲਟ ਟਰੈਕ ਡਾਊਨਲੋਡ ਕਰੋ ਜਾਂ ਹੇਠਾਂ ਦਿੱਤੀ ਕਸਟਮ ਆਡੀਓ ਇੰਪੋਰਟ ਕਰੋ।"
        ),
        "Default Recitation (Offline)" to mapOf(
            DisplayLanguage.English to "Default Recitation (Offline)",
            DisplayLanguage.Hindi to "डिफ़ॉल्ट पाठ (ऑफ़लाइन)",
            DisplayLanguage.Punjabi to "ਡਿਫਾਲਟ ਪਾਠ (ਆਫਲਾਈਨ)"
        ),
        "Online Streaming Tracks / Links" to mapOf(
            DisplayLanguage.English to "Online Streaming Tracks / Links",
            DisplayLanguage.Hindi to "ऑनलाइन स्ट्रीमिंग ट्रैक / लिंक",
            DisplayLanguage.Punjabi to "ਆਨਲਾਈਨ ਸਟ੍ਰੀਮਿੰਗ ਟਰੈਕ / ਲਿੰਕ"
        ),
        "Requires active internet connection" to mapOf(
            DisplayLanguage.English to "Requires active internet connection",
            DisplayLanguage.Hindi to "सक्रिय इंटरनेट कनेक्शन की आवश्यकता है",
            DisplayLanguage.Punjabi to "ਆਨਲਾਈਨ ਚਲਾਉਣ ਲਈ ਇੰਟਰਨੈੱਟ ਦੀ ਲੋੜ ਹੈ"
        ),
        "No online streaming tracks available." to mapOf(
            DisplayLanguage.English to "No online streaming tracks available.",
            DisplayLanguage.Hindi to "कोई ऑनलाइन स्ट्रीमिंग ट्रैक उपलब्ध नहीं है।",
            DisplayLanguage.Punjabi to "ਕੋਈ ਆਨਲਾਈਨ ਸਟ੍ਰੀਮਿੰਗ ਟਰੈਕ ਉਪਲਬਧ ਨਹੀਂ ਹੈ।"
        ),
        "Default Recitation (Online Stream)" to mapOf(
            DisplayLanguage.English to "Default Recitation (Online Stream)",
            DisplayLanguage.Hindi to "डिफ़ॉल्ट पाठ (ऑनलाइन स्ट्रीम)",
            DisplayLanguage.Punjabi to "ਡਿਫਾਲਟ ਪਾਠ (ਆਨਲਾਈਨ ਸਟ੍ਰੀਮ)"
        ),
        "Import" to mapOf(
            DisplayLanguage.English to "Import",
            DisplayLanguage.Hindi to "आयात करें",
            DisplayLanguage.Punjabi to "ਇੰਪੋਰਟ"
        ),
        "Download default recitation" to mapOf(
            DisplayLanguage.English to "Download default recitation",
            DisplayLanguage.Hindi to "डिफ़ॉल्ट पाठ डाउनलोड करें",
            DisplayLanguage.Punjabi to "ਡਿਫਾਲਟ ਪਾਠ ਡਾਊਨਲੋਡ ਕਰੋ"
        ),
        "Artist Name cannot be empty" to mapOf(
            DisplayLanguage.English to "Artist Name cannot be empty",
            DisplayLanguage.Hindi to "कलाकार का नाम खाली नहीं हो सकता",
            DisplayLanguage.Punjabi to "ਕਲਾਕਾਰ ਦਾ ਨਾਮ ਖਾਲੀ ਨਹੀਂ ਹੋ ਸਕਦਾ"
        ),
        "Today" to mapOf(
            DisplayLanguage.English to "Today",
            DisplayLanguage.Hindi to "आज",
            DisplayLanguage.Punjabi to "ਅੱਜ"
        ),
        "Current Nitnem" to mapOf(
            DisplayLanguage.English to "Current Nitnem",
            DisplayLanguage.Hindi to "वर्तमान नितनेम",
            DisplayLanguage.Punjabi to "ਮੌਜੂਦਾ ਨਿਤਨੇਮ"
        ),
        "Your Paath" to mapOf(
            DisplayLanguage.English to "Your Paath",
            DisplayLanguage.Hindi to "आपका पाठ",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡਾ ਪਾਠ"
        ),
        "Larivaar" to mapOf(
            DisplayLanguage.English to "Larivaar",
            DisplayLanguage.Hindi to "लड़ीवार",
            DisplayLanguage.Punjabi to "ਲੜੀਵਾਰ"
        ),
        "Open Sync Studio" to mapOf(
            DisplayLanguage.English to "Open Sync Studio",
            DisplayLanguage.Hindi to "सिंक स्टूडियो खोलें",
            DisplayLanguage.Punjabi to "ਸਿੰਕ ਸਟੂਡੀਓ ਖੋਲ੍ਹੋ"
        ),
        "Import Custom Audio File" to mapOf(
            DisplayLanguage.English to "Import Custom Audio File",
            DisplayLanguage.Hindi to "कस्टम ऑडियो फ़ाइल आयात करें",
            DisplayLanguage.Punjabi to "ਕਸਟਮ ਆਡੀਓ ਫਾਈਲ ਇੰਪੋਰਟ ਕਰੋ"
        ),
        "Deleted track" to mapOf(
            DisplayLanguage.English to "Deleted track",
            DisplayLanguage.Hindi to "ट्रैक हटा दिया गया",
            DisplayLanguage.Punjabi to "ਟਰੈਕ ਹਟਾਇਆ ਗਿਆ"
        ),
        "Audio Muted" to mapOf(
            DisplayLanguage.English to "Audio Muted",
            DisplayLanguage.Hindi to "ऑडियो म्यूट किया गया",
            DisplayLanguage.Punjabi to "ਆਡੀਓ ਮਿਊਟ ਕੀਤਾ ਗਿਆ"
        ),
        "Auto Scroll Started" to mapOf(
            DisplayLanguage.English to "Auto Scroll Started",
            DisplayLanguage.Hindi to "ऑटो स्क्रॉल शुरू",
            DisplayLanguage.Punjabi to "ਆਟੋ ਸਕ੍ਰੋਲ ਸ਼ੁਰੂ"
        ),
        "Speed" to mapOf(
            DisplayLanguage.English to "Speed",
            DisplayLanguage.Hindi to "गति",
            DisplayLanguage.Punjabi to "ਗਤੀ"
        ),
        "Imported successfully!" to mapOf(
            DisplayLanguage.English to "Imported successfully!",
            DisplayLanguage.Hindi to "सफलतापूर्वक आयात किया गया!",
            DisplayLanguage.Punjabi to "ਸਫਲਤਾਪੂਰਵਕ ਇੰਪੋਰਟ ਹੋ ਗਿਆ!"
        ),
        "Manage Audio" to mapOf(
            DisplayLanguage.English to "Manage Audio",
            DisplayLanguage.Hindi to "ऑडियो प्रबंधन",
            DisplayLanguage.Punjabi to "ਆਡੀਓ ਪ੍ਰਬੰਧਨ"
        ),
        "Consistency" to mapOf(
            DisplayLanguage.English to "Consistency",
            DisplayLanguage.Hindi to "निरंतरता",
            DisplayLanguage.Punjabi to "ਲਗਾਤਾਰਤਾ"
        ),
        "Listening" to mapOf(
            DisplayLanguage.English to "Listening",
            DisplayLanguage.Hindi to "श्रवण",
            DisplayLanguage.Punjabi to "ਸੁਣਨਾ"
        ),
        "Other Banis" to mapOf(
            DisplayLanguage.English to "Other Banis",
            DisplayLanguage.Hindi to "अन्य बाणियां",
            DisplayLanguage.Punjabi to "ਹੋਰ ਬਾਣੀਆਂ"
        ),
        "Paaths" to mapOf(
            DisplayLanguage.English to "Paaths",
            DisplayLanguage.Hindi to "पाठ",
            DisplayLanguage.Punjabi to "ਪਾਠ"
        ),
        "Days" to mapOf(
            DisplayLanguage.English to "Days",
            DisplayLanguage.Hindi to "दिन",
            DisplayLanguage.Punjabi to "ਦਿਨ"
        ),
        "Reading" to mapOf(
            DisplayLanguage.English to "Reading",
            DisplayLanguage.Hindi to "पठन",
            DisplayLanguage.Punjabi to "ਪੜ੍ਹਨਾ"
        ),
        "Playback" to mapOf(
            DisplayLanguage.English to "Playback",
            DisplayLanguage.Hindi to "प्लेबैक",
            DisplayLanguage.Punjabi to "ਪਲੇਬੈਕ"
        ),
        "Appearance" to mapOf(
            DisplayLanguage.English to "Appearance",
            DisplayLanguage.Hindi to "दिखावट",
            DisplayLanguage.Punjabi to "ਦਿੱਖ"
        ),
        "Reminders" to mapOf(
            DisplayLanguage.English to "Reminders",
            DisplayLanguage.Hindi to "अनुस्मारक",
            DisplayLanguage.Punjabi to "ਰੀਮਾਈਂਡਰ"
        ),
        "Display Language" to mapOf(
            DisplayLanguage.English to "Display Language",
            DisplayLanguage.Hindi to "प्रदर्शन भाषा",
            DisplayLanguage.Punjabi to "ਐਪ ਦੀ ਭਾਸ਼ਾ"
        ),
        "Gurmukhi" to mapOf(
            DisplayLanguage.English to "Gurmukhi",
            DisplayLanguage.Hindi to "गुरमुखी",
            DisplayLanguage.Punjabi to "ਗੁਰਮੁਖੀ"
        ),
        "Font Size" to mapOf(
            DisplayLanguage.English to "Font Size",
            DisplayLanguage.Hindi to "अक्षर आकार",
            DisplayLanguage.Punjabi to "ਅੱਖਰਾਂ ਦਾ ਆਕਾਰ"
        ),
        "Style" to mapOf(
            DisplayLanguage.English to "Style",
            DisplayLanguage.Hindi to "शैली",
            DisplayLanguage.Punjabi to "ਸ਼ੈਲੀ"
        ),
        "Visible Content" to mapOf(
            DisplayLanguage.English to "Visible Content",
            DisplayLanguage.Hindi to "दृश्य सामग्री",
            DisplayLanguage.Punjabi to "ਦਿੱਖ ਸਮੱਗਰੀ"
        ),
        "Roman Size" to mapOf(
            DisplayLanguage.English to "Roman Size",
            DisplayLanguage.Hindi to "रोमन आकार",
            DisplayLanguage.Punjabi to "ਰੋਮਨ ਆਕਾਰ"
        ),
        "AI Gurbani Quiz" to mapOf(
            DisplayLanguage.English to "AI Gurbani Quiz",
            DisplayLanguage.Hindi to "AI गुरबानी क्विज",
            DisplayLanguage.Punjabi to "AI ਗੁਰਬਾਣੀ ਕਵਿਜ਼"
        ),
        "Check your knowledge by practicing daily Gurbani questions." to mapOf(
            DisplayLanguage.English to "Check your knowledge by practicing daily Gurbani questions.",
            DisplayLanguage.Hindi to "दैनिक गुरबानी प्रश्नों का अभ्यास करके अपने ज्ञान का परीक्षण करें।",
            DisplayLanguage.Punjabi to "ਰੋਜ਼ਾਨਾ ਗੁਰਬਾਣੀ ਪ੍ਰਸ਼ਨਾਂ ਦਾ ਅਭਿਆਸ ਕਰਕੇ ਆਪਣੇ ਗਿਆਨ ਦੀ ਜਾਂਚ ਕਰੋ।"
        ),
        "ੴ ਸਤਿਨਾਮੁ ਵਾਹਿਗੁਰੂ" to mapOf(
            DisplayLanguage.English to "Ek Onkar Satnam Waheguru",
            DisplayLanguage.Hindi to "ੴ सतनाम वाहेगुरु",
            DisplayLanguage.Punjabi to "ੴ ਸਤਿਨਾਮੁ ਵਾਹਿਗੁਰੂ"
        ),
        "ਨਿਤਨੇਮ ਗੁਟਕਾ ਸਾਹਿਬ" to mapOf(
            DisplayLanguage.English to "Nitnem Gutka Sahib",
            DisplayLanguage.Hindi to "नितनेम गुटका साहिब",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਗੁਟਕਾ ਸਾਹਿਬ"
        ),
        // Tabs
        "Home" to mapOf(
            DisplayLanguage.English to "Home",
            DisplayLanguage.Hindi to "मुख्य",
            DisplayLanguage.Punjabi to "ਮੁੱਖ"
        ),
        "Read" to mapOf(
            DisplayLanguage.English to "Read",
            DisplayLanguage.Hindi to "पाठ",
            DisplayLanguage.Punjabi to "ਪਾਠ"
        ),
        "Practice" to mapOf(
            DisplayLanguage.English to "Practice",
            DisplayLanguage.Hindi to "अभ्यास",
            DisplayLanguage.Punjabi to "ਅਭਿਆਸ"
        ),
        "Settings" to mapOf(
            DisplayLanguage.English to "Settings",
            DisplayLanguage.Hindi to "सेटिंग्स",
            DisplayLanguage.Punjabi to "ਸੈਟਿੰਗਾਂ"
        ),

        // HomeScreen Titles & Dialog
        "Nitnem Gutka" to mapOf(
            DisplayLanguage.English to "Nitnem Gutka",
            DisplayLanguage.Hindi to "नितनेम गुटका",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਗੁਟਕਾ"
        ),
        "About Nitnem Gutka Steek" to mapOf(
            DisplayLanguage.English to "About Nitnem Gutka Steek",
            DisplayLanguage.Hindi to "नितनेम गुटका स्टीक के बारे में",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਗੁਟਕਾ ਸਟੀਕ ਬਾਰੇ"
        ),
        "This application is designed to support your daily Nitnem Gurbani..." to mapOf(
            DisplayLanguage.English to "This application is designed to support your daily Nitnem Gurbani recitation, meditation, and learning journey.",
            DisplayLanguage.Hindi to "यह एप्लिकेशन आपके दैनिक नितनेम गुरबाणी पाठ, ध्यान और सीखने की यात्रा का समर्थन करने के लिए डिज़ाइन किया गया है।",
            DisplayLanguage.Punjabi to "ਇਹ ਐਪਲੀਕੇਸ਼ਨ ਤੁਹਾਡੀ ਰੋਜ਼ਾਨਾ ਨਿਤਨੇਮ ਗੁਰਬਾਣੀ ਪਾਠ, ਸਿਮਰਨ ਅਤੇ ਸਿੱਖਣ ਦੀ ਯਾਤਰਾ ਵਿੱਚ ਸਹਾਇਤਾ ਲਈ ਤਿਆਰ ਕੀਤੀ ਗਈ ਹੈ।"
        ),
        "Key Features" to mapOf(
            DisplayLanguage.English to "Key Features",
            DisplayLanguage.Hindi to "मुख्य विशेषताएं",
            DisplayLanguage.Punjabi to "ਮੁੱਖ ਵਿਸ਼ੇਸ਼ਤਾਵਾਂ"
        ),
        "Data & Source Attribution" to mapOf(
            DisplayLanguage.English to "Data & Source Attribution",
            DisplayLanguage.Hindi to "डेटा और स्रोत श्रेय",
            DisplayLanguage.Punjabi to "ਡੇਟਾ ਅਤੇ ਸਰੋਤ ਸਿਹਰਾ"
        ),
        "Humble Apology & Credits" to mapOf(
            DisplayLanguage.English to "Humble Apology & Credits",
            DisplayLanguage.Hindi to "विनम्र क्षमा और आभार",
            DisplayLanguage.Punjabi to "ਨਿਮਰਤਾ ਸਹਿਤ ਭੁੱਲ ਬਖ਼ਸ਼ਾਉਣੀ ਅਤੇ ਕ੍ਰੈਡਿਟ"
        ),
        "Made with love and utmost respect for Waheguru..." to mapOf(
            DisplayLanguage.English to "Made with love and utmost respect for Waheguru. While compiling and coding, unintentional errors (shudh/ashudh) may have occurred. We apologize deeply for any mistakes. Please read with clean mind and heart, and forgive any mistakes made by the team.\n\nਭੁਲਣ ਅੰਦਰਿ ਸਭ ਕੋ ਅਭੁਲੁ ਗੁਰੂ ਕਰਤਾਰੁ ॥",
            DisplayLanguage.Hindi to "वाहेगुरु के प्रति अत्यंत आदर और प्रेम से बनाया गया। संकलन और कोडिंग के दौरान, अनजाने में त्रुटियां (शुद्ध/अशुद्ध) हो सकती हैं। हम किसी भी गलती के लिए क्षमा चाहते हैं। कृपया स्वच्छ मन से पढ़ें और टीम की गलतियों को माफ करें।\n\nਭੁਲਣ ਅੰਦਰਿ ਸਭ ਕੋ ਅਭੁਲੁ ਗੁਰੂ ਕਰਤਾਰੁ ॥",
            DisplayLanguage.Punjabi to "ਵਾਹਿਗੁਰੂ ਜੀ ਦੇ ਪ੍ਰਤੀ ਅਤਿਅੰਤ ਸਤਿਕਾਰ ਅਤੇ ਪਿਆਰ ਨਾਲ ਬਣਾਇਆ ਗਿਆ। ਸੰਕਲਨ ਅਤੇ ਕੋਡਿੰਗ ਦੌਰਾਨ, ਅਣਜਾਣੇ ਵਿੱਚ ਗਲਤੀਆਂ (ਸ਼ੁੱਧ/ਅਸ਼ੁੱਧ) ਹੋ ਸਕਦੀਆਂ ਹਨ। ਅਸੀਂ ਕਿਸੇ ਵੀ ਗਲਤੀ ਲਈ ਦਿਲੋਂ ਖ਼ਿਮਾ ਦੇ ਜਾਚਕ ਹਾਂ। ਕਿਰਪਾ ਕਰਕੇ ਸਾਫ਼ ਮਨ ਨਾਲ ਪੜ੍ਹੋ ਅਤੇ ਟੀਮ ਦੀਆਂ ਗਲਤੀਆਂ ਨੂੰ ਬਖ਼ਸ਼ੋ।\n\nਭੁਲਣ ਅੰਦਰਿ ਸਭ ਕੋ ਅਭੁਲੁ ਗੁਰੂ ਕਰਤਾਰੁ ॥"
        ),
        "Degh Teg Fateh" to mapOf(
            DisplayLanguage.English to "Degh Teg Fateh",
            DisplayLanguage.Hindi to "देग तेग फ़तेह",
            DisplayLanguage.Punjabi to "ਦੇਗ ਤੇਗ ਫ਼ਤਿਹ"
        ),
        "Today's" to mapOf(
            DisplayLanguage.English to "Today's",
            DisplayLanguage.Hindi to "आज का",
            DisplayLanguage.Punjabi to "ਅੱਜ ਦਾ"
        ),
        "This Month" to mapOf(
            DisplayLanguage.English to "This Month",
            DisplayLanguage.Hindi to "इस महीने",
            DisplayLanguage.Punjabi to "ਇਸ ਮਹੀਨੇ"
        ),
        "Time" to mapOf(
            DisplayLanguage.English to "Time",
            DisplayLanguage.Hindi to "समय",
            DisplayLanguage.Punjabi to "ਸਮਾਂ"
        ),
        "Completed" to mapOf(
            DisplayLanguage.English to "Completed",
            DisplayLanguage.Hindi to "पूर्ण",
            DisplayLanguage.Punjabi to "ਪੂਰਾ ਹੋਇਆ"
        ),
        "Strong Days" to mapOf(
            DisplayLanguage.English to "Strong Days",
            DisplayLanguage.Hindi to "मजबूत दिन",
            DisplayLanguage.Punjabi to "ਦ੍ਰਿੜ੍ਹ ਦਿਨ"
        ),
        "Nitnem Banis" to mapOf(
            DisplayLanguage.English to "Nitnem Banis",
            DisplayLanguage.Hindi to "निटनेम बाणियां",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਬਾਣੀਆਂ"
        ),
        "Continue Journey" to mapOf(
            DisplayLanguage.English to "Continue Journey",
            DisplayLanguage.Hindi to "यात्रा जारी रखें",
            DisplayLanguage.Punjabi to "ਸਫ਼ਰ ਜਾਰੀ ਰੱਖੋ"
        ),
        "Recommended Now" to mapOf(
            DisplayLanguage.English to "Recommended Now",
            DisplayLanguage.Hindi to "अभी अनुशंसित",
            DisplayLanguage.Punjabi to "ਹੁਣੇ ਪੜ੍ਹੋ"
        ),
        "Today's Nitnem Complete" to mapOf(
            DisplayLanguage.English to "Today's Nitnem Complete",
            DisplayLanguage.Hindi to "आज का नितनेम पूर्ण",
            DisplayLanguage.Punjabi to "ਅੱਜ ਦਾ ਨਿਤਨੇਮ ਪੂਰਾ ਹੋਇਆ"
        ),
        "Streak" to mapOf(
            DisplayLanguage.English to "Streak",
            DisplayLanguage.Hindi to "दैनिक क्रम",
            DisplayLanguage.Punjabi to "ਦਿਨਾਂ ਦਾ ਕ੍ਰਮ"
        ),

        // Hero Card Prayer Statuses
        "Amrit Vela" to mapOf(
            DisplayLanguage.English to "Amrit Vela",
            DisplayLanguage.Hindi to "अमृत वेला",
            DisplayLanguage.Punjabi to "ਅੰਮ੍ਰਿਤ ਵੇਲਾ"
        ),
        "Perfect time for Japji Sahib and simran." to mapOf(
            DisplayLanguage.English to "Perfect time for Japji Sahib and simran.",
            DisplayLanguage.Hindi to "जपजी साहिब और सिमरन के लिए सही समय।",
            DisplayLanguage.Punjabi to "ਜਪੁਜੀ ਸਾਹਿਬ ਅਤੇ ਸਿਮਰਨ ਲਈ ਉੱਤਮ ਸਮਾਂ।"
        ),
        "Spiritual energy peaks before sunrise." to mapOf(
            DisplayLanguage.English to "Spiritual energy peaks before sunrise.",
            DisplayLanguage.Hindi to "सूर्योदय से पहले आध्यात्मिक ऊर्जा चरम पर होती है।",
            DisplayLanguage.Punjabi to "ਸੂਰਜ ਚੜ੍ਹਨ ਤੋਂ ਪਹਿਲਾਂ ਅਧਿਆਤਮਿਕ ਊਰਜਾ ਸਿਖਰ 'ਤੇ ਹੁੰਦੀ ਹੈ।"
        ),
        "Morning Nitnem" to mapOf(
            DisplayLanguage.English to "Morning Nitnem",
            DisplayLanguage.Hindi to "सुबह का नितनेम",
            DisplayLanguage.Punjabi to "ਸਵੇਰ ਦਾ ਨਿਤਨੇਮ"
        ),
        "Start the day with clarity and discipline." to mapOf(
            DisplayLanguage.English to "Start the day with clarity and discipline.",
            DisplayLanguage.Hindi to "स्पष्टता और अनुशासन के साथ दिन की शुरुआत करें।",
            DisplayLanguage.Punjabi to "ਸਪੱਸ਼ਟਤਾ ਅਤੇ ਅਨੁਸ਼ਾਸਨ ਨਾਲ ਦਿਨ ਦੀ ਸ਼ੁਰੂਆਤ ਕਰੋ।"
        ),
        "Morning Gurbani builds focus for the day." to mapOf(
            DisplayLanguage.English to "Morning Gurbani builds focus for the day.",
            DisplayLanguage.Hindi to "सुबह की गुरबाणी दिन भर के लिए ध्यान केंद्रित करती है।",
            DisplayLanguage.Punjabi to "ਸਵੇਰ ਦੀ ਗੁਰਬਾਣੀ ਦਿਨ ਭਰ ਲਈ ਮਨ ਦਾ ਟਿਕਾਅ ਬਣਾਉਂਦੀ ਹੈ।"
        ),
        "Afternoon Reflection" to mapOf(
            DisplayLanguage.English to "Afternoon Reflection",
            DisplayLanguage.Hindi to "दोपहर का विचार",
            DisplayLanguage.Punjabi to "ਦੁਪਹਿਰ ਦਾ ਵਿਚਾਰ"
        ),
        "Pause and reconnect with Gurbani." to mapOf(
            DisplayLanguage.English to "Pause and reconnect with Gurbani.",
            DisplayLanguage.Hindi to "रुकें और गुरबाणी से फिर जुड़ें।",
            DisplayLanguage.Punjabi to "ਰੁਕੋ ਅਤੇ ਗੁਰਬਾਣੀ ਨਾਲ ਦੁਬਾਰਾ ਜੁੜੋ।"
        ),
        "Small spiritual breaks reduce mental noise." to mapOf(
            DisplayLanguage.English to "Small spiritual breaks reduce mental noise.",
            DisplayLanguage.Hindi to "छोटे आध्यात्मिक विराम मानसिक शोर को कम करते हैं।",
            DisplayLanguage.Punjabi to "ਛੋਟੇ ਅਧਿਆਤਮਿਕ ਵਿਰਾਮ ਮਾਨਸਿਕ ਸ਼ੋਰ ਨੂੰ ਘਟਾਉਂਦੇ ਹਨ।"
        ),
        "Recharge spiritually after the day." to mapOf(
            DisplayLanguage.English to "Recharge spiritually after the day.",
            DisplayLanguage.Hindi to "दिन भर के बाद आध्यात्मिक रूप से तरोताजा हों।",
            DisplayLanguage.Punjabi to "ਦਿਨ ਭਰ ਦੇ ਕੰਮਾਂ ਤੋਂ ਬਾਅਦ ਅਧਿਆਤਮਿਕ ਤੌਰ 'ਤੇ ਤਾਜ਼ਾ ਹੋਵੋ।"
        ),
        "Evening paath restores mental calm." to mapOf(
            DisplayLanguage.English to "Evening paath restores mental calm.",
            DisplayLanguage.Hindi to "शाम का पाठ मानसिक शांति बहाल करता है।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਮ ਦਾ ਪਾਠ ਮਾਨਸਿਕ ਸ਼ਾਂਤੀ ਪ੍ਰਦਾਨ ਕਰਦਾ ਹੈ।"
        ),
        "Prepare the mind for peaceful rest." to mapOf(
            DisplayLanguage.English to "Prepare the mind for peaceful rest.",
            DisplayLanguage.Hindi to "शांतिपूर्ण आराम के लिए मन को तैयार करें।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤਮਈ ਆਰਾਮ ਲਈ ਮਨ ਨੂੰ ਤਿਆਰ ਕਰੋ।"
        ),
        "Night prayer slows the mind before sleep." to mapOf(
            DisplayLanguage.English to "Night prayer slows the mind before sleep.",
            DisplayLanguage.Hindi to "रात की प्रार्थना सोने से पहले मन को शांत करती है।",
            DisplayLanguage.Punjabi to "ਰਾਤ ਦਾ ਪਾਠ ਸੌਣ ਤੋਂ ਪਹਿਲਾਂ ਮਨ ਦੀ ਗਤੀ ਨੂੰ ਸ਼ਾਂਤ ਕਰਦਾ ਹੈ।"
        ),
        "Current Focus" to mapOf(
            DisplayLanguage.English to "Current Focus",
            DisplayLanguage.Hindi to "वर्तमान ध्यान",
            DisplayLanguage.Punjabi to "ਮੌਜੂਦਾ ਧਿਆਨ"
        ),
        "Waheguru Ji" to mapOf(
            DisplayLanguage.English to "Waheguru Ji",
            DisplayLanguage.Hindi to "वाहेगुरु जी",
            DisplayLanguage.Punjabi to "ਵਾਹਿਗੁਰੂ ਜੀ"
        ),

        // Settings Toggles & Categories
        "Reading Settings" to mapOf(
            DisplayLanguage.English to "Reading Settings",
            DisplayLanguage.Hindi to "पठन सेटिंग्स",
            DisplayLanguage.Punjabi to "ਪੜ੍ਹਨ ਦੀਆਂ ਸੈਟਿੰਗਾਂ"
        ),
        "Reading Styles" to mapOf(
            DisplayLanguage.English to "Reading Styles",
            DisplayLanguage.Hindi to "पठन शैलियाँ",
            DisplayLanguage.Punjabi to "ਪੜ੍ਹਨ ਦੀਆਂ ਸ਼ੈਲੀਆਂ"
        ),
        "Bold" to mapOf(
            DisplayLanguage.English to "Bold",
            DisplayLanguage.Hindi to "गहरे अक्षर",
            DisplayLanguage.Punjabi to "ਗੂੜ੍ਹੇ ਅੱਖਰ"
        ),
        "Center" to mapOf(
            DisplayLanguage.English to "Center",
            DisplayLanguage.Hindi to "मध्य संरेखित",
            DisplayLanguage.Punjabi to "ਵਿਚਕਾਰ ਸੰਰੇਖਿਤ"
        ),
        "Compact" to mapOf(
            DisplayLanguage.English to "Compact",
            DisplayLanguage.Hindi to "सघन मोड",
            DisplayLanguage.Punjabi to "ਸੰਘਣਾ ਮੋਡ"
        ),
        "Highlight" to mapOf(
            DisplayLanguage.English to "Highlight",
            DisplayLanguage.Hindi to "सक्रिय पंक्ति उजागर करें",
            DisplayLanguage.Punjabi to "ਚੱਲ ਰਹੀ ਲਾਈਨ ਉਜਾਗਰ ਕਰੋ"
        ),
        "Translations" to mapOf(
            DisplayLanguage.English to "Translations",
            DisplayLanguage.Hindi to "अनुवाद",
            DisplayLanguage.Punjabi to "ਅਨੁਵਾਦ"
        ),
        "English Translation" to mapOf(
            DisplayLanguage.English to "English Translation",
            DisplayLanguage.Hindi to "अंग्रेजी अनुवाद",
            DisplayLanguage.Punjabi to "ਅੰਗਰੇਜ਼ੀ ਅਨੁਵਾਦ"
        ),
        "Punjabi Translation" to mapOf(
            DisplayLanguage.English to "Punjabi Translation",
            DisplayLanguage.Hindi to "पंजाबी अनुवाद",
            DisplayLanguage.Punjabi to "ਪੰਜਾਬੀ ਅਨੁਵਾਦ"
        ),
        "Roman Transliteration" to mapOf(
            DisplayLanguage.English to "Roman Transliteration",
            DisplayLanguage.Hindi to "रोमन लिप्यंतरण",
            DisplayLanguage.Punjabi to "ਰੋਮਨ ਲਿਪੀਅੰਤਰਨ"
        ),
        "Word Meanings" to mapOf(
            DisplayLanguage.English to "Word Meanings",
            DisplayLanguage.Hindi to "शब्दार्थ",
            DisplayLanguage.Punjabi to "ਸ਼ਬਦਾਰਥ"
        ),
        "Typography" to mapOf(
            DisplayLanguage.English to "Typography",
            DisplayLanguage.Hindi to "अक्षर आकार",
            DisplayLanguage.Punjabi to "ਅੱਖਰਾਂ ਦਾ ਆਕਾਰ"
        ),
        "Gurmukhi Size" to mapOf(
            DisplayLanguage.English to "Gurmukhi Size",
            DisplayLanguage.Hindi to "गुरमुखी आकार",
            DisplayLanguage.Punjabi to "ਗੁਰਮੁਖੀ ਆਕਾਰ"
        ),
        "Translation Size" to mapOf(
            DisplayLanguage.English to "Translation Size",
            DisplayLanguage.Hindi to "अनुवाद आकार",
            DisplayLanguage.Punjabi to "ਅਨੁਵਾਦ ਆਕਾਰ"
        ),
        "Transliteration Size" to mapOf(
            DisplayLanguage.English to "Transliteration Size",
            DisplayLanguage.Hindi to "लिप्यंतरण आकार",
            DisplayLanguage.Punjabi to "ਲਿਪੀਅੰਤਰਨ ਆਕਾਰ"
        ),
        "Word Meaning Size" to mapOf(
            DisplayLanguage.English to "Word Meaning Size",
            DisplayLanguage.Hindi to "शब्दार्थ आकार",
            DisplayLanguage.Punjabi to "ਸ਼ਬਦਾਰਥ ਆਕਾਰ"
        ),
        "Line Spacing" to mapOf(
            DisplayLanguage.English to "Line Spacing",
            DisplayLanguage.Hindi to "पंक्ति रिक्ति",
            DisplayLanguage.Punjabi to "ਲਾਈਨਾਂ ਵਿੱਚ ਦੂਰੀ"
        ),
        "Text Colors" to mapOf(
            DisplayLanguage.English to "Text Colors",
            DisplayLanguage.Hindi to "पाठ के रंग",
            DisplayLanguage.Punjabi to "ਲਿਖਤ ਦੇ ਰੰਗ"
        ),
        "Gurmukhi Text" to mapOf(
            DisplayLanguage.English to "Gurmukhi Text",
            DisplayLanguage.Hindi to "गुरमुखी पाठ",
            DisplayLanguage.Punjabi to "ਗੁਰਮੁਖੀ ਲਿਖਤ"
        ),
        "Transliteration" to mapOf(
            DisplayLanguage.English to "Transliteration",
            DisplayLanguage.Hindi to "लिप्यंतरण",
            DisplayLanguage.Punjabi to "ਲਿਪੀਅੰਤਰਨ"
        ),
        "Translation" to mapOf(
            DisplayLanguage.English to "Translation",
            DisplayLanguage.Hindi to "अनुवाद",
            DisplayLanguage.Punjabi to "ਅਨੁਵਾਦ"
        ),

        // Playback Settings
        "Playback Settings" to mapOf(
            DisplayLanguage.English to "Playback Settings",
            DisplayLanguage.Hindi to "प्लेबैक सेटिंग्स",
            DisplayLanguage.Punjabi to "ਪਲੇਬੈਕ ਸੈਟਿੰਗਾਂ"
        ),
        "Playback Speed" to mapOf(
            DisplayLanguage.English to "Playback Speed",
            DisplayLanguage.Hindi to "प्लेबैक गति",
            DisplayLanguage.Punjabi to "ਪਲੇਬੈਕ ਦੀ ਗਤੀ"
        ),
        "Auto Scroll Speed" to mapOf(
            DisplayLanguage.English to "Auto Scroll Speed",
            DisplayLanguage.Hindi to "ऑटो स्क्रॉल गति",
            DisplayLanguage.Punjabi to "ਆਟੋ ਸਕ੍ਰੋਲ ਦੀ ਗਤੀ"
        ),
        "Auto Scroll" to mapOf(
            DisplayLanguage.English to "Auto Scroll",
            DisplayLanguage.Hindi to "ऑटो स्क्रॉल",
            DisplayLanguage.Punjabi to "ਆਟੋ ਸਕ੍ਰੋਲ"
        ),
        "Repeat Mode" to mapOf(
            DisplayLanguage.English to "Repeat Mode",
            DisplayLanguage.Hindi to "पुनरावृत्ति मोड",
            DisplayLanguage.Punjabi to "ਦੁਹਰਾਓ ਮੋਡ"
        ),
        "Ambient Mode" to mapOf(
            DisplayLanguage.English to "Ambient Mode (Distraction-Free)",
            DisplayLanguage.Hindi to "एम्बिएंट मोड (ध्यान-मुक्त)",
            DisplayLanguage.Punjabi to "ਐਂਬੀਐਂਟ ਮੋਡ (ਧਿਆਨ-ਮੁਕਤ)"
        ),

        // Appearance
        "Appearance Settings" to mapOf(
            DisplayLanguage.English to "Appearance Settings",
            DisplayLanguage.Hindi to "दिखावट सेटिंग्स",
            DisplayLanguage.Punjabi to "ਦਿੱਖ ਦੀਆਂ ਸੈਟਿੰਗਾਂ"
        ),
        "Theme" to mapOf(
            DisplayLanguage.English to "Theme",
            DisplayLanguage.Hindi to "थीम",
            DisplayLanguage.Punjabi to "ਥੀਮ"
        ),
        "Dark Mode" to mapOf(
            DisplayLanguage.English to "Dark Mode",
            DisplayLanguage.Hindi to "डार्क मोड",
            DisplayLanguage.Punjabi to "ਡਾਰਕ ਮੋਡ"
        ),
        "AMOLED Black" to mapOf(
            DisplayLanguage.English to "AMOLED Black",
            DisplayLanguage.Hindi to "अमोलेड ब्लैक",
            DisplayLanguage.Punjabi to "AMOLED ਬਲੈਕ"
        ),
        "Elder Mode" to mapOf(
            DisplayLanguage.English to "Elder Mode",
            DisplayLanguage.Hindi to "बुजुर्ग मोड",
            DisplayLanguage.Punjabi to "ਬਜ਼ੁਰਗ ਮੋਡ"
        ),
        "Accent Theme" to mapOf(
            DisplayLanguage.English to "Accent Theme",
            DisplayLanguage.Hindi to "एक्सेंट थीम",
            DisplayLanguage.Punjabi to "ਰੰਗ ਦੀ ਥੀਮ"
        ),

        // Reminders
        "Enable Notifications" to mapOf(
            DisplayLanguage.English to "Enable Notifications",
            DisplayLanguage.Hindi to "सूचनाएं सक्षम करें",
            DisplayLanguage.Punjabi to "ਨੋਟੀਫਿਕੇਸ਼ਨ ਚਾਲੂ ਕਰੋ"
        ),
        "Prayer Reminders" to mapOf(
            DisplayLanguage.English to "Prayer Reminders",
            DisplayLanguage.Hindi to "प्रार्थना अनुस्मारक",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਦੇ ਰੀਮਾਈਂਡਰ"
        ),
        "Custom reminder schedule" to mapOf(
            DisplayLanguage.English to "Custom reminder schedule",
            DisplayLanguage.Hindi to "कस्टम अनुस्मारक अनुसूची",
            DisplayLanguage.Punjabi to "ਨੋਟੀਫਿਕੇਸ਼ਨ ਦਾ ਸਮਾਂ"
        ),
        "Manage" to mapOf(
            DisplayLanguage.English to "Manage",
            DisplayLanguage.Hindi to "बदलें",
            DisplayLanguage.Punjabi to "ਸਮਾਂ ਬਦਲੋ"
        ),
        "Personalize Nitnem" to mapOf(
            DisplayLanguage.English to "Personalize Nitnem",
            DisplayLanguage.Hindi to "नितनेम को अनुकूलित करें",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ ਨਿੱਜੀ ਬਣਾਓ"
        ),
        "Focused Gurbani experience designed for peace and consistency." to mapOf(
            DisplayLanguage.English to "Focused Gurbani experience designed for peace and consistency.",
            DisplayLanguage.Hindi to "शांति और निरंतरता के लिए डिज़ाइन किया गया केंद्रित गुरबानी अनुभव।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤੀ ਅਤੇ ਲਗਾਤਾਰਤਾ ਲਈ ਤਿਆਰ ਕੀਤਾ ਗਿਆ ਗੁਰਬਾਣੀ ਅਨੁਭਵ।"
        ),
        "Select App Language" to mapOf(
            DisplayLanguage.English to "Select App Language",
            DisplayLanguage.Hindi to "ऐप की भाषा चुनें",
            DisplayLanguage.Punjabi to "ਐਪ ਦੀ ਭਾਸ਼ਾ ਚੁਣੋ"
        ),
        "Warm appearance" to mapOf(
            DisplayLanguage.English to "Warm appearance",
            DisplayLanguage.Hindi to "गर्म रूप (हल्का)",
            DisplayLanguage.Punjabi to "ਨਿੱਘੀ ਦਿੱਖ"
        ),
        "Balanced contrast" to mapOf(
            DisplayLanguage.English to "Balanced contrast",
            DisplayLanguage.Hindi to "संतुलित कंट्रास्ट",
            DisplayLanguage.Punjabi to "ਸੰਤੁਲਿਤ ਕੰਟਰਾਸਟ"
        ),
        "Pure black" to mapOf(
            DisplayLanguage.English to "Pure black",
            DisplayLanguage.Hindi to "गहरा काला (अमोलेड)",
            DisplayLanguage.Punjabi to "ਸ਼ੁੱਧ ਕਾਲਾ"
        ),
        "Prayer Timings" to mapOf(
            DisplayLanguage.English to "Prayer Timings",
            DisplayLanguage.Hindi to "प्रार्थना का समय",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਦਾ ਸਮਾਂ"
        ),
        "Ready" to mapOf(
            DisplayLanguage.English to "Ready",
            DisplayLanguage.Hindi to "तैयार",
            DisplayLanguage.Punjabi to "ਤਿਆਰ"
        ),
        "Your Nitnem experience has been prepared." to mapOf(
            DisplayLanguage.English to "Your Nitnem experience has been prepared.",
            DisplayLanguage.Hindi to "आपका नितनेम अनुभव तैयार कर दिया गया है।",
            DisplayLanguage.Punjabi to "ਤੁਹਾਡਾ ਨਿਤਨੇਮ ਅਨੁਭਵ ਤਿਆਰ ਕਰ ਦਿੱਤਾ ਗਿਆ ਹੈ।"
        ),
        "Done" to mapOf(
            DisplayLanguage.English to "Done",
            DisplayLanguage.Hindi to "हो गया",
            DisplayLanguage.Punjabi to "ਠੀਕ ਹੈ"
        ),
        "Cancel" to mapOf(
            DisplayLanguage.English to "Cancel",
            DisplayLanguage.Hindi to "रद्द करें",
            DisplayLanguage.Punjabi to "ਰੱਦ ਕਰੋ"
        ),
        "Allowed: 2 AM - 8 AM" to mapOf(
            DisplayLanguage.English to "Allowed: 2 AM - 8 AM",
            DisplayLanguage.Hindi to "अनुमति: सुबह 2 - 8 बजे",
            DisplayLanguage.Punjabi to "ਮਨਜ਼ੂਰ ਸਮਾਂ: ਸਵੇਰੇ 2 ਤੋਂ 8 ਵਜੇ"
        ),
        "Allowed: 4 PM - 9 PM" to mapOf(
            DisplayLanguage.English to "Allowed: 4 PM - 9 PM",
            DisplayLanguage.Hindi to "अनुमति: शाम 4 - रात 9 बजे",
            DisplayLanguage.Punjabi to "ਮਨਜ਼ੂਰ ਸਮਾਂ: ਸ਼ਾਮ 4 ਤੋਂ ਰਾਤ 9 ਵਜੇ"
        ),
        "Allowed: 8 PM - 2 AM" to mapOf(
            DisplayLanguage.English to "Allowed: 8 PM - 2 AM",
            DisplayLanguage.Hindi to "अनुमति: रात 8 - सुबह 2 बजे",
            DisplayLanguage.Punjabi to "ਮਨਜ਼ੂਰ ਸਮਾਂ: ਰਾਤ 8 ਤੋਂ ਸਵੇਰੇ 2 ਵਜੇ"
        ),
        "Amrit Vela should be between 2 AM and 8 AM" to mapOf(
            DisplayLanguage.English to "Amrit Vela should be between 2 AM and 8 AM",
            DisplayLanguage.Hindi to "अमृत वेला सुबह 2 से 8 बजे के बीच होना चाहिए",
            DisplayLanguage.Punjabi to "ਅੰਮ੍ਰਿਤ ਵੇਲਾ ਸਵੇਰੇ 2 ਤੋਂ 8 ਵਜੇ ਦੇ ਵਿਚਕਾਰ ਹੋਣਾ ਚਾਹੀਦਾ ਹੈ"
        ),
        "Rehras should be between 4 PM and 9 PM" to mapOf(
            DisplayLanguage.English to "Rehras should be between 4 PM and 9 PM",
            DisplayLanguage.Hindi to "रहरास शाम 4 से रात 9 बजे के बीच होना चाहिए",
            DisplayLanguage.Punjabi to "ਰਹਿਰਾਸ ਸਾਹਿਬ ਸ਼ਾਮ 4 ਤੋਂ ਰਾਤ 9 ਵਜੇ ਦੇ ਵਿਚਕਾਰ ਹੋਣਾ ਚਾਹੀਦਾ ਹੈ"
        ),
        "Sohila should be between 8 PM and 2 AM" to mapOf(
            DisplayLanguage.English to "Sohila should be between 8 PM and 2 AM",
            DisplayLanguage.Hindi to "सोहिला रात 8 से सुबह 2 बजे के बीच होना चाहिए",
            DisplayLanguage.Punjabi to "ਸੋਹਿਲਾ ਸਾਹਿਬ ਰਾਤ 8 ਤੋਂ ਸਵੇਰੇ 2 ਵਜੇ ਦੇ ਵਿਚਕਾਰ ਹੋਣਾ ਚਾਹੀਦਾ ਹੈ"
        ),
        "Rehras" to mapOf(
            DisplayLanguage.English to "Rehras",
            DisplayLanguage.Hindi to "रहरास",
            DisplayLanguage.Punjabi to "ਰਹਿਰਾਸ"
        ),
        "Sohila" to mapOf(
            DisplayLanguage.English to "Sohila",
            DisplayLanguage.Hindi to "सोहिला",
            DisplayLanguage.Punjabi to "ਸੋਹਿਲਾ"
        ),
        "Light" to mapOf(
            DisplayLanguage.English to "Light",
            DisplayLanguage.Hindi to "लाइट",
            DisplayLanguage.Punjabi to "ਲਾਈਟ"
        ),
        "Dark" to mapOf(
            DisplayLanguage.English to "Dark",
            DisplayLanguage.Hindi to "डार्क",
            DisplayLanguage.Punjabi to "ਡਾਰਕ"
        ),
        "AMOLED" to mapOf(
            DisplayLanguage.English to "AMOLED",
            DisplayLanguage.Hindi to "AMOLED",
            DisplayLanguage.Punjabi to "AMOLED"
        ),
        "Line Meaning" to mapOf(
            DisplayLanguage.English to "Line Meaning",
            DisplayLanguage.Hindi to "पंक्ति का अर्थ",
            DisplayLanguage.Punjabi to "ਤੁਕ ਦਾ ਅਰਥ"
        ),
        "Tap chips below to toggle visible sections" to mapOf(
            DisplayLanguage.English to "Tap chips below to toggle visible sections",
            DisplayLanguage.Hindi to "दृश्य अनुभागों को टॉगल करने के लिए नीचे दिए गए चिप्स पर टैप करें",
            DisplayLanguage.Punjabi to "ਦਿੱਖ ਭਾਗਾਂ ਨੂੰ ਬਦਲਣ ਲਈ ਹੇਠਾਂ ਦਿੱਤੇ ਚਿਪਸ 'ਤੇ ਟੈਪ ਕਰੋ"
        ),
        "Hindi" to mapOf(
            DisplayLanguage.English to "Hindi",
            DisplayLanguage.Hindi to "हिंदी",
            DisplayLanguage.Punjabi to "ਹਿੰਦੀ"
        ),
        "English" to mapOf(
            DisplayLanguage.English to "English",
            DisplayLanguage.Hindi to "अंग्रेज़ी",
            DisplayLanguage.Punjabi to "ਅੰਗਰੇਜ਼ੀ"
        ),
        "Punjabi" to mapOf(
            DisplayLanguage.English to "Punjabi",
            DisplayLanguage.Hindi to "पंजाबी",
            DisplayLanguage.Punjabi to "ਪੰਜਾਬੀ"
        ),
        "Hindi Translation" to mapOf(
            DisplayLanguage.English to "Hindi Translation",
            DisplayLanguage.Hindi to "हिंदी अनुवाद",
            DisplayLanguage.Punjabi to "ਹਿੰਦੀ ਅਨੁਵਾਦ"
        ),
        "Japji Sahib" to mapOf(
            DisplayLanguage.English to "Japji Sahib",
            DisplayLanguage.Hindi to "जपजी साहिब",
            DisplayLanguage.Punjabi to "ਜਪੁਜੀ ਸਾਹਿਬ"
        ),
        "Jaap Sahib" to mapOf(
            DisplayLanguage.English to "Jaap Sahib",
            DisplayLanguage.Hindi to "जाप साहिब",
            DisplayLanguage.Punjabi to "ਜਾਪੁ ਸਾਹਿਬ"
        ),
        "Tav Prasad Savaiye" to mapOf(
            DisplayLanguage.English to "Tav Prasad Savaiye",
            DisplayLanguage.Hindi to "तव प्रसाद सवैये",
            DisplayLanguage.Punjabi to "ਤ੍ਵ ਪ੍ਰਸਾਦਿ ਸਵਯੇ"
        ),
        "Benti Chaupai Sahib" to mapOf(
            DisplayLanguage.English to "Benti Chaupai Sahib",
            DisplayLanguage.Hindi to "बेनती चौपई साहिब",
            DisplayLanguage.Punjabi to "ਬੇਨਤੀ ਚੌਪਈ ਸਾਹਿਬ"
        ),
        "Anand Sahib" to mapOf(
            DisplayLanguage.English to "Anand Sahib",
            DisplayLanguage.Hindi to "आनंद साहिब",
            DisplayLanguage.Punjabi to "ਅਨੰਦੁ ਸਾਹਿਬ"
        ),
        "Rehras Sahib" to mapOf(
            DisplayLanguage.English to "Rehras Sahib",
            DisplayLanguage.Hindi to "रहरास साहिब",
            DisplayLanguage.Punjabi to "ਰਹਰਾਸਿ ਸਾਹਿਬ"
        ),
        "Kirtan Sohila" to mapOf(
            DisplayLanguage.English to "Kirtan Sohila",
            DisplayLanguage.Hindi to "कीर्तन सोहिला",
            DisplayLanguage.Punjabi to "ਕੀਰਤਨ ਸੋਹਿਲਾ"
        ),
        "Ardas" to mapOf(
            DisplayLanguage.English to "Ardas",
            DisplayLanguage.Hindi to "अरदास",
            DisplayLanguage.Punjabi to "ਅਰਦਾਸ"
        ),
        "Sukhmani Sahib" to mapOf(
            DisplayLanguage.English to "Sukhmani Sahib",
            DisplayLanguage.Hindi to "सुखमनी साहिब",
            DisplayLanguage.Punjabi to "ਸੁਖਮਨੀ ਸਾਹਿਬ"
        ),
        "Asa Di Vaar" to mapOf(
            DisplayLanguage.English to "Asa Di Vaar",
            DisplayLanguage.Hindi to "आसा दी वार",
            DisplayLanguage.Punjabi to "ਆਸਾ ਕੀ ਵਾਰ"
        ),
        "Shabad Hazare" to mapOf(
            DisplayLanguage.English to "Shabad Hazare",
            DisplayLanguage.Hindi to "शब्द हजारे",
            DisplayLanguage.Punjabi to "ਸ਼ਬਦ ਹਜ਼ਾਰੇ"
        ),
        "Salok Mahalla 9" to mapOf(
            DisplayLanguage.English to "Salok Mahalla 9",
            DisplayLanguage.Hindi to "सलोक महला 9",
            DisplayLanguage.Punjabi to "ਸਲੋਕ ਮਹਲਾ ੯"
        ),
        "Barah Maha" to mapOf(
            DisplayLanguage.English to "Barah Maha",
            DisplayLanguage.Hindi to "बारह माहा",
            DisplayLanguage.Punjabi to "ਬਾਰਹ ਮਾਹਾ"
        ),
        "Dakhni Oankar" to mapOf(
            DisplayLanguage.English to "Dakhni Oankar",
            DisplayLanguage.Hindi to "दखनी ਓਅੰਕਾਰੁ",
            DisplayLanguage.Punjabi to "ਦਖਣੀ ਓਅੰਕਾਰੁ"
        ),
        "Sidha Gosht" to mapOf(
            DisplayLanguage.English to "Sidha Gosht",
            DisplayLanguage.Hindi to "सिद्ध गोश्ट",
            DisplayLanguage.Punjabi to "ਸਿਧ ਗੋਸਟਿ"
        ),
        "Basant Ki Vaar" to mapOf(
            DisplayLanguage.English to "Basant Ki Vaar",
            DisplayLanguage.Hindi to "बसंत की वार",
            DisplayLanguage.Punjabi to "ਬਸੰਤ ਕੀ ਵਾਰ"
        ),
        "Ramkali Ki Vaar" to mapOf(
            DisplayLanguage.English to "Ramkali Ki Vaar",
            DisplayLanguage.Hindi to "रामकली की वार",
            DisplayLanguage.Punjabi to "ਰਾਮਕਲੀ ਕੀ ਵਾਰ"
        ),
        "Aarti" to mapOf(
            DisplayLanguage.English to "Aarti",
            DisplayLanguage.Hindi to "आरती",
            DisplayLanguage.Punjabi to "ਆਰਤੀ"
        ),
        "Chandi Di Vaar" to mapOf(
            DisplayLanguage.English to "Chandi Di Vaar",
            DisplayLanguage.Hindi to "चंडी दी वार",
            DisplayLanguage.Punjabi to "ਚੰਡੀ ਦੀ ਵਾਰ"
        ),
        "Ragmala" to mapOf(
            DisplayLanguage.English to "Ragmala",
            DisplayLanguage.Hindi to "रागमाला",
            DisplayLanguage.Punjabi to "ਰਾਗਮਾਲਾ"
        ),
        "Nitnem" to mapOf(
            DisplayLanguage.English to "Nitnem",
            DisplayLanguage.Hindi to "नितनेम",
            DisplayLanguage.Punjabi to "ਨਿਤਨੇਮ"
        ),
        "Peaceful Gurbani reading and listening." to mapOf(
            DisplayLanguage.English to "Peaceful Gurbani reading and listening.",
            DisplayLanguage.Hindi to "शांतिपूर्ण गुरबानी पठन और श्रवण।",
            DisplayLanguage.Punjabi to "ਸ਼ਾਂਤਮਈ ਗੁਰਬਾਣੀ ਪੜ੍ਹਨਾ ਅਤੇ ਸੁਣਨਾ।"
        ),
        "Gurbani Reading" to mapOf(
            DisplayLanguage.English to "Gurbani Reading",
            DisplayLanguage.Hindi to "गुरबानी पठन",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਪੜ੍ਹਨਾ"
        ),
        "Gurbani Listening" to mapOf(
            DisplayLanguage.English to "Gurbani Listening",
            DisplayLanguage.Hindi to "गुरबानी श्रवण",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਸੁਣਨਾ"
        ),
        "Enjoy synced Gurbani playback for better pronunciation and immersion, or import and sync your own custom audio files with banis." to mapOf(
            DisplayLanguage.English to "Enjoy synced Gurbani playback for better pronunciation and immersion, or import and sync your own custom audio files with banis.",
            DisplayLanguage.Hindi to "बेहतर उच्चारण और मन लगाने के लिए सिंक किए गए गुरबानी प्लेबैक का आनंद लें, या अपनी खुद की कस्टम ऑडियो फाइलों को आयात और सिंक करें।",
            DisplayLanguage.Punjabi to "ਬਿਹਤਰ ਉਚਾਰਨ ਅਤੇ ਮਨ ਜੋੜਨ ਲਈ ਸਿੰਕ ਕੀਤੇ ਗੁਰਬਾਣੀ ਪਲੇਬੈਕ ਦਾ ਅਨੰਦ ਲਓ, ਜਾਂ ਬਾਣੀਆਂ ਨਾਲ ਆਪਣੀਆਂ ਖੁਦ ਦੀਆਂ ਕਸਟਮ ਆਡੀਓ ਫਾਈਲਾਂ ਇੰਪੋਰਟ ਅਤੇ ਸਿੰਕ ਕਰੋ।"
        ),
        "Track your paath streaks and consistency with daily reminders and notifications to keep you on track." to mapOf(
            DisplayLanguage.English to "Track your paath streaks and consistency with daily reminders and notifications to keep you on track.",
            DisplayLanguage.Hindi to "आपको ट्रैक पर रखने के लिए दैनिक रिमाइंडर्स और नोटिफिकेशन के साथ अपने पाठ के दैनिक क्रम और निरंतरता को ट्रैक करें।",
            DisplayLanguage.Punjabi to "ਤੁਹਾਨੂੰ ਸਹੀ ਰਸਤੇ 'ਤੇ ਰੱਖਣ ਲਈ ਰੋਜ਼ਾਨਾ ਰੀਮਾਈਂਡਰਾਂ ਅਤੇ ਨੋਟੀਫਿਕੇਸ਼ਨਾਂ ਨਾਲ ਆਪਣੇ ਪਾਠ ਦੇ ਦਿਨਾਂ ਦੇ ਕ੍ਰਮ ਅਤੇ ਲਗਾਤਾਰਤਾ ਨੂੰ ਟ੍ਰੈਕ ਕਰੋ।"
        ),
        "Test your knowledge with gamified daily Gurbani questions." to mapOf(
            DisplayLanguage.English to "Test your knowledge with gamified daily Gurbani questions.",
            DisplayLanguage.Hindi to "रोमांचक दैनिक गुरबानी प्रश्नों के साथ अपने ज्ञान का परीक्षण करें।",
            DisplayLanguage.Punjabi to "ਰੋਜ਼ਾਨਾ ਰੋਮਾਂਚਕ ਗੁਰਬਾਣੀ ਪ੍ਰਸ਼ਨਾਂ ਨਾਲ ਆਪਣੇ ਗਿਆਨ ਦੀ ਪਰਖ ਕਰੋ।"
        ),
        "Gurbani Database" to mapOf(
            DisplayLanguage.English to "Gurbani Database",
            DisplayLanguage.Hindi to "गुरबानी डेटाबेस",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਡੇਟਾਬੇਸ"
        ),
        "All Gurbani texts & translations, transliterations are taken from https://www.shabados.com/" to mapOf(
            DisplayLanguage.English to "All Gurbani texts & translations, transliterations are taken from https://www.shabados.com/",
            DisplayLanguage.Hindi to "सभी गुरबानी पाठ, अनुवाद और लिप्यंतरण https://www.shabados.com/ से लिए गए हैं",
            DisplayLanguage.Punjabi to "ਸਾਰੇ ਗੁਰਬਾਣੀ ਪਾਠ, ਅਨੁਵਾਦ ਅਤੇ ਲਿਪੀਅੰਤਰਨ https://www.shabados.com/ ਤੋਂ ਲਏ ਗਏ ਹਨ"
        ),
        "Parsed from https://data.gurbaninow.com/" to mapOf(
            DisplayLanguage.English to "Sri Harmandir Sahib Hukamnama parsed from https://data.gurbaninow.com/",
            DisplayLanguage.Hindi to "श्री हरिमंदिर साहिब हुकमनामा https://data.gurbaninow.com/ से लिया गया",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਹਰਿਮੰਦਰ ਸਾਹਿਬ ਹੁਕਮਨਾਮਾ https://data.gurbaninow.com/ ਤੋਂ ਲਿਆ ਗਿਆ"
        ),
        "Adjust fonts, translations, transliterations, and themes for comfortable paath." to mapOf(
            DisplayLanguage.English to "Adjust fonts, translations, transliterations, and themes for comfortable paath.",
            DisplayLanguage.Hindi to "सुविधाजनक पाठ के लिए फ़ॉन्ट, अनुवाद, लिप्यंतरण और थीम समायोजित करें।",
            DisplayLanguage.Punjabi to "ਸੁਖਾਲੇ ਪਾਠ ਲਈ ਅੱਖਰ, ਅਨੁਵਾਦ, ਲਿਪੀਅੰਤਰਨ ਅਤੇ ਥੀਮ ਬਦਲੋ।"
        ),
        "Audio" to mapOf(
            DisplayLanguage.English to "Audio",
            DisplayLanguage.Hindi to "ऑडियो",
            DisplayLanguage.Punjabi to "ਆਡੀਓ"
        ),
        "Synced audio playback helps with pronunciation and immersion." to mapOf(
            DisplayLanguage.English to "Synced audio playback helps with pronunciation and immersion.",
            DisplayLanguage.Hindi to "सिंक किया गया ऑडियो प्लेबैक उच्चारण और समझ में मदद करता है।",
            DisplayLanguage.Punjabi to "ਸਿੰਕ ਕੀਤਾ ਆਡੀਓ ਪਲੇਬੈਕ ਉਚਾਰਨ ਅਤੇ ਮਨ ਜੋੜਨ ਵਿੱਚ ਮਦਦ ਕਰਦਾ ਹੈ।"
        ),
        "Daily Progress" to mapOf(
            DisplayLanguage.English to "Daily Progress",
            DisplayLanguage.Hindi to "दैनिक प्रगति",
            DisplayLanguage.Punjabi to "ਰੋਜ਼ਾਨਾ ਪ੍ਰਗਤੀ"
        ),
        "Track streaks and Nitnem consistency." to mapOf(
            DisplayLanguage.English to "Track streaks and Nitnem consistency.",
            DisplayLanguage.Hindi to "दैनिक क्रम और नितनेम निरंतरता को ट्रैक करें।",
            DisplayLanguage.Punjabi to "ਦਿਨਾਂ ਦੇ ਕ੍ਰਮ ਅਤੇ ਨਿਤਨੇਮ ਦੀ ਲਗਾਤਾਰਤਾ ਨੂੰ ਟਰੈਕ ਕਰੋ।"
        ),
        "Database Source" to mapOf(
            DisplayLanguage.English to "Database Source",
            DisplayLanguage.Hindi to "डेटाबेस स्रोत",
            DisplayLanguage.Punjabi to "ਡੇਟਾਬੇਸ ਸਰੋਤ"
        ),
        "All Gurbani texts & translations, transliterations are taken from ShabadOS Github Repository" to mapOf(
            DisplayLanguage.English to "All Gurbani texts & translations, transliterations are taken from ShabadOS Github Repository",
            DisplayLanguage.Hindi to "सभी गुरबानी पाठ, अनुवाद और लिप्यंतरण ShabadOS गिथब रिपॉजिटरी से लिए गए हैं",
            DisplayLanguage.Punjabi to "ਸਾਰੇ ਗੁਰਬਾਣੀ ਪਾਠ, ਅਨੁਵਾਦ ਅਤੇ ਲਿਪੀਅੰਤਰਨ ShabadOS ਗਿਥਬ ਰਿਪੋਜ਼ਟਰੀ ਤੋਂ ਲਏ ਗਏ ਹਨ"
        ),
        "Continue" to mapOf(
            DisplayLanguage.English to "Continue",
            DisplayLanguage.Hindi to "जारी रखें",
            DisplayLanguage.Punjabi to "ਜਾਰੀ ਰੱਖੋ"
        ),
        "Recommended" to mapOf(
            DisplayLanguage.English to "Recommended",
            DisplayLanguage.Hindi to "अनुशंसित",
            DisplayLanguage.Punjabi to "ਸਿਫਾਰਸ਼ੀ"
        ),
        "Sun Rising" to mapOf(
            DisplayLanguage.English to "Sun Rising",
            DisplayLanguage.Hindi to "सूर्योदय",
            DisplayLanguage.Punjabi to "ਸੂਰਜ ਚੜ੍ਹਨਾ"
        ),
        "Dawn Prayer" to mapOf(
            DisplayLanguage.English to "Dawn Prayer",
            DisplayLanguage.Hindi to "उषा काल प्रार्थना",
            DisplayLanguage.Punjabi to "ਅੰਮ੍ਰਿਤ ਵੇਲੇ ਦੀ ਪ੍ਰਾਰਥਨਾ"
        ),
        "Sun Rised" to mapOf(
            DisplayLanguage.English to "Sun Rised",
            DisplayLanguage.Hindi to "सूर्य उदय",
            DisplayLanguage.Punjabi to "ਸੂਰਜ ਚੜ੍ਹਿਆ"
        ),
        "Afternoon" to mapOf(
            DisplayLanguage.English to "Afternoon",
            DisplayLanguage.Hindi to "दोपहर",
            DisplayLanguage.Punjabi to "ਦੁਪਹਿਰ"
        ),
        "Morning" to mapOf(
            DisplayLanguage.English to "Morning",
            DisplayLanguage.Hindi to "सुबह",
            DisplayLanguage.Punjabi to "ਸਵੇਰ"
        ),
        "Evening" to mapOf(
            DisplayLanguage.English to "Evening",
            DisplayLanguage.Hindi to "शाम",
            DisplayLanguage.Punjabi to "ਸ਼ਾਮ"
        ),
        "Night" to mapOf(
            DisplayLanguage.English to "Night",
            DisplayLanguage.Hindi to "रात",
            DisplayLanguage.Punjabi to "ਰਾਤ"
        ),
        "Sunset Prayer" to mapOf(
            DisplayLanguage.English to "Sunset Prayer",
            DisplayLanguage.Hindi to "सूर्यास्त प्रार्थना",
            DisplayLanguage.Punjabi to "ਸੰਧਿਆ ਵੇਲੇ ਦੀ ਪ੍ਰਾਰਥਨਾ"
        ),
        "Soon to be Night" to mapOf(
            DisplayLanguage.English to "Soon to be Night",
            DisplayLanguage.Hindi to "संध्या काल",
            DisplayLanguage.Punjabi to "ਰਾਤ ਹੋਣ ਵਾਲੀ ਹੈ"
        ),
        "Midnight" to mapOf(
            DisplayLanguage.English to "Midnight",
            DisplayLanguage.Hindi to "मध्यरात्रि",
            DisplayLanguage.Punjabi to "ਅੱਧੀ ਰਾਤ"
        ),
        "Midnight Meditation" to mapOf(
            DisplayLanguage.English to "Midnight Meditation",
            DisplayLanguage.Hindi to "मध्यरात्रि ध्यान",
            DisplayLanguage.Punjabi to "ਅੱਧੀ ਰਾਤ ਦਾ ਸਿਮਰਨ"
        ),
        "Connect with the Divine in deep silence." to mapOf(
            DisplayLanguage.English to "Connect with the Divine in deep silence.",
            DisplayLanguage.Hindi to "गहरे सन्नाटे में परमात्मा से जुड़ें।",
            DisplayLanguage.Punjabi to "ਗੂੜ੍ਹੇ ਟਿਕਾਅ ਵਿੱਚ ਪਰਮਾਤਮਾ ਨਾਲ ਜੁੜੋ।"
        ),
        "Peaceful silence of midnight aids concentration." to mapOf(
            DisplayLanguage.English to "Peaceful silence of midnight aids concentration.",
            DisplayLanguage.Hindi to "मध्यरात्रि की शांति ध्यान लगाने में सहायक है।",
            DisplayLanguage.Punjabi to "ਅੱਧੀ ਰਾਤ ਦਾ ਸ਼ਾਂਤਮਈ ਟਿਕਾਅ ਸਿਮਰਨ ਵਿੱਚ ਸਹਾਇਕ ਹੈ।"
        ),
        "AI Quiz" to mapOf(
            DisplayLanguage.English to "AI Quiz",
            DisplayLanguage.Hindi to "AI क्विज़",
            DisplayLanguage.Punjabi to "AI ਕਵਿਜ਼"
        ),
        "Meditate" to mapOf(
            DisplayLanguage.English to "Meditate",
            DisplayLanguage.Hindi to "ध्यान",
            DisplayLanguage.Punjabi to "ਸਿਮਰਨ"
        ),
        "Sync" to mapOf(
            DisplayLanguage.English to "Sync",
            DisplayLanguage.Hindi to "सिंक",
            DisplayLanguage.Punjabi to "ਸਿੰਕ"
        ),
        "Practice Gurbani Through Interactive Questions" to mapOf(
            DisplayLanguage.English to "Practice Gurbani Through Interactive Questions",
            DisplayLanguage.Hindi to "इंटरैक्टिव प्रश्नों के माध्यम से गुरबानी का अभ्यास करें",
            DisplayLanguage.Punjabi to "ਇੰਟਰਐਕਟਿਵ ਪ੍ਰਸ਼ਨਾਂ ਰਾਹੀਂ ਗੁਰਬਾਣੀ ਦਾ ਅਭਿਆਸ ਕਰੋ"
        ),
        "Naam Simran • Breath • Stillness" to mapOf(
            DisplayLanguage.English to "Naam Simran • Breath • Stillness",
            DisplayLanguage.Hindi to "नाम सिमरन • श्वास • स्थिरता",
            DisplayLanguage.Punjabi to "ਨਾਮ ਸਿਮਰਨ • ਸੁਆਸ • ਟਿਕਾਅ"
        ),
        "Manage Audio Synchronization" to mapOf(
            DisplayLanguage.English to "Manage Audio Synchronization",
            DisplayLanguage.Hindi to "ऑडियो सिंक्रोनाइजेशन प्रबंधित करें",
            DisplayLanguage.Punjabi to "ਆਡੀਓ ਸਿੰਕ੍ਰੋਨਾਈਜ਼ੇਸ਼ਨ ਦਾ ਪ੍ਰਬੰਧਨ ਕਰੋ"
        ),
        "Personalise your Nitnem experience" to mapOf(
            DisplayLanguage.English to "Personalise your Nitnem experience",
            DisplayLanguage.Hindi to "अपने नितनेम अनुभव को अनुकूलित करें",
            DisplayLanguage.Punjabi to "ਆਪਣੇ ਨਿਤਨੇਮ ਅਨੁਭਵ ਨੂੰ ਨਿੱਜੀ ਬਣਾਓ"
        ),
        "No bookmarks yet" to mapOf(
            DisplayLanguage.English to "No bookmarks yet",
            DisplayLanguage.Hindi to "अभी तक कोई बुकमार्क नहीं",
            DisplayLanguage.Punjabi to "ਅਜੇ ਕੋਈ ਬੁੱਕਮਾਰਕ ਨਹੀਂ"
        ),
        "Listen" to mapOf(
            DisplayLanguage.English to "Listen",
            DisplayLanguage.Hindi to "सुनें",
            DisplayLanguage.Punjabi to "ਸੁਣੋ"
        ),
        "Saved" to mapOf(
            DisplayLanguage.English to "Saved",
            DisplayLanguage.Hindi to "सहेजे गए",
            DisplayLanguage.Punjabi to "ਸੰਭਾਲੇ ਹੋਏ"
        ),
        "Save" to mapOf(
            DisplayLanguage.English to "Save",
            DisplayLanguage.Hindi to "सहेजें",
            DisplayLanguage.Punjabi to "ਸੰਭਾਲੋ"
        ),
        "Emerald" to mapOf(
            DisplayLanguage.English to "Emerald",
            DisplayLanguage.Hindi to "पन्ना",
            DisplayLanguage.Punjabi to "ਹਰਾ (ਪੰਨਾ)"
        ),
        "Sapphire" to mapOf(
            DisplayLanguage.English to "Sapphire",
            DisplayLanguage.Hindi to "नीलम",
            DisplayLanguage.Punjabi to "ਨੀਲਾ (ਨੀਲਮ)"
        ),
        "Royal" to mapOf(
            DisplayLanguage.English to "Royal",
            DisplayLanguage.Hindi to "शाही",
            DisplayLanguage.Punjabi to "ਸ਼ਾਹੀ"
        ),
        "Sunset" to mapOf(
            DisplayLanguage.English to "Sunset",
            DisplayLanguage.Hindi to "सूर्यास्त",
            DisplayLanguage.Punjabi to "ਸੂਰਜ ਡੁੱਬਣਾ"
        ),
        "Ashtpadi" to mapOf(
            DisplayLanguage.English to "Ashtpadi",
            DisplayLanguage.Hindi to "अष्टपदी",
            DisplayLanguage.Punjabi to "ਅਸਟਪਦੀ"
        ),
        "Pauri" to mapOf(
            DisplayLanguage.English to "Pauri",
            DisplayLanguage.Hindi to "पौड़ी",
            DisplayLanguage.Punjabi to "ਪਉੜੀ"
        ),
        "Verse" to mapOf(
            DisplayLanguage.English to "Verse",
            DisplayLanguage.Hindi to "पद",
            DisplayLanguage.Punjabi to "ਛੰਦ"
        ),
        "Savaiya" to mapOf(
            DisplayLanguage.English to "Savaiya",
            DisplayLanguage.Hindi to "सवैया",
            DisplayLanguage.Punjabi to "ਸਵਈਆ"
        ),
        "Shabad" to mapOf(
            DisplayLanguage.English to "Shabad",
            DisplayLanguage.Hindi to "शब्द",
            DisplayLanguage.Punjabi to "ਸ਼ਬਦ"
        ),
        "Salok" to mapOf(
            DisplayLanguage.English to "Salok",
            DisplayLanguage.Hindi to "सलोक",
            DisplayLanguage.Punjabi to "ਸਲੋਕ"
        ),
        "Stanza" to mapOf(
            DisplayLanguage.English to "Stanza",
            DisplayLanguage.Hindi to "छंद",
            DisplayLanguage.Punjabi to "ਬੰਦ"
        ),
        "Bani Information" to mapOf(
            DisplayLanguage.English to "Bani Information",
            DisplayLanguage.Hindi to "बाणी जानकारी",
            DisplayLanguage.Punjabi to "ਬਾਣੀ ਦੀ ਜਾਣਕਾਰੀ"
        ),
        "Author" to mapOf(
            DisplayLanguage.English to "Author",
            DisplayLanguage.Hindi to "लेखक",
            DisplayLanguage.Punjabi to "ਲੇਖਕ"
        ),
        "Source Ang" to mapOf(
            DisplayLanguage.English to "Source Ang",
            DisplayLanguage.Hindi to "स्रोत अंग",
            DisplayLanguage.Punjabi to "ਸਰੋਤ ਅੰਗ"
        ),
        "Structure" to mapOf(
            DisplayLanguage.English to "Structure",
            DisplayLanguage.Hindi to "संरचना",
            DisplayLanguage.Punjabi to "ਬਣਤਰ"
        ),
        "Estimated Time" to mapOf(
            DisplayLanguage.English to "Estimated Time",
            DisplayLanguage.Hindi to "अनुमानित समय",
            DisplayLanguage.Punjabi to "ਅਨੁਮਾਨਿਤ ਸਮਾਂ"
        ),
        "Credits & Attribution" to mapOf(
            DisplayLanguage.English to "Credits & Attribution",
            DisplayLanguage.Hindi to "क्रेडिट और आभार",
            DisplayLanguage.Punjabi to "ਕ੍ਰੈਡਿਟ ਅਤੇ ਸਰੋਤ ਸਿਹਰਾ"
        ),
        "Close" to mapOf(
            DisplayLanguage.English to "Close",
            DisplayLanguage.Hindi to "बंद करें",
            DisplayLanguage.Punjabi to "ਬੰਦ ਕਰੋ"
        ),
        "Ready to begin" to mapOf(
            DisplayLanguage.English to "Ready to begin",
            DisplayLanguage.Hindi to "शुरू करने के लिए तैयार",
            DisplayLanguage.Punjabi to "ਸ਼ੁਰੂ ਕਰਨ ਲਈ ਤਿਆਰ"
        ),
        "Inhale • Wahe" to mapOf(
            DisplayLanguage.English to "Inhale • Wahe",
            DisplayLanguage.Hindi to "श्वास अंदर • वाहे",
            DisplayLanguage.Punjabi to "ਸੁਆਸ ਅੰਦਰ • ਵਾਹਿ"
        ),
        "Exhale • Guru" to mapOf(
            DisplayLanguage.English to "Exhale • Guru",
            DisplayLanguage.Hindi to "श्वास बाहर • गुरु",
            DisplayLanguage.Punjabi to "ਸੁਆਸ ਬਾਹਰ • ਗੁਰੂ"
        ),
        "Listen inwardly" to mapOf(
            DisplayLanguage.English to "Listen inwardly",
            DisplayLanguage.Hindi to "भीतर सुनें",
            DisplayLanguage.Punjabi to "ਅੰਦਰ ਧਿਆਨ ਨਾਲ ਸੁਣੋ"
        ),
        "Internal stillness" to mapOf(
            DisplayLanguage.English to "Internal stillness",
            DisplayLanguage.Hindi to "आंतरिक स्थिरता",
            DisplayLanguage.Punjabi to "ਅੰਦਰੂਨੀ ਟਿਕਾਅ"
        ),
        "Session completed peacefully" to mapOf(
            DisplayLanguage.English to "Session completed peacefully",
            DisplayLanguage.Hindi to "सत्र शांतिपूर्वक पूरा हुआ",
            DisplayLanguage.Punjabi to "ਅਭਿਆਸ ਸ਼ਾਂਤੀ ਨਾਲ ਪੂਰਾ ਹੋਇਆ"
        ),
        "Stay present with the breath" to mapOf(
            DisplayLanguage.English to "Stay present with the breath",
            DisplayLanguage.Hindi to "श्वास के साथ उपस्थित रहें",
            DisplayLanguage.Punjabi to "ਸੁਆਸਾਂ ਨਾਲ ਜੁੜੇ ਰਹੋ"
        ),
        "Tap the circle to begin" to mapOf(
            DisplayLanguage.English to "Tap the circle to begin",
            DisplayLanguage.Hindi to "शुरू करने के लिए चक्र पर टैप करें",
            DisplayLanguage.Punjabi to "ਸ਼ੁਰੂ ਕਰਨ ਲਈ ਗੋਲੇ 'ਤੇ ਟੈਪ ਕਰੋ"
        ),
        "Pause" to mapOf(
            DisplayLanguage.English to "Pause",
            DisplayLanguage.Hindi to "विराम",
            DisplayLanguage.Punjabi to "ਰੋਕੋ"
        ),
        "Start" to mapOf(
            DisplayLanguage.English to "Start",
            DisplayLanguage.Hindi to "शुरू",
            DisplayLanguage.Punjabi to "ਸ਼ੁਰੂ"
        ),
        "Reset" to mapOf(
            DisplayLanguage.English to "Reset",
            DisplayLanguage.Hindi to "पुनर्स्थापित",
            DisplayLanguage.Punjabi to "ਮੁੜ-ਸ਼ੁਰੂ"
        ),
        "Saas Saas" to mapOf(
            DisplayLanguage.English to "Saas Saas",
            DisplayLanguage.Hindi to "सास सास",
            DisplayLanguage.Punjabi to "ਸਾਸਿ ਸਾਸਿ"
        ),
        "Breath Focus" to mapOf(
            DisplayLanguage.English to "Breath Focus",
            DisplayLanguage.Hindi to "श्वास ध्यान",
            DisplayLanguage.Punjabi to "ਸੁਆਸਾਂ ਦਾ ਧਿਆਨ"
        ),
        "Dhyaan" to mapOf(
            DisplayLanguage.English to "Dhyaan",
            DisplayLanguage.Hindi to "ध्यान",
            DisplayLanguage.Punjabi to "ਧਿਆਨ"
        ),
        "Naam Listening" to mapOf(
            DisplayLanguage.English to "Naam Listening",
            DisplayLanguage.Hindi to "नाम श्रवण",
            DisplayLanguage.Punjabi to "ਨਾਮ ਸੁਣਨਾ"
        ),
        "Anhad" to mapOf(
            DisplayLanguage.English to "Anhad",
            DisplayLanguage.Hindi to "अनहद",
            DisplayLanguage.Punjabi to "ਅਨਹਦ"
        ),
        "Stillness" to mapOf(
            DisplayLanguage.English to "Stillness",
            DisplayLanguage.Hindi to "स्थिरता",
            DisplayLanguage.Punjabi to "ਟਿਕਾਅ"
        ),
        "Inhale 'Wahe' mentally and exhale 'Guru'. Synchronize awareness with each breath cycle." to mapOf(
            DisplayLanguage.English to "Inhale 'Wahe' mentally and exhale 'Guru'. Synchronize awareness with each breath cycle.",
            DisplayLanguage.Hindi to "मन ही मन 'वाहे' श्वास लें और 'गुरु' बाहर निकालें। प्रत्येक श्वास चक्र के साथ जागरूकता सिंक करें।",
            DisplayLanguage.Punjabi to "ਸੁਆਸ ਅੰਦਰ ਲੈਣ ਵੇਲੇ 'ਵਾਹਿ' ਅਤੇ ਸੁਆਸ ਬਾਹਰ ਕੱਢਣ ਵੇਲੇ 'ਗੁਰੂ' ਦਾ ਚਿੰਤਨ ਕਰੋ।"
        ),
        "Recite Waheguru gently and listen deeply to the internal resonance of the Shabad." to mapOf(
            DisplayLanguage.English to "Recite Waheguru gently and listen deeply to the internal resonance of the Shabad.",
            DisplayLanguage.Hindi to "सहजता से वाहेगुरु का जाप करें और शबद की आंतरिक प्रतिध्वनि को गहराई से सुनें।",
            DisplayLanguage.Punjabi to "ਸਹਿਜਤਾ ਨਾਲ ਵਾਹਿਗੁਰੂ ਦਾ ਜਾਪ ਕਰੋ ਅਤੇ ਸ਼ਬਦ ਦੀ ਅੰਦਰੂਨੀ ਆਵਾਜ਼ ਨੂੰ ਗਹਿਰਾਈ ਨਾਲ ਸੁਣੋ।"
        ),
        "Remain internally silent and observe the subtle vibration of Naam within." to mapOf(
            DisplayLanguage.English to "Remain internally silent and observe the subtle vibration of Naam within.",
            DisplayLanguage.Hindi to "आंतरिक रूप से शांत रहें और भीतर नाम के सूक्ष्म कंपन का अनुभव करें।",
            DisplayLanguage.Punjabi to "ਅੰਦਰੂਨੀ ਤੌਰ 'ਤੇ ਸ਼ਾਂਤ ਰਹੋ ਅਤੇ ਅੰਦਰ ਨਾਮ ਦੇ ਸੂਖਮ ਪ੍ਰਕਾਸ਼ ਨੂੰ ਮਹਿਸੂਸ ਕਰੋ।"
        ),
        "Meditate upon the Divine with every fiber of your being." to mapOf(
            DisplayLanguage.English to "Meditate upon the Divine with every fiber of your being.",
            DisplayLanguage.Hindi to "अपने रोम-रोम से परमात्मा का ध्यान करें।",
            DisplayLanguage.Punjabi to "ਆਪਣੇ ਰੋਮ-ਰੋਮ ਨਾਲ ਪਰਮਾਤਮਾ ਦਾ ਸਿਮਰਨ ਕਰੋ।"
        ),
        "No quiz available" to mapOf(
            DisplayLanguage.English to "No quiz available",
            DisplayLanguage.Hindi to "कोई प्रश्नोत्तरी उपलब्ध नहीं है",
            DisplayLanguage.Punjabi to "ਕੋਈ ਕਵਿਜ਼ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
        ),
        "Quiz Complete" to mapOf(
            DisplayLanguage.English to "Quiz Complete",
            DisplayLanguage.Hindi to "प्रश्नोत्तरी पूर्ण",
            DisplayLanguage.Punjabi to "ਕਵਿਜ਼ ਪੂਰੀ ਹੋਈ"
        ),
        "Final Score" to mapOf(
            DisplayLanguage.English to "Final Score",
            DisplayLanguage.Hindi to "अंतिम स्कोर",
            DisplayLanguage.Punjabi to "ਕੁੱਲ ਅੰਕ"
        ),
        "Correct" to mapOf(
            DisplayLanguage.English to "Correct",
            DisplayLanguage.Hindi to "सही",
            DisplayLanguage.Punjabi to "ਸਹੀ"
        ),
        "Mistakes" to mapOf(
            DisplayLanguage.English to "Mistakes",
            DisplayLanguage.Hindi to "गलतियां",
            DisplayLanguage.Punjabi to "ਗਲਤੀਆਂ"
        ),
        "Restart Quiz" to mapOf(
            DisplayLanguage.English to "Restart Quiz",
            DisplayLanguage.Hindi to "प्रश्नोत्तरी पुनरारंभ करें",
            DisplayLanguage.Punjabi to "ਕਵਿਜ਼ ਮੁੜ-ਸ਼ੁਰੂ ਕਰੋ"
        ),
        "Review Mistakes" to mapOf(
            DisplayLanguage.English to "Review Mistakes",
            DisplayLanguage.Hindi to "गलतियों की समीक्षा करें",
            DisplayLanguage.Punjabi to "ਗਲਤੀਆਂ ਦੀ ਸਮੀਖਿਆ ਕਰੋ"
        ),
        "Question" to mapOf(
            DisplayLanguage.English to "Question",
            DisplayLanguage.Hindi to "प्रश्न",
            DisplayLanguage.Punjabi to "ਪ੍ਰਸ਼ਨ"
        ),
        "Correct Answer" to mapOf(
            DisplayLanguage.English to "Correct Answer",
            DisplayLanguage.Hindi to "सही उत्तर",
            DisplayLanguage.Punjabi to "ਸਹੀ ਉੱਤਰ"
        ),
        "Score" to mapOf(
            DisplayLanguage.English to "Score",
            DisplayLanguage.Hindi to "स्कोर",
            DisplayLanguage.Punjabi to "ਅੰਕ"
        ),
        "Fill Missing Word" to mapOf(
            DisplayLanguage.English to "Fill Missing Word",
            DisplayLanguage.Hindi to "लापता शब्द भरें",
            DisplayLanguage.Punjabi to "ਖਾਲੀ ਥਾਂ ਭਰੋ"
        ),
        "Translation Match" to mapOf(
            DisplayLanguage.English to "Translation Match",
            DisplayLanguage.Hindi to "अनुवाद मिलान",
            DisplayLanguage.Punjabi to "ਅਨੁਵਾਦ ਦਾ ਮਿਲਾਨ"
        ),
        "Next Line" to mapOf(
            DisplayLanguage.English to "Next Line",
            DisplayLanguage.Hindi to "अगली पंक्ति",
            DisplayLanguage.Punjabi to "ਅਗਲੀ ਤੁਕ"
        ),
        "True or False" to mapOf(
            DisplayLanguage.English to "True or False",
            DisplayLanguage.Hindi to "सही या गलत",
            DisplayLanguage.Punjabi to "ਸਹੀ ਜਾਂ ਗਲਤ"
        ),
        "Arrange Sequence" to mapOf(
            DisplayLanguage.English to "Arrange Sequence",
            DisplayLanguage.Hindi to "क्रम व्यवस्थित करें",
            DisplayLanguage.Punjabi to "ਸਹੀ ਕ੍ਰਮ ਵਿੱਚ ਲਾਓ"
        ),
        "Hint" to mapOf(
            DisplayLanguage.English to "Hint",
            DisplayLanguage.Hindi to "संकेत",
            DisplayLanguage.Punjabi to "ਇਸ਼ਾਰਾ"
        ),
        "Tap words below..." to mapOf(
            DisplayLanguage.English to "Tap words below...",
            DisplayLanguage.Hindi to "नीचे दिए गए शब्दों पर टैप करें...",
            DisplayLanguage.Punjabi to "ਹੇਠਾਂ ਦਿੱਤੇ ਸ਼ਬਦਾਂ 'ਤੇ ਟੈਪ ਕਰੋ..."
        ),
        "Finish Quiz" to mapOf(
            DisplayLanguage.English to "Finish Quiz",
            DisplayLanguage.Hindi to "प्रश्नोत्तरी समाप्त करें",
            DisplayLanguage.Punjabi to "ਕਵਿਜ਼ ਖ਼ਤਮ ਕਰੋ"
        ),
        "Arrange the Gurbani line correctly" to mapOf(
            DisplayLanguage.English to "Arrange the Gurbani line correctly",
            DisplayLanguage.Hindi to "गुरबाणी पंक्ति को सही ढंग से व्यवस्थित करें",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ ਤੁਕ ਨੂੰ ਸਹੀ ਕ੍ਰਮ ਵਿੱਚ ਲਾਓ"
        ),
        "True" to mapOf(
            DisplayLanguage.English to "True",
            DisplayLanguage.Hindi to "सही",
            DisplayLanguage.Punjabi to "ਸਹੀ"
        ),
        "False" to mapOf(
            DisplayLanguage.English to "False",
            DisplayLanguage.Hindi to "गलत",
            DisplayLanguage.Punjabi to "ਗਲਤ"
        ),
        "Guru Nanak Dev Ji" to mapOf(
            DisplayLanguage.English to "Guru Nanak Dev Ji",
            DisplayLanguage.Hindi to "गुरु नानक देव जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ"
        ),
        "Guru Gobind Singh Ji" to mapOf(
            DisplayLanguage.English to "Guru Gobind Singh Ji",
            DisplayLanguage.Hindi to "गुरु गोबिंद सिंह जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਗੋਬਿੰਦ ਸਿੰਘ ਜੀ"
        ),
        "Guru Amar Das Ji" to mapOf(
            DisplayLanguage.English to "Guru Amar Das Ji",
            DisplayLanguage.Hindi to "गुरु अमर दास जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਅਮਰ ਦਾਸ ਜੀ"
        ),
        "Multiple Gurus" to mapOf(
            DisplayLanguage.English to "Multiple Gurus",
            DisplayLanguage.Hindi to "अनेक गुरु",
            DisplayLanguage.Punjabi to "ਅਨੇਕਾਂ ਗੁਰੂ ਸਾਹਿਬਾਨ"
        ),
        "Guru Arjan Dev Ji" to mapOf(
            DisplayLanguage.English to "Guru Arjan Dev Ji",
            DisplayLanguage.Hindi to "गुरु अर्जुन देव जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ"
        ),
        "Traditional" to mapOf(
            DisplayLanguage.English to "Traditional",
            DisplayLanguage.Hindi to "पारंपरिक",
            DisplayLanguage.Punjabi to "ਪੁਰਾਤਨ ਮਰਯਾਦਾ"
        ),
        "Guru Nanak Dev Ji & Guru Arjan Dev Ji" to mapOf(
            DisplayLanguage.English to "Guru Nanak Dev Ji & Guru Arjan Dev Ji",
            DisplayLanguage.Hindi to "गुरु नानक देव जी और गुरु अर्जुन देव जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਅਤੇ ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ"
        ),
        "Guru Tegh Bahadur Ji" to mapOf(
            DisplayLanguage.English to "Guru Tegh Bahadur Ji",
            DisplayLanguage.Hindi to "गुरु तेग बहादुर जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਤੇਗ ਬਹਾਦਰ ਜੀ"
        ),
        "Guru Nanak Dev Ji & Bhagats" to mapOf(
            DisplayLanguage.English to "Guru Nanak Dev Ji & Bhagats",
            DisplayLanguage.Hindi to "गुरु नानक देव जी और भगत",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਅਤੇ ਭਗਤ ਸਾਹਿਬਾਨ"
        ),
        "Sri Guru Granth Sahib Ji" to mapOf(
            DisplayLanguage.English to "Sri Guru Granth Sahib Ji",
            DisplayLanguage.Hindi to "श्री गुरु ग्रंथ साहिब जी",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ"
        ),
        "Sri Guru Granth Sahib Paath Bhog (Ragmala)" to mapOf(
            DisplayLanguage.English to "Sri Guru Granth Sahib Paath Bhog (Ragmala)",
            DisplayLanguage.Hindi to "श्री गुरु ग्रंथ साहिब पाठ भोग (रागमाला)",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਪਾਠ ਭੋਗ (ਰਾਗਮਾਲਾ)"
        ),
   "Daily Hukamnama" to mapOf(
    DisplayLanguage.English to "Daily Hukamnama",
    DisplayLanguage.Hindi to "रोज़ का हुकमनामा",
    DisplayLanguage.Punjabi to "ਰੋਜ਼ ਦਾ ਹੁਕਮਨਾਮਾ"
),
        "Fetching Hukamnama..." to mapOf(
            DisplayLanguage.English to "Fetching Hukamnama...",
            DisplayLanguage.Hindi to "हुकमनामा प्राप्त किया जा रहा है...",
            DisplayLanguage.Punjabi to "ਹੁਕਮਨਾਮਾ ਲੋਡ ਕੀਤਾ ਜਾ ਰਿਹਾ ਹੈ..."
        ),
        "Guru Granth Sahib Ji" to mapOf(
            DisplayLanguage.English to "Guru Granth Sahib Ji",
            DisplayLanguage.Hindi to "गुरु ग्रंथ साहिब जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ"
        ),
        "Dasam Granth" to mapOf(
            DisplayLanguage.English to "Dasam Granth",
            DisplayLanguage.Hindi to "दशम ग्रंथ",
            DisplayLanguage.Punjabi to "ਦਸਮ ਗ੍ਰੰਥ"
        ),
        "Ang" to mapOf(
            DisplayLanguage.English to "Ang",
            DisplayLanguage.Hindi to "अंग",
            DisplayLanguage.Punjabi to "ਅੰਗ"
        ),
        "Guru Ram Das Ji" to mapOf(
            DisplayLanguage.English to "Guru Ram Das Ji",
            DisplayLanguage.Hindi to "गुरु राम दास जी",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਰਾਮਦਾਸ ਜੀ"
        ),
        "Bhagat Kabir Ji" to mapOf(
            DisplayLanguage.English to "Bhagat Kabir Ji",
            DisplayLanguage.Hindi to "भगत कबीर जी",
            DisplayLanguage.Punjabi to "ਭਗਤ ਕਬੀਰ ਜੀ"
        ),
        "Bhagat Ravidas Ji" to mapOf(
            DisplayLanguage.English to "Bhagat Ravidas Ji",
            DisplayLanguage.Hindi to "भगत रविदास जी",
            DisplayLanguage.Punjabi to "ਭਗਤ ਰਵਿਦਾਸ ਜੀ"
        ),
        "Bhagat Namdev Ji" to mapOf(
            DisplayLanguage.English to "Bhagat Namdev Ji",
            DisplayLanguage.Hindi to "भगत नामदेव जी",
            DisplayLanguage.Punjabi to "ਭਗਤ ਨਾਮਦੇਵ ਜੀ"
        ),
        "Bhagat Farid Ji" to mapOf(
            DisplayLanguage.English to "Bhagat Farid Ji",
            DisplayLanguage.Hindi to "भगत फरीद जी",
            DisplayLanguage.Punjabi to "ਭਗਤ फरीद जी"
        ),
        "Sheikh Farid Ji" to mapOf(
            DisplayLanguage.English to "Sheikh Farid Ji",
            DisplayLanguage.Hindi to "शेख फरीद जी",
            DisplayLanguage.Punjabi to "ਸੇਖ ਫਰੀਦ ਜੀ"
        ),
        "Asa" to mapOf(
            DisplayLanguage.English to "Asa",
            DisplayLanguage.Hindi to "आसा",
            DisplayLanguage.Punjabi to "ਆਸਾ"
        ),
        "Sorath" to mapOf(
            DisplayLanguage.English to "Sorath",
            DisplayLanguage.Hindi to "सोरठि",
            DisplayLanguage.Punjabi to "ਸੋਰਠਿ"
        ),
        "Dhanasari" to mapOf(
            DisplayLanguage.English to "Dhanasari",
            DisplayLanguage.Hindi to "धनासरी",
            DisplayLanguage.Punjabi to "ਧਨਾਸਰੀ"
        ),
        "Jaitsri" to mapOf(
            DisplayLanguage.English to "Jaitsri",
            DisplayLanguage.Hindi to "जैतसरी",
            DisplayLanguage.Punjabi to "ਜੈਤਸਰੀ"
        ),
        "Todi" to mapOf(
            DisplayLanguage.English to "Todi",
            DisplayLanguage.Hindi to "टोडी",
            DisplayLanguage.Punjabi to "ਟੋਡੀ"
        ),
        "Suhi" to mapOf(
            DisplayLanguage.English to "Suhi",
            DisplayLanguage.Hindi to "सूही",
            DisplayLanguage.Punjabi to "ਸੂਹੀ"
        ),
        "Bilaval" to mapOf(
            DisplayLanguage.English to "Bilaval",
            DisplayLanguage.Hindi to "बिलावलु",
            DisplayLanguage.Punjabi to "ਬਿਲਾਵਲੁ"
        ),
        "Ramkali" to mapOf(
            DisplayLanguage.English to "Ramkali",
            DisplayLanguage.Hindi to "रामकली",
            DisplayLanguage.Punjabi to "ਰਾਮਕਲੀ"
        ),
        "Maru" to mapOf(
            DisplayLanguage.English to "Maru",
            DisplayLanguage.Hindi to "मारू",
            DisplayLanguage.Punjabi to "ਮਾਰੂ"
        ),
        "Bhairav" to mapOf(
            DisplayLanguage.English to "Bhairav",
            DisplayLanguage.Hindi to "भैरउ",
            DisplayLanguage.Punjabi to "ਭੈਰਉ"
        ),
        "Basant" to mapOf(
            DisplayLanguage.English to "Basant",
            DisplayLanguage.Hindi to "बसंतु",
            DisplayLanguage.Punjabi to "ਬਸੰਤੁ"
        ),
        "Sarang" to mapOf(
            DisplayLanguage.English to "Sarang",
            DisplayLanguage.Hindi to "सारंग",
            DisplayLanguage.Punjabi to "ਸਾਰੰਗ"
        ),
        "Malhar" to mapOf(
            DisplayLanguage.English to "Malhar",
            DisplayLanguage.Hindi to "मलार",
            DisplayLanguage.Punjabi to "ਮਲਾਰ"
        ),
        "Kalyan" to mapOf(
            DisplayLanguage.English to "Kalyan",
            DisplayLanguage.Hindi to "कल्याण",
            DisplayLanguage.Punjabi to "ਕਲਿਆਨ"
        ),
        "Prabhati" to mapOf(
            DisplayLanguage.English to "Prabhati",
            DisplayLanguage.Hindi to "प्रभाती",
            DisplayLanguage.Punjabi to "ਪ੍ਰਭਾਤੀ"
        ),
        "Gauri" to mapOf(
            DisplayLanguage.English to "Gauri",
            DisplayLanguage.Hindi to "गउड़ी",
            DisplayLanguage.Punjabi to "ਗਉੜੀ"
        ),
        "Gujri" to mapOf(
            DisplayLanguage.English to "Gujri",
            DisplayLanguage.Hindi to "गूजरी",
            DisplayLanguage.Punjabi to "ਗੂਜਰੀ"
        ),
        "Bihagra" to mapOf(
            DisplayLanguage.English to "Bihagra",
            DisplayLanguage.Hindi to "बिहागड़ा",
            DisplayLanguage.Punjabi to "ਬਿਹਾਗੜਾ"
        ),
        "Wadahans" to mapOf(
            DisplayLanguage.English to "Wadahans",
            DisplayLanguage.Hindi to "वडहंसु",
            DisplayLanguage.Punjabi to "ਵਡਹੰਸੁ"
        ),
        "Devgandhari" to mapOf(
            DisplayLanguage.English to "Devgandhari",
            DisplayLanguage.Hindi to "देवगंधारी",
            DisplayLanguage.Punjabi to "ਦੇਵਗੰਧਾਰੀ"
        ),
        "Sri Raag" to mapOf(
            DisplayLanguage.English to "Sri Raag",
            DisplayLanguage.Hindi to "श्री राग",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਰਾਗ"
        ),
        "Majh" to mapOf(
            DisplayLanguage.English to "Majh",
            DisplayLanguage.Hindi to "माझ",
            DisplayLanguage.Punjabi to "ਮਾਝ"
        ),
        "Gond" to mapOf(
            DisplayLanguage.English to "Gond",
            DisplayLanguage.Hindi to "गोंड",
            DisplayLanguage.Punjabi to "ਗੋਂਡ"
        ),
        "Nat Narayan" to mapOf(
            DisplayLanguage.English to "Nat Narayan",
            DisplayLanguage.Hindi to "नट नारायण",
            DisplayLanguage.Punjabi to "ਨਟ ਨਾਰਾਇਨ"
        ),
        "Mali Gaura" to mapOf(
            DisplayLanguage.English to "Mali Gaura",
            DisplayLanguage.Hindi to "माली गउड़ा",
            DisplayLanguage.Punjabi to "ਮਾਲੀ ਗਉੜਾ"
        ),
        "Tukhari" to mapOf(
            DisplayLanguage.English to "Tukhari",
            DisplayLanguage.Hindi to "तुखारी",
            DisplayLanguage.Punjabi to "ਤੁਖਾਰੀ"
        ),
        "Kedara" to mapOf(
            DisplayLanguage.English to "Kedara",
            DisplayLanguage.Hindi to "केदारा",
            DisplayLanguage.Punjabi to "ਕੇਦਾਰਾ"
        ),
        "Kanara" to mapOf(
            DisplayLanguage.English to "Kanara",
            DisplayLanguage.Hindi to "कानड़ा",
            DisplayLanguage.Punjabi to "ਕਾਨੜਾ"
        ),
        "Jaijavanti" to mapOf(
            DisplayLanguage.English to "Jaijavanti",
            DisplayLanguage.Hindi to "जैजावंती",
            DisplayLanguage.Punjabi to "ਜੈਜਾਵੰਤੀ"
        ),
        "Traditional Gurbani Recitation" to mapOf(
            DisplayLanguage.English to "Traditional Gurbani Recitation",
            DisplayLanguage.Hindi to "पारंपरिक गुरबाणी पाठ",
            DisplayLanguage.Punjabi to "ਪੁਰਾਤਨ ਗੁਰਬਾਣੀ ਪਾਠ"
        ),
        "Bhai Jarnail Singh Ji" to mapOf(
            DisplayLanguage.English to "Bhai Jarnail Singh Ji",
            DisplayLanguage.Hindi to "भाई जरनैल सिंह जी",
            DisplayLanguage.Punjabi to "ਭਾਈ ਜਰਨੈਲ ਸਿੰਘ ਜੀ"
        ),
        "38 Pauris, 2 Saloks" to mapOf(
            DisplayLanguage.English to "38 Pauris, 2 Saloks",
            DisplayLanguage.Hindi to "38 पौड़ी, 2 सलोक",
            DisplayLanguage.Punjabi to "38 ਪਉੜੀਆਂ, 2 ਸਲੋਕ"
        ),
        "199 Verses" to mapOf(
            DisplayLanguage.English to "199 Verses",
            DisplayLanguage.Hindi to "199 छंद",
            DisplayLanguage.Punjabi to "199 ਛੰਦ"
        ),
        "10 Savaiye" to mapOf(
            DisplayLanguage.English to "10 Savaiye",
            DisplayLanguage.Hindi to "10 सवैये",
            DisplayLanguage.Punjabi to "10 ਸਵਈਏ"
        ),
        "29 Verses" to mapOf(
            DisplayLanguage.English to "29 Verses",
            DisplayLanguage.Hindi to "29 छंद",
            DisplayLanguage.Punjabi to "29 ਛੰਦ"
        ),
        "40 Pauris" to mapOf(
            DisplayLanguage.English to "40 Pauris",
            DisplayLanguage.Hindi to "40 पौड़ी",
            DisplayLanguage.Punjabi to "40 ਪਉੜੀਆਂ"
        ),
        "Compilation" to mapOf(
            DisplayLanguage.English to "Compilation",
            DisplayLanguage.Hindi to "संकलन",
            DisplayLanguage.Punjabi to "ਸੰਗ੍ਰਹਿ"
        ),
        "5 Shabads" to mapOf(
            DisplayLanguage.English to "5 Shabads",
            DisplayLanguage.Hindi to "5 शब्द",
            DisplayLanguage.Punjabi to "5 ਸ਼ਬਦ"
        ),
        "24 Ashtpadis, 24 Saloks" to mapOf(
            DisplayLanguage.English to "24 Ashtpadis, 24 Saloks",
            DisplayLanguage.Hindi to "24 अष्टपदी, 24 सलोक",
            DisplayLanguage.Punjabi to "24 ਅਸਟਪਦੀਆਂ, 24 ਸਲੋਕ"
        ),
        "24 Pauris, 59 Saloks" to mapOf(
            DisplayLanguage.English to "24 Pauris, 59 Saloks",
            DisplayLanguage.Hindi to "24 पौड़ी, 59 सलोक",
            DisplayLanguage.Punjabi to "24 ਪਉੜੀਆਂ, 59 ਸਲੋਕ"
        ),
        "Supplication" to mapOf(
            DisplayLanguage.English to "Supplication",
            DisplayLanguage.Hindi to "अरदास (विनती)",
            DisplayLanguage.Punjabi to "ਅਰਦਾਸ (ਬੇਨਤੀ)"
        ),
        "7 Shabads" to mapOf(
            DisplayLanguage.English to "7 Shabads",
            DisplayLanguage.Hindi to "7 शब्द",
            DisplayLanguage.Punjabi to "7 ਸ਼ਬਦ"
        ),
        "57 Saloks" to mapOf(
            DisplayLanguage.English to "57 Saloks",
            DisplayLanguage.Hindi to "57 सलोक",
            DisplayLanguage.Punjabi to "57 ਸਲੋਕ"
        ),
        "14 Stanzas" to mapOf(
            DisplayLanguage.English to "14 Stanzas",
            DisplayLanguage.Hindi to "14 छंद",
            DisplayLanguage.Punjabi to "14 ਬੰਦ"
        ),
        "54 Pauris" to mapOf(
            DisplayLanguage.English to "54 Pauris",
            DisplayLanguage.Hindi to "54 पौड़ी",
            DisplayLanguage.Punjabi to "54 ਪਉੜੀਆਂ"
        ),
        "73 Pauris" to mapOf(
            DisplayLanguage.English to "73 Pauris",
            DisplayLanguage.Hindi to "73 पौड़ी",
            DisplayLanguage.Punjabi to "73 ਪਉੜੀਆਂ"
        ),
        "3 Pauris" to mapOf(
            DisplayLanguage.English to "3 Pauris",
            DisplayLanguage.Hindi to "3 पौड़ी",
            DisplayLanguage.Punjabi to "3 ਪਉੜੀਆਂ"
        ),
        "21 Pauris" to mapOf(
            DisplayLanguage.English to "21 Pauris",
            DisplayLanguage.Hindi to "21 पौड़ी",
            DisplayLanguage.Punjabi to "21 ਪਉੜੀਆਂ"
        ),
        "55 Pauris" to mapOf(
            DisplayLanguage.English to "55 Pauris",
            DisplayLanguage.Hindi to "55 पौड़ी",
            DisplayLanguage.Punjabi to "55 ਪਉੜੀਆਂ"
        ),
        "Rise with Simran" to mapOf(
            DisplayLanguage.English to "Rise with Simran",
            DisplayLanguage.Hindi to "सिमरन के साथ जागें",
            DisplayLanguage.Punjabi to "ਸਿਮਰਨ ਨਾਲ ਜਾਗੋ"
        ),
        "Focused Nitnem" to mapOf(
            DisplayLanguage.English to "Focused Nitnem",
            DisplayLanguage.Hindi to "एकाग्र नितनेम",
            DisplayLanguage.Punjabi to "ਧਿਆਨ ਨਾਲ ਨਿਤਨੇਮ"
        ),
        "Inner Reflection" to mapOf(
            DisplayLanguage.English to "Inner Reflection",
            DisplayLanguage.Hindi to "आत्म-चिंतन",
            DisplayLanguage.Punjabi to "ਆਤਮ-ਚਿੰਤਨ"
        ),
        "Sunset Prayer" to mapOf(
            DisplayLanguage.English to "Sunset Prayer",
            DisplayLanguage.Hindi to "सूर्यास्त प्रार्थना",
            DisplayLanguage.Punjabi to "ਸੰਧਿਆ ਵੇਲੇ ਦਾ ਪਾਠ"
        ),
        "Peaceful Rest" to mapOf(
            DisplayLanguage.English to "Peaceful Rest",
            DisplayLanguage.Hindi to "शांतिपूर्ण विश्राम",
            DisplayLanguage.Punjabi to "ਸੁਖਦਾਈ ਬਿਸਰਾਮ"
        ),
        "Select Paath" to mapOf(
            DisplayLanguage.English to "Select Paath",
            DisplayLanguage.Hindi to "पाठ चुनें",
            DisplayLanguage.Punjabi to "ਪਾਠ ਚੁਣੋ"
        ),
        "Completed Today" to mapOf(
            DisplayLanguage.English to "Completed Today",
            DisplayLanguage.Hindi to "आज पूर्ण",
            DisplayLanguage.Punjabi to "ਅੱਜ ਪੂਰਾ"
        ),
        "3 AM - 6 AM" to mapOf(
            DisplayLanguage.English to "3 AM - 6 AM",
            DisplayLanguage.Hindi to "सुबह 3 से 6",
            DisplayLanguage.Punjabi to "ਸਵੇਰ 3 ਤੋਂ 6"
        ),
        "7 AM - 11 AM" to mapOf(
            DisplayLanguage.English to "7 AM - 11 AM",
            DisplayLanguage.Hindi to "सुबह 7 से 11",
            DisplayLanguage.Punjabi to "ਸਵੇਰ 7 ਤੋਂ 11"
        ),
        "12 PM - 3 PM" to mapOf(
            DisplayLanguage.English to "12 PM - 3 PM",
            DisplayLanguage.Hindi to "दोपहर 12 से 3",
            DisplayLanguage.Punjabi to "ਦੁਪਹਿਰ 12 ਤੋਂ 3"
        ),
        "4 PM - 6 PM" to mapOf(
            DisplayLanguage.English to "4 PM - 6 PM",
            DisplayLanguage.Hindi to "शाम 4 से 6",
            DisplayLanguage.Punjabi to "ਸ਼ਾਮ 4 ਤੋਂ 6"
        ),
        "Monday" to mapOf(
            DisplayLanguage.English to "Monday",
            DisplayLanguage.Hindi to "सोमवार",
            DisplayLanguage.Punjabi to "ਸੋਮਵਾਰ"
        ),
        "Tuesday" to mapOf(
            DisplayLanguage.English to "Tuesday",
            DisplayLanguage.Hindi to "मंगलवार",
            DisplayLanguage.Punjabi to "ਮੰਗਲਵਾਰ"
        ),
        "Wednesday" to mapOf(
            DisplayLanguage.English to "Wednesday",
            DisplayLanguage.Hindi to "बुधवार",
            DisplayLanguage.Punjabi to "ਬੁੱਧਵਾਰ"
        ),
        "Thursday" to mapOf(
            DisplayLanguage.English to "Thursday",
            DisplayLanguage.Hindi to "गुरुवार",
            DisplayLanguage.Punjabi to "ਵੀਰਵਾਰ"
        ),
        "Friday" to mapOf(
            DisplayLanguage.English to "Friday",
            DisplayLanguage.Hindi to "शुक्रवार",
            DisplayLanguage.Punjabi to "ਸ਼ੁੱਕਰਵਾਰ"
        ),
        "Saturday" to mapOf(
            DisplayLanguage.English to "Saturday",
            DisplayLanguage.Hindi to "शनिवार",
            DisplayLanguage.Punjabi to "ਸ਼ਨੀਵਾਰ"
        ),
        "Sunday" to mapOf(
            DisplayLanguage.English to "Sunday",
            DisplayLanguage.Hindi to "रविवार",
            DisplayLanguage.Punjabi to "ਐਤਵਾਰ"
        ),
        "January" to mapOf(
            DisplayLanguage.English to "January",
            DisplayLanguage.Hindi to "जनवरी",
            DisplayLanguage.Punjabi to "ਜਨਵਰੀ"
        ),
        "February" to mapOf(
            DisplayLanguage.English to "February",
            DisplayLanguage.Hindi to "फ़रवरी",
            DisplayLanguage.Punjabi to "ਫਰਵਰੀ"
        ),
        "March" to mapOf(
            DisplayLanguage.English to "March",
            DisplayLanguage.Hindi to "मार्च",
            DisplayLanguage.Punjabi to "ਮਾਰਚ"
        ),
        "April" to mapOf(
            DisplayLanguage.English to "April",
            DisplayLanguage.Hindi to "अप्रैल",
            DisplayLanguage.Punjabi to "ਅਪ੍ਰੈਲ"
        ),
        "May" to mapOf(
            DisplayLanguage.English to "May",
            DisplayLanguage.Hindi to "मई",
            DisplayLanguage.Punjabi to "ਮਈ"
        ),
        "June" to mapOf(
            DisplayLanguage.English to "June",
            DisplayLanguage.Hindi to "जून",
            DisplayLanguage.Punjabi to "ਜੂਨ"
        ),
        "July" to mapOf(
            DisplayLanguage.English to "July",
            DisplayLanguage.Hindi to "जुलाई",
            DisplayLanguage.Punjabi to "ਜੁਲਾਈ"
        ),
        "August" to mapOf(
            DisplayLanguage.English to "August",
            DisplayLanguage.Hindi to "अगस्त",
            DisplayLanguage.Punjabi to "ਅਗਸਤ"
        ),
        "September" to mapOf(
            DisplayLanguage.English to "September",
            DisplayLanguage.Hindi to "सितंबर",
            DisplayLanguage.Punjabi to "ਸਤੰਬਰ"
        ),
        "October" to mapOf(
            DisplayLanguage.English to "October",
            DisplayLanguage.Hindi to "अक्टूबर",
            DisplayLanguage.Punjabi to "ਅਕਤੂਬਰ"
        ),
        "November" to mapOf(
            DisplayLanguage.English to "November",
            DisplayLanguage.Hindi to "नवंबर",
            DisplayLanguage.Punjabi to "ਨਵੰਬਰ"
        ),
        "December" to mapOf(
            DisplayLanguage.English to "December",
            DisplayLanguage.Hindi to "दिसंबर",
            DisplayLanguage.Punjabi to "ਦਿਸੰਬਰ"
        ),
        "Salok Mahalla 9, Mundavni, Ragmala" to mapOf(
            DisplayLanguage.English to "Salok Mahalla 9, Mundavni, Ragmala",
            DisplayLanguage.Hindi to "सलोक महला 9, मुंदावणी, रागमाला",
            DisplayLanguage.Punjabi to "ਸਲੋਕ ਮਹਲਾ ੯, ਮੁੰਦਾਵਣੀ, ਰਾਗਮਾਲਾ"
        ),
        "Bhog" to mapOf(
            DisplayLanguage.English to "Bhog",
            DisplayLanguage.Hindi to "भोग",
            DisplayLanguage.Punjabi to "ਭੋਗ"
        ),
        "Ang 1" to mapOf(
            DisplayLanguage.English to "Ang 1",
            DisplayLanguage.Hindi to "अंग 1",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧"
        ),
        "Ang 8" to mapOf(
            DisplayLanguage.English to "Ang 8",
            DisplayLanguage.Hindi to "अंग 8",
            DisplayLanguage.Punjabi to "ਅੰਗ ੮"
        ),
        "Ang 12" to mapOf(
            DisplayLanguage.English to "Ang 12",
            DisplayLanguage.Hindi to "अंग 12",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧੨"
        ),
        "Ang 96" to mapOf(
            DisplayLanguage.English to "Ang 96",
            DisplayLanguage.Hindi to "अंग 96",
            DisplayLanguage.Punjabi to "ਅੰਗ ੯੬"
        ),
        "Ang 133" to mapOf(
            DisplayLanguage.English to "Ang 133",
            DisplayLanguage.Hindi to "अंग 133",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧੩੩"
        ),
        "Ang 262" to mapOf(
            DisplayLanguage.English to "Ang 262",
            DisplayLanguage.Hindi to "अंग 262",
            DisplayLanguage.Punjabi to "ਅੰਗ ੨੬੨"
        ),
        "Ang 462" to mapOf(
            DisplayLanguage.English to "Ang 462",
            DisplayLanguage.Hindi to "अंग 462",
            DisplayLanguage.Punjabi to "ਅੰਗ ੪੬੨"
        ),
        "Ang 663" to mapOf(
            DisplayLanguage.English to "Ang 663",
            DisplayLanguage.Hindi to "अंग 663",
            DisplayLanguage.Punjabi to "ਅੰਗ ੬੬੩"
        ),
        "Ang 917" to mapOf(
            DisplayLanguage.English to "Ang 917",
            DisplayLanguage.Hindi to "अंग 917",
            DisplayLanguage.Punjabi to "ਅੰਗ ੯੧੭"
        ),
        "Ang 929" to mapOf(
            DisplayLanguage.English to "Ang 929",
            DisplayLanguage.Hindi to "अंग 929",
            DisplayLanguage.Punjabi to "ਅੰਗ ੯੨੯"
        ),
        "Ang 938" to mapOf(
            DisplayLanguage.English to "Ang 938",
            DisplayLanguage.Hindi to "अंग 938",
            DisplayLanguage.Punjabi to "ਅੰਗ ੯੩੮"
        ),
        "Ang 947" to mapOf(
            DisplayLanguage.English to "Ang 947",
            DisplayLanguage.Hindi to "अंग 947",
            DisplayLanguage.Punjabi to "ਅੰਗ ੯੪੭"
        ),
        "Ang 1193" to mapOf(
            DisplayLanguage.English to "Ang 1193",
            DisplayLanguage.Hindi to "अंग 1193",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧੧੯੩"
        ),
        "Ang 1426" to mapOf(
            DisplayLanguage.English to "Ang 1426",
            DisplayLanguage.Hindi to "अंग 1426",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧੪੨੬"
        ),
        "Ang 1429" to mapOf(
            DisplayLanguage.English to "Ang 1429",
            DisplayLanguage.Hindi to "अंग 1429",
            DisplayLanguage.Punjabi to "ਅੰਗ ੧੪੨੯"
        ),
        "Dasam Granth Ang 1" to mapOf(
            DisplayLanguage.English to "Dasam Granth Ang 1",
            DisplayLanguage.Hindi to "दशम ग्रंथ अंग 1",
            DisplayLanguage.Punjabi to "ਦਸਮ ਗ੍ਰੰਥ ਅੰਗ ੧"
        ),
        "Dasam Granth Ang 13" to mapOf(
            DisplayLanguage.English to "Dasam Granth Ang 13",
            DisplayLanguage.Hindi to "दशम ग्रंथ अंग 13",
            DisplayLanguage.Punjabi to "ਦਸਮ ਗ੍ਰੰਥ ਅੰਗ ੧੩"
        ),
        "Dasam Granth Ang 1386" to mapOf(
            DisplayLanguage.English to "Dasam Granth Ang 1386",
            DisplayLanguage.Hindi to "दशम ग्रंथ अंग 1386",
            DisplayLanguage.Punjabi to "ਦਸਮ ਗ੍ਰੰਥ ਅੰਗ ੧੩੮੬"
        ),
        "Dasam Granth Ang 119" to mapOf(
            DisplayLanguage.English to "Dasam Granth Ang 119",
            DisplayLanguage.Hindi to "दशम ग्रंथ अंग 119",
            DisplayLanguage.Punjabi to "ਦਸਮ ਗ੍ਰੰਥ ਅੰਗ ੧੧੯"
        ),
        "Sikh Rehat Maryada" to mapOf(
            DisplayLanguage.English to "Sikh Rehat Maryada",
            DisplayLanguage.Hindi to "सिख रहत मर्यादा",
            DisplayLanguage.Punjabi to "ਸਿੱਖ ਰਹਿਤ ਮਰਯਾਦਾ"
        ),
        "Gurbani" to mapOf(
            DisplayLanguage.English to "Gurbani",
            DisplayLanguage.Hindi to "गुरबाणी",
            DisplayLanguage.Punjabi to "ਗੁਰਬਾਣੀ"
        ),
        "mins" to mapOf(
            DisplayLanguage.English to "mins",
            DisplayLanguage.Hindi to "मिनट",
            DisplayLanguage.Punjabi to "ਮਿੰਟ"
        ),
        "Line" to mapOf(
            DisplayLanguage.English to "Line",
            DisplayLanguage.Hindi to "पंक्ति",
            DisplayLanguage.Punjabi to "ਲਾਈਨ"
        ),
        "Lines" to mapOf(
            DisplayLanguage.English to "Lines",
            DisplayLanguage.Hindi to "पंक्तियाँ",
            DisplayLanguage.Punjabi to "ਲਾਈਨਾਂ"
        ),
        "Meanings" to mapOf(
            DisplayLanguage.English to "Meanings",
            DisplayLanguage.Hindi to "शब्दार्थ",
            DisplayLanguage.Punjabi to "ਸ਼ਬਦਾਰਥ"
        ),
        "Hindi Transliteration" to mapOf(
            DisplayLanguage.English to "Hindi Transliteration",
            DisplayLanguage.Hindi to "हिंदी लिप्यंतरण",
            DisplayLanguage.Punjabi to "ਹਿੰਦੀ ਲਿਪੀਅੰਤਰਨ"
        ),
        "Sync Studio" to mapOf(
            DisplayLanguage.English to "Sync Studio",
            DisplayLanguage.Hindi to "सिंक स्टूडियो",
            DisplayLanguage.Punjabi to "ਸਿੰਕ ਸਟੂਡੀਓ"
        ),
        "Not Synced" to mapOf(
            DisplayLanguage.English to "Not Synced",
            DisplayLanguage.Hindi to "सिंक नहीं हुआ",
            DisplayLanguage.Punjabi to "ਸਿੰਕ ਨਹੀਂ ਹੋਇਆ"
        ),
        "LIVE" to mapOf(
            DisplayLanguage.English to "LIVE",
            DisplayLanguage.Hindi to "लाइव",
            DisplayLanguage.Punjabi to "ਲਾਈਵ"
        ),
        "OFFLINE" to mapOf(
            DisplayLanguage.English to "OFFLINE",
            DisplayLanguage.Hindi to "ऑफ़लाइन",
            DisplayLanguage.Punjabi to "ਔਫਲਾਈਨ"
        ),
        "Play" to mapOf(
            DisplayLanguage.English to "Play",
            DisplayLanguage.Hindi to "चलाएं",
            DisplayLanguage.Punjabi to "ਚਲਾਓ"
        ),
        "Undo" to mapOf(
            DisplayLanguage.English to "Undo",
            DisplayLanguage.Hindi to "पूर्ववत करें",
            DisplayLanguage.Punjabi to "ਵਾਪਸ ਲਓ"
        ),
        "Save failed" to mapOf(
            DisplayLanguage.English to "Save failed",
            DisplayLanguage.Hindi to "सहेजना विफल रहा",
            DisplayLanguage.Punjabi to "ਸੰਭਾਲਣਾ ਅਸਫਲ ਰਿਹਾ"
        ),
        "About" to mapOf(
            DisplayLanguage.English to "About",
            DisplayLanguage.Hindi to "विवरण",
            DisplayLanguage.Punjabi to "ਬਾਰੇ"
        ),
        "Significance" to mapOf(
            DisplayLanguage.English to "Significance",
            DisplayLanguage.Hindi to "महत्व",
            DisplayLanguage.Punjabi to "ਮਹੱਤਵ"
        ),
        "License" to mapOf(
            DisplayLanguage.English to "License",
            DisplayLanguage.Hindi to "लाइसेंस",
            DisplayLanguage.Punjabi to "ਲਾਇਸੈਂਸ"
        ),
        "Japji Sahib About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Nanak Dev Ji, this is the opening Bani of Sri Guru Granth Sahib Ji and contains the foundational philosophy of Sikhism.",
            DisplayLanguage.Hindi to "गुरु नानक देव जी द्वारा रचित, यह श्री गुरु ग्रंथ साहिब जी की प्रारंभिक बाणी है और इसमें सिख धर्म का मूलभूत दर्शन है।",
            DisplayLanguage.Punjabi to "ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਦੁਆਰਾ ਉਚਾਰਨ ਕੀਤੀ ਗਈ, ਇਹ ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ ਦੀ ਆਰੰਭਿਕ ਬਾਣੀ ਹੈ ਅਤੇ ਸਿੱਖ ਧਰਮ ਦੇ ਮੂਲ ਫਲਸਫੇ ਨੂੰ ਦਰਸਾਉਂਦੀ ਹੈ।"
        ),
        "Japji Sahib Significance" to mapOf(
            DisplayLanguage.English to "Recited in the morning. It establishes the concept of One Universal Creator (Ek Onkar) and guides the soul towards truth and merging with the Divine.",
            DisplayLanguage.Hindi to "सुबह पाठ किया जाता है। यह एक सर्वव्यापी कर्ता (ੴ) की अवधारणा स्थापित करता है और आत्मा को सत्य और परमात्मा में विलीन होने की ओर निर्देशित करता है।",
            DisplayLanguage.Punjabi to "ਸਵੇਰ ਵੇਲੇ ਪੜ੍ਹੀ ਜਾਂਦੀ ਹੈ। ਇਹ ਇੱਕ ਅਕਾਲ ਪੁਰਖ (ੴ) ਦੇ ਸੰਕਲਪ ਨੂੰ ਦ੍ਰਿੜ੍ਹ ਕਰਵਾਉਂਦੀ ਹੈ ਅਤੇ ਆਤਮਾ ਨੂੰ ਸੱਚ ਦੇ ਮਾਰਗ 'ਤੇ ਚੱਲਣ ਦੀ ਪ੍ਰੇਰਨਾ ਦਿੰਦੀ ਹੈ।"
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
        "Sukhmani Sahib About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Arjan Dev Ji, the fifth Guru, meaning the 'Treasure of Peace' or 'Peace of the Mind', written in Raag Gauri.",
            DisplayLanguage.Hindi to "पांचवें गुरु, गुरु अर्जुन देव जी द्वारा गौरी राग में रचित, जिसका अर्थ है 'शांति की मणि' या 'मन की शांति'।",
            DisplayLanguage.Punjabi to "ਪੰਜਵੇਂ ਪਾਤਸ਼ਾਹ ਸ੍ਰੀ ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ ਦੁਆਰਾ ਗਉੜੀ ਰਾਗ ਵਿੱਚ ਉਚਾਰਨ ਕੀਤੀ ਗਈ ਮਹਾਨ ਬਾਣੀ, ਜਿਸਦਾ ਅਰਥ ਹੈ 'ਸੁਖਾਂ ਦੀ ਮਣੀ' ਜਾਂ 'ਮਨ ਨੂੰ ਸ਼ਾਂਤੀ ਦੇਣ ਵਾਲੀ ਬਾਣੀ'।"
        ),
        "Sukhmani Sahib Significance" to mapOf(
            DisplayLanguage.English to "Brings immense mental peace, removes anxiety, and lists the benefits of remembering God's Name (Naam Simran) and living a virtuous life.",
            DisplayLanguage.Hindi to "अत्यंत मानसिक शांति लाता है, चिंता को दूर करता है, और ईश्वर के नाम (नाम सिमरन) को याद करने और एक सदाचारी जीवन जीने के लाभों को सूचीबद्ध करता है।",
            DisplayLanguage.Punjabi to "ਇਿਸ ਬਾਣੀ ਦਾ ਪਾਠ ਕਰਨ ਨਾਲ ਮਨ ਨੂੰ ਅਥਾਹ ਸ਼ਾਂਤੀ ਮਿਲਦੀ ਹੈ, ਮਾਨਸਿਕ ਚਿੰਤਾਵਾਂ ਦੂਰ ਹੁੰਦੀਆਂ ਹਨ, ਅਤੇ ਨਾਮ ਸਿਮਰਨ ਤੇ ਪਰਉਪਕਾਰੀ ਜੀਵਨ ਜਿਊਣ ਦੀ ਪ੍ਰੇਰਨਾ ਮਿਲਦੀ ਹੈ।"
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
        "Ardas About" to mapOf(
            DisplayLanguage.English to "A traditional congregational petition/supplication that summarizes Sikh history and honors the sacrifices of the Gurus and martyrs.",
            DisplayLanguage.Hindi to "एक पारंपरिक प्रार्थना/याचिका जो सिख इतिहास का सार प्रस्तुत करती है और गुरुओं और शहीदों के बलिदानों का सम्मान करती है।",
            DisplayLanguage.Punjabi to "ਸਿੱਖ ਕੌਮ ਦੀ ਪਾਵਨ ਅਰਦਾਸ (ਬੇਨਤੀ), ਜੋ ਸਿੱਖ ਇਤਿਹਾਸ ਦੇ ਮਹਾਨ ਸ਼ਹੀਦਾਂ ਅਤੇ ਗੁਰੂ ਸਾਹਿਬਾਨ ਦੀਆਂ ਕੁਰਬਾਨੀਆਂ ਨੂੰ ਯਾਦ ਕਰਵਾਉਂਦੀ ਹੈ।"
        ),
        "Ardas Significance" to mapOf(
            DisplayLanguage.English to "Offered standing before and after any significant action, prayer, or ceremony. It connects the individual with the collective consciousness and seeks grace.",
            DisplayLanguage.Hindi to "किसी भी महत्वपूर्ण कार्य, प्रार्थना या समारोह से पहले और बाद में खड़े होकर की जाती है। यह व्यक्ति को सामूहिक चेतना से जोड़ती है और कृपा मांगती है।",
            DisplayLanguage.Punjabi to "ਹਰ ਇੱਕ ਕਾਰਜ ਦੇ ਆਰੰਭ ਅਤੇ ਸਮਾਪਤੀ ਵੇਲੇ ਖੜ੍ਹੇ ਹੋ ਕੇ ਕੀਤੀ ਜਾਂਦੀ ਹੈ। ਇਹ ਮਨੁੱਖ ਨੂੰ ਚੜ੍ਹਦੀ ਕਲਾ ਵਿੱਚ ਰੱਖਦੀ ਹੈ ਅਤੇ ਵਾਹਿਗੁਰੂ ਜੀ ਦੇ ਚਰਨਾਂ ਵਿੱਚ ਸ਼ੁਕਰਾਨਾ ਤੇ ਭੁੱਲਾਂ ਬਖ਼ਸ਼ਾਉਣ ਦੀ ਜੋਦੜੀ ਹੈ।"
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
        "Salok Mahalla 9 About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Tegh Bahadur Ji, the ninth Guru, representing his final teachings written before his martyrdom.",
            DisplayLanguage.Hindi to "नौवें गुरु, गुरु तेग बहादुर जी द्वारा रचित, जो उनकी शहादत से पहले लिखे गए उनके अंतिम उपदेशों का प्रतिनिधित्व करते हैं।",
            DisplayLanguage.Punjabi to "ਨੌਵੇਂ ਪਾਤਸ਼ਾਹ ਸ੍ਰੀ ਗੁਰੂ ਤੇਗ ਬਹਾਦਰ ਸਾਹਿਬ ਜੀ ਦੁਆਰਾ ਉਚਾਰਨ ਕੀਤੇ ਗਏ ਪਾਵਨ ਸਲੋਕ, ਜੋ ਆਪ ਜੀ ਦੀ ਸ਼ਹੀਦੀ ਤੋਂ ਪਹਿਲਾਂ ਦੇ ਅੰਤਿਮ ਉਪਦੇਸ਼ ਹਨ।"
        ),
        "Salok Mahalla 9 Significance" to mapOf(
            DisplayLanguage.English to "Focuses on the transient nature of the world, detaching from ego, and finding permanent support only in the Divine Name.",
            DisplayLanguage.Hindi to "संसार की नश्वरता, अहंकार से मुक्ति और केवल दिव्य नाम में स्थायी सहारा खोजने पर ध्यान केंद्रित करता है।",
            DisplayLanguage.Punjabi to "ਇਹ ਸਲੋਕ ਜੀਵਨ ਦੀ ਨਾਸ਼ਮਾਨਤਾ, ਹਉਮੈ ਦੇ ਤਿਆਗ ਅਤੇ ਕੇਵਲ ਅਕਾਲ ਪੁਰਖ ਦੇ ਨਾਮ ਦਾ ਆਸਰਾ ਲੈਣ ਦੀ ਸਿੱਖਿਆ ਦਿੰਦੇ ਹਨ।"
        ),
        "Barah Maha About" to mapOf(
            DisplayLanguage.English to "A calendar poem composed by Guru Arjan Dev Ji in Raag Majh, describing the soul's journey across the twelve months of the year.",
            DisplayLanguage.Hindi to "गुरु अर्जुन देव जी द्वारा राग माझ में रचित एक कैलेंडर कविता, जो वर्ष के बारह महीनों में आत्मा की यात्रा का वर्णन करती है।",
            DisplayLanguage.Punjabi to "ਪੰਜਵੇਂ ਪਾਤਸ਼ਾਹ ਸ੍ਰੀ ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ ਦੁਆਰਾ ਮਾਝ ਰਾਗ ਵਿੱਚ ਉਚਾਰਨ ਕੀਤੀ ਗਈ ਬਾਣੀ, ਜੋ ਸਾਲ ਦੇ ਬਾਰਾਂ ਮਹੀਨਿਆਂ ਰਾਹੀਂ ਜੀਵ-ਆਤਮਾ ਦੀ ਪ੍ਰਭੂ-ਮਿਲਾਪ ਦੀ ਯਾਤਰਾ ਨੂੰ ਬਿਆਨ ਕਰਦੀ ਹੈ।"
        ),
        "Barah Maha Significance" to mapOf(
            DisplayLanguage.English to "Helps reflect on the seasons of life and the constant opportunity to remember God in every phase and season.",
            DisplayLanguage.Hindi to "जीवन के मौसमों और हर चरण और मौसम में भगवान को याद करने के निरंतर अवसर पर विचार करने में मदद करता है।",
            DisplayLanguage.Punjabi to "ਇਹ ਬਾਣੀ ਸਮੇਂ ਅਤੇ ਰੁੱਤਾਂ ਦੀ ਤਬਦੀਲੀ ਦੇ ਨਾਲ-ਨਾਲ ਹਰ ਮਹੀਨੇ ਨਾਮ ਸਿਮਰਨ ਨਾਲ ਜੁੜੇ ਰਹਿਣ ਦੀ ਸਿੱਖਿਆ ਦਿੰਦੀ ਹੈ।"
        ),
        "Dakhni Oankar About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Nanak Dev Ji, structured around the Gurmukhi alphabet, focusing on the ultimate reality of the Creator.",
            DisplayLanguage.Hindi to "गुरु नानक देव जी द्वारा रचित, गुरमुखी वर्णमाला के इर्द-गिर्द संरचित, जो सृष्टिकर्ता की अंतिम वास्तविकता पर ध्यान केंद्रित करती है।",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਦੁਆਰਾ ਰਾਮਕਲੀ ਰਾਗ ਵਿੱਚ ਉਚਾਰਨ ਕੀਤੀ ਗਈ ਬਾਣੀ, ਜੋ ਅਕਾਲ ਪੁਰਖ ਦੀ ਹੋਂਦ ਅਤੇ ਸ੍ਰਿਸ਼ਟੀ ਦੇ ਸੱਚ ਨੂੰ ਪ੍ਰਗਟ ਕਰਦੀ ਹੈ।"
        ),
        "Dakhni Oankar Significance" to mapOf(
            DisplayLanguage.English to "Inspires true knowledge, destruction of pride, and recognizing the single light residing in all creation.",
            DisplayLanguage.Hindi to "सच्चे ज्ञान, अहंकार के विनाश और संपूर्ण सृष्टि में निवास करने वाले एकमात्र प्रकाश को पहचानने की प्रेरणा देता है।",
            DisplayLanguage.Punjabi to "ਇਹ ਬਾਣੀ ਮਨੁੱਖ ਨੂੰ ਸੱਚਾ ਗਿਆਨ ਬਖ਼ਸ਼ਦੀ ਹੈ, ਹਉਮੈ ਦਾ ਨਾਸ਼ ਕਰਦੀ ਹੈ ਅਤੇ ਸਾਰੇ ਜੀਵਾਂ ਵਿੱਚ ਇੱਕੋ ਜੋਤ ਦੇ ਦਰਸ਼ਨ ਕਰਵਾਉਂਦੀ ਹੈ।"
        ),
        "Sidha Gosht About" to mapOf(
            DisplayLanguage.English to "A recorded dialogue between Guru Nanak Dev Ji and the Siddha yogis, addressing true renunciation and the path of householder yoga.",
            DisplayLanguage.Hindi to "गुरु नानक देव जी और सिद्ध योगियों के बीच एक संवाद, जो सच्चे वैराग्य और गृहस्थ योग के मार्ग को संबोधित करता है।",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਅਤੇ ਸਿੱਧ ਜੋਗੀਆਂ ਵਿਚਕਾਰ ਹੋਈ ਗੋਸਟਿ (ਗੱਲਬਾਤ), ਜੋ ਸੱਚੀ ਉਦਾਸੀ ਅਤੇ ਗ੍ਰਹਿਸਥ ਜੀਵਨ ਵਿੱਚ ਰਹਿ ਕੇ ਨਾਮ ਜਪਣ ਦੇ ਮਾਰਗ ਨੂੰ ਦਰਸਾਉਂਦੀ ਹੈ।"
        ),
        "Sidha Gosht Significance" to mapOf(
            DisplayLanguage.English to "Teaches that true liberation is achieved not by running away to mountains, but by remaining detached in the world through the Shabad.",
            DisplayLanguage.Hindi to "सिखाता है कि सच्ची मुक्ति पहाड़ों पर भाग जाने से नहीं, बल्कि शबद के माध्यम से संसार में अनासक्त रहने से प्राप्त होती है।",
            DisplayLanguage.Punjabi to "ਇਹ ਬਾਣੀ ਸਿਖਾਉਂਦੀ ਹੈ ਕਿ ਘਰ-ਬਾਰ ਛੱਡ ਕੇ ਜੰਗਲਾਂ ਵਿੱਚ ਭਟਕਣ ਦੀ ਬਜਾਏ, ਸੰਸਾਰ ਵਿੱਚ ਰਹਿ ਕੇ ਸ਼ਬਦ ਦੀ ਕਮਾਈ ਦੁਆਰਾ ਮੁਕਤੀ ਪ੍ਰਾਪਤ ਕੀਤੀ ਜਾ ਸਕਦੀ ਹੈ।"
        ),
        "Basant Ki Vaar About" to mapOf(
            DisplayLanguage.English to "A short spring ballad composed by Guru Arjan Dev Ji, celebrating spiritual rejuvenation and growth.",
            DisplayLanguage.Hindi to "गुरु अर्जुन देव जी द्वारा रचित बसंत ऋतु की एक लघु वीर गाथा, जो आध्यात्मिक कायाकल्प और विकास का जश्न बनाती है।",
            DisplayLanguage.Punjabi to "ਪੰਜਵੇਂ ਪਾਤਸ਼ਾਹ ਸ੍ਰੀ ਗੁਰੂ ਅਰਜਨ ਦੇਵ ਜੀ ਦੁਆਰਾ ਬਸੰਤ ਰਾਗ ਵਿੱਚ ਉਚਾਰਨ ਕੀਤੀ ਗਈ ਲਘੂ ਵਾਰ, ਜੋ ਆਤਮਿਕ ਖੇੜੇ ਅਤੇ ਹਰਿਆਵਲ ਨੂੰ ਪ੍ਰਗਟ ਕਰਦੀ ਹੈ।"
        ),
        "Basant Ki Vaar Significance" to mapOf(
            DisplayLanguage.English to "Symbolizes the blossoming of the soul and the arrival of spiritual spring when connected to Naam.",
            DisplayLanguage.Hindi to "नाम से जुड़ने पर आत्मा के खिलने और आध्यात्मिक वसंत के आगमन का प्रतीक है।",
            DisplayLanguage.Punjabi to "ਇਹ ਬਾਣੀ ਮਨੁੱਖੀ ਮਨ ਦੇ ਖੇੜੇ ਅਤੇ ਪ੍ਰਭੂ ਦੇ ਨਾਮ ਰੰਗ ਵਿੱਚ ਰੰਗੇ ਜਾਣ 'ਤੇ ਆਤਮਿਕ ਬਸੰਤ ਦੇ ਆਗਮਨ ਦਾ ਪ੍ਰਤੀਕ ਹੈ।"
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
        "Aarti About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Nanak Dev Ji and other Bhagats, describing the cosmic worship of the Creator by the sky, stars, wind, and forests.",
            DisplayLanguage.Hindi to "गुरु नानक देव जी और अन्य भक्तों द्वारा रचित, जो आकाश, तारों, हवा और जंगलों द्वारा सृष्टिकर्ता की ब्रह्मांडीय पूजा का वर्णन करती है।",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਨਾਨਕ ਦੇਵ ਜੀ ਅਤੇ ਵੱਖ-ਵੱਖ ਭਗਤ ਸਾਹਿਬਾਨ ਦੁਆਰਾ ਉਚਾਰਨ ਕੀਤੀ ਗਈ ਆਰਤੀ, ਜੋ ਕੁਦਰਤ (ਆਕਾਸ਼, ਤਾਰੇ, ਹਵਾ ਅਤੇ ਜੰਗਲ) ਦੁਆਰਾ ਕੀਤੀ ਜਾ ਰਹੀ ਬ੍ਰਹਮ ਆਰਤੀ ਨੂੰ ਬਿਆਨ ਕਰਦੀ ਹੈ।"
        ),
        "Aarti Significance" to mapOf(
            DisplayLanguage.English to "Removes localized idol worship and opens the eyes to the grand, eternal adoration of God taking place across the entire universe.",
            DisplayLanguage.Hindi to "स्थानीय मूर्ति पूजा को दूर करता है और पूरे ब्रह्मांड में हो रही ईश्वर की महान, शाश्वत आराधना के प्रति आंखें खोलता है।",
            DisplayLanguage.Punjabi to "ਇਹ ਬਾਣੀ ਪੱਥਰਾਂ ਦੀ ਆਰਤੀ ਕਰਨ ਦੀ ਬਜਾਏ, ਸਾਰੀ ਕਾਇਨਾਤ ਵਿੱਚ ਹੋ ਰਹੀ ਅਕਾਲ ਪੁਰਖ ਦੀ ਵਿਰਾਟ ਆਰਤੀ ਨੂੰ ਦੇਖਣ ਦੀ ਸੂਝ ਬਖ਼ਸ਼ਦੀ ਹੈ।"
        ),
        "Chandi Di Vaar About" to mapOf(
            DisplayLanguage.English to "Composed by Guru Gobind Singh Ji, describing the battle between gods and demons as a metaphor for the struggle between good and evil.",
            DisplayLanguage.Hindi to "गुरु गोबिंद सिंह जी द्वारा रचित, जिसमें देवताओं और राक्षसों के बीच युद्ध को अच्छाई और बुराई के बीच संघर्ष के रूप में वर्णित किया गया है।",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਗੋਬਿੰਦ ਸਿੰਘ ਜੀ ਦੁਆਰਾ ਉਚਾਰਨ ਕੀਤੀ ਗਈ ਬੀਰ ਰਸੀ ਵਾਰ, ਜੋ ਦੇਵਤਿਆਂ ਅਤੇ ਦੈਂਤਾਂ ਦੀ ਜੰਗ ਰਾਹੀਂ ਸੱਚ ਅਤੇ ਝੂਠ ਦੇ ਯੁੱਧ ਨੂੰ ਦਰਸਾਉਂਦੀ ਹੈ।"
        ),
        "Chandi Di Vaar Significance" to mapOf(
            DisplayLanguage.English to "Instills martial spirit (Bir Ras), removes cowardice, and inspires fighting for righteousness and justice.",
            DisplayLanguage.Hindi to "वीर रस का संचार करता है, कायरता को दूर करता है, और धार्मिकता और न्याय के लिए लड़ने की प्रेरणा देता है।",
            DisplayLanguage.Punjabi to "ਇਸ ਵਾਰ ਦਾ ਪਾਠ ਮਨੁੱਖ ਵਿੱਚ ਬੀਰ ਰਸ (ਸੂਰਬੀਰਤਾ) ਭਰਦਾ ਹੈ, ਕਮਜ਼ੋਰੀ ਤੇ ਡਰ ਨੂੰ ਦੂਰ ਕਰਦਾ ਹੈ ਅਤੇ ਧਰਮ ਦੀ ਰੱਖਿਆ ਲਈ ਲੜਨ ਦੀ ਪ੍ਰੇਰਨਾ ਦਿੰਦਾ ਹੈ।"
        ),
        "Ragmala About" to mapOf(
            DisplayLanguage.English to "The concluding index of musical measures (Raags) in Sri Guru Granth Sahib Ji, outlining the relationship between various musical families.",
            DisplayLanguage.Hindi to "श्री गुरु ग्रंथ साहिब जी में रागों का समापन सूचकांक, जो विभिन्न राग परिवारों के बीच संबंधों को रेखांकित करता है।",
            DisplayLanguage.Punjabi to "ਸ੍ਰੀ ਗੁਰੂ ਗ੍ਰੰਥ ਸਾਹਿਬ ਜੀ ਦੇ ਅਖ਼ੀਰ ਵਿੱਚ ਦਰਜ ਰਾਗਾਂ ਦੀ ਸੂਚੀ (ਰਾਗਮਾਲਾ), ਜੋ ਵੱਖ-ਵੱਖ ਰਾਗਾਂ ਅਤੇ ਰਾਗਣੀਆਂ ਦੇ ਪਰਿਵਾਰਕ ਸਬੰਧਾਂ ਨੂੰ ਦਰਸਾਉਂਦੀ ਹੈ।"
        ),
        "Ragmala Significance" to mapOf(
            DisplayLanguage.English to "Signifies the completion of the holy scripture and the harmonious organization of the divine music.",
            DisplayLanguage.Hindi to "पवित्र ग्रंथ के पूर्ण होने और दिव्य संगीत के सामंजस्यपूर्ण संगठन को दर्शाता है।",
            DisplayLanguage.Punjabi to "ਇਹ ਬਾਣੀ ਗੁਰਬਾਣੀ ਦੇ ਅਖ਼ੀਰਲੇ ਭਾਗ ਵਜੋਂ ਸੰਪੂਰਨਤਾ ਦਾ ਪ੍ਰਤੀਕ ਹੈ ਅਤੇ ਬ੍ਰਹਮ ਸੰਗੀਤ ਦੀ ਇੱਕਸੁਰਤਾ ਨੂੰ ਪ੍ਰਗਟ ਕਰਦੀ ਹੈ।"
        )
    )
}
