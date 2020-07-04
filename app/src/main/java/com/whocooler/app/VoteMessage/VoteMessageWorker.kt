package com.whocooler.app.VoteMessage

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Helpers.VoteType
import com.whocooler.app.Common.Models.MessageVoteModel
import com.whocooler.app.Common.Utilities.BASE_URL
import io.reactivex.rxjava3.subjects.PublishSubject
import org.json.JSONObject

class VoteMessageWorker {

    fun postVote(objectId: String, voteType: VoteType, isReply: Boolean) : PublishSubject<MessageVoteModel> {
        val responseSubject = PublishSubject.create<MessageVoteModel>()

        val objectIdParameterName: String = if (isReply) "thread_id" else "message_id"

        val jsonBody = JSONObject()
        jsonBody.put("is_positive", voteType == VoteType.up)
        jsonBody.put(objectIdParameterName, objectId)

        val postVoteRequest = object : JsonObjectRequest(Method.POST, BASE_URL + "messagevote", null, Response.Listener { response ->
            val parsedResponse = Gson().fromJson(response.toString(), MessageVoteModel :: class.java)
            responseSubject.onNext(parsedResponse)
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

        App.prefs.requestQueue.add(postVoteRequest)

        return responseSubject
    }

    fun deleteVote(objectId: String, isReply: Boolean) : PublishSubject<MessageVoteModel> {
        val responseSubject = PublishSubject.create<MessageVoteModel>()

        val objectIdParameterName: String = if (isReply) "thread_id" else "message_id"

        val postVoteRequest = object : JsonObjectRequest(Method.DELETE, BASE_URL + "messagevote?$objectIdParameterName=$objectId", null, Response.Listener { response ->
            val parsedResponse = Gson().fromJson(response.toString(), MessageVoteModel :: class.java)
            responseSubject.onNext(parsedResponse)
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

        App.prefs.requestQueue.add(postVoteRequest)

        return responseSubject
    }
}