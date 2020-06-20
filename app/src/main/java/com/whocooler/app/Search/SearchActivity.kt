package com.whocooler.app.Search

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebateSide
import com.whocooler.app.Common.Models.SearchResponse
import com.whocooler.app.Common.Services.DebateService
import com.whocooler.app.DebateList.Adapters.DebateListAdapter
import com.whocooler.app.DebateList.DebateListModels
import com.whocooler.app.R
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_debate_detail.*
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(), SearchContracts.PresenterViewContract {

    lateinit var interactor: SearchContracts.ViewInteractorContract
    var router: SearchContracts.RouterInterface? = null
    lateinit var debateAdapter: DebateListAdapter
    private var reloadPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupSearchView()
        setupModule()
    }

    private fun setupModule() {
        var activity = this
        var interactor = SearchInteractor()
        var presenter = SearchPresenter()
        var router = SearchRouter()

        activity.interactor = interactor
        activity.router = router
        interactor.presenter = presenter
        presenter.activity = activity
        router.activity = activity
    }

    private fun setupSearchView() {
        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.queryHint = "Start searching"
        searchView.onActionViewExpanded()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.count() > 2) {
                    interactor?.search(newText, 1)
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
    }

    override fun setupDebateAdapter(response: SearchResponse) {
        DebateService.debates = response.debates

        val voteClickHandler: (Debate, DebateSide, Int) -> PublishSubject<Debate> = { debate, debateSide, position ->
            interactor.vote(debate.id, debateSide.id, position)
        }

        val debateClickHandler: (Debate, Int) -> Unit = { debate, adapterPosition ->
            reloadPosition = adapterPosition
            router?.navigateToDebate(DebateService.debates[adapterPosition], adapterPosition)
        }

        val authRequiredHandler: () -> Unit = {
            router?.navigateToAuth()
        }

        debateAdapter =
            DebateListAdapter(
                response.debates,
                voteClickHandler,
                debateClickHandler,
                authRequiredHandler
            )

        debateAdapter.notifyDataSetChanged()

        search_recycler_view.adapter = debateAdapter
        val layoutManager = LinearLayoutManager(this)
        search_recycler_view.layoutManager = layoutManager
    }

    override fun onRestart() {
        super.onRestart()

        if (reloadPosition != null)  {
            debateAdapter.debates = DebateService.debates
            debateAdapter.notifyItemChanged(reloadPosition!!)
        }
    }

    override fun showErrorToast(message: String) {
        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
    }

}
