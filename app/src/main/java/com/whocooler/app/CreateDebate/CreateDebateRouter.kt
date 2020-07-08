package com.whocooler.app.CreateDebate

import android.content.Intent
import com.whocooler.app.Authorization.AuhtorizationActivity
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Utilities.EXTRA_DEBATE
import com.whocooler.app.Common.Utilities.EXTRA_DEBATE_POSITION
import com.whocooler.app.Common.Utilities.EXTRA_SHOULD_UPDATE_WITH_INTERNET
import com.whocooler.app.DebateDetail.DebateDetailActivity

class CreateDebateRouter: CreateDebateContracts.RouterInterface {

    var activity: CreateDebateActivity? = null

    override fun navigateToAuth() {
        val authIntent = Intent(activity, AuhtorizationActivity:: class.java)
        activity?.startActivity(authIntent)
    }

    override fun navigateToDebate(debate: Debate) {
        val debateDetailIntent = Intent(activity, DebateDetailActivity::class.java)
        debateDetailIntent.putExtra(EXTRA_DEBATE, debate)
        debateDetailIntent.putExtra(EXTRA_DEBATE_POSITION, -1)
        debateDetailIntent.putExtra(EXTRA_SHOULD_UPDATE_WITH_INTERNET, false)

        activity?.startActivity(debateDetailIntent)
        activity?.finish()
    }

}