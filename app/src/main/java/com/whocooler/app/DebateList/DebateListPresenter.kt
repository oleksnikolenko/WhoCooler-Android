package com.whocooler.app.DebateList

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebatesResponse
import com.whocooler.app.Common.Utilities.FEEDBACK_CONTACT_POSITION
import com.whocooler.app.Common.Utilities.FEEDBACK_INPUT_POSITION
import com.whocooler.app.DebateDetail.DebateDetailAdapter
import com.whocooler.app.DebateList.Adapters.DebateListAdapter
import com.whocooler.app.R

class DebateListPresenter : DebateListContracts.InteractorPresenterContract {

    var activity: DebateListContracts.PresenterViewContract? = null

    override fun presentDebates(response: DebatesResponse, shouldReloadCategories: Boolean) {
        var categories = ArrayList<Category> ()
        categories.addAll(response.categories)
        categories.add(0, Category.Constant.ALL)
        categories.add(1, Category.Constant.FAVORITES)

        if (shouldReloadCategories) {
            activity?.setupCategoryAdapter(categories)
        }

        if (response.debates.isEmpty() && activity?.selectedCategoryId == Category.Constant.FAVORITES.id) {
            activity?.setupEmptyState(App.appContext.getString(R.string.empty_favorites))
        } else {
            var rows = ArrayList<DebateListAdapter.IDebateListRow>()
            response.debates.forEach { debate ->
                if (debate.type == "sides") {
                    rows.add(DebateListAdapter.SidesRow(debate))
                } else if (debate.type == "statement") {
                    rows.add(DebateListAdapter.StatementRow(debate))
                }
            }
            if (App.prefs.shouldShowFeedbackInput && rows.count() > FEEDBACK_INPUT_POSITION) {
                rows.add(FEEDBACK_INPUT_POSITION, DebateListAdapter.FeedbackInputRow())
            }
            if (App.prefs.shouldShowContactFeedback && App.prefs.sessionNumber > 1 && rows.count() > FEEDBACK_CONTACT_POSITION) {
                rows.add(FEEDBACK_CONTACT_POSITION, DebateListAdapter.ContactFeedbackRow())
            }
            activity?.setupDebateAdapter(rows, response.debates)
        }

        activity?.toggleErrorWidgetVisibility(false)
    }

    override fun presentAuthScreen() {
        activity?.navigateToAuth()
    }

    override fun addNewDebates(response: DebatesResponse) {
        activity?.addNewDebates(response)
    }

    override fun updateDebateDataSource() {
        activity?.updateDebateDataSource()
    }

    override fun presentNoInternet() {
        activity?.showNoInternerError()
    }

    override fun didSendFeedback() {
        activity?.updateDebateDataSource()
    }

}