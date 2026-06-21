package io.github.skippyall.bol.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("addButton")
class AddButtonMessage(val name: String) : Message()

@Serializable
@SerialName("click")
class ClickButtonMessage(var name: String) : Message()

@Serializable
@SerialName("delete")
class DeleteButtonMessage(val name: String) : Message()

@Serializable
@SerialName("edit")
class EditButtonMessage(val oldName: String, val newName: String, val newVotes: Int) : Message()

@Serializable
@SerialName("joinVote")
class JoinVoteMessage(val voteName: String) : Message()