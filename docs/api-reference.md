# API Reference

This document provides a complete reference for all methods and signals available in the Google Play Games Services plugin.

## Table of Contents

- [Initialization](#initialization)
- [Authentication](#authentication)
- [Achievements](#achievements)
- [Leaderboards](#leaderboards)
- [Events](#events)
- [Player Stats](#player-stats)
- [Player Info](#player-info)
- [Saved Games](#saved-games)
- [Signals](#signals)
- [Utility Methods](#utility-methods)

## Initialization

### `init(requestEmail: bool, requestProfile: bool, requestToken: bool, token: String)`

Initializes the plugin with basic configuration.

**Parameters:**
- `requestEmail` (bool): Whether to request user's email address
- `requestProfile` (bool): Whether to request user's profile information
- `requestToken` (bool): Whether to request authentication token
- `token` (String): Authentication token (usually empty string)

**Example:**
```gdscript
play_games_services.init(true, false, false, "")
```

### `initWithSavedGames(saveGameName: String, requestEmail: bool, requestProfile: bool, requestToken: bool, token: String)`

Initializes the plugin with saved games functionality enabled.

**Parameters:**
- `saveGameName` (String): Default name for saved games
- `requestEmail` (bool): Whether to request user's email address
- `requestProfile` (bool): Whether to request user's profile information
- `requestToken` (bool): Whether to request authentication token
- `token` (String): Authentication token (usually empty string)

**Example:**
```gdscript
play_games_services.initWithSavedGames("MyGameSave", true, false, false, "")
```

## Authentication

### `signIn()`

Initiates the sign-in process for Google Play Games Services.

**Example:**
```gdscript
play_games_services.signIn()
```

**Related Signals:**
- `_on_sign_in_success(userProfile: String)`
- `_on_sign_in_failed(errorCode: int)`

### `signOut()`

Signs out the current user from Google Play Games Services.

**Example:**
```gdscript
play_games_services.signOut()
```

**Related Signals:**
- `_on_sign_out_success()`
- `_on_sign_out_failed()`

### `isSignedIn() -> bool`

Checks if a user is currently signed in.

**Returns:** `bool` - True if user is signed in, false otherwise

**Example:**
```gdscript
if play_games_services.isSignedIn():
    print("User is signed in")
```

## Achievements

### `showAchievements()`

Displays the achievements UI.

**Example:**
```gdscript
play_games_services.showAchievements()
```

### `unlockAchievement(achievementId: String)`

Unlocks a specific achievement.

**Parameters:**
- `achievementId` (String): The ID of the achievement to unlock

**Example:**
```gdscript
play_games_services.unlockAchievement("CgkIqt-jg_MWEAIQAQ")
```

**Related Signals:**
- `_on_achievement_unlocked(achievementId: String)`
- `_on_achievement_unlocking_failed(achievementId: String)`

### `revealAchievement(achievementId: String)`

Reveals a hidden achievement.

**Parameters:**
- `achievementId` (String): The ID of the achievement to reveal

**Example:**
```gdscript
play_games_services.revealAchievement("CgkIqt-jg_MWEAIQAQ")
```

**Related Signals:**
- `_on_achievement_revealed(achievementId: String)`
- `_on_achievement_revealing_failed(achievementId: String)`

### `incrementAchievement(achievementId: String, steps: int)`

Increments an incremental achievement by the specified number of steps.

**Parameters:**
- `achievementId` (String): The ID of the achievement to increment
- `steps` (int): Number of steps to increment

**Example:**
```gdscript
play_games_services.incrementAchievement("CgkIqt-jg_MWEAIQAQ", 5)
```

**Related Signals:**
- `_on_achievement_incremented(achievementId: String)`
- `_on_achievement_incrementing_failed(achievementId: String)`

### `setAchievementSteps(achievementId: String, steps: int)`

Sets the number of steps for an incremental achievement.

**Parameters:**
- `achievementId` (String): The ID of the achievement
- `steps` (int): Number of steps to set

**Example:**
```gdscript
play_games_services.setAchievementSteps("CgkIqt-jg_MWEAIQAQ", 10)
```

**Related Signals:**
- `_on_achievement_steps_set(achievementId: String)`
- `_on_achievement_steps_setting_failed(achievementId: String)`

### `loadAchievementInfo(forceReload: bool)`

Loads achievement information.

**Parameters:**
- `forceReload` (bool): Whether to force reload from server

**Example:**
```gdscript
play_games_services.loadAchievementInfo(false)
```

**Related Signals:**
- `_on_achievement_info_loaded(achievementsJson: String)`
- `_on_achievement_info_load_failed()`

## Leaderboards

### `showLeaderBoard(leaderboardId: String)`

Displays a specific leaderboard.

**Parameters:**
- `leaderboardId` (String): The ID of the leaderboard to show

**Example:**
```gdscript
play_games_services.showLeaderBoard("CgkIqt-jg_MWEAIQAQ")
```

### `showAllLeaderBoards()`

Displays all leaderboards.

**Example:**
```gdscript
play_games_services.showAllLeaderBoards()
```

### `submitLeaderBoardScore(leaderboardId: String, score: int)`

Submits a score to a leaderboard.

**Parameters:**
- `leaderboardId` (String): The ID of the leaderboard
- `score` (int): The score to submit

**Example:**
```gdscript
play_games_services.submitLeaderBoardScore("CgkIqt-jg_MWEAIQAQ", 1000)
```

**Related Signals:**
- `_on_leaderboard_score_submitted(leaderboardId: String)`
- `_on_leaderboard_score_submitting_failed(leaderboardId: String)`

## Events

### `submitEvent(eventId: String, incrementBy: int)`

Submits an event with a specified increment value.

**Parameters:**
- `eventId` (String): The ID of the event
- `incrementBy` (int): Value to increment the event by

**Example:**
```gdscript
play_games_services.submitEvent("CgkIqt-jg_MWEAIQAQ", 1)
```

**Related Signals:**
- `_on_event_submitted(eventId: String)`
- `_on_event_submitting_failed(eventId: String)`

### `loadEvents()`

Loads all events.

**Example:**
```gdscript
play_games_services.loadEvents()
```

**Related Signals:**
- `_on_events_loaded(eventsJson: String)`
- `_on_events_empty()`
- `_on_events_loading_failed()`

### `loadEventsById(eventIds: Array)`

Loads specific events by their IDs.

**Parameters:**
- `eventIds` (Array): Array of event ID strings

**Example:**
```gdscript
var event_ids = ["event1", "event2", "event3"]
play_games_services.loadEventsById(event_ids)
```

## Player Stats

### `loadPlayerStats(forceRefresh: bool)`

Loads player statistics.

**Parameters:**
- `forceRefresh` (bool): Whether to force refresh from server

**Example:**
```gdscript
play_games_services.loadPlayerStats(false)
```

**Related Signals:**
- `_on_player_stats_loaded(statsJson: String)`
- `_on_player_stats_loading_failed()`

## Player Info

### `loadPlayerInfo()`

Loads player profile information.

**Example:**
```gdscript
play_games_services.loadPlayerInfo()
```

**Related Signals:**
- `_on_player_info_loaded(playerInfoJson: String)`
- `_on_player_info_loading_failed()`

## Saved Games

### `showSavedGames(title: String, allowAddButton: bool, allowDeleteButton: bool, maxSavedGamesToShow: int)`

Shows the saved games UI.

**Parameters:**
- `title` (String): Title for the saved games UI
- `allowAddButton` (bool): Whether to show add button
- `allowDeleteButton` (bool): Whether to show delete button
- `maxSavedGamesToShow` (int): Maximum number of saved games to display

**Example:**
```gdscript
play_games_services.showSavedGames("My Saved Games", true, true, 5)
```

### `saveSnapshot(name: String, data: String, description: String)`

Saves game data to a snapshot.

**Parameters:**
- `name` (String): Name of the snapshot
- `data` (String): Game data to save (JSON string)
- `description` (String): Description of the save

**Example:**
```gdscript
var save_data = {"level": 5, "score": 1000}
play_games_services.saveSnapshot("save1", JSON.print(save_data), "Level 5 completed")
```

**Related Signals:**
- `_on_game_saved_success()`
- `_on_game_saved_fail()`

### `loadSnapshot(name: String)`

Loads game data from a snapshot.

**Parameters:**
- `name` (String): Name of the snapshot to load

**Example:**
```gdscript
play_games_services.loadSnapshot("save1")
```

**Related Signals:**
- `_on_game_load_success(data: String)`
- `_on_game_load_fail()`
- `_on_create_new_snapshot(name: String)`

## Utility Methods

### `isGooglePlayServicesAvailable() -> bool`

Checks if Google Play Services is available on the device.

**Returns:** `bool` - True if available, false otherwise

**Example:**
```gdscript
if play_games_services.isGooglePlayServicesAvailable():
    print("Google Play Services is available")
```

## Signals

All signals are automatically emitted by the plugin when corresponding operations complete. Connect to these signals to handle responses from Google Play Games Services.

### Authentication Signals

- `_on_sign_in_success(userProfile: String)` - Emitted when sign-in succeeds
- `_on_sign_in_failed(errorCode: int)` - Emitted when sign-in fails
- `_on_sign_out_success()` - Emitted when sign-out succeeds
- `_on_sign_out_failed()` - Emitted when sign-out fails

### Achievement Signals

- `_on_achievement_unlocked(achievementId: String)`
- `_on_achievement_unlocking_failed(achievementId: String)`
- `_on_achievement_revealed(achievementId: String)`
- `_on_achievement_revealing_failed(achievementId: String)`
- `_on_achievement_incremented(achievementId: String)`
- `_on_achievement_incrementing_failed(achievementId: String)`
- `_on_achievement_steps_set(achievementId: String)`
- `_on_achievement_steps_setting_failed(achievementId: String)`
- `_on_achievement_info_loaded(achievementsJson: String)`
- `_on_achievement_info_load_failed()`

### Leaderboard Signals

- `_on_leaderboard_score_submitted(leaderboardId: String)`
- `_on_leaderboard_score_submitting_failed(leaderboardId: String)`

### Event Signals

- `_on_event_submitted(eventId: String)`
- `_on_event_submitting_failed(eventId: String)`
- `_on_events_loaded(eventsJson: String)`
- `_on_events_empty()`
- `_on_events_loading_failed()`

### Player Stats Signals

- `_on_player_stats_loaded(statsJson: String)`
- `_on_player_stats_loading_failed()`

### Player Info Signals

- `_on_player_info_loaded(playerInfoJson: String)`
- `_on_player_info_loading_failed()`

### Saved Games Signals

- `_on_game_saved_success()`
- `_on_game_saved_fail()`
- `_on_game_load_success(data: String)`
- `_on_game_load_fail()`
- `_on_create_new_snapshot(name: String)`

## Error Codes

Common error codes you might encounter:

- `0`: Success
- `4`: Sign in required
- `6`: Resolution required
- `7`: Network error
- `8`: Internal error
- `10`: Developer error
- `12`: Canceled
- `13`: Timeout
- `14`: Interrupted
- `15`: Invalid account
- `16`: Resolution required
- `17`: Sign in failed

---

**Next**: [Usage Examples](examples.md)
