package com.example.plantonista.gateway.tcp

import android.util.Log
import com.example.plantonista.state.Event
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintStream
import java.net.Socket
import java.util.Scanner


class TCPClient(private val timeoutMillis: Long) {
    private val gson = Gson()

    suspend fun sync(address: String, port: Int, events: Array<Event>): Array<Event> {
        var newEvents = arrayOf<Event>()

        withContext(Dispatchers.IO) {
            delay(5000)

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

                var msg = events
                    .filter { it.createdAt !in createdAts }
                    .map { it.toMsg() }
                    .toTypedArray()

                output.println(gson.toJson(msg))

                msg = gson.fromJson(input.nextLine(), Array<EventMessage>::class.java)

                newEvents = msg.map { it.toEntity() }.toTypedArray()

                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return newEvents
    }

    companion object {
        private val TAG = TCPClient::class.java.simpleName
    }
}