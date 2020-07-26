package com.whocooler.app.Search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.SearchResponse
import com.whocooler.app.DebateList.Adapters.SearchAdapter
import com.whocooler.app.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity(), SearchContracts.PresenterViewContract {

    lateinit var interactor: SearchContracts.ViewInteractorContract
    var router: SearchContracts.RouterInterface? = null
    lateinit var debateAdapter: SearchAdapter
    private var reloadPosition: Int? = null
    private lateinit var emptyData: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setContentView(R.layout.activity_search)

        setupSearchView()
        setupModule()
        setupActionBar()

        emptyData = findViewById(R.id.search_empty_data)
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

        // Counter is needed to set delays for search
        var counter: CountDownTimer? = null

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                if (counter != null) {
                    counter?.cancel()
                }

                counter = object: CountDownTimer(750, 375) {
                    override fun onTick(millisUntilFinished: Long) {}

                    override fun onFinish() {
                        if (newText.length > 1) {
                            toggleEmptyDataVisibility(false)
                            interactor?.search(newText, 1)
                        } else if (newText.isEmpty() && ::debateAdapter.isInitialized) {
                            toggleEmptyDataVisibility(false)
                            debateAdapter.rows = ArrayList()
                            debateAdapter.notifyDataSetChanged()
                        }
                    }
                }
                counter?.start()

                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
    }

    override fun setupDebateAdapter(response: SearchResponse, rows: ArrayList<SearchAdapter.ISearchListRow>) {
        val debateClickHandler: (Debate, Int) -> Unit = { debate, adapterPosition ->
            reloadPosition = adapterPosition
            router?.navigateToDebate(debate, adapterPosition)
        }

        val didClickMoreHandler: () -> Unit = {
            showMoreAlert()
        }

        debateAdapter =
            SearchAdapter(
                rows,
                debateClickHandler,
                didClickMoreHandler
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

    // Prepares nav bar
    private fun setupActionBar() {
        setSupportActionBar(findViewById(R.id.search_toolbar))
        supportActionBar?.setTitle("")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Handles navigation back
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun setupNotFound() {
        toggleEmptyDataVisibility(true)
    }

    private fun toggleEmptyDataVisibility(isEmptyDataShown: Boolean) {
        if (isEmptyDataShown) {
            emptyData.visibility = View.VISIBLE
            search_recycler_view.visibility = View.GONE
        } else {
            emptyData.visibility = View.GONE
            search_recycler_view.visibility = View.VISIBLE
        }
    }

}
