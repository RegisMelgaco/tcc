package com.example.plantonista.distevents

import android.content.Context
import androidx.room.Room
import com.example.plantonista.distevents.sqlite.Database
import com.example.plantonista.distevents.sqlite.NetworkDao
import com.example.plantonista.distevents.sqlite.NetworkEntity
import com.example.plantonista.distevents.sqlite.NodeDao
import com.example.plantonista.distevents.tracker.CreateNetworkInput
import com.example.plantonista.distevents.tracker.TrackerService
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Tracker(
    context: Context,
    host: String,
) {
    private val client: TrackerService = Retrofit.Builder()
        .baseUrl(host)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TrackerService::class.java)

    private val networkDao: NetworkDao
    private val nodeDao: NodeDao

    init {
        val db = Room.databaseBuilder(context, Database::class.java, "dist_events").build()
        networkDao = db.networkDao()
        nodeDao = db.nodeDao()
    }

    suspend fun createNetwork(data: NetworkData): NetworkData {
        val secret = (1..20)
            .map { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }
            .joinToString("")

        try {
            client.createNetwork(CreateNetworkInput(secret, data.name))
        } catch (e: HttpException) {
            if (e.code() == 400) {
                throw InvalidNetworkNameException(data.name)
            }

            throw e
        }

        val now = System.currentTimeMillis() / 1000L

        val network = NetworkEntity(name = data.name, secret = secret, updatedNodesAt = now)

        networkDao.create(network)

        return network.toData()
    }

    fun getNetwork(name: String, user: String) = Network(client, networkDao, nodeDao, name, user)

    fun listNetworks() = networkDao.getAll().map { it.toData() }
}