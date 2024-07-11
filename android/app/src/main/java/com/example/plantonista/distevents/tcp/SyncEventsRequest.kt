package com.example.plantonista.distevents.tcp

data class SyncEventsRequest(
    val networkName: String,
    val heads: List<EventStreamHead>
)

data class EventStreamHead(
    val networkName: String,
    val author: String,
    var index: Int,
)
