package com.whocooler.app.CreateDebate

import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Utilities.UNEXPECTED_ERROR

class CreateDebatePresenter : CreateDebateContracts.InteractorPresenterContract {

    var activity: CreateDebateContracts.PresenterViewContract? = null

    override fun didFetchCategories(categories: ArrayList<Category>) {
        activity?.displayCategories(categories)
    }

    override fun navigateToDebate(debate: Debate) {
        activity?.navigateToDebate(debate)
    }

    override fun presentError(errorDescription: String) {
        activity?.showError(UNEXPECTED_ERROR + errorDescription)
    }
}