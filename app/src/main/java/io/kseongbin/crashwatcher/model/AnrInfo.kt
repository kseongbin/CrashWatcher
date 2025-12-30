package io.kseongbin.crashwatcher.model

internal data class AnrInfo(
    val timestamp: Long,
    val mainThreadStackTrace: Array<StackTraceElement>,
    val allThreadStackTraces: Map<Thread, Array<StackTraceElement>>?,
    val deviceInfo: DeviceInfo?,
    val appInfo: AppInfo?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnrInfo

        if (timestamp != other.timestamp) return false
        if (!mainThreadStackTrace.contentEquals(other.mainThreadStackTrace)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + mainThreadStackTrace.contentHashCode()
        return result
    }
}
