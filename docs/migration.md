# Migration Guide

This guide helps you migrate from older versions of the Google Play Games Services plugin or from other GPGS implementations to the current version.

## Table of Contents

- [Migrating from v1.x to v2.x](#migrating-from-v1x-to-v2x)
- [Migrating from Other GPGS Plugins](#migrating-from-other-gpgs-plugins)
- [Breaking Changes](#breaking-changes)
- [API Changes](#api-changes)
- [Configuration Changes](#configuration-changes)
- [Best Practices for Migration](#best-practices-for-migration)

## Migrating from v1.x to v2.x

### Major Changes

The v2.x release includes significant updates to align with Google Play Games Services v2 API and modern Android development practices.

#### 1. Updated Dependencies

**Old (v1.x)**:
```ini
[dependencies]
remote=["com.google.android.gms:play-services-games:19.0.0"]
```

**New (v2.x)**:
```ini
[dependencies]
remote=["com.google.android.gms:play-services-games:21.0.0", "com.google.android.gms:play-services-auth:19.0.0", "com.google.code.gson:gson:2.8.6"]
```

#### 2. Initialization Changes

**Old (v1.x)**:
```gdscript
# Simple initialization
play_games_services.init()
```

**New (v2.x)**:
```gdscript
# More explicit initialization with parameters
play_games_services.init(requestEmail, requestProfile, requestToken, token)

# Example
play_games_services.init(true, false, false, "")
```

#### 3. Signal Name Changes

Some signal names have been updated for consistency:

**Old (v1.x)**:
```gdscript
play_games_services.connect("_on_sign_in_success", self, "_on_sign_in_success")
play_games_services.connect("_on_sign_in_failed", self, "_on_sign_in_failed")
```

**New (v2.x)**:
```gdscript
# Signal names remain the same, but parameters may have changed
play_games_services.connect("_on_sign_in_success", self, "_on_sign_in_success")
play_games_services.connect("_on_sign_in_failed", self, "_on_sign_in_failed")
```

### Step-by-Step Migration

#### Step 1: Update Plugin Files

1. **Remove old plugin files**:
   ```
   android/plugins/GodotPlayGamesServices.aar (old)
   android/plugins/GodotPlayGamesServices.gdap (old)
   ```

2. **Add new plugin files**:
   ```
   android/plugins/GodotPlayGamesServices.release.aar (new)
   android/plugins/GodotPlayGamesServices.gdap (new)
   ```

#### Step 2: Update Initialization Code

**Before**:
```gdscript
func _ready():
    if Engine.has_singleton("GodotPlayGamesServices"):
        play_games_services = Engine.get_singleton("GodotPlayGamesServices")
        play_games_services.init()
```

**After**:
```gdscript
func _ready():
    if Engine.has_singleton("GodotPlayGamesServices"):
        play_games_services = Engine.get_singleton("GodotPlayGamesServices")
        
        # Check if Google Play Services is available
        if play_games_services.isGooglePlayServicesAvailable():
            play_games_services.init(true, false, false, "")
        else:
            print("Google Play Services not available")
```

#### Step 3: Update Signal Handlers

Review all signal handlers to ensure they match the new API:

**Before**:
```gdscript
func _on_sign_in_success(user_data):
    # Handle sign in success
    pass
```

**After**:
```gdscript
func _on_sign_in_success(user_profile_json: String):
    var user_profile = JSON.parse(user_profile_json).result
    print("Signed in as: ", user_profile.displayName)
```

#### Step 4: Update Method Calls

Some method names or parameters may have changed:

**Before**:
```gdscript
# Old method calls (example)
play_games_services.submitScore(leaderboard_id, score)
```

**After**:
```gdscript
# New method calls
play_games_services.submitLeaderBoardScore(leaderboard_id, score)
```

#### Step 5: Test Thoroughly

After migration:
1. Test all GPGS features
2. Verify achievements unlock correctly
3. Check leaderboard submissions
4. Test saved games functionality
5. Ensure error handling works

## Migrating from Other GPGS Plugins

### From cgisca/PGSGP

If you're migrating from the original cgisca plugin:

#### 1. Plugin Structure Changes

**Old Structure**:
```
android/plugins/
├── GodotPlayGamesServices.aar
└── GodotPlayGamesServices.gdap
```

**New Structure** (same, but updated files):
```
android/plugins/
├── GodotPlayGamesServices.release.aar
└── GodotPlayGamesServices.gdap
```

#### 2. API Compatibility

Most API calls remain compatible, but check for:

- Updated signal parameters
- New initialization requirements
- Enhanced error handling

#### 3. Configuration Updates

Update your Google Play Console configuration:
- Ensure OAuth credentials are current
- Verify SHA-1 fingerprints
- Check app ID configuration

### From Custom Implementations

If you have a custom GPGS implementation:

#### 1. Replace Custom Code

**Before** (custom implementation):
```gdscript
# Custom GPGS wrapper
extends Node

func sign_in():
    # Custom sign-in logic
    pass

func unlock_achievement(id: String):
    # Custom achievement logic
    pass
```

**After** (using plugin):
```gdscript
# Using the plugin
extends Node

var play_games_services

func _ready():
    if Engine.has_singleton("GodotPlayGamesServices"):
        play_games_services = Engine.get_singleton("GodotPlayGamesServices")
        play_games_services.init(true, false, false, "")

func sign_in():
    if play_games_services:
        play_games_services.signIn()

func unlock_achievement(id: String):
    if play_games_services and play_games_services.isSignedIn():
        play_games_services.unlockAchievement(id)
```

#### 2. Update Signal Handling

Replace custom callbacks with plugin signals:

**Before**:
```gdscript
# Custom callback system
func on_sign_in_complete(success: bool, user_data: Dictionary):
    if success:
        handle_sign_in_success(user_data)
    else:
        handle_sign_in_failure()
```

**After**:
```gdscript
# Plugin signal system
func _ready():
    play_games_services.connect("_on_sign_in_success", self, "_on_sign_in_success")
    play_games_services.connect("_on_sign_in_failed", self, "_on_sign_in_failed")

func _on_sign_in_success(user_profile_json: String):
    var user_profile = JSON.parse(user_profile_json).result
    handle_sign_in_success(user_profile)

func _on_sign_in_failed(error_code: int):
    handle_sign_in_failure(error_code)
```

## Breaking Changes

### v2.0 Breaking Changes

#### 1. Minimum Requirements

- **Godot**: Now requires Godot 4.x (was 3.x)
- **Android SDK**: Minimum API level 21 (was 16)
- **Google Play Services**: v21.0.0 (was v19.0.0)

#### 2. Initialization Parameters

**Old**:
```gdscript
play_games_services.init()
```

**New**:
```gdscript
play_games_services.init(requestEmail: bool, requestProfile: bool, requestToken: bool, token: String)
```

#### 3. JSON Parsing

**Old** (Godot 3.x):
```gdscript
var user_data = JSON.parse(json_string).result
```

**New** (Godot 4.x):
```gdscript
var json = JSON.new()
var parse_result = json.parse(json_string)
if parse_result == OK:
    var user_data = json.data
```

#### 4. Signal Connection Syntax

**Old** (Godot 3.x):
```gdscript
play_games_services.connect("_on_sign_in_success", self, "_on_sign_in_success")
```

**New** (Godot 4.x):
```gdscript
play_games_services.connect("_on_sign_in_success", _on_sign_in_success)
```

## API Changes

### Method Signature Updates

#### 1. Initialization Methods

**v1.x**:
```gdscript
init()
initWithSavedGames(saveGameName: String)
```

**v2.x**:
```gdscript
init(requestEmail: bool, requestProfile: bool, requestToken: bool, token: String)
initWithSavedGames(saveGameName: String, requestEmail: bool, requestProfile: bool, requestToken: bool, token: String)
```

#### 2. Achievement Methods

Most achievement methods remain the same:
```gdscript
# These remain unchanged
unlockAchievement(achievementId: String)
revealAchievement(achievementId: String)
incrementAchievement(achievementId: String, steps: int)
setAchievementSteps(achievementId: String, steps: int)
```

#### 3. New Methods

**v2.x** introduces new methods:
```gdscript
# New utility method
isGooglePlayServicesAvailable() -> bool

# Enhanced player info
loadPlayerInfo()
```

### Signal Parameter Changes

#### 1. Sign-In Success

**v1.x**:
```gdscript
func _on_sign_in_success(user_data: Dictionary):
    pass
```

**v2.x**:
```gdscript
func _on_sign_in_success(user_profile_json: String):
    var user_profile = JSON.parse(user_profile_json).result
    pass
```

#### 2. Achievement Info

**v1.x**:
```gdscript
func _on_achievement_info_loaded(achievements: Array):
    pass
```

**v2.x**:
```gdscript
func _on_achievement_info_loaded(achievements_json: String):
    var achievements = JSON.parse(achievements_json).result
    pass
```

## Configuration Changes

### Google Play Console

#### 1. OAuth Configuration

Ensure your OAuth configuration is up to date:

1. **Check Credentials**: Verify OAuth client is properly configured
2. **Update SHA-1**: Add SHA-1 fingerprints for all build types
3. **Verify Scopes**: Ensure necessary scopes are enabled

#### 2. App Configuration

Update app configuration in Google Play Console:

1. **Verify App ID**: Ensure app ID matches your configuration
2. **Check Package Name**: Verify package name is correct
3. **Update Icons**: Ensure all required icons are uploaded

### Android Manifest

#### 1. Permissions

**Old**:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

**New** (additional permissions may be required):
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

#### 2. Metadata

Ensure app ID metadata is correctly configured:
```xml
<meta-data
    android:name="com.google.android.gms.games.APP_ID"
    android:value="@string/app_id" />
```

## Best Practices for Migration

### 1. Create a Migration Plan

1. **Backup Current Implementation**: Save your current working code
2. **Test Environment**: Set up a test environment for migration
3. **Incremental Migration**: Migrate features one at a time
4. **Thorough Testing**: Test each feature after migration

### 2. Migration Checklist

#### Pre-Migration:
- [ ] Backup current project
- [ ] Document current GPGS usage
- [ ] Identify all GPGS-related code
- [ ] Note current achievement/leaderboard IDs

#### During Migration:
- [ ] Update plugin files
- [ ] Update initialization code
- [ ] Update method calls
- [ ] Update signal handlers
- [ ] Update JSON parsing (Godot 4.x)

#### Post-Migration:
- [ ] Test sign-in/sign-out
- [ ] Test all achievements
- [ ] Test all leaderboards
- [ ] Test saved games
- [ ] Test error handling
- [ ] Performance testing

### 3. Common Migration Issues

#### Issue: Plugin Not Detected
**Solution**: Ensure new plugin files are correctly placed and export settings are updated.

#### Issue: Sign-In Fails
**Solution**: Verify OAuth configuration and SHA-1 fingerprints are updated.

#### Issue: JSON Parsing Errors
**Solution**: Update JSON parsing code for Godot 4.x syntax.

#### Issue: Signal Connection Errors
**Solution**: Update signal connection syntax for Godot 4.x.

### 4. Testing Strategy

#### 1. Unit Testing
Test individual GPGS features:
```gdscript
func test_sign_in():
    assert(play_games_services != null, "Plugin should be available")
    play_games_services.signIn()
    # Wait for signal and verify result

func test_achievement_unlock():
    if play_games_services.isSignedIn():
        play_games_services.unlockAchievement("test_achievement_id")
        # Verify achievement unlocked signal
```

#### 2. Integration Testing
Test complete workflows:
- Sign in → unlock achievement → submit score → save game
- Test offline/online scenarios
- Test error conditions

#### 3. Device Testing
Test on multiple devices:
- Different Android versions
- Devices with/without Google Play Services
- Different screen sizes and orientations

### 5. Rollback Plan

If migration fails:

1. **Restore Backup**: Revert to backed-up version
2. **Document Issues**: Note what went wrong
3. **Plan Fixes**: Address issues before retry
4. **Gradual Migration**: Consider migrating features incrementally

## Support and Resources

### Getting Help

If you encounter issues during migration:

1. **Check Documentation**: Review all documentation sections
2. **GitHub Issues**: Search existing issues or create new ones
3. **Community Forums**: Ask questions in Godot community forums
4. **Test Environment**: Use a separate test project for migration

### Useful Resources

- [API Reference](api-reference.md) - Complete API documentation
- [Configuration Guide](configuration.md) - Setup instructions
- [Troubleshooting Guide](troubleshooting.md) - Common issues and solutions
- [Examples](examples.md) - Practical implementation examples

---

**Previous**: [Troubleshooting Guide](troubleshooting.md) | **Home**: [Documentation Index](README.md)
