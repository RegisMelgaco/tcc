package com.example.plantonista.gateway.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantonista.distevents.EventStreamer
import com.example.plantonista.entity.Shift
import com.example.plantonista.event.AcceptShiftExchangeRequestEvent
import com.example.plantonista.event.AppEventType
import com.example.plantonista.event.CancelShiftExchangeRequestEvent
import com.example.plantonista.event.OpenShiftExchangeRequestEvent
import com.example.plantonista.gateway.distevents.GlobalStreamer
import com.example.plantonista.gateway.preferences.getUsername
import com.example.plantonista.state.GlobalShiftState
import com.example.plantonista.state.ShiftState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class ShiftsViewModel(
    private val shiftState: ShiftState = GlobalShiftState,
): ViewModel() {
    val shifts: StateFlow<List<Shift>>
        get() = shiftState.shifts

    private lateinit var me: String

    fun setup(context: Context) {
        me = getUsername(context)
    }

    val initialIndex = shifts.transform {
        var result = 0

        val minMax = Calendar.getInstance().let { calendar ->
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH + 1))
            calendar.time
        }

        for(indexed in it.withIndex()) {
            if (Date(indexed.value.endMill) < minMax)
                result = indexed.index
        }

        emit(result)
    }

    fun member(shift: Shift) =
        shiftState.memberState.members.value.firstOrNull{ it.email == shift.memberEmail }?.name ?: shift.memberEmail

    fun hasOpenExchangeRequest(shift: Shift) =
        shiftState.openShiftExchangeRequest.value.firstOrNull { it.author == shift.memberEmail && it.shiftStartUnix == shift.startUnix } != null

    fun isMine(shift: Shift) = shift.memberEmail == me

    fun canOpenExchangeRequest(shift: Shift) = !hasOpenExchangeRequest(shift) && isMine(shift)

    fun canCancelExchangeRequest(shift: Shift) = hasOpenExchangeRequest(shift) && isMine(shift)

    fun canAcceptExchangeRequest(shift: Shift) = hasOpenExchangeRequest(shift) && isMine(shift)

    fun openExchangeRequest(context: Context, shift: Shift) {
        val event = OpenShiftExchangeRequestEvent(
            GlobalStreamer.networkData.name,
            getUsername(context),
            shift.startUnix,
        )

        viewModelScope.launch(Dispatchers.IO) {
            GlobalStreamer.submit(event)
        }
    }

    fun cancelExchangeRequest(context: Context, shift: Shift) {
        val event = CancelShiftExchangeRequestEvent(
            GlobalStreamer.networkData.name,
            getUsername(context),
            shift.startUnix,
        )

        viewModelScope.launch(Dispatchers.IO) {
            GlobalStreamer.submit(event)
        }
    }

    fun acceptExchangeRequest(context: Context, shift: Shift) {
        val event = AcceptShiftExchangeRequestEvent(
            GlobalStreamer.networkData.name,
            getUsername(context),
            shift.memberEmail,
            shift.startUnix,
        )

        viewModelScope.launch(Dispatchers.IO) {
            GlobalStreamer.submit(event)
        }
    }
}