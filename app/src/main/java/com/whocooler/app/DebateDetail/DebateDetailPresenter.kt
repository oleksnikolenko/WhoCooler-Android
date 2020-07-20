package com.whocooler.app.DebateDetail

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.Message
import com.whocooler.app.Common.Models.MessagesList
import com.whocooler.app.R

class DebateDetailPresenter: DebateDetailContracts.InteractorPresenterContract {

    var activity: DebateDetailContracts.PresenterViewContract? = null

    override fun presentDebate(debate: Debate) {
        val rows = ArrayList<DebateDetailAdapter.IDetailRow>()
        if (debate.type == "sides") {
            rows.add(DebateDetailAdapter.HeaderSidesRow(debate))
        } else if (debate.type == "statement") {
            rows.add(DebateDetailAdapter.HeaderStatementRow(debate))
        }
        rows.add(DebateDetailAdapter.MessageHeaderRow(debate.messageCount))

        if (debate.messagesList.messages.isEmpty()) {
            rows.add(DebateDetailAdapter.EmptyMessagesRow())
        } else {
            debate.messagesList.messages.forEach { message ->
                rows.add(DebateDetailAdapter.MessageRow(message))
            }
        }

        activity?.displayDebate(rows)
    }

    override fun presentNewMessage(message: Message) {
        activity?.addNewMessage(DebateDetailAdapter.MessageRow(message))
        activity?.resetEditText()
        activity?.updateMessageCounter(1)
    }

    override fun presentAuthScreen() {
        activity?.navigateToAuth()
    }

    override fun updateDebate(debate: Debate) {
        activity?.updateDebate(debate)
    }

    override fun presentNewRepliesBatch(message: Message, index: Int) {
        activity?.addNewRepliesBatch(message, index)
    }

    override fun presentNewReply(threadMessage: Message, index: Int) {
        activity?.addNewReply(threadMessage, index)
        activity?.resetEditText()
        activity?.updateMessageCounter(1)
    }

    override fun presentNewMessagesBatch(messagesList: MessagesList) {
        val rows = ArrayList<DebateDetailAdapter.IDetailRow>()

        messagesList.messages.forEach {message->
            rows.add(DebateDetailAdapter.MessageRow(message))
        }

        activity?.addNewMessagesBatch(rows)
    }

    override fun presentError(errorDescription: String) {
        activity?.showErrorToast(App.appContext.getString(R.string.error_unexpected) + errorDescription)
    }

}