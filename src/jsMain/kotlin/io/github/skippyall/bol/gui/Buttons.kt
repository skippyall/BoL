package io.github.skippyall.bol.gui

import io.github.skippyall.bol.ClientMessageHandler
import io.github.skippyall.bol.ClientNetworkHandler
import io.github.skippyall.bol.message.ClickButtonMessage
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.asList

object Buttons {
    lateinit var container: Element
    var sort: Sort = Sort.INPUT_ORDER

    fun update() {
        clear()
        val voteData = ClientMessageHandler.vote?.toList()?.toMutableList()
        if(voteData != null && !voteData.isEmpty()) {
            val comparator = sort.comparator
            if(comparator != null) {
                voteData.sortWith(comparator)
            } else {
                voteData.reverse()
            }

            for(voteOption in voteData) {
                addButton(voteOption.first, voteOption.second)
            }
        }
    }

    fun clear() {
        while(container.children.length > 0) {
            container.removeChild(container.firstChild!!)
        }
    }

    fun onButtonClick(name: String) {
        if(EditMode.editButtons) {
            EditMode.onEdit(name)
        } else {
            ClientNetworkHandler.sendMessage(ClickButtonMessage(name))
        }
    }

    fun addButton(name: String, votes: Int) {
        val button = document.createElement("button") as HTMLButtonElement
        button.setAttribute("class", "teacher_button")
        button.setAttribute("teacher", name)
        button.onclick = { onButtonClick(name) }

        val paragraph = document.createElement("p") as HTMLParagraphElement

        button.appendChild(paragraph)
        paragraph.innerText = "$name $votes"

        container.appendChild(button)
    }

    fun findButton(name: String): Element? {
        val buttons = document.getElementsByClassName("teacher_button")
        for(currentButton in buttons.asList()) {
            if(currentButton.getAttribute("teacher") == name) {
                return currentButton
            }
        }
        return null
    }

    fun confirmButtonClick(name: String) {
        val button = findButton(name)
        if(button != null) {
            button.classList.add("confirm_button")
            window.setTimeout({ button.classList.remove("confirm_button") }, 500)
        }
    }
}