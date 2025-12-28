package io.kseongbin.stacktrace.internal

import android.app.Application
import io.kseongbin.stacktrace.CrashLogger
import io.kseongbin.stacktrace.CrashLoggerConfig
import io.mockk.*
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
class CrashDetectorTest {

    private lateinit var application: Application
    private lateinit var config: CrashLoggerConfig
    private lateinit var logWriter: LogWriter
    private lateinit var crashDetector: CrashDetector
    private lateinit var defaultHandler: Thread.UncaughtExceptionHandler

    @Before
    fun setup() {
        application = RuntimeEnvironment.getApplication()
        config = CrashLoggerConfig()
        logWriter = mockk(relaxed = true)

        defaultHandler = mockk(relaxed = true)
        Thread.setDefaultUncaughtExceptionHandler(defaultHandler)

        crashDetector = CrashDetector(application, config, logWriter)

        // Initialize CrashLogger for isEnabled() check
        CrashLogger.initialize(application)
    }

    @After
    fun tearDown() {
        CrashLogger.shutdown()
        clearAllMocks()
    }

    @Test
    fun `uncaughtException should write crash log when enabled`() {
        val thread = Thread.currentThread()
        val throwable = RuntimeException("Test crash")

        crashDetector.uncaughtException(thread, throwable)

        verify(exactly = 1) { logWriter.writeCrashLog(any()) }
    }

    @Test
    fun `uncaughtException should call default handler`() {
        val thread = Thread.currentThread()
        val throwable = RuntimeException("Test crash")

        crashDetector.uncaughtException(thread, throwable)

        verify(exactly = 1) { defaultHandler.uncaughtException(thread, throwable) }
    }

    @Test
    fun `uncaughtException should not throw when logWriter fails`() {
        val thread = Thread.currentThread()
        val throwable = RuntimeException("Test crash")

        every { logWriter.writeCrashLog(any()) } throws Exception("Write failed")

        // Should not throw
        crashDetector.uncaughtException(thread, throwable)

        // Should still call default handler
        verify(exactly = 1) { defaultHandler.uncaughtException(thread, throwable) }
    }

    @Test
    fun `uncaughtException should not write log when CrashLogger is disabled`() {
        CrashLogger.setEnabled(false)

        val thread = Thread.currentThread()
        val throwable = RuntimeException("Test crash")

        crashDetector.uncaughtException(thread, throwable)

        verify(exactly = 0) { logWriter.writeCrashLog(any()) }
    }

    @Test
    fun `uncaughtException should capture correct thread info`() {
        val thread = Thread.currentThread()
        val throwable = RuntimeException("Test crash")

        crashDetector.uncaughtException(thread, throwable)

        verify {
            logWriter.writeCrashLog(match {
                it.threadName == thread.name && it.threadId == thread.id
            })
        }
    }

    @Test
    fun `uncaughtException should capture throwable`() {
        val thread = Thread.currentThread()
        val throwable = RuntimeException("Test crash message")

        crashDetector.uncaughtException(thread, throwable)

        verify {
            logWriter.writeCrashLog(match {
                it.throwable.message == "Test crash message"
            })
        }
    }

    @Test
    fun `uncaughtException should include device info when configured`() {
        val configWithDeviceInfo = CrashLoggerConfig(includeDeviceInfo = true)
        val detectorWithDeviceInfo = CrashDetector(application, configWithDeviceInfo, logWriter)

        val thread = Thread.currentThread()
        val throwable = RuntimeException("Test crash")

        detectorWithDeviceInfo.uncaughtException(thread, throwable)

        verify {
            logWriter.writeCrashLog(match {
                it.deviceInfo != null
            })
        }
    }

    @Test
    fun `uncaughtException should exclude device info when not configured`() {
        val configWithoutDeviceInfo = CrashLoggerConfig(includeDeviceInfo = false)
        val detectorWithoutDeviceInfo = CrashDetector(application, configWithoutDeviceInfo, logWriter)

        val thread = Thread.currentThread()
        val throwable = RuntimeException("Test crash")

        detectorWithoutDeviceInfo.uncaughtException(thread, throwable)

        verify {
            logWriter.writeCrashLog(match {
                it.deviceInfo == null
            })
        }
    }

    @Test
    fun `uncaughtException should include app info when configured`() {
        val configWithAppInfo = CrashLoggerConfig(includeAppInfo = true)
        val detectorWithAppInfo = CrashDetector(application, configWithAppInfo, logWriter)

        val thread = Thread.currentThread()
        val throwable = RuntimeException("Test crash")

        detectorWithAppInfo.uncaughtException(thread, throwable)

        verify {
            logWriter.writeCrashLog(match {
                it.appInfo != null
            })
        }
    }

    @Test
    fun `uncaughtException should exclude app info when not configured`() {
        val configWithoutAppInfo = CrashLoggerConfig(includeAppInfo = false)
        val detectorWithoutAppInfo = CrashDetector(application, configWithoutAppInfo, logWriter)

        val thread = Thread.currentThread()
        val throwable = RuntimeException("Test crash")

        detectorWithoutAppInfo.uncaughtException(thread, throwable)

        verify {
            logWriter.writeCrashLog(match {
                it.appInfo == null
            })
        }
    }
}
