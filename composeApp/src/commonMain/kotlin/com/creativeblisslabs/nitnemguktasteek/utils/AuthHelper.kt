package com.creativeblisslabs.nitnemguktasteek.utils

import androidx.compose.runtime.Composable
import com.creativeblisslabs.nitnemguktasteek.data.RemoteAssetTrack

data class NitnemAuthUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String = ""
)

data class NitnemCloudProfile(
    val userId: String,
    val displayName: String,
    val email: String,
    val totalXp: Int = 0,
    val streakDays: Int = 0,
    val weeklyStreak: Int = 0,
    val monthlyStreak: Int = 0,
    val completedBanisCount: Int = 0,
    val completedBanisToday: List<String> = emptyList(),
    val totalRecitationMinutes: Int = 0,
    val quizScore: Int = 0,
    val malaMantraCount: Int = 0
)

interface AuthLauncher {
    val currentUser: NitnemAuthUser?
    fun signInWithGoogle()
    fun signOut()
    suspend fun fetchAudioCatalog(): List<RemoteAssetTrack> = emptyList()
    suspend fun restoreUserProfile(userId: String): NitnemCloudProfile? = null
    suspend fun syncDailyHistory(
        userId: String,
        date: String,
        completedBanis: List<String>,
        readingMinutes: Int,
        listeningMinutes: Int
    ): Boolean = false
}

@Composable
expect fun rememberAuthLauncher(
    onSignInSuccess: (NitnemAuthUser) -> Unit = {},
    onSignInError: (String) -> Unit = {}
): AuthLauncher

