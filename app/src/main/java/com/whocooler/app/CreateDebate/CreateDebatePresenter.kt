package com.whocooler.app.CreateDebate

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.R

class CreateDebatePresenter : CreateDebateContracts.InteractorPresenterContract {

    var activity: CreateDebateContracts.PresenterViewContract? = null

    override fun navigateToDebate(debate: Debate) {
        activity?.navigateToDebate(debate)
    }

    override fun presentError(errorDescription: String) {
        activity?.showError(App.appContext.getString(R.string.error_unexpected) + errorDescription)
    }
}