package io.kseongbin.crashwatcher.model

internal data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val brand: String,
    val androidVersion: String,
    val apiLevel: Int,
    val buildId: String,
    val availableRamMb: Long,
    val totalStorageGb: Long
)
