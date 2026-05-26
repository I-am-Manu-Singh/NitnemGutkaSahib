package org.audiocular.nitnem

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.audiocular.nitnem.app.NitnemApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Nitnem Gutka Steek"
    ) {
        NitnemApp()
    }
}
