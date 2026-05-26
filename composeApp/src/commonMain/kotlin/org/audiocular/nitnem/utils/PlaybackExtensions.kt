package org.audiocular.nitnem.utils

import org.audiocular.nitnem.models.PlaybackSnapshot

val PlaybackSnapshot.progress: Float
    get() {

        return if (durationMs <= 0L)
            0f
        else
            (
                positionMs.toFloat() /
                    durationMs.toFloat()
                ).coerceIn(0f, 1f)
    }