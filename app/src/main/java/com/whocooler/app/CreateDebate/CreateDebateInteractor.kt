package com.whocooler.app.CreateDebate

import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.File

class CreateDebateInteractor : CreateDebateContracts.ViewInteractorContract {

    var presenter: CreateDebateContracts.InteractorPresenterContract? = null
    var worker = CreateDebateWorker()

    override fun getCategoryList() {
        worker.getCategories().subscribeBy(
            onNext = { response ->
                presenter?.didFetchCategories(response.categories)
            }, onError = {
                presenter?.presentError()
            }
        )
    }

    override fun createDebate(
        leftName: String,
        rightName: String,
        leftImage: File,
        rightImage: File,
        categoryId: String
    ) {
        worker.createDebate(leftName, rightName, leftImage, rightImage, categoryId).subscribeBy(
            onNext = { debate ->
                presenter?.navigateToDebate(debate)
            }, onError = {
                presenter?.presentError()
            }
        )
    }
}