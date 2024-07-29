package com.example.plantonista.gateway.ui.viewmodel

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantonista.event.AcceptShiftExchangeRequestEvent
import com.example.plantonista.event.ConfirmShiftExchangeRequestEvent
import com.example.plantonista.gateway.distevents.GlobalStreamer
import com.example.plantonista.gateway.preferences.getUsername
import com.example.plantonista.state.GlobalShiftState
import com.example.plantonista.state.ShiftState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NotificationsViewModel(private val shiftState: ShiftState = GlobalShiftState): ViewModel() {
    private lateinit var me: String

    fun setup(context: Context) {
        me = getUsername(context)
    }

    val acceptShiftExchangeRequests: Flow<List<AcceptShiftExchangeRequestEvent>>
        get() = shiftState.acceptShiftExchangeRequests.map { it.filter { it.memberEmail == me } }

    fun getAuthorName(event: AcceptShiftExchangeRequestEvent) =
        shiftState.memberState.getName(event.author)

    fun confirm(event: AcceptShiftExchangeRequestEvent) {
        val newEvent = ConfirmShiftExchangeRequestEvent(
            GlobalStreamer.networkData.name,
            me,
            event.author,
            event.shiftStartUnix,
            System.currentTimeMillis() / 1000
        )

        viewModelScope.launch(Dispatchers.IO) {
            GlobalStreamer.submit(newEvent)
        }
    }
}