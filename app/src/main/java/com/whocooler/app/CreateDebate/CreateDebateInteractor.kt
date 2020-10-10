package com.whocooler.app.CreateDebate

import com.whocooler.app.Common.Services.AnalyticsEvent
import com.whocooler.app.Common.Services.AnalyticsService
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.File

class CreateDebateInteractor : CreateDebateContracts.ViewInteractorContract {

    var presenter: CreateDebateContracts.InteractorPresenterContract? = null
    var worker = CreateDebateWorker()

    override fun createDebateSides(
        leftName: String,
        rightName: String,
        leftImage: ByteArray,
        rightImage: ByteArray,
        categoryId: String,
        debateName: String?
    ) {
        worker.createDebateSides(leftName, rightName, leftImage, rightImage, categoryId, debateName).subscribeBy(
            onNext = { debate ->
                AnalyticsService.trackEvent(AnalyticsEvent.NEW_CREATED, "id", debate.id)
                presenter?.navigateToDebate(debate)
            }, onError = {
                presenter?.presentError(it.localizedMessage)
            }
        )
    }

    override fun createDebateStatement(
        leftName: String,
        rightName: String,
        debateImage: ByteArray,
        categoryId: String,
        debateName: String
    ) {
        worker.createDebateStatement(leftName, rightName, debateImage, categoryId, debateName).subscribeBy(
            onNext = { debate ->
                AnalyticsService.trackEvent(AnalyticsEvent.NEW_CREATED, "id", debate.id)
                presenter?.navigateToDebate(debate)
            }, onError = {
                presenter?.presentError(it.localizedMessage)
            }
        )
    }
}