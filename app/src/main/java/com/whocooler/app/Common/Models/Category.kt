package com.whocooler.app.Common.Models

import android.content.res.Resources
import android.os.Parcelable
import com.whocooler.app.Common.App.App
import com.whocooler.app.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(val id: String, val name: String) : Parcelable {

    object Constant {
        @JvmStatic val ALL = Category("all", App.appContext.getString(R.string.category_all_name))
        @JvmStatic val FAVORITES = Category("favorites",  App.appContext.getString(R.string.category_favorites_name))
    }

}