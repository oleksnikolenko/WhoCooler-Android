package com.whocooler.app.PickCategory

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.whocooler.app.Common.Models.Category
import com.whocooler.app.R
import kotlinx.android.synthetic.main.activity_pick_category.*

class PickCategoryActivity : AppCompatActivity(), PickCategoryContracts.PresenterViewContract {

    var interactor: PickCategoryContracts.ViewInteractorContract? = null
    var router: PickCategoryContracts.RouterInterface? = null
    lateinit var categoryAdapter: PickCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setContentView(R.layout.activity_pick_category)
        setupModule()

        interactor?.requestCategoryList()
        setupSearchView()
        setupAddCategoryButtonHandler()
    }

    private fun setupModule() {
        var activity = this
        var presenter = PickCategoryPresenter()
        var interactor = PickCategoryInteractor()
        var router = PickCategoryRouter()

        activity.interactor = interactor
        activity.router = router
        presenter.activity = activity
        interactor.presenter = presenter
        router.activity = activity
    }

    override fun displayCategories(categories: ArrayList<Category>) {
        val pickCategoryHandler: (Category) -> Unit = { category ->
            didSelectCategory(category)
        }
        categoryAdapter = PickCategoryAdapter(categories, pickCategoryHandler)

        pick_category_recycler.adapter = categoryAdapter
        pick_category_recycler.layoutManager = LinearLayoutManager(this)

        categoryAdapter.notifyDataSetChanged()
    }

    override fun didSelectCategory(category: Category) {
        router?.returnBackToDebateCreate(category)
    }

    private fun setupSearchView() {
        val searchView = findViewById<SearchView>(R.id.category_pick_search)
        searchView.queryHint = getString(R.string.search_hint)

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
                        if (newText.isNotEmpty()) {
                            interactor?.searchCategory(newText)
                        } else {
                            categoryAdapter.categories = ArrayList()
                            categoryAdapter.notifyDataSetChanged()
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

    private fun setupAddCategoryButtonHandler() {
        var addButton: ImageButton = findViewById(R.id.category_pick_add)

        addButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.create_new_category))

            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.hint = getString(R.string.create_new_category_name)
            builder.setView(input)

            builder.setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                interactor?.createCategory(input.text.toString())
            })

            builder.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })

            val dialog = builder.create()

            dialog.show()
        }
    }


}
