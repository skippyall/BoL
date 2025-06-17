package io.github.skippyall.bol

import io.github.skippyall.bol.message.DataMessage
import io.github.skippyall.bol.message.Message
import io.javalin.Javalin
import io.javalin.http.servlet.splitKeyValueStringAndGroupByKey
import io.javalin.http.staticfiles.Location
import io.javalin.websocket.WsContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

object BoL {
    val logger = LoggerFactory.getLogger("BoL")
    var votes: MutableMap<String,Vote> = mutableMapOf()
    var handlers = mutableSetOf<WsContext>()
    var handlersToVote = mutableMapOf<WsContext,Vote>()

    val pretty = Json {
        prettyPrint = true
    }

    val dataFile = File("data.json")

    val javalin: Javalin = Javalin.create { config ->
        config.staticFiles.add { staticFileConfig ->
            staticFileConfig.location = Location.CLASSPATH
            staticFileConfig.directory = "static"
        }
    }
        .get("/") {
            it.redirect("/vote.html")
        }
        .get("/data") {
            it.json(Json.encodeToString(vote))
        }
        .post("/add_button_confirm") {
            val arguments = splitKeyValueStringAndGroupByKey(it.body(), "UTF-8")
            val name = arguments["name"]?.get(0)
            if(name != null) {
                if(vote.teachers.firstOrNull{it.name == name} == null) {
                    vote.addTeacherAndUpdate(Vote.Teacher(name))
                    it.redirect("/add_button_success.html")
                } else {
                    it.redirect("/add_button_error.html")
                }
            }
        }
        .ws("/websocket") {
            it.onMessage {
                Json.decodeFromString<Message>(it.message()).onReceive()
            }

            it.onConnect { handler ->
                handler.send(Json.encodeToString(DataMessage(vote) as Message))
                handlers.add(handler)
            }

            it.onClose { handler ->
                handlers.remove(handler)
            }
        }

    val saveThread = Thread {
        try {
            while (true) {
                Thread.sleep(15000)
                try {
                    dataFile.writeText(pretty.encodeToString(vote))

                    for (context in handlers) {
                        context.sendPing()
                    }
                } catch (_: IOException) {
                }
            }
        } catch (_: InterruptedException) {}
    }

    @JvmStatic
    fun main() {
        start()
        while(!readLine().equals("stop")) {}
        stop()
    }

    fun start() {
        try {
            vote = Json.decodeFromString<Vote>(dataFile.readText(Charsets.UTF_8))
        }
        catch (_: IOException) {}
        catch (_: SerializationException) {}
        catch (_: IllegalArgumentException) {}

        javalin.start(25574)
        saveThread.start()
    }

    fun stop() {
        for (context in handlers) {
            context.closeSession()
        }

        javalin.stop()
        saveThread.interrupt()

        try {
            dataFile.writeText(pretty.encodeToString(vote))
        } catch (_: IOException) {}
    }

    fun sendToEveryone(message: Message) {
        for (remote in handlers) {
            send(remote, message)
        }
    }

    fun sendToVote(vote: Vote, message: Message) {
        for (remote in handlers) {
            
        }
    }

    fun send(remote: WsContext, message: Message) {
        remote.send(Json.encodeToString(message))
    }
}

fun main() = BoL.main()