package com.whocooler.app.Search

import android.content.Intent
import com.whocooler.app.Authorization.AuhtorizationActivity
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Utilities.EXTRA_DEBATE
import com.whocooler.app.Common.Utilities.EXTRA_DEBATE_POSITION
import com.whocooler.app.Common.Utilities.EXTRA_SHOULD_UPDATE_WITH_INTERNET
import com.whocooler.app.DebateDetail.DebateDetailActivity

class SearchRouter : SearchContracts.RouterInterface {

    var activity: SearchActivity? = null

    override fun navigateToAuth() {
        val authItent = Intent(activity, AuhtorizationActivity:: class.java)
        activity?.startActivity(authItent)
    }

    override fun navigateToDebate(debate: Debate, position: Int) {
        val debateDetailIntent = Intent(activity, DebateDetailActivity::class.java)

        debateDetailIntent.putExtra(EXTRA_DEBATE, debate)
        debateDetailIntent.putExtra(EXTRA_DEBATE_POSITION, position)
        debateDetailIntent.putExtra(EXTRA_SHOULD_UPDATE_WITH_INTERNET, false)

        activity?.startActivity(debateDetailIntent)
    }

}