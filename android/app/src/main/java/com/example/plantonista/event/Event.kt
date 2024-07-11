package com.example.plantonista.event

import com.example.plantonista.distevents.Event
import com.example.plantonista.distevents.EventData
import com.example.plantonista.distevents.EventFactory


typealias AppEvent = Event<AppEventType>

enum class AppEventType {
    AddMember
}

class UnknownEventTypeException(val type: String): Exception()

class AppEventFactory: EventFactory<AppEventType> {
    override fun fromData(data: EventData): AppEvent = when(data.type) {
        AppEventType.AddMember.toString() -> AddMemberEvent(data.networkName, data.author, data.payload!!["name"] as String, data.payload["email"] as String, data.createdAt)
        else -> throw UnknownEventTypeException(data.type)
    }
}
