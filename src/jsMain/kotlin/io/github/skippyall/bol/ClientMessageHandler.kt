package io.github.skippyall.bol

import io.github.skippyall.bol.gui.Buttons
import io.github.skippyall.bol.gui.VoteSelect
import io.github.skippyall.bol.message.ConfirmClickButtonMessage
import io.github.skippyall.bol.message.DeleteClientMessage
import io.github.skippyall.bol.message.VoteDataMessage
import io.github.skippyall.bol.message.JoinVoteMessage
import io.github.skippyall.bol.message.ListVotesMessage
import io.github.skippyall.bol.message.Message
import io.github.skippyall.bol.message.UpdateButtonMessage

object ClientMessageHandler {
    var currentVoteName: String? = null
    var vote: MutableMap<String, Int>? = null
    var voteNames: Collection<String> = listOf()

    fun handleMessage(message: Message) {
        when(message) {
            is ListVotesMessage -> {
                voteNames = message.votes
                VoteSelect.update()
                if(voteNames.isNotEmpty() && currentVoteName == null) {
                    joinVote(voteNames.first())
                }
            }
            is VoteDataMessage -> {
                vote = message.vote.toMutableMap()
                Buttons.update()
            }
            is UpdateButtonMessage -> {
                vote?.remove(message.oldName)
                vote?.put(message.newName, message.votes)
                Buttons.update()
            }
            is ConfirmClickButtonMessage -> {
                Buttons.confirmButtonClick(message.name)
            }
            is DeleteClientMessage -> {
                vote?.remove(message.name)
                Buttons.update()
            }
            else -> {}
        }
    }

    fun joinVote(vote: String) {
        currentVoteName = vote
        ClientNetworkHandler.sendMessage(JoinVoteMessage(vote))
    }

    fun onConnected() {
        currentVoteName?.let { ClientNetworkHandler.sendMessage(JoinVoteMessage(it)) }
    }
}
