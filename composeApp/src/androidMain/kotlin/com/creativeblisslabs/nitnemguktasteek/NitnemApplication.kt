package com.creativeblisslabs.nitnemguktasteek

import android.app.Application
import android.content.pm.ApplicationInfo
import android.util.Log

import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.creativeblisslabs.nitnemguktasteek.domain.billing.RevenueCatBillingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure
/**
 * Main Application class for Nitnem Gutka Steek.
 *
 * Initializes:
 * - Firebase
 * - Firebase App Check
 * - Firestore offline persistence
 * - RevenueCat KMP Billing
 */

class NitnemApplication : Application() {

    override fun onCreate() {

        super.onCreate()

        initFirebase()

        initFirebaseAppCheck()

        initFirestore()

        initRevenueCat()

        initFirestoreAudioCatalog()
    }

    private fun initFirestoreAudioCatalog() {
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                com.creativeblisslabs.nitnemguktasteek.auth.NitnemAuthManager.fetchFirestoreAudioCatalog()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Firestore audio catalog: ${e.message}")
            }
        }
    }

 private fun initRevenueCat() {
    try {
        val isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        
        // Configure the RevenueCat KMP SDK globally
        Purchases.configure(
            apiKey = BuildConfig.REVENUECAT_GOOGLE_API_KEY
        ) {
            appUserId = null // Uses anonymous ID generation by default
        }
        
        if (isDebug) {
            Purchases.logLevel = LogLevel.DEBUG
        }

        RevenueCatBillingManager().initialize(debug = isDebug)
        Log.d(TAG, "RevenueCat initialized successfully")
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize RevenueCat: ${e.message}", e)
    }
}

    private fun initFirebase() {

        try {

            FirebaseApp.initializeApp(this)

            Log.d(
                TAG,
                "Firebase initialized successfully"
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed to initialize Firebase: ${e.message}",
                e
            )
        }
    }

    private fun initFirestore() {

        try {

            val firestore =
                FirebaseFirestore.getInstance()

            val settings =
                FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .setCacheSizeBytes(
                        100L * 1024L * 1024L
                    )
                    .build()

            firestore.firestoreSettings =
                settings

            Log.d(
                TAG,
                "Firestore initialized with 100MB offline cache"
            )

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed to initialize Firestore: ${e.message}",
                e
            )
        }
    }

    private fun initFirebaseAppCheck() {

        try {

            val firebaseAppCheck =
                FirebaseAppCheck.getInstance()

            val isDebug =
                (
                    applicationInfo.flags and
                        ApplicationInfo.FLAG_DEBUGGABLE
                    ) != 0

            if (isDebug) {

                firebaseAppCheck
                    .installAppCheckProviderFactory(
                        DebugAppCheckProviderFactory
                            .getInstance()
                    )

                Log.d(
                    TAG,
                    "Firebase App Check: DEBUG provider"
                )

            } else {

                firebaseAppCheck
                    .installAppCheckProviderFactory(
                        PlayIntegrityAppCheckProviderFactory
                            .getInstance()
                    )

                Log.d(
                    TAG,
                    "Firebase App Check: Play Integrity"
                )
            }

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Failed to initialize Firebase App Check: ${e.message}",
                e
            )
        }
    }

    companion object {

        private const val TAG =
            "NitnemApplication"
    }
}