package com.example.plantonista.gateway.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantonista.Configs
import com.example.plantonista.distevents.Tracker
import com.example.plantonista.event.AddMemberEvent
import com.example.plantonista.gateway.distevents.GlobalStreamer
import com.example.plantonista.gateway.preferences.getUsername
import kotlinx.coroutines.runBlocking

class TeamCreateViewModel: ViewModel() {
    suspend fun createNetwork(context: Context, name: String) {
        runBlocking {
            Tracker(context, Configs.TRACKER_ADDRESS).createNetwork(name)

            val username = getUsername(context)

            GlobalStreamer.setup(context, name, username)
            GlobalStreamer.submit(AddMemberEvent(name, username, username, username, System.currentTimeMillis() / 1000))
        }
    }
}