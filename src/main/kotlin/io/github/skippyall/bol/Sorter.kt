package io.github.skippyall.bol

fun main() {
    BoL.load()
    for(vote in BoL.votes.values) {
        vote.teachers.sortWith((Comparator.comparingInt<Vote.Teacher> { teacher -> teacher.votes }).reversed())
        while(vote.teachers.size > 3) {
            vote.teachers.removeLast()
        }
    }
    BoL.save()
}