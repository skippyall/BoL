package io.github.skippyall.bol.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("confirm")
class ConfirmMessage(val voteOption: String) : Message()