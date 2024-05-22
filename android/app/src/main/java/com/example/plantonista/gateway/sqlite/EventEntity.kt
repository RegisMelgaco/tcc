package com.example.plantonista.gateway.sqlite

import androidx.room.Entity

@Entity("event", primaryKeys = ["author", "createdAt", "type"])
data class EventEntity(
    val author: String,
    val createdAt: Long,
    val type: String,
    val payload: String,
)
