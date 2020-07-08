package com.whocooler.app.Search

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.SearchResponse
import com.whocooler.app.R

class SearchPresenter : SearchContracts.InteractorPresenterContract {

    var activity: SearchContracts.PresenterViewContract? = null

    override fun presentDebates(response: SearchResponse) {
        if (response.debates.isNotEmpty()) {
            activity?.setupDebateAdapter(response)
        } else {
            activity?.setupNotFound()
        }
    }

    override fun presentError(errorDescription: String) {
        activity?.showErrorToast(App.appContext.getString(R.string.error_unexpected) + errorDescription)
    }

}