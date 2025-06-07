console.log("Hello World")

const socket = new WebSocket(window.location.origin + "/websocket")
socket.onmessage = (event) => {
    var data = JSON.parse(event.data)
    console.log(data)

    if(data.type == "data") {
        handleDataMessage(data.vote)
    } else if (data.type == "update") {
        handleUpdateMessage(data.teacher)
    }
}

function handleDataMessage(data) {
    for(teacher of data.teachers) {
        updateTeacher(teacher)
    }
}

function handleUpdateMessage(teacher) {
    updateTeacher(teacher)
}

async function updateAllButtons() {
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
        var container = document.getElementById("teacher_buttons")
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

function onButtonClick(teacher) {
    socket.send(JSON.stringify({
        "type": "click",
        "name": teacher
    }))
}
