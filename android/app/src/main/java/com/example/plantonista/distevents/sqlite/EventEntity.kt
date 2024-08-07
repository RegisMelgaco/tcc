package com.example.plantonista.distevents.sqlite

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.example.plantonista.distevents.EventData
import com.example.plantonista.distevents.IndexedEventData
import com.example.plantonista.distevents.tcp.EventStreamHead
import com.google.gson.Gson

@Entity("event", primaryKeys = ["networkName", "author", "pos"])
data class EventEntity(
    val author: String,
    val pos: Int,
    val createdAt: Long,
    val type: String,
    val networkName: String,
    val payload: String,
) {
    fun toIndexedData(): IndexedEventData {
        var payload: Map<String, Any>? = null
        if (this.payload != "") {
            payload = Gson().fromJson<Map<String, Any>>(this.payload, Map::class.java)
        }

        return IndexedEventData(EventData(author, createdAt, type, networkName, payload), pos)
    }
}

fun IndexedEventData.toEntity(): EventEntity {
    val payload = Gson().toJson(this.data.payload)

    return EventEntity(data.author, index, data.createdAt, data.type, data.networkName, payload)
}

data class EventStreamHead(
    @ColumnInfo("networkName") val networkName: String,
    @ColumnInfo("author") val author: String,
    @ColumnInfo("pos") var index: Int,
) {
    fun toRequest() = EventStreamHead(networkName, author, index)
}
