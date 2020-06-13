package com.whocooler.app.UserProfile

import com.whocooler.app.Common.Models.User

class UserProfilePresenter: UserProfileContracts.InteractorPresenterContract {

    var output: UserProfileContracts.PresenterViewContract? = null

    override fun presentProfile(user: User) {
        output?.displayProfile(user)
    }

    override fun navigateToAuth() {
        output?.navigateToAuth()
    }
}