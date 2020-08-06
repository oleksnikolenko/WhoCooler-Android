package com.whocooler.app.CreateDebate

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Debate
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import java.io.ByteArrayOutputStream

class CreateDebateWorker {

    private val gson = Gson()

    fun createDebateSides(
        leftName: String,
        rightName: String,
        leftImage: ByteArray,
        rightImage: ByteArray,
        categoryId: String,
        debateName: String?
    ) : PublishSubject<Debate> {
        val responseSubject = PublishSubject.create<Debate>()
        val map = HashMap<String, RequestBody>()

        val leftNameReq = RequestBody.create(MediaType.parse("text/plain"), leftName)
        val rightNameReq = RequestBody.create(MediaType.parse("text/plain"), rightName)
        val categoryIdReq = RequestBody.create(MediaType.parse("text/plain"), categoryId)
        val leftImageReq = RequestBody.create(MediaType.parse("image/jpeg"), leftImage)
        val rightImageReq = RequestBody.create(MediaType.parse("image/jpeg"), rightImage)
        val debateTypeReq = RequestBody.create(MediaType.parse("text/plain"), "sides")

        map.put("leftside_name", leftNameReq)
        map.put("rightside_name", rightNameReq)
        map.put("category_list", categoryIdReq)
        map.put("leftside_image\"; filename=\"left.jpg\"", leftImageReq)
        map.put("rightside_image\"; filename=\"right.jpg\"", rightImageReq)
        map.put("debate_type", debateTypeReq)

        if (debateName != null) {
            val debateNameReq = RequestBody.create(MediaType.parse("text/plain"), debateName)
            map.put("name", debateNameReq)
        }

        val authToken = "Bearer ${App.prefs.userSession?.accessToken}"
        val url = "${App.locale}/debatecreate"

        App.apiService.createDebateSides(url, authToken, map).enqueue(
            object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    responseSubject.onError(t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
                ) {
                    val jsonBody = response.body()?.string()

                    if (jsonBody != null) {
                        val debate = gson.fromJson(jsonBody, Debate::class.java)

                        responseSubject.onNext(debate)
                    }
                }
            }
        )

        return responseSubject
    }

    fun createDebateStatement(
        leftName: String,
        rightName: String,
        debateImage: ByteArray,
        categoryId: String,
        debateName: String
    ) : PublishSubject<Debate> {
        val responseSubject = PublishSubject.create<Debate>()
        val map = HashMap<String, RequestBody>()

        val leftNameReq = RequestBody.create(MediaType.parse("text/plain"), leftName)
        val rightNameReq = RequestBody.create(MediaType.parse("text/plain"), rightName)
        val categoryIdReq = RequestBody.create(MediaType.parse("text/plain"), categoryId)
        val debateImageReq = RequestBody.create(MediaType.parse("image/jpeg"), debateImage)
        val debateNameReq = RequestBody.create(MediaType.parse("text/plain"), debateName)
        val debateTypeReq = RequestBody.create(MediaType.parse("text/plain"), "statement")

        map.put("leftside_name", leftNameReq)
        map.put("rightside_name", rightNameReq)
        map.put("category_list", categoryIdReq)
        map.put("debate_image\"; filename=\"debate.jpg\"", debateImageReq);
        map.put("name", debateNameReq)
        map.put("debate_type", debateTypeReq)

        val authToken = "Bearer ${App.prefs.userSession?.accessToken}"
        val url = "${App.locale}/debatecreate"

        App.apiService.createDebateStatement(url, authToken, map).enqueue(
            object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    responseSubject.onError(t)
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
                ) {
                    val jsonBody = response.body()?.string()

                    if (jsonBody != null) {
                        val debate = gson.fromJson(jsonBody, Debate::class.java)

                        responseSubject.onNext(debate)
                    }
                }
            }
        )

        return responseSubject
    }

}