package org.audiocular.nitnem.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.viewmodel.NitnemViewModel

@Composable
fun NitnemApp(
    viewModel: NitnemViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val themeMode = when {
        uiState.darkMode && uiState.amoledMode -> ThemeMode.Amoled
        uiState.darkMode -> ThemeMode.Dark
        else -> ThemeMode.Light
    }
    AppTheme.themeMode = themeMode

    val colorScheme = when (themeMode) {
        ThemeMode.Light -> lightColorScheme(
            primary = Accent,
            secondary = Gold,
            background = Background,
            surface = Color.White,
            onSurface = Color.Black,
            onBackground = Color.Black
        )
        ThemeMode.Dark -> darkColorScheme(
            primary = Accent,
            secondary = Gold,
            background = Background,
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White,
            onBackground = Color.White
        )
        ThemeMode.Amoled -> darkColorScheme(
            primary = Accent,
            secondary = Gold,
            background = Color.Black,
            surface = Color.Black,
            onSurface = Color.White,
            onBackground = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme
    ) {
        Surface(
            color = Background
        ) {
            NitnemRoot(viewModel = viewModel)
        }
    }
}