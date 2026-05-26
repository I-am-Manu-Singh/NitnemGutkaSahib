package org.audiocular.nitnem.app.audio

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import org.audiocular.nitnem.MainActivity
import org.audiocular.nitnem.R
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.CommandButton
import com.google.common.collect.ImmutableList

object PlaybackActions {
    const val PLAY = "org.audiocular.nitnem.PLAY"
    const val PAUSE = "org.audiocular.nitnem.PAUSE"
}

class AudioNotificationManager(

    private val context: Context
) : MediaNotification.Provider {

    override fun createNotification(
        session: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationRequiredCallback: MediaNotification.Provider.Callback
    ): MediaNotification {
        val playing = session.player.isPlaying
        val title = session.player.currentMediaItem?.mediaMetadata?.title?.toString() ?: "Nitnem"
        val notification = build(title, playing)
        return MediaNotification(NOTIFICATION_ID, notification)
    }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: android.os.Bundle
    ): Boolean {
        return false
    }

    companion object {

        const val CHANNEL_ID =
            "nitnem_audio_playback"

        const val NOTIFICATION_ID =
            1001
    }

    fun build(

        title: String,

        playing: Boolean
    ): Notification {

        val openIntent =

            PendingIntent.getActivity(

                context,

                0,

                Intent(
                    context,
                    MainActivity::class.java
                ),

                PendingIntent.FLAG_IMMUTABLE
            )

        val playPauseIntent =

            PendingIntent.getService(

                context,

                1,

                Intent(

                    context,

                    NitnemAudioService::class.java

                ).apply {

                    action =

                        if (playing)
                            PlaybackActions.PAUSE
                        else
                            PlaybackActions.PLAY
                },

                PendingIntent.FLAG_IMMUTABLE
            )

        return NotificationCompat.Builder(

            context,
            CHANNEL_ID

        )

            .setSmallIcon(
                R.drawable.ic_notification
            )

            .setContentTitle(title)

            .setContentText(
                "Nitnem Playback"
            )

            .setContentIntent(
                openIntent
            )

            .setOngoing(
                playing
            )

            .addAction(

                if (playing)
                    android.R.drawable.ic_media_pause
                else
                    android.R.drawable.ic_media_play,

                if (playing)
                    "Pause"
                else
                    "Play",

                playPauseIntent
            )

            .setOnlyAlertOnce(true)

            .build()
    }
}

