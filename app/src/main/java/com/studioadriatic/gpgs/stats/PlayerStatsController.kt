package com.studioadriatic.gpgs.stats

import android.app.Activity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.PlayGames
import com.google.gson.Gson
import com.studioadriatic.gpgs.model.PlayerStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PlayerStatsController(
    private val activity: Activity,
    private val playerStatsListener: PlayerStatsListener
) {

    private fun isSignedIn(): Boolean = GoogleSignIn.getLastSignedInAccount(activity) != null

    fun checkPlayerStats(forceRefresh: Boolean) {
        if (!isSignedIn()) {
            playerStatsListener.onPlayerStatsLoadingFailed()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = PlayGames.getPlayerStatsClient(activity).loadPlayerStats(forceRefresh).await()
                val stats = result?.get()

                if (stats != null) {
                    val playerStats = PlayerStats(
                        avgSessionLength = stats.averageSessionLength.toDouble(),
                        daysSinceLastPlayed = stats.daysSinceLastPlayed,
                        numberOfPurchases = stats.numberOfPurchases,
                        numberOfSessions = stats.numberOfSessions,
                        sessionPercentile = stats.sessionPercentile.toDouble(),
                        spendPercentile = stats.spendPercentile.toDouble()
                    )

                    playerStatsListener.onPlayerStatsLoaded(Gson().toJson(playerStats))
                } else {
                    playerStatsListener.onPlayerStatsLoadingFailed()
                }
            } catch (e: Exception) {
                Log.e("godot", "Failed to load player stats", e)
                playerStatsListener.onPlayerStatsLoadingFailed()
            }
        }
    }
}
