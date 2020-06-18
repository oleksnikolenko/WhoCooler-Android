package com.whocooler.app.UserProfile

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.t2r2.volleyexample.FileDataPart
import com.t2r2.volleyexample.VolleyFileUploadRequest
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.UserEditResponse
import com.whocooler.app.Common.Utilities.BASE_URL
import io.reactivex.rxjava3.subjects.PublishSubject
import java.nio.charset.Charset

class UserProfileWorker {

    fun updateUserName(newName: String) : PublishSubject<UserEditResponse> {
        val responseSubject = PublishSubject.create<UserEditResponse>()

        val BOUNDARY = "AS24adije32MDJHEM9oMaGnKUXtfHq"
        val MULTIPART_FORMDATA = "multipart/form-data;boundary=" + BOUNDARY

        val updateNameRequest = object : JsonObjectRequest(Method.PATCH, BASE_URL + "useredit", null, Response.Listener { response ->
            val editResponse = Gson().fromJson(response.toString(), UserEditResponse :: class.java)
            responseSubject.onNext(editResponse)
        }, Response.ErrorListener {
            Log.d("?!?!RESPONSE ERROR ", "VOTE ERROR" + it.networkResponse + it.localizedMessage)
        }) {
            override fun getBodyContentType(): String {
                return MULTIPART_FORMDATA
            }
            override fun getBody(): ByteArray {
                val params = HashMap<String, String>()
                params.put("name", newName)

                val map: List<String> = params.map { (key, value) ->
                    "--$BOUNDARY\nContent-Disposition: form-data; name=\"$key\"\n\n$value"
                }
                val endResult = "${map.joinToString("")}\n--$BOUNDARY--\n"
                return endResult.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                var token = App.prefs.userSession?.accessToken
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token)
                }
                return headers
            }
        }

        App.prefs.requestQueue.add(updateNameRequest)

        return responseSubject
    }

    fun updateUserAvatar(imageData: ByteArray) : PublishSubject<UserEditResponse> {
        val responseSubject = PublishSubject.create<UserEditResponse>()
        val request = object : VolleyFileUploadRequest(
            Method.PATCH,
            BASE_URL + "useredit",
            Response.Listener {response->
                var json = String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
                val editResponse = Gson().fromJson(json, UserEditResponse :: class.java)
                responseSubject.onNext(editResponse)
            },
            Response.ErrorListener {
                Log.d("?!?!RESPONSE ERROR ", "VOTE ERROR" + it.networkResponse + it.localizedMessage)
            }
        ) {
            override fun getByteData(): Map<String, Any>? {
                var params = HashMap<String, FileDataPart>()
                params["avatar"] = FileDataPart("avatar.jpeg", imageData, "jpeg")
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                var token = App.prefs.userSession?.accessToken
                if (token != null) {
                    println("?!?! TOKEN $token")
                    headers.put("Authorization", "Bearer " + token)
                }
                return headers
            }
        }

        App.prefs.requestQueue.add(request)

        return responseSubject
    }

}
