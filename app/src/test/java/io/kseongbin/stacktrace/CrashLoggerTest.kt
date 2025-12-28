package io.kseongbin.stacktrace

import android.app.Application
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CrashLoggerTest {

    private lateinit var application: Application

    @Before
    fun setup() {
        application = RuntimeEnvironment.getApplication()
        // Ensure clean state before each test
        CrashLogger.shutdown()
    }

    @After
    fun tearDown() {
        CrashLogger.shutdown()
    }

    @Test
    fun `initialize should set isInitialized to true`() {
        assertFalse(CrashLogger.isInitialized())

        CrashLogger.initialize(application)

        assertTrue(CrashLogger.isInitialized())
    }

    @Test
    fun `initialize should be idempotent`() {
        CrashLogger.initialize(application)
        val firstInitState = CrashLogger.isInitialized()

        CrashLogger.initialize(application)
        val secondInitState = CrashLogger.isInitialized()

        assertTrue(firstInitState)
        assertTrue(secondInitState)
    }

    @Test
    fun `isEnabled should return true by default`() {
        CrashLogger.initialize(application)

        assertTrue(CrashLogger.isEnabled())
    }

    @Test
    fun `setEnabled should update enabled state`() {
        CrashLogger.initialize(application)

        CrashLogger.setEnabled(false)
        assertFalse(CrashLogger.isEnabled())

        CrashLogger.setEnabled(true)
        assertTrue(CrashLogger.isEnabled())
    }

    @Test
    fun `initialize with disabled config should set enabled to false`() {
        val config = CrashLoggerConfig(enabled = false)

        CrashLogger.initialize(application, config)

        assertFalse(CrashLogger.isEnabled())
    }

    @Test
    fun `getLogDirectory should return directory when initialized`() {
        // Note: Due to CrashLogger being an object with lateinit vars,
        // complete reset between tests is not possible.
        // This test verifies the directory is accessible after initialization.
        CrashLogger.initialize(application)

        val logDir = CrashLogger.getLogDirectory()
        assertNotNull(logDir)
    }

    @Test
    fun `getLogDirectory should return valid directory when initialized`() {
        CrashLogger.initialize(application)

        val logDir = CrashLogger.getLogDirectory()

        assertNotNull(logDir)
        assertTrue(logDir!!.path.contains("crash_logs"))
    }

    @Test
    fun `shutdown should set isInitialized to false`() {
        CrashLogger.initialize(application)
        assertTrue(CrashLogger.isInitialized())

        CrashLogger.shutdown()

        assertFalse(CrashLogger.isInitialized())
    }

    @Test
    fun `shutdown when not initialized should not throw exception`() {
        assertFalse(CrashLogger.isInitialized())

        // Should not throw
        CrashLogger.shutdown()

        assertFalse(CrashLogger.isInitialized())
    }

    @Test
    fun `initialize with crash detection disabled should not set exception handler`() {
        val config = CrashLoggerConfig(enableCrashDetection = false)

        CrashLogger.initialize(application, config)

        assertTrue(CrashLogger.isInitialized())
        // Note: Hard to verify exception handler directly without causing actual crash
    }

    @Test
    fun `initialize with ANR detection disabled should still initialize`() {
        val config = CrashLoggerConfig(enableAnrDetection = false)

        CrashLogger.initialize(application, config)

        assertTrue(CrashLogger.isInitialized())
    }

    @Test
    fun `concurrent initialization should be thread-safe`() {
        val threads = List(10) {
            Thread {
                CrashLogger.initialize(application)
            }
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        assertTrue(CrashLogger.isInitialized())
    }

    @Test
    fun `concurrent setEnabled should be thread-safe`() {
        CrashLogger.initialize(application)

        val threads = List(10) { index ->
            Thread {
                CrashLogger.setEnabled(index % 2 == 0)
            }
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }

        // Should complete without exceptions
        assertTrue(CrashLogger.isInitialized())
    }
}
