package com.whocooler.app.Common.Utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley
import com.whocooler.app.Common.Models.UserSession
import com.google.gson.Gson

class SharedPrefs(context: Context) {

    val PREFS_FILENAME = "prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val requestQueue = Volley.newRequestQueue(context)

    var userSession: UserSession?
        get() = Gson().fromJson(prefs.getString(PREFS_USER_SESSION, ""), UserSession::class.java)
        set(value) = prefs.edit().putString(PREFS_USER_SESSION, Gson().toJson(value)).apply()

    fun isTokenEmpty(): Boolean {
        return userSession?.accessToken == null
    }

}