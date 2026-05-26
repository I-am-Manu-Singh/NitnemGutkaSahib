package org.audiocular.nitnem.audiosync

import org.audiocular.nitnem.models.BaniLine

class AudioLineSynchronizer {
    fun activeLine(lines: List<BaniLine>, positionMs: Long): BaniLine? =
        lines.firstOrNull { positionMs in it.startsAtMs until it.endsAtMs }
            ?: lines.lastOrNull { positionMs >= it.endsAtMs }

    fun nextSeekPosition(lines: List<BaniLine>, currentLineId: String): Long =
        lines.firstOrNull { it.id == currentLineId }?.startsAtMs ?: 0L
}
