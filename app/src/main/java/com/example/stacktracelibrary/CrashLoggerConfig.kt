package com.example.stacktracelibrary

/**
 * CrashLogger의 설정 옵션
 */
data class CrashLoggerConfig(
    /** 크래시 감지 활성화 (UncaughtExceptionHandler) */
    val enableCrashDetection: Boolean = true,

    /** ANR 감지 활성화 (Watchdog thread) */
    val enableAnrDetection: Boolean = true,

    /** ANR 감지 타임아웃 (밀리초, 기본값 5초) */
    val anrTimeoutMs: Long = 5000L,

    /** 보관할 최대 로그 파일 수 (오래된 것부터 삭제) */
    val maxLogFiles: Int = 10,

    /** 로그 파일명 접두사 */
    val logFilePrefix: String = "crash_log",

    /** 디바이스 정보 포함 (제조사, 모델, Android 버전 등) */
    val includeDeviceInfo: Boolean = true,

    /** 앱 정보 포함 (패키지명, 버전 등) */
    val includeAppInfo: Boolean = true,

    /** ANR 로그에 모든 스레드 스택트레이스 포함 */
    val includeThreadInfo: Boolean = true
)
