package com.studioadriatic.gpgs.signin

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
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
    private var isUserAuthenticated: Boolean = false
    private var currentUserProfile: UserProfile? = null

    fun signIn() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val gamesSignInClient = PlayGames.getGamesSignInClient(activity)
                val authResult = gamesSignInClient.isAuthenticated.await()
                
                if (authResult.isAuthenticated) {
                    Log.i("godot", "Using cached signin data")
                    isUserAuthenticated = true
                    handleSuccessfulSignIn()
                } else {
                    Log.i("godot", "Using new signin data")
                    val signInResult = gamesSignInClient.signIn().await()
                    if (signInResult.isAuthenticated) {
                        isUserAuthenticated = true
                        handleSuccessfulSignIn()
                    } else {
                        isUserAuthenticated = false
                        currentUserProfile = null
                        signInListener.onSignInFailed(-1)
                    }
                }
            } catch (e: Exception) {
                Log.e("godot", "Sign in failed", e)
                isUserAuthenticated = false
                currentUserProfile = null
                signInListener.onSignInFailed(e.hashCode())
            }
        }
    }

    private fun handleSuccessfulSignIn() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Use Play Games Services to get player info instead of deprecated GoogleSignIn
                val playersClient = PlayGames.getPlayersClient(activity)
                val player = playersClient.currentPlayer.await()
                
                val userProfile = UserProfile(
                    player.displayName,
                    null, // Email is not available through Play Games Services
                    null, // ID token is not available through Play Games Services
                    player.playerId
                )
                
                currentUserProfile = userProfile
                signInListener.onSignedInSuccessfully(userProfile)
            } catch (e: Exception) {
                Log.e("godot", "Failed to get player info", e)
                // Fallback to basic profile
                val userProfile = UserProfile(null, null, null, null)
                currentUserProfile = userProfile
                signInListener.onSignedInSuccessfully(userProfile)
            }
        }
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
                isUserAuthenticated = false
                currentUserProfile = null
                signInListener.onSignOutSuccess()
            } catch (e: Exception) {
                Log.e("godot", "Sign out failed", e)
                signInListener.onSignOutFailed()
            }
        }
    }

    fun isSignedIn(): Boolean {
        return isUserAuthenticated
    }

    // Method to check authentication status using Play Games Services
    fun checkAuthenticationStatus() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val gamesSignInClient = PlayGames.getGamesSignInClient(activity)
                val authResult = gamesSignInClient.isAuthenticated.await()
                isUserAuthenticated = authResult.isAuthenticated
                
                if (!isUserAuthenticated) {
                    currentUserProfile = null
                }
                
                Log.i("godot", "Authentication status checked: $isUserAuthenticated")
            } catch (e: Exception) {
                Log.e("godot", "Failed to check authentication status", e)
                isUserAuthenticated = false
                currentUserProfile = null
            }
        }
    }

}
