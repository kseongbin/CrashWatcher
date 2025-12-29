# CrashLogger - Android Crash & ANR Detection Library

안드로이드 앱의 크래시와 ANR(Application Not Responding)을 감지하고 로그를 자동으로 저장하는 경량 라이브러리입니다.

[English Documentation](README.md)

## 주요 기능

- **크래시 감지**: 모든 미처리 예외를 자동으로 캡처하여 상세한 스택트레이스 저장
- **ANR 감지**: Watchdog 방식으로 메인 스레드 응답성 모니터링
- **권한 불필요**: 앱 전용 외부 저장소 사용 (getExternalFilesDir)
- **자동 로그 로테이션**: 설정 가능한 최대 파일 수 유지
- **상세한 메타데이터**: 디바이스 정보, 앱 버전, 스레드 상태 등 자동 수집
- **스레드 안전**: 동시성 상황에서도 안전한 로그 작성
- **경량**: 최소한의 의존성, 작은 APK 용량

## 설치

### 1단계: JitPack 저장소 추가

`settings.gradle.kts`에 JitPack 저장소를 추가하세요:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

또는 구버전 프로젝트 구조를 사용하는 경우, 루트 `build.gradle.kts`에 추가:

```kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2단계: 의존성 추가

**Gradle (Kotlin DSL)**

```kotlin
dependencies {
    implementation("com.github.kseongbin:StackTraceLibrary:1.0.0")
}
```

**Gradle (Groovy)**

```groovy
dependencies {
    implementation 'com.github.kseongbin:StackTraceLibrary:1.0.0'
}
```

### 대안: 수동 AAR 설치

[Releases](https://github.com/kseongbin/StackTraceLibrary/releases)에서 최신 AAR을 다운로드하여 프로젝트에 추가:

1. `stacktrace-release-1.0.0.aar` 다운로드
2. `app/libs/` 디렉터리에 파일 배치
3. 의존성 추가:

```kotlin
dependencies {
    implementation(files("libs/stacktrace-release-1.0.0.aar"))
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

`AndroidManifest.xml`에 `Application` 클래스 선언을 잊지 마세요:

```xml
<application
    android:name=".MyApplication"
    ...>
</application>
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

### 런타임 제어 (On/Off)

실행 중에 크래시 로깅을 활성화/비활성화할 수 있습니다:

```kotlin
// 로깅 비활성화 (필요 없을 때)
CrashLogger.setEnabled(false)

// 추적이 필요할 때 로깅 활성화
CrashLogger.setEnabled(true)

// 현재 상태 확인
if (CrashLogger.isEnabled()) {
    // 로깅이 활성화되어 있음
}
```

**권장 방법: BuildConfig 기반 초기화**

```kotlin
// Debug 빌드에서만 자동으로 활성화
CrashLogger.initialize(
    context = this,
    config = CrashLoggerConfig(
        enabled = BuildConfig.DEBUG  // Release는 off, Debug는 on
    )
)
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
| `enabled` | `true` | 라이브러리 전체 활성화/비활성화 (런타임 제어 가능) |
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

## 종료 (선택사항)

필요한 경우 ANR 감지를 중지할 수 있습니다:

```kotlin
CrashLogger.shutdown()
```

## 샘플 앱

`sample/` 디렉토리에 라이브러리 사용법을 보여주는 샘플 Android 앱이 포함되어 있습니다.

자세한 내용은 [sample/README.md](sample/README.md)를 참고하세요.

## 개발 및 테스트

### 테스트 실행

```bash
./gradlew test
```

테스트 리포트 확인:
```bash
open app/build/reports/tests/testDebugUnitTest/index.html
```

**테스트 커버리지**: 33개 단위 테스트 (100% 통과)

자세한 테스트 가이드 및 예시는 [TESTING.md](TESTING.md)를 참고하세요.

### 라이브러리 빌드

**Release AAR 빌드**:
```bash
./gradlew assembleRelease
```

**출력 위치**: `app/build/outputs/aar/`
- `stacktrace-debug-1.0.0.aar`
- `stacktrace-release-1.0.0.aar`

## 라이선스

```
Copyright 2025 Seongbin Kim

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 기여하기

이슈 제보나 Pull Request는 언제나 환영합니다!

1. 저장소 Fork
2. Feature 브랜치 생성 (`git checkout -b feature/AmazingFeature`)
3. 변경사항 커밋 (`git commit -m 'Add some AmazingFeature'`)
4. 브랜치에 Push (`git push origin feature/AmazingFeature`)
5. Pull Request 생성

## 지원

문제가 있거나 질문이 있으시면 GitHub Issue를 생성해주세요.

## 로드맵

- [ ] 원격 크래시 리포팅 통합
- [ ] 커스텀 크래시 콜백
- [ ] Proguard/R8 매핑 지원
- [ ] 성능 메트릭 수집

---
