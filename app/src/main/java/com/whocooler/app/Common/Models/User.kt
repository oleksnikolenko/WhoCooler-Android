package com.whocooler.app.Common.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String,
    @SerializedName("avatar_url") val avatar: String?,
    @SerializedName("username") val name: String
): Parcelable