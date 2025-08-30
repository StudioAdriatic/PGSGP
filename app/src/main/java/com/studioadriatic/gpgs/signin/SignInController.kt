package com.studioadriatic.gpgs.signin

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.PlayGames
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInController(
    private var activity: Activity,
    private var signInListener: SignInListener
) {

    companion object {
        const val RC_SIGN_IN = 77
    }

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(activity, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)

    fun signIn() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val gamesSignInClient = PlayGames.getGamesSignInClient(activity)
                val authResult = gamesSignInClient.isAuthenticated.await()
                
                if (authResult.isAuthenticated) {
                    Log.i("godot", "Using cached signin data")
                    handleSuccessfulSignIn()
                } else {
                    Log.i("godot", "Using new signin data")
                    val signInResult = gamesSignInClient.signIn().await()
                    if (signInResult.isAuthenticated) {
                        handleSuccessfulSignIn()
                    } else {
                        signInListener.onSignInFailed(-1)
                    }
                }
            } catch (e: Exception) {
                Log.e("godot", "Sign in failed", e)
                signInListener.onSignInFailed(e.hashCode())
            }
        }
    }

    private fun handleSuccessfulSignIn() {
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        val userProfile = UserProfile(
            lastSignedInAccount?.displayName,
            lastSignedInAccount?.email,
            lastSignedInAccount?.idToken,
            lastSignedInAccount?.id
        )
        signInListener.onSignedInSuccessfully(userProfile)
    }

    fun onSignInActivityResult(data: Intent?) {
        // This method is deprecated and should not be used with the modern sign-in flow
        // The new automatic sign-in flow in signIn() method handles everything
        Log.w("godot", "onSignInActivityResult is deprecated - using automatic sign-in flow instead")
        signIn()
    }

    fun signOut() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                googleSignInClient.signOut().await()
                signInListener.onSignOutSuccess()
            } catch (e: Exception) {
                Log.e("godot", "Sign out failed", e)
                signInListener.onSignOutFailed()
            }
        }
    }

    fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(activity) != null
    }

}
