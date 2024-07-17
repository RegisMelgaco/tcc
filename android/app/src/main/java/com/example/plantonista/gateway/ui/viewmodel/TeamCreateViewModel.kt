package com.example.plantonista.gateway.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.plantonista.Configs
import com.example.plantonista.distevents.NetworkData
import com.example.plantonista.distevents.Tracker
import com.example.plantonista.event.AddMemberEvent
import com.example.plantonista.gateway.distevents.GlobalStreamer
import com.example.plantonista.gateway.preferences.getUsername

class TeamCreateViewModel: ViewModel() {
    suspend fun createNetwork(context: Context, data: NetworkData) {
        Tracker(context, Configs.TRACKER_ADDRESS).createNetwork(data)

        val username = getUsername(context)

        GlobalStreamer.setup(context, data.name, username)
        GlobalStreamer.submit(AddMemberEvent(data.name, username, username, username, System.currentTimeMillis() / 1000))
    }
}