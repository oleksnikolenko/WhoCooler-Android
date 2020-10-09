package com.whocooler.app.DebateList.Adapters

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebateSide
import com.whocooler.app.Common.Utilities.dip
import com.whocooler.app.Common.ui.votecontainer.VoteContainerModel
import com.whocooler.app.Common.ui.votecontainer.VoteContainerWidget
import com.whocooler.app.R
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.whocooler.app.Common.Utilities.VOTE_BUTTON_SHADE_COLOR
import io.reactivex.rxjava3.subjects.PublishSubject


class DebateListAdapter(
    var rows: ArrayList<IDebateListRow>,
    val voteClick: (Debate, DebateSide, Int) -> PublishSubject<Debate>,
    val debateClick: (Debate, Int) -> Unit,
    val authRequired: () -> Unit,
    val toggleFavorites: ((Debate) -> Unit)? = null,
    val shouldShowDebateInfo: Boolean = false,
    val didClickMore: () -> Unit,
    val didClickAgreeToContactFeedback: () -> Unit,
    val didAgreeToSendInputFeedback: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface IDebateListRow
    class SidesRow(val debate: Debate) : IDebateListRow
    class StatementRow(val debate: Debate) : IDebateListRow
    class ContactFeedbackRow: IDebateListRow
    class FeedbackInputRow: IDebateListRow

    companion object {
        private const val TYPE_SIDES = 0
        private const val TYPE_STATEMENT = 1
        private const val TYPE_CONTACT_FEEDBACK = 2
        private const val TYPE_FEEDBACK_INPUT = 3
    }

    class SidesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: MaterialTextView = itemView.findViewById(R.id.category_id)
        val leftSideImage: AppCompatImageView = itemView.findViewById(R.id.left_image)
        val rightSideImage: AppCompatImageView = itemView.findViewById(R.id.right_image)
        var debateName: MaterialTextView? = itemView.findViewById(R.id.list_debate_name)
        val voteContainer: VoteContainerWidget = itemView.findViewById(R.id.vote_container)
        val votesCounter: MaterialTextView?= itemView?.findViewById(R.id.listVotesCounter)
        val messageCounter: MaterialTextView? = itemView?.findViewById(R.id.listMessageCounter)
        val favorites: AppCompatImageView? = itemView?.findViewById(R.id.detail_favorites)
        val debateInfoView: LinearLayout? = itemView?.findViewById(R.id.list_info_view)
        val more: AppCompatImageView? = itemView?.findViewById(R.id.list_more)
    }

    class StatementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: MaterialTextView = itemView.findViewById(R.id.category_id)
        val debateImage: AppCompatImageView = itemView.findViewById(R.id.left_image)
        var debateName: MaterialTextView? = itemView.findViewById(R.id.list_debate_name)
        val voteContainer: VoteContainerWidget = itemView.findViewById(R.id.vote_container)
        val votesCounter: MaterialTextView?= itemView?.findViewById(R.id.listVotesCounter)
        val messageCounter: MaterialTextView? = itemView?.findViewById(R.id.listMessageCounter)
        val favorites: AppCompatImageView? = itemView?.findViewById(R.id.detail_favorites)
        val debateInfoView: LinearLayout? = itemView?.findViewById(R.id.list_info_view)
        val more: AppCompatImageView? = itemView?.findViewById(R.id.list_more)
    }

    class ContactFeedbackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: MaterialTextView = itemView.findViewById(R.id.contact_feedback_title)
        val description: MaterialTextView = itemView.findViewById(R.id.contact_feedback_description)
        val agreeButton: Button = itemView.findViewById(R.id.contact_feedback_agree)
    }

    class FeedbackInputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: MaterialTextView = itemView.findViewById(R.id.contact_feedback_title)
        val description: MaterialTextView = itemView.findViewById(R.id.contact_feedback_description)
        val agreeButton: Button = itemView.findViewById(R.id.contact_feedback_agree)
    }

    override fun getItemViewType(position: Int): Int = when(rows[position]) {
        is SidesRow -> TYPE_SIDES
        is StatementRow -> TYPE_STATEMENT
        is ContactFeedbackRow -> TYPE_CONTACT_FEEDBACK
        is FeedbackInputRow -> TYPE_FEEDBACK_INPUT
        else -> throw IllegalArgumentException()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_SIDES -> onCreateSidesViewHolder(parent)
        TYPE_STATEMENT -> onCreateStatementViewHolder(parent)
        TYPE_CONTACT_FEEDBACK -> onCreateFeedback(parent)
        TYPE_FEEDBACK_INPUT -> onCreateFeedback(parent)
        else -> throw IllegalArgumentException()
    }

    override fun getItemCount(): Int = rows.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder.itemViewType) {
        TYPE_SIDES -> onBindSides(holder, rows[position] as SidesRow)
        TYPE_STATEMENT -> onBindStatement(holder, rows[position] as StatementRow)
        TYPE_CONTACT_FEEDBACK -> onBindContactFeedBack(holder, rows[position] as ContactFeedbackRow)
        TYPE_FEEDBACK_INPUT -> onBindFeedbackInput(holder, rows[position] as FeedbackInputRow)
        else -> throw IllegalArgumentException()
    }

    fun onCreateFeedback(parent: ViewGroup): RecyclerView.ViewHolder {
        val container = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            addView(
                MaterialTextView(parent.context).apply {
                    id = R.id.contact_feedback_title
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(dip(12), dip(16), dip(12), 0)
                        setTextColor(Color.BLACK)
                        typeface = Typeface.DEFAULT_BOLD
                        textSize = 18f
                    }

                    gravity = Gravity.CENTER_HORIZONTAL
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
            )
            addView(
                MaterialTextView(parent.context).apply {
                    id = R.id.contact_feedback_description
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(dip(12), dip(8), dip(12), 0)
                        setTextColor(Color.BLACK)
                    }
                    gravity = Gravity.CENTER_HORIZONTAL
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                }
            )
            addView(
                Button(parent.context).apply {
                    id = R.id.contact_feedback_agree
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(dip(16), dip(8), dip(16), 0)

                    }
                }
            )
            addView(LinearLayout(parent.context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    parent.dip(1)
                ).apply {
                    setMargins(0, dip(8), 0, dip(8))
                    setBackgroundColor(Color.LTGRAY)
                }
            })
        }

        return ContactFeedbackViewHolder(container)
    }

    fun onCreateSidesViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
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
                gravity = Gravity.LEFT
                layoutParams =  LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dip(24), 0, dip(16), dip(16))
                    setTextColor(Color.BLACK)
                    typeface = Typeface.DEFAULT_BOLD
                    textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                    textSize = 24f
                }
            })
            addView(VoteContainerWidget(parent.context).apply {
                id = R.id.vote_container
            })
            // DEBATE INFO VIEW
            addView(LinearLayout(parent.context).apply {
                id = R.id.list_info_view
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setPadding(0, dip(16), 0, dip(16))
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
                layoutParams = LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    parent.dip(1)
                ).apply {
                    setBackgroundColor(Color.LTGRAY)
                }
            })

            refreshDrawableState()
        }

        return SidesViewHolder(container)
    }

    fun onCreateStatementViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
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
                gravity = Gravity.LEFT
                layoutParams =  LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dip(24), 0, dip(16), dip(16))
                    setTextColor(Color.BLACK)
                    typeface = Typeface.DEFAULT_BOLD
                    textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                    textSize = 24f
                }
            })
            addView(VoteContainerWidget(parent.context).apply {
                id = R.id.vote_container
            })
            // DEBATE INFO VIEW
            addView(LinearLayout(parent.context).apply {
                id = R.id.list_info_view
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setPadding(0, dip(16), 0, dip(16))
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
                layoutParams = LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    parent.dip(1)
                ).apply {
                    setBackgroundColor(Color.LTGRAY)
                }
            })

            refreshDrawableState()
        }

        return StatementViewHolder(container)
    }

    fun onBindSides(holder: RecyclerView.ViewHolder, row: SidesRow) {
        var sidesHolder = holder as DebateListAdapter.SidesViewHolder
        var debate = row.debate
        Picasso.get().load(debate.leftSide.image).into(sidesHolder.leftSideImage)
        Picasso.get().load(debate.rightSide.image).into(sidesHolder.rightSideImage)

        sidesHolder.category.text = if (debate.promotionType == "debate_of_day") debate.category.name else "Дебат дня"

        if (debate.name != null) {
            sidesHolder.debateName?.text = debate.name
            sidesHolder.debateName?.visibility = View.VISIBLE
        } else {
            sidesHolder.debateName?.visibility = View.GONE
        }

        sidesHolder.votesCounter?.text = debate.votesCount.toString()
        sidesHolder.messageCounter?.text = debate.messageCount.toString()

        if (debate.isFavorite) {
            sidesHolder.favorites?.setImageResource(R.drawable.filled_favorites)
        } else {
            sidesHolder.favorites?.setImageResource(R.drawable.non_filled_favorites)
        }

        sidesHolder.favorites?.setOnClickListener {
            toggleFavorites?.let {
                it(debate)
            }
        }

        sidesHolder.voteContainer.acceptModel(VoteContainerModel(debate = row.debate))

        sidesHolder.leftSideImage.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.rightSideImage.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.category.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.debateName?.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.debateInfoView?.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.more?.setOnClickListener {
            didClickMore()
        }

        sidesHolder.voteContainer.leftClicked = {
            if (App.prefs.isTokenEmpty()) {
                authRequired()
            } else {
                voteClick(row.debate, row.debate.leftSide, sidesHolder.adapterPosition).subscribe {
                    sidesHolder.voteContainer.acceptModel(VoteContainerModel(debate = it), true)
                }
            }
        }

        sidesHolder.voteContainer.rightClicked = {
            if (App.prefs.isTokenEmpty()) {
                authRequired()
            } else {
                voteClick(row.debate, row.debate.rightSide, sidesHolder.adapterPosition).subscribe {
                    sidesHolder.voteContainer.acceptModel(VoteContainerModel(debate = it), true)
                }
            }
        }
    }

    fun onBindContactFeedBack(holder: RecyclerView.ViewHolder, row: ContactFeedbackRow) {
        var contactFeedbackHolder = holder as ContactFeedbackViewHolder

        contactFeedbackHolder.title.text = App.appContext.getString(R.string.contact_feedback_title)
        contactFeedbackHolder.description.text = App.appContext.getString(R.string.contact_feedback_description)
        contactFeedbackHolder.agreeButton.text = App.appContext.getString(R.string.agree)

        contactFeedbackHolder.agreeButton.setOnClickListener {
            didClickAgreeToContactFeedback()
        }
    }

    fun onBindFeedbackInput(holder: RecyclerView.ViewHolder, row: FeedbackInputRow) {
        var feedbackInputHolder = holder as ContactFeedbackViewHolder

        feedbackInputHolder.title.text = App.appContext.getString(R.string.contact_feedback_title)
        feedbackInputHolder.description.text = App.appContext.getString(R.string.contact_feedback_input_description)
        feedbackInputHolder.agreeButton.text = App.appContext.getString(R.string.allright)

        feedbackInputHolder.agreeButton.setOnClickListener {
            didAgreeToSendInputFeedback()
        }
    }

    fun onBindStatement(holder: RecyclerView.ViewHolder, row: StatementRow) {
        var sidesHolder = holder as DebateListAdapter.StatementViewHolder
        var debate = row.debate

        Picasso.get().load(row.debate.image).into(sidesHolder.debateImage)

        sidesHolder.category.text = if (debate.promotionType == "debate_of_day") App.appContext.getString(R.string.debate_of_day) else debate.category.name

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
            toggleFavorites?.let {
                it(row.debate)
            }
        }

        sidesHolder.voteContainer.acceptModel(VoteContainerModel(debate = row.debate))

        sidesHolder.debateImage.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.more?.setOnClickListener {
            didClickMore()
        }

        sidesHolder.category.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.debateName?.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.debateInfoView?.setOnClickListener {
            debateClick(row.debate, sidesHolder.adapterPosition)
        }

        sidesHolder.voteContainer.leftClicked = {
            if (App.prefs.isTokenEmpty()) {
                authRequired()
            } else {
                voteClick(row.debate, row.debate.leftSide, sidesHolder.adapterPosition).subscribe {
                    sidesHolder.voteContainer.acceptModel(VoteContainerModel(debate = it), true)
                }
            }
        }

        sidesHolder.voteContainer.rightClicked = {
            if (App.prefs.isTokenEmpty()) {
                authRequired()
            } else {
                voteClick(row.debate, row.debate.rightSide, sidesHolder.adapterPosition).subscribe {
                    sidesHolder.voteContainer.acceptModel(VoteContainerModel(debate = it), true)
                }
            }
        }
    }

    fun update(rows: ArrayList<IDebateListRow>) {
        this.rows = rows
        notifyDataSetChanged()
    }

}