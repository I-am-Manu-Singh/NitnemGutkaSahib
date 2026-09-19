package com.creativeblisslabs.nitnemguktasteek.analytics

sealed class AnalyticsEvent {

    data class BaniCompleted(

        val baniId: String,

        val readingMinutes: Int

    ) : AnalyticsEvent()

    data class PlaybackStarted(

        val baniId: String

    ) : AnalyticsEvent()

    data class PlaybackPaused(

        val baniId: String,

        val positionMs: Long

    ) : AnalyticsEvent()

    data class BookmarkAdded(

        val lineId: String

    ) : AnalyticsEvent()

    data class AppOpened(

        val timestamp: Long

    ) : AnalyticsEvent()
}