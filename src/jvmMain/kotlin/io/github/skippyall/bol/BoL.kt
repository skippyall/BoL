package io.github.skippyall.bol

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

object BoL {
    val logger: Logger = LoggerFactory.getLogger("BoL")
    var votes = mutableMapOf<String, ModifyableVote>()

    val pretty = Json {
        prettyPrint = true
    }

    val dataFile = File("data.json")

    @Volatile
    var waiting = false
    @Volatile
    var shouldStop = false

    val saveThread = Thread({
        try {
            while (true) {
                waiting = true
                Thread.sleep(15000)
                waiting = false
                save()

                Webserver.sendPing()

                if(shouldStop) {
                    break
                }
            }
        } catch (_: InterruptedException) {}
    }, "Save Thread")

    var port: Int? = null

    fun main(args: Array<String>) {
        val portIndex = args.indexOf("-p")
        if(portIndex != -1 && portIndex < args.size - 1) {
            port = args[portIndex + 1].toInt()
        }

        start()
        while(!readlnOrNull().equals("stop")) {}
        stop()
    }

    fun start() {
        load()
        Webserver.start()
        saveThread.start()
    }

    fun stop() {
        Webserver.stop()

        shouldStop = true
        if(waiting) {
            saveThread.interrupt()
        }

        save()
    }

    fun load() {
        try {
            val decodedVotes = Json.decodeFromString<Map<String, MutableMap<String, Int>>>(dataFile.readText(Charsets.UTF_8))
            votes = decodedVotes.mapValues { ModifyableVote(it.key, it.value) }.toMutableMap()
        }
        catch (_: IOException) {}
        catch (_: SerializationException) {}
        catch (_: IllegalArgumentException) {}
    }

    fun save() {
        try {
            dataFile.writeText(pretty.encodeToString<Map<String, Map<String, Int>>>(votes.mapValues { it.value.voteOptions }))
        } catch (_: IOException) {}
    }
}

fun main(args: Array<String>) = BoL.main(args)