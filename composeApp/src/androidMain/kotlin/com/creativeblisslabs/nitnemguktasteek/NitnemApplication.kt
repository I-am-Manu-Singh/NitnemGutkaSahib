package com.creativeblisslabs.nitnemguktasteek

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

/**
 * Main Application class for Nitnem Gutka Sahib.
 * Configures Firebase, Play Integrity API, and Firebase App Check attestation.
 */
class NitnemApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initFirebaseAppCheck()
    }

    /**
     * Initializes Firebase App Check with Google Play Integrity in production,
     * and DebugAppCheckProviderFactory during local debugging.
     */
    private fun initFirebaseAppCheck() {
        try {
            FirebaseApp.initializeApp(this)
            val firebaseAppCheck = FirebaseAppCheck.getInstance()

            val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
            if (isDebug) {
                // Debug provider for local emulator and development testing
                firebaseAppCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
                )
                Log.d(TAG, "Firebase App Check initialized with DebugAppCheckProviderFactory")
            } else {
                // Google Play Integrity API for release and production distribution
                firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance()
                )
                Log.d(TAG, "Firebase App Check initialized with PlayIntegrityAppCheckProviderFactory")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase App Check: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "NitnemApplication"
    }
}
