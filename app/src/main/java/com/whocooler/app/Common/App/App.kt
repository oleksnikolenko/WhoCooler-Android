package com.whocooler.app.Common.App

import android.app.Application
import android.content.Context
import com.whocooler.app.Common.Networking.buildApiService
import com.whocooler.app.Common.Utilities.SharedPrefs

class App: Application() {

    companion object {
        lateinit var prefs: SharedPrefs
        val apiService by lazy { buildApiService() }
        lateinit var appContext: Context

    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()

        appContext = applicationContext
    }

}