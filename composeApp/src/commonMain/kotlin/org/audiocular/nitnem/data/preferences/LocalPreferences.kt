package org.audiocular.nitnem.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.localDataStore by
    preferencesDataStore(
        name = "nitnem_local"
    )

class LocalPreferences(
    private val context: Context
) {

    companion object {

        /*
         * DAILY COMPLETION
         */

        private val COMPLETED_BANIS =
            stringSetPreferencesKey(
                "completed_banis"
            )

        private val LAST_DATE =
            stringPreferencesKey(
                "last_date"
            )

        /*
         * REMINDERS
         */

        private const val REMINDER_PREFIX =
            "reminder_"

        /*
         * WIDGET
         */

        private val WIDGET_COMPLETED =
            intPreferencesKey(
                "widget_completed"
            )

        private val WIDGET_TOTAL =
            intPreferencesKey(
                "widget_total"
            )
    }

    /*
     * COMPLETED BANIS
     */

    suspend fun getCompletedBanisToday():
        Set<String> {

        clearIfNewDay()

        return context
            .localDataStore
            .data
            .map {

                it[COMPLETED_BANIS]
                    ?: emptySet()
            }
            .first()
    }

    suspend fun markBaniCompleted(
        baniId: String
    ) {

        clearIfNewDay()

        val current =
            getCompletedBanisToday()
                .toMutableSet()

        current.add(baniId)

        context.localDataStore.edit {

            it[COMPLETED_BANIS] =
                current

            /*
             * UPDATE WIDGET
             */

            it[WIDGET_COMPLETED] =
                current.size

            it[WIDGET_TOTAL] = 7
        }
    }

    /*
     * REMINDERS
     */

    suspend fun wasReminderSent(
        key: String
    ): Boolean {

        clearIfNewDay()

        val reminderKey =
            booleanPreferencesKey(
                "$REMINDER_PREFIX$key"
            )

        return context
            .localDataStore
            .data
            .map {

                it[reminderKey]
                    ?: false
            }
            .first()
    }

    suspend fun markReminderSent(
        key: String
    ) {

        val reminderKey =
            booleanPreferencesKey(
                "$REMINDER_PREFIX$key"
            )

        context.localDataStore.edit {

            it[reminderKey] = true
        }
    }

    /*
     * WIDGET
     */

    val widgetCompletedFlow:
        Flow<Int> =

        context.localDataStore.data.map {

            it[WIDGET_COMPLETED]
                ?: 0
        }

    val widgetTotalFlow:
        Flow<Int> =

        context.localDataStore.data.map {

            it[WIDGET_TOTAL]
                ?: 7
        }

    /*
     * RESET EACH DAY
     */

    private suspend fun clearIfNewDay() {

        val today =
            LocalDate.now()
                .toString()

        val prefs =
            context.localDataStore
                .data
                .first()

        val savedDate =
            prefs[LAST_DATE]

        if (savedDate == today) {
            return
        }

        context.localDataStore.edit {

            /*
             * RESET COMPLETION
             */

            it[COMPLETED_BANIS] =
                emptySet()

            /*
             * RESET WIDGET
             */

            it[WIDGET_COMPLETED] = 0

            it[WIDGET_TOTAL] = 7

            /*
             * SAVE DATE
             */

            it[LAST_DATE] = today

            /*
             * REMOVE REMINDERS
             */

            val reminderKeys =

                it.asMap()
                    .keys
                    .filterIsInstance<
                        Preferences.Key<Boolean>
                    >()
                    .filter { key ->

                        key.name.startsWith(
                            REMINDER_PREFIX
                        )
                    }

            reminderKeys.forEach { key ->

                it.remove(key)
            }
        }
    }
}