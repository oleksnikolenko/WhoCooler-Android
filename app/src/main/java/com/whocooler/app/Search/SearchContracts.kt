package com.whocooler.app.Search

import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.SearchResponse
import io.reactivex.rxjava3.subjects.PublishSubject

class SearchContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun search(context: String, page: Int)
        fun vote(debateId: String, sideId: String, position: Int) : PublishSubject<Debate>
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun presentDebates(response: SearchResponse)
    }

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun setupDebateAdapter(response: SearchResponse)
    }

    // Router

    interface RouterInterface {
        fun navigateToAuth()
        fun navigateToDebate(debate: Debate, position: Int)
    }

}