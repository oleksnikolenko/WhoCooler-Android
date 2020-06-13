package com.whocooler.app.Authorization

class AuthorizationPresenter: AuthorizationContracts.InteractorPresenterContract {

    var output: AuthorizationContracts.PresenterViewContract? = null

    override fun didAuthenticate() {
        output?.dismissActivity()
    }
}