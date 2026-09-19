package com.creativeblisslabs.nitnemguktasteek.data.generated

import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.BaniLine
import com.creativeblisslabs.nitnemguktasteek.models.BaniTime
import com.creativeblisslabs.nitnemguktasteek.models.ContentSource

fun generatedMorningNitnem(): Bani = Bani(
    id = "morning-nitnem",
    title = "Morning Nitnem (5 Banis)",
    time = BaniTime.Morning,
    estimatedMinutes = 58,
    audio = null,
    source = ContentSource(
        text = "ShabadOS Database 5.0.0-next.0, Panj Bania collection.",
        audio = "Morning Nitnem recitation by Bhai Jarnail Singh Ji",
        licenseNote = "Gurbani text and supporting translations from ShabadOS database package."
    ),
    author = "Guru Nanak Dev Ji, Guru Gobind Singh Ji, Guru Amar Das Ji",
    ang = "Ang 1 - 922",
    structure = "Japji Sahib • Jaap Sahib • Tav Prasad Savaiye • Chaupai Sahib • Anand Sahib",
    lines = generatedMorningNitnemLines()
)

fun generatedMorningNitnemLines(): List<BaniLine> = buildList {
    addAll(generatedJapjiSahibLines())
    addAll(generatedJaapSahibLines())
    addAll(generatedTavPrasadSavaiyeLines())
    addAll(generatedBentiChaupaiSahibLines())
    addAll(generatedAnandSahibLines())
}
