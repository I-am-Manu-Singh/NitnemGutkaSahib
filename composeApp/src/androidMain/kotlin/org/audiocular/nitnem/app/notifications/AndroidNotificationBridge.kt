package org.audiocular.nitnem.app.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import org.audiocular.nitnem.MainActivity
import org.audiocular.nitnem.notifications.NitnemNotificationChannels

class AndroidNotificationBridge(
    private val context: Context
) : NotificationBridge {

    private val notificationManager =
        context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

    /*
     * Prevent spam updates
     */

    private var lastProgressBucket = -1

    override fun showToast(
        message: String
    ) {

        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun sendReminderNotification(

        title: String,

        message: String

    ) {

        val baniId = when {
            title.contains("Amrit Vela", ignoreCase = true) ||
                    title.contains("Japji", ignoreCase = true) ||
                    title.contains("Hukamnama", ignoreCase = true) ||
                    title.contains("Morning", ignoreCase = true) ||
                    title.contains("Reflection", ignoreCase = true) ||
                    title.contains("Midday", ignoreCase = true) -> "japji-sahib"

            title.contains("Rehras", ignoreCase = true) -> "rehras-sahib"

            title.contains("Sohila", ignoreCase = true) ||
                    title.contains("streak", ignoreCase = true) -> "kirtan-sohila"

            else -> null
        }

        val targetTab = when {
            title.contains("Reflection", ignoreCase = true) || title.contains("Simran", ignoreCase = true) -> "Practice"
            else -> null
        }

        val intent =

            Intent(

                context,

                MainActivity::class.java

            ).apply {

                flags =

                    Intent.FLAG_ACTIVITY_CLEAR_TOP or

                            Intent.FLAG_ACTIVITY_SINGLE_TOP

                baniId?.let {

                    putExtra(

                        "bani_id",

                        it

                    )

                }

                targetTab?.let {
                    putExtra("target_tab", it)
                }

            }

        val requestCode =

            baniId?.hashCode()

                ?: title.hashCode()

        val pendingIntent =

            PendingIntent.getActivity(

                context,

                requestCode,

                intent,

                PendingIntent.FLAG_UPDATE_CURRENT or

                        PendingIntent.FLAG_IMMUTABLE

            )

        val builder =

            NotificationCompat.Builder(

                context,

                NitnemNotificationChannels.REMINDERS

            )

                .setSmallIcon(

                    org.audiocular.nitnem.R.drawable.ic_notification

                )

                .setContentTitle(title)

                .setContentText(message)

                .setStyle(

                    NotificationCompat.BigTextStyle()

                        .bigText(message)

                )

                .setContentIntent(

                    pendingIntent

                )

                .setAutoCancel(true)

                .setCategory(

                    NotificationCompat.CATEGORY_REMINDER

                )

                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setFullScreenIntent(pendingIntent, true)
                .setColor(0xFFFFB300.toInt())
                .setLights(0xFFFFB300.toInt(), 1000, 1000)
                .setVibrate(longArrayOf(0, 250, 250, 250))
        /*
         * ACTIONS
         */

        if (baniId != null) {

            val readIntent =
                Intent(
                    context,
                    MainActivity::class.java
                ).apply {

                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP

                    putExtra(
                        "bani_id",
                        baniId
                    )

                    putExtra(
                        "action",
                        "READ"
                    )
                }

            val readPendingIntent =
                PendingIntent.getActivity(

                    context,

                    readIntent.hashCode(),

                    readIntent,

                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

            val listenIntent =
                Intent(
                    context,
                    MainActivity::class.java
                ).apply {

                    flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP

                    putExtra(
                        "bani_id",
                        baniId
                    )

                    putExtra(
                        "action",
                        "LISTEN"
                    )
                }

            val listenPendingIntent =
                PendingIntent.getActivity(

                    context,

                    listenIntent.hashCode(),

                    listenIntent,

                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

            builder.addAction(
                org.audiocular.nitnem.R.drawable.ic_notification,
                "Read Now",
                readPendingIntent
            )

            builder.addAction(
                org.audiocular.nitnem.R.drawable.ic_notification,
                "Listen",
                listenPendingIntent
            )
        }

        notificationManager.notify(
            1,
            builder.build()
        )

    }

    override fun sendStreakNotification(
        streakDays: Int
    ) {

        val intent =
            Intent(
                context,
                MainActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("target_tab", "Home")
            }

        val pendingIntent =
            PendingIntent.getActivity(

                context,

                101,

                intent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                NitnemNotificationChannels.REMINDERS
            )
                .setSmallIcon(
                    org.audiocular.nitnem.R.drawable.ic_notification
                )
                .setContentTitle(
                    "Spiritual Milestone 🌟"
                )
                .setContentText(
                    "You've maintained your Nitnem streak for $streakDays days."
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "You've maintained your Nitnem streak for $streakDays days."
                        )
                )
                .setCategory(
                    NotificationCompat.CATEGORY_STATUS
                )
                .setContentIntent(
                    pendingIntent
                )
                .setAutoCancel(true)
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setColor(0xFFFFB300.toInt())
                .setLights(0xFFFFB300.toInt(), 1000, 1000)
                .setVibrate(longArrayOf(0, 250, 250, 250))
                .build()
        notificationManager.notify(
            2,
            notification
        )
    }

    override fun sendCompletionNotification(
        baniTitle: String
    ) {

        val intent =
            Intent(
                context,
                MainActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("target_tab", "Home")
            }

        val pendingIntent =
            PendingIntent.getActivity(

                context,

                102,

                intent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                NitnemNotificationChannels.REMINDERS
            )
                .setSmallIcon(
                    org.audiocular.nitnem.R.drawable.ic_notification
                )
                .setContentTitle(
                    "Bani Completed 🙏"
                )
                .setContentText(
                    "You completed $baniTitle."
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "You completed $baniTitle."
                        )
                )
                .setCategory(
                    NotificationCompat.CATEGORY_STATUS
                )
                .setContentIntent(
                    pendingIntent
                )
                .setAutoCancel(true)
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setColor(0xFFFFB300.toInt())
                .setLights(0xFFFFB300.toInt(), 1000, 1000)
                .setVibrate(longArrayOf(0, 250, 250, 250))
                .build()

        notificationManager.notify(
            3,
            notification
        )
    }

    override fun showReadingProgressNotification(
        baniId: String,
        baniTitle: String,
        progress: Int
    ) {

        /*
         * AUTO DISMISS
         */

        if (progress >= 100) {

            dismissReadingProgressNotification()

            sendCompletionNotification(
                baniTitle = baniTitle
            )

            return
        }

        /*
         * THROTTLE UPDATES
         * Updates only every 5%
         */

        val currentBucket =
            progress / 5

        if (
            currentBucket ==
            lastProgressBucket
        ) {

            return
        }

        lastProgressBucket =
            currentBucket

        val intent =
            Intent(
                context,
                MainActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP

                putExtra(
                    "bani_id",
                    baniId
                )
            }

        val pendingIntent =
            PendingIntent.getActivity(

                context,

                104,

                intent,

                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                NitnemNotificationChannels.REMINDERS
            )
                .setSmallIcon(
                    org.audiocular.nitnem.R.drawable.ic_notification
                )
                .setContentTitle(
                    "Reading $baniTitle"
                )
                .setContentText(
                    "${progress.coerceIn(0, 100)}% completed"
                )
                .setProgress(
                    100,
                    progress,
                    false
                )
                .setContentIntent(
                    pendingIntent
                )
                .setOngoing(true)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setSilent(true)
                .setCategory(
                    NotificationCompat.CATEGORY_PROGRESS
                )
                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )
                .setColor(0xFFFFB300.toInt())
                .build()

        notificationManager.notify(
            1001,
            notification
        )
    }

    override fun dismissReadingProgressNotification() {

        lastProgressBucket = -1

        notificationManager.cancel(
            1001
        )
    }

    companion object {

        const val REMINDER_ID = 1

        const val STREAK_ID = 2

        const val COMPLETION_ID = 3

        const val READING_PROGRESS_ID = 1001

    }
}

@Composable
actual fun rememberNotificationBridge(): NotificationBridge {

    val context = LocalContext.current

    return remember(context) {
        AndroidNotificationBridge(context)
    }
}
