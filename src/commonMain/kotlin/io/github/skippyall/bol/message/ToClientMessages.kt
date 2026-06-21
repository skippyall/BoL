package io.github.skippyall.bol.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("confirm")
class ConfirmClickButtonMessage(val name: String) : Message()

@Serializable
@SerialName("data")
class VoteDataMessage(var vote: Map<String, Int>) : Message()

@Serializable
@SerialName("listVotes")
class ListVotesMessage(val votes: Collection<String>) : Message()

@Serializable
@SerialName("update")
class UpdateButtonMessage(val oldName: String, val newName: String, val votes: Int) : Message() {
    constructor(name: String, votes: Int) : this(name, name, votes)
}

@Serializable
@SerialName("deleteClient")
class DeleteClientMessage(val name: String) : Message()