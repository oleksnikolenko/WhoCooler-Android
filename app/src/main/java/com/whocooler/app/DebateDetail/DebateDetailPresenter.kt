package com.whocooler.app.DebateDetail

import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.Message

class DebateDetailPresenter: DebateDetailContracts.InteractorPresenterContract {

    var activity: DebateDetailContracts.PresenterViewContract? = null

    override fun presentDebate(debate: Debate) {
        val rows = ArrayList<DebateDetailAdapter.IRow>()
        rows.add(DebateDetailAdapter.HeaderRow(debate))
        rows.add(DebateDetailAdapter.MessageHeaderRow(debate.messageCount))

        debate.messagesList.messages.forEach { message ->
            rows.add(DebateDetailAdapter.MessageRow(message))
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
}