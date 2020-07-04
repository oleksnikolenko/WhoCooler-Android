package com.whocooler.app.VoteMessage

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Helpers.VoteType
import com.whocooler.app.Common.Models.Message
import io.reactivex.rxjava3.kotlin.subscribeBy

class VoteMessageInteractor: VoteMessageContracts.ViewInteractorContract {

    private var worker = VoteMessageWorker()
    var presenter: VoteMessageContracts.InteractorPresenterContract? = null

    override fun postVote(model: Message, voteType: VoteType, isReply: Boolean) {
        if (App.prefs.isTokenEmpty()) {
            presenter?.authRequired()
            return
        }
        worker.postVote(model.id, voteType, isReply).subscribeBy(
            onNext = { messageVoteModel ->
                model.didVote(messageVoteModel, voteType)
                presenter?.didVote(model)
            }, onError = {
                presenter?.presentError()
            }
        )
    }

    override fun deleteVote(model: Message, isReply: Boolean) {
        if (App.prefs.isTokenEmpty()) {
            presenter?.authRequired()
            return
        }
        worker.deleteVote(model.id, isReply).subscribeBy(
            onNext = { messageVoteModel ->
                model.didVote(messageVoteModel, VoteType.none)
                presenter?.didVote(model)
            }, onError = {
                presenter?.presentError()
            }
        )
    }
}