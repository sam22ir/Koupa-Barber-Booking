package com.koupa.barberbooking

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KoupaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide dependencies here
    }
}
