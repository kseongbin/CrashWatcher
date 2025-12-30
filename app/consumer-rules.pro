# 사용자 앱에 자동 적용될 ProGuard 규칙

# 크래시 스택트레이스 보존
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions

# CrashLogger Public API 보존
-keep class io.kseongbin.crashwatcher.CrashLogger { *; }
-keep class io.kseongbin.crashwatcher.CrashLoggerConfig { *; }

# Model 클래스 보존 (로그 데이터 직렬화용)
-keep class io.kseongbin.crashwatcher.model.** { *; }
