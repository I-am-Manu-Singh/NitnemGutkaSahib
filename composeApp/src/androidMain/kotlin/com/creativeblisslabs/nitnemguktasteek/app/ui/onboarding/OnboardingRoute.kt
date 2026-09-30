package com.creativeblisslabs.nitnemguktasteek.app.ui.onboarding

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.withContext
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.notifications.PrayerReminderScheduler
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.AccentStyle
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.AppTheme
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.ThemeMode
import com.creativeblisslabs.nitnemguktasteek.data.preferences.AppPreferences
import com.creativeblisslabs.nitnemguktasteek.data.preferences.LocalPreferences
import com.creativeblisslabs.nitnemguktasteek.data.preferences.SettingsRepository

@Composable
fun OnboardingRoute(
   viewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel<OnboardingViewModel>(),
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()

    // Initialize theme state on first composition if needed
    LaunchedEffect(Unit) {
        val currentThemeStr = when (AppTheme.themeMode) {
            ThemeMode.Light -> "Light"
            ThemeMode.Amoled -> "AMOLED"
            else -> "Dark"
        }
        viewModel.updateState { it.copy(selectedTheme = currentThemeStr) }
    }

    val authLauncher = com.creativeblisslabs.nitnemguktasteek.utils.rememberAuthLauncher(
        onSignInSuccess = { user ->
            Toast.makeText(context, "Signed in as ${user.displayName}", Toast.LENGTH_SHORT).show()
            viewModel.updateState { it.copy(page = 3) }
        },
        onSignInError = { error ->
            Toast.makeText(context, "Sign in failed: $error", Toast.LENGTH_LONG).show()
        }
    )

    OnboardingPagerScreen(
        state = state,
        onGoogleSignIn = {
            authLauncher.signInWithGoogle()
        },
        onStateChange = { newState ->
            viewModel.updateState { newState }

            // Apply theme live
            when (newState.selectedTheme) {
                "Light" -> {
                    AppTheme.themeMode = ThemeMode.Light
                    AppTheme.accentStyle = AccentStyle.Emerald
                }
                "Dark" -> {
                    AppTheme.themeMode = ThemeMode.Dark
                    AppTheme.accentStyle = AccentStyle.Sunset
                }
                "AMOLED" -> {
                    AppTheme.themeMode = ThemeMode.Amoled
                    AppTheme.accentStyle = AccentStyle.Royal
                }
            }
        },
        onNext = {
            scope.launch(Dispatchers.IO) {
                val prefs = AppPreferences(context)
                val settings = SettingsRepository(context, prefs)
                val localPrefs = LocalPreferences(context)

                // Onboarding & daily streak suppression
                localPrefs.markDailyStreakShownToday()

                // Display language
                val displayLanguage = when (state.selectedLanguage) {
                    "Punjabi" -> DisplayLanguage.Punjabi
                    "Hindi" -> DisplayLanguage.Hindi
                    else -> DisplayLanguage.English
                }

                // Theme configuration
                var darkMode = false
                var amoledMode = false
                when (state.selectedTheme) {
                    "Light" -> {
                        darkMode = false
                        amoledMode = false
                    }
                    "Dark" -> {
                        darkMode = true
                        amoledMode = false
                    }
                    "AMOLED" -> {
                        darkMode = true
                        amoledMode = true
                    }
                }

                // Accent style configuration
                val accentStyle = when (state.selectedTheme) {
                    "AMOLED" -> AccentStyle.Royal
                    "Dark" -> AccentStyle.Sunset
                    else -> AccentStyle.Emerald
                }

                // Batch save settings
                settings.saveOnboardingSettings(
                    language = displayLanguage,
                    darkMode = darkMode,
                    amoledMode = amoledMode,
                    accent = accentStyle,
                    notifications = true,
                    amritTime = state.amritTime,
                    rehrasTime = state.rehrasTime,
                    sohilaTime = state.sohilaTime,
                    autoScroll = true,
                    repeatMode = "OFF",
                    ambientMode = false,
                    widgetTheme = state.widgetTheme,
                    widgetContent = state.widgetContent,
                    showGurmukhi = state.showGurmukhi,
                    showDevanagari = state.showDevanagariTransliteration,
                    showRoman = state.showRomanTransliteration,
                    showEnglish = state.showEnglishTranslation,
                    showPunjabi = state.showPunjabiTranslation,
                    showWordMeaning = state.showWordMeaning,
                    larivaarMode = state.larivaarMode
                )

                // Write widget prefs
                context.getSharedPreferences("nitnem_widget_state", Context.MODE_PRIVATE)
                    .edit()
                    .putString("widget_theme", state.widgetTheme)
                    .putString("widget_content", state.widgetContent)
                    .apply()

                // Schedule notifications
                PrayerReminderScheduler.schedulePrayerReminders(
                    context = context,
                    amritTime = state.amritTime,
                    rehrasTime = state.rehrasTime,
                    sohilaTime = state.sohilaTime
                )

                // Finish on main thread
                withContext(Dispatchers.Main) {
                    onFinish()
                }
            }
        }
    )
}