package com.whocooler.app.Common.Models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class MessagesList(
    var messages: @RawValue ArrayList<Message>,
    @SerializedName("has_next_page") var hasNextPage: Boolean
) : Parcelable