package com.whocooler.app.DebateList

import android.content.Intent
import com.whocooler.app.Authorization.AuhtorizationActivity
import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Utilities.EXTRA_DEBATE
import com.whocooler.app.Common.Utilities.EXTRA_DEBATE_POSITION
import com.whocooler.app.Common.Utilities.EXTRA_SHOULD_UPDATE_WITH_INTERNET
import com.whocooler.app.Common.Utilities.LAUNCH_AUTH_WITH_RESULT
import com.whocooler.app.CreateDebate.CreateDebateActivity
import com.whocooler.app.DebateDetail.DebateDetailActivity
import com.whocooler.app.Search.SearchActivity
import com.whocooler.app.UserProfile.UserProfileActivity

class DebateListRouter : DebateListContracts.RouterInterface {

    var activity: DebateListActivity? = null

    override fun navigateToDebate(debate: Debate, position: Int) {
        val debateDetailIntent = Intent(activity, DebateDetailActivity::class.java)

        debateDetailIntent.putExtra(EXTRA_DEBATE, debate)
        debateDetailIntent.putExtra(EXTRA_DEBATE_POSITION, position)
        debateDetailIntent.putExtra(EXTRA_SHOULD_UPDATE_WITH_INTERNET, true)

        activity?.startActivity(debateDetailIntent)
    }

    override fun navigateToAuthorization() {
        val authItent = Intent(activity, AuhtorizationActivity:: class.java)
        activity?.startActivityForResult(authItent, LAUNCH_AUTH_WITH_RESULT)
    }

    override fun navigateToUserProfile() {
        if (App.prefs.isTokenEmpty()) {
            navigateToAuthorization()
        } else {
            val authItent = Intent(activity, UserProfileActivity:: class.java)
            activity?.startActivityForResult(authItent, LAUNCH_AUTH_WITH_RESULT)
        }
    }

    override fun navigateToSearch() {
        val searchIntent = Intent(activity, SearchActivity:: class.java)
        activity?.startActivity(searchIntent)
    }

    override fun navigateToCreateDebate() {
        val createDebateIntent = Intent(activity, CreateDebateActivity:: class.java)
        activity?.startActivity(createDebateIntent)
    }
}