package com.example.whocooler.DebateList

import android.content.Context
import com.example.whocooler.Common.Models.Debate
import com.example.whocooler.Common.Models.DebatesResponse

class DebateListPresenter : DebateListContracts.InteractorPresenterContract {

    var output: DebateListContracts.PresenterViewContract? = null

    override fun presentDebates(response: DebatesResponse) {
        print("?!?!PRESENTER!!, response: {$response}")
        output?.setupAdapter(response)
    }

    override fun reloadDebate(debate: Debate, position: Int) {
        output?.reloadDebate(debate, position)
    }

}