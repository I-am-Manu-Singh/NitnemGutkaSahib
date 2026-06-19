package org.audiocular.nitnem

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import org.audiocular.nitnem.app.models.AppTab
import org.audiocular.nitnem.app.models.ExperienceTab
import org.audiocular.nitnem.app.models.DisplayLanguage
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.collectLatest
import org.audiocular.nitnem.app.NitnemApp
import org.audiocular.nitnem.app.notifications.PrayerReminderScheduler
import org.audiocular.nitnem.app.ui.onboarding.OnboardingRoute
import org.audiocular.nitnem.app.ui.screens.SplashRoute
import org.audiocular.nitnem.app.ui.theme.AccentStyle
import org.audiocular.nitnem.app.ui.theme.AppTheme
import org.audiocular.nitnem.app.ui.theme.ThemeMode
import org.audiocular.nitnem.data.analytics.AnalyticsRepository
import org.audiocular.nitnem.data.bookmarks.BookmarksRepository
import org.audiocular.nitnem.data.playback.PlaybackRepository
import org.audiocular.nitnem.data.preferences.SettingsRepository
import org.audiocular.nitnem.notifications.NitnemNotificationChannels
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import android.app.Activity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
class MainActivity : ComponentActivity() {

    private val _notificationIntent = mutableStateOf<Intent?>(null)
    val notificationIntent: State<Intent?> = _notificationIntent

    fun clearNotificationIntent() {
        _notificationIntent.value = null
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        _notificationIntent.value = intent
    }

    private val notificationPermissionLauncher =

        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }
override fun onCreate(
    savedInstanceState: Bundle?
) {

    super.onCreate(savedInstanceState)
    _notificationIntent.value = intent

    /*
     * HIDE STATUS BAR
     */

    WindowCompat.setDecorFitsSystemWindows(
        window,
        false
    )

    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).let { controller ->

        controller.hide(
            WindowInsetsCompat.Type.statusBars()
        )

        controller.systemBarsBehavior =

            WindowInsetsControllerCompat
                .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    /*
     * NOTIFICATION CHANNELS
     */

    NitnemNotificationChannels
        .create(this)

    NitnemNotificationChannels
        .createAudioChannel(this)

    requestNotificationPermissionIfNeeded()

    setContent {

        MainRoot()
    }
}

    private fun requestNotificationPermissionIfNeeded() {

        if (
            Build.VERSION.SDK_INT <
            Build.VERSION_CODES.TIRAMISU
        ) {

            return
        }

        val granted =

            ContextCompat.checkSelfPermission(

                this,

                Manifest.permission.POST_NOTIFICATIONS

            ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {

            notificationPermissionLauncher.launch(

                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }
}

/*
 * ROUTES
 */

private enum class AppRoute {

    Splash,
    Onboarding,
    Home
}

@Composable
private fun MainRoot() {

    val context =
        LocalContext.current

    /*
     * REPOSITORIES
     */

    val settingsRepository = remember {

        SettingsRepository(context)
    }

    val analyticsRepository = remember {

        AnalyticsRepository(context)
    }

    val playbackRepository = remember {

        PlaybackRepository(context)
    }

    val bookmarksRepository = remember {

        BookmarksRepository(context)
    }

    /*
     * VIEWMODEL
     */

    val viewModel = remember {

        NitnemViewModel(

            settingsRepository =
                settingsRepository,

            analyticsRepository =
                analyticsRepository,

            playbackRepository =
                playbackRepository,

            bookmarksRepository =
                bookmarksRepository
        )
    }

    val mainActivity = context as? MainActivity
    val notificationIntent by (mainActivity?.notificationIntent ?: remember { mutableStateOf(null) })

    LaunchedEffect(notificationIntent) {
        val intent = notificationIntent
        if (intent != null) {
            val targetTab = intent.getStringExtra("target_tab")
            if (targetTab != null) {
                runCatching {
                    viewModel.selectTab(AppTab.valueOf(targetTab))
                }
                mainActivity?.clearNotificationIntent()
            }

            val baniId = intent.getStringExtra("bani_id")
            if (baniId != null) {
                viewModel.selectBani(baniId)
                viewModel.selectTab(AppTab.Experience)

                val action = intent.getStringExtra("action")
                if (action == "LISTEN") {
                    viewModel.selectExperienceTab(ExperienceTab.Listen)
                } else if (action == "READ") {
                    viewModel.selectExperienceTab(ExperienceTab.Read)
                }

                mainActivity?.clearNotificationIntent()
            }
        }
    }

    /*
     * ROUTING
     */

    var currentRoute by remember {

        mutableStateOf(
            AppRoute.Splash
        )
    }

    /*
     * LOAD THEME + ACCENT
     */

    var displayLanguage by remember {
        mutableStateOf(DisplayLanguage.English)
    }

    LaunchedEffect(Unit) {

        settingsRepository
            .preferencesFlow
            .collectLatest { settings ->

                displayLanguage = settings.displayLanguage

                /*
                 * ACCENT
                 */

                AppTheme.accentStyle =

                    runCatching {

                        AccentStyle.valueOf(
                            settings.accentStyle
                        )

                    }.getOrDefault(
                        AccentStyle.Emerald
                    )

                /*
                 * THEME
                 */

                AppTheme.themeMode = when {

                    settings.amoledMode -> {
                        ThemeMode.Amoled
                    }

                    settings.darkMode -> {
                        ThemeMode.Dark
                    }

                    else -> {
                        ThemeMode.Light
                    }
                }
            }
    }

    /*
     * ROUTES
     */

    when (currentRoute) {

        AppRoute.Splash -> {

            SplashRoute(

                displayLanguage = displayLanguage,

                onOpenHome = {

                    currentRoute =
                        AppRoute.Home
                },

                onOpenOnboarding = {

                    currentRoute =
                        AppRoute.Onboarding
                }
            )
        }

        AppRoute.Onboarding -> {

            OnboardingRoute(

                onFinish = {

                    /*
                     * SAVE ONBOARDING
                     */

                    viewModel
                        .completeOnboarding()

                    /*
                     * SCHEDULE REMINDERS
                     */

                    PrayerReminderScheduler
                        .schedule(context)

                    /*
                     * OPEN HOME
                     */

                    currentRoute =
                        AppRoute.Home
                }
            )
        }

        AppRoute.Home -> {

            NitnemApp(
                viewModel = viewModel
            )
        }
    }
}