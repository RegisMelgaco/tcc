package com.example.plantonista.event

import com.example.plantonista.distevents.EventData

class AddAdminEvent(
    val networkName: String,
    val author: String,
    val email: String,
    private val createdAt: Long = System.currentTimeMillis() / 1000L,
): AppEvent {
    override fun toData() = EventData(
        author,
        createdAt,
        getType().toString(),
        networkName,
        mapOf("email" to email)
    )

    override fun getType() = AppEventType.AddAdmin

    override fun author() = author

    override fun getCreatedAt() = createdAt
}
