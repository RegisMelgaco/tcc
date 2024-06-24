package com.example.plantonista.distevents.sqlite

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.plantonista.distevents.NetworkData

@Entity("network")
data class NetworkEntity(
    @PrimaryKey
    val name: String,
    val secret: String,
    val updatedNodesAt: Long
) {
    fun toData() = NetworkData(name)
}
