package com.whocooler.app.DebateList

import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.DebatesResponse

class DebateListPresenter : DebateListContracts.InteractorPresenterContract {

    var output: DebateListContracts.PresenterViewContract? = null

    override fun presentDebates(response: DebatesResponse, shouldReloadCategories: Boolean) {
        var categories = ArrayList<Category> ()
        categories.addAll(response.categories)
        categories.add(0, Category.Constant.ALL)

        if (shouldReloadCategories) {
            output?.setupCategoryAdapter(categories)
        }
        output?.setupDebateAdapter(response)
    }

}