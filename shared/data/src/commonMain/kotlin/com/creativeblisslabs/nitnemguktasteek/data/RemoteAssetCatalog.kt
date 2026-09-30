package com.creativeblisslabs.nitnemguktasteek.data

import kotlinx.serialization.Serializable
import com.creativeblisslabs.nitnemguktasteek.models.AudioTrack

@Serializable
data class RemoteAssetTrack(
    val id: String,
    val baniId: String,
    val baniTitle: String,
    val artist: String,
    val audioFileName: String = "",      // e.g. "japji_sahib_hari_singh.opus"
    val timingFileName: String? = null, // e.g. "japji_sahib_hari_singh_timing.json"
    val audioUrl: String? = null,
    val timingUrl: String? = null,
    val durationMs: Long = 0L,
    val approxSizeBytes: Long = 0L,
    val isPrimaryNitnem: Boolean = false
) {
    fun rawAudioUrl(): String =
        audioUrl?.takeIf { it.isNotBlank() }
            ?: "${com.creativeblisslabs.nitnemguktasteek.security.StringObfuscator.githubRawAudioBase()}$audioFileName"

    fun rawTimingUrl(): String? =
        timingUrl?.takeIf { it.isNotBlank() }
            ?: timingFileName?.let { "${com.creativeblisslabs.nitnemguktasteek.security.StringObfuscator.githubRawTimingBase()}$it" }

    fun toAudioTrack(): AudioTrack =
        AudioTrack(
            id = id,
            title = baniTitle,
            artist = artist,
            durationMs = durationMs,
            license = "Public Domain / CC",
            attribution = "Cloud Firestore Catalog",
            remoteUrl = rawAudioUrl()
        )
}

/**
 * Dynamic Directory Catalog of online and packaged audio and timing assets for Nitnem Gutka.
 * All tracks and metadata are derived dynamically from audio file specifications.
 */
object RemoteAssetCatalog {

    val GITHUB_RAW_AUDIO_BASE: String
        get() = com.creativeblisslabs.nitnemguktasteek.security.StringObfuscator.githubRawAudioBase()

    val GITHUB_RAW_TIMING_BASE: String
        get() = com.creativeblisslabs.nitnemguktasteek.security.StringObfuscator.githubRawTimingBase()

    val GITHUB_MANIFEST_URL: String
        get() = com.creativeblisslabs.nitnemguktasteek.security.StringObfuscator.githubManifestUrl()

    fun fromAudioFileName(
        audioFileName: String,
        timingFileName: String? = null
    ): RemoteAssetTrack {
        val cleanName = audioFileName.removeSuffix(".opus").removeSuffix(".mp3").removeSuffix(".m4a")

        val (baniId, baniTitle, isPrimaryCandidate) = when {
            cleanName.startsWith("japji_sahib") || cleanName.startsWith("japji") ->
                Triple("japji-sahib", "Japji Sahib", true)
            cleanName.startsWith("jaap_sahib") || cleanName.startsWith("jaap") ->
                Triple("jaap-sahib", "Jaap Sahib", true)
            cleanName.startsWith("tav_prasad") ->
                Triple("tav-prasad-savaiye", "Tav Prasad Savaiye", true)
            cleanName.startsWith("chaupai_sahib") ->
                Triple("benti-chaupai-sahib", "Chaupai Sahib", true)
            cleanName.startsWith("anand_sahib") ->
                Triple("anand-sahib", "Anand Sahib", true)
            cleanName.startsWith("ardas") ->
                Triple("ardas", "Ardas Sahib", true)
            cleanName.startsWith("rehras_sahib") ->
                Triple("rehras-sahib", "Rehras Sahib", true)
            cleanName.startsWith("kirtan_sohila") ->
                Triple("kirtan-sohila", "Kirtan Sohila", true)
            cleanName.startsWith("sukhmani_sahib") ->
                Triple("sukhmani-sahib", "Sukhmani Sahib", false)
            cleanName.startsWith("aarti") ->
                Triple("aarti", "Aarti", false)
            cleanName.startsWith("chandi_di_vaar") ->
                Triple("chandi-di-vaar", "Chandi Di Vaar", false)
            cleanName.startsWith("shabad_hazare") ->
                Triple("shabad-hazare", "Shabad Hazare", false)
            cleanName.startsWith("barah_maha") ->
                Triple("barah-maha", "Barah Maha", false)
            cleanName.startsWith("salok_mahalla_9") ->
                Triple("salok-mahalla-9", "Salok Mahalla 9", false)
            cleanName.startsWith("dakhni_oankar") ->
                Triple("dakhni-oankar", "Dakhni Oankar", false)
            cleanName.startsWith("sidha_gosht") || cleanName.startsWith("sidh_gosht") ->
                Triple("sidha-gosht", "Sidh Gosht", false)
            cleanName.startsWith("basant_ki_vaar") ->
                Triple("basant-ki-vaar", "Basant Ki Vaar", false)
            cleanName.startsWith("ramkali_ki_vaar") ->
                Triple("ramkali-ki-vaar", "Ramkali Ki Vaar", false)
            cleanName.startsWith("asa_di_vaar") ->
                Triple("asa-di-vaar", "Asa Di Vaar", false)
            cleanName.startsWith("morning_nitnem") ->
                Triple("morning-nitnem", "Morning Nitnem (5 Banis)", false)
            else ->
                Triple(cleanName.replace('_', '-'), cleanName.replace('_', ' '), false)
        }

        // Only mark as primary if it's the designated default reciter for that Bani
        // For Nitnem, we typically treat Bhai Harinder Singh Ji or Bhai Hari Singh Ji as the "standard" baseline.
        val isPrimary = isPrimaryCandidate && (
            cleanName.endsWith("hari_singh") || 
            cleanName.endsWith("harinder_singh") ||
            // Fallback for cases where there is only one track for a Nitnem bani
            (baniId == "japji-sahib" && cleanName == "japji_sahib_hari_singh")
        )

        val rawArtistSlug = cleanName.substringAfterLast("${cleanName.substringBeforeLast('_')}_", "")
            .ifBlank { cleanName.substringAfterLast('_') }

        val artist = when {
            cleanName.endsWith("hari_singh") -> "Bhai Hari Singh Ji"
            cleanName.endsWith("harinder_singh") -> "Bhai Harinder Singh Ji"
            cleanName.endsWith("harshdeep_kaur") -> "Harshdeep Kaur"
            cleanName.endsWith("jarnail_singh") -> "Bhai Jarnail Singh Ji"
            cleanName.endsWith("thakur_singh") -> "Giani Thakur Singh Ji"
            cleanName.endsWith("darshan_singh") -> "Master Darshan Singh Ji"
            cleanName.endsWith("annie_ahluwalia") -> "Annie Ahluwalia"
            cleanName.endsWith("hargun_kaur") -> "Hargun Kaur"
            cleanName.endsWith("satvinder_harvinder_singh") -> "Bhai Satvinder & Harvinder Singh Ji"
            cleanName.endsWith("satvinder_singh") -> "Bhai Satvinder Singh Ji"
            cleanName.endsWith("ravinder_singh") -> "Bhai Ravinder Singh Ji"
            cleanName.endsWith("pishora_singh") -> "Giani Pishora Singh Ji"
            cleanName.endsWith("lakhwinder_singh") -> "Bhai Lakhwinder Singh Ji"
            cleanName.endsWith("gurpreet_singh") -> "Bhai Gurpreet Singh Ji"
            cleanName.endsWith("amrit_singh") -> "Bhai Amrit Singh Ji"
            cleanName.endsWith("manpreet_singh") -> "Bhai Manpreet Singh Ji"
            else -> {
                // Dynamically format whatever suffix is provided (e.g., "bhai_xyz_singh" -> "Bhai Xyz Singh Ji")
                val parts = cleanName.substringAfterLast("sahib_", "").substringAfterLast("vaar_", "").substringAfterLast("nitnem_", "")
                if (parts.isNotBlank() && parts != cleanName) {
                    val formatted = parts.split('_').joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                    if (!formatted.contains("Ji", ignoreCase = true) && (formatted.contains("Singh", ignoreCase = true) || formatted.contains("Kaur", ignoreCase = true))) {
                        "$formatted Ji"
                    } else {
                        formatted
                    }
                } else {
                    "Gurbani Kirtan"
                }
            }
        }

        val resolvedTimingFile = timingFileName ?: "${cleanName}_timing.json"

        return RemoteAssetTrack(
            id = "curated_$cleanName",
            baniId = baniId,
            baniTitle = baniTitle,
            artist = artist,
            audioFileName = audioFileName,
            timingFileName = resolvedTimingFile,
            isPrimaryNitnem = isPrimary
        )
    }

    private val RAW_AUDIO_DEFINITIONS: List<Pair<String, String?>> = listOf(
        // Japji Sahib (Multiple Tracks)
        "japji_sahib_hari_singh.opus" to "japji_sahib_hari_singh_timing.json",
        "japji_sahib_harinder_singh.opus" to null,
        "japji_sahib_harshdeep_kaur.opus" to null,

        // Jaap Sahib
        "jaap_sahib_harinder_singh.opus" to null,

        // Tav Prasad Savaiye (Multiple Tracks)
        "tav_prasad_harinder_singh.opus" to null,
        "tav_prasad_thakur_singh.opus" to null,

        // Chaupai Sahib (Multiple Tracks)
        "chaupai_sahib_harinder_singh.opus" to null,
        "chaupai_sahib_harshdeep_kaur.opus" to "chaupai_sahib_harshdeep_kaur_timing.json",

        // Anand Sahib (Multiple Tracks)
        "anand_sahib_harinder_singh.opus" to null,
        "anand_sahib_harshdeep_kaur.opus" to null,

        // Ardas Sahib (Multiple Tracks)
        "ardas_harinder_singh.opus" to null,
        "ardas_hargun_kaur.opus" to null,

        // Rehras Sahib (Multiple Tracks)
        "rehras_sahib_annie_ahluwalia.opus" to null,
        "rehras_sahib_harinder_singh.opus" to null,
        "rehras_sahib_jarnail_singh.opus" to null,

        // Kirtan Sohila (Multiple Tracks)
        "kirtan_sohila_annie_ahluwalia.opus" to "kirtan_sohila_annie_ahluwalia_timing.json",
        "kirtan_sohila_harinder_singh.opus" to null,

        // Sukhmani Sahib (Multiple Tracks)
        "sukhmani_sahib_harinder_singh.opus" to null,
        "sukhmani_sahib_hargun_kaur.opus" to null,

        // Aarti (Multiple Tracks)
        "aarti_hargun_kaur.opus" to null,
        "aarti_satvinder_singh.opus" to null,

        // Chandi Di Vaar
        "chandi_di_vaar_darshan_singh.opus" to null,

        // Shabad Hazare
        "shabad_hazare_ravinder_singh.opus" to null,

        // Barah Maha
        "barah_maha_darshan_singh.opus" to null,

        // Salok Mahalla 9
        "salok_mahalla_9_darshan_singh.opus" to null,

        // Dakhni Oankar
        "dakhni_oankar_pishora_singh.opus" to null,

        // Sidha Gosht
        "sidha_gosht_jarnail_singh.opus" to null,

        // Basant Ki Vaar
        "basant_ki_vaar_satvinder_harvinder_singh.opus" to null,

        // Ramkali Ki Vaar
        "ramkali_ki_vaar_darshan_singh.opus" to null,

        // Asa Di Vaar
        "asa_di_vaar_lakhwinder_singh.opus" to null,

        // Morning Nitnem (5 Banis)
        "morning_nitnem_jarnail_singh.opus" to null
    )

    val ALL_REMOTE_ASSETS: List<RemoteAssetTrack> = RAW_AUDIO_DEFINITIONS.map { (audioFile, timingFile) ->
        fromAudioFileName(audioFile, timingFile)
    }

    private var dynamicAssets: List<RemoteAssetTrack>? = null

    fun updateCatalog(assets: List<RemoteAssetTrack>) {
        if (assets.isNotEmpty()) {
            dynamicAssets = assets
        }
    }

    val currentAssets: List<RemoteAssetTrack>
        get() = dynamicAssets ?: ALL_REMOTE_ASSETS

    fun normalizeBaniId(id: String): String = when (id.lowercase().trim()) {
        "sidh-gosht", "sidha-gosht", "sidh_gosht", "sidha_gosht" -> "sidha-gosht"
        "ardas-sahib", "ardas_sahib", "ardas" -> "ardas"
        "chaupai-sahib", "chaupai_sahib", "benti-chaupai", "benti-chaupai-sahib", "benti_chaupai_sahib" -> "benti-chaupai-sahib"
        "tav-prasad", "tav_prasad", "tav-prasad-savaiye", "tav_prasad_savaiye" -> "tav-prasad-savaiye"
        "shabad-hazare", "shabad_hazare", "shabad-hazare-patshahi-10", "shabad_hazare_patshahi_10" -> "shabad-hazare"
        "asa-di-vaar", "asa_di_vaar" -> "asa-di-vaar"
        "salok-mahalla-9", "salok_mahalla_9", "salok-mahalla-nauvan", "salok_mahalla_nauvan" -> "salok-mahalla-9"
        "basant-ki-vaar", "basant_ki_vaar" -> "basant-ki-vaar"
        "ramkali-ki-vaar", "ramkali_ki_vaar" -> "ramkali-ki-vaar"
        "chandi-di-vaar", "chandi_di_vaar" -> "chandi-di-vaar"
        "dakhni-oankar", "dakhni_oankar" -> "dakhni-oankar"
        else -> id.lowercase().trim().replace('_', '-')
    }

    fun getTracksForBani(baniId: String): List<RemoteAssetTrack> {
        val norm = normalizeBaniId(baniId)
        return currentAssets.filter { normalizeBaniId(it.baniId) == norm }
    }

    fun getTrackById(trackId: String): RemoteAssetTrack? =
        currentAssets.firstOrNull { it.id == trackId }
}
