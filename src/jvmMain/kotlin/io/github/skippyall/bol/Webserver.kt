package io.github.skippyall.bol

import io.github.skippyall.bol.BoL.logger
import io.github.skippyall.bol.ServerMessageHandler.onReceive
import io.github.skippyall.bol.message.ListVotesMessage
import io.github.skippyall.bol.message.Message
import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import io.javalin.websocket.WsContext
import kotlinx.serialization.json.Json

object Webserver {
    lateinit var javalin: Javalin

    var handlers = mutableSetOf<WsContext>()
    var handlersToVote = mutableMapOf<String, String>()

    fun start() {
        javalin = Javalin.create { config ->
            config.staticFiles.add { staticFileConfig ->
                staticFileConfig.location = Location.CLASSPATH
                staticFileConfig.directory = "static"
            }
            config.routes
                .get("/") {
                    it.redirect("/index.html")
                }
                .get("/data") {
                    it.result(Json.encodeToString(BoL.votes))
                }
                .ws("/websocket") {
                    it.onMessage {
                        try {
                            Json.decodeFromString<Message>(it.message()).onReceive(it)
                        } catch (e: Exception) {
                            logger.error("Error while decoding message from client", e)
                        }
                    }

                    it.onConnect { handler ->
                        ListVotesMessage(BoL.votes.keys).send(handler)
                        handlers.add(handler)
                    }

                    it.onClose { handler ->
                        handlers.remove(handler)
                        handlersToVote.remove(handler.sessionId)
                    }
                }
        }

        javalin.start(BoL.port ?: 0)
        println("Mögliche Adressen:")
        for(address in findLocalIp()) {
            println("http://${address}:${javalin.port()}")
        }
    }

    fun stop() {
        for (context in handlers) {
            context.closeSession()
        }

        javalin.stop()
    }

    fun sendPing() {
        for (context in handlers) {
            context.sendPing()
        }
    }

    fun getVote(context: WsContext): ModifyableVote? {
        return BoL.votes[handlersToVote[context.sessionId()]]
    }

    fun sendToEveryone(message: Message) {
        for (context in handlers) {
            send(context, message)
        }
    }

    fun sendToVote(name: String, message: Message) {
        for (context in handlers) {
            if(handlersToVote[context.sessionId] == name) {
                send(context, message)
            }
        }
    }

    fun send(context: WsContext, message: Message) {
        context.send(Json.encodeToString(message))
    }

    fun Message.sendToVote(name: String) = sendToVote(name, this)
    fun Message.send(context: WsContext) = send(context, this)
}