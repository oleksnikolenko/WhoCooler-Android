package com.whocooler.app.Common.Models

import com.google.gson.annotations.SerializedName

data class MessageVoteModel(
    @SerializedName("vote_type") val voteType: String,
    @SerializedName("object_id") val objectId: String,
    @SerializedName("vote_count") val voteCount: Int
)