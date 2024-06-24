package com.example.plantonista.distevents.tcp

data class SyncEventsRequest(
    val heads: List<EventStreamHead>
)

data class EventStreamHead(
    val author: String,
    val headDateTime: Long,
)
