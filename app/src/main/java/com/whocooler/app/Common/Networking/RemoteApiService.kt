package com.whocooler.app.Common.Networking

import com.whocooler.app.Common.App.App
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

@JvmSuppressWildcards
interface RemoteApiService {

    @POST("en/debatecreate")
    fun createDebate(@Body request: RequestBody): retrofit2.Call<ResponseBody>

    @Multipart
    @POST("en/debatecreate")
    fun acreateDebate(
        @Header("Authorization") authorization: String,
        @PartMap params: Map<String, RequestBody>
    ): retrofit2.Call<ResponseBody>
}