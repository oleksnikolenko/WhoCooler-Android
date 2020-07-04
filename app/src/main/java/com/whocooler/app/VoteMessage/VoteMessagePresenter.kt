package com.whocooler.app.VoteMessage

import com.whocooler.app.Common.Models.Message

class VoteMessagePresenter: VoteMessageContracts.InteractorPresenterContract {

    var view: VoteMessageContracts.PresenterViewContract? = null

    override fun didVote(model: Message) {
        view?.update(model)
    }

    override fun authRequired() {
        view?.authRequired()
    }

    override fun presentError() {
        view?.errorOccurred()
    }

}