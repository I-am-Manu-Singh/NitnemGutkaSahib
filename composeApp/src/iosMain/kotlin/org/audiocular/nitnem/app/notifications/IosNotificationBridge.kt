package org.audiocular.nitnem.app.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private object IosNotificationBridge : NotificationBridge {
    override fun sendReminderNotification(title: String, message: String) {}
    override fun sendStreakNotification(streakDays: Int) {}
    override fun sendCompletionNotification(baniTitle: String) {}
}

@Composable
actual fun rememberNotificationBridge(): NotificationBridge =
    remember { IosNotificationBridge }
