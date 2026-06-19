package org.audiocular.nitnem.utils

import org.audiocular.nitnem.models.AudioTrack
import java.io.File

fun AudioTrack.timingKey(): String {

    val url = remoteUrl.orEmpty()

    /*
     * RAW AUDIO
     * raw/chaupai_sahib_darshan_singh
     */

    if (url.startsWith("raw/")) {

        return url
            .removePrefix("raw/")
            .substringBeforeLast(".")
    }

    /*
     * ASSET AUDIO
     * asset://audio/chaupai_sahib_darshan_singh.mp3
     */

    if (url.startsWith("asset://audio/")) {

        return url
            .removePrefix("asset://audio/")
            .substringBeforeLast(".")
    }

    /*
     * LOCAL IMPORTED AUDIO
     */

    localPath?.let { path ->

        return File(path)
            .nameWithoutExtension
    }

    /*
     * REMOTE FILE URL
     * https://.../chaupai_sahib_darshan_singh.mp3
     */

    if (url.isNotBlank()) {

        return url
            .substringAfterLast("/")
            .substringBeforeLast(".")
    }

    /*
     * FALLBACK
     */

    return id
        .replace("-", "_")
}