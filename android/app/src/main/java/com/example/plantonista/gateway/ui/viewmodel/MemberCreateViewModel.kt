package com.example.plantonista.gateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantonista.event.AddMemberEvent
import com.example.plantonista.gateway.distevents.GlobalStreamer
import com.example.plantonista.state.EmailInUseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MemberCreateViewModel: ViewModel() {
    fun createMember(
        name: String,
        email: String,
        onSuccess : () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                GlobalStreamer.submit(
                    AddMemberEvent(GlobalStreamer.networkName, GlobalStreamer.author, name, email)
                )

                onSuccess()
            } catch (e: EmailInUseException) {
                onError("email '$email' já foi cadastrado.")
            }
        }
    }
}