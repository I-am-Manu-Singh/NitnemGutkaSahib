package org.audiocular.nitnem.app.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private object NoOpJourneyWidgetBridge : JourneyWidgetBridge {
    override fun update(
        completed: Int,
        total: Int,
        currentTitle: String,
        streakDays: Int,
        weeklyCompleted: Int,
        monthlyConsistencyDays: Int,
        completedBaniIds: List<String>
    ) = Unit
}

@Composable
actual fun rememberJourneyWidgetBridge(): JourneyWidgetBridge =
    remember { NoOpJourneyWidgetBridge }
