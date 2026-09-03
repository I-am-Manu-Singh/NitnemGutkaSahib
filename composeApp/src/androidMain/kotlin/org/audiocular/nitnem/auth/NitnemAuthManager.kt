package org.audiocular.nitnem.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

data class NitnemUserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val streakDays: Int = 0,
    val totalXp: Int = 0,
    val completedBanisCount: Int = 0,
    val totalRecitationMinutes: Int = 0,
    val photoUrl: String = ""
)

object NitnemAuthManager {
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    val isLoggedIn: Boolean get() = auth.currentUser != null

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("752230736895-mbdu13l4tfdtq9fg5q204vlihnonjffi.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun signInWithGoogleIdToken(idToken: String): Result<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user ?: return@withContext Result.failure(Exception("Firebase user is null"))
            
            // Auto restore or initialize user profile
            val existingProfile = restoreUserData(user.uid)
            val fallbackName = user.displayName?.takeIf { it.isNotBlank() }
                ?: user.email?.substringBefore('@')?.replace(".", " ")?.replace("_", " ")?.capitalizeWords()
                ?: "Gurmukh"
            if (existingProfile == null) {
                val newProfile = NitnemUserProfile(
                    uid = user.uid,
                    displayName = fallbackName,
                    email = user.email ?: "",
                    photoUrl = user.photoUrl?.toString() ?: ""
                )
                saveUserData(newProfile)
            }
            Result.success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } }

    suspend fun restoreUserData(userId: String): NitnemUserProfile? = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("users").document(userId).get().await()
            if (!doc.exists()) return@withContext null
            
            val user = auth.currentUser
            val fallbackName = user?.displayName?.takeIf { it.isNotBlank() }
                ?: user?.email?.substringBefore('@')?.replace(".", " ")?.replace("_", " ")?.capitalizeWords()
                ?: "Gurmukh"

            NitnemUserProfile(
                uid = userId,
                displayName = doc.getString("displayName")?.takeIf { it.isNotBlank() } ?: fallbackName,
                email = doc.getString("email") ?: (user?.email ?: ""),
                streakDays = doc.getLong("currentStreak")?.toInt() ?: 0,
                totalXp = doc.getLong("xp")?.toInt() ?: 0,
                completedBanisCount = doc.getLong("completedBanisCount")?.toInt() ?: 0,
                totalRecitationMinutes = doc.getLong("totalRecitationMinutes")?.toInt() ?: 0,
                photoUrl = user?.photoUrl?.toString() ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveUserData(profile: NitnemUserProfile): Boolean = withContext(Dispatchers.IO) {
        try {
            val map = hashMapOf(
                "userId" to profile.uid,
                "displayName" to profile.displayName,
                "email" to profile.email,
                "currentStreak" to profile.streakDays,
                "xp" to profile.totalXp,
                "completedBanisCount" to profile.completedBanisCount,
                "totalRecitationMinutes" to profile.totalRecitationMinutes,
                "lastActiveTimestamp" to System.currentTimeMillis()
            )
            firestore.collection("users").document(profile.uid).set(map, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun deleteAccountAndCloudData(): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext false
            val uid = user.uid
            
            // Delete Firestore user document
            firestore.collection("users").document(uid).delete().await()
            
            // Delete Firebase Auth User
            user.delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
