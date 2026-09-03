package org.audiocular.nitnem.utils

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import org.audiocular.nitnem.auth.NitnemAuthManager

@Composable
actual fun rememberAuthLauncher(
    onSignInSuccess: (NitnemAuthUser) -> Unit,
    onSignInError: (String) -> Unit
): AuthLauncher {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentUserState by remember {
        val user = NitnemAuthManager.currentUser
        mutableStateOf(
            if (user != null) {
                val fallback = user.displayName?.takeIf { it.isNotBlank() }
                    ?: user.email?.substringBefore('@')?.replace(".", " ")?.replace("_", " ")?.split(" ")?.joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
                    ?: "Gurmukh"
                NitnemAuthUser(
                    uid = user.uid,
                    displayName = fallback,
                    email = user.email ?: "",
                    photoUrl = user.photoUrl?.toString() ?: ""
                )
            } else null
        )
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        android.util.Log.d("NitnemAuth", "googleSignInLauncher resultCode: ${result.resultCode}, data: ${result.data}")
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                android.util.Log.d("NitnemAuth", "Account: ${account?.email}, idToken: ${idToken?.take(15)}...")
                if (idToken != null) {
                    scope.launch {
                        val authResult = NitnemAuthManager.signInWithGoogleIdToken(idToken)
                        if (authResult.isSuccess) {
                            val fbUser = authResult.getOrNull()
                            val fallback = account.displayName?.takeIf { it.isNotBlank() }
                                ?: fbUser?.displayName?.takeIf { it.isNotBlank() }
                                ?: account.email?.substringBefore('@')?.replace(".", " ")?.replace("_", " ")?.split(" ")?.joinToString(" ") { it.replaceFirstChar { c -> c.titlecase() } }
                                ?: "Gurmukh"
                            val authUser = NitnemAuthUser(
                                uid = fbUser?.uid ?: account.id ?: "",
                                displayName = fallback,
                                email = account.email ?: fbUser?.email ?: "",
                                photoUrl = account.photoUrl?.toString() ?: ""
                            )
                            currentUserState = authUser
                            onSignInSuccess(authUser)
                        } else {
                            val errorMsg = authResult.exceptionOrNull()?.message ?: "Sign in failed"
                            android.util.Log.e("NitnemAuth", "Firebase auth failed: $errorMsg", authResult.exceptionOrNull())
                            onSignInError(errorMsg)
                        }
                    }
                } else {
                    val msg = "Google Sign-In returned null ID token. Ensure Web Client ID and SHA-1 are configured in Firebase."
                    android.util.Log.e("NitnemAuth", msg)
                    onSignInError(msg)
                }
            } catch (e: ApiException) {
                val msg = "Google Sign-In failed (status code ${e.statusCode}): ${e.message}"
                android.util.Log.e("NitnemAuth", msg, e)
                onSignInError(msg)
            } catch (e: Exception) {
                android.util.Log.e("NitnemAuth", "Sign in exception: ${e.message}", e)
                onSignInError(e.message ?: "Sign in failed")
            }
        } else {
            val msg = "Sign-in cancelled or failed (resultCode: ${result.resultCode})"
            android.util.Log.w("NitnemAuth", msg)
            if (result.data != null) {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    task.getResult(ApiException::class.java)
                } catch (e: ApiException) {
                    val detailedMsg = "Google Sign-In error code ${e.statusCode} (${com.google.android.gms.common.api.CommonStatusCodes.getStatusCodeString(e.statusCode)})"
                    android.util.Log.e("NitnemAuth", detailedMsg, e)
                    onSignInError(detailedMsg)
                    return@rememberLauncherForActivityResult
                } catch (e: Exception) {
                    android.util.Log.e("NitnemAuth", "Error parsing result: ${e.message}", e)
                }
            }
            onSignInError(msg)
        }
    }

    return remember(currentUserState) {
        object : AuthLauncher {
            override val currentUser: NitnemAuthUser?
                get() = currentUserState

            override fun signInWithGoogle() {
                val client = NitnemAuthManager.getGoogleSignInClient(context)
                googleSignInLauncher.launch(client.signInIntent)
            }

            override fun signOut() {
                NitnemAuthManager.signOut()
                currentUserState = null
            }
        }
    }
}
