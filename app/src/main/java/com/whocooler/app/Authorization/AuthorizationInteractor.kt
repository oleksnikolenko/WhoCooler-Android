package com.whocooler.app.Authorization

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Services.AnalyticsEvent
import com.whocooler.app.Common.Services.AnalyticsService
import io.reactivex.rxjava3.kotlin.subscribeBy

class AuthorizationInteractor: AuthorizationContracts.ViewInteractorContract {

    var presenter: AuthorizationContracts.InteractorPresenterContract? = null
    var worker = AuthorizationWorker()

    override fun authorize(token: String) {
        worker.authorize(token).subscribeBy(
            onNext = {userSession ->
                AnalyticsService.trackEvent(AnalyticsEvent.LOGIN_SUCCESS)
                presenter?.didAuthenticate()
                App.prefs.userSession = userSession
            }, onError = {
                presenter?.presentError(it.localizedMessage)
            }
        )
    }

}