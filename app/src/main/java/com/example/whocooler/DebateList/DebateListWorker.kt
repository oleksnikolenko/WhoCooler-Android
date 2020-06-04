package com.example.whocooler.DebateList

import android.database.Observable
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.whocooler.Common.App.App
import com.example.whocooler.Common.Models.DebateVoteResponse
import com.example.whocooler.Common.Models.DebatesResponse
import com.example.whocooler.Common.Models.User
import com.example.whocooler.Common.Utilities.BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.subjects.PublishSubject
import org.json.JSONObject

class DebateListWorker {

    fun getDebates(page: Int, categoryId: String?, sorting: String) : PublishSubject<DebatesResponse> {
        val responseSubject = PublishSubject.create<DebatesResponse>()
        val debatesRequest = object : JsonObjectRequest(Method.GET, BASE_URL + "en/debates", null, Response.Listener {response ->
            println("?!?!RESPONSE ${response}")
            val debatesResponse = Gson().fromJson(response.toString(), DebatesResponse :: class.java)

            responseSubject.onNext(debatesResponse)
        }, Response.ErrorListener {
            Log.d("RESPONSE ERROR ", it.localizedMessage)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
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
            val debateVoteResponse = Gson().fromJson(response.toString(), DebateVoteResponse :: class.java)

            responseSubject.onNext(debateVoteResponse)
            println("VOTE IS SUCCESSFUL, response: $debateVoteResponse")
        }, Response.ErrorListener {
            Log.d("RESPONSE ERROR ", it.localizedMessage)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                // TODO: - REMOVE HARDCODED TOKEN
                headers.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0NzFhMmYxZS05ZDljLTQ5OGYtYTk4Yi05YTg3OWM5NmFjMDkiLCJpYXQiOjE1OTA5OTYzODUsIm5iZiI6MTU5MDk5NjM4NSwiaWRlbnRpdHkiOiJ2dUdzZUZvMUhEaDd4Vml6V1p0cVhpUlVhVVAyIiwiZXhwIjoxNTk4NzcyMzg1LCJmcmVzaCI6ZmFsc2UsInR5cGUiOiJhY2Nlc3MifQ.FWYUuafCOWWB2aOGyvLgUSh2X7XPAaOEOHoI__sim08")
                return headers
            }
        }

        App.prefs.requestQueue.add(voteRequest)

        return responseSubject
    }

}