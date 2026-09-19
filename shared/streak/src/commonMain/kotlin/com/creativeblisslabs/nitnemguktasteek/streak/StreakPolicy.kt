package com.creativeblisslabs.nitnemguktasteek.streak

import com.creativeblisslabs.nitnemguktasteek.models.StreakSummary

class StreakPolicy {
    fun completeDay(previous: StreakSummary): StreakSummary =
        previous.copy(
            currentDays = previous.currentDays + 1,
            weeklyCompletedDays = (previous.weeklyCompletedDays + 1).coerceAtMost(7),
            monthlyCompletedDays = previous.monthlyCompletedDays + 1,
            compassionateMessage = "You showed up with care today."
        )

    fun recover(previous: StreakSummary): StreakSummary =
        previous.copy(
            freezesAvailable = (previous.freezesAvailable - 1).coerceAtLeast(0),
            compassionateMessage = "Begin gently again. Nothing sacred is lost."
        )
}
