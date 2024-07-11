package com.example.plantonista.gateway.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantonista.Configs
import com.example.plantonista.distevents.NetworkData
import com.example.plantonista.distevents.Tracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeamListViewModel : ViewModel() {
    val teams : StateFlow<List<NetworkData>>
        get() = _teams

    private val _teams = MutableStateFlow<List<NetworkData>>(listOf())

    fun updateTeams(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _teams.value = Tracker(context, Configs.TRACKER_ADDRESS).listNetworks()
        }
    }
}