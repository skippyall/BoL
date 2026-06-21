class VoteOption {
    /** @type {String} */ name
    /** @type {Number} */ votes
}

class Vote {
    /** @type {Array<VoteOption>} */ voteOptions
}

/** @type {String[]} */ var voteNames = []
/** @type {String} */ var currentVoteName = null
/** @type {Vote} */ var vote = null