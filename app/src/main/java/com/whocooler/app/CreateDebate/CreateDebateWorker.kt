package com.whocooler.app.CreateDebate

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.CategoriesResponse
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Utilities.BASE_URL
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import java.io.File

class CreateDebateWorker {

    private val gson = Gson()

    fun createDebate(
        leftName: String,
        rightName: String,
        leftImage: File,
        rightImage: File,
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

        map.put("leftside_name", leftNameReq)
        map.put("rightside_name", rightNameReq)
        map.put("category_list", categoryIdReq)
        map.put("leftside_image\"; filename=\"left.jpg\"", leftImageReq);
        map.put("rightside_image\"; filename=\"right.jpg\"", rightImageReq);

        if (debateName != null) {
            val debateNameReq = RequestBody.create(MediaType.parse("text/plain"), debateName)
            map.put("name", debateNameReq)
        }

        val authToken = "Bearer ${App.prefs.userSession?.accessToken}"
        val url = "${App.locale}/debatecreate"

        App.apiService.createDebate(url, authToken, map).enqueue(
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