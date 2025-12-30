package io.kseongbin.crashwatcher.model

internal data class AppInfo(
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val processName: String,
    val installSource: String?
)
