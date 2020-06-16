package com.whocooler.app.DebateDetail

import android.content.Intent
import com.whocooler.app.Authorization.AuhtorizationActivity

class DebateDetailRouter: DebateDetailContracts.RouterInterface {

    var activity: DebateDetailActivity? = null

    override fun navigateToAuth() {
        val authItent = Intent(activity, AuhtorizationActivity:: class.java)
        activity?.startActivity(authItent)
    }
}