package com.creativeblisslabs.nitnemguktasteek.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class AndroidFirebaseAnalyticsTracker(
    context: Context
) : AppAnalyticsTracker {

    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    override fun logEvent(name: String, params: Map<String, Any>) {
        val bundle = Bundle()
        params.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Float -> bundle.putFloat(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                else -> bundle.putString(key, value.toString())
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }

    override fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
        crashlytics.setCustomKey(name, value)
    }

    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
        crashlytics.setUserId(userId)
    }

    override fun logBaniOpened(baniId: String, baniTitle: String) {
        logEvent("bani_opened", mapOf(
            "bani_id" to baniId,
            "bani_title" to baniTitle
        ))
        crashlytics.log("Opened Bani: $baniTitle ($baniId)")
    }

    override fun logBaniCompleted(baniId: String, durationSeconds: Long, completedFully: Boolean) {
        logEvent("bani_completed", mapOf(
            "bani_id" to baniId,
            "duration_seconds" to durationSeconds,
            "completed_fully" to completedFully
        ))
        crashlytics.log("Completed Bani: $baniId in $durationSeconds seconds")
    }

    override fun logAudioPlayback(baniId: String, action: String, artist: String?) {
        val map = mutableMapOf<String, Any>(
            "bani_id" to baniId,
            "action" to action
        )
        artist?.let { map["artist"] = it }
        logEvent("audio_playback", map)
    }

    override fun logStreakUpdated(currentStreak: Int, bestStreak: Int) {
        logEvent("streak_updated", mapOf(
            "current_streak" to currentStreak,
            "best_streak" to bestStreak
        ))
        setUserProperty("current_streak", currentStreak.toString())
    }

    override fun logQuizCompleted(score: Int, totalQuestions: Int, timeSpentSeconds: Long) {
        logEvent("quiz_completed", mapOf(
            "score" to score,
            "total_questions" to totalQuestions,
            "time_spent_seconds" to timeSpentSeconds
        ))
    }

    override fun logSimranSession(durationMinutes: Int, count: Int) {
        logEvent("simran_session", mapOf(
            "duration_minutes" to durationMinutes,
            "jaap_count" to count
        ))
    }

    override fun recordNonFatalException(throwable: Throwable, message: String?) {
        message?.let { crashlytics.log(it) }
        crashlytics.recordException(throwable)
    }
}
