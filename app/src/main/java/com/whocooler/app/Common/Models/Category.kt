package com.whocooler.app.Common.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(val id: String, val name: String) : Parcelable {

    object Constant {
        @JvmStatic val ALL = Category("all", "All")
        @JvmStatic val FAVORITES = Category("favorites", "Favorites")
    }

}