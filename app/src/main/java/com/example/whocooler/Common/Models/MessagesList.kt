package com.example.whocooler.Common.Models

import com.google.gson.annotations.SerializedName

class MessagesList(
    var messages: ArrayList<Message>,
    @SerializedName("has_next_page") var hasNextPage: Boolean
)