package io.github.skippyall.bol

fun main() {
    BoL.load()
    for(vote in BoL.votes.values) {
        vote.voteOptions.sortWith((Comparator.comparingInt<Vote.VoteOption> { teacher -> teacher.votes }).reversed())
        while(vote.voteOptions.size > 3) {
            vote.voteOptions.removeLast()
        }
    }
    BoL.save()
}