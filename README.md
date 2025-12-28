# CrashLogger - Android Crash & ANR Detection Library

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange.svg)](https://developer.android.com/about/versions/oreo)

A lightweight Android library that automatically detects and logs crashes and ANRs (Application Not Responding) with detailed stack traces and device information.

[한국어 문서](README_KR.md)

## Features

- **Crash Detection**: Automatically captures all uncaught exceptions with detailed stack traces
- **ANR Detection**: Monitors main thread responsiveness using watchdog pattern
- **No Permissions Required**: Uses app-specific external storage (`getExternalFilesDir`)
- **Automatic Log Rotation**: Maintains configurable maximum number of log files
- **Rich Metadata**: Automatically collects device info, app version, thread states, and more
- **Thread-Safe**: Safe log writing in concurrent environments
- **Lightweight**: Minimal dependencies and small APK footprint

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.example:crashlogger:1.0.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.example:crashlogger:1.0.0'
}
```

## Quick Start

### Basic Initialization

Initialize in your `Application` class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashLogger.initialize(this)
    }
}
```

Don't forget to declare your `Application` class in `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApplication"
    ...>
</application>
```

### Custom Configuration

```kotlin
CrashLogger.initialize(
    context = this,
    config = CrashLoggerConfig(
        enableCrashDetection = true,
        enableAnrDetection = true,
        anrTimeoutMs = 3000L,        // 3 seconds
        maxLogFiles = 20,            // Keep recent 20 logs
        logFilePrefix = "myapp_log",
        includeDeviceInfo = true,
        includeAppInfo = true,
        includeThreadInfo = true
    )
)
```

### Accessing Log Files

```kotlin
val logDirectory = CrashLogger.getLogDirectory()
// Location: {externalFilesDir}/crash_logs/
```

## Log Format

Logs are saved as text files with the following naming pattern:

```
crash_log_crash_20251228_153045.txt  // Crash log
crash_log_anr_20251228_153112.txt    // ANR log
```

### Sample Crash Log

```
=== CRASH LOG ===
Timestamp: 2025-12-28 15:30:45.123
Type: CRASH

=== EXCEPTION INFO ===
Thread: main (id=1)
Exception: java.lang.NullPointerException: ...
Stack Trace:
  at com.example.MainActivity.onCreate(MainActivity.kt:25)
  ...

=== DEVICE INFO ===
Manufacturer: Samsung
Model: SM-G998B
Android Version: 14 (API 34)
Available RAM: 2048 MB
...

=== APP INFO ===
Package: com.example.myapp
Version: 1.0.0 (1)
...
```

## Configuration Options

| Option | Default | Description |
|--------|---------|-------------|
| `enableCrashDetection` | `true` | Enable crash logging |
| `enableAnrDetection` | `true` | Enable ANR detection |
| `anrTimeoutMs` | `5000` | ANR detection threshold (milliseconds) |
| `maxLogFiles` | `10` | Maximum number of log files to keep |
| `logFilePrefix` | `"crash_log"` | Prefix for log file names |
| `includeDeviceInfo` | `true` | Include device metadata |
| `includeAppInfo` | `true` | Include app version info |
| `includeThreadInfo` | `true` | Include all thread dumps (for ANR) |

## Requirements

- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 36
- **Permissions**: None required

## How It Works

### Crash Detection
- Implements `Thread.UncaughtExceptionHandler`
- Chains with existing handler to maintain normal crash behavior
- Writes logs synchronously before app termination

### ANR Detection
- Watchdog thread requests tick updates from main thread
- Detects ANR if no response within timeout period
- Captures main thread stack trace + all thread dumps

## Thread Safety

- All log writes are synchronized
- ANR watchdog uses atomic counters
- Safe in multi-threaded environments

## Shutdown (Optional)

You can optionally stop ANR detection when needed:

```kotlin
CrashLogger.shutdown()
```

## License

```
Copyright 2025 Seongbin Kim

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Contributing

Issues and Pull Requests are always welcome!

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Support

If you have any questions or encounter issues, please create a GitHub Issue.

## Roadmap

- [ ] Remote crash reporting integration
- [ ] Custom crash callbacks
- [ ] Proguard/R8 mapping support
- [ ] Performance metrics collection

---

**Made with ❤️ for Android developers**
