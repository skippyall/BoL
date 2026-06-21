package io.github.skippyall.bol

import io.github.skippyall.bol.gui.Gui
import io.github.skippyall.bol.message.Message
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket

object ClientNetworkHandler {
    var socket: WebSocket? = null

    fun connect() {
        Gui.clear()
        val newSocket = WebSocket(window.location.origin + "/websocket")
        socket = newSocket
        newSocket.onopen = {
            ClientMessageHandler.onConnected()
        }
        newSocket.onmessage = { onMessage(it) }
        newSocket.onclose = { onLoseConnection() }
    }

    fun sendMessage(message: Message) {
        val text = Json.encodeToString<Message>(message)
        println("Send: $text")
        socket?.send(text)
    }

    fun onMessage(event: MessageEvent) {
        val message = Json.decodeFromString<Message>(event.data as String)
        println("Received: ${event.data as String}")
        ClientMessageHandler.handleMessage(message)
    }

    fun onLoseConnection() {
        Gui.clear()
        window.setTimeout({
            connect()
        }, 2000)
    }
}