package io.github.skippyall.bol.message

import io.github.skippyall.bol.Vote
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("update")
class UpdateMessage(val voteOption: Vote.VoteOption) : Message()