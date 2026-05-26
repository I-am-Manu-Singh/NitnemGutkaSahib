package org.audiocular.nitnem.app.ui.onboarding

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import org.audiocular.nitnem.app.notifications.PrayerReminderScheduler
import org.audiocular.nitnem.app.ui.theme.AccentStyle
import org.audiocular.nitnem.app.ui.theme.AppTheme
import org.audiocular.nitnem.app.ui.theme.ThemeMode
import org.audiocular.nitnem.data.preferences.AppPreferences
import org.audiocular.nitnem.data.preferences.SettingsRepository
import org.audiocular.nitnem.app.models.DisplayLanguage

@Composable
fun OnboardingRoute(
    onFinish: () -> Unit
) {

    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val viewModel = remember {
        OnboardingViewModel()
    }

    val state by
        viewModel.state.collectAsState()

    OnboardingPagerScreen(

        state = state,

        onStateChange = { newState ->

       viewModel.updateState {
    newState
}

            /*
             * APPLY THEME LIVE
             *
             * Tiny glowing rectangle simulator
             * finally behaving correctly.
             */

            when (newState.selectedTheme) {

                "Light" -> {

                    AppTheme.themeMode =
                        ThemeMode.Light

                    AppTheme.accentStyle =
                        AccentStyle.Emerald
                }

                "Dark" -> {

                    AppTheme.themeMode =
                        ThemeMode.Dark

                    AppTheme.accentStyle =
                        AccentStyle.Sunset
                }

                "AMOLED" -> {

                    AppTheme.themeMode =
                        ThemeMode.Amoled

                    AppTheme.accentStyle =
                        AccentStyle.Royal
                }
            }
        },

        onNext = {

            /*
             * PAGE FLOW
             */

            if (state.page < 4) {

                viewModel.nextPage()

            } else {

                /*
                 * SAVE EVERYTHING
                 *
                 * Because otherwise onboarding
                 * becomes decorative fiction.
                 */

                scope.launch(kotlinx.coroutines.Dispatchers.IO) {

                    val prefs =
                        AppPreferences(context)

                    val settings =
                        SettingsRepository(context)

                    /*
                     * ONBOARDING
                     */

                    prefs.setOnboardingCompleted()

                    /*
                     * DISPLAY LANGUAGE
                     */

                    val displayLanguage =
                        when (state.selectedLanguage) {

                            "Punjabi" ->
                                DisplayLanguage.Punjabi

                            "Hindi" ->
                                DisplayLanguage.Hindi

                            else ->
                                DisplayLanguage.English
                        }

                    /*
                     * THEME
                     */

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

                    /*
                     * ACCENT STYLE
                     */

                    val accentStyle =
                        when (state.selectedTheme) {

                            "AMOLED" ->
                                AccentStyle.Royal

                            "Dark" ->
                                AccentStyle.Sunset

                            else ->
                                AccentStyle.Emerald
                        }

                    /*
                     * BATCH SAVE SETTINGS
                     */

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
                        repeatMode = false,
                        ambientMode = false,
                        widgetTheme = state.widgetTheme,
                        widgetContent = state.widgetContent
                    )

                    /*
                     * WRITE WIDGET PREFS
                     */

                    context.getSharedPreferences("nitnem_widget_state", android.content.Context.MODE_PRIVATE)
                        .edit()
                        .putString("widget_theme", state.widgetTheme)
                        .putString("widget_content", state.widgetContent)
                        .apply()

                    /*
                     * SCHEDULE NOTIFICATIONS
                     *
                     * Humans love being interrupted
                     * by spiritually themed alarms.
                     */

                    org.audiocular.nitnem.app.notifications.PrayerReminderScheduler.schedulePrayerReminders(
                        context = context,
                        amritTime = state.amritTime,
                        rehrasTime = state.rehrasTime,
                        sohilaTime = state.sohilaTime
                    )

                    /*
                     * FINISH ON MAIN THREAD
                     */

                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        onFinish()
                    }
                }
            }
        }
    )
}