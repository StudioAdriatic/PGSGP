# Installation Guide

This guide provides detailed instructions for installing and setting up the Google Play Games Services plugin in your Godot project.

## Prerequisites

Before installing the plugin, ensure you have:

- **Godot Engine 4.x** installed
- **Android SDK** and build tools set up
- **Java Development Kit (JDK)** 8 or higher
- **Google Play Console** account
- Your game registered in Google Play Console

## Installation Methods

### Method 1: Download Pre-built Plugin (Recommended)

1. **Download the Plugin**
   - Go to the [GitHub releases page](https://github.com/StudioAdriatic/PGSGP/releases)
   - Download the latest `GodotPlayGamesServices.release.aar` file
   - Download the corresponding `GodotPlayGamesServices.gdap` file

2. **Create Plugin Directory**
   ```
   your_project/
   └── android/
       └── plugins/
           ├── GodotPlayGamesServices.release.aar
           └── GodotPlayGamesServices.gdap
   ```

3. **Enable Custom Build**
   - Open your project in Godot
   - Go to **Project → Project Settings → Export**
   - Select **Android** platform
   - Check **Use Custom Build**
   - In the **Plugins** section, enable **GodotPlayGamesServices**

### Method 2: Build from Source

1. **Clone the Repository**
   ```bash
   git clone https://github.com/StudioAdriatic/PGSGP.git
   cd PGSGP
   ```

2. **Build the Plugin**
   ```bash
   ./gradlew build
   ```

3. **Copy Built Files**
   - Copy `app/build/outputs/aar/app-release.aar` to your project's `android/plugins/` directory
   - Rename it to `GodotPlayGamesServices.release.aar`
   - Copy the `.gdap` file from the demo folder

## Project Configuration

### 1. Android Export Settings

In Godot's **Project Settings → Export → Android**:

#### Required Settings:
- ✅ **Use Custom Build**: Must be enabled
- ✅ **GodotPlayGamesServices**: Enable in Plugins section
- **Min SDK**: 21 or higher
- **Target SDK**: 33 or higher

#### Permissions:
Add these permissions to your Android manifest:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 2. Android Manifest Configuration

Create or modify `android/build/AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.yourcompany.yourgame">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/icon"
        android:label="@string/godot_project_name_string"
        android:theme="@style/GodotAppMainTheme">

        <!-- Google Play Games Services metadata -->
        <meta-data
            android:name="com.google.android.gms.games.APP_ID"
            android:value="@string/app_id" />

        <activity
            android:name="org.godotengine.godot.GodotApp"
            android:exported="true"
            android:launchMode="singleTask"
            android:screenOrientation="userLandscape"
            android:theme="@style/GodotAppSplashTheme">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### 3. String Resources

Create `android/build/res/values/strings.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_id">YOUR_GOOGLE_PLAY_GAMES_APP_ID</string>
</resources>
```

Replace `YOUR_GOOGLE_PLAY_GAMES_APP_ID` with your actual app ID from Google Play Console.

## Google Play Console Setup

### 1. Create or Configure Your Game

1. Go to [Google Play Console](https://play.google.com/console)
2. Select your app or create a new one
3. Navigate to **Play Games Services → Setup and management → Configuration**

### 2. Configure OAuth Consent

1. Go to **Credentials** in your Google Cloud Console
2. Configure the OAuth consent screen
3. Add your app's package name and SHA-1 fingerprint

### 3. Get Your App ID

1. In Google Play Console, go to **Play Games Services → Configuration**
2. Copy your **Application ID** (it looks like: `123456789012`)
3. Use this ID in your `strings.xml` file

### 4. Generate SHA-1 Fingerprint

For debug builds:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

For release builds:
```bash
keytool -list -v -keystore your-release-key.keystore -alias your-key-alias
```

Add the SHA-1 fingerprint to your Google Play Console credentials.

## Verification

### 1. Test Plugin Detection

Create a test script:

```gdscript
extends Node

func _ready():
    if Engine.has_singleton("GodotPlayGamesServices"):
        print("✅ Plugin detected successfully")
        var gpgs = Engine.get_singleton("GodotPlayGamesServices")
        
        if gpgs.isGooglePlayServicesAvailable():
            print("✅ Google Play Services available")
        else:
            print("❌ Google Play Services not available")
    else:
        print("❌ Plugin not found - check installation")
```

### 2. Test Build Process

1. Export your project to Android
2. Install on a device with Google Play Services
3. Check the console output for plugin detection

## Troubleshooting Installation

### Plugin Not Found

**Symptoms**: `Engine.has_singleton("GodotPlayGamesServices")` returns false

**Solutions**:
- Verify `.aar` and `.gdap` files are in `android/plugins/` directory
- Ensure "Use Custom Build" is enabled
- Check that the plugin is enabled in export settings
- Clean and rebuild your project

### Build Errors

**Symptoms**: Build fails during export

**Solutions**:
- Update Android SDK and build tools
- Verify Godot export templates are up to date
- Check that all required permissions are in manifest
- Ensure minimum SDK version is 21 or higher

### Google Play Services Not Available

**Symptoms**: `isGooglePlayServicesAvailable()` returns false

**Solutions**:
- Test on a device with Google Play Services installed
- Update Google Play Services on the test device
- Verify the device meets minimum requirements

### Authentication Issues

**Symptoms**: Sign-in fails immediately

**Solutions**:
- Verify SHA-1 fingerprint is correctly registered
- Check that app ID matches Google Play Console
- Ensure OAuth consent screen is properly configured
- Test with a Google account that has access to the app

## File Structure After Installation

Your project should look like this:

```
your_project/
├── android/
│   ├── build/
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   │       └── values/
│   │           └── strings.xml
│   └── plugins/
│       ├── GodotPlayGamesServices.release.aar
│       └── GodotPlayGamesServices.gdap
├── scenes/
├── scripts/
└── project.godot
```

## Next Steps

After successful installation:

1. **Configure Google Play Console**: Set up achievements, leaderboards, etc.
2. **Initialize the Plugin**: Add initialization code to your game
3. **Test Features**: Implement and test each GPGS feature
4. **Handle Edge Cases**: Add proper error handling

---

**Next**: [Configuration Guide](configuration.md)
