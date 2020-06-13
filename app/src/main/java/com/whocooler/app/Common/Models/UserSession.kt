package com.whocooler.app.Common.Models

import com.google.gson.annotations.SerializedName

data class UserSession(
    var user: User,
    @SerializedName("access_token") var accessToken: String
)