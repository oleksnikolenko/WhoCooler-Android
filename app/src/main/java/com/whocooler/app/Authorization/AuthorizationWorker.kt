package com.whocooler.app.Authorization

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.UserSession
import com.whocooler.app.Common.Utilities.BASE_URL
import com.google.gson.Gson
import io.reactivex.rxjava3.subjects.PublishSubject
import org.json.JSONObject

class AuthorizationWorker {

    fun authorize(token: String) : PublishSubject<UserSession> {
        val responseSubject = PublishSubject.create<UserSession>()

        val jsonBody = JSONObject()
        jsonBody.put("token", token)
        jsonBody.put("platform","android")
        val requestBody = jsonBody.toString()

        val authorizeRequest = object : JsonObjectRequest(
            Method.POST,
            BASE_URL + "register",
            null,
            Response.Listener { response ->
                val authResponse = Gson().fromJson(response.toString(), UserSession :: class.java)
                responseSubject.onNext(authResponse)
            }, Response.ErrorListener {
                responseSubject.onError(it)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.prefs.requestQueue.add(authorizeRequest)

        return responseSubject
    }

}