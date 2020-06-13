package com.whocooler.app.UserProfile

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.whocooler.app.Common.Models.User
import com.whocooler.app.R
import com.squareup.picasso.Picasso

class UserProfileActivity: AppCompatActivity(), UserProfileContracts.PresenterViewContract {

    lateinit var interactor: UserProfileContracts.ViewInteractorContract
    lateinit var router: UserProfileContracts.RouterInterface

    private fun setupModule() {
        var activity = this
        var interactor = UserProfileInteractor()
        var presenter = UserProfilePresenter()
        var router = UserProfileRouter()

        activity.interactor = interactor
        activity.router = router
        interactor.output = presenter
        presenter.output = activity
        router.activity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupModule()
        setContentView(R.layout.activity_user_profile)

        interactor.getProfile()
    }

    override fun displayProfile(user: User) {
        val userAvatarView = findViewById<ImageView>(R.id.rounded_user_image)
        val namePlaceholder = findViewById<TextView>(R.id.profile_name_placeholder)
        val userName = findViewById<TextView>(R.id.profile_name_txt)
        val logoutBtn = findViewById<Button>(R.id.user_profile_logout_btn)

        Picasso.get().load(user.avatar).into(userAvatarView)
        namePlaceholder.text = "Name"
        userName.text = user.name
        logoutBtn.text = "Logout"

        logoutBtn.setOnClickListener {
            interactor.logout()
        }
    }

    override fun navigateToAuth() {
        router?.navigateToAuth()
    }


}