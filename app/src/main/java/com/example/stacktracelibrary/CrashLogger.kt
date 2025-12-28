package com.example.stacktracelibrary

import android.content.Context
import com.example.stacktracelibrary.internal.AnrDetector
import com.example.stacktracelibrary.internal.CrashDetector
import com.example.stacktracelibrary.internal.LogWriter
import java.io.File

/**
 * CrashLogger 라이브러리의 메인 진입점
 *
 * 사용법:
 * ```kotlin
 * class MyApp : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         CrashLogger.initialize(this)
 *     }
 * }
 * ```
 */
object CrashLogger {

    @Volatile
    private var isInitialized = false

    private lateinit var crashDetector: CrashDetector
    private lateinit var anrDetector: AnrDetector
    private lateinit var logWriter: LogWriter

    /**
     * 크래시 및 ANR 감지를 초기화합니다.
     * 여러 번 호출해도 안전합니다 (중복 초기화 방지)
     *
     * @param context Application context
     * @param config 설정 옵션
     */
    @Synchronized
    fun initialize(context: Context, config: CrashLoggerConfig = CrashLoggerConfig()) {
        if (isInitialized) return

        val appContext = context.applicationContext
        logWriter = LogWriter(appContext, config)

        if (config.enableCrashDetection) {
            crashDetector = CrashDetector(appContext, config, logWriter)
            Thread.setDefaultUncaughtExceptionHandler(crashDetector)
        }

        if (config.enableAnrDetection) {
            anrDetector = AnrDetector(appContext, config, logWriter)
            anrDetector.start()
        }

        isInitialized = true
    }

    /**
     * 초기화 여부 확인
     */
    fun isInitialized(): Boolean = isInitialized

    /**
     * 로그 디렉토리 경로 반환
     */
    fun getLogDirectory(): File? = if (::logWriter.isInitialized) {
        logWriter.getLogDirectory()
    } else null

    /**
     * CrashLogger 종료 (ANR 감지 중단)
     */
    @Synchronized
    fun shutdown() {
        if (!isInitialized) return

        if (::anrDetector.isInitialized) {
            anrDetector.stop()
        }

        isInitialized = false
    }
}
