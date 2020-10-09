package com.whocooler.app.DebateList

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.messaging.FirebaseMessaging
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebateSide
import com.whocooler.app.Common.Models.DebatesResponse
import com.whocooler.app.Common.Services.DebateService
import com.whocooler.app.Common.Utilities.LAUNCH_AUTH_WITH_RESULT
import com.whocooler.app.Common.ui.error.ErrorInternetWidget
import com.whocooler.app.DebateList.Adapters.DebateListAdapter
import com.whocooler.app.DebateList.Adapters.DebateListCategoryAdapter
import com.whocooler.app.R
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
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
    private lateinit var errorWidget: ErrorInternetWidget
    private lateinit var mainDataProgressBar: ProgressBar

    private fun setup() {
        val activity = this
        val interactor = DebateListInteractor()
        val presenter = DebateListPresenter()
        val router = DebateListRouter()

        activity.interactor = interactor
        activity.router = router
        interactor.presenter = presenter
        presenter.activity = activity
        router.activity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setContentView(R.layout.activity_main)
        setup()
        setupToolbar()
        handlePagination()
        setupCreateDebateListener()

        interactor.getDebates(DebateListModels.DebateListRequest(selectedCategoryId, selectedSorting,true))

        errorWidget = findViewById(R.id.list_error_widget)
        errorWidget.refreshButton.setOnClickListener {
            refreshDebates()
        }

        mainDataProgressBar = findViewById(R.id.list_main_data_progress_bar)

        togglePaginationProgressBar(false)
    }

    private fun refreshDebates() {
        val shouldReloadCategories = this::debateAdapter.isInitialized
        interactor.getDebates(DebateListModels.DebateListRequest(selectedCategoryId, selectedSorting, !shouldReloadCategories))
    }

    override fun onRestart() {
        super.onRestart()

        if (reloadPosition != null)  {
            debateAdapter.rows = DebateService.getListRows()
            listRecyclerView.itemAnimator = null
            debateAdapter.notifyItemChanged(reloadPosition!!)
            reloadPosition = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LAUNCH_AUTH_WITH_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                refreshDebates()
            }
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.listToolBar)
        setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(Color.WHITE)

        val toolbarTitle = toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = getString(R.string.app_name)

        val toolbarProfile = toolbar.findViewById<Button>(R.id.toolbar_profile)
        toolbarProfile.setOnClickListener {
            router.navigateToUserProfile()
        }

        val toolbarSorting = toolbar.findViewById<ImageButton>(R.id.list_toolbar_sorting)
        toolbarSorting.setOnClickListener {
            showSortingAlert()
        }

        var searchButton = toolbar.findViewById<ImageButton>(R.id.toolbar_search)
        searchButton.setOnClickListener {
            reloadPosition = null
            router.navigateToSearch()
        }
    }

    private fun showSortingAlert() {
        val builder = AlertDialog.Builder(this)
        var currentSorting: String
        if (selectedSorting == "popular") {
            currentSorting = getString(R.string.sorting_popular)
        } else if (selectedSorting == "newest") {
            currentSorting = getString(R.string.sorting_newest)
        } else if (selectedSorting == "oldest") {
            currentSorting = getString(R.string.sorting_oldest)
        } else {
            currentSorting = getString(R.string.sorting_popular)
        }
        builder.setTitle(getString(R.string.sorting_title) + currentSorting)

        val sortings = arrayOf(
            getString(R.string.sorting_popular),
            getString(R.string.sorting_newest),
            getString(R.string.sorting_oldest))


        val defaultSortingNamesForBackend = arrayOf(
            "popular",
            "newest",
            "oldest")

        builder.setItems(sortings) { _, which ->
            updateSorting(defaultSortingNamesForBackend[which])
        }

        val dialog = builder.create()

        dialog.show()
    }

    private fun setupCreateDebateListener() {
        val createDebateButton = findViewById<FloatingActionButton>(R.id.list_floating_button)
        createDebateButton.setOnClickListener {
            router.navigateToCreateDebate()
        }
    }

    private fun updateSorting(sortingForBackend: String) {
        if (sortingForBackend != selectedSorting) {
            selectedSorting = sortingForBackend
            interactor.getDebates(DebateListModels.DebateListRequest(selectedCategoryId, selectedSorting))
        }
    }

    override fun setupCategoryAdapter(categories: ArrayList<Category>) {
        val categoryClickHandler: (Category) -> Unit = { category ->
            debateAdapter.rows.clear()
            debateAdapter.notifyDataSetChanged()
            selectedCategoryId = category.id
            interactor.getDebates(DebateListModels.DebateListRequest(selectedCategoryId, selectedSorting))
        }

        listCategoriesRecyclerView.adapter = DebateListCategoryAdapter(categories, categoryClickHandler)
        listCategoriesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun setupDebateAdapter(rows: ArrayList<DebateListAdapter.IDebateListRow>, debates: ArrayList<Debate>) {
        DebateService.debates = debates
        mainDataProgressBar.visibility = View.GONE

        val voteClickHandler: (Debate, DebateSide, Int) -> PublishSubject<Debate> = { debate, debateSide, position ->
            interactor.vote(debate.id, debateSide.id, position)
        }

        val debateClickHandler: (Debate, Int) -> Unit = { debate, adapterPosition ->
            reloadPosition = adapterPosition
            router.navigateToDebate(DebateService.debates[adapterPosition], adapterPosition)
        }

        val authRequiredHandler: () -> Unit = {
            router.navigateToAuthorization()
        }

        val toggleFavoritesHandler: (Debate) -> Unit = {debate->
            interactor.toggleFavorites(debate)
        }

        val didClickMoreHandler: () -> Unit = {
            showMoreAlert()
        }

        debateAdapter =
            DebateListAdapter(
                rows,
                voteClickHandler,
                debateClickHandler,
                authRequiredHandler,
                toggleFavoritesHandler,
                true,
                didClickMoreHandler
            )

        debateAdapter.notifyDataSetChanged()

        listRecyclerView.adapter = debateAdapter
        listRecyclerView.layoutManager = linearLayout

        val emptyTextNotification = findViewById<TextView>(R.id.list_empty_state)
        emptyTextNotification.visibility = View.GONE
    }

    override fun addNewDebates(response: DebatesResponse) {
        debateAdapter.rows = DebateService.getListRows()
        debateAdapter.notifyDataSetChanged()
        handlePagination()
        togglePaginationProgressBar(false)
    }

    override fun updateDebateDataSource() {
        debateAdapter.update(DebateService.getListRows())
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
                    togglePaginationProgressBar(true)
                }
            }
        }
        listRecyclerView.addOnScrollListener(scrollListener)
    }

    override fun showNoInternerError() {
        toggleErrorWidgetVisibility(true)
    }

    override fun navigateToAuth() {
        router.navigateToAuthorization()
    }

    private fun togglePaginationProgressBar(isVisible: Boolean) {
        val progressBar = findViewById<ProgressBar>(R.id.list_bottom_progress_bar)
        progressBar.isVisible = isVisible
    }

    private fun showMoreAlert() {
        val builder = AlertDialog.Builder(this)
        val options = arrayOf(getString(R.string.report))

        // TODO: - Fix when more will function properly
        builder.setItems(options) { _, _ -> }

        val dialog = builder.create()
        dialog.show()
    }

    override fun toggleErrorWidgetVisibility(isVisible: Boolean) {
        errorWidget.visibility = if (isVisible) View.VISIBLE else View.GONE
        if (isVisible) {
            mainDataProgressBar.visibility = View.GONE
        }
    }
}