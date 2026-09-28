package com.creativeblisslabs.nitnemguktasteek.app.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object PrayerReminderScheduler {

    fun schedule(
        context: Context
    ) {

        val request =

            PeriodicWorkRequestBuilder<
                PrayerReminderWorker
            >(
                1,
                TimeUnit.HOURS
            ).build()

        WorkManager
            .getInstance(context)

            .enqueueUniquePeriodicWork(

                "nitnem_prayer_reminders",

                ExistingPeriodicWorkPolicy.UPDATE,

                request
            )
    }

    /*
     * COMPATIBILITY WRAPPER
     *
     * Because onboarding expects this.
     */

    fun schedulePrayerReminders(

        context: Context,

        amritTime: String,

        rehrasTime: String,

        sohilaTime: String
    ) {

    
        schedule(context)
    }

    fun scheduleSnoozeReminder(
        context: Context,
        baniId: String? = null
    ) {
        val inputData = androidx.work.workDataOf(
            "is_snooze" to true,
            "snooze_bani_id" to (baniId ?: "")
        )

        val snoozeRequest = androidx.work.OneTimeWorkRequestBuilder<PrayerReminderWorker>()
            .setInitialDelay(15, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "nitnem_snooze_reminder",
            androidx.work.ExistingWorkPolicy.REPLACE,
            snoozeRequest
        )
    }
}