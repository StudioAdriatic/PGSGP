# Troubleshooting Guide

This guide helps you diagnose and fix common issues when using the Google Play Games Services plugin for Godot.

## Table of Contents

- [Plugin Detection Issues](#plugin-detection-issues)
- [Authentication Problems](#authentication-problems)
- [Achievement Issues](#achievement-issues)
- [Leaderboard Problems](#leaderboard-problems)
- [Saved Games Issues](#saved-games-issues)
- [Build and Export Problems](#build-and-export-problems)
- [Network and Connectivity Issues](#network-and-connectivity-issues)
- [Performance Issues](#performance-issues)
- [Debug Tools and Logging](#debug-tools-and-logging)

## Plugin Detection Issues

### Problem: Plugin Not Found
**Symptoms**: `Engine.has_singleton("GodotPlayGamesServices")` returns `false`

**Solutions**:

1. **Check File Placement**:
   ```
   your_project/
   └── android/
       └── plugins/
           ├── GodotPlayGamesServices.release.aar ✓
           └── GodotPlayGamesServices.gdap ✓
   ```

2. **Verify Export Settings**:
   - Go to **Project → Project Settings → Export**
   - Select **Android** platform
   - Ensure **Use Custom Build** is checked
   - In **Plugins** section, verify **GodotPlayGamesServices** is enabled

3. **Check File Names**:
   - Ensure files are named exactly as shown above
   - No extra spaces or characters in filenames

4. **Clean and Rebuild**:
   ```gdscript
   # In Godot editor
   Project → Export → Android → Clean
   # Then export again
   ```

### Problem: Plugin Loads But Methods Don't Work
**Symptoms**: Plugin detected but method calls fail silently

**Solutions**:

1. **Check Initialization**:
   ```gdscript
   func _ready():
       if Engine.has_singleton("GodotPlayGamesServices"):
           var gpgs = Engine.get_singleton("GodotPlayGamesServices")
           
           # Check if Google Play Services is available
           if gpgs.isGooglePlayServicesAvailable():
               gpgs.init(true, false, false, "")
               print("GPGS initialized successfully")
           else:
               print("Google Play Services not available")
   ```

2. **Verify Dependencies**:
   - Check that `.gdap` file contains correct dependencies
   - Ensure Google Play Services is installed on test device

## Authentication Problems

### Problem: Sign-In Fails Immediately
**Error Code**: Usually `10` (Developer Error) or `12` (Canceled)

**Solutions**:

1. **Check SHA-1 Fingerprint**:
   ```bash
   # For debug builds
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   
   # For release builds
   keytool -list -v -keystore your-release-key.keystore -alias your-key-alias
   ```
   - Copy the SHA-1 fingerprint to Google Cloud Console credentials

2. **Verify App ID Configuration**:
   ```xml
   <!-- android/build/res/values/strings.xml -->
   <resources>
       <string name="app_id">YOUR_ACTUAL_APP_ID_HERE</string>
   </resources>
   ```

3. **Check Package Name**:
   - Ensure package name in Godot export settings matches Google Cloud Console
   - Verify package name in OAuth credentials

4. **OAuth Consent Screen**:
   - Ensure OAuth consent screen is properly configured
   - Check that app is not in "Testing" mode for production users

### Problem: Sign-In Works But User Profile Is Empty
**Symptoms**: Sign-in succeeds but user data is missing

**Solutions**:

1. **Check Initialization Parameters**:
   ```gdscript
   # Request email and profile data
   play_games_services.init(true, false, "")
   #                        ↑     ↑
   #                   email  profile
   ```

2. **Verify OAuth Scopes**:
   - Ensure OAuth client has necessary scopes
   - Check Google Cloud Console credentials configuration

### Problem: Sign-In Requires User Interaction Every Time
**Symptoms**: No automatic sign-in, always shows Google sign-in dialog

**Solutions**:

1. **Check Silent Sign-In**:
   ```gdscript
   func attempt_silent_sign_in():
       if play_games_services and play_games_services.isSignedIn():
           print("Already signed in")
           return
       
       # This should attempt silent sign-in first
       play_games_services.signIn()
   ```

2. **Verify Credentials**:
   - Ensure OAuth credentials are correctly linked
   - Check that app is properly configured in Google Play Console

## Achievement Issues

### Problem: Achievements Don't Unlock
**Symptoms**: `unlockAchievement()` called but achievement remains locked

**Solutions**:

1. **Verify Achievement IDs**:
   ```gdscript
   # Double-check IDs from Google Play Console
   const ACHIEVEMENTS = {
       "FIRST_WIN": "CgkIqt-jg_MWEAIQAQ"  # Exact ID from console
   }
   
   func unlock_achievement():
       if play_games_services and play_games_services.isSignedIn():
           play_games_services.unlockAchievement(ACHIEVEMENTS.FIRST_WIN)
       else:
           print("Not signed in - cannot unlock achievement")
   ```

2. **Check Sign-In Status**:
   ```gdscript
   func safe_unlock_achievement(achievement_id: String):
       if not play_games_services:
           print("GPGS not available")
           return
           
       if not play_games_services.isSignedIn():
           print("User not signed in")
           return
           
       play_games_services.unlockAchievement(achievement_id)
   ```

3. **Monitor Signals**:
   ```gdscript
   func _ready():
       play_games_services.connect("_on_achievement_unlocked", self, "_on_achievement_unlocked")
       play_games_services.connect("_on_achievement_unlocking_failed", self, "_on_achievement_failed")
   
   func _on_achievement_unlocked(achievement_id: String):
       print("✅ Achievement unlocked: ", achievement_id)
   
   func _on_achievement_failed(achievement_id: String):
       print("❌ Failed to unlock: ", achievement_id)
   ```

### Problem: Incremental Achievements Don't Progress
**Symptoms**: `incrementAchievement()` called but progress doesn't update

**Solutions**:

1. **Use Correct Method**:
   ```gdscript
   # For incremental achievements, use increment
   play_games_services.incrementAchievement(achievement_id, steps)
   
   # For setting specific step count
   play_games_services.setAchievementSteps(achievement_id, total_steps)
   ```

2. **Check Achievement Configuration**:
   - Verify achievement is configured as "Incremental" in Google Play Console
   - Ensure step count matches your game logic

## Leaderboard Problems

### Problem: Scores Don't Submit
**Symptoms**: `submitLeaderBoardScore()` called but scores don't appear

**Solutions**:

1. **Verify Leaderboard IDs**:
   ```gdscript
   const LEADERBOARDS = {
       "HIGH_SCORE": "CgkIqt-jg_MWEAIQAQ"  # Exact ID from console
   }
   
   func submit_score(score: int):
       if play_games_services and play_games_services.isSignedIn():
           play_games_services.submitLeaderBoardScore(LEADERBOARDS.HIGH_SCORE, score)
           print("Submitting score: ", score)
   ```

2. **Check Score Format**:
   ```gdscript
   # For time-based leaderboards, submit in milliseconds
   var time_in_seconds = 45.67
   var time_in_milliseconds = int(time_in_seconds * 1000)
   play_games_services.submitLeaderBoardScore(leaderboard_id, time_in_milliseconds)
   ```

3. **Monitor Submission**:
   ```gdscript
   func _ready():
       play_games_services.connect("_on_leaderboard_score_submitted", self, "_on_score_submitted")
       play_games_services.connect("_on_leaderboard_score_submitting_failed", self, "_on_score_failed")
   
   func _on_score_submitted(leaderboard_id: String):
       print("✅ Score submitted to: ", leaderboard_id)
   
   func _on_score_failed(leaderboard_id: String):
       print("❌ Score submission failed: ", leaderboard_id)
   ```

## Saved Games Issues

### Problem: Saved Games Don't Sync
**Symptoms**: Save/load operations fail or don't sync to cloud

**Solutions**:

1. **Initialize with Saved Games**:
   ```gdscript
   # Use initWithSavedGames instead of init
   play_games_services.initWithSavedGames("MyGameSave", true, false, false, "")
   ```

2. **Check Saved Games Configuration**:
   - Verify saved games are enabled in Google Play Console
   - Ensure user is signed in before save/load operations

3. **Handle Save Conflicts**:
   ```gdscript
   func _ready():
       play_games_services.connect("_on_game_saved_success", self, "_on_save_success")
       play_games_services.connect("_on_game_saved_fail", self, "_on_save_failed")
       play_games_services.connect("_on_game_load_success", self, "_on_load_success")
       play_games_services.connect("_on_game_load_fail", self, "_on_load_failed")
   
   func _on_save_failed():
       print("Save failed - possibly due to conflict or network issue")
       # Implement retry logic or show user message
   ```

### Problem: Save Data Corruption
**Symptoms**: Loaded data is invalid or corrupted

**Solutions**:

1. **Validate JSON Data**:
   ```gdscript
   func _on_game_load_success(data: String):
       var json_result = JSON.parse(data)
       if json_result.error != OK:
           print("Invalid JSON data loaded")
           return
       
       var save_data = json_result.result
       if not validate_save_data(save_data):
           print("Save data validation failed")
           return
       
       apply_save_data(save_data)
   
   func validate_save_data(data) -> bool:
       # Check required fields exist
       return data.has("level") and data.has("score")
   ```

2. **Implement Backup Strategy**:
   ```gdscript
   func save_game():
       # Save locally first
       save_local_backup()
       
       # Then save to cloud
       if play_games_services and play_games_services.isSignedIn():
           var save_data = JSON.print(game_data)
           play_games_services.saveSnapshot("MyGameSave", save_data, "Auto save")
   ```

## Build and Export Problems

### Problem: Build Fails with Dependency Errors
**Symptoms**: Android build fails with missing dependency errors

**Solutions**:

1. **Check .gdap File**:
   ```ini
   [config]
   name="GodotPlayGamesServices"
   binary_type="local"
   binary="GodotPlayGamesServices.release.aar"
   
   [dependencies]
   remote=["com.google.android.gms:play-services-games:21.0.0", "com.google.android.gms:play-services-auth:19.0.0", "com.google.code.gson:gson:2.8.6"]
   ```

2. **Update Android SDK**:
   - Ensure Android SDK and build tools are up to date
   - Update Godot export templates

3. **Check Minimum SDK Version**:
   ```
   Min SDK: 21 or higher
   Target SDK: 33 or higher
   ```

### Problem: APK Installs But Crashes on Launch
**Symptoms**: App installs successfully but crashes immediately

**Solutions**:

1. **Check Logcat Output**:
   ```bash
   adb logcat | grep -i "your.package.name"
   ```

2. **Verify Permissions**:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   ```

3. **Check ProGuard Rules** (if using):
   ```
   -keep class com.studioadriatic.gpgs.** { *; }
   -keep class com.google.android.gms.** { *; }
   ```

## Network and Connectivity Issues

### Problem: Features Work Offline But Fail Online
**Symptoms**: GPGS features fail when network is available

**Solutions**:

1. **Check Network Permissions**:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
   ```

2. **Implement Network Checking**:
   ```gdscript
   func check_network_connectivity():
       # Check if device has network connectivity
       # Implement appropriate fallback behavior
       pass
   ```

3. **Handle Network Timeouts**:
   ```gdscript
   func _on_sign_in_failed(error_code: int):
       match error_code:
           7: # Network error
               print("Network error - check connection")
           13: # Timeout
               print("Request timed out - try again")
           _:
               print("Sign in failed with error: ", error_code)
   ```

## Performance Issues

### Problem: Game Stutters When Using GPGS
**Symptoms**: Frame drops or stuttering when calling GPGS methods

**Solutions**:

1. **Batch Operations**:
   ```gdscript
   # Instead of calling for each event
   func track_multiple_events():
       # Batch events and submit periodically
       event_queue.append({"id": "BUTTON_CLICK", "count": 1})
       
       if event_queue.size() >= 10:
           submit_batched_events()
   ```

2. **Use Signals Properly**:
   ```gdscript
   # Don't block on GPGS operations
   func unlock_achievement_async(achievement_id: String):
       if play_games_services and play_games_services.isSignedIn():
           play_games_services.unlockAchievement(achievement_id)
           # Don't wait for response, handle in signal
   ```

## Debug Tools and Logging

### Enable Debug Logging

```gdscript
extends Node

var debug_enabled = true

func debug_print(message: String):
    if debug_enabled:
        print("[GPGS DEBUG] ", message)

func _ready():
    if play_games_services:
        # Connect all signals for debugging
        var signals = [
            "_on_sign_in_success", "_on_sign_in_failed",
            "_on_achievement_unlocked", "_on_achievement_unlocking_failed",
            "_on_leaderboard_score_submitted", "_on_leaderboard_score_submitting_failed"
        ]
        
        for signal_name in signals:
            play_games_services.connect(signal_name, self, "_debug_signal_handler", [signal_name])

func _debug_signal_handler(param1 = null, param2 = null, signal_name = ""):
    debug_print("Signal received: " + signal_name)
    if param1 != null:
        debug_print("  Param 1: " + str(param1))
    if param2 != null:
        debug_print("  Param 2: " + str(param2))
```

### Test Device Setup

```gdscript
func setup_test_environment():
    debug_print("=== GPGS Test Environment ===")
    debug_print("Plugin available: " + str(Engine.has_singleton("GodotPlayGamesServices")))
    
    if play_games_services:
        debug_print("Google Play Services available: " + str(play_games_services.isGooglePlayServicesAvailable()))
        debug_print("User signed in: " + str(play_games_services.isSignedIn()))
    
    debug_print("Android version: " + OS.get_name())
    debug_print("Device model: " + OS.get_model_name())
```

### Common Error Codes Reference

```gdscript
func interpret_error_code(error_code: int) -> String:
    match error_code:
        0: return "Success"
        4: return "Sign in required"
        6: return "Resolution required"
        7: return "Network error"
        8: return "Internal error"
        10: return "Developer error (check configuration)"
        12: return "Canceled by user"
        13: return "Timeout"
        14: return "Interrupted"
        15: return "Invalid account"
        16: return "Resolution required"
        17: return "Sign in failed"
        _: return "Unknown error: " + str(error_code)
```

## Getting Help

If you're still experiencing issues:

1. **Check the GitHub Issues**: [PGSGP Issues](https://github.com/StudioAdriatic/PGSGP/issues)
2. **Review Google Play Console**: Check for any configuration warnings
3. **Test on Multiple Devices**: Ensure the issue isn't device-specific
4. **Enable Debug Logging**: Use the debug tools provided above
5. **Create Minimal Test Case**: Isolate the problem to specific functionality

When reporting issues, include:
- Godot version
- Plugin version
- Android version and device model
- Complete error logs
- Steps to reproduce
- Expected vs actual behavior

---

**Previous**: [Configuration Guide](configuration.md) | **Home**: [Documentation Index](README.md)
