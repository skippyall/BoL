package io.github.skippyall.bol

import java.io.File
import java.io.IOException

fun main() {
    BoL.load()
    val output = mutableMapOf<String, Map<String, Int>>()
    for(vote in BoL.votes) {
        val options = vote.value.voteOptions.entries
            .sortedWith(Comparator.comparingInt<Map.Entry<String, Int>> { it.value }.reversed())
            .associate(Map.Entry<String, Int>::toPair)
        output[vote.key] = options
    }

    try {
        File("sorted.json").writeText(BoL.pretty.encodeToString<Map<String, Map<String, Int>>>(output))
    } catch (_: IOException) {}
}