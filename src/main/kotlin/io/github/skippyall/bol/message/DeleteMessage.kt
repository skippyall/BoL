package io.github.skippyall.bol.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("delete")
class DeleteMessage(val voteOption: String) : Message()