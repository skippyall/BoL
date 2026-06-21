package io.github.skippyall.bol.gui

import io.github.skippyall.bol.ClientNetworkHandler
import io.github.skippyall.bol.message.AddButtonMessage
import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement

object AddButton {
    lateinit var nameInput: HTMLInputElement

    fun init() {
        nameInput = document.getElementById("add_button_name") as HTMLInputElement

        val addButtonButton = document.getElementById("add_button_button") as HTMLButtonElement
        addButtonButton.onclick = { addButton() }
    }

    fun addButton() {
        println("Hi")
        ClientNetworkHandler.sendMessage(AddButtonMessage(nameInput.value))
    }
}