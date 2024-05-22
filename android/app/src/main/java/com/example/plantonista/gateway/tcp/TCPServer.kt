package com.example.plantonista.gateway.tcp


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.plantonista.R
import com.example.plantonista.state.Event
import com.example.plantonista.state.GivePositionEvent
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.EOFException
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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
            var socket: Socket? = null

            val events = mutableListOf<Event>(GivePositionEvent("regis", 1))

            try {
                serverSocket = ServerSocket(PORT)

                Log.d(TAG, "connecting to port: $PORT")

                while (working.get()) {
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

                            var msg = gson.fromJson(input.nextLine(), Array<EventMessage>::class.java)

                            events.addAll(msg.map { it.toEntity() })

                            msg = events
                                .filter { it.createdAt !in createdAts }
                                .map { it.toMsg() }
                                .toTypedArray()

                            output.println(gson.toJson(msg))
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
        private val TAG = TCPServer::class.java.simpleName
        private const val PORT = 2001
        private const val TIMEOUT = 30_000L
    }
}