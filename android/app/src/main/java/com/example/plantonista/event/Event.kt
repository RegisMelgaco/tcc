package com.example.plantonista.event

import com.example.plantonista.distevents.Event
import com.example.plantonista.distevents.EventData
import com.example.plantonista.distevents.EventFactory


interface PlantonistaEvent: Event {
    fun type(): PlantonistaEventType
}

enum class PlantonistaEventType(val str: String) {
}

class UnknownEventTypeException(val type: String): Exception()

class EventFactory: EventFactory<PlantonistaEvent> {
    override fun fromData(data: EventData): PlantonistaEvent = when(data.type) {
        else -> throw UnknownEventTypeException(data.type)
    }
}
