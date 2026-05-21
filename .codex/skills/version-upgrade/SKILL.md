---
name: version-upgrade
description: Use when upgrading important Android project versions in MoneyTracker, including Kotlin, KSP, AGP, compileSdk, targetSdk, Gradle, Compose compiler, Paparazzi, Hilt, Room, or other build-critical libraries. Guides compatibility checks, files to inspect, quick verification, and fallback strategy.
metadata:
  short-description: Upgrade Kotlin, SDK, AGP, or build-critical versions
---

# Version Upgrade

Use this skill when changing Kotlin, KSP, AGP, Gradle, SDK, Compose compiler, Paparazzi, Hilt, Room, or other build-critical versions.

## Argument First

Before editing, state the short reason for the chosen target version and the expected compatibility chain. Keep it concrete: which current failure or library requirement forced the upgrade.

## Files To Inspect

- `gradle/libs.versions.toml` for central version declarations.
- `buildSrc/build.gradle.kts` for hardcoded build plugin dependencies.
- `build-logic` or convention plugins if present.
- Root and module `build.gradle.kts` files for duplicated plugin or dependency versions.
- `gradle/wrapper/gradle-wrapper.properties` only when AGP or Gradle compatibility requires it.
- `AGENTS.md` and module `AGENTS.md` files for project-specific constraints.

Do not change `versionCode`, `versionName`, signing config, keystores, or `local.properties`.

## Compatibility Checklist

- Kotlin, KSP, Compose compiler/plugin, and AGP must be mutually compatible.
- KSP version must match the Kotlin line, for example `2.x.y-z`.
- Paparazzi must support the selected Kotlin metadata, AGP, compileSdk, and layoutlib combination.
- AGP must support the selected Gradle wrapper and SDK levels.
- Hilt/KAPT/KSP behavior must still work with existing project flags, especially `enableAggregatingTask = false`.
- Room compiler/runtime versions must stay aligned if either side changes.
- Prefer existing catalog aliases and convention plugins over module-local hardcoded versions.

## Fast Verification

Run the smallest useful checks first:

1. `./gradlew :app:compileDevelopDebugKotlin`
2. `./gradlew :app:recordPaparazziDebug` when Paparazzi, Compose, SDK, or layoutlib may be affected.
3. `./gradlew agentCheck` before committing.

If Paparazzi is only being used to render snapshots, do not add release unit test tasks to that loop unless the change specifically affects release-only behavior or all variants must be refreshed.

## Failure Triage

- Metadata version errors usually mean Kotlin, KSP, Compose compiler, or Paparazzi are on incompatible lines.
- Layoutlib/render errors usually point at Paparazzi, compileSdk, or AGP mismatch.
- Missing generated symbols usually point at KSP/Hilt/Room incompatibility or stale generated outputs.
- If one tool requires a newer Kotlin line than the rest of the stack supports, prefer the newest mutually compatible set instead of only bumping one version.

## Reporting

Report the changed versions, the files touched, and the exact verification commands that passed or failed. If a verification command fails because dependencies cannot be downloaded, ask for approval to rerun with network access.
