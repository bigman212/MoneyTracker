# AGENTS.md

## Check Ownership

- Use custom Detekt rules for Kotlin PSI/AST rules that should run with static analysis reports, such as naming, declaration shape, mapper prefixes, and code-style conventions that need Kotlin syntax awareness.
- Use Konsist tests for architecture and declaration consistency rules that read best as executable project structure tests, such as layer dependencies, package placement, repository implementation naming, and matching implementation/interface conventions.
- Use Gradle harness tasks only for repository-wide checks that are not Kotlin AST problems, such as `AGENTS.md` text validation, generated report inspection, file-system boundaries, and cross-module build orchestration.
- Keep each new Detekt rule in its own file under `src/main/java/com/moneytracker/lint/detekt/rules/`.
- Keep Konsist tests under `src/test/java/com/moneytracker/lint/konsist/` and name test classes with `KonsistTest` suffix.
