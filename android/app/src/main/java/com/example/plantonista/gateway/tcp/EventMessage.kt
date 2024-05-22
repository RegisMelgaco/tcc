package com.example.plantonista.gateway.tcp

import com.example.plantonista.state.Event
import com.example.plantonista.state.GivePositionEvent

data class EventMessage(
    val author: String,
    val createdAt: Long,
    val type: Event.Type,
    val payload: Map<String, Any>?
) {
    fun toEntity(): Event = when(type) {
        Event.Type.GivePosition -> GivePositionEvent(author, createdAt)
    }
}

fun Event.toMsg() = when(type()) {
    Event.Type.GivePosition -> EventMessage(author, createdAt, Event.Type.GivePosition, null)
}
