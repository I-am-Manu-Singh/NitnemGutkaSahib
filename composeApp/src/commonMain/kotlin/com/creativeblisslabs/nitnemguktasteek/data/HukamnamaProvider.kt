package com.creativeblisslabs.nitnemguktasteek.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.creativeblisslabs.nitnemguktasteek.data.generated.GeneratedBaniCatalog
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.daysUntil
import android.content.Context

@Serializable
data class HukamnamaLine(
    val gurmukhi: String,
    
    val larivaar: String,

    val transliteration: String,

    val transliterationHi: String,

    val translationEn: String,

    val translationPa: String
)

@Serializable
data class Hukamnama(

    val lines: List<HukamnamaLine>,

    val ang: String,

    val raag: String,

    val writer: String,

    val source: String,

    val date: String,
    val isFromApi: Boolean
)

@Serializable
data class HukamnamaCache(
    val lastFetchTime: Long,
    val hukamnama: Hukamnama
)

object HukamnamaProvider {

    private val client by lazy {
        HttpClient()
    }

    private val jsonParser by lazy {

        Json {
            ignoreUnknownKeys = true
        }
    }

    suspend fun getDailyHukamnama(context: Context): Hukamnama = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val cacheFile = java.io.File(context.cacheDir, "hukamnama_cache.json")
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val timeZone = TimeZone.currentSystemDefault()
        val currentLocalDate = Clock.System.now().toLocalDateTime(timeZone).date

        // Try reading from cache first
        var cachedData: HukamnamaCache? = null
        if (cacheFile.exists()) {
            try {
                val jsonContent = cacheFile.readText()
                cachedData = jsonParser.decodeFromString<HukamnamaCache>(jsonContent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (cachedData != null) {
            val lastFetchLocalDate = Instant.fromEpochMilliseconds(cachedData.lastFetchTime)
                .toLocalDateTime(timeZone).date

            val isTodayHukamnama = cachedData.hukamnama.date.contains(currentLocalDate.dayOfMonth.toString()) &&
                    cachedData.hukamnama.date.contains(currentLocalDate.month.name, ignoreCase = true)

            // If the last fetch was on the same calendar day AND it is today's actual Hukamnama, return it directly to save data
            if (lastFetchLocalDate == currentLocalDate && isTodayHukamnama) {
                return@withContext cachedData.hukamnama
            }

            // Otherwise (different day), we try to fetch a new one
            try {
                val newHukamnama = fetchAndParse()
                val newCache = HukamnamaCache(lastFetchTime = currentTime, hukamnama = newHukamnama)
                cacheFile.writeText(jsonParser.encodeToString(HukamnamaCache.serializer(), newCache))
                return@withContext newHukamnama
            } catch (e: Exception) {
                e.printStackTrace()
                // Network fetch failed (offline)
                // Check if offline for more than 1 day
                val daysBetween = lastFetchLocalDate.daysUntil(currentLocalDate)
                if (daysBetween <= 1) {
                    // Cached data is from yesterday, we can still show it
                    return@withContext cachedData.hukamnama
                } else {
                    // Cached data is older than 1 day, show app generated one
                    return@withContext getRandomFromCatalog()
                }
            }
        } else {
            // No cache exists yet, fetch from API
            try {
                val newHukamnama = fetchAndParse()
                val newCache = HukamnamaCache(lastFetchTime = currentTime, hukamnama = newHukamnama)
                cacheFile.writeText(jsonParser.encodeToString(HukamnamaCache.serializer(), newCache))
                return@withContext newHukamnama
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext getRandomFromCatalog()
            }
        }
    }

    private suspend fun fetchAndParse(): Hukamnama {

        val responseText =
            client.get(
                "https://data.gurbaninow.com/v2/hukamnama/today"
            ).bodyAsText()

        val root =
            jsonParser
                .parseToJsonElement(
                    responseText
                )
                .jsonObject

        val hukamnamaArray =
            root["hukamnama"]
                ?.jsonArray

        val hukamnamaInfo =
            root["hukamnamainfo"]
                ?.jsonObject

        val dateInfo =
            root["date"]
                ?.jsonObject
                ?.get("gregorian")
                ?.jsonObject

        if (
            hukamnamaArray == null ||
            hukamnamaArray.isEmpty()
        ) {

            return getRandomFromCatalog()
        }

        val parsedLines =
            hukamnamaArray.mapNotNull {

                val line =
                    it.jsonObject["line"]
                        ?.jsonObject

                val type =
                    line?.get("type")
                        ?.jsonPrimitive
                        ?.content
                        ?.toIntOrNull()

                /*
                 * Skip headings/meta
                 */

                if (
                    line == null ||
                    type == 2
                ) {

                    return@mapNotNull null
                }

                HukamnamaLine(

                    gurmukhi =
                        line["gurmukhi"]
                            ?.jsonObject
                            ?.get("unicode")
                            ?.jsonPrimitive
                            ?.content
                            ?: "",

                    larivaar =
                        line["larivaar"]
                            ?.jsonObject
                            ?.get("unicode")
                            ?.jsonPrimitive
                            ?.content
                            ?: "",

                    transliteration =
                        line["transliteration"]
                            ?.jsonObject
                            ?.get("english")
                            ?.jsonObject
                            ?.get("text")
                            ?.jsonPrimitive
                            ?.content
                            ?: "",

                    transliterationHi =
                        line["transliteration"]
                            ?.jsonObject
                            ?.get("devanagari")
                            ?.jsonObject
                            ?.get("text")
                            ?.jsonPrimitive
                            ?.content
                            ?: "",

                    translationEn =
                        line["translation"]
                            ?.jsonObject
                            ?.get("english")
                            ?.jsonObject
                            ?.get("default")
                            ?.jsonPrimitive
                            ?.content
                            ?: "",

                    translationPa =
                        line["translation"]
                            ?.jsonObject
                            ?.get("punjabi")
                            ?.jsonObject
                            ?.get("default")
                            ?.jsonObject
                            ?.get("unicode")
                            ?.jsonPrimitive
                            ?.content
                            ?: ""
                )
            }

        val writer =
            hukamnamaInfo
                ?.get("writer")
                ?.jsonObject
                ?.get("english")
                ?.jsonPrimitive
                ?.content
                ?: "Guru Granth Sahib Ji"

        val raag =
            hukamnamaInfo
                ?.get("raag")
                ?.jsonObject
                ?.get("english")
                ?.jsonPrimitive
                ?.content
                ?: "Sri Guru Granth Sahib Ji"

        val source =
            hukamnamaInfo
                ?.get("source")
                ?.jsonObject
                ?.get("english")
                ?.jsonPrimitive
                ?.content
                ?: "Sri Harmandir Sahib"

        val ang =
            hukamnamaInfo
                ?.get("pageno")
                ?.jsonPrimitive
                ?.content
                ?: "0"

        val formattedDate =
            buildString {

                append(
                    dateInfo
                        ?.get("day")
                        ?.jsonPrimitive
                        ?.content
                        ?: ""
                )

                append(", ")

                append(
                    dateInfo
                        ?.get("month")
                        ?.jsonPrimitive
                        ?.content
                        ?: ""
                )

                append(" ")

                append(
                    dateInfo
                        ?.get("date")
                        ?.jsonPrimitive
                        ?.content
                        ?: ""
                )

                append(", ")

                append(
                    dateInfo
                        ?.get("year")
                        ?.jsonPrimitive
                        ?.content
                        ?: ""
                )
            }

        return Hukamnama(

            lines =
                parsedLines,

            ang =
                ang,

            raag =
                raag,

            writer =
                writer,

            source =
                source,

            date =
                formattedDate,
            isFromApi = true
        )
    }

    fun getRandomFromCatalog(): Hukamnama {

        val sourceBanis = listOf(

            "asa-di-vaar",
            "sukhmani-sahib",
            "anand-sahib",
            "barah-maha",
            "dakhni-oankar",
            "sidha-gosht",
            "ramkali-ki-vaar"
        )

        val randomBani =
            GeneratedBaniCatalog.banis
                .filter {
                    it.id in sourceBanis
                }
                .random()

        val selectedChunk =
            randomBani.lines
                .filter {
                    it.gurmukhi.isNotBlank()
                }
                .shuffled()
                .take(6)

        /*
         * Create proper individual lines
         * instead of one giant paragraph
         */

        val parsedLines =
            selectedChunk.map {

                HukamnamaLine(

                    gurmukhi =
                        it.gurmukhi,

                    larivaar =
                        it.gurmukhi.replace(" ", ""),

                    transliteration =
                        it.wordMeaning,

                    transliterationHi =
                        it.hindi,

                    translationPa =
                        it.punjabi,

                    translationEn =
                        it.translation
                )
            }

        return Hukamnama(

            lines =
                parsedLines,

            ang =
                randomBani.title,

            raag =
                "Sri Guru Granth Sahib Ji",

            writer =
                "Guru Granth Sahib Ji",

            source =
                "Offline Gurbani Collection",

            date =
                Clock.System.now()
                    .toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    )
                    .date
                    .toString(),
            isFromApi = false
        )
    }
}