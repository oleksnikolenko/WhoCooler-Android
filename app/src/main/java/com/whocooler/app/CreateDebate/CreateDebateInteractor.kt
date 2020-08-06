package com.whocooler.app.CreateDebate

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
                presenter?.navigateToDebate(debate)
            }, onError = {
                presenter?.presentError(it.localizedMessage)
            }
        )
    }
}