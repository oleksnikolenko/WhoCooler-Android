package com.example.whocooler.DebateList

import android.animation.LayoutTransition
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AnimationUtils
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
    var response: DebatesResponse,
    val voteClick: (Debate, DebateSide, Int) -> Unit,
    val debateClick: (Debate) -> Unit
) : RecyclerView.Adapter<DebateListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val category: MaterialTextView = itemView.findViewById(R.id.category_id)
        private val leftSideImage: AppCompatImageView = itemView.findViewById(R.id.left_image)
        private val rightSideImage: AppCompatImageView = itemView.findViewById(R.id.right_image)
        private val voteContainer: VoteContainerWidget = itemView.findViewById(R.id.vote_container)

        val votesCounter = itemView?.findViewById<MaterialTextView>(R.id.listVotesCounter)
        val messageCounter = itemView?.findViewById<MaterialTextView>(R.id.listMessageCounter)

        fun bindDebate(debate: Debate) {
            Picasso.get().load(debate.leftSide.image).into(leftSideImage)
            Picasso.get().load(debate.rightSide.image).into(rightSideImage)

            category.text = debate.category.name
            voteContainer.acceptModel(VoteContainerModel(debate = debate))
            votesCounter.text = debate.votesCount.toString()
            messageCounter.text = debate.messageCount.toString()

            voteContainer.leftClicked = {
                voteClick(debate, debate.leftSide, adapterPosition)
                voteContainer.acceptModel(VoteContainerModel(debate = reloadVoteButton(adapterPosition, debate.leftSide)), true)
            }

            voteContainer.rightClicked = {
                voteClick(debate, debate.rightSide, adapterPosition)
                voteContainer.acceptModel(VoteContainerModel(debate = reloadVoteButton(adapterPosition, debate.rightSide)), true)
            }
        }
    }

    private fun reloadVoteButton(position: Int, side: DebateSide): Debate {
        val currentLeftSide = response.debates[position].leftSide
        val currentRightSide = response.debates[position].rightSide

        if (currentLeftSide.id == side.id) {
            if (currentLeftSide.isVotedByUser) {
                response.debates[position].leftSide.isVotedByUser = false
                response.debates[position].leftSide.ratingCount -= 1
                response.debates[position].votesCount -= 1
            } else if (currentRightSide.isVotedByUser) {
                response.debates[position].leftSide.isVotedByUser = true
                response.debates[position].leftSide.ratingCount += 1

                response.debates[position].rightSide.isVotedByUser = false
                response.debates[position].rightSide.ratingCount -= 1
            } else {
                response.debates[position].leftSide.isVotedByUser = true
                response.debates[position].leftSide.ratingCount += 1
            }
        } else {
            if (currentRightSide.isVotedByUser) {
                response.debates[position].rightSide.isVotedByUser = false
                response.debates[position].rightSide.ratingCount -= 1
                response.debates[position].votesCount -= 1
            } else if (currentLeftSide.isVotedByUser) {
                response.debates[position].rightSide.isVotedByUser = true
                response.debates[position].rightSide.ratingCount += 1

                response.debates[position].leftSide.isVotedByUser = false
                response.debates[position].leftSide.ratingCount -= 1
            } else {
                response.debates[position].rightSide.isVotedByUser = true
                response.debates[position].rightSide.ratingCount += 1
            }
        }

        return response.debates[position]
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
                    updatePadding(
                        left = parent.dip(12),
                        top = parent.dip(12),
                        bottom = parent.dip(12)
                    )
                }
            )

            addView(LinearLayout(parent.context).apply {
                val customLayoutParams = LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                )
                customLayoutParams.setMargins(dip(12), 0, dip(12), dip(20))

                layoutParams = customLayoutParams
                orientation = LinearLayout.HORIZONTAL
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
            })
            addView(LinearLayout(parent.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dip(18)
                ).apply {
                    setMargins(0, dip(16), 0, 0)
                }
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER

                addView(AppCompatImageView(parent.context).apply {
                    id = R.id.vote_counter_image
                    layoutParams = LinearLayout.LayoutParams(
                        parent.dip(18),
                        parent.dip(18)
                    ).apply {
                        setImageResource(R.drawable.user)
                    }
                })
                addView(MaterialTextView(parent.context).apply {
                    id = R.id.listVotesCounter
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.MATCH_PARENT
                    ).apply {
                        setMargins(dip(10), 0, 0, 0)
                        setTextColor(Color.BLACK)
                    }
                })
                addView(AppCompatImageView(parent.context).apply {
                    id = R.id.message_counter_image
                    layoutParams = LinearLayout.LayoutParams(
                        dip(18),
                        LinearLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                        setMargins(dip(12), 0, 0, 0)
                        setImageResource(R.drawable.message)
                    }
                })
                addView(MaterialTextView(parent.context).apply {
                    id = R.id.listMessageCounter
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.MATCH_PARENT
                    ).apply {
                        setMargins(dip(12), 0, 0, 0)
                        setTextColor(Color.BLACK)
                    }
                })
                addView(AppCompatImageView(parent.context).apply {
                    id = R.id.favorites_image
                    layoutParams = LinearLayout.LayoutParams(
                        dip(18),
                        LinearLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                        setMargins(dip(12), 0, 0, 0)
                        setImageResource(R.drawable.non_filled_favorites)
                    }
                })
            })

            addView(LinearLayout(parent.context).apply {
                val dividerLayoutParams = LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    parent.dip(1)
                )
                dividerLayoutParams.topMargin = 40

                layoutParams = dividerLayoutParams

                setBackgroundColor(Color.LTGRAY)
            })

            refreshDrawableState()
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