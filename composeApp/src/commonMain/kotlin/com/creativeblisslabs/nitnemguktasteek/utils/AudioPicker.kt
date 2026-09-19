package com.creativeblisslabs.nitnemguktasteek.utils

import androidx.compose.runtime.Composable

interface AudioPickerLauncher {
    fun launch()
}

@Composable
expect fun rememberAudioPicker(onAudioPicked: (String) -> Unit): AudioPickerLauncher
