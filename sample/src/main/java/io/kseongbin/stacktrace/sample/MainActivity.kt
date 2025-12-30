package io.kseongbin.crashwatcher.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import io.kseongbin.crashwatcher.CrashLogger
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
    }

    private fun setupViews() {
        // Log directory info
        val logDirPath = CrashLogger.getLogDirectory()?.absolutePath ?: "Not available"
        findViewById<TextView>(R.id.tvLogPath).text = "Log directory:\n$logDirPath"

        // Crash test buttons
        findViewById<Button>(R.id.btnNullPointer).setOnClickListener {
            triggerNullPointerException()
        }

        findViewById<Button>(R.id.btnArrayIndex).setOnClickListener {
            triggerArrayIndexOutOfBounds()
        }

        findViewById<Button>(R.id.btnArithmetic).setOnClickListener {
            triggerArithmeticException()
        }

        findViewById<Button>(R.id.btnAnr).setOnClickListener {
            triggerAnr()
        }

        findViewById<Button>(R.id.btnViewLogs).setOnClickListener {
            viewLogFiles()
        }

        findViewById<Button>(R.id.btnClearLogs).setOnClickListener {
            clearLogs()
        }
    }

    private fun triggerNullPointerException() {
        // Intentional crash
        val str: String? = null
        str!!.length  // This will crash
    }

    private fun triggerArrayIndexOutOfBounds() {
        // Intentional crash
        val array = arrayOf(1, 2, 3)
        array[10]  // This will crash
    }

    private fun triggerArithmeticException() {
        // Intentional crash
        val result = 10 / 0  // This will crash
    }

    private fun triggerAnr() {
        Toast.makeText(this, "Freezing main thread for 5 seconds...", Toast.LENGTH_SHORT).show()

        // Block main thread to trigger ANR
        Thread.sleep(5000)
    }

    private fun viewLogFiles() {
        val logDir = CrashLogger.getLogDirectory()

        if (logDir == null || !logDir.exists()) {
            Toast.makeText(this, "No log directory found", Toast.LENGTH_SHORT).show()
            return
        }

        val logFiles = logDir.listFiles()?.sortedByDescending { it.lastModified() }

        if (logFiles.isNullOrEmpty()) {
            Toast.makeText(this, "No log files found", Toast.LENGTH_SHORT).show()
            return
        }

        val fileNames = logFiles.map { "${it.name} (${it.length()} bytes)" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Log Files (${logFiles.size})")
            .setItems(fileNames) { _, which ->
                viewLogFileContent(logFiles[which])
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun viewLogFileContent(file: File) {
        val content = file.readText()

        AlertDialog.Builder(this)
            .setTitle(file.name)
            .setMessage(content)
            .setPositiveButton("Share") { _, _ ->
                shareLogFile(file)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun shareLogFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(intent, "Share log file"))
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to share: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun clearLogs() {
        val logDir = CrashLogger.getLogDirectory() ?: return

        val files = logDir.listFiles() ?: return
        var deletedCount = 0

        files.forEach { file ->
            if (file.delete()) {
                deletedCount++
            }
        }

        Toast.makeText(this, "Deleted $deletedCount log files", Toast.LENGTH_SHORT).show()
    }
}
