package com.example.whocooler.DebateList

import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.whocooler.Common.Models.Debate
import com.example.whocooler.Common.Models.DebateSide
import com.example.whocooler.Common.Models.DebatesResponse
import com.example.whocooler.Common.Utilities.dip
import com.example.whocooler.Common.ui.votecontainer.VoteContainerModel
import com.example.whocooler.Common.ui.votecontainer.VoteContainerWidget
import com.example.whocooler.R
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso


class DebateListAdapter(
    val response: DebatesResponse,
    val voteClick: (Debate, DebateSide) -> Unit,
    val debateClick: (Debate) -> Unit
) : RecyclerView.Adapter<DebateListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val category: MaterialTextView = itemView.findViewById(R.id.category_id)
        private val leftSideImage: AppCompatImageView = itemView.findViewById(R.id.left_image)
        private val rightSideImage: AppCompatImageView = itemView.findViewById(R.id.right_image)
        private val voteContainer: VoteContainerWidget = itemView.findViewById(R.id.vote_container)

//        val leftSideButton = itemView.findViewById<ToggleButton>(R.id.listLeftSideButton)
//        val rightSideImage = itemView?.findViewById<ImageView>(R.id.listRightSideImage)
//        val rightSideButton = itemView?.findViewById<ToggleButton>(R.id.listRightSideButton)
//        val votesCounter = itemView?.findViewById<TextView>(R.id.listVotesCounter)
//        val messageCounter = itemView?.findViewById<TextView>(R.id.listMessageCounter)
//        val debateInfoView = itemView?.findViewById<LinearLayout>(R.id.listInfoView)

        fun bindDebate(debate: Debate) {
            category.text = debate.category.name
            Picasso.get().load(debate.leftSide.image).into(leftSideImage)
            Picasso.get().load(debate.rightSide.image).into(rightSideImage)
            voteContainer.acceptModel(
                VoteContainerModel(
                    leftName = debate.leftSide.name,
                    rightName = debate.rightSide.name,
                    leftVote = debate.leftSide.ratingCount,
                    rightVote = debate.rightSide.ratingCount
                )
            )

//            leftSideButton?.text = debate.leftSide.name
//            rightSideButton?.text = debate.rightSide.name
//            votesCounter?.text = debate.votesCount.toString()
//            messageCounter?.text = debate.messageCount.toString()

//            leftSideButton?.setOnClickListener {
//                voteClick(debate, debate.leftSide)
//            }
//
//            rightSideButton?.setOnClickListener {
//                voteClick(debate, debate.rightSide)
//            }
//
//            rightSideImage?.setOnClickListener {
//                debateClick(debate)
//            }
//
//            leftSideImage?.setOnClickListener {
//                debateClick(debate)
//            }
//
//            debateInfoView?.setOnClickListener {
//                debateClick(debate)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val container = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            addView(
                MaterialTextView(parent.context).apply {
                    id = R.id.category_id
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    )
                    updatePadding(top = parent.dip(12), bottom = parent.dip(12))
                }
            )

            addView(LinearLayout(parent.context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                )
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                dividerDrawable = GradientDrawable().apply {
                    setSize(parent.dip(1), 0)
                }
                background = GradientDrawable().apply {
                    cornerRadius = parent.dip(10).toFloat()
                }
                clipToOutline = true
                outlineProvider = ViewOutlineProvider.BACKGROUND
                weightSum = 2.0f

                addView(AppCompatImageView(parent.context).apply {
                    id = R.id.left_image
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        parent.dip(140)
                    ).apply {
                        weight = 1.0f
                    }
                })

                addView(AppCompatImageView(parent.context).apply {
                    id = R.id.right_image
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        parent.dip(140)
                    ).apply {
                        weight = 1.0f
                    }
                })
            })
            addView(VoteContainerWidget(parent.context).apply {
                id = R.id.vote_container
                updatePadding(
                    top = parent.dip(16),
                    left = parent.dip(16),
                    right = parent.dip(16)
                )
            })
        }

        return ViewHolder(container)
    }

    override fun getItemCount(): Int {
        return response.debates.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDebate(response.debates[position])
    }
}