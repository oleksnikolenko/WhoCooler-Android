package com.whocooler.app.DebateDetail

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebateVoteResponse
import com.whocooler.app.Common.Models.Message
import com.whocooler.app.Common.Models.MessagesList
import com.whocooler.app.Common.Utilities.BASE_URL
import io.reactivex.rxjava3.subjects.PublishSubject
import org.json.JSONObject

class DebateDetailWorker {

    fun getDebate(id: String) : PublishSubject<Debate> {
        var responseSubject = PublishSubject.create<Debate>()

        val debateRequest = object : JsonObjectRequest(Method.GET, BASE_URL + "debate?debate_id=$id", null, Response.Listener {

        }, Response.ErrorListener {
            Log.d("RESPONSE ERROR ", "Error" + it.localizedMessage)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                var token = App.prefs.userSession?.accessToken
                if (token != null) {
                    headers.put("Authorization", "Bearer " + token)
                }
                return headers
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }

        App.prefs.requestQueue.add(debateRequest)

        return responseSubject
    }

    fun sendMessage(text: String, debateId: String) : PublishSubject<Message> {
        val responseSubject = PublishSubject.create<Message>()

        val jsonBody = JSONObject()
        jsonBody.put("debate_id", debateId)
        jsonBody.put("text", text)
        val requestBody = jsonBody.toString()

        val sendMessageRequest = object : JsonObjectRequest(Method.POST, BASE_URL + "message", null, Response.Listener {response ->
            val debateVoteResponse = Gson().fromJson(response.toString(), Message :: class.java)

            responseSubject.onNext(debateVoteResponse)
        }, Response.ErrorListener {
            Log.d("?!?!RESPONSE ERROR ", "VOTE ERROR" + it.networkResponse + it.localizedMessage)
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

        App.prefs.requestQueue.add(sendMessageRequest)

        return responseSubject
    }

    fun sendReply(text: String, threadId: String) : PublishSubject<Message> {
        val responseSubject = PublishSubject.create<Message>()

        val jsonBody = JSONObject()
        jsonBody.put("thread_id", threadId)
        jsonBody.put("text", text)
        val requestBody = jsonBody.toString()

        val sendMessageRequest = object : JsonObjectRequest(Method.POST, BASE_URL + "message", null, Response.Listener {response ->
            val debateVoteResponse = Gson().fromJson(response.toString(), Message :: class.java)

            responseSubject.onNext(debateVoteResponse)
        }, Response.ErrorListener {
            Log.d("?!?!RESPONSE ERROR ", "VOTE ERROR" + it.networkResponse + it.localizedMessage)
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

        App.prefs.requestQueue.add(sendMessageRequest)

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
        }, Response.ErrorListener {
            Log.d("?!?!RESPONSE ERROR ", "VOTE ERROR" + it.networkResponse + it.localizedMessage)
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

    fun getNextReplies(id: String, after: Any) : PublishSubject<MessagesList> {
        val responseSubject = PublishSubject.create<MessagesList>()

        val voteRequest = object : JsonObjectRequest(Method.GET, BASE_URL + "messages?thread_id=$id&after_time=$after", null, Response.Listener {response ->
            val debateVoteResponse = Gson().fromJson(response.toString(), MessagesList :: class.java)

            responseSubject.onNext(debateVoteResponse)
        }, Response.ErrorListener {
            Log.d("?!?!RESPONSE ERROR ", "VOTE ERROR" + it.networkResponse + it.localizedMessage)
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

        App.prefs.requestQueue.add(voteRequest)

        return responseSubject
    }
}