package org.audiocular.nitnem.utils

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberAudioPicker(onAudioPicked: (String) -> Unit): AudioPickerLauncher {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAudioPicked(it.toString()) }
    }
    return remember(launcher) {
        object : AudioPickerLauncher {
            override fun launch() {
                launcher.launch("audio/*")
            }
        }
    }
}
