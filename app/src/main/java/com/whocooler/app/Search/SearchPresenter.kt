package com.whocooler.app.Search

import com.whocooler.app.Common.Models.SearchResponse
import com.whocooler.app.Common.Utilities.UNEXPECTED_ERROR

class SearchPresenter : SearchContracts.InteractorPresenterContract {

    var activity: SearchContracts.PresenterViewContract? = null

    override fun presentDebates(response: SearchResponse) {
        activity?.setupDebateAdapter(response)
    }

    override fun presentError(errorDescription: String) {
        activity?.showErrorToast(UNEXPECTED_ERROR + errorDescription)
    }

}