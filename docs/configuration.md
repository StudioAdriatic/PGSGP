# Configuration Guide

This guide covers how to configure Google Play Games Services in Google Play Console and set up your Godot project for optimal integration.

## Table of Contents

- [Google Play Console Setup](#google-play-console-setup)
- [OAuth Configuration](#oauth-configuration)
- [Achievements Configuration](#achievements-configuration)
- [Leaderboards Configuration](#leaderboards-configuration)
- [Events Configuration](#events-configuration)
- [Saved Games Configuration](#saved-games-configuration)
- [Testing Configuration](#testing-configuration)

## Google Play Console Setup

### 1. Create or Select Your App

1. Go to [Google Play Console](https://play.google.com/console)
2. Select your existing app or create a new one
3. Complete the basic app information if creating new

### 2. Enable Play Games Services

1. In your app dashboard, navigate to **Grow → Play Games Services → Setup and management → Configuration**
2. Click **Create** if this is your first time setting up GPGS
3. Fill in the required information:
   - **Game name**: Display name for your game
   - **Description**: Brief description of your game
   - **Category**: Select appropriate game category
   - **Graphic assets**: Upload required icons and banners

### 3. Get Your Application ID

1. After creating the configuration, you'll see your **Application ID**
2. Copy this ID (format: `123456789012`)
3. You'll need this for your Android project configuration

## OAuth Configuration

### 1. Set Up OAuth Consent Screen

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your project (or create one if needed)
3. Navigate to **APIs & Services → OAuth consent screen**
4. Configure the consent screen:
   - **User Type**: External (for public apps)
   - **App name**: Your game's name
   - **User support email**: Your support email
   - **App domain**: Your website (optional)
   - **Developer contact information**: Your email

### 2. Create OAuth Credentials

1. Go to **APIs & Services → Credentials**
2. Click **Create Credentials → OAuth client ID**
3. Select **Android** as application type
4. Configure the Android client:
   - **Name**: Your app name
   - **Package name**: Your Android package name (e.g., `com.yourcompany.yourgame`)
   - **SHA-1 certificate fingerprint**: Your app's SHA-1 fingerprint

### 3. Generate SHA-1 Fingerprint

#### For Debug Builds:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

#### For Release Builds:
```bash
keytool -list -v -keystore your-release-key.keystore -alias your-key-alias
```

#### Alternative Method (using Godot):
1. In Godot, go to **Project → Export → Android**
2. In the **Keystore** section, you can see or generate your keystore
3. Use the keystore path with the keytool command above

### 4. Link Credentials to Play Games Services

1. Back in Google Play Console, go to **Play Games Services → Setup and management → Configuration**
2. Click **Add credential**
3. Select **Android** and choose your OAuth client
4. Save the configuration

## Achievements Configuration

### 1. Create Achievements

1. In Google Play Console, go to **Play Games Services → Setup and management → Achievements**
2. Click **Add achievement**
3. Configure each achievement:

#### Standard Achievement:
```
Name: First Victory
Description: Win your first game
Points: 10
Icon: Upload 512x512 PNG image
Initial state: Hidden/Revealed
```

#### Incremental Achievement:
```
Name: Dedicated Player
Description: Play 100 games
Points: 25
Steps: 100
Icon: Upload 512x512 PNG image
Initial state: Revealed
```

### 2. Achievement Best Practices

- **Points**: Award 5-20 points for easy achievements, 50+ for difficult ones
- **Icons**: Use consistent art style, 512x512 PNG format
- **Names**: Keep them short and descriptive
- **Descriptions**: Clearly explain what the player needs to do
- **Hidden vs Revealed**: Hide story spoilers, reveal gameplay achievements

### 3. Get Achievement IDs

1. After creating achievements, note their IDs (format: `CgkI...`)
2. Use these IDs in your Godot code:

```gdscript
const ACHIEVEMENTS = {
    "FIRST_WIN": "CgkIqt-jg_MWEAIQAQ",
    "DEDICATED_PLAYER": "CgkIqt-jg_MWEAIQAR"
}
```

## Leaderboards Configuration

### 1. Create Leaderboards

1. Go to **Play Games Services → Setup and management → Leaderboards**
2. Click **Add leaderboard**
3. Configure each leaderboard:

#### High Score Leaderboard:
```
Name: High Scores
Score format: Numeric (higher is better)
Score order: Larger is better
Icon: Upload 512x512 PNG image
```

#### Time-based Leaderboard:
```
Name: Best Times
Score format: Time (lower is better)
Score order: Smaller is better
Icon: Upload 512x512 PNG image
```

### 2. Leaderboard Types

#### Score Formats:
- **Numeric**: Standard integer scores
- **Time**: For time-based competitions (milliseconds)
- **Currency**: For money/points with decimal places

#### Score Orders:
- **Larger is better**: High scores, points, levels
- **Smaller is better**: Best times, fewest moves

### 3. Get Leaderboard IDs

```gdscript
const LEADERBOARDS = {
    "HIGH_SCORE": "CgkIqt-jg_MWEAIQAQ",
    "BEST_TIME": "CgkIqt-jg_MWEAIQAR"
}
```

## Events Configuration

### 1. Create Events

1. Go to **Play Games Services → Setup and management → Events**
2. Click **Add event**
3. Configure events:

#### Example Events:
```
Button Clicks:
- Name: Button Clicks
- Description: Track button interactions
- Icon: Upload 512x512 PNG image

Levels Completed:
- Name: Levels Completed  
- Description: Track level progression
- Icon: Upload 512x512 PNG image

Items Collected:
- Name: Items Collected
- Description: Track item collection
- Icon: Upload 512x512 PNG image
```

### 2. Event Best Practices

- **Granular Tracking**: Track specific user actions
- **Meaningful Data**: Focus on events that provide insights
- **Performance**: Don't track too frequently (batch events when possible)
- **Privacy**: Ensure events don't contain personal information

### 3. Get Event IDs

```gdscript
const EVENTS = {
    "BUTTON_CLICKS": "CgkIqt-jg_MWEAIQAQ",
    "LEVELS_COMPLETED": "CgkIqt-jg_MWEAIQAR",
    "ITEMS_COLLECTED": "CgkIqt-jg_MWEAIQAS"
}
```

## Saved Games Configuration

### 1. Enable Saved Games

1. In Google Play Console, go to **Play Games Services → Setup and management → Configuration**
2. Scroll to **Saved Games**
3. Toggle **Enable saved games for this game**

### 2. Configure Saved Games Settings

```
Maximum save file size: 3MB (default)
Maximum number of saves per player: 10 (default)
```

### 3. Initialize with Saved Games

In your Godot project, use the saved games initialization:

```gdscript
# Initialize with saved games enabled
play_games_services.initWithSavedGames("MyGameSave", true, false, false, "")
```

### 4. Saved Games Best Practices

- **File Size**: Keep saves under 1MB when possible
- **Naming**: Use consistent naming conventions
- **Descriptions**: Provide meaningful save descriptions
- **Conflict Resolution**: Handle save conflicts gracefully
- **Offline Support**: Maintain local saves as backup

## Testing Configuration

### 1. Add Test Accounts

1. In Google Play Console, go to **Play Games Services → Setup and management → Testers**
2. Add test accounts (Gmail addresses)
3. These accounts can test your game before release

### 2. Testing Checklist

#### Before Testing:
- [ ] OAuth credentials configured
- [ ] SHA-1 fingerprint added
- [ ] App ID correctly set in strings.xml
- [ ] All achievements/leaderboards created
- [ ] Test accounts added

#### During Testing:
- [ ] Sign-in works correctly
- [ ] Achievements unlock properly
- [ ] Leaderboard scores submit
- [ ] Events are tracked
- [ ] Saved games sync correctly
- [ ] Error handling works

### 3. Debug Configuration

#### Enable Debug Logging:
```gdscript
# Add debug prints in your signal handlers
func _on_sign_in_success(user_profile_json: String):
    print("DEBUG: Sign in successful - ", user_profile_json)

func _on_achievement_unlocked(achievement_id: String):
    print("DEBUG: Achievement unlocked - ", achievement_id)
```

#### Test on Different Devices:
- Device with Google Play Services
- Device without Google Play Services
- Different Android versions
- Different screen sizes

## Production Configuration

### 1. Release Preparation

1. **Update OAuth Credentials**: Add production SHA-1 fingerprint
2. **Test with Release Build**: Ensure everything works with signed APK
3. **Review Privacy Policy**: Update if collecting new data
4. **Final Testing**: Test all features with production credentials

### 2. Publishing Checklist

- [ ] All achievements tested and working
- [ ] All leaderboards tested and working
- [ ] Events tracking correctly
- [ ] Saved games syncing properly
- [ ] Error handling implemented
- [ ] Privacy policy updated
- [ ] Production OAuth credentials configured

### 3. Post-Launch Monitoring

#### Monitor in Google Play Console:
- **Player engagement** with achievements
- **Leaderboard participation**
- **Event data** for insights
- **Error reports** from crashes

#### Analytics Integration:
```gdscript
# Track GPGS usage
func track_gpgs_feature_usage(feature: String):
    # Send to your analytics service
    Analytics.track_event("gpgs_feature_used", {"feature": feature})
```

## Troubleshooting Configuration

### Common Issues:

#### Sign-in Fails:
- Verify SHA-1 fingerprint is correct
- Check OAuth credentials are linked
- Ensure app ID matches in strings.xml

#### Achievements Don't Unlock:
- Verify achievement IDs are correct
- Check user is signed in
- Ensure achievements are published

#### Leaderboards Don't Work:
- Verify leaderboard IDs are correct
- Check score format matches configuration
- Ensure user is signed in

#### Events Not Tracking:
- Verify event IDs are correct
- Check network connectivity
- Ensure events are published

---

**Next**: [Troubleshooting Guide](troubleshooting.md)
