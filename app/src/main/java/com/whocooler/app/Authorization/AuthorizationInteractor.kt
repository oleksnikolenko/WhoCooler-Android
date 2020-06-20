package com.whocooler.app.Authorization

import com.whocooler.app.Common.App.App
import io.reactivex.rxjava3.kotlin.subscribeBy

class AuthorizationInteractor: AuthorizationContracts.ViewInteractorContract {

    var presenter: AuthorizationContracts.InteractorPresenterContract? = null
    var worker = AuthorizationWorker()

    override fun authorize(token: String) {
        worker.authorize(token).subscribeBy(
            onNext = {userSession ->
                presenter?.didAuthenticate()
                App.prefs.userSession = userSession
            }, onError = {
                presenter?.presentError()
            }
        )
    }

}