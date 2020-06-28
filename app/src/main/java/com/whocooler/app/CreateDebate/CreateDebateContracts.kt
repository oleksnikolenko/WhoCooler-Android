package com.whocooler.app.CreateDebate

import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import java.io.File

class CreateDebateContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun getCategoryList()
        fun createDebate(
            leftName: String,
            rightName: String,
            leftImage: File,
            rightImage: File,
            categoryId: String
        )
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun didFetchCategories(categories: ArrayList<Category>)
        fun navigateToDebate(debate: Debate)
        fun presentError()
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun displayCategories(categories: ArrayList<Category>)
        fun navigateToDebate(debate: Debate)
        fun showError(message: String)
    }

    // Router

    interface RouterInterface {
        fun navigateToAuth()
        fun navigateToDebate(debate: Debate)
    }

}