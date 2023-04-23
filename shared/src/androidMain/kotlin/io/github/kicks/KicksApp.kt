package io.github.kicks

import android.app.Application
import android.content.Context

class KicksApp : Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

}