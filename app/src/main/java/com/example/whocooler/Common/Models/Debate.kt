package com.example.whocooler.Common.Models

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
    @SerializedName("votes_count") val votesCount: Int,
    @SerializedName("is_favorite") var isFavorite: Boolean,
    @SerializedName("message_count") var messageCount: Int) : Parcelable