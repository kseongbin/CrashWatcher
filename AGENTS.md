# Repository Guidelines

## Project Structure & Module Organization
CrashLogger lives in the `app/` module. Kotlin sources are under `app/src/main/java/io/kseongbin/stacktrace/`, Android resources are in `app/src/main/res/`, and reusable ANR fixtures belong in `app/src/main/assets/`. Keep JVM unit tests inside `app/src/test/java/`; place device or integration coverage in `app/src/androidTest/java/`. Root Gradle coordination happens in `build.gradle.kts`, `settings.gradle.kts`, and the `gradle/` version-catalog folder. Leave `local.properties` untouched because it stores machine-specific SDK paths.

## Build, Test, and Development Commands
Use Gradle wrappers for every task. `./gradlew assembleDebug` produces the debug AAR for smoke validation, while `./gradlew assembleRelease` is the artifact we ship. `./gradlew testDebugUnitTest` runs the JUnit4 + Mockito suite. Pair that with `./gradlew connectedDebugAndroidTest` whenever instrumentation coverage is needed (emulator or device required). Run both `./gradlew lint` and `./gradlew detekt` before sending reviews; CI blocks on their results.

## Coding Style & Naming Conventions
Follow Kotlin + Compose norms: 4-space indents, trailing commas in multi-line builders, and expression-bodied functions where clear. Files and classes use PascalCase (for example `CrashLoggerConfig.kt`), while properties and functions use camelCase. Keep constants in `object` holders using `UPPER_SNAKE_CASE`. Prefer `val`, explicit visibility modifiers, and `internal` packages for platform bridges. Every public API must include concise KDoc covering purpose, parameters, and threading expectations.

## Testing Guidelines
Stick to JUnit4. Use `runTest` when exercising coroutines and set `Dispatchers.setMain` to a test dispatcher to avoid flaky ANR watchdogs. Name suites `<Class>Test` and methods using the `doesThing_whenCondition` pattern, e.g., `fun detectsCrash_whenUnhandledExceptionThrown()`. Guard coverage for watchdog timing, log rotation, and runtime enable/disable paths, and add regression tests whenever you fix a bug that previously escaped.

## Commit & Pull Request Guidelines
Commits follow `Type: Summary` (examples: `Fix: Drop legacy API`, `Docs: Update README`). Keep subjects under 72 characters and reference issue IDs in the body when helpful. Pull requests must describe scope, list validation commands with outcomes, and attach screenshots or log snippets relevant to crash output. Request at least one reviewer, link tracked issues, and ensure assemble, tests, lint, and detekt all pass before marking ready.

## Security & Configuration Tips
Never commit keystores, `local.properties`, API secrets, or crash artifacts. Store sensitive data in the consuming app and clean `app/build/tmp/crash_logs/` before pushing. If a debug log is required for discussion, attach it outside the repo (e.g., issue tracker uploads).
