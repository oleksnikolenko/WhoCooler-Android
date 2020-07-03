package com.whocooler.app.UserProfile

import com.whocooler.app.Common.Models.User
import com.whocooler.app.Common.Utilities.UNEXPECTED_ERROR

class UserProfilePresenter: UserProfileContracts.InteractorPresenterContract {

    var activity: UserProfileContracts.PresenterViewContract? = null

    override fun presentProfile(user: User) {
        activity?.displayProfile(user)
    }

    override fun navigateToAuth() {
        activity?.navigateToDebateList()
    }

    override fun presentError(errorDescription: String) {
        activity?.showErrorToast(UNEXPECTED_ERROR + errorDescription)
    }
}