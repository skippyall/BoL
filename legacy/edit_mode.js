var editButtons = false
var currentEditButton = null
/** @type {HTMLButtonElement} */ var editToggleButton = null
/** @type {HTMLDivElement} */ var editWindow = null
/** @type {HTMLDivElement} */ var mainContent = null

var editName = null
var editCount = null
var deleteButton = null

function initEditMode() {
    editToggleButton = document.getElementById("edit_toggle_button")
    editWindow = document.getElementById("edit_button")
    mainContent = document.getElementById("main_content")
    editName = document.getElementById("edit_name")
    editCount = document.getElementById("edit_count")
    deleteButton = document.getElementById("delete_button")
}

function onEditToggle() {
    editButtons = !editButtons
    if(editButtons) {
        editToggleButton.innerText = "Zum Zählmodus"
        document.body.classList.add("edit_mode")
    } else {
        editToggleButton.innerText = "Zum Anpassungsmodus"
        document.body.classList.remove("edit_mode")
    }
}

function onEditButton(teacher) {
    currentEditButton = teacher
    editWindow.classList.add("edit_button_show")
    editWindow.classList.remove("edit_button_hide")
    mainContent.classList.add("blur")

    editName.value = teacher
}

function hideEditButton() {
    editWindow.classList.remove("edit_button_show")
    editWindow.classList.add("edit_button_hide")
    mainContent.classList.remove("blur")
}