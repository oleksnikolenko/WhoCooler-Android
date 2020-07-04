package com.whocooler.app.VoteMessage

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.alpha
import com.google.android.material.textview.MaterialTextView
import com.whocooler.app.Common.Helpers.VoteType
import com.whocooler.app.Common.Models.Message
import com.whocooler.app.Common.Utilities.dip
import com.whocooler.app.R
import io.reactivex.rxjava3.subjects.PublishSubject

class VoteMessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), VoteMessageContracts.PresenterViewContract {

    lateinit var thumbsUpImage: AppCompatImageView
    lateinit var thumbsDownImage: AppCompatImageView
    lateinit var model: Message

    private var interactor: VoteMessageContracts.ViewInteractorContract? = null

    var authRequired = PublishSubject.create<Unit>()
    var errorHappened = PublishSubject.create<Unit>()

    init {
        setupModule()
        orientation = HORIZONTAL

        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            dip(30)
        )

        addViews()
    }

    private fun setupModule() {
        var view = this
        var presenter = VoteMessagePresenter()
        var interactor = VoteMessageInteractor()

        view.interactor = interactor
        interactor.presenter = presenter
        presenter.view = view
    }

    private fun addViews() {
        addView(
            AppCompatImageView(context).apply {
                id = R.id.vote_thumbs_up
                setImageResource(R.drawable.non_chosen_thumb_up)
                layoutParams = LayoutParams(
                    dip(16),
                    dip(16)
                )
                setOnClickListener {
                    if (model.voteType == VoteType.up) {
                        interactor?.deleteVote(model, model.threadId != null)
                    } else {
                        interactor?.postVote(model, VoteType.up, model.threadId != null)
                    }
                }
            }
        )
        addView(
            MaterialTextView(context).apply {
                id = R.id.vote_message_counter
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(dip(10), 0 , dip(10), 0)
                }
            }
        )
        addView(
            AppCompatImageView(context).apply {
                id = R.id.vote_thumbs_down
                setImageResource(R.drawable.non_chosen_thumb_down)
                layoutParams = LayoutParams(
                    dip(16),
                    dip(16)
                ).apply {
                    setMargins(0, dip(4), 0, 0 )
                }
                setOnClickListener {
                    if (model.voteType == VoteType.down) {
                        interactor?.deleteVote(model, model.threadId != null)
                    } else {
                        interactor?.postVote(model, VoteType.down, model.threadId != null)
                    }
                }
            }
        )
    }

    fun acceptModel(viewObject: Message) {
        model = viewObject
        val voteMessageCounter = findViewById<MaterialTextView>(R.id.vote_message_counter)

        thumbsUpImage = findViewById(R.id.vote_thumbs_up)
        thumbsDownImage = findViewById(R.id.vote_thumbs_down)

        voteMessageCounter.text = viewObject.voteCount.toString()

        if (viewObject.voteCount > 0) {
            voteMessageCounter.setTextColor(Color.GREEN)
        } else if (viewObject.voteCount < 0) {
            voteMessageCounter.setTextColor(Color.RED)
        } else {
            voteMessageCounter.setTextColor(Color.BLACK)
        }

        voteMessageCounter.alpha = 0.5f

        when (viewObject.voteType.name) {
            VoteType.up.name -> setupThumbsUpVoted()
            VoteType.down.name -> setupThumbsDownVoted()
            else -> setupNoneVoted()
        }
    }

    private fun setupThumbsUpVoted() {
        thumbsUpImage.setImageResource(R.drawable.thumb_up)
        thumbsUpImage.setColorFilter(Color.DKGRAY)

        thumbsDownImage.clearColorFilter()
        thumbsDownImage.setImageResource(R.drawable.non_chosen_thumb_down)

        refreshDrawableState()
    }

    private fun setupThumbsDownVoted() {
        thumbsDownImage.setImageResource(R.drawable.thumb_down)
        thumbsDownImage.setColorFilter(Color.DKGRAY)

        thumbsUpImage.clearColorFilter()
        thumbsUpImage.setImageResource(R.drawable.non_chosen_thumb_up)

        refreshDrawableState()
    }

    private fun setupNoneVoted() {
        thumbsUpImage.clearColorFilter()
        thumbsUpImage.setImageResource(R.drawable.non_chosen_thumb_up)

        thumbsUpImage.clearColorFilter()
        thumbsDownImage.setImageResource(R.drawable.non_chosen_thumb_down)

        refreshDrawableState()
    }

    override fun update(message: Message) {
        acceptModel(message)
    }

    override fun authRequired() {
        authRequired.onNext(Unit)
    }

    override fun errorOccurred() {
        errorHappened.onNext(Unit)
    }

}