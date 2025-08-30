package com.studioadriatic.gpgs

import android.app.Activity
import android.content.Intent
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.SnapshotsClient
import com.google.android.gms.games.snapshot.SnapshotMetadata
import com.google.gson.Gson
import com.studioadriatic.gpgs.accountinfo.PlayerInfoController
import com.studioadriatic.gpgs.accountinfo.PlayerInfoListener
import com.studioadriatic.gpgs.achievements.AchievementsController
import com.studioadriatic.gpgs.achievements.AchievementsListener
import com.studioadriatic.gpgs.events.EventsController
import com.studioadriatic.gpgs.events.EventsListener
import com.studioadriatic.gpgs.leaderboards.LeaderBoardsListener
import com.studioadriatic.gpgs.leaderboards.LeaderboardsController
import com.studioadriatic.gpgs.savedgames.SavedGamesController
import com.studioadriatic.gpgs.savedgames.SavedGamesListener
import com.studioadriatic.gpgs.signin.SignInController
import com.studioadriatic.gpgs.signin.SignInListener
import com.studioadriatic.gpgs.signin.UserProfile
import com.studioadriatic.gpgs.stats.PlayerStatsController
import com.studioadriatic.gpgs.stats.PlayerStatsListener
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import java.math.BigInteger
import java.util.Random

class PlayGameServicesGodot(godot: Godot) : GodotPlugin(godot), AchievementsListener, EventsListener,
    LeaderBoardsListener, SavedGamesListener, SignInListener, PlayerStatsListener, PlayerInfoListener {

    private lateinit var signInController: SignInController
    private lateinit var achievementsController: AchievementsController
    private lateinit var leaderboardsController: LeaderboardsController
    private lateinit var eventsController: EventsController
    private lateinit var playerStatsController: PlayerStatsController
    private lateinit var playerInfoController: PlayerInfoController
    private lateinit var savedGamesController: SavedGamesController
    private lateinit var gamesSignInClient: GamesSignInClient

    private lateinit var saveGameName: String

    companion object {
        val SIGNAL_SIGN_IN_SUCCESSFUL = SignalInfo("_on_sign_in_success", String::class.java)
        val SIGNAL_SIGN_IN_FAILED = SignalInfo("_on_sign_in_failed", Int::class.javaObjectType)
        val SIGNAL_SIGN_OUT_SUCCESS = SignalInfo("_on_sign_out_success")
        val SIGNAL_SIGN_OUT_FAILED = SignalInfo("_on_sign_out_failed")
        val SIGNAL_ACHIEVEMENT_UNLOCKED = SignalInfo("_on_achievement_unlocked", String::class.java)
        val SIGNAL_ACHIEVEMENT_UNLOCKED_FAILED = SignalInfo("_on_achievement_unlocking_failed", String::class.java)
        val SIGNAL_ACHIEVEMENT_REVEALED = SignalInfo("_on_achievement_revealed", String::class.java)
        val SIGNAL_ACHIEVEMENT_REVEALED_FAILED = SignalInfo("_on_achievement_revealing_failed", String::class.java)
        val SIGNAL_ACHIEVEMENT_INCREMENTED = SignalInfo("_on_achievement_incremented", String::class.java)
        val SIGNAL_ACHIEVEMENT_INCREMENTED_FAILED =
            SignalInfo("_on_achievement_incrementing_failed", String::class.java)
        val SIGNAL_ACHIEVEMENT_STEPS_SET = SignalInfo("_on_achievement_steps_set", String::class.java)
        val SIGNAL_ACHIEVEMENT_STEPS_SET_FAILED =
            SignalInfo("_on_achievement_steps_setting_failed", String::class.java)
        val SIGNAL_ACHIEVEMENT_INFO_LOAD = SignalInfo("_on_achievement_info_loaded", String::class.java)
        val SIGNAL_ACHIEVEMENT_INFO_LOAD_FAILED = SignalInfo("_on_achievement_info_load_failed", String::class.java)
        val SIGNAL_LEADERBOARD_SCORE_SUBMITTED = SignalInfo("_on_leaderboard_score_submitted", String::class.java)
        val SIGNAL_LEADERBOARD_SCORE_SUBMITTED_FAILED =
            SignalInfo("_on_leaderboard_score_submitting_failed", String::class.java)
        val SIGNAL_EVENT_SUBMITTED = SignalInfo("_on_event_submitted", String::class.java)
        val SIGNAL_EVENT_SUBMITTED_FAILED = SignalInfo("_on_event_submitting_failed", String::class.java)
        val SIGNAL_EVENTS_LOADED = SignalInfo("_on_events_loaded", String::class.java)
        val SIGNAL_EVENTS_EMPTY = SignalInfo("_on_events_empty")
        val SIGNAL_EVENTS_LOADED_FAILED = SignalInfo("_on_events_loading_failed")
        val SIGNAL_PLAYER_STATS_LOADED = SignalInfo("_on_player_stats_loaded", String::class.java)
        val SIGNAL_PLAYER_STATS_LOADED_FAILED = SignalInfo("_on_player_stats_loading_failed")
        val SIGNAL_SAVED_GAME_SUCCESS = SignalInfo("_on_game_saved_success")
        val SIGNAL_SAVED_GAME_FAILED = SignalInfo("_on_game_saved_fail")
        val SIGNAL_SAVED_GAME_LOAD_SUCCESS = SignalInfo("_on_game_load_success", String::class.java)
        val SIGNAL_SAVED_GAME_LOAD_FAIL = SignalInfo("_on_game_load_fail")
        val SIGNAL_SAVED_GAME_CREATE_SNAPSHOT = SignalInfo("_on_create_new_snapshot", String::class.java)
        val SIGNAL_PLAYER_INFO_LOADED = SignalInfo("_on_player_info_loaded", String::class.java)
        val SIGNAL_PLAYER_INFO_LOADED_FAILED = SignalInfo("_on_player_info_loading_failed")
    }

    override fun getPluginName(): String {
        return BuildConfig.LIBRARY_NAME
    }

    override fun getPluginMethods(): MutableList<String> {
        return mutableListOf(
            "isGooglePlayServicesAvailable",
            "init",
            "initWithSavedGames",
            "signIn",
            "signOut",
            "isSignedIn",
            "showAchievements",
            "unlockAchievement",
            "revealAchievement",
            "incrementAchievement",
            "setAchievementSteps",
            "loadAchievementInfo",
            "showLeaderBoard",
            "showAllLeaderBoards",
            "submitLeaderBoardScore",
            "submitEvent",
            "loadEvents",
            "loadEventsById",
            "loadPlayerStats",
            "showSavedGames",
            "saveSnapshot",
            "loadSnapshot",
            "loadPlayerInfo"
        )
    }

    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            SIGNAL_SIGN_IN_SUCCESSFUL,
            SIGNAL_SIGN_IN_FAILED,
            SIGNAL_SIGN_OUT_SUCCESS,
            SIGNAL_SIGN_OUT_FAILED,
            SIGNAL_ACHIEVEMENT_UNLOCKED,
            SIGNAL_ACHIEVEMENT_UNLOCKED_FAILED,
            SIGNAL_ACHIEVEMENT_REVEALED,
            SIGNAL_ACHIEVEMENT_REVEALED_FAILED,
            SIGNAL_ACHIEVEMENT_INCREMENTED,
            SIGNAL_ACHIEVEMENT_INCREMENTED_FAILED,
            SIGNAL_ACHIEVEMENT_STEPS_SET,
            SIGNAL_ACHIEVEMENT_STEPS_SET_FAILED,
            SIGNAL_ACHIEVEMENT_INFO_LOAD,
            SIGNAL_ACHIEVEMENT_INFO_LOAD_FAILED,
            SIGNAL_LEADERBOARD_SCORE_SUBMITTED,
            SIGNAL_LEADERBOARD_SCORE_SUBMITTED_FAILED,
            SIGNAL_EVENT_SUBMITTED,
            SIGNAL_EVENT_SUBMITTED_FAILED,
            SIGNAL_EVENTS_LOADED,
            SIGNAL_EVENTS_EMPTY,
            SIGNAL_EVENTS_LOADED_FAILED,
            SIGNAL_PLAYER_STATS_LOADED,
            SIGNAL_PLAYER_STATS_LOADED_FAILED,
            SIGNAL_SAVED_GAME_SUCCESS,
            SIGNAL_SAVED_GAME_FAILED,
            SIGNAL_SAVED_GAME_LOAD_SUCCESS,
            SIGNAL_SAVED_GAME_LOAD_FAIL,
            SIGNAL_SAVED_GAME_CREATE_SNAPSHOT,
            SIGNAL_PLAYER_INFO_LOADED,
            SIGNAL_PLAYER_INFO_LOADED_FAILED
        )
    }

    override fun onMainActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SignInController.RC_SIGN_IN) {
            signInController.onSignInActivityResult(data)
        } else if (requestCode == SavedGamesController.RC_SAVED_GAMES) {
            if (data != null) {
                if (data.hasExtra(SnapshotsClient.EXTRA_SNAPSHOT_METADATA)) {
                    data.getParcelableExtra<SnapshotMetadata>(SnapshotsClient.EXTRA_SNAPSHOT_METADATA)?.let {
                        savedGamesController.loadSnapshot(it.uniqueName)
                    }
                } else if (data.hasExtra(SnapshotsClient.EXTRA_SNAPSHOT_NEW)) {
                    val unique = BigInteger(281, Random()).toString(13)
                    savedGamesController.createNewSnapshot("$saveGameName$unique")
                }
            }
        }
    }

    fun isGooglePlayServicesAvailable(): Boolean {
        val activity = getActivity() ?: return false
        val result: Int = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)
        return result == ConnectionResult.SUCCESS
    }

    fun init(enablePopups: Boolean, requestEmail: Boolean, requestProfile: Boolean, requestToken: String) {
        initialize(false, enablePopups, "DefaultGame", requestEmail, requestProfile, requestToken)
    }

    fun initWithSavedGames(enablePopups: Boolean, saveGameName: String, requestEmail: Boolean, requestProfile: Boolean, requestToken: String) {
        initialize(true, enablePopups, saveGameName, requestEmail, requestProfile, requestToken)
    }

    private fun initialize(enableSaveGamesFunctionality: Boolean, enablePopups: Boolean, saveGameName: String,
                           requestEmail: Boolean, requestProfile: Boolean, requestToken: String) {
        this.saveGameName = saveGameName

        val activity = getActivity() ?: return

        PlayGamesSdk.initialize(activity)

        signInController = SignInController(activity, this)
        achievementsController = AchievementsController(activity, this)
        leaderboardsController = LeaderboardsController(activity, this)
        eventsController = EventsController(activity, this)
        playerStatsController = PlayerStatsController(activity, this)
        playerInfoController = PlayerInfoController(activity, this)
        savedGamesController = SavedGamesController(activity, this)

        gamesSignInClient = PlayGames.getGamesSignInClient(activity)

        // Note: enablePopups parameter is not used in the current implementation
        // The modern Play Games Services SDK handles popups automatically
    }

    fun signIn() {
        signInController.signIn()
    }

    fun signOut() {
        signInController.signOut()
    }

    fun isSignedIn(): Boolean {
        return signInController.isSignedIn()
    }

    fun showAchievements() {
        achievementsController.showAchievements()
    }

    fun unlockAchievement(achievementName: String) {
        achievementsController.unlockAchievement(achievementName)
    }

    fun revealAchievement(achievementName: String) {
        achievementsController.revealAchievement(achievementName)
    }

    fun incrementAchievement(achievementName: String, step: Int) {
        achievementsController.incrementAchievement(achievementName, step)
    }

    fun setAchievementSteps(achievementName: String, steps: Int) {
        achievementsController.setAchievementSteps(achievementName, steps)
    }

    fun loadAchievementInfo(forceReload: Boolean) {
        achievementsController.loadAchievementInfo(forceReload)
    }

    fun showLeaderBoard(leaderBoardId: String) {
        leaderboardsController.showLeaderboard(leaderBoardId)
    }

    fun showAllLeaderBoards() {
        leaderboardsController.showAllLeaderboards()
    }

    fun submitLeaderBoardScore(leaderBoardId: String, score: Int) {
        leaderboardsController.submitScore(leaderBoardId, score)
    }

    fun submitEvent(eventId: String, incrementBy: Int) {
        eventsController.submitEvent(eventId, incrementBy)
    }

    fun loadEvents() {
        eventsController.loadEvents()
    }

    fun loadEventsById(ids: Array<String>) {
        eventsController.loadEventById(ids)
    }

    fun loadPlayerStats(forceRefresh: Boolean) {
        playerStatsController.checkPlayerStats(forceRefresh)
    }

    fun showSavedGames(title: String, allowAdBtn: Boolean, allowDeleteBtn: Boolean, maxNumberOfSavedGamesToShow: Int) {
        savedGamesController.showSavedGamesUI(title, allowAdBtn, allowDeleteBtn, maxNumberOfSavedGamesToShow)
    }

    fun saveSnapshot(name: String, data: String, description: String) {
        savedGamesController.saveSnapshot(name, data, description)
    }

    fun loadSnapshot(name: String) {
        savedGamesController.loadSnapshot(name)
    }

    fun loadPlayerInfo() {
        playerInfoController.fetchPlayerInfo()
    }

    override fun onAchievementUnlocked(achievementName: String) {
        emitSignal(SIGNAL_ACHIEVEMENT_UNLOCKED.name, achievementName)
    }

    override fun onAchievementUnlockingFailed(achievementName: String) {
        emitSignal(SIGNAL_ACHIEVEMENT_UNLOCKED_FAILED.name, achievementName)
    }

    override fun onAchievementRevealed(achievementName: String) {
        emitSignal(SIGNAL_ACHIEVEMENT_REVEALED.name, achievementName)
    }

    override fun onAchievementRevealingFailed(achievementName: String) {
        emitSignal(SIGNAL_ACHIEVEMENT_REVEALED_FAILED.name, achievementName)
    }

    override fun onAchievementIncremented(achievementName: String) {
        emitSignal(SIGNAL_ACHIEVEMENT_INCREMENTED.name, achievementName)
    }

    override fun onAchievementIncrementingFailed(achievementName: String) {
        emitSignal(SIGNAL_ACHIEVEMENT_INCREMENTED_FAILED.name, achievementName)
    }

    override fun onAchievementStepsSet(achievementName: String) {
        emitSignal(SIGNAL_ACHIEVEMENT_STEPS_SET.name, achievementName)
    }

    override fun onAchievementStepsSettingFailed(achievementName: String) {
        emitSignal(SIGNAL_ACHIEVEMENT_STEPS_SET_FAILED.name, achievementName)
    }

    override fun onAchievementInfoLoaded(achievementsJson: String) {
        emitSignal(SIGNAL_ACHIEVEMENT_INFO_LOAD.name, achievementsJson)
    }

    override fun onAchievementInfoLoadingFailed() {
        emitSignal(SIGNAL_ACHIEVEMENT_INFO_LOAD_FAILED.name)
    }

    override fun onEventSubmitted(eventId: String) {
        emitSignal(SIGNAL_EVENT_SUBMITTED.name, eventId)
    }

    override fun onEventSubmittingFailed(eventId: String) {
        emitSignal(SIGNAL_EVENT_SUBMITTED_FAILED.name, eventId)
    }

    override fun onEventsLoaded(eventsJson: String) {
        emitSignal(SIGNAL_EVENTS_LOADED.name, eventsJson)
    }

    override fun onEventsEmpty() {
        emitSignal(SIGNAL_EVENTS_EMPTY.name)
    }

    override fun onEventsLoadingFailed() {
        emitSignal(SIGNAL_EVENTS_LOADED_FAILED.name)
    }

    override fun onLeaderBoardScoreSubmitted(leaderboardId: String) {
        emitSignal(SIGNAL_LEADERBOARD_SCORE_SUBMITTED.name, leaderboardId)
    }

    override fun onLeaderBoardScoreSubmittingFailed(leaderboardId: String) {
        emitSignal(SIGNAL_LEADERBOARD_SCORE_SUBMITTED_FAILED.name, leaderboardId)
    }

    override fun onSavedGameSuccess() {
        emitSignal(SIGNAL_SAVED_GAME_SUCCESS.name)
    }

    override fun onSavedGameFailed() {
        emitSignal(SIGNAL_SAVED_GAME_FAILED.name)
    }

    override fun onSavedGameLoadFailed() {
        emitSignal(SIGNAL_SAVED_GAME_LOAD_FAIL.name)
    }

    override fun onSavedGameLoadSuccess(data: String) {
        emitSignal(SIGNAL_SAVED_GAME_LOAD_SUCCESS.name, data)
    }

    override fun onSavedGameCreateSnapshot(currentSaveName: String) {
        emitSignal(SIGNAL_SAVED_GAME_CREATE_SNAPSHOT.name, currentSaveName)
    }

    override fun onSignedInSuccessfully(userProfile: UserProfile) {
        emitSignal(SIGNAL_SIGN_IN_SUCCESSFUL.name, Gson().toJson(userProfile))
    }

    override fun onSignInFailed(statusCode: Int) {
        emitSignal(SIGNAL_SIGN_IN_FAILED.name, statusCode)
    }

    override fun onSignOutSuccess() {
        emitSignal(SIGNAL_SIGN_OUT_SUCCESS.name)
    }

    override fun onSignOutFailed() {
        emitSignal(SIGNAL_SIGN_OUT_FAILED.name)
    }

    override fun onPlayerStatsLoaded(statsJson: String) {
        emitSignal(SIGNAL_PLAYER_STATS_LOADED.name, statsJson)
    }

    override fun onPlayerStatsLoadingFailed() {
        emitSignal(SIGNAL_PLAYER_STATS_LOADED_FAILED.name)
    }

    override fun onPlayerInfoLoaded(response: String) {
        emitSignal(SIGNAL_PLAYER_INFO_LOADED.name, response)
    }

    override fun onPlayerInfoLoadingFailed() {
        emitSignal(SIGNAL_PLAYER_INFO_LOADED_FAILED.name)
    }
}
