package io.github.skippyall.bol

import io.github.skippyall.bol.Webserver.send
import io.github.skippyall.bol.message.AddButtonMessage
import io.github.skippyall.bol.message.ClickButtonMessage
import io.github.skippyall.bol.message.ConfirmClickButtonMessage
import io.github.skippyall.bol.message.DeleteButtonMessage
import io.github.skippyall.bol.message.EditButtonMessage
import io.github.skippyall.bol.message.JoinVoteMessage
import io.github.skippyall.bol.message.Message
import io.github.skippyall.bol.message.VoteDataMessage
import io.javalin.websocket.WsContext

object ServerMessageHandler {
    fun Message.onReceive(context: WsContext) {
        when (val message = this) {
            is AddButtonMessage -> message.onReceive(context)
            is ClickButtonMessage -> message.onReceive(context)
            is JoinVoteMessage -> message.onReceive(context)
            is DeleteButtonMessage -> message.onReceive(context)
            is EditButtonMessage -> message.onReceive(context)
            else -> {}
        }
    }

    fun AddButtonMessage.onReceive(context: WsContext) {
        val vote = Webserver.getVote(context) ?: return

        if (vote.voteOptions.containsKey(this.name)) {
            return
        }

        vote.updateOption(name, 0)
    }

    fun ClickButtonMessage.onReceive(context: WsContext) {
        val vote = Webserver.getVote(context) ?: return

        vote.incrementOption(this.name)

        ConfirmClickButtonMessage(name).send(context)
    }

    fun JoinVoteMessage.onReceive(context: WsContext) {
        val vote = BoL.votes[voteName] ?: return

        VoteDataMessage(vote.voteOptions).send(context)
        Webserver.handlersToVote[context.sessionId()] = voteName
    }

    fun DeleteButtonMessage.onReceive(context: WsContext) {
        val vote = Webserver.getVote(context) ?: return
        vote.deleteOption(name)
    }

    fun EditButtonMessage.onReceive(context: WsContext) {
        val vote = Webserver.getVote(context) ?: return
        vote.updateOption(oldName, newName, newVotes)
    }
}