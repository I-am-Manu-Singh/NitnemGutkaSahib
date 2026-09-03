package org.audiocular.nitnem.models

import kotlinx.serialization.Serializable

@Serializable
enum class BaniTime {

    Morning,
    Afternoon,
    Evening,
    Night
}

@Serializable
enum class ScriptMode {

    Gurmukhi,
    Transliteration,
    Translation,
    WordMeaning
}

@Serializable
data class LineTiming(

    val id: String,

    val startsAtMs: Long,

    val endsAtMs: Long
)

@Serializable
data class PrayerReminder(

    val prayerId: String,

    val title: String,

    val timeHour: Int,

    val timeMinute: Int,

    val enabled: Boolean
)

@Serializable
data class Bani(

    val id: String,

    val title: String,

    val time: BaniTime,

    val estimatedMinutes: Int,

    val lines: List<BaniLine>,

    val audio: AudioTrack? = null,

    val source: ContentSource,

    val author: String = "",

    val ang: String = "",

    val structure: String = ""
)

@Serializable
data class BaniLine(

    val id: String,

    val gurmukhi: String,

    val transliteration: String,

    val translation: String,

    val hindi: String,

    val punjabi: String,

    val wordMeaning: String,

    val startsAtMs: Long,

    val endsAtMs: Long
)

@Serializable
data class AudioTrack(

    val id: String,

    val title: String,

    val artist: String,

    val durationMs: Long,

    val license: String,

    val attribution: String,

    val localPath: String? = null,

    val remoteUrl: String? = null
)

@Serializable
data class ContentSource(

    val text: String,

    val audio: String,

    val licenseNote: String
)

@Serializable
data class PlaybackSnapshot(

    val currentTitle: String =
        "",

    val currentArtist: String =
        "",

    val currentArtwork: String? =
        null,

    val isReady: Boolean =
        false,

    val isPlaying: Boolean =
        false,

    val positionMs: Long =
        0L,

    val durationMs: Long =
        0L,

    val speed: Float =
        1f,

    val error: String? =
        null,

    val isEnded: Boolean =
        false,

    val isLoading: Boolean =
        false,

    val isMuted: Boolean =
        false,

    val pitch: Float =
        1f,

    val sleepTimerRemainingMs: Long? =
        null,

    val repeatMode: String =
        "OFF"
)

@Serializable
data class JourneyStep(

    val id: String,

    val baniId: String,

    val title: String,

    val time: BaniTime,

    val isCompleted: Boolean,

    val progress: Float
)
@Serializable

data class UserAnalytics(

    val totalCompletedBanis: Int = 0,

    val totalReadingMinutes: Int = 0,

    val currentStreak: Int = 0,

    val longestStreak: Int = 0,

    val monthlyConsistencyDays: Int = 0,

    val completionHistory:

        Map<String, Set<String>> = emptyMap()

)
@Serializable
data class Bookmark(

    val lineId: String,

    val baniId: String,

    val createdAt: Long
)

@Serializable
data class ReminderPreference(

    val id: String,

    val baniTime: BaniTime,

    val localHour: Int,

    val localMinute: Int,

    val silent: Boolean
)

@Serializable
data class Reflection(

    val baniId: String,

    val title: String,

    val insight: String,

    val takeaway: String
)

@Serializable
data class StreakSummary(
    val freezesAvailable: Int = 2,
    val currentDays: Int,

    val weeklyCompletedDays: Int,

    val monthlyCompletedDays: Int,

    val compassionateMessage: String
)

@Serializable
data class HomeSnapshot(

    val greeting: String,

    val journey: List<JourneyStep>,

    val streak: StreakSummary,

    val continueTitle: String,

    val reminder: String,

    val reflection: Reflection
)