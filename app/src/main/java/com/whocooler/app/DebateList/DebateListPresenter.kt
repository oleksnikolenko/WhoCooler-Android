package com.whocooler.app.DebateList

import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.DebatesResponse
import com.whocooler.app.Common.Utilities.UNEXPECTED_ERROR

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
            activity?.setupEmptyState("You have no favorites yet\nAdd them in the feed and they will appear here")
        } else {
            activity?.setupDebateAdapter(response)
        }
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

    override fun presentError() {
        activity?.showErrorToast(UNEXPECTED_ERROR)
    }

}