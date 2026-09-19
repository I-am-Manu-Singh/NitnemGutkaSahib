package com.creativeblisslabs.nitnemguktasteek.utils

import com.creativeblisslabs.nitnemguktasteek.models.PlaybackSnapshot

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