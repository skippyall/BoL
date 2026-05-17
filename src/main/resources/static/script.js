/** @type {WebSocket} */var socket = null;
var vote = null;

/** @type {HTMLSelectElement}*/ var voteSelect = null
/** @type {HTMLElement}*/ var container = null
/** @type {HTMLInputElement} */ var nameInput = null

var sortType = "abc"
/** @type {HTMLSelectElement}*/ var sortTypeSelect = null

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
    initSortSelect()
    initEditMode()
    container = document.getElementById("teacher_buttons")
    nameInput = document.getElementById("name")
}

function reset() {
    resetButtons()
    resetVoteSelect()
}

function changeVote(newVote) {
    vote = newVote

    socket.send(JSON.stringify({
        "type": "joinVote",
        "voteName": newVote
    }))
    resetButtons()
}

function handleDataMessage(data) {
    resetButtons()

    for(teacher of data.vote.voteOptions) {
        updateTeacher(teacher)
    }
}

function handleUpdateMessage(data) {
    updateTeacher(data.voteOption)
}

function handleListVotesMessage(data) {
    resetVoteSelect()
    voteSelect.add(new Option("Wähle eine Abstimmung...", ""))
    for(vote of data.votes) {
        voteSelect.add(new Option(vote, vote))
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

function resetVoteSelect() {
    while(voteSelect.options.length > 0) {
        voteSelect.options.remove(0)
    }
}

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
        button = document.createElement("button")
        button.setAttribute("class", "teacher_button")
        button.setAttribute("teacher", name)
        

        button.onclick = () => onButtonClick(name)

        paragraph = document.createElement("p")

        button.appendChild(paragraph)

    } else {
        paragraph = button.firstChild
        button.remove()
    }

    button.setAttribute("votes", votes)
    insertButton(button, container.lastChild)
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
    if(editButtons) {
        onEditButton(teacher)
    } else {
        socket.send(JSON.stringify({
            "type": "click",
            "name": teacher
        }))
    }
}

function sortButtons() {
    const size = container.children.length
    for(var i = 1; i < size; i++) {
        var button = container.children[i]
        var init = button.previousElementSibling

        button.remove()
        insertButton(button, init)
    }
}

function insertButton(button, initButton) {
    var compareButton = initButton
    while(compareButton && compare(compareButton, button)) {
        compareButton = compareButton.previousElementSibling
    }

    if(compareButton) {
        compareButton.after(button)
    } else {
        if(container.firstChild) {
            container.firstChild.before(button)
        } else {
            container.appendChild(button)
        }
    }
}

function compare(button1, button2) {
    if(sortType == "abc") {
        return button1.getAttribute("teacher").localeCompare(button2.getAttribute("teacher")) > 0
    }
    if(sortType == "votes") {
        return Number(button1.getAttribute("votes")) < Number(button2.getAttribute("votes"))
    }
}

function initSortSelect() {
    sortTypeSelect = document.getElementById("sort_type")
    
    sortTypeSelect.addEventListener("change", onSortChange)
}

function onSortChange() {
    sortType = sortTypeSelect.value

    sortButtons()
}

window.onload = init

connect()
setInterval(checkConnected, 5000)