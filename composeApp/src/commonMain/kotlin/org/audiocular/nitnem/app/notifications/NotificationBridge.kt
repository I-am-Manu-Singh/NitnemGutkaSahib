package org.audiocular.nitnem.app.notifications

import androidx.compose.runtime.Composable

interface NotificationBridge {

    fun sendReminderNotification(
        title: String,
        message: String
    )

    fun sendStreakNotification(
        streakDays: Int
    )

    fun sendCompletionNotification(
        baniTitle: String
    )

    fun showToast(
        message: String
    )

    fun showReadingProgressNotification(
        baniId: String,
        baniTitle: String,
        progress: Int
    )

    fun dismissReadingProgressNotification()
}

@Composable
expect fun rememberNotificationBridge(): NotificationBridge