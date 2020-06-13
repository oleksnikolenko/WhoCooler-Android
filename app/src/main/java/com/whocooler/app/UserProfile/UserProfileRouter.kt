package com.whocooler.app.UserProfile

import android.content.Intent
import com.whocooler.app.Authorization.AuhtorizationActivity

class UserProfileRouter: UserProfileContracts.RouterInterface {

    var activity: UserProfileActivity? = null

    override fun navigateToAuth() {
        val authItent = Intent(activity, AuhtorizationActivity:: class.java)
        activity?.startActivity(authItent)
        activity?.finish()
    }

}