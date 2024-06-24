package com.example.plantonista.distevents.tracker

import com.example.plantonista.distevents.NodeData
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

data class SyncInput(
    val email: String,
    val localIPs: List<String>,
    val updatedAt: Long,
    val networkSecret: String,
)

data class SyncOutput(
    val self: NodeData,
    val neighbors: List<NodeData>?
)

interface TrackerService {
    @PUT("/v1/sync")
    suspend fun sync(
        @Body body: SyncInput
    ): SyncOutput

    @POST("/v1/network")
    suspend fun createNetwork(
        @Body body: CreateNetworkInput
    )
}

data class CreateNetworkInput(
    val secret: String,
    val name: String
)