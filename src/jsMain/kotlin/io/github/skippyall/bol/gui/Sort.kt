package io.github.skippyall.bol.gui

import kotlinx.browser.document
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.Option

enum class Sort(val displayName: String, val comparator: Comparator<Pair<String, Int>>?) {
    INPUT_ORDER("Geändert", null),
    ABC("ABC", compareBy { it.first }),
    VOTES("Votes", compareByDescending { it.second });

    companion object {
        lateinit var sortSelect: HTMLSelectElement

        fun init() {
            sortSelect = document.getElementById("sort_type") as HTMLSelectElement
            for(sort in entries) {
                sortSelect.add(Option(sort.displayName, sort.name))
            }
            sortSelect.onchange = { updateSort() }
        }

        fun updateSort() {
            Buttons.sort = Sort.valueOf(sortSelect.value)
            Buttons.update()
        }
    }
}