package com.whocooler.app.Common.Models

import com.google.gson.annotations.SerializedName

data class DebatesResponse(
    val categories: ArrayList<Category>,
    var debates: ArrayList<Debate>,
    @SerializedName("has_next_page") var hasNextPage: Boolean
)
