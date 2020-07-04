package com.whocooler.app.Search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebateSide
import com.whocooler.app.Common.Models.SearchResponse
import com.whocooler.app.DebateList.Adapters.DebateListAdapter
import com.whocooler.app.R
import io.reactivex.rxjava3.subjects.PublishSubject
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
        searchView.queryHint = getString(R.string.search_hint)
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
        val voteClickHandler: (Debate, DebateSide, Int) -> PublishSubject<Debate> = { debate, debateSide, position ->
            interactor.vote(debate.id, debateSide.id, position)
        }

        val debateClickHandler: (Debate, Int) -> Unit = { debate, adapterPosition ->
            reloadPosition = adapterPosition
            router?.navigateToDebate(debateAdapter.debates[adapterPosition], adapterPosition)
        }

        val authRequiredHandler: () -> Unit = {
            router?.navigateToAuth()
        }

        val didClickMoreHandler: () -> Unit = {
            showMoreAlert()
        }

        debateAdapter =
            DebateListAdapter(
                response.debates,
                voteClickHandler,
                debateClickHandler,
                authRequiredHandler,
                didClickMore = didClickMoreHandler
            )

        debateAdapter.notifyDataSetChanged()

        search_recycler_view.adapter = debateAdapter
        val layoutManager = LinearLayoutManager(this)
        search_recycler_view.layoutManager = layoutManager
    }

    override fun showErrorToast(message: String) {
        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun showMoreAlert() {
        val builder = AlertDialog.Builder(this)
        val options = arrayOf(getString(R.string.report))

        // TODO: - Fix when more will function properly
        builder.setItems(options) { _, _ -> }

        val dialog = builder.create()
        dialog.show()
    }

}
