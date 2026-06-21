package io.github.skippyall.bol.gui

import io.github.skippyall.bol.ClientMessageHandler
import io.github.skippyall.bol.ClientNetworkHandler
import io.github.skippyall.bol.message.DeleteButtonMessage
import io.github.skippyall.bol.message.EditButtonMessage
import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

object EditMode {
    lateinit var editToggleButton: HTMLButtonElement
    lateinit var editWindow: HTMLElement
    lateinit var mainContent: HTMLElement

    lateinit var editName: HTMLInputElement
    lateinit var editCount: HTMLInputElement
    lateinit var deleteButton: HTMLButtonElement
    lateinit var editSaveButton: HTMLButtonElement
    lateinit var editAbortButton: HTMLButtonElement

    var editButtons = false
    var currentButton: String? = null

    fun init() {
        editWindow = document.getElementById("edit_button") as HTMLElement
        mainContent = document.getElementById("main_content") as HTMLElement
        editName = document.getElementById("edit_name") as HTMLInputElement
        editCount = document.getElementById("edit_count") as HTMLInputElement

        editToggleButton = document.getElementById("edit_toggle_button") as HTMLButtonElement
        deleteButton = document.getElementById("delete_button") as HTMLButtonElement
        editSaveButton = document.getElementById("edit_save_button") as HTMLButtonElement
        editAbortButton = document.getElementById("edit_abort_button") as HTMLButtonElement

        editToggleButton.onclick = { onEditToggle() }
        deleteButton.onclick = { onDelete() }
        editSaveButton.onclick = { onEditClose() }
        editAbortButton.onclick = { onEditAbort() }
    }

    fun onEditToggle() {
        editButtons = !editButtons
        if(editButtons) {
            editToggleButton.innerText = "Zum Zählmodus"
            document.body?.classList?.add("edit_mode")
        } else {
            editToggleButton.innerText = "Zum Anpassungsmodus"
            document.body?.classList?.remove("edit_mode")
        }
    }

    fun onEdit(name: String) {
        val vote = ClientMessageHandler.vote

        if(vote != null && vote.contains(name)) {
            currentButton = name
            editWindow.classList.add("edit_button_show")
            editWindow.classList.remove("edit_button_hide")
            mainContent.classList.add("blur")

            editName.value = name
            editCount.value = vote[name].toString()
        }
    }

    fun hideEditDialog() {
        editWindow.classList.remove("edit_button_show")
        editWindow.classList.add("edit_button_hide")
        mainContent.classList.remove("blur")
        currentButton = null
    }

    fun onEditClose() {
        val currentButton = currentButton
        if(currentButton != null) {
            val newName = editName.value
            val newVotes = editCount.value.toIntOrNull()
            if(newVotes != null) {
                ClientNetworkHandler.sendMessage(EditButtonMessage(currentButton, newName, newVotes))
            }
            hideEditDialog()
        }
    }

    fun onDelete() {
        val currentButton = currentButton
        if(currentButton != null) {
            ClientNetworkHandler.sendMessage(DeleteButtonMessage(currentButton))
            hideEditDialog()
        }
    }

    fun onEditAbort() {
        hideEditDialog()
    }
}