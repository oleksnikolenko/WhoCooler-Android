package com.example.whocooler.DebateList

import com.example.whocooler.Common.Models.Debate
import com.example.whocooler.Common.Models.DebatesResponse

class DebateListContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun getDebates(request: DebateListModels.DebateListRequest)
        fun vote(debateId: String, sideId: String, position: Int)
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun presentDebates(response: DebatesResponse)
        fun reloadDebate(debate: Debate, position: Int)
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun setupAdapter(response: DebatesResponse)
        fun reloadDebate(debate: Debate, position: Int)
    }

    // Router

    interface RouterInterface {
        fun navigateToDebate(debate: Debate)
    }

}