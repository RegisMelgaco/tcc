package com.example.plantonista.distevents

data class NodeData(
    val email: String,
    val publicIP: String,
    val updatedAt: Long,
    val localIPs: List<String>?
)
