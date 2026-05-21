---
name: add-screen
description: Use when adding or changing an Android Compose screen in MoneyTracker. Enforces previews, Paparazzi snapshots, resource access rules, and snapshot approval before committing PNG changes.
metadata:
  short-description: Add or change Compose screens
---

# Add Screen

Use this skill when creating or modifying an Android Compose screen.

## UI Workflow

When creating or changing a screen:

1. Always add `@Preview` functions with concrete UI state.
   If the screen has multiple states (`Loading`, `Empty`, `Loaded`, `Error`), add one preview for each state.
2. Always add a Paparazzi test for every preview.
3. Run `./gradlew :app:recordPaparazziDebug` for the fast render loop and report the list of created or changed PNG files to the user.
4. Show the generated or changed PNG to the user and wait for explicit design approval before committing any UI code.
5. Do not commit PNG snapshots until the user explicitly approves them.
6. Do not stage or commit `src/test/snapshots/` unless the user explicitly asks to version snapshot PNGs after approval.

The aggregate `./gradlew recordPaparazzi` may run release Paparazzi/unit-test tasks. Do not use it for the fast render loop unless the change specifically touches release-only behavior or all variants must be refreshed; `./gradlew agentCheck` remains the final full verification before commit.

Snapshot PNG files under `src/test/snapshots/` are gitignored by default. Treat them as local review artifacts unless the user explicitly asks to version them.

## Screen Structure

- Every public Compose screen must be split into `MyScreen` and private `MyScreenContent`.
- `MyScreen` wires state/callback defaults and calls `MyScreenContent`.
- `MyScreenContent` renders the UI and is the function previews should call.
- The content function name must use the exact screen prefix plus `Content`, for example `MainScreen` -> `MainScreenContent`.

## UI Component Contracts

- Treat reusable rows, cards, list items, empty states, and similar pieces as UI components, even when they are private composables.
- Move reusable UI components into their own composable functions or files instead of nesting them inside screen content.
- A UI component may accept primitives or a dedicated immutable display model.
- Dedicated display models passed into UI components must end with `Ui`, for example `ExpenseRowUi`.
- Keep a small component-specific `Ui` model next to the component when that is clearer than creating a separate file.
- Keep screen-level state immutable and named consistently with the screen, for example `MainScreenUi`.

## UI Restrictions

- Do not use `setContent { ... }` directly outside an `Activity` or `Fragment`.
- Do not use `LocalContext.current` to access resources. Use `stringResource`, `colorResource`, or the appropriate Compose resource API instead.
- A composable that accepts `Modifier` must declare it as the first optional parameter with the default value `Modifier`.
- Do not add a `Modifier` parameter to a composable unless it is passed into that composable's root UI node.
- Do not create decorative or placeholder modifiers just to keep an API symmetrical. Add modifiers only when they carry layout, behavior, or semantics that the component actually needs.
- Use opaque colors for screen and component container/background surfaces. Do not use `copy(alpha = ...)` for those colors.
