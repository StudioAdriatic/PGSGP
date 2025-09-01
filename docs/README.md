# Google Play Games Services Plugin for Godot - Documentation

Welcome to the comprehensive documentation for the Google Play Games Services (GPGS) plugin for Godot Engine.

## Table of Contents

1. [Getting Started](getting-started.md)
2. [Installation Guide](installation.md)
3. [Configuration](configuration.md)
4. [API Reference](api-reference.md)
5. [Usage Examples](examples.md)
6. [Troubleshooting](troubleshooting.md)
7. [Migration Guide](migration.md)

## Overview

This plugin provides seamless integration between Godot Engine and Google Play Games Services, enabling you to add social gaming features to your Android games.

### Supported Features

- **Authentication**: Sign-in/Sign-out functionality
- **Achievements**: Unlock, reveal, increment achievements
- **Leaderboards**: Submit scores and display leaderboards
- **Events**: Track and submit game events
- **Player Stats**: Access player statistics
- **Player Info**: Retrieve player profile information
- **Saved Games**: Cloud save functionality

### Requirements

- Godot Engine 4.x
- Android platform
- Google Play Games Services v2 (21.0.0)
- Google Play Console account with configured game

### Quick Start

```gdscript
# Initialize the plugin
var play_games_services = Engine.get_singleton("GodotPlayGamesServices")
play_games_services.init(true, false, false, "")

# Sign in
play_games_services.signIn()

# Connect to signals
play_games_services.connect("_on_sign_in_success", self, "_on_sign_in_success")
```

## Documentation Structure

Each section of this documentation covers specific aspects of the plugin:

- **Getting Started**: Basic setup and first steps
- **Installation**: Detailed installation instructions
- **Configuration**: Google Play Console and project setup
- **API Reference**: Complete method and signal documentation
- **Examples**: Practical usage examples for all features
- **Troubleshooting**: Common issues and solutions
- **Migration**: Upgrading from previous versions

## Support

For issues, questions, or contributions, please visit the [GitHub repository](https://github.com/StudioAdriatic/PGSGP).

---

*This plugin is maintained by [Studio Adriatic](https://studioadriatic.com)*
