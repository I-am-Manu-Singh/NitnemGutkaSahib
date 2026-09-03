package org.audiocular.nitnem.utils

import androidx.compose.runtime.Composable

data class NitnemAuthUser(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String = ""
)

interface AuthLauncher {
    val currentUser: NitnemAuthUser?
    fun signInWithGoogle()
    fun signOut()
}

@Composable
expect fun rememberAuthLauncher(
    onSignInSuccess: (NitnemAuthUser) -> Unit = {},
    onSignInError: (String) -> Unit = {}
): AuthLauncher
