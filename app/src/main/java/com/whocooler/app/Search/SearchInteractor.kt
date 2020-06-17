package com.whocooler.app.Search

import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Services.DebateService
import com.whocooler.app.DebateList.DebateListWorker
import io.reactivex.rxjava3.subjects.PublishSubject

class SearchInteractor : SearchContracts.ViewInteractorContract {

    var presenter: SearchContracts.InteractorPresenterContract? = null
    private var worker = SearchWorker()
    private var debateWorker = DebateListWorker()

    override fun search(context: String, page: Int) {
        worker.search(context, page).subscribe { searchResponse ->
            presenter?.presentDebates(searchResponse)
        }
    }

    override fun vote(debateId: String, sideId: String, position: Int) : PublishSubject<Debate> {
        val responseSubject = PublishSubject.create<Debate>()
        debateWorker.vote(debateId, sideId).subscribe {response ->
            responseSubject.onNext(response.debate)
            DebateService.updateDebate(response.debate)
        }
        return responseSubject
    }

}