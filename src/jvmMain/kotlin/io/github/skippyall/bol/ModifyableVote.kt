package io.github.skippyall.bol

import io.github.skippyall.bol.Webserver.sendToVote
import io.github.skippyall.bol.message.DeleteClientMessage
import io.github.skippyall.bol.message.UpdateButtonMessage

class ModifyableVote(val name: String, voteOptions: MutableMap<String, Int>) {
    private val mutableVoteOptions = voteOptions
    val voteOptions: Map<String, Int> = voteOptions

    fun updateOption(name: String, votes: Int) {
        updateOption(name, name, votes)
    }

    fun updateOption(oldName: String, newName: String, votes: Int) {
        mutableVoteOptions.remove(oldName)
        mutableVoteOptions[newName] = votes
        UpdateButtonMessage(oldName, newName, votes).sendToVote(this.name)
    }

    fun incrementOption(name: String) {
        synchronized(this) {
            val votes = mutableVoteOptions[name]
            if (votes != null) {
                updateOption(name, votes + 1)
            }
        }
    }

    fun deleteOption(name: String) {
        mutableVoteOptions.remove(name)
        DeleteClientMessage(name).sendToVote(this.name)
    }
}