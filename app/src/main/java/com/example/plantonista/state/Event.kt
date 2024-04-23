package com.example.plantonista.state


interface Event : Comparable<Event> {
    val author: String
    val createdAt: Long

    fun type(): Type

    enum class Type(val value: String) {
        GivePosition("give-position")
    }
}

data class GivePositionEvent(
    override val author: String,
    override val createdAt: Long = System.currentTimeMillis()
) : Event {
    override fun type() = Event.Type.GivePosition

    override fun compareTo(other: Event) = when {
        createdAt != other.createdAt -> (createdAt - other.createdAt).toInt()
        type() != other.type() -> type() compareTo other.type()
        author != other.author -> author compareTo author

        else -> 0
    }
}