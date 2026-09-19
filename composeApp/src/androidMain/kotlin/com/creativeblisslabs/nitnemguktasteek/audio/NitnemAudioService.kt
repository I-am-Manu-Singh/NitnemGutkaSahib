package com.creativeblisslabs.nitnemguktasteek.app.audio

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.DefaultMediaNotificationProvider
import com.creativeblisslabs.nitnemguktasteek.audio.AndroidStreamAudioController

class NitnemAudioService :
    MediaSessionService() {

    private var mediaSession:
        MediaSession? = null

    override fun onCreate() {
        println("NitnemAudioService: onCreate started")
        try {
            super.onCreate()

            val controller = AndroidStreamAudioController.getInstance(this)
            println("NitnemAudioService: controller resolved")

            val openIntent = Intent(this, com.creativeblisslabs.nitnemguktasteek.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val openPendingIntent = PendingIntent.getActivity(
                this,
                0,
                openIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            mediaSession = MediaSession.Builder(this, controller.player)
                .setSessionActivity(openPendingIntent)
                .build()
            println("NitnemAudioService: MediaSession created")
            addSession(mediaSession!!)
            println("NitnemAudioService: MediaSession added to service")

            val notificationProvider = DefaultMediaNotificationProvider.Builder(this)
                .setChannelId("nitnem_audio_playback")
        
                .setChannelName(com.creativeblisslabs.nitnemguktasteek.R.string.notification_channel_playback)
                .build()
            notificationProvider.setSmallIcon(com.creativeblisslabs.nitnemguktasteek.R.drawable.ic_notification)

            setMediaNotificationProvider(notificationProvider)
            println("NitnemAudioService: Notification provider set with small icon")
        } catch (e: Exception) {
            println("NitnemAudioService: Exception in onCreate:")
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("NitnemAudioService: onStartCommand called action=${intent?.action}")
        intent?.action?.let { action ->
            val controller = AndroidStreamAudioController.getInstance(this)
            when (action) {
                PlaybackActions.PLAY -> controller.resume()
                PlaybackActions.PAUSE -> controller.pause()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(
        controllerInfo:
            MediaSession.ControllerInfo
    ): MediaSession? {

        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player != null && player.isPlaying) {
            return
        }
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {

        mediaSession?.release()

        mediaSession = null

        super.onDestroy()
    }
}