package com.whocooler.app.CreateDebate

import com.whocooler.app.Common.Models.Debate
import java.io.File

class CreateDebateContracts {

    interface ViewInteractorContract {
        // Functions for View output / Interactor input
        fun createDebateSides(
            leftName: String,
            rightName: String,
            leftImage: ByteArray,
            rightImage: ByteArray,
            categoryId: String,
            debateName: String?
        )
        fun createDebateStatement(
            leftName: String,
            rightName: String,
            debateImage: ByteArray,
            categoryId: String,
            debateName: String
        )
    }

    interface InteractorPresenterContract {
        // Functions for Interactor output / Presenter input
        fun navigateToDebate(debate: Debate)
        fun presentError(errorDescription: String)
    }

    // Presenter -> View

    interface PresenterViewContract {
        // Functions for Presenter output / View input
        fun navigateToDebate(debate: Debate)
        fun showError(message: String)
    }

    // Router

    interface RouterInterface {
        fun navigateToAuth()
        fun navigateToDebate(debate: Debate)
        fun navigateToPickCategory()
    }

}