package com.whocooler.app.DebateList.Adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebateSide
import com.whocooler.app.Common.Models.DebatesResponse
import com.whocooler.app.Common.Utilities.dip
import com.whocooler.app.Common.ui.votecontainer.VoteContainerModel
import com.whocooler.app.Common.ui.votecontainer.VoteContainerWidget
import com.whocooler.app.R
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlin.properties.Delegates


class DebateListAdapter(
    var debates: ArrayList<Debate>,
    val voteClick: (Debate, DebateSide, Int) -> PublishSubject<Debate>,
    val debateClick: (Debate, Int) -> Unit,
    val authRequired: () -> Unit,
    val toggleFavorites: ((Debate) -> Unit)? = null,
    val shouldShowDebateInfo: Boolean = false,
    val didClickMore: () -> Unit
) : RecyclerView.Adapter<DebateListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val category: MaterialTextView = itemView.findViewById(R.id.category_id)
        private val leftSideImage: AppCompatImageView = itemView.findViewById(R.id.left_image)
        private val rightSideImage: AppCompatImageView = itemView.findViewById(R.id.right_image)
        private val voteContainer: VoteContainerWidget = itemView.findViewById(R.id.vote_container)
        private val votesCounter: MaterialTextView?= itemView?.findViewById(R.id.listVotesCounter)
        private val messageCounter: MaterialTextView? = itemView?.findViewById(R.id.listMessageCounter)
        private val favorites: AppCompatImageView? = itemView?.findViewById(R.id.detail_favorites)
        private val more: AppCompatImageView? = itemView?.findViewById(R.id.list_more)

        fun bindDebate(debate: Debate) {
            Picasso.get().load(debate.leftSide.image).into(leftSideImage)
            Picasso.get().load(debate.rightSide.image).into(rightSideImage)

            category.text = debate.category.name

            if (shouldShowDebateInfo == true) {
                votesCounter?.text = debate.votesCount.toString()
                messageCounter?.text = debate.messageCount.toString()

                if (debate.isFavorite) {
                    favorites?.setImageResource(R.drawable.filled_favorites)
                } else {
                    favorites?.setImageResource(R.drawable.non_filled_favorites)
                }

                favorites?.setOnClickListener {
                    toggleFavorites?.let {
                        it(debate)
                    }
                }
            }
            voteContainer.acceptModel(VoteContainerModel(debate = debate))

            leftSideImage.setOnClickListener {
                debateClick(debate, adapterPosition)
            }

            rightSideImage.setOnClickListener {
                debateClick(debate, adapterPosition)
            }

            more?.setOnClickListener {
                didClickMore()
            }

            voteContainer.leftClicked = {
                if (App.prefs.isTokenEmpty()) {
                    authRequired()
                } else {
                    voteClick(debate, debate.leftSide, adapterPosition).subscribe {
                        voteContainer.acceptModel(VoteContainerModel(debate = it), true)
                    }
                }
            }

            voteContainer.rightClicked = {
                if (App.prefs.isTokenEmpty()) {
                    authRequired()
                } else {
                    voteClick(debate, debate.rightSide, adapterPosition).subscribe {
                        voteContainer.acceptModel(VoteContainerModel(debate = it), true)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val container = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            addView(ConstraintLayout(parent.context).apply {
                id = R.id.list_constraint_layout
                layoutParams = LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dip(12), dip(12), dip(12), dip(12))
                }
                addView(
                    MaterialTextView(parent.context).apply {
                        id = R.id.category_id
                        gravity = Gravity.CENTER_VERTICAL or Gravity.START or Gravity.LEFT
                        layoutParams = LinearLayout.LayoutParams(
                            RecyclerView.LayoutParams.WRAP_CONTENT,
                            RecyclerView.LayoutParams.WRAP_CONTENT
                        )
                    }
                )
                addView(
                    AppCompatImageView(parent.context).apply {
                        id = R.id.list_more
                        setImageResource(R.drawable.more)
                        gravity = Gravity.CENTER_VERTICAL or Gravity.END or Gravity.RIGHT
                        layoutParams = LinearLayout.LayoutParams(
                            dip(16),
                            dip(16)
                        )
                    }
                )

                val set = ConstraintSet()
                var parentLayout = findViewById<ConstraintLayout>(R.id.list_constraint_layout)
                set.clone(parentLayout)
                set.connect(R.id.category_id, ConstraintSet.START, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.START)
                set.connect(R.id.category_id, ConstraintSet.TOP, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.TOP)
                set.connect(R.id.list_more, ConstraintSet.TOP, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.TOP)
                set.connect(R.id.list_more, ConstraintSet.END, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.END)

                set.applyTo(parentLayout)
            })

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
            if (shouldShowDebateInfo == true) {
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
                        id = R.id.detail_favorites
                        layoutParams = LinearLayout.LayoutParams(
                            dip(18),
                            LinearLayout.LayoutParams.MATCH_PARENT
                        ).apply {
                            setMargins(dip(12), 0, 0, 0)
                        }
                    })
                })
            }
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
        return debates.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindDebate(debates[position])
    }

    fun update(debates: ArrayList<Debate>) {
        this.debates = debates
        notifyDataSetChanged()
    }

}