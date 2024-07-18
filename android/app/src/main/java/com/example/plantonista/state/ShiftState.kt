package com.example.plantonista.state

import android.util.Log
import com.example.plantonista.distevents.EventHandler
import com.example.plantonista.distevents.EventValidator
import com.example.plantonista.entity.Shift
import com.example.plantonista.event.AddShiftEvent
import com.example.plantonista.event.AppEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class ShiftState(val memberState: MemberState) {
    val shifts: StateFlow<List<Shift>>
        get() = _shifts

    private val _shifts = MutableStateFlow<List<Shift>>(listOf())

    fun cleanUp() {
        _shifts.value = listOf()
    }

    val handle: EventHandler<AppEventType> = mapOf(
        AppEventType.AddShift to { event ->
            event as AddShiftEvent

            _shifts.value += Shift(event.memberEmail, event.start, event.durationMin)

            Log.i(TAG, "adding shift to state: $event")
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
        }
    )

    companion object {
        private val TAG = MemberState::class.simpleName
    }
}

object GlobalShiftState: ShiftState(GlobalMemberState)