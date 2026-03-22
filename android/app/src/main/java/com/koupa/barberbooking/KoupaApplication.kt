package com.koupa.barberbooking

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class KoupaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // ── OSMDroid (OpenStreetMap) initialization ──────────────────────────
        // MUST be called before any MapView is created, otherwise crashes.
        Configuration.getInstance().apply {
            load(this@KoupaApplication,
                getSharedPreferences("osmdroid_prefs", MODE_PRIVATE))
            userAgentValue = "com.koupa.barberbooking/1.0.0"
        }
    }
}
