package com.studioadriatic.gpgs.utils

import android.app.Activity
import android.util.Log
import com.google.android.gms.games.PlayGames
import kotlinx.coroutines.tasks.await

object AuthenticationHelper {
    
    suspend fun isSignedIn(activity: Activity): Boolean {
        return try {
            val gamesSignInClient = PlayGames.getGamesSignInClient(activity)
            val authResult = gamesSignInClient.isAuthenticated.await()
            authResult.isAuthenticated
        } catch (e: Exception) {
            Log.e("godot", "Failed to check authentication status", e)
            false
        }
    }
}
