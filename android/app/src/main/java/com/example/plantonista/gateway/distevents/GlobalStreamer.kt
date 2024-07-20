package com.example.plantonista.gateway.distevents

import android.content.Context
import android.util.Log
import com.example.plantonista.distevents.Event
import com.example.plantonista.distevents.EventHandler
import com.example.plantonista.distevents.EventStreamer
import com.example.plantonista.distevents.NetworkData
import com.example.plantonista.event.AppEventFactory
import com.example.plantonista.event.AppEventType
import com.example.plantonista.state.GlobalMemberState
import com.example.plantonista.state.GlobalShiftState

object GlobalStreamer {
    private var streamer: EventStreamer<AppEventType>? = null

    val networkData: NetworkData
        get() {
            try {
                return streamer!!.networkData
            } catch (e: Exception) {
                throw StreamerNotSetup()
            }
        }

    val author: String
        get() {
            try {
                return streamer!!.author
            } catch (e: Exception) {
                throw StreamerNotSetup()
            }
        }

    fun setup(context: Context, networkName: String, username: String) {
        Log.d(TAG, "creating new streamer")
        if (streamer != null) {
            Log.d(TAG, "replacing old streamer: networkName=${streamer?.networkName}")
        }

        streamer = EventStreamer(
            context,
            networkName,
            username,
            AppEventFactory(),
            GlobalMemberState.validate + GlobalShiftState.validate
        )

        isStreaming = false
    }

    private var isStreaming = false
    fun stream(handler: EventHandler<AppEventType>) {
        try {
            streamer!!.stream(handler)
        } catch (e: NullPointerException) {
            throw StreamerNotSetup()
        }
    }

    fun submit(event: Event<AppEventType>) {
        if (streamer == null) {
            Log.d(TAG, "failed to submit: streamer is not set")
        }

        try {
            streamer!!.submit(event)
        } catch (e: NullPointerException) {
            throw StreamerNotSetup()
        }
    }

    private val TAG = GlobalStreamer::class.simpleName
}

class StreamerNotSetup: Exception("streamer is not setup")
