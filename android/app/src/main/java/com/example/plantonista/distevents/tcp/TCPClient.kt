package com.example.plantonista.distevents.tcp

import android.util.Log
import com.example.plantonista.distevents.EventData
import com.example.plantonista.distevents.sqlite.EventDao
import com.google.gson.Gson
import kotlinx.coroutines.withTimeout
import java.io.PrintStream
import java.net.Socket
import java.util.Scanner


class TCPClient(
    private val eventDao: EventDao,
    private val timeoutMillis: Long,
    private val getLocalEvents: (SyncEventsRequest) -> List<EventData>
) {
    private val gson = Gson()

    suspend fun sync(address: String, port: Int, request: SyncEventsRequest): List<EventData> {
        var newEvents = arrayOf<EventData>()

        withTimeout(timeoutMillis) {
            Log.d(TAG, "sync started")

            try {
                val socket = Socket(address, port)
                val input = Scanner(socket.getInputStream())
                val output = PrintStream(socket.getOutputStream())

                output.println(gson.toJson(request))

                Log.d(TAG, "sent")

                val peerSyncRequest = gson.fromJson(input.nextLine(), SyncEventsRequest::class.java)

                Log.d(TAG, "received request: $peerSyncRequest")

//                val resp = mutableListOf<EventData>()
//                for(head in peerSyncRequest.heads) {
//                    resp.addAll(
//                        getLocalEvents(peerSyncRequest)
//                    )
//                }

                output.println(gson.toJson(
                    getLocalEvents(peerSyncRequest),
                ))

                newEvents = gson.fromJson(input.nextLine(), Array<EventData>::class.java)

                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return newEvents.toList()
    }

    companion object {
        private val TAG = TCPClient::class.java.simpleName
    }
}