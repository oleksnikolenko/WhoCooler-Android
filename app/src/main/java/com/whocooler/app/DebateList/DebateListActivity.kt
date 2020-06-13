package com.whocooler.app.DebateList

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Models.DebateSide
import com.whocooler.app.Common.Models.DebatesResponse
import com.whocooler.app.Common.Utilities.dip
import com.whocooler.app.DebateList.Adapters.DebateListAdapter
import com.whocooler.app.DebateList.Adapters.DebateListCategoryAdapter
import com.whocooler.app.R
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
        setupToolbar()

        interactor.getDebates(DebateListModels.DebateListRequest(shouldReloadCategories = true))
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.listToolBar)
        toolbar.title = "Who's Cooler"
        toolbar.setTitleTextColor(Color.BLACK)
        toolbar.setBackgroundColor(Color.WHITE)

        val userButton = Button(this)
        userButton.layoutParams = Toolbar.LayoutParams(
            toolbar.dip(28),
            toolbar.dip(28)
        ).apply {
            gravity = Gravity.CENTER
        }
        userButton.setBackgroundResource(R.drawable.profile)

        userButton.setOnClickListener {
            router?.navigateToUserProfile()
        }

        toolbar.addView(userButton)
    }

    override fun setupCategoryAdapter(categories: ArrayList<Category>) {
        var categoryClickHandler: (Category) -> Unit = {category ->
            debateAdapter.response.debates.clear()
            debateAdapter.notifyDataSetChanged()
            interactor.getDebates(DebateListModels.DebateListRequest(category.id))
        }

        listCategoriesRecyclerView.adapter = DebateListCategoryAdapter(categories, categoryClickHandler)

        listCategoriesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun setupDebateAdapter(response: DebatesResponse) {
        var voteClickHandler: (Debate, DebateSide, Int) -> Unit = { debate, debateSide, position ->
            debate.id?.let {
                interactor.vote(it, debateSide.id, position)
            }
        }

        var debateClickHandler: (Debate) -> Unit = {debate ->
            router?.navigateToDebate(debate)
        }

        var authRequiredHandler: () -> Unit = {
            router?.navigateToAuthorization()
        }

        debateAdapter =
            DebateListAdapter(
                response,
                voteClickHandler,
                debateClickHandler,
                authRequiredHandler
            )

        debateAdapter.notifyDataSetChanged()

        listRecyclerView.adapter = debateAdapter
        val layoutManager = LinearLayoutManager(this)
        listRecyclerView.layoutManager = layoutManager
    }

    override fun onRestart() {
        super.onRestart()

        interactor.getDebates(DebateListModels.DebateListRequest(shouldReloadCategories = true))
    }
}