package io.github.skippyall.bol

import io.github.skippyall.bol.message.ListVotesMessage
import io.github.skippyall.bol.message.Message
import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import io.javalin.websocket.WsContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

object BoL {
    val logger: Logger = LoggerFactory.getLogger("BoL")
    var votes = mutableMapOf<String,Vote>()
    var handlers = mutableSetOf<WsContext>()
    var handlersToVote = mutableMapOf<String,Vote>()

    val pretty = Json {
        prettyPrint = true
    }

    val dataFile = File("data.json")

    lateinit var javalin: Javalin

    @Volatile
    var waiting = false
    @Volatile
    var shouldStop = false

    val saveThread = Thread {
        try {
            while (true) {
                waiting = true
                Thread.sleep(15000)
                waiting = false
                save()

                for (context in handlers) {
                    context.sendPing()
                }

                if(shouldStop) {
                    break
                }
            }
        } catch (_: InterruptedException) {}
    }

    fun main() {
        start()
        while(!readlnOrNull().equals("stop")) {}
        stop()
    }

    fun start() {
        load()

        javalin = Javalin.create { config ->
            config.staticFiles.add { staticFileConfig ->
                staticFileConfig.location = Location.CLASSPATH
                staticFileConfig.directory = "static"
            }
            config.routes
                .get("/") {
                    it.redirect("/vote.html")
                }
                .get("/data") {
                    it.result(Json.encodeToString(votes))
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
                        ListVotesMessage(votes.keys).send(handler)
                        handlers.add(handler)
                    }

                    it.onClose { handler ->
                        handlers.remove(handler)
                    }
                }
        }

        javalin.start(25602)
        saveThread.start()
    }

    fun stop() {
        for (context in handlers) {
            context.closeSession()
        }

        javalin.stop()

        shouldStop = true
        if(waiting) {
            saveThread.interrupt()
        }
    }

    fun load() {
        try {
            votes = Json.decodeFromString<MutableMap<String,Vote>>(dataFile.readText(Charsets.UTF_8))
        }
        catch (_: IOException) {}
        catch (_: SerializationException) {}
        catch (_: IllegalArgumentException) {}
    }

    fun save() {
        try {
            dataFile.writeText(pretty.encodeToString(votes))
        } catch (_: IOException) {}
    }

    fun getVote(context: WsContext): Vote? {
        return handlersToVote[context.sessionId()]
    }

    fun sendToEveryone(message: Message) {
        for (context in handlers) {
            send(context, message)
        }
    }

    fun sendToVote(vote: Vote, message: Message) {
        for (context in handlers) {
            if(getVote(context) == vote) {
                send(context, message)
            }
        }
    }

    fun send(context: WsContext, message: Message) {
        context.send(Json.encodeToString(message))
    }
}

fun main() = BoL.main()