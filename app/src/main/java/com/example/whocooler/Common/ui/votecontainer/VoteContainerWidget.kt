package com.example.whocooler.Common.ui.votecontainer

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.example.whocooler.Common.Utilities.dip
import com.example.whocooler.R
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.textview.MaterialTextView
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class VoteContainerWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val containerLeft: LinearLayout = getInnerContainer()
    private val containerRight: LinearLayout = getInnerContainer()

    private val leftName: MaterialTextView = containerLeft.findViewById(R.id.name)
    private val leftPercent: MaterialTextView = containerLeft.findViewById(R.id.percent)
    private val leftVotes: MaterialTextView = containerLeft.findViewById(R.id.votes)

    private val rightName: MaterialTextView = containerRight.findViewById(R.id.name)
    private val rightPercent: MaterialTextView = containerRight.findViewById(R.id.percent)
    private val rightVotes: MaterialTextView = containerRight.findViewById(R.id.votes)

    init {
        orientation = HORIZONTAL
        weightSum = 100F
        gravity = Gravity.CENTER_VERTICAL
        addView(containerLeft)
        addView(containerRight)
        layoutParams = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            dip(80)
        )
    }

    private fun getInnerContainer(): LinearLayout {
        return LinearLayout(context).apply {
            orientation = VERTICAL
            gravity = Gravity.CENTER
            showDividers = SHOW_DIVIDER_MIDDLE
            dividerDrawable = GradientDrawable().apply {
                setSize(0, dip(6))
            }
            layoutParams = LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT
            )
            addView(MaterialTextView(context).apply {
                id = R.id.name
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    0
                ).apply {
                    weight = 1.0f
                }
                gravity = Gravity.CENTER
                textSize = 22.0f
            })
            addView(FlexboxLayout(context).apply {
                flexWrap = FlexWrap.WRAP
                alignItems = AlignItems.BASELINE
                showDividerHorizontal = FlexboxLayout.SHOW_DIVIDER_MIDDLE
                showDividerVertical = FlexboxLayout.SHOW_DIVIDER_MIDDLE

                val divider = GradientDrawable().apply {
                    setSize(dip(4), dip(2))
                }
                dividerDrawableHorizontal = divider
                dividerDrawableVertical = divider

                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
                addView(MaterialTextView(context).apply {
                    id = R.id.percent
                    layoutParams = LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    textSize = 18.0f
                })
                addView(MaterialTextView(context).apply {
                    id = R.id.votes
                    layoutParams = LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    textSize = 14.0f
                })
            })
        }
    }

    fun acceptModel(viewObject: VoteContainerModel) {
        val actualPercentLeft = max(20.0f, min(80.0f, viewObject.getPercentLeft())).roundToInt()
        val actualPercentRight = (100 - actualPercentLeft)
        containerLeft.updateLayoutParams<LayoutParams> {
            weight = actualPercentLeft.toFloat()
        }
        containerRight.updateLayoutParams<LayoutParams> {
            weight = actualPercentRight.toFloat()
        }
        leftName.text = viewObject.leftName
        rightName.text = viewObject.rightName
        leftPercent.text = String.format(
            context.getString(R.string.percent),
            viewObject.getPercentLeft().roundToInt().toString()
        )
        rightPercent.text = String.format(
            context.getString(R.string.percent),
            viewObject.getPercentRight().roundToInt().toString()
        )
        leftVotes.text = String.format(
            context.getString(R.string.vote),
            viewObject.leftVote.toString()
        )
        rightVotes.text = String.format(
            context.getString(R.string.vote),
            viewObject.rightVote.toString()
        )
    }
}