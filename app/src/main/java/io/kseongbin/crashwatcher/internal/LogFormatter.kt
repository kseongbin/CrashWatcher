package io.kseongbin.crashwatcher.internal

import io.kseongbin.crashwatcher.model.AnrInfo
import io.kseongbin.crashwatcher.model.AppInfo
import io.kseongbin.crashwatcher.model.CrashInfo
import io.kseongbin.crashwatcher.model.DeviceInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class LogFormatter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    fun formatCrashLog(crashInfo: CrashInfo): String = buildString {
        appendLine("=== CRASH LOG ===")
        appendLine("Timestamp: ${dateFormat.format(Date(crashInfo.timestamp))}")
        appendLine("Type: CRASH")
        appendLine("Log Version: 1.0")
        appendLine()

        appendLine("=== EXCEPTION INFO ===")
        appendLine("Thread: ${crashInfo.threadName} (id=${crashInfo.threadId})")
        appendLine("Exception: ${crashInfo.throwable.javaClass.name}: ${crashInfo.throwable.message}")
        appendLine("Stack Trace:")
        crashInfo.throwable.stackTrace.forEach { element ->
            appendLine("  at $element")
        }
        appendLine()

        crashInfo.deviceInfo?.let {
            appendDeviceInfo(it)
        }

        crashInfo.appInfo?.let {
            appendAppInfo(it)
        }

        appendLine("=== END LOG ===")
    }

    fun formatAnrLog(anrInfo: AnrInfo): String = buildString {
        appendLine("=== ANR LOG ===")
        appendLine("Timestamp: ${dateFormat.format(Date(anrInfo.timestamp))}")
        appendLine("Type: ANR")
        appendLine("Log Version: 1.0")
        appendLine()

        appendLine("=== MAIN THREAD STACK TRACE ===")
        anrInfo.mainThreadStackTrace.forEach { element ->
            appendLine("  at $element")
        }
        appendLine()

        anrInfo.allThreadStackTraces?.let { traces ->
            appendLine("=== ALL THREADS (${traces.size} total) ===")
            traces.forEach { (thread, stackTrace) ->
                appendLine("Thread: ${thread.name} (id=${thread.id}, state=${thread.state})")
                stackTrace.take(10).forEach { element ->
                    appendLine("  at $element")
                }
                if (stackTrace.size > 10) {
                    appendLine("  ... ${stackTrace.size - 10} more")
                }
                appendLine()
            }
        }

        anrInfo.deviceInfo?.let {
            appendDeviceInfo(it)
        }

        anrInfo.appInfo?.let {
            appendAppInfo(it)
        }

        appendLine("=== END LOG ===")
    }

    private fun StringBuilder.appendDeviceInfo(deviceInfo: DeviceInfo) {
        appendLine("=== DEVICE INFO ===")
        appendLine("Manufacturer: ${deviceInfo.manufacturer}")
        appendLine("Model: ${deviceInfo.model}")
        appendLine("Brand: ${deviceInfo.brand}")
        appendLine("Android Version: ${deviceInfo.androidVersion} (API ${deviceInfo.apiLevel})")
        appendLine("Build ID: ${deviceInfo.buildId}")
        appendLine("Available RAM: ${deviceInfo.availableRamMb} MB")
        appendLine("Total Storage: ${deviceInfo.totalStorageGb} GB")
        appendLine()
    }

    private fun StringBuilder.appendAppInfo(appInfo: AppInfo) {
        appendLine("=== APP INFO ===")
        appendLine("Package: ${appInfo.packageName}")
        appendLine("Version: ${appInfo.versionName} (${appInfo.versionCode})")
        appendLine("Process: ${appInfo.processName}")
        appInfo.installSource?.let {
            appendLine("Install Source: $it")
        }
        appendLine()
    }
}
