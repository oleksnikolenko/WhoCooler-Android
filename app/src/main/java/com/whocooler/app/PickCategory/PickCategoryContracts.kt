package com.whocooler.app.PickCategory

import com.whocooler.app.Common.Models.CategoriesResponse
import com.whocooler.app.Common.Models.Category

class PickCategoryContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun requestCategoryList()
        fun searchCategory(searchContex: String)
        fun createCategory(name: String)
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun didFetchCategories(categoriesResponse: CategoriesResponse)
        fun didCreateCategory(category: Category)
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun displayCategories(categories: ArrayList<Category>)
        fun didSelectCategory(category: Category)
    }

    // Router

    interface RouterInterface {
        fun returnBackToDebateCreate(category: Category)
    }
}