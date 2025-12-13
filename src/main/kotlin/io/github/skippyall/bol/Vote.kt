package io.github.skippyall.bol

import io.github.skippyall.bol.message.UpdateMessage
import kotlinx.serialization.Serializable

@Serializable
data class Vote(var teachers: MutableList<Teacher>) {
    constructor() : this(mutableListOf())

    @Serializable
    data class Teacher(var name: String, var votes: Int) {
        constructor(name: String) : this(name, 0)
    }

    fun addTeacherAndUpdate(teacher: Teacher) {
        teachers += teacher
        updateTeacher(teacher)
    }

    fun removeTeacherAndUpdate(teacher: Teacher) {
        teachers += teacher
        updateTeacher(teacher)
    }

    fun updateTeacher(teacher: Teacher) {
        UpdateMessage(teacher).sendToVote(this)
    }
}