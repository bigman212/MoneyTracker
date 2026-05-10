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

`agentCheck` is the harness command. Do not assume plain `./gradlew check` is equivalent.

Harness rule: each custom verification task must live in its own task file under `buildSrc/src/main/kotlin/moneytracker/agentchecks/`; `MoneyTrackerAgentChecksPlugin` should only orchestrate `agentCheck`.

## Pull Requests

Agents may run `git` and `gh` commands for local repository, branch, commit, push, and pull request work.

Branch names must start with one of these prefixes:

- `feature/`
- `refactor/`
- `bugfix/`
- `ci/`

PR titles must start with exactly one work scope in parentheses before the main title:

- `(feat)`
- `(fixes)`
- `(refactor)`

Commit messages must follow the same scope rule. The title text after the scope must start with a lowercase letter.

Examples:

- `(feat) add compose stability harness`
- `(fixes) handle missing compose reports`
- `(refactor) move harness task wiring`

Useful fix commands:

- `./gradlew spotlessApply`
- `./gradlew ktlintFormat`

Lint reports are generated under each module's `build/reports/` directory:

- Android Lint: text and SARIF reports under `build/reports/`.
- detekt: markdown and SARIF reports under `build/reports/detekt/`.
- ktlint: `build/reports/ktlint/`
- Spotless failures print a patch-style diff directly in Gradle output.

## Current Project Shape

Module-specific agent instructions live next to each module:

- [`:app`](app/AGENTS.md)
- [`:core:database`](core/database/AGENTS.md)

Dependency rules:

- `:app` may depend on `:feature:*`, `:core:ui`, `:core:designsystem`, and DI/runtime modules.
- `:feature:*` may depend on `:core:domain`, `:core:ui`, and `:core:designsystem`.
- `:core:domain` must not depend on Android UI, Compose, or data implementation modules.
- `:core:data` may depend on `:core:domain`, but features must not depend on data implementations directly.
- `:core:ui` may depend on Compose and `:core:designsystem`, but not on features.
- `:core:designsystem` must not depend on features or data.

New Android modules should use convention plugins instead of duplicating setup:

- App modules: `id("moneytracker.android.application")`
- Android library modules: `id("moneytracker.android.library")`
- Shared quality tooling is applied from the root through `id("moneytracker.quality")`.
- Agent harness tasks come from `id("moneytracker.agent-checks")`.

## Versions

Version and SDK compatibility comes from `gradle/libs.versions.toml` and convention plugins in `buildSrc`. Do not update existing versions without explicit user approval.

## Load-Bearing Conventions

- ViewModels do not know about Composable functions or Compose `Modifier`.
- Composables receive state and callbacks; they do not fetch repositories directly.
- Repository APIs should expose streams as `Flow` for observable data; use `suspend` only for one-shot commands.
- UI state should be immutable Kotlin data classes.
- Feature modules own feature-specific UI state, events, and screen-level ViewModels.
- Resources, theme values, typography, icons, and dimensions should come from `:core:designsystem` once that module exists.
- Keep version declarations in `gradle/libs.versions.toml`; do not hardcode dependency versions in module build files.
- Keep Android entry points in `:app`; do not put Activities in feature modules unless the architecture is explicitly changed.
- Prefer APIs compatible with versions declared in `gradle/libs.versions.toml`.
- `companion object` must be the first declaration inside a class body.
- Use Hilt for DI. Do not introduce Koin or manual service locator containers.
- Use kotlinx.serialization for JSON parsing. Do not introduce Gson, Moshi, Jackson, or `ObjectMapper`.
- Use Room for database access in `:core:database`.
- If detekt reports a violation, fix the design using SOLID/GRASP or an appropriate pattern. Do not suppress detekt findings unless the rule is provably wrong for the local context.
- Hilt modules currently set `enableAggregatingTask = false` to avoid the known JavaPoet aggregate task conflict on this AGP/Kotlin stack; preserve it unless the stack is upgraded and `agentCheck` proves it is no longer needed.

## Do Not Touch

- `versionCode`
- `versionName`, unless the task explicitly requires a release/version change.
- `signingConfigs`
- signing keys, keystores, passwords, or release credentials.
- `local.properties`
- generated Gradle wrapper files unless the task explicitly asks for a Gradle upgrade.
