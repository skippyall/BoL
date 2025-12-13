package io.github.skippyall.bol.message

import io.github.skippyall.bol.BoL
import io.javalin.websocket.WsContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("click")
class ClickMessage(var name: String) : Message() {
    override fun onReceive(context: WsContext) {
        val vote = BoL.handlersToVote[context.sessionId()]
        if(vote == null) {
            return
        }
        val teacher = vote.teachers.first { teacher -> teacher.name == name}
        teacher.votes++

        vote.updateTeacher(teacher)
        ConfirmMessage(name).send(context)
    }
}