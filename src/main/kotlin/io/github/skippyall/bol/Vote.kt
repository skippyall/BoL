package io.github.skippyall.bol

import io.github.skippyall.bol.message.UpdateMessage
import kotlinx.serialization.Serializable

@Serializable
data class Vote(var voteOptions: MutableList<VoteOption>) {
    constructor() : this(mutableListOf())

    @Serializable
    data class VoteOption(var name: String, var votes: Int) {
        constructor(name: String) : this(name, 0)
    }

    fun addTeacherAndUpdate(voteOption: VoteOption) {
        voteOptions += voteOption
        updateTeacher(voteOption)
    }

    fun removeTeacherAndUpdate(voteOption: VoteOption) {
        voteOptions += voteOption
        updateTeacher(voteOption)
    }

    fun updateTeacher(voteOption: VoteOption) {
        UpdateMessage(voteOption).sendToVote(this)
    }
}