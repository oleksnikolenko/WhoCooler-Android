package com.whocooler.app.Common.Services

import com.whocooler.app.Common.Models.Debate

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
}