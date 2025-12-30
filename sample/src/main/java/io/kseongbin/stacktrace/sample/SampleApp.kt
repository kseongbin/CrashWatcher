package io.kseongbin.crashwatcher.sample

import android.app.Application
import io.kseongbin.crashwatcher.CrashLogger
import io.kseongbin.crashwatcher.CrashLoggerConfig

class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize CrashLogger
        CrashLogger.initialize(
            context = this,
            config = CrashLoggerConfig(
                enableCrashDetection = true,
                enableAnrDetection = true,
                anrTimeoutMs = 3000L,  // 3 seconds for easier testing
                maxLogFiles = 20,
                includeDeviceInfo = true,
                includeAppInfo = true,
                includeThreadInfo = true
            )
        )
    }
}
