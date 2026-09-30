package com.creativeblisslabs.nitnemguktasteek

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.creativeblisslabs.nitnemguktasteek.app.models.AppTab
import com.creativeblisslabs.nitnemguktasteek.app.models.ExperienceTab
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.collectLatest
import com.creativeblisslabs.nitnemguktasteek.app.NitnemApp
import com.creativeblisslabs.nitnemguktasteek.app.notifications.PrayerReminderScheduler
import com.creativeblisslabs.nitnemguktasteek.app.ui.onboarding.OnboardingRoute
import com.creativeblisslabs.nitnemguktasteek.app.ui.screens.SplashRoute
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.AccentStyle
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.AppTheme
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.ThemeMode
import com.creativeblisslabs.nitnemguktasteek.data.analytics.AnalyticsRepository
import com.creativeblisslabs.nitnemguktasteek.data.bookmarks.BookmarksRepository
import com.creativeblisslabs.nitnemguktasteek.data.playback.PlaybackRepository
import com.creativeblisslabs.nitnemguktasteek.data.preferences.SettingsRepository
import com.creativeblisslabs.nitnemguktasteek.data.preferences.AppPreferences
import com.creativeblisslabs.nitnemguktasteek.notifications.NitnemNotificationChannels
import com.creativeblisslabs.nitnemguktasteek.viewmodel.NitnemViewModel
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

        /*
         * BILLING INITIALIZATION
         */
        com.creativeblisslabs.nitnemguktasteek.domain.billing.RevenueCatBillingManager.instance.initialize()

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
    val context = LocalContext.current

    val appPreferences = remember { AppPreferences(context) }
    val settingsRepository = remember {
        SettingsRepository(context, appPreferences)
    }

    val analyticsRepository = remember { AnalyticsRepository(context) }
    val playbackRepository = remember { PlaybackRepository(context) }
    val bookmarksRepository = remember { BookmarksRepository(context) }

    val viewModel = remember {
        NitnemViewModel(
            settingsRepository = settingsRepository,
            analyticsRepository = analyticsRepository,
            playbackRepository = playbackRepository,
            bookmarksRepository = bookmarksRepository
        )
    }

    val mainActivity = context as? MainActivity
    val notificationIntent by (mainActivity?.notificationIntent ?: remember { mutableStateOf(null) })

    /*
     * REACTIVE ONBOARDING ROUTING STATE:
     * Collects onboarding status safely from AppPreferences flow.
     */
    val onboardingCompleted by appPreferences.onboardingCompleted.collectAsState(initial = null)
    
    var currentRoute by androidx.compose.runtime.saveable.rememberSaveable {
        mutableStateOf(AppRoute.Splash)
    }

    LaunchedEffect(onboardingCompleted) {
        if (onboardingCompleted == true && currentRoute == AppRoute.Splash) {
            // Already handled by SplashRoute timer, but this ensures consistency if the timer finishes after state loads
        }
    }

    LaunchedEffect(notificationIntent) {
        val intent = notificationIntent
        if (intent != null) {
            val action = intent.getStringExtra("action")
            val baniId = intent.getStringExtra("bani_id")
            val targetTab = intent.getStringExtra("target_tab")

            // Only proceed if this is actually a notification intent with relevant extras.
            // A normal launch intent will have null for these extras.
            if (action == null && baniId == null && targetTab == null) {
                return@LaunchedEffect
            }

            if (action == "SNOOZE_15") {
                val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as? android.app.NotificationManager
                notificationManager?.cancel(1)
                PrayerReminderScheduler.scheduleSnoozeReminder(context, baniId)
                android.widget.Toast.makeText(context, "Reminder snoozed for 15 minutes", android.widget.Toast.LENGTH_SHORT).show()
                mainActivity?.clearNotificationIntent()
                return@LaunchedEffect
            }

            currentRoute = AppRoute.Home

            if (targetTab != null) {
                runCatching {
                    viewModel.selectTab(AppTab.valueOf(targetTab))
                }
            }

            if (baniId != null) {
                viewModel.selectBani(baniId)
                viewModel.selectTab(AppTab.Experience)

                if (action == "LISTEN") {
                    viewModel.selectExperienceTab(ExperienceTab.Listen)
                } else {
                    viewModel.selectExperienceTab(ExperienceTab.Read)
                }
            }

            mainActivity?.clearNotificationIntent()
        }
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

    // ── SYSTEM BARS COLOR & ICON THEME SYNC ──
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            val isDarkTheme = AppTheme.themeMode == ThemeMode.Dark || AppTheme.themeMode == ThemeMode.Amoled
            insetsController.isAppearanceLightStatusBars = !isDarkTheme
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme
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
                    currentRoute = AppRoute.Home
                },
                onOpenOnboarding = {
                    currentRoute = AppRoute.Onboarding
                }
            )
        }

        AppRoute.Onboarding -> {
            OnboardingRoute(
                onFinish = {
                    /*
                     * SAVE ONBOARDING
                     */
                    viewModel.completeOnboarding()

                    /*
                     * SCHEDULE REMINDERS
                     */
                    PrayerReminderScheduler.schedule(context)

                    /*
                     * OPEN HOME & REDIRECT TO SETTINGS TAB
                     */
                    viewModel.selectTab(AppTab.Settings)
                    currentRoute = AppRoute.Home
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