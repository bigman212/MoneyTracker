# AGENTS.md

## Commands

Use these exact Gradle commands from the repository root:

- `./gradlew agentCheck` - single-command harness for formatting, lint, static analysis, unit tests, and debug assembly.
- `./gradlew spotlessCheck`
- `./gradlew ktlintCheck`
- `./gradlew detekt`
- `./gradlew testDebugUnitTest`
- `./gradlew lintDebug`
- `./gradlew assembleDebug`

`agentCheck` runs matching tasks across all modules in this order:

1. `spotlessCheck`
2. `ktlintCheck`
3. `detekt`
4. `lintDebug`
5. `testDebugUnitTest`
6. `assembleDebug`

`agentCheck` is the harness command. Do not assume plain `./gradlew check` is equivalent.

Useful fix commands:

- `./gradlew spotlessApply`
- `./gradlew ktlintFormat`

Lint reports are generated under each module's `build/reports/` directory:

- Android Lint: `build/reports/lint-results-debug.*`
- detekt: `build/reports/detekt/detekt.*`
- ktlint: `build/reports/ktlint/`
- Spotless failures print a patch-style diff directly in Gradle output.

## Current Project Shape

Active Gradle modules:

- `:app` - Android application, package `com.moneytracker`, Compose UI entry point.

Expected module layout when the project is split:

- `:app` - app shell, navigation graph, DI wiring, Android entry points.
- `:core:data` - repositories, local/remote data sources, persistence, DTO/entity mapping.
- `:core:domain` - business models and use cases without Android UI dependencies.
- `:core:ui` - reusable Compose UI primitives that are not brand/design-system tokens.
- `:core:designsystem` - theme, typography, colors, icons, dimensions, app resources.
- `:feature:*` - feature screens, feature ViewModels, feature-specific UI state/events.

Dependency rules:

- `:app` may depend on `:feature:*`, `:core:ui`, `:core:designsystem`, and DI/runtime modules.
- `:feature:*` may depend on `:core:domain`, `:core:ui`, and `:core:designsystem`.
- `:core:domain` must not depend on Android UI, Compose, or data implementation modules.
- `:core:data` may depend on `:core:domain`, but features must not depend on data implementations directly.
- `:core:ui` may depend on Compose and `:core:designsystem`, but not on features.
- `:core:designsystem` must not depend on features or data.

## Versions

Keep API suggestions compatible with these project versions:

- `minSdk = 26`
- `targetSdk = 36`
- `compileSdk = 36`
- Android Gradle Plugin `8.13.2`
- Kotlin `2.0.21`
- Compose Compiler is provided by `org.jetbrains.kotlin.plugin.compose` version `2.0.21`.
- Compose BOM `2024.09.00`
- ktlint Gradle plugin `14.2.0`, ktlint CLI `1.8.0`
- detekt `1.23.8`
- Spotless `8.4.0`
- Java/Kotlin JVM target `11`

## Load-Bearing Conventions

- ViewModels do not know about Composable functions or Compose `Modifier`.
- Composables receive state and callbacks; they do not fetch repositories directly.
- Repository APIs should expose streams as `Flow` for observable data; use `suspend` only for one-shot commands.
- UI state should be immutable Kotlin data classes.
- Feature modules own feature-specific UI state, events, and screen-level ViewModels.
- Resources, theme values, typography, icons, and dimensions should come from `:core:designsystem` once that module exists.
- Keep version declarations in `gradle/libs.versions.toml`; do not hardcode dependency versions in module build files.
- Keep Android entry points in `:app`; do not put Activities in feature modules unless the architecture is explicitly changed.
- Prefer Kotlin/Compose APIs available for Kotlin `2.0.21`, AGP `8.13.2`, and minSdk `26`.

## Do Not Touch

- `versionCode`
- `versionName`, unless the task explicitly requires a release/version change.
- `signingConfigs`
- signing keys, keystores, passwords, or release credentials.
- `local.properties`
- generated Gradle wrapper files unless the task explicitly asks for a Gradle upgrade.
