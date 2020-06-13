package com.whocooler.app.DebateList

import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebatesResponse

class DebateListContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun getDebates(request: DebateListModels.DebateListRequest)
        fun vote(debateId: String, sideId: String, position: Int)
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun presentDebates(response: DebatesResponse, shouldReloadCategories: Boolean)
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun setupDebateAdapter(response: DebatesResponse)
        fun setupCategoryAdapter(categories: ArrayList<Category>)
    }

    // Router

    interface RouterInterface {
        fun navigateToDebate(debate: Debate)
        fun navigateToAuthorization()
        fun navigateToUserProfile()
    }

}