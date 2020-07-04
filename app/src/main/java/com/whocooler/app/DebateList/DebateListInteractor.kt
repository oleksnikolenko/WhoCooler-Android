package com.whocooler.app.DebateList

import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.NetworkError
import com.android.volley.NoConnectionError
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebatesResponse
import com.whocooler.app.Common.Services.DebateService
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class DebateListInteractor : DebateListContracts.ViewInteractorContract {

    var presenter: DebateListContracts.InteractorPresenterContract? = null
    val worker = DebateListWorker()

    private var page = 1
    private var categoryId: String = Category.Constant.ALL.id
    private var selectedSorting: String = "popular"
    private var response: DebatesResponse? = null

    override fun getDebates(request: DebateListModels.DebateListRequest) {
        categoryId = request.categoryId
        selectedSorting = request.selectedSorting
        worker.getDebates(1, categoryId, selectedSorting).subscribeBy(
            onNext = {response ->
                this.response = response
                presenter?.presentDebates(response, shouldReloadCategories = request.shouldReloadCategories)
                page = 1
            }, onError = {
                handleError(it)
            }
        )
    }

    override fun getNextPage() {
        if (response?.hasNextPage == false) {
            return
        }
        worker.getDebates(page + 1, categoryId, selectedSorting).subscribeBy(
            onNext = { response->
                this.response?.debates?.addAll(response.debates)
                this.response?.hasNextPage = response.hasNextPage
                page += 1

                presenter?.addNewDebates(response)
            }, onError = {
                handleError(it)
            }
        )
    }

    override fun vote(debateId: String, sideId: String, position: Int) : PublishSubject<Debate> {
        val responseSubject = PublishSubject.create<Debate>()
        worker.vote(debateId, sideId).subscribeBy(
            onNext = { response ->
                responseSubject.onNext(response.debate)
                DebateService.debates.set(position, response.debate)
            }, onError = {
                handleError(it)
            }
        )
        return responseSubject
    }

    override fun toggleFavorites(debate: Debate) {
        if (App.prefs.isTokenEmpty()) {
            presenter?.presentAuthScreen()
        } else if (!debate.isFavorite) {
            addToFavorites(debate)
        } else {
            deleteFromFavorites(debate)
        }
    }

    private fun addToFavorites(debate: Debate) {
        worker.addToFavorites(debate).subscribeBy(
            onNext = {
                DebateService.toggleFavorite(debate)
                presenter?.updateDebateDataSource()
            }, onError = {
                handleError(it)
            }
        )
    }

    private fun deleteFromFavorites(debate: Debate) {
        worker.deleteFromFavorites(debate).subscribeBy(
            onNext = {
                DebateService.toggleFavorite(debate)
                presenter?.updateDebateDataSource()
            }, onError = {
                handleError(it)
            }
        )
    }

    override fun hasDebatesListNextPage(): Boolean {
        return response?.hasNextPage ?: false
    }

    private fun handleError(error: Throwable) {
        if (error is NetworkError) {
            presenter?.presentNoInternet()
        } else if (error is NoConnectionError) {
            Toast.makeText(App.appContext, error.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

}