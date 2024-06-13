package com.example.plantonista.distevents.sqlite

import androidx.room.Entity
import com.example.plantonista.distevents.EventData
import com.google.gson.Gson

@Entity("event", primaryKeys = ["author", "createdAt", "type"])
data class EventEntity(
    val author: String,
    val createdAt: Long,
    val type: String,
    val payload: String,
) {
    fun toData(): EventData {
        var payload: Map<String, Any>? = null
        if (this.payload != "") {
            payload = Gson().fromJson<Map<String, Any>>(this.payload, Map::class.java)
        }

        return EventData(author, createdAt, type, payload)
    }
}

fun EventData.toEntity(): EventEntity {
    val payload = Gson().toJson(this.payload)

    return EventEntity(author, createdAt, type, payload)
}
