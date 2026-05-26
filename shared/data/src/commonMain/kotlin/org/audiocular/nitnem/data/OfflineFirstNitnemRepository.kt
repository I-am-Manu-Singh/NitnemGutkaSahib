package org.audiocular.nitnem.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.audiocular.nitnem.data.generated.GeneratedBaniCatalog
import org.audiocular.nitnem.domain.NitnemRepository
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.HomeSnapshot
import org.audiocular.nitnem.models.JourneyStep
import org.audiocular.nitnem.models.Reflection
import org.audiocular.nitnem.models.ScriptMode
import org.audiocular.nitnem.models.StreakSummary

class OfflineFirstNitnemRepository :
    NitnemRepository {

    private val completedIds =

        MutableStateFlow<Set<String>>(
            emptySet()
        )

    private val scriptMode =

        MutableStateFlow(
            ScriptMode.Gurmukhi
        )

    private val baniState =
        MutableStateFlow<List<Bani>>(emptyList())

    init {
        CoroutineScope(Dispatchers.Default).launch {
            val list = GeneratedBaniCatalog.banis
            baniState.value = list
        }
    }

    override fun observeHome():
        Flow<HomeSnapshot> {

        return combine(

            completedIds,
            baniState,
            scriptMode

        ) { completed, items, _ ->

            val safeItems =

                items.ifEmpty {
                    emptyList()
                }

            val progress =

                if (safeItems.isEmpty()) {

                    0f

                } else {

                    completed.size.toFloat() /
                        safeItems.size.toFloat()
                }

            HomeSnapshot(

                greeting =
                    "A peaceful start is waiting for you.",

                journey =

                    safeItems.map { bani ->

                        JourneyStep(

                            id =
                                "journey-${bani.id}",

                            baniId =
                                bani.id,

                            title =
                                bani.title,

                            time =
                                bani.time,

                            isCompleted =
                                bani.id in completed,

                            progress =

                                if (
                                    bani.id in completed
                                ) {

                                    1f

                                } else {

                                    0f
                                }
                        )
                    },

                streak = StreakSummary(

                    currentDays =

                        if (completed.isEmpty()) {
                            4
                        } else {
                            5 + completed.size
                        },

                    weeklyCompletedDays =
                        (4 + completed.size)
                            .coerceAtMost(7),

                    monthlyCompletedDays =
                        18 + completed.size,

                    freezesAvailable = 2,

                    compassionateMessage =
                        "Your journey continues today."
                ),

                continueTitle =

                    safeItems.firstOrNull {

                        it.id !in completed

                    }?.title
                        ?: "Kirtan Sohila",

                reminder =
                    "Take 5 peaceful minutes today.",

                reflection = Reflection(

                    baniId =
                        "japji-sahib",

                    title =
                        "Today’s Reflection",

                    insight =
                        "Let the first breath of the day become a quiet return to presence.",

                    takeaway =
                        "Read one pauri slowly, without rushing the meaning."
                )
            )
        }
    }

    override fun observeBanis():
        Flow<List<Bani>> {

        return baniState
    }

    override suspend fun markComplete(
        bani: Bani
    ) {

        completedIds.value =

            completedIds.value + bani.id
    }

    override suspend fun updateScriptMode(
        mode: ScriptMode
    ) {

        scriptMode.value = mode
    }
}