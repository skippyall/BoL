package io.github.skippyall.bol

import io.github.skippyall.bol.message.DataMessage
import io.github.skippyall.bol.message.Message
import io.javalin.Javalin
import io.javalin.http.servlet.splitKeyValueStringAndGroupByKey
import io.javalin.http.staticfiles.Location
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.eclipse.jetty.websocket.api.RemoteEndpoint
import java.io.File
import java.io.IOException

object BoL {
    var vote: Vote = Vote()
    var handlers = mutableSetOf<RemoteEndpoint>()

    fun main() {
        val dataFile = File("data.json")

        try {
            vote = Json.decodeFromString<Vote>(dataFile.readText(Charsets.UTF_8))
        }
        catch (_: IOException) {}
        catch (_: SerializationException) {}
        catch (_: IllegalArgumentException) {}

        val write = Json {
            prettyPrint = true
        }

        val javalin = Javalin.create { config ->
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
                    handlers.add(handler.session.remote)
                }

                it.onClose { handler ->
                    handlers.remove(handler.session.remote)
                }
            }
            .start(1234)

        val thread2 = Thread {
            try {
                while (true) {
                    Thread.sleep(10000)
                    try {
                        dataFile.writeText(write.encodeToString(vote))
                        println("saved")
                    } catch (_: IOException) {
                    }
                }
            } catch (_: InterruptedException) {}
        }

        thread2.start()


        while(!readLine().equals("stop")) { }

        javalin.stop()

        thread2.interrupt()

        try {
            dataFile.writeText(write.encodeToString(vote))
        } catch (_: IOException) {}
    }

    fun sendToEveryone(message: Message) {
        for (remote in handlers) {
            remote.sendString(Json.encodeToString(message))
        }
    }
}

fun main() = BoL.main()