package com.ivy.covid19_map

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CenterApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // do something
    }
}