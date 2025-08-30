package com.studioadriatic.gpgs.leaderboards

interface LeaderBoardsListener {
    fun onLeaderBoardScoreSubmitted(leaderboardId: String)
    fun onLeaderBoardScoreSubmittingFailed(leaderboardId: String)
}
