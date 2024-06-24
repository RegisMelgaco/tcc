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
                    if (serverSocket != null) {
                        socket = serverSocket!!.accept()

                        Log.i(TAG, "connection accepted with $socket")

                        withTimeout(TIMEOUT) {
                            val input = Scanner(socket.getInputStream())
                            val output = PrintStream(socket.getOutputStream())

                            val peerSyncRequest = gson.fromJson(input.nextLine(), SyncEventsRequest::class.java)

                            Log.d(TAG, "received createdAts: $peerSyncRequest")

                            val ownSyncRequest = eventDao.getEventStreamHead()

                            output.println(gson.toJson(
                                SyncEventsRequest(ownSyncRequest.map { it.toRequest() })
                            ))

                            val ownSyncResponse = gson.fromJson(input.nextLine(), Array<EventData>::class.java)

                            eventDao.insertAll(
                                ownSyncResponse.map { it.toEntity() }
                            )

                            val peerSyncResponse = mutableListOf<EventData>()
                            for(head in peerSyncRequest.heads) {
                                peerSyncResponse.addAll(
                                    eventDao.getEventsByEventStreamHead(
                                        head.headDateTime, head.author,
                                    ).map { it.toData() }
                                )
                            }

                            output.println(gson.toJson(peerSyncResponse))
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