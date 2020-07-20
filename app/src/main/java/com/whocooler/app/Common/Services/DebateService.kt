package com.whocooler.app.Common.Services

import com.whocooler.app.Common.Models.Debate
import com.whocooler.app.DebateList.Adapters.DebateListAdapter

object DebateService {
    var debates = ArrayList<Debate>()

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
        return list
    }
}