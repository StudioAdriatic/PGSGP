# AGENTS.md - Context and Guidelines for AI Coding Agents

Welcome, AI Coding Agent! This document provides the essential context, coding standards, automated systems, and procedures for contributing to the Play Games Services Godot Plugin (PGSGP) repository. Please read and follow these guidelines to maintain consistency and quality.

## Project Overview

PGSGP is an Android Play Games Services plugin for the Godot Game Engine (v4.x). It is written in Kotlin/Java and integrates with the Android standard Gradle build pipeline.

### Core Structure
- `app/`: The core Android library containing source code in Kotlin.
- `godot-lib/`: Gradle subproject managing the Godot AAR templates for local development compilation.
- `demo/`: A Godot demo project showing the plugin in action.
- `.github/`: Contains workflows for matrix testing, linting, building, and publishing releases.

---

## Coding and Tool Conventions

### Gradle & Kotlin
- Use Gradle wrapper (`./gradlew`) for all compilation, testing, and linting.
- Gradle compiles using JDK 17 (Java 17 target).
- Keep external library dependencies declared as `implementation` (or `compileOnly` when targeting Godot interfaces) in `app/build.gradle`.

### Automating GDAP Configuration
The `GodotPlayGamesServices.gdap` file contains configuration and dependency information that Godot uses. **Do NOT update this file manually.** Instead, use the automated script:
```bash
python3 generate_gdap.py
```
This script reads the dependencies directly from `app/build.gradle` and writes a clean, up-to-date `.gdap` file.

---

## Testing & Verification

### Running Unit Tests
Always run the JUnit/Robolectric test suite before pushing or proposing changes:
```bash
./gradlew test
```

### Running Linting
Always run the Android/Kotlin linter to ensure code style consistency:
```bash
./gradlew lint
```

---

## Git & Release Workflow

- **Branch Naming**: Feature branches should follow `feature/description` or `fix/description`.
- **Commit Messages**: Write concise, imperative commit messages (e.g., `feature: Add support for Godot 4.6.3`).
- **Release Triggers**: Releases are automatically triggered via GitHub Actions when pushing a git tag matching the pattern `v*.*.*` or `v*.*` to the `master` branch.

---

## Multi-Version Godot Support Skill

To assist with updating the plugin for new Godot stable releases, there is a dedicated agent skill located under:
- [.agents/update-godot-version/SKILL.md](file:///.agents/update-godot-version/SKILL.md)

Use this skill when tasked with extending the plugin's support to newly released versions of Godot Engine.
