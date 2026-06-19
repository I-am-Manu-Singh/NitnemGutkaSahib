package org.audiocular.nitnem.app.sync

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json
import org.audiocular.nitnem.models.LineTiming
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

            /*
             * 1. CHECK DOWNLOADED FILE
             */

            val localFile = File(
                context.filesDir,
                "audio/${timingKey}_timing.json"
            )

            if (
                localFile.exists() &&
                localFile.length() > 0
            ) {

                Log.d(
                    TAG,
                    "Using local timing file -> ${localFile.name}"
                )

                return parser.decodeFromString(
                    localFile.readText()
                )
            }

            /*
             * 2. CHECK ASSET FILE
             */

            val assetPath =
                "timing/${timingKey}_timing.json"

            val assetExists = try {

                context.assets
                    .open(assetPath)
                    .close()

                true

            } catch (_: Exception) {

                false
            }

            if (!assetExists) {

                Log.d(
                    TAG,
                    "No timing file found for -> $timingKey"
                )

                return emptyList()
            }

            val json =
                context.assets
                    .open(assetPath)
                    .bufferedReader()
                    .use { it.readText() }

            Log.d(
                TAG,
                "Using asset timing file -> $assetPath"
            )

            parser.decodeFromString(json)

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed loading timing file for -> $timingKey",
                e
            )

            emptyList()
        }
    }
}