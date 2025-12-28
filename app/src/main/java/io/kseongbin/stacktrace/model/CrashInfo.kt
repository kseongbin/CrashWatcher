package io.kseongbin.stacktrace.model

internal data class CrashInfo(
    val timestamp: Long,
    val threadName: String,
    val threadId: Long,
    val throwable: Throwable,
    val deviceInfo: DeviceInfo?,
    val appInfo: AppInfo?
)
