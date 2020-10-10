package com.whocooler.app.DebateDetail

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.Message
import com.whocooler.app.Common.Services.AnalyticsEvent
import com.whocooler.app.Common.Services.AnalyticsService
import com.whocooler.app.Common.Services.DebateService
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class DebateDetailInteractor: DebateDetailContracts.ViewInteractorContract {

    private val oneReplyBatchCount = 5
    var presenter: DebateDetailContracts.InteractorPresenterContract? = null
    val worker = DebateDetailWorker()
    lateinit var debate: Debate

    override fun initDebate(debate: Debate, shouldUpdateWithInternet: Boolean) {
        this.debate = debate

        presenter?.presentDebate(debate)

        if (shouldUpdateWithInternet) {
            worker.getDebate(debate.id).subscribeBy(onNext = {
                DebateService.updateDebate(it)
                this.debate = it
                presenter?.presentDebate(it)
            }, onError = {
                presenter?.presentError(it.localizedMessage)
            })
        }
    }

    override fun hasMessageListNextPage() : Boolean {
        return debate.messagesList.hasNextPage
    }

    override fun handleSend(text: String, threadId: String?, editedMessage: Message?, index: Int?) {
        AnalyticsService.trackEvent(AnalyticsEvent.COMMENT_SEND_TRY)
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
        worker.sendMessage(text, debate.id).subscribeBy(
            onNext = {message->
                debate.messagesList.messages.add(0, message)
                presenter?.presentNewMessage(message)
                AnalyticsService.trackEvent(AnalyticsEvent.COMMENT_SENT_SUCCESS)
            }, onError = {
                presenter?.presentError(it.localizedMessage)
            }
        )
    }

    private fun sendReply(text: String, threadId: String, index: Int) {
        worker.sendReply(text, threadId).subscribeBy(
            onNext = {reply->
                AnalyticsService.trackEvent(AnalyticsEvent.COMMENT_SENT_SUCCESS)
                val parentIndex = getIndexOfMessage(threadId)
                if (parentIndex != null) {
                    presenter?.presentNewReply(reply, index)
                    debate.messagesList.messages[parentIndex].replyList.add(reply)
                    debate.messagesList.messages[parentIndex].replyCount += 1
                }
            }, onError = {
                presenter?.presentError(it.localizedMessage)
            }
        )
    }

    override fun vote(sideId: String) : PublishSubject<Debate> {
        val responseSubject = PublishSubject.create<Debate>()
        worker.vote(debate.id, sideId).subscribeBy(
            onNext = { response ->
                responseSubject.onNext(response.debate)
                presenter?.updateDebate(response.debate)
            }, onError = {
                presenter?.presentError(it.localizedMessage)
            }
        )
        return responseSubject
    }

    override fun getNextRepliesPage(parentMessage: Message, index: Int) {
        worker.getNextMessages(
            parentMessage.id,
            parentMessage.replyList.firstOrNull()?.createdTime ?: 0,
            true
        ).subscribeBy(
            onNext = { messagesList->
                var parentIndex = debate.messagesList.messages.indexOf(parentMessage)
                if (parentIndex != -1) {
                    debate.messagesList.messages[parentIndex].setNotShowReplyCount(oneReplyBatchCount)
                    debate.messagesList.messages[parentIndex].replyList.addAll(0, messagesList.messages)

                    presenter?.presentNewRepliesBatch(debate.messagesList.messages[parentIndex], index)
                }
            }, onError = {
                presenter?.presentError(it.localizedMessage)
            }
        )
    }

    override fun getNextMessagesPage() {
        if (debate.messagesList.hasNextPage == false) {
            return
        }
        val lastTime = debate?.messagesList.messages.last().createdTime
        if (lastTime != null) {
            worker.getNextMessages(debate.id, lastTime, false).subscribeBy(
                onNext = { messagesList ->
                    debate.messagesList.messages.addAll(messagesList.messages)
                    debate.messagesList.hasNextPage = messagesList.hasNextPage

                    presenter?.presentNewMessagesBatch(messagesList)
                }, onError = {
                    presenter?.presentError(it.localizedMessage)
                }
            )
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