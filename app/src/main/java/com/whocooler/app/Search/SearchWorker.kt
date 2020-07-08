package com.whocooler.app.Search

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.DebateVoteResponse
import com.whocooler.app.Common.Models.SearchResponse
import com.whocooler.app.Common.Utilities.BASE_URL
import io.reactivex.rxjava3.subjects.PublishSubject
import org.json.JSONObject

class SearchWorker {

    fun search(context: String, page: Int) : PublishSubject<SearchResponse> {
        val responseSubject = PublishSubject.create<SearchResponse>()

        val searchRequest = object : JsonObjectRequest(Method.GET, BASE_URL + "${App.locale}/search?search_context=$context&page=$page", null, Response.Listener { response ->
            val searchResponse = Gson().fromJson(response.toString(), SearchResponse :: class.java)

            responseSubject.onNext(searchResponse)
        }, Response.ErrorListener {
            responseSubject.onError(it)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
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

        App.prefs.requestQueue.add(searchRequest)

        return responseSubject
    }

}