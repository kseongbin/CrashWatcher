package io.kseongbin.crashwatcher.internal

import io.kseongbin.crashwatcher.model.AnrInfo
import io.kseongbin.crashwatcher.model.AppInfo
import io.kseongbin.crashwatcher.model.CrashInfo
import io.kseongbin.crashwatcher.model.DeviceInfo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LogFormatterTest {

    private lateinit var formatter: LogFormatter

    @Before
    fun setup() {
        formatter = LogFormatter()
    }

    @Test
    fun `formatCrashLog should contain crash header`() {
        val crashInfo = createSampleCrashInfo()

        val result = formatter.formatCrashLog(crashInfo)

        assertTrue(result.contains("=== CRASH LOG ==="))
        assertTrue(result.contains("Type: CRASH"))
        assertTrue(result.contains("Log Version: 1.0"))
    }

    @Test
    fun `formatCrashLog should contain exception info`() {
        val exception = RuntimeException("Test exception")
        val crashInfo = createSampleCrashInfo(throwable = exception)

        val result = formatter.formatCrashLog(crashInfo)

        assertTrue(result.contains("=== EXCEPTION INFO ==="))
        assertTrue(result.contains("Exception: java.lang.RuntimeException: Test exception"))
        assertTrue(result.contains("Thread: main"))
    }

    @Test
    fun `formatCrashLog should include device info when provided`() {
        val deviceInfo = DeviceInfo(
            manufacturer = "Samsung",
            model = "Galaxy S23",
            brand = "samsung",
            androidVersion = "14",
            apiLevel = 34,
            buildId = "TEST_BUILD",
            availableRamMb = 8192,
            totalStorageGb = 256
        )
        val crashInfo = createSampleCrashInfo(deviceInfo = deviceInfo)

        val result = formatter.formatCrashLog(crashInfo)

        assertTrue(result.contains("=== DEVICE INFO ==="))
        assertTrue(result.contains("Manufacturer: Samsung"))
        assertTrue(result.contains("Model: Galaxy S23"))
        assertTrue(result.contains("Android Version: 14 (API 34)"))
    }

    @Test
    fun `formatCrashLog should include app info when provided`() {
        val appInfo = AppInfo(
            packageName = "com.example.app",
            versionName = "1.0.0",
            versionCode = 1,
            processName = "com.example.app",
            installSource = "com.android.vending"
        )
        val crashInfo = createSampleCrashInfo(appInfo = appInfo)

        val result = formatter.formatCrashLog(crashInfo)

        assertTrue(result.contains("=== APP INFO ==="))
        assertTrue(result.contains("Package: com.example.app"))
        assertTrue(result.contains("Version: 1.0.0 (1)"))
        assertTrue(result.contains("Install Source: com.android.vending"))
    }

    @Test
    fun `formatCrashLog should end with proper footer`() {
        val crashInfo = createSampleCrashInfo()

        val result = formatter.formatCrashLog(crashInfo)

        assertTrue(result.endsWith("=== END LOG ===\n"))
    }

    @Test
    fun `formatAnrLog should contain ANR header`() {
        val anrInfo = createSampleAnrInfo()

        val result = formatter.formatAnrLog(anrInfo)

        assertTrue(result.contains("=== ANR LOG ==="))
        assertTrue(result.contains("Type: ANR"))
        assertTrue(result.contains("Log Version: 1.0"))
    }

    @Test
    fun `formatAnrLog should contain main thread stack trace`() {
        val anrInfo = createSampleAnrInfo()

        val result = formatter.formatAnrLog(anrInfo)

        assertTrue(result.contains("=== MAIN THREAD STACK TRACE ==="))
        assertTrue(result.contains("at "))
    }

    @Test
    fun `formatAnrLog should include all threads when provided`() {
        val thread1 = Thread.currentThread()
        val allThreads = mapOf(
            thread1 to thread1.stackTrace
        )
        val anrInfo = createSampleAnrInfo(allThreadStackTraces = allThreads)

        val result = formatter.formatAnrLog(anrInfo)

        assertTrue(result.contains("=== ALL THREADS"))
        assertTrue(result.contains("Thread: ${thread1.name}"))
    }

    @Test
    fun `formatAnrLog should limit stack trace to 10 elements per thread`() {
        val thread = Thread.currentThread()
        val longStackTrace = Array(20) {
            StackTraceElement("TestClass", "testMethod$it", "TestFile.kt", it)
        }
        val allThreads = mapOf(thread to longStackTrace)
        val anrInfo = createSampleAnrInfo(allThreadStackTraces = allThreads)

        val result = formatter.formatAnrLog(anrInfo)

        assertTrue(result.contains("... 10 more"))
    }

    // Helper functions
    private fun createSampleCrashInfo(
        timestamp: Long = System.currentTimeMillis(),
        threadName: String = "main",
        threadId: Long = 1L,
        throwable: Throwable = RuntimeException("Test"),
        deviceInfo: DeviceInfo? = null,
        appInfo: AppInfo? = null
    ) = CrashInfo(
        timestamp = timestamp,
        threadName = threadName,
        threadId = threadId,
        throwable = throwable,
        deviceInfo = deviceInfo,
        appInfo = appInfo
    )

    private fun createSampleAnrInfo(
        timestamp: Long = System.currentTimeMillis(),
        mainThreadStackTrace: Array<StackTraceElement> = Thread.currentThread().stackTrace,
        allThreadStackTraces: Map<Thread, Array<StackTraceElement>>? = null,
        deviceInfo: DeviceInfo? = null,
        appInfo: AppInfo? = null
    ) = AnrInfo(
        timestamp = timestamp,
        mainThreadStackTrace = mainThreadStackTrace,
        allThreadStackTraces = allThreadStackTraces,
        deviceInfo = deviceInfo,
        appInfo = appInfo
    )
}
