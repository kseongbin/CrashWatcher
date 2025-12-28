# Sample App

This is a sample Android app that demonstrates how to use the StackTrace library and test crash detection.

## Features

### Crash Tests
- **NullPointerException**: Trigger a null pointer crash
- **ArrayIndexOutOfBounds**: Trigger an array index crash
- **ArithmeticException**: Trigger a divide by zero crash

### ANR Test
- **Trigger ANR**: Freeze main thread for 5 seconds to trigger ANR detection

### Log Management
- **View Log Files**: Browse and read crash/ANR log files
- **Share Logs**: Share log files with other apps
- **Clear Logs**: Delete all log files

## How to Run

### Method 1: Android Studio
1. Open project in Android Studio
2. Select **sample** module from run configuration dropdown
3. Click **Run** button
4. App will install and launch on your device/emulator

### Method 2: Command Line
```bash
# Build and install
./gradlew :sample:installDebug

# Run on device
adb shell am start -n io.kseongbin.stacktrace.sample/.MainActivity
```

## Testing Crash Detection

1. **Launch the app**
2. **Tap any crash button** (e.g., "💥 NullPointerException")
3. **App will crash and close**
4. **Relaunch the app**
5. **Tap "📄 View Log Files"**
6. **Select the most recent log file** to view crash details

## Testing ANR Detection

1. **Launch the app**
2. **Tap "⏱️ Trigger ANR (5 sec freeze)"**
3. **Wait 5 seconds** (app will appear frozen)
4. **After unfreezing, tap "📄 View Log Files"**
5. **Check for ANR log file** (if detected within 3 seconds)

## Log File Location

```
/sdcard/Android/data/io.kseongbin.stacktrace.sample/files/crash_logs/
```

You can also access logs via:
- Android Studio Device File Explorer
- `adb pull /sdcard/Android/data/io.kseongbin.stacktrace.sample/files/crash_logs/`

## Configuration

The sample app initializes CrashLogger in `SampleApp.kt`:

```kotlin
CrashLogger.initialize(
    context = this,
    config = CrashLoggerConfig(
        enableCrashDetection = true,
        enableAnrDetection = true,
        anrTimeoutMs = 3000L,  // 3 seconds for easier testing
        maxLogFiles = 20,
        includeDeviceInfo = true,
        includeAppInfo = true,
        includeThreadInfo = true
    )
)
```

## Troubleshooting

### Logs not appearing?
- Make sure the app has been relaunched after crash
- Check that CrashLogger is initialized in Application class
- Verify log directory exists: `/sdcard/Android/data/io.kseongbin.stacktrace.sample/files/crash_logs/`

### ANR not detected?
- ANR timeout is set to 3 seconds
- Make sure you wait the full 5 seconds during the freeze
- ANR detection may vary based on device performance

### Can't share logs?
- FileProvider is configured for log sharing
- Make sure you have a file manager or messaging app installed
