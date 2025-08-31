package com.studioadriatic.gpgs.achievements

import android.app.Activity
import android.util.Log
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.achievement.Achievement
import com.google.gson.Gson
import com.studioadriatic.gpgs.model.AchievementInfo
import com.studioadriatic.gpgs.utils.AuthenticationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AchievementsController(
    private val activity: Activity,
    private val achievementsListener: AchievementsListener
) {

    companion object {
        const val RC_ACHIEVEMENT_UI = 9003
    }

    fun unlockAchievement(achievementName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) {
                achievementsListener.onAchievementUnlockingFailed(achievementName)
                return@launch
            }

            try {
                PlayGames.getAchievementsClient(activity).unlock(achievementName)
                achievementsListener.onAchievementUnlocked(achievementName)
            } catch (e: Exception) {
                Log.e("godot", "Failed to unlock achievement: $achievementName", e)
                achievementsListener.onAchievementUnlockingFailed(achievementName)
            }
        }
    }

    fun revealAchievement(achievementName: String) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) {
                achievementsListener.onAchievementRevealingFailed(achievementName)
                return@launch
            }

            try {
                PlayGames.getAchievementsClient(activity).reveal(achievementName)
                achievementsListener.onAchievementRevealed(achievementName)
            } catch (e: Exception) {
                Log.e("godot", "Failed to reveal achievement: $achievementName", e)
                achievementsListener.onAchievementRevealingFailed(achievementName)
            }
        }
    }

    fun incrementAchievement(achievementName: String, step: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) {
                achievementsListener.onAchievementIncrementingFailed(achievementName)
                return@launch
            }

            try {
                PlayGames.getAchievementsClient(activity).increment(achievementName, step)
                achievementsListener.onAchievementIncremented(achievementName)
            } catch (e: Exception) {
                Log.e("godot", "Failed to increment achievement: $achievementName", e)
                achievementsListener.onAchievementIncrementingFailed(achievementName)
            }
        }
    }

    fun setAchievementSteps(achievementName: String, steps: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) {
                achievementsListener.onAchievementStepsSettingFailed(achievementName)
                return@launch
            }

            try {
                PlayGames.getAchievementsClient(activity).setSteps(achievementName, steps)
                achievementsListener.onAchievementStepsSet(achievementName)
            } catch (e: Exception) {
                Log.e("godot", "Failed to set achievement steps: $achievementName", e)
                achievementsListener.onAchievementStepsSettingFailed(achievementName)
            }
        }
    }

    fun showAchievements() {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) {
                Log.w("godot", "Cannot show achievements: user not signed in")
                return@launch
            }

            try {
                val intent = PlayGames.getAchievementsClient(activity).achievementsIntent.await()
                activity.startActivityForResult(intent, RC_ACHIEVEMENT_UI)
            } catch (e: Exception) {
                Log.e("godot", "Failed to show achievements", e)
            }
        }
    }

    fun loadAchievementInfo(forceReload: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!AuthenticationHelper.isSignedIn(activity)) {
                achievementsListener.onAchievementInfoLoadingFailed()
                return@launch
            }

            try {
                val result = PlayGames.getAchievementsClient(activity).load(forceReload).await()
                val achievementData = result?.get()

                if (achievementData != null) {
                    val achievementList = achievementData.map { achievement ->
                        val type = achievement.type
                        val isIncremental = type == Achievement.TYPE_INCREMENTAL
                        
                        AchievementInfo(
                            id = achievement.achievementId,
                            name = achievement.name,
                            description = achievement.description,
                            state = achievement.state,
                            type = type,
                            currentSteps = if (isIncremental) achievement.currentSteps else null,
                            totalSteps = if (isIncremental) achievement.totalSteps else null,
                            xp = achievement.xpValue
                        )
                    }

                    achievementsListener.onAchievementInfoLoaded(Gson().toJson(achievementList))
                } else {
                    achievementsListener.onAchievementInfoLoadingFailed()
                }
            } catch (e: Exception) {
                Log.e("godot", "Failed to load achievement info", e)
                achievementsListener.onAchievementInfoLoadingFailed()
            }
        }
    }
}
