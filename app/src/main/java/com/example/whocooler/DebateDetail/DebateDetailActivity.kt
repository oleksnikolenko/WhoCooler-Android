package com.example.whocooler.DebateDetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.whocooler.Common.Models.Debate
import com.example.whocooler.Common.Utilities.EXTRA_DEBATE
import com.example.whocooler.DebateList.DebateListInteractor
import com.example.whocooler.DebateList.DebateListPresenter
import com.example.whocooler.DebateList.DebateListRouter
import com.example.whocooler.R

class DebateDetailActivity: AppCompatActivity(), DebateDetailContracts.PresenterViewContract {

    lateinit var interactor: DebateDetailContracts.ViewInteractorContract
    lateinit var router: DebateDetailContracts.RouterInterface
    lateinit var debateDetailAdapter: DebateDetailAdapter

    lateinit var debate: Debate

    private fun setup() {
        var activity = this
        var interactor = DebateDetailInteractor()
        var presenter = DebateDetailPresenter()
        var router = DebateDetailRouter()

        activity.interactor = interactor
        activity.router = router
        interactor.output = presenter
        presenter.output = activity
        router.activity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
        setContentView(R.layout.activity_debate_detail)

        val debate = intent.getParcelableExtra<Debate>(EXTRA_DEBATE)
        interactor.initDebate(debate)
    }



}