package com.example.plantonista.gateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plantonista.entity.Shift
import com.example.plantonista.state.GlobalShiftState
import com.example.plantonista.state.ShiftState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import java.util.Calendar
import java.util.Date

class ShiftsViewModel(
    private val shiftState: ShiftState = GlobalShiftState
): ViewModel() {
    val shifts: StateFlow<List<Shift>>
        get() = shiftState.shifts

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
}