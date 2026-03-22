package com.koupa.barberbooking

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class KoupaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // ── Global crash handler — saves crash to SharedPreferences ───────────
        val crashPrefs = getSharedPreferences("koupa_crash", MODE_PRIVATE)
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val trace = buildString {
                    appendLine("Thread: ${thread.name}")
                    appendLine("Error: ${throwable::class.simpleName}: ${throwable.message}")
                    throwable.stackTrace.take(10).forEach { appendLine("  at $it") }
                    throwable.cause?.let {
                        appendLine("Caused by: ${it::class.simpleName}: ${it.message}")
                        it.stackTrace.take(5).forEach { st -> appendLine("  at $st") }
                    }
                }
                Log.e("KoupaCrash", trace)
                crashPrefs.edit().putString("last_crash", trace).apply()
            } catch (_: Exception) {}
            defaultHandler?.uncaughtException(thread, throwable)
        }

        // ── OSMDroid (OpenStreetMap) initialization ──────────────────────────
        // MUST be called before any MapView is created, otherwise crashes.
        try {
            Configuration.getInstance().apply {
                load(this@KoupaApplication,
                    getSharedPreferences("osmdroid_prefs", MODE_PRIVATE))
                userAgentValue = "com.koupa.barberbooking/1.0.0"
            }
        } catch (e: Exception) {
            Log.e("KoupaCrash", "OSMDroid init failed", e)
        }
    }
}
