package com.creativeblisslabs.nitnemguktasteek.auth

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
    val completedBanisToday: List<String> = emptyList(),
    val quizScore: Int = 0,
    val malaMantraCount: Int = 0,
    val photoUrl: String = ""
)

object NitnemAuthManager {
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser

    val isLoggedIn: Boolean get() = auth.currentUser != null

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(com.creativeblisslabs.nitnemguktasteek.security.StringObfuscator.webClientId())
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

    /**
     * Upload / Upsert complete audio & timing catalog from RemoteAssetCatalog to Firestore table `audio_catalog`
     */
    suspend fun uploadCompleteCatalogToFirestore(): Boolean = withContext(Dispatchers.IO) {
        try {
            val allTracks = com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.ALL_REMOTE_ASSETS
            val collectionName = com.creativeblisslabs.nitnemguktasteek.security.StringObfuscator.audioCatalogCollection()
            android.util.Log.d("FirestoreCatalog", "Uploading catalog (${allTracks.size} tracks) to collection: $collectionName")
            
            // Chunk into batches of 15 items to avoid transaction limits and timeouts
            val chunks = allTracks.chunked(15)
            for (chunk in chunks) {
                val batch = firestore.batch()
                chunk.forEach { track ->
                    val docRef = firestore.collection(collectionName).document(track.id)
                    val map = hashMapOf(
                        "id" to track.id,
                        "baniId" to track.baniId,
                        "baniTitle" to track.baniTitle,
                        "artist" to track.artist,
                        "audioFileName" to track.audioFileName,
                        "timingFileName" to (track.timingFileName ?: ""),
                        "audioUrl" to track.rawAudioUrl(),
                        "timingUrl" to (track.rawTimingUrl() ?: ""),
                        "durationMs" to track.durationMs,
                        "approxSizeBytes" to track.approxSizeBytes,
                        "isPrimaryNitnem" to track.isPrimaryNitnem,
                        "license" to "Public Domain / Gurmat Sangeet",
                        "attribution" to "Gurmat Sangeet"
                    )
                    batch.set(docRef, map, SetOptions.merge())
                }
                batch.commit().await()
            }
            android.util.Log.d("FirestoreCatalog", "Successfully populated /audio_catalog collection in Firestore")
            true
        } catch (e: Exception) {
            android.util.Log.e("FirestoreCatalog", "Error uploading audio catalog to Firestore: ${e.message}", e)
            false
        }
    }

    /**
     * Fetch complete audio & timing catalog dynamically from Firestore table `audio_catalog`
     */
    suspend fun fetchFirestoreAudioCatalog(): List<com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetTrack> = withContext(Dispatchers.IO) {
        try {
            val collectionName = com.creativeblisslabs.nitnemguktasteek.security.StringObfuscator.audioCatalogCollection()
            var snapshot = firestore.collection(collectionName).get().await()
            
            if (snapshot.isEmpty) {
                android.util.Log.d("FirestoreCatalog", "Collection $collectionName is empty. Initializing upload...")
                uploadCompleteCatalogToFirestore()
                snapshot = firestore.collection(collectionName).get().await()
            }

            if (!snapshot.isEmpty) {
                val fetched = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getString("id") ?: doc.id
                    val baniId = doc.getString("baniId") ?: return@mapNotNull null
                    val baniTitle = doc.getString("baniTitle") ?: ""
                    val artist = doc.getString("artist") ?: ""
                    val audioFileName = doc.getString("audioFileName") ?: ""
                    val timingFileName = doc.getString("timingFileName")?.takeIf { it.isNotBlank() }
                    val audioUrl = doc.getString("audioUrl")
                    val timingUrl = doc.getString("timingUrl")?.takeIf { it.isNotBlank() }
                    val durationMs = doc.getLong("durationMs") ?: 0L
                    val approxSizeBytes = doc.getLong("approxSizeBytes") ?: 0L
                    val isPrimary = doc.getBoolean("isPrimaryNitnem") ?: false

                    com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetTrack(
                        id = id,
                        baniId = baniId,
                        baniTitle = baniTitle,
                        artist = artist,
                        audioFileName = audioFileName,
                        timingFileName = timingFileName,
                        audioUrl = audioUrl,
                        timingUrl = timingUrl,
                        durationMs = durationMs,
                        approxSizeBytes = approxSizeBytes,
                        isPrimaryNitnem = isPrimary
                    )
                }
                if (fetched.isNotEmpty()) {
                    android.util.Log.d("FirestoreCatalog", "Loaded ${fetched.size} tracks from Firestore")
                    return@withContext fetched
                }
            }
            com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.ALL_REMOTE_ASSETS
        } catch (e: Exception) {
            android.util.Log.e("FirestoreCatalog", "Failed fetching dynamic catalog from Firestore: ${e.message}", e)
            com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetCatalog.ALL_REMOTE_ASSETS
        }
    }

    suspend fun restoreUserData(userId: String): NitnemUserProfile? = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("users").document(userId).get().await()
            if (!doc.exists()) return@withContext null
            
            val user = auth.currentUser
            val fallbackName = user?.displayName?.takeIf { it.isNotBlank() }
                ?: user?.email?.substringBefore('@')?.replace(".", " ")?.replace("_", " ")?.capitalizeWords()
                ?: "Gurmukh"

            val completedBanisToday = (doc.get("completedBanisToday") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

            NitnemUserProfile(
                uid = userId,
                displayName = doc.getString("displayName")?.takeIf { it.isNotBlank() } ?: fallbackName,
                email = doc.getString("email") ?: (user?.email ?: ""),
                streakDays = doc.getLong("currentStreak")?.toInt() ?: 0,
                totalXp = doc.getLong("xp")?.toInt() ?: 0,
                completedBanisCount = doc.getLong("completedBanisCount")?.toInt() ?: 0,
                totalRecitationMinutes = doc.getLong("totalRecitationMinutes")?.toInt() ?: 0,
                completedBanisToday = completedBanisToday,
                quizScore = doc.getLong("quizScore")?.toInt() ?: 0,
                malaMantraCount = doc.getLong("malaMantraCount")?.toInt() ?: 0,
                photoUrl = user?.photoUrl?.toString() ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun syncDailyHistory(userId: String, date: String, completedBanis: List<String>, readingMinutes: Int, listeningMinutes: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val historyMap = hashMapOf(
                "date" to date,
                "completedBanis" to completedBanis,
                "readingMinutes" to readingMinutes,
                "listeningMinutes" to listeningMinutes,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(userId)
                .collection("daily_history")
                .document(date)
                .set(historyMap, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
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
                "completedBanisToday" to profile.completedBanisToday,
                "totalRecitationMinutes" to profile.totalRecitationMinutes,
                "quizScore" to profile.quizScore,
                "malaMantraCount" to profile.malaMantraCount,
                "lastActiveTimestamp" to System.currentTimeMillis()
            )
            firestore.collection("users").document(profile.uid).set(map, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Performs a bidirectional union-merge between local bookmarks and Firestore cloud bookmarks.
     * Guarantees zero bookmark loss upon signing in on multiple devices.
     */
    suspend fun mergeAndSyncBookmarks(userId: String, localBookmarks: Set<String>): Set<String> = withContext(Dispatchers.IO) {
        try {
            val cloudBookmarks = fetchCloudBookmarks(userId) ?: emptySet()
            val mergedBookmarks = localBookmarks + cloudBookmarks
            if (mergedBookmarks != cloudBookmarks) {
                val map = hashMapOf(
                    "bookmarkedLineIds" to mergedBookmarks.toList(),
                    "lastUpdatedTimestamp" to System.currentTimeMillis()
                )
                firestore.collection("users").document(userId).set(map, SetOptions.merge()).await()
            }
            mergedBookmarks
        } catch (e: Exception) {
            e.printStackTrace()
            localBookmarks
        }
    }

    suspend fun syncBookmarks(userId: String, bookmarkLineIds: Set<String>): Boolean = withContext(Dispatchers.IO) {
        try {
            val map = hashMapOf(
                "bookmarkedLineIds" to bookmarkLineIds.toList(),
                "lastUpdatedTimestamp" to System.currentTimeMillis()
            )
            firestore.collection("users").document(userId).set(map, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchCloudBookmarks(userId: String): Set<String>? = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("users").document(userId).get().await()
            if (doc.exists()) {
                val list = doc.get("bookmarkedLineIds") as? List<*>
                list?.mapNotNull { it?.toString() }?.toSet()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
