package com.example.whocooler.DebateList

import android.content.Intent
import com.example.whocooler.Common.Models.Debate
import com.example.whocooler.Common.Utilities.EXTRA_DEBATE
import com.example.whocooler.DebateDetail.DebateDetailActivity

class DebateListRouter : DebateListContracts.RouterInterface {

    var activity: DebateListActivity? = null

    override fun navigateToDebate(debate: Debate) {
        val debateDetailIntent = Intent(activity, DebateDetailActivity::class.java)
        debateDetailIntent.putExtra(EXTRA_DEBATE, debate)
        activity?.startActivity(debateDetailIntent)
    }
}