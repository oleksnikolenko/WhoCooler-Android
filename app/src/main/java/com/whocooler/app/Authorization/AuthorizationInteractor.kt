package com.whocooler.app.Authorization

import android.util.Log
import com.whocooler.app.Common.App.App
import io.reactivex.rxjava3.kotlin.subscribeBy

class AuthorizationInteractor: AuthorizationContracts.ViewInteractorContract {

    var output: AuthorizationContracts.InteractorPresenterContract? = null
    var worker = AuthorizationWorker()

    override fun authorize(token: String) {
        worker.authorize(token).subscribeBy(
            onNext = {userSession ->
                output?.didAuthenticate()
                App.prefs.userSession = userSession
            }, onError = {
                Log.d("?!?!ERROR", it.localizedMessage)
            }
        )
    }

}