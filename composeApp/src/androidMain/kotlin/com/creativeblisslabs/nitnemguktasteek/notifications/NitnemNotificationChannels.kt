package com.creativeblisslabs.nitnemguktasteek.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.creativeblisslabs.nitnemguktasteek.R

object NitnemNotificationChannels {
    const val REMINDERS = "nitnem_gentle_reminders"
    const val PLAYBACK = "nitnem_audio_playback"
    const val STREAK = "nitnem_streak_reminders"
    const val HUKAMNAMA = "nitnem_daily_hukamnama"

    const val GROUP_PRAYERS = "group_prayers"
    const val GROUP_MEDIA = "group_media"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel Groups (Item 067)
        val prayerGroup = android.app.NotificationChannelGroup(
            GROUP_PRAYERS,
            context.getString(R.string.notification_group_prayers)
        )
        val mediaGroup = android.app.NotificationChannelGroup(
            GROUP_MEDIA,
            context.getString(R.string.notification_group_media)
        )
        notificationManager.createNotificationChannelGroups(listOf(prayerGroup, mediaGroup))

        val reminderChannel = NotificationChannel(
            REMINDERS,
            context.getString(R.string.notification_channel_reminders),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            group = GROUP_PRAYERS
            description = context.getString(R.string.notification_channel_reminders_description)
            setShowBadge(true)
            enableVibration(true)
        }

        val streakChannel = NotificationChannel(
            STREAK,
            context.getString(R.string.notification_channel_streak),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            group = GROUP_PRAYERS
            description = context.getString(R.string.notification_channel_streak_description)
            setShowBadge(false)
        }

        val hukamnamaChannel = NotificationChannel(
            HUKAMNAMA,
            context.getString(R.string.notification_channel_hukamnama),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            group = GROUP_PRAYERS
            description = context.getString(R.string.notification_channel_hukamnama_description)
            setShowBadge(false)
        }

        val playbackChannel = NotificationChannel(
            PLAYBACK,
            context.getString(R.string.notification_channel_playback),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            group = GROUP_MEDIA
            description = context.getString(R.string.notification_channel_playback_description)
            setShowBadge(false)
        }

        notificationManager.createNotificationChannels(
            listOf(reminderChannel, streakChannel, hukamnamaChannel, playbackChannel)
        )
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
