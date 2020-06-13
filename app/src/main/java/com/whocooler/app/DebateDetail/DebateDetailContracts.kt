package com.whocooler.app.DebateDetail

import com.whocooler.app.Common.Models.Debate

class DebateDetailContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun initDebate(debate: Debate)
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
    }

    // Router

    interface RouterInterface {
    }

}