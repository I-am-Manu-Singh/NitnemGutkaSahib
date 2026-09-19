package com.creativeblisslabs.nitnemguktasteek.app.widget

import androidx.compose.runtime.Composable

interface JourneyWidgetBridge {
    fun update(
        completed: Int,
        total: Int,
        currentTitle: String,
        streakDays: Int,
        weeklyCompleted: Int,
        monthlyConsistencyDays: Int,
        completedBaniIds: List<String>,
        widgetTheme: String,
        widgetContent: String
    )
}

@Composable
expect fun rememberJourneyWidgetBridge(): JourneyWidgetBridge

