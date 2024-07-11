package com.example.plantonista.distevents.tcp

import android.util.Log
import com.example.plantonista.distevents.IndexedEventData
import com.google.gson.Gson
import kotlinx.coroutines.withTimeout
import java.io.PrintStream
import java.net.Socket
import java.util.Scanner


class TCPClient(
    private val getLocalEvents: (SyncEventsRequest) -> List<IndexedEventData>
) {
    private val gson = Gson()

    suspend fun sync(address: String, port: Int, request: SyncEventsRequest): List<IndexedEventData> {
        var newEvents = arrayOf<IndexedEventData>()

        withTimeout(TIMEOUT_MS) {
            Log.d(TAG, "sync started")

            try {
                val socket = Socket(address, port)
                val input = Scanner(socket.getInputStream())
                val output = PrintStream(socket.getOutputStream())

                output.println(gson.toJson(request))

                Log.d(TAG, "sent")

                val peerSyncRequest = gson.fromJson(input.nextLine(), SyncEventsRequest::class.java)

                Log.d(TAG, "received request: $peerSyncRequest")

                output.println(gson.toJson(
                    getLocalEvents(peerSyncRequest),
                ))

                newEvents = gson.fromJson(input.nextLine(), Array<IndexedEventData>::class.java)

                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return newEvents.toList()
    }

    companion object {
        private val TAG = TCPClient::class.java.simpleName
        private const val TIMEOUT_MS = 5000L
    }
}