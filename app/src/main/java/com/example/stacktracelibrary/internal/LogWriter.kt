package com.example.stacktracelibrary.internal

import android.content.Context
import com.example.stacktracelibrary.CrashLoggerConfig
import com.example.stacktracelibrary.model.AnrInfo
import com.example.stacktracelibrary.model.CrashInfo
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class LogWriter(
    private val context: Context,
    private val config: CrashLoggerConfig
) {
    private val _logDirectory: File by lazy {
        File(context.getExternalFilesDir(null), "crash_logs").apply {
            if (!exists()) mkdirs()
        }
    }

    private val formatter = LogFormatter()
    private val timestampFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    @Synchronized
    fun writeCrashLog(crashInfo: CrashInfo) {
        try {
            val timestamp = timestampFormat.format(Date(crashInfo.timestamp))
            val logFile = File(_logDirectory, "${config.logFilePrefix}_crash_$timestamp.txt")

            val logContent = formatter.formatCrashLog(crashInfo)
            logFile.writeText(logContent)

            cleanupOldLogs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun writeAnrLog(anrInfo: AnrInfo) {
        try {
            val timestamp = timestampFormat.format(Date(anrInfo.timestamp))
            val logFile = File(_logDirectory, "${config.logFilePrefix}_anr_$timestamp.txt")

            val logContent = formatter.formatAnrLog(anrInfo)
            logFile.writeText(logContent)

            cleanupOldLogs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLogDirectory(): File = _logDirectory

    private fun cleanupOldLogs() {
        try {
            _logDirectory.listFiles()
                ?.sortedByDescending { it.lastModified() }
                ?.drop(config.maxLogFiles)
                ?.forEach { it.delete() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
