package com.example.whocooler.Common.Models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    @SerializedName("avatar_url") val avatar: String?,
    @SerializedName("username") val name: String
)