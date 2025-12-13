package io.github.skippyall.bol.message

import io.github.skippyall.bol.BoL
import io.github.skippyall.bol.Vote
import io.javalin.websocket.WsContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("addButton")
class AddButtonMessage(val name: String) : Message() {
    override fun onReceive(context: WsContext) {
        val vote = BoL.getVote(context)
        if(vote == null) {
            return
        }

        vote.addTeacherAndUpdate(Vote.Teacher(name))
    }
}