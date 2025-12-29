# 샘플 앱

StackTrace 라이브러리의 사용법과 크래시 감지 기능을 테스트하는 샘플 안드로이드 앱입니다.

## 기능

### 크래시 테스트
- **NullPointerException**: Null 포인터 크래시 발생
- **ArrayIndexOutOfBounds**: 배열 인덱스 초과 크래시 발생
- **ArithmeticException**: 0으로 나누기 크래시 발생

### ANR 테스트
- **ANR 발생**: 메인 스레드를 5초간 정지시켜 ANR 감지 테스트

### 로그 관리
- **로그 파일 보기**: 크래시/ANR 로그 파일 탐색 및 읽기
- **로그 공유**: 로그 파일을 다른 앱과 공유
- **로그 삭제**: 모든 로그 파일 삭제

## 실행 방법

### 방법 1: Android Studio
1. Android Studio에서 프로젝트 열기
2. 실행 구성 드롭다운에서 **sample** 모듈 선택
3. **Run** 버튼 클릭
4. 앱이 기기/에뮬레이터에 설치되고 실행됩니다

### 방법 2: 커맨드 라인
```bash
# 빌드 및 설치
./gradlew :sample:installDebug

# 기기에서 실행
adb shell am start -n io.kseongbin.stacktrace.sample/.MainActivity
```

## 크래시 감지 테스트

1. **앱 실행**
2. **크래시 버튼 탭** (예: "💥 NullPointerException")
3. **앱이 크래시되고 종료됨**
4. **앱 재실행**
5. **"📄 View Log Files" 탭**
6. **가장 최근 로그 파일 선택**하여 크래시 상세 내용 확인

## ANR 감지 테스트

1. **앱 실행**
2. **"⏱️ Trigger ANR (5 sec freeze)" 탭**
3. **5초 대기** (앱이 멈춘 것처럼 보임)
4. **정지 해제 후 "📄 View Log Files" 탭**
5. **ANR 로그 파일 확인** (3초 이내 감지된 경우)

## 로그 파일 위치

```
/sdcard/Android/data/io.kseongbin.stacktrace.sample/files/crash_logs/
```

다음 방법으로도 로그에 접근할 수 있습니다:
- Android Studio Device File Explorer
- `adb pull /sdcard/Android/data/io.kseongbin.stacktrace.sample/files/crash_logs/`

## 설정

샘플 앱은 `SampleApp.kt`에서 CrashLogger를 초기화합니다:

```kotlin
CrashLogger.initialize(
    context = this,
    config = CrashLoggerConfig(
        enableCrashDetection = true,
        enableAnrDetection = true,
        anrTimeoutMs = 3000L,  // 테스트를 위해 3초로 설정
        maxLogFiles = 20,
        includeDeviceInfo = true,
        includeAppInfo = true,
        includeThreadInfo = true
    )
)
```

## 문제 해결

### 로그가 나타나지 않나요?
- 크래시 후 앱을 재실행했는지 확인
- Application 클래스에서 CrashLogger가 초기화되었는지 확인
- 로그 디렉터리 존재 확인: `/sdcard/Android/data/io.kseongbin.stacktrace.sample/files/crash_logs/`

### ANR이 감지되지 않나요?
- ANR 타임아웃이 3초로 설정되어 있습니다
- 정지 중 전체 5초를 기다렸는지 확인
- ANR 감지는 기기 성능에 따라 다를 수 있습니다

### 로그를 공유할 수 없나요?
- FileProvider가 로그 공유를 위해 설정되어 있습니다
- 파일 관리자 또는 메시징 앱이 설치되어 있는지 확인
