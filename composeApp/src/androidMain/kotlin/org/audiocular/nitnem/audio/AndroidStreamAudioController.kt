package org.audiocular.nitnem.app.audio

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.C
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cache.CacheDataSource

class AndroidStreamAudioController private constructor(
    private val context: Context
) : StreamAudioController {

    companion object {
        @Volatile
        private var INSTANCE: AndroidStreamAudioController? = null

        private var simpleCache: SimpleCache? = null

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

    /*
     * PLAYER
     */

    private val httpDataSourceFactory =
        DefaultHttpDataSource.Factory()
            .setUserAgent(
                "NitnemGutka"
            )
            .setAllowCrossProtocolRedirects(true)

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
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(
                    cacheDataSourceFactory
                )
            )
            .build()
            .apply {

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

                    updateState()
                }

                override fun onPlayerError(
                    error: PlaybackException
                ) {

                    error.printStackTrace()

                    _playback.update {

                        it.copy(
                            error =
                                error.localizedMessage,
                            isLoading = false
                        )
                    }
                }
            }
        )

        restorePlaybackState()
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

                delay(500)
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

        val duration =

            if (
                player.duration ==
                C.TIME_UNSET
            ) {

                0L

            } else {

                player.duration
            }

        _playback.update {

            it.copy(

                isPlaying =
                    player.isPlaying,

                positionMs =
                    player.currentPosition,

                durationMs =
                    duration,

                speed =
                    player.playbackParameters.speed,

                isReady =

                    player.playbackState ==
                        Player.STATE_READY ||

                        player.playbackState ==
                        Player.STATE_BUFFERING,

                error = null,

                isEnded = player.playbackState == Player.STATE_ENDED,

                isLoading = player.playbackState == Player.STATE_BUFFERING || player.isLoading
            )
        }
    }

    /*
     * URI
     */

    private fun createUri(
        url: String
    ): Uri {

        return when {

            url.startsWith("raw/") -> {

                val resourceName =
                    url.removePrefix("raw/")

                val resourceId =

                    context.resources.getIdentifier(

                        resourceName,

                        "raw",

                        context.packageName
                    )

                if (resourceId == 0) {

                    throw IllegalArgumentException(
                        "Raw resource not found: $resourceName"
                    )
                }

                Uri.parse(
                    "android.resource://${context.packageName}/$resourceId"
                )
            }

            url.startsWith("http") -> {

                Uri.parse(url)
            }

            url.startsWith("/") -> {

                Uri.fromFile(
                    File(url)
                )
            }

            else -> {

                Uri.parse(url)
            }
        }
    }

    /*
     * PREPARE
     */

    private fun prepareInternal(
        url: String,
        title: String,
        initialPositionMs: Long,
        playWhenReady: Boolean
    ) {
        if (url.isBlank()) {
            return
        }

        if (url.startsWith("simulate://")) {
            isSimulated = true
            currentUrl = url
            currentTitle = title
            simulatedIsPlaying = playWhenReady
            player.stop() // Stop ExoPlayer if it was playing

            // Parse duration
            val durationParam = url.substringAfter("durationMs=", "")
            simulatedDurationMs = durationParam.toLongOrNull() ?: (15 * 60 * 1000L)
            simulatedPositionMs = initialPositionMs
            
            if (playWhenReady) {
                startPollingProgress()
            } else {
                stopPollingProgress()
            }
            updateSimulatedState()
            return
        }

        isSimulated = false

        if (
            currentUrl == url &&
            player.playbackState != Player.STATE_IDLE &&
            player.playbackState != Player.STATE_ENDED
        ) {
            if (playWhenReady) {
                player.play()
                startBackgroundPlayback()
                updateState()
            }
            return
        }

        currentUrl = url

        scope.launch {
            _playback.update {
                it.copy(
                    isPlaying = playWhenReady,
                    isReady = false,
                    isLoading = true,
                    currentTitle = title
                )
            }
            try {
                val resolvedUrl = if (url.contains("youtube.com") || url.contains("youtu.be")) {
                    val videoId = extractYoutubeVideoId(url)
                    if (videoId != null) {
                        resolveYoutubeAudioUrl(videoId) ?: url
                    } else {
                        url
                    }
                } else {
                    url
                }

                val mediaItem = MediaItem.Builder()
                    .setUri(createUri(resolvedUrl))
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(title)
                            .setArtist("Nitnem Gutka Steek")
                            .build()
                    )
                    .build()

                player.stop()
                player.clearMediaItems()
                player.setMediaItem(mediaItem)

                if (initialPositionMs > 0L) {
                    player.seekTo(initialPositionMs)
                }

                player.prepare()
                if (playWhenReady) {
                    player.play()
                    startBackgroundPlayback()
                }
                updateState()

            } catch (e: Exception) {
                e.printStackTrace()
                _playback.update {
                    it.copy(error = e.localizedMessage)
                }
            }
        }
    }

    override fun prepare(
        url: String,
        title: String,
        initialPositionMs: Long
    ) {
        prepareInternal(url, title, initialPositionMs, playWhenReady = false)
    }

    /*
     * PLAY
     */

    override fun play(
        url: String,
        title: String
    ) {
        prepareInternal(url, title, initialPositionMs = 0L, playWhenReady = true)
        savePlaybackState()
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

                play(url)
            }

            Player.STATE_ENDED -> {

                player.seekTo(0)

                resume()
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

    override fun seekTo(
        positionMs: Long
    ) {
        if (isSimulated) {
            simulatedPositionMs = positionMs.coerceIn(0L, simulatedDurationMs)
            updateSimulatedState()
            savePlaybackState()
            return
        }

        val duration =
            player.duration

        val safePosition =

            if (
                duration == C.TIME_UNSET
            ) {

                positionMs.coerceAtLeast(0L)

            } else {

                positionMs.coerceIn(
                    0L,
                    duration
                )
            }

        player.seekTo(
            safePosition
        )

        savePlaybackState()

        updateState()
    }

    override fun seekForward(
        millis: Long
    ) {
        if (isSimulated) {
            seekTo(simulatedPositionMs + millis)
            return
        }

        seekTo(
            player.currentPosition + millis
        )
    }

    override fun seekBack(
        millis: Long
    ) {
        if (isSimulated) {
            seekTo(simulatedPositionMs - millis)
            return
        }

        seekTo(
            player.currentPosition - millis
        )
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

        player.playbackParameters =

            PlaybackParameters(
                speed.coerceIn(
                    0.5f,
                    2f
                )
            )

        updateState()
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

        prepare(url, title, position)
    }

    /*
     * STOP
     */

    override fun stop() {

        player.stop()

        stopBackgroundPlayback()

        updateState()
    }

    override fun release() {

        savePlaybackState()
    }

    /*
     * DOWNLOAD
     */

    override suspend fun downloadAudio(
        baniId: String,
        remoteUrl: String
    ): Boolean = withContext(
        Dispatchers.IO
    ) {

        try {

            val destinationFile =

                File(
                    audioDir,
                    "$baniId.mp3"
                )

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
        baniId: String
    ): Boolean {

        val resourceName = baniId.replace("-", "_")
        val resourceId = context.resources.getIdentifier(
            resourceName,
            "raw",
            context.packageName
        )
        if (resourceId != 0) {
            return true
        }

        val file =

            File(
                audioDir,
                "$baniId.mp3"
            )

        return (
            file.exists() &&
                file.length() > 0
            )
    }

    override fun getLocalAudioPath(
        baniId: String
    ): String? {

        val resourceName = baniId.replace("-", "_")
        val resourceId = context.resources.getIdentifier(
            resourceName,
            "raw",
            context.packageName
        )
        if (resourceId != 0) {
            return "raw/$resourceName"
        }

        val file =

            File(
                audioDir,
                "$baniId.mp3"
            )

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
        baniId: String
    ) {

        val file =

            File(
                audioDir,
                "$baniId.mp3"
            )

        if (file.exists()) {

            file.delete()
        }
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
        val userTracks = getCustomTracks().filter { it.id.startsWith("${baniId}_") || it.id.startsWith(baniId) }
        val curated = CuratedAudioCatalog.getTracksForBani(baniId)
        return curated + userTracks
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
                title = "$artist - Custom Sync",
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

    override fun getActiveTrackId(baniId: String): String {
        val prefs = context.getSharedPreferences("audio_state", Context.MODE_PRIVATE)
        return prefs.getString("active_track_id_$baniId", "default") ?: "default"
    }

    override fun setActiveTrackId(baniId: String, trackId: String) {
        val prefs = context.getSharedPreferences("audio_state", Context.MODE_PRIVATE)
        prefs.edit().putString("active_track_id_$baniId", trackId).apply()
    }

    override fun getActiveAudioUrl(baniId: String): String? {
        val activeTrackId = getActiveTrackId(baniId)
        if (activeTrackId != "default") {
            // First check user imported tracks
            val customTrack = getCustomTracks().firstOrNull { it.id == activeTrackId }
            if (customTrack != null) {
                val path = customTrack.localPath
                if (path != null) {
                    val file = File(path)
                    if (file.exists()) {
                        return file.absolutePath
                    }
                }
            }
            // Next check curated online tracks
            val curatedTrack = CuratedAudioCatalog.getTracksForBani(baniId).firstOrNull { it.id == activeTrackId }
            if (curatedTrack != null && curatedTrack.remoteUrl != null) {
                return curatedTrack.remoteUrl
            }
        }
        val defaultLocal = getLocalAudioPath(baniId)
        if (defaultLocal != null) {
            return defaultLocal
        }
        val bani = GeneratedBaniCatalog.banis.firstOrNull { it.id == baniId }
        val audio = bani?.audio
        if (!audio?.remoteUrl.isNullOrBlank()) {
            return audio.remoteUrl
        }
        // If the default catalog has no remoteUrl, fall back to the first curated track!
        val firstCurated = CuratedAudioCatalog.getTracksForBani(baniId).firstOrNull()
        if (firstCurated != null && !firstCurated.remoteUrl.isNullOrBlank()) {
            return firstCurated.remoteUrl
        }
        return null
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