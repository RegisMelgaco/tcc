package com.example.plantonista.gateway.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.plantonista.Configs
import com.example.plantonista.distevents.NetworkData
import com.example.plantonista.distevents.Tracker

class TeamCreateViewModel: ViewModel() {
    suspend fun createNetwork(context: Context, data: NetworkData) {
        Tracker(context, Configs.TRACKER_ADDRESS).createNetwork(data)
    }
}