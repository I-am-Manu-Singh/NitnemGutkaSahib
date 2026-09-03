package org.audiocular.nitnem.audio

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.audiocular.nitnem.models.PlaybackSnapshot
import org.audiocular.nitnem.models.AudioTrack
import org.audiocular.nitnem.data.generated.GeneratedBaniCatalog
import org.audiocular.nitnem.data.CuratedAudioCatalog
import android.media.MediaMetadataRetriever
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import org.audiocular.nitnem.app.audio.AudioControllerHolder
import org.audiocular.nitnem.app.audio.NitnemAudioService
import org.audiocular.nitnem.utils.timingKey
import androidx.core.net.toUri

@UnstableApi
class AndroidStreamAudioController private constructor(
    private val context: Context
) : StreamAudioController {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AndroidStreamAudioController? = null

        private var simpleCache: SimpleCache? = null

        @OptIn(UnstableApi::class)
        @Synchronized
        private fun getCache(context: Context): SimpleCache {
            if (simpleCache == null) {
                val cacheDir = File(context.cacheDir, "audio_cache")
                val evictor = LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024L) // 100 MB cache
                val databaseProvider = StandaloneDatabaseProvider(context)
                simpleCache = SimpleCache(cacheDir, evictor, databaseProvider)
            }
            return simpleCache!!
        }

        fun getInstance(context: Context): AndroidStreamAudioController {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AndroidStreamAudioController(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
private val activeTrackState =
    MutableStateFlow<Map<String, String>>(
        emptyMap()
    )
    /*
     * PLAYER
     */

    private val httpDataSourceFactory =
        DefaultHttpDataSource.Factory()
            .setUserAgent(
                // Browser-like UA — Archive.org CDN responds faster to known clients
                "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/100.0.4896.58 Mobile Safari/537.36"
            )
            // Archive.org CDN can take 15-20s on a cold hit; 8s default causes silent failures
            .setConnectTimeoutMs(20_000)
            .setReadTimeoutMs(20_000)
            .setAllowCrossProtocolRedirects(true)
            // Disable gzip so range-request streaming works correctly
            .setDefaultRequestProperties(mapOf("Accept-Encoding" to "identity"))

    private val dataSourceFactory =
        DefaultDataSource.Factory(
            context,
            httpDataSourceFactory
        )

    private val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(getCache(context))
        .setUpstreamDataSourceFactory(dataSourceFactory)
        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    internal val player =
        ExoPlayer.Builder(context)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        15_000,
                        50_000,
                        1_500,
                        2_500
                    )
                    .build()
            )
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(
                    cacheDataSourceFactory
                )
            )
            .build()
            .apply {

                /*
                 * AUDIO ATTRIBUTES
                 */

                setAudioAttributes(

                    AudioAttributes.Builder()

                        .setUsage(C.USAGE_MEDIA)

                        .setContentType(
                            C.AUDIO_CONTENT_TYPE_MUSIC
                        )

                        .build(),

                    true
                )

                /*
                 * IMPORTANT
                 */

                setHandleAudioBecomingNoisy(true)

                playWhenReady = false
            }
    /*
     * STORAGE
     */

    private val audioDir =
        File(
            context.filesDir,
            "audio"
        ).apply {

            if (!exists()) {
                mkdirs()
            }
        }

    /*
     * PLAYBACK
     */

    private val _playback =
        MutableStateFlow(
            PlaybackSnapshot()
        )

    override val playback:
        StateFlow<PlaybackSnapshot> =
        _playback.asStateFlow()

    /*
     * COROUTINES
     */

    private val scope =
        CoroutineScope(
            Dispatchers.Main +
                SupervisorJob()
        )

    private var progressJob: Job? =
        null

    /*
     * STATE
     */

    private var isSimulated = false
    private var simulatedPositionMs = 0L
    private var simulatedDurationMs = 15 * 60 * 1000L
    private var simulatedIsPlaying = false
    private var simulatedSpeed = 1f
    private var currentTitle = "Nitnem"
    private var prepareGeneration = 0

    private var prepareTimeoutJob: Job? = null
    private var currentUrl:
        String? = null

    private fun updateSimulatedState() {
        _playback.update {
            it.copy(
                currentTitle = currentTitle,
                currentArtist = "Simulated Track",
                isPlaying = simulatedIsPlaying,
                positionMs = simulatedPositionMs,
                durationMs = simulatedDurationMs,
                speed = simulatedSpeed,
                isReady = true,
                error = null,
                isEnded = simulatedPositionMs >= simulatedDurationMs,
                isLoading = false
            )
        }
    }

    private var lastSaveTime = 0L

    init {

        AudioControllerHolder.controller =
            this

        player.addListener(

            object : Player.Listener {

                override fun onIsPlayingChanged(
                    isPlaying: Boolean
                ) {

                    println(

                        "PLAYER STATE -> " +

                                "isPlaying=$isPlaying, " +

                                "currentUrl=$currentUrl"

                    )

                    if (isPlaying) {

                        startPollingProgress()

                        startBackgroundPlayback()

                    } else {

                        stopPollingProgress()
                    }

                    updateState()
                }

                override fun onPlaybackStateChanged(
                    playbackState: Int
                ) {

                    when (playbackState) {

                        Player.STATE_IDLE -> {

                            println(
                                "EXO STATE -> IDLE"
                            )
                        }

                        Player.STATE_BUFFERING -> {

                            println(
                                "EXO STATE -> BUFFERING"
                            )
                        }

                        Player.STATE_READY -> {

                            prepareTimeoutJob?.cancel()

                            println("EXO STATE -> READY")

                            println(
                                "READY INFO -> " +
                                        "playWhenReady=${player.playWhenReady}, " +
                                        "isPlaying=${player.isPlaying}, " +
                                        "duration=${player.duration}"
                            )

                            if (
                                player.playWhenReady &&
                                !player.isPlaying
                            ) {

                                println("FORCING PLAY")

                                player.play()
                            }
                        }

                        Player.STATE_ENDED -> {

                            val duration =
                                player.duration

                            val position =
                                player.currentPosition

                            val actuallyEnded =
                                duration > 0 &&
                                        position >= duration - 500

                            if (!actuallyEnded) {

                                println(
                                    "IGNORING TRANSIENT ENDED STATE"
                                )

                                return
                            }

                            println(
                                "EXO STATE -> ENDED"
                            )

                            stopPollingProgress()

                            prepareTimeoutJob?.cancel()
                        }
                    }

                    updateState()
                }

                override fun onPlayerError(
                    error: PlaybackException
                ) {

                    println(
                        "PLAYER ERROR -> ${error.message}"
                    )

                    error.printStackTrace()
                    prepareTimeoutJob?.cancel()
                    stopPollingProgress()

                    /*
                     * HARD RESET
                     */

                    try {
                        prepareTimeoutJob?.cancel()

                        player.clearMediaItems()

                    } catch (e: Exception) {

                        e.printStackTrace()
                    }

                    _playback.update {

                        it.copy(
                            error =
                                error.localizedMessage
                                    ?: "Playback failed",
                            isLoading = false,
                            isPlaying = false,
                            isReady = false
                        )
                    }
                }
                override fun onMediaItemTransition(
                    mediaItem: MediaItem?,
                    reason: Int
                ) {
                    println(
                        "MEDIA ITEM TRANSITION -> " +
                                mediaItem?.mediaMetadata?.title
                    )
                }
            }
        )

        /*
         * RESTORE LAST PLAYBACK
         */

        scope.launch {

            delay(300)

            try {

                restorePlaybackState()

            } catch (e: Exception) {

                e.printStackTrace()

                currentUrl = null

                _playback.update {

                    it.copy(
                        isLoading = false,
                        isPlaying = false,
                        isReady = false
                    )
                }
            }
        }
    }

    /*
     * POLLING
     */

    private fun startPollingProgress() {

        progressJob?.cancel()

        progressJob = scope.launch {
            var lastTime = System.currentTimeMillis()
            while (isActive) {
                val now = System.currentTimeMillis()
                val delta = now - lastTime
                lastTime = now

                if (isSimulated) {
                    if (simulatedIsPlaying) {
                        simulatedPositionMs += (delta * simulatedSpeed).toLong()
                        if (simulatedPositionMs >= simulatedDurationMs) {
                            simulatedPositionMs = simulatedDurationMs
                            simulatedIsPlaying = false
                        }
                        updateSimulatedState()
                    }
                } else {
                    updateState()
                }

                if (now - lastSaveTime >= 2000L) {
                    lastSaveTime = now
                    savePlaybackState()
                }

                delay(100)
            }
        }
    }

    private fun stopPollingProgress() {

        progressJob?.cancel()

        progressJob = null
    }

    /*
 * UPDATE UI STATE
 */
    private fun updateState() {
        println(
            "STATE UPDATE -> " +
                    "state=${player.playbackState}, " +
                    "isPlaying=${player.isPlaying}, " +
                    "playWhenReady=${player.playWhenReady}"
        )
        val playbackState =
            player.playbackState

        val duration =
            if (player.duration == C.TIME_UNSET) {
                0L
            } else {
                player.duration
            }

        val isLoading =
            playbackState == Player.STATE_BUFFERING

        val isReady =
            playbackState == Player.STATE_READY

        val isEnded =
            playbackState == Player.STATE_ENDED

        _playback.update {

            it.copy(
                isPlaying = player.isPlaying,

                positionMs =
                    player.currentPosition,

                durationMs =
                    duration,

                speed =
                    player.playbackParameters.speed,

                isReady =
                    isReady,

                isLoading =
                    isLoading,

                isEnded =
                    isEnded,

                error = null,
                isMuted = player.volume == 0f
            )
        }

        println(
            "UI PLAYBACK -> " +
                    "loading=$isLoading, " +
                    "ready=$isReady"
        )
    }
/*
 * URI
 */
private fun createUri(
    url: String
): Uri {

    return when {

        /*
         * ASSET AUDIO
         */

        url.startsWith("asset://") -> {

            val assetPath =
                url.removePrefix("asset://")

            "asset:///$assetPath".toUri()
        }

        /*
         * REMOTE URL
         */

        url.startsWith("http://") ||
                url.startsWith("https://") -> {

            Uri.parse(url)
        }

        /*
         * LOCAL FILE
         */

        url.startsWith("/") -> {

            Uri.fromFile(
                File(url)
            )
        }

        /*
         * CONTENT URI
         */

        url.startsWith("content://") -> {

            Uri.parse(url)
        }

        /*
         * FALLBACK
         */

        else -> {

            Uri.parse(url)
        }
    }
}
    private fun prepareInternal(
        url: String,
        title: String,
        initialPositionMs: Long,
        playWhenReady: Boolean
    ) {

        if (url.isBlank()) return

        val generation = ++prepareGeneration
        println("PREPARE START -> $title")
        println("PREPARE URL -> $url")
        println("GENERATION -> $generation")

        prepareTimeoutJob?.cancel()

        isSimulated = false

        currentUrl = url

        currentTitle = title

        scope.launch {

            try {

                _playback.update {

                    it.copy(
                        currentTitle = title,
                        isLoading = true,
                        isReady = false,
                        isEnded = false,
                        error = null
                    )
                }

                val finalUri =

                    if (url.startsWith("raw/")) {

                        val rawName =
                            url.removePrefix("raw/")

                        val resId =
                            context.resources.getIdentifier(
                                rawName,
                                "raw",
                                context.packageName
                            )

                        println(
                            "RAW LOOKUP -> " +
                                    "name=$rawName, " +
                                    "resId=$resId"
                        )

                        if (resId == 0) {

                            throw IllegalArgumentException(
                                "Raw resource not found: $rawName"
                            )
                        }

                        RawResourceDataSource
                            .buildRawResourceUri(resId)

                    } else {

                        createUri(url)
                    }

                println("FINAL URI -> $finalUri")

                val mediaItem =

                    MediaItem.Builder()
                        .setUri(finalUri)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(title)
                                .build()
                        )
                        .build()
                prepareTimeoutJob?.cancel()

                println("SETTING MEDIA ITEM")
                player.stop()

                player.clearMediaItems()
                player.setMediaItem(
                    mediaItem,
                    initialPositionMs
                )

                println("SETTING PLAYWHENREADY -> $playWhenReady")

                player.playWhenReady = playWhenReady

                println("CALLING PREPARE")

                player.prepare()

                if (!url.startsWith("raw/")) {

                    prepareTimeoutJob = scope.launch {

                        delay(10_000L)

                        if (

                            generation == prepareGeneration &&

                            player.playbackState != Player.STATE_READY &&

                            player.playbackState != Player.STATE_ENDED

                        ) {

                            player.clearMediaItems()

                            _playback.update {

                                it.copy(

                                    isLoading = false,

                                    isReady = false,

                                    isPlaying = false,

                                    error = "Audio failed to load"

                                )

                            }

                        }

                    }

                }


            } catch (e: Exception) {

                e.printStackTrace()

                _playback.update {

                    it.copy(
                        isLoading = false,
                        isPlaying = false,
                        isReady = false,
                        error = e.message
                    )
                }
            }
        }
    }
    override fun prepare(

        url: String,

        title: String,

        initialPositionMs: Long

    ) {

        println("PREPARE API CALLED")

        prepareInternal(url, title, initialPositionMs, false)

    }
    /*
     * PLAY
     */
    override fun play(
        url: String,
        title: String
    ) {

        println("PLAY API CALLED")

        println(
            "PLAY CHECK -> " +
                    "currentUrl=$currentUrl, " +
                    "requestedUrl=$url, " +
                    "isPlaying=${player.isPlaying}, " +
                    "state=${player.playbackState}"
        )

        /*
         * SAME TRACK
         */
        if (currentUrl == url) {

            println("PLAY SKIPPED SAME URL -> $url")

            if (!player.isPlaying) {

                player.play()
            }

            return
        }

        println(
            "PREPARING NEW TRACK -> $url"
        )

        prepareInternal(
            url = url,
            title = title,
            initialPositionMs = 0L,
            playWhenReady = true
        )
    }

    override fun pause() {
        if (isSimulated) {
            simulatedIsPlaying = false
            stopPollingProgress()
            updateSimulatedState()
            return
        }

        player.pause()

        savePlaybackState()

        updateState()
    }

    override fun resume() {
        println("RESUME API CALLED")
        if (isSimulated) {
            simulatedIsPlaying = true
            startPollingProgress()
            updateSimulatedState()
            return
        }

        player.play()

        startBackgroundPlayback()

        updateState()
    }

    override fun playPause() {
        if (isSimulated) {
            if (simulatedIsPlaying) {
                pause()
            } else {
                if (simulatedPositionMs >= simulatedDurationMs) {
                    simulatedPositionMs = 0L
                }
                resume()
            }
            return
        }

        when (

            player.playbackState

        ) {

            Player.STATE_IDLE -> {

                val url =
                    currentUrl ?: return

                play(
                    url = url,
                    title = currentTitle
                )
            }

            Player.STATE_ENDED -> {
                prepareTimeoutJob?.cancel()
                println(
                    "EXO STATE -> ENDED"
                )

                stopPollingProgress()

                /*
                 * IGNORE TRANSIENT ENDED
                 * DURING PREPARE
                 */

                if (
                    player.playbackState ==
                    Player.STATE_BUFFERING
                ) {
                    return
                }
                if (player.duration <= 0L) {
                    return
                }
                _playback.update {

                    it.copy(
                        isLoading = false,
                        isPlaying = false,
                        isEnded = true
                    )
                }
            }
            else -> {

                if (player.isPlaying) {

                    pause()

                } else {

                    resume()
                }
            }
        }

        updateState()
    }

    /*
     * SEEK
     */

    override fun seekTo(positionMs: Long) {
        println("SEEK TO -> $positionMs")

        player.seekTo(positionMs)

        println(
            "AFTER SEEK -> state=${player.playbackState}, " +
                    "isPlaying=${player.isPlaying}"
        )

        savePlaybackState()
        updateState()
    }

    override fun seekForward(millis: Long) {
        if (player.playbackState != Player.STATE_READY) return
        seekTo(player.currentPosition + millis)
    }

    override fun seekBack(millis: Long) {
        if (player.playbackState != Player.STATE_READY) return
        seekTo(player.currentPosition - millis)
    }

    /*
     * SPEED
     */

    override fun updatePlaybackSpeed(
        speed: Float
    ) {
        if (isSimulated) {
            simulatedSpeed = speed.coerceIn(0.5f, 2f)
            updateSimulatedState()
            return
        }

        val currentPitch = player.playbackParameters.pitch
        player.playbackParameters = PlaybackParameters(speed.coerceIn(0.5f, 2f), currentPitch)
        updateState()
    }

    override fun updatePlaybackPitch(
        pitch: Float
    ) {
        if (isSimulated) return
        val currentSpeed = player.playbackParameters.speed
        player.playbackParameters = PlaybackParameters(currentSpeed, pitch.coerceIn(0.5f, 2f))
        updateState()
    }

    private var sleepTimerJob: Job? = null
    private var sleepTimerEndTimestamp: Long? = null

    override fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) {
            sleepTimerEndTimestamp = null
            _playback.update { it.copy(sleepTimerRemainingMs = null) }
            return
        }

        val durationMs = minutes * 60 * 1000L
        sleepTimerEndTimestamp = System.currentTimeMillis() + durationMs

        sleepTimerJob = scope.launch {
            while (isActive) {
                val remaining = (sleepTimerEndTimestamp ?: break) - System.currentTimeMillis()
                if (remaining <= 0L) {
                    _playback.update { it.copy(sleepTimerRemainingMs = 0L) }
                    pause()
                    sleepTimerEndTimestamp = null
                    break
                }
                _playback.update { it.copy(sleepTimerRemainingMs = remaining) }
                delay(1000L)
            }
        }
    }

    override fun setRepeatMode(mode: String) {
        _playback.update { it.copy(repeatMode = mode) }
        when (mode) {
            "ONE" -> player.repeatMode = Player.REPEAT_MODE_ONE
            "ALL" -> player.repeatMode = Player.REPEAT_MODE_ALL
            else -> player.repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    /*
     * VOLUME
     */

    override fun setVolume(
        volume: Float
    ) {
        if (isSimulated) return

        player.volume =
            volume.coerceIn(
                0f,
                1f
            )

        updateState()
    }

    /*
     * REPEAT
     */

    override fun repeatLine(
        startMs: Long
    ) {

        seekTo(startMs)
    }

    /*
     * BACKGROUND
     */

    override fun startBackgroundPlayback() {
        if (isSimulated) return
        println("AndroidStreamAudioController: startBackgroundPlayback called")
        try {
            val intent = Intent(

                context,

                NitnemAudioService::class.java
            )

            context.startService(
                intent
            )
            println("AndroidStreamAudioController: startService executed successfully")
        } catch (e: Exception) {
            println("AndroidStreamAudioController: Failed to start background service:")
            e.printStackTrace()
        }
    }

    override fun stopBackgroundPlayback() {

        val intent = Intent(

            context,

            NitnemAudioService::class.java
        )

        context.stopService(intent)
    }

    /*
     * SAVE / RESTORE
     */

    override fun savePlaybackState() {

        val title = if (isSimulated) {
            currentTitle
        } else {
            player.currentMediaItem?.mediaMetadata?.title?.toString()
        }

        context.getSharedPreferences(
            "audio_state",
            Context.MODE_PRIVATE
        ).edit()

            .putString(
                "last_url",
                currentUrl
            )

            .putLong(
                "last_position",
                if (isSimulated) simulatedPositionMs else player.currentPosition
            )

            .putString(
                "last_title",
                title
            )

            .apply()
    }

    override fun restorePlaybackState() {

        val prefs =

            context.getSharedPreferences(
                "audio_state",
                Context.MODE_PRIVATE
            )

        val url =

            prefs.getString(
                "last_url",
                null
            ) ?: return


        if (
            url.startsWith("http://") ||
            url.startsWith("https://")
        ) {
            return
        }
        val position =

            prefs.getLong(
                "last_position",
                0L
            )

        val title =

            prefs.getString(
                "last_title",
                "Nitnem"
            ) ?: "Nitnem"
        println("RESTORE URL -> $url")
        prepareInternal(
            url = url,
            title = title,
            initialPositionMs = position,
            playWhenReady = false
        )

    }

    /*
     * STOP
     */

    override fun stop() {
        println("PLAYER STOP")
        player.stop()

        stopBackgroundPlayback()

        updateState()
    }

    override fun release() {
        println("PLAYER RELEASE")
        savePlaybackState()
        try {
            stopPollingProgress()
            prepareTimeoutJob?.cancel()
            player.stop()
            player.release()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            synchronized(Companion) {
                if (INSTANCE === this) {
                    INSTANCE = null
                }
            }
        }
    }

    /*
     * DOWNLOAD
     */

    override suspend fun downloadAudio(
        baniId: String,
        remoteUrl: String,
        trackId: String?
    ): Boolean = withContext(
        Dispatchers.IO
    ) {

        try {

            val filename = if (trackId != null && trackId != "default") {
                "${baniId}_$trackId.mp3"
            } else {
                "$baniId.mp3"
            }
            val destinationFile = File(audioDir, filename)

            if (
                destinationFile.exists()
            ) {

                destinationFile.delete()
            }

            val connection =

                URL(remoteUrl)
                    .openConnection() as
                    HttpURLConnection

            connection.setRequestProperty(
                "User-Agent",
                "NitnemGutka"
            )

            connection.connectTimeout =
                15000

            connection.readTimeout =
                15000

            if (
                connection.responseCode !=
                HttpURLConnection.HTTP_OK
            ) {

                return@withContext false
            }

            connection.inputStream.use { input ->

                destinationFile.outputStream()
                    .use { output ->

                        input.copyTo(output)
                    }
            }

            true

        } catch (e: Exception) {

            e.printStackTrace()

            false
        }
    }
    override fun isAudioDownloaded(
        baniId: String,
        trackId: String?
    ): Boolean {

        val activeId =
            trackId ?: getActiveTrackId(baniId)

        /*
         * CURATED RAW TRACK
         */

        val curatedTrack =
            CuratedAudioCatalog
                .getTracksForBani(baniId)
                .firstOrNull {
                    it.id == activeId
                }

        if (
            curatedTrack?.remoteUrl
                ?.startsWith("raw/") == true
        ) {

            return true
        }



        /*
         * FILE CHECK
         */

        val filename =

            if (activeId != "default") {

                "${baniId}_$activeId.mp3"

            } else {

                "$baniId.mp3"
            }

        val file =
            File(audioDir, filename)

        return (
                file.exists() &&
                        file.length() > 0
                )
    }
    override fun getLocalAudioPath(
        baniId: String,
        trackId: String?
    ): String? {

        val activeId =
            trackId ?: getActiveTrackId(baniId)

        /*
         * CURATED TRACK
         */

        val curatedTrack =
            CuratedAudioCatalog
                .getTracksForBani(baniId)
                .firstOrNull {
                    it.id == activeId
                }

        if (curatedTrack != null) {

            /*
             * RAW RESOURCE
             */

            if (
                curatedTrack.remoteUrl
                    ?.startsWith("raw/") == true
            ) {

                return curatedTrack.remoteUrl
            }

            /*
             * DOWNLOADED CURATED FILE
             */

            val curatedFile =
                File(
                    audioDir,
                    "${baniId}_${activeId}.mp3"
                )

            if (
                curatedFile.exists() &&
                curatedFile.length() > 0
            ) {

                return curatedFile.absolutePath
            }
        }

        /*
         * IMPORTED / DOWNLOADED
         */

        val filename =

            if (activeId != "default") {

                "${baniId}_$activeId.mp3"

            } else {

                "$baniId.mp3"
            }

        val file =
            File(audioDir, filename)

        return if (
            file.exists() &&
            file.length() > 0
        ) {

            file.absolutePath

        } else {

            null
        }
    }

    override fun deleteAudio(
        baniId: String,
        trackId: String?
    ) {

        val filename = if (trackId != null && trackId != "default") {
            "${baniId}_$trackId.mp3"
        } else {
            "$baniId.mp3"
        }
        val file = File(audioDir, filename)

        if (file.exists()) {

            file.delete()
        }
    }
    private fun resolveTrackUrl(
        url: String?
    ): String? {

        if (url.isNullOrBlank()) {
            return null
        }

        /*
         * ASSET AUDIO
         */

        if (
            url.startsWith("raw/")
        ) {

            return url
        }

        /*
         * REMOTE URL
         */

        if (
            url.startsWith("http://") ||
            url.startsWith("https://")
        ) {

            return url
        }

        return url
    }

    private val json = Json { ignoreUnknownKeys = true }
    private val customTracksFile by lazy { File(audioDir, "custom_tracks.json") }

    override fun getCustomTracks(): List<AudioTrack> {
        return try {
            if (customTracksFile.exists()) {
                json.decodeFromString<List<AudioTrack>>(customTracksFile.readText())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getCustomTracks(baniId: String): List<AudioTrack> {
        val userTracks =
            getCustomTracks().filter {

                it.id.startsWith(
                    "${baniId}_custom_"
                )
            }
        val curated =
            CuratedAudioCatalog
                .getTracksForBani(baniId)
                .map { track ->

                    if (
                        track.remoteUrl
                            ?.startsWith("raw/") == true
                    ) {

                        track.copy(
                            localPath = track.remoteUrl
                        )

                    } else {

                        val filename =
                            "${baniId}_${track.id}.mp3"

                        val file =
                            File(audioDir, filename)

                        if (
                            file.exists() &&
                            file.length() > 0
                        ) {

                            track.copy(
                                localPath =
                                    file.absolutePath
                            )

                        } else {

                            track
                        }
                    }
                }
        return (
    curated + userTracks
).distinctBy {
    it.id
}
    }

    override fun importCustomTrack(baniId: String, artist: String, fileUri: String): AudioTrack? {
        return try {
            val contentResolver = context.contentResolver
            val uri = Uri.parse(fileUri)
            val artistClean = artist.lowercase().replace(" ", "_").replace(Regex("[^a-z0-9_]"), "")
            val filename = "${baniId}_custom_${artistClean}.mp3"
            val destinationFile = File(audioDir, filename)

            if (destinationFile.exists()) {
                destinationFile.delete()
            }

            contentResolver.openInputStream(uri)?.use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            var durationMs = 0L
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, Uri.fromFile(destinationFile))
                val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                durationMs = durationStr?.toLongOrNull() ?: 0L
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                retriever.release()
            }

            val trackId = "${baniId}_custom_${artistClean}_${System.currentTimeMillis()}"
            val newTrack = AudioTrack(
                id = trackId,
                title = artist,
                artist = artist,
                durationMs = durationMs,
                license = "Custom Import",
                attribution = "Local User Audio",
                localPath = destinationFile.absolutePath,
                remoteUrl = null
            )

            val currentTracks = getCustomTracks().toMutableList()
            currentTracks.removeAll { it.id == trackId }
            currentTracks.add(newTrack)

            customTracksFile.writeText(Json.encodeToString(currentTracks))
            newTrack
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun deleteCustomTrack(trackId: String): Boolean {
        if (trackId.startsWith("curated_")) {
            val baniId = GeneratedBaniCatalog.banis.firstOrNull { b ->
                CuratedAudioCatalog.getTracksForBani(b.id).any { it.id == trackId }
            }?.id
            if (baniId != null) {
                val filename = "${baniId}_$trackId.mp3"
                val file = File(audioDir, filename)
                if (file.exists()) {
                    file.delete()
                }
                val timingsFile = File(audioDir, "${baniId}_${trackId}_timings.json")
                if (timingsFile.exists()) {
                    timingsFile.delete()
                }
                if (getActiveTrackId(baniId) == trackId) {
                    setActiveTrackId(baniId, "default")
                }
                return true
            }
            return false
        }
        return try {
            val currentTracks = getCustomTracks().toMutableList()
            val trackToDelete = currentTracks.firstOrNull { it.id == trackId }
            if (trackToDelete != null) {
                trackToDelete.localPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        file.delete()
                    }
                }
                currentTracks.remove(trackToDelete)
                customTracksFile.writeText(Json.encodeToString(currentTracks))

                val baniId = trackId.substringBefore("_custom_").substringBefore("_")
                val timingsFile = File(audioDir, "${baniId}_${trackId}_timings.json")
                if (timingsFile.exists()) {
                    timingsFile.delete()
                }

                if (getActiveTrackId(baniId) == trackId) {
                    setActiveTrackId(baniId, "default")
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun getActiveTrackId(
        baniId: String
    ): String {

        val memoryValue =
            activeTrackState.value[baniId]

        if (memoryValue != null) {

            println(
                "GET ACTIVE TRACK (MEMORY) -> bani=$baniId track=$memoryValue"
            )

            return memoryValue
        }

        val prefs =
            context.getSharedPreferences(
                "audio_state",
                Context.MODE_PRIVATE
            )

        val savedValue =
            prefs.getString(
                "active_track_id_$baniId",
                "default"
            ) ?: "default"

        println(
            "GET ACTIVE TRACK (PREFS) -> bani=$baniId track=$savedValue"
        )

        return savedValue
    }

    override fun setActiveTrackId(

        baniId: String,

        trackId: String

    ) {

        println("SET ACTIVE TRACK -> bani=$baniId track=$trackId")

    activeTrackState.update {

        it + (baniId to trackId)
    }

    val prefs =
        context.getSharedPreferences(
            "audio_state",
            Context.MODE_PRIVATE
        )

    prefs.edit()
        .putString(
            "active_track_id_$baniId",
            trackId
        )
        .apply()
}

    override fun getActiveAudioUrl(
        baniId: String
    ): String? {

        val activeTrackId =
            getActiveTrackId(baniId)
        println(
            "ACTIVE AUDIO URL -> bani=$baniId activeTrack=$activeTrackId"
        )
        /*
         * SELECTED TRACK
         */

        if (activeTrackId != "default") {

            /*
             * CUSTOM TRACK
             */

            val customTrack =
                getCustomTracks(baniId)
                    .firstOrNull {
                        it.id == activeTrackId
                    }

            if (customTrack != null) {



                /*
                 * LOCAL IMPORTED FILE
                 */

                customTrack.localPath?.let { path ->

                    val file = File(path)

                    if (
                        file.exists() &&
                        file.length() > 0
                    ) {

                        return file.absolutePath
                    }
                }

                /*
                 * REMOTE URL
                 */

                if (
                    !customTrack.remoteUrl
                        .isNullOrBlank()
                ) {

                    return resolveTrackUrl(
                        customTrack.remoteUrl
                    )
                }
            }

            /*
             * CURATED TRACK
             */

            val curatedTrack =
                CuratedAudioCatalog
                    .getTracksForBani(baniId)
                    .firstOrNull {
                        it.id == activeTrackId
                    }

            if (curatedTrack != null) {

                /*
                 * DOWNLOADED VERSION
                 */

                val downloadedFile =
                    File(
                        audioDir,
                        "${baniId}_${activeTrackId}.mp3"
                    )

                if (
                    downloadedFile.exists() &&
                    downloadedFile.length() > 0
                ) {

                    return downloadedFile.absolutePath
                }

                /*
                 * ASSET / REMOTE
                 */

                if (
                    !curatedTrack.remoteUrl
                        .isNullOrBlank()
                ) {

                    return resolveTrackUrl(
                        curatedTrack.remoteUrl
                    )
                }
            }
        }

        /*
         * DEFAULT LOCAL / DOWNLOADED
         */

        val defaultLocal =
            getLocalAudioPath(baniId)

        if (
            !defaultLocal.isNullOrBlank()
        ) {

            return defaultLocal
        }

        /*
         * GENERATED CATALOG
         */

        /*
   * GENERATED CATALOG
   * ONLY ALLOW RAW URLS
   */

        val generatedRemote =
            GeneratedBaniCatalog
                .banis
                .firstOrNull {
                    it.id == baniId
                }
                ?.audio
                ?.remoteUrl

        if (
            !generatedRemote.isNullOrBlank() &&
            generatedRemote.startsWith("raw/")
        ) {

            return resolveTrackUrl(
                generatedRemote
            )
        }

        /*
         * FALLBACK CURATED TRACK
         */

        val fallbackTrack =
            CuratedAudioCatalog
                .getTracksForBani(baniId)
                .firstOrNull()

        if (fallbackTrack != null) {

            val downloadedFile =
                File(
                    audioDir,
                    "${baniId}_${fallbackTrack.id}.mp3"
                )

            if (
                downloadedFile.exists() &&
                downloadedFile.length() > 0
            ) {

                return downloadedFile.absolutePath
            }

            if (
                !fallbackTrack.remoteUrl
                    .isNullOrBlank()
            ) {

                return resolveTrackUrl(
                    fallbackTrack.remoteUrl
                )
            }
        }

        return null
    }
    override fun hasTimingFile(
        baniId: String,
        trackId: String
    ): Boolean {

        println(
            "HAS TIMING FILE -> " +
                    "bani=$baniId, " +
                    "track=$trackId"
        )

        val resolvedTrackId =
            if (trackId == "default") {

                CuratedAudioCatalog
                    .getTracksForBani(baniId)
                    .firstOrNull()
                    ?.id
                    ?: "default"

            } else {

                trackId
            }

        println(
            "TIMING RESOLUTION -> " +
                    "requestedTrack=$trackId, " +
                    "resolvedTrack=$resolvedTrackId"
        )

        val track =
            getCustomTracks(baniId)
                .firstOrNull { it.id == resolvedTrackId }
                ?: CuratedAudioCatalog
                    .getTracksForBani(baniId)
                    .firstOrNull { it.id == resolvedTrackId }
                ?: return false

        val timingKey =
            track.timingKey()

        val assetPath =
            "timing/${timingKey}_timing.json"

        println(
            "TIMING CHECK -> " +
                    "trackId=${track.id}, " +
                    "timingKey=$timingKey, " +
                    "assetPath=$assetPath"
        )


        /*
         * DEBUG: LIST ALL TIMING FILES
         */

        println("ASSET TIMING FILES:")

        context.assets.list("timing")?.forEach { fileName ->

            println("TIMING ASSET -> $fileName")

        }
        val localTimingFile = java.io.File(context.filesDir, "audio/${timingKey}_timing.json")
        if (localTimingFile.exists() && localTimingFile.length() > 0) {
            println("TIMING FILE FOUND IN LOCAL STORAGE -> ${localTimingFile.absolutePath}")
            return true
        }

        return try {

            context.assets.open(assetPath).use { }

            println(
                "TIMING FILE FOUND -> $assetPath"
            )

            true

        } catch (e: Exception) {

            println(
                "TIMING FILE NOT FOUND -> $assetPath"
            )

            // Missing timing file is expected for many tracks.
            // Avoid flooding logcat with stack traces.

            false
        }
    }
    override fun playRawResource(
        rawName: String,
        title: String
    ) {

        val rawUrl =
            "raw/$rawName"

        prepareInternal(
            url = rawUrl,
            title = title,
            initialPositionMs = 0L,
            playWhenReady = true
        )
    }
    private fun extractYoutubeVideoId(url: String): String? {
        return when {
            url.contains("youtu.be/") -> {
                val index = url.indexOf("youtu.be/") + 9
                val end = url.indexOf('?', index)
                if (end == -1) url.substring(index) else url.substring(index, end)
            }
            url.contains("v=") -> {
                val index = url.indexOf("v=") + 2
                val end = url.indexOf('&', index)
                if (end == -1) url.substring(index) else url.substring(index, end)
            }
            url.contains("embed/") -> {
                val index = url.indexOf("embed/") + 6
                val end = url.indexOf('?', index)
                if (end == -1) url.substring(index) else url.substring(index, end)
            }
            else -> null
        }
    }

    private suspend fun resolveYoutubeAudioUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        val instances = listOf(
            "https://inv.nadeko.net",
            "https://yt.chocolatemoo53.com",
            "https://invidious.nerdvpn.de",
            "https://yewtu.be",
            "https://vid.puffyan.us"
        )
        for (instance in instances) {
            val testUrl = "$instance/latest_version?id=$videoId&itag=140&local=true"
            try {
                val connection = URL(testUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "HEAD"
                connection.connectTimeout = 3000
                connection.readTimeout = 3000
                connection.instanceFollowRedirects = false
                val responseCode = connection.responseCode
                if (responseCode == 200 || responseCode == 301 || responseCode == 302 || responseCode == 307) {
                    connection.disconnect()
                    return@withContext testUrl
                }
                connection.disconnect()
            } catch (e: Exception) {
                // Try next
            }
        }
        null
    }

}

@OptIn(UnstableApi::class)
@Composable
actual fun rememberStreamAudioController():
        StreamAudioController {

    val context =
        LocalContext.current.applicationContext

    return remember {

        AndroidStreamAudioController.getInstance(
            context
        )
    }
}