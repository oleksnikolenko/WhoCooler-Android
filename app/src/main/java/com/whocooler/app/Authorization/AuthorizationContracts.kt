package com.whocooler.app.Authorization

class AuthorizationContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun authorize(token: String)
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun didAuthenticate()
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun dismissActivity()
    }

    // Router

    interface RouterInterface {
    }

}