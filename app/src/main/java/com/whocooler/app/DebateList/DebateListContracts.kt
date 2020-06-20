package com.whocooler.app.DebateList

import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebatesResponse
import io.reactivex.rxjava3.subjects.PublishSubject

class DebateListContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun getDebates(request: DebateListModels.DebateListRequest)
        fun vote(debateId: String, sideId: String, position: Int) : PublishSubject<Debate>
        fun toggleFavorites(debate: Debate)
        fun hasDebatesListNextPage(): Boolean
        fun getNextPage()
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun presentDebates(response: DebatesResponse, shouldReloadCategories: Boolean)
        fun updateDebateDataSource()
        fun addNewDebates(response: DebatesResponse)
        fun presentError()
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun setupDebateAdapter(response: DebatesResponse)
        fun setupCategoryAdapter(categories: ArrayList<Category>)
        fun updateDebateDataSource()
        fun setupEmptyState(text: String)
        fun addNewDebates(response: DebatesResponse)
        fun showErrorToast(message: String)

        var selectedCategoryId: String
    }

    // Router

    interface RouterInterface {
        fun navigateToDebate(debate: Debate, position: Int)
        fun navigateToAuthorization()
        fun navigateToUserProfile()
        fun navigateToSearch()
    }

}