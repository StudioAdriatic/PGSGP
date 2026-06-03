---
name: update-godot-version
description: "Procedure to add support for a new Godot Engine stable release in the PGSGP repository"
---

# Update Godot Version Skill

This skill documents the precise sequence of files and values that must be modified to support a new Godot Engine stable version. Follow these steps meticulously when asked to update the project's supported Godot versions.

## Step-by-Step Procedure

### 1. Update Version Matrix Configuration
Add the new Godot version and its corresponding compatible Kotlin version to [.github/versions.json](file:///.github/versions.json). Place new versions in descending order (newest at the top):

```json
{
  "versions": [
    {
      "godot": "NEW_GODOT_VERSION",
      "kotlin": "COMPATIBLE_KOTLIN_VERSION"
    },
    ...
  ]
}
```

### 2. Update Default Local Development Template
If the new Godot version is the latest stable version, update the fallback compilation AAR in [godot-lib/build.gradle](file:///godot-lib/build.gradle):

```gradle
artifacts.add("default", file('godot-lib.NEW_GODOT_VERSION.stable.template_release.aar'))
```

### 3. Update CI/CD Workflows
Update references to the default/latest Godot version across all GitHub workflows:

- **[.github/workflows/ci.yml](file:///.github/workflows/ci.yml)**:
  - In `Setup godot-lib for lint`, update the downloaded template `filename` and `url`.
  - In the `build-check` job, update `godot_version` input to the new latest stable version.
- **[.github/workflows/release.yml](file:///.github/workflows/release.yml)**:
  - In the `test` job, update `godot_version` input to the new latest stable version.
- **[.github/workflows/build-reusable.yml](file:///.github/workflows/build-reusable.yml)**:
  - Update `godot_version` input `default` parameter.
- **[.github/workflows/test-reusable.yml](file:///.github/workflows/test-reusable.yml)**:
  - Update `godot_version` input `default` parameter.

### 4. Update Documentation and Badges
Ensure all documentation accurately reflects the new support:

- **[README.md](file:///README.md)**: Update the Godot version badge to display the new latest stable version.
- **[Changelog.md](file:///Changelog.md)**: Add a new release entry (e.g. `v3.1.x`) documenting the newly supported Godot version.
- **[docs/gdap-automation.md](file:///docs/gdap-automation.md)**: Update supported versions lists, generated AAR lists, and GDAP lists.

### 5. Verify the Updates
Verify the entire update before committing or tagging:
- Run the `.gdap` generator script locally:
  ```bash
  python3 generate_gdap.py
  ```
- Run the build/lint pipelines to make sure no syntax errors were introduced.
