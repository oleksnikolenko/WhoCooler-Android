package com.whocooler.app.DebateList

import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.DebateVoteResponse
import com.whocooler.app.Common.Models.DebatesResponse
import com.whocooler.app.Common.Utilities.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.Empty
import io.reactivex.rxjava3.subjects.PublishSubject
import org.json.JSONObject

class DebateListWorker {

    var gson = GsonBuilder().create()

    fun getDebates(page: Int, categoryId: String? = "all", sorting: String) : PublishSubject<DebatesResponse> {
        val responseSubject = PublishSubject.create<DebatesResponse>()

        val debatesRequest = object : JsonObjectRequest(Method.GET, BASE_URL + "${App.locale}/debates" + "?category_id=$categoryId&sorting=$sorting&page=$page", null, Response.Listener {response ->
            val debatesResponse = gson.fromJson(response.toString(), DebatesResponse::class.java)

            if (debatesResponse != null) {
                responseSubject.onNext(debatesResponse)
            }
        }, Response.ErrorListener {
            responseSubject.onError(it)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                var token = App.prefs.userSession?.accessToken
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token)
                }
                return headers
            }
        }

        App.prefs.requestQueue.add(debatesRequest)

        return responseSubject
    }

    fun vote(debateId: String, sideId: String) : PublishSubject<DebateVoteResponse> {
        val responseSubject = PublishSubject.create<DebateVoteResponse>()

        val jsonBody = JSONObject()
        jsonBody.put("debate_id", debateId)
        jsonBody.put("side_id", sideId)
        val requestBody = jsonBody.toString()

        val voteRequest = object : JsonObjectRequest(Method.POST, BASE_URL + "vote", null, Response.Listener {response ->
            val debateVoteResponse = gson.fromJson(response.toString(), DebateVoteResponse :: class.java)

            responseSubject.onNext(debateVoteResponse)
        }, Response.ErrorListener {
            responseSubject.onError(it)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
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

        App.prefs.requestQueue.add(voteRequest)

        return responseSubject
    }

    fun addToFavorites(debate: Debate) : PublishSubject<Empty> {
        val responseSubject = PublishSubject.create<Empty>()

        val jsonBody = JSONObject()
        jsonBody.put("debate_id", debate.id)

        val addFavoritesRequest = object : JsonObjectRequest(Method.POST, BASE_URL + "favorites", null, Response.Listener {
            val emptyCallback = gson.fromJson(it.toString(), Empty :: class.java)

            responseSubject.onNext(emptyCallback)
        }, Response.ErrorListener {
            responseSubject.onError(it)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return jsonBody.toString().toByteArray()
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

        App.prefs.requestQueue.add(addFavoritesRequest)

        return responseSubject
    }

    fun deleteFromFavorites(debate: Debate) : PublishSubject<Empty> {
        val responseSubject = PublishSubject.create<Empty>()

        val deleteFavoritesRequest = object : JsonObjectRequest(Method.DELETE, BASE_URL + "favorites?debate_id=${debate.id}", null, Response.Listener {
            val emptyCallback = gson.fromJson(it.toString(), Empty :: class.java)

            responseSubject.onNext(emptyCallback)
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

        App.prefs.requestQueue.add(deleteFavoritesRequest)

        return responseSubject
    }

}