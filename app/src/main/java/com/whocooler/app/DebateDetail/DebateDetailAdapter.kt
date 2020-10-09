package com.whocooler.app.DebateDetail

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebateSide
import com.whocooler.app.Common.Models.Message
import com.whocooler.app.Common.Utilities.VOTE_BUTTON_SHADE_COLOR
import com.whocooler.app.Common.Utilities.dip
import com.whocooler.app.Common.Views.RoundRectCornerImageView
import com.whocooler.app.Common.ui.votecontainer.VoteContainerModel
import com.whocooler.app.Common.ui.votecontainer.VoteContainerWidget
import com.whocooler.app.R
import com.whocooler.app.VoteMessage.VoteMessageView
import io.reactivex.rxjava3.subjects.PublishSubject

class DebateDetailAdapter(
    val context: Context,
    var rows: List<IDetailRow>,
    val voteClick: (DebateSide) -> PublishSubject<Debate>,
    val authRequired: () -> Unit,
    val getNextRepliesPage: (message: Message, index: Int) -> Unit,
    val didClickReply: (parentMessageId: String, index: Int) -> Unit,
    val didClickMore: () -> Unit,
    val errorHappened: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface IDetailRow
    class HeaderSidesRow(val debate: Debate) : IDetailRow
    class HeaderStatementRow(val debate: Debate) : IDetailRow
    class MessageRow(val message: Message) : IDetailRow
    class MessageHeaderRow(var messageCount: Int) : IDetailRow
    class ReplyRow(var reply: Message): IDetailRow
    class EmptyMessagesRow(): IDetailRow

    companion object {
        private const val TYPE_HEADER_SIDES = 0
        private const val TYPE_HEADER_STATEMENT = 1
        private const val TYPE_MESSAGE = 2
        private const val TYPE_MESSAGE_HEADER = 3
        private const val TYPE_REPLY = 4
        private const val TYPE_EMPTY_MESSAGES = 5
    }

    class HeaderSidesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val leftSideImage: AppCompatImageView = itemView.findViewById(R.id.detail_left_image)
        val rightSideImage: AppCompatImageView = itemView.findViewById(R.id.detail_right_image)
        val debateName: MaterialTextView = itemView.findViewById(R.id.detail_debate_name)
        val voteContainer: VoteContainerWidget = itemView.findViewById(R.id.detail_vote_container)
    }

    class HeaderStatementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val debateImage: AppCompatImageView = itemView.findViewById(R.id.detail_left_image)
        val debateName: MaterialTextView = itemView.findViewById(R.id.detail_debate_name)
        val voteContainer: VoteContainerWidget = itemView.findViewById(R.id.detail_vote_container)
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userAvatar: AppCompatImageView = itemView.findViewById(R.id.detail_message_avatar)
        val userName: TextView = itemView.findViewById(R.id.detail_message_user_name)
        var messageText: TextView = itemView.findViewById(R.id.detail_message_text)
        var messageDate: TextView = itemView.findViewById(R.id.detail_message_date)
        var showRepliesTextView: MaterialTextView = itemView.findViewById(R.id.detail_message_show_replies)
        var messageReply: MaterialTextView = itemView.findViewById(R.id.detail_message_reply)
        var moreImage: AppCompatImageView = itemView.findViewById(R.id.detail_message_more)
        var voteMessageView: VoteMessageView = itemView.findViewById(R.id.detail_message_vote)
    }

    class MessageHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageCounter: MaterialTextView = itemView.findViewById(R.id.detail_message_header_counter)
        var messageCountNumber: Int = 0
    }

    class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userAvatar: AppCompatImageView = itemView.findViewById(R.id.detail_reply_avatar)
        val userName: TextView = itemView.findViewById(R.id.detail_reply_user_name)
        var messageText: TextView = itemView.findViewById(R.id.detail_reply_text)
        var messageDate: TextView = itemView.findViewById(R.id.detail_reply_date)
        var reply: MaterialTextView = itemView.findViewById(R.id.detail_reply_reply)
        var voteReplyView: VoteMessageView = itemView.findViewById(R.id.detail_reply_vote)
    }

    class EmptyMessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun getItemViewType(position: Int): Int = when(rows[position]) {
        is HeaderSidesRow -> TYPE_HEADER_SIDES
        is HeaderStatementRow -> TYPE_HEADER_STATEMENT
        is MessageRow -> TYPE_MESSAGE
        is MessageHeaderRow -> TYPE_MESSAGE_HEADER
        is ReplyRow -> TYPE_REPLY
        is EmptyMessagesRow -> TYPE_EMPTY_MESSAGES
        else -> throw IllegalArgumentException()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_HEADER_SIDES -> headerSidesViewHolder()
        TYPE_HEADER_STATEMENT -> headerStatementViewHolder()
        TYPE_MESSAGE -> messageViewHolder(parent)
        TYPE_MESSAGE_HEADER -> messageHeaderViewHolder(parent)
        TYPE_REPLY -> replyViewHolder(parent)
        TYPE_EMPTY_MESSAGES -> emptyMessagesViewHolder(parent)
        else -> throw IllegalArgumentException()
    }

    override fun getItemCount(): Int = rows.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder.itemViewType) {
        TYPE_HEADER_SIDES -> onBindSidesHeader(holder, rows[position] as HeaderSidesRow)
        TYPE_HEADER_STATEMENT -> onBindStatementHeader(holder, rows[position] as HeaderStatementRow)
        TYPE_MESSAGE -> onBindMessage(holder, rows[position] as MessageRow)
        TYPE_MESSAGE_HEADER -> onBindMessageHeader(holder, rows[position] as MessageHeaderRow)
        TYPE_REPLY -> onBindReply(holder, rows[position] as ReplyRow)
        TYPE_EMPTY_MESSAGES -> onBindEmptyMessages(holder, rows[position] as EmptyMessagesRow)
        else -> throw IllegalArgumentException()
    }

    private fun onBindSidesHeader(holder: RecyclerView.ViewHolder, row: HeaderSidesRow) {
        val headerRow = holder as HeaderSidesViewHolder

        Picasso.get().load(row.debate.leftSide.image).into(headerRow.leftSideImage)
        Picasso.get().load(row.debate.rightSide.image).into(headerRow.rightSideImage)

        if (row.debate.name != null) {
            headerRow.debateName.text = row.debate.name
            headerRow.debateName.visibility = View.VISIBLE
        } else {
            headerRow.debateName.visibility = View.GONE
        }

        headerRow.voteContainer.acceptModel(VoteContainerModel(debate = row.debate))

        headerRow.voteContainer.leftClicked = {
            if (App.prefs.isTokenEmpty()) {
                authRequired()
            } else {
                voteClick(row.debate.leftSide).subscribe {
                    headerRow.voteContainer.acceptModel(VoteContainerModel(debate = it), true)
                }
            }
        }

        headerRow.voteContainer.rightClicked = {
            if (App.prefs.isTokenEmpty()) {
                authRequired()
            } else {
                voteClick(row.debate.rightSide).subscribe {
                    headerRow.voteContainer.acceptModel(VoteContainerModel(debate = it), true)
                }
            }
        }
    }

    private fun onBindStatementHeader(holder: RecyclerView.ViewHolder, row: HeaderStatementRow) {
        val headerRow = holder as HeaderStatementViewHolder

        Picasso.get().load(row.debate.image).into(headerRow.debateImage)

        if (row.debate.name != null) {
            headerRow.debateName.text = row.debate.name
            headerRow.debateName.visibility = View.VISIBLE
        } else {
            headerRow.debateName.visibility = View.GONE
        }

        headerRow.voteContainer.acceptModel(VoteContainerModel(debate = row.debate))

        headerRow.voteContainer.leftClicked = {
            if (App.prefs.isTokenEmpty()) {
                authRequired()
            } else {
                voteClick(row.debate.leftSide).subscribe {
                    headerRow.voteContainer.acceptModel(VoteContainerModel(debate = it), true)
                }
            }
        }

        headerRow.voteContainer.rightClicked = {
            if (App.prefs.isTokenEmpty()) {
                authRequired()
            } else {
                voteClick(row.debate.rightSide).subscribe {
                    headerRow.voteContainer.acceptModel(VoteContainerModel(debate = it), true)
                }
            }
        }
    }

    private fun onBindMessage(holder: RecyclerView.ViewHolder, row: MessageRow) {
        var messageRow = holder as MessageViewHolder

        Picasso.get().load(row.message.user.avatar).into(messageRow.userAvatar)
        messageRow.userName.text = row.message.user.name
        messageRow.messageText.text = row.message.text
        messageRow.messageDate.text = getDateTime(row.message.createdTime)

        messageRow.messageReply.setOnClickListener {
            didClickReply(row.message.id, messageRow.adapterPosition - 2)
        }

        messageRow.moreImage.setOnClickListener {
            didClickMore()
        }

        messageRow.voteMessageView.acceptModel(row.message)

        messageRow.voteMessageView.authRequired.subscribe {
            authRequired()
        }

        messageRow.voteMessageView.errorHappened.subscribe {
            errorHappened()
        }

        if (row.message.getNotShownReplyCount() > 0) {
            messageRow.showRepliesTextView.text = "${context.getString(R.string.message_show)} ${row.message.getNotShownReplyCount()} ${context.getString(R.string.message_more_replies)}"
        } else {
            messageRow.showRepliesTextView.visibility = View.GONE
        }

        messageRow.showRepliesTextView.setOnClickListener {
            getNextRepliesPage(row.message, messageRow.adapterPosition)
        }
    }

    private fun onBindMessageHeader(holder: RecyclerView.ViewHolder, row: MessageHeaderRow) {
        var messageHeaderRow = holder as MessageHeaderViewHolder

        messageHeaderRow.messageCountNumber = row.messageCount
        messageHeaderRow.messageCounter.text =  messageHeaderRow.messageCountNumber.toString()
    }

    private fun onBindReply(holder: RecyclerView.ViewHolder, row: ReplyRow) {
        var replyRow = holder as ReplyViewHolder

        Picasso.get().load(row.reply.user.avatar).into(replyRow.userAvatar)
        replyRow.userName.text = row.reply.user.name
        replyRow.messageText.text = row.reply.text
        replyRow.messageDate.text = getDateTime(row.reply.createdTime)
        replyRow.reply.setOnClickListener {
            val threadId = row.reply.threadId

            if (threadId != null) {
                didClickReply(threadId, replyRow.adapterPosition - 2)
            }
        }

        replyRow.voteReplyView.acceptModel(row.reply)

        replyRow.voteReplyView.authRequired.subscribe {
            authRequired()
        }
        replyRow.voteReplyView.errorHappened.subscribe {
            errorHappened()
        }
    }

    private fun onBindEmptyMessages(holder: RecyclerView.ViewHolder, row: EmptyMessagesRow) {}

    private fun headerSidesViewHolder() : RecyclerView.ViewHolder {
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )

            addView(LinearLayout(context).apply {
                val customLayoutParams = LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                )
                customLayoutParams.setMargins(0, 0, 0, dip(20))

                layoutParams = customLayoutParams
                orientation = LinearLayout.HORIZONTAL
                showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                dividerDrawable = GradientDrawable().apply {
                    setSize(dip(1), 0)
                }
                clipToOutline = true
                outlineProvider = ViewOutlineProvider.BACKGROUND
                weightSum = 2.0f

                addView(AppCompatImageView(context).apply {
                    id = R.id.detail_left_image
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        dip(150)
                    ).apply {
                        weight = 1.0f
                        setBackgroundColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
                    }
                })

                addView(AppCompatImageView(context).apply {
                    id = R.id.detail_right_image
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        dip(150)
                    ).apply {
                        weight = 1.0f
                        setBackgroundColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
                    }
                })
            })
            addView(MaterialTextView(context).apply {
                id = R.id.detail_debate_name
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
            addView(VoteContainerWidget(context).apply {
                id = R.id.detail_vote_container
            })

            refreshDrawableState()
        }

        return HeaderSidesViewHolder(container)
    }

    private fun headerStatementViewHolder() : RecyclerView.ViewHolder {
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )

            addView(LinearLayout(context).apply {
                val customLayoutParams = LinearLayout.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                )
                customLayoutParams.setMargins(0, 0, 0, dip(20))

                layoutParams = customLayoutParams
                orientation = LinearLayout.HORIZONTAL
                dividerDrawable = GradientDrawable().apply {
                    setSize(dip(1), 0)
                }
                clipToOutline = true
                outlineProvider = ViewOutlineProvider.BACKGROUND
                weightSum = 1.0f

                addView(AppCompatImageView(context).apply {
                    id = R.id.detail_left_image
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        dip(225)
                    ).apply {
                        weight = 1.0f
                        setBackgroundColor(Color.parseColor(VOTE_BUTTON_SHADE_COLOR))
                    }
                })
            })
            addView(MaterialTextView(context).apply {
                id = R.id.detail_debate_name
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
            addView(VoteContainerWidget(context).apply {
                id = R.id.detail_vote_container
            })

            refreshDrawableState()
        }

        return HeaderStatementViewHolder(container)
    }

    private fun messageViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder {
        val container = ConstraintLayout(context).apply {
            id = R.id.message_constraint_layout
            layoutParams = LinearLayout.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )

            addView(
                RoundRectCornerImageView(context).apply {
                    id = R.id.detail_message_avatar
                    layoutParams = LinearLayout.LayoutParams(
                        dip(32),
                        dip(32)
                    )
                }
            )
            addView(
                MaterialTextView(context).apply {
                    id = R.id.detail_message_user_name
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setTextColor(Color.BLACK)
                        typeface = Typeface.DEFAULT_BOLD
                    }
                }
            )
            addView(
                MaterialTextView(context).apply {
                    id = R.id.detail_message_date
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        textSize = 12f
                    }
                }
            )
            addView(
                AppCompatImageView(context).apply {
                    id = R.id.detail_message_more
                    setImageResource(R.drawable.more)
                    layoutParams = LinearLayout.LayoutParams(
                        dip(36),
                        dip(36)
                    ).apply {
                        setPadding(dip(4), dip(12), dip(16), dip(8))
                    }
                }
            )
            addView(
                MaterialTextView(context).apply {
                    id = R.id.detail_message_text
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setTextColor(Color.BLACK)
                    }
                }
            )
            addView(
                MaterialTextView(context).apply {
                    id = R.id.detail_message_reply
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        text = context.getString(R.string.reply)
                        setPadding(0, dip(6), dip(6), dip(6))
                    }
                }
            )
            addView(VoteMessageView(context).apply {
                id = R.id.detail_message_vote
            })
            addView(
                MaterialTextView(context).apply {
                    id = R.id.detail_message_show_replies
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setTextColor(Color.BLACK)
                    }
                }
            )

            val set = ConstraintSet()
            var parentLayout = findViewById<ConstraintLayout>(R.id.message_constraint_layout)
            set.clone(parentLayout)
            set.connect(R.id.detail_message_avatar, ConstraintSet.START, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.START, dip(16))
            set.connect(R.id.detail_message_avatar, ConstraintSet.TOP, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.TOP, dip(16))
            set.connect(R.id.detail_message_user_name, ConstraintSet.START, R.id.detail_message_avatar, ConstraintSet.END, dip(12))
            set.connect(R.id.detail_message_user_name, ConstraintSet.TOP, R.id.detail_message_avatar, ConstraintSet.TOP)
            set.connect(R.id.detail_message_more, ConstraintSet.TOP, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.TOP)
            set.connect(R.id.detail_message_more, ConstraintSet.END, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.END)
            set.connect(R.id.detail_message_date, ConstraintSet.TOP, R.id.detail_message_user_name, ConstraintSet.TOP)
            set.connect(R.id.detail_message_date, ConstraintSet.BOTTOM, R.id.detail_message_user_name, ConstraintSet.BOTTOM)
            set.connect(R.id.detail_message_date, ConstraintSet.START, R.id.detail_message_user_name, ConstraintSet.END, dip(8))
            set.connect(R.id.detail_message_text, ConstraintSet.START, R.id.detail_message_user_name, ConstraintSet.START)
            set.connect(R.id.detail_message_text, ConstraintSet.TOP, R.id.detail_message_user_name, ConstraintSet.BOTTOM, dip(4))
            set.connect(R.id.detail_message_text, ConstraintSet.END, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.END, dip(16))
            set.connect(R.id.detail_message_reply, ConstraintSet.TOP, R.id.detail_message_text, ConstraintSet.BOTTOM)
            set.connect(R.id.detail_message_reply, ConstraintSet.START, R.id.detail_message_user_name, ConstraintSet.START)
            set.connect(R.id.detail_message_vote, ConstraintSet.END, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.END, dip(8))
            set.connect(R.id.detail_message_vote, ConstraintSet.TOP, R.id.detail_message_reply, ConstraintSet.TOP)
            set.connect(R.id.detail_message_vote, ConstraintSet.BOTTOM, R.id.detail_message_reply, ConstraintSet.BOTTOM)
            set.connect(R.id.detail_message_show_replies, ConstraintSet.TOP, R.id.detail_message_reply, ConstraintSet.BOTTOM, dip(8))
            set.connect(R.id.detail_message_show_replies, ConstraintSet.START, R.id.detail_message_user_name, ConstraintSet.START)

            set.applyTo(parentLayout)

            refreshDrawableState()
        }

        return MessageViewHolder(container)
    }

    private fun replyViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder {
        val container = ConstraintLayout(context).apply {
            id = R.id.reply_constraint_layout
            layoutParams = LinearLayout.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )

            addView(
                RoundRectCornerImageView(context).apply {
                    id = R.id.detail_reply_avatar
                    layoutParams = LinearLayout.LayoutParams(
                        dip(24),
                        dip(24)
                    )
                }
            )
            addView(
                MaterialTextView(context).apply {
                    id = R.id.detail_reply_user_name
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setTextColor(Color.BLACK)
                        typeface = Typeface.DEFAULT_BOLD
                    }
                }
            )
            addView(
                MaterialTextView(context).apply {
                    id = R.id.detail_reply_date
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        textSize = 12f
                    }
                }
            )
            addView(
                MaterialTextView(context).apply {
                    id = R.id.detail_reply_text
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setTextColor(Color.BLACK)
                    }
                }
            )
            addView(
                MaterialTextView(context).apply {
                    id = R.id.detail_reply_reply
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        text = context.getString(R.string.reply)
                        setPadding(0, dip(6), dip(6), dip(6))
                    }
                }
            )
            addView(VoteMessageView(context).apply {
                id = R.id.detail_reply_vote
            })

            val set = ConstraintSet()
            var parentLayout = findViewById<ConstraintLayout>(R.id.reply_constraint_layout)
            set.clone(parentLayout)
            set.connect(R.id.detail_reply_avatar, ConstraintSet.START, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.START, dip(46))
            set.connect(R.id.detail_reply_avatar, ConstraintSet.TOP, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.TOP, dip(8))
            set.connect(R.id.detail_reply_user_name, ConstraintSet.START, R.id.detail_reply_avatar, ConstraintSet.END, dip(12))
            set.connect(R.id.detail_reply_user_name, ConstraintSet.TOP, R.id.detail_reply_avatar, ConstraintSet.TOP)
            set.connect(R.id.detail_reply_date, ConstraintSet.TOP, R.id.detail_reply_user_name, ConstraintSet.TOP)
            set.connect(R.id.detail_reply_date, ConstraintSet.BOTTOM, R.id.detail_reply_user_name, ConstraintSet.BOTTOM)
            set.connect(R.id.detail_reply_date, ConstraintSet.START, R.id.detail_reply_user_name, ConstraintSet.END, dip(8))
            set.connect(R.id.detail_reply_text, ConstraintSet.START, R.id.detail_reply_user_name, ConstraintSet.START)
            set.connect(R.id.detail_reply_text, ConstraintSet.TOP, R.id.detail_reply_user_name, ConstraintSet.BOTTOM, dip(4))
            set.connect(R.id.detail_reply_text, ConstraintSet.END, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.END, dip(16))
            set.connect(R.id.detail_reply_reply, ConstraintSet.TOP, R.id.detail_reply_text, ConstraintSet.BOTTOM)
            set.connect(R.id.detail_reply_reply, ConstraintSet.START, R.id.detail_reply_user_name, ConstraintSet.START)
            set.connect(R.id.detail_reply_vote, ConstraintSet.END, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.END, dip(8))
            set.connect(R.id.detail_reply_vote, ConstraintSet.TOP, R.id.detail_reply_reply, ConstraintSet.TOP)
            set.connect(R.id.detail_reply_vote, ConstraintSet.BOTTOM, R.id.detail_reply_reply, ConstraintSet.BOTTOM)

            set.applyTo(parentLayout)

            refreshDrawableState()
        }

        return ReplyViewHolder(container)
    }

    private fun messageHeaderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder {
        val container = LinearLayout(context).apply {
            val customLayoutParams = LinearLayout.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )

            layoutParams = customLayoutParams
            orientation = LinearLayout.HORIZONTAL

            addView(
                MaterialTextView(context).apply {
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        textSize = 18f
                        setTextColor(Color.BLACK)
                        text = context.getString(R.string.comments_header)
                        setTypeface(Typeface.DEFAULT_BOLD)
                        setMargins(dip(16), dip(12), dip(12),0)
                    }
                }
            )
            addView(
                MaterialTextView(context).apply {
                    id = R.id.detail_message_header_counter
                    gravity = Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, dip(12), 0, 0)
                    }
                }
            )
        }

        return MessageHeaderViewHolder(container)
    }

    private fun emptyMessagesViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder {
        var container = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(0, dip(80), 0, 0)
            }

            gravity = Gravity.CENTER_HORIZONTAL

            addView(
                MaterialTextView(context).apply {
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    ).apply {
                        text = context.getString(R.string.empty_messages)
                    }
                }
            )
        }

        return EmptyMessagesViewHolder(container)
    }

    private fun getDateTime(d: Double): String? {
        val sdf = java.text.SimpleDateFormat("MMM d")
        val date = java.util.Date(d.toLong() * 1000)
        return sdf.format(date)
    }

    fun update(rows: List<IDetailRow>) {
        this.rows = rows
        notifyDataSetChanged()
    }

}