package com.example.whocooler.DebateList

import com.example.whocooler.Common.Models.DebatesResponse

class DebateListInteractor : DebateListContracts.ViewInteractorContract {

    var output: DebateListContracts.InteractorPresenterContract? = null
    val worker = DebateListWorker()

    private var page = 1
    private var categoryId: String? = null
    private var selectedSorting: String = "popular"
    private var response: DebatesResponse? = null

    override fun getDebates(request: DebateListModels.DebateListRequest) {
        worker.getDebates(1, categoryId, selectedSorting).subscribe { response ->
            println("?!?!Subscription")
            this.response = response
            output?.presentDebates(response)
        }
    }

    override fun vote(debateId: String, sideId: String) {
        worker.vote(debateId, sideId).subscribe {response ->
            println("?!?!Subscription in interactor, $response")
        }
    }



}