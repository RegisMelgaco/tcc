package com.example.plantonista.distevents

data class EventData(
    val author: String,
    val createdAt: Long,
    val type: String,
    val payload: Map<String, Any>?
)