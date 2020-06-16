package com.whocooler.app.DebateDetail

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.Message
import com.whocooler.app.Common.Services.DebateService
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class DebateDetailInteractor: DebateDetailContracts.ViewInteractorContract {

    private val oneReplyBatchCount = 5
    var presenter: DebateDetailContracts.InteractorPresenterContract? = null
    val worker = DebateDetailWorker()
    lateinit var debate: Debate

    override fun initDebate(debate: Debate) {
        this.debate = debate

        presenter?.presentDebate(debate)
    }

    override fun handleSend(text: String, threadId: String?, editedMessage: Message?, index: Int?) {
        if (App.prefs.isTokenEmpty()) {
            presenter?.presentAuthScreen()
            return
        } else if (threadId != null && index != null) {
            sendReply(text, threadId, index)
        } else {
            sendMessage(text)
        }
    }

    private fun sendMessage(text: String) {
        worker.sendMessage(text, debate.id).subscribe {message->
            debate.messagesList.messages.add(0, message)
            presenter?.presentNewMessage(message)
        }
    }

    private fun sendReply(text: String, threadId: String, index: Int) {
        worker.sendReply(text, threadId).subscribeBy(
            onNext = {reply->
                val parentIndex = getIndexOfMessage(threadId)
                if (parentIndex != null) {
                    presenter?.presentNewReply(reply, index)
                    debate.messagesList.messages[parentIndex].replyList.add(reply)
                    debate.messagesList.messages[parentIndex].replyCount += 1
                }
            }, onError = {
                // TODO: Handle error
            }
        )
    }

    override fun vote(sideId: String) : PublishSubject<Debate> {
        val responseSubject = PublishSubject.create<Debate>()
        worker.vote(debate.id, sideId).subscribe {response ->
            responseSubject.onNext(response.debate)
            presenter?.updateDebate(response.debate)
        }
        return responseSubject
    }

    override fun getNextRepliesPage(parentMessage: Message, index: Int) {
        worker.getNextReplies(
            parentMessage.id,
            parentMessage.replyList.firstOrNull()?.createdTime ?: 0
        ).subscribe {messagesList->
            debate.messagesList.messages[index].replyList.addAll(0, messagesList.messages)
            debate.messagesList.messages[index].notShownReplyCount -= oneReplyBatchCount

            presenter?.presentNewRepliesBatch(debate.messagesList.messages[index], index)
        }
    }

    private fun getIndexOfMessage(id: String) : Int? {
        debate.messagesList.messages.forEachIndexed { index, message ->
            if (message.id == id) {
                return index
            }
        }
        return null
    }

}