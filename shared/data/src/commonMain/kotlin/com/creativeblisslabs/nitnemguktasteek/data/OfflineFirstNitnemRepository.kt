package com.creativeblisslabs.nitnemguktasteek.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.creativeblisslabs.nitnemguktasteek.data.generated.GeneratedBaniCatalog
import com.creativeblisslabs.nitnemguktasteek.domain.NitnemRepository
import com.creativeblisslabs.nitnemguktasteek.models.Bani
import com.creativeblisslabs.nitnemguktasteek.models.HomeSnapshot
import com.creativeblisslabs.nitnemguktasteek.models.JourneyStep
import com.creativeblisslabs.nitnemguktasteek.models.Reflection
import com.creativeblisslabs.nitnemguktasteek.models.ScriptMode
import com.creativeblisslabs.nitnemguktasteek.models.StreakSummary

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

                streak = run {
                    val policy = com.creativeblisslabs.nitnemguktasteek.streak.StreakPolicy()
                    var currentStreak = StreakSummary(
                        freezesAvailable = 2,
                        currentDays = 0,
                        weeklyCompletedDays = 0,
                        monthlyCompletedDays = 0,
                        compassionateMessage = "Begin gently again. Nothing sacred is lost."
                    )
                    for (i in 0 until completed.size) {
                        currentStreak = policy.completeDay(currentStreak)
                    }
                    currentStreak
                },

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