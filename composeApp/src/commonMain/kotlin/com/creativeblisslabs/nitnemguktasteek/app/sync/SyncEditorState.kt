package com.creativeblisslabs.nitnemguktasteek.app.sync

import androidx.compose.runtime.*
import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.LineTiming

class SyncEditorState(
    bani: Bani
) {

    val lines =
        bani.lines

    var currentIndex by mutableStateOf(0)

    var timings by mutableStateOf(
        emptyList<LineTiming>()
    )

    fun syncCurrentLine(
        currentMs: Long
    ) {

        val line =
            lines.getOrNull(currentIndex)
                ?: return

        val updated =
            timings.toMutableList()

        // close previous line

        if (updated.isNotEmpty()) {

            val previous =
                updated.last()

            updated[updated.lastIndex] =

                previous.copy(
                    endsAtMs = currentMs
                )
        }

        // add current line

        updated += LineTiming(

            id = line.id,

            startsAtMs = currentMs,

            // temporary end time until next tap

            endsAtMs = currentMs + 1000L
        )

        timings = updated

        currentIndex++
    }

    fun undo() {

        if (timings.isEmpty()) {
            return
        }

        timings =
            timings.dropLast(1)

        currentIndex =
            (currentIndex - 1)
                .coerceAtLeast(0)
    }

    fun completeLastLine(
        audioDurationMs: Long
    ) {

        if (timings.isEmpty()) {
            return
        }

        val updated =
            timings.toMutableList()

        val last =
            updated.last()

        updated[updated.lastIndex] =

            last.copy(
                endsAtMs = audioDurationMs
            )

        timings = updated
    }

    fun exportJson(): String {

        return TimingExporter.export(
            timings
        )
    }
}