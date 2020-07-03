package com.whocooler.app.UserProfile

import com.whocooler.app.Common.Models.User
import java.io.ByteArrayOutputStream

class UserProfileContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun getProfile()
        fun logout()
        fun updateUserName(newName: String)
        fun updateUserAvatar(image: ByteArray)
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun presentProfile(user: User)
        fun navigateToAuth()
        fun presentError(errorDescription: String)
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun displayProfile(user: User)
        fun navigateToDebateList()
        fun showErrorToast(message: String)
    }

    // Router

    interface RouterInterface {
        fun navigateToAuth()
        fun navigateToDebateList()
    }

}