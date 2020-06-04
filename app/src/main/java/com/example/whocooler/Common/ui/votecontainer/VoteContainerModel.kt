package com.example.whocooler.Common.ui.votecontainer

data class VoteContainerModel(
    val leftName: String,
    val rightName: String,
    val leftVote: Int,
    val rightVote: Int
) {

    private fun getTotal(): Int {
        return leftVote + rightVote
    }

    fun getPercentLeft(): Float {
        return (leftVote.toFloat() / getTotal()) * 100
    }

    fun getPercentRight(): Float {
        return (rightVote.toFloat() / getTotal()) * 100
    }
}