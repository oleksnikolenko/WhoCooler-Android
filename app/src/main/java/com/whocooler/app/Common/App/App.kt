package com.whocooler.app.Common.App

import android.app.Application
import android.content.Context
import com.whocooler.app.Common.Networking.buildApiService
import com.whocooler.app.Common.Utilities.SharedPrefs
import java.util.*

class App: Application() {

    companion object {
        lateinit var prefs: SharedPrefs
        val apiService by lazy { buildApiService() }
        lateinit var appContext: Context
        lateinit var locale: String
    }

    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()

        setLocale(Locale.getDefault().language)

        appContext = applicationContext
        prefs.sessionNumber = prefs.sessionNumber + 1
    }

    private fun setLocale(language: String) {
        if (language == "ru") {
            locale = language
        } else {
            locale = "en"
        }
    }

}