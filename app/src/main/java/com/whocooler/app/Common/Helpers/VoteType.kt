package com.whocooler.app.Common.Helpers

enum class VoteType {
    up, down, none;

    companion object {
        fun from(value: String) = VoteType.values().first { it.name == value }
    }
}