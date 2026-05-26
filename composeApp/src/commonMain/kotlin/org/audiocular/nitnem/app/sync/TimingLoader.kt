package org.audiocular.nitnem.app.sync

import android.content.Context
import kotlinx.serialization.json.Json
import org.audiocular.nitnem.models.LineTiming
import java.io.File

object TimingLoader {

    private val parser = Json {

        ignoreUnknownKeys = true
    }

    fun loadTimings(
        context: Context,
        baniId: String
    ): List<LineTiming> {
        val prefs = context.getSharedPreferences("audio_state", Context.MODE_PRIVATE)
        val activeTrackId = prefs.getString("active_track_id_$baniId", "default") ?: "default"
        return loadTimings(context, baniId, activeTrackId)
    }

    fun loadTimings(
        context: Context,
        baniId: String,
        trackId: String
    ): List<LineTiming> {

        return try {
            val localTimingFile = if (trackId != "default") {
                File(context.filesDir, "audio/${baniId}_${trackId}_timings.json")
            } else {
                File(context.filesDir, "audio/${baniId}_timings.json")
            }

            if (localTimingFile.exists() && localTimingFile.length() > 0) {
                val jsonContent = localTimingFile.readText()
                parser.decodeFromString<List<LineTiming>>(jsonContent)
            } else {
                val resourceName =
                    "${baniId.replace("-", "_")}_timing"

                val resourceId =
                    context.resources.getIdentifier(
                        resourceName,
                        "raw",
                        context.packageName
                    )

                if (resourceId == 0) {

                    emptyList()

                } else {

                    val json =
                        context.resources
                            .openRawResource(resourceId)
                            .bufferedReader()
                            .use {
                                it.readText()
                            }

                    parser.decodeFromString<List<LineTiming>>(json)
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()

            emptyList()
        }
    }
}