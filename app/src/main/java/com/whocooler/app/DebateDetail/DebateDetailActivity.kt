package com.whocooler.app.DebateDetail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebateSide
import com.whocooler.app.Common.Models.Message
import com.whocooler.app.Common.Services.DebateService
import com.whocooler.app.Common.Utilities.EXTRA_DEBATE
import com.whocooler.app.Common.Utilities.EXTRA_DEBATE_POSITION
import com.whocooler.app.R
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_debate_detail.*

class DebateDetailActivity: AppCompatActivity(), DebateDetailContracts.PresenterViewContract {

    lateinit var interactor: DebateDetailContracts.ViewInteractorContract
    lateinit var router: DebateDetailContracts.RouterInterface
    lateinit var debateDetailAdapter: DebateDetailAdapter

    var linearLayout = LinearLayoutManager(this)
    private lateinit var scrollListener: RecyclerView.OnScrollListener

    private var inputParentMessageId: String? = null
    private var inputParentIndex: Int? = null

    lateinit var debate: Debate
    var debatePosition: Int = -1

    var rows = ArrayList<DebateDetailAdapter.IDetailRow>()

    private val lastVisibleItemPosition: Int
        get() = linearLayout.findLastVisibleItemPosition()

    private fun setup() {
        var activity = this
        var interactor = DebateDetailInteractor()
        var presenter = DebateDetailPresenter()
        var router = DebateDetailRouter()

        activity.interactor = interactor
        activity.router = router
        interactor.presenter = presenter
        presenter.activity = activity
        router.activity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
        setContentView(R.layout.activity_debate_detail)

        debate = intent.getParcelableExtra<Debate>(EXTRA_DEBATE)
        debatePosition = intent.getIntExtra(EXTRA_DEBATE_POSITION, debatePosition)
        interactor.initDebate(debate)
        setupSendMessageOnClickListener()
        handlePagination()
        toggleProgressBar(false)
    }

    private fun setupSendMessageOnClickListener() {
        val sendMessageButton = findViewById<ImageButton>(R.id.detail_send_button)
        val editText = findViewById<EditText>(R.id.detail_edit_text)

        sendMessageButton.setOnClickListener {
            if (editText.text.toString().isNotEmpty()) {
                interactor.handleSend(editText.text.toString(), inputParentMessageId, null, inputParentIndex)
            }
        }
    }

    override fun displayDebate(rows: ArrayList<DebateDetailAdapter.IDetailRow>) {
        var authRequiredHandler: () -> Unit = {
            router?.navigateToAuth()
        }
        var voteClickHandler: (DebateSide) -> PublishSubject<Debate> = { debateSide ->
            interactor.vote(debateSide.id)
        }
        var getNextRepliesHandler: (message: Message, index: Int) -> Unit = { message, index ->
            // Index - 2 because messages start counting after main header + message header views
            interactor.getNextRepliesPage(message, index-2)
        }
        var didClickReply: (parentMessageId: String, index: Int) -> Unit = {messageId, index->
            inputParentMessageId = messageId
            inputParentIndex = index

            detail_edit_text.requestFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(detail_edit_text, InputMethodManager.SHOW_IMPLICIT)
        }

        debateDetailAdapter = DebateDetailAdapter(this,
            rows,
            voteClickHandler,
            authRequiredHandler,
            getNextRepliesHandler,
            didClickReply
        )

        detail_recycler_view.adapter = debateDetailAdapter
        detail_recycler_view.layoutManager = linearLayout//LinearLayoutManager(this)

        this.rows = rows
    }

    override fun resetEditText() {
        detail_edit_text.text.clear()

        this.currentFocus?.let { v->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
        }

        inputParentIndex = null
        inputParentMessageId = null
    }

    override fun navigateToAuth() {
        router?.navigateToAuth()
    }

    override fun addNewMessage(row: DebateDetailAdapter.IDetailRow) {
        this.rows.add(2, row)
        debateDetailAdapter.update(this.rows)
    }

    override fun updateMessageCounter(value: Int) {
        var headerRow = rows.get(1) as? DebateDetailAdapter.MessageHeaderRow
        headerRow?.messageCount = headerRow?.messageCount?.plus(value)!!
        debateDetailAdapter.update(this.rows)

        debate.messageCount += value
        DebateService.debates.set(debatePosition, debate)
    }

    override fun updateDebate(debate: Debate) {
        this.debate = debate
        DebateService.debates.set(debatePosition, debate)
    }

    override fun addNewRepliesBatch(message: Message, index: Int) {
        val replyRows = ArrayList<DebateDetailAdapter.IDetailRow>()

        message.replyList.forEach { message->
            replyRows.add(DebateDetailAdapter.ReplyRow(message))
        }
        this.rows.addAll(index + 3, replyRows)
        debateDetailAdapter.update(this.rows)
    }

    override fun addNewMessagesBatch(rows: ArrayList<DebateDetailAdapter.IDetailRow>) {
        this.rows.addAll(rows)
        debateDetailAdapter.update(this.rows)
        handlePagination()
        toggleProgressBar(false)
    }

    override fun addNewReply(reply: Message, index: Int) {
        val replyRow = DebateDetailAdapter.ReplyRow(reply)
        rows.add(index + 3, replyRow)
        debateDetailAdapter.update(rows)
    }

    private fun handlePagination() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = linearLayout.itemCount
                if (totalItemCount == lastVisibleItemPosition + 1 && interactor.hasMessageListNextPage()) {
                    interactor.getNextMessagesPage()
                    recyclerView.removeOnScrollListener(scrollListener)
                    toggleProgressBar(true)
                }
            }
        }
        detail_recycler_view.addOnScrollListener(scrollListener)
    }

    private fun toggleProgressBar(isVisible: Boolean) {
        val progressBar = findViewById<ProgressBar>(R.id.detail_progress_bar_bottom)

        progressBar.isVisible = isVisible
    }


}