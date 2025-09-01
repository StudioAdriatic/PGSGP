# Getting Started

This guide will help you quickly set up and start using the Google Play Games Services plugin in your Godot project.

## Prerequisites

Before you begin, ensure you have:

- Godot Engine 4.x installed
- Android development environment set up
- A Google Play Console account
- Your game configured in Google Play Console with GPGS enabled

## Quick Setup

### 1. Download the Plugin

Download the latest release from the [GitHub releases page](https://github.com/StudioAdriatic/PGSGP/releases) or build it from source.

### 2. Install the Plugin

1. Copy the `GodotPlayGamesServices.release.aar` file to your project's `android/plugins/` directory
2. Copy the `GodotPlayGamesServices.gdap` file to the same directory
3. Enable the plugin in your project's Android export settings

### 3. Configure Android Export

In Godot's Project Settings → Export → Android:

1. Enable "Use Custom Build"
2. Check "GodotPlayGamesServices" in the Plugins section
3. Add the following permissions to your Android manifest:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 4. Basic Implementation

Create a script and add the following code:

```gdscript
extends Node

var play_games_services

func _ready():
    # Check if the plugin is available
    if Engine.has_singleton("GodotPlayGamesServices"):
        play_games_services = Engine.get_singleton("GodotPlayGamesServices")
        
        # Connect to essential signals
        play_games_services.connect("_on_sign_in_success", self, "_on_sign_in_success")
        play_games_services.connect("_on_sign_in_failed", self, "_on_sign_in_failed")
        
        # Initialize the plugin
        play_games_services.init(false, false, "")
        
        # Automatically sign in
        play_games_services.signIn()
    else:
        print("Google Play Games Services plugin not found")

func _on_sign_in_success(user_profile: String):
    var profile = JSON.parse(user_profile).result
    print("Signed in as: ", profile.displayName)

func _on_sign_in_failed(error_code: int):
    print("Sign in failed with error: ", error_code)
```

### 5. Test Your Setup

1. Export your project to Android
2. Install and run on a device with Google Play Services
3. Check the console output for sign-in status

## Next Steps

Once you have the basic setup working:

1. **Configure Google Play Console**: Set up achievements, leaderboards, and other features
2. **Explore the API**: Check out the [API Reference](api-reference.md) for all available methods
3. **Add Features**: Implement achievements, leaderboards, and other GPGS features
4. **Handle Edge Cases**: Review the [Troubleshooting](troubleshooting.md) guide

## Common First-Time Issues

### Plugin Not Found
- Ensure the `.aar` and `.gdap` files are in the correct directory
- Verify the plugin is enabled in Android export settings
- Check that you're using a custom Android build

### Sign-In Fails
- Verify your app is properly configured in Google Play Console
- Check that your app's SHA-1 fingerprint is registered
- Ensure you're testing on a device with Google Play Services

### Build Errors
- Make sure you have the latest Android SDK and build tools
- Verify all required permissions are in your manifest
- Check that Godot's Android export template is up to date

## Example Project

Check out the `demo/` folder in the plugin repository for a complete working example that demonstrates all plugin features.

---

**Next**: [Installation Guide](installation.md)
