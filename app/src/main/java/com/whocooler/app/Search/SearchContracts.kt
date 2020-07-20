package com.whocooler.app.Search

import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.SearchResponse
import com.whocooler.app.DebateList.Adapters.SearchAdapter
import io.reactivex.rxjava3.subjects.PublishSubject

class SearchContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun search(context: String, page: Int)
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun presentDebates(response: SearchResponse)
        fun presentError(errorDescription: String)
    }

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun setupDebateAdapter(response: SearchResponse, rows: ArrayList<SearchAdapter.ISearchListRow>)
        fun showErrorToast(message: String)
        fun setupNotFound()
    }

    // Router

    interface RouterInterface {
        fun navigateToAuth()
        fun navigateToDebate(debate: Debate, position: Int)
    }

}