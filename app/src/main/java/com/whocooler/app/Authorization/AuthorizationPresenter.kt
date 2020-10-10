package com.whocooler.app.Authorization

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Services.AnalyticsService
import com.whocooler.app.R

class AuthorizationPresenter: AuthorizationContracts.InteractorPresenterContract {

    var activity: AuthorizationContracts.PresenterViewContract? = null

    override fun didAuthenticate() {
        activity?.dismissActivity()
    }

    override fun presentError(errorDescription: String) {
        activity?.showErrorToast(App.appContext.getString(R.string.error_unexpected) + errorDescription)
    }
}