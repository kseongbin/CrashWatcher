# CrashLogger - Android Crash & ANR Detection Library

안드로이드 앱의 크래시와 ANR(Application Not Responding)을 감지하고 로그를 자동으로 저장하는 경량 라이브러리입니다.

## 주요 기능

- **크래시 감지**: 모든 미처리 예외를 자동으로 캡처하여 상세한 스택트레이스 저장
- **ANR 감지**: Watchdog 방식으로 메인 스레드 응답성 모니터링
- **권한 불필요**: 앱 전용 외부 저장소 사용 (getExternalFilesDir)
- **자동 로그 로테이션**: 설정 가능한 최대 파일 수 유지
- **상세한 메타데이터**: 디바이스 정보, 앱 버전, 스레드 상태 등 자동 수집
- **스레드 안전**: 동시성 상황에서도 안전한 로그 작성
- **경량**: 최소한의 의존성, 작은 APK 용량

## 설치

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.example:crashlogger:1.0.0")
}
```

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.example:crashlogger:1.0.0'
}
```

## 사용법

### 기본 초기화

`Application` 클래스에서 초기화:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashLogger.initialize(this)
    }
}
```

### 커스텀 설정

```kotlin
CrashLogger.initialize(
    context = this,
    config = CrashLoggerConfig(
        enableCrashDetection = true,
        enableAnrDetection = true,
        anrTimeoutMs = 3000L,        // 3초
        maxLogFiles = 20,            // 최근 20개 로그 유지
        logFilePrefix = "myapp_log",
        includeDeviceInfo = true,
        includeAppInfo = true,
        includeThreadInfo = true
    )
)
```

### 로그 파일 접근

```kotlin
val logDirectory = CrashLogger.getLogDirectory()
// 위치: {externalFilesDir}/crash_logs/
```

## 로그 형식

로그는 아래 형식으로 txt 파일로 저장됩니다:

```
crash_log_crash_20251228_153045.txt  // 크래시 로그
crash_log_anr_20251228_153112.txt    // ANR 로그
```

### 크래시 로그 예시

```
=== CRASH LOG ===
Timestamp: 2025-12-28 15:30:45.123
Type: CRASH

=== EXCEPTION INFO ===
Thread: main (id=1)
Exception: java.lang.NullPointerException: ...
Stack Trace:
  at com.example.MainActivity.onCreate(MainActivity.kt:25)
  ...

=== DEVICE INFO ===
Manufacturer: Samsung
Model: SM-G998B
Android Version: 14 (API 34)
Available RAM: 2048 MB
...

=== APP INFO ===
Package: com.example.myapp
Version: 1.0.0 (1)
...
```

## 설정 옵션

| 옵션 | 기본값 | 설명 |
|------|--------|------|
| `enableCrashDetection` | `true` | 크래시 로깅 활성화 |
| `enableAnrDetection` | `true` | ANR 감지 활성화 |
| `anrTimeoutMs` | `5000` | ANR 감지 임계값 (밀리초) |
| `maxLogFiles` | `10` | 보관할 최대 로그 파일 수 |
| `logFilePrefix` | `"crash_log"` | 로그 파일명 접두사 |
| `includeDeviceInfo` | `true` | 디바이스 메타데이터 포함 |
| `includeAppInfo` | `true` | 앱 버전 정보 포함 |
| `includeThreadInfo` | `true` | 모든 스레드 덤프 포함 (ANR) |

## 시스템 요구사항

- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 36
- **권한**: 불필요

## 동작 원리

### 크래시 감지
- `Thread.UncaughtExceptionHandler` 구현
- 기존 핸들러로 체이닝하여 정상적인 크래시 동작 유지
- 앱 종료 전에 동기식으로 로그 작성

### ANR 감지
- Watchdog 스레드가 메인 스레드에 tick 업데이트 요청
- 타임아웃 내에 응답이 없으면 ANR 감지
- 메인 스레드 스택트레이스 + 모든 스레드 덤프 캡처

## 스레드 안전성

- 모든 로그 쓰기는 synchronized
- ANR watchdog은 atomic counter 사용
- 멀티스레드 환경에서 안전

## 라이선스

Apache License 2.0

## 기여하기

이슈 제보나 Pull Request는 언제나 환영합니다!

## 지원

문제가 있거나 질문이 있으시면 GitHub Issue를 생성해주세요.
