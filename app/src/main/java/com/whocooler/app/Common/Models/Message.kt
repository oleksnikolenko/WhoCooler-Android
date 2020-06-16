package com.whocooler.app.Common.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(
    val id: String,
    val user: User,
    val text: String,
    @SerializedName("created_time") val createdTime: Double,
    @SerializedName("vote_count") var voteCount: Int,
    @SerializedName("user_vote") var userVote: String,
    @SerializedName("thread_id") val threadId: String?,
    @SerializedName("thread_message_list") var replyList: ArrayList<Message>,
    @SerializedName("thread_message_count") var replyCount: Int,
    @Transient var notShownReplyCount: Int = replyCount
) : Parcelable {
    init {
        notShownReplyCount = replyCount
    }
}