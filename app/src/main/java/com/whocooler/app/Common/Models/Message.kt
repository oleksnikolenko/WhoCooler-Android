package com.whocooler.app.Common.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.whocooler.app.Common.Helpers.VoteType
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Message(
    val id: String,
    val user: User,
    val text: String,
    @SerializedName("created_time") val createdTime: Double,
    @SerializedName("vote_count") var voteCount: Int,
    @SerializedName("user_vote") var userVote: String,
    @SerializedName("thread_id") var threadId: String?,
    @SerializedName("thread_message_list") var replyList: ArrayList<Message>,
    @SerializedName("thread_message_count") var replyCount: Int
) : Parcelable {

    private var notShownReply: Int? = null

    fun getNotShownReplyCount() : Int {
        if (notShownReply != null) {
            return notShownReply!!
        } else {
            notShownReply = replyCount
            return replyCount
        }
    }

    fun setNotShowReplyCount(reduceValue: Int) {
        if (notShownReply != null) {
            notShownReply = notShownReply!! - reduceValue
        }
    }

    var voteType: VoteType
        get() = VoteType.from(userVote)
        set(value) { userVote = value.name }

    fun didVote(model: MessageVoteModel, voteType: VoteType) {
        voteCount = model.voteCount
        userVote = voteType.name
    }

}