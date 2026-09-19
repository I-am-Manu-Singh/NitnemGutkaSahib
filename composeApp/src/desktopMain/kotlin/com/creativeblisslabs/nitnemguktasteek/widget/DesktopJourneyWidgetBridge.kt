package com.creativeblisslabs.nitnemguktasteek.app.widget

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
        completedBaniIds: List<String>,
        widgetTheme: String,
        widgetContent: String
    ) = Unit
}

@Composable
actual fun rememberJourneyWidgetBridge(): JourneyWidgetBridge =
    remember { NoOpJourneyWidgetBridge }
