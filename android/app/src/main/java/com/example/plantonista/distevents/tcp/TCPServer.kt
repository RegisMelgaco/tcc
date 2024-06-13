package com.example.plantonista.distevents.tcp


import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.room.Room
import com.example.plantonista.distevents.EventData
import com.example.plantonista.distevents.sqlite.Database
import com.example.plantonista.distevents.sqlite.toEntity
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.PrintStream
import java.net.ServerSocket
import java.net.Socket
import java.util.Scanner
import java.util.concurrent.atomic.AtomicBoolean

class TCPServer : Service() {
    private val gson = Gson()
    private var serverSocket: ServerSocket? = null
    private val working = AtomicBoolean(/* initialValue = */ true)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d(TAG, "created")

        GlobalScope.launch {
            var socket: Socket?

            val eventDao = Room.databaseBuilder(applicationContext, Database::class.java, Database.NAME).build().eventDao()

            try {
                serverSocket = ServerSocket(PORT)

                Log.d(TAG, "connecting to port: $PORT")

                while (working.get()) {
                    val events = eventDao.getAll()
                    Log.d(TAG, "working: $events")

                    if (serverSocket != null) {
                        socket = serverSocket!!.accept()

                        Log.i(TAG, "connection accepted with $socket")

                        withTimeout(TIMEOUT) {
                            val input = Scanner(socket.getInputStream())
                            val output = PrintStream(socket.getOutputStream())

                            val createdAts = gson.fromJson(input.nextLine(), Array<Long>::class.java)

                            Log.d(TAG, "received createdAts: $createdAts")

                            output.println(gson.toJson(events.map { it.createdAt }))

                            val newEvents = gson.fromJson(input.nextLine(), Array<EventData>::class.java)

                            eventDao.insertAll(
                                newEvents.map { it.toEntity() }
                            )

                            val resp = events
                                .filter { it.createdAt !in createdAts }
                                .map { it.toData() }
                                .toTypedArray()

                            output.println(gson.toJson(resp))
                        }
                    } else {
                        Log.e(TAG, "Couldn't create ServerSocket!")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        working.set(false)
    }

    companion object {
        const val PORT = 2001

        private val TAG = TCPServer::class.java.simpleName
        private var TIMEOUT = 30_000L
    }
}