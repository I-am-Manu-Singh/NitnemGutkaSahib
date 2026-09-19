package com.creativeblisslabs.nitnemguktasteek.reminders

import com.creativeblisslabs.nitnemguktasteek.models.BaniTime
import com.creativeblisslabs.nitnemguktasteek.models.ReminderPreference

class ReminderPlanner {
    fun defaultReminders(): List<ReminderPreference> = listOf(
        ReminderPreference("morning", BaniTime.Morning, 5, 30, silent = false),
        ReminderPreference("evening", BaniTime.Evening, 18, 30, silent = true),
        ReminderPreference("night", BaniTime.Night, 21, 30, silent = true)
    )

    fun gentleMessage(time: BaniTime): String = when (time) {
        BaniTime.Morning -> "Begin the morning with a quiet breath."
        BaniTime.Afternoon -> "Take 5 peaceful minutes today."
        BaniTime.Evening -> "Evening Rehraas time."
        BaniTime.Night -> "Close the day softly."
    }
}
