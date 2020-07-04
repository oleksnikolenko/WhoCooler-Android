package com.whocooler.app.VoteMessage

import com.whocooler.app.Common.Helpers.VoteType
import com.whocooler.app.Common.Models.Message

class VoteMessageContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun postVote(model: Message, voteType: VoteType, isReply: Boolean)
        fun deleteVote(model: Message, isReply: Boolean)
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun didVote(model: Message)
        fun authRequired()
        fun presentError()
    }

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun update(message: Message)
        fun authRequired()
        fun errorOccurred()
    }

    // Router

    interface RouterInterface {

    }
}