package org.audiocular.nitnem.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberAudioPicker(onAudioPicked: (String) -> Unit): AudioPickerLauncher {
    return remember {
        object : AudioPickerLauncher {
            override fun launch() {
                // Mock implementation
            }
        }
    }
}
