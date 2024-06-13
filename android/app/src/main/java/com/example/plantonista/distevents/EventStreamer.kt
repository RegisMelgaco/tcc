package com.example.plantonista.distevents

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteConstraintException
import android.os.SystemClock
import android.util.Log
import androidx.room.Room
import com.example.plantonista.distevents.sqlite.Database
import com.example.plantonista.distevents.sqlite.EventDao
import com.example.plantonista.distevents.sqlite.NodeDao
import com.example.plantonista.distevents.sqlite.toEntity
import com.example.plantonista.distevents.tcp.TCPClient
import com.example.plantonista.distevents.tcp.TCPServer
import com.example.plantonista.distevents.tracker.SyncInput
import com.example.plantonista.distevents.tracker.TrackerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException

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
    trackerHost: String,
    private val author: String
) {

    private val tracker: TrackerService = Retrofit.Builder()
        .baseUrl(trackerHost)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TrackerService::class.java)

    private val nodeDao: NodeDao
    private val eventDao: EventDao

    private val lastUpdateSharedPref: SharedPreferences

    private lateinit var localIPs: List<String>
    private var self: NodeData? = null


    init {
        val db = Room.databaseBuilder(context, Database::class.java, "dist_events").build()

        nodeDao = db.nodeDao()
        eventDao = db.eventDao()

        lastUpdateSharedPref = context.getSharedPreferences(LAST_NODE_UPDATE, Context.MODE_PRIVATE)

        try {
            val en = NetworkInterface.getNetworkInterfaces()
            val ips = mutableListOf<String>()

            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.getInetAddresses()

                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        inetAddress.getHostAddress()?.let { ips.add(it) }
                    }
                }

            }

            localIPs = ips
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
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

    suspend fun syncEvents(delayMs: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            while(true) {
                SystemClock.sleep(delayMs)

                val nodes = mutableListOf<NodeData>()

                try {
                    Log.d(TAG, "sync started")

                    val resp = tracker.sync(
                        SyncInput(
                            author,
                            localIPs,
                            lastUpdateSharedPref.getString(LAST_NODE_UPDATE, null)
                        )
                    )

                    Log.d(TAG, "got tracker resp: $resp")

                    val neighbors = resp.neighbors ?: listOf()

                    self = resp.self
                    nodes.addAll(neighbors)

                    val entities = neighbors.filter { it != self }.map { it.toEntity() }

                    Log.d(TAG, "storing: $entities")

                    nodeDao.deleteNodes(entities.map { it.node })

                    nodeDao.insertNodes(entities.map { it.node })

                    for (localIPs in entities.map { it.localIPs }) {
                        nodeDao.insertLocalIPs(localIPs)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "failed to sync nodes: $e")
                    nodes.addAll(
                        nodeDao.getAll().map { it.toData() }
                    )
                }

                val events = eventDao.getAll().map { it.toData() }

                for (node in nodes) {
                    if (self?.publicIP == node.publicIP) {
                        node.localIPs?.forEach { ip ->
                            syncWithIP(ip, events)
                        }

                        continue
                    }

                    syncWithIP(node.publicIP, events)
                }
            }
        }
    }

    private suspend fun syncWithIP(ip: String, events: List<EventData>) {
        Log.d(TAG, "syncing with $ip")

        try {
            val newEvents = TCPClient(TIMEOUT).sync(ip, TCPServer.PORT, events)

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