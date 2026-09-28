package com.creativeblisslabs.nitnemguktasteek.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appDataStore by
    preferencesDataStore(
        name = "nitnem_app"
    )

class AppPreferences(
    private val context: Context
) {

    companion object {

        /*
         * ONLY CORE APP PREFS
         *
         * Everything else belongs in
         * SettingsRepository.
         *
         * Two sources of truth is how
         * apps become paranormal activity.
         */

        private val ONBOARDING_COMPLETED =
            booleanPreferencesKey(
                "onboarding_completed"
            )
    }

    /*
     * FLOWS
     */

    val onboardingCompleted:
        Flow<Boolean> =

        context.appDataStore.data.map {

            it[ONBOARDING_COMPLETED]
                ?: false
        }

    /*
     * SAVE
     */

    suspend fun setOnboardingCompleted() {

        context.appDataStore.edit {

            it[ONBOARDING_COMPLETED] =
                true
        }
    }

    suspend fun completeOnboarding() {
        setOnboardingCompleted()
    }
}