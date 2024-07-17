package com.example.plantonista.gateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plantonista.entity.Shift
import com.example.plantonista.state.GlobalShiftState
import com.example.plantonista.state.ShiftState
import kotlinx.coroutines.flow.StateFlow

class ShiftsViewModel(private val shiftState: ShiftState = GlobalShiftState): ViewModel() {
    val shifts: StateFlow<List<Shift>>
        get() = shiftState.shifts
}