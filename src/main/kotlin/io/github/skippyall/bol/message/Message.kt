package io.github.skippyall.bol.message

import io.github.skippyall.bol.BoL
import kotlinx.serialization.Serializable

@Serializable
sealed class Message {
    open fun onReceive() {}

    fun sendToEveryone() = BoL.sendToEveryone(this)
}