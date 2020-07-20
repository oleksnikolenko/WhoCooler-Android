package com.whocooler.app.Search

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.SearchResponse
import com.whocooler.app.DebateList.Adapters.DebateListAdapter
import com.whocooler.app.DebateList.Adapters.SearchAdapter
import com.whocooler.app.R

class SearchPresenter : SearchContracts.InteractorPresenterContract {

    var activity: SearchContracts.PresenterViewContract? = null

    override fun presentDebates(response: SearchResponse) {
        if (response.debates.isNotEmpty()) {
            var rows = ArrayList<SearchAdapter.ISearchListRow>()
            response.debates.forEach { debate ->
                if (debate.type == "sides") {
                    rows.add(SearchAdapter.SidesRow(debate))
                } else if (debate.type == "statement") {
                    rows.add(SearchAdapter.StatementRow(debate))
                }
            }
            activity?.setupDebateAdapter(response, rows)
        } else {
            activity?.setupNotFound()
        }
    }

    override fun presentError(errorDescription: String) {
        activity?.showErrorToast(App.appContext.getString(R.string.error_unexpected) + errorDescription)
    }

}