/** @type {WebSocket} */var socket = null;


const messageHandlers = {
    "listVotes": handleListVotesMessage,
    "data": handleDataMessage,
    "update": handleUpdateMessage,
    "confirm": handleConfirmMessage
}

function connect() {
    socket = new WebSocket(window.location.origin + "/websocket")
    socket.onopen = () => {
        console.log("onopen")
        if(currentVoteName != null) {
            socket.send(JSON.stringify({
                "type": "joinVote",
                "voteName": currentVoteName
            }))
        }
    }
    

    socket.onmessage = (event) => {
        var data = JSON.parse(event.data)
        console.log(data)

        messageHandlers[data.type](data)
    }

    socket.onclose = onLoseConnection
}

function checkConnected() {
    if(socket.readyState == WebSocket.CLOSED || socket.readyState == WebSocket.CLOSING) {
        onLoseConnection()
    }
}

function onLoseConnection() {
    reset()
    connect()
}

function reset() {
    resetUi()
}

function changeVote(newVote) {
    currentVoteName = newVote

    socket.send(JSON.stringify({
        "type": "joinVote",
        "voteName": newVote
    }))
    resetButtons()
}

function handleDataMessage(data) {
    vote = data

    resetButtons()

    for(teacher of data.vote.voteOptions) {
        updateTeacherButton(teacher)
    }
}

function handleUpdateMessage(data) {
    vote.voteOptions

    updateTeacherButton(data.voteOption)
}

function handleListVotesMessage(data) {
    resetVoteSelect()
    voteSelect.add(new Option("Wähle eine Abstimmung...", ""))
    for(currentVoteName of data.votes) {
        voteSelect.add(new Option(currentVoteName, currentVoteName))
    }
}

function handleConfirmMessage(data) {
    /** @type {HTMLButtonElement}*/ const button = findButton(data.voteOption)
    button.classList.add("confirm_button")
    setTimeout(() => button.classList.remove("confirm_button"), 500)
}

function initVoteSelect() {
    voteSelect = document.getElementById("vote_select")
    voteSelect.addEventListener("change", onVoteSelect)
}

function onVoteSelect() {
    const value = voteSelect.selectedOptions[0].value
    console.log("selected "+ value)
    if(value != "") {
        changeVote(value)
    }
    
}

function addButton() {
    socket.send(JSON.stringify({
        "type": "addButton",
        "name": nameInput.value
    }))
}

function onButtonClick(teacher) {
    if(editButtons) {
        onEditButton(teacher)
    } else {
        socket.send(JSON.stringify({
            "type": "click",
            "name": teacher
        }))
    }
}

connect()
setInterval(checkConnected, 5000)