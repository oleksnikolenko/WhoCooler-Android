package com.whocooler.app.Common.Models

import com.google.gson.annotations.SerializedName

data class Message(
    val id: String,
    val user: User,
    val text: String,
    @SerializedName("created_time") val createdTime: Double,
    @SerializedName("vote_count") var voteCount: Int,
    @SerializedName("user_vote") var userVote: String,
    @SerializedName("thread_message_list") var replyList: ArrayList<Message>,
    @SerializedName("thread_message_count") var replyCount: Int,
    @Transient var notShownReplyCount: Int = replyCount
)