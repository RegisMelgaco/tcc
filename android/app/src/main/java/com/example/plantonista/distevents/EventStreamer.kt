package com.example.plantonista.distevents

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.SystemClock
import android.util.Log
import androidx.room.Room
import com.example.plantonista.distevents.sqlite.Database
import com.example.plantonista.distevents.sqlite.EventDao
import com.example.plantonista.distevents.sqlite.toEntity
import com.example.plantonista.distevents.tcp.SyncEventsRequest
import com.example.plantonista.distevents.tcp.TCPClient
import com.example.plantonista.distevents.tcp.TCPServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface Event {
    fun toData(): EventData
}

interface EventFactory<E: Event> {
    fun fromData(data: EventData): E
}

class EventStreamer<E: Event>(
    context: Context,
    private val factory: EventFactory<E>,
    private val handler: EventHandler<E>,
) {
    private val eventDao: EventDao

    init {
        val db = Room.databaseBuilder(context, Database::class.java, "dist_events").build()

        eventDao = db.eventDao()
    }

    fun add(event: E) {
        Log.d(TAG, "added event: ${event.toData()}")

        CoroutineScope(Dispatchers.Main).launch {
            handler(event)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                eventDao.insertAll(listOf(event.toData().toEntity()))
            } catch (e: SQLiteConstraintException) {
                Log.e(TAG, "failed to store event: $e")
            }
        }
    }

    fun replayEvents(since: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val events = eventDao.getWhereNewer(since)

            CoroutineScope(Dispatchers.Main).launch {
                for (event in events) {
                    handler(factory.fromData(event.toData()))
                }
            }

            Log.d(TAG, "replayed $events")
        }
    }

    suspend fun syncEvents(network: Network,delayMs: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            while(true) {
                SystemClock.sleep(delayMs)

                network
                    .getNeighboursAddresses()
                    .forEach { syncWithIP(it) }
            }
        }
    }

    private suspend fun syncWithIP(ip: String) {
        Log.d(TAG, "syncing with $ip")

        try {
            val ownHeads = eventDao.getEventStreamHead()
            val request = SyncEventsRequest(ownHeads.map { it.toRequest() })
            val getLocalEvents =  { syncEventsRequest: SyncEventsRequest ->
                val resp = mutableListOf<EventData>()
                for (head in syncEventsRequest.heads) {
                    resp.addAll(
                        eventDao
                            .getEventsByEventStreamHead(head.headDateTime, head.author)
                            .map { it.toData() }
                    )
                }

                resp
            }

            val newEvents = TCPClient(eventDao, TIMEOUT, getLocalEvents).sync(ip, TCPServer.PORT, request)

            Log.d(TAG, "new events $newEvents")

            for (ne in newEvents) {
                add(factory.fromData(ne))
            }
        } catch (e: Exception) {
            Log.e(TAG, "failed to sync with node: $e")
        }
    }

    companion object {
        fun getEventService() = TCPServer::class.java
        const val TIMEOUT = 5000L
        private const val LAST_NODE_UPDATE = "last_node_update"
        private val TAG = EventStreamer::class.qualifiedName
    }
}

typealias EventHandler<T> = (T) -> Unit