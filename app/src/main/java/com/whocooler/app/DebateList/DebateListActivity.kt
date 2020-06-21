package com.whocooler.app.DebateList

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebateSide
import com.whocooler.app.Common.Models.DebatesResponse
import com.whocooler.app.Common.Services.DebateService
import com.whocooler.app.DebateList.Adapters.DebateListAdapter
import com.whocooler.app.DebateList.Adapters.DebateListCategoryAdapter
import com.whocooler.app.R
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class DebateListActivity : AppCompatActivity(), DebateListContracts.PresenterViewContract {

    lateinit var interactor: DebateListContracts.ViewInteractorContract
    lateinit var router: DebateListContracts.RouterInterface
    lateinit var debateAdapter: DebateListAdapter
    private var reloadPosition: Int? = null
    override var selectedCategoryId: String = Category.Constant.ALL.id
    private var selectedSorting: String = "popular"
    private var linearLayout = LinearLayoutManager(this)
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private val lastVisibleItemPosition: Int
        get() = linearLayout.findLastVisibleItemPosition()

    private fun setup() {
        var activity = this
        var interactor = DebateListInteractor()
        var presenter = DebateListPresenter()
        var router = DebateListRouter()

        activity.interactor = interactor
        activity.router = router
        interactor.presenter = presenter
        presenter.activity = activity
        router.activity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
        setContentView(R.layout.activity_main)
        setupToolbar()
        handlePagination()

        interactor.getDebates(DebateListModels.DebateListRequest(Category.Constant.ALL.id, selectedSorting,true))

        toggleProgressBar(false)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.listToolBar)
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(Color.WHITE)

        val toolbarTitle = toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = "Who's Cooler"

        val toolbarProfile = toolbar.findViewById<Button>(R.id.toolbar_profile)
        toolbarProfile.setOnClickListener {
            router?.navigateToUserProfile()
        }

        val toolbarSorting = toolbar.findViewById<TextView>(R.id.toolbar_sorting)
        toolbarSorting.text = "Sorting: Popular"
        toolbarSorting.setOnClickListener {
            showSortingAlert()
        }

        var searchButton = toolbar.findViewById<ImageButton>(R.id.toolbar_search)
        searchButton.setOnClickListener {
            reloadPosition = null
            router?.navigateToSearch()
        }
    }

    private fun showSortingAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sorting")

        val sortings = arrayOf("Popular", "Newest", "Oldest")

        builder.setItems(sortings) { _, which ->
            updateSorting(sortings[which])
        }

        val dialog = builder.create()

        dialog.show()
    }

    private fun updateSorting(sorting: String) {
        val toolbarSorting = findViewById<TextView>(R.id.toolbar_sorting)
        toolbarSorting.text = "Sorting: $sorting"

        selectedSorting = sorting.toLowerCase(Locale.ROOT)
        interactor.getDebates(DebateListModels.DebateListRequest(selectedCategoryId, selectedSorting))
    }

    override fun setupCategoryAdapter(categories: ArrayList<Category>) {
        val categoryClickHandler: (Category) -> Unit = { category ->
            debateAdapter.debates.clear()
            debateAdapter.notifyDataSetChanged()
            selectedCategoryId = category.id
            interactor.getDebates(DebateListModels.DebateListRequest(selectedCategoryId, selectedSorting))
        }

        listCategoriesRecyclerView.adapter = DebateListCategoryAdapter(categories, categoryClickHandler)
        listCategoriesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun setupDebateAdapter(response: DebatesResponse) {
        println("?!?! SETUP DATA SERVICE RESPONSE COUNT ${response.debates.count()}")
        DebateService.debates = response.debates

        val voteClickHandler: (Debate, DebateSide, Int) -> PublishSubject<Debate> = { debate, debateSide, position ->
            interactor.vote(debate.id, debateSide.id, position)
        }

        val debateClickHandler: (Debate, Int) -> Unit = { debate, adapterPosition ->
            reloadPosition = adapterPosition
            router?.navigateToDebate(DebateService.debates[adapterPosition], adapterPosition)
        }

        val authRequiredHandler: () -> Unit = {
            router?.navigateToAuthorization()
        }

        val toggleFavoritesHandler: (Debate) -> Unit = {debate->
            interactor?.toggleFavorites(debate)
        }

        debateAdapter =
            DebateListAdapter(
                response.debates,
                voteClickHandler,
                debateClickHandler,
                authRequiredHandler,
                toggleFavoritesHandler,
                true
            )

        debateAdapter.notifyDataSetChanged()

        listRecyclerView.adapter = debateAdapter
        listRecyclerView.layoutManager = linearLayout

        val emptyTextNotification = findViewById<TextView>(R.id.list_empty_state)
        emptyTextNotification.visibility = View.GONE
    }

    override fun addNewDebates(response: DebatesResponse) {
        debateAdapter.debates = DebateService.debates
        debateAdapter.notifyDataSetChanged()
        handlePagination()
        toggleProgressBar(false)
    }

    override fun onRestart() {
        super.onRestart()

        if (reloadPosition != null)  {
            debateAdapter.debates = DebateService.debates
            debateAdapter.notifyItemChanged(reloadPosition!!)
        } else {
            interactor.getDebates(DebateListModels.DebateListRequest(selectedCategoryId, selectedSorting,true))
        }
    }

    override fun updateDebateDataSource() {
        debateAdapter.update(DebateService.debates)
    }

    override fun setupEmptyState(text: String) {
        val emptyTextNotification = findViewById<TextView>(R.id.list_empty_state)
        emptyTextNotification.text = text
        emptyTextNotification.visibility = View.VISIBLE
    }

    private fun handlePagination() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = linearLayout.itemCount
                if (totalItemCount == lastVisibleItemPosition + 1 && interactor.hasDebatesListNextPage()) {
                    interactor.getNextPage()
                    recyclerView.removeOnScrollListener(scrollListener)
                    toggleProgressBar(true)
                }
            }
        }
        listRecyclerView.addOnScrollListener(scrollListener)
    }

    override fun showErrorToast(message: String) {
        Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToAuth() {
        router?.navigateToAuthorization()
    }

    private fun toggleProgressBar(isVisible: Boolean) {
        val progressBar = findViewById<ProgressBar>(R.id.list_bottom_progress_bar)
        progressBar.isVisible = isVisible
    }
}