package com.creativeblisslabs.nitnemguktasteek.app.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppTab(
    val label: String,
    val icon: ImageVector
) {

    Home(
        label = "Home",
        icon = Icons.Filled.Home
    ),

    Experience(
        label = "Read",
        icon = Icons.Filled.MenuBook
    ),

    Practice(
        label = "Practice",
        icon = Icons.Filled.SelfImprovement
    ),

    Settings(
        label = "Settings",
        icon = Icons.Filled.Settings
    )
}