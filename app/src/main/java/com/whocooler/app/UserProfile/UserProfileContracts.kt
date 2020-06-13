package com.whocooler.app.UserProfile

import com.whocooler.app.Common.Models.User

class UserProfileContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun getProfile()
        fun logout()
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun presentProfile(user: User)
        fun navigateToAuth()
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun displayProfile(user: User)
        fun navigateToAuth()
    }

    // Router

    interface RouterInterface {
        fun navigateToAuth()
    }

}