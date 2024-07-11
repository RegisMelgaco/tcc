package com.example.plantonista.gateway.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantonista.gateway.distevents.GlobalStreamer
import com.example.plantonista.gateway.preferences.getUsername
import com.example.plantonista.state.GlobalMemberState
import com.example.plantonista.state.MemberState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TeamViewModel(
    private val memberState: MemberState = GlobalMemberState
): ViewModel() {
    fun setup(context: Context, networkName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            GlobalStreamer.setup(context, networkName, getUsername(context))
            GlobalStreamer.stream(memberState.handle)
        }
    }
}