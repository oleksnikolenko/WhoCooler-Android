package com.whocooler.app.Common.Models

import com.google.gson.annotations.SerializedName

data class SearchResponse (
    var debates: ArrayList<Debate>,
    @SerializedName("has_next_page") val hasNextPage: Boolean
)