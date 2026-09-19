package com.creativeblisslabs.nitnemguktasteek.domain.search

import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.BaniLine

data class SearchResult(
    val baniId: String,
    val baniTitle: String,
    val lineIndex: Int,
    val line: BaniLine,
    val matchedGurmukhiInitials: String
)

object GurbaniSearchEngine {

    /**
     * Extracts first letter of each Gurmukhi word in a line, ignoring common punctuation.
     * e.g. "ੴ ਸਤਿ ਨਾਮੁ ਕਰਤਾ ਪੁਰਖੁ" -> "ੴਸਨਕਪ"
     */
    fun extractFirstLetters(text: String): String {
        return text
            .replace(Regex("[।|,|;||!|?|:|\"|'|(|)|\\[|\\]|{}]"), " ")
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .mapNotNull { it.firstOrNull() }
            .joinToString("")
    }

    /**
     * Searches all lines across all loaded Banis by First Letter of Gurmukhi words or query text.
     */
    fun searchByFirstLetters(
        banis: List<Bani>,
        query: String,
        limit: Int = 100
    ): List<SearchResult> {
        val cleanQuery = query.trim().replace(" ", "")
        if (cleanQuery.isBlank()) return emptyList()

        val results = mutableListOf<SearchResult>()

        for (bani in banis) {
            for ((index, line) in bani.lines.withIndex()) {
                val initials = extractFirstLetters(line.gurmukhi)
                if (initials.contains(cleanQuery, ignoreCase = true) || 
                    line.gurmukhi.contains(query.trim(), ignoreCase = true) ||
                    line.transliteration.contains(query.trim(), ignoreCase = true) ||
                    line.translation.contains(query.trim(), ignoreCase = true)) {
                    results.add(
                        SearchResult(
                            baniId = bani.id,
                            baniTitle = bani.title,
                            lineIndex = index,
                            line = line,
                            matchedGurmukhiInitials = initials
                        )
                    )
                    if (results.size >= limit) return results
                }
            }
        }

        return results
    }

    /**
     * Search by Ang (Page) number or Raag header in available Banis.
     */
    fun searchByAngOrRaag(
        banis: List<Bani>,
        query: String
    ): List<Bani> {
        val q = query.trim()
        if (q.isBlank()) return emptyList()

        return banis.filter { bani ->
            bani.ang.contains(q, ignoreCase = true) ||
            bani.title.contains(q, ignoreCase = true) ||
            bani.structure.contains(q, ignoreCase = true)
        }
    }
}
