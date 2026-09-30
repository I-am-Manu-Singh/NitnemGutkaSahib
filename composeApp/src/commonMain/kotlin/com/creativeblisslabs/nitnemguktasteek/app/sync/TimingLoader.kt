package com.creativeblisslabs.nitnemguktasteek.app.sync

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import com.creativeblisslabs.nitnemguktasteek.models.LineTiming
import java.io.File

object TimingLoader {

    private const val TAG = "TimingLoader"

    private val parser = Json {
        ignoreUnknownKeys = true
    }

    fun loadTimings(
        context: Context,
        timingKey: String
    ): List<LineTiming> {

        Log.d(
            TAG,
            "Loading timings for key -> $timingKey"
        )

        return try {
            val cleanKey = timingKey.removePrefix("curated_")
            val cleanKeyUnderscore = cleanKey.replace('-', '_')
            val cleanKeyHyphen = cleanKey.replace('_', '-')

            val remoteAssetTrack = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById(timingKey)
                ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById("curated_$cleanKeyUnderscore")
                ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.currentAssets.firstOrNull { 
                    it.id == timingKey || it.id == "curated_$cleanKeyUnderscore" ||
                    it.timingFileName?.removeSuffix(".json")?.removeSuffix("_timing") == cleanKeyUnderscore ||
                    it.audioFileName.removeSuffix(".opus").removeSuffix(".mp3") == cleanKeyUnderscore
                }

            val expectedArtistName = remoteAssetTrack?.artist?.removeSuffix(" Ji")?.substringAfter("Bhai ")?.substringAfter("Giani ")?.substringAfter("Master ")?.trim()

            val rawCandidateFileNames = mutableListOf<String>()
            if (!remoteAssetTrack?.timingFileName.isNullOrBlank()) {
                rawCandidateFileNames.add(remoteAssetTrack!!.timingFileName!!)
            }
            rawCandidateFileNames.add("${cleanKeyUnderscore}_timing.json")
            rawCandidateFileNames.add("${cleanKeyUnderscore}.json")
            rawCandidateFileNames.add("${cleanKeyHyphen}_timing.json")
            rawCandidateFileNames.add("${cleanKeyHyphen}.json")
            rawCandidateFileNames.add("${timingKey}_timing.json")
            rawCandidateFileNames.add("$timingKey.json")

            val isDefaultBaniTrack = remoteAssetTrack == null || remoteAssetTrack.isPrimaryNitnem || timingKey == "default" || expectedArtistName.isNullOrBlank()

            val candidateFileNames = mutableListOf<String>()
            if (!expectedArtistName.isNullOrBlank()) {
                val artistSlug1 = expectedArtistName.lowercase().replace(" & ", "_").replace(" ", "_")
                val artistSlug2 = expectedArtistName.lowercase().replace(" & ", "-").replace(" ", "-")
                val artistSlug3 = expectedArtistName.lowercase().replace(" ", "")
                for (name in rawCandidateFileNames) {
                    if (name.lowercase().contains(artistSlug1) || 
                        name.lowercase().contains(artistSlug2) ||
                        name.lowercase().contains(artistSlug3)) {
                        if (!candidateFileNames.contains(name)) {
                            candidateFileNames.add(name)
                        }
                    }
                }
            } else {
                for (name in rawCandidateFileNames) {
                    if (!candidateFileNames.contains(name)) {
                        candidateFileNames.add(name)
                    }
                }
            }

            /*
             * 1. CHECK DOWNLOADED INTERNAL STORAGE FILE
             */
            val audioDir = File(context.filesDir, "audio")
            if (audioDir.exists() && audioDir.isDirectory) {
                for (fileName in candidateFileNames) {
                    val localFile = File(audioDir, fileName)
                    if (localFile.exists() && localFile.length() > 0) {
                        try {
                            val parsed = parser.decodeFromString<List<LineTiming>>(localFile.readText())
                            if (parsed.isNotEmpty()) {
                                Log.d(TAG, "Using local timing file -> ${localFile.name}")
                                return parsed
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Error parsing local timing file $fileName: ${e.message}")
                        }
                    }
                }
            }

            /*
             * 2. CHECK ASSET FILE
             */
            for (fileName in candidateFileNames) {
                val assetPath = "timing/$fileName"
                val assetExists = try {
                    context.assets.open(assetPath).close()
                    true
                } catch (_: Exception) {
                    false
                }

                if (assetExists) {
                    val json = context.assets.open(assetPath).bufferedReader().use { it.readText() }
                    try {
                        val parsed = parser.decodeFromString<List<LineTiming>>(json)
                        if (parsed.isNotEmpty()) {
                            Log.d(TAG, "Using asset timing file -> $assetPath")
                            return parsed
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing asset timing file $assetPath: ${e.message}")
                    }
                }
            }

            /*
             * 3. CHECK REMOTE GITHUB TIMING FALLBACK (Only for matching track)
             */
            val remoteUrls = mutableListOf<String>()
            if (remoteAssetTrack?.timingFileName != null) {
                remoteAssetTrack.rawTimingUrl()?.let { remoteUrls.add(it) }
            }
            val timingBase = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.GITHUB_RAW_TIMING_BASE
            remoteUrls.add("${timingBase}${cleanKeyUnderscore}_timing.json")
            remoteUrls.add("${timingBase}${cleanKeyUnderscore}.json")
            remoteUrls.add("${timingBase}${cleanKeyHyphen}_timing.json")
            remoteUrls.add("${timingBase}${timingKey}_timing.json")

            val finalRemoteUrls = mutableListOf<String>()
            if (!expectedArtistName.isNullOrBlank()) {
                val artistSlug1 = expectedArtistName.lowercase().replace(" & ", "_").replace(" ", "_")
                val artistSlug2 = expectedArtistName.lowercase().replace(" & ", "-").replace(" ", "-")
                val trackUrl = remoteAssetTrack?.rawTimingUrl()
                for (url in remoteUrls) {
                    if (url == trackUrl || url.lowercase().contains(artistSlug1) || url.lowercase().contains(artistSlug2)) {
                        if (!finalRemoteUrls.contains(url)) {
                            finalRemoteUrls.add(url)
                        }
                    }
                }
            } else {
                for (url in remoteUrls) {
                    if (!finalRemoteUrls.contains(url)) {
                        finalRemoteUrls.add(url)
                    }
                }
            }

            for (remoteTimingUrl in finalRemoteUrls) {
                if (remoteTimingUrl.isBlank()) continue
                try {
                    val connection = java.net.URL(remoteTimingUrl).openConnection() as java.net.HttpURLConnection
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000
                    connection.setRequestProperty("User-Agent", "NitnemGutka")

                    if (connection.responseCode == java.net.HttpURLConnection.HTTP_OK) {
                        val remoteJson = connection.inputStream.bufferedReader().use { it.readText() }
                        if (remoteJson.isNotBlank() && remoteJson.trim().startsWith("[")) {
                            val parsed = parser.decodeFromString<List<LineTiming>>(remoteJson)
                            if (parsed.isNotEmpty()) {
                                // Cache locally for offline future use
                                try {
                                    val saveName = remoteAssetTrack?.timingFileName ?: "${cleanKeyUnderscore}_timing.json"
                                    val saveFile = File(context.filesDir, "audio/$saveName")
                                    saveFile.parentFile?.mkdirs()
                                    saveFile.writeText(remoteJson)
                                    // Also cache alias
                                    File(context.filesDir, "audio/${cleanKeyUnderscore}_timing.json").writeText(remoteJson)
                                } catch (e: Exception) {
                                    Log.w(TAG, "Could not cache remote timing file: ${e.message}")
                                }
                                return parsed
                            }
                        }
                    }
                    connection.disconnect()
                } catch (e: Exception) {
                    Log.w(TAG, "Failed downloading remote timing from $remoteTimingUrl: ${e.message}")
                }
            }

            /*
             * 4. EMBEDDED BANI CATALOG BASELINE FALLBACK (Only for primary/default track)
             */
            if (isDefaultBaniTrack) {
                val normBaniId = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.normalizeBaniId(cleanKeyHyphen)
                val embeddedBani = com.creativeblisslabs.nitnemguktasteek.data.generated.GeneratedBaniCatalog.banis.firstOrNull { 
                    it.id == normBaniId || it.id == cleanKeyHyphen
                }
                if (embeddedBani != null && embeddedBani.lines.any { it.startsAtMs > 0L || it.endsAtMs > 0L }) {
                    Log.d(TAG, "Using embedded line timings for default bani -> ${embeddedBani.id}")
                    return embeddedBani.lines.map { line ->
                        LineTiming(id = line.id, startsAtMs = line.startsAtMs, endsAtMs = line.endsAtMs)
                    }
                }
            }

            Log.d(TAG, "No timing file found for -> $timingKey")
            emptyList()

        } catch (e: Exception) {
            Log.e(TAG, "Failed loading timing file for -> $timingKey", e)
            emptyList()
        }
    }

    fun isTimingDownloadedLocally(
        context: Context,
        timingKey: String
    ): Boolean {
        val cleanKey = timingKey.removePrefix("curated_")
        val cleanKeyUnderscore = cleanKey.replace('-', '_')

        val remoteAssetTrack = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById(timingKey)
            ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById("curated_$cleanKeyUnderscore")
            ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.currentAssets.firstOrNull { 
                it.id == timingKey || it.timingFileName?.removeSuffix(".json")?.removeSuffix("_timing") == timingKey || it.audioFileName.removeSuffix(".opus").removeSuffix(".mp3") == timingKey
            }

        val expectedArtistName = remoteAssetTrack?.artist?.removeSuffix(" Ji")?.substringAfter("Bhai ")?.substringAfter("Giani ")?.substringAfter("Master ")?.trim()

        val rawCandidateNames = mutableListOf<String>()
        if (remoteAssetTrack?.timingFileName != null) {
            rawCandidateNames.add(remoteAssetTrack.timingFileName!!)
        }
        rawCandidateNames.add("${timingKey}_timing.json")
        rawCandidateNames.add("$timingKey.json")

        val candidateNames = mutableListOf<String>()
        if (!expectedArtistName.isNullOrBlank()) {
            val artistSlug1 = expectedArtistName.lowercase().replace(" & ", "_").replace(" ", "_")
            val artistSlug2 = expectedArtistName.lowercase().replace(" & ", "-").replace(" ", "-")
            val artistSlug3 = expectedArtistName.lowercase().replace(" ", "")
            for (name in rawCandidateNames) {
                if (name.lowercase().contains(artistSlug1) || 
                    name.lowercase().contains(artistSlug2) ||
                    name.lowercase().contains(artistSlug3)) {
                    if (!candidateNames.contains(name)) {
                        candidateNames.add(name)
                    }
                }
            }
        } else {
            for (name in rawCandidateNames) {
                if (!candidateNames.contains(name)) {
                    candidateNames.add(name)
                }
            }
        }

        for (fileName in candidateNames) {
            val file = File(context.filesDir, "audio/$fileName")
            if (file.exists() && file.length() > 0) {
                return true
            }
        }
        return false
    }

    suspend fun downloadTimingFile(
        context: Context,
        timingKey: String,
        explicitUrl: String? = null
    ): Boolean {
        Log.d(TAG, "Attempting to download timing file for key: $timingKey")
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val cleanKey = timingKey.removePrefix("curated_")
                val cleanKeyUnderscore = cleanKey.replace('-', '_')

                val remoteAssetTrack = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById(timingKey)
                    ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById("curated_$cleanKeyUnderscore")
                    ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.currentAssets.firstOrNull { 
                        it.id == timingKey || it.timingFileName?.removeSuffix(".json")?.removeSuffix("_timing") == timingKey || it.audioFileName.removeSuffix(".opus").removeSuffix(".mp3") == timingKey
                    }

                val expectedArtistName = remoteAssetTrack?.artist?.removeSuffix(" Ji")?.substringAfter("Bhai ")?.substringAfter("Giani ")?.substringAfter("Master ")?.trim()

                if (remoteAssetTrack == null) {
                    Log.w(TAG, "No matching remote asset found for timingKey: $timingKey. Proceeding with fallback URLs.")
                }

                val remoteUrls = mutableListOf<String>()
                if (!explicitUrl.isNullOrBlank()) {
                    remoteUrls.add(explicitUrl)
                }
                if (remoteAssetTrack?.timingFileName != null) {
                    remoteAssetTrack.rawTimingUrl()?.let { remoteUrls.add(it) }
                }
                val timingBase = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.GITHUB_RAW_TIMING_BASE
                remoteUrls.add("${timingBase}${timingKey}_timing.json")
                remoteUrls.add("${timingBase}${timingKey}.json")

                val finalUrls = mutableListOf<String>()
                if (!expectedArtistName.isNullOrBlank()) {
                    val artistSlug1 = expectedArtistName.lowercase().replace(" & ", "_").replace(" ", "_")
                    val artistSlug2 = expectedArtistName.lowercase().replace(" & ", "-").replace(" ", "-")
                    val artistSlug3 = expectedArtistName.lowercase().replace(" ", "")
                    for (url in remoteUrls) {
                        if (url.isBlank()) continue
                        if (url == explicitUrl || 
                            url == remoteAssetTrack?.rawTimingUrl() || 
                            url.lowercase().contains(artistSlug1) || 
                            url.lowercase().contains(artistSlug2) ||
                            url.lowercase().contains(artistSlug3)) {
                            if (!finalUrls.contains(url)) {
                                finalUrls.add(url)
                            }
                        }
                    }
                } else {
                    for (url in remoteUrls) {
                        if (url.isNotBlank() && !finalUrls.contains(url)) {
                            finalUrls.add(url)
                        }
                    }
                }
                Log.d(TAG, "Potential URLs for $timingKey: $finalUrls")

                for (urlStr in finalUrls) {
                    try {
                        Log.v(TAG, "Downloading from: $urlStr")
                        val connection = java.net.URL(urlStr).openConnection() as java.net.HttpURLConnection
                        connection.connectTimeout = 8000
                        connection.readTimeout = 8000
                        connection.setRequestProperty("User-Agent", "NitnemGutka")

                        if (connection.responseCode == java.net.HttpURLConnection.HTTP_OK) {
                            val json = connection.inputStream.bufferedReader().use { it.readText() }
                            if (json.isNotBlank()) {
                                val parsed = try {
                                    parser.decodeFromString<List<LineTiming>>(json)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Failed to parse JSON from $urlStr: ${e.message}")
                                    null
                                }
                                
                                if (!parsed.isNullOrEmpty()) {
                                    val saveName = remoteAssetTrack?.timingFileName ?: "${timingKey}_timing.json"
                                    val saveFile = File(context.filesDir, "audio/$saveName")
                                    saveFile.parentFile?.mkdirs()
                                    saveFile.writeText(json)
                                    Log.d(TAG, "Successfully downloaded and cached timing file -> ${saveFile.name}")
                                    return@withContext true
                                }
                            }
                        } else {
                            Log.v(TAG, "HTTP ${connection.responseCode} from $urlStr")
                        }
                        connection.disconnect()
                    } catch (e: Exception) {
                        Log.w(TAG, "Error downloading timing from $urlStr: ${e.message}")
                    }
                }
                Log.w(TAG, "Exhausted all URLs for $timingKey without success.")
                false
            } catch (e: Exception) {
                Log.e(TAG, "Failed downloading timing file for $timingKey", e)
                false
            }
        }
    }
}