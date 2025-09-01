# GDAP File Automation

This document explains the automated system for generating `GodotPlayGamesServices.gdap` files that are synchronized with the project's dependencies.

## Overview

The GDAP (Godot Android Plugin) file is required for each release version of the Godot plugin. It contains configuration and dependency information that Godot uses when building Android projects with the plugin.

## Problem Solved

Previously, GDAP files had to be manually created and maintained for each release, which could lead to:
- Outdated dependency versions in GDAP files
- Missing GDAP files for new releases
- Human error in dependency synchronization

## Solution

An automated system that:
1. Extracts dependencies directly from `app/build.gradle`
2. Generates GDAP files automatically during the release process
3. Creates version-specific GDAP files for each supported Godot version
4. Ensures dependencies are always in sync with the actual build configuration

## Components

### 1. Centralized Version Management (`.github/versions.json`)

A JSON configuration file that serves as the single source of truth for supported Godot versions:

```json
{
  "godot": ["4.4.1", "4.3", "4.2.2", "4.1.4", "4.0.4"]
}
```

This file is used by all workflows to ensure consistency across:
- Build matrices in `build-reusable.yml` and `test-reusable.yml`
- GDAP file generation in `release.yml`
- Any future workflows that need version information

### 2. Matrix Setup Workflow (`.github/workflows/matrix-setup.yml`)

A reusable workflow that:
- Reads the versions from `.github/versions.json`
- Provides the version list as outputs in both JSON array and space-separated string formats
- Is called by other workflows to get the current supported versions

### 3. Python Script (`generate_gdap.py`)

A Python script that:
- Parses the `app/build.gradle` file
- Extracts implementation dependencies (excluding test dependencies)
- Generates properly formatted GDAP files
- Supports command-line arguments for custom input/output paths

**Usage:**
```bash
# Generate GDAP file with default settings
python3 generate_gdap.py

# Generate GDAP file with custom paths
python3 generate_gdap.py path/to/build.gradle output/file.gdap
```

### 4. GitHub Actions Integration

The release workflow (`.github/workflows/release.yml`) has been enhanced to:
- Automatically generate GDAP files for each supported Godot version
- Update binary names to match version-specific AAR files
- Include GDAP files in GitHub releases alongside AAR files

**Supported Godot Versions:**
- 4.4.1
- 4.3
- 4.2.2
- 4.1.4
- 4.0.4

## Generated Files

For each release, the following files are automatically generated and included:

**AAR Files:**
- `GodotPlayGamesServices-godot-4.4.1.aar`
- `GodotPlayGamesServices-godot-4.3.aar`
- `GodotPlayGamesServices-godot-4.2.2.aar`
- `GodotPlayGamesServices-godot-4.1.4.aar`
- `GodotPlayGamesServices-godot-4.0.4.aar`

**GDAP Files:**
- `GodotPlayGamesServices-godot-4.4.1.gdap`
- `GodotPlayGamesServices-godot-4.3.gdap`
- `GodotPlayGamesServices-godot-4.2.2.gdap`
- `GodotPlayGamesServices-godot-4.1.4.gdap`
- `GodotPlayGamesServices-godot-4.0.4.gdap`

## GDAP File Format

Each generated GDAP file follows this format:

```ini
[config]

name="GodotPlayGamesServices"
binary_type="local"
binary="GodotPlayGamesServices-godot-X.X.X.aar"

[dependencies]

remote=["com.google.android.gms:play-services-games-v2:21.0.0", "com.google.android.gms:play-services-auth:21.2.0", "com.google.code.gson:gson:2.13.1", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3", "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3"]
```

## Current Dependencies

The system automatically extracts these dependencies from `app/build.gradle`:

- `com.google.android.gms:play-services-games-v2:21.0.0`
- `com.google.android.gms:play-services-auth:21.2.0`
- `com.google.code.gson:gson:2.13.1`
- `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3`
- `org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3`

## Maintenance

### Adding New Godot Versions

To support a new Godot version, simply update the `.github/versions.json` file:

```json
{
  "godot": ["4.4.1", "4.3", "4.2.2", "4.1.4", "4.0.4", "4.5.0"]
}
```

All workflows will automatically use the updated version list on the next run. No other files need to be modified.

### Updating Dependencies

Dependencies are automatically synchronized. When you update dependencies in `app/build.gradle`, the next release will automatically include the updated versions in all GDAP files.

### Excluding Dependencies

The script automatically excludes:
- Test dependencies (junit, mockito, espresso, robolectric)
- Kotlin standard library (automatically included by Godot)
- Project dependencies (compileOnly dependencies)

## Benefits

1. **Consistency**: GDAP files are always in sync with actual dependencies
2. **Automation**: No manual intervention required for releases
3. **Accuracy**: Eliminates human error in dependency management
4. **Scalability**: Easy to add support for new Godot versions
5. **Maintainability**: Single source of truth for dependencies

## Troubleshooting

### Script Fails to Find Dependencies

- Ensure `app/build.gradle` exists and contains a `dependencies` block
- Check that dependencies are declared with `implementation` (not `api` or `compileOnly`)

### Missing GDAP Files in Release

- Verify the release workflow completed successfully
- Check GitHub Actions logs for any errors in the GDAP generation step

### Incorrect Dependencies in GDAP

- Verify dependencies are correctly declared in `app/build.gradle`
- Ensure test dependencies are properly marked (contain keywords like 'test', 'junit', etc.)
