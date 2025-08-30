package com.studioadriatic.gpgs.accountinfo

import android.app.Activity
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.PlayGames
import com.google.gson.Gson
import com.studioadriatic.gpgs.model.PlayerInfo
import com.studioadriatic.gpgs.model.PlayerLevel
import com.studioadriatic.gpgs.model.PlayerLevelInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PlayerInfoController(
    private val activity: Activity,
    private val playerInfoListener: PlayerInfoListener
) {

    private fun isSignedIn(): Boolean = GoogleSignIn.getLastSignedInAccount(activity) != null

    fun fetchPlayerInfo() {
        if (!isSignedIn()) {
            playerInfoListener.onPlayerInfoLoadingFailed()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val player = PlayGames.getPlayersClient(activity).currentPlayer.await()

                if (player != null) {
                    val levelInfo = player.levelInfo
                    val playerLevelInfo = levelInfo?.let {
                        PlayerLevelInfo(
                            currentXpTotal = it.currentXpTotal,
                            lastLevelUpTimestamp = it.lastLevelUpTimestamp,
                            currentLevel = it.currentLevel?.let { level ->
                                PlayerLevel(
                                    levelNumber = level.levelNumber,
                                    minXp = level.minXp,
                                    maxXp = level.maxXp
                                )
                            },
                            nextLevel = it.nextLevel?.let { level ->
                                PlayerLevel(
                                    levelNumber = level.levelNumber,
                                    minXp = level.minXp,
                                    maxXp = level.maxXp
                                )
                            }
                        )
                    }

                    val playerInfo = PlayerInfo(
                        playerId = player.playerId,
                        displayName = player.displayName,
                        name = player.displayName,
                        iconImageUrl = player.iconImageUrl,
                        hiResImageUrl = player.hiResImageUrl,
                        title = player.title,
                        bannerImageLandscapeUrl = player.bannerImageLandscapeUrl,
                        bannerImagePortraitUrl = player.bannerImagePortraitUrl,
                        levelInfo = playerLevelInfo
                    )

                    playerInfoListener.onPlayerInfoLoaded(Gson().toJson(playerInfo))
                } else {
                    playerInfoListener.onPlayerInfoLoadingFailed()
                }
            } catch (e: Exception) {
                Log.e("godot", "Failed to fetch player info", e)
                playerInfoListener.onPlayerInfoLoadingFailed()
            }
        }
    }
}
