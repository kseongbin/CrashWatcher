# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Android application built with Kotlin and Jetpack Compose. The project uses Android Gradle Plugin 8.12.3, Kotlin 2.0.21, and targets Android API 36 (minimum API 26).

## Build System

**Gradle Version Catalog**: Dependencies are managed via `gradle/libs.versions.toml` using Gradle version catalogs.

**Build Commands**:
```bash
# Build the app
./gradlew build

# Clean build
./gradlew clean build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device/emulator
./gradlew installDebug
```

## Testing

**Run all tests**:
```bash
./gradlew test
```

**Run unit tests only**:
```bash
./gradlew testDebugUnitTest
```

**Run instrumented tests** (requires connected device/emulator):
```bash
./gradlew connectedAndroidTest
```

**Run specific test class**:
```bash
./gradlew test --tests io.kseongbin.stacktrace.ExampleUnitTest
```

**Run instrumented test class**:
```bash
./gradlew connectedAndroidTest --tests io.kseongbin.stacktrace.ExampleInstrumentedTest
```

## Code Quality

**Lint checks**:
```bash
./gradlew lint
./gradlew lintDebug
```

## Architecture

**UI Framework**: Jetpack Compose with Material3
- Main composables are in `app/src/main/java/com/example/stacktracelibrary/`
- Theme configuration in `ui/theme/` (Color.kt, Theme.kt, Type.kt)
- Single activity architecture with `MainActivity` as the entry point

**Package Structure**:
- `io.kseongbin.stacktrace` - Main application code
- `io.kseongbin.stacktrace.ui.theme` - Compose theme components

**Java/Kotlin Compatibility**: Java 11 target for both source and target compatibility

**Compose Compiler**: Kotlin Compose plugin 2.0.21 with compose BOM 2024.09.00

## Development Notes

**Namespace**: `io.kseongbin.stacktrace`

**Min SDK**: API 26 (Android 8.0)
**Target SDK**: API 36
**Compile SDK**: API 36

**ProGuard**: Currently disabled for release builds (can be enabled in `app/build.gradle.kts`)

**Edge-to-Edge Display**: The app uses `enableEdgeToEdge()` for modern Android UI

## Adding Dependencies

Add new dependencies to `gradle/libs.versions.toml`:
1. Define version in `[versions]` section
2. Add library in `[libraries]` section
3. Reference in `app/build.gradle.kts` using `libs.` prefix

Example: `implementation(libs.androidx.core.ktx)`
