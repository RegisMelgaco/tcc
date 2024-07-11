package com.example.plantonista.gateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantonista.event.AddMemberEvent
import com.example.plantonista.gateway.distevents.GlobalStreamer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemberCreateViewModel: ViewModel() {
    fun createMember(name: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            GlobalStreamer.submit(
                AddMemberEvent(GlobalStreamer.networkName, GlobalStreamer.author, name, email)
            )
        }
    }
}