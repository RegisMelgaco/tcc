package com.example.plantonista.gateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantonista.entity.Member
import com.example.plantonista.event.AddShiftEvent
import com.example.plantonista.gateway.distevents.GlobalStreamer
import com.example.plantonista.state.AuthorIsNotAdminException
import com.example.plantonista.state.GlobalMemberState
import com.example.plantonista.state.MemberNotFoundException
import com.example.plantonista.state.MemberState
import com.example.plantonista.state.ShiftHasTimeConflictException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShiftCreateViewModel(
    private val memberState: MemberState = GlobalMemberState,
): ViewModel() {
    val members: StateFlow<List<Member>>
        get() = memberState.members

    fun createShift(
        memberIndex: Int,
        start: Long,
        durationMin: Int,
        onSuccess : () -> Unit,
        onError: (String) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                GlobalStreamer.submit(
                    AddShiftEvent(GlobalStreamer.networkName, GlobalStreamer.author, members.value[memberIndex].email, start, durationMin)
                )

                onSuccess()
            } catch (e: MemberNotFoundException) {
                onError("Membro com '${e.email}' não foi encontrado.")
            } catch (e: ShiftHasTimeConflictException) {
                onError("Horário escolhido tem conflito com horário existente.")
            } catch (e: AuthorIsNotAdminException) {
                onError("Somente usuários administrativos podem adicionar um plantão.")
            }
        }
    }
}