package com.studioadriatic.gpgs.leaderboards

import android.app.Activity
import android.util.Log
import com.google.android.gms.games.PlayGames
import com.studioadriatic.gpgs.utils.AuthenticationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LeaderboardsController(
    private val activity: Activity,
    private val leaderBoardsListener: LeaderBoardsListener
) {

    companion object {
        const val RC_LEADERBOARD_UI = 9004
    }

    fun submitScore(leaderboardId: String, score: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) {
                leaderBoardsListener.onLeaderBoardScoreSubmittingFailed(leaderboardId)
                return@launch
            }

            try {
                PlayGames.getLeaderboardsClient(activity).submitScore(leaderboardId, score.toLong())
                leaderBoardsListener.onLeaderBoardScoreSubmitted(leaderboardId)
            } catch (e: Exception) {
                Log.e("godot", "Failed to submit score for leaderboard: $leaderboardId", e)
                leaderBoardsListener.onLeaderBoardScoreSubmittingFailed(leaderboardId)
            }
        }
    }

    fun showLeaderboard(leaderboardId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) return@launch

            try {
                val intent = PlayGames.getLeaderboardsClient(activity).getLeaderboardIntent(leaderboardId).await()
                activity.startActivityForResult(intent, RC_LEADERBOARD_UI)
            } catch (e: Exception) {
                Log.e("godot", "Failed to show leaderboard: $leaderboardId", e)
            }
        }
    }

    fun showAllLeaderboards() {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) return@launch

            try {
                val intent = PlayGames.getLeaderboardsClient(activity).allLeaderboardsIntent.await()
                activity.startActivityForResult(intent, RC_LEADERBOARD_UI)
            } catch (e: Exception) {
                Log.e("godot", "Failed to show all leaderboards", e)
            }
        }
    }
}
