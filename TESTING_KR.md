# 테스트 가이드

StackTrace 라이브러리의 테스트 가이드입니다.

[English Documentation](TESTING.md)

## 빠른 시작

**모든 테스트 실행:**
```bash
./gradlew test
```

**결과 확인:**
```bash
open app/build/reports/tests/testDebugUnitTest/index.html
```

**현재 상태:** 33개 테스트, 100% 통과 ✅

## 테스트 구조

```
app/src/test/java/io/kseongbin/stacktrace/
├── CrashLoggerTest.kt           (13 테스트)
├── internal/
│   ├── CrashDetectorTest.kt     (8 테스트)
│   └── LogFormatterTest.kt      (12 테스트)
```

## 테스트 실행

### 전체 테스트
```bash
./gradlew test                    # 모든 테스트 (debug + release)
./gradlew testDebugUnitTest       # Debug 테스트만
./gradlew testReleaseUnitTest     # Release 테스트만
```

### 특정 테스트 클래스
```bash
./gradlew test --tests "io.kseongbin.stacktrace.CrashLoggerTest"
./gradlew test --tests "*.internal.LogFormatterTest"
```

### 특정 테스트 메서드
```bash
./gradlew test --tests "*.CrashLoggerTest.initialize should set isInitialized to true"
```

### 깨끗한 재빌드
```bash
./gradlew clean test              # 빌드 디렉토리 먼저 삭제
./gradlew test --rerun-tasks      # 모든 작업 강제 재실행
```

## 테스트 기술

| 기술 | 버전 | 용도 |
|------|------|------|
| **JUnit 4** | 4.13.2 | 테스트 프레임워크 |
| **Robolectric** | 4.14 | JVM에서 Android 실행 (에뮬레이터 불필요) |
| **MockK** | 1.13.14 | Kotlin Mocking 라이브러리 |
| **Coroutines Test** | 1.8.0 | 비동기 테스트 유틸리티 |

## 테스트 작성

### Given-When-Then 패턴
```kotlin
@Test
fun `테스트 설명`() {
    // Given: 초기 상태 설정
    val config = CrashLoggerConfig(enabled = false)

    // When: 액션 수행
    CrashLogger.initialize(application, config)

    // Then: 결과 검증
    assertFalse(CrashLogger.isEnabled())
}
```

### MockK를 사용한 Mocking
```kotlin
@Test
fun `크래시 로그 작성 검증`() {
    val logWriter = mockk<LogWriter>(relaxed = true)
    val detector = CrashDetector(application, config, logWriter)

    detector.uncaughtException(thread, RuntimeException("Test"))

    verify(exactly = 1) { logWriter.writeCrashLog(any()) }
}
```

### 헬퍼 함수
```kotlin
private fun createSampleCrashInfo(
    throwable: Throwable = RuntimeException("Test")
) = CrashInfo(
    timestamp = System.currentTimeMillis(),
    threadName = "main",
    threadId = 1L,
    throwable = throwable,
    deviceInfo = null,
    appInfo = null
)
```

## Android Studio

### 모든 테스트 실행
`app/src/test/java` 우클릭 → **"Run 'Tests in 'java''"**

### 단일 테스트 실행
클래스/메서드명 옆 **녹색 실행 버튼** 클릭

### 결과 확인
하단의 **Run** 패널에서 테스트 결과 확인

## 테스트 리포트

### HTML 리포트 위치
```
app/build/reports/tests/testDebugUnitTest/index.html
```

### 포함된 내용
- ✅ 통과/실패 테스트 개수
- ⏱️ 테스트별 실행 시간
- 📊 성공률 퍼센티지
- 🔍 실패 시 스택 트레이스
- 📁 패키지/클래스 계층 구조

### Release 테스트 리포트
```
app/build/reports/tests/testReleaseUnitTest/index.html
```

## 문제 해결

### 테스트가 실행되지 않음
```bash
./gradlew clean test
```

### "No Tests Found" 오류
- 파일이 `app/src/test/java/`에 있어야 함
- 클래스명이 `Test`로 끝나야 함
- 메서드에 `@Test` 어노테이션 필요

### Robolectric 오류
`app/src/test/resources/robolectric.properties` 확인:
```
sdk=34
```

### CI/CD 실패
```bash
./gradlew test --rerun-tasks --no-build-cache
```

## 테스트 커버리지

### 커버리지 리포트 생성
```bash
./gradlew jacocoTestReport
```

### 리포트 확인
```bash
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

## 다음 단계

**추가 테스트 작성 대상:**
- `AnrDetector` - ANR 감지 로직
- `LogWriter` - 파일 I/O 작업
- `DeviceInfoCollector` - 디바이스 메타데이터

**커버리지 개선:**
- 엣지 케이스와 오류 조건
- 스레드 안전성 시나리오
- 설정 조합 테스트

## 참고 자료

- [JUnit 4 문서](https://junit.org/junit4/)
- [Robolectric 문서](http://robolectric.org/)
- [MockK 문서](https://mockk.io/)
- [Android 테스트 가이드](https://developer.android.com/training/testing)
