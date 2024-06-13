package com.example.plantonista

import com.example.plantonista.distevents.Event
import com.example.plantonista.distevents.EventData
import com.example.plantonista.distevents.EventFactory



interface MathEvent: Event {
    fun type(): MathEventType
}

enum class MathEventType(val str: String) {
    Add("add")
}

class UnknownEventTypeException(val type: String): Exception()

class EventFactory: EventFactory<MathEvent> {
    override fun fromData(data: EventData): MathEvent = when(data.type) {
        MathEventType.Add.str -> {
            AddEvent((data.payload?.get("value") as Double).toInt(), data.createdAt, data.author)
        }
        else -> throw UnknownEventTypeException(data.type)
    }
}

class AddEvent(
    val value: Int,
    val createdAt: Long,
    val author: String
): MathEvent {
    override fun type() = MathEventType.Add

    override fun toData() = EventData(author, createdAt, TYPE, mapOf("value" to value))

    companion object {
        private const val TYPE = "add"
    }
}