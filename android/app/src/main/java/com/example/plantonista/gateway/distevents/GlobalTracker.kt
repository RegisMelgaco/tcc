package com.example.plantonista.gateway.distevents

import android.content.Context
import com.example.plantonista.distevents.Tracker

object GlobalTracker {
    private lateinit var tracker: Tracker

    fun setup(context: Context, host: String) {
        tracker = Tracker(context, host)
    }

    suspend fun sync(username: String) {
        tracker.syncEvents(username)
    }
}