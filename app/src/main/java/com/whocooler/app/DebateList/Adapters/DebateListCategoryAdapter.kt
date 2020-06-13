package com.whocooler.app.DebateList.Adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Utilities.VOTE_BUTTON_SHADE_COLOR
import com.whocooler.app.Common.Utilities.dip
import com.whocooler.app.R
import com.google.android.material.textview.MaterialTextView
import kotlin.collections.ArrayList

class DebateListCategoryAdapter(
    var categories: ArrayList<Category>,
    var categoryClick: (Category) -> Unit
) : RecyclerView.Adapter<DebateListCategoryAdapter.ViewHolder>() {

    var selected_position = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTextView: MaterialTextView = itemView.findViewById(R.id.category_list_text)

        fun bindCategory(category: Category) {
            categoryTextView.text = category.name
            categoryTextView.setTextColor(Color.BLACK)

            categoryTextView.setOnClickListener {
                selected_position = adapterPosition
                notifyDataSetChanged()
                categoryClick(category)
            }

            if (adapterPosition==selected_position) {
                itemView.background = GradientDrawable().apply {
                    cornerRadius = (categoryTextView.textSize / 1.5).toFloat()
                    setColor(Color.BLACK)
                }
                categoryTextView.setTextColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
            } else {
                itemView.background = GradientDrawable().apply {
                    cornerRadius = (categoryTextView.textSize / 1.5).toFloat()
                    setColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
                }
                categoryTextView.setTextColor(Color.BLACK)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var container = LinearLayout(parent.context).apply {
            clipToOutline = true
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(parent.dip(2), 0, parent.dip(2), 0)
            }

            updatePadding(dip(12), dip(5), dip(12), dip(5))
            addView(
                MaterialTextView(parent.context).apply {
                    id = R.id.category_list_text
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    )
                }
            )
        }

        return ViewHolder(container)
    }

    override fun getItemCount(): Int {
        return categories.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindCategory(categories[position])
    }

}