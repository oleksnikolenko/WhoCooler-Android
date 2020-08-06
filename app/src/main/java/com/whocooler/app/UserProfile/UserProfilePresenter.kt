package com.whocooler.app.UserProfile

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.User
import com.whocooler.app.R

class UserProfilePresenter: UserProfileContracts.InteractorPresenterContract {

    var activity: UserProfileContracts.PresenterViewContract? = null

    override fun presentProfile(user: User) {
        activity?.displayProfile(user)
    }

    override fun navigateToAuth() {
        activity?.navigateToDebateList()
    }

    override fun presentError(errorDescription: String?) {
        activity?.showErrorToast(App.appContext.getString(R.string.error_unexpected) + errorDescription)
    }
}