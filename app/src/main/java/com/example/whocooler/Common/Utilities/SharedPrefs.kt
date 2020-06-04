package com.example.whocooler.Common.Utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

class SharedPrefs(context: Context) {

    val PREFS_FILENAME = "prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val FCM_TOKEN = "fcmToken"

    val requestQueue = Volley.newRequestQueue(context)
    var fcmToken: String?
        get() = prefs.getString(FCM_TOKEN, "")
        set(value) = prefs.edit().putString(FCM_TOKEN, value).apply()

}