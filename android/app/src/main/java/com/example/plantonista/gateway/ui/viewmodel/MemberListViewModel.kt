package com.example.plantonista.gateway.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.plantonista.entity.Member
import com.example.plantonista.state.GlobalMemberState
import com.example.plantonista.state.MemberState
import kotlinx.coroutines.flow.StateFlow

class MemberListViewModel(private val memberState: MemberState = GlobalMemberState): ViewModel() {
    val members: StateFlow<List<Member>>
        get() = memberState.members
}