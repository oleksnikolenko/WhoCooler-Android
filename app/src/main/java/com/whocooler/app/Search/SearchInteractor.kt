package com.whocooler.app.Search

import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Services.DebateService
import com.whocooler.app.DebateList.DebateListWorker
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

class SearchInteractor : SearchContracts.ViewInteractorContract {

    var presenter: SearchContracts.InteractorPresenterContract? = null
    private var worker = SearchWorker()

    override fun search(context: String, page: Int) {
        worker.search(context, page).subscribe { searchResponse ->
            presenter?.presentDebates(searchResponse)
        }
    }

}