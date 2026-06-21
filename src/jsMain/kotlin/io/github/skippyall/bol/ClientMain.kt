package io.github.skippyall.bol

import io.github.skippyall.bol.gui.Gui
import kotlinx.browser.window

fun main() {
    window.onload = {
        Gui.init()
        ClientNetworkHandler.connect()
    }
}