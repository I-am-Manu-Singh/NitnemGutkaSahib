package org.audiocular.nitnem.data.analytics

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

private val Context.analyticsDataStore by
    preferencesDataStore(
        name = "nitnem_analytics"
    )

@Serializable
data class DailyProgress(

    val date: String,

    val completedBanis: Set<String>,

   val readingMinutes: Int,

val listeningMinutes: Int = 0
)

@Serializable
data class AnalyticsState(

    val history: List<DailyProgress> =
        emptyList()
)

class AnalyticsRepository(
    private val context: Context
) {

    companion object {

        private val ANALYTICS_KEY =
            stringPreferencesKey(
                "analytics_state"
            )
    }

    private val json = Json {

        ignoreUnknownKeys = true
    }

    val analyticsFlow:
        Flow<AnalyticsState> =

        context.analyticsDataStore
            .data
            .map { prefs ->

                val raw =
                    prefs[ANALYTICS_KEY]

                if (raw == null) {

                    AnalyticsState()

                } else {

                    runCatching {

                        json.decodeFromString<
                            AnalyticsState
                        >(raw)

                    }.getOrDefault(
                        AnalyticsState()
                    )
                }
            }
suspend fun addListeningMinutes(
    minutes: Int
) {

    val today =
        LocalDate.now().toString()

    context.analyticsDataStore.edit { prefs ->

        val current =
            prefs[ANALYTICS_KEY]

        val state =

            if (current == null) {

                AnalyticsState()

            } else {

                runCatching {

                    json.decodeFromString<
                        AnalyticsState
                    >(current)

                }.getOrDefault(
                    AnalyticsState()
                )
            }

        val existing =
            state.history.firstOrNull {
                it.date == today
            }

        val updatedDay =

            if (existing != null) {

                existing.copy(

                    listeningMinutes =
                        existing.listeningMinutes +
                            minutes
                )

            } else {

                DailyProgress(

                    date = today,

                    completedBanis =
                        emptySet(),

                    readingMinutes = 0,

                    listeningMinutes =
                        minutes
                )
            }

        val updatedHistory =
            state.history
                .filterNot {
                    it.date == today
                } + updatedDay

        prefs[ANALYTICS_KEY] =

            json.encodeToString(

                AnalyticsState(
                    history =
                        updatedHistory
                )
            )
    }
}
    suspend fun markCompleted(

        baniId: String,

        readingMinutes: Int
    ) {

        val today =
            LocalDate.now().toString()

        context.analyticsDataStore.edit { prefs ->

            val current =
                prefs[ANALYTICS_KEY]

            val state =
                if (current == null) {

                    AnalyticsState()

                } else {

                    runCatching {

                        json.decodeFromString<
                            AnalyticsState
                        >(current)

                    }.getOrDefault(
                        AnalyticsState()
                    )
                }

            val existing =
                state.history.firstOrNull {
                    it.date == today
                }

            val updatedDay =

                if (existing != null) {

                    existing.copy(

                        completedBanis =
                            existing.completedBanis +
                                baniId,

                        readingMinutes =
                            existing.readingMinutes +
                                readingMinutes
                    )

                } else {

                    DailyProgress(

                        date = today,

                        completedBanis =
                            setOf(baniId),

                        readingMinutes =
                            readingMinutes
                    )
                }

            val updatedHistory =
                state.history
                    .filterNot {
                        it.date == today
                    } + updatedDay

            prefs[ANALYTICS_KEY] =

                json.encodeToString(

                    AnalyticsState(
                        history =
                            updatedHistory
                    )
                )
        }
    }

    fun calculateCurrentStreak(
        history: List<DailyProgress>
    ): Int {

        val sortedDates =
            history.map {
                LocalDate.parse(it.date)
            }.sortedDescending()

        if (sortedDates.isEmpty()) {
            return 0
        }

        var streak = 0
        var expected =
            LocalDate.now()

        for (date in sortedDates) {

            if (date == expected) {

                streak++

                expected =
                    expected.minusDays(1)

            } else {

                break
            }
        }

        return streak
    }

    fun monthlyConsistency(
        history: List<DailyProgress>
    ): Int {

        val now =
            LocalDate.now()

        return history.count {

            val date =
                LocalDate.parse(it.date)

            date.month == now.month &&
                date.year == now.year
        }
    }

    fun totalReadingMinutes(
        history: List<DailyProgress>
    ): Int {

        return history.sumOf {
            it.readingMinutes
        }
    }
}