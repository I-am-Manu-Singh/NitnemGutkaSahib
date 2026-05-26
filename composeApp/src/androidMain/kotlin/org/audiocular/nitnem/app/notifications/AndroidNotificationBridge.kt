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

            title.contains(
                "Amrit Vela",
                ignoreCase = true
            ) ||

            title.contains(
                "Japji",
                ignoreCase = true
            ) -> {

                "japji-sahib"
            }

            title.contains(
                "Rehras",
                ignoreCase = true
            ) -> {

                "rehras-sahib"
            }

            title.contains(
                "Sohila",
                ignoreCase = true
            ) -> {

                "kirtan-sohila"
            }

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

                if (baniId != null) {

                    putExtra(
                        "bani_id",
                        baniId
                    )
                }
            }

        val pendingIntent =
            PendingIntent.getActivity(

                context,

                100,

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
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )

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
                .setContentIntent(
                    pendingIntent
                )
                .setAutoCancel(true)
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
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
                .setContentIntent(
                    pendingIntent
                )
                .setAutoCancel(true)
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
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
                    "$progress% completed"
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

                /*
                 * IMPORTANT
                 */

                .setOnlyAlertOnce(true)
                .setSilent(true)

                /*
                 * LOW PRIORITY
                 */

                .setPriority(
                    NotificationCompat.PRIORITY_LOW
                )

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
}

@Composable
actual fun rememberNotificationBridge(): NotificationBridge {

    val context = LocalContext.current

    return remember(context) {
        AndroidNotificationBridge(context)
    }
}