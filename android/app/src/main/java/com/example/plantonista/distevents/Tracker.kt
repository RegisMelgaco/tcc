package com.example.plantonista.distevents

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.room.Room
import com.example.plantonista.distevents.sqlite.Database
import com.example.plantonista.distevents.sqlite.EventDao
import com.example.plantonista.distevents.sqlite.NetworkDao
import com.example.plantonista.distevents.sqlite.NetworkEntity
import com.example.plantonista.distevents.sqlite.NodeDao
import com.example.plantonista.distevents.sqlite.getNewEvents
import com.example.plantonista.distevents.sqlite.toEntity
import com.example.plantonista.distevents.tcp.EventStreamHead
import com.example.plantonista.distevents.tcp.SyncEventsRequest
import com.example.plantonista.distevents.tcp.TCPClient
import com.example.plantonista.distevents.tcp.TCPServer
import com.example.plantonista.distevents.tracker.CreateNetworkInput
import com.example.plantonista.distevents.tracker.TrackerService
import kotlinx.coroutines.runBlocking
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
    private val eventDao: EventDao

    init {
        val db = Room.databaseBuilder(context, Database::class.java, "dist_events").build()
        networkDao = db.networkDao()
        nodeDao = db.nodeDao()
        eventDao = db.eventDao()
    }

    suspend fun createNetwork(name: String) {
        runBlocking {
            val secret = (1..20)
                .map { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }
                .joinToString("")

            try {
                client.createNetwork(CreateNetworkInput(secret, name))
            } catch (e: HttpException) {
                if (e.code() == 400) {
                    throw InvalidNetworkNameException(name)
                }

                throw e
            }

            val now = System.currentTimeMillis() / 1000L

            val network = NetworkEntity(name = name, secret = secret, updatedNodesAt = now)

            networkDao.create(network)
        }
    }

    fun saveNetwork(name: String, secret: String) {
        networkDao.create(NetworkEntity(name, secret, 0))
    }

    fun getNetwork(name: String, author: String) = Network(client, networkDao, nodeDao, name, author)

    fun listNetworks() = networkDao.getAll().map { it.toData() }

    suspend fun syncEvents(username: String) {
        for (netData in listNetworks()) {
            val net = getNetwork(netData.name, username)

            net
                .getNeighboursAddresses()
                .forEach { syncWithIP(netData.name, it) }
        }
    }

    private suspend fun syncWithIP(networkName: String, ip: String) {
        Log.d(TAG, "syncing with $ip")

        try {
            val ownHeads = eventDao.getEventStreamHead(networkName)
            val request = SyncEventsRequest(networkName, ownHeads.map { it.toRequest() })
            val getLocalEvents =  { syncEventsRequest: SyncEventsRequest ->
                getNewEvents(eventDao, syncEventsRequest)
            }

            val newEvents = TCPClient(getLocalEvents).sync(ip, TCPServer.PORT, request)

            Log.d(TAG, "new events $newEvents")

            eventDao.insertAll(newEvents.map { it.toEntity() })
        } catch (e: Exception) {
            Log.e(TAG, "failed to sync with node: $e")
        }
    }

    companion object {
        private val TAG = Tracker::class.simpleName
    }
}