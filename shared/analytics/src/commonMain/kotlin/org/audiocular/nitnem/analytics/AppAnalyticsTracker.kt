package org.audiocular.nitnem.analytics

interface AppAnalyticsTracker {
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
    fun setUserProperty(name: String, value: String)
    fun setUserId(userId: String)
    fun logBaniOpened(baniId: String, baniTitle: String)
    fun logBaniCompleted(baniId: String, durationSeconds: Long, completedFully: Boolean)
    fun logAudioPlayback(baniId: String, action: String, artist: String? = null)
    fun logStreakUpdated(currentStreak: Int, bestStreak: Int)
    fun logQuizCompleted(score: Int, totalQuestions: Int, timeSpentSeconds: Long)
    fun logSimranSession(durationMinutes: Int, count: Int)
    fun recordNonFatalException(throwable: Throwable, message: String? = null)
}

object NoOpAnalyticsTracker : AppAnalyticsTracker {
    override fun logEvent(name: String, params: Map<String, Any>) {}
    override fun setUserProperty(name: String, value: String) {}
    override fun setUserId(userId: String) {}
    override fun logBaniOpened(baniId: String, baniTitle: String) {}
    override fun logBaniCompleted(baniId: String, durationSeconds: Long, completedFully: Boolean) {}
    override fun logAudioPlayback(baniId: String, action: String, artist: String?) {}
    override fun logStreakUpdated(currentStreak: Int, bestStreak: Int) {}
    override fun logQuizCompleted(score: Int, totalQuestions: Int, timeSpentSeconds: Long) {}
    override fun logSimranSession(durationMinutes: Int, count: Int) {}
    override fun recordNonFatalException(throwable: Throwable, message: String?) {}
}
