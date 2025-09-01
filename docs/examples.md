# Usage Examples

This guide provides practical examples for implementing all Google Play Games Services features in your Godot game.

## Table of Contents

- [Basic Setup](#basic-setup)
- [Authentication Examples](#authentication-examples)
- [Achievements Examples](#achievements-examples)
- [Leaderboards Examples](#leaderboards-examples)
- [Events Examples](#events-examples)
- [Player Stats Examples](#player-stats-examples)
- [Saved Games Examples](#saved-games-examples)
- [Complete Game Manager](#complete-game-manager)

## Basic Setup

### Simple GPGS Manager

Create a singleton script (`GPGSManager.gd`) for managing Google Play Games Services:

```gdscript
extends Node

signal gpgs_signed_in(user_profile)
signal gpgs_sign_in_failed(error_code)
signal gpgs_signed_out

var play_games_services
var is_initialized = false

func _ready():
    if Engine.has_singleton("GodotPlayGamesServices"):
        play_games_services = Engine.get_singleton("GodotPlayGamesServices")
        _connect_signals()
        _initialize_gpgs()
    else:
        print("Google Play Games Services not available")

func _connect_signals():
    # Authentication signals
    play_games_services.connect("_on_sign_in_success", self, "_on_sign_in_success")
    play_games_services.connect("_on_sign_in_failed", self, "_on_sign_in_failed")
    play_games_services.connect("_on_sign_out_success", self, "_on_sign_out_success")
    
    # Achievement signals
    play_games_services.connect("_on_achievement_unlocked", self, "_on_achievement_unlocked")
    play_games_services.connect("_on_achievement_unlocking_failed", self, "_on_achievement_unlocking_failed")
    
    # Leaderboard signals
    play_games_services.connect("_on_leaderboard_score_submitted", self, "_on_leaderboard_score_submitted")

func _initialize_gpgs():
    if play_games_services.isGooglePlayServicesAvailable():
        play_games_services.init(true, false, false, "")
        is_initialized = true
        print("GPGS initialized successfully")
    else:
        print("Google Play Services not available on this device")

func _on_sign_in_success(user_profile_json: String):
    var user_profile = JSON.parse(user_profile_json).result
    print("Signed in as: ", user_profile.displayName)
    emit_signal("gpgs_signed_in", user_profile)

func _on_sign_in_failed(error_code: int):
    print("Sign in failed: ", error_code)
    emit_signal("gpgs_sign_in_failed", error_code)

func _on_sign_out_success():
    print("Signed out successfully")
    emit_signal("gpgs_signed_out")

func _on_achievement_unlocked(achievement_id: String):
    print("Achievement unlocked: ", achievement_id)

func _on_achievement_unlocking_failed(achievement_id: String):
    print("Failed to unlock achievement: ", achievement_id)

func _on_leaderboard_score_submitted(leaderboard_id: String):
    print("Score submitted to leaderboard: ", leaderboard_id)
```

## Authentication Examples

### Auto Sign-In on Game Start

```gdscript
extends Control

func _ready():
    # Connect to GPGS manager signals
    GPGSManager.connect("gpgs_signed_in", self, "_on_gpgs_signed_in")
    GPGSManager.connect("gpgs_sign_in_failed", self, "_on_gpgs_sign_in_failed")
    
    # Attempt automatic sign-in
    if GPGSManager.is_initialized:
        GPGSManager.play_games_services.signIn()

func _on_gpgs_signed_in(user_profile):
    $UI/SignInButton.text = "Welcome, " + user_profile.displayName
    $UI/SignInButton.disabled = true
    $UI/SignOutButton.visible = true

func _on_gpgs_sign_in_failed(error_code):
    $UI/SignInButton.text = "Sign In to Google Play"
    $UI/SignInButton.disabled = false

func _on_sign_in_button_pressed():
    if GPGSManager.play_games_services:
        GPGSManager.play_games_services.signIn()

func _on_sign_out_button_pressed():
    if GPGSManager.play_games_services:
        GPGSManager.play_games_services.signOut()
        $UI/SignInButton.text = "Sign In to Google Play"
        $UI/SignInButton.disabled = false
        $UI/SignOutButton.visible = false
```

### Check Sign-In Status

```gdscript
func check_authentication_status():
    if GPGSManager.play_games_services:
        var is_signed_in = GPGSManager.play_games_services.isSignedIn()
        if is_signed_in:
            print("User is signed in")
            enable_online_features()
        else:
            print("User is not signed in")
            disable_online_features()

func enable_online_features():
    $UI/AchievementsButton.disabled = false
    $UI/LeaderboardsButton.disabled = false
    $UI/SavedGamesButton.disabled = false

func disable_online_features():
    $UI/AchievementsButton.disabled = true
    $UI/LeaderboardsButton.disabled = true
    $UI/SavedGamesButton.disabled = true
```

## Achievements Examples

### Achievement Manager

```gdscript
extends Node

# Achievement IDs (replace with your actual IDs)
const ACHIEVEMENTS = {
    "FIRST_WIN": "CgkIqt-jg_MWEAIQAQ",
    "SCORE_1000": "CgkIqt-jg_MWEAIQAR",
    "PLAY_10_GAMES": "CgkIqt-jg_MWEAIQAS",
    "PERFECT_GAME": "CgkIqt-jg_MWEAIQAT"
}

var games_played = 0
var highest_score = 0

func _ready():
    # Connect achievement signals
    if GPGSManager.play_games_services:
        GPGSManager.play_games_services.connect("_on_achievement_unlocked", self, "_on_achievement_unlocked")
        GPGSManager.play_games_services.connect("_on_achievement_incremented", self, "_on_achievement_incremented")

func unlock_first_win():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.unlockAchievement(ACHIEVEMENTS.FIRST_WIN)

func check_score_achievement(score: int):
    if score >= 1000 and GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.unlockAchievement(ACHIEVEMENTS.SCORE_1000)

func increment_games_played():
    games_played += 1
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.incrementAchievement(ACHIEVEMENTS.PLAY_10_GAMES, 1)

func unlock_perfect_game():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.unlockAchievement(ACHIEVEMENTS.PERFECT_GAME)

func show_achievements():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.showAchievements()

func _on_achievement_unlocked(achievement_id: String):
    # Show custom notification
    show_achievement_notification(achievement_id)

func show_achievement_notification(achievement_id: String):
    var achievement_name = get_achievement_name(achievement_id)
    # Create a custom popup or notification
    print("🏆 Achievement Unlocked: " + achievement_name)

func get_achievement_name(achievement_id: String) -> String:
    for key in ACHIEVEMENTS:
        if ACHIEVEMENTS[key] == achievement_id:
            return key.replace("_", " ").capitalize()
    return "Unknown Achievement"
```

### Game Integration Example

```gdscript
extends Node

var score = 0
var game_completed = false

func _ready():
    # Connect to game events
    connect("game_won", self, "_on_game_won")
    connect("score_updated", self, "_on_score_updated")
    connect("game_completed", self, "_on_game_completed")

func _on_game_won():
    AchievementManager.unlock_first_win()
    AchievementManager.increment_games_played()

func _on_score_updated(new_score: int):
    score = new_score
    AchievementManager.check_score_achievement(score)

func _on_game_completed():
    if score == 100:  # Perfect score
        AchievementManager.unlock_perfect_game()
```

## Leaderboards Examples

### Leaderboard Manager

```gdscript
extends Node

# Leaderboard IDs (replace with your actual IDs)
const LEADERBOARDS = {
    "HIGH_SCORE": "CgkIqt-jg_MWEAIQAQ",
    "WEEKLY_SCORE": "CgkIqt-jg_MWEAIQAR",
    "TOTAL_POINTS": "CgkIqt-jg_MWEAIQAS"
}

func _ready():
    if GPGSManager.play_games_services:
        GPGSManager.play_games_services.connect("_on_leaderboard_score_submitted", self, "_on_score_submitted")
        GPGSManager.play_games_services.connect("_on_leaderboard_score_submitting_failed", self, "_on_score_submit_failed")

func submit_high_score(score: int):
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.submitLeaderBoardScore(LEADERBOARDS.HIGH_SCORE, score)
        print("Submitting high score: ", score)

func submit_weekly_score(score: int):
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.submitLeaderBoardScore(LEADERBOARDS.WEEKLY_SCORE, score)

func add_to_total_points(points: int):
    # For cumulative leaderboards, you might want to track locally and submit total
    var total_points = get_total_points() + points
    save_total_points(total_points)
    
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.submitLeaderBoardScore(LEADERBOARDS.TOTAL_POINTS, total_points)

func show_high_score_leaderboard():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.showLeaderBoard(LEADERBOARDS.HIGH_SCORE)

func show_all_leaderboards():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.showAllLeaderBoards()

func _on_score_submitted(leaderboard_id: String):
    print("Score successfully submitted to: ", leaderboard_id)
    show_score_submitted_notification()

func _on_score_submit_failed(leaderboard_id: String):
    print("Failed to submit score to: ", leaderboard_id)

func show_score_submitted_notification():
    # Show custom notification
    print("📊 Score submitted to leaderboard!")

func get_total_points() -> int:
    # Load from save file or return 0
    return 0

func save_total_points(points: int):
    # Save to file
    pass
```

## Events Examples

### Event Tracking

```gdscript
extends Node

# Event IDs (replace with your actual IDs)
const EVENTS = {
    "BUTTON_CLICKS": "CgkIqt-jg_MWEAIQAQ",
    "LEVELS_COMPLETED": "CgkIqt-jg_MWEAIQAR",
    "ITEMS_COLLECTED": "CgkIqt-jg_MWEAIQAS",
    "TIME_PLAYED": "CgkIqt-jg_MWEAIQAT"
}

func _ready():
    if GPGSManager.play_games_services:
        GPGSManager.play_games_services.connect("_on_event_submitted", self, "_on_event_submitted")

func track_button_click():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.submitEvent(EVENTS.BUTTON_CLICKS, 1)

func track_level_completed():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.submitEvent(EVENTS.LEVELS_COMPLETED, 1)

func track_items_collected(count: int):
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.submitEvent(EVENTS.ITEMS_COLLECTED, count)

func track_play_time(minutes: int):
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.submitEvent(EVENTS.TIME_PLAYED, minutes)

func load_events():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.loadEvents()

func _on_event_submitted(event_id: String):
    print("Event submitted: ", event_id)
```

## Player Stats Examples

### Stats Manager

```gdscript
extends Node

var player_stats = {}

func _ready():
    if GPGSManager.play_games_services:
        GPGSManager.play_games_services.connect("_on_player_stats_loaded", self, "_on_player_stats_loaded")
        GPGSManager.play_games_services.connect("_on_player_info_loaded", self, "_on_player_info_loaded")

func load_player_stats():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.loadPlayerStats(false)

func load_player_info():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.loadPlayerInfo()

func _on_player_stats_loaded(stats_json: String):
    player_stats = JSON.parse(stats_json).result
    print("Player stats loaded: ", player_stats)
    update_stats_ui()

func _on_player_info_loaded(info_json: String):
    var player_info = JSON.parse(info_json).result
    print("Player info loaded: ", player_info)
    update_player_info_ui(player_info)

func update_stats_ui():
    # Update UI with player stats
    if player_stats.has("averageSessionLength"):
        $UI/StatsPanel/SessionLength.text = "Avg Session: " + str(player_stats.averageSessionLength) + " min"
    
    if player_stats.has("daysSinceLastPlayed"):
        $UI/StatsPanel/LastPlayed.text = "Last played: " + str(player_stats.daysSinceLastPlayed) + " days ago"

func update_player_info_ui(player_info):
    if player_info.has("displayName"):
        $UI/PlayerPanel/PlayerName.text = player_info.displayName
    
    if player_info.has("playerId"):
        print("Player ID: ", player_info.playerId)
```

## Saved Games Examples

### Save Game Manager

```gdscript
extends Node

const SAVE_GAME_NAME = "MyGameSave"

var game_data = {
    "level": 1,
    "score": 0,
    "coins": 100,
    "unlocked_levels": [1],
    "settings": {
        "sound_enabled": true,
        "music_enabled": true
    }
}

func _ready():
    if GPGSManager.play_games_services:
        GPGSManager.play_games_services.connect("_on_game_saved_success", self, "_on_game_saved")
        GPGSManager.play_games_services.connect("_on_game_saved_fail", self, "_on_game_save_failed")
        GPGSManager.play_games_services.connect("_on_game_load_success", self, "_on_game_loaded")
        GPGSManager.play_games_services.connect("_on_game_load_fail", self, "_on_game_load_failed")
        GPGSManager.play_games_services.connect("_on_create_new_snapshot", self, "_on_create_new_snapshot")

func save_game():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        var save_data = JSON.print(game_data)
        var description = "Level " + str(game_data.level) + " - Score: " + str(game_data.score)
        GPGSManager.play_games_services.saveSnapshot(SAVE_GAME_NAME, save_data, description)
        print("Saving game...")

func load_game():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.loadSnapshot(SAVE_GAME_NAME)
        print("Loading game...")

func show_saved_games():
    if GPGSManager.play_games_services and GPGSManager.play_games_services.isSignedIn():
        GPGSManager.play_games_services.showSavedGames("My Saved Games", true, true, 5)

func _on_game_saved():
    print("✅ Game saved successfully to cloud")
    show_save_notification("Game saved to cloud!")

func _on_game_save_failed():
    print("❌ Failed to save game to cloud")
    show_save_notification("Failed to save to cloud")

func _on_game_loaded(data: String):
    var loaded_data = JSON.parse(data).result
    if loaded_data:
        game_data = loaded_data
        apply_loaded_data()
        print("✅ Game loaded successfully from cloud")
        show_save_notification("Game loaded from cloud!")
    else:
        print("❌ Invalid save data")

func _on_game_load_failed():
    print("❌ Failed to load game from cloud")
    show_save_notification("Failed to load from cloud")

func _on_create_new_snapshot(name: String):
    print("Creating new snapshot: ", name)
    # This is called when user selects "Create new save" from the UI

func apply_loaded_data():
    # Apply the loaded data to your game
    update_ui()
    load_level(game_data.level)

func update_ui():
    $UI/ScoreLabel.text = "Score: " + str(game_data.score)
    $UI/CoinsLabel.text = "Coins: " + str(game_data.coins)
    $UI/LevelLabel.text = "Level: " + str(game_data.level)

func show_save_notification(message: String):
    # Show custom notification
    print("💾 " + message)
```

## Complete Game Manager

Here's a complete example that ties everything together:

```gdscript
extends Node

# Game Manager that handles all GPGS features
signal game_data_changed

var current_level = 1
var current_score = 0
var total_coins = 100
var games_played = 0

func _ready():
    # Initialize all managers
    setup_gpgs_connections()
    
    # Auto sign-in
    if GPGSManager.is_initialized:
        GPGSManager.play_games_services.signIn()

func setup_gpgs_connections():
    GPGSManager.connect("gpgs_signed_in", self, "_on_signed_in")
    GPGSManager.connect("gpgs_signed_out", self, "_on_signed_out")

func _on_signed_in(user_profile):
    print("Welcome back, ", user_profile.displayName)
    # Load cloud save
    SaveGameManager.load_game()
    # Load player stats
    StatsManager.load_player_stats()

func _on_signed_out():
    print("Signed out from Google Play Games")

func complete_level(score: int):
    current_score += score
    current_level += 1
    games_played += 1
    
    # Track achievements
    AchievementManager.check_score_achievement(current_score)
    AchievementManager.increment_games_played()
    
    # Submit to leaderboard
    LeaderboardManager.submit_high_score(current_score)
    
    # Track events
    EventTracker.track_level_completed()
    
    # Save game
    update_save_data()
    SaveGameManager.save_game()
    
    emit_signal("game_data_changed")

func collect_coins(amount: int):
    total_coins += amount
    EventTracker.track_items_collected(amount)
    emit_signal("game_data_changed")

func update_save_data():
    SaveGameManager.game_data = {
        "level": current_level,
        "score": current_score,
        "coins": total_coins,
        "games_played": games_played,
        "last_played": OS.get_unix_time()
    }

func show_achievements():
    AchievementManager.show_achievements()

func show_leaderboards():
    LeaderboardManager.show_all_leaderboards()

func show_saved_games():
    SaveGameManager.show_saved_games()
```

This comprehensive example shows how to integrate all Google Play Games Services features into a cohesive game experience. Each manager handles its specific functionality while the main GameManager coordinates everything.

---

**Next**: [Configuration Guide](configuration.md)
