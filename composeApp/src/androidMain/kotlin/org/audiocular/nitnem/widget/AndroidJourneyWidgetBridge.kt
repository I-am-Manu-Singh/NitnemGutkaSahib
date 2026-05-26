package org.audiocular.nitnem.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.audiocular.nitnem.widget.NitnemWidget
private class AndroidJourneyWidgetBridge(
    private val context: Context
) : JourneyWidgetBridge {

    /*
     * SAFE BACKGROUND SCOPE
     */

    private val scope =
        CoroutineScope(
            Dispatchers.IO +
                SupervisorJob()
        )

    override fun update(
        completed: Int,
        total: Int,
        currentTitle: String,
        streakDays: Int,
        weeklyCompleted: Int,
        monthlyConsistencyDays: Int,
        completedBaniIds: List<String>
    ) {

        val safeTotal =
            total.coerceAtLeast(1)

        /*
         * SAVE WIDGET STATE
         */

        context.getSharedPreferences(
            "nitnem_widget_state",
            Context.MODE_PRIVATE
        ).edit()

            .putInt(
                "completed",
                completed
            )

            .putInt(
                "total",
                safeTotal
            )

            .putString(
                "current_title",
                currentTitle
            )

            .putInt(
                "streak_days",
                streakDays
            )

            .putInt(
                "weekly_completed",
                weeklyCompleted
            )

            .putInt(
                "monthly_consistency_days",
                monthlyConsistencyDays
            )

            .putStringSet(
                "completed_bani_ids",
                completedBaniIds.toSet()
            )

            .apply()

        /*
         * UPDATE GLANCE WIDGET
         */

        scope.launch {

            try {

                NitnemWidget()
                    .updateAll(context)

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}

@Composable
actual fun rememberJourneyWidgetBridge():
    JourneyWidgetBridge {

    val context =
        LocalContext.current.applicationContext

    return remember {

        AndroidJourneyWidgetBridge(
            context
        )
    }
}