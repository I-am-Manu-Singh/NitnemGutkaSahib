package org.audiocular.nitnem.app.widget

import android.content.Context
import android.content.Intent
import android.content.ComponentName
import android.appwidget.AppWidgetManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.audiocular.nitnem.widget.NitnemWidget
import org.audiocular.nitnem.widget.NitnemJourneyWidgetProvider

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
        completedBaniIds: List<String>,
        widgetTheme: String,
        widgetContent: String
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

            .putString(
                "widget_theme",
                widgetTheme
            )

            .putString(
                "widget_content",
                widgetContent
            )

            .commit()

        /*
         * UPDATE GLANCE WIDGET & BROADCAST
         */

        scope.launch {

            try {

                NitnemWidget()
                    .updateAll(context)

                // Send a direct system broadcast to the widget provider to force instant redraw
                val intent = Intent(context, NitnemJourneyWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val ids = appWidgetManager.getAppWidgetIds(
                        ComponentName(context, NitnemJourneyWidgetProvider::class.java)
                    )
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(intent)

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