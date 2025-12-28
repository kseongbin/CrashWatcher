package com.example.stacktracelibrary.internal

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.example.stacktracelibrary.model.AppInfo
import com.example.stacktracelibrary.model.DeviceInfo

internal class DeviceInfoCollector(private val context: Context) {

    fun collectDeviceInfo(): DeviceInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val stat = StatFs(Environment.getDataDirectory().path)
        val totalStorageBytes = stat.blockCountLong * stat.blockSizeLong

        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            brand = Build.BRAND,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            buildId = Build.ID,
            availableRamMb = memoryInfo.availMem / (1024 * 1024),
            totalStorageGb = totalStorageBytes / (1024 * 1024 * 1024)
        )
    }

    fun collectAppInfo(): AppInfo {
        val packageManager = context.packageManager
        val packageName = context.packageName

        val packageInfo = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
        } catch (e: Exception) {
            null
        }

        val installSource = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                packageManager.getInstallSourceInfo(packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstallerPackageName(packageName)
            }
        } catch (e: Exception) {
            null
        }

        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo?.longVersionCode ?: 0
        } else {
            @Suppress("DEPRECATION")
            packageInfo?.versionCode?.toLong() ?: 0
        }

        return AppInfo(
            packageName = packageName,
            versionName = packageInfo?.versionName ?: "Unknown",
            versionCode = versionCode,
            processName = getCurrentProcessName(),
            installSource = installSource
        )
    }

    private fun getCurrentProcessName(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            android.app.Application.getProcessName()
        } else {
            context.packageName
        }
    }
}
