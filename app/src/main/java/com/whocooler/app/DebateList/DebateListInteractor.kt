package com.whocooler.app.DebateList

import android.util.Log
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
                presenter?.presentError()
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
                presenter?.presentError()
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
                presenter?.presentError()
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
                presenter?.presentError()
            }
        )
    }

    private fun deleteFromFavorites(debate: Debate) {
        worker.deleteFromFavorites(debate).subscribeBy(
            onNext = {
                DebateService.toggleFavorite(debate)
                presenter?.updateDebateDataSource()
            }, onError = {
                presenter?.presentError()
            }
        )
    }

    override fun hasDebatesListNextPage(): Boolean {
        return response?.hasNextPage ?: false
    }
}