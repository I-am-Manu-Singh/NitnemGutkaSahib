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

        /*
         * You can later use these times
         * inside Worker / AlarmManager.
         *
         * Right now compile stability first.
         */

        schedule(context)
    }
}