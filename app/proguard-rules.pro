# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# CrashLogger 라이브러리 규칙
-keep class com.example.stacktracelibrary.CrashLogger { *; }
-keep class com.example.stacktracelibrary.CrashLoggerConfig { *; }

# 스택트레이스 정보 보존
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Signature

# Internal 클래스 난독화 방지 (더 나은 크래시 로그를 위해)
-keep class com.example.stacktracelibrary.internal.** { *; }
-keep class com.example.stacktracelibrary.model.** { *; }