/** @type {HTMLSelectElement}*/ var voteSelect = null
/** @type {HTMLElement}*/ var container = null
/** @type {HTMLInputElement} */ var nameInput = null

var sortType = "abc"
/** @type {HTMLSelectElement}*/ var sortTypeSelect = null


function initUi() {
    initVoteSelect()
    initSortSelect()
    initEditMode()
    container = document.getElementById("teacher_buttons")
    nameInput = document.getElementById("name")
}

function resetUi() {
    resetButtons()
    resetVoteSelect()
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

function updateTeacherButton(teacher) {
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

window.onload = initUi