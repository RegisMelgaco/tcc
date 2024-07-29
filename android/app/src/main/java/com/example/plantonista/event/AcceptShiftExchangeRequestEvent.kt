package com.example.plantonista.event

import com.example.plantonista.distevents.EventData

class AcceptShiftExchangeRequestEvent(
    val networkName: String,
    val author: String,
    val memberEmail: String,
    val shiftStartUnix: Long,
    private val createdAt: Long = System.currentTimeMillis() / 1000L,
): AppEvent {
    override fun toData() = EventData(
        author,
        createdAt,
        getType().toString(),
        networkName,
        mapOf("shiftStart" to shiftStartUnix)
    )

    override fun getType() = AppEventType.AcceptShiftExchangeRequest

    override fun author() = author

    override fun getCreatedAt() = createdAt
}