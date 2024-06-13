package com.example.plantonista.distevents.tcp

import android.util.Log
import com.example.plantonista.distevents.EventData
import com.google.gson.Gson
import kotlinx.coroutines.withTimeout
import java.io.PrintStream
import java.net.Socket
import java.util.Scanner


class TCPClient(private val timeoutMillis: Long) {
    private val gson = Gson()

    suspend fun sync(address: String, port: Int, events: List<EventData>): List<EventData> {
        var newEvents = arrayOf<EventData>()

        withTimeout(timeoutMillis) {
            Log.d(TAG, "sync started")

            try {
                val socket = Socket(address, port)
                val input = Scanner(socket.getInputStream())
                val output = PrintStream(socket.getOutputStream())

                var createdAts = events.map { it.createdAt }

                output.println(gson.toJson(createdAts))

                Log.d(TAG, "sent")

                createdAts = gson.fromJson(input.nextLine(), Array<Long>::class.java).toList()

                Log.d(TAG, "received createdAts: $createdAts")

                val resp = events
                    .filter { it.createdAt !in createdAts }
                    .toTypedArray()

                output.println(gson.toJson(resp))

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