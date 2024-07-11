package com.example.plantonista.distevents

data class EventData(
    val author: String,
    val createdAt: Long,
    val type: String,
    val networkName: String,
    val payload: Map<String, Any>?
)

data class IndexedEventData(
    val data: EventData,
    val index: Int
)
