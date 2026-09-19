package com.creativeblisslabs.nitnemguktasteek.app.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import com.creativeblisslabs.nitnemguktasteek.data.HukamnamaProvider
import com.creativeblisslabs.nitnemguktasteek.data.preferences.LocalPreferences
import com.creativeblisslabs.nitnemguktasteek.data.preferences.SettingsRepository
import java.util.Calendar

class PrayerReminderWorker(

    context: Context,

    params: WorkerParameters

) : CoroutineWorker(
    context,
    params
) {

    override suspend fun doWork(): Result {

        return try {

            val prefs =
                LocalPreferences(
                    applicationContext
                )

            val completedToday =
                prefs.getCompletedBanisToday()

            val hour =
                Calendar.getInstance()
                    .get(Calendar.HOUR_OF_DAY)

            val bridge =
                AndroidNotificationBridge(
                    applicationContext
                )

            /*
             * MORNING
             */

            if (

                hour in 4..10 &&

                !completedToday.contains(
                    "japji-sahib"
                ) &&

                !prefs.wasReminderSent(
                    "morning_japji"
                )
            ) {

                val hukamnama =
                    HukamnamaProvider
                        .getDailyHukamnama(applicationContext)

                val textLines = hukamnama.lines
                    .take(5)
                    .map { it.gurmukhi }
                    .filter { it.isNotBlank() }
                    .joinToString("\n")
                val displayMsg = if (textLines.isNotBlank()) textLines else "Start your morning with Gurbani."

                bridge.sendReminderNotification(
                    "Daily Hukamnama 🌅",
                    displayMsg
                )

                prefs.markReminderSent(
                    "morning_japji"
                )
            }

            /*
             * MIDDAY
             */

            else if (

                hour in 12..15 &&

                completedToday.size < 3 &&

                !prefs.wasReminderSent(
                    "midday_simran"
                )
            ) {

                bridge.sendReminderNotification(

                    "Midday Reflection ✨",

                    "Pause for a few peaceful moments with Gurbani."
                )

                prefs.markReminderSent(
                    "midday_simran"
                )
            }

            /*
             * EVENING
             */

            else if (

                hour in 17..20 &&

                !completedToday.contains(
                    "rehras-sahib"
                ) &&

                !prefs.wasReminderSent(
                    "rehras_evening"
                )
            ) {

                bridge.sendReminderNotification(

                    "Rehras Sahib 🌇",

                    "Recharge spiritually after the day's journey."
                )

                prefs.markReminderSent(
                    "rehras_evening"
                )
            }

            /*
             * NIGHT
             */

            else if (

                hour in 21..23 &&

                !completedToday.contains(
                    "kirtan-sohila"
                ) &&

                !prefs.wasReminderSent(
                    "night_sohila"
                )
            ) {

                bridge.sendReminderNotification(

                    "Kirtan Sohila 🌙",

                    "Prepare the mind for peaceful rest before sleep."
                )

                prefs.markReminderSent(
                    "night_sohila"
                )
            }

            /*
             * STREAK SAVER
             */

            if (

                hour >= 20 &&

                completedToday.isEmpty() &&

                !prefs.wasReminderSent(
                    "streak_saver"
                )
            ) {

                val settings =
                    SettingsRepository(
                        applicationContext
                    )

                val streak =
                    settings
                        .preferencesFlow
                        .first()
                        .streakDays

                if (streak > 0) {

                    bridge.sendReminderNotification(

                        "Save your streak! 🔥",

                        "Keep your $streak-day streak alive. Take a few minutes to read Kirtan Sohila."
                    )

                    prefs.markReminderSent(
                        "streak_saver"
                    )
                }
            }

            Result.success()

        } catch (e: Exception) {

            e.printStackTrace()

            Result.failure()
        }
    }
}