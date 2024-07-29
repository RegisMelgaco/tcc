package com.example.plantonista.state

import android.util.Log
import com.example.plantonista.distevents.EventHandler
import com.example.plantonista.distevents.EventValidator
import com.example.plantonista.entity.Shift
import com.example.plantonista.event.AcceptShiftExchangeRequestEvent
import com.example.plantonista.event.AddShiftEvent
import com.example.plantonista.event.AppEventType
import com.example.plantonista.event.CancelShiftExchangeRequestEvent
import com.example.plantonista.event.OpenShiftExchangeRequestEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class ShiftState(val memberState: MemberState) {
    val shifts: StateFlow<List<Shift>>
        get() = _shifts

    private val _shifts = MutableStateFlow<List<Shift>>(listOf())

    val openShiftExchangeRequest: StateFlow<List<OpenShiftExchangeRequestEvent>>
        get() = _openShiftExchangeRequest

    private val _openShiftExchangeRequest = MutableStateFlow<List<OpenShiftExchangeRequestEvent>>(listOf())

    val acceptShiftExchangeRequests: StateFlow<List<AcceptShiftExchangeRequestEvent>>
        get() = _acceptShiftExchangeRequests

    private val _acceptShiftExchangeRequests = MutableStateFlow<List<AcceptShiftExchangeRequestEvent>>(listOf())

    fun cleanUp() {
        _shifts.value = listOf()
    }

    val handle: EventHandler<AppEventType> = mapOf(
        AppEventType.AddShift to { event ->
            event as AddShiftEvent

            _shifts.value += Shift(event.memberEmail, event.start, event.durationMin)

            Log.i(TAG, "adding shift to state: $event")
        },
        AppEventType.OpenShiftExchangeRequest to { event ->
            _openShiftExchangeRequest.value += event as OpenShiftExchangeRequestEvent
        },
        AppEventType.CancelShiftExchangeRequest to { event ->
            event as CancelShiftExchangeRequestEvent

            val res = openShiftExchangeRequest.value.filter { it.author != event.author || it.shiftStartUnix != event.shiftStartUnix  }
            if (res.size < openShiftExchangeRequest.value.size) {
                _openShiftExchangeRequest.value = res
            }
        },
        AppEventType.AcceptShiftExchangeRequest to { event ->
            _acceptShiftExchangeRequests.value += event as AcceptShiftExchangeRequestEvent
        }
    )

    val validate: EventValidator<AppEventType> = mapOf(
        AppEventType.AddShift to { event ->
            event as AddShiftEvent

            if (!memberState.authorIsAdmin(event.author))
                throw AuthorIsNotAdminException(event.author)

            if (!memberState.hasMember(event.memberEmail))
                throw MemberNotFoundException(event.memberEmail)

            for (shift in shifts.value.filter { it.memberEmail == event.memberEmail }) {
                val eventEnd = event.start + event.durationMin * 60 // unix time
                val shiftEnd = shift.endUnix // unix time

                val hasOverlap = shift.startUnix in event.start .. eventEnd || shiftEnd in event.start .. eventEnd

                if (hasOverlap) {
                    throw ShiftHasTimeConflictException(event, shift)
                }
            }

            true
        },
    )

    companion object {
        private val TAG = MemberState::class.simpleName
    }
}

object GlobalShiftState: ShiftState(GlobalMemberState)