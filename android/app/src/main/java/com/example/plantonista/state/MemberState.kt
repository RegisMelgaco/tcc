package com.example.plantonista.state

import android.util.Log
import com.example.plantonista.distevents.EventHandler
import com.example.plantonista.distevents.EventValidator
import com.example.plantonista.entity.Member
import com.example.plantonista.event.AddAdminEvent
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

            val isAdmin = members.value.isEmpty()

            _members.value += Member(event.name, event.email, isAdmin)

            Log.i(TAG, "adding member to state: $event")
        },
        AppEventType.AddAdmin to { event ->
            event as AddAdminEvent

            for (i in _members.value.indices) {
                val m = _members.value[i]
                if (m.email == event.email) {
                    _members.value[i] = m.copy(isAdmin = true)
                }
            }
        }
    )

    val validate: EventValidator<AppEventType> = mapOf(
        AppEventType.AddMember to { event ->
            event as AddMemberEvent

            if (!authorIsAdmin(event.author)) {
                throw AuthorIsNotAdminException(event.author)
            }

            val isInUse = members.value.firstOrNull { it.email == event.email } != null
            if (isInUse) {
                throw EmailInUseException(event.email)
            }

            true
        },
        AppEventType.AddAdmin to { event ->
            event as AddAdminEvent

            if (!authorIsAdmin(event.author))
                throw AuthorIsNotAdminException(event.author)

            if (!hasMember(event.email))
                throw MemberNotFoundException(event.email)

            true
        }
    )

    fun authorIsAdmin(author: String) =
        members.value.firstOrNull{ it.email == author }?.isAdmin ?: false

    fun hasMember(email: String) =
        members.value.firstOrNull { it.email == email } != null

    companion object {
        private val TAG = MemberState::class.simpleName
    }
}

object GlobalMemberState: MemberState()
