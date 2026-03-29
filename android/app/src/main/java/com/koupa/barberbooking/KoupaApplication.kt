package com.koupa.barberbooking

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class KoupaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // ── Firebase & Crashlytics initialization ──────────────────────────────
        try {
            FirebaseApp.initializeApp(this)
            FirebaseCrashlytics.getInstance().apply {
                setCrashlyticsCollectionEnabled(true)
                setCustomKey("app_version", "1.0.0")
                setCustomKey("market", "Algeria")
            }
        } catch (e: Exception) {
            Log.e("KoupaCrash", "Firebase/Crashlytics init failed", e)
        }

        // ── Global crash handler ───────────────────────────────────────────────
        // Saves crash locally (for SplashScreen dialog) + sends to Crashlytics
        val crashPrefs = getSharedPreferences("koupa_crash", MODE_PRIVATE)
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                // 1. Send to Firebase Crashlytics
                FirebaseCrashlytics.getInstance().apply {
                    log("Thread: ${thread.name}")
                    recordException(throwable)
                }

                // 2. Save locally for the onscreen diagnostic dialog
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

        // ── OSMDroid (OpenStreetMap) initialization ────────────────────────────
        try {
            Configuration.getInstance().apply {
                load(this@KoupaApplication,
                    getSharedPreferences("osmdroid_prefs", MODE_PRIVATE))
                userAgentValue = "com.koupa.barberbooking/1.0.0"
            }
        } catch (e: Exception) {
            Log.e("KoupaCrash", "OSMDroid init failed", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}
