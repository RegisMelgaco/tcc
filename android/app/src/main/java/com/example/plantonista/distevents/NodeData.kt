package com.example.plantonista.distevents

data class NodeData(
    val email: String,
    val publicIP: String,
    val updatedAt: String,
    val localIPs: List<String>?
)
