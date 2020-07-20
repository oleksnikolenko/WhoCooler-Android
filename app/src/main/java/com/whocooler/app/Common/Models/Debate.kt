package com.whocooler.app.Common.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//@SerializedName("categories") val categories: List<Category>,

@Parcelize
data class Debate(
    @SerializedName("id") val id: String,
    @SerializedName("leftside") var leftSide: DebateSide,
    @SerializedName("rightside") var rightSide: DebateSide,
    @SerializedName("category") val category: Category,
    @SerializedName("votes_count") var votesCount: Int,
    @SerializedName("message_list") var messagesList: MessagesList,
    @SerializedName("is_favorite") var isFavorite: Boolean,
    @SerializedName("message_count") var messageCount: Int,
    var image: String?,
    var type: String,
    var name: String?) : Parcelable