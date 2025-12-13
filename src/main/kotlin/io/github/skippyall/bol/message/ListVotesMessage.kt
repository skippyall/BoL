package io.github.skippyall.bol.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("listVotes")
class ListVotesMessage(val votes: Collection<String>) : Message()