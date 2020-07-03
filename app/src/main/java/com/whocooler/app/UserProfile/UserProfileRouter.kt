package com.whocooler.app.UserProfile

import android.app.Activity
import android.content.Intent
import com.whocooler.app.Authorization.AuhtorizationActivity
import com.whocooler.app.Common.Utilities.EXTRA_SHOULD_RELOAD_DEBATE_LIST

class UserProfileRouter: UserProfileContracts.RouterInterface {

    var activity: UserProfileActivity? = null

    override fun navigateToAuth() {
        val authIntent = Intent(activity, AuhtorizationActivity:: class.java)
        authIntent.putExtra(EXTRA_SHOULD_RELOAD_DEBATE_LIST, true)
        activity?.startActivity(authIntent)
    }

    override fun navigateToDebateList() {
        val returnIntent = Intent()
        activity?.setResult(Activity.RESULT_OK, returnIntent)
        activity?.finish()
    }

}