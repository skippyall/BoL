package io.github.skippyall.bol.message

import io.github.skippyall.bol.BoL
import io.javalin.websocket.WsContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("joinVote")
class JoinVoteMessage(val voteName: String) : Message() {
    override fun onReceive(context: WsContext) {
        val vote = BoL.votes[voteName]
        if(vote == null) {
            return
        }
        DataMessage(vote).send(context)
        BoL.handlersToVote[context.sessionId()] = vote

        println("Vote switch to $voteName")
    }
}