package io.kseongbin.stacktrace

import android.content.Context
import io.kseongbin.stacktrace.internal.AnrDetector
import io.kseongbin.stacktrace.internal.CrashDetector
import io.kseongbin.stacktrace.internal.LogWriter
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

    @Volatile
    private var enabled = true

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

        enabled = config.enabled
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

    /**
     * CrashLogger 활성화/비활성화 (런타임 제어)
     *
     * @param enabled true: 로깅 활성화, false: 로깅 비활성화
     */
    @Synchronized
    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    /**
     * CrashLogger 활성화 상태 확인
     *
     * @return true: 활성화, false: 비활성화
     */
    fun isEnabled(): Boolean = enabled
}
