package com.example.whocooler.DebateList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whocooler.Common.Models.Debate
import com.example.whocooler.Common.Models.DebateSide
import com.example.whocooler.Common.Models.DebatesResponse
import com.example.whocooler.R
import kotlinx.android.synthetic.main.activity_main.*

class DebateListActivity : AppCompatActivity(), DebateListContracts.PresenterViewContract {

    lateinit var interactor: DebateListContracts.ViewInteractorContract
    lateinit var router: DebateListContracts.RouterInterface
    lateinit var debateAdapter: DebateListAdapter

    private fun setup() {
        var activity = this
        var interactor = DebateListInteractor()
        var presenter = DebateListPresenter()
        var router = DebateListRouter()

        activity.interactor = interactor
        activity.router = router
        interactor.output = presenter
        presenter.output = activity
        router.activity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
        setContentView(R.layout.activity_main)

        interactor.getDebates(DebateListModels.DebateListRequest())
    }

    override fun setupAdapter(response: DebatesResponse) {
        var voteClickHandler: (Debate, DebateSide) -> Unit = { debate, debateSide ->
            debate.id?.let { interactor.vote(it, debateSide.id) }
        }

        var debateClickHandler: (Debate) -> Unit = {debate ->
            router?.navigateToDebate(debate)
        }

        debateAdapter = DebateListAdapter(response, voteClickHandler, debateClickHandler)

        listRecyclerView.adapter = debateAdapter
        val layoutManager = LinearLayoutManager(this)
        listRecyclerView.layoutManager = layoutManager
    }

}