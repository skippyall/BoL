/** @type {WebSocket} */var socket = null;
var vote = null;

/** @type {HTMLSelectElement}*/ var voteSelect = null
/** @type {HTMLElement}*/ var container = null
/** @type {HTMLInputElement} */ var nameInput = null

const messageHandlers = {
    "listVotes": handleListVotesMessage,
    "data": handleDataMessage,
    "update": handleUpdateMessage,
    "confirm": handleConfirmMessage
}

function connect() {
    socket = new WebSocket(window.location.origin + "/websocket")
    socket.onopen = () => {
        if(vote != null) {
            socket.send(JSON.stringify({
                "type": "joinVote",
                "voteName": vote
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

function init() {
    initVoteSelect()
    container = document.getElementById("teacher_buttons")
    nameInput = document.getElementById("name")
}

function reset() {
    resetButtons()
    resetVoteSelect()
}

function changeVote(newVote) {
    if(vote == newVote) {
        return
    }

    vote = newVote

    socket.send(JSON.stringify({
        "type": "joinVote",
        "voteName": newVote
    }))
    resetButtons()
}

function handleDataMessage(data) {
    resetButtons()

    for(teacher of data.vote.teachers) {
        updateTeacher(teacher)
    }
}

function handleUpdateMessage(data) {
    updateTeacher(data.teacher)
}

function handleListVotesMessage(data) {
    resetVoteSelect()
    voteSelect.add(new Option("Wähle eine Abstimmung...", ""))
    for(vote of data.votes) {
        voteSelect.add(new Option(vote, vote))
    }
}

function handleConfirmMessage(data) {
    /** @type {HTMLButtonElement}*/ const button = findButton(data.teacher)
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

function resetVoteSelect() {
    while(voteSelect.options.length > 0) {
        voteSelect.options.remove(0)
    }
}

/*async function updateAllButtons() {
    var response = await fetch(window.location.origin + "/data")
    if(response.ok) {
        var data = await response.json()
        
        var container = document.getElementById("teacher_buttons")
        while (container.firstChild) {
            container.removeChild(container.firstChild);
        }

        for(teacher of data.teachers) {
            updateTeacher(teacher)
        }
    }
}*/

function resetButtons() {
    while (container.firstChild) {
        container.removeChild(container.firstChild);
    }
}

function updateTeacher(teacher) {
    const name = teacher.name
    const votes = teacher.votes

    var button = findButton(name)
    var paragraph
    if(!button) {
        var button = document.createElement("button")
        button.setAttribute("class", "teacher_button")
        button.setAttribute("teacher", name)

        button.onclick = () => onButtonClick(name)

        paragraph = document.createElement("p")

        button.appendChild(paragraph)
        container.appendChild(button)

    } else {
        paragraph = button.firstChild
    }

    paragraph.innerText = name + " " + votes
}

function findButton(teacher) {
    var buttons = document.getElementsByClassName("teacher_button")
    for(var currentButton of buttons) {
        if(currentButton.getAttribute("teacher") == teacher) {
            return currentButton
        }
    }
}

function addButton() {
    socket.send(JSON.stringify({
        "type": "addButton",
        "name": nameInput.value
    }))
}

function onButtonClick(teacher) {
    socket.send(JSON.stringify({
        "type": "click",
        "name": teacher
    }))
}

window.onload = init

connect()
setInterval(checkConnected, 5000)