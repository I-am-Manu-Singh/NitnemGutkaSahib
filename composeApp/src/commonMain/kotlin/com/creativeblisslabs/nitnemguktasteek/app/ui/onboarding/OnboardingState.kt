package com.creativeblisslabs.nitnemguktasteek.app.ui.onboarding

data class OnboardingState(

    val page: Int = 0,

    val selectedLanguage: String = "English",

    val selectedTheme: String = "Dark",

    val amritTime: String = "04:30 AM",

    val rehrasTime: String = "06:30 PM",

    val sohilaTime: String = "10:00 PM",

    val widgetTheme: String = "Classic Gold",

    val widgetContent: String = "Mascot Assistant",

    val showGurmukhi: Boolean = true,

    val showDevanagariTransliteration: Boolean = false,

    val showRomanTransliteration: Boolean = true,

    val showEnglishTranslation: Boolean = true,

    val showPunjabiTranslation: Boolean = false,

    val showWordMeaning: Boolean = false,

    val larivaarMode: Boolean = false
)