package com.example.plantonista.event

import com.example.plantonista.distevents.EventData

class AddShiftEvent(
    val networkName: String,
    val author: String,
    val memberEmail: String,
    val start: Long,
    val durationMin: Int,
    private val createdAt: Long = System.currentTimeMillis() / 1000L,
): AppEvent {
    override fun toData() = EventData(
        author,
        createdAt,
        getType().toString(),
        networkName,
        mapOf("memberEmail" to memberEmail, "start" to start, "durationMin" to durationMin)
    )

    override fun getType() = AppEventType.AddShift

    override fun author() = author

    override fun getCreatedAt() = createdAt
}

