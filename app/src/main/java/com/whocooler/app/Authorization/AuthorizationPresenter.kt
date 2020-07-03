package com.whocooler.app.Authorization

import com.whocooler.app.Common.Utilities.UNEXPECTED_ERROR

class AuthorizationPresenter: AuthorizationContracts.InteractorPresenterContract {

    var activity: AuthorizationContracts.PresenterViewContract? = null

    override fun didAuthenticate() {
        activity?.dismissActivity()
    }

    override fun presentError(errorDescription: String) {
        activity?.showErrorToast(UNEXPECTED_ERROR + errorDescription)
    }
}