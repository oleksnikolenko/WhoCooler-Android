package com.whocooler.app.PickCategory

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Utilities.dip
import com.whocooler.app.R

class PickCategoryAdapter(
    var categories: ArrayList<Category>,
    var categoryClick: (Category) -> Unit
):  RecyclerView.Adapter<PickCategoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val categoryTextView: MaterialTextView = itemView.findViewById(R.id.pick_category_text)

        fun bindCategory(category: Category) {
            categoryTextView.text = category.name

            categoryTextView.setOnClickListener {
                categoryClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val container = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(parent.dip(24), dip(12), parent.dip(12), 0)
            }

            addView(
                MaterialTextView(parent.context).apply {
                    id = R.id.pick_category_text
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setTextColor(Color.BLACK)
                        textSize = 18f
                    }
                }
            )

            addView(
                LinearLayout(parent.context).apply {
                    gravity = Gravity.BOTTOM
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dip(1)
                    ).apply {
                        setMargins(0, dip(12), 0, 0)
                        setBackgroundColor(Color.LTGRAY)
                    }
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