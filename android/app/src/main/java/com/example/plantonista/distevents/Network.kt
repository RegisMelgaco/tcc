package com.example.plantonista.distevents

import android.util.Log
import com.example.plantonista.distevents.sqlite.NetworkDao
import com.example.plantonista.distevents.sqlite.NodeDao
import com.example.plantonista.distevents.sqlite.toEntity
import com.example.plantonista.distevents.tracker.SyncInput
import com.example.plantonista.distevents.tracker.TrackerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL

data class NetworkData(
    val name: String,
)

class Network(
    private val client: TrackerService,
    private val networkDao: NetworkDao,
    private val nodeDao: NodeDao,
    val name: String,
    val user: String,
) {
    suspend fun getNeighboursAddresses(): List<String> {
        val nodes = mutableListOf<NodeData>()
        var self: NodeData

        val en = withContext(Dispatchers.IO) {
            NetworkInterface.getNetworkInterfaces()
        }
        val localIPs = mutableListOf<String>()

        while (en.hasMoreElements()) {
            val intf = en.nextElement()
            val enumIpAddr = intf.getInetAddresses()

            while (enumIpAddr.hasMoreElements()) {
                val inetAddress = enumIpAddr.nextElement()
                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                    inetAddress.getHostAddress()?.let { localIPs.add(it) }
                }
            }

        }

        try {
            Log.d(TAG, "sync nodes started")

            val network = networkDao.getByName(name)

            val resp = client.sync(
                SyncInput(
                    user,
                    localIPs,
                    network.updatedNodesAt,
                    network.secret
                )
            )

            Log.d(TAG, "sync nodes response: $resp")

            val neighbors = resp.neighbors ?: listOf()

            self = resp.self
            nodes.addAll(neighbors)

            val entities = neighbors.filter { it != self }.map { it.toEntity() }

            Log.d(TAG, "storing nodes: $entities")

            nodeDao.deleteNodes(entities.map { it.node })

            nodeDao.insertNodes(entities.map { it.node })

            for (neighborLocalIP in entities.map { it.localIPs }) {
                nodeDao.insertLocalIPs(neighborLocalIP)
            }
        } catch (e: Exception) {
            Log.e(TAG, "failed to sync nodes: $e")
            nodes.addAll(
                nodeDao.getAll().map { it.toData() }
            )

            val buff = ByteArray(1024)
            val read =
                withContext(Dispatchers.IO) {
                    val httpsURLConnection = URL("https://api.ipify.org").openConnection()
                    val iStream = httpsURLConnection.getInputStream()
                    iStream.read(buff)
                }

            val now = System.currentTimeMillis() / 1000L

            self = NodeData(user, String(buff,0, read), now, localIPs)
        }

        val addresses = mutableListOf<String>()

        for (node in nodes) {
            if (self.publicIP == node.publicIP) {
                node.localIPs?.let { addresses.addAll(it) }

                continue
            }

            addresses.add(node.publicIP)
        }

        return addresses
    }

    companion object {
        private val TAG = Network::class.simpleName
        private const val LAST_NODE_UPDATE = "LAST_NODE_UPDATE"
    }
}
