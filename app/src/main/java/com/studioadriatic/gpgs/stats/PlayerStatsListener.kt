package com.studioadriatic.gpgs.stats

interface PlayerStatsListener {
    fun onPlayerStatsLoaded(statsJson: String)
    fun onPlayerStatsLoadingFailed()
}
