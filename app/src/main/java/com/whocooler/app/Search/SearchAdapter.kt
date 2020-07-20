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
import androidx.recyclerview.widget.RecyclerView
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Utilities.dip
import com.whocooler.app.Common.ui.votecontainer.VoteContainerModel
import com.whocooler.app.Common.ui.votecontainer.VoteContainerWidget
import com.whocooler.app.R
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.whocooler.app.Common.Utilities.VOTE_BUTTON_SHADE_COLOR


class SearchAdapter(
    var rows: ArrayList<SearchAdapter.ISearchListRow>,
    val debateClick: (Debate, Int) -> Unit,
    val didClickMore: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ISearchListRow
    class SidesRow(val debate: Debate) : ISearchListRow
    class StatementRow(val debate: Debate) : ISearchListRow

    companion object {
        private const val TYPE_SIDES = 0
        private const val TYPE_STATEMENT = 1
    }

    class SidesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: MaterialTextView = itemView.findViewById(R.id.category_id)
        val leftSideImage: AppCompatImageView = itemView.findViewById(R.id.left_image)
        val rightSideImage: AppCompatImageView = itemView.findViewById(R.id.right_image)
        val voteContainer: VoteContainerWidget = itemView.findViewById(R.id.vote_container)
        val votesCounter: MaterialTextView?= itemView?.findViewById(R.id.listVotesCounter)
        val messageCounter: MaterialTextView? = itemView?.findViewById(R.id.listMessageCounter)
        val favorites: AppCompatImageView? = itemView?.findViewById(R.id.detail_favorites)
        val more: AppCompatImageView? = itemView?.findViewById(R.id.list_more)
        var debateName: MaterialTextView? = itemView.findViewById(R.id.list_debate_name)
    }

    class StatementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: MaterialTextView = itemView.findViewById(R.id.category_id)
        val debateImage: AppCompatImageView = itemView.findViewById(R.id.left_image)
        val voteContainer: VoteContainerWidget = itemView.findViewById(R.id.vote_container)
        val votesCounter: MaterialTextView?= itemView?.findViewById(R.id.listVotesCounter)
        val messageCounter: MaterialTextView? = itemView?.findViewById(R.id.listMessageCounter)
        val favorites: AppCompatImageView? = itemView?.findViewById(R.id.detail_favorites)
        val more: AppCompatImageView? = itemView?.findViewById(R.id.list_more)
        var debateName: MaterialTextView? = itemView.findViewById(R.id.list_debate_name)
    }

    override fun getItemViewType(position: Int): Int = when(rows[position]) {
        is SearchAdapter.SidesRow -> SearchAdapter.TYPE_SIDES
        is SearchAdapter.StatementRow -> SearchAdapter.TYPE_STATEMENT
        else -> throw IllegalArgumentException()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        SearchAdapter.TYPE_SIDES -> onCreateSidesViewHolder(parent)
        SearchAdapter.TYPE_STATEMENT -> onCreateStatementViewHolder(parent)
        else -> throw IllegalArgumentException()
    }

    override fun getItemCount(): Int = rows.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder.itemViewType) {
        SearchAdapter.TYPE_SIDES -> onBindSides(holder, rows[position] as SidesRow)
        SearchAdapter.TYPE_STATEMENT -> onBindStatement(holder, rows[position] as StatementRow)
        else -> throw IllegalArgumentException()
    }

    private fun onCreateSidesViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
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
                        parent.dip(150)
                    ).apply {
                        weight = 1.0f
                        setBackgroundColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
                    }
                })

                addView(AppCompatImageView(parent.context).apply {
                    id = R.id.right_image
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        parent.dip(150)
                    ).apply {
                        weight = 1.0f
                        setBackgroundColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
                    }
                })
            })
            addView(MaterialTextView(parent.context).apply {
                id = R.id.list_debate_name
                gravity = Gravity.CENTER
                layoutParams =  LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dip(16), 0, dip(16), dip(12))
                    setTextColor(Color.BLACK)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    textSize = 20f
                }
            })
            addView(VoteContainerWidget(parent.context).apply {
                id = R.id.vote_container
            })
            addView(LinearLayout(parent.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
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
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(dip(10), 0, 0, 0)
                        setTextColor(Color.BLACK)
                    }
                })
                addView(AppCompatImageView(parent.context).apply {
                    id = R.id.message_counter_image
                    layoutParams = LinearLayout.LayoutParams(
                        parent.dip(18),
                        parent.dip(18)
                    ).apply {
                        setMargins(dip(12), 0, 0, 0)
                        setImageResource(R.drawable.message)
                    }
                })
                addView(MaterialTextView(parent.context).apply {
                    id = R.id.listMessageCounter
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
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

        return SidesViewHolder(container)
    }

    private fun onCreateStatementViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
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
                weightSum = 1.0f

                addView(AppCompatImageView(parent.context).apply {
                    id = R.id.left_image
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        parent.dip(225)
                    ).apply {
                        weight = 1.0f
                        setBackgroundColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
                    }
                })
            })
            addView(MaterialTextView(parent.context).apply {
                id = R.id.list_debate_name
                gravity = Gravity.CENTER
                layoutParams =  LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dip(16), 0, dip(16), dip(12))
                    setTextColor(Color.BLACK)
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    textSize = 20f
                }
            })
            addView(VoteContainerWidget(parent.context).apply {
                id = R.id.vote_container
            })
            addView(LinearLayout(parent.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
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
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(dip(10), 0, 0, 0)
                        setTextColor(Color.BLACK)
                    }
                })
                addView(AppCompatImageView(parent.context).apply {
                    id = R.id.message_counter_image
                    layoutParams = LinearLayout.LayoutParams(
                        parent.dip(18),
                        parent.dip(18)
                    ).apply {
                        setMargins(dip(12), 0, 0, 0)
                        setImageResource(R.drawable.message)
                    }
                })
                addView(MaterialTextView(parent.context).apply {
                    id = R.id.listMessageCounter
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
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

        return SearchAdapter.StatementViewHolder(container)
    }

    fun update(rows: ArrayList<ISearchListRow>) {
        this.rows = rows
        notifyDataSetChanged()
    }

    fun onBindSides(holder: RecyclerView.ViewHolder, row: SidesRow) {
        var sidesHolder = holder as SearchAdapter.SidesViewHolder

        Picasso.get().load(row.debate.leftSide.image).into(sidesHolder.leftSideImage)
        Picasso.get().load(row.debate.rightSide.image).into(sidesHolder.rightSideImage)

        sidesHolder.category.text = row.debate.category.name

        if (row.debate.name != null) {
            sidesHolder.debateName?.text = row.debate.name
            sidesHolder.debateName?.visibility = View.VISIBLE
        } else {
            sidesHolder.debateName?.visibility = View.GONE
        }

        sidesHolder.votesCounter?.text = row.debate.votesCount.toString()
        sidesHolder.messageCounter?.text = row.debate.messageCount.toString()

        if (row.debate.isFavorite) {
            sidesHolder.favorites?.setImageResource(R.drawable.filled_favorites)
        } else {
            sidesHolder.favorites?.setImageResource(R.drawable.non_filled_favorites)
        }

        sidesHolder.favorites?.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }
        sidesHolder.voteContainer.acceptModel(VoteContainerModel(debate = row.debate))

        sidesHolder.leftSideImage.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.rightSideImage.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.more?.setOnClickListener {
            didClickMore()
        }

        sidesHolder.voteContainer.leftClicked = {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.voteContainer.rightClicked = {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }
    }

    fun onBindStatement(holder: RecyclerView.ViewHolder, row: StatementRow) {
        var statementHolder = holder as SearchAdapter.StatementViewHolder

        Picasso.get().load(row.debate.image).into(statementHolder.debateImage)

        statementHolder.category.text = row.debate.category.name

        if (row.debate.name != null) {
            statementHolder.debateName?.text = row.debate.name
            statementHolder.debateName?.visibility = View.VISIBLE
        } else {
            statementHolder.debateName?.visibility = View.GONE
        }

        statementHolder.votesCounter?.text = row.debate.votesCount.toString()
        statementHolder.messageCounter?.text = row.debate.messageCount.toString()

        if (row.debate.isFavorite) {
            statementHolder.favorites?.setImageResource(R.drawable.filled_favorites)
        } else {
            statementHolder.favorites?.setImageResource(R.drawable.non_filled_favorites)
        }

        statementHolder.favorites?.setOnClickListener {
            debateClick(row.debate, statementHolder.adapterPosition)
        }
        statementHolder.voteContainer.acceptModel(VoteContainerModel(debate = row.debate))

        statementHolder.debateImage.setOnClickListener {
            debateClick(row.debate, statementHolder.adapterPosition)
        }

        statementHolder.more?.setOnClickListener {
            didClickMore()
        }

        statementHolder.voteContainer.leftClicked = {
            debateClick(row.debate, statementHolder.adapterPosition)
        }

        statementHolder.voteContainer.rightClicked = {
            debateClick(row.debate, statementHolder.adapterPosition)
        }
    }

}