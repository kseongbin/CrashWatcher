# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Android Crash & ANR Detection Library** built with Kotlin. This is a library module (not an app) that automatically detects and logs crashes and ANRs with detailed stack traces.

- **Package**: `io.kseongbin.stacktrace`
- **Version**: 1.0.0
- **Min SDK**: API 26 (Android 8.0 Oreo)
- **Target SDK**: API 36
- **Java**: Version 17
- **Kotlin**: 2.0.21
- **AGP**: 8.12.3

## Build System

**Gradle Version Catalog**: Dependencies are managed via `gradle/libs.versions.toml`.

**Build Library (AAR)**:
```bash
# Build both debug and release AAR
./gradlew clean build

# Build release AAR only
./gradlew assembleRelease

# Build debug AAR only
./gradlew assembleDebug
```

**Output**: `app/build/outputs/aar/`
- `stacktrace-debug-1.0.0.aar`
- `stacktrace-release-1.0.0.aar`

## Testing

**Run all tests**:
```bash
./gradlew test
```

**Run unit tests**:
```bash
./gradlew testDebugUnitTest
```

**Run instrumented tests** (requires connected device/emulator):
```bash
./gradlew connectedAndroidTest
```

**Lint checks**:
```bash
./gradlew lint
```

## Library Structure

```
io.kseongbin.stacktrace/
├── CrashLogger.kt              # Main entry point
├── CrashLoggerConfig.kt        # Configuration options
├── internal/
│   ├── CrashDetector.kt        # UncaughtExceptionHandler
│   ├── AnrDetector.kt          # Watchdog for ANR detection
│   ├── LogWriter.kt            # File I/O operations
│   ├── LogFormatter.kt         # Log formatting
│   └── DeviceInfoCollector.kt  # Device metadata collection
└── model/
    ├── CrashInfo.kt
    ├── AnrInfo.kt
    ├── DeviceInfo.kt
    └── AppInfo.kt
```

## Key Features

1. **Runtime On/Off Control**: `CrashLogger.setEnabled(true/false)`
2. **BuildConfig Integration**: Auto-disable in release builds
3. **No Permissions Required**: Uses `getExternalFilesDir()`
4. **Thread-Safe**: Synchronized logging operations
5. **ProGuard Rules**: Auto-applied via `consumer-rules.pro`

## ProGuard Configuration

**Library does NOT minify** (minifyEnabled = false) - user apps handle minification.

**Consumer ProGuard Rules** (`app/consumer-rules.pro`):
- Automatically applied to apps using this library
- Preserves public API and stack trace information
- No manual configuration needed by library users

## Version Management

Update version in `app/build.gradle.kts`:
```kotlin
defaultConfig {
    version = "1.0.0"  // Update here
}
```

AAR filename will automatically update to `stacktrace-{buildType}-{version}.aar`

## Branch Protection

- **main** branch is protected
- Pull requests required for collaborators
- Owner can bypass (for convenience)

## Documentation

- `README.md` - English documentation
- `README_KR.md` - Korean documentation
- Both include usage examples, API reference, and configuration options
