# Testing Guide

This guide shows you how to run and understand the tests for the StackTrace library.

## Quick Start

### Run All Tests
```bash
./gradlew test
```

### Run Debug Tests Only
```bash
./gradlew testDebugUnitTest
```

### View Test Report
After running tests, open the HTML report:
```bash
open app/build/reports/tests/testDebugUnitTest/index.html
```

Or manually navigate to:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

---

## Test Structure

The library has **33 unit tests** across 3 test classes:

```
app/src/test/java/io/kseongbin/stacktrace/
├── CrashLoggerTest.kt           (13 tests)
├── internal/
│   ├── CrashDetectorTest.kt     (8 tests)
│   └── LogFormatterTest.kt      (12 tests)
```

---

## Test Examples

### Example 1: Testing CrashLogger Initialization

**File**: `CrashLoggerTest.kt`

```kotlin
@Test
fun `initialize should set isInitialized to true`() {
    // Given: CrashLogger is not initialized
    assertFalse(CrashLogger.isInitialized())

    // When: We initialize CrashLogger
    CrashLogger.initialize(application)

    // Then: It should be initialized
    assertTrue(CrashLogger.isInitialized())
}
```

**What this tests**:
- ✅ CrashLogger properly initializes
- ✅ isInitialized() returns correct state

---

### Example 2: Testing Runtime Enable/Disable

**File**: `CrashLoggerTest.kt`

```kotlin
@Test
fun `setEnabled should update enabled state`() {
    // Given: CrashLogger is initialized and enabled
    CrashLogger.initialize(application)

    // When: We disable it
    CrashLogger.setEnabled(false)

    // Then: It should be disabled
    assertFalse(CrashLogger.isEnabled())

    // When: We enable it again
    CrashLogger.setEnabled(true)

    // Then: It should be enabled
    assertTrue(CrashLogger.isEnabled())
}
```

**What this tests**:
- ✅ Runtime on/off control works correctly
- ✅ State changes are reflected immediately

---

### Example 3: Testing Crash Detection

**File**: `CrashDetectorTest.kt`

```kotlin
@Test
fun `uncaughtException should write crash log when enabled`() {
    // Given: A crash detector and a mock log writer
    val logWriter = mockk<LogWriter>(relaxed = true)
    val crashDetector = CrashDetector(application, config, logWriter)

    // When: An uncaught exception occurs
    val thread = Thread.currentThread()
    val throwable = RuntimeException("Test crash")
    crashDetector.uncaughtException(thread, throwable)

    // Then: It should write a crash log
    verify(exactly = 1) { logWriter.writeCrashLog(any()) }
}
```

**What this tests**:
- ✅ Crash detector captures exceptions
- ✅ Log writer is called with crash info
- Uses **MockK** to verify behavior without actual file I/O

---

### Example 4: Testing Log Formatting

**File**: `LogFormatterTest.kt`

```kotlin
@Test
fun `formatCrashLog should contain crash header`() {
    // Given: A crash info object
    val crashInfo = createSampleCrashInfo()

    // When: We format it as a log
    val result = formatter.formatCrashLog(crashInfo)

    // Then: It should contain the proper headers
    assertTrue(result.contains("=== CRASH LOG ==="))
    assertTrue(result.contains("Type: CRASH"))
    assertTrue(result.contains("Log Version: 1.0"))
}
```

**What this tests**:
- ✅ Log formatting produces correct structure
- ✅ Required headers are present
- ✅ Log version is included

---

## Running Individual Test Classes

### Run Only CrashLoggerTest
```bash
./gradlew test --tests "io.kseongbin.stacktrace.CrashLoggerTest"
```

### Run Only LogFormatterTest
```bash
./gradlew test --tests "io.kseongbin.stacktrace.internal.LogFormatterTest"
```

### Run Only CrashDetectorTest
```bash
./gradlew test --tests "io.kseongbin.stacktrace.internal.CrashDetectorTest"
```

---

## Running Individual Test Methods

### Run a specific test
```bash
./gradlew test --tests "io.kseongbin.stacktrace.CrashLoggerTest.initialize should set isInitialized to true"
```

**Note**: Use the exact test method name (with backticks as shown in the code)

---

## Understanding Test Results

### Console Output
After running tests, you'll see:
```
BUILD SUCCESSFUL in 4s
33 tests completed
0 failures
```

### HTML Report
The HTML report shows:
- ✅ **Passed tests** (green)
- ❌ **Failed tests** (red) with stack traces
- ⏱️ **Execution time** per test
- 📊 **Success rate** percentage

---

## Testing with Android Studio

### Method 1: Run All Tests
1. Right-click on `app/src/test/java` folder
2. Select **"Run 'Tests in 'java''"**

### Method 2: Run Single Test Class
1. Open the test file (e.g., `CrashLoggerTest.kt`)
2. Click the **green play button** next to the class name
3. View results in the Run panel

### Method 3: Run Single Test Method
1. Click the **green play button** next to a specific test method
2. See immediate results in Android Studio

---

## Test Technologies Used

### 1. **JUnit 4**
- Standard Java testing framework
- Provides `@Test`, `@Before`, `@After` annotations
- Assertions like `assertTrue()`, `assertEquals()`

### 2. **Robolectric 4.14**
- Runs Android code on JVM (no emulator needed)
- Provides Android framework classes (Context, Application)
- Fast test execution

### 3. **MockK 1.13.14**
- Kotlin-friendly mocking library
- Creates mock objects for testing
- Verifies method calls with `verify { }`

### 4. **Coroutines Test 1.8.0**
- Testing utilities for Kotlin coroutines
- Not currently used but available for future async tests

---

## Common Test Patterns

### Pattern 1: Given-When-Then
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

### Pattern 2: Using Helper Functions
```kotlin
private fun createSampleCrashInfo(
    throwable: Throwable = RuntimeException("Test"),
    deviceInfo: DeviceInfo? = null
) = CrashInfo(
    timestamp = System.currentTimeMillis(),
    threadName = "main",
    threadId = 1L,
    throwable = throwable,
    deviceInfo = deviceInfo,
    appInfo = null
)
```

### Pattern 3: Mocking with MockK
```kotlin
@Test
fun `test with mock`() {
    val logWriter = mockk<LogWriter>(relaxed = true)

    // Execute code that uses logWriter
    crashDetector.uncaughtException(thread, throwable)

    // Verify interaction
    verify(exactly = 1) { logWriter.writeCrashLog(any()) }
}
```

---

## Troubleshooting

### Issue: Tests not running
**Solution**: Clean and rebuild
```bash
./gradlew clean test
```

### Issue: "No tests found"
**Solution**: Check test file location
- Must be in `app/src/test/java/`
- Must have `@Test` annotation
- Class must end with `Test`

### Issue: Robolectric errors
**Solution**: Check SDK version in `robolectric.properties`
```
sdk=34
```

### Issue: Tests pass locally but fail in CI
**Solution**: Use `--rerun-tasks` to ensure clean execution
```bash
./gradlew test --rerun-tasks
```

---

## Next Steps

### Add More Tests
Consider adding tests for:
- `AnrDetector` - ANR detection logic
- `LogWriter` - File I/O operations
- `DeviceInfoCollector` - Device metadata collection

### Test Coverage
Generate coverage report:
```bash
./gradlew jacocoTestReport
```

View at: `app/build/reports/jacoco/jacocoTestReport/html/index.html`

---

## Summary

**To run tests**:
```bash
./gradlew test
```

**To view results**:
```bash
open app/build/reports/tests/testDebugUnitTest/index.html
```

**Current test count**: 33 tests across 3 classes

**Success rate**: 100% ✅

---

For more information, see:
- [JUnit 4 Documentation](https://junit.org/junit4/)
- [Robolectric Documentation](http://robolectric.org/)
- [MockK Documentation](https://mockk.io/)
