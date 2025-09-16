# Changelog

### 3.1.0
- Added support for Godot 4.5

### 3.0.0
Major codebase modernization and improvements:
- **BREAKING**: Migrated to Play Games Services v2 API
- **BREAKING**: Updated to modern Kotlin coroutines with async/await patterns
- **BREAKING**: Replaced deprecated callback-based APIs with suspend functions
- Fixed critical typo: Renamed `Achivemements.kt` to `Achievements.kt`
- Removed legacy popup management code (handled automatically by Play Games Services v2)
- Enhanced error handling with comprehensive try-catch blocks and meaningful error codes
- Improved memory management and lifecycle handling across all controllers

### 1.3.0
- Build for Godot 3.4

### 1.2.0
- Added posibility to enable/disable saved games functionality.
- Fixed bug in saved games functionality.

### 1.1.0
- Fix crash when trying to sign in

### 1.0.0
- Implemented Google play games services features:
- Sign-in/Sign out
- Achievements
- Leaderboards
- Events
- Player Stats
- Saved Games
