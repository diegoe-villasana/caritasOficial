package com.example.template2025

import android.app.Application
import com.example.template2025.model.ApiClient

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.initialize(this)
    }
}