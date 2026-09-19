package com.creativeblisslabs.nitnemguktasteek.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.creativeblisslabs.nitnemguktasteek.R

object NitnemNotificationChannels {
    const val REMINDERS = "nitnem_gentle_reminders"
    const val PLAYBACK = "nitnem_audio_playback"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val reminderChannel = NotificationChannel(
            REMINDERS,
            context.getString(R.string.notification_channel_reminders),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_reminders_description)
            setShowBadge(false)
        }

        val playbackChannel = NotificationChannel(
            PLAYBACK,
            context.getString(R.string.notification_channel_playback),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.notification_channel_playback_description)
            setShowBadge(false)
        }

        notificationManager.createNotificationChannels(listOf(reminderChannel, playbackChannel))
    }
    fun createAudioChannel(
    context: Context
) {

    if (
        Build.VERSION.SDK_INT >=
        Build.VERSION_CODES.O
    ) {

        val channel =
            NotificationChannel(

                "nitnem_audio",

                "Nitnem Audio",

                NotificationManager.IMPORTANCE_LOW
            ).apply {

                description =
                    "Background Gurbani playback"
            }

        val manager =
            context.getSystemService(
                NotificationManager::class.java
            )

        manager.createNotificationChannel(
            channel
        )
    }
}
}
