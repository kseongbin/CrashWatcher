# Repository Guidelines

## Project Structure & Module Organization
- Root Gradle scripts live at `build.gradle.kts`, `settings.gradle.kts`, and `gradle/` for version catalogs. Avoid editing `local.properties`; it is developer-specific.
- The `app/` module packages the CrashLogger library. Source code resides under `app/src/main/java/io/kseongbin/stacktrace/`, while resources live in `app/src/main/res/`.
- JVM unit tests belong in `app/src/test/java/`; instrumentation or integration tests go in `app/src/androidTest/java/`.
- Sample assets (e.g., ANR fixtures) should sit in `app/src/main/assets/` or module-specific asset folders to keep logging artifacts isolated.

## Build, Test, and Development Commands
- `./gradlew assembleDebug` — builds the debug AAR for local validation.
- `./gradlew assembleRelease` — produces the release artifact; run before publishing.
- `./gradlew testDebugUnitTest` — executes JVM unit tests (JUnit4 + Mockito).
- `./gradlew connectedDebugAndroidTest` — runs instrumentation tests on a device/emulator; ensure one is attached.
- `./gradlew lint` and `./gradlew detekt` — run static analysis; both must pass before merging.

## Coding Style & Naming Conventions
- Kotlin + Compose-style guidelines: 4-space indentation, trailing commas for multi-line builders, and expression-bodied functions when concise.
- Class and file names use PascalCase (`CrashLoggerConfig.kt`); methods and vars use camelCase. Constants live in `object` holders with `UPPER_SNAKE_CASE`.
- Prefer immutability (`val`) and explicit visibility. Public APIs require KDoc summarizing purpose, params, and thread guarantees.
- Place platform abstractions in `internal` packages to keep the public surface minimal.

## Testing Guidelines
- Use JUnit4 with `runTest` coroutines helpers when touching suspending code. Mock clock or thread executors via `Dispatchers.setMain` to avoid flaky ANR timing.
- Name tests `<Class>Test` and individual methods using `fun detectsCrash_whenUnhandledExceptionThrown()`.
- Maintain coverage for watchdog timing, log rotation, and runtime enable/disable paths. Add regression tests whenever a bug is fixed.

## Commit & Pull Request Guidelines
- Follow the existing `Type: Summary` pattern seen in history (`Feature`, `Fix`, `Build`, `Docs`). Keep summaries under 72 characters and reference issue IDs in the body when applicable.
- Each PR should describe scope, testing evidence (commands + results), and any screenshots/log excerpts relevant to crash output.
- Link to tracked issues, request at least one reviewer, and confirm CI (assemble + tests + lint/detekt) passes before requesting review.

## Security & Configuration Tips
- Never commit personal keystores, `local.properties`, or API secrets. Store sensitive config in the consuming app, not the library.
- Logs written during development live under `app/build/tmp/crash_logs/`; delete before committing to keep diffs clean.
