package com.example.whocooler.Common.ui.votecontainer

import com.example.whocooler.Common.Models.Debate

data class VoteContainerModel(val debate: Debate) {

    private fun getTotal(): Int {
        return debate.leftSide.ratingCount + debate.rightSide.ratingCount//leftVote + rightVote
    }

    fun getPercentLeft(): Float {
        return (debate.leftSide.ratingCount.toFloat() / getTotal()) * 100
    }

    fun getPercentRight(): Float {
        return (debate.rightSide.ratingCount.toFloat() / getTotal()) * 100
    }
}