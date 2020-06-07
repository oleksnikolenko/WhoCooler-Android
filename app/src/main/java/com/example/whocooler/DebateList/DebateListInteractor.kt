package com.example.whocooler.DebateList

import android.util.Log
import com.example.whocooler.Common.Models.DebatesResponse
import io.reactivex.rxjava3.kotlin.subscribeBy

class DebateListInteractor : DebateListContracts.ViewInteractorContract {

    var output: DebateListContracts.InteractorPresenterContract? = null
    val worker = DebateListWorker()

    private var page = 1
    private var categoryId: String? = null
    private var selectedSorting: String = "popular"
    private var response: DebatesResponse? = null

    override fun getDebates(request: DebateListModels.DebateListRequest) {
        worker.getDebates(1, categoryId, selectedSorting).subscribeBy(
            onNext = {response ->
                println("?!?!Subscription")
                this.response = response
                output?.presentDebates(response)
            }, onError = {
                Log.d("ERROR", it.localizedMessage)
            }
        )
    }

    override fun vote(debateId: String, sideId: String, position: Int) {
        worker.vote(debateId, sideId).subscribe {response ->
            println("?!?!Subscription in interactor, $response")
            output?.reloadDebate(response.debate, position)
        }
    }



}