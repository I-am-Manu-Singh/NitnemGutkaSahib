package org.audiocular.nitnem.audiosync

import org.audiocular.nitnem.models.BaniLine
class AudioLineSynchronizer {

    fun activeLine(
        lines: List<BaniLine>,
        positionMs: Long
    ): BaniLine? {

        if (
            lines.isEmpty() ||
            lines.all {
                it.startsAtMs == 0L &&
                        it.endsAtMs == 0L
            }
        ) {

            return null
        }

        val active =
            lines.firstOrNull {
                positionMs in it.startsAtMs until it.endsAtMs
            }

        if (active != null) {



            return active
        }

        val fallback =
            lines.lastOrNull {
                positionMs >= it.endsAtMs
            }

        return fallback
    }

    fun nextSeekPosition(
        lines: List<BaniLine>,
        currentLineId: String
    ): Long =
        lines.firstOrNull {
            it.id == currentLineId
        }?.startsAtMs ?: 0L
}