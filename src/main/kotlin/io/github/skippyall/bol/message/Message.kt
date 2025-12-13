package io.github.skippyall.bol.message

import io.github.skippyall.bol.BoL
import io.github.skippyall.bol.Vote
import io.javalin.websocket.WsContext
import kotlinx.serialization.Serializable

@Serializable
sealed class Message {
    open fun onReceive(context: WsContext) {}

    fun sendToEveryone() = BoL.sendToEveryone(this)
    fun sendToVote(vote: Vote) = BoL.sendToVote(vote, this)
    fun send(context: WsContext) = BoL.send(context, this)
}