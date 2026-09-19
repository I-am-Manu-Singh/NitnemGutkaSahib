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
            // Find possible file names to check (exact timingFileName from RemoteAssetCatalog or derived keys)
            val remoteAssetTrack = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById(timingKey)
                ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.currentAssets.firstOrNull { 
                    it.id == timingKey || it.timingFileName?.removeSuffix(".json")?.removeSuffix("_timing") == timingKey || it.audioFileName.removeSuffix(".opus").removeSuffix(".mp3") == timingKey
                }

            val candidateFileNames = mutableListOf<String>()
            val remoteTimingFileName = remoteAssetTrack?.timingFileName
            if (remoteTimingFileName != null) {
                candidateFileNames.add(remoteTimingFileName)
            }
            candidateFileNames.add("${timingKey}_timing.json")
            candidateFileNames.add("$timingKey.json")
            if (!timingKey.endsWith("_timing")) {
                candidateFileNames.add("${timingKey}_timing.json")
            }

            /*
             * 1. CHECK DOWNLOADED INTERNAL STORAGE FILE
             */
            for (fileName in candidateFileNames.distinct()) {
                val localFile = File(
                    context.filesDir,
                    "audio/$fileName"
                )
                if (localFile.exists() && localFile.length() > 0) {
                    Log.d(TAG, "Using local timing file -> ${localFile.name}")
                    try {
                        val parsed = parser.decodeFromString<List<LineTiming>>(localFile.readText())
                        if (parsed.isNotEmpty()) return parsed
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing local timing file $fileName: ${e.message}")
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
                    Log.d(TAG, "Using asset timing file -> $assetPath")
                    try {
                        val parsed = parser.decodeFromString<List<LineTiming>>(json)
                        if (parsed.isNotEmpty()) return parsed
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing asset timing file $assetPath: ${e.message}")
                    }
                }
            }

            /*
             * 3. CHECK REMOTE GITHUB TIMING FALLBACK
             */
            val remoteUrls = mutableListOf<String>()
            if (remoteAssetTrack?.timingFileName != null) {
                remoteUrls.add(remoteAssetTrack.rawTimingUrl() ?: "")
            }
            val timingBase = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.GITHUB_RAW_TIMING_BASE
            remoteUrls.add("${timingBase}${timingKey}_timing.json")
            remoteUrls.add("${timingBase}${timingKey}.json")

            for (remoteTimingUrl in remoteUrls.filter { it.isNotBlank() }.distinct()) {
                try {
                    Log.d(TAG, "Attempting remote timing download from -> $remoteTimingUrl")
                    val connection = java.net.URL(remoteTimingUrl).openConnection() as java.net.HttpURLConnection
                    connection.connectTimeout = 8000
                    connection.readTimeout = 8000
                    connection.setRequestProperty("User-Agent", "NitnemGutka")

                    if (connection.responseCode == java.net.HttpURLConnection.HTTP_OK) {
                        val remoteJson = connection.inputStream.bufferedReader().use { it.readText() }
                        if (remoteJson.isNotBlank()) {
                            val parsed = parser.decodeFromString<List<LineTiming>>(remoteJson)
                            if (parsed.isNotEmpty()) {
                                // Cache locally for offline future use
                                try {
                                    val saveName = remoteAssetTrack?.timingFileName ?: "${timingKey}_timing.json"
                                    val saveFile = File(context.filesDir, "audio/$saveName")
                                    saveFile.parentFile?.mkdirs()
                                    saveFile.writeText(remoteJson)
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

            Log.d(
                TAG,
                "No timing file found for -> $timingKey"
            )

            emptyList()

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed loading timing file for -> $timingKey",
                e
            )

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
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val remoteAssetTrack = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.getTrackById(timingKey)
                    ?: com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.currentAssets.firstOrNull { 
                        it.id == timingKey || it.timingFileName?.removeSuffix(".json")?.removeSuffix("_timing") == timingKey || it.audioFileName.removeSuffix(".opus").removeSuffix(".mp3") == timingKey
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

                for (urlStr in remoteUrls.filter { it.isNotBlank() }.distinct()) {
                    try {
                        val connection = java.net.URL(urlStr).openConnection() as java.net.HttpURLConnection
                        connection.connectTimeout = 8000
                        connection.readTimeout = 8000
                        connection.setRequestProperty("User-Agent", "NitnemGutka")

                        if (connection.responseCode == java.net.HttpURLConnection.HTTP_OK) {
                            val json = connection.inputStream.bufferedReader().use { it.readText() }
                            if (json.isNotBlank()) {
                                val parsed = parser.decodeFromString<List<LineTiming>>(json)
                                if (parsed.isNotEmpty()) {
                                    val saveName = remoteAssetTrack?.timingFileName ?: "${timingKey}_timing.json"
                                    val saveFile = File(context.filesDir, "audio/$saveName")
                                    saveFile.parentFile?.mkdirs()
                                    saveFile.writeText(json)
                                    Log.d(TAG, "Successfully downloaded and cached timing file -> ${saveFile.name}")
                                    return@withContext true
                                }
                            }
                        }
                        connection.disconnect()
                    } catch (e: Exception) {
                        Log.w(TAG, "Error downloading timing from $urlStr: ${e.message}")
                    }
                }
                false
            } catch (e: Exception) {
                Log.e(TAG, "Failed downloading timing file for $timingKey", e)
                false
            }
        }
    }
}