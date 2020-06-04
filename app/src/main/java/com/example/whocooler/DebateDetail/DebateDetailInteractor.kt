package com.example.whocooler.DebateDetail

import com.example.whocooler.Common.Models.Debate

class DebateDetailInteractor: DebateDetailContracts.ViewInteractorContract {

    var output: DebateDetailContracts.InteractorPresenterContract? = null
    val worker = DebateDetailWorker()

    override fun initDebate(debate: Debate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}