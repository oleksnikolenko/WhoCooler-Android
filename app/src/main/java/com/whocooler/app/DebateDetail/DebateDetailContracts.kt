package com.whocooler.app.DebateDetail

import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.Message
import com.whocooler.app.Common.Models.MessagesList
import io.reactivex.rxjava3.subjects.PublishSubject

class DebateDetailContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun initDebate(debate: Debate, shouldUpdateWithInternet: Boolean)
        fun handleSend(text: String, threadId: String? = null, editedMessage: Message? = null, index: Int?=null)
        fun vote(sideId: String) : PublishSubject<Debate>
        fun getNextRepliesPage(parentMessage: Message, index: Int)
        fun getNextMessagesPage()
        fun hasMessageListNextPage(): Boolean
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun presentDebate(debate: Debate)
        fun presentAuthScreen()
        fun presentNewMessage(message: Message)
        fun presentNewReply(threadMessage: Message, index: Int)
        fun updateDebate(debate: Debate)
        fun presentNewRepliesBatch(message: Message, index: Int)
        fun presentNewMessagesBatch(messagesList: MessagesList)
        fun presentError(errorDescription: String)
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun displayDebate(rows: ArrayList<DebateDetailAdapter.IDetailRow>)
        fun navigateToAuth()
        fun addNewMessage(row: DebateDetailAdapter.IDetailRow)
        fun addNewReply(reply: Message, index: Int)
        fun resetEditText()
        fun updateMessageCounter(value: Int)
        fun updateDebate(debate: Debate)
        fun addNewRepliesBatch(message: Message, index: Int)
        fun addNewMessagesBatch(rows: ArrayList<DebateDetailAdapter.IDetailRow>)
        fun showErrorToast(message: String)
    }

    // Router

    interface RouterInterface {
        fun navigateToAuth()
    }

}