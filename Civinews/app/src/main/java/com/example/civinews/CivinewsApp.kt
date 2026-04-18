package com.example.civinews

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CivinewsApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Configuramos Cloudinary una sola vez para toda la app
        val config = mapOf(
            "cloud_name" to "dtecznnri",
            "secure" to true
        )
        MediaManager.init(this, config)
    }
}