package com.example.plantonista.state

import android.util.Log
import com.example.plantonista.distevents.EventHandler
import com.example.plantonista.distevents.EventValidator
import com.example.plantonista.entity.Member
import com.example.plantonista.event.AddMemberEvent
import com.example.plantonista.event.AppEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class MemberState {
    val members: StateFlow<List<Member>>
        get() = _members

    private val _members = MutableStateFlow<MutableList<Member>>(mutableListOf())

    fun cleanUp() {
        _members.value.clear()
    }

    val handle: EventHandler<AppEventType> = mapOf(
        AppEventType.AddMember to { e ->
            val event = e as AddMemberEvent
            _members.value += Member(event.name, event.email)

            Log.i(TAG, "adding member to state: $event")
        }
    )

    val validate: EventValidator<AppEventType> = mapOf(
        AppEventType.AddMember to { event ->
            event as AddMemberEvent

            val isInUse = members.value.firstOrNull { it.email == event.email } != null
            if (isInUse) {
                throw EmailInUseException(event.email)
            }

            true
        }
    )

    companion object {
        private val TAG = MemberState::class.simpleName
    }
}

object GlobalMemberState: MemberState()

class EmailInUseException(val email: String): Exception("email '$email' is in use")