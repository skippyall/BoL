package io.github.skippyall.bol.gui

import io.github.skippyall.bol.ClientMessageHandler
import kotlinx.browser.document
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.Option

object VoteSelect {
    lateinit var voteSelect: HTMLSelectElement

    fun init() {
        voteSelect = document.getElementById("vote_select") as HTMLSelectElement
        voteSelect.addEventListener("change", {
            ClientMessageHandler.joinVote(voteSelect.value)
        })
    }

    fun update() {
        clear()
        for(voteName in ClientMessageHandler.voteNames) {
            voteSelect.options.add(Option(voteName, voteName))
        }
    }

    fun clear() {
        while (voteSelect.size > 0) {
            voteSelect.remove(0)
        }
    }
}