package com.example.plantonista.state

import com.example.plantonista.entity.Shift
import com.example.plantonista.event.AddShiftEvent

class MemberNotFoundException(val email: String) : Exception("member with email '$email' not found")
class EmailInUseException(val email: String): Exception("email '$email' is in use")
class ShiftHasTimeConflictException(val event: AddShiftEvent, val shift: Shift) : Exception("new shift $event and shift $shift has time conflict")
class AuthorIsNotAdminException(val author: String) : Exception("author '$author' is not admin")