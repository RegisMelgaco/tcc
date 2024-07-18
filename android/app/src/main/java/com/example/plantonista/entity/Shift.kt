package com.example.plantonista.entity

data class Shift(
    val memberEmail: String,
    val startUnix: Long, // unix
    val durationMin: Int,
) {
    val endUnix: Long
        get() = startUnix + (durationMin * 60)

    val startMill: Long
        get() = startUnix * 1000

    val endMill: Long
        get() = endUnix * 1000
}