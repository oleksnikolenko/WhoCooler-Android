package com.whocooler.app.Common.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DebateSide(
    val id: String,
    val name: String,
    val image: String,
    @SerializedName("rating_count") var ratingCount: Int,
    @SerializedName("is_voted_by_user") var isVotedByUser: Boolean
) : Parcelable