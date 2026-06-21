package io.github.skippyall.bol.gui

import kotlinx.browser.document

object Gui {
    fun init() {
        VoteSelect.init()
        Buttons.container = document.getElementById("teacher_buttons")!!
        AddButton.init()
        Sort.init()
        EditMode.init()
    }

    fun clear() {
        VoteSelect.clear()
        Buttons.clear()
    }
}