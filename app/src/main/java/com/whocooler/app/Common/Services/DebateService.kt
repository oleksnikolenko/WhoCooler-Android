package com.whocooler.app.Common.Services

import com.whocooler.app.Common.App.App
import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.Common.Utilities.FEEDBACK_CONTACT_POSITION
import com.whocooler.app.Common.Utilities.FEEDBACK_INPUT_POSITION
import com.whocooler.app.DebateList.Adapters.DebateListAdapter

object DebateService {
    private var debates = ArrayList<Debate>()

    fun setDebates(debates: ArrayList<Debate>) {
        this.debates = debates
    }

    fun updateDebate(debate: Debate) {
        var currentDebate = debates.filter { it.id == debate.id }.first()
        debates[debates.indexOf(currentDebate)] = debate
    }

    fun toggleFavorite(debate: Debate) {
        var debate = debates.filter { it.id == debate.id }.first()
        debates[debates.indexOf(debate)].isFavorite = !debates[debates.indexOf(debate)].isFavorite
    }

    fun getListRows(): ArrayList<DebateListAdapter.IDebateListRow> {
        var list = ArrayList<DebateListAdapter.IDebateListRow>()
        debates.forEach { debate ->
            if (debate.type == "sides") {
                list.add(DebateListAdapter.SidesRow(debate))
            } else if (debate.type == "statement") {
                list.add(DebateListAdapter.StatementRow(debate))
            }
        }
        if (App.prefs.shouldShowFeedbackInput && list.count() > FEEDBACK_INPUT_POSITION) {
            list.add(FEEDBACK_INPUT_POSITION, DebateListAdapter.FeedbackInputRow())
        }
        if (App.prefs.shouldShowContactFeedback && App.prefs.sessionNumber > 1 && list.count() > FEEDBACK_CONTACT_POSITION) {
            list.add(FEEDBACK_CONTACT_POSITION, DebateListAdapter.ContactFeedbackRow())
        }
        return list
    }
}