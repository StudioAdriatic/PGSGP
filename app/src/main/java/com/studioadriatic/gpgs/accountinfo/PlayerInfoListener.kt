package com.studioadriatic.gpgs.accountinfo

interface PlayerInfoListener {
    fun onPlayerInfoLoadingFailed()
    fun onPlayerInfoLoaded(response: String)
}
