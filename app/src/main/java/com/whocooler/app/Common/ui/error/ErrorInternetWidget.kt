package com.whocooler.app.Common.ui.error

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.marginTop
import com.google.android.material.textview.MaterialTextView
import com.whocooler.app.Common.Utilities.dip
import com.whocooler.app.R

class ErrorInternetWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val refreshButton = Button(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            text = context.getString(R.string.refresh)
            setMargins(0, dip(12), 0, 0)
        }
    }

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER

        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        addViews()
    }

    fun addViews() {
        addView(AppCompatImageView(context).apply {
            setImageResource(R.drawable.no_internet)
            layoutParams = LinearLayout.LayoutParams(
                dip(48),
                dip(48)
            )
        })

        addView(MaterialTextView(context).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                text = context.getString(R.string.error_no_internet)
                setMargins(0, dip(12), 0, 0)
            }
        })

        addView(refreshButton)
    }

}