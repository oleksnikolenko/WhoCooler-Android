package com.whocooler.app.Common.Networking

import com.whocooler.app.Common.Utilities.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit

fun buildClient(): OkHttpClient =
    OkHttpClient.Builder()
        .build()

fun buildRetrofit(): Retrofit {
    return Retrofit.Builder()
        .client(buildClient())
        .baseUrl(BASE_URL)
        .build()
}

fun buildApiService(): RemoteApiService =
    buildRetrofit().create(RemoteApiService::class.java)