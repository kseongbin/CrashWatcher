# 사용자 앱에 자동 적용될 ProGuard 규칙

# 크래시 스택트레이스 보존
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions

# CrashLogger Public API 보존
-keep class com.example.stacktracelibrary.CrashLogger { *; }
-keep class com.example.stacktracelibrary.CrashLoggerConfig { *; }
