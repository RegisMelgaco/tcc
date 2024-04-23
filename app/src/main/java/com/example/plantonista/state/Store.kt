package com.example.plantonista.state

class Store {
    private val events = arrayListOf<Event>()

    private fun appendEvent(event: Event) {
        events.add(event)
    }
}