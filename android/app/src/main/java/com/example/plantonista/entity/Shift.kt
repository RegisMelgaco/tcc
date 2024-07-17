package com.example.plantonista.entity

data class Shift(
    val memberEmail: String,
    val start: Long,
    val durationMin: Int,
)