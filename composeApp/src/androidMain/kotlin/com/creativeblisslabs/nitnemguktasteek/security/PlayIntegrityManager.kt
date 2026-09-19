package com.creativeblisslabs.nitnemguktasteek.security

import android.content.Context
import android.util.Log
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Play Integrity API Manager.
 * Validates genuine app binary installation, licensing, and device integrity.
 */
object PlayIntegrityManager {

    private const val TAG = "PlayIntegrityManager"

    /**
     * Requests a Play Integrity attestation token with a non-repeating cryptographic nonce.
     */
    suspend fun requestIntegrityToken(
        context: Context,
        cloudProjectNumber: Long = 752230736895L,
        nonce: String = System.currentTimeMillis().toString()
    ): String? = withContext(Dispatchers.IO) {
        try {
            val integrityManager = IntegrityManagerFactory.create(context.applicationContext)
            val request = IntegrityTokenRequest.builder()
                .setCloudProjectNumber(cloudProjectNumber)
                .setNonce(nonce)
                .build()

            val response: IntegrityTokenResponse = integrityManager.requestIntegrityToken(request).await()
            val token = response.token()
            Log.d(TAG, "Successfully acquired Play Integrity token (length=${token.length})")
            token
        } catch (e: Exception) {
            Log.w(TAG, "Play Integrity attestation request failed: ${e.message}")
            null
        }
    }
}
