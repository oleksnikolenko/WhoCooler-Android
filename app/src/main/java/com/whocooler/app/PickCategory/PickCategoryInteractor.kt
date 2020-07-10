package com.whocooler.app.PickCategory

import android.widget.Toast
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Category
import io.reactivex.rxjava3.kotlin.subscribeBy

class PickCategoryInteractor: PickCategoryContracts.ViewInteractorContract {

    var presenter: PickCategoryContracts.InteractorPresenterContract? = null
    val worker = PickCategoryWorker()

    override fun requestCategoryList() {
        worker.getCategories().subscribeBy(
            onNext = { categoriesResponse ->
                presenter?.didFetchCategories(categoriesResponse)
            }, onError = {
                handleError(it)
            }
        )
    }

    override fun searchCategory(searchContex: String) {
        worker?.search(searchContex).subscribeBy(
            onNext = { categoriesResponse ->
                presenter?.didFetchCategories(categoriesResponse)
            }, onError = {
                handleError(it)
            }
        )
    }

    override fun createCategory(name: String) {
        worker?.createCategory(name).subscribeBy(
            onNext = {
                presenter?.didCreateCategory(it)
            }, onError = {
                handleError(it)
            }
        )
    }

    private fun handleError(error: Throwable) {
        Toast.makeText(App.appContext, error.localizedMessage, Toast.LENGTH_LONG).show()
    }

}