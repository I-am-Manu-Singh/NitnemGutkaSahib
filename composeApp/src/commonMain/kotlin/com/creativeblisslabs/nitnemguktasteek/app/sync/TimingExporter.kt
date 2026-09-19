package com.creativeblisslabs.nitnemguktasteek.app.sync

import com.creativeblisslabs.nitnemguktasteek.models.LineTiming

object TimingExporter {

    fun export(
        timings: List<LineTiming>
    ): String {

        return buildString {

            append("[\n")

            timings.forEachIndexed { index, timing ->

                append(
                    """
{
  "id": "${timing.id}",
  "startsAtMs": ${timing.startsAtMs},
  "endsAtMs": ${timing.endsAtMs}
}
                    """.trimIndent()
                )

                if (index != timings.lastIndex) {

                    append(",")
                }

                append("\n")
            }

            append("]")
        }
    }
}