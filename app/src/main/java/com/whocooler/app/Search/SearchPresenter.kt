package com.whocooler.app.Search

import com.whocooler.app.Common.Models.SearchResponse

class SearchPresenter : SearchContracts.InteractorPresenterContract {

    var activity: SearchContracts.PresenterViewContract? = null

    override fun presentDebates(response: SearchResponse) {
        activity?.setupDebateAdapter(response)
    }

}