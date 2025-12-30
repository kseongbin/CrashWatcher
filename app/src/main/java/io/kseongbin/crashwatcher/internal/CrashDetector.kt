package io.kseongbin.crashwatcher.internal

import android.content.Context
import io.kseongbin.crashwatcher.CrashLogger
import io.kseongbin.crashwatcher.CrashLoggerConfig
import io.kseongbin.crashwatcher.model.CrashInfo

internal class CrashDetector(
    private val context: Context,
    private val config: CrashLoggerConfig,
    private val logWriter: LogWriter
) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private val deviceInfoCollector = DeviceInfoCollector(context)

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            // Check if CrashLogger is enabled
            if (CrashLogger.isEnabled()) {
                val crashInfo = CrashInfo(
                    timestamp = System.currentTimeMillis(),
                    threadName = thread.name,
                    threadId = thread.id,
                    throwable = throwable,
                    deviceInfo = if (config.includeDeviceInfo) {
                        deviceInfoCollector.collectDeviceInfo()
                    } else null,
                    appInfo = if (config.includeAppInfo) {
                        deviceInfoCollector.collectAppInfo()
                    } else null
                )

                logWriter.writeCrashLog(crashInfo)
            }
        } catch (e: Exception) {
            // Fail silently to avoid secondary crashes
            e.printStackTrace()
        } finally {
            // Chain to original handler to maintain normal crash behavior
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
