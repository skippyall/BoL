package io.github.skippyall.bol.message

import io.github.skippyall.bol.BoL.vote
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("click")
class ClickMessage(var name: String) : Message() {
    override fun onReceive() {
        val teacher = vote.teachers.first { teacher -> teacher.name == name}
        teacher.votes++

        vote.updateTeacher(teacher)
    }
}