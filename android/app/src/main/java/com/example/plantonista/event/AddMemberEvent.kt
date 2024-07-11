package com.example.plantonista.event

import com.example.plantonista.distevents.EventData

class AddMemberEvent(
    val networkName: String,
    val author: String,
    val name: String,
    val email: String,
    private val createdAt: Long = System.currentTimeMillis() / 1000L,
): AppEvent {
    override fun toData() = EventData(
        author,
        createdAt,
        getType().toString(),
        networkName,
        mapOf("name" to name, "email" to email)
    )

    override fun getType() = AppEventType.AddMember

    override fun author() = author

    override fun getCreatedAt() = createdAt
}
