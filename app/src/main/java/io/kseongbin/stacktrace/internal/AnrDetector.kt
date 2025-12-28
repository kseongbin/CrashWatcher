package io.kseongbin.stacktrace.internal

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import io.kseongbin.stacktrace.CrashLogger
import io.kseongbin.stacktrace.CrashLoggerConfig
import io.kseongbin.stacktrace.model.AnrInfo
import java.util.concurrent.atomic.AtomicLong

internal class AnrDetector(
    private val context: Context,
    private val config: CrashLoggerConfig,
    private val logWriter: LogWriter
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val watchdogThread = HandlerThread("CrashLogger-AnrWatchdog")
    private lateinit var watchdogHandler: Handler

    private val tick = AtomicLong(0)
    private val reported = AtomicLong(0)
    private val deviceInfoCollector = DeviceInfoCollector(context)

    @Volatile
    private var isRunning = false

    fun start() {
        if (isRunning) return

        watchdogThread.start()
        watchdogHandler = Handler(watchdogThread.looper)
        isRunning = true
        scheduleCheck()
    }

    fun stop() {
        isRunning = false
        watchdogThread.quitSafely()
    }

    private fun scheduleCheck() {
        if (!isRunning) return

        val currentTick = tick.get()

        // Post tick increment to main thread
        mainHandler.post { tick.incrementAndGet() }

        // Check from watchdog thread after timeout
        watchdogHandler.postDelayed({
            if (tick.get() == currentTick && reported.get() != currentTick) {
                // Main thread hasn't responded - ANR detected
                reported.set(currentTick)
                detectAnr()
            }
            scheduleCheck() // Continue monitoring
        }, config.anrTimeoutMs)
    }

    private fun detectAnr() {
        try {
            // Check if CrashLogger is enabled
            if (!CrashLogger.isEnabled()) return

            val mainThread = Looper.getMainLooper().thread
            val anrInfo = AnrInfo(
                timestamp = System.currentTimeMillis(),
                mainThreadStackTrace = mainThread.stackTrace,
                allThreadStackTraces = if (config.includeThreadInfo) {
                    Thread.getAllStackTraces()
                } else null,
                deviceInfo = if (config.includeDeviceInfo) {
                    deviceInfoCollector.collectDeviceInfo()
                } else null,
                appInfo = if (config.includeAppInfo) {
                    deviceInfoCollector.collectAppInfo()
                } else null
            )

            logWriter.writeAnrLog(anrInfo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
