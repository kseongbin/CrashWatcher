# Testing Guide

Complete testing guide for the StackTrace library.

[한국어 문서](TESTING_KR.md)

## Quick Start

**Run all tests:**
```bash
./gradlew test
```

**View results:**
```bash
open app/build/reports/tests/testDebugUnitTest/index.html
```

**Current status:** 33 tests, 100% passing ✅

## Test Structure

```
app/src/test/java/io/kseongbin/stacktrace/
├── CrashLoggerTest.kt           (13 tests)
├── internal/
│   ├── CrashDetectorTest.kt     (8 tests)
│   └── LogFormatterTest.kt      (12 tests)
```

## Running Tests

### All Tests
```bash
./gradlew test                    # All tests (debug + release)
./gradlew testDebugUnitTest       # Debug tests only
./gradlew testReleaseUnitTest     # Release tests only
```

### Specific Test Class
```bash
./gradlew test --tests "io.kseongbin.stacktrace.CrashLoggerTest"
./gradlew test --tests "*.internal.LogFormatterTest"
```

### Specific Test Method
```bash
./gradlew test --tests "*.CrashLoggerTest.initialize should set isInitialized to true"
```

### Clean Rebuild
```bash
./gradlew clean test              # Clean build directory first
./gradlew test --rerun-tasks      # Force rerun all tasks
```

## Test Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **JUnit 4** | 4.13.2 | Testing framework |
| **Robolectric** | 4.14 | Android on JVM (no emulator) |
| **MockK** | 1.13.14 | Kotlin mocking library |
| **Coroutines Test** | 1.8.0 | Async testing utilities |

## Writing Tests

### Given-When-Then Pattern
```kotlin
@Test
fun `test description`() {
    // Given: Setup initial state
    val config = CrashLoggerConfig(enabled = false)

    // When: Perform action
    CrashLogger.initialize(application, config)

    // Then: Verify outcome
    assertFalse(CrashLogger.isEnabled())
}
```

### Mocking with MockK
```kotlin
@Test
fun `verify crash log write`() {
    val logWriter = mockk<LogWriter>(relaxed = true)
    val detector = CrashDetector(application, config, logWriter)

    detector.uncaughtException(thread, RuntimeException("Test"))

    verify(exactly = 1) { logWriter.writeCrashLog(any()) }
}
```

### Helper Functions
```kotlin
private fun createSampleCrashInfo(
    throwable: Throwable = RuntimeException("Test")
) = CrashInfo(
    timestamp = System.currentTimeMillis(),
    threadName = "main",
    threadId = 1L,
    throwable = throwable,
    deviceInfo = null,
    appInfo = null
)
```

## Android Studio

### Run All Tests
Right-click `app/src/test/java` → **"Run 'Tests in 'java''"**

### Run Single Test
Click **green play button** next to class/method name

### View Results
Check **Run** panel at the bottom for test results

## Test Reports

### HTML Report Location
```
app/build/reports/tests/testDebugUnitTest/index.html
```

### What's Included
- ✅ Passed/failed test counts
- ⏱️ Execution time per test
- 📊 Success rate percentage
- 🔍 Stack traces for failures
- 📁 Package/class hierarchy

### Release Test Report
```
app/build/reports/tests/testReleaseUnitTest/index.html
```

## Troubleshooting

### Tests Not Running
```bash
./gradlew clean test
```

### "No Tests Found"
- File must be in `app/src/test/java/`
- Class must end with `Test`
- Methods must have `@Test` annotation

### Robolectric Errors
Check `app/src/test/resources/robolectric.properties`:
```
sdk=34
```

### CI/CD Failures
```bash
./gradlew test --rerun-tasks --no-build-cache
```

## Test Coverage

### Generate Coverage Report
```bash
./gradlew jacocoTestReport
```

### View Report
```bash
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

## Next Steps

**Add more tests for:**
- `AnrDetector` - ANR detection logic
- `LogWriter` - File I/O operations
- `DeviceInfoCollector` - Device metadata

**Improve coverage:**
- Edge cases and error conditions
- Thread safety scenarios
- Configuration variations

## Resources

- [JUnit 4 Documentation](https://junit.org/junit4/)
- [Robolectric Documentation](http://robolectric.org/)
- [MockK Documentation](https://mockk.io/)
- [Android Testing Guide](https://developer.android.com/training/testing)
