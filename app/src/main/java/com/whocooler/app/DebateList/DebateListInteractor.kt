package com.whocooler.app.DebateList

import android.util.Log
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.DebatesResponse
import io.reactivex.rxjava3.kotlin.subscribeBy

class DebateListInteractor : DebateListContracts.ViewInteractorContract {

    var output: DebateListContracts.InteractorPresenterContract? = null
    val worker = DebateListWorker()

    private var page = 1
    private var categoryId: String = Category.Constant.ALL.id
    private var selectedSorting: String = "popular"
    private var response: DebatesResponse? = null

    override fun getDebates(request: DebateListModels.DebateListRequest) {
        categoryId = request.categoryId
        selectedSorting = request.selectedSorting
        Log.d("CATEGORY_ID", categoryId)
        worker.getDebates(1, categoryId, selectedSorting).subscribeBy(
            onNext = {response ->
                this.response = response
                output?.presentDebates(response, shouldReloadCategories = request.shouldReloadCategories)
            }, onError = {
                Log.d("ERROR", it.localizedMessage)
            }
        )
    }

    override fun vote(debateId: String, sideId: String, position: Int) {
        worker.vote(debateId, sideId).subscribe {response ->
        }
    }



}