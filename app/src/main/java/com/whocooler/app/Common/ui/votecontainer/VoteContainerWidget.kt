package com.whocooler.app.Common.ui.votecontainer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.whocooler.app.Common.Utilities.*
import com.whocooler.app.Common.Views.AutoResizeTextView
import com.whocooler.app.R
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class VoteContainerWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var containerLeft: LinearLayout? = null
    var containerRight: LinearLayout? = null

    var leftClicked: (() -> Unit)? = null
    var rightClicked: (() -> Unit)? = null

    private var divider = LinearLayout(context).apply {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        layoutParams = LayoutParams(
            0,
            LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
    }

    private var leftName: AutoResizeTextView? = containerLeft?.findViewById(R.id.name)
    private var leftPercent: AutoResizeTextView? = containerLeft?.findViewById(R.id.percent)

    private var rightName: AutoResizeTextView? = containerRight?.findViewById(R.id.name)
    private var rightPercent: AutoResizeTextView? = containerRight?.findViewById(R.id.percent)

    private fun leftShader() : LinearGradient {
        return LinearGradient(
            0f,
            0f,
           leftName?.paint?.measureText(leftName?.text.toString())!!.toFloat(),
            leftName?.textSize!! / 2, intArrayOf(
                Color.parseColor(VOTE_BUTTON_LEFT_START_COLOR),
                Color.parseColor(VOTE_BUTTON_LEFT_END_COLOR)
            ),
            null, Shader.TileMode.CLAMP
        )
    }

    private fun rightShader() : LinearGradient {
        return LinearGradient(
            0f,
            leftName?.textSize!! / 2,
            rightName?.paint?.measureText(rightName?.text.toString())!!.toFloat(),
            rightName?.textSize!! / 2, intArrayOf(
                Color.parseColor(VOTE_BUTTON_RIGHT_START_COLOR),
                Color.parseColor(VOTE_BUTTON_RIGHT_END_COLOR)
            ),
            null, Shader.TileMode.CLAMP
        )
    }

    init {
        orientation = HORIZONTAL
        weightSum = 100F
        gravity = Gravity.CENTER_VERTICAL

        val customLayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            dip(74)
        )
        customLayoutParams.setMargins(dip(12), 0 ,dip(12) , 0)
        layoutParams = customLayoutParams
        setBackgroundResource(R.drawable.custom_background_border)
    }

    private fun getInnerContainer(isVoted: Boolean): LinearLayout {
        if (isVoted) {
           return votedLinearLayout()
        } else {
            return nonVotedLinearLayout()
        }
    }

    fun nonVotedLinearLayout() : LinearLayout {
        return LinearLayout(context).apply {
            orientation = VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
            addView(AutoResizeTextView(context).apply {
                id = R.id.name
                maxLines = 2
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, 0).apply {
                    weight = 1.0f
                    setMargins(dip(4), 0, dip(4) ,0)
                }
                gravity = Gravity.CENTER
                textSize = 22.0f
            })
        }
    }

    fun votedLinearLayout() : LinearLayout {
        return LinearLayout(context).apply {
            orientation = VERTICAL
            gravity = Gravity.CENTER
            showDividers = SHOW_DIVIDER_MIDDLE
            layoutParams = LayoutParams(
                0,
                LayoutParams.MATCH_PARENT
            )
            addView(AutoResizeTextView(context).apply {
                id = R.id.name
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    0
                ).apply {
                    weight = 1.0f
                    setMargins(dip(4), 0, dip(4) ,0)
                }
                gravity = Gravity.CENTER
                textSize = 22.0f
            })
            addView(FlexboxLayout(context).apply {
                flexWrap = FlexWrap.WRAP
                alignItems = AlignItems.BASELINE
                showDividerHorizontal = FlexboxLayout.SHOW_DIVIDER_MIDDLE
                showDividerVertical = FlexboxLayout.SHOW_DIVIDER_MIDDLE

                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
                addView(AutoResizeTextView(context).apply {
                    id = R.id.percent
                    maxLines = 1
                    layoutParams = LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(dip(2), 0, dip(2) ,dip(2))
                    }
                })
            })
        }
    }

    fun acceptModel(viewObject: VoteContainerModel, isReloading: Boolean = false) {
        if (isReloading) {
            var visible = false
            TransitionManager.beginDelayedTransition(this)
            visible = !visible
            visibility = if (visible) View.VISIBLE else View.GONE
        }

        if (viewObject.debate.leftSide.isVotedByUser) {
            containerLeft = getInnerContainer(true)
            containerRight = getInnerContainer(true)

            removeAllViewsInLayout()
            overlay.clear()

            addView(containerLeft)
            addView(containerRight)

            containerLeft?.setOnClickListener {
                leftClicked?.let { it1 -> it1() }
            }

            containerRight?.setOnClickListener {
                rightClicked?.let { it1 -> it1() }
            }

            containerLeft?.background  = GradientDrawable().apply {
                setColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
                setStroke(0, null)
                shape = GradientDrawable.RECTANGLE
                clipToOutline = true
            }
            setupVotedSubviews()
            if (viewObject.debate.rightSide.ratingCount == 0) {
                singleWinnerLeft(viewObject)
            } else {
                votedLayout(viewObject)
            }
        } else if (viewObject.debate.rightSide.isVotedByUser) {
            containerLeft = getInnerContainer(true)
            containerRight = getInnerContainer(true)

            removeAllViewsInLayout()
            overlay.clear()

            addView(containerLeft)
            addView(containerRight)

            containerLeft?.setOnClickListener {
                leftClicked?.let { it1 -> it1() }
            }

            containerRight?.setOnClickListener {
                rightClicked?.let { it1 -> it1() }
            }

            containerRight?.background = GradientDrawable().apply {
                setColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
                setStroke(0, null)
                clipToOutline = true
            }
            setupVotedSubviews()
            if (viewObject.debate.leftSide.ratingCount == 0) {
                singleWinnerRight(viewObject)
            } else {
                votedLayout(viewObject)
            }
        } else {
            containerLeft = getInnerContainer(false)
            containerRight = getInnerContainer(false)

            removeAllViewsInLayout()
            overlay.clear()

            addView(containerLeft)
            addView(divider)
            addView(containerRight)

            containerLeft?.setOnClickListener {
                leftClicked?.let { it1 -> it1() }
            }

            containerRight?.setOnClickListener {
                rightClicked?.let { it1 -> it1() }
            }

            setupUnVotedSubviews()
            nonVotedLayout(viewObject)
        }
    }

    private fun setupVotedSubviews() {
        leftName = containerLeft?.findViewById(R.id.name)
        leftPercent = containerLeft?.findViewById(R.id.percent)

        rightName = containerRight?.findViewById(R.id.name)
        rightPercent = containerRight?.findViewById(R.id.percent)
    }

    private fun setupUnVotedSubviews() {
        leftName = containerLeft?.findViewById(R.id.name)
        rightName = containerRight?.findViewById(R.id.name)
    }

    @SuppressLint("SetTextI18n")
    fun votedLayout(viewObject: VoteContainerModel) {
        val actualPercentLeft = max(20.0f, min(80.0f, viewObject.getPercentLeft())).roundToInt()
        val actualPercentRight = (100 - actualPercentLeft)

        containerLeft?.updateLayoutParams<LayoutParams> {
            weight = actualPercentLeft.toFloat()
        }
        containerRight?.updateLayoutParams<LayoutParams> {
            weight = actualPercentRight.toFloat()
        }

        leftName?.text = viewObject.debate.leftSide.name
        leftName?.paint?.setShader(leftShader())

        rightName?.text = viewObject.debate.rightSide.name
        rightName?.paint?.setShader(rightShader())

        leftPercent?.text = String.format(
            context.getString(R.string.percent),
            viewObject.getPercentLeft().roundToInt().toString()
        ) + String.format(
            context.getString(R.string.vote),
            " " + viewObject.debate.leftSide.ratingCount.toString()
        )
        rightPercent?.text = String.format(
            context.getString(R.string.percent),
            viewObject.getPercentRight().roundToInt().toString()
        ) + String.format(
            context.getString(R.string.vote),
            " " + viewObject.debate.rightSide.ratingCount.toString()
        )
    }

    fun nonVotedLayout(viewObject: VoteContainerModel) {
        containerLeft?.updateLayoutParams<LayoutParams> {
            weight = 49.75f
        }
        divider?.updateLayoutParams<LayoutParams> {
            weight = 0.5f
        }
        containerRight?.updateLayoutParams<LayoutParams> {
            weight = 49.75f
        }

        leftName?.text = viewObject.debate.leftSide.name
        leftName?.paint?.setShader(leftShader())

        rightName?.text = viewObject.debate.rightSide.name
        rightName?.paint?.setShader(rightShader())
    }

    @SuppressLint("SetTextI18n")
    fun singleWinnerLeft(viewObject: VoteContainerModel) {
        containerLeft?.updateLayoutParams<LayoutParams> { weight = 100f }

        leftName?.text = viewObject.debate.leftSide.name
        leftName?.paint?.setShader(leftShader())

        leftPercent?.text = String.format(
            context.getString(R.string.percent),
            "100"
        ) + String.format(
            context.getString(R.string.vote),
            " " + viewObject.debate.leftSide.ratingCount.toString()
        )
    }

    @SuppressLint("SetTextI18n")
    fun singleWinnerRight(viewObject: VoteContainerModel) {
        containerRight?.updateLayoutParams<LayoutParams> { weight = 100f }

        rightName?.text = viewObject.debate.rightSide.name
        rightName?.paint?.setShader(rightShader())

        rightPercent?.text = String.format(
            context.getString(R.string.percent),
            "100"
        ) + String.format(
            context.getString(R.string.vote),
            " " + viewObject.debate.rightSide.ratingCount.toString()
        )
    }
}
