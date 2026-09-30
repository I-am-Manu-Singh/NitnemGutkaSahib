package com.creativeblisslabs.nitnemguktasteek

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.creativeblisslabs.nitnemguktasteek.app.NitnemApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bani: Nitnem & Steek Companion"
    ) {
        NitnemApp()
    }
}
