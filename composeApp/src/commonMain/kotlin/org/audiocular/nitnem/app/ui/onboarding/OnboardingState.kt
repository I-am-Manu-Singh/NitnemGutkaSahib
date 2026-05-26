package org.audiocular.nitnem.app.ui.onboarding

data class OnboardingState(

    val page: Int = 0,

    val selectedLanguage: String = "English",

    val selectedTheme: String = "Dark",

    val amritTime: String = "04:30 AM",

    val rehrasTime: String = "06:30 PM",

    val sohilaTime: String = "10:00 PM",

    val widgetTheme: String = "Classic Gold",

    val widgetContent: String = "Next Bani"
)