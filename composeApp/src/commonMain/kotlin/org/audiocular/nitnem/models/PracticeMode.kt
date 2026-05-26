package org.audiocular.nitnem.app.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector

enum class PracticeMode(
    val label: String,
    val icon: ImageVector
) {

    Path(
        label = "Path",
        icon = Icons.Filled.Headphones
    ),

    Repeat(
        label = "Repeat",
        icon = Icons.Filled.Repeat
    ),

    Jaap(
        label = "Jaap",
        icon = Icons.Filled.SelfImprovement
    ),

    Meditation(
        label = "Meditate",
        icon = Icons.Filled.Bedtime
    )
}