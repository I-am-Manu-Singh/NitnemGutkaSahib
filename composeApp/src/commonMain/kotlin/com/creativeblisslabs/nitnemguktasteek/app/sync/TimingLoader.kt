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
            val baniIdPart = cleanKeyUnderscore.substringBefore("_hari_singh")
                .substringBefore("_harinder_singh")
                .substringBefore("_harshdeep_kaur")
                .substringBefore("_jarnail_singh")
                .substringBefore("_thakur_singh")
                .substringBefore("_darshan_singh")
                .substringBefore("_annie_ahluwalia")
                .substringBefore("_hargun_kaur")
                .substringBefore("_satvinder_singh")
                .substringBefore("_ravinder_singh")
                .substringBefore("_pishora_singh")
                .substringBefore("_lakhwinder_singh")

            val remoteAssetTrack = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById(timingKey)
                ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById("curated_$cleanKeyUnderscore")
                ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.currentAssets.firstOrNull { 
                    it.id == timingKey || it.id == "curated_$cleanKeyUnderscore" ||
                    it.timingFileName?.removeSuffix(".json")?.removeSuffix("_timing") == cleanKeyUnderscore ||
                    it.audioFileName.removeSuffix(".opus").removeSuffix(".mp3") == cleanKeyUnderscore
                }

            val candidateFileNames = mutableListOf<String>()
            if (!remoteAssetTrack?.timingFileName.isNullOrBlank()) {
                candidateFileNames.add(remoteAssetTrack!!.timingFileName!!)
            }
            candidateFileNames.add("${cleanKeyUnderscore}_timing.json")
            candidateFileNames.add("${cleanKeyUnderscore}.json")
            candidateFileNames.add("${cleanKeyHyphen}_timing.json")
            candidateFileNames.add("${cleanKeyHyphen}.json")
            candidateFileNames.add("${timingKey}_timing.json")
            candidateFileNames.add("$timingKey.json")

            val isDefaultBaniTrack = cleanKeyUnderscore == baniIdPart || cleanKeyHyphen == baniIdPart.replace('_', '-') || timingKey == "default"
            if (isDefaultBaniTrack) {
                candidateFileNames.add("${baniIdPart}_timing.json")
                candidateFileNames.add("${baniIdPart}.json")
            }

            /*
             * 1. CHECK DOWNLOADED INTERNAL STORAGE FILE
             */
            val audioDir = File(context.filesDir, "audio")
            if (audioDir.exists() && audioDir.isDirectory) {
                // Exact candidate match only
                for (fileName in candidateFileNames.distinct()) {
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
            for (fileName in candidateFileNames.distinct()) {
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
            if (isDefaultBaniTrack) {
                remoteUrls.add("${timingBase}${baniIdPart}_timing.json")
                remoteUrls.add("${timingBase}${baniIdPart}.json")
            }

            for (remoteTimingUrl in remoteUrls.filter { it.isNotBlank() }.distinct()) {
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
                    it.id == normBaniId || it.id == cleanKeyHyphen || it.id == baniIdPart.replace('_', '-')
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
        val remoteAssetTrack = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById(timingKey)
            ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.currentAssets.firstOrNull { 
                it.id == timingKey || it.timingFileName?.removeSuffix(".json")?.removeSuffix("_timing") == timingKey || it.audioFileName.removeSuffix(".opus").removeSuffix(".mp3") == timingKey
            }

        val candidateNames = listOfNotNull(
            remoteAssetTrack?.timingFileName,
            "${timingKey}_timing.json",
            "$timingKey.json"
        ).distinct()

        return candidateNames.any { fileName ->
            val file = File(context.filesDir, "audio/$fileName")
            file.exists() && file.length() > 0
        }
    }

    suspend fun downloadTimingFile(
        context: Context,
        timingKey: String,
        explicitUrl: String? = null
    ): Boolean {
        Log.d(TAG, "Attempting to download timing file for key: $timingKey")
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val remoteAssetTrack = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById(timingKey)
                    ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.currentAssets.firstOrNull { 
                        it.id == timingKey || it.timingFileName?.removeSuffix(".json")?.removeSuffix("_timing") == timingKey || it.audioFileName.removeSuffix(".opus").removeSuffix(".mp3") == timingKey
                    }

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

                val cleanUrls = remoteUrls.filter { it.isNotBlank() }.distinct()
                Log.d(TAG, "Potential URLs for $timingKey: $cleanUrls")

                for (urlStr in cleanUrls) {
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