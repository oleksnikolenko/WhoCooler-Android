package com.whocooler.app.PickCategory

import com.whocooler.app.Common.Models.CategoriesResponse
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Utilities.EXTRA_PICK_CATEGORY

class PickCategoryPresenter: PickCategoryContracts.InteractorPresenterContract {

    var activity: PickCategoryContracts.PresenterViewContract? = null

    override fun didFetchCategories(categoriesResponse: CategoriesResponse) {
        activity?.displayCategories(categoriesResponse.categories)
    }

    override fun didCreateCategory(category: Category) {
        activity?.didSelectCategory(category)
    }

}